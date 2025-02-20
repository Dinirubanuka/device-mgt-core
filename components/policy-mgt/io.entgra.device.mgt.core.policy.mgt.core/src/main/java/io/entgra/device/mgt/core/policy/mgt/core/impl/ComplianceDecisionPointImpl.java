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


package io.entgra.device.mgt.core.policy.mgt.core.impl;

import io.entgra.device.mgt.core.device.mgt.common.Device;
import io.entgra.device.mgt.core.device.mgt.common.DeviceIdentifier;
import io.entgra.device.mgt.core.device.mgt.common.EnrolmentInfo;
import io.entgra.device.mgt.core.device.mgt.common.exceptions.DeviceManagementException;
import io.entgra.device.mgt.core.device.mgt.common.exceptions.InvalidDeviceException;
import io.entgra.device.mgt.core.device.mgt.common.operation.mgt.Operation;
import io.entgra.device.mgt.core.device.mgt.common.operation.mgt.OperationManagementException;
import io.entgra.device.mgt.core.device.mgt.common.policy.mgt.Policy;
import io.entgra.device.mgt.core.device.mgt.common.policy.mgt.ProfileFeature;
import io.entgra.device.mgt.core.device.mgt.common.policy.mgt.monitor.ComplianceFeature;
import io.entgra.device.mgt.core.device.mgt.common.policy.mgt.monitor.NonComplianceData;
import io.entgra.device.mgt.core.device.mgt.common.policy.mgt.monitor.PolicyComplianceException;
import io.entgra.device.mgt.core.device.mgt.core.operation.mgt.PolicyOperation;
import io.entgra.device.mgt.core.device.mgt.core.operation.mgt.ProfileOperation;
import io.entgra.device.mgt.core.device.mgt.core.service.DeviceManagementProviderService;
import io.entgra.device.mgt.core.policy.mgt.common.monitor.ComplianceDecisionPoint;
import io.entgra.device.mgt.core.policy.mgt.core.internal.PolicyManagementDataHolder;
import io.entgra.device.mgt.core.policy.mgt.core.util.PolicyManagementConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

public class ComplianceDecisionPointImpl implements ComplianceDecisionPoint {

    private static final Log log = LogFactory.getLog(ComplianceDecisionPointImpl.class);

    @Override
    public String getNoneComplianceRule(Policy policy) throws PolicyComplianceException {
        return policy.getCompliance();
    }

    @Override
    public void setDevicesAsUnreachable(List<DeviceIdentifier> deviceIdentifiers) throws PolicyComplianceException {

        try {
            DeviceManagementProviderService service = this.getDeviceManagementProviderService();
            for (DeviceIdentifier deviceIdentifier : deviceIdentifiers) {
                Device device = service.getDevice(deviceIdentifier, false);
                service.setStatus(device, EnrolmentInfo.Status.UNREACHABLE);
            }
        } catch (DeviceManagementException e) {
            String msg = "Error occurred while setting the device as unreachable";
            log.error(msg, e);
            throw new PolicyComplianceException(msg, e);
        }

    }

    @Override
    public void setDevicesAsInactive(List<DeviceIdentifier> deviceIdentifiers) throws PolicyComplianceException {
        try {
            DeviceManagementProviderService service = this.getDeviceManagementProviderService();
            for (DeviceIdentifier deviceIdentifier : deviceIdentifiers) {
                Device device = service.getDevice(deviceIdentifier, false);
                service.setStatus(device, EnrolmentInfo.Status.INACTIVE);
            }
        } catch (DeviceManagementException e) {
            String msg = "Error occurred while setting the device as inactive";
            log.error(msg, e);
            throw new PolicyComplianceException(msg, e);
        }
    }

    @Override
    public void setDevicesAsUnreachableWith(List<Device> devices) throws PolicyComplianceException {
        try {
            DeviceManagementProviderService service = this.getDeviceManagementProviderService();
            for (Device device : devices) {
                DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
                deviceIdentifier.setId(device.getDeviceIdentifier());
                deviceIdentifier.setType(device.getType());
                service.setStatus(device, EnrolmentInfo.Status.UNREACHABLE);
            }
        } catch (DeviceManagementException e) {
            String msg = "Error occurred while setting the device as unreachable";
            log.error(msg, e);
            throw new PolicyComplianceException(msg, e);
        }
    }

