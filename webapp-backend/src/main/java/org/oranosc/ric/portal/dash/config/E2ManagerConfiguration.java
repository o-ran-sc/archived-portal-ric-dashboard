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
package org.oranosc.ric.portal.dash.config;

import org.oranosc.ric.e2mgr.client.api.DefaultApi;
import org.oranosc.ric.e2mgr.client.invoker.ApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

/**
 * Creates an E2 manager client as a bean to be managed by the Spring container.
 */
@Configuration
public class E2ManagerConfiguration {

	// Populated by the autowired constructor
	private final String e2mgrBasepath;

	@Autowired
	public E2ManagerConfiguration(@Value("${e2mgr.basepath}") final String e2mgrBasepath) {
		Assert.notNull(e2mgrBasepath, "base path must not be null");
		this.e2mgrBasepath = e2mgrBasepath;
	}

	/**
	 * @return A DefaultApi with an ApiClient configured from properties
	 */
	@Bean
	public DefaultApi e2ManagerClient() {
		ApiClient apiClient = new ApiClient(new RestTemplate());
		apiClient.setBasePath(e2mgrBasepath);
		return new DefaultApi(apiClient);
	}

}
