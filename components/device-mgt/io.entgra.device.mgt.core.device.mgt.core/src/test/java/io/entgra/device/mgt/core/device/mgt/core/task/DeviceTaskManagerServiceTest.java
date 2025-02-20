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
package io.entgra.device.mgt.core.device.mgt.core.task;

import io.entgra.device.mgt.core.device.mgt.core.TestTaskServiceImpl;
import io.entgra.device.mgt.core.device.mgt.core.common.TestDataHolder;
import io.entgra.device.mgt.core.device.mgt.core.config.DeviceConfigurationManager;
import io.entgra.device.mgt.core.device.mgt.core.internal.DeviceManagementDataHolder;
import io.entgra.device.mgt.core.device.mgt.core.task.impl.DeviceTaskManagerServiceImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.ntask.common.TaskException;
import org.wso2.carbon.ntask.core.TaskInfo;
import org.wso2.carbon.ntask.core.TaskManager;
import org.wso2.carbon.ntask.core.internal.TasksDSComponent;
import org.wso2.carbon.ntask.core.service.TaskService;
import org.wso2.carbon.ntask.core.service.impl.TaskServiceImpl;

import java.lang.reflect.Field;

public class DeviceTaskManagerServiceTest {
    private static final Log log = LogFactory.getLog(DeviceTaskManagerService.class);
    private static final String TASK_TYPE = "DEVICE_MONITORING";
    private DeviceTaskManagerService deviceTaskManagerService;
    private TaskService taskService;

    @BeforeClass
    public void init() throws Exception {
        DeviceConfigurationManager.getInstance().initConfig();
        log.info("Initializing Device Task Manager Service Test Suite");
        this.taskService = new TestTaskServiceImpl();
        DeviceManagementDataHolder.getInstance().setTaskService(this.taskService);
        this.deviceTaskManagerService = new DeviceTaskManagerServiceImpl();
        DeviceManagementDataHolder.getInstance().setDeviceTaskManagerService(this.deviceTaskManagerService);
        Field taskServiceField = TasksDSComponent.class.getDeclaredField("taskService");
        taskServiceField.setAccessible(true);
        taskServiceField.set(null, Mockito.mock(TaskServiceImpl.class, Mockito.RETURNS_MOCKS));
    }

    @Test(groups = "Device Task Manager Service Test Group")
    public void testStartTask() {
        try {
            log.debug("Attempting to start task from testStartTask");
            this.deviceTaskManagerService.startTask(TestDataHolder.TEST_DEVICE_TYPE,
                    TestDataHolder.generateMonitoringTaskConfig(true, 60000, 1));
            TaskManager taskManager = this.taskService.getTaskManager(TASK_TYPE);
            Assert.assertEquals(this.taskService.getRegisteredTaskTypes().size(), 1);
            Assert.assertNotNull(taskManager
                    .getTask(TestDataHolder.TEST_DEVICE_TYPE +
                            String.valueOf(TestDataHolder.SUPER_TENANT_ID)));
            log.debug("Task Successfully started");
        } catch (DeviceMgtTaskException | TaskException e) {
            Assert.fail("Exception occurred when starting the task", e);
        }
    }

    @Test(groups = "Device Task Manager Service Test Group", dependsOnMethods = "testStartTask")
    public void testUpdateTask() {
        try {
            log.debug("Attempting to update task from testStartTask");
            TaskManager taskManager = this.taskService.getTaskManager(TASK_TYPE);
            this.deviceTaskManagerService.updateTask(TestDataHolder.TEST_DEVICE_TYPE,
                    TestDataHolder.generateMonitoringTaskConfig(true, 30000, 1));
            Assert.assertEquals(this.taskService.getRegisteredTaskTypes().size(), 1);
            Assert.assertEquals(taskManager.getAllTasks().size(), 1);
            log.debug("Task Successfully updated");
        } catch (DeviceMgtTaskException | TaskException e) {
            Assert.fail("Exception occurred when updating the task", e);
        }
    }

