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
import java.util.List;

import org.oransc.ric.a1med.client.api.A1MediatorApi;
import org.oransc.ric.a1med.client.model.PolicyTypeSchema;
import org.oransc.ric.portal.dashboard.DashboardApplication;
import org.oransc.ric.portal.dashboard.DashboardConstants;
import org.oransc.ric.portal.dashboard.config.A1MediatorApiBuilder;
import org.oransc.ric.portal.dashboard.model.SuccessTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

	// Publish paths in constants so tests are easy to write
	public static final String CONTROLLER_PATH = DashboardConstants.ENDPOINT_PREFIX + "/a1-p";
	public static final String PP_TYPE_ID = "poltype";
	public static final String PP_INST_ID = "polinst";
	// The get- and put-instance methods use the same path
	private static final String POLICY_INSTANCE_METHOD_PATH = /* controller path + */ DashboardConstants.RIC_INSTANCE_KEY
			+ "/{" + DashboardConstants.RIC_INSTANCE_KEY + "}/" + PP_TYPE_ID + "/{" + PP_TYPE_ID + "}/" + PP_INST_ID
			+ "/{" + PP_INST_ID + "}";

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

	@ApiOperation(value = "Gets the registered policy type IDs from the A1 Mediator", response = Integer.class, responseContainer = "List")
	@GetMapping(DashboardConstants.RIC_INSTANCE_KEY + "/{" + DashboardConstants.RIC_INSTANCE_KEY + "}/" + PP_TYPE_ID)
	@Secured({ DashboardConstants.ROLE_ADMIN, DashboardConstants.ROLE_STANDARD })
	public List<Integer> getAllPolicyTypes(@PathVariable(DashboardConstants.RIC_INSTANCE_KEY) String instanceKey) {
		logger.debug("getAllPolicyTypes: instance {}", instanceKey);
		Object result = a1MediatorClientBuilder.getA1MediatorApi(instanceKey).a1ControllerGetAllPolicyTypes();
		@SuppressWarnings("unchecked")
		List<Integer> result2 = (List<Integer>) result;
		return result2;
	}

	@ApiOperation(value = "Gets the specified policy type from the A1 Mediator", response = PolicyTypeSchema.class)
	@GetMapping(DashboardConstants.RIC_INSTANCE_KEY + "/{" + DashboardConstants.RIC_INSTANCE_KEY + "}/" + PP_TYPE_ID
			+ "/{" + PP_TYPE_ID + "}")
	@Secured({ DashboardConstants.ROLE_ADMIN, DashboardConstants.ROLE_STANDARD })
	public PolicyTypeSchema getPolicyType(@PathVariable(DashboardConstants.RIC_INSTANCE_KEY) String instanceKey,
			@PathVariable(PP_TYPE_ID) Integer policyTypeId) {
		logger.debug("getPolicyType: instance {} typeId {}", instanceKey, policyTypeId);
		return a1MediatorClientBuilder.getA1MediatorApi(instanceKey).a1ControllerGetPolicyType(policyTypeId);
	}

	@ApiOperation(value = "Gets the specified policy instance from the A1 Mediator")
	@GetMapping(POLICY_INSTANCE_METHOD_PATH)
	@Secured({ DashboardConstants.ROLE_ADMIN, DashboardConstants.ROLE_STANDARD })
	public Object getPolicyInstance(@PathVariable(DashboardConstants.RIC_INSTANCE_KEY) String instanceKey,
			@PathVariable(PP_TYPE_ID) Integer policyTypeId, //
			@PathVariable(PP_INST_ID) String policyInstanceId) {
		logger.debug("getPolicyInstance: instance {} typeId {} instanceId {}", instanceKey, policyTypeId,
				policyInstanceId);
		return a1MediatorClientBuilder.getA1MediatorApi(instanceKey).a1ControllerGetPolicyInstance(policyTypeId,
				policyInstanceId);
	}

	@ApiOperation(value = "Creates or replaces the specified policy instance at the A1 Mediator")
	@PutMapping(POLICY_INSTANCE_METHOD_PATH)
	@Secured({ DashboardConstants.ROLE_ADMIN })
	public ResponseEntity<String> createPolicyInstance(
			@PathVariable(DashboardConstants.RIC_INSTANCE_KEY) String instanceKey,
			@PathVariable(PP_TYPE_ID) Integer policyTypeId, //
			@PathVariable(PP_INST_ID) String policyInstanceId,
			@ApiParam(value = "Policy body") @RequestBody String policyBody) {
		logger.debug("createPolicyInstance: instance {} typeId {} instanceId {}", instanceKey, policyTypeId,
				policyInstanceId);
		A1MediatorApi api = a1MediatorClientBuilder.getA1MediatorApi(instanceKey);
		api.a1ControllerCreateOrReplacePolicyInstance(policyTypeId, policyInstanceId, policyBody);
		return ResponseEntity.status(api.getApiClient().getStatusCode().value()).body(null);
	}

}
