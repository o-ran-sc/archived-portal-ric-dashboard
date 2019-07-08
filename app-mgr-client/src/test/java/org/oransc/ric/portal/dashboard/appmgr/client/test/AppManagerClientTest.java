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
package org.oransc.ric.portal.dashboard.appmgr.client.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.oransc.ric.plt.appmgr.client.api.HealthApi;
import org.oransc.ric.plt.appmgr.client.api.XappApi;
import org.oransc.ric.plt.appmgr.client.invoker.ApiClient;
import org.oransc.ric.plt.appmgr.client.model.AllDeployedXapps;
import org.oransc.ric.plt.appmgr.client.model.Xapp;
import org.springframework.web.client.RestClientException;

/**
 * Demonstrates use of the generated xApp manager client.
 * 
 * The test fails because no server is available.
 */
public class AppManagerClientTest {

	@Test
	public void demo() {
		ApiClient apiClient = new ApiClient();
		apiClient.setBasePath("http://localhost:30099/");
		try {
			HealthApi healthApi = new HealthApi(apiClient);
			healthApi.getHealthAlive();
			System.out.println("getHealthAlive answered: " + apiClient.getStatusCode().toString());
			Assertions.assertTrue(apiClient.getStatusCode().is2xxSuccessful());
		} catch (RestClientException e) {
			System.err.println("getHealthAlive failed: " + e.toString());
		}
		try {
			XappApi xappApi = new XappApi(apiClient);
			AllDeployedXapps allXapps = xappApi.getAllXapps();
			System.out.println("getAllXapps answered: " + apiClient.getStatusCode().toString());
			Assertions.assertTrue(apiClient.getStatusCode().is2xxSuccessful());
			System.out.println("xApp count: " + allXapps.size());
			for (Xapp x : allXapps)
				System.out.println("xApp: " + x.toString());
		} catch (RestClientException e) {
			System.err.println("getAllXapps failed: " + e.toString());
		}
	}

}
