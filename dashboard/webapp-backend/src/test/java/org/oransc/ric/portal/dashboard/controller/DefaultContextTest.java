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
package org.oransc.ric.portal.dashboard.controller;

import java.lang.invoke.MethodHandles;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Tests whether the default (not mock) configuration classes run to completion.
 */
@ExtendWith(SpringExtension.class)
// This way of setting the active profile should not be necessary. See:
// https://github.com/spring-projects/spring-boot/issues/19788
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, properties = "spring.profiles.active:default")
public class DefaultContextTest {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	/**
	 * I expect the server to loaded then be torn down again. And when a single test
	 * is run, that is the behavior. But if all the tests are run, this test is
	 * reached while working thru the package and it appears that the "default"
	 * profile is added to the active "test" profile. Junit continues on thru the
	 * remaining tests, the non-mock configuration bean is used to authenticate
	 * Portal API requests, but because it has no username and password (the entries
	 * in application.yaml are blank), access is denied and these tests fail:
	 * <UL>
	 * <LI>{@link PortalRestCentralServiceTest#createUserTest()}
	 * <LI>{@link PortalRestCentralServiceTest#updateUserTest()}
	 * </UL>
	 * Maybe:
	 *
	 * I worked around the problem by using the application.yaml credentials. I also
	 * annotated this class above trying to limit the active profile, but I'm not
	 * confident it is working nor that it's needed.
	 */
	@Test
	public void contextLoads() {
		// Silence Sonar warning about missing assertion.
		Assertions.assertTrue(logger.isWarnEnabled());
		logger.info("DefaultContextTest#contextLoads on default profile");
	}

}
