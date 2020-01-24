/*-
 * ========================LICENSE_START=================================
 * O-RAN-SC
 * %%
 * Copyright (C) 2019 AT&T Intellectual Property
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ========================LICENSE_END===================================
 */
package org.oransc.ric.portal.dashboard.config;

import java.lang.invoke.MethodHandles;

import org.oransc.ric.e2mgr.client.api.HealthCheckApi;
import org.oransc.ric.e2mgr.client.api.NodebApi;
import org.oransc.ric.e2mgr.client.invoker.ApiClient;
import org.oransc.ric.portal.dashboard.model.RicInstance;
import org.oransc.ric.portal.dashboard.model.RicRegionList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

/**
 * The OpenAPI generated API client code using Spring RestTemplate is not thread
 * safe according to https://github.com/swagger-api/swagger-codegen/issues/9222
 *
 * As a workaround this builder creates a new client at every request. If this
 * proves to be too slow then clients could be cached for each thread.
 */
public class E2ManagerApiBuilder {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final String urlSuffix;
	private final RicRegionList instanceConfig;

	public E2ManagerApiBuilder(final RicRegionList instanceConfig, final String urlSuffix) {
		logger.debug("ctor: suffix {}", urlSuffix);
		this.instanceConfig = instanceConfig;
		this.urlSuffix = urlSuffix;
	}

	private ApiClient apiClient(String instanceKey) {
		RicInstance instance = instanceConfig.getInstance(instanceKey);
		String url = new DefaultUriBuilderFactory(instance.getPltUrlPrefix().trim()).builder()
				.path(this.urlSuffix.trim()).build().normalize().toString();
		logger.debug("apiClient URL {}", url);
		return new ApiClient(new RestTemplate()).setBasePath(url);
	}

	public HealthCheckApi getHealthCheckApi(String instanceKey) {
		return new HealthCheckApi(apiClient(instanceKey));
	}

	public NodebApi getNodebApi(String instanceKey) {
		return new NodebApi(apiClient(instanceKey));
	}

}
