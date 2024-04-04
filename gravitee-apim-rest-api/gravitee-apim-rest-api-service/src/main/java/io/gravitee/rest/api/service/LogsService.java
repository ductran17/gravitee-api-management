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
package io.gravitee.rest.api.service;

import io.gravitee.rest.api.model.analytics.query.LogQuery;
import io.gravitee.rest.api.model.log.*;
import io.gravitee.rest.api.service.common.ExecutionContext;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
public interface LogsService {
    SearchLogResponse<ApiRequestItem> findByApi(ExecutionContext executionContext, String api, LogQuery query);
    SearchLogResponse<ApplicationRequestItem> findByApplication(ExecutionContext executionContext, String application, LogQuery query);
    SearchLogResponse<PlatformRequestItem> findPlatform(ExecutionContext executionContext, LogQuery query);
    ApiRequest findApiLog(ExecutionContext executionContext, String id, Long timestamp);
    ApplicationRequest findApplicationLog(ExecutionContext executionContext, String applicationId, String id, Long timestamp);
    String exportAsCsv(ExecutionContext executionContext, SearchLogResponse<?> searchLogResponse);
}