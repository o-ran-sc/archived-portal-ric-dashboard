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

import org.oranosc.ric.e2mgr.client.api.DefaultApi;
import org.oranosc.ric.e2mgr.client.invoker.ApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class E2ManagerConfiguration {

	@Value("${e2mgr.basepath}")
	private String e2ManagerBaseUrl;

	/**
	 * @return A DefaultApi with an ApiClient configured from properties
	 */
	@Bean
	public DefaultApi e2ManagerClient() {
		ApiClient apiClient = new ApiClient(new RestTemplate());
		apiClient.setBasePath(e2ManagerBaseUrl);
		return new DefaultApi(apiClient);
	}

}
