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
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.onap.portalsdk.core.restful.domain.EcompUser;
import org.oransc.ric.portal.dashboard.DashboardUserManagerTest;
import org.oransc.ric.portal.dashboard.exception.UnknownInstanceException;
import org.oransc.ricplt.e2mgr.client.model.GetNodebResponse;
import org.oransc.ricplt.e2mgr.client.model.NodebIdentity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModelTest extends AbstractModelTest {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private void checkAppStats(AppStats m) {
		Assert.assertEquals(s1, m.getInstanceKey());
		Assert.assertTrue(i1 == m.getStatsDetails().getAppId());
		Assert.assertEquals(s2, m.getStatsDetails().getAppName());
		Assert.assertEquals(s3, m.getStatsDetails().getMetricUrl());
	}

	@Test
	public void testAppStats() {
		StatsDetailsTransport n = new StatsDetailsTransport();
		n.setAppId(i1);
		n.setAppName(s2);
		n.setMetricUrl(s3);
		AppStats m = new AppStats();
		m = new AppStats(s1, n);
		m.setInstanceKey(s1);
		m.setStatsDetails(n);
		checkAppStats(m);
		Assert.assertNotEquals(1, m.hashCode());
		logger.info(m.toString());
	}

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
		Assert.assertTrue(eud.isCredentialsNonExpired());
		Assert.assertTrue(eud.isEnabled());
		logger.info(eud.toString());
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
		m.nodebIdentity(nodebIdentity).nodebStatus(nodebResponse);
		Assert.assertEquals(m.getNodebIdentity(), nodebIdentity);
		Assert.assertEquals(m.getNodebStatus(), nodebResponse);
		logger.debug(m.toString());
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

	private void checkRicInstanceKeyName(RicInstanceKeyName m) {
		Assert.assertEquals(s1, m.getKey());
		Assert.assertEquals(s2, m.getName());
	}

	@Test
	public void testRicInstanceKeyName() {
		RicInstanceKeyName m = new RicInstanceKeyName(s1, s1);
		m = new RicInstanceKeyName();
		m.setKey(s1);
		m.setName(s2);
		checkRicInstanceKeyName(m);
		Assert.assertTrue(m.equals(m));
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new RicInstanceKeyName()));
		Assert.assertNotEquals(1, m.hashCode());
		logger.info(m.toString());
	}

	private void checkRicInstance(RicInstance m) {
		Assert.assertEquals(s1, m.getAppUrlPrefix());
		Assert.assertEquals(s2, m.getCaasUrlPrefix());
		Assert.assertEquals(s3, m.getKey());
		Assert.assertEquals(s4, m.getName());
	}

	@Test
	public void testRicInstance() {
		RicInstance m = new RicInstance();
		m.setAppUrlPrefix(s1);
		m.setCaasUrlPrefix(s2);
		m.setKey(s3);
		m.setName(s4);
		checkRicInstance(m);
		Assert.assertTrue(m.equals(m));
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new RicInstance()));
		Assert.assertNotEquals(1, m.hashCode());
		logger.info(m.toString());
	}

	private void checkRicRegion(RicRegion m) {
		Assert.assertEquals(s1, m.getName());
	}

	@Test
	public void testRicRegion() {
		RicRegion m = new RicRegion();
		m.setName(s1);
		checkRicRegion(m);
		Assert.assertTrue(m.equals(m));
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new RicRegion()));
		Assert.assertNotEquals(1, m.hashCode());
		logger.info(m.toString());
	}

	@Test
	public void testRicRegionList() {
		new RicRegionList();
		List<RicRegion> list = new ArrayList<>();
		final RicRegionList m = new RicRegionList(list);
		Assert.assertEquals(list, m.getRegions());
		Assert.assertNotNull(m.getSimpleInstances());
		Assertions.assertThrows(UnknownInstanceException.class, () -> {
			m.getInstance(s1);
		});
		logger.info(m.toString());
	}

	private void checkRicRegionTransport(RicRegionTransport m) {
		Assert.assertEquals(s1, m.getName());
		Assert.assertFalse(m.getInstances().isEmpty());
	}

	@Test
	public void testRicRegionTransport() {
		RicRegionTransport m = new RicRegionTransport().name(s1);
		m.instances(new ArrayList<RicInstanceKeyName>());
		m.getInstances().add(new RicInstanceKeyName(s1, s2));
		checkRicRegionTransport(m);
		Assert.assertTrue(m.equals(m));
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new RicRegionTransport()));
		Assert.assertNotEquals(1, m.hashCode());
		logger.info(m.toString());
	}

	private void checkStatsDetailsTransport(StatsDetailsTransport m) {
		Assert.assertTrue(i1 == m.getAppId());
		Assert.assertEquals(s1, m.getAppName());
		Assert.assertEquals(s2, m.getMetricUrl());
	}

	@Test
	public void testStatDetailsTransport() {
		StatsDetailsTransport m = new StatsDetailsTransport();
		m.setAppId(i1);
		m.setAppName(s1);
		m.setMetricUrl(s2);
		checkStatsDetailsTransport(m);
		Assert.assertTrue(m.equals(m));
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new StatsDetailsTransport()));
		Assert.assertNotEquals(1, m.hashCode());
		logger.info(m.toString());
	}

}
