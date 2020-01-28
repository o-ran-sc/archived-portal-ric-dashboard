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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.onap.portalsdk.core.restful.domain.EcompUser;
import org.oransc.ric.portal.dashboard.DashboardConstants;
import org.oransc.ric.portal.dashboard.config.RICInstanceMockConfiguration;
import org.oransc.ric.portal.dashboard.model.AppStats;
import org.oransc.ric.portal.dashboard.model.RicInstanceKeyName;
import org.oransc.ric.portal.dashboard.model.StatsDetailsTransport;
import org.oransc.ric.portal.dashboard.model.SuccessTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AdminControllerTest extends AbstractControllerTest {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

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
	public void getInstancesTest() {
		URI uri = buildUri(null, AdminController.CONTROLLER_PATH, AdminController.INSTANCE_METHOD);
		logger.info("Invoking {}", uri);
		ResponseEntity<List<RicInstanceKeyName>> response = testRestTemplateStandardRole().exchange(uri, HttpMethod.GET,
				null, new ParameterizedTypeReference<List<RicInstanceKeyName>>() {
				});
		Assertions.assertFalse(response.getBody().isEmpty());
	}

	@Test
	public void getUsersTest() {
		URI uri = buildUri(null, AdminController.CONTROLLER_PATH, AdminController.USER_METHOD);
		logger.info("Invoking {}", uri);
		ResponseEntity<List<EcompUser>> response = testRestTemplateAdminRole().exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<EcompUser>>() {
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
	
	@Order(1)
	@Test
	public void getAppStatsTest() {
		URI uri = buildUri(null, AdminController.CONTROLLER_PATH, DashboardConstants.RIC_INSTANCE_KEY, "i1", AdminController.STATAPPMETRIC_METHOD);
		logger.info("Invoking uri {}", uri);
		ResponseEntity<List<AppStats>> response = testRestTemplateAdminRole().exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<AppStats>>() {
				});
		Assertions.assertFalse(response.getBody().isEmpty());
		Assertions.assertNotEquals(0,response.getBody().get(0).getStatsDetails().getAppId());
	}
	
	@Order(2)
	@Test
	public void createAppStatsTest() {
		URI uri = buildUri(null, AdminController.CONTROLLER_PATH, DashboardConstants.RIC_INSTANCE_KEY, RICInstanceMockConfiguration.INSTANCE_KEY_1, AdminController.STATAPPMETRIC_METHOD);
		logger.info("Invoking uri {}", uri);
		StatsDetailsTransport statsDetails = new StatsDetailsTransport();
		statsDetails.setAppName("MachLearn-2");
		statsDetails.setMetricUrl("https://www.example2.com");
		AppStats st =  testRestTemplateAdminRole().postForObject(uri, statsDetails, AppStats.class);
		Assertions.assertFalse(st.getStatsDetails().getAppName().isEmpty());
		statsDetails.setAppName("MachLearn-2-next");
		statsDetails.setMetricUrl("https://www.example2-next.com");
		AppStats stNext =  testRestTemplateAdminRole().postForObject(uri, statsDetails, AppStats.class);
		Assertions.assertTrue(st.getStatsDetails().getAppId()<stNext.getStatsDetails().getAppId());
	}
	
	@Order(3)
	@Test
	public void updateAppStatsTest() {
		URI uri = buildUri(null, AdminController.CONTROLLER_PATH, DashboardConstants.RIC_INSTANCE_KEY, RICInstanceMockConfiguration.INSTANCE_KEY_1, AdminController.STATAPPMETRIC_METHOD);
		logger.info("Invoking uri {}", uri);
		StatsDetailsTransport statsDetails = new StatsDetailsTransport();
		statsDetails.setAppId(1);
		statsDetails.setAppName("MachLearn-1");
		statsDetails.setMetricUrl("https://www.example1.com");
		HttpEntity<StatsDetailsTransport> entity = new HttpEntity<>(statsDetails);
		ResponseEntity<String> stringResponse = testRestTemplateAdminRole().exchange(uri, HttpMethod.PUT, entity,
				String.class);
		Assertions.assertTrue(stringResponse.getStatusCode().is2xxSuccessful());
	}
	
	@Order(4)
	@Test
	public void deleteAppStatsTest() {
		URI uri = buildUri(null, AdminController.CONTROLLER_PATH, DashboardConstants.RIC_INSTANCE_KEY, RICInstanceMockConfiguration.INSTANCE_KEY_1, AdminController.STATAPPMETRIC_METHOD, DashboardConstants.APP_ID, "1");
		logger.info("Invoking uri {}", uri);
		ResponseEntity<String> stringResponse = testRestTemplateAdminRole().exchange(uri, HttpMethod.DELETE, null,
				String.class);
		Assertions.assertTrue(stringResponse.getStatusCode().is2xxSuccessful());
	}

	@Test
	public void throwHttpStatusCodeExceptionTest() {
		URI uri = buildUri(null, AdminController.CONTROLLER_PATH,
				AdminControllerExtension.HTTP_STATUS_CODE_EXCEPTION_METHOD);
		logger.debug("Invoking {}", uri);
		ResponseEntity<String> response = testRestTemplateStandardRole().exchange(uri, HttpMethod.GET, null,
				String.class);
		logger.debug("{}", response.getBody().toString());
		Assertions.assertTrue(response.getStatusCode().is5xxServerError());
	}

	@Test
	public void throwRestClientResponseExceptionTest() {
		URI uri = buildUri(null, AdminController.CONTROLLER_PATH,
				AdminControllerExtension.REST_CLIENT_RESPONSE_EXCEPTION_METHOD);
		logger.debug("Invoking {}", uri);
		ResponseEntity<String> errorResponse = testRestTemplateStandardRole().exchange(uri, HttpMethod.GET, null,
				String.class);
		logger.debug("{}", errorResponse.getBody());
		Assertions.assertTrue(errorResponse.getStatusCode().is5xxServerError());
	}

	@Test
	public void throwRuntimeExceptionTest() {
		URI uri = buildUri(null, AdminController.CONTROLLER_PATH, AdminControllerExtension.RUNTIME_EXCEPTION_METHOD);
		logger.debug("Invoking {}", uri);
		ResponseEntity<String> errorResponse = testRestTemplateStandardRole().exchange(uri, HttpMethod.GET, null,
				String.class);
		logger.debug("{}", errorResponse.getBody());
		Assertions.assertTrue(errorResponse.getStatusCode().is5xxServerError());
	}

}
