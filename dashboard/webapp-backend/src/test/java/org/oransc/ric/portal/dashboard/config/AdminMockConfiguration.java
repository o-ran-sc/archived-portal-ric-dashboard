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

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.HashSet;
import java.util.Set;

import org.onap.portalsdk.core.onboarding.exception.PortalAPIException;
import org.onap.portalsdk.core.restful.domain.EcompRole;
import org.onap.portalsdk.core.restful.domain.EcompUser;
import org.oransc.ric.portal.dashboard.AppStatsManager;
import org.oransc.ric.portal.dashboard.DashboardUserManager;
import org.oransc.ric.portal.dashboard.exception.StatsManagerException;
import org.oransc.ric.portal.dashboard.model.StatsDetailsTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Creates user manager and stats manager with mock data.
 */
@Configuration
@Profile("test")
public class AdminMockConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	// Autowire all the properties required by the real class
	// (even tho not used here) as a test of the properties.
	@Autowired
	public AdminMockConfiguration(@Value("${userfile}") final String userfile,
			@Value("${statsfile}") final String statsfile) {
		logger.info("ctor userfile {} statsfile {}", userfile, statsfile);
	}

	@Bean
	// The bean (method) name must be globally unique
	public DashboardUserManager userManager() throws IOException, PortalAPIException {
		logger.debug("userManager: adding mock data");
		DashboardUserManager mgr = new DashboardUserManager(true);
		String[] firsts = { "John", "Alice", "Pierce", "Paul", "Jack" };
		String[] lasts = { "Doe", "Nolan", "King", "Smith", "Reacher" };
		String[] logins = { "jdoe", "anolan", "pking", "psmith", "jreacher" };
		boolean[] actives = { true, true, false, false, true };
		EcompRole role = new EcompRole();
		role.setName("view");
		Set<EcompRole> roles = new HashSet<>();
		roles.add(role);
		for (int i = 0; i < firsts.length; ++i) {
			EcompUser eu = new EcompUser();
			eu.setFirstName(firsts[i]);
			eu.setLastName(lasts[i]);
			eu.setLoginId(logins[i]);
			eu.setActive(actives[i]);
			eu.setRoles(roles);
			mgr.createUser(eu);
		}
		return mgr;
	}

	@Bean
	// The bean (method) name must be globally unique
	public AppStatsManager statsManager() throws IOException, StatsManagerException {
		logger.debug("statsManager: adding mock data");
		AppStatsManager mgr = new AppStatsManager(true);
		String instanceKey = RICInstanceMockConfiguration.INSTANCE_KEY_1;
		StatsDetailsTransport statsDetails = new StatsDetailsTransport();
		statsDetails.setAppId(0);
		statsDetails.setAppName("MachLearn");
		statsDetails.setMetricUrl("https://www.example.com");
		mgr.createStats(instanceKey, statsDetails);
		return mgr;
	}

}
