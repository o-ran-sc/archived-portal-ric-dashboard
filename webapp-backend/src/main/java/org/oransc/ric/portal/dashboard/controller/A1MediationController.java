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
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import javax.servlet.http.HttpServletResponse;

import org.oransc.ric.portal.dashboard.DashboardConstants;
import org.oransc.ric.portal.dashboard.model.DelayTransport;
import org.oransc.ric.portal.dashboard.model.ErrorTransport;
import org.oransc.ric.portal.dashboard.model.IDashboardResponse;
import org.oransc.ric.portal.dashboard.model.LoadTransport;
import org.oransc.ric.portal.dashboard.model.MetricsTransport;
import org.oransc.ric.portal.dashboard.model.PathTransport;
import org.oransc.ric.portal.dashboard.model.SuccessTransport;
import org.oransc.ric.portal.dashboard.model.UrlTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import io.swagger.annotations.ApiOperation;

/**
 * Provides endpoints to get/set paths, AND to access the REST resources at
 * those paths. This allows very late binding of deployment details.
 */
@Configuration
@RestController
@RequestMapping(value = DashboardConstants.ENDPOINT_PREFIX + "/a1med", produces = MediaType.APPLICATION_JSON_VALUE)
public class A1MediationController {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private static final String A1_MEDIATION_URL = "url";
	private static final String A1_MEDIATION_DELAY = "delay";
	private static final String A1_MEDIATION_DELAY_PATH = A1_MEDIATION_DELAY + "path";
	private static final String A1_MEDIATION_LOAD = "load";
	private static final String A1_MEDIATION_LOAD_PATH = A1_MEDIATION_LOAD + "path";
	private static final String A1_MEDIATION_METRICS = "metrics";
	private static final String A1_MEDIATION_METRICS_PATH = A1_MEDIATION_METRICS + "path";

	@Value("${a1med.basepath}")
	private String a1MediationUrl;
	@Value("${a1med.delaypath}")
	private String a1MediationDelayPath;
	@Value("${a1med.loadpath}")
	private String a1MediationLoadPath;
	@Value("${a1med.metricspath}")
	private String a1MediationMetricsPath;

	// For demo purposes
	private final boolean mockData = true;
	private final DelayTransport mockDelay = new DelayTransport(10);
	private final LoadTransport mockLoad = new LoadTransport(1);
	private final MetricsTransport mockMetrics = new MetricsTransport(11, 100, 123);

	private final RestTemplate restTemplate = new RestTemplate();

