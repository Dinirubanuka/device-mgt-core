/*
 *   Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.device.application.mgt.store.api.services.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.application.mgt.common.ApplicationRelease;
import org.wso2.carbon.device.application.mgt.store.api.APIUtil;
import org.wso2.carbon.device.application.mgt.common.Application;
import org.wso2.carbon.device.application.mgt.common.ApplicationList;
import org.wso2.carbon.device.application.mgt.common.Filter;
import org.wso2.carbon.device.application.mgt.common.exception.ApplicationManagementException;
import org.wso2.carbon.device.application.mgt.common.services.ApplicationManager;
import org.wso2.carbon.device.application.mgt.core.exception.NotFoundException;
import org.wso2.carbon.device.application.mgt.store.api.services.ApplicationManagementAPI;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of Application Management related APIs.
 */
@Produces({"application/json"})
@Path("/store/applications")
public class ApplicationManagementAPIImpl implements ApplicationManagementAPI {

    private static final int DEFAULT_LIMIT = 20;
    private static Log log = LogFactory.getLog(ApplicationManagementAPIImpl.class);

    @GET
    @Consumes("application/json")
    @Override
    public Response getApplications(
            @Valid Filter filter,
            @QueryParam("offset") int offset,
            @QueryParam("limit") int limit) {
        ApplicationManager applicationManager = APIUtil.getApplicationManager();

        try {
            filter.setOffset(offset);
            filter.setLimit(limit);

            ApplicationList applications = applicationManager.getApplications(filter);
            List<ApplicationRelease> publishedApplicationRelease = new ArrayList<>();

            for (Application application : applications.getApplications()) {

                for (ApplicationRelease appRelease: application.getApplicationReleases()){
                    if (appRelease.isPublishedRelease()){
                        publishedApplicationRelease.add(appRelease);
                    }
                }
                if (publishedApplicationRelease.size()>1){
                    String msg = "Application " + application.getName()
                            + " has more than one PUBLISHED application releases";
                    log.error(msg);
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity(msg).build();
                }
                application.setApplicationReleases(publishedApplicationRelease);
                publishedApplicationRelease.clear();
            }
            return Response.status(Response.Status.OK).entity(applications).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (ApplicationManagementException e) {
            String msg = "Error occurred while getting the application list";
            log.error(msg, e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @GET
    @Consumes("application/json")
    @Path("/{appType}")
    public Response getApplication(
            @PathParam("appType") String appType,
            @QueryParam("appName") String appName) {
        ApplicationManager applicationManager = APIUtil.getApplicationManager();
        List<ApplicationRelease> publishedApplicationRelease = new ArrayList<>();
        try {
            Application application = applicationManager.getApplication(appType, appName);
            if (application == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Application with application type: " + appType + " not found").build();
            }

            for (ApplicationRelease appRelease : application.getApplicationReleases()) {
                if (appRelease.isPublishedRelease()) {
                    publishedApplicationRelease.add(appRelease);
                }
            }
            if (publishedApplicationRelease.size() > 1) {
                String msg =
                        "Application " + application.getName() + " has more than one PUBLISHED application releases";
                log.error(msg);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(msg).build();
            }
            application.setApplicationReleases(publishedApplicationRelease);

            return Response.status(Response.Status.OK).entity(application).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (ApplicationManagementException e) {
            log.error("Error occurred while getting application with the application type: " + appType
                    + " and application name: " + appName, e);
            return APIUtil.getResponse(e, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

//    todo --> get applications by category

}
