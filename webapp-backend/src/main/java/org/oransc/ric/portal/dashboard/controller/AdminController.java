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

import org.oransc.ric.portal.dashboard.DashboardApplication;
import org.oransc.ric.portal.dashboard.DashboardConstants;
import org.oransc.ric.portal.dashboard.model.DashboardUser;
import org.oransc.ric.portal.dashboard.model.ErrorTransport;
import org.oransc.ric.portal.dashboard.model.IDashboardResponse;
import org.oransc.ric.portal.dashboard.model.SuccessTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Value;

import io.swagger.annotations.ApiOperation;

/**
 * Answers REST requests for admin services like version, health etc.
 */
@RestController
@RequestMapping(value = AdminController.CONTROLLER_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminController {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	// Publish paths in constants so tests are easy to write
	public static final String CONTROLLER_PATH = DashboardConstants.ENDPOINT_PREFIX + "/admin";
	public static final String USER_METHOD = "user";
	public static final String HEALTH_METHOD = "health";
	public static final String VERSION_METHOD = DashboardConstants.VERSION_METHOD;
	public static final String XAPPMETRICS_METHOD = "metrics";

	private final DashboardUser[] users;

	private static final String ACTIVE = "Active";
	private static final String INACTIVE = "Inactive";

	@Value("${metrics.url.ac}")
	private String acAppMetricsUrl;

	@Value("${metrics.url.mc}")
	private String mcAppMetricsUrl;
	public AdminController() {
		// Mock data
		users = new DashboardUser[] { //
				new DashboardUser(1, "John", "Doe", ACTIVE), //
				new DashboardUser(2, "Alice", "Nolan", ACTIVE), //
				new DashboardUser(3, "Pierce", "King", INACTIVE), //
				new DashboardUser(4, "Paul", "Smith", INACTIVE), //
				new DashboardUser(5, "Jack", "Reacher", ACTIVE) };
	}

	@ApiOperation(value = "Gets the Dashboard MANIFEST.MF property Implementation-Version.", response = SuccessTransport.class)
	@GetMapping(VERSION_METHOD)
	// No role required
	public SuccessTransport getVersion() {
		// These endpoints are invoked repeatedly by K8S
		logger.trace("getVersion");
		return new SuccessTransport(200,
				DashboardApplication.getImplementationVersion(MethodHandles.lookup().lookupClass()));
	}

	@ApiOperation(value = "Checks the health of the application.", response = SuccessTransport.class)
	@GetMapping(HEALTH_METHOD)
	// No role required
	public SuccessTransport getHealth() {
		// These endpoints are invoked repeatedly by K8S
		logger.trace("getHealth");
		return new SuccessTransport(200, "Dashboard is healthy!");
	}

	@ApiOperation(value = "Gets the list of application users.", response = DashboardUser.class, responseContainer = "List")
	@GetMapping(USER_METHOD)
	@Secured({ DashboardConstants.ROLE_ADMIN })
	public DashboardUser[] getUsers() {
		logger.debug("getUsers");
		return users;
	}

	@ApiOperation(value = "Gets the kibana metrics URL for the specified app.", response = SuccessTransport.class)
	@GetMapping(XAPPMETRICS_METHOD)
	public IDashboardResponse getAppMetricsUrl(@RequestParam String app, HttpServletResponse response) {
		String metricsUrl = null;
		if (DashboardConstants.APP_NAME_AC.equals(app))
			metricsUrl = acAppMetricsUrl;
		else if (DashboardConstants.APP_NAME_MC.equals(app))
			metricsUrl = mcAppMetricsUrl;
		logger.debug("getAppMetricsUrl: app {} metricsurl {}", app, metricsUrl);
		if (metricsUrl != null)
			return new SuccessTransport(200, metricsUrl);
		else {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(400, "Client provided app name is invalid as: " + app);
		}
	}
}
