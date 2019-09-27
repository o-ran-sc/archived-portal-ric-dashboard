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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.onap.portalsdk.core.onboarding.util.PortalApiConstants;
import org.onap.portalsdk.core.restful.domain.EcompUser;
import org.oransc.ric.portal.dashboard.DashboardConstants;
import org.oransc.ric.portal.dashboard.config.PortalApIMockConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class PortalRestCentralServiceTest extends AbstractControllerTest {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	// paths are hardcoded here exactly like the EPSDK-FW library :(

	@Test
	public void getAnalyticsTest() {
		// paths are hardcoded here exactly like the EPSDK-FW library :(
		URI uri = buildUri(null, PortalApiConstants.API_PREFIX, "/analytics");
		logger.info("Invoking {}", uri);
		ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, null, String.class);
		// No Portal is available so this always fails
		Assertions.assertTrue(response.getStatusCode().is4xxClientError());
	}

	@Test
	public void getLoginPageTest() {
		URI uri = buildUri(null, DashboardConstants.LOGIN_PAGE);
		logger.info("Invoking {}", uri);
		ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, null, String.class);
		Assertions.assertTrue(response.getStatusCode().is2xxSuccessful());
		Assertions.assertTrue(response.getBody().contains("Please log in"));
	}

	private HttpEntity<Object> getEntityWithHeaders(Object body) {
		HttpHeaders headers = new HttpHeaders();
		headers.set(PortalApIMockConfiguration.PORTAL_USERNAME_HEADER_KEY,
				PortalApIMockConfiguration.PORTAL_USERNAME_HEADER_KEY);
		headers.set(PortalApIMockConfiguration.PORTAL_PASSWORD_HEADER_KEY,
				PortalApIMockConfiguration.PORTAL_PASSWORD_HEADER_KEY);
		HttpEntity<Object> entity = new HttpEntity<>(body, headers);
		return entity;
	}

	@Test
	public void createUserTest() {
		final String loginId = "login1";
		URI create = buildUri(null, PortalApiConstants.API_PREFIX, "user");
		logger.info("Invoking {}", create);
		EcompUser user = new EcompUser();
		user.setLoginId(loginId);
		HttpEntity<Object> requestEntity = getEntityWithHeaders(user);
		ResponseEntity<String> response = restTemplate.exchange(create, HttpMethod.POST, requestEntity, String.class);
		Assertions.assertTrue(response.getStatusCode().is2xxSuccessful());
	}

	@Test
	public void updateUserTest() {
		final String loginId = "login2";
		URI create = buildUri(null, PortalApiConstants.API_PREFIX, "user");
		logger.info("Invoking {}", create);
		EcompUser user = new EcompUser();
		user.setLoginId(loginId);
		HttpEntity<Object> requestEntity = getEntityWithHeaders(user);
		// Create
		ResponseEntity<String> response = restTemplate.exchange(create, HttpMethod.POST, requestEntity, String.class);
		Assertions.assertTrue(response.getStatusCode().is2xxSuccessful());
		URI update = buildUri(null, PortalApiConstants.API_PREFIX, "user", loginId);
		user.setEmail("user@company.org");
		requestEntity = getEntityWithHeaders(user);
		response = restTemplate.exchange(update, HttpMethod.POST, requestEntity, String.class);
		Assertions.assertTrue(response.getStatusCode().is2xxSuccessful());
	}

}
