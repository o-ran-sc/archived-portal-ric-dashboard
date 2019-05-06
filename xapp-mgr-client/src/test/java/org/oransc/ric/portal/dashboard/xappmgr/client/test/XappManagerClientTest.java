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
package org.oransc.ric.portal.dashboard.xappmgr.client.test;

import org.junit.jupiter.api.Test;
import org.oransc.ric.xappmgr.client.api.DefaultApi;
import org.oransc.ric.xappmgr.client.invoker.ApiClient;
import org.oransc.ric.xappmgr.client.model.AllXapps;
import org.oransc.ric.xappmgr.client.model.Xapp;
import org.springframework.web.client.RestClientException;

/**
 * Demonstrates use of the generated xApp manager client.
 * 
 * The test fails because no server is available.
 * 
 * The ugly name "DefaultApi" is generated because the spec lacks appropriate
 * tags on the operation, also see
 * https://stackoverflow.com/questions/38293236/swagger-swagger-codegen-maven-plugin-generate-default-api-interface
 */
public class XappManagerClientTest {

	@Test
	public void demo() {
		ApiClient apiClient = new ApiClient();
		apiClient.setBasePath("http://localhost:30099/");
		DefaultApi apiInstance = new DefaultApi(apiClient);
		try {
			apiInstance.getHealth();
			System.out.println("getHealth answered: " + apiClient.getStatusCode().toString());
		} catch (RestClientException e) {
			System.err.println("getHealth failed: " + e.toString());
		}
		try {
			AllXapps allXapps = apiInstance.getAllXapps();
			System.out.println("getAllXapps answered: " + apiClient.getStatusCode().toString());
			System.out.println("xApp count: " + allXapps.size());
			for (Xapp x : allXapps)
				System.out.println("xApp: " + x.toString());
		} catch (RestClientException e) {
			System.err.println("getAllXapps failed: " + e.toString());
		}
	}

}
