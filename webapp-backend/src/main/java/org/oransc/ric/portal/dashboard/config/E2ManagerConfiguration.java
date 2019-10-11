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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

/**
 * Creates an E2 manager client as a bean to be managed by the Spring container.
 */
@Configuration
@Profile("!test")
public class E2ManagerConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	// Populated by the autowired constructor
	private final String e2mgrUrl;

	@Autowired
	public E2ManagerConfiguration(@Value("${e2mgr.url.prefix}") final String urlPrefix,
			@Value("${e2mgr.url.suffix}") final String urlSuffix) {
		logger.debug("ctor prefix '{}' suffix '{}'", urlPrefix, urlSuffix);
		e2mgrUrl = new DefaultUriBuilderFactory(urlPrefix.trim()).builder().path(urlSuffix.trim()).build().normalize()
				.toString();
		logger.info("Configuring E2 Manager at URL {}", e2mgrUrl);
	}

	private ApiClient apiClient() {
		ApiClient apiClient = new ApiClient(new RestTemplate());
		apiClient.setBasePath(e2mgrUrl);
		return apiClient;
	}

	@Bean
	// The bean (method) name must be globally unique
	public HealthCheckApi e2MgrHealthCheckApi() {
		return new HealthCheckApi(apiClient());
	}

	@Bean
	// The bean (method) name must be globally unique
	public NodebApi e2MgrNodebApi() {
		return new NodebApi(apiClient());
	}

}
