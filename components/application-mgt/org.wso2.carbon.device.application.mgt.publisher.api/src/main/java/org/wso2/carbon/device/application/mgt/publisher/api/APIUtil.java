/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.device.application.mgt.publisher.api;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.device.application.mgt.common.services.*;
import org.wso2.carbon.device.application.mgt.publisher.api.beans.ErrorResponse;

import javax.ws.rs.core.Response;


/**
 * Holds util methods required for Application-Mgt API component.
 */
public class APIUtil {

    private static Log log = LogFactory.getLog(APIUtil.class);

    private static ApplicationManager applicationManager;
    private static ApplicationStorageManager applicationStorageManager;
    private static SubscriptionManager subscriptionManager;
    private static UnrestrictedRoleManager unrestrictedRoleManager;


    public static ApplicationManager getApplicationManager() {
        if (applicationManager == null) {
            synchronized (APIUtil.class) {
                if (applicationManager == null) {
                    PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
                    applicationManager =
                            (ApplicationManager) ctx.getOSGiService(ApplicationManager.class, null);
                    if (applicationManager == null) {
                        String msg = "Application Manager service has not initialized.";
                        log.error(msg);
                        throw new IllegalStateException(msg);
                    }
                }
            }
        }

        return applicationManager;
    }

    /**
     * To get the Application Storage Manager from the osgi context.
     * @return ApplicationStoreManager instance in the current osgi context.
     */
    public static ApplicationStorageManager getApplicationStorageManager() {
        if (applicationStorageManager == null) {
            synchronized (APIUtil.class) {
                if (applicationStorageManager == null) {
                    PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
                    applicationStorageManager = (ApplicationStorageManager) ctx
                            .getOSGiService(ApplicationStorageManager.class, null);
                    if (applicationStorageManager == null) {
                        String msg = "Application Storage Manager service has not initialized.";
                        log.error(msg);
                        throw new IllegalStateException(msg);
                    }
                }
            }
        }
        return applicationStorageManager;
    }

    public static Response getResponse(Exception ex, Response.Status status) {
        return getResponse(ex.getMessage(), status);
    }

    public static Response getResponse(String message, Response.Status status) {
        ErrorResponse errorMessage = new ErrorResponse();
        errorMessage.setMessage(message);
        if (status == null) {
            status = Response.Status.INTERNAL_SERVER_ERROR;
        }
        errorMessage.setCode(status.getStatusCode());
        return Response.status(status).entity(errorMessage).build();
    }

    /**
     * To get the Subscription Manager from the osgi context.
     * @return SubscriptionManager instance in the current osgi context.
     */
    public static SubscriptionManager getSubscriptionManager() {
        if (subscriptionManager == null) {
            synchronized (APIUtil.class) {
                if (subscriptionManager == null) {
                    PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
                    subscriptionManager =
                            (SubscriptionManager) ctx.getOSGiService(SubscriptionManager.class, null);
                    if (subscriptionManager == null) {
                        String msg = "Subscription Manager service has not initialized.";
                        log.error(msg);
                        throw new IllegalStateException(msg);
                    }
                }
            }
        }

        return subscriptionManager;
    }

    /**
     * To get the Unrestricted Role manager from the osgi context.
     * @return Unrestricted Role manager instance in the current osgi context.
     */
    public static UnrestrictedRoleManager getUnrestrictedRoleManager() {
        if (unrestrictedRoleManager == null) {
            synchronized (APIUtil.class) {
                if (unrestrictedRoleManager == null) {
                    PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
                    unrestrictedRoleManager =
                            (UnrestrictedRoleManager) ctx.getOSGiService(UnrestrictedRoleManager.class, null);
                    if (unrestrictedRoleManager == null) {
                        String msg = "Subscription Manager service has not initialized.";
                        log.error(msg);
                        throw new IllegalStateException(msg);
                    }
                }
            }
        }

        return unrestrictedRoleManager;
    }
}
