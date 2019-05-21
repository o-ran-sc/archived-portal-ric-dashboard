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
package org.oransc.ric.portal.dashboard.config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.invoke.MethodHandles;

import org.oransc.ric.xappmgr.client.api.HealthApi;
import org.oransc.ric.xappmgr.client.api.XappApi;
import org.oransc.ric.xappmgr.client.invoker.ApiClient;
import org.oransc.ric.xappmgr.client.model.AllXapps;
import org.oransc.ric.xappmgr.client.model.SubscriptionRequest;
import org.oransc.ric.xappmgr.client.model.SubscriptionResponse;
import org.oransc.ric.xappmgr.client.model.XAppInfo;
import org.oransc.ric.xappmgr.client.model.Xapp;
import org.oransc.ric.xappmgr.client.model.Xapp.StatusEnum;
import org.oransc.ric.xappmgr.client.model.XappInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;

/**
 * Creates an implementation of the xApp manager client that answers requests
 * with mock data.
 */
@Profile("mock")
@Configuration
public class XappManagerMockConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final AllXapps allXapps;

	public XappManagerMockConfiguration() {
		logger.info("Configuring mock xApp Manager");
		Xapp ac = new Xapp().name("Admission Control").version("v3").status(StatusEnum.FAILED);
		ac.addInstancesItem(
				new XappInstance().name("cdef-3456").ip("3.4.5.6").port(200).status(XappInstance.StatusEnum.RUNNING));
		Xapp an = new Xapp().name("ANR Control").version("v0").status(StatusEnum.SUPERSEDED);
		an.addInstancesItem(
				new XappInstance().name("fedc-8765").ip("8.7.6.5").port(400).status(XappInstance.StatusEnum.RUNNING));
		Xapp dc = new Xapp().name("Dual Connectivity").version("v2").status(StatusEnum.DELETED);
		dc.addInstancesItem(
				new XappInstance().name("def0-6789").ip("6.7.8.9").port(300).status(XappInstance.StatusEnum.COMPLETED));
		allXapps = new AllXapps();
		allXapps.add(ac);
		allXapps.add(an);
		allXapps.add(dc);
	}

	@Bean
	public HealthApi xappHealthMockApi() {
		ApiClient mockClient = mock(ApiClient.class);
		when(mockClient.getStatusCode()).thenReturn(HttpStatus.OK);
		HealthApi mockApi = mock(HealthApi.class);
		when(mockApi.getApiClient()).thenReturn(mockClient);
		doAnswer(i -> {
			return null;
		}).when(mockApi).getHealthAlive();
		doAnswer(i -> {
			return null;
		}).when(mockApi).getHealthReady();
		return mockApi;
	}

	@Bean
	public XappApi xappMgrMockApi() {
		ApiClient mockClient = mock(ApiClient.class);
		when(mockClient.getStatusCode()).thenReturn(HttpStatus.OK);

		XappApi mockApi = mock(XappApi.class);
		when(mockApi.getApiClient()).thenReturn(mockClient);

		SubscriptionResponse subRes = new SubscriptionResponse().eventType(SubscriptionResponse.EventTypeEnum.ALL)
				.id("subid").version(1);
		when(mockApi.addSubscription(any(SubscriptionRequest.class))).thenReturn(subRes);

		doAnswer(i -> {
			return null;
		}).when(mockApi).deleteSubscription(any(String.class));

		when(mockApi.deployXapp(any(XAppInfo.class))).thenReturn(new Xapp());

		when(mockApi.getAllXapps()).thenReturn(allXapps);

		Xapp xappByName = new Xapp().name("name").status(StatusEnum.UNKNOWN).version("v1");
		when(mockApi.getXappByName(any(String.class))).thenReturn(xappByName);

		doAnswer(i -> {
			return null;
		}).when(mockApi).undeployXapp(any(String.class));

		return mockApi;
	}

}
