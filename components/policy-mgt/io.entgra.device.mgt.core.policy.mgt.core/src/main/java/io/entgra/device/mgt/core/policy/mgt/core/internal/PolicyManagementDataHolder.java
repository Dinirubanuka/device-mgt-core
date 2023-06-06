/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * you may obtain a copy of the License at
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

package io.entgra.device.mgt.core.policy.mgt.core.internal;

import io.entgra.device.mgt.core.device.mgt.core.config.DeviceConfigurationManager;
import io.entgra.device.mgt.core.device.mgt.core.config.policy.PolicyConfiguration;
import io.entgra.device.mgt.core.device.mgt.core.service.DeviceManagementProviderService;
import io.entgra.device.mgt.core.device.mgt.core.service.GroupManagementProviderService;
import io.entgra.device.mgt.core.policy.mgt.common.PolicyEvaluationPoint;
import io.entgra.device.mgt.core.policy.mgt.common.PolicyInformationPoint;
import io.entgra.device.mgt.core.policy.mgt.core.PolicyManagerService;
import io.entgra.device.mgt.core.policy.mgt.core.mgt.MonitoringManager;
import io.entgra.device.mgt.core.policy.mgt.core.mgt.PolicyManager;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.ntask.core.service.TaskService;
import org.wso2.carbon.user.core.service.RealmService;

import java.util.HashMap;
import java.util.Map;

public class PolicyManagementDataHolder {

    private RealmService realmService;
    private final Map<String, PolicyEvaluationPoint> policyEvaluationPoints = new HashMap<>();
    private PolicyInformationPoint policyInformationPoint;
    private DeviceManagementProviderService deviceManagementService;
    private GroupManagementProviderService groupManagementService;
    private PolicyManagerService policyManagerService;
    private MonitoringManager monitoringManager;
    private PolicyManager policyManager;
    private TaskService taskService;

    private static final PolicyManagementDataHolder thisInstance = new PolicyManagementDataHolder();

    private PolicyManagementDataHolder() {}

    public static PolicyManagementDataHolder getInstance() {
        return thisInstance;
    }

    public PolicyManager getPolicyManager() {
        return policyManager;
    }

    public void setPolicyManager(PolicyManager policyManager) {
        this.policyManager = policyManager;
    }

    public MonitoringManager getMonitoringManager() {
        return monitoringManager;
    }

    public void setMonitoringManager(MonitoringManager monitoringManager) {
        this.monitoringManager = monitoringManager;
    }

    public RealmService getRealmService() {
        return realmService;
    }

    public void setRealmService(RealmService realmService) {
        this.realmService = realmService;
    }

    public PolicyEvaluationPoint getPolicyEvaluationPoint() {
        PolicyConfiguration policyConfiguration = DeviceConfigurationManager.getInstance().
                getDeviceManagementConfig().getPolicyConfiguration();
        String policyEvaluationPointName = policyConfiguration.getPolicyEvaluationPointName();
        return policyEvaluationPoints.get(policyEvaluationPointName);
    }

    public void setPolicyEvaluationPoint(String name, PolicyEvaluationPoint policyEvaluationPoint) {
        policyEvaluationPoints.put(name,policyEvaluationPoint);
    }

    public void removePolicyEvaluationPoint(PolicyEvaluationPoint policyEvaluationPoint) {
        policyEvaluationPoints.remove(policyEvaluationPoint.getName());
    }


    public PolicyInformationPoint getPolicyInformationPoint() {
        return policyInformationPoint;
    }

    public void setPolicyInformationPoint(PolicyInformationPoint policyInformationPoint) {
        this.policyInformationPoint = policyInformationPoint;
    }

    public DeviceManagementProviderService getDeviceManagementService() {
        return deviceManagementService;
    }

    public void setDeviceManagementService(DeviceManagementProviderService deviceManagementService) {
        this.deviceManagementService = deviceManagementService;
    }

    public TaskService getTaskService() {
        return taskService;
    }

    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }

    public synchronized GroupManagementProviderService getGroupManagementService() {
        if (groupManagementService == null) {
            PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
            groupManagementService = (GroupManagementProviderService)
                    ctx.getOSGiService(GroupManagementProviderService.class, null);
            if (groupManagementService == null) {
                String msg = "GroupImpl Management service has not initialized.";
                throw new IllegalStateException(msg);
            }
        }
        return groupManagementService;
    }

    public PolicyManagerService getPolicyManagerService() {
        return policyManagerService;
    }

    public void setPolicyManagerService(PolicyManagerService policyManagerService) {
        this.policyManagerService = policyManagerService;
    }
}
