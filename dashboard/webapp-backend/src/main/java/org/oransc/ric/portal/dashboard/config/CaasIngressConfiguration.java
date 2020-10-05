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

import java.lang.invoke.MethodHandles;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import org.oransc.ric.portal.dashboard.model.RicRegionList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Creates instances of CAAS-Ingres clients.
 */
@Configuration
@Profile("!test")
public class CaasIngressConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	// Populated by the autowired constructor
	private final String urlSuffix;
	private final RicRegionList instanceConfig;

	@Autowired
	public CaasIngressConfiguration(@Value("${caasingress.plt.url.suffix}") final String pltUrlSuffix, //
			@Value("${caasingress.insecure}") final Boolean insecureFlag, //
			final RicRegionList instanceConfig) throws KeyManagementException, NoSuchAlgorithmException {
		logger.debug("ctor: suffix {} insecure flag {}", pltUrlSuffix, insecureFlag);
		this.urlSuffix = pltUrlSuffix;
		this.instanceConfig = instanceConfig;
	}

	@Bean
	// The bean (method) name must be globally unique
	public SimpleKubernetesClientBuilder simpleKubernetesClientBuilder() {
		return new SimpleKubernetesClientBuilder(instanceConfig, urlSuffix);
	}

}
