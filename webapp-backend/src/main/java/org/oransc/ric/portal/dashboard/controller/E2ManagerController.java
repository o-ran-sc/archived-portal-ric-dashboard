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
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.oransc.ric.e2mgr.client.api.E2ManagerApi;
import org.oransc.ric.e2mgr.client.model.SetupRequest;
import org.oransc.ric.portal.dashboard.DashboardApplication;
import org.oransc.ric.portal.dashboard.DashboardConstants;
import org.oransc.ric.portal.dashboard.model.E2SetupRequestType;
import org.oransc.ric.portal.dashboard.model.E2SetupResponse;
import org.oransc.ric.portal.dashboard.model.IDashboardResponse;
import org.oransc.ric.portal.dashboard.model.SuccessTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;

/**
 * Provides methods to contact the E2 Manager.
 * 
 * As of this writing the E2 interface only supports setup connection and check
 * health actions; it does not support query or close operations on existing
 * connections. So this class mocks up some of that functionality.
 */
@Configuration
@RestController
@RequestMapping(value = DashboardConstants.ENDPOINT_PREFIX + "/e2mgr", produces = MediaType.APPLICATION_JSON_VALUE)
public class E2ManagerController {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	// Populated by the autowired constructor
	private final E2ManagerApi e2ManagerApi;

	// Stores the requests and results.
	// TODO remove when the E2 manager is extended.
	private Set<E2SetupResponse> responses = new HashSet<>();

	@Autowired
	public E2ManagerController(final E2ManagerApi e2ManagerApi) {
		Assert.notNull(e2ManagerApi, "API must not be null");
		this.e2ManagerApi = e2ManagerApi;
	}

	private void assertNotNull(Object o) {
		if (o == null)
			throw new IllegalArgumentException("Null not permitted");
	}

	private void assertNotEmpty(String s) {
		assertNotNull(s);
		if (s.isEmpty())
			throw new IllegalArgumentException("Empty not permitted");
	}

	@ApiOperation(value = "Gets the E2 manager client library MANIFEST.MF property Implementation-Version.", response = SuccessTransport.class)
	@RequestMapping(value = DashboardConstants.VERSION_PATH, method = RequestMethod.GET)
	public SuccessTransport getVersion() {
		logger.debug("getVersion enter");
		return new SuccessTransport(200, DashboardApplication.getVersion(E2ManagerApi.class));
	}

	@ApiOperation(value = "Gets the health from the E2 manager, expressed as the response code.")
	@RequestMapping(value = "/health", method = RequestMethod.GET)
	public void getHealth(HttpServletResponse response) {
		logger.debug("getHealth");
		e2ManagerApi.healthCheck();
		response.setStatus(e2ManagerApi.getApiClient().getStatusCode().value());
	}

	@ApiOperation(value = "Gets the unique requests submitted to the E2 manager.", response = E2SetupResponse.class, responseContainer = "List")
	@RequestMapping(value = "/setup", method = RequestMethod.GET)
	public Iterable<E2SetupResponse> getRequests() {
		logger.debug("getRequests");
		return responses;
	}

	@ApiOperation(value = "Sets up an EN-DC RAN connection via the E2 manager.", response = E2SetupResponse.class)
	@RequestMapping(value = "/endcSetup", method = RequestMethod.POST)
	public E2SetupResponse endcSetup(@RequestBody SetupRequest setupRequest, HttpServletResponse response) {
		logger.debug("endcSetup {}", setupRequest);
		int responseCode = -1;
		try {
			assertNotEmpty(setupRequest.getRanIp());
			assertNotEmpty(setupRequest.getRanName());
			assertNotNull(setupRequest.getRanPort());
			e2ManagerApi.endcSetup(setupRequest);
			responseCode = e2ManagerApi.getApiClient().getStatusCode().value();
		} catch (Exception ex) {
			logger.warn("endcSetup failed", ex);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			responseCode = HttpServletResponse.SC_BAD_REQUEST;
		}
		E2SetupResponse r = new E2SetupResponse(E2SetupRequestType.ENDC, setupRequest, responseCode);
		responses.add(r);
		return r;
	}

	@ApiOperation(value = "Sets up an X2 RAN connection via the E2 manager.", response = E2SetupResponse.class)
	@RequestMapping(value = "/x2Setup", method = RequestMethod.POST)
	public IDashboardResponse x2Setup(@RequestBody SetupRequest setupRequest, HttpServletResponse response) {
		logger.debug("x2Setup {}", setupRequest);
		int responseCode = -1;
		try {
			assertNotEmpty(setupRequest.getRanIp());
			assertNotEmpty(setupRequest.getRanName());
			assertNotNull(setupRequest.getRanPort());
			e2ManagerApi.setup(setupRequest);
			responseCode = e2ManagerApi.getApiClient().getStatusCode().value();
		} catch (Exception ex) {
			logger.warn("x2Setup failed", ex);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			responseCode = HttpServletResponse.SC_BAD_REQUEST;
		}
		E2SetupResponse r = new E2SetupResponse(E2SetupRequestType.X2, setupRequest, responseCode);
		responses.add(r);
		return r;
	}

}
