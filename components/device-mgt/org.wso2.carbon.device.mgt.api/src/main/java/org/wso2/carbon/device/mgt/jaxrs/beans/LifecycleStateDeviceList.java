/*
 *   Copyright (c) 2023, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.device.mgt.jaxrs.beans;

import io.swagger.annotations.ApiModelProperty;
import org.wso2.carbon.device.mgt.common.LifecycleStateDevice;

import java.util.ArrayList;
import java.util.List;

public class LifecycleStateDeviceList extends BasePaginatedResult {

    private List<LifecycleStateDevice> lifecycleStates = new ArrayList<>();

    @ApiModelProperty(value = "List of lifecycleStates history returned")
    public List<LifecycleStateDevice> getLifecycleStates() {
        return lifecycleStates;
    }

    public void setLifecycleStates(List<LifecycleStateDevice> lifecycleStates) {
        this.lifecycleStates = lifecycleStates;
    }

    @Override
    public String toString() {
        return "LifecycleStateDeviceList{" +
                "lifecycleStates=" + lifecycleStates +
                '}';
    }
}