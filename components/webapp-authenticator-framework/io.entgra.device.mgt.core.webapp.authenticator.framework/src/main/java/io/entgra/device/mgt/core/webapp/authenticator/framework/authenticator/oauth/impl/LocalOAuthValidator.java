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
package io.entgra.device.mgt.core.webapp.authenticator.framework.authenticator.oauth.impl;

import io.entgra.device.mgt.core.webapp.authenticator.framework.authenticator.oauth.OAuth2TokenValidator;
import io.entgra.device.mgt.core.webapp.authenticator.framework.authenticator.oauth.OAuthConstants;
import io.entgra.device.mgt.core.webapp.authenticator.framework.authenticator.oauth.OAuthTokenValidationException;
import io.entgra.device.mgt.core.webapp.authenticator.framework.authenticator.oauth.OAuthValidationResponse;
import io.entgra.device.mgt.core.webapp.authenticator.framework.internal.AuthenticatorFrameworkDataHolder;
import org.wso2.carbon.identity.oauth2.dto.OAuth2TokenValidationRequestDTO;
import org.wso2.carbon.identity.oauth2.dto.OAuth2TokenValidationResponseDTO;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;
import org.wso2.carbon.utils.multitenancy.MultitenantUtils;

/**
 * Handles the OAuth2 token validation from the same server using OSGi services.
 */
public class LocalOAuthValidator implements OAuth2TokenValidator {

    @Override
    public OAuthValidationResponse validateToken(String accessToken, String resource)
            throws OAuthTokenValidationException {
        OAuth2TokenValidationRequestDTO validationRequest = new OAuth2TokenValidationRequestDTO();
        OAuth2TokenValidationRequestDTO.OAuth2AccessToken oauthToken =
                validationRequest.new OAuth2AccessToken();
        oauthToken.setTokenType(OAuthConstants.BEARER_TOKEN_TYPE);
        oauthToken.setIdentifier(accessToken);
        validationRequest.setAccessToken(oauthToken);

        //Set the resource context param. This will be used in scope validation.
        OAuth2TokenValidationRequestDTO.TokenValidationContextParam
                resourceContextParam = validationRequest.new TokenValidationContextParam();
        resourceContextParam.setKey(OAuthConstants.RESOURCE_KEY);
        resourceContextParam.setValue(resource);

        OAuth2TokenValidationRequestDTO.TokenValidationContextParam[]
                tokenValidationContextParams =
                new OAuth2TokenValidationRequestDTO.TokenValidationContextParam[1];
        tokenValidationContextParams[0] = resourceContextParam;
        validationRequest.setContext(tokenValidationContextParams);

        OAuth2TokenValidationResponseDTO tokenValidationResponse = AuthenticatorFrameworkDataHolder.getInstance().
                getOAuth2TokenValidationService().findOAuthConsumerIfTokenIsValid(
                validationRequest).getAccessTokenValidationResponse();
        boolean isValid = tokenValidationResponse.isValid();
        String userName;
        String tenantDomain;
        if (isValid) {
            userName = MultitenantUtils.getTenantAwareUsername(
                    tokenValidationResponse.getAuthorizedUser());
            tenantDomain =
                    MultitenantUtils.getTenantDomain(tokenValidationResponse.getAuthorizedUser());
            if (MultitenantConstants.SUPER_TENANT_DOMAIN_NAME.equals(tenantDomain)) {
                tenantDomain = MultitenantUtils.getTenantDomain(userName);
            }
            return new OAuthValidationResponse(userName, tenantDomain, true);
        } else {
            OAuthValidationResponse oAuthValidationResponse = new OAuthValidationResponse();
            oAuthValidationResponse.setErrorMsg(tokenValidationResponse.getErrorMsg());
            return oAuthValidationResponse;
        }
    }
}
