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
package org.oransc.ric.portal.dashboard.controller;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.oransc.ric.portal.dashboard.DashboardConstants;
import org.oransc.ric.portal.dashboard.config.A1MediatorMockConfiguration;
import org.oransc.ric.portal.dashboard.config.RICInstanceMockConfiguration;
import org.oransc.ric.portal.dashboard.model.SuccessTransport;
import org.oransc.ricplt.a1.client.model.PolicyTypeSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class A1MediatorControllerTest extends AbstractControllerTest {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Test
	public void versionTest() {
		URI uri = buildUri(null, A1MediatorController.CONTROLLER_PATH, DashboardConstants.VERSION_METHOD);
		logger.info("Invoking {}", uri);
		SuccessTransport st = restTemplate.getForObject(uri, SuccessTransport.class);
		Assertions.assertFalse(st.getData().toString().isEmpty());
	}

	@Test
	public void getTypeIdsTest() throws IOException {
		URI uri = buildUri(null, A1MediatorController.CONTROLLER_PATH, DashboardConstants.RIC_INSTANCE_KEY,
				RICInstanceMockConfiguration.INSTANCE_KEY_1, A1MediatorController.PP_TYPE_ID);
		logger.info("Invoking {}", uri);
		ResponseEntity<List<Integer>> response = testRestTemplateStandardRole().exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<Integer>>() {
				});
		Assertions.assertTrue(response.getStatusCode().is2xxSuccessful());
		Assert.assertFalse(response.getBody().isEmpty());
	}

	@Test
	public void getPolicyTypeTest() throws IOException {
		URI uri = buildUri(null, A1MediatorController.CONTROLLER_PATH, DashboardConstants.RIC_INSTANCE_KEY,
				RICInstanceMockConfiguration.INSTANCE_KEY_1, A1MediatorController.PP_TYPE_ID,
				Integer.toString(A1MediatorMockConfiguration.ADMISSION_CONTROL_POLICY_ID));
		logger.info("Invoking {}", uri);
		ResponseEntity<PolicyTypeSchema> response = testRestTemplateStandardRole().exchange(uri, HttpMethod.GET, null,
				PolicyTypeSchema.class);
		Assertions.assertTrue(response.getStatusCode().is2xxSuccessful());
		Assert.assertFalse(response.getBody().getName().isEmpty());
	}

	@Test
	public void getInstanceTest() throws IOException {
		URI uri = buildUri(null, A1MediatorController.CONTROLLER_PATH, DashboardConstants.RIC_INSTANCE_KEY,
				RICInstanceMockConfiguration.INSTANCE_KEY_1, A1MediatorController.PP_TYPE_ID,
				Integer.toString(A1MediatorMockConfiguration.ADMISSION_CONTROL_POLICY_ID),
				A1MediatorController.PP_INST_ID, A1MediatorMockConfiguration.AC_CONTROL_NAME);
		logger.info("Invoking {}", uri);
		ResponseEntity<String> response = testRestTemplateStandardRole().exchange(uri, HttpMethod.GET, null,
				String.class);
		Assertions.assertTrue(response.getStatusCode().is2xxSuccessful());
		Assert.assertFalse(response.getBody().isEmpty());
	}

	@Test
	public void putInstanceTest() throws IOException {
		URI uri = buildUri(null, A1MediatorController.CONTROLLER_PATH, DashboardConstants.RIC_INSTANCE_KEY,
				RICInstanceMockConfiguration.INSTANCE_KEY_1, A1MediatorController.PP_TYPE_ID,
				Integer.toString(A1MediatorMockConfiguration.ADMISSION_CONTROL_POLICY_ID),
				A1MediatorController.PP_INST_ID, A1MediatorMockConfiguration.AC_CONTROL_NAME);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode body = mapper.readTree("{ \"policy\" : true }");
		HttpEntity<JsonNode> entity = new HttpEntity<>(body);
		logger.info("Invoking {} with body {}", uri, body);
		ResponseEntity<Void> voidResponse = testRestTemplateAdminRole().exchange(uri, HttpMethod.PUT, entity,
				Void.class);
		Assertions.assertTrue(voidResponse.getStatusCode().is2xxSuccessful());
	}

}