    @Override
    public void setDeviceAsReachable(DeviceIdentifier deviceIdentifier) throws PolicyComplianceException {

        try {

            DeviceManagementProviderService service = this.getDeviceManagementProviderService();
            Device device = service.getDevice(deviceIdentifier, false);
            service.setStatus(device, EnrolmentInfo.Status.ACTIVE);

        } catch (DeviceManagementException e) {
            String msg = "Error occurred while setting the device as reachable for " +
                    deviceIdentifier.getId() + " - " + deviceIdentifier.getType();
            log.error(msg, e);
            throw new PolicyComplianceException(msg, e);
        }

    }

    @Override
    public void reEnforcePolicy(DeviceIdentifier deviceIdentifier, NonComplianceData complianceData) throws
            PolicyComplianceException {

        // do not re-enforce policy if the only feature to be applied is enrollment app install
        if (complianceData.getComplianceFeatures().size() != 1 || !PolicyManagementConstants
                .ENROLLMENT_APP_INSTALL_FEATURE_CODE.equals(complianceData.getComplianceFeatures().get(0)
                        .getFeatureCode())) {
            try {
                Policy policy = complianceData.getPolicy();
                if (policy != null) {
                    List<DeviceIdentifier> deviceIdentifiers = new ArrayList<>();
                    deviceIdentifiers.add(deviceIdentifier);

                    List<ProfileOperation> profileOperationList = new ArrayList<>();

                    PolicyOperation policyOperation = new PolicyOperation();
                    policyOperation.setEnabled(true);
                    policyOperation.setType(Operation.Type.POLICY);
                    policyOperation.setCode(PolicyManagementConstants.MONITOR_POLICY_BUNDLE);

                    if (complianceData.isCompletePolicy()) {
                        List<ProfileFeature> effectiveFeatures = policy.getProfile().getProfileFeaturesList();
                        for (ProfileFeature feature : effectiveFeatures) {
                            ProfileOperation profileOperation = new ProfileOperation();
                            profileOperation.setCode(feature.getFeatureCode());
                            profileOperation.setEnabled(true);
                            profileOperation.setStatus(Operation.Status.PENDING);
                            profileOperation.setType(Operation.Type.PROFILE);
                            profileOperation.setPayLoad(feature.getContent());
                            profileOperationList.add(profileOperation);
                        }
                    } else {
                        List<ComplianceFeature> noneComplianceFeatures = complianceData.getComplianceFeatures();
                        List<ProfileFeature> effectiveFeatures = policy.getProfile().getProfileFeaturesList();
                        for (ComplianceFeature feature : noneComplianceFeatures) {
                            for (ProfileFeature pf : effectiveFeatures) {
                                if (pf.getFeatureCode().equalsIgnoreCase(feature.getFeatureCode())) {
                                    ProfileOperation profileOperation = new ProfileOperation();
                                    profileOperation.setCode(feature.getFeatureCode());
                                    profileOperation.setEnabled(true);
                                    profileOperation.setStatus(Operation.Status.PENDING);
                                    profileOperation.setType(Operation.Type.PROFILE);
                                    profileOperation.setPayLoad(pf.getContent());
                                    profileOperationList.add(profileOperation);
                                }
                            }
                        }
                    }
                    policyOperation.setProfileOperations(profileOperationList);
                    policyOperation.setPayLoad(policyOperation.getProfileOperations());

                    //TODO: Fix this properly later adding device type to be passed in when the task manage executes "addOperations()"
                    String type = null;
                    if (deviceIdentifiers.size() > 0) {
                        type = deviceIdentifiers.get(0).getType();
                    }
                    PolicyManagementDataHolder.getInstance().getDeviceManagementService().
                            addOperation(type, policyOperation, deviceIdentifiers);

                }
            } catch (InvalidDeviceException e) {
                throw new PolicyComplianceException("Invalid Device identifiers found.", e);
            } catch (OperationManagementException e) {
                throw new PolicyComplianceException("Error occurred while re-enforcing the policy to device " + deviceIdentifier.getId() + " - " +
                        deviceIdentifier.getType(), e);
            }
        }
    }

