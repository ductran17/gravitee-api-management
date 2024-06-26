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
package io.gravitee.apim.core.plan.domain_service;

import io.gravitee.apim.core.audit.domain_service.AuditDomainService;
import io.gravitee.apim.core.audit.model.ApiAuditLogEntity;
import io.gravitee.apim.core.audit.model.AuditInfo;
import io.gravitee.apim.core.audit.model.AuditProperties;
import io.gravitee.apim.core.audit.model.event.PlanAuditEvent;
import io.gravitee.apim.core.datetime.TimeProvider;
import io.gravitee.apim.core.exception.ValidationDomainException;
import io.gravitee.apim.core.plan.crud_service.PlanCrudService;
import io.gravitee.apim.core.plan.model.Plan;
import io.gravitee.apim.core.subscription.query_service.SubscriptionQueryService;
import java.sql.Time;
import java.util.Map;

public class DeletePlanDomainService {

    private final PlanCrudService planCrudService;
    private final SubscriptionQueryService subscriptionQueryService;
    private final AuditDomainService auditService;

    public DeletePlanDomainService(
        PlanCrudService planCrudService,
        SubscriptionQueryService subscriptionQueryService,
        AuditDomainService auditService
    ) {
        this.planCrudService = planCrudService;
        this.subscriptionQueryService = subscriptionQueryService;
        this.auditService = auditService;
    }

    public void delete(Plan plan, AuditInfo auditInfo) {
        if (!subscriptionQueryService.findActiveSubscriptionsByPlan(plan.getId()).isEmpty()) {
            throw new ValidationDomainException("Impossible to delete a plan with active subscriptions");
        }
        planCrudService.delete(plan.getId());
        createAuditLog(plan, auditInfo);
    }

    private void createAuditLog(Plan planToDelete, AuditInfo auditInfo) {
        auditService.createApiAuditLog(
            ApiAuditLogEntity
                .builder()
                .organizationId(auditInfo.organizationId())
                .environmentId(auditInfo.environmentId())
                .apiId(planToDelete.getApiId())
                .event(PlanAuditEvent.PLAN_DELETED)
                .actor(auditInfo.actor())
                .oldValue(planToDelete)
                .createdAt(TimeProvider.now())
                .properties(Map.of(AuditProperties.PLAN, planToDelete.getId()))
                .build()
        );
    }
}
