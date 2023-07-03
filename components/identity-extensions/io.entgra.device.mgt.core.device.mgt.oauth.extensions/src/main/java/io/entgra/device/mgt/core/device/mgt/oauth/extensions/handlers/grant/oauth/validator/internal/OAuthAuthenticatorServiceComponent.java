/*
 * Copyright (c) 2018 - 2023, Entgra (Pvt) Ltd. (http://www.entgra.io) All Rights Reserved.
 *
 * Entgra (Pvt) Ltd. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.entgra.device.mgt.core.device.mgt.oauth.extensions.handlers.grant.oauth.validator.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.ComponentContext;
import org.wso2.carbon.identity.oauth2.OAuth2TokenValidationService;

/**
 * @scr.component name="io.entgra.device.mgt.core.device.mgt.oauth.extensions.authenticator" immediate="true"
 * @scr.reference name="identity.oauth2.validation.service"
 * interface="org.wso2.carbon.identity.oauth2.OAuth2TokenValidationService"
 * cardinality="1..1"
 * policy="dynamic"
 * bind="setOAuth2ValidationService"
 * unbind="unsetOAuth2ValidationService"
 */
public class OAuthAuthenticatorServiceComponent {

    private static final Log log = LogFactory.getLog(OAuthAuthenticatorServiceComponent.class);

    @SuppressWarnings("unused")
    protected void activate(ComponentContext componentContext) {
        if (log.isDebugEnabled()) {
            log.debug("Starting Backend OAuthAuthenticator Framework Bundle");
        }
    }

    @SuppressWarnings("unused")
    protected void deactivate(ComponentContext componentContext) {
        //do nothing
    }

    /**
     * Sets OAuth2TokenValidation Service.
     *
     * @param tokenValidationService An instance of OAuth2TokenValidationService.
     */
    @SuppressWarnings("unused")
    protected void setOAuth2ValidationService(OAuth2TokenValidationService tokenValidationService) {
        if (log.isDebugEnabled()) {
            log.debug("Setting OAuth2TokenValidationService Service");
        }
        OAuthAuthenticatorDataHolder.getInstance().setOAuth2TokenValidationService(tokenValidationService);
    }

    /**
     * Unsets OAuth2TokenValidation Service.
     *
     * @param tokenValidationService An instance of OAuth2TokenValidationService
     */
    @SuppressWarnings("unused")
    protected void unsetOAuth2ValidationService(OAuth2TokenValidationService tokenValidationService) {
        if (log.isDebugEnabled()) {
            log.debug("Unsetting OAuth2TokenValidationService Service");
        }
        OAuthAuthenticatorDataHolder.getInstance().setOAuth2TokenValidationService(null);
    }
}