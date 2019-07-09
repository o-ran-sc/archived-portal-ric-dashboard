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
package org.oransc.ric.portal.dashboard.test.controller;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.URI;

import org.junit.Assert;
import org.junit.Test;
import org.oransc.ric.portal.dashboard.controller.AcXappController;
import org.oransc.ric.portal.dashboard.model.SuccessTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AcXappControllerTest extends AbstractControllerTest {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Test
	public void versionTest() {
		URI uri = buildUri(null, AcXappController.CONTROLLER_PATH, AcXappController.VERSION_METHOD);
		logger.info("Invoking {}", uri);
		SuccessTransport st = restTemplate.getForObject(uri, SuccessTransport.class);
		Assert.assertFalse(st.getData().toString().isEmpty());
	}

	@Test
	public void getTest() throws IOException {
		// Always returns 501; surprised that no exception is thrown.
		URI uri = buildUri(null, AcXappController.CONTROLLER_PATH, AcXappController.ADMCTRL_METHOD);
		logger.info("Invoking {}", uri);
		ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, null, String.class);
		Assert.assertTrue(response.getStatusCode().is5xxServerError());
	}

	@Test
	public void putTest() throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode body = mapper.readTree("{ \"policy\" : true }");
		URI uri = buildUri(null, AcXappController.CONTROLLER_PATH, AcXappController.ADMCTRL_METHOD);
		HttpEntity<JsonNode> entity = new HttpEntity<>(body);
		logger.info("Invoking {}", uri);
		ResponseEntity<Void> voidResponse = restTemplate.exchange(uri, HttpMethod.PUT, entity, Void.class);
		Assert.assertTrue(voidResponse.getStatusCode().is2xxSuccessful());
	}

}
