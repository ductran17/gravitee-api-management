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
package io.gravitee.rest.api.service.configuration.application;

import io.gravitee.rest.api.model.NewApplicationEntity;
import io.gravitee.rest.api.model.UpdateApplicationEntity;
import io.gravitee.rest.api.model.configuration.application.registration.ClientRegistrationProviderEntity;
import io.gravitee.rest.api.model.configuration.application.registration.NewClientRegistrationProviderEntity;
import io.gravitee.rest.api.model.configuration.application.registration.UpdateClientRegistrationProviderEntity;
import io.gravitee.rest.api.service.common.ExecutionContext;
import io.gravitee.rest.api.service.impl.configuration.application.registration.client.register.ClientRegistrationResponse;
import java.util.Set;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
public interface ClientRegistrationService {
    Set<ClientRegistrationProviderEntity> findAll(ExecutionContext executionContext);

    ClientRegistrationProviderEntity create(
        ExecutionContext executionContext,
        NewClientRegistrationProviderEntity clientRegistrationProvider
    );

    ClientRegistrationProviderEntity update(
        ExecutionContext executionContext,
        String id,
        UpdateClientRegistrationProviderEntity clientRegistrationProvider
    );

    ClientRegistrationProviderEntity findById(String environmentId, String id);

    void delete(ExecutionContext executionContext, String id);

    ClientRegistrationResponse register(ExecutionContext executionContext, NewApplicationEntity application);

    ClientRegistrationResponse update(
        ExecutionContext executionContext,
        String previousRegistrationResponse,
        UpdateApplicationEntity application
    );

    ClientRegistrationResponse renewClientSecret(ExecutionContext executionContext, String previousRegistrationResponse);
}
