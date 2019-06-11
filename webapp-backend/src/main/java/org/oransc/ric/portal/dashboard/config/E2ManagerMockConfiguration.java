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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.invoke.MethodHandles;

import org.oransc.ric.e2mgr.client.api.HealthCheckApi;
import org.oransc.ric.e2mgr.client.api.NodebApi;
import org.oransc.ric.e2mgr.client.invoker.ApiClient;
import org.oransc.ric.e2mgr.client.model.GetNodebResponse;
import org.oransc.ric.e2mgr.client.model.SetupRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;

/**
 * Creates a mock implementation of the E2 manager client API. This version
 * answers only status codes, no data, so the mock implementations are trivial.
 */
@Profile("mock")
@Configuration
public class E2ManagerMockConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final GetNodebResponse nodebResponse;
	
	public E2ManagerMockConfiguration() {
		logger.info("Configuring mock E2 Manager");
		nodebResponse = new GetNodebResponse().ip("1.2.3.4").port(123).ranName("myRan");
	}

	private ApiClient apiClient() {
		ApiClient mockClient = mock(ApiClient.class);
		when(mockClient.getStatusCode()).thenReturn(HttpStatus.OK);
		return mockClient;
	}

	@Bean
	public HealthCheckApi e2HealthCheckApi() {
		ApiClient apiClient = apiClient();
		HealthCheckApi mockApi = mock(HealthCheckApi.class);
		when(mockApi.getApiClient()).thenReturn(apiClient);

		doAnswer(i -> {
			return null;
		}).when(mockApi).healthGet();

		return mockApi;
	}

	@Bean
	public NodebApi e2NodebApi() {
		ApiClient apiClient = apiClient();
		NodebApi mockApi = mock(NodebApi.class);
		when(mockApi.getApiClient()).thenReturn(apiClient);

		doAnswer(i -> {
			return nodebResponse;
		}).when(mockApi).getNb(any(String.class));

		doAnswer(i -> {
			return null;
		}).when(mockApi).endcSetup(any(SetupRequest.class));

		doAnswer(i -> {
			return null;
		}).when(mockApi).x2Setup(any(SetupRequest.class));

		return mockApi;
	}

}
