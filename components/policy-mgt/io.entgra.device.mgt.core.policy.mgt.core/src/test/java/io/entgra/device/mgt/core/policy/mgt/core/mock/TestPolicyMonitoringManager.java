/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package io.entgra.device.mgt.core.policy.mgt.core.mock;

import io.entgra.device.mgt.core.device.mgt.common.DeviceIdentifier;
import io.entgra.device.mgt.core.device.mgt.common.policy.mgt.Policy;
import io.entgra.device.mgt.core.device.mgt.common.policy.mgt.PolicyMonitoringManager;
import io.entgra.device.mgt.core.device.mgt.common.policy.mgt.monitor.ComplianceFeature;
import io.entgra.device.mgt.core.device.mgt.common.policy.mgt.monitor.NonComplianceData;
import io.entgra.device.mgt.core.device.mgt.common.policy.mgt.monitor.PolicyComplianceException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

public class TestPolicyMonitoringManager implements PolicyMonitoringManager {

    private static Log log = LogFactory.getLog(TestPolicyMonitoringManager.class);
    @Override
    public NonComplianceData checkPolicyCompliance(DeviceIdentifier deviceIdentifier, Policy policy, Object response)
            throws PolicyComplianceException {
        if (log.isDebugEnabled()) {
            log.debug("Checking policy compliance status of device '" + deviceIdentifier.getId() + "'");
        }
        NonComplianceData nonComplianceData = new NonComplianceData();
        if (response == null || policy == null) {
            return nonComplianceData;
        }

        List<ComplianceFeature> complianceFeatures = (List<ComplianceFeature>) response;
        List<ComplianceFeature> nonComplianceFeatures = new ArrayList<>();

        for (ComplianceFeature complianceFeature : complianceFeatures) {
            if (!complianceFeature.isCompliant()) {
                nonComplianceFeatures.add(complianceFeature);
                nonComplianceData.setStatus(false);
                break;
            }
        }
        nonComplianceData.setComplianceFeatures(nonComplianceFeatures);

        return nonComplianceData;
    }
}
