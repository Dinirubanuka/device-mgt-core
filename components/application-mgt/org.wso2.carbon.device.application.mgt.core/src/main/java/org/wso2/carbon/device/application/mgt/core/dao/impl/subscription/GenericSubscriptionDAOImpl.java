/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.device.application.mgt.core.dao.impl.subscription;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.wso2.carbon.device.application.mgt.common.dto.ApplicationDTO;
import org.wso2.carbon.device.application.mgt.common.dto.DeviceSubscriptionDTO;
import org.wso2.carbon.device.application.mgt.common.exception.DBConnectionException;
import org.wso2.carbon.device.application.mgt.core.dao.SubscriptionDAO;
import org.wso2.carbon.device.application.mgt.core.dao.common.Util;
import org.wso2.carbon.device.application.mgt.core.dao.impl.AbstractDAOImpl;
import org.wso2.carbon.device.application.mgt.core.exception.ApplicationManagementDAOException;
import org.wso2.carbon.device.application.mgt.core.exception.UnexpectedServerErrorException;
import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.group.mgt.DeviceGroup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class GenericSubscriptionDAOImpl extends AbstractDAOImpl implements SubscriptionDAO {
    private static Log log = LogFactory.getLog(GenericSubscriptionDAOImpl.class);

    @Override
    public void subscribeDeviceToApplication(int tenantId, String subscribedBy, List<Device> deviceList, int appId,
            int releaseId, String installStatus) throws ApplicationManagementDAOException {
        Connection conn;
        PreparedStatement stmt = null;
        try {
            conn = this.getDBConnection();
            long time = System.currentTimeMillis() / 1000;
            String sql = "INSERT INTO AP_DEVICE_SUBSCRIPTION(TENANT_ID, SUBSCRIBED_BY, SUBSCRIBED_TIMESTAMP, "
                    + "DM_DEVICE_ID, AP_APP_RELEASE_ID, AP_APP_ID, INSTALL_STATUS) VALUES (?, ?, ?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            for (Device device : deviceList) {
                stmt.setInt(1, tenantId);
                stmt.setString(2, subscribedBy);
                stmt.setLong(3, time);
                stmt.setInt(4, device.getId());
                stmt.setInt(5, releaseId);
                stmt.setInt(6, appId);
                stmt.setString(7, installStatus);
                stmt.addBatch();
                if (log.isDebugEnabled()) {
                    log.debug("Adding a mapping to device ID[" + device.getId() + "] to the application [" + appId
                            + "], release[" + releaseId + "]");
                }
            }
            stmt.executeBatch();
        } catch (SQLException | DBConnectionException e) {
            throw new ApplicationManagementDAOException("Error occurred while adding device application mapping to DB",
                    e);
        } finally {
            Util.cleanupResources(stmt, null);
        }
    }

    @Override
    public void subscribeUserToApplication(int tenantId, String subscribedBy, List<String> userList, int appId,
            int releaseId) throws ApplicationManagementDAOException {
        Connection conn;
        PreparedStatement stmt = null;
        try {
            conn = this.getDBConnection();
            long time = System.currentTimeMillis() / 1000;
            String sql = "INSERT INTO AP_USER_SUBSCRIPTION(TENANT_ID, SUBSCRIBED_BY, SUBSCRIBED_TIMESTAMP, "
                    + "USER_NAME, AP_APP_RELEASE_ID, AP_APP_ID) VALUES (?, ?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            for (String user : userList) {
                stmt = conn.prepareStatement(sql);
                stmt.setInt(1, tenantId);
                stmt.setString(2, subscribedBy);
                stmt.setLong(3, time);
                stmt.setString(4, user);
                stmt.setInt(5, releaseId);
                stmt.setInt(6, appId);
                stmt.addBatch();
                if (log.isDebugEnabled()) {
                    log.debug("Adding a mapping to user[" + user + "] to the application [" + appId + "], release["
                            + releaseId + "]");
                }
            }
            stmt.executeBatch();
        } catch (SQLException | DBConnectionException e) {
            throw new ApplicationManagementDAOException("Error occurred while adding device application mapping to DB",
                    e);
        } finally {
            Util.cleanupResources(stmt, null);
        }
    }

    @Override
    public void subscribeRoleToApplication(int tenantId, String subscribedBy, List<String> roleList, int appId,
            int releaseId) throws ApplicationManagementDAOException {
        Connection conn;
        PreparedStatement stmt = null;
        try {
            conn = this.getDBConnection();
            long time = System.currentTimeMillis() / 1000;
            String sql = "INSERT INTO AP_ROLE_SUBSCRIPTION(TENANT_ID, SUBSCRIBED_BY, SUBSCRIBED_TIMESTAMP, "
                    + "ROLE_NAME, AP_APP_RELEASE_ID, AP_APP_ID) VALUES (?, ?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            for (String role : roleList) {
                stmt.setInt(1, tenantId);
                stmt.setString(2, subscribedBy);
                stmt.setLong(3, time);
                stmt.setString(4, role);
                stmt.setInt(5, releaseId);
                stmt.setInt(6, appId);
                stmt.addBatch();
                if (log.isDebugEnabled()) {
                    log.debug("Adding a mapping to role[" + role + "] to the application [" + appId + "], release["
                            + releaseId + "]");
                }
            }
            stmt.executeBatch();
        } catch (SQLException | DBConnectionException e) {
            throw new ApplicationManagementDAOException("Error occurred while adding device application mapping to DB",
                    e);
        } finally {
            Util.cleanupResources(stmt, null);
        }
    }

    @Override
    public void subscribeGroupToApplication(int tenantId, String subscribedBy, List<DeviceGroup> groupList, int appId,
            int releaseId) throws ApplicationManagementDAOException {
        Connection conn;
        PreparedStatement stmt = null;
        try {
            conn = this.getDBConnection();
            long time = System.currentTimeMillis() / 1000;
            String sql = "INSERT INTO AP_GROUP_SUBSCRIPTION(TENANT_ID, SUBSCRIBED_BY, SUBSCRIBED_TIMESTAMP, "
                    + "DM_GROUP_ID, AP_APP_RELEASE_ID, AP_APP_ID) VALUES (?, ?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            for (DeviceGroup group : groupList) {
                stmt.setInt(1, tenantId);
                stmt.setString(2, subscribedBy);
                stmt.setLong(3, time);
                stmt.setInt(4, group.getGroupId());
                stmt.setInt(5, releaseId);
                stmt.setInt(6, appId);
                stmt.addBatch();
                if (log.isDebugEnabled()) {
                    log.debug("Adding a mapping to group ID[" + group.getGroupId() + "] to the application [" + appId
                            + "], release[" + releaseId + "]");
                }
            }
            stmt.executeBatch();
        } catch (SQLException | DBConnectionException e) {
            throw new ApplicationManagementDAOException("Error occurred while adding device application mapping to DB",
                    e);
        } finally {
            Util.cleanupResources(stmt, null);
        }
    }

    @Override
    public List<DeviceSubscriptionDTO> getDeviceSubscriptions(int appReleaseId, int tenantId) throws
            ApplicationManagementDAOException {
        if (log.isDebugEnabled()) {
            log.debug("Getting device subscriptions for the application release id " + appReleaseId
                    + " from the database");
        }
        Connection conn;
        try {
            conn = this.getDBConnection();
            String sql = "SELECT "
                    + "DS.ID AS ID, "
                    + "DS.SUBSCRIBED_BY AS SUBSCRIBED_BY, "
                    + "DS.SUBSCRIBED_TIMESTAMP AS SUBSCRIBED_AT, "
                    + "DS.UNSUBSCRIBED AS IS_UNSUBSCRIBED, "
                    + "DS.UNSUBSCRIBED_BY AS UNSUBSCRIBED_BY, "
                    + "DS.UNSUBSCRIBED_TIMESTAMP AS UNSUBSCRIBED_AT, "
                    + "DS.SUBSCRIBED_FROM AS SUBSCRIBED_FROM, "
                    + "DS.DM_DEVICE_ID AS DEVICE_ID "
                    + "FROM AP_DEVICE_SUBSCRIPTION DS "
                    + "WHERE DS.AP_APP_RELEASE_ID = ? AND DS.TENANT_ID=?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, appReleaseId);
                stmt.setInt(2, tenantId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (log.isDebugEnabled()) {
                        log.debug("Successfully retrieved device subscriptions for application release id "
                                + appReleaseId);
                    }
                    return Util.loadDeviceSubscriptions(rs);
                }
            }
        } catch (SQLException e) {
            String msg =
                    "Error occurred while getting device subscription data for application ID: " + appReleaseId + ".";
            log.error(msg);
            throw new ApplicationManagementDAOException(msg, e);
        } catch (DBConnectionException e) {
            String msg =
                    "Error occurred while obtaining the DB connection for getting device subscription for applicationID: "
                            + appReleaseId + ".";
            log.error(msg);
            throw new ApplicationManagementDAOException(msg, e);
        }
    }
}
