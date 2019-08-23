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
package org.oransc.ric.portal.dashboard.a1med.client.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.oransc.ric.a1med.client.api.A1MediatorApi;
import org.oransc.ric.a1med.client.invoker.ApiClient;
import org.springframework.web.client.RestClientException;

/**
 * Demonstrates use of the generated A1 mediator client.
 * 
 * The tests fail because no server is available.
 */
public class A1MediatorClientTest {

	@Test
	public void demo() {
		ApiClient apiClient = new ApiClient();
		apiClient.setBasePath("http://localhost:30099/");
		A1MediatorApi a1Api = new A1MediatorApi(apiClient);
		try {
			Object o = a1Api.a1ControllerGetHandler("policy");
			System.out.println(
					"getPolicy answered code {} " + apiClient.getStatusCode().toString() + ", content " + o.toString());
			Assertions.assertTrue(apiClient.getStatusCode().is2xxSuccessful());
		} catch (RestClientException e) {
			System.err.println("getPolicy failed: " + e.toString());
		}
		try {
			String policy = "{}";
			a1Api.a1ControllerPutHandler("policy", policy);
			System.out.println("putPolicy answered: " + apiClient.getStatusCode().toString());
			Assertions.assertTrue(apiClient.getStatusCode().is2xxSuccessful());
		} catch (RestClientException e) {
			System.err.println("getPolicy failed: " + e.toString());
		}
	}
}
