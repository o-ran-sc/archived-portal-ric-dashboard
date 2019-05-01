/*-
 * ========================LICENSE_START=================================
 * ORAN-OSC
 * %%
 * Copyright (C) 2019 AT&T Intellectual Property and Nokia
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

import org.oransc.ric.e2mgr.client.api.EndcSetupRequestApi;
import org.oransc.ric.e2mgr.client.api.HealthCheckApi;
import org.oransc.ric.e2mgr.client.api.X2SetupRequestApi;
import org.oransc.ric.e2mgr.client.invoker.ApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

/**
 * Creates an E2 manager client as a bean to be managed by the Spring container.
 */
@Configuration
@Profile("!mock")
public class E2ManagerConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	// Populated by the autowired constructor
	private final String e2mgrBasepath;

	@Autowired
	public E2ManagerConfiguration(@Value("${e2mgr.basepath}") final String e2mgrBasepath) {
		Assert.notNull(e2mgrBasepath, "base path must not be null");
		logger.info("Configuring E2 Manager at base path {}", e2mgrBasepath);
		this.e2mgrBasepath = e2mgrBasepath;
	}

	private ApiClient apiClient() {
		ApiClient apiClient = new ApiClient(new RestTemplate());
		apiClient.setBasePath(e2mgrBasepath);
		return apiClient;
	}

	@Bean
	public EndcSetupRequestApi endcSetupRequestApi() {
		return new EndcSetupRequestApi(apiClient());
	}

	@Bean
	public HealthCheckApi healthCheckApi() {
		return new HealthCheckApi(apiClient());
	}

	@Bean
	public X2SetupRequestApi x2SetupRequestApi() {
		return new X2SetupRequestApi(apiClient());
	}

}
