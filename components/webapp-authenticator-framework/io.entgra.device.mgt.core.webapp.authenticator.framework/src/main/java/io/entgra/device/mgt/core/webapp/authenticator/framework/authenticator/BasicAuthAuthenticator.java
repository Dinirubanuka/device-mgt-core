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
package io.entgra.device.mgt.core.webapp.authenticator.framework.authenticator;

import io.entgra.device.mgt.core.webapp.authenticator.framework.AuthenticationException;
import io.entgra.device.mgt.core.webapp.authenticator.framework.AuthenticationInfo;
import io.entgra.device.mgt.core.webapp.authenticator.framework.Constants;
import io.entgra.device.mgt.core.webapp.authenticator.framework.Utils.Utils;
import io.entgra.device.mgt.core.webapp.authenticator.framework.internal.AuthenticatorFrameworkDataHolder;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tomcat.util.buf.ByteChunk;
import org.apache.tomcat.util.buf.MessageBytes;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.api.UserStoreManager;
import org.wso2.carbon.utils.multitenancy.MultitenantUtils;

import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Properties;
import java.util.StringTokenizer;

public class BasicAuthAuthenticator implements WebappAuthenticator {

    private static final String BASIC_AUTH_AUTHENTICATOR = "BasicAuth";
    private static final String AUTH_HEADER = "basic ";
    private static final Log log = LogFactory.getLog(BasicAuthAuthenticator.class);

    @Override
    public void init() {

    }

    @Override
    public boolean canHandle(Request request) {
        /*
        This is done to avoid every web app being able to use basic auth. Add the following to
        the required web.xml of the web app. This is a global config for a web app to allow all
        contexts of the web app to use basic auth
        <context-param>
            <param-name>basicAuth</param-name>
            <param-value>true</param-value>
	    </context-param>

	    Adding the basicAuthAllowList parameter allows to selectively allow some context paths in a
	    web app to use basic auth while all the other context remain unavailable with basic auth.
	    If this parameter is present, any context that requires basic auth must be specially
	    added as comma separated list to the param-value of basicAuthAllowList.
         */
        if (!isAllowListedForBasicAuth(request)) {
            if (!isAuthenticationSupported(request)) {
                return false;
            }
        }
        if (request.getCoyoteRequest() == null || request.getCoyoteRequest().getMimeHeaders() == null) {
            return false;
        }
        MessageBytes authorization =
                request.getCoyoteRequest().getMimeHeaders().getValue(Constants.HTTPHeaders.HEADER_HTTP_AUTHORIZATION);
        if (authorization != null) {
            authorization.toBytes();
            ByteChunk authBC = authorization.getByteChunk();
            if (authBC.startsWithIgnoreCase(AUTH_HEADER, 0)) {
                return true;
            }
        }
        return false;
    }

    private boolean isAllowListedForBasicAuth(Request request) {
        String param = request.getContext().findParameter("basicAuthAllowList");
        if (param != null && !param.isEmpty()) {
            //Add the nonSecured end-points to cache
            String[] basicAuthAllowList = param.split(",");
            for (String contexPath : basicAuthAllowList) {
                if (request.getRequestURI().toString().endsWith(contexPath.trim())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public AuthenticationInfo authenticate(Request request, Response response) {
        AuthenticationInfo authenticationInfo = new AuthenticationInfo();
        Credentials credentials = getCredentials(request);
        try {
            if (credentials == null) {
                authenticationInfo.setMessage("Found invalid payload to authenticate.");
                authenticationInfo.setStatus(Status.FAILURE);
                return authenticationInfo;
            }
            int tenantId = Utils.getTenantIdOFUser(credentials.getUsername());
            if (tenantId == -1) {
                authenticationInfo.setMessage("Tenant Domain doesn't exists or tenant domain hasn't loaded properly.");
                authenticationInfo.setStatus(Status.FAILURE);
                return authenticationInfo;
            }
            UserStoreManager userStore = AuthenticatorFrameworkDataHolder.getInstance().getRealmService().
                    getTenantUserRealm(tenantId).getUserStoreManager();
            String username = MultitenantUtils.getTenantAwareUsername(credentials.getUsername());
            boolean authenticated = userStore.authenticate(username, credentials.getPassword());
            if (authenticated) {
                authenticationInfo.setStatus(Status.CONTINUE);
                authenticationInfo.setUsername(username);
                authenticationInfo.setTenantDomain(Utils.getTenantDomain(tenantId));
                authenticationInfo.setTenantId(tenantId);
            } else {
                authenticationInfo.setMessage("Failed to authorize incoming request.");
                authenticationInfo.setStatus(Status.FAILURE);
            }
        } catch (UserStoreException e) {
            log.error("Error occurred while authenticating the user." + credentials.getUsername(), e);
        } catch (AuthenticationException e) {
            log.error("Error occurred while obtaining the tenant Id for user." + credentials.getUsername(), e);
        }
        return authenticationInfo;
    }

    @Override
    public String getName() {
        return BasicAuthAuthenticator.BASIC_AUTH_AUTHENTICATOR;
    }

    @Override
    public void setProperties(Properties properties) {

    }

    @Override
    public Properties getProperties() {
        return null;
    }

    @Override
    public String getProperty(String name) {
        return null;
    }

    private Credentials getCredentials(Request request) {
        Credentials credentials = null;
        String username;
        String password = null;
        MessageBytes authorization = request.getCoyoteRequest().getMimeHeaders()
                .getValue(Constants.HTTPHeaders.HEADER_HTTP_AUTHORIZATION);
        if (authorization != null) {
            authorization.toBytes();
            String authorizationString = authorization.getByteChunk().toString();
            if (authorizationString.toLowerCase().startsWith(AUTH_HEADER)) {
                // Authorization: Basic base64credentials
                String base64Credentials = authorizationString.substring(AUTH_HEADER.length()).trim();
                String decodedString = new String(Base64.getDecoder().decode(base64Credentials),
                        Charset.forName("UTF-8"));
                int colon = decodedString.indexOf(':', 0);
                if (colon < 0) {
                    username = decodedString;
                } else {
                    username = decodedString.substring(0, colon);
                    password = decodedString.substring(colon + 1);
                }
                credentials = new Credentials(username, password);
            }
        }
        return credentials;
    }

    public static class Credentials {
        private String username;
        private String password;

        Credentials(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String getUsername() {
            return username;
        }

        String getPassword() {
            return password;
        }
    }

    private boolean isAuthenticationSupported(Request request) {
        String param = request.getContext().findParameter("basicAuth");
        return (param != null && Boolean.parseBoolean(param));
    }

}