    @Test(groups = "Device Task Manager Service Test Group", dependsOnMethods = "testUpdateTask")
    public void testStopTask() {
        log.debug("Attempting to stop task from testStopTask");
        try {
            this.deviceTaskManagerService.stopTask(TestDataHolder.TEST_DEVICE_TYPE,
                    TestDataHolder.generateMonitoringTaskConfig(true, 30000, 1));
            TaskManager taskManager = this.taskService.getTaskManager(TASK_TYPE);
            Assert.assertEquals(taskManager.getAllTasks().size(), 0);
        } catch (DeviceMgtTaskException | TaskException e) {
            Assert.fail("Exception occurred when stopping the task", e);
        }
    }

    @Test(groups = "Device Task Manager Service Test Group", dependsOnMethods = "testStopTask", expectedExceptions = {
            DeviceMgtTaskException.class })
    public void testUpdateUnscheduledTask() throws DeviceMgtTaskException {
        log.debug("Attempting to update unscheduled task");
        this.deviceTaskManagerService.updateTask(TestDataHolder.TEST_DEVICE_TYPE,
                TestDataHolder.generateMonitoringTaskConfig(true, 60000, 1));
    }

    @Test(groups = "Device Task Manager Service Test Group", dependsOnMethods = "testUpdateUnscheduledTask", expectedExceptions = {
            DeviceMgtTaskException.class })
    public void testStartTaskWhenUnableToRetrieveTaskManager()
            throws DeviceMgtTaskException, TaskException {
        TaskService taskService = Mockito.mock(TestTaskServiceImpl.class);
        Mockito.doThrow(new TaskException("Unable to get TaskManager", TaskException.Code.UNKNOWN)).when(taskService)
                .getTaskManager(TASK_TYPE);
        DeviceManagementDataHolder.getInstance().setTaskService(taskService);
        this.deviceTaskManagerService.startTask(TestDataHolder.TEST_DEVICE_TYPE,
                TestDataHolder.generateMonitoringTaskConfig(true, 60000, 2));
    }

    @Test(groups = "Device Task Manager Service Test Group", dependsOnMethods = "testUpdateUnscheduledTask", expectedExceptions = {
            DeviceMgtTaskException.class })
    public void testUpdateTaskWhenUnableToRetrieveTaskManager()
            throws DeviceMgtTaskException, TaskException {
        TaskService taskService = Mockito.mock(TestTaskServiceImpl.class);
        Mockito.doThrow(new TaskException("Unable to get TaskManager", TaskException.Code.UNKNOWN)).when(taskService)
                .getTaskManager(TASK_TYPE);
        DeviceManagementDataHolder.getInstance().setTaskService(taskService);
        this.deviceTaskManagerService.updateTask(TestDataHolder.TEST_DEVICE_TYPE,
                TestDataHolder.generateMonitoringTaskConfig(true, 60000, 2));
    }

    @Test(groups = "Device Task Manager Service Test Group", dependsOnMethods = "testUpdateUnscheduledTask",
            expectedExceptions = {DeviceMgtTaskException.class })
    public void testStartTaskWhenFailedToRegisterTaskType()
            throws DeviceMgtTaskException, TaskException {
        TaskService taskService = Mockito.mock(TestTaskServiceImpl.class);
        Mockito.doThrow(new TaskException("Unable to register task type", TaskException.Code.UNKNOWN)).when(taskService)
                .registerTaskType(TASK_TYPE);
        DeviceManagementDataHolder.getInstance().setTaskService(taskService);
        this.deviceTaskManagerService.startTask(TestDataHolder.TEST_DEVICE_TYPE,
                TestDataHolder.generateMonitoringTaskConfig(true, 60000, 2));
    }

    @Test(groups = "Device Task Manager Service Test Group", dependsOnMethods = "testUpdateUnscheduledTask",
            expectedExceptions = {DeviceMgtTaskException.class })
    public void testStartTaskWhenFailedToRegisterTask()
            throws DeviceMgtTaskException, TaskException {
        TestTaskServiceImpl taskService = new TestTaskServiceImpl();
        TaskManager taskManager = Mockito.mock(TestTaskManagerImpl.class);
        taskService.setTaskManager(taskManager);
        Mockito.doThrow(new TaskException("Unable to register task", TaskException.Code.UNKNOWN)).when(taskManager)
                .registerTask(Mockito.any(TaskInfo.class));
        DeviceManagementDataHolder.getInstance().setTaskService(taskService);
        this.deviceTaskManagerService.startTask(TestDataHolder.TEST_DEVICE_TYPE,
                TestDataHolder.generateMonitoringTaskConfig(true, 60000, 2));
    }

