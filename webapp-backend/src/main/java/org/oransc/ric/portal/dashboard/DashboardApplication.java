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

import org.oransc.ric.portal.dashboard.model.RicInstance;
import org.oransc.ric.portal.dashboard.model.RicRegion;
import org.oransc.ric.portal.dashboard.model.RicRegionList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.util.Assert;

@SpringBootApplication
// Limit scan to dashboard classes; exclude generated API classes
@ComponentScan("org.oransc.ric.portal.dashboard")
public class DashboardApplication implements CommandLineRunner {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired
	private RicRegionList instanceConfig;

	public static void main(String[] args) {
		SpringApplication.run(DashboardApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// Ensure output appears on the console by using level WARN
		logger.warn("run: version '{}'", getImplementationVersion(MethodHandles.lookup().lookupClass()));
		// Validate configuration
		List<RicRegion> regions = instanceConfig.getRegions();
		Assert.notEmpty(regions, "Region list empty");
		for (RicRegion r : regions) {
			Assert.notEmpty(r.getInstances(), "Instance list empty for region " + r.getName());
			for (RicInstance it : r.getInstances()) {
				logger.warn("run: RIC region {} instance {}", r, it);
				Assert.hasText(it.getKey(), "Instance key missing");
				Assert.hasText(it.getName(), "Name missing for instance " + it.getKey());
				Assert.hasText(it.getAppUrlPrefix(), "App URL prefix missing for instance " + it.getKey());
				Assert.hasText(it.getCaasUrlPrefix(), "Caas URL prefix missing for instance " + it.getKey());
				Assert.hasText(it.getPltUrlPrefix(), "Plt URL prefix missing for instance " + it.getKey());
			}
		}
	}

	/**
	 * Gets version details for the specified class.
	 * 
	 * @param clazz
	 *                  Class to get the version
	 * 
	 * @return the value of the MANIFEST.MF property Implementation-Version as
	 *         written by maven when packaged in a jar; 'unknown' otherwise.
	 */
	public static String getImplementationVersion(Class<?> clazz) {
		String classPath = clazz.getResource(clazz.getSimpleName() + ".class").toString();
		return classPath.startsWith("jar") ? clazz.getPackage().getImplementationVersion() : "unknown-not-jar";
	}

}
