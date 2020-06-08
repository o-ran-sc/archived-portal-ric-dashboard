/*-
 * ========================LICENSE_START=================================
 * O-RAN-SC
 * %%
 * Copyright (C) 2020 AT&T Intellectual Property
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
package org.oransc.ric.portal.dashboard.xappobrd.client.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.oransc.itdev.xapponboarder.client.api.HealthApi;
import org.oransc.itdev.xapponboarder.client.invoker.ApiClient;
import org.springframework.web.client.RestClientException;

/**
 * Demonstrates use of the generated xapp onboarder client.
 * 
 * The test fails because no server is available.
 */
public class XappOnboarderClientTest {

	@Test
	public void demo() {
		ApiClient apiClient = new ApiClient();
		apiClient.setBasePath("http://localhost:30099/");
		HealthApi healthApi = new HealthApi(apiClient);
		try {
			healthApi.getHealthCheck();
			System.out.println("getHealthCheck answered: " + apiClient.getStatusCode().toString());
			Assertions.assertTrue(apiClient.getStatusCode().is2xxSuccessful());
		} catch (RestClientException e) {
			System.err.println("getHealth failed: " + e.toString());
		}
	}
}
