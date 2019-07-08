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

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.oransc.ric.e2mgr.client.model.GetNodebResponse;
import org.oransc.ric.e2mgr.client.model.NodebIdentity;
import org.oransc.ric.e2mgr.client.model.SetupRequest;
import org.oransc.ric.portal.dashboard.DashboardConstants;
import org.oransc.ric.portal.dashboard.controller.E2ManagerController;
import org.oransc.ric.portal.dashboard.model.RanDetailsTransport;
import org.oransc.ric.portal.dashboard.model.SuccessTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class E2ManagerControllerTest extends AbstractControllerTest {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Test
	public void versionTest() {
		URI uri = buildUri(null, E2ManagerController.CONTROLLER_PATH, DashboardConstants.VERSION_METHOD);
		logger.info("Invoking {}", uri);
		SuccessTransport st = restTemplate.getForObject(uri, SuccessTransport.class);
		Assert.assertFalse(st.getData().toString().isEmpty());
	}

	@Test
	public void healthTest() {
		URI uri = buildUri(null, E2ManagerController.CONTROLLER_PATH, E2ManagerController.HEALTH_METHOD);
		logger.info("Invoking {}", uri);
		ResponseEntity<Void> voidResponse = restTemplate.getForEntity(uri, Void.class);
		Assert.assertTrue(voidResponse.getStatusCode().is2xxSuccessful());
	}

	@Test
	public void ranDetailsTest() {
		URI uri = buildUri(null, E2ManagerController.CONTROLLER_PATH, E2ManagerController.RAN_METHOD);
		logger.info("Invoking {}", uri);
		ResponseEntity<List<RanDetailsTransport>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<RanDetailsTransport>>() {
				});
		Assert.assertFalse(response.getBody().isEmpty());
	}

	@Test
	public void nodebListTest() {
		URI uri = buildUri(null, E2ManagerController.CONTROLLER_PATH, E2ManagerController.NODEB_LIST_METHOD);
		logger.info("Invoking {}", uri);
		ResponseEntity<List<NodebIdentity>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<NodebIdentity>>() {
				});
		Assert.assertFalse(response.getBody().isEmpty());
	}

	@Test
	public void nodebStatusTest() {
		URI uri = buildUri(null, E2ManagerController.CONTROLLER_PATH, E2ManagerController.NODEB_METHOD, "nodeb");
		logger.info("Invoking {}", uri);
		GetNodebResponse response = restTemplate.getForObject(uri, GetNodebResponse.class);
		Assert.assertNotNull(response.getRanName());
	}

	@Test
	public void bigRedButtonTest() {
		URI uri = buildUri(null, E2ManagerController.CONTROLLER_PATH, E2ManagerController.NODEB_METHOD);
		logger.info("Invoking {}", uri);
		ResponseEntity<Void> voidResponse = restTemplate.exchange(uri, HttpMethod.DELETE, null, Void.class);
		Assert.assertTrue(voidResponse.getStatusCode().is2xxSuccessful());
	}

	@Test
	public void endcSetupTest() {
		URI uri = buildUri(null, E2ManagerController.CONTROLLER_PATH, E2ManagerController.ENDC_SETUP_METHOD);
		logger.info("Invoking {}", uri);
		SetupRequest setup = new SetupRequest();
		HttpEntity<SetupRequest> entity = new HttpEntity<>(setup);
		ResponseEntity<Void> voidResponse = restTemplate.exchange(uri, HttpMethod.POST, entity, Void.class);
		Assert.assertTrue(voidResponse.getStatusCode().is2xxSuccessful());
	}

	@Test
	public void x2SetupTest() {
		URI uri = buildUri(null, E2ManagerController.CONTROLLER_PATH, E2ManagerController.X2_SETUP_METHOD);
		logger.info("Invoking {}", uri);
		SetupRequest setup = new SetupRequest();
		HttpEntity<SetupRequest> entity = new HttpEntity<>(setup);
		ResponseEntity<Void> voidResponse = restTemplate.exchange(uri, HttpMethod.POST, entity, Void.class);
		Assert.assertTrue(voidResponse.getStatusCode().is2xxSuccessful());
	}

}
