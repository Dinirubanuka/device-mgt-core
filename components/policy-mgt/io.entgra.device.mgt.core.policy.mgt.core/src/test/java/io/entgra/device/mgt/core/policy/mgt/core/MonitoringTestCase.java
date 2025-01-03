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


package io.entgra.device.mgt.core.policy.mgt.core;

import io.entgra.device.mgt.core.device.mgt.common.Device;
import io.entgra.device.mgt.core.device.mgt.common.DeviceIdentifier;
import io.entgra.device.mgt.core.device.mgt.common.exceptions.DeviceManagementException;
import io.entgra.device.mgt.core.device.mgt.common.operation.mgt.OperationManager;
import io.entgra.device.mgt.core.device.mgt.common.policy.mgt.Policy;
import io.entgra.device.mgt.core.device.mgt.common.policy.mgt.monitor.PolicyComplianceException;
import io.entgra.device.mgt.core.device.mgt.core.device.details.mgt.impl.DeviceInformationManagerImpl;
import io.entgra.device.mgt.core.device.mgt.core.internal.DeviceManagementDataHolder;
import io.entgra.device.mgt.core.device.mgt.core.operation.mgt.OperationManagerImpl;
import io.entgra.device.mgt.core.device.mgt.core.service.DeviceManagementProviderService;
import io.entgra.device.mgt.core.device.mgt.core.service.DeviceManagementProviderServiceImpl;
import io.entgra.device.mgt.core.device.mgt.core.service.GroupManagementProviderServiceImpl;
import io.entgra.device.mgt.core.policy.mgt.common.PolicyManagementException;
import io.entgra.device.mgt.core.policy.mgt.core.internal.PolicyManagementDataHolder;
import io.entgra.device.mgt.core.policy.mgt.core.mgt.MonitoringManager;
import io.entgra.device.mgt.core.policy.mgt.core.mgt.PolicyManager;
import io.entgra.device.mgt.core.policy.mgt.core.mgt.impl.MonitoringManagerImpl;
import io.entgra.device.mgt.core.policy.mgt.core.mgt.impl.PolicyManagerImpl;
import io.entgra.device.mgt.core.policy.mgt.core.services.PolicyMonitoringManagerTest;
import junit.framework.Assert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.Test;

import java.util.List;

public class MonitoringTestCase extends BasePolicyManagementDAOTest {

    private static final Log log = LogFactory.getLog(MonitoringTestCase.class);

    private static final String ANDROID = "android";

    private DeviceIdentifier identifier = new DeviceIdentifier();

    @Test
    public void testMonitorDao() {

        DeviceManagementProviderService service = new DeviceManagementProviderServiceImpl();
        DeviceManagementDataHolder.getInstance().setDeviceManagementProvider(service);
        DeviceManagementDataHolder.getInstance().setDeviceInformationManager(new DeviceInformationManagerImpl());
        DeviceManagementDataHolder.getInstance().setGroupManagementProviderService(new GroupManagementProviderServiceImpl());
        PolicyManagerService policyManagerService = new PolicyManagerServiceImpl();
        PolicyManagementDataHolder.getInstance().setPolicyManagerService(policyManagerService);

        List<Policy> policies = null;
        List<Device> devices = null;
        try {
            policies = policyManagerService.getPolicies(ANDROID);
            devices = service.getAllDevices(ANDROID);
        } catch (PolicyManagementException e) {
            log.error("Error occurred while retrieving the list of policies defined against the device type '" +
                    ANDROID + "'", e);
            Assert.fail();
        } catch (DeviceManagementException e) {
            log.error("Error occurred while retrieving the list of devices pertaining to the type '" +
                    ANDROID + "'", e);
            Assert.fail();
        }

        for (Policy policy : policies) {
            log.debug("Policy Name : " + policy.getPolicyName());
        }

        for (Device device : devices) {
            log.debug("Device Name : " + device.getDeviceIdentifier());
        }

        identifier.setType(ANDROID);
        identifier.setId(devices.get(0).getDeviceIdentifier());

//        PolicyAdministratorPoint administratorPoint = new PolicyAdministratorPointImpl();
//
//        administratorPoint.setPolicyUsed(identifier, policies.get(0));

    }

