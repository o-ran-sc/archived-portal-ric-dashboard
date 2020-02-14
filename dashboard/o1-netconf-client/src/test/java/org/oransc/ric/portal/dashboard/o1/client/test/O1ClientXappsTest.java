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

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.yang.gen.v1.urn.o.ran.ric.xapp.desc._1._0.rev200129.Ric;
import org.opendaylight.yang.gen.v1.urn.o.ran.ric.xapp.desc._1._0.rev200129.RicBuilder;
import org.opendaylight.yang.gen.v1.urn.o.ran.ric.xapp.desc._1._0.rev200129.ric.Xapps;
import org.opendaylight.yang.gen.v1.urn.o.ran.ric.xapp.desc._1._0.rev200129.ric.XappsBuilder;
import org.opendaylight.yang.gen.v1.urn.o.ran.ric.xapp.desc._1._0.rev200129.ric.xapps.Xapp;
import org.opendaylight.yang.gen.v1.urn.o.ran.ric.xapp.desc._1._0.rev200129.ric.xapps.XappBuilder;

/**
 * Demonstrates use of the generated O1 client.
 * 
 * The test fails because no server is available.
 */
public class O1ClientXappsTest {

	@Test
	public void testUeec() {
		XappBuilder xappBuilder = new XappBuilder();
		Xapp xapp = xappBuilder.setName("name").setNamespace("namespace").setReleaseName("relName").setVersion("ver")
				.build();
		List<Xapp> values = new ArrayList<>();
		values.add(xapp);
		XappsBuilder xappsBuilder = new XappsBuilder();
		Xapps xapps = xappsBuilder.setXapp(values).build();
		RicBuilder ricBuilder = new RicBuilder();
		Ric ric = ricBuilder.setXapps(xapps).build();
		Assert.assertNotNull(ric);
	}

}