    @Test(groups = "Device Task Manager Service Test Group", dependsOnMethods = "testUpdateUnscheduledTask",
            expectedExceptions = {DeviceMgtTaskException.class })
    public void testUpdateTaskWhenFailedToRegisterTask()
            throws DeviceMgtTaskException, TaskException {
        TestTaskServiceImpl taskService = new TestTaskServiceImpl();
        TaskManager taskManager = Mockito.mock(TestTaskManagerImpl.class);
        taskService.setTaskManager(taskManager);
        Mockito.doThrow(new TaskException("Unable to register task", TaskException.Code.UNKNOWN)).when(taskManager)
                .registerTask(Mockito.any(TaskInfo.class));
        DeviceManagementDataHolder.getInstance().setTaskService(taskService);
        this.deviceTaskManagerService.updateTask(TestDataHolder.TEST_DEVICE_TYPE,
                TestDataHolder.generateMonitoringTaskConfig(true, 60000, 2));
    }

    @Test(groups = "Device Task Manager Service Test Group", dependsOnMethods = "testUpdateUnscheduledTask",
            expectedExceptions = {DeviceMgtTaskException.class })
    public void testUpdateTaskWhenFailedToRescheduleTask()
            throws DeviceMgtTaskException, TaskException {
        TestTaskServiceImpl taskService = new TestTaskServiceImpl();
        TaskManager taskManager = Mockito.mock(TestTaskManagerImpl.class);
        taskService.setTaskManager(taskManager);
        Mockito.doThrow(new TaskException("Unable to reschedule task", TaskException.Code.UNKNOWN)).when(taskManager)
                .rescheduleTask(Mockito.any(String.class));
        DeviceManagementDataHolder.getInstance().setTaskService(taskService);
        this.deviceTaskManagerService.updateTask(TestDataHolder.TEST_DEVICE_TYPE,
                TestDataHolder.generateMonitoringTaskConfig(true, 60000, 2));
    }

    @Test(groups = "Device Task Manager Service Test Group", dependsOnMethods = "testUpdateUnscheduledTask",
            expectedExceptions = {DeviceMgtTaskException.class })
    public void testUpdateTaskWhenFailedToDeleteTask()
            throws DeviceMgtTaskException, TaskException {
        TestTaskServiceImpl taskService = new TestTaskServiceImpl();
        TaskManager taskManager = Mockito.mock(TestTaskManagerImpl.class);
        taskService.setTaskManager(taskManager);
        Mockito.doThrow(new TaskException("Unable to delete task", TaskException.Code.UNKNOWN)).when(taskManager)
                .deleteTask(Mockito.any(String.class));
        DeviceManagementDataHolder.getInstance().setTaskService(taskService);
        this.deviceTaskManagerService.updateTask(TestDataHolder.TEST_DEVICE_TYPE,
                TestDataHolder.generateMonitoringTaskConfig(true, 60000, 2));
    }

    @Test(groups = "Device Task Manager Service Test Group", dependsOnMethods = "testUpdateUnscheduledTask",
            expectedExceptions = {DeviceMgtTaskException.class })
    public void testStopTaskWhenFailedToDeleteTask()
            throws DeviceMgtTaskException, TaskException {
        TestTaskServiceImpl taskService = new TestTaskServiceImpl();
        TaskManager taskManager = Mockito.mock(TestTaskManagerImpl.class);
        taskService.setTaskManager(taskManager);
        Mockito.doThrow(new TaskException("Unable to delete task", TaskException.Code.UNKNOWN)).when(taskManager)
                .deleteTask(Mockito.any(String.class));
        DeviceManagementDataHolder.getInstance().setTaskService(taskService);
        this.deviceTaskManagerService.stopTask(TestDataHolder.TEST_DEVICE_TYPE,
                TestDataHolder.generateMonitoringTaskConfig(true, 60000, 2));
    }
}
