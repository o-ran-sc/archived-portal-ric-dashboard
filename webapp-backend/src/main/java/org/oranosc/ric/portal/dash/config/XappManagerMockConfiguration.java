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
package org.oranosc.ric.portal.dash.config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.oranosc.ric.xappmgr.client.api.DefaultApi;
import org.oranosc.ric.xappmgr.client.invoker.ApiClient;
import org.oranosc.ric.xappmgr.client.model.AllXapps;
import org.oranosc.ric.xappmgr.client.model.SubscriptionRequest;
import org.oranosc.ric.xappmgr.client.model.SubscriptionResponse;
import org.oranosc.ric.xappmgr.client.model.XAppInfo;
import org.oranosc.ric.xappmgr.client.model.Xapp;
import org.oranosc.ric.xappmgr.client.model.Xapp.StatusEnum;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;

/**
 * Creates an implementation of the xApp manager client that answers requests
 * with mock data.
 */
@Profile("mock")
@Configuration
public class XappManagerMockConfiguration {

	private final AllXapps allXapps;
	
	public XappManagerMockConfiguration() {
		allXapps = new AllXapps();
		allXapps.add(new Xapp().name("Pendulum Control").version("v1").status(StatusEnum.DEPLOYED));
		allXapps.add(new Xapp().name("Dual Connectivity").version("v2").status(StatusEnum.DELETED));
		allXapps.add(new Xapp().name("Admission Control").version("v3").status(StatusEnum.FAILED));
		allXapps.add(new Xapp().name("ANR Control").version("v0").status(StatusEnum.SUPERSEDED));
	}

	@Bean
	@Primary
	public DefaultApi xappManagerMockClient() {
		ApiClient mockClient = mock(ApiClient.class);
		when(mockClient.getStatusCode()).thenReturn(HttpStatus.OK);

		DefaultApi mockApi = mock(DefaultApi.class);
		when(mockApi.getApiClient()).thenReturn(mockClient);

		SubscriptionResponse subRes = new SubscriptionResponse().eventType(SubscriptionResponse.EventTypeEnum.ALL)
				.id("subid").version(1);
		when(mockApi.addSubscription(any(SubscriptionRequest.class))).thenReturn(subRes);

		doAnswer(i -> {
			return null;
		}).when(mockApi).deleteSubscription(any(Integer.class));

		when(mockApi.deployXapp(any(XAppInfo.class))).thenReturn(new Xapp());
		
		when(mockApi.getAllXapps()).thenReturn(allXapps);
		
		doAnswer(i -> {
			return null;
		}).when(mockApi).getHealth();

		Xapp xappByName = new Xapp().name("name").status(StatusEnum.UNKNOWN).version("v1");
		when(mockApi.getXappByName(any(String.class))).thenReturn(xappByName);

		doAnswer(i -> {
			return null;
		}).when(mockApi).undeployXapp(any(String.class));

		return mockApi;
	}

}
