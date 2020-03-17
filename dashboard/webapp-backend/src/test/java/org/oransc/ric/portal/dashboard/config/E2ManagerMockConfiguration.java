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

import org.oransc.ricplt.e2mgr.client.api.HealthCheckApi;
import org.oransc.ricplt.e2mgr.client.api.NodebApi;
import org.oransc.ricplt.e2mgr.client.invoker.ApiClient;
import org.oransc.ricplt.e2mgr.client.model.GetNodebResponse;
import org.oransc.ricplt.e2mgr.client.model.NodebIdentity;
import org.oransc.ricplt.e2mgr.client.model.NodebIdentityGlobalNbId;
import org.oransc.ricplt.e2mgr.client.model.ResetRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

	public static final String RAN_NAME_1 = "Connected-RAN";
	public static final String RAN_NAME_2 = "Unknown-RAN";

	// Simulate remote method delay for UI testing
	private int delayMs;

	@Autowired
	public E2ManagerMockConfiguration(@Value("${mock.config.delay:0}") int delayMs) {
		logger.debug("ctor: configured with delay {}", delayMs);
		this.delayMs = delayMs;
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

	/**
	 * Builds a mock NodebApi object.
	 * 
	 * @param instanceKey
	 *                        RIC instance
	 * @return Object that returns instance-specific results
	 */
	private NodebApi nodebApi(String instanceKey) {

		final NodebIdentityGlobalNbId globalNbId = new NodebIdentityGlobalNbId().nbId("mockNbId-" + instanceKey)
				.plmnId("mockPlmId");
		final List<NodebIdentity> nodebIdList = new ArrayList<>();
		final Map<String, GetNodebResponse> nodebResponseMap = new HashMap<>();
		// Complete entry
		nodebIdList.add(new NodebIdentity().inventoryName(RAN_NAME_1).globalNbId(globalNbId));
		nodebResponseMap.put(RAN_NAME_1, new GetNodebResponse().connectionStatus("CONNECTED").failureType("")
				.ip("127.0.0.1").nodeType("mockNodeType").port(123).ranName(RAN_NAME_1));
		// Partial entry
		// [{"nodebIdentity":{"globalNbId":null,"inventoryName":"AAAA123456"},
		// "nodebStatus":{"connectionStatus":"CONNECTING","enb":null,"failureType":null,
		// "globalNbId":null,"gnb":null,"ip":"10.2.0.6","nodeType":null,"port":36444,
		// "ranName":"AAAA123456","setupFailure":null}}]
		nodebIdList.add(new NodebIdentity().inventoryName(RAN_NAME_1).globalNbId(globalNbId));
		nodebResponseMap.put(RAN_NAME_1,
				new GetNodebResponse().connectionStatus("CONNECTING").ip("127.0.0.1").port(456).ranName(RAN_NAME_2).nodeType("ENDC").port(100));
		nodebIdList.add(new NodebIdentity().inventoryName(RAN_NAME_2).globalNbId(globalNbId));
		nodebResponseMap.put(RAN_NAME_2,
				new GetNodebResponse().connectionStatus("CONNECTED").ip("127.0.0.2").port(456).ranName(RAN_NAME_2).nodeType("X2").port(200));

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
		return mockApi;
	}

	@Bean
	// Must use the same name as the non-mock configuration
	public E2ManagerApiBuilder e2ManagerApiBuilder() {
		final E2ManagerApiBuilder mockBuilder = mock(E2ManagerApiBuilder.class);
		final HealthCheckApi mockHealthCheckApi = healthCheckApi();
		when(mockBuilder.getHealthCheckApi(any(String.class))).thenReturn(mockHealthCheckApi);
		for (final String key : RICInstanceMockConfiguration.INSTANCE_KEYS) {
			final NodebApi mockNodebApi = nodebApi(key);
			when(mockBuilder.getNodebApi(key)).thenReturn(mockNodebApi);
		}
		return mockBuilder;
	}

}