    @Test(dependsOnMethods = ("testMonitorDao"))
    public void getDeviceAppliedPolicy() throws PolicyManagementException {

        PolicyManager manager = new PolicyManagerImpl();
        Policy policy = null;

        policy = manager.getAppliedPolicyToDevice(identifier);


        if (policy != null) {

            log.debug(policy.getId());
            log.debug(policy.getPolicyName());
            log.debug(policy.getCompliance());
        } else {
            log.debug("Applied policy was a null object.");
        }
    }


    @Test(dependsOnMethods = ("getDeviceAppliedPolicy"))
    public void addComplianceOperation() throws PolicyManagementException, DeviceManagementException,
            PolicyComplianceException {

        log.debug("Compliance operations adding started.");

        PolicyManager manager = new PolicyManagerImpl();
        Policy policy = null;

        policy = manager.getAppliedPolicyToDevice(identifier);

        OperationManager operationManager = new OperationManagerImpl();

        DeviceManagementDataHolder.getInstance().setOperationManager(operationManager);

        if (policy != null) {
            log.debug(policy.getId());
            log.debug(policy.getPolicyName());
            log.debug(policy.getCompliance());
        }

        MonitoringManager monitoringManager = new MonitoringManagerImpl();

        DeviceManagementProviderService service = new DeviceManagementProviderServiceImpl();
        List<Device> devices = service.getAllDevices(ANDROID, false);

        // monitoringManager.addMonitoringOperation(devices);

        log.debug("Compliance operations adding done.");

    }


    @Test(dependsOnMethods = ("addComplianceOperation"))
    public void checkComplianceFromMonitoringService() throws PolicyManagementException, DeviceManagementException,
            PolicyComplianceException {


        PolicyMonitoringManagerTest monitoringServiceTest = new PolicyMonitoringManagerTest();
        TestDeviceManagementProviderService deviceManagementProviderService = new TestDeviceManagementProviderService();
        deviceManagementProviderService.setPolicyMonitoringManager(monitoringServiceTest);
        PolicyManagementDataHolder.getInstance().setDeviceManagementService(deviceManagementProviderService);

        DeviceManagementProviderService adminService = new DeviceManagementProviderServiceImpl();

        // PolicyManager policyManagerService = new PolicyManagerImpl();

        List<Device> devices = adminService.getAllDevices();

        for (Device device : devices) {
            log.debug(device.getDeviceIdentifier());
            log.debug(device.getType());
            log.debug(device.getName());
        }

        PolicyManager manager = new PolicyManagerImpl();
        Policy policy = null;

        policy = manager.getAppliedPolicyToDevice(identifier);

        if (policy != null) {
            Object ob = new Object();

            monitoringServiceTest.checkPolicyCompliance(identifier, policy, ob);
        }
    }


    @Test(dependsOnMethods = ("checkComplianceFromMonitoringService"))
    public void checkCompliance() throws DeviceManagementException, PolicyComplianceException,
            PolicyManagementException {

        PolicyMonitoringManagerTest monitoringServiceTest = new PolicyMonitoringManagerTest();
        TestDeviceManagementProviderService deviceManagementProviderService = new TestDeviceManagementProviderService();
        deviceManagementProviderService.setPolicyMonitoringManager(monitoringServiceTest);
        PolicyManagementDataHolder.getInstance().setDeviceManagementService(deviceManagementProviderService);

        DeviceManagementProviderService adminService = new DeviceManagementProviderServiceImpl();

        List<Device> devices = adminService.getAllDevices();

        DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
        deviceIdentifier.setId(devices.get(0).getDeviceIdentifier());
        deviceIdentifier.setType(devices.get(0).getType());

        Object ob = new Object();

        MonitoringManager monitoringManager = new MonitoringManagerImpl();

        log.debug(identifier.getId());
        log.debug(identifier.getType());


        monitoringManager.checkPolicyCompliance(identifier, ob);


    }

}
