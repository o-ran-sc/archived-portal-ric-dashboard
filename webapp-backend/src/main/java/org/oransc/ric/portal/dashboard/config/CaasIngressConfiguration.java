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

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import org.oransc.ric.portal.dashboard.k8sapi.SimpleKubernetesClient;
import org.oransc.ric.portal.dashboard.util.HttpsURLConnectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.web.util.DefaultUriBuilderFactory;

/**
 * Creates instances of CAAS-Ingres clients.
 */
@org.springframework.context.annotation.Configuration
@Profile("!test")
public class CaasIngressConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	// Populated by the autowired constructor
	private final String caasIngressAuxUrl;
	private final String caasIngressPltUrl;

	@Autowired
	public CaasIngressConfiguration( //
			@Value("${caasingress.insecure}") final Boolean insecureFlag, //
			@Value("${caasingress.aux.url.prefix}") final String auxUrlPrefix, //
			@Value("${caasingress.aux.url.suffix}") final String auxUrlSuffix, //
			@Value("${caasingress.plt.url.prefix}") final String pltUrlPrefix,
			@Value("${caasingress.plt.url.suffix}") final String pltUrlSuffix)
			throws KeyManagementException, NoSuchAlgorithmException {
		logger.debug("ctor caasingress aux prefix '{}' suffix '{}'", auxUrlPrefix, auxUrlSuffix);
		logger.debug("ctor caasingress plt prefix '{}' suffix '{}'", pltUrlPrefix, pltUrlSuffix);
		caasIngressAuxUrl = new DefaultUriBuilderFactory(auxUrlPrefix.trim()).builder().path(auxUrlSuffix.trim())
				.build().normalize().toString();
		caasIngressPltUrl = new DefaultUriBuilderFactory(pltUrlPrefix.trim()).builder().path(pltUrlSuffix.trim())
				.build().normalize().toString();
		logger.info("Configuring CAAS-Ingress URLs: aux {}, plt {}", caasIngressAuxUrl, caasIngressPltUrl);
		if (insecureFlag) {
			logger.warn("ctor: insecure flag set, disabling SSL checks");
			HttpsURLConnectionUtils.turnOffSslChecking();
		}
	}

	@Bean
	// The bean (method) name must be globally unique
	public SimpleKubernetesClient ciAuxApi() throws IOException {
		return new SimpleKubernetesClient(caasIngressAuxUrl);
	}

	@Bean
	// The bean (method) name must be globally unique
	public SimpleKubernetesClient ciPltApi() throws IOException {
		return new SimpleKubernetesClient(caasIngressPltUrl);
	}

}
