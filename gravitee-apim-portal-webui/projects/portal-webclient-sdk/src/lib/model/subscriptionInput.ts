/**
 * Gravitee.io Portal Rest API
 * API dedicated to the devportal part of Gravitee
 *
 * Contact: contact@graviteesource.com
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
import { SubscriptionConfigurationInput } from './subscriptionConfigurationInput';
import { PageRevisionId } from './pageRevisionId';
import { ApiKeyModeEnum } from './apiKeyModeEnum';


export interface SubscriptionInput { 
    /**
     * Id of the application which subscribe
     */
    application?: string;
    /**
     * Id of the plan to which the application subscribe
     */
    plan?: string;
    /**
     * A request message to the api owner why a user want to subscribe
     */
    request?: string;
    /**
     * Specify if the general conditions have been accepted by the API subscriber
     */
    general_conditions_accepted?: boolean;
    general_conditions_content_revision?: PageRevisionId;
    /**
     * Subscription metadata
     */
    metadata?: { [key: string]: string; };
    configuration?: SubscriptionConfigurationInput;
    api_key_mode?: ApiKeyModeEnum;
}
