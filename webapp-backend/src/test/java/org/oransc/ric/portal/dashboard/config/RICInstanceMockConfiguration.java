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

import java.util.ArrayList;
import java.util.List;

import org.oransc.ric.portal.dashboard.model.RicInstance;
import org.oransc.ric.portal.dashboard.model.RicInstanceList;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Publishes a mock list of RIC instances.
 */
@Component
@Profile("test")
public class RICInstanceMockConfiguration {

	// Publish constants for use in tests
	public static final String INSTANCE_KEY_1 = "i1";
	public static final String INSTANCE_KEY_2 = "i2";

	@Bean
	public RicInstanceList ricInstanceList() {
		List<RicInstance> instances = new ArrayList<>();
		RicInstance i1 = new RicInstance().key(INSTANCE_KEY_1).name("Friendly Name One")
				.appUrlPrefix("http://foo.bar/app").pltUrlPrefix("http://foo.bar/plt")
				.caasUrlPrefix("http://foo.bar/caas");
		instances.add(i1);
		RicInstance i2 = new RicInstance().key(INSTANCE_KEY_2).name("Friendly Name Two")
				.appUrlPrefix("http://foo.bar/2/app").pltUrlPrefix("http://foo.bar/2/plt")
				.caasUrlPrefix("http://foo.bar/2/caas");
		instances.add(i2);
		return new RicInstanceList(instances);
	}

}
