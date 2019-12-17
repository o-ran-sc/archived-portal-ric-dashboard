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
 * Creates mock implementations of Kubernetes clients that answer requests with
 * sample data read from the filesystem.
 */
@Configuration
@Profile("test")
public class CaasIngressMockConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	// Simulate remote method delay for UI testing
	@Value("${mock.config.delay:0}")
	private int delayMs;

	private final String pltPods;

	public CaasIngressMockConfiguration() throws IOException {
		logger.info("Configuring mock CAAS-Ingres clients");
		// Files in src/test/resources
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
	public SimpleKubernetesClient ciPltApi() throws IOException {
		SimpleKubernetesClient mockClient = mock(SimpleKubernetesClient.class);
		doAnswer(inv -> {
			String ns = inv.<String>getArgument(0);
			logger.debug("listPods for namespace {}", ns);
			if ("ricplt".equals(ns))
				return pltPods;
			else
				throw new Exception("Fake server failure");
		}).when(mockClient).listPods(any(String.class));
		return mockClient;
	}

}
