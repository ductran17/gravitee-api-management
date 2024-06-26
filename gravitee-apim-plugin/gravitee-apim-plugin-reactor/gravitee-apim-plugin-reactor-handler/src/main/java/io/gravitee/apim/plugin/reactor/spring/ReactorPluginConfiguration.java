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
package io.gravitee.apim.plugin.reactor.spring;

import io.gravitee.apim.plugin.reactor.internal.DefaultReactorPluginManager;
import io.gravitee.node.plugins.service.ServiceManager;
import io.gravitee.plugin.core.api.PluginContextFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Yann TAVERNIER (yann.tavernier at graviteesource.com)
 * @author GraviteeSource Team
 */
@Configuration
public class ReactorPluginConfiguration {

    @Bean
    public DefaultReactorPluginManager reactorPluginManager(
        final ApplicationContext applicationContext,
        final PluginContextFactory pluginContextFactory,
        final ServiceManager serviceManager
    ) {
        return new DefaultReactorPluginManager(applicationContext, pluginContextFactory, serviceManager);
    }
}
