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
import java.util.HashMap;
import java.util.Map;

import org.oransc.ric.a1med.client.api.A1MediatorApi;
import org.oransc.ric.a1med.client.invoker.ApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Creates a mock implementation of the A1 mediator client API.
 */
@Profile("test")
@Configuration
public class A1MediatorMockConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	// A "control" is an element in the XApp descriptor
	public static final String AC_CONTROL_NAME = "admission_control_policy";

	// Simulate remote method delay for UI testing
	@Value("${mock.config.delay:0}")
	private int delayMs;

	private final Map<String, String> appPolicyMap;

	public A1MediatorMockConfiguration() {
		logger.info("Configuring mock A1 Mediator");
		appPolicyMap = new HashMap<>();
		// Define a mock AC policy
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode node = mapper.createObjectNode();
		// These fields are defined in the ACAdmissionIntervalControl
		// Typescript interface, but are otherwise unknown to this backend.
		node.put("enforce", Boolean.TRUE);
		node.put("window_length", 0);
		node.put("blocking_rate", 0);
		node.put("trigger_threshold", 0);
		appPolicyMap.put(AC_CONTROL_NAME, node.toString());
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
			if (delayMs > 0) {
				logger.debug("a1ControllerGetHandler sleeping {}", delayMs);
				Thread.sleep(delayMs);
			}
			String appName = inv.<String>getArgument(0);
			return appPolicyMap.get(appName);
		}).when(mockApi).a1ControllerGetHandler(any(String.class));
		doAnswer(inv -> {
			if (delayMs > 0) {
				logger.debug("a1ControllerPutHandler sleeping {}", delayMs);
				Thread.sleep(delayMs);
			}
			String appName = inv.<String>getArgument(0);
			String policy = inv.<String>getArgument(1);
			appPolicyMap.put(appName, policy);
			return null;
		}).when(mockApi).a1ControllerPutHandler(any(String.class), any(Object.class));
		return mockApi;
	}

}
