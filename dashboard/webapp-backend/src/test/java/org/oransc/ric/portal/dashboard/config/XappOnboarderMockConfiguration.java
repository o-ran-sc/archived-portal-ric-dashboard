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
package org.oransc.ric.portal.dashboard.config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.lang.invoke.MethodHandles;

import org.oransc.itdev.xapponboarder.client.api.ChartsApi;
import org.oransc.itdev.xapponboarder.client.api.HealthApi;
import org.oransc.itdev.xapponboarder.client.api.OnboardApi;
import org.oransc.itdev.xapponboarder.client.invoker.ApiClient;
import org.oransc.itdev.xapponboarder.client.model.Descriptor;
import org.oransc.itdev.xapponboarder.client.model.DescriptorRemote;
import org.oransc.itdev.xapponboarder.client.model.Status;
import org.oransc.ric.portal.dashboard.TestUtils;
import org.oransc.ric.portal.dashboard.model.RicRegionList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;

/**
 * Creates a mock implementation of the Xapp Onboarder client API.
 */
@Configuration
@Profile("test")
public class XappOnboarderMockConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	// Simulate remote method delay for UI testing
	private int delayMs;

	// Autowire all the properties required by the real class
	// (even tho not used here) as a test of the properties.
	@Autowired
	public XappOnboarderMockConfiguration(@Value("${xappobrd.url.suffix}") final String urlSuffix, //
			final RicRegionList instanceConfig, //
			@Value("${mock.config.delay:0}") int delayMs) {
		logger.info("ctor: configured with suffix {}, instances {}, delay {}", urlSuffix, instanceConfig, delayMs);
		this.delayMs = delayMs;
	}

	private ApiClient apiClient() {
		ApiClient mockClient = mock(ApiClient.class);
		when(mockClient.getStatusCode()).thenReturn(HttpStatus.OK);
		return mockClient;
	}

	private HealthApi healthApi() {
		ApiClient apiClient = apiClient();
		HealthApi mockApi = mock(HealthApi.class);
		when(mockApi.getApiClient()).thenReturn(apiClient);
		doAnswer(i -> {
			return new Status().status("OK");
		}).when(mockApi).getHealthCheck();
		return mockApi;
	}

	private ChartsApi chartsApi() throws IOException {
		final String sampleChartListJson = TestUtils.readDataFromPath("sample-chart-list.json");
		final String sampleChartYaml = TestUtils.readDataFromPath("sample-chart.yaml");
		final String sampleValuesYaml = TestUtils.readDataFromPath("sample-values.yaml");
		ApiClient apiClient = apiClient();
		ChartsApi mockApi = mock(ChartsApi.class);
		when(mockApi.getApiClient()).thenReturn(apiClient);
		doAnswer(inv -> {
			if (delayMs > 0) {
				logger.debug("getChartsList sleeping {}", delayMs);
				Thread.sleep(delayMs);
			}
			return sampleChartListJson;
		}).when(mockApi).getChartsList();
		doAnswer(inv -> {
			if (delayMs > 0) {
				logger.debug("getChartsFetcher sleeping {}", delayMs);
				Thread.sleep(delayMs);
			}
			return sampleChartYaml;
		}).when(mockApi).getChartsFetcher(any(String.class), any(String.class));
		doAnswer(inv -> {
			if (delayMs > 0) {
				logger.debug("getValuesYamlFetcher sleeping {}", delayMs);
				Thread.sleep(delayMs);
			}
			return sampleValuesYaml;
		}).when(mockApi).getValuesYamlFetcher(any(String.class), any(String.class));
		return mockApi;
	}

	private OnboardApi onboardApi() {
		ApiClient apiClient = apiClient();
		OnboardApi mockApi = mock(OnboardApi.class);
		when(mockApi.getApiClient()).thenReturn(apiClient);
		doAnswer(inv -> {
			if (delayMs > 0) {
				logger.debug("postOnboardxApps sleeping {}", delayMs);
				Thread.sleep(delayMs);
			}
			return new Status().status("OK");
		}).when(mockApi).postOnboardxApps(any(Descriptor.class));
		doAnswer(inv -> {
			if (delayMs > 0) {
				logger.debug("postOnboardxAppsDownload sleeping {}", delayMs);
				Thread.sleep(delayMs);
			}
			return new Status().status("OK");
		}).when(mockApi).postOnboardxAppsDownload(any(DescriptorRemote.class));
		return mockApi;
	}

	@Bean
	// Must use the same name as the non-mock configuration
	public XappOnboarderApiBuilder xappOnboarderApiBuilder() throws IOException {
		final XappOnboarderApiBuilder mockBuilder = mock(XappOnboarderApiBuilder.class);
		final HealthApi mockHealthApi = healthApi();
		when(mockBuilder.getHealthApi(any(String.class))).thenReturn(mockHealthApi);
		final ChartsApi mockChartsApi = chartsApi();
		when(mockBuilder.getChartsApi(any(String.class))).thenReturn(mockChartsApi);
		final OnboardApi mockOnboardApi = onboardApi();
		when(mockBuilder.getOnboardApi(any(String.class))).thenReturn(mockOnboardApi);
		return mockBuilder;
	}

}
