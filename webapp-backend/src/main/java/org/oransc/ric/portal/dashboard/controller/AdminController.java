/*-
 * ========================LICENSE_START=================================
 * O-RAN-SC
 * %%
 * Copyright (C) 2019 AT&T Intellectual Property
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
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.onap.portalsdk.core.restful.domain.EcompUser;
import org.oransc.ric.portal.dashboard.DashboardApplication;
import org.oransc.ric.portal.dashboard.DashboardConstants;
import org.oransc.ric.portal.dashboard.DashboardUserManager;
import org.oransc.ric.portal.dashboard.model.ErrorTransport;
import org.oransc.ric.portal.dashboard.model.IDashboardResponse;
import org.oransc.ric.portal.dashboard.model.RanDetailsTransport;
import org.oransc.ric.portal.dashboard.model.RicInstanceList;
import org.oransc.ric.portal.dashboard.model.StatsDetailsTransport;
import org.oransc.ric.portal.dashboard.model.StatsIdentity;
import org.oransc.ric.portal.dashboard.model.StatsResponse;
import org.oransc.ric.portal.dashboard.model.StatsSetupRequest;
import org.oransc.ric.portal.dashboard.model.RicInstanceKeyName;
import org.oransc.ric.portal.dashboard.model.RicInstanceList;
import org.oransc.ric.portal.dashboard.model.SuccessTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;

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
	public static final String HEALTH_METHOD = "health";
	public static final String INSTANCE_METHOD = "instance";
	public static final String USER_METHOD = "user";
	public static final String VERSION_METHOD = DashboardConstants.VERSION_METHOD;
	public static final String XAPPMETRICS_METHOD = "metrics";
	public static final String STATAPPMETRIC_METHOD = "appmetric";

	@Value("${metrics.url.ac}")
	private String acAppMetricsUrl;

	@Value("${metrics.url.mc}")
	private String mcAppMetricsUrl;

	@Autowired
	private DashboardUserManager dashboardUserManager;

	@Autowired
	private RicInstanceList instanceConfig;
	
	private List<StatsDetailsTransport> details = new ArrayList<>();
	
	private int uniqAppMetricId = 0;

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

	@ApiOperation(value = "Gets the list of application users.", response = EcompUser.class, responseContainer = "List")
	@GetMapping(USER_METHOD)
	@Secured({ DashboardConstants.ROLE_ADMIN }) // regular users should not see this
	public List<EcompUser> getUsers() {
		logger.debug("getUsers");
		return dashboardUserManager.getUsers();
	}

	@ApiOperation(value = "Gets the list of RIC instances.", response = RicInstanceKeyName.class, responseContainer = "List")
	@GetMapping(INSTANCE_METHOD)
	@Secured({ DashboardConstants.ROLE_ADMIN, DashboardConstants.ROLE_STANDARD })
	public List<RicInstanceKeyName> getInstances() {
		logger.debug("getInstances");
		return instanceConfig.getKeyNameList();
	}

	@ApiOperation(value = "Gets the kibana metrics URL for the specified app.", response = SuccessTransport.class)
	@GetMapping(XAPPMETRICS_METHOD)
	@Secured({ DashboardConstants.ROLE_ADMIN, DashboardConstants.ROLE_STANDARD })
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
	
	@ApiOperation(value = "Gets all App identities and metrics statuses.", response = StatsDetailsTransport.class, responseContainer = "List")
	@GetMapping(DashboardConstants.RIC_INSTANCE_KEY + "/{" + DashboardConstants.RIC_INSTANCE_KEY + "}/" + STATAPPMETRIC_METHOD)
	@Secured({ DashboardConstants.ROLE_ADMIN, DashboardConstants.ROLE_STANDARD })
	public List<StatsDetailsTransport> getStatsDetails(
			@PathVariable(DashboardConstants.RIC_INSTANCE_KEY) String instanceKey) {
		logger.debug("getStatsDetails instance {}", instanceKey);
		/*details = new ArrayList<>();
		StatsIdentity stiden = new StatsIdentity("1");
		StatsResponse stresp = new StatsResponse("MC", "www.test.com");
		details.add(new StatsDetailsTransport(stiden, stresp));*/
		return details;
	}
	
	@ApiOperation(value = "Sets up an app metrics status .")
	@PostMapping(DashboardConstants.RIC_INSTANCE_KEY + "/{" + DashboardConstants.RIC_INSTANCE_KEY + "}/"
			+ STATAPPMETRIC_METHOD)
	@Secured({ DashboardConstants.ROLE_ADMIN })
	public void statsSetup(@PathVariable(DashboardConstants.RIC_INSTANCE_KEY) String instanceKey,
			@RequestBody StatsSetupRequest statsSetupRequest, HttpServletResponse response) {
		logger.debug("StatsSetup instance {} request {}", instanceKey, statsSetupRequest);
		details.add(new StatsDetailsTransport(new StatsIdentity(++uniqAppMetricId),
				new StatsResponse(statsSetupRequest.getAppName(), statsSetupRequest.getMetricUrl())));
		//response.setStatus(api.getApiClient().getStatusCode().value());
	}

	@ApiOperation(value = "Edits an app metrics status .")
	@PutMapping(DashboardConstants.RIC_INSTANCE_KEY + "/{" + DashboardConstants.RIC_INSTANCE_KEY + "}/"
			+ STATAPPMETRIC_METHOD)
	@Secured({ DashboardConstants.ROLE_ADMIN })
	public void editStats(@PathVariable(DashboardConstants.RIC_INSTANCE_KEY) String instanceKey,
			@RequestBody StatsSetupRequest statsSetupRequest, HttpServletResponse response) {
		logger.debug("editStats instance {} request {}", instanceKey, statsSetupRequest);
		for (StatsDetailsTransport st: details) {
			if (st.getStatsIdentity().getAppId()==statsSetupRequest.getAppId()) {
				st.getStatsStatus().setAppName(statsSetupRequest.getAppName());
				st.getStatsStatus().setMetricUrl(statsSetupRequest.getMetricUrl());
				return;
			}
		}
		details.add(new StatsDetailsTransport(new StatsIdentity(++uniqAppMetricId),
				new StatsResponse(statsSetupRequest.getAppName(), statsSetupRequest.getMetricUrl())));
		//response.setStatus(api.getApiClient().getStatusCode().value());
	}



}
