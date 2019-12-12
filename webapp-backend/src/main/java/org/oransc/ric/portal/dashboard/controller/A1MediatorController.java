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

import javax.servlet.http.HttpServletResponse;

import org.oransc.ric.a1med.client.api.A1MediatorApi;
import org.oransc.ric.portal.dashboard.DashboardApplication;
import org.oransc.ric.portal.dashboard.DashboardConstants;
import org.oransc.ric.portal.dashboard.config.A1MediatorApiBuilder;
import org.oransc.ric.portal.dashboard.model.SuccessTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * Proxies calls from the front end to the A1 Mediator API to get and put
 * policies. All methods are deliberately kept ignorant of the data format.
 * 
 * If a method throws RestClientResponseException, it is handled by
 * {@link CustomResponseEntityExceptionHandler#handleProxyMethodException(Exception, org.springframework.web.context.request.WebRequest)}
 * which returns status 502. All other exceptions are handled by Spring which
 * returns status 500.
 */
@RestController
@RequestMapping(value = A1MediatorController.CONTROLLER_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class A1MediatorController {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	/** This path lacks the RIC instance pattern */
	public static final String CONTROLLER_PATH = DashboardConstants.ENDPOINT_PREFIX + "/a1-p";
	// Path parameters
	public static final String PP_POLICIES = "policies";
	// The get and put methods use the same path
	private static final String POLICY_METHOD_PATH = /* controller path + */ DashboardConstants.RIC_INSTANCE_KEY + "/{"
			+ DashboardConstants.RIC_INSTANCE_KEY + "}/" + PP_POLICIES + "/{" + PP_POLICIES + "}";

	// Populated by the autowired constructor
	private final A1MediatorApiBuilder a1MediatorClientBuilder;

	@Autowired
	public A1MediatorController(final A1MediatorApiBuilder a1MediatorApiBuilder) {
		Assert.notNull(a1MediatorApiBuilder, "builder must not be null");
		this.a1MediatorClientBuilder = a1MediatorApiBuilder;
		if (logger.isDebugEnabled())
			logger.debug("ctor: configured with builder type {}", a1MediatorApiBuilder.getClass().getName());
	}

	@ApiOperation(value = "Gets the A1 client library MANIFEST.MF property Implementation-Version.", response = SuccessTransport.class)
	@GetMapping(DashboardConstants.VERSION_METHOD)
	// No role required
	public SuccessTransport getA1MediatorClientVersion() {
		return new SuccessTransport(200, DashboardApplication.getImplementationVersion(A1MediatorApi.class));
	}

	@ApiOperation(value = "Gets the specified policy from the A1 Mediator")
	@GetMapping(POLICY_METHOD_PATH)
	@Secured({ DashboardConstants.ROLE_ADMIN, DashboardConstants.ROLE_STANDARD })
	public Object getPolicy(@PathVariable(DashboardConstants.RIC_INSTANCE_KEY) String instanceKey,
			@PathVariable(PP_POLICIES) String policyName) {
		logger.debug("getPolicy instance {} policy {}", instanceKey, policyName);
		return a1MediatorClientBuilder.getA1MediatorApi(instanceKey).a1ControllerGetHandler(policyName);
	}

	@ApiOperation(value = "Puts the specified policy to the A1 Mediator")
	@PutMapping(POLICY_METHOD_PATH)
	@Secured({ DashboardConstants.ROLE_ADMIN })
	public void putPolicy(@PathVariable(DashboardConstants.RIC_INSTANCE_KEY) String instanceKey,
			@PathVariable(PP_POLICIES) String policyName, @ApiParam(value = "Policy body") @RequestBody String policy, //
			HttpServletResponse response) {
		logger.debug("putPolicy instance {} name {} value {}", instanceKey, policyName, policy);
		A1MediatorApi api = a1MediatorClientBuilder.getA1MediatorApi(instanceKey);
		api.a1ControllerPutHandler(policyName, policy);
		response.setStatus(api.getApiClient().getStatusCode().value());
	}

}
