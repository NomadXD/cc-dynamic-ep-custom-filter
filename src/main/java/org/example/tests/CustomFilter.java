/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org).
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.example.tests;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.wso2.choreo.connect.enforcer.commons.model.APIConfig;
import org.wso2.choreo.connect.enforcer.commons.model.RequestContext;
import org.wso2.choreo.connect.enforcer.commons.Filter;

import java.util.Map;

public class CustomFilter implements Filter {
    private static final Logger log = LogManager.getLogger(CustomFilter.class);
    private Map<String, String> configProperties;

    @Override
    public void init(APIConfig apiConfig, Map<String, String> configProperties) {
        log.info("CustomFilter initialized");
        this.configProperties = configProperties;
    }

    @Override
    public boolean handleRequest(RequestContext requestContext) {
        log.info("Custom filter intercepted request");
        String endpointName = requestContext.getHeaders().get("x-dynamic-endpoint");
        log.info("User passed header:" + endpointName);
        // template: <organizationID>_<EndpointName>_xwso2cluster_<vHost>_<API name><API version>
        String dynamicEpHeader = String.format("%s_%s_xwso2cluster_%s_%s%s",
                requestContext.getMatchedAPI().getOrganizationId(),
                endpointName, requestContext.getMatchedAPI().getVhost(),
                requestContext.getMatchedAPI().getName(), requestContext.getMatchedAPI().getVersion());
        requestContext.addOrModifyHeaders("x-wso2-cluster-header", dynamicEpHeader);
        return true;
    }
}
