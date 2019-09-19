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

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.oransc.ric.portal.dashboard.config.A1MediatorMockConfiguration;
import org.oransc.ric.portal.dashboard.model.SuccessTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class A1MediatorControllerTest extends AbstractControllerTest {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Test
	public void versionTest() {
		URI uri = buildUri(null, A1MediatorController.CONTROLLER_PATH, A1MediatorController.VERSION_METHOD);
		logger.info("Invoking {}", uri);
		SuccessTransport st = restTemplate.getForObject(uri, SuccessTransport.class);
		Assertions.assertFalse(st.getData().toString().isEmpty());
	}

	@Test
	public void getTest() throws IOException {
		URI uri = buildUri(null, A1MediatorController.CONTROLLER_PATH, A1MediatorController.PP_POLICY_TYPES);
		logger.info("Invoking {}", uri);
		ResponseEntity<String> response = testRestTemplateStandardRole().exchange(uri, HttpMethod.GET, null,
				String.class);
		Assertions.assertTrue(response.getStatusCode().is2xxSuccessful());
		Assert.assertFalse(response.getBody().isEmpty());
	}

	@Test
	public void putTest() throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode body = mapper.readTree("{ \"policy\" : true }");
		URI uri = buildUri(null, A1MediatorController.CONTROLLER_PATH, A1MediatorController.PP_POLICY_TYPES, "1",
				A1MediatorController.PP_POLICY_INSTANCES, "ID");
		HttpEntity<JsonNode> entity = new HttpEntity<>(body);
		logger.info("Invoking {}", uri);
		ResponseEntity<Void> voidResponse = testRestTemplateAdminRole().exchange(uri, HttpMethod.PUT, entity,
				Void.class);
		Assertions.assertTrue(voidResponse.getStatusCode().is2xxSuccessful());
	}

}
