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

import org.oransc.ric.anrxapp.client.api.HealthApi;
import org.oransc.ric.anrxapp.client.api.NcrtApi;
import org.oransc.ric.anrxapp.client.model.GgNodeBTable;
import org.oransc.ric.anrxapp.client.model.NeighborCellRelationMod;
import org.oransc.ric.anrxapp.client.model.NeighborCellRelationTable;
import org.oransc.ric.portal.dashboard.DashboardApplication;
import org.oransc.ric.portal.dashboard.DashboardConstants;
import org.oransc.ric.portal.dashboard.model.SuccessTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;

/**
 * Proxies calls from the front end to the ANR xApp, which manages a Neighbor
 * Cell Relation Table (NCRT).
 * 
 * If a method throws RestClientResponseException, it is handled by
 * {@link CustomResponseEntityExceptionHandler#handleProxyMethodException(Exception, org.springframework.web.context.request.WebRequest)}
 * which returns status 502. All other exceptions are handled by Spring which
 * returns status 500.
 */
@Configuration
@RestController
@RequestMapping(value = AnrXappController.CONTROLLER_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class AnrXappController {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	// Publish paths in constants so tests are easy to write
	public static final String CONTROLLER_PATH = DashboardConstants.ENDPOINT_PREFIX + "/xapp/anr";
	// Endpoints
	public static final String HEALTH_ALIVE_METHOD = "/health/alive";
	public static final String HEALTH_READY_METHOD = "/health/ready";
	public static final String GNODEBS_METHOD = "/gnodebs";
	public static final String NCRT_METHOD = "/ncrt";
	public static final String VERSION_METHOD = DashboardConstants.VERSION_METHOD;

	// Path parameters
	public static final String PP_SERVING = "servingcells";
	public static final String PP_NEIGHBOR = "neighborcells";

	// Populated by the autowired constructor
	private final HealthApi healthApi;
	private final NcrtApi ncrtApi;

	@Autowired
	public AnrXappController(final HealthApi anrHealthApi, final NcrtApi anrNcrtApi) {
		Assert.notNull(anrHealthApi, "API must not be null");
		Assert.notNull(anrNcrtApi, "API must not be null");
		this.healthApi = anrHealthApi;
		this.ncrtApi = anrNcrtApi;
		if (logger.isDebugEnabled())
			logger.debug("ctor: configured with client types {} and {}", anrHealthApi.getClass().getName(),
					anrNcrtApi.getClass().getName());
	}

	@ApiOperation(value = "Gets the ANR client library MANIFEST.MF property Implementation-Version.", response = SuccessTransport.class)
	@GetMapping(VERSION_METHOD)
	// No role required
	public SuccessTransport getClientVersion() {
		return new SuccessTransport(200, DashboardApplication.getImplementationVersion(HealthApi.class));
	}

	@ApiOperation(value = "Performs a liveness probe on the ANR xApp, result expressed as the response code.")
	@GetMapping(HEALTH_ALIVE_METHOD)
	// No role required
	public void getHealthAlive(HttpServletResponse response) {
		logger.debug("getHealthAlive");
		healthApi.getHealthAlive();
		response.setStatus(healthApi.getApiClient().getStatusCode().value());
	}

	@ApiOperation(value = "Performs a readiness probe on the ANR xApp, result expressed as the response code.")
	@GetMapping(HEALTH_READY_METHOD)
	// No role required
	public void getHealthReady(HttpServletResponse response) {
		logger.debug("getHealthReady");
		healthApi.getHealthReady();
		response.setStatus(healthApi.getApiClient().getStatusCode().value());
	}

	@ApiOperation(value = "Returns list of gNodeB IDs based on NCRT in ANR", response = GgNodeBTable.class)
	@GetMapping(GNODEBS_METHOD)
	@Secured({ DashboardConstants.ROLE_ADMIN, DashboardConstants.ROLE_STANDARD })
	public GgNodeBTable getGnodebs() {
		logger.debug("getGnodebs");
		return ncrtApi.getgNodeB();
	}

	@ApiOperation(value = "Returns neighbor cell relation table for all gNodeBs or based on query parameters", response = NeighborCellRelationTable.class)
	@GetMapping(NCRT_METHOD)
	@Secured({ DashboardConstants.ROLE_ADMIN, DashboardConstants.ROLE_STANDARD })
	public NeighborCellRelationTable getNcrt( //
			@RequestParam(required = false) String ggnodeb, //
			@RequestParam(required = false) String servingCellNrcgi, //
			@RequestParam(required = false) String neighborCellNrpci) {
		logger.debug("getNcrt: ggnbid {}, servingCellNrpci {}, neighborCellNrcgi {}", ggnodeb, servingCellNrcgi,
				neighborCellNrpci);
		return ncrtApi.getNcrt(ggnodeb, servingCellNrcgi, neighborCellNrpci);
	}

	// /ncrt/servingcells/{servCellNrcgi}/neighborcells/{neighCellNrpci} :
	@ApiOperation(value = "Modify neighbor cell relation based on Serving Cell NRCGI and Neighbor Cell NRPCI")
	@PutMapping(NCRT_METHOD + "/" + PP_SERVING + "/{" + PP_SERVING + "}/" + PP_NEIGHBOR + "/{" + PP_NEIGHBOR + "}")
	@Secured({ DashboardConstants.ROLE_ADMIN })
	public void modifyNcrt(@PathVariable(PP_SERVING) String servingCellNrcgi, //
			@PathVariable(PP_NEIGHBOR) String neighborCellNrpci, //
			@RequestBody NeighborCellRelationMod ncrMod, HttpServletResponse response) {
		logger.debug("modifyNcrt: servingCellNrcgi {}, neighborCellNrpci {}, ncrMod {}", servingCellNrcgi,
				neighborCellNrpci, ncrMod);
		ncrtApi.modifyNcrt(servingCellNrcgi, neighborCellNrpci, ncrMod);
		response.setStatus(ncrtApi.getApiClient().getStatusCode().value());
	}

	@ApiOperation(value = "Delete neighbor cell relation based on Serving Cell NRCGI and Neighbor Cell NRPCI")
	@DeleteMapping(NCRT_METHOD + "/" + PP_SERVING + "/{" + PP_SERVING + "}/" + PP_NEIGHBOR + "/{" + PP_NEIGHBOR + "}")
	@Secured({ DashboardConstants.ROLE_ADMIN })
	public void deleteNcrt(@PathVariable(PP_SERVING) String servingCellNrcgi, //
			@PathVariable(PP_NEIGHBOR) String neighborCellNrpci, //
			HttpServletResponse response) {
		logger.debug("deleteNcrt: servingCellNrcgi {}, neighborCellNrpci {}", servingCellNrcgi, neighborCellNrpci);
		ncrtApi.deleteNcrt(servingCellNrcgi, neighborCellNrpci);
		response.setStatus(ncrtApi.getApiClient().getStatusCode().value());
	}

}
