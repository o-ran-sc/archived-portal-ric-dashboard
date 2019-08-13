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
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.oransc.ric.e2mgr.client.api.HealthCheckApi;
import org.oransc.ric.e2mgr.client.api.NodebApi;
import org.oransc.ric.e2mgr.client.model.GetNodebResponse;
import org.oransc.ric.e2mgr.client.model.NodebIdentity;
import org.oransc.ric.e2mgr.client.model.ResetRequest;
import org.oransc.ric.e2mgr.client.model.SetupRequest;
import org.oransc.ric.portal.dashboard.DashboardApplication;
import org.oransc.ric.portal.dashboard.DashboardConstants;
import org.oransc.ric.portal.dashboard.model.RanDetailsTransport;
import org.oransc.ric.portal.dashboard.model.SuccessTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

	// Publish paths in constants so tests are easy to write
	public static final String CONTROLLER_PATH = DashboardConstants.ENDPOINT_PREFIX + "/e2mgr";
	// Endpoints
	public static final String HEALTH_METHOD = "health";
	public static final String NODEB_SHUTDOWN_METHOD = "/nodebShutdownPut";
	public static final String NODEB_LIST_METHOD = "/nodeb-ids";
	public static final String RAN_METHOD = "/ran";
	public static final String RESET_METHOD = "/reset";
	public static final String ENDC_SETUP_METHOD = "/endcSetup";
	public static final String X2_SETUP_METHOD = "/x2Setup";
	public static final String VERSION_METHOD = DashboardConstants.VERSION_METHOD;
	// Path parameters
	private static final String PP_RANNAME = "ranName";

	// Populated by the autowired constructor
	private final HealthCheckApi e2HealthCheckApi;
	private final NodebApi e2NodebApi;

	@Autowired
	public E2ManagerController(final HealthCheckApi e2HealthCheckApi, final NodebApi e2NodebApi) {
		Assert.notNull(e2HealthCheckApi, "API must not be null");
		Assert.notNull(e2NodebApi, "API must not be null");
		this.e2HealthCheckApi = e2HealthCheckApi;
		this.e2NodebApi = e2NodebApi;
	}

	@ApiOperation(value = "Gets the E2 manager client library MANIFEST.MF property Implementation-Version.", response = SuccessTransport.class)
	@GetMapping(VERSION_METHOD)
	// No role required
	public SuccessTransport getClientVersion() {
		return new SuccessTransport(200, DashboardApplication.getImplementationVersion(HealthCheckApi.class));
	}

	@ApiOperation(value = "Gets the health from the E2 manager, expressed as the response code.")
	@GetMapping(HEALTH_METHOD)
	// No role required
	public void healthGet(HttpServletResponse response) {
		logger.debug("healthGet");
		e2HealthCheckApi.healthGet();
		response.setStatus(e2HealthCheckApi.getApiClient().getStatusCode().value());
	}

	// This calls other methods to simplify the task of the front-end.
	@ApiOperation(value = "Gets all RAN identities and statuses from the E2 manager.", response = RanDetailsTransport.class, responseContainer = "List")
	@GetMapping(RAN_METHOD)
	@Secured({ DashboardConstants.ROLE_ADMIN, DashboardConstants.ROLE_STANDARD })
	public List<RanDetailsTransport> getRanDetails() {
		logger.debug("getRanDetails");
		List<NodebIdentity> nodebIdList = e2NodebApi.getNodebIdList();
		List<RanDetailsTransport> details = new ArrayList<>();
		for (NodebIdentity nbid : nodebIdList) {
			GetNodebResponse nbResp = null;
			try {
				// Catch exceptions to keep looping despite failures
				nbResp = e2NodebApi.getNb(nbid.getInventoryName());
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
	@GetMapping(NODEB_LIST_METHOD)
	@Secured({ DashboardConstants.ROLE_ADMIN, DashboardConstants.ROLE_STANDARD })
	public List<NodebIdentity> getNodebIdList() {
		logger.debug("getNodebIdList");
		return e2NodebApi.getNodebIdList();
	}

	@ApiOperation(value = "Get RAN by name.", response = GetNodebResponse.class)
	@GetMapping(NODEB_SHUTDOWN_METHOD + "/{" + PP_RANNAME + "}")
	@Secured({ DashboardConstants.ROLE_ADMIN, DashboardConstants.ROLE_STANDARD })
	public GetNodebResponse getNb(@PathVariable(PP_RANNAME) String ranName) {
		logger.debug("getNb {}", ranName);
		return e2NodebApi.getNb(ranName);
	}

	@ApiOperation(value = "Sets up an EN-DC RAN connection via the E2 manager.")
	@PostMapping(ENDC_SETUP_METHOD)
	@Secured({ DashboardConstants.ROLE_ADMIN })
	public void endcSetup(@RequestBody SetupRequest setupRequest, HttpServletResponse response) {
		logger.debug("endcSetup {}", setupRequest);
		e2NodebApi.endcSetup(setupRequest);
		response.setStatus(e2NodebApi.getApiClient().getStatusCode().value());
	}

	@ApiOperation(value = "Sets up an X2 RAN connection via the E2 manager.")
	@PostMapping(X2_SETUP_METHOD)
	@Secured({ DashboardConstants.ROLE_ADMIN })
	public void x2Setup(@RequestBody SetupRequest setupRequest, HttpServletResponse response) {
		logger.debug("x2Setup {}", setupRequest);
		e2NodebApi.x2Setup(setupRequest);
		response.setStatus(e2NodebApi.getApiClient().getStatusCode().value());
	}

	@ApiOperation(value = "Close all connections to the RANs and delete the data from the nodeb-rnib DB.")
	@PutMapping(NODEB_SHUTDOWN_METHOD)
	@Secured({ DashboardConstants.ROLE_ADMIN })
	public void nodebShutdownPut(HttpServletResponse response) {
		logger.debug("nodebShutdownPut");
		e2NodebApi.nodebShutdownPut();
		response.setStatus(e2NodebApi.getApiClient().getStatusCode().value());
	}

	@ApiOperation(value = "Abort any other ongoing procedures over X2 between the RIC and the RAN.")
	@PutMapping(RESET_METHOD + "/{" + PP_RANNAME + "}")
	@Secured({ DashboardConstants.ROLE_ADMIN })
	public void reset(@PathVariable(PP_RANNAME) String ranName, @RequestBody ResetRequest resetRequest,
			HttpServletResponse response) {
		logger.debug("reset");
		e2NodebApi.reset(ranName, resetRequest);
		response.setStatus(e2NodebApi.getApiClient().getStatusCode().value());
	}

}
