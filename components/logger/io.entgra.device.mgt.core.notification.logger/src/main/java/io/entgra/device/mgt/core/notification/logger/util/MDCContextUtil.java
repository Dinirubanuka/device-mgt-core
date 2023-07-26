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
package io.entgra.device.mgt.core.notification.logger.util;

import io.entgra.device.mgt.core.notification.logger.DeviceLogContext;
import io.entgra.device.mgt.core.notification.logger.UserLogContext;
import org.apache.log4j.MDC;

public final class MDCContextUtil {

    public static void populateDeviceMDCContext(final DeviceLogContext mdcContext) {
        if (mdcContext.getDeviceName() != null) {
            MDC.put("DeviceName", mdcContext.getDeviceName());
        }
        if (mdcContext.getDeviceType() != null) {
            MDC.put("DeviceType", mdcContext.getDeviceType());
        }
        if (mdcContext.getOperationCode() != null) {
            MDC.put("OperationCode", mdcContext.getOperationCode());
        }
        if (mdcContext.getTenantID() != null) {
            MDC.put("TenantId", mdcContext.getTenantID());
        }
    }

    public static void populateUserMDCContext(final UserLogContext mdcContext) {
        if (mdcContext.getUserName() != null) {
            MDC.put("UserName", mdcContext.getUserName());
        }
        if (mdcContext.getUserEmail() != null) {
            MDC.put("UserEmail", mdcContext.getUserEmail());
        }
        if (mdcContext.getMetaInfo() != null) {
            MDC.put("MetaInfo", mdcContext.getMetaInfo());
        }
        if (mdcContext.getTenantID() != null) {
            MDC.put("TenantId", mdcContext.getTenantID());
        }
        if (mdcContext.isUserRegistered()) {
            MDC.put("IsUserRegistered", "Registered");
        }
        if (mdcContext.isDeviceRegisterged()) {
            MDC.put("IsDeviceRegistered", mdcContext.isDeviceRegisterged());
        }
        if (mdcContext.getTenantDomain() != null) {
            MDC.put("TenantDomain", mdcContext.getTenantDomain());
        }

    }
}


