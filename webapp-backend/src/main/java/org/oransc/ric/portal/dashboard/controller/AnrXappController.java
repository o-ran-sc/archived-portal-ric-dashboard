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
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;

/**
 * Provides methods to contact the ANR xApp which manages a Neighbor Cell
 * Relation Table (NCRT).
 */
@Configuration
@RestController
@RequestMapping(value = DashboardConstants.ENDPOINT_PREFIX + "/xapp/anr", produces = MediaType.APPLICATION_JSON_VALUE)
public class AnrXappController {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	// Query parameters
	private static final String QP_NODEB = "ggnodeb";
	private static final String QP_SERVING = "servingCellNrcgi";
	private static final String QP_NEIGHBOR = "neighborCellNrpci";
	// Path parameters
	private static final String PP_SERVING = "servingcells";
	private static final String PP_NEIGHBOR = "neighborcells";

	// Populated by the autowired constructor
	private final HealthApi healthApi;
	private final NcrtApi ncrtApi;

	@Autowired
	public AnrXappController(final HealthApi healthApi, final NcrtApi ncrtApi) {
		Assert.notNull(healthApi, "API must not be null");
		Assert.notNull(ncrtApi, "API must not be null");
		this.healthApi = healthApi;
		this.ncrtApi = ncrtApi;
	}

	@ApiOperation(value = "Gets the ANR client library MANIFEST.MF property Implementation-Version.", response = SuccessTransport.class)
	@RequestMapping(value = DashboardConstants.VERSION_PATH, method = RequestMethod.GET)
	public SuccessTransport getAnrXappClientVersion() {
		return new SuccessTransport(200, DashboardApplication.getImplementationVersion(HealthApi.class));
	}

	@ApiOperation(value = "Performs a liveness probe on the ANR xApp, result expressed as the response code.")
	@RequestMapping(value = "/health/alive", method = RequestMethod.GET)
	public void getAnrXappHealthAlive(HttpServletResponse response) {
		healthApi.getHealthAlive();
		response.setStatus(healthApi.getApiClient().getStatusCode().value());
	}

	@ApiOperation(value = "Performs a readiness probe on the ANR xApp, result expressed as the response code.")
	@RequestMapping(value = "/health/ready", method = RequestMethod.GET)
	public void getAnrXappHealthReady(HttpServletResponse response) {
		healthApi.getHealthReady();
		response.setStatus(healthApi.getApiClient().getStatusCode().value());
	}

	@ApiOperation(value = "Returns list of gNodeB IDs based on NCRT in ANR", response = GgNodeBTable.class)
	@RequestMapping(value = "/gnodebs", method = RequestMethod.GET)
	public GgNodeBTable getGnodebs() {
		return ncrtApi.getgNodeB();
	}

	@ApiOperation(value = "Returns neighbor cell relation table for all gNodeBs or based on query parameters", response = NeighborCellRelationTable.class)
	@RequestMapping(value = "/ncrt", method = RequestMethod.GET)
	public NeighborCellRelationTable getNcrtInfo( //
			@RequestParam(name = QP_NODEB, required = false) String ggnbId, //
			@RequestParam(name = QP_SERVING, required = false) String servingCellNrcgi, //
			@RequestParam(name = QP_NEIGHBOR, required = false) String neighborCellNrpci) {
		logger.debug("getNcrtInfo: ggnbid {}, servingCellNrpci {} neighborCellNrcgi {}", ggnbId, servingCellNrcgi,
				neighborCellNrpci);
		return ncrtApi.getNcrt(ggnbId, servingCellNrcgi, neighborCellNrpci);
	}

	// /ncrt/servingcells/{servCellNrcgi}/neighborcells/{neighCellNrpci} :
	@ApiOperation(value = "Modify neighbor cell relation based on Serving Cell NRCGI and Neighbor Cell NRPCI")
	@RequestMapping(value = "/ncrt/" + PP_SERVING + "/{" + PP_SERVING + "}/" + PP_NEIGHBOR + "/{" + PP_NEIGHBOR
			+ "}", method = RequestMethod.PUT)
	public void modifyNcrt(@PathVariable(PP_SERVING) String servingCellNrcgi, //
			@PathVariable(PP_NEIGHBOR) String neighborCellNrpci, //
			@RequestBody NeighborCellRelationMod ncrMod, HttpServletResponse response) {
		logger.debug("modifyNcrt: servingCellNrcgi {}, neighborCellNrpci {}, ncrMod {}", servingCellNrcgi,
				neighborCellNrpci, ncrMod);
		ncrtApi.modifyNcrt(servingCellNrcgi, neighborCellNrpci, ncrMod);
		response.setStatus(healthApi.getApiClient().getStatusCode().value());
	}

	@ApiOperation(value = "Delete neighbor cell relation based on Serving Cell NRCGI and Neighbor Cell NRPCI")
	@RequestMapping(value = "/ncrt/" + PP_SERVING + "/{" + PP_SERVING + "}/" + PP_NEIGHBOR + "/{" + PP_NEIGHBOR
			+ "}", method = RequestMethod.DELETE)
	public void deleteNcrt(@PathVariable(PP_SERVING) String servingCellNrcgi, //
			@PathVariable(PP_NEIGHBOR) String neighborCellNrpci, //
			HttpServletResponse response) {
		logger.debug("deleteNcrt: servingCellNrcgi {}, neighborCellNrpci {}", servingCellNrcgi, neighborCellNrpci);
		ncrtApi.deleteNcrt(servingCellNrcgi, neighborCellNrpci);
		response.setStatus(healthApi.getApiClient().getStatusCode().value());
	}
	
}
