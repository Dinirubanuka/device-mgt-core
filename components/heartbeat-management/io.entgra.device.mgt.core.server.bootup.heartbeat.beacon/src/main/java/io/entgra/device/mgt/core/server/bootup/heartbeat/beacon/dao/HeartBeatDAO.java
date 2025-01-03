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

package io.entgra.device.mgt.core.server.bootup.heartbeat.beacon.dao;

import io.entgra.device.mgt.core.server.bootup.heartbeat.beacon.dao.exception.HeartBeatDAOException;
import io.entgra.device.mgt.core.server.bootup.heartbeat.beacon.dto.ElectedCandidate;
import io.entgra.device.mgt.core.server.bootup.heartbeat.beacon.dto.HeartBeatEvent;
import io.entgra.device.mgt.core.server.bootup.heartbeat.beacon.dto.ServerContext;

import java.util.List;
import java.util.Map;

/**
 * This interface represents the key operations associated with persisting group related information.
 */
public interface HeartBeatDAO {

    String recordServerCtx(ServerContext ctx) throws HeartBeatDAOException;

    boolean recordHeatBeat(HeartBeatEvent event) throws HeartBeatDAOException;

    boolean checkUUIDValidity(String uuid) throws HeartBeatDAOException;

    String retrieveExistingServerCtx(ServerContext ctx) throws HeartBeatDAOException;

    Map<String, ServerContext> getActiveServerDetails(int elapsedTimeInSeconds) throws HeartBeatDAOException;

    boolean recordElectedCandidate(String serverUUID) throws HeartBeatDAOException;

    void purgeCandidates() throws HeartBeatDAOException;

    ElectedCandidate retrieveCandidate() throws HeartBeatDAOException;

    boolean acknowledgeTask(String uuid, List<String> taskList) throws HeartBeatDAOException;

}
