/*-
 * ========================LICENSE_START=================================
 * ORAN-OSC
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
package org.oranosc.ric.portal.dash.controller;

import java.lang.invoke.MethodHandles;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.oranosc.ric.e2mgr.client.api.DefaultApi;
import org.oranosc.ric.e2mgr.client.model.RanSetupRequest;
import org.oranosc.ric.portal.dash.DashboardConstants;
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
 * connections. So this class mocks up some of that needed functionality.
 */
@Configuration
@RestController
@RequestMapping(value = DashboardConstants.ENDPOINT_PREFIX + "/e2mgr", produces = MediaType.APPLICATION_JSON_VALUE)
public class E2ManagerController {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	// Populated by the autowired constructor
	private final DefaultApi e2MgrClient;

	// Tracks the requests previously submitted.
	// TODO remove when the E2 manager is extended.
	private Set<RanSetupRequest> requests = new HashSet<>();

	@Autowired
	public E2ManagerController(final DefaultApi e2MgrClient) {
		Assert.notNull(e2MgrClient, "client must not be null");
		this.e2MgrClient = e2MgrClient;
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

	@ApiOperation(value = "Gets the health from the E2 manager, expressed as the response code.")
	@RequestMapping(value = "/health", method = RequestMethod.GET)
	public void getHealth(HttpServletResponse response) {
		logger.debug("getHealth");
		e2MgrClient.getHealth();
		response.setStatus(e2MgrClient.getApiClient().getStatusCode().value());
	}

	@ApiOperation(value = "Gets the unique requests submitted to the E2 manager.", response = RanSetupRequest.class, responseContainer = "List")
	@RequestMapping(value = "/setup", method = RequestMethod.GET)
	public Iterable<RanSetupRequest> getRequests() {
		logger.debug("getRequests");
		return requests;
	}

	@ApiOperation(value = "Sets up a RAN connection via the E2 manager.")
	@RequestMapping(value = "/setup", method = RequestMethod.POST)
	public void setup(@RequestBody RanSetupRequest rsr, HttpServletResponse response) {
		logger.debug("setup {}", rsr);
		try {
			assertNotEmpty(rsr.getRanIp());
			assertNotEmpty(rsr.getRanName());
			assertNotNull(rsr.getRanPort());
		} catch (Exception ex) {
			logger.error("Bad request", ex);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		try {
			requests.add(rsr);
			e2MgrClient.setupRan(rsr);
		} catch (Exception ex) {
			logger.error("Failed", ex);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

}
