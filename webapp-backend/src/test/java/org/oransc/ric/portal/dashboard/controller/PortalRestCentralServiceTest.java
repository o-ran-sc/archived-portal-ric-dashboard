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
import org.oransc.ric.portal.dashboard.config.PortalApiMockConfiguration;
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
	public void analyticsTest() {
		// paths are hardcoded here exactly like the EPSDK-FW library :(
		URI uri = buildUri(null, PortalApiConstants.API_PREFIX, "/analytics");
		logger.info("Invoking {}", uri);
		ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, null, String.class);
		Assertions.assertTrue(response.getStatusCode().is4xxClientError());
	}

	@Test
	public void getUserTest() {
		URI uri = buildUri(null, PortalApiConstants.API_PREFIX, "/user/userid");
		logger.info("Invoking {}", uri);
		HttpHeaders headers = new HttpHeaders();
		headers.set(PortalApiMockConfiguration.PORTAL_USERNAME_HEADER_KEY,
				PortalApiMockConfiguration.PORTAL_USERNAME_HEADER_KEY);
		headers.set(PortalApiMockConfiguration.PORTAL_PASSWORD_HEADER_KEY,
				PortalApiMockConfiguration.PORTAL_PASSWORD_HEADER_KEY);
		HttpEntity<Object> request = new HttpEntity<>(headers);
		ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, request, String.class);
		Assertions.assertTrue(response.getStatusCode().is4xxClientError());
	}

}
