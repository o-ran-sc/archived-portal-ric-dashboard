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

import org.oransc.ric.anrxapp.client.api.HealthApi;
import org.oransc.ric.anrxapp.client.api.NcrtApi;
import org.oransc.ric.anrxapp.client.invoker.ApiClient;
import org.oransc.ric.anrxapp.client.model.NeighborCellRelation;
import org.oransc.ric.anrxapp.client.model.NeighborCellRelationDelTable;
import org.oransc.ric.anrxapp.client.model.NeighborCellRelationModTable;
import org.oransc.ric.anrxapp.client.model.NeighborCellRelationTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;

/**
 * Creates a mock implementation of the ANR xApp client APIs.
 */
@Profile("mock")
@Configuration
public class AnrXappMockConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public AnrXappMockConfiguration() {
		logger.info("Configuring mock ANR xApp client");
	}

	private ApiClient apiClient() {
		ApiClient mockClient = mock(ApiClient.class);
		when(mockClient.getStatusCode()).thenReturn(HttpStatus.OK);
		return mockClient;
	}

	@Bean
	public HealthApi anrHealthMockApi() {
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
	public NcrtApi ncrtPapi() {
		ApiClient apiClient = apiClient();
		NcrtApi mockApi = mock(NcrtApi.class);
		when(mockApi.getApiClient()).thenReturn(apiClient);

		NeighborCellRelation a = new NeighborCellRelation().cellIdentifierNrcgi("A12345").neighborCellNrpci("A123456")
				.neighborCellNrcgi("A12347").flagNoHo(true).flagNoXn(true).flagNoRemove(true);
		NeighborCellRelation e = new NeighborCellRelation().cellIdentifierNrcgi("E12345").neighborCellNrpci("E123456")
				.neighborCellNrcgi("E12347").flagNoHo(true).flagNoXn(true).flagNoRemove(true);
		NeighborCellRelationTable ncrt = new NeighborCellRelationTable().addNcrtRelationsItem(a)
				.addNcrtRelationsItem(e);

		when(mockApi.getNcrtInfo(any(String.class), any(String.class), any(Integer.class))).thenReturn(ncrt);
		when(mockApi.getCellNcrtInfo(any(String.class), any(String.class), any(String.class), any(String.class),
				any(Integer.class))).thenReturn(ncrt);

		doAnswer(i -> {
			return null;
		}).when(mockApi).deleteNcrt(any(String.class), any(NeighborCellRelationDelTable.class), any(String.class),
				any(String.class));

		doAnswer(i -> {
			return null;
		}).when(mockApi).modifyNcrt(any(String.class), any(NeighborCellRelationModTable.class), any(String.class),
				any(String.class));

		return mockApi;
	}

}
