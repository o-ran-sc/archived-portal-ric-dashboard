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
package org.oransc.ric.portal.dashboard.controller;

import java.lang.invoke.MethodHandles;
import java.net.URI;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.oransc.ric.portal.dashboard.DashboardConstants;
import org.oransc.ric.portal.dashboard.config.RICInstanceMockConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CaasIngressControllerTest extends AbstractControllerTest {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Test
	public void pltTest() {
		final String nsPlt = "ricplt";
		URI uri = buildUri(null, CaasIngressController.CONTROLLER_PATH, DashboardConstants.RIC_INSTANCE_KEY,
				RICInstanceMockConfiguration.INSTANCE_KEY_1, CaasIngressController.PODS_METHOD,
				CaasIngressController.PP_CLUSTER, CaasIngressController.CLUSTER_PLT, CaasIngressController.PP_NAMESPACE,
				nsPlt);
		logger.info("Invoking {}", uri);
		String s = testRestTemplateStandardRole().getForObject(uri, String.class);
		Assertions.assertFalse(s.isEmpty());
		Assertions.assertTrue(s.contains(nsPlt));
	}

	@Test
	public void unknownClusterTest() {
		final String nsPlt = "ricplt";
		URI uri = buildUri(null, CaasIngressController.CONTROLLER_PATH, DashboardConstants.RIC_INSTANCE_KEY,
				RICInstanceMockConfiguration.INSTANCE_KEY_1, CaasIngressController.PODS_METHOD,
				CaasIngressController.PP_CLUSTER, "cluster", CaasIngressController.PP_NAMESPACE, nsPlt);
		logger.info("Invoking {}", uri);
		String s = testRestTemplateStandardRole().getForObject(uri, String.class);
		Assertions.assertFalse(s.isEmpty());
		Assertions.assertTrue(s.contains("unknown"));
	}

	// Unknown namespace triggers a controller exception
	@Test
	public void bogusNsTest() {
		final String ns = "unknown";
		URI uri = buildUri(null, CaasIngressController.CONTROLLER_PATH, DashboardConstants.RIC_INSTANCE_KEY,
				RICInstanceMockConfiguration.INSTANCE_KEY_1, CaasIngressController.PODS_METHOD,
				CaasIngressController.PP_CLUSTER, CaasIngressController.CLUSTER_PLT, CaasIngressController.PP_NAMESPACE,
				ns);
		logger.info("Invoking {}", uri);
		String s = testRestTemplateStandardRole().getForObject(uri, String.class);
		Assertions.assertFalse(s.isEmpty());
		Assertions.assertTrue(s.contains("RIC Dashboard Error"));
	}

}