    @Override
    public void markDeviceAsNoneCompliance(DeviceIdentifier deviceIdentifier) throws PolicyComplianceException {

//        try {
//            DeviceManagementProviderService service = this.getDeviceManagementProviderService();
//            Device device = service.getDevice(deviceIdentifier);
//            service.setStatus(deviceIdentifier, device.getEnrolmentInfo().getOwner(),
//                    EnrolmentInfo.Status.BLOCKED);
//
//        } catch (DeviceManagementException e) {
//            String msg = "Error occurred while marking device as none compliance " + deviceIdentifier.getId() + " - " +
//                    deviceIdentifier.getType();
//            log.error(msg, e);
//            throw new PolicyComplianceException(msg, e);
//        }
    }

    @Override
    public void markDeviceAsCompliance(DeviceIdentifier deviceIdentifier) throws PolicyComplianceException {

        try {
            DeviceManagementProviderService service = this.getDeviceManagementProviderService();
            Device device = service.getDevice(deviceIdentifier, false);
            service.setStatus(device, EnrolmentInfo.Status.ACTIVE);

        } catch (DeviceManagementException e) {
            String msg = "Error occurred while marking device as compliance " + deviceIdentifier.getId() + " - " +
                    deviceIdentifier.getType();
            log.error(msg, e);
        }

    }

    @Override
    public void deactivateDevice(DeviceIdentifier deviceIdentifier) throws PolicyComplianceException {

        try {
            DeviceManagementProviderService service = this.getDeviceManagementProviderService();
            Device device = service.getDevice(deviceIdentifier, false);
            service.setStatus(device, EnrolmentInfo.Status.INACTIVE);

        } catch (DeviceManagementException e) {
            String msg = "Error occurred while deactivating the device " + deviceIdentifier.getId() + " - " +
                    deviceIdentifier.getType();
            log.error(msg, e);
            throw new PolicyComplianceException(msg, e);
        }
    }

    @Override
    public void activateDevice(DeviceIdentifier deviceIdentifier) throws PolicyComplianceException {

        try {
            DeviceManagementProviderService service = this.getDeviceManagementProviderService();
            Device device = service.getDevice(deviceIdentifier, false);
            service.setStatus(device, EnrolmentInfo.Status.ACTIVE);

        } catch (DeviceManagementException e) {
            String msg = "Error occurred while activating the device " + deviceIdentifier.getId() + " - " +
                    deviceIdentifier.getType();
            log.error(msg, e);
            throw new PolicyComplianceException(msg, e);
        }
    }

    @Override
    public void validateDevicePolicyCompliance(DeviceIdentifier deviceIdentifier, NonComplianceData complianceData) throws
            PolicyComplianceException {

        Policy policy = complianceData.getPolicy();
        String compliance = this.getNoneComplianceRule(policy);

        if ("".equals(compliance)) {
            String msg = "Compliance rule is empty for the policy " + policy.getPolicyName() + ". Therefore " +
                    "Monitoring Engine cannot run.";
            throw new PolicyComplianceException(msg);
        }

        if (PolicyManagementConstants.ENFORCE.equalsIgnoreCase(compliance)) {
            this.reEnforcePolicy(deviceIdentifier, complianceData);
        }

        if (PolicyManagementConstants.WARN.equalsIgnoreCase(compliance)) {
            this.markDeviceAsNoneCompliance(deviceIdentifier);
        }

        if (PolicyManagementConstants.BLOCK.equalsIgnoreCase(compliance)) {
            this.markDeviceAsNoneCompliance(deviceIdentifier);
        }
    }

    private DeviceManagementProviderService getDeviceManagementProviderService() {
        return PolicyManagementDataHolder.getInstance().getDeviceManagementService();
    }
}
