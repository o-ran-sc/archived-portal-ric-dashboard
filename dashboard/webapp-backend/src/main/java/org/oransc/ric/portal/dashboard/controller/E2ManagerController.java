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
import java.util.ArrayList;
import java.util.List;

import org.oransc.ric.portal.dashboard.DashboardApplication;
import org.oransc.ric.portal.dashboard.DashboardConstants;
import org.oransc.ric.portal.dashboard.config.E2ManagerApiBuilder;
import org.oransc.ric.portal.dashboard.model.RanDetailsTransport;
import org.oransc.ric.portal.dashboard.model.SuccessTransport;
import org.oransc.ricplt.e2mgr.client.api.HealthCheckApi;
import org.oransc.ricplt.e2mgr.client.api.NodebApi;
import org.oransc.ricplt.e2mgr.client.model.GetNodebResponse;
import org.oransc.ricplt.e2mgr.client.model.NodebIdentity;
import org.oransc.ricplt.e2mgr.client.model.ResetRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
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
import org.springframework.web.client.HttpStatusCodeException;

import io.swagger.annotations.ApiOperation;

/**
 * Proxies calls from the front end to the E2 Manager API.
 * 
 * If a method throws RestClientResponseException, it is handled by
 * {@link CustomResponseEntityExceptionHandler#handleProxyMethodException(Exception, org.springframework.web.context.request.WebRequest)}
 * which returns status 502. All other exceptions are handled by Spring which
 * returns status 500.
 */
