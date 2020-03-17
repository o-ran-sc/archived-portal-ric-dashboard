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

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.List;

import org.junit.After;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.oransc.ric.portal.dashboard.DashboardConstants;
import org.oransc.ric.portal.dashboard.config.E2ManagerMockConfiguration;
import org.oransc.ric.portal.dashboard.config.RICInstanceMockConfiguration;
import org.oransc.ric.portal.dashboard.model.RanDetailsTransport;
import org.oransc.ric.portal.dashboard.model.SuccessTransport;
import org.oransc.ricplt.e2mgr.client.model.GetNodebResponse;
import org.oransc.ricplt.e2mgr.client.model.NodebIdentity;
import org.oransc.ricplt.e2mgr.client.model.ResetRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class E2ManagerControllerTest extends AbstractControllerTest {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private ResponseEntity<Void> reset() {
		URI uri = buildUri(null, E2ManagerController.CONTROLLER_PATH, DashboardConstants.RIC_INSTANCE_KEY,
				RICInstanceMockConfiguration.INSTANCE_KEY_1, E2ManagerController.NODEB_PREFIX, "ignored",
				E2ManagerController.RESET_METHOD);
		logger.info("Invoking {}", uri);
		ResetRequest reset = new ResetRequest();
		HttpEntity<ResetRequest> entity = new HttpEntity<>(reset);
		return testRestTemplateAdminRole().exchange(uri, HttpMethod.PUT, entity, Void.class);
	}

	@Test
	public void resetTest() {
		ResponseEntity<Void> voidResponse = reset();
		logger.debug("resetTest: response {}", voidResponse);
		Assertions.assertTrue(voidResponse.getStatusCode().is2xxSuccessful());
	}

	@Test
	public void versionTest() {
		URI uri = buildUri(null, E2ManagerController.CONTROLLER_PATH, DashboardConstants.VERSION_METHOD);
		logger.info("Invoking {}", uri);
		SuccessTransport st = restTemplate.getForObject(uri, SuccessTransport.class);
		Assertions.assertFalse(st.getData().toString().isEmpty());
	}

	@Test
	public void healthTest() {
		URI uri = buildUri(null, E2ManagerController.CONTROLLER_PATH, DashboardConstants.RIC_INSTANCE_KEY,
				RICInstanceMockConfiguration.INSTANCE_KEY_1, E2ManagerController.HEALTH_METHOD);
		logger.info("Invoking {}", uri);
		ResponseEntity<Void> voidResponse = restTemplate.getForEntity(uri, Void.class);
		Assertions.assertTrue(voidResponse.getStatusCode().is2xxSuccessful());
	}

	@Test
	public void ranDetailsTest() {
		URI uri = buildUri(null, E2ManagerController.CONTROLLER_PATH, DashboardConstants.RIC_INSTANCE_KEY,
				RICInstanceMockConfiguration.INSTANCE_KEY_1, E2ManagerController.RAN_METHOD);
		logger.info("Invoking {}", uri);
		ResponseEntity<List<RanDetailsTransport>> response = testRestTemplateStandardRole().exchange(uri,
				HttpMethod.GET, null, new ParameterizedTypeReference<List<RanDetailsTransport>>() {
				});
		Assertions.assertFalse(response.getBody().isEmpty());
	}

	@Test
	public void nodebListTest() {
		URI uri = buildUri(null, E2ManagerController.CONTROLLER_PATH, DashboardConstants.RIC_INSTANCE_KEY,
				RICInstanceMockConfiguration.INSTANCE_KEY_1, E2ManagerController.NODEB_LIST_METHOD);
		logger.info("Invoking {}", uri);
		ResponseEntity<List<NodebIdentity>> response = testRestTemplateStandardRole().exchange(uri, HttpMethod.GET,
				null, new ParameterizedTypeReference<List<NodebIdentity>>() {
				});
		Assertions.assertFalse(response.getBody().isEmpty());
	}

	@Test
	public void nodebStatusTest() {
		URI uri = buildUri(null, E2ManagerController.CONTROLLER_PATH, DashboardConstants.RIC_INSTANCE_KEY,
				RICInstanceMockConfiguration.INSTANCE_KEY_1, E2ManagerController.NODEB_PREFIX,
				E2ManagerMockConfiguration.RAN_NAME_1);
		logger.info("Invoking {}", uri);
		GetNodebResponse response = testRestTemplateStandardRole().getForObject(uri, GetNodebResponse.class);
		Assertions.assertNotNull(response.getRanName());
	}

	// Aka big--button test, run this last
	@After
	public void nodebShutdownPutTest() {
		URI uri = buildUri(null, E2ManagerController.CONTROLLER_PATH, DashboardConstants.RIC_INSTANCE_KEY,
				RICInstanceMockConfiguration.INSTANCE_KEY_1, E2ManagerController.NODEB_SHUTDOWN_METHOD);
		logger.info("Invoking {}", uri);
		ResponseEntity<Void> voidResponse = testRestTemplateAdminRole().exchange(uri, HttpMethod.PUT, null, Void.class);
		logger.debug("nodebPutTest: response {}", voidResponse);
		Assertions.assertTrue(voidResponse.getStatusCode().is2xxSuccessful());
	}

}
