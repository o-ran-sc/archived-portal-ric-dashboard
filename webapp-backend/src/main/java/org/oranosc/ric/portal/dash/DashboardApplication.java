/*-
 * ========================LICENSE_START=================================
 * ORAN-OSC
 * %%
 * Copyright (C) 2019 AT&T Intellectual Property and Nokia
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
package org.oranosc.ric.portal.dash;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
// Limit the annotation scan to the dashboard classes;
// exclude the generated client classes!
@ComponentScan("org.oranosc.ric.portal.dash")
public class DashboardApplication {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public static void main(String[] args) {
		SpringApplication.run(DashboardApplication.class, args);
		// Force this onto the console by using level WARN
		logger.warn("main: version '{}' successful start", getVersion());
	}

	/**
	 * Gets version details.
	 * 
	 * @return the value of the MANIFEST.MF property Implementation-Version as
	 *         written by maven when packaged in a jar; 'unknown' otherwise.
	 */
	private static String getVersion() {
		Class<?> clazz = MethodHandles.lookup().lookupClass();
		String classPath = clazz.getResource(clazz.getSimpleName() + ".class").toString();
		return classPath.startsWith("jar") ? clazz.getPackage().getImplementationVersion() : "unknown";
	}

}
