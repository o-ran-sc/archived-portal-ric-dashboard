/*-
 * ========================LICENSE_START=================================
 * O-RAN-SC
 * %%
 * Copyright (C) 2019 AT&T Intellectual Property
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

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.oransc.ric.a1med.client.api.A1MediatorApi;
import org.oransc.ric.a1med.client.invoker.ApiClient;
import org.oransc.ric.a1med.client.model.PolicyTypeSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Creates a mock implementation of the A1 mediator client builder with mock
 * methods that answer Admission Control mock data.
 */
@Configuration
@Profile("test")
public class A1MediatorMockConfiguration extends AbstractMockConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	// A "control" is an element in the XApp descriptor
	public static final String AC_CONTROL_NAME = "admission_control_policy";
	public static final Integer ADMISSION_CONTROL_POLICY_ID = 21000;

	// Simulate remote method delay for UI testing
	private final int delayMs;

	// Mock values
	private final List<Integer> policyTypeIds;
	private final PolicyTypeSchema rateControlPolicyType;
	private final Map<String, String> appPolicyMap;

	public A1MediatorMockConfiguration(@Value("${mock.config.delay:0}") int delayMs)
			throws IOException, JsonParseException, JsonMappingException {
		logger.debug("ctor: configured with delay {}", delayMs);
		this.delayMs = delayMs;
		policyTypeIds = new ArrayList<>();
		policyTypeIds.add(ADMISSION_CONTROL_POLICY_ID);
		ObjectMapper mapper = new ObjectMapper();
		final String policyType = readDataFromPath("rate-control-policy-type.json");
		rateControlPolicyType = mapper.readValue(policyType, PolicyTypeSchema.class);
		final String policyInstance = readDataFromPath("rate-control-policy-instance.json");
		appPolicyMap = new HashMap<>();
		appPolicyMap.put(AC_CONTROL_NAME, policyInstance);
	}

	private void delay() throws InterruptedException {
		if (delayMs > 0) {
			logger.debug("delay: sleeping {}", delayMs);
			Thread.sleep(delayMs);
		}
	}

	private ApiClient apiClient() {
		ApiClient mockClient = mock(ApiClient.class);
		when(mockClient.getStatusCode()).thenReturn(HttpStatus.OK);
		return mockClient;
	}

	private A1MediatorApi a1MediatorApi(String instanceKey) {
		logger.debug("a1MediatorApi: instance {}", instanceKey);
		ApiClient apiClient = apiClient();
		A1MediatorApi mockApi = mock(A1MediatorApi.class);
		when(mockApi.getApiClient()).thenReturn(apiClient);
		doAnswer(inv -> {
			delay();
			return policyTypeIds;
		}).when(mockApi).a1ControllerGetAllPolicyTypes();
		doAnswer(inv -> {
			delay();
			Integer policyTypeId = inv.<Integer>getArgument(0);
			if (policyTypeId.compareTo(ADMISSION_CONTROL_POLICY_ID) != 0)
				throw new IllegalArgumentException("Unexpected policy type: " + policyTypeId);
			return rateControlPolicyType;
		}).when(mockApi).a1ControllerGetPolicyType(any(Integer.class));
		doAnswer(inv -> {
			delay();
			Integer policyTypeId = inv.<Integer>getArgument(0);
			if (policyTypeId.compareTo(ADMISSION_CONTROL_POLICY_ID) != 0)
				throw new IllegalArgumentException("Unexpected policy type: " + policyTypeId);
			String policyInstId = inv.<String>getArgument(1);
			if (!AC_CONTROL_NAME.equals(policyInstId))
				throw new IllegalArgumentException("Unexpected policy instance: " + policyInstId);
			return appPolicyMap.get(policyInstId);
		}).when(mockApi).a1ControllerGetPolicyInstance(any(Integer.class), any(String.class));
		doAnswer(inv -> {
			delay();
			Integer policyTypeId = inv.<Integer>getArgument(0);
			if (policyTypeId.compareTo(ADMISSION_CONTROL_POLICY_ID) != 0)
				throw new IllegalArgumentException("Unexpected policy type: " + policyTypeId);
			String policyInstId = inv.<String>getArgument(1);
			if (!AC_CONTROL_NAME.equals(policyInstId))
				throw new IllegalArgumentException("Unexpected policy instance: " + policyInstId);
			String policy = inv.<String>getArgument(2);
			appPolicyMap.put(policyInstId, policy);
			return null;
		}).when(mockApi).a1ControllerCreateOrReplacePolicyInstance(any(Integer.class), any(String.class),
				any(Object.class));
		return mockApi;
	}

	@Bean
	// Must use the same name as the non-mock configuration
	public A1MediatorApiBuilder a1MediatorApiBuilder() {
		final A1MediatorApiBuilder mockBuilder = mock(A1MediatorApiBuilder.class);
		for (final String key : RICInstanceMockConfiguration.INSTANCE_KEYS) {
			final A1MediatorApi mockApi = a1MediatorApi(key);
			when(mockBuilder.getA1MediatorApi(key)).thenReturn(mockApi);
		}
		return mockBuilder;
	}

}
