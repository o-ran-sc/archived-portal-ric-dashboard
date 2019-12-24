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
import java.util.ArrayList;
import java.util.List;

import org.oransc.ric.portal.dashboard.model.RicInstance;
import org.oransc.ric.portal.dashboard.model.RicInstanceList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Publishes a mock list of RIC instances.
 */
@Component
@Profile("test")
public class RICInstanceMockConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	// Publish constants for use in tests
	public static final String INSTANCE_KEY_1 = "i1";
	public static final String INSTANCE_KEY_2 = "i2";
	public static final String[] INSTANCE_KEYS = { INSTANCE_KEY_1, INSTANCE_KEY_2 };

	// Simulate remote method delay for UI testing
	private int delayMs;

	@Autowired
	public RICInstanceMockConfiguration(@Value("${mock.config.delay:0}") int delayMs) {
		logger.debug("ctor: configured with delay {}", delayMs);
		this.delayMs = delayMs;
	}

	@Bean
	public RicInstanceList ricInstanceList() throws InterruptedException {
		if (delayMs > 0) {
			logger.debug("ricInstanceList sleeping {}", delayMs);
			Thread.sleep(delayMs);
		}
		List<RicInstance> instances = new ArrayList<>();
		for (String key : INSTANCE_KEYS) {
			RicInstance i = new RicInstance().key(key).name("RIC Instance " + key)
					.appUrlPrefix("http://" + key + ".domain.name/app")
					.pltUrlPrefix("http://" + key + ".domain.name/plt")
					.caasUrlPrefix("http://" + key + ".domain.name/caas");
			instances.add(i);
		}
		return new RicInstanceList(instances);
	}

}
