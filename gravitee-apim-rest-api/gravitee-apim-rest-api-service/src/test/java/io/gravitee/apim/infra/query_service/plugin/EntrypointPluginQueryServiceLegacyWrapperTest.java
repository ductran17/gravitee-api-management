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
package io.gravitee.apim.infra.query_service.plugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.gravitee.apim.core.plugin.model.ConnectorPlugin;
import io.gravitee.definition.model.v4.ApiType;
import io.gravitee.definition.model.v4.ConnectorFeature;
import io.gravitee.definition.model.v4.ConnectorMode;
import io.gravitee.definition.model.v4.listener.ListenerType;
import io.gravitee.definition.model.v4.listener.entrypoint.Qos;
import io.gravitee.rest.api.model.v4.connector.ConnectorPluginEntity;
import io.gravitee.rest.api.service.v4.EntrypointConnectorPluginService;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class EntrypointPluginQueryServiceLegacyWrapperTest {

    EntrypointConnectorPluginService entrypointConnectorPluginService;
    EntrypointPluginQueryServiceLegacyWrapper service;

    @BeforeEach
    void setUp() {
        entrypointConnectorPluginService = mock(EntrypointConnectorPluginService.class);
        service = new EntrypointPluginQueryServiceLegacyWrapper(entrypointConnectorPluginService);
    }

    @Nested
    class FindBySupportedApi {

        @Test
        void should_return_plugins_list_supported_by_the_given_api_type() {
            when(entrypointConnectorPluginService.findBySupportedApi(any()))
                .thenAnswer(invocation -> {
                    var entity = new ConnectorPluginEntity();
                    entity.setId("id1");
                    entity.setName("Plugin 1");
                    entity.setVersion("1.0.0");
                    entity.setDescription("description1");
                    entity.setSupportedApiType(invocation.getArgument(0));
                    entity.setSupportedListenerType(ListenerType.HTTP);
                    entity.setSupportedModes(Set.of(ConnectorMode.REQUEST_RESPONSE));
                    entity.setSupportedQos(Set.of(Qos.AUTO));
                    entity.setAvailableFeatures(Set.of(ConnectorFeature.LIMIT));
                    return Set.of(entity);
                });

            var result = service.findBySupportedApi(ApiType.PROXY);

            assertThat(result)
                .hasSize(1)
                .containsExactly(
                    ConnectorPlugin
                        .builder()
                        .id("id1")
                        .name("Plugin 1")
                        .version("1.0.0")
                        .description("description1")
                        .supportedApiType(ApiType.PROXY)
                        .supportedListenerType(ListenerType.HTTP)
                        .supportedModes(Set.of(ConnectorMode.REQUEST_RESPONSE))
                        .supportedQos(Set.of(Qos.AUTO))
                        .availableFeatures(Set.of(ConnectorFeature.LIMIT))
                        .build()
                );
        }
    }
}
