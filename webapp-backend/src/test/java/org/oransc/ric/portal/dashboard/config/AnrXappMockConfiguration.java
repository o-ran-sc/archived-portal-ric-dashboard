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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.invoke.MethodHandles;

import org.oransc.ric.anrxapp.client.api.HealthApi;
import org.oransc.ric.anrxapp.client.api.NcrtApi;
import org.oransc.ric.anrxapp.client.invoker.ApiClient;
import org.oransc.ric.anrxapp.client.model.GgNodeBTable;
import org.oransc.ric.anrxapp.client.model.NeighborCellRelation;
import org.oransc.ric.anrxapp.client.model.NeighborCellRelationMod;
import org.oransc.ric.anrxapp.client.model.NeighborCellRelationTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;

/**
 * Creates a mock implementation of the ANR xApp client APIs.
 */
@Profile("test")
@Configuration
public class AnrXappMockConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	// Simulate remote method delay for UI testing
	@Value("${mock.config.delay:0}")
	private int delayMs;

	private static final String GNODEB1 = "001EF5:0045FE50";
	private static final String GNODEB2 = "001EF6:0045FE51";
	private static final String GNODEB3 = "001EF7:0045FE52";

	// Sonar wants separate declarations
	private final NeighborCellRelationTable ncrt;
	private final NeighborCellRelationTable ncrtNodeB1;
	private final NeighborCellRelationTable ncrtNodeB2;
	private final NeighborCellRelationTable ncrtNodeB3;
	private final GgNodeBTable gNodebTable;

	public AnrXappMockConfiguration() {
		logger.info("Configuring mock ANR xApp client");
		gNodebTable = new GgNodeBTable();
		gNodebTable.addGNodeBIdsItem(GNODEB1).addGNodeBIdsItem(GNODEB2).addGNodeBIdsItem(GNODEB3);
		ncrtNodeB1 = new NeighborCellRelationTable();
		ncrtNodeB2 = new NeighborCellRelationTable();
		ncrtNodeB3 = new NeighborCellRelationTable();
		ncrt = new NeighborCellRelationTable();
		String[] neighbors1 = { "1104", "1105", "1106" };
		for (String n : neighbors1)
			ncrtNodeB1.addNcrtRelationsItem(
					new NeighborCellRelation().servingCellNrcgi(GNODEB1 + ":1100").neighborCellNrpci(n)
							.neighborCellNrcgi(GNODEB1 + ":" + n).flagNoHo(true).flagNoXn(true).flagNoRemove(true));
		String[] neighbors2 = { "1471", "1472", "1473" };
		for (String n : neighbors2)
			ncrtNodeB2.addNcrtRelationsItem(
					new NeighborCellRelation().servingCellNrcgi(GNODEB2 + ":1400").neighborCellNrpci(n)
							.neighborCellNrcgi(GNODEB2 + ":" + n).flagNoHo(false).flagNoXn(false).flagNoRemove(false));
		String[] neighbors3 = { "3601", "3601", "3602" };
		for (String n : neighbors3)
			ncrtNodeB3.addNcrtRelationsItem(
					new NeighborCellRelation().servingCellNrcgi(GNODEB3 + ":3600").neighborCellNrpci(n)
							.neighborCellNrcgi(GNODEB3 + ":" + n).flagNoHo(true).flagNoXn(true).flagNoRemove(true));
		for (NeighborCellRelation ncr : ncrtNodeB1.getNcrtRelations())
			ncrt.addNcrtRelationsItem(ncr);
		for (NeighborCellRelation ncr : ncrtNodeB2.getNcrtRelations())
			ncrt.addNcrtRelationsItem(ncr);
		for (NeighborCellRelation ncr : ncrtNodeB3.getNcrtRelations())
			ncrt.addNcrtRelationsItem(ncr);
	}

	private ApiClient apiClient() {
		ApiClient mockClient = mock(ApiClient.class);
		when(mockClient.getStatusCode()).thenReturn(HttpStatus.OK);
		return mockClient;
	}

	@Bean
	// Use the same name as regular configuration
	public HealthApi anrHealthApi() {
		ApiClient apiClient = apiClient();
		HealthApi mockApi = mock(HealthApi.class);
		when(mockApi.getApiClient()).thenReturn(apiClient);
		doAnswer(i -> null).when(mockApi).getHealthAlive();
		doAnswer(i -> null).when(mockApi).getHealthReady();
		return mockApi;
	}

	@Bean
	// Use the same name as regular configuration
	public NcrtApi anrNcrtApi() {
		ApiClient apiClient = apiClient();
		NcrtApi mockApi = mock(NcrtApi.class);
		when(mockApi.getApiClient()).thenReturn(apiClient);
		doAnswer(inv -> {
			if (delayMs > 0) {
				logger.debug("getgNodeB sleeping {}", delayMs);
				Thread.sleep(delayMs);
			}
			return gNodebTable;
		}).when(mockApi).getgNodeB();
		// Swagger sends nulls; front end sends empty strings
		doAnswer(inv -> {
			if (delayMs > 0) {
				logger.debug("getNcrt (1) sleeping {}", delayMs);
				Thread.sleep(delayMs);
			}
			return ncrt;
		}).when(mockApi).getNcrt((String) isNull(), (String) isNull(), (String) isNull());
		doAnswer(inv -> {
			if (delayMs > 0) {
				logger.debug("getNcrt (2) sleeping {}", delayMs);
				Thread.sleep(delayMs);
			}
			return ncrt;
		}).when(mockApi).getNcrt(eq(""), any(String.class), any(String.class));
		doAnswer(inv -> {
			if (delayMs > 0) {
				logger.debug("getNcrt (3) sleeping {}", delayMs);
				Thread.sleep(delayMs);
			}
			return ncrtNodeB1;
		}).when(mockApi).getNcrt(eq(GNODEB1), any(String.class), any(String.class));
		doAnswer(inv -> {
			if (delayMs > 0) {
				logger.debug("getNcrt (4) sleeping {}", delayMs);
				Thread.sleep(delayMs);
			}
			return ncrtNodeB2;
		}).when(mockApi).getNcrt(eq(GNODEB2), any(String.class), any(String.class));
		doAnswer(inv -> {
			if (delayMs > 0) {
				logger.debug("getNcrt (5) sleeping {}", delayMs);
				Thread.sleep(delayMs);
			}
			return ncrtNodeB3;
		}).when(mockApi).getNcrt(eq(GNODEB3), any(String.class), any(String.class));
		doAnswer(inv -> {
			if (delayMs > 0) {
				logger.debug("deleteNcrt sleeping {}", delayMs);
				Thread.sleep(delayMs);
			}
			String servCellNrcgi = inv.<String>getArgument(0);
			String neighCellNrpci = inv.<String>getArgument(1);
			for (NeighborCellRelation ncr : ncrt.getNcrtRelations()) {
				if (servCellNrcgi.equals(ncr.getServingCellNrcgi())
						&& neighCellNrpci.equals(ncr.getNeighborCellNrpci())) {
					logger.debug("deleteNcrt: removing {}", ncr);
					ncrt.getNcrtRelations().remove(ncr);
					break;
				}
			}
			return null;
		}).when(mockApi).deleteNcrt(any(String.class), any(String.class));
		doAnswer(inv -> {
			if (delayMs > 0) {
				logger.debug("modifyNcrt sleeping {}", delayMs);
				Thread.sleep(delayMs);
			}
			String servCellNrcgi = inv.<String>getArgument(0);
			String neighCellNrpci = inv.<String>getArgument(1);
			NeighborCellRelationMod mod = inv.<NeighborCellRelationMod>getArgument(2);
			for (NeighborCellRelation ncr : ncrt.getNcrtRelations()) {
				if (servCellNrcgi.equals(ncr.getServingCellNrcgi())
						&& neighCellNrpci.equals(ncr.getNeighborCellNrpci())) {
					logger.debug("modifyNcrt: modifying {} to {}", ncr, mod);
					ncr.setFlagNoHo(mod.isFlagNoHo());
					ncr.setFlagNoRemove(mod.isFlagNoRemove());
					ncr.setFlagNoXn(mod.isFlagNoXn());
					break;
				}
			}
			return null;
		}).when(mockApi).modifyNcrt(any(String.class), any(String.class), any(NeighborCellRelationMod.class));
		return mockApi;
	}

}
