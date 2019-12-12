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
package org.oransc.ric.portal.dashboard.config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.oransc.ric.e2mgr.client.api.HealthCheckApi;
import org.oransc.ric.e2mgr.client.api.NodebApi;
import org.oransc.ric.e2mgr.client.invoker.ApiClient;
import org.oransc.ric.e2mgr.client.model.GetNodebResponse;
import org.oransc.ric.e2mgr.client.model.NodebIdentity;
import org.oransc.ric.e2mgr.client.model.NodebIdentityGlobalNbId;
import org.oransc.ric.e2mgr.client.model.ResetRequest;
import org.oransc.ric.e2mgr.client.model.SetupRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;

/**
 * Creates a mock implementation of the E2 Manager client API.
 */
@Configuration
@Profile("test")
public class E2ManagerMockConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	// Simulate remote method delay for UI testing
	@Value("${mock.config.delay:0}")
	private int delayMs;

	public static final String RAN_NAME_1 = "Connected-RAN";
	public static final String RAN_NAME_2 = "Unknown-RAN";

	private final List<NodebIdentity> nodebIdList;
	private final Map<String, GetNodebResponse> nodebResponseMap;
	private final NodebIdentityGlobalNbId globalNbId;

	public E2ManagerMockConfiguration() {
		logger.info("Configuring mock E2 Manager");
		globalNbId = new NodebIdentityGlobalNbId().nbId("mockNbId").plmnId("mockPlmId");
		nodebIdList = new ArrayList<>();
		nodebResponseMap = new HashMap<>();
		// Complete entry
		nodebIdList.add(new NodebIdentity().inventoryName(RAN_NAME_1).globalNbId(globalNbId));
		nodebResponseMap.put(RAN_NAME_1, new GetNodebResponse().connectionStatus("CONNECTED").failureType("")
				.ip("127.0.0.1").nodeType("mockNodeType").port(123).ranName(RAN_NAME_1));
		// Partial entry
		// [{"nodebIdentity":{"globalNbId":null,"inventoryName":"AAAA123456"},
		// "nodebStatus":{"connectionStatus":"CONNECTING","enb":null,"failureType":null,
		// "globalNbId":null,"gnb":null,"ip":"10.2.0.6","nodeType":null,"port":36444,
		// "ranName":"AAAA123456","setupFailure":null}}]
		nodebIdList.add(new NodebIdentity().inventoryName(RAN_NAME_2));
		nodebResponseMap.put(RAN_NAME_2,
				new GetNodebResponse().connectionStatus("CONNECTING").ip("127.0.0.2").port(456).ranName(RAN_NAME_2));
	}

	private ApiClient apiClient() {
		ApiClient mockClient = mock(ApiClient.class);
		when(mockClient.getStatusCode()).thenReturn(HttpStatus.OK);
		return mockClient;
	}

	private HealthCheckApi healthCheckApi() {
		ApiClient apiClient = apiClient();
		HealthCheckApi mockApi = mock(HealthCheckApi.class);
		when(mockApi.getApiClient()).thenReturn(apiClient);
		doAnswer(i -> null).when(mockApi).healthGet();
		return mockApi;
	}

	private NodebApi nodebApi() {
		ApiClient apiClient = apiClient();
		NodebApi mockApi = mock(NodebApi.class);
		when(mockApi.getApiClient()).thenReturn(apiClient);
		doAnswer(inv -> {
			if (delayMs > 0) {
				logger.debug("nodebShutdownPut sleeping {}", delayMs);
				Thread.sleep(delayMs);
			}
			nodebIdList.clear();
			return null;
		}).when(mockApi).nodebShutdownPut();
		doAnswer(inv -> {
			if (delayMs > 0) {
				logger.debug("reset sleeping {}", delayMs);
				Thread.sleep(delayMs);
			}
			return null;
		}).when(mockApi).reset(any(String.class), any(ResetRequest.class));
		doAnswer(inv -> {
			if (delayMs > 0) {
				logger.debug("getNb sleeping {}", delayMs);
				Thread.sleep(delayMs);
			}
			String invName = inv.<String>getArgument(0);
			return nodebResponseMap.get(invName);
		}).when(mockApi).getNb(any(String.class));
		doAnswer(inv -> {
			if (delayMs > 0) {
				logger.debug("getNodebIdList sleeping {}", delayMs);
				Thread.sleep(delayMs);
			}
			return nodebIdList;
		}).when(mockApi).getNodebIdList();
		doAnswer(inv -> {
			if (delayMs > 0) {
				logger.debug("endcSetup sleeping {}", delayMs);
				Thread.sleep(delayMs);
			}
			SetupRequest sr = inv.<SetupRequest>getArgument(0);
			nodebIdList.add(new NodebIdentity().inventoryName(sr.getRanName()).globalNbId(globalNbId));
			nodebResponseMap.put(sr.getRanName(),
					new GetNodebResponse().connectionStatus("mockConnectionStatus").failureType("mockFailureType")
							.ip(sr.getRanIp()).nodeType("ENDC").port(sr.getRanPort()).ranName(sr.getRanName()));
			return null;
		}).when(mockApi).endcSetup(any(SetupRequest.class));
		doAnswer(inv -> {
			if (delayMs > 0) {
				logger.debug("x2Setup sleeping {}", delayMs);
				Thread.sleep(delayMs);
			}
			SetupRequest sr = inv.<SetupRequest>getArgument(0);
			nodebIdList.add(new NodebIdentity().inventoryName(sr.getRanName()).globalNbId(globalNbId));
			nodebResponseMap.put(sr.getRanName(),
					new GetNodebResponse().connectionStatus("mockConnectionStatus").failureType("mockFailureType")
							.ip(sr.getRanIp()).nodeType("X2").port(sr.getRanPort()).ranName(sr.getRanName()));
			return null;
		}).when(mockApi).x2Setup(any(SetupRequest.class));
		return mockApi;
	}

	@Bean
	// Must use the same name as the non-mock configuration
	public E2ManagerApiBuilder e2ManagerApiBuilder() {
		final E2ManagerApiBuilder mockBuilder = mock(E2ManagerApiBuilder.class);
		final HealthCheckApi mockHealthCheckApi = healthCheckApi();
		when(mockBuilder.getHealthCheckApi(any(String.class))).thenReturn(mockHealthCheckApi);
		final NodebApi mockNodebApi = nodebApi();
		when(mockBuilder.getNodebApi(any(String.class))).thenReturn(mockNodebApi);
		return mockBuilder;
	}

}
