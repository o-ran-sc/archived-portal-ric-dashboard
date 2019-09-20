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

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.invoke.MethodHandles;

import org.oransc.ric.portal.dashboard.k8sapi.SimpleKubernetesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Creates an implementation of the Kubernetes client that answers requests with
 * mock data.
 */
@Profile("test")
@Configuration
public class CaasIngressMockConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	// Simulate remote method delay for UI testing
	@Value("${mock.config.delay:0}")
	private int delayMs;

	private final String auxPods;
	private final String pltPods;

	public CaasIngressMockConfiguration() throws IOException {
		logger.info("Configuring mock CAAS-Ingres clients");
		// Files in src/test/resources
		auxPods = readDataFromPath("caas-ingress-ricaux-pods.json");
		pltPods = readDataFromPath("caas-ingress-ricplt-pods.json");
	}

	private String readDataFromPath(String path) throws IOException {
		InputStream is = MethodHandles.lookup().lookupClass().getClassLoader().getResourceAsStream(path);
		if (is == null) {
			String msg = "Failed to find resource on classpath: " + path;
			logger.error(msg);
			throw new RuntimeException(msg);
		}
		InputStreamReader reader = new InputStreamReader(is, "UTF-8");
		StringBuilder sb = new StringBuilder();
		char[] buf = new char[8192];
		int i;
		while ((i = reader.read(buf)) > 0)
			sb.append(buf, 0, i);
		reader.close();
		is.close();
		return sb.toString();
	}

	@Bean
	// Use the same name as regular configuration
	public SimpleKubernetesClient ciAuxApi() throws IOException {
		SimpleKubernetesClient mockClient = mock(SimpleKubernetesClient.class);
		doAnswer(inv -> {
			logger.debug("listPods for aux");
			return auxPods;
		}).when(mockClient).listPods("ricaux");
		return mockClient;
	}

	@Bean
	// Use the same name as regular configuration
	public SimpleKubernetesClient ciPltApi() throws IOException {
		SimpleKubernetesClient mockClient = mock(SimpleKubernetesClient.class);
		doAnswer(inv -> {
			logger.debug("listPods for plt");
			return pltPods;
		}).when(mockClient).listPods("ricplt");
		return mockClient;
	}

}
