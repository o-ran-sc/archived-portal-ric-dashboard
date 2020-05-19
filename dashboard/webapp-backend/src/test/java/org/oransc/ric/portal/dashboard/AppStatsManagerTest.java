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
package org.oransc.ric.portal.dashboard;

import java.lang.invoke.MethodHandles;
import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.oransc.ric.portal.dashboard.config.RICInstanceMockConfiguration;
import org.oransc.ric.portal.dashboard.exception.StatsManagerException;
import org.oransc.ric.portal.dashboard.model.AppStats;
import org.oransc.ric.portal.dashboard.model.StatsDetailsTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
public class AppStatsManagerTest {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Test
	public void testStatsMgr() throws Exception {
		new AppStatsManager("file.json");
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new AppStatsManager(null);
		});
		AppStatsManager mgr = new AppStatsManager(true);
		AppStats as1 = mgr.createStats(RICInstanceMockConfiguration.INSTANCE_KEY_1,
				new StatsDetailsTransport(-1, "app1 name", "http://app1"));
		logger.info("Created stats {}", as1);
		AppStats as2 = mgr.createStats(RICInstanceMockConfiguration.INSTANCE_KEY_1,
				new StatsDetailsTransport(-1, "app2 name", "http://app2"));
		logger.info("Created stats {}", as2);
		List<AppStats> all = mgr.getStats();
		Assert.assertFalse(all.isEmpty());
		List<AppStats> byInstance = mgr.getStatsByInstance(RICInstanceMockConfiguration.INSTANCE_KEY_1);
		Assert.assertFalse(byInstance.isEmpty());
		final AppStatsManager mgr2 = new AppStatsManager(false);
		AppStats as3 = mgr2.createStats(RICInstanceMockConfiguration.INSTANCE_KEY_1,
				new StatsDetailsTransport(-1, "app3 name", "http://app3"));
		Assert.assertNotEquals(as2.getStatsDetails().getAppId(), as3.getStatsDetails().getAppId());
		Assertions.assertThrows(StatsManagerException.class, () -> {
			mgr2.createStats(as3.getInstanceKey(), as3.getStatsDetails());
		});
		AppStats as = mgr.getStatsById(as1.getInstanceKey(), as1.getStatsDetails().getAppId());
		Assert.assertEquals(as1, as);
		AppStats notFound = mgr.getStatsById("bogus", 12345);
		Assert.assertNull(notFound);
		as1.getStatsDetails().setMetricUrl("http://other");
		mgr.updateStats(as1.getInstanceKey(), as1.getStatsDetails());
		mgr.deleteStats(as1.getInstanceKey(), as1.getStatsDetails().getAppId());
		Assertions.assertThrows(StatsManagerException.class, () -> {
			mgr2.updateStats("bogus", as1.getStatsDetails());
		});
		Assertions.assertThrows(StatsManagerException.class, () -> {
			mgr2.deleteStats("bogus", 999);
		});
	}

}
