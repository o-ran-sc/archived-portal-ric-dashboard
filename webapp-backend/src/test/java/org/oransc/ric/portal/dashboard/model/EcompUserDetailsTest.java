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
package org.oransc.ric.portal.dashboard.model;

import java.lang.invoke.MethodHandles;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.onap.portalsdk.core.restful.domain.EcompUser;
import org.oransc.ric.portal.dashboard.DashboardUserManagerTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EcompUserDetailsTest {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Test
	public void testEcompUserDetails() {
		EcompUser eu = DashboardUserManagerTest.createEcompUser("lgid");
		logger.info("EcompUser {}", eu);
		EcompUserDetails eud = new EcompUserDetails(eu);
		Assert.assertNotNull(eud.getAuthorities());
		Assert.assertNull(eud.getPassword());
		Assert.assertNotNull(eud.getUsername());
		Assert.assertTrue(eud.isAccountNonExpired());
		Assert.assertTrue(eud.isAccountNonLocked());
	}

}
