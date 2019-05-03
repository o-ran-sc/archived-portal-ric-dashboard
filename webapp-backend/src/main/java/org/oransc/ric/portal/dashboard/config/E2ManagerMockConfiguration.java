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
package org.oransc.ric.portal.dashboard.config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.invoke.MethodHandles;

import org.oransc.ric.e2mgr.client.api.E2ManagerApi;
import org.oransc.ric.e2mgr.client.invoker.ApiClient;
import org.oransc.ric.e2mgr.client.model.SetupRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;

/**
 * Creates a mock implementation of the E2 manager client API.
 */
@Profile("mock")
@Configuration
public class E2ManagerMockConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public E2ManagerMockConfiguration() {
		logger.info("Configuring mock E2 Manager");
	}

	private ApiClient apiClient() {
		ApiClient mockClient = mock(ApiClient.class);
		when(mockClient.getStatusCode()).thenReturn(HttpStatus.OK);
		return mockClient;
	}

	@Bean
	public E2ManagerApi e2ManagerApi() {
		ApiClient apiClient = apiClient();
		E2ManagerApi mockApi = mock(E2ManagerApi.class);
		when(mockApi.getApiClient()).thenReturn(apiClient);

		doAnswer(i -> {
			return null;
		}).when(mockApi).healthCheck();

		doAnswer(i -> {
			return null;
		}).when(mockApi).endcSetup(any(SetupRequest.class));

		doAnswer(i -> {
			return null;
		}).when(mockApi).setup(any(SetupRequest.class));

		return mockApi;
	}

}
