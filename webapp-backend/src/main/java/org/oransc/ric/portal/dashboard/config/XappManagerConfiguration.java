/*-
 * ========================LICENSE_START=================================
 * O-RAN-SC
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
import java.net.MalformedURLException;
import java.net.URL;

import org.oransc.ric.xappmgr.client.api.HealthApi;
import org.oransc.ric.xappmgr.client.api.XappApi;
import org.oransc.ric.xappmgr.client.invoker.ApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

/**
 * Creates an xApp manager client as a bean to be managed by the Spring
 * container.
 */
@Configuration
@Profile("!mock")
public class XappManagerConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	// Populated by the autowired constructor
	private final String xappMgrUrl;

	@Autowired
	public XappManagerConfiguration(@Value("${xappmgr.url}") final String url) throws MalformedURLException {
		logger.info("Configuring xApp Manager at base path {}", url);
		new URL(url);
		this.xappMgrUrl = url;
	}

	private ApiClient apiClient() {
		ApiClient apiClient = new ApiClient(new RestTemplate());
		apiClient.setBasePath(xappMgrUrl);
		return apiClient;
	}

	/**
	 * @return A HealthApi with an ApiClient configured from properties
	 */
	@Bean
	public HealthApi xappHealthApi() {
		return new HealthApi(apiClient());
	}

	/**
	 * @return An XappApi with an ApiClient configured from properties
	 */
	@Bean
	public XappApi xappMgrApi() {
		return new XappApi(apiClient());
	}
}
