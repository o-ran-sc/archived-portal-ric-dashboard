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
package org.oransc.ric.portal.dashboard.config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.onap.portalsdk.core.onboarding.crossapi.PortalRestAPIProxy;
import org.onap.portalsdk.core.onboarding.exception.PortalAPIException;
import org.onap.portalsdk.core.onboarding.util.PortalApiConstants;
import org.onap.portalsdk.core.restful.domain.EcompRole;
import org.onap.portalsdk.core.restful.domain.EcompUser;
import org.oransc.ric.portal.dashboard.DashboardConstants;
import org.oransc.ric.portal.dashboard.LoginServlet;
import org.oransc.ric.portal.dashboard.portalapi.DashboardUserManager;
import org.oransc.ric.portal.dashboard.portalapi.PortalAuthManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@Profile("test")
public class WebSecurityMockConfiguration extends WebSecurityConfigurerAdapter {

	// Unfortunately EPSDK-FW does not define these as constants
	public static final String PORTAL_USERNAME_HEADER_KEY = "username";
	public static final String PORTAL_PASSWORD_HEADER_KEY = "password";

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public WebSecurityMockConfiguration(@Value("${portalapi.userfile}") final String userFilePath) {
		logger.debug("ctor: user file path {}", userFilePath);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// no login checks when testing
		http.authorizeRequests().anyRequest().permitAll() //
				.and() //
				.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
		// do not create a login filter
	}

	/**
	 * Open access to the documentation.
	 */
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/v2/api-docs", "/swagger-resources/**", "/swagger-ui.html", "/webjars/**",
				"/users/register", "/auth/login", "oauth/login/**");
	}

	@Bean
	public ServletRegistrationBean<LoginServlet> loginServlet() {
		LoginServlet servlet = new LoginServlet();
		final ServletRegistrationBean<LoginServlet> servletBean = new ServletRegistrationBean<>(servlet,
				DashboardConstants.LOGIN_PAGE);
		servletBean.setName("LoginServlet");
		return servletBean;
	}

	@Bean
	public ServletRegistrationBean<PortalRestAPIProxy> portalApiProxyServlet() {
		PortalRestAPIProxy servlet = new PortalRestAPIProxy();
		final ServletRegistrationBean<PortalRestAPIProxy> servletBean = new ServletRegistrationBean<>(servlet,
				PortalApiConstants.API_PREFIX + "/*");
		servletBean.setName("PortalRestApiProxyServlet");
		return servletBean;
	}

	@Bean
	public PortalAuthManager portalAuthManager() throws Exception {
		PortalAuthManager mockManager = mock(PortalAuthManager.class);
		final Map<String, String> credentialsMap = new HashMap<>();
		credentialsMap.put("appName", "appName");
		credentialsMap.put(PORTAL_USERNAME_HEADER_KEY, PORTAL_USERNAME_HEADER_KEY);
		credentialsMap.put(PORTAL_PASSWORD_HEADER_KEY, PORTAL_PASSWORD_HEADER_KEY);
		doAnswer(inv -> {
			logger.debug("getAppCredentials");
			return credentialsMap;
		}).when(mockManager).getAppCredentials();
		doAnswer(inv -> {
			logger.debug("getUserId");
			return "userId";
		}).when(mockManager).valdiateEcompSso(any(HttpServletRequest.class));
		doAnswer(inv -> {
			logger.debug("getAppCredentials");
			return credentialsMap;
		}).when(mockManager).getAppCredentials();
		return mockManager;
	}

	// This implementation is so light it can be used during tests.
	@Bean
	public DashboardUserManager dashboardUserManager() throws IOException, PortalAPIException {
		File f = new File("/tmp/users.json");
		if (f.exists())
			f.delete();
		DashboardUserManager um = new DashboardUserManager(f.getAbsolutePath());
		// Mock user for convenience in testing
		EcompUser demo = new EcompUser();
		demo.setLoginId("demo");
		demo.setFirstName("Demo");
		demo.setLastName("User");
		demo.setActive(true);
		EcompRole role = new EcompRole();
		role.setName("view");
		Set<EcompRole> roles = new HashSet<>();
		roles.add(role);
		demo.setRoles(roles);
		um.createUser(demo);
		return um;
	}

}
