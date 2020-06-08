/*-
 * ========================LICENSE_START=================================
 * O-RAN-SC
 * %%
 * Copyright (C) 2020 AT&T Intellectual Property
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

import org.oransc.ric.portal.dashboard.model.RicRegionList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Creates an Xapp onboarder client builder as a bean to be managed by the Spring
 * container.
 */
@Configuration
@Profile("!test")
public class XappOnboarderConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	// Populated by the autowired constructor
	private final String urlSuffix;
	private final RicRegionList instanceConfig;

	@Autowired
	public XappOnboarderConfiguration(@Value("${xappobrd.url.suffix}") final String urlSuffix,
			final RicRegionList instanceConfig) {
		logger.debug("ctor: URL suffix {}", urlSuffix);
		this.urlSuffix = urlSuffix;
		this.instanceConfig = instanceConfig;
	}

	@Bean
	// The bean (method) name must be globally unique
	public XappOnboarderApiBuilder xappOnboarderApiBuilder() {
		return new XappOnboarderApiBuilder(instanceConfig, urlSuffix);
	}

}
