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
package org.oransc.ric.portal.dashboard.controller;

import java.lang.invoke.MethodHandles;
import java.net.URI;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.oransc.ric.plt.appmgr.client.model.AllDeployedXapps;
import org.oransc.ric.plt.appmgr.client.model.AllXappConfig;
import org.oransc.ric.plt.appmgr.client.model.ConfigMetadata;
import org.oransc.ric.plt.appmgr.client.model.XAppConfig;
import org.oransc.ric.plt.appmgr.client.model.XAppInfo;
import org.oransc.ric.plt.appmgr.client.model.Xapp;
import org.oransc.ric.portal.dashboard.model.DashboardDeployableXapps;
import org.oransc.ric.portal.dashboard.model.SuccessTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class AppManagerControllerTest extends AbstractControllerTest {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Test
	public void versionTest() {
		URI uri = buildUri(null, AppManagerController.CONTROLLER_PATH, AppManagerController.VERSION_METHOD);
		logger.info("Invoking {}", uri);
		SuccessTransport st = restTemplate.getForObject(uri, SuccessTransport.class);
		Assertions.assertFalse(st.getData().toString().isEmpty());
	}

	@Test
	public void healthAliveTest() {
		URI uri = buildUri(null, AppManagerController.CONTROLLER_PATH, AppManagerController.HEALTH_ALIVE_METHOD);
		logger.info("Invoking {}", uri);
		ResponseEntity<Void> voidResponse = restTemplate.getForEntity(uri, Void.class);
		Assertions.assertTrue(voidResponse.getStatusCode().is2xxSuccessful());
	}

	@Test
	public void healthReadyTest() {
		URI uri = buildUri(null, AppManagerController.CONTROLLER_PATH, AppManagerController.HEALTH_READY_METHOD);
		logger.info("Invoking {}", uri);
		ResponseEntity<Void> voidResponse = restTemplate.getForEntity(uri, Void.class);
		Assertions.assertTrue(voidResponse.getStatusCode().is2xxSuccessful());
	}

	@Test
	public void appListTest() {
		URI uri = buildUri(null, AppManagerController.CONTROLLER_PATH, AppManagerController.XAPPS_LIST_METHOD);
		logger.info("Invoking {}", uri);
		DashboardDeployableXapps apps = testRestTemplateStandardRole().getForObject(uri,
				DashboardDeployableXapps.class);
		Assertions.assertFalse(apps.isEmpty());
	}

	@Test
	public void appStatusesTest() {
		URI uri = buildUri(null, AppManagerController.CONTROLLER_PATH, AppManagerController.XAPPS_METHOD);
		logger.info("Invoking {}", uri);
		AllDeployedXapps apps = testRestTemplateStandardRole().getForObject(uri, AllDeployedXapps.class);
		Assertions.assertFalse(apps.isEmpty());
	}

	@Test
	public void appStatusTest() {
		URI uri = buildUri(null, AppManagerController.CONTROLLER_PATH, AppManagerController.XAPPS_METHOD, "app");
		logger.info("Invoking {}", uri);
		Xapp app = testRestTemplateStandardRole().getForObject(uri, Xapp.class);
		Assertions.assertFalse(app.getName().isEmpty());
	}

	@Test
	public void deployAppTest() {
		URI uri = buildUri(null, AppManagerController.CONTROLLER_PATH, AppManagerController.XAPPS_METHOD);
		logger.info("Invoking {}", uri);
		XAppInfo info = new XAppInfo();
		Xapp app = testRestTemplateAdminRole().postForObject(uri, info, Xapp.class);
		Assertions.assertFalse(app.getName().isEmpty());
	}

	@Test
	public void undeployAppTest() {
		URI uri = buildUri(null, AppManagerController.CONTROLLER_PATH, AppManagerController.XAPPS_METHOD, "app");
		logger.info("Invoking {}", uri);
		ResponseEntity<Void> voidResponse = testRestTemplateAdminRole().exchange(uri, HttpMethod.DELETE, null,
				Void.class);
		Assertions.assertTrue(voidResponse.getStatusCode().is2xxSuccessful());
	}

	@Test
	public void getConfigTest() {
		URI uri = buildUri(null, AppManagerController.CONTROLLER_PATH, AppManagerController.CONFIG_METHOD);
		logger.info("Invoking {}", uri);
		AllXappConfig config = testRestTemplateStandardRole().getForObject(uri, AllXappConfig.class);
		Assertions.assertFalse(config.isEmpty());
	}

	@Test
	public void createConfigTest() {
		URI uri = buildUri(null, AppManagerController.CONTROLLER_PATH, AppManagerController.CONFIG_METHOD);
		logger.info("Invoking {}", uri);
		XAppConfig newConfig = new XAppConfig();
		XAppConfig response = testRestTemplateAdminRole().postForObject(uri, newConfig, XAppConfig.class);
		Assertions.assertNotNull(response.getConfig());
	}

	@Test
	public void deleteConfigTest() {
		URI uri = buildUri(null, AppManagerController.CONTROLLER_PATH, AppManagerController.CONFIG_METHOD, "app");
		logger.info("Invoking {}", uri);
		ConfigMetadata delConfig = new ConfigMetadata();
		HttpEntity<ConfigMetadata> entity = new HttpEntity<>(delConfig);
		ResponseEntity<Void> voidResponse = testRestTemplateAdminRole().exchange(uri, HttpMethod.DELETE, entity,
				Void.class);
		Assertions.assertTrue(voidResponse.getStatusCode().is2xxSuccessful());
	}

}
