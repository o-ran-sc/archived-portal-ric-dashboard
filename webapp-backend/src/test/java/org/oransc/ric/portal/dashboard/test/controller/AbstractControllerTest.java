/*-
 * ========================LICENSE_START=================================
 * O-RAN-SC
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
package org.oransc.ric.portal.dashboard.test.controller;

import static org.oransc.ric.portal.dashboard.test.config.TestWebSecurityConfigurerAdapter.TESTPASS;
import static org.oransc.ric.portal.dashboard.test.config.TestWebSecurityConfigurerAdapter.TESTUSER;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Defines values and objects reused by all the controller tests. I guess this
 * violates the "composition over inheritance" advice but it saves many lines of
 * code in each subclass.
 *
 * Activate profile "mock" to configure the mocked versions of remote endpoints
 * 
 * Activate profile "test" to configure the test credentials
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles({ "mock", "test" })
public class AbstractControllerTest {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	// Created by Spring black magic
	// https://spring.io/guides/gs/testing-web/
	@LocalServerPort
	private int localServerPort;
	private String httpUrl;
	protected TestRestTemplate restTemplate;

	@Before
	public void setUp() throws Exception {
		httpUrl = "http://localhost:" + localServerPort + "/";
		restTemplate = new TestRestTemplate(TESTUSER, TESTPASS);
	}

	/**
	 * Builds URI using localhost, the Spring-defined random port, plus the
	 * arguments.
	 * 
	 * @param queryParams
	 *                        Map of string-string query parameters
	 * @param path
	 *                        Array of path components. If a component has an
	 *                        embedded slash, the string is split and each
	 *                        subcomponent is added individually.
	 * @return URI
	 */
	protected URI buildUri(final Map<String, String> queryParams, final String... path) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(this.httpUrl);
		for (int p = 0; p < path.length; ++p) {
			if (path[p] == null || path[p].isEmpty()) {
				throw new IllegalArgumentException("Unexpected null or empty at path index " + Integer.toString(p));
			} else if (path[p].contains("/")) {
				String[] subpaths = path[p].split("/");
				for (String s : subpaths)
					if (!s.isEmpty())
						builder.pathSegment(s);
			} else {
				builder.pathSegment(path[p]);
			}
		}
		if (queryParams != null && queryParams.size() > 0) {
			for (Map.Entry<String, String> entry : queryParams.entrySet()) {
				if (entry.getKey() == null || entry.getValue() == null)
					throw new IllegalArgumentException("Unexpected null key or value");
				else
					builder.queryParam(entry.getKey(), entry.getValue());
			}
		}
		return builder.build().encode().toUri();
	}

	// Because I put the annotations on this parent class,
	// must define at least one test here.
	@Test
	public void contextLoads() {
		logger.info("Context loads on mock profile");
		// Silence Sonar warning about missing assertion.
		Assertions.assertThat(logger.isWarnEnabled());
	}

}