	private URI buildUri(final String baseUrl, final String[] paths) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl);
		for (int p = 0; p < paths.length; ++p) {
			if (paths[p] == null)
				throw new IllegalArgumentException("Unexpected null at index " + Integer.toString(p));
			// this allows slashes
			builder.path(paths[p]);
		}
		return builder.build().encode().toUri();
	}

	@ApiOperation(value = "Gets the A1 Mediation URL.", response = IDashboardResponse.class)
	@RequestMapping(value = A1_MEDIATION_URL, method = RequestMethod.GET)
	public IDashboardResponse getA1MediationUrl() {
		return new UrlTransport(a1MediationUrl);
	}

	@ApiOperation(value = "Sets the A1 Mediation URL.", response = IDashboardResponse.class)
	@RequestMapping(value = A1_MEDIATION_URL, method = RequestMethod.PUT)
	public IDashboardResponse setA1MediationUrl(@RequestBody UrlTransport st, HttpServletResponse response) {
		try {
			this.a1MediationUrl = new URL(st.getUrl()).toString();
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (MalformedURLException ex) {
			logger.error("Failed to parse url " + st.getUrl(), ex);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(400, "Bad URL", ex);
		}
	}

	@ApiOperation(value = "Gets the A1 Mediation delay path.", response = IDashboardResponse.class)
	@RequestMapping(value = A1_MEDIATION_DELAY_PATH, method = RequestMethod.GET)
	public IDashboardResponse getA1MediationDelayPath() {
		return new PathTransport(a1MediationDelayPath);
	}

	@ApiOperation(value = "Sets the A1 Mediation delay path.", response = IDashboardResponse.class)
	@RequestMapping(value = A1_MEDIATION_DELAY_PATH, method = RequestMethod.PUT)
	public IDashboardResponse setA1MediationDelayPath(@RequestBody PathTransport st) {
		this.a1MediationDelayPath = st.getPath();
		return new SuccessTransport(HttpServletResponse.SC_OK, null);
	}

	@ApiOperation(value = "Gets the A1 Mediation load path.", response = IDashboardResponse.class)
	@RequestMapping(value = A1_MEDIATION_LOAD_PATH, method = RequestMethod.GET)
	public IDashboardResponse getA1MediationLoadPath() {
		return new PathTransport(a1MediationLoadPath);
	}

	@ApiOperation(value = "Sets the A1 Mediation load path.", response = IDashboardResponse.class)
	@RequestMapping(value = A1_MEDIATION_LOAD_PATH, method = RequestMethod.PUT)
	public IDashboardResponse setA1MediationLoadPath(@RequestBody PathTransport st) {
		this.a1MediationLoadPath = st.getPath();
		return new SuccessTransport(HttpServletResponse.SC_OK, null);
	}

	@ApiOperation(value = "Gets the A1 Mediation metrics path.", response = IDashboardResponse.class)
	@RequestMapping(value = A1_MEDIATION_METRICS_PATH, method = RequestMethod.GET)
	public IDashboardResponse getA1MediationMetricsPath() {
		return new PathTransport(a1MediationMetricsPath);
	}

	@ApiOperation(value = "Sets the A1 Mediation metrics path.", response = IDashboardResponse.class)
	@RequestMapping(value = A1_MEDIATION_METRICS_PATH, method = RequestMethod.PUT)
	public IDashboardResponse setA1MediationMetricsPath(@RequestBody PathTransport st) {
		this.a1MediationMetricsPath = st.getPath();
		return new SuccessTransport(HttpServletResponse.SC_OK, null);
	}

	@ApiOperation(value = "Gets the A1 Mediation delay value.", response = DelayTransport.class)
	@RequestMapping(value = A1_MEDIATION_DELAY, method = RequestMethod.GET)
	public DelayTransport getA1MediationDelay() {
		if (mockData) {
			return mockDelay;
		} else {
			URI uri = buildUri(a1MediationUrl, new String[] { a1MediationDelayPath });
			logger.debug("getA1MediationDelay: uri {}", uri);
			ResponseEntity<DelayTransport> response = restTemplate.exchange(uri, HttpMethod.GET, null,
					new ParameterizedTypeReference<DelayTransport>() {
					});
			return response.getBody();
		}
	}

	@ApiOperation(value = "Sets the A1 Mediation delay value.")
	@RequestMapping(value = A1_MEDIATION_DELAY, method = RequestMethod.PUT)
	public void putA1MediationDelay(DelayTransport value) {
		if (mockData) {
			mockDelay.setDelay(value.getDelay());
		} else {
			URI uri = buildUri(a1MediationUrl, new String[] { a1MediationDelayPath });
			logger.debug("putA1MediationDelay: uri {}", uri);
			restTemplate.put(uri, value);
		}
	}

	@ApiOperation(value = "Gets the A1 Mediation load value.", response = LoadTransport.class)
	@RequestMapping(value = A1_MEDIATION_LOAD, method = RequestMethod.GET)
	public LoadTransport getA1MediationLoad() {
		if (mockData) {
			return mockLoad;
		} else {
			URI uri = buildUri(a1MediationUrl, new String[] { a1MediationLoadPath });
			logger.debug("getA1MediationLoad: uri {}", uri);
			ResponseEntity<LoadTransport> response = restTemplate.exchange(uri, HttpMethod.GET, null,
					new ParameterizedTypeReference<LoadTransport>() {
					});
			return response.getBody();
		}
	}

	@ApiOperation(value = "Sets the A1 Mediation delay value.")
	@RequestMapping(value = A1_MEDIATION_LOAD, method = RequestMethod.PUT)
	public void putA1MediationLoad(LoadTransport value) {
		if (mockData) {
			mockLoad.setLoad(value.getLoad());
		} else {
			URI uri = buildUri(a1MediationUrl, new String[] { a1MediationDelayPath });
			logger.debug("putA1MediationLoad: uri {}", uri);
			restTemplate.put(uri, value);
		}
	}

	@ApiOperation(value = "Gets the A1 Mediation metrics object.", response = MetricsTransport.class)
	@RequestMapping(value = A1_MEDIATION_METRICS, method = RequestMethod.GET)
	public MetricsTransport getA1MediationMetrics() {
		if (mockData) {
			return mockMetrics;
		} else {
			URI uri = buildUri(a1MediationUrl, new String[] { a1MediationLoadPath });
			logger.debug("getA1MediationMetrics: uri {}", uri);
			ResponseEntity<MetricsTransport> response = restTemplate.exchange(uri, HttpMethod.GET, null,
					new ParameterizedTypeReference<MetricsTransport>() {
					});
			return response.getBody();
		}
	}

}
