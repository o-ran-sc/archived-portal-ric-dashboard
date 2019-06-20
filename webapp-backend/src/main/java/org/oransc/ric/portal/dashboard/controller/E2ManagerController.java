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

import org.oransc.ric.e2mgr.client.api.HealthCheckApi;
import org.oransc.ric.e2mgr.client.api.NodebApi;
import org.oransc.ric.e2mgr.client.model.GetNodebResponse;
import org.oransc.ric.e2mgr.client.model.SetupRequest;
import org.oransc.ric.portal.dashboard.DashboardApplication;
import org.oransc.ric.portal.dashboard.DashboardConstants;
import org.oransc.ric.portal.dashboard.model.E2SetupRequestType;
import org.oransc.ric.portal.dashboard.model.E2SetupResponse;
import org.oransc.ric.portal.dashboard.model.SuccessTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;

import io.swagger.annotations.ApiOperation;

/**
 * Proxies calls from the front end to the E2 Manager API. All methods answer
 * 502 on failure: <blockquote>HTTP server received an invalid response from a
 * server it consulted when acting as a proxy or gateway.</blockquote>
 * 
 * As of this writing the E2 interface does not support get-all, so this class
 * mocks up some of that functionality.
 */
@Configuration
@RestController
@RequestMapping(value = DashboardConstants.ENDPOINT_PREFIX + "/e2mgr", produces = MediaType.APPLICATION_JSON_VALUE)
public class E2ManagerController {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	// Path parameters
	private static final String PP_RANNAME = "ranName";

	// Populated by the autowired constructor
	private final HealthCheckApi e2HealthCheckApi;
	private final NodebApi e2NodebApi;

	// Stores the requests and results.
	// TODO remove when the E2 manager is extended.
	private Set<E2SetupResponse> responses = new HashSet<>();

	@Autowired
	public E2ManagerController(final HealthCheckApi e2HealthCheckApi, final NodebApi e2NodebApi) {
		Assert.notNull(e2HealthCheckApi, "API must not be null");
		Assert.notNull(e2NodebApi, "API must not be null");
		this.e2HealthCheckApi = e2HealthCheckApi;
		this.e2NodebApi = e2NodebApi;
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
	public SuccessTransport getE2ManagerClientVersion() {
		return new SuccessTransport(200, DashboardApplication.getImplementationVersion(HealthCheckApi.class));
	}

	@ApiOperation(value = "Gets the health from the E2 manager, expressed as the response code.")
	@RequestMapping(value = "/health", method = RequestMethod.GET)
	public Object healthGet(HttpServletResponse response) {
		logger.debug("healthGet");
		try {
			e2HealthCheckApi.healthGet();
			response.setStatus(e2HealthCheckApi.getApiClient().getStatusCode().value());
			return null;
		} catch (HttpStatusCodeException ex) {
			logger.warn("healthGet failed: {}", ex.toString());
			return ResponseEntity.status(HttpServletResponse.SC_BAD_GATEWAY).body(ex.getResponseBodyAsString());
		}
	}

	// TODO replace with actual functionality
	@ApiOperation(value = "Gets the unique requests submitted to the E2 manager.", response = E2SetupResponse.class, responseContainer = "List")
	@RequestMapping(value = "/setup", method = RequestMethod.GET)
	public Iterable<E2SetupResponse> getRequests() {
		logger.debug("getRequests");
		return responses;
	}

	@ApiOperation(value = "Get RAN by name.", response = GetNodebResponse.class)
	@RequestMapping(value = "/nodeb/{" + PP_RANNAME + "}", method = RequestMethod.GET)
	public Object getNb(@PathVariable(PP_RANNAME) String ranName) {
		logger.debug("getNb {}", ranName);
		try {
			return e2NodebApi.getNb(ranName);
		} catch (HttpStatusCodeException ex) {
			logger.warn("getNb failed: {}", ex.toString());
			return ResponseEntity.status(HttpServletResponse.SC_BAD_GATEWAY).body(ex.getResponseBodyAsString());
		}
	}

	@ApiOperation(value = "Close all connections to the RANs and delete the data from the nodeb-rnib DB.")
	@RequestMapping(value = "/nodeb", method = RequestMethod.DELETE)
	public void nodebDelete() {
		logger.debug("nodebDelete");
		e2NodebApi.nodebDelete();
		// TODO: remove this mock functionality
		responses.clear();
	}

	@ApiOperation(value = "Sets up an EN-DC RAN connection via the E2 manager.", response = E2SetupResponse.class)
	@RequestMapping(value = "/endcSetup", method = RequestMethod.POST)
	public Object endcSetup(@RequestBody SetupRequest setupRequest) {
		logger.debug("endcSetup {}", setupRequest);
		try {
			assertNotEmpty(setupRequest.getRanIp());
			assertNotEmpty(setupRequest.getRanName());
			assertNotNull(setupRequest.getRanPort());
		} catch (Exception ex) {
			return new E2SetupResponse(E2SetupRequestType.ENDC, setupRequest, HttpServletResponse.SC_BAD_REQUEST);
		}
		try {
			e2NodebApi.endcSetup(setupRequest);
			E2SetupResponse r = new E2SetupResponse(E2SetupRequestType.ENDC, setupRequest,
					e2NodebApi.getApiClient().getStatusCode().value());
			responses.add(r);
			return r;
		} catch (HttpStatusCodeException ex) {
			logger.warn("endcSetup failed: {}", ex.toString());
			return ResponseEntity.status(HttpServletResponse.SC_BAD_GATEWAY).body(ex.getResponseBodyAsString());
		}
	}

	@ApiOperation(value = "Sets up an X2 RAN connection via the E2 manager.", response = E2SetupResponse.class)
	@RequestMapping(value = "/x2Setup", method = RequestMethod.POST)
	public Object x2Setup(@RequestBody SetupRequest setupRequest) {
		logger.debug("x2Setup {}", setupRequest);
		try {
			assertNotEmpty(setupRequest.getRanIp());
			assertNotEmpty(setupRequest.getRanName());
			assertNotNull(setupRequest.getRanPort());
		} catch (Exception ex) {
			return new E2SetupResponse(E2SetupRequestType.ENDC, setupRequest, HttpServletResponse.SC_BAD_REQUEST);
		}
		try {
			e2NodebApi.x2Setup(setupRequest);
			E2SetupResponse r = new E2SetupResponse(E2SetupRequestType.X2, setupRequest,
					e2NodebApi.getApiClient().getStatusCode().value());
			responses.add(r);
			return r;
		} catch (HttpStatusCodeException ex) {
			logger.warn("x2Setup failed: {}", ex.toString());
			return ResponseEntity.status(HttpServletResponse.SC_BAD_GATEWAY).body(ex.getResponseBodyAsString());
		}
	}

}
