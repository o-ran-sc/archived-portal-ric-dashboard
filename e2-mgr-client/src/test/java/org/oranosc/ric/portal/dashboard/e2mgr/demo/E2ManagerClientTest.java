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
package org.oranosc.ric.portal.dashboard.e2mgr.demo;

import org.junit.jupiter.api.Test;
import org.oranosc.ric.e2mgr.client.api.HealthCheckApi;
import org.oranosc.ric.e2mgr.client.invoker.ApiClient;
import org.springframework.web.client.RestClientException;

/**
 * Demonstrates use of the generated E2 manager client.
 * 
 * The test fails because no server is available.
 */
public class E2ManagerClientTest {

	@Test
	public void demo() {
		ApiClient apiClient = new ApiClient();
		apiClient.setBasePath("http://localhost:30099/");
		HealthCheckApi e2Health = new HealthCheckApi(apiClient);
		try {
			e2Health.healthGet();
			System.out.println("getHealth answered: " + apiClient.getStatusCode().toString());
		} catch (RestClientException e) {
			System.err.println("getHealth failed: " + e.toString());
		}
	}
}
