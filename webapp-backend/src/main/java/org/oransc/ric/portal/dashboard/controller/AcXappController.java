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

import javax.servlet.http.HttpServletResponse;

import org.oransc.ric.a1med.client.api.A1MediatorApi;
import org.oransc.ric.portal.dashboard.DashboardApplication;
import org.oransc.ric.portal.dashboard.DashboardConstants;
import org.oransc.ric.portal.dashboard.model.SuccessTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * Provides methods to manage policies of the Admission Control xApp, which
 * initially defines just one. All requests go via the A1 Mediator.
 */
@RestController
@RequestMapping(value = DashboardConstants.ENDPOINT_PREFIX + "/xapp/ac", produces = MediaType.APPLICATION_JSON_VALUE)
public class AcXappController {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	// A "control" is an element in the XApp descriptor
	private static final String AC_CONTROL_NAME = "admission_control_policy";

	// Populated by the autowired constructor
	private final A1MediatorApi a1MediatorApi;

	@Autowired
	public AcXappController(final A1MediatorApi a1MediatorApi) {
		Assert.notNull(a1MediatorApi, "API must not be null");
		this.a1MediatorApi = a1MediatorApi;
	}

	@ApiOperation(value = "Gets the A1 client library MANIFEST.MF property Implementation-Version.", response = SuccessTransport.class)
	@RequestMapping(value = DashboardConstants.VERSION_PATH, method = RequestMethod.GET)
	public SuccessTransport getA1MediatorClientVersion() {
		return new SuccessTransport(200, DashboardApplication.getImplementationVersion(A1MediatorApi.class));
	}

	/*
	 * GET policy is not supported at present by A1 Mediator! Always returns 501.
	 */
	@ApiOperation(value = "Gets the admission control policy for AC xApp via the A1 Mediator")
	@RequestMapping(value = "admctrl", method = RequestMethod.GET)
	public Object getAdmissionControlPolicy(HttpServletResponse response) {
		logger.debug("getAdmissionControlPolicy");
		response.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
		return null;
	}

	/*
	 * This controller is deliberately kept ignorant of the data expected by AC. The
	 * fields are defined in the ACAdmissionIntervalControl Typescript interface.
	 */
	@ApiOperation(value = "Sets the admission control policy for AC xApp via the A1 Mediator")
	@RequestMapping(value = "catime", method = RequestMethod.PUT)
	public void setAdmissionControlPolicy(@ApiParam(value = "Admission control policy") @RequestBody JsonNode acPolicy, //
			HttpServletResponse response) {
		logger.debug("setAdmissionControlPolicy {}", acPolicy);
		a1MediatorApi.a1ControllerPutHandler(AC_CONTROL_NAME, acPolicy);
		response.setStatus(a1MediatorApi.getApiClient().getStatusCode().value());
	}

}
