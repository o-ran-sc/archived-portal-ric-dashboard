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

import org.oransc.ric.anrxapp.client.api.HealthApi;
import org.oransc.ric.anrxapp.client.api.NcrtApi;
import org.oransc.ric.anrxapp.client.invoker.ApiClient;
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
 * Creates an ANR XApp client as a bean to be managed by the Spring container.
 */
@Configuration
@Profile("!mock")
public class AnrXappConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	// Populated by the autowired constructor
	private final String anrXappBasepath;

	@Autowired
	public AnrXappConfiguration(@Value("${anrxapp.basepath}") final String anrXappBasepath) {
		Assert.notNull(anrXappBasepath, "base path must not be null");
		logger.info("Configuring ANR client at base path {}", anrXappBasepath);
		this.anrXappBasepath = anrXappBasepath;
	}

	private ApiClient apiClient() {
		ApiClient apiClient = new ApiClient(new RestTemplate());
		apiClient.setBasePath(anrXappBasepath);
		return apiClient;
	}

	/**
	 * @return A HealthApi with an ApiClient configured from properties
	 */
	@Bean
	public HealthApi anrHealthApi() {
		return new HealthApi(apiClient());
	}

	@Bean
	public NcrtApi anrNcrtApi() {
		return new NcrtApi(apiClient());
	}

}
