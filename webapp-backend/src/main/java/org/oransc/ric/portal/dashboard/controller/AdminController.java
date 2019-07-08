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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.oransc.ric.portal.dashboard.DashboardApplication;
import org.oransc.ric.portal.dashboard.DashboardConstants;
import org.oransc.ric.portal.dashboard.model.DashboardUser;
import org.oransc.ric.portal.dashboard.model.LoginTransport;
import org.oransc.ric.portal.dashboard.model.SuccessTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;

/**
 * Answers REST requests for admin services like version, health etc.
 */
@RestController
@RequestMapping(value = AdminController.CONTROLLER_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminController extends AbstractController {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	// Publish paths in constants so tests are easy to write
	public static final String CONTROLLER_PATH = DashboardConstants.ENDPOINT_PREFIX + "/admin";
	public static final String USER_METHOD = "user";
	public static final String HEALTH_METHOD = "health";
	public static final String LOGIN_METHOD = "login";
	public static final String VERSION_METHOD = DashboardConstants.VERSION_METHOD;

	private final DashboardUser[] users;

	private static final String ACTIVE = "Active";
	private static final String INACTIVE = "Inactive";

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
	public SuccessTransport getVersion() {
		logger.debug("getVersion");
		// No role requirement
		return new SuccessTransport(200,
				DashboardApplication.getImplementationVersion(MethodHandles.lookup().lookupClass()));
	}

	@ApiOperation(value = "Checks the health of the application.", response = SuccessTransport.class)
	@GetMapping(HEALTH_METHOD)
	public SuccessTransport getHealth() {
		logger.debug("getHealth");
		// No role requirement
		return new SuccessTransport(200, "Dashboard is healthy!");
	}

	@ApiOperation(value = "Requests login.", response = SuccessTransport.class)
	@RequestMapping(LOGIN_METHOD)
	public SuccessTransport login(@RequestBody LoginTransport loginRequest, HttpServletResponse response) {
		logger.debug("login {}", loginRequest.getUsername());
		// No role requirement
		// TODO
		if ("demo".equals(loginRequest.getUsername()) && "demo".equals(loginRequest.getPassword()))
			return new SuccessTransport(200, "Login " + loginRequest.getUsername());
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		return null;
	}

	@ApiOperation(value = "Gets the list of application users.", response = DashboardUser.class, responseContainer = "List")
	@GetMapping(USER_METHOD)
	public DashboardUser[] getUsers(HttpServletRequest request) {
		logger.debug("getUsers");
		checkRoles(request, DashboardConstants.USER_ROLE_PRIV);
		return users;
	}

}
