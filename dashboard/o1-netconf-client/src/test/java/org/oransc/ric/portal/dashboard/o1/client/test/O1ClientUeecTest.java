/*
 * 
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
package org.oransc.ric.portal.dashboard.o1.client.test;

import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.yang.gen.v1.urn.o.ran.ric.ueec.config._1._0.rev200129.Ric;
import org.opendaylight.yang.gen.v1.urn.o.ran.ric.ueec.config._1._0.rev200129.RicBuilder;
import org.opendaylight.yang.gen.v1.urn.o.ran.ric.ueec.config._1._0.rev200129.ric.Config;
import org.opendaylight.yang.gen.v1.urn.o.ran.ric.ueec.config._1._0.rev200129.ric.ConfigBuilder;
import org.opendaylight.yang.gen.v1.urn.o.ran.ric.ueec.config._1._0.rev200129.ric.config.Control;
import org.opendaylight.yang.gen.v1.urn.o.ran.ric.ueec.config._1._0.rev200129.ric.config.ControlBuilder;

/**
 * Demonstrates use of the generated O1 client.
 * 
 * The test fails because no server is available.
 */
public class O1ClientUeecTest {

	@Test
	public void testUeec() {
		ControlBuilder controlBuilder = new ControlBuilder();
		Control control = controlBuilder.setActive(true).build();
		ConfigBuilder configBuilder = new ConfigBuilder();
		Config config = configBuilder.setName("name").setName("namespace").setControl(control).build();
		RicBuilder ueecRicBuilder = new RicBuilder();
		Ric ric = ueecRicBuilder.setConfig(config).build();
		Assert.assertNotNull(ric);
	}

}
