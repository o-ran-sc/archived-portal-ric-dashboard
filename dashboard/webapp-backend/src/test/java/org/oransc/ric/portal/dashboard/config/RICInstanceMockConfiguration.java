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
import org.oransc.ric.portal.dashboard.model.RicRegion;
import org.oransc.ric.portal.dashboard.model.RicRegionList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Publishes a mock list of RIC instances.
 */
@Configuration
@Profile("test")
public class RICInstanceMockConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	// Publish constants for use in tests
	public static final String REGION_NAME_1 = "Region AAA";
	public static final String REGION_NAME_2 = "Region DDD";
	public static final String INSTANCE_KEY_1 = "i1";
	public static final String INSTANCE_KEY_2 = "i2";
	public static final String[] INSTANCE_KEYS = { INSTANCE_KEY_1, INSTANCE_KEY_2 };

	// No constructor needed, don't simulate delay from the Dashboard

	@Bean
	public RicRegionList ricRegions() {
		logger.debug("Creating mock bean ricRegions");
		List<RicRegion> regions = new ArrayList<>();
		RicRegion region1 = new RicRegion().name(REGION_NAME_1);
		regions.add(region1);
		List<RicInstance> instances1 = new ArrayList<>();
		region1.setInstances(instances1);
		String key1 = INSTANCE_KEY_1;
		instances1.add(new RicInstance().key(key1).name("RIC Instance " + key1) //
				.appUrlPrefix("http://" + key1 + ".domain.name/app") //
				.pltUrlPrefix("http://" + key1 + ".domain.name/plt") //
				.caasUrlPrefix("http://" + key1 + ".domain.name/caas"));
		RicRegion region2 = new RicRegion().name(REGION_NAME_2);
		regions.add(region2);
		List<RicInstance> instances2 = new ArrayList<>();
		region2.setInstances(instances2);
		String key2 = INSTANCE_KEY_2;
		instances2.add(new RicInstance().key(key2).name("RIC Instance " + key2) //
				.appUrlPrefix("http://" + key2 + ".domain.name/app") //
				.pltUrlPrefix("http://" + key2 + ".domain.name/plt") //
				.caasUrlPrefix("http://" + key2 + ".domain.name/caas"));
		return new RicRegionList(regions);
	}

}
