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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.invoke.MethodHandles;

import org.oransc.ric.anrxapp.client.api.GnodebsApi;
import org.oransc.ric.anrxapp.client.api.HealthApi;
import org.oransc.ric.anrxapp.client.api.NcrtApi;
import org.oransc.ric.anrxapp.client.invoker.ApiClient;
import org.oransc.ric.anrxapp.client.model.NeighborCellRelation;
import org.oransc.ric.anrxapp.client.model.NeighborCellRelationMod;
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

	private final NeighborCellRelationTable ncrt, ncrtNodeB1, ncrtNodeB2;

	public AnrXappMockConfiguration() {
		logger.info("Configuring mock ANR xApp client");
		ncrtNodeB1 = new NeighborCellRelationTable();
		ncrtNodeB2 = new NeighborCellRelationTable();
		ncrt = new NeighborCellRelationTable();
		String[] cells1 = { "A", "B", "C", "D" };
		for (String s : cells1)
			ncrtNodeB1.addNcrtRelationsItem(
					new NeighborCellRelation().servingCellNrcgi(s + "12345").neighborCellNrpci(s + "12346")
							.neighborCellNrcgi(s + "12347").flagNoHo(true).flagNoXn(true).flagNoRemove(true));
		String[] cells2 = { "E", "F", "G", "H" };
		for (String s : cells2)
			ncrtNodeB2.addNcrtRelationsItem(
					new NeighborCellRelation().servingCellNrcgi(s + "12345").neighborCellNrpci(s + "12346")
							.neighborCellNrcgi(s + "12347").flagNoHo(true).flagNoXn(true).flagNoRemove(true));
		for (NeighborCellRelation ncr : ncrtNodeB1.getNcrtRelations())
			ncrt.addNcrtRelationsItem(ncr);
		for (NeighborCellRelation ncr : ncrtNodeB2.getNcrtRelations())
			ncrt.addNcrtRelationsItem(ncr);
	}

	private ApiClient apiClient() {
		ApiClient mockClient = mock(ApiClient.class);
		when(mockClient.getStatusCode()).thenReturn(HttpStatus.OK);
		return mockClient;
	}

	@Bean
	public HealthApi anrHealthApi() {
		ApiClient apiClient = apiClient();
		HealthApi mockApi = mock(HealthApi.class);
		when(mockApi.getApiClient()).thenReturn(apiClient);
		doAnswer(i -> {
			return null;
		}).when(mockApi).getHealthAlive();
		doAnswer(i -> {
			return null;
		}).when(mockApi).getHealthReady();
		return mockApi;
	}

	@Bean
	public GnodebsApi anrGnodebsMockApi() {
		ApiClient mockClient = mock(ApiClient.class);
		when(mockClient.getStatusCode()).thenReturn(HttpStatus.OK);
		GnodebsApi mockApi = mock(GnodebsApi.class);

		return mockApi;
	}

	@Bean
	public NcrtApi ncrtMockApi() {
		ApiClient apiClient = apiClient();
		NcrtApi mockApi = mock(NcrtApi.class);
		when(mockApi.getApiClient()).thenReturn(apiClient);
		// Swagger sends nulls; front end sends empty strings
		when(mockApi.getNcrtInfo((String) isNull(), (String) isNull(), (String) isNull())).thenReturn(ncrt);
		when(mockApi.getNcrtInfo(eq(""), any(String.class), any(String.class))).thenReturn(ncrt);
		when(mockApi.getNcrtInfo(startsWith("A"), any(String.class), any(String.class))).thenReturn(ncrtNodeB1);
		when(mockApi.getNcrtInfo(startsWith("B"), any(String.class), any(String.class))).thenReturn(ncrtNodeB2);
		doAnswer(i -> {
			return null;
		}).when(mockApi).deleteNcrt(any(String.class), any(String.class));
		doAnswer(i -> {
			return null;
		}).when(mockApi).modifyNcrt(any(String.class), any(String.class), any(NeighborCellRelationMod.class));
		return mockApi;
	}

}