@Configuration
@RestController
@RequestMapping(value = E2ManagerController.CONTROLLER_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class E2ManagerController {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	// Publish paths in constants for tests
	public static final String CONTROLLER_PATH = DashboardConstants.ENDPOINT_PREFIX + "/e2mgr";
	// Dashboard only
	public static final String HEALTH_METHOD = "health";
	// Keep these consistent with the E2M implementation
	/* package */ static final String NODEB_PREFIX = "nodeb";
	public static final String RAN_METHOD = NODEB_PREFIX + "/ran";
	public static final String NODEB_SHUTDOWN_METHOD = NODEB_PREFIX + "/shutdown";
	public static final String NODEB_LIST_METHOD = NODEB_PREFIX + "/ids";
	// Reset uses prefix, adds a path parameter below
	public static final String RESET_METHOD = "reset";
	// Path parameters
	private static final String PP_RANNAME = "ranName";

	// Populated by the autowired constructor
	private final E2ManagerApiBuilder e2ManagerApiBuilder;

	@Autowired
	public E2ManagerController(final E2ManagerApiBuilder e2ManagerApiBuilder) {
		Assert.notNull(e2ManagerApiBuilder, "builder must not be null");
		this.e2ManagerApiBuilder = e2ManagerApiBuilder;
		if (logger.isDebugEnabled())
			logger.debug("ctor: configured with builder type {}", e2ManagerApiBuilder.getClass().getName());
	}

	@ApiOperation(value = "Gets the E2 manager client library MANIFEST.MF property Implementation-Version.", response = SuccessTransport.class)
	@GetMapping(DashboardConstants.VERSION_METHOD)
	// No role required
	public SuccessTransport getClientVersion() {
		return new SuccessTransport(200, DashboardApplication.getImplementationVersion(HealthCheckApi.class));
	}

	@ApiOperation(value = "Gets the health from the E2 manager, expressed as the response code.")
	@GetMapping(DashboardConstants.RIC_INSTANCE_KEY + "/{" + DashboardConstants.RIC_INSTANCE_KEY + "}/" + HEALTH_METHOD)
	// No role required
	public ResponseEntity<String> healthGet(@PathVariable(DashboardConstants.RIC_INSTANCE_KEY) String instanceKey) {
		logger.debug("healthGet instance {}", instanceKey);
		HealthCheckApi api = e2ManagerApiBuilder.getHealthCheckApi(instanceKey);
		api.healthGet();
		return ResponseEntity.status(api.getApiClient().getStatusCode().value()).body(null);
	}

	// This calls other methods to simplify the task of the front-end.
	@ApiOperation(value = "Gets all RAN identities and statuses from the E2 manager.", response = RanDetailsTransport.class, responseContainer = "List")
	@GetMapping(DashboardConstants.RIC_INSTANCE_KEY + "/{" + DashboardConstants.RIC_INSTANCE_KEY + "}/" + RAN_METHOD)
	@Secured({ DashboardConstants.ROLE_ADMIN, DashboardConstants.ROLE_STANDARD })
	public List<RanDetailsTransport> getRanDetails(
			@PathVariable(DashboardConstants.RIC_INSTANCE_KEY) String instanceKey) {
		logger.debug("getRanDetails instance {}", instanceKey);
		NodebApi api = e2ManagerApiBuilder.getNodebApi(instanceKey);
		List<NodebIdentity> nodebIdList = api.getNodebIdList();
		logger.debug("getRanDetails: nodebIdList {}", nodebIdList);
		List<RanDetailsTransport> details = new ArrayList<>();
		for (NodebIdentity nbid : nodebIdList) {
			GetNodebResponse nbResp = null;
			try {
				// Catch exceptions to keep looping despite failures
				nbResp = api.getNb(nbid.getInventoryName());
			} catch (HttpStatusCodeException ex) {
				logger.warn("E2 getNb failed for name {}: {}", nbid.getInventoryName(), ex.toString());
				nbResp = new GetNodebResponse().connectionStatus("UNKNOWN").ip("UNKNOWN").port(-1)
						.ranName(nbid.getInventoryName());
			}
			details.add(new RanDetailsTransport(nbid, nbResp));
		}
		return details;
	}

	@ApiOperation(value = "Get RAN identities list.", response = NodebIdentity.class, responseContainer = "List")
	@GetMapping(DashboardConstants.RIC_INSTANCE_KEY + "/{" + DashboardConstants.RIC_INSTANCE_KEY + "}/"
			+ NODEB_LIST_METHOD)
	@Secured({ DashboardConstants.ROLE_ADMIN, DashboardConstants.ROLE_STANDARD })
	public List<NodebIdentity> getNodebIdList(@PathVariable(DashboardConstants.RIC_INSTANCE_KEY) String instanceKey) {
		logger.debug("getNodebIdList instance {}", instanceKey);
		return e2ManagerApiBuilder.getNodebApi(instanceKey).getNodebIdList();
	}

	@ApiOperation(value = "Get RAN by name.", response = GetNodebResponse.class)
	@GetMapping(DashboardConstants.RIC_INSTANCE_KEY + "/{" + DashboardConstants.RIC_INSTANCE_KEY + "}/" + NODEB_PREFIX
			+ "/{" + PP_RANNAME + "}")
	@Secured({ DashboardConstants.ROLE_ADMIN, DashboardConstants.ROLE_STANDARD })
	public GetNodebResponse getNb(@PathVariable(DashboardConstants.RIC_INSTANCE_KEY) String instanceKey,
			@PathVariable(PP_RANNAME) String ranName) {
		logger.debug("getNb instance {} name {}", instanceKey, ranName);
		return e2ManagerApiBuilder.getNodebApi(instanceKey).getNb(ranName);
	}

	@ApiOperation(value = "Close all connections to the RANs and delete the data from the nodeb-rnib DB.")
	@PutMapping(DashboardConstants.RIC_INSTANCE_KEY + "/{" + DashboardConstants.RIC_INSTANCE_KEY + "}/"
			+ NODEB_SHUTDOWN_METHOD)
	@Secured({ DashboardConstants.ROLE_ADMIN })
	public ResponseEntity<String> nodebShutdownPut(
			@PathVariable(DashboardConstants.RIC_INSTANCE_KEY) String instanceKey) {
		logger.debug("nodebShutdownPut instance {}", instanceKey);
		NodebApi api = e2ManagerApiBuilder.getNodebApi(instanceKey);
		api.nodebShutdownPut();
		return ResponseEntity.status(api.getApiClient().getStatusCode().value()).body(null);
	}

	@ApiOperation(value = "Abort any other ongoing procedures over X2 between the RIC and the RAN.")
	@PutMapping(DashboardConstants.RIC_INSTANCE_KEY + "/{" + DashboardConstants.RIC_INSTANCE_KEY + "}/" + NODEB_PREFIX
			+ "/{" + PP_RANNAME + "}/" + RESET_METHOD)
	@Secured({ DashboardConstants.ROLE_ADMIN })
	public ResponseEntity<String> reset(@PathVariable(DashboardConstants.RIC_INSTANCE_KEY) String instanceKey,
			@PathVariable(PP_RANNAME) String ranName, @RequestBody ResetRequest resetRequest) {
		logger.debug("reset instance {} name {}", instanceKey, ranName);
		NodebApi api = e2ManagerApiBuilder.getNodebApi(instanceKey);
		api.reset(ranName, resetRequest);
		return ResponseEntity.status(api.getApiClient().getStatusCode().value()).body(null);
	}

}
