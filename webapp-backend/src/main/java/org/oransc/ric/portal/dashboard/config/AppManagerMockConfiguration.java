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

import org.oransc.ric.plt.appmgr.client.api.HealthApi;
import org.oransc.ric.plt.appmgr.client.api.XappApi;
import org.oransc.ric.plt.appmgr.client.invoker.ApiClient;
import org.oransc.ric.plt.appmgr.client.model.AllDeployableXapps;
import org.oransc.ric.plt.appmgr.client.model.AllDeployedXapps;
import org.oransc.ric.plt.appmgr.client.model.AllXappConfig;
import org.oransc.ric.plt.appmgr.client.model.ConfigMetadata;
import org.oransc.ric.plt.appmgr.client.model.SubscriptionRequest;
import org.oransc.ric.plt.appmgr.client.model.SubscriptionResponse;
import org.oransc.ric.plt.appmgr.client.model.XAppConfig;
import org.oransc.ric.plt.appmgr.client.model.XAppInfo;
import org.oransc.ric.plt.appmgr.client.model.Xapp;
import org.oransc.ric.plt.appmgr.client.model.Xapp.StatusEnum;
import org.oransc.ric.plt.appmgr.client.model.XappInstance;
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
public class AppManagerMockConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final AllDeployableXapps availXapps;
	private final AllDeployedXapps deployedXapps;
	private final AllXappConfig allXappConfigs;

	public AppManagerMockConfiguration() {
		logger.info("Configuring mock xApp Manager");
		final String[] appNames = { "AdmissionControl", "Automatic Neighbor Relation", "Dual Connectivity" };
		final String configJson = " { \"config\" : \"example\" }";
		final String descriptorJson = " { \"descriptor\" : \"example\" }";
		allXappConfigs = new AllXappConfig();
		availXapps = new AllDeployableXapps();
		deployedXapps = new AllDeployedXapps();
		for (String n : appNames) {
			ConfigMetadata metadata = new ConfigMetadata().configName("config-" + n).name(n).namespace("namespace");
			XAppConfig config = new XAppConfig().config(configJson).descriptor(descriptorJson).metadata(metadata);
			allXappConfigs.add(config);
			availXapps.add(n);
			Xapp xapp = new Xapp().name(n).version("version").status(StatusEnum.UNKNOWN);
			xapp.addInstancesItem(new XappInstance().name("abcd-1234").ip("1.2.3.4").port(200)
					.status(XappInstance.StatusEnum.RUNNING));
			deployedXapps.add(xapp);
		}
	}

	@Bean
	// Use the same name as regular configuration
	public HealthApi xappMgrHealthApi() {
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
	// Use the same name as regular configuration
	public XappApi xappMgrXappApi() {
		ApiClient mockClient = mock(ApiClient.class);
		when(mockClient.getStatusCode()).thenReturn(HttpStatus.OK);

		XappApi mockApi = mock(XappApi.class);
		when(mockApi.getApiClient()).thenReturn(mockClient);

		when(mockApi.getAllXappConfig()).thenReturn(allXappConfigs);

		when(mockApi.createXappConfig(any(XAppConfig.class))).thenReturn(allXappConfigs.get(0));

		when(mockApi.modifyXappConfig(any(XAppConfig.class))).thenReturn(allXappConfigs.get(0));

		doAnswer(i -> {
			return null;
		}).when(mockApi).deleteXappConfig(any(ConfigMetadata.class));

		when(mockApi.deployXapp(any(XAppInfo.class))).thenReturn(deployedXapps.get(0));

		when(mockApi.listAllXapps()).thenReturn(availXapps);

		when(mockApi.getAllXapps()).thenReturn(deployedXapps);

		when(mockApi.getXappByName(any(String.class))).thenReturn(deployedXapps.get(0));

		doAnswer(i -> {
			return null;
		}).when(mockApi).undeployXapp(any(String.class));

		SubscriptionResponse subRes = new SubscriptionResponse().eventType(SubscriptionResponse.EventTypeEnum.ALL)
				.id("subid").version(1);
		when(mockApi.addSubscription(any(SubscriptionRequest.class))).thenReturn(subRes);

		doAnswer(i -> {
			return null;
		}).when(mockApi).deleteSubscription(any(String.class));

		return mockApi;
	}

}
