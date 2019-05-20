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
import org.oransc.ric.anrxapp.client.model.NeighborCellRelationDel;
import org.oransc.ric.anrxapp.client.model.NeighborCellRelationDel.IdTypeEnum;
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

	private static final String CELL = "cell";
	private static final String GGNBID = "ggnbId";
	private static final String START_INDEX = "startIndex";
	private static final String LIMIT = "limit";
	/** Literal values 'nrpci' or 'nrcgi' */
	private static final String ID_TYPE = "idType";
	/** Value carried here is either a NRPCI or a NRCGI */
	private static final String ID = "id";
	private static final String NRPCI = "neighborCellIdentifierNrpci";
	// New Radio Cell Global Identifier
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

	@ApiOperation(value = "Query NCRT of all cells; all or one gNB(s)", response = NeighborCellRelationTable.class)
	@RequestMapping(value = "/cell", method = RequestMethod.GET)
	public NeighborCellRelationTable getNcrtInfo( //
			@RequestParam(name = GGNBID, required = false) String ggnbId, //
			@RequestParam(name = START_INDEX, required = false) String startIndex, //
			@RequestParam(name = LIMIT, required = false) Integer limit) {
		logger.debug("getNcrtInfo: ggnbid {}, startIndex {}, limit {}", ggnbId, startIndex, limit);
		return ncrtApi.getNcrtInfo(ggnbId, startIndex, limit);
	}

	@ApiOperation(value = "Query NCRT of a single serving cell", response = NeighborCellRelationTable.class)
	@RequestMapping(value = "/" + CELL + "/{" + CELL + "}", method = RequestMethod.GET)
	public NeighborCellRelationTable getCellNcrtInfo(@PathVariable(CELL) String cellIdentifierNrcgi, //
			@RequestParam(name = START_INDEX, required = false) String startIndex, //
			@RequestParam(name = LIMIT, required = false) Integer limit,
			@RequestParam(name = NRPCI, required = false) String nrpci,
			@RequestParam(name = NRCGI, required = false) String nrcgi) {
		logger.debug("getCellNcrtInfo: cell {}, startIndex {}, limit {}, nrpci {}, nrcgi {}", cellIdentifierNrcgi,
				startIndex, limit, nrpci, nrcgi);
		return ncrtApi.getCellNcrtInfo(cellIdentifierNrcgi, startIndex, limit, nrpci, nrcgi);
	}

	@ApiOperation(value = "Modify neighbor cell relation based on Source Cell NR CGI and Target Cell NR PCI / NR CGI")
	@RequestMapping(value = "/" + CELL + "/{" + CELL + "}", method = RequestMethod.PUT)
	public void modifyNcrt(@PathVariable(CELL) String cellIdentifierNrcgi, //
			@RequestBody NeighborCellRelationModTable ncrtModTable, //
			HttpServletResponse response) {
		logger.debug("modifyNcrt: cellIdentifierNrcgi {}, modTable {}", cellIdentifierNrcgi, ncrtModTable);
		ncrtApi.modifyNcrt(cellIdentifierNrcgi, ncrtModTable);
		response.setStatus(healthApi.getApiClient().getStatusCode().value());
	}

	/*
	 * This DEL method does not accept a body but the remote requires one; note the
	 * API spec is evolving rapidly.
	 */
	@ApiOperation(value = "Delete neighbor cell relation based on Source Cell NR CGI and Target Cell NR PCI / NR CGI")
	@RequestMapping(value = "/" + CELL + "/{" + CELL + "}/" + ID_TYPE + "/{" + ID_TYPE + "}/" + ID + "/{" + ID
			+ "}", method = RequestMethod.DELETE)
	public void deleteNcrt(@PathVariable(CELL) String cellIdentifierNrcgi, //
			@PathVariable(ID_TYPE) String idType, //
			@PathVariable(ID) String id, //
			HttpServletResponse response) {
		logger.debug("modifyNcrt: cellIdentifierNrcgi {}, idType {}, id {}", cellIdentifierNrcgi, idType, id);
		// Construct the delete table.
		NeighborCellRelationDel.IdTypeEnum idTypeEnum = NeighborCellRelationDel.IdTypeEnum.fromValue(idType);
		NeighborCellRelationDel del = new NeighborCellRelationDel().idType(idTypeEnum);
		if (idTypeEnum == IdTypeEnum.NRCGI)
			del.setNeighborCellNrcgi(id);
		else
			del.setNeighborCellNrpci(id);
		NeighborCellRelationDelTable tbl = new NeighborCellRelationDelTable();
		tbl.add(del);
		ncrtApi.deleteNcrt(cellIdentifierNrcgi, tbl);
		response.setStatus(healthApi.getApiClient().getStatusCode().value());
	}

}
