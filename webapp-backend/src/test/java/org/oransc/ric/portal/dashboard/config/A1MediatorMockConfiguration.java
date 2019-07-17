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

import org.oransc.ric.a1med.client.api.A1MediatorApi;
import org.oransc.ric.a1med.client.invoker.ApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;

/**
 * Creates a mock implementation of the A1 mediator client API.
 */
@Profile("test")
@Configuration
public class A1MediatorMockConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	// Simulate remote method delay for UI testing
	private final int delayMs = 500;

	public A1MediatorMockConfiguration() {
		logger.info("Configuring mock A1 Mediator");
	}

	private ApiClient apiClient() {
		ApiClient mockClient = mock(ApiClient.class);
		when(mockClient.getStatusCode()).thenReturn(HttpStatus.OK);
		return mockClient;
	}

	@Bean
	// Use the same name as regular configuration
	public A1MediatorApi a1MediatorApi() {
		ApiClient apiClient = apiClient();
		A1MediatorApi mockApi = mock(A1MediatorApi.class);
		when(mockApi.getApiClient()).thenReturn(apiClient);
		doAnswer(inv -> {
			logger.debug("a1ControllerGetHandler sleeping {}", delayMs);
			Thread.sleep(delayMs);
			return null;
		}).when(mockApi).a1ControllerGetHandler(any(String.class));
		doAnswer(inv -> {
			logger.debug("a1ControllerPutHandler sleeping {}", delayMs);
			Thread.sleep(delayMs);
			return null;
		}).when(mockApi).a1ControllerPutHandler(any(String.class), any(Object.class));
		return mockApi;
	}

}
