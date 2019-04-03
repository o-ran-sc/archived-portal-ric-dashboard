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
import org.oranosc.ric.portal.dashboard.model.AllXapps;
import org.oranosc.ric.portal.dashboard.model.Xapp;
import org.oranosc.ric.portal.dashboard.model.Xapp.StatusEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = DashboardConstants.ENDPOINT_PREFIX + "/catalog", produces = MediaType.APPLICATION_JSON_VALUE)
public class CatalogController {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@RequestMapping(method = RequestMethod.GET)
	public AllXapps getXapps() {
		logger.debug("getXapps: enter");
		return populateCatalog();
	}

	// @TODO This method to be removed when endpoint for data fetch from RIC team
	// available
	private AllXapps populateCatalog() {
		AllXapps cList = new AllXapps();
		cList.add(buildXapp("Pendulum Control", "v1", StatusEnum.DEPLOYED));
		cList.add(buildXapp("Dual Connectivity", "v2", StatusEnum.DELETED));
		cList.add(buildXapp("Admission Control", "v1", StatusEnum.FAILED));
		cList.add(buildXapp("ANR Control", "v0", StatusEnum.SUPERSEDED));
		return cList;
	}

	private Xapp buildXapp(String name, String version, StatusEnum status) {
		Xapp xapp = new Xapp();
		xapp.setName(name);
		xapp.setVersion(version);
		xapp.setStatus(status);
		return xapp;
	}

}
