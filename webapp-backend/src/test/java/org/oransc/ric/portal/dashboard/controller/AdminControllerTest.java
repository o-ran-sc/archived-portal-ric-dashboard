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
package org.oransc.ric.portal.dashboard.controller;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.oransc.ric.portal.dashboard.DashboardConstants;
import org.oransc.ric.portal.dashboard.model.DashboardUser;
import org.oransc.ric.portal.dashboard.model.ErrorTransport;
import org.oransc.ric.portal.dashboard.model.IDashboardResponse;
import org.oransc.ric.portal.dashboard.model.SuccessTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class AdminControllerTest extends AbstractControllerTest {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	//public static final String APP_NAME = DashboardConstants.APP_NAME;

	@Test
	public void versionTest() {
		URI uri = buildUri(null, AdminController.CONTROLLER_PATH, AdminController.VERSION_METHOD);
		logger.info("Invoking {}", uri);
		SuccessTransport st = restTemplate.getForObject(uri, SuccessTransport.class);
		Assertions.assertFalse(st.getData().toString().isEmpty());
	}

	@Test
	public void healthTest() {
		URI uri = buildUri(null, AdminController.CONTROLLER_PATH, AdminController.HEALTH_METHOD);
		logger.info("Invoking {}", uri);
		ResponseEntity<Void> voidResponse = restTemplate.getForEntity(uri, Void.class);
		Assertions.assertTrue(voidResponse.getStatusCode().is2xxSuccessful());
	}

	@Test
	public void getUsersTest() {
		URI uri = buildUri(null, AdminController.CONTROLLER_PATH, AdminController.USER_METHOD);
		logger.info("Invoking {}", uri);
		ResponseEntity<List<DashboardUser>> response = testRestTemplateAdminRole().exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<DashboardUser>>() {
				});
		Assertions.assertFalse(response.getBody().isEmpty());
	}

	@Test
	public void getUsersTestRoleAuthFail() {
		URI uri = buildUri(null, AdminController.CONTROLLER_PATH, AdminController.USER_METHOD);
		logger.info("Invoking {}", uri);
		ResponseEntity<String> response = testRestTemplateStandardRole().exchange(uri, HttpMethod.GET, null,
				String.class);
		Assertions.assertTrue(response.getStatusCode().is4xxClientError());
	}

	@Test
	public void getxAppMetricsUrlTest() {
		Map<String, String> metricsQueryParms = new HashMap<String, String>();
		metricsQueryParms.put("app", DashboardConstants.APP_NAME_AC);
		URI uri = buildUri(metricsQueryParms, AdminController.CONTROLLER_PATH, AdminController.XAPPMETRICS_METHOD);
		logger.debug("Invoking {}", uri);
		ResponseEntity<SuccessTransport> successResponse = testRestTemplateStandardRole().exchange(uri, HttpMethod.GET, null,
				SuccessTransport.class);
		Assertions.assertFalse(successResponse.getBody().getData().toString().isEmpty());
		Assertions.assertTrue(successResponse.getStatusCode().is2xxSuccessful());
	}

	@Test
	public void getxAppMetricsUrlTestFail() {
		Map<String, String> metricsQueryParms = new HashMap<String, String>(); 
		//Providing a bogus value for application name in query parameter to test failure 
		metricsQueryParms.put("app", "ABCD");
		URI uri = buildUri(metricsQueryParms, AdminController.CONTROLLER_PATH, AdminController.XAPPMETRICS_METHOD);
		logger.debug("Invoking {}", uri);
		ResponseEntity<ErrorTransport> errorResponse = testRestTemplateStandardRole().exchange(uri, HttpMethod.GET, null,
				ErrorTransport.class);
		logger.debug("{}", errorResponse.getBody().getMessage().toString());
		Assertions.assertTrue(errorResponse.getStatusCode().is4xxClientError());
	}

}
