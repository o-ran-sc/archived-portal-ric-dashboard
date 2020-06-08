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
package org.oransc.ric.portal.dashboard.config.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.oransc.itdev.xapponboarder.client.api.ChartsApi;
import org.oransc.itdev.xapponboarder.client.api.HealthApi;
import org.oransc.ric.portal.dashboard.config.RICInstanceMockConfiguration;
import org.oransc.ric.portal.dashboard.config.XappOnboarderApiBuilder;

public class XappOnboarderConfigTest extends AbstractConfigTest {

	@Test
	public void builderTest() {
		XappOnboarderApiBuilder builder = new XappOnboarderApiBuilder(instanceConfig, "suffix");
		HealthApi healthApi = builder.getHealthApi(RICInstanceMockConfiguration.INSTANCE_KEY_1);
		Assertions.assertNotNull(healthApi);
		ChartsApi chartsApi = builder.getChartsApi(RICInstanceMockConfiguration.INSTANCE_KEY_1);
		Assertions.assertNotNull(chartsApi);
	}

}
