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
package org.oransc.ric.portal.dashboard.test.controller;

import java.lang.invoke.MethodHandles;
import java.net.URI;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.oransc.ric.anrxapp.client.model.GgNodeBTable;
import org.oransc.ric.anrxapp.client.model.NeighborCellRelationMod;
import org.oransc.ric.anrxapp.client.model.NeighborCellRelationTable;
import org.oransc.ric.portal.dashboard.controller.AnrXappController;
import org.oransc.ric.portal.dashboard.model.SuccessTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class AnrXappControllerTest extends AbstractControllerTest {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Test
	public void versionTest() {
		URI uri = buildUri(null, AnrXappController.CONTROLLER_PATH, AnrXappController.VERSION_METHOD);
		logger.info("Invoking {}", uri);
		SuccessTransport st = restTemplate.getForObject(uri, SuccessTransport.class);
		Assertions.assertFalse(st.getData().toString().isEmpty());
	}

	@Test
	public void healthAliveTest() {
		URI uri = buildUri(null, AnrXappController.CONTROLLER_PATH, AnrXappController.HEALTH_ALIVE_METHOD);
		logger.info("Invoking {}", uri);
		ResponseEntity<Void> voidResponse = restTemplate.getForEntity(uri, Void.class);
		Assertions.assertTrue(voidResponse.getStatusCode().is2xxSuccessful());
	}

	@Test
	public void healthReadyTest() {
		URI uri = buildUri(null, AnrXappController.CONTROLLER_PATH, AnrXappController.HEALTH_READY_METHOD);
		logger.info("Invoking {}", uri);
		ResponseEntity<Void> voidResponse = restTemplate.getForEntity(uri, Void.class);
		Assertions.assertTrue(voidResponse.getStatusCode().is2xxSuccessful());
	}

	@Test
	public void gnodebsTest() {
		URI uri = buildUri(null, AnrXappController.CONTROLLER_PATH, AnrXappController.GNODEBS_METHOD);
		logger.info("Invoking {}", uri);
		GgNodeBTable list = restTemplate.getForObject(uri, GgNodeBTable.class);
		Assertions.assertFalse(list.getGNodeBIds().isEmpty());
	}

	@Test
	public void ncrtGetTest() {
		URI uri = buildUri(null, AnrXappController.CONTROLLER_PATH, AnrXappController.NCRT_METHOD);
		logger.info("Invoking {}", uri);
		NeighborCellRelationTable table = restTemplate.getForObject(uri, NeighborCellRelationTable.class);
		Assertions.assertFalse(table.getNcrtRelations().isEmpty());
	}

	@Test
	public void ncrtPutTest() {
		URI uri = buildUri(null, AnrXappController.CONTROLLER_PATH, AnrXappController.NCRT_METHOD,
				AnrXappController.PP_SERVING, "serving", AnrXappController.PP_NEIGHBOR, "neighbor");
		logger.info("Invoking {}", uri);
		HttpEntity<NeighborCellRelationMod> entity = new HttpEntity<>(new NeighborCellRelationMod());
		ResponseEntity<Void> voidResponse = restTemplate.exchange(uri, HttpMethod.PUT, entity, Void.class);
		Assertions.assertTrue(voidResponse.getStatusCode().is2xxSuccessful());
	}

	@Test
	public void ncrtDeleteTest() {
		URI uri = buildUri(null, AnrXappController.CONTROLLER_PATH, AnrXappController.NCRT_METHOD,
				AnrXappController.PP_SERVING, "serving", AnrXappController.PP_NEIGHBOR, "neighbor");
		logger.info("Invoking {}", uri);
		ResponseEntity<Void> voidResponse = restTemplate.exchange(uri, HttpMethod.DELETE, null, Void.class);
		Assertions.assertTrue(voidResponse.getStatusCode().is2xxSuccessful());
	}

}
