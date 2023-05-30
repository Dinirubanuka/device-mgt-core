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

package io.entgra.device.mgt.core.device.mgt.core.config.remote.session;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This class represents the information related to Remote Session configuration.
 */
@XmlRootElement(name = "RemoteSessionConfiguration")
public class RemoteSessionConfiguration {

    private String remoteSessionServerUrl;
    private boolean enabled;
    private int maxHTTPConnectionPerHost;
    private int maxTotalHTTPConnections;
    private int maxMessagesPerSecond;
    private int sessionIdleTimeOut;
    private int maxMessageBufferSize;

    public void setRemoteSessionServerUrl(String remoteSessionServerUrl) {
        this.remoteSessionServerUrl = remoteSessionServerUrl;
    }

    /**
     * Remote session server url
     * @return
     */
    @XmlElement(name = "RemoteSessionServerUrl", required = true)
    public String getRemoteSessionServerUrl() {
        return remoteSessionServerUrl;
    }

    /**
     * Remote session enabled
     * @return
     */
    @XmlElement(name = "Enabled", required = true)
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Maximum connections per host for external http invocations
     * @return
     */
    @XmlElement(name = "MaximumHTTPConnectionPerHost", required = true, defaultValue = "2")
    public int getMaxHTTPConnectionPerHost() {
        return maxHTTPConnectionPerHost;
    }

    public void setMaxHTTPConnectionPerHost(int maxHTTPConnectionPerHost) {
        this.maxHTTPConnectionPerHost = maxHTTPConnectionPerHost;
    }

    /**
     *  Maximum total connections  for external http invocation
     */
    @XmlElement(name = "MaximumTotalHTTPConnections", required = true, defaultValue = "100")
    public int getMaxTotalHTTPConnections() {
        return maxTotalHTTPConnections;
    }

    public void setMaxTotalHTTPConnections(int maxTotalHTTPConnections) {
        this.maxTotalHTTPConnections = maxTotalHTTPConnections;
    }

    /**
     * This is for protect device from message spamming. Throttling limit in term of messages  for device
     * @return
     */
    @XmlElement(name = "MaximumMessagesPerSecond", required = true, defaultValue = "10")
    public int getMaxMessagesPerSession() {
        return maxMessagesPerSecond;
    }

    public void setMaxMessagesPerSession(int maxMessagesPerSession) {
        this.maxMessagesPerSecond = maxMessagesPerSession;
    }

    /**
     * Maximum idle timeout in minutes
     * @return
     */
    @XmlElement(name = "SessionIdleTimeOut", required = true, defaultValue = "5")
    public int getSessionIdleTimeOut() {
        return sessionIdleTimeOut;
    }

    public void setSessionIdleTimeOut(int sessionIdleTimeOut) {
        this.sessionIdleTimeOut = sessionIdleTimeOut;
    }

    /**
     * Maximum session buffer size in kilo bytes
     * @return
     */
    @XmlElement(name = "MaximumMessageBufferSize", required = true, defaultValue = "640")
    public int getMaxMessageBufferSize() {
        return maxMessageBufferSize;
    }

    public void setMaxMessageBufferSize(int MaxMessageBufferSize) {
        this.maxMessageBufferSize = MaxMessageBufferSize;
    }
}


