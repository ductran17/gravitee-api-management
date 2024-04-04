/*
 * Copyright © 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.rest.api.management.rest.resource.auth;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.proc.JWSAlgorithmFamilyJWSKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.nimbusds.jwt.proc.JWTProcessor;
import io.gravitee.apim.core.installation.query_service.InstallationAccessQueryService;
import io.gravitee.rest.api.idp.api.authentication.UserDetails;
import io.gravitee.rest.api.model.UserEntity;
import io.gravitee.rest.api.security.cookies.CookieGenerator;
import io.gravitee.rest.api.security.utils.AuthoritiesProvider;
import io.gravitee.rest.api.service.MembershipService;
import io.gravitee.rest.api.service.UserService;
import io.gravitee.rest.api.service.common.ExecutionContext;
import io.gravitee.rest.api.service.common.GraviteeContext;
import io.gravitee.rest.api.service.v4.ApiSearchService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.Optional;
import java.util.Set;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

@Singleton
@Path("/auth/cockpit")
public class CockpitAuthenticationResource extends AbstractAuthenticationResource {

    protected static final String ORG_CLAIM = "org";
    protected static final String KID = "cockpit";
    protected static final String REDIRECT_URI_CLAIM = "redirect_uri";
    protected static final String ENVIRONMENT_CLAIM = "env";
    protected static final String API_CLAIM = "api";
    private static final Logger LOGGER = LoggerFactory.getLogger(CockpitAuthenticationResource.class);
    private static final String COCKPIT_SOURCE = "cockpit";

    @Autowired
    protected MembershipService membershipService;

    @Autowired
    protected CookieGenerator cookieGenerator;

    private boolean enabled;
    private JWTProcessor<SecurityContext> jwtProcessor;

    @Autowired
    private UserService userService;

    @Autowired
    private Environment environment;

    @Autowired
    private AuthoritiesProvider authoritiesProvider;

    @Autowired
    private ApiSearchService apiSearchService;

    @Autowired
    private InstallationAccessQueryService installationAccessQueryService;

    @PostConstruct
    public void afterPropertiesSet() {
        enabled = environment.getProperty("cockpit.enabled", Boolean.class, false);

        if (enabled) {
            try {
                // Initialize the JWT processor which will handle JWT signature verification and assertions such as expiration time.
                final RSAKey rsaKey = new RSAKey.Builder((RSAPublicKey) getPublicKey()).keyID(KID).build();
                final JWSAlgorithmFamilyJWSKeySelector<SecurityContext> jwsKeySelector = new JWSAlgorithmFamilyJWSKeySelector<>(
                    JWSAlgorithm.Family.RSA,
                    new ImmutableJWKSet<>(new JWKSet(rsaKey))
                );

                // Note that the JWT processor can be configured with custom verifiers if we want more assertions (ex: validate iss, check org claims is defined, ...)
                final ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
                jwtProcessor.setJWSKeySelector(jwsKeySelector);
                this.jwtProcessor = jwtProcessor;
            } catch (Exception e) {
                throw new RuntimeException("Unable to initialize cockpit filter", e);
            }
        }
    }

    @GET
    public Response tokenExchange(@QueryParam(value = "token") final String token, @Context final HttpServletResponse httpResponse) {
        if (!enabled) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        try {
            // Verify and get claims from token.
            final JWTClaimsSet jwtClaimsSet = jwtProcessor.process(token, null);

            // Initialize execution context from cockpit token.
            final String organizationId = jwtClaimsSet.getStringClaim(ORG_CLAIM);
            final String environmentId = jwtClaimsSet.getStringClaim(ENVIRONMENT_CLAIM);
            GraviteeContext.fromExecutionContext(new ExecutionContext(organizationId, environmentId));

            // Retrieve the user.
            final UserEntity user = userService.findBySource(organizationId, COCKPIT_SOURCE, jwtClaimsSet.getSubject(), true);

            //set user to Authentication Context
            final Set<GrantedAuthority> authorities = authoritiesProvider.retrieveAuthorities(user.getId(), organizationId, environmentId);

            UserDetails userDetails = new UserDetails(user.getId(), "", authorities);
            userDetails.setOrganizationId(user.getOrganizationId());
            userDetails.setEmail(user.getEmail());
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, null, authorities));

            // Cockpit user is authenticated, connect user (ie: generate cookie).
            super.connectUser(user, httpResponse);

            final String apiCrossId = jwtClaimsSet.getStringClaim(API_CLAIM);
            final String apiId = Optional
                .ofNullable(apiCrossId)
                .flatMap(crossId -> this.apiSearchService.findIdByEnvironmentIdAndCrossId(environmentId, crossId))
                .orElse(null);
            String url = String.format(
                "%s/#!/environments/%s/%s",
                installationAccessQueryService.getConsoleUrl(organizationId),
                environmentId,
                apiId == null ? "home" : URLEncoder.encode(String.format("apis/%s/general", apiId), StandardCharsets.UTF_8)
            );

            // Redirect the user.
            return Response.temporaryRedirect(new URI(url)).build();
        } catch (Exception e) {
            LOGGER.error("Error occurred when trying to log user using cockpit.", e);
            return Response.serverError().build();
        } finally {
            GraviteeContext.cleanContext();
        }
    }

    private Key getPublicKey() throws Exception {
        final KeyStore trustStore = loadTrustStore();
        final Certificate cert = trustStore.getCertificate(environment.getProperty("cockpit.keystore.key.alias", "cockpit-client"));

        return cert.getPublicKey();
    }

    private KeyStore loadTrustStore() throws Exception {
        final KeyStore keystore = KeyStore.getInstance(environment.getProperty("cockpit.keystore.type"));

        try (InputStream is = new File(environment.getProperty("cockpit.keystore.path")).toURI().toURL().openStream()) {
            final String password = environment.getProperty("cockpit.keystore.password");
            keystore.load(is, null == password ? null : password.toCharArray());
        }

        return keystore;
    }
}