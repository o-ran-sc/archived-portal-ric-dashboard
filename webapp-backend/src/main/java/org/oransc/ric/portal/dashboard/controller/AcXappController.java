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
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * Provides methods to manage policies of the Admission Control xApp. All
 * messages go via the A1 Mediatior.
 */
@Configuration
@RestController
@RequestMapping(value = DashboardConstants.ENDPOINT_PREFIX + "/xapp/ac", produces = MediaType.APPLICATION_JSON_VALUE)
public class AcXappController {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private static final String POLICY_NAME = "policyname";

	// Populated by the autowired constructor
	private final A1MediatorApi a1MediatorApi;

	@Autowired
	public AcXappController(final A1MediatorApi a1MediatorApi) {
		Assert.notNull(a1MediatorApi, "API must not be null");
		this.a1MediatorApi = a1MediatorApi;
	}

	@ApiOperation(value = "Gets the A1 client library MANIFEST.MF property Implementation-Version.", response = SuccessTransport.class)
	@RequestMapping(value = DashboardConstants.VERSION_PATH, method = RequestMethod.GET)
	public SuccessTransport getVersion() {
		logger.debug("getVersion enter");
		return new SuccessTransport(200, DashboardApplication.getVersion(A1MediatorApi.class));
	}

	@ApiOperation(value = "Gets the named policy for AC xApp via the A1 Mediator")
	@RequestMapping(value = "policy/{" + POLICY_NAME + "}", method = RequestMethod.GET)
	public Object getPolicy(@PathVariable(POLICY_NAME) String policyName) {
		logger.debug("getPolicy: policy {}", policyName);
		a1MediatorApi.a1ControllerGetHandler(policyName);
		return null;
	}

	@ApiOperation(value = "Sets the named policy for AC xApp via the A1 Mediator")
	@RequestMapping(value = "policy/{" + POLICY_NAME + "}", method = RequestMethod.PUT)
	public void putPolicy(@PathVariable(POLICY_NAME) String policyName, //
			@ApiParam(value = "JSON formatted policy") @RequestBody JsonNode policy, //
			HttpServletResponse response) {
		logger.debug("putPolicy: policy {}", policyName);
		a1MediatorApi.a1ControllerPutHandler(policyName, policy);
		response.setStatus(a1MediatorApi.getApiClient().getStatusCode().value());
	}

}
