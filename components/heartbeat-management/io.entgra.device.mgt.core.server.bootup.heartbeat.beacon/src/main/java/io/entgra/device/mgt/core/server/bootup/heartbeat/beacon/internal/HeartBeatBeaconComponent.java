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

package io.entgra.device.mgt.core.server.bootup.heartbeat.beacon.internal;

import io.entgra.device.mgt.core.server.bootup.heartbeat.beacon.HeartBeatBeaconUtils;
import io.entgra.device.mgt.core.server.bootup.heartbeat.beacon.config.HeartBeatBeaconConfig;
import io.entgra.device.mgt.core.server.bootup.heartbeat.beacon.config.datasource.DataSourceConfig;
import io.entgra.device.mgt.core.server.bootup.heartbeat.beacon.dao.HeartBeatBeaconDAOFactory;
import io.entgra.device.mgt.core.server.bootup.heartbeat.beacon.service.ClusterFormationChangedNotifierRepository;
import io.entgra.device.mgt.core.server.bootup.heartbeat.beacon.service.HeartBeatManagementService;
import io.entgra.device.mgt.core.server.bootup.heartbeat.beacon.service.HeartBeatManagementServiceImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.ComponentContext;
import org.wso2.carbon.ndatasource.core.DataSourceService;

import java.util.List;

/**
 * @scr.component name="io.entgra.device.mgt.core.server.bootup.heartbeat.beacon.heartbeatBeaconComponent"
 * immediate="true"
 * @scr.reference name="org.wso2.carbon.ndatasource"
 * interface="org.wso2.carbon.ndatasource.core.DataSourceService"
 * cardinality="1..1"
 * policy="dynamic"
 * bind="setDataSourceService"
 * unbind="unsetDataSourceService"
 */
public class HeartBeatBeaconComponent {

    private static Log log = LogFactory.getLog(HeartBeatBeaconComponent.class);

    @SuppressWarnings("unused")
    protected void activate(ComponentContext componentContext) {
        try {
            if (log.isDebugEnabled()) {
                log.debug("Initializing heart beat management bundle");
            }
            this.registerHeartBeatServices(componentContext);

            //heart beat notifier configuration */
            HeartBeatBeaconConfig.init();

            if(HeartBeatBeaconConfig.getInstance().isEnabled()) {
                DataSourceConfig dsConfig = HeartBeatBeaconConfig.getInstance().getDataSourceConfig();
                HeartBeatBeaconDAOFactory.init(dsConfig);

                ClusterFormationChangedNotifierRepository clusterFormationChangedNotifierRepository
                        = new ClusterFormationChangedNotifierRepository();
                List<String> notifiers = HeartBeatBeaconConfig.getInstance().getNotifiers();
                if (notifiers != null && notifiers.size() > 0) {
                    for (String notifier : notifiers) {
                        clusterFormationChangedNotifierRepository.addNotifier(notifier);
                    }
                }
                HeartBeatBeaconDataHolder.getInstance().setClusterFormationChangedNotifierRepository(
                        clusterFormationChangedNotifierRepository);

                //Setting up executors to notify heart beat status */
                HeartBeatExecutor.setUpNotifiers(HeartBeatBeaconUtils.getServerDetails());
            }



            if (log.isDebugEnabled()) {
                log.debug("Heart Beat Notifier bundle has been successfully initialized");
            }
        } catch (Throwable e) {
            log.error("Error occurred while initializing Heart Beat Notifier bundle", e);
        }
    }

    @SuppressWarnings("unused")
    protected void deactivate(ComponentContext componentContext) {
        //do nothing
    }

    private void registerHeartBeatServices(ComponentContext componentContext) {
        if (log.isDebugEnabled()) {
            log.debug("Registering Heart Beat Management service");
        }
        HeartBeatManagementService heartBeatServiceProvider = new HeartBeatManagementServiceImpl();
        HeartBeatBeaconDataHolder.getInstance().setHeartBeatManagementService(heartBeatServiceProvider);
        componentContext.getBundleContext().registerService(HeartBeatManagementService.class, heartBeatServiceProvider, null);
    }

    protected void setDataSourceService(DataSourceService dataSourceService) {
        /* This is to avoid mobile device management component getting initialized before the underlying datasources
        are registered */
        if (log.isDebugEnabled()) {
            log.debug("Data source service set to heart beat management component");
        }
    }

    protected void unsetDataSourceService(DataSourceService dataSourceService) {
        //do nothing
    }

}
