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

import org.oransc.ric.plt.appmgr.client.api.HealthApi;
import org.oransc.ric.plt.appmgr.client.api.XappApi;
import org.oransc.ric.plt.appmgr.client.model.AllDeployableXapps;
import org.oransc.ric.plt.appmgr.client.model.AllDeployedXapps;
import org.oransc.ric.plt.appmgr.client.model.AllXappConfig;
import org.oransc.ric.plt.appmgr.client.model.ConfigMetadata;
import org.oransc.ric.plt.appmgr.client.model.XAppConfig;
import org.oransc.ric.plt.appmgr.client.model.XAppInfo;
import org.oransc.ric.plt.appmgr.client.model.Xapp;
import org.oransc.ric.portal.dashboard.DashboardApplication;
import org.oransc.ric.portal.dashboard.DashboardConstants;
import org.oransc.ric.portal.dashboard.model.AppTransport;
import org.oransc.ric.portal.dashboard.model.DashboardDeployableXapps;
import org.oransc.ric.portal.dashboard.model.SuccessTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;

import io.swagger.annotations.ApiOperation;

/**
 * Proxies calls from the front end to the App Manager API. All methods answer
 * 502 on failure: <blockquote>HTTP server received an invalid response from a
 * server it consulted when acting as a proxy or gateway.</blockquote>
 */
