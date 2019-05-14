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

import org.oransc.ric.portal.dashboard.DashboardApplication;
import org.oransc.ric.portal.dashboard.DashboardConstants;
import org.oransc.ric.portal.dashboard.model.SuccessTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;

/**
 * Answers REST requests for the API service health etc.
 */
@RestController
@RequestMapping(value = DashboardConstants.ENDPOINT_PREFIX + "/dashboard", produces = MediaType.APPLICATION_JSON_VALUE)
public class HealthcheckController {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@ApiOperation(value = "Checks the health of the application by (TBD).", response = SuccessTransport.class)
	@RequestMapping(value = DashboardConstants.HEALTHCHECK_PATH, method = RequestMethod.GET)
	public SuccessTransport getHealth() {
		logger.debug("getHealth enter");
		long count = 0;
		return new SuccessTransport(200, "(TBD) reports count is " + count);
	}

	@ApiOperation(value = "Gets the Dashboard MANIFEST.MF property Implementation-Version.", response = SuccessTransport.class)
	@RequestMapping(value = DashboardConstants.VERSION_PATH, method = RequestMethod.GET)
	public SuccessTransport getVersion() {
		logger.debug("getVersion enter");
		return new SuccessTransport(200, DashboardApplication.getImplementationVersion(MethodHandles.lookup().lookupClass()));
	}

}
