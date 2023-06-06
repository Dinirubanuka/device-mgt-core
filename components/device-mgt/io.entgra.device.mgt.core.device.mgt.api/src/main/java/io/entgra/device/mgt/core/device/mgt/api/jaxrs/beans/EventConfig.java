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

package io.entgra.device.mgt.core.device.mgt.api.jaxrs.beans;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

public class EventConfig {

    @ApiModelProperty(
            name = "id",
            value = "id of the event entry")
    private int id;

    @ApiModelProperty(
            name = "eventLogic",
            value = "Logic of the event should be handled at the device level",
            required = true)
    private String eventLogic;

    @ApiModelProperty(
            name = "actions",
            value = "List of actions to be triggered according to the logic",
            required = true)
    private List<EventAction> actions;

    public String getEventLogic() {
        return eventLogic;
    }

    public void setEventLogic(String eventLogic) {
        this.eventLogic = eventLogic;
    }

    public List<EventAction> getActions() {
        return actions;
    }

    public void setActions(List<EventAction> actions) {
        this.actions = actions;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
