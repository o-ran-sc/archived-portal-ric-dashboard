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
import org.oransc.ric.anrxapp.client.model.NeighborCellRelationDelTable;
import org.oransc.ric.anrxapp.client.model.NeighborCellRelationModTable;
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

	private static final String CELL_ID = "cellIdentifier";
	private static final String GGNBID = "ggnbId";
	private static final String START_INDEX = "startIndex";
	private static final String LIMIT = "limit";
	private static final String NRPCI = "neighborCellIdentifierNrpci";
	private static final String NRCGI = "neighborCellIdentifierNrcgi";

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
	public SuccessTransport getVersion() {
		logger.debug("getVersion enter");
		return new SuccessTransport(200, DashboardApplication.getImplementationVersion(HealthApi.class));
	}

	@ApiOperation(value = "Performs a liveness probe on the ANR xApp, result expressed as the response code.")
	@RequestMapping(value = "/health/alive", method = RequestMethod.GET)
	public void getHealthAlive(HttpServletResponse response) {
		logger.debug("getHealthAlive");
		healthApi.getHealthAlive();
		response.setStatus(healthApi.getApiClient().getStatusCode().value());
	}

	@ApiOperation(value = "Performs a readiness probe on the ANR xApp, result expressed as the response code.")
	@RequestMapping(value = "/health/ready", method = RequestMethod.GET)
	public void getHealthReady(HttpServletResponse response) {
		logger.debug("getHealthReady");
		healthApi.getHealthReady();
		response.setStatus(healthApi.getApiClient().getStatusCode().value());
	}

	@ApiOperation(value = "Query NCRT of all cells, all or one gNB(s)", response = NeighborCellRelationTable.class)
	@RequestMapping(value = "/cell", method = RequestMethod.GET)
	public NeighborCellRelationTable getNcrtInfo( //
			@RequestParam(name = GGNBID, required = false) String ggnbId, //
			@RequestParam(name = START_INDEX, required = false) String startIndex, //
			@RequestParam(name = LIMIT, required = false) Integer limit) {
		logger.debug("queryNcrtAllCells: ggnbid {}, startIndex {} limit {}", ggnbId, startIndex, limit);
		return ncrtApi.getNcrtInfo(ggnbId, startIndex, limit);
	}

	@ApiOperation(value = "Query NCRT of a single serving cell", response = NeighborCellRelationTable.class)
	@RequestMapping(value = "/cell/" + CELL_ID + "/{" + CELL_ID + "}", method = RequestMethod.GET)
	public NeighborCellRelationTable getCellNcrtInfo(@PathVariable(CELL_ID) String cellIdentifier, //
			@RequestParam(name = START_INDEX, required = false) String startIndex, //
			@RequestParam(name = LIMIT, required = false) Integer limit,
			@RequestParam(name = NRPCI, required = false) String nrpci,
			@RequestParam(name = NRCGI, required = false) String nrcgi) {
		logger.debug("queryNcrtAllCells: cellIdentifier {}, startIndex {} limit {} nrpci {} nrcgi {}", cellIdentifier,
				startIndex, limit, nrpci, nrcgi);
		return ncrtApi.getCellNcrtInfo(cellIdentifier, startIndex, limit, nrpci, nrcgi);
	}

	@ApiOperation(value = "Modify neighbor cell relation based on Source Cell NR CGI and Target Cell NR PCI / NR CGI")
	@RequestMapping(value = "/cell/" + CELL_ID + "/{" + CELL_ID + "}", method = RequestMethod.PUT)
	public void modifyNcrt(@PathVariable(CELL_ID) String cellIdentifier, //
			@RequestBody NeighborCellRelationModTable ncrtModTable, //
			HttpServletResponse response) {
		logger.debug("modifyNcrt: cellIdentifier {} modTable {}", cellIdentifier, ncrtModTable);
		ncrtApi.modifyNcrt(cellIdentifier, ncrtModTable);
		response.setStatus(healthApi.getApiClient().getStatusCode().value());
	}

	/*
	 * TODO: DELETE should not have a body - the path should identify the resource to be deleted.
	 */
	@ApiOperation(value = "Delete neighbor cell relation based on Source Cell NR CGI and Target Cell NR PCI / NR CGI")
	@RequestMapping(value = "/cell/" + CELL_ID + "/{" + CELL_ID + "}", method = RequestMethod.DELETE)
	public void deleteNcrt(@PathVariable(CELL_ID) String cellIdentifier, //
			@RequestBody NeighborCellRelationDelTable ncrtDelTable, //
			HttpServletResponse response) {
		logger.debug("modifyNcrt: cellIdentifier {} delTable {}", cellIdentifier, ncrtDelTable);
		ncrtApi.deleteNcrt(cellIdentifier, ncrtDelTable);
		response.setStatus(healthApi.getApiClient().getStatusCode().value());
	}

}
