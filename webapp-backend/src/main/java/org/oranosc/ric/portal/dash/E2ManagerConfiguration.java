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
package org.oranosc.ric.portal.dash;

import org.oranosc.ric.portal.dashboard.e2mgr.client.api.DefaultApi;
import org.oranosc.ric.portal.dashboard.e2mgr.client.invoker.ApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

@Configuration
@ComponentScan("org.oranosc.ric.portal")
public class E2ManagerConfiguration {

	@Value("${e2.manager.base.url}")
	private String e2ManagerBaseUrl;

	/**
	 * Required by autowired constructor {@link DefaultApi#DefaultApi(ApiClient)}
	 * 
	 * @return Instance of E2 Manager client configured from properties
	 */
	@Bean
	@Primary // ignore the one in the Jar file
	public ApiClient e2ApiClient() {
		ApiClient apiClient = new ApiClient(restTemplate());
		apiClient.setBasePath(e2ManagerBaseUrl);
		return apiClient;
	}

	/**
	 * Required by autowired constructor {@link ApiClient#ApiClient(RestTemplate)}
	 * 
	 * @return Instance of RestTemplate
	 */
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

}
