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

import java.io.IOException;
import java.lang.invoke.MethodHandles;

import org.oransc.ric.portal.dashboard.TestUtils;
import org.oransc.ric.portal.dashboard.k8sapi.SimpleKubernetesClient;
import org.oransc.ric.portal.dashboard.model.RicRegionList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
	private int delayMs;

	// Autowire all the properties required by the real class
	// (even tho not used here) as a test of the properties.
	@Autowired
	public CaasIngressMockConfiguration(@Value("${caasingress.plt.url.suffix}") final String pltUrlSuffix, //
			@Value("${caasingress.insecure}") final Boolean insecureFlag, //
			final RicRegionList instanceConfig, //
			@Value("${mock.config.delay:0}") int delayMs) {
		logger.info("ctor: configured with suffix {}, insecure {}, instance {}, delay {}", pltUrlSuffix, insecureFlag,
				instanceConfig, delayMs);
		this.delayMs = delayMs;
	}

	private SimpleKubernetesClient simpleKubernetesClient(String instanceKey) throws IOException {
		// File in src/test/resources
		String podFile = RICInstanceMockConfiguration.INSTANCE_KEY_1.equals(instanceKey)
				? "caas-ingress-ricplt-pods-1.json"
				: "caas-ingress-ricplt-pods-2.json";
		String pltPods = TestUtils.readDataFromPath(podFile);
		SimpleKubernetesClient mockClient = mock(SimpleKubernetesClient.class);
		doAnswer(inv -> {
			String ns = inv.<String>getArgument(0);
			logger.debug("listPods for namespace {}", ns);
			if (delayMs > 0) {
				logger.debug("listPods sleeping {}", delayMs);
				Thread.sleep(delayMs);
			}
			if ("ricplt".equals(ns))
				return pltPods;
			else
				throw new IllegalArgumentException("Fake server failure");
		}).when(mockClient).listPods(any(String.class));
		return mockClient;
	}

	@Bean
	// The bean (method) name must be globally unique
	public SimpleKubernetesClientBuilder simpleKubernetesClientBuilder() throws IOException {
		final SimpleKubernetesClientBuilder mockBuilder = mock(SimpleKubernetesClientBuilder.class);
		for (final String key : RICInstanceMockConfiguration.INSTANCE_KEYS) {
			SimpleKubernetesClient client = simpleKubernetesClient(key);
			when(mockBuilder.getSimpleKubernetesClient(key)).thenReturn(client);
		}
		return mockBuilder;
	}

}