@Configuration
@RestController
@RequestMapping(value = AppManagerController.CONTROLLER_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class AppManagerController {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	// Publish paths in constants so tests are easy to write
	public static final String CONTROLLER_PATH = DashboardConstants.ENDPOINT_PREFIX + "/appmgr";
	// Endpoints
	public static final String HEALTH_ALIVE_METHOD = "/health/alive";
	public static final String HEALTH_READY_METHOD = "/health/ready";
	public static final String CONFIG_METHOD = "/config";
	public static final String XAPPS_METHOD = "/xapps";
	public static final String XAPPS_LIST_METHOD = XAPPS_METHOD + "/list";
	// Path parameters
	public static final String PP_XAPP_NAME = "xAppName";

	// Populated by the autowired constructor
	private final HealthApi healthApi;
	private final XappApi xappApi;

	@Autowired
	public AppManagerController(final HealthApi healthApi, final XappApi xappApi) {
		Assert.notNull(healthApi, "health API must not be null");
		Assert.notNull(xappApi, "xapp API must not be null");
		this.healthApi = healthApi;
		this.xappApi = xappApi;
		if (logger.isDebugEnabled())
			logger.debug("ctor: configured with client types {} and {}", healthApi.getClass().getName(),
					xappApi.getClass().getName());
	}

	@ApiOperation(value = "Gets the XApp manager client library MANIFEST.MF property Implementation-Version.", response = SuccessTransport.class)
	@RequestMapping(value = DashboardConstants.VERSION_METHOD, method = RequestMethod.GET)
	public SuccessTransport getXappManagerClientVersion() {
		return new SuccessTransport(200, DashboardApplication.getImplementationVersion(HealthApi.class));
	}

	@ApiOperation(value = "Health check of xApp Manager - Liveness probe.")
	@RequestMapping(value = HEALTH_ALIVE_METHOD, method = RequestMethod.GET)
	public Object getHealthAlive(HttpServletResponse response) {
		logger.debug("getHealthAlive");
		try {
			healthApi.getHealthAlive();
			response.setStatus(healthApi.getApiClient().getStatusCode().value());
			return null;
		} catch (HttpStatusCodeException ex) {
			logger.error("getHealthAlive failed: {}", ex.toString());
			return ResponseEntity.status(HttpServletResponse.SC_BAD_GATEWAY).body(ex.getResponseBodyAsString());
		}
	}

	@ApiOperation(value = "Readiness check of xApp Manager - Readiness probe.")
	@RequestMapping(value = HEALTH_READY_METHOD, method = RequestMethod.GET)
	public Object getHealthReady(HttpServletResponse response) {
		logger.debug("getHealthReady");
		try {
			healthApi.getHealthReady();
			response.setStatus(healthApi.getApiClient().getStatusCode().value());
			return null;
		} catch (HttpStatusCodeException ex) {
			logger.error("getHealthReady failed: {}", ex.toString());
			return ResponseEntity.status(HttpServletResponse.SC_BAD_GATEWAY).body(ex.getResponseBodyAsString());
		}
	}

	@ApiOperation(value = "Returns the configuration of all xapps.", response = AllXappConfig.class)
	@RequestMapping(value = CONFIG_METHOD, method = RequestMethod.GET)
	public Object getAllXappConfig() {
		logger.debug("getAllXappConfig");
		try {
			return xappApi.getAllXappConfig();
		} catch (HttpStatusCodeException ex) {
			logger.error("getAllXappConfig failed: {}", ex.toString());
			return ResponseEntity.status(HttpServletResponse.SC_BAD_GATEWAY).body(ex.getResponseBodyAsString());
		}
	}

	@ApiOperation(value = "Create xApp config.")
	@RequestMapping(value = CONFIG_METHOD, method = RequestMethod.POST)
	public Object createXappConfig(@RequestBody XAppConfig xAppConfig) {
		logger.debug("createXappConfig {}", xAppConfig);
		try {
			return xappApi.createXappConfig(xAppConfig);
		} catch (HttpStatusCodeException ex) {
			logger.error("undeployXapp failed: {}", ex.toString());
			return ResponseEntity.status(HttpServletResponse.SC_BAD_GATEWAY).body(ex.getResponseBodyAsString());
		}
	}

	@ApiOperation(value = "Modify xApp config.")
	@RequestMapping(value = CONFIG_METHOD, method = RequestMethod.PUT)
	public Object modifyXappConfig(@RequestBody XAppConfig xAppConfig) {
		logger.debug("modifyXappConfig {}", xAppConfig);
		try {
			return xappApi.modifyXappConfig(xAppConfig);
		} catch (HttpStatusCodeException ex) {
			logger.error("modifyXappConfig failed: {}", ex.toString());
			return ResponseEntity.status(HttpServletResponse.SC_BAD_GATEWAY).body(ex.getResponseBodyAsString());
		}
	}

	@ApiOperation(value = "Delete xApp configuration.")
	@RequestMapping(value = CONFIG_METHOD + "/{" + PP_XAPP_NAME + "}", method = RequestMethod.DELETE)
	public Object deleteXappConfig(@PathVariable(PP_XAPP_NAME) String xappName,
			@RequestBody ConfigMetadata configMetadata, HttpServletResponse response) {
		logger.debug("deleteXappConfig {}", xappName);
		try {
			xappApi.deleteXappConfig(configMetadata);
			response.setStatus(healthApi.getApiClient().getStatusCode().value());
			return null;
		} catch (HttpStatusCodeException ex) {
			logger.error("deleteXappConfig failed: {}", ex.toString());
			return ResponseEntity.status(HttpServletResponse.SC_BAD_GATEWAY).body(ex.getResponseBodyAsString());
		}
	}

	@ApiOperation(value = "Returns a list of deployable xapps.", response = DashboardDeployableXapps.class)
	@RequestMapping(value = XAPPS_LIST_METHOD, method = RequestMethod.GET)
	public Object getAvailableXapps() {
		logger.debug("getAvailableXapps");
		try {
			AllDeployableXapps appNames = xappApi.listAllXapps();
			// Answer a collection of structure instead of string
			DashboardDeployableXapps apps = new DashboardDeployableXapps();
			for (String n : appNames)
				apps.add(new AppTransport(n));
			return apps;
		} catch (HttpStatusCodeException ex) {
			logger.error("getAvailableXapps failed: {}", ex.toString());
			return ResponseEntity.status(HttpServletResponse.SC_BAD_GATEWAY).body(ex.getResponseBodyAsString());
		}
	}

	@ApiOperation(value = "Returns the status of all deployed xapps.", response = AllDeployedXapps.class)
	@RequestMapping(value = XAPPS_METHOD, method = RequestMethod.GET)
	public Object getDeployedXapps() {
		logger.debug("getDeployedXapps");
		try {
			return xappApi.getAllXapps();
		} catch (HttpStatusCodeException ex) {
			logger.error("getDeployedXapps failed: {}", ex.toString());
			return ResponseEntity.status(HttpServletResponse.SC_BAD_GATEWAY).body(ex.getResponseBodyAsString());
		}
	}

	@ApiOperation(value = "Returns the status of a given xapp.", response = Xapp.class)
	@RequestMapping(value = XAPPS_METHOD + "/{" + PP_XAPP_NAME + "}", method = RequestMethod.GET)
	public Object getXapp(@PathVariable(PP_XAPP_NAME) String xAppName) {
		logger.debug("getXapp {}", xAppName);
		try {
			return xappApi.getXappByName(xAppName);
		} catch (HttpStatusCodeException ex) {
			logger.error("getXapp failed: {}", ex.toString());
			return ResponseEntity.status(HttpServletResponse.SC_BAD_GATEWAY).body(ex.getResponseBodyAsString());
		}
	}

	@ApiOperation(value = "Deploy a xapp.", response = Xapp.class)
	@RequestMapping(value = XAPPS_METHOD, method = RequestMethod.POST)
	public Object deployXapp(@RequestBody XAppInfo xAppInfo) {
		logger.debug("deployXapp {}", xAppInfo);
		try {
			return xappApi.deployXapp(xAppInfo);
		} catch (HttpStatusCodeException ex) {
			logger.error("deployXapp failed: {}", ex.toString());
			return ResponseEntity.status(HttpServletResponse.SC_BAD_GATEWAY).body(ex.getResponseBodyAsString());
		}
	}

	@ApiOperation(value = "Undeploy an existing xapp.")
	@RequestMapping(value = XAPPS_METHOD + "/{" + PP_XAPP_NAME + "}", method = RequestMethod.DELETE)
	public Object undeployXapp(@PathVariable(PP_XAPP_NAME) String xAppName, HttpServletResponse response) {
		logger.debug("undeployXapp {}", xAppName);
		try {
			xappApi.undeployXapp(xAppName);
			response.setStatus(healthApi.getApiClient().getStatusCode().value());
			return null;
		} catch (HttpStatusCodeException ex) {
			logger.error("undeployXapp failed: {}", ex.toString());
			return ResponseEntity.status(HttpServletResponse.SC_BAD_GATEWAY).body(ex.getResponseBodyAsString());
		}
	}

}
