/*-
 * ========================LICENSE_START=================================
 * O-RAN-SC
 * %%
 * Copyright (C) 2019 AT&T Intellectual Property
 * Modifications Copyright (C) 2019 Nordix Foundation
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Creates a mock implementation of the A1 mediator client API.
 */
@Configuration
@Profile("test")
public class A1MediatorMockConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	// Simulate remote method delay for UI testing
	@Value("${mock.config.delay:0}")
	private int delayMs;

	private final MockPolicyStore database = new MockPolicyStore();

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
			return database.getTypes();
		}).when(mockApi).a1ControllerGetAllPolicyTypes();

		doAnswer(inv -> {
			if (delayMs > 0) {
				logger.debug("a1ControllerGetPolicyType sleeping {}", delayMs);
				Thread.sleep(delayMs);
			}
			Integer polcyTypeId = inv.<Integer>getArgument(0);
			PolicyTypeSchema policyType = database.getPolicyType(polcyTypeId);
			policyType.setCreateSchema(database.normalize((String) policyType.getCreateSchema()));
			return policyType;
		}).when(mockApi).a1ControllerGetPolicyType(any(Integer.class));

		doAnswer(inv -> {
			if (delayMs > 0) {
				logger.debug("a1ControllerGetAllInstancesForType sleeping {}", delayMs);
				Thread.sleep(delayMs);
			}
			Integer polcyTypeId = inv.<Integer>getArgument(0);
			return database.getInstances(Optional.of(polcyTypeId));
		}).when(mockApi).a1ControllerGetAllInstancesForType(any(Integer.class));

		doAnswer(inv -> {
			if (delayMs > 0) {
				logger.debug("a1ControllerGetPolicyInstance sleeping {}", delayMs);
				Thread.sleep(delayMs);
			}
			Integer polcyTypeId = inv.<Integer>getArgument(0);
			String instanceId = inv.<String>getArgument(1);
			return database.normalize(database.getInstance(polcyTypeId, instanceId));
		}).when(mockApi).a1ControllerGetPolicyInstance(any(Integer.class), any(String.class));

		doAnswer(inv -> {
			if (delayMs > 0) {
				logger.debug("a1ControllerCreateOrReplacePolicyInstance sleeping {}", delayMs);
				Thread.sleep(delayMs);
			}
			Integer polcyTypeId = inv.<Integer>getArgument(0);
			String instanceId = inv.<String>getArgument(1);
			String instance = inv.<String>getArgument(2);
			database.putInstance(polcyTypeId, instanceId, instance);
			return null;
		}).when(mockApi).a1ControllerCreateOrReplacePolicyInstance(any(Integer.class), any(String.class),
				any(String.class));

		doAnswer(inv -> {
			if (delayMs > 0) {
				logger.debug("a1ControllerDeletePolicyInstance sleeping {}", delayMs);
				Thread.sleep(delayMs);
			}
			Integer polcyTypeId = inv.<Integer>getArgument(0);
			String instanceId = inv.<String>getArgument(1);
			database.deleteInstance(polcyTypeId, instanceId);
			return null;
		}).when(mockApi).a1ControllerDeletePolicyInstance(any(Integer.class), any(String.class));

		return mockApi;
	}

	class MockPolicyStore {

		public class PolicyException extends Exception {

			private static final long serialVersionUID = 1L;

			public PolicyException(String message) {
				super(message);
				System.out.println("**** Exception " + message);
			}
		}

		private class PolicyTypeHolder {
			PolicyTypeHolder(PolicyTypeSchema pt) {
				this.policyType = pt;
			}

			String getInstance(String instanceId) throws PolicyException {
				String instance = instances.get(instanceId);
				if (instance == null) {
					throw new PolicyException("Instance not found: " + instanceId);
				}
				return instance;
			}

			PolicyTypeSchema getPolicyType() {
				return policyType;
			}

			void putInstance(String id, String data) {
				instances.put(id, data);
			}

			void deleteInstance(String id) {
				instances.remove(id);
			}

			List<String> getInstances() {
				return new ArrayList<>(instances.keySet());
			}

			private final PolicyTypeSchema policyType;
			private Map<String, String> instances = new HashMap<>();
		}

		MockPolicyStore() {
			try {
				String schema1 = getStringFromFile("anr-policy-schema.json");
				PolicyTypeSchema policy1 = new PolicyTypeSchema();
				policy1.setPolicyTypeId(1);
				policy1.setName("ANR");
				policy1.setDescription("ANR Neighbour Cell Relation Policy");
				policy1.setCreateSchema(schema1);
				types.put(1, new PolicyTypeHolder(policy1));

				String schema2 = getStringFromFile("demo-policy-schema-1.json");
				PolicyTypeSchema policy2 = new PolicyTypeSchema();
				policy2.setPolicyTypeId(2);
				policy2.setName("type1");
				policy2.setDescription("Type1 description");
				policy2.setCreateSchema(schema2);
				types.put(2, new PolicyTypeHolder(policy2));

				String schema3 = getStringFromFile("demo-policy-schema-2.json");
				PolicyTypeSchema policy3 = new PolicyTypeSchema();
				policy3.setPolicyTypeId(3);
				policy3.setName("type2");
				policy3.setDescription("Type2 description");
				policy3.setCreateSchema(schema3);
				types.put(3, new PolicyTypeHolder(policy3));

				String schema4 = getStringFromFile("demo-policy-schema-3.json");
				PolicyTypeSchema policy4 = new PolicyTypeSchema();
				policy4.setPolicyTypeId(4);
				policy4.setName("type3");
				policy4.setDescription("Type3 description");
				policy4.setCreateSchema(schema4);
				types.put(4, new PolicyTypeHolder(policy4));
				try {
					String policyInstance = getStringFromFile("anr-policy-instance.json");
					putInstance(1, "ANR-1", policyInstance);
				} catch (JsonProcessingException | PolicyException e) {
					logger.warn("Unable to add policy type.", e);
				}
			} catch (IOException e) {
				logger.warn("Unable to load database.", e);
			}
		}

		String normalize(String str) {
			return str.replace('\n', ' ');
		}

		void putInstance(Integer typeId, String instanceId, String instanceData)
				throws JsonProcessingException, PolicyException {
			PolicyTypeHolder type = getTypeHolder(typeId);
			type.putInstance(instanceId, instanceData);
		}

		void deleteInstance(Integer typeId, String instanceId) throws JsonProcessingException, PolicyException {
			PolicyTypeHolder type = getTypeHolder(typeId);
			type.deleteInstance(instanceId);
		}

		String getInstance(Integer typeId, String instanceId) throws JsonProcessingException, PolicyException {
			return getTypeHolder(typeId).getInstance(instanceId);
		}

		List<Integer> getTypes() {
			return new ArrayList<>(types.keySet());
		}

		List<String> getInstances(Optional<Integer> typeId) throws PolicyException {
			if (typeId.isPresent()) {
				return getTypeHolder(typeId.get()).getInstances();
			} else {
				Set<String> res = new HashSet<String>();
				for (Iterator<PolicyTypeHolder> i = types.values().iterator(); i.hasNext();) {
					res.addAll(i.next().getInstances());
				}
				return new ArrayList<>(res);
			}
		}

		private PolicyTypeHolder getTypeHolder(Integer typeId) throws PolicyException {
			PolicyTypeHolder typeHolder = types.get(typeId);
			if (typeHolder == null) {
				throw new PolicyException("Type not found: " + typeId);
			}
			return typeHolder;
		}

		private PolicyTypeSchema getPolicyType(Integer typeId) throws PolicyException {
			PolicyTypeHolder typeHolder = getTypeHolder(typeId);
			return typeHolder.getPolicyType();
		}

		private String getStringFromFile(String path) throws IOException {
			InputStream is = MethodHandles.lookup().lookupClass().getClassLoader().getResourceAsStream(path);
			if (is == null) {
				String msg = "Failed to find resource on classpath: " + path;
				logger.error(msg);
				throw new RuntimeException(msg);
			}
			InputStreamReader reader = new InputStreamReader(is, "UTF-8");
			StringBuilder sb = new StringBuilder();
			char[] buf = new char[8192];
			int i;
			while ((i = reader.read(buf)) > 0)
				sb.append(buf, 0, i);
			reader.close();
			is.close();
			return sb.toString();
		}

		private Map<Integer, PolicyTypeHolder> types = new HashMap<>();

	}
}
