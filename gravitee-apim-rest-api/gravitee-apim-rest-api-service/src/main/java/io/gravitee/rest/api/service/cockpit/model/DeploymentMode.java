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
package io.gravitee.rest.api.service.cockpit.model;

import io.gravitee.cockpit.api.command.designer.DeployModelPayload;
import java.util.Optional;

public enum DeploymentMode {
    API_DOCUMENTED,
    API_MOCKED,
    API_PUBLISHED;

    public static DeploymentMode fromDeployModelPayload(DeployModelPayload payload) {
        DeployModelPayload.DeploymentMode mode = Optional
            .ofNullable(payload.getMode())
            .orElse(DeployModelPayload.DeploymentMode.API_DOCUMENTED);

        if (mode == DeployModelPayload.DeploymentMode.API_MOCKED) {
            return DeploymentMode.API_MOCKED;
        }

        if (mode == DeployModelPayload.DeploymentMode.API_PUBLISHED) {
            return DeploymentMode.API_PUBLISHED;
        }

        return DeploymentMode.API_DOCUMENTED;
    }
}
