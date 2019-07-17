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
package org.oransc.ric.portal.dashboard.test.config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

import org.oransc.ric.e2mgr.client.api.HealthCheckApi;
import org.oransc.ric.e2mgr.client.api.NodebApi;
import org.oransc.ric.e2mgr.client.invoker.ApiClient;
import org.oransc.ric.e2mgr.client.model.GetNodebResponse;
import org.oransc.ric.e2mgr.client.model.NodebIdentity;
import org.oransc.ric.e2mgr.client.model.NodebIdentityGlobalNbId;
import org.oransc.ric.e2mgr.client.model.SetupRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;

/**
 * Creates a mock implementation of the E2 Manager client API.
 */
@Profile("test")
@Configuration
public class E2ManagerMockConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final List<NodebIdentity> nodebIdList;
	private final GetNodebResponse nodebResponse;
	// Simulate remote method delay for UI testing
	private final int delayMs = 500;

	public E2ManagerMockConfiguration() {
		logger.info("Configuring mock E2 Manager");
		NodebIdentityGlobalNbId globalNbId = new NodebIdentityGlobalNbId().nbId("mockNbId").plmnId("mockPlmId");
		NodebIdentity nbid = new NodebIdentity().inventoryName("mockInvName").globalNbId(globalNbId);
		nodebIdList = new ArrayList<>();
		nodebIdList.add(nbid);
		nodebResponse = new GetNodebResponse().connectionStatus("mockConnectionStatus").failureType("mockFailureType")
				.ip("127.0.0.1").nodeType("mockNodeType").port(123).ranName("mockRanName");
	}

	private ApiClient apiClient() {
		ApiClient mockClient = mock(ApiClient.class);
		when(mockClient.getStatusCode()).thenReturn(HttpStatus.OK);
		return mockClient;
	}

	@Bean
	// Use the same name as regular configuration
	public HealthCheckApi e2MgrHealthCheckApi() {
		ApiClient apiClient = apiClient();
		HealthCheckApi mockApi = mock(HealthCheckApi.class);
		when(mockApi.getApiClient()).thenReturn(apiClient);
		doAnswer(i -> null).when(mockApi).healthGet();
		return mockApi;
	}

	@Bean
	// Use the same name as regular configuration
	public NodebApi e2MgrNodebApi() {
		ApiClient apiClient = apiClient();
		NodebApi mockApi = mock(NodebApi.class);
		when(mockApi.getApiClient()).thenReturn(apiClient);
		doAnswer(inv -> {
			logger.debug("nodebDelete sleeping {}", delayMs);
			Thread.sleep(delayMs);
			return null;
		}).when(mockApi).nodebDelete();
		doAnswer(inv -> {
			logger.debug("getNb sleeping {}", delayMs);
			Thread.sleep(delayMs);
			return nodebResponse;
		}).when(mockApi).getNb(any(String.class));
		doAnswer(inv -> {
			logger.debug("getNodebIdList sleeping {}", delayMs);
			Thread.sleep(delayMs);
			return nodebIdList;
		}).when(mockApi).getNodebIdList();
		doAnswer(inv -> {
			logger.debug("endcSetup sleeping {}", delayMs);
			Thread.sleep(delayMs);
			return null;
		}).when(mockApi).endcSetup(any(SetupRequest.class));
		doAnswer(inv -> {
			logger.debug("x2Setup sleeping {}", delayMs);
			Thread.sleep(delayMs);
			return null;
		}).when(mockApi).x2Setup(any(SetupRequest.class));
		return mockApi;
	}

}
