/*-
 * ========================LICENSE_START=================================
 * ORAN-OSC
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
package org.oranosc.ric.portal.dash.controller;

import java.lang.invoke.MethodHandles;

import javax.servlet.http.HttpServletResponse;

import org.oranosc.ric.portal.dash.DashboardConstants;
import org.oranosc.ric.portal.dash.model.ErrorTransport;
import org.oranosc.ric.xappmgr.client.api.DefaultApi;
import org.oranosc.ric.xappmgr.client.model.AllXapps;
import org.oranosc.ric.xappmgr.client.model.XAppInfo;
import org.oranosc.ric.xappmgr.client.model.Xapp;
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

import io.swagger.annotations.ApiOperation;

/**
 * Mimics the xApp Manager API. These controller methods just proxy calls from
 * the front-end thru to the real back-end.
 *
 */
@Configuration
@RestController
@RequestMapping(value = DashboardConstants.ENDPOINT_PREFIX + "/xappmgr", produces = MediaType.APPLICATION_JSON_VALUE)
public class XappManagerController {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	// Populated by the autowired constructor
	private final DefaultApi xappMgrClient;

	@Autowired
	public XappManagerController(final DefaultApi xappMgrClient) {
		Assert.notNull(xappMgrClient, "client must not be null");
		this.xappMgrClient = xappMgrClient;
	}

	@ApiOperation(value = "Calls the xApp Manager health check.")
	@RequestMapping(value = "/health", method = RequestMethod.GET)
	public void getHealth(HttpServletResponse response) {
		logger.debug("getHealth");
		xappMgrClient.getHealth();
		response.setStatus(xappMgrClient.getApiClient().getStatusCode().value());
	}

	@ApiOperation(value = "Calls the xApp Manager to get the list of xApps.", response = AllXapps.class)
	@RequestMapping(value = "/xapps", method = RequestMethod.GET)
	public AllXapps getAllXapps() {
		logger.debug("getAllXapps via " + xappMgrClient.getApiClient().getBasePath());
		return xappMgrClient.getAllXapps();
	}

	@ApiOperation(value = "Calls the xApp Manager to get the named xApp.", response = Xapp.class)
	@RequestMapping(value = "/xapps/{xAppName}", method = RequestMethod.GET)
	public Xapp getXapp(@PathVariable("xAppName") String xAppName) {
		logger.debug("getXapp {}", xAppName);
		return xappMgrClient.getXappByName(xAppName);
	}

	@ApiOperation(value = "Calls the xApp Manager to deploy the specified Xapp.", response = Xapp.class)
	@RequestMapping(value = "/xapps", method = RequestMethod.POST)
	public Object deployXapp(@RequestBody XAppInfo xAppInfo, HttpServletResponse response) {
		logger.debug("deployXapp {}", xAppInfo);
		try {
			return xappMgrClient.deployXapp(xAppInfo);
		} catch (Exception ex) {
			logger.error("deployXapp failed", ex);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(500, "deployXapp failed", ex);
		}
	}

	@ApiOperation(value = "Calls the xApp Manager to undeploy the named Xapp.")
	@RequestMapping(value = "/xapps/{xAppName}", method = RequestMethod.DELETE)
	public void undeployXapp(@PathVariable("xAppName") String xAppName, HttpServletResponse response) {
		logger.debug("undeployXapp {}", xAppName);
		try {
			xappMgrClient.undeployXapp(xAppName);
		} catch (Exception ex) {
			logger.error("deployXapp failed", ex);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

}
