/*-
 * ========================LICENSE_START=================================
 * O-RAN-SC
 * %%
 * Copyright (C) 2020 AT&T Intellectual Property
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

import org.oransc.itdev.xapponboarder.client.api.ChartsApi;
import org.oransc.itdev.xapponboarder.client.api.HealthApi;
import org.oransc.itdev.xapponboarder.client.model.Descriptor;
import org.oransc.itdev.xapponboarder.client.model.DescriptorRemote;
import org.oransc.itdev.xapponboarder.client.model.Status;
import org.oransc.ric.portal.dashboard.DashboardApplication;
import org.oransc.ric.portal.dashboard.DashboardConstants;
import org.oransc.ric.portal.dashboard.config.XappOnboarderApiBuilder;
import org.oransc.ric.portal.dashboard.model.SuccessTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;

/**
 * Proxies calls from the front end to the Xapp Onboarder API.
 * 
 * If a method throws RestClientResponseException, it is handled by a method in
 * {@link CustomResponseEntityExceptionHandler} which returns status 502. All
 * other exceptions are handled by Spring which returns status 500.
 */
@Configuration
@RestController
@RequestMapping(value = XappOnboarderController.CONTROLLER_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class XappOnboarderController {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	// Publish paths in constants for tests
	public static final String CONTROLLER_PATH = DashboardConstants.ENDPOINT_PREFIX + "/xappobrd";
	// Dashboard only
	public static final String HEALTH_METHOD = "health";
	public static final String CHARTS_METHOD = "charts";
	public static final String ONBOARD_METHOD = "onboard";
	public static final String ONBOARD_DOWNLOAD_METHOD = "onboard/download";
	// Path parameters
	private static final String XAPPNAME_PP = "xapp";
	private static final String VERSION_PP = "ver";

	// Populated by the autowired constructor
	private final XappOnboarderApiBuilder xappOnboarderApiBuilder;

	@Autowired
	public XappOnboarderController(final XappOnboarderApiBuilder xappOnboarderApiBuilder) {
		Assert.notNull(xappOnboarderApiBuilder, "builder must not be null");
		this.xappOnboarderApiBuilder = xappOnboarderApiBuilder;
		if (logger.isDebugEnabled())
			logger.debug("ctor: configured with builder type {}", xappOnboarderApiBuilder.getClass().getName());
	}

	@ApiOperation(value = "Gets the xapp onboarder client library MANIFEST.MF property Implementation-Version.", response = SuccessTransport.class)
	@GetMapping(DashboardConstants.VERSION_METHOD)
	// No role required
	public SuccessTransport getClientVersion() {
		return new SuccessTransport(200, DashboardApplication.getImplementationVersion(HealthApi.class));
	}

	@ApiOperation(value = "Gets the health from the xapp onboarder, expressed as the response code.")
	@GetMapping(DashboardConstants.RIC_INSTANCE_KEY + "/{" + DashboardConstants.RIC_INSTANCE_KEY + "}/" + HEALTH_METHOD)
	// No role required
	public ResponseEntity<Status> getHealthCheck(
			@PathVariable(DashboardConstants.RIC_INSTANCE_KEY) String instanceKey) {
		logger.debug("getHealthCheck instance {}", instanceKey);
		HealthApi api = xappOnboarderApiBuilder.getHealthApi(instanceKey);
		Status status = api.getHealthCheck();
		return ResponseEntity.status(api.getApiClient().getStatusCode().value()).body(status);
	}

	@ApiOperation(value = "Gets the helm charts.", response = String.class)
	@GetMapping(DashboardConstants.RIC_INSTANCE_KEY + "/{" + DashboardConstants.RIC_INSTANCE_KEY + "}/" + CHARTS_METHOD)
	@Secured({ DashboardConstants.ROLE_ADMIN, DashboardConstants.ROLE_STANDARD })
	public ResponseEntity<String> getCharts(@PathVariable(DashboardConstants.RIC_INSTANCE_KEY) String instanceKey) {
		logger.debug("getCharts instance {}", instanceKey);
		ChartsApi api = xappOnboarderApiBuilder.getChartsApi(instanceKey);
		api.getChartsList(); // TODO
		return ResponseEntity.status(api.getApiClient().getStatusCode().value()).body(null);
	}

	@ApiOperation(value = "Gets the helm chart for the specified xApp and version.", response = String.class)
	@GetMapping(DashboardConstants.RIC_INSTANCE_KEY + "/{" + DashboardConstants.RIC_INSTANCE_KEY + "}/" + CHARTS_METHOD
			+ "/" + XAPPNAME_PP + "/{" + XAPPNAME_PP + "}/" + VERSION_PP + "/{" + VERSION_PP + "}")
	@Secured({ DashboardConstants.ROLE_ADMIN, DashboardConstants.ROLE_STANDARD })
	public ResponseEntity<String> getChart(@PathVariable(DashboardConstants.RIC_INSTANCE_KEY) String instanceKey,
			@PathVariable(XAPPNAME_PP) String xappName, @PathVariable(VERSION_PP) String version) {
		logger.debug("getChart instance {} xapp {} ver {}", instanceKey, xappName, version);
		ChartsApi api = xappOnboarderApiBuilder.getChartsApi(instanceKey);
		api.getChartsFetcher(xappName, version); // TODO
		return ResponseEntity.status(api.getApiClient().getStatusCode().value()).body(null);
	}

	@ApiOperation(value = "Gets the values yaml for the specified xApp and version.", response = String.class)
	@GetMapping(DashboardConstants.RIC_INSTANCE_KEY + "/{" + DashboardConstants.RIC_INSTANCE_KEY + "}/" + CHARTS_METHOD
			+ "/" + XAPPNAME_PP + "/{" + XAPPNAME_PP + "}/" + VERSION_PP + "/{" + VERSION_PP + "}/values.yaml")
	@Secured({ DashboardConstants.ROLE_ADMIN, DashboardConstants.ROLE_STANDARD })
	public ResponseEntity<String> getValues(@PathVariable(DashboardConstants.RIC_INSTANCE_KEY) String instanceKey,
			@PathVariable(XAPPNAME_PP) String xappName, @PathVariable(VERSION_PP) String version) {
		logger.debug("getValues instance {} xapp {} ver {}", instanceKey, xappName, version);
		ChartsApi api = xappOnboarderApiBuilder.getChartsApi(instanceKey);
		api.getValuesYamlFetcher(xappName, version); // TODO
		return ResponseEntity.status(api.getApiClient().getStatusCode().value()).body(null);
	}

	@ApiOperation(value = "Onboard xApp using the xApp descriptor and schema in the request body.", response = Status.class)
	@PostMapping(ONBOARD_METHOD)
	@Secured({ DashboardConstants.ROLE_ADMIN })
	public Status onboardXapp(@PathVariable(DashboardConstants.RIC_INSTANCE_KEY) String instanceKey, //
			@Validated @RequestBody Descriptor appDescriptor) {
		logger.debug("onboardxApp instance {} descriptor {}", instanceKey, appDescriptor);
		return xappOnboarderApiBuilder.getOnboardApi(instanceKey).postOnboardxApps(appDescriptor);
	}

	@ApiOperation(value = "Onboard xApp after downloading the xApp descriptor and schema from the URLs.", response = Status.class)
	@PostMapping(ONBOARD_DOWNLOAD_METHOD)
	@Secured({ DashboardConstants.ROLE_ADMIN })
	public Status onboardRemoteXapp(@PathVariable(DashboardConstants.RIC_INSTANCE_KEY) String instanceKey, //
			@Validated @RequestBody DescriptorRemote appDescriptor) {
		logger.debug("onboardRemoteXapp instance {} descriptor {}", instanceKey, appDescriptor);
		return xappOnboarderApiBuilder.getOnboardApi(instanceKey).postOnboardxAppsDownload(appDescriptor);
	}

}
