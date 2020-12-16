/*
 * Copyright (c) 2020, Entgra (Pvt) Ltd. (http://www.entgra.io) All Rights Reserved.
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

package org.wso2.carbon.device.mgt.core.geo.task;

import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.DeviceManagementConstants;
import org.wso2.carbon.device.mgt.common.geo.service.GeoFenceEventMeta;
import org.wso2.carbon.device.mgt.common.geo.service.GeofenceData;
import org.wso2.carbon.device.mgt.core.event.config.GroupEventOperationExecutor;
import org.wso2.carbon.device.mgt.core.event.config.EventOperationExecutor;

import java.util.List;

/**
 * Responsible for Event operation task creation management.
 * Wrap event operation executor creation
 */
public class GeoFenceEventOperationManager {
    private final int tenantId;
    private final String eventOperationCode;
    private final EventCreateCallback callback;

    public GeoFenceEventOperationManager(String eventOperationCode, int tenantId, EventCreateCallback callback) {
        this.eventOperationCode = eventOperationCode;
        this.tenantId = tenantId;
        this.callback = callback;
    }

    /**
     * Get executor for create EVENT_CONFIG / EVENT_REVOKE operations at the time of a geofence
     * created, updated or deleted
     * @param geofenceData created geofence data object
     * @return {@link EventOperationExecutor} Created executor to create operations
     */
    public EventOperationExecutor getEventOperationExecutor(GeofenceData geofenceData) {
        GeoFenceEventMeta geoFenceEventMeta = new GeoFenceEventMeta(geofenceData);
        EventOperationExecutor executor = new EventOperationExecutor(geoFenceEventMeta, geofenceData.getGroupIds(),
                tenantId, DeviceManagementConstants.EventServices.GEOFENCE, eventOperationCode);
        executor.setCallback(callback);
        return executor;
    }

    /**
     * Get executor for create EVENT_CONFIG / EVENT_REVOKE operations at the time of a device/s
     * assigned into a group or removed from a group
     * @param groupId Id of the assigned / removed group
     * @param deviceIdentifiers Device identifiers assigned to / removed from the group
     * @return {@link GroupEventOperationExecutor} Created executor to create operations
     */
    public GroupEventOperationExecutor getEventOperationExecutor(int groupId, List<DeviceIdentifier> deviceIdentifiers) {
        GroupEventOperationExecutor executor = new GroupEventOperationExecutor(groupId, deviceIdentifiers, tenantId, eventOperationCode);
        executor.setCallback(callback);
        return executor;
    }
}