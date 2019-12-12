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
import org.oransc.ric.e2mgr.client.model.GetNodebResponse;
import org.oransc.ric.e2mgr.client.model.NodebIdentity;
import org.oransc.ric.portal.dashboard.DashboardUserManagerTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModelTest extends AbstractModelTest {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private void checkAppTransport(AppTransport m) {
		Assert.assertEquals(s1, m.getName());
		Assert.assertEquals(s2, m.getVersion());
	}

	@Test
	public void testAppTransport() {
		AppTransport m = new AppTransport(s1);
		m = new AppTransport();
		m.setName(s1);
		m.setVersion(s2);
		checkAppTransport(m);
		logger.info(m.toString());
	}

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

	private void checkErrorTransport(ErrorTransport m) {
		Assert.assertEquals(s1, m.getError());
		Assert.assertEquals(s2, m.getMessage());
		Assert.assertEquals(s3, m.getPath());
		Assert.assertEquals(i1, m.getStatus());
		Assert.assertEquals(t1, m.getTimestamp());
	}

	@Test
	public void testErrorTransport() {
		ErrorTransport m = new ErrorTransport(i1, s1);
		m = new ErrorTransport(i1, new Exception());
		m = new ErrorTransport(i1, s1, s2, s3);
		m = new ErrorTransport();
		m.setError(s1);
		m.setMessage(s2);
		m.setPath(s3);
		m.setStatus(i1);
		m.setTimestamp(t1);
		checkErrorTransport(m);
		logger.info(m.toString());
	}

	private void checkInstanceTransport(RicInstanceKeyName m) {
		Assert.assertEquals(s1, m.getKey());
		Assert.assertEquals(s2, m.getName());
	}

	@Test
	public void testInstanceTransport() {
		RicInstanceKeyName m = new RicInstanceKeyName(s1, s1);
		m = new RicInstanceKeyName();
		m.setKey(s1);
		m.setName(s2);
		checkInstanceTransport(m);
		logger.info(m.toString());
	}

	@Test
	public void testRanDetailsTransport() {
		RanDetailsTransport m = new RanDetailsTransport();
		NodebIdentity nodebIdentity = new NodebIdentity();
		GetNodebResponse nodebResponse = new GetNodebResponse();
		m = new RanDetailsTransport(nodebIdentity, nodebResponse);
		Assert.assertEquals(m.getNodebIdentity(), nodebIdentity);
		Assert.assertEquals(m.getNodebStatus(), nodebResponse);
	}

	private void checkSuccessTransport(SuccessTransport m) {
		Assert.assertEquals(s1, m.getData());
		Assert.assertEquals(1, m.getStatus());
	}

	@Test
	public void testSuccessTransport() {
		SuccessTransport m = new SuccessTransport(1, s1);
		m = new SuccessTransport();
		m.setData(s1);
		m.setStatus(1);
		checkSuccessTransport(m);
		logger.info(m.toString());
	}

}
