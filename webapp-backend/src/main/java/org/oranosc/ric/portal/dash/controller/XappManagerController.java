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

import org.oranosc.ric.portal.dash.DashboardConstants;
import org.oranosc.ric.portal.dashboard.xmc.api.DefaultApi;
import org.oranosc.ric.portal.dashboard.xmc.model.AllXapps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;

@Configuration
@RestController
@RequestMapping(value = DashboardConstants.ENDPOINT_PREFIX + "/xappmgr", produces = MediaType.APPLICATION_JSON_VALUE)
public class XappManagerController {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired
	private DefaultApi xappManagerClient;

	@ApiOperation(value = "Gets the list of xApps from the xApp manager.", response = AllXapps.class)
	@RequestMapping(value = "/xapps", method = RequestMethod.GET)
	public AllXapps getAllXapps() {
		logger.debug("getAllXapps via " + xappManagerClient.getApiClient().getBasePath());
		AllXapps all = xappManagerClient.getAllXapps();
		return all;
	}

}
