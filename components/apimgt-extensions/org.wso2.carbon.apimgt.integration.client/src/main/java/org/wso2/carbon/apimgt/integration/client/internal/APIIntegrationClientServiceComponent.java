/*
 *   Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */
package org.wso2.carbon.apimgt.integration.client.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.wso2.carbon.apimgt.integration.client.IntegrationClientServiceImpl;
import org.wso2.carbon.apimgt.integration.client.configs.APIMConfigReader;
import org.wso2.carbon.apimgt.integration.client.service.IntegrationClientService;
import org.osgi.service.component.annotations.*;
import io.entgra.device.mgt.core.identity.jwt.client.extension.service.JWTClientManagerService;

@Component(
        name = "org.wso2.carbon.api.integration.client",
        immediate = true)
public class APIIntegrationClientServiceComponent {

    private static Log log = LogFactory.getLog(APIIntegrationClientServiceComponent.class);

    @Activate
    protected void activate(ComponentContext componentContext) {
        try {
            if (log.isDebugEnabled()) {
                log.debug("Initializing apimgt client bundle");
            }

            /* Initializing webapp publisher configuration */
            APIMConfigReader.init();
            BundleContext bundleContext = componentContext.getBundleContext();
            bundleContext.registerService(IntegrationClientService.class.getName(), IntegrationClientServiceImpl.getInstance(), null);

            if (log.isDebugEnabled()) {
                log.debug("apimgt client bundle has been successfully initialized");
            }
        } catch (Throwable e) {
            log.error("Error occurred while initializing apimgt client bundle", e);
        }
    }

    @Deactivate
    protected void deactivate(ComponentContext componentContext) {
        //do nothing
    }

    @Reference(
            name = "api.integration.client.service",
            service = org.wso2.carbon.apimgt.impl.APIManagerConfigurationService.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            bind = "setJWTClientManagerService",
            unbind = "unsetJWTClientManagerService")
    protected void setJWTClientManagerService(JWTClientManagerService jwtClientManagerService) {
        if (jwtClientManagerService != null) {
            log.debug("jwtClientManagerService service is initialized");
        }
        APIIntegrationClientDataHolder.getInstance().setJwtClientManagerService(jwtClientManagerService);
    }

    protected void unsetJWTClientManagerService(JWTClientManagerService jwtClientManagerService) {
        APIIntegrationClientDataHolder.getInstance().setJwtClientManagerService(null);
    }

}
