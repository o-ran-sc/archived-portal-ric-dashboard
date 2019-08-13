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

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;

import org.onap.portalsdk.core.onboarding.crossapi.PortalRestAPIProxy;
import org.onap.portalsdk.core.onboarding.util.PortalApiConstants;
import org.oransc.ric.portal.dashboard.DashboardConstants;
import org.oransc.ric.portal.dashboard.LoginServlet;
import org.oransc.ric.portal.dashboard.controller.AcXappController;
import org.oransc.ric.portal.dashboard.controller.AdminController;
import org.oransc.ric.portal.dashboard.controller.AnrXappController;
import org.oransc.ric.portal.dashboard.controller.AppManagerController;
import org.oransc.ric.portal.dashboard.controller.E2ManagerController;
import org.oransc.ric.portal.dashboard.controller.SimpleErrorController;
import org.oransc.ric.portal.dashboard.portalapi.DashboardUserManager;
import org.oransc.ric.portal.dashboard.portalapi.PortalAuthManager;
import org.oransc.ric.portal.dashboard.portalapi.PortalAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
@Profile("!test")
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	// Although constructor arguments are recommended over field injection,
	// this results in fewer lines of code.
	@Value("${userfile}")
	private String userFilePath;
	@Value("${portalapi.appname}")
	private String appName;
	@Value("${portalapi.username}")
	private String userName;
	@Value("${portalapi.password}")
	private String password;
	@Value("${portalapi.decryptor}")
	private String decryptor;
	@Value("${portalapi.usercookie}")
	private String userCookie;

	protected void configure(HttpSecurity http) throws Exception {
		logger.debug("configure: portalapi.username {}", userName);
		// A chain of ".and()" always baffles me
		http.authorizeRequests().anyRequest().authenticated();
		http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
		http.addFilterBefore(portalAuthenticationFilterBean(), BasicAuthenticationFilter.class);
	}

	/**
	 * Resource paths that do not require authentication, especially including
	 * Swagger-generated documentation.
	 */
	public static final String[] OPEN_PATHS = { //
			"/v2/api-docs", //
			"/swagger-resources/**", //
			"/swagger-ui.html", //
			"/webjars/**", //
			PortalApiConstants.API_PREFIX + "/**", //
			AcXappController.CONTROLLER_PATH + "/" + AcXappController.VERSION_METHOD, //
			AdminController.CONTROLLER_PATH + "/" + AdminController.HEALTH_METHOD, //
			AdminController.CONTROLLER_PATH + "/" + AdminController.VERSION_METHOD, //
			AnrXappController.CONTROLLER_PATH + "/" + AnrXappController.HEALTH_ALIVE_METHOD, //
			AnrXappController.CONTROLLER_PATH + "/" + AnrXappController.HEALTH_READY_METHOD, //
			AnrXappController.CONTROLLER_PATH + "/" + AnrXappController.VERSION_METHOD, //
			AppManagerController.CONTROLLER_PATH + "/" + AppManagerController.HEALTH_ALIVE_METHOD, //
			AppManagerController.CONTROLLER_PATH + "/" + AppManagerController.HEALTH_READY_METHOD, //
			AppManagerController.CONTROLLER_PATH + "/" + AppManagerController.VERSION_METHOD, //
			E2ManagerController.CONTROLLER_PATH + "/" + E2ManagerController.HEALTH_METHOD, //
			E2ManagerController.CONTROLLER_PATH + "/" + E2ManagerController.VERSION_METHOD, //
			SimpleErrorController.ERROR_PATH, //
			DashboardConstants.LOGIN_PAGE //
	};

	@Override
	public void configure(WebSecurity web) throws Exception {
		// This disables Spring security, but not the app's filter.
		web.ignoring().antMatchers(OPEN_PATHS);
	}

	@Bean
	public PortalAuthManager portalAuthManagerBean()
			throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		return new PortalAuthManager(appName, userName, password, decryptor, userCookie);
	}

	@Bean
	public DashboardUserManager dashboardUserManagerBean() throws IOException {
		return new DashboardUserManager(userFilePath);
	}

	/*
	 * If this is annotated with @Bean, it is created automatically AND REGISTERED,
	 * and Spring processes annotations in the source of the class. However, the
	 * filter is added in the chain apparently in the wrong order. Alternately, with
	 * no @Bean and added to the chain up in the configure() method in the desired
	 * order, the ignoring() matcher pattern configured above causes Spring to
	 * bypass this filter, which seems to me means the filter participates
	 * correctly.
	 */
	public PortalAuthenticationFilter portalAuthenticationFilterBean()
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		PortalAuthenticationFilter portalAuthenticationFilter = new PortalAuthenticationFilter(portalAuthManagerBean(),
				dashboardUserManagerBean());
		return portalAuthenticationFilter;
	}

	/**
	 * Instantiates the EPSDK-FW servlet. Needed because this app is not configured
	 * to scan the EPSDK-FW packages; there's also a chance that Spring-Boot does
	 * not automatically process @WebServlet annotations.
	 * 
	 * @return Servlet registration bean for the Portal Rest API proxy servlet.
	 */
	@Bean
	public ServletRegistrationBean<PortalRestAPIProxy> portalApiProxyServletBean() {
		PortalRestAPIProxy servlet = new PortalRestAPIProxy();
		final ServletRegistrationBean<PortalRestAPIProxy> servletBean = new ServletRegistrationBean<>(servlet,
				PortalApiConstants.API_PREFIX + "/*");
		servletBean.setName("PortalRestApiProxyServlet");
		return servletBean;
	}

	/**
	 * Instantiates a trivial login servlet that serves a basic page with a link to
	 * authenticate at Portal. The login filter redirects to this page instead of
	 * Portal.
	 * 
	 * @return Servlet registration bean for the Dashboard login servlet.
	 */
	@Bean
	public ServletRegistrationBean<LoginServlet> loginServletBean() {
		LoginServlet servlet = new LoginServlet();
		final ServletRegistrationBean<LoginServlet> servletBean = new ServletRegistrationBean<>(servlet,
				DashboardConstants.LOGIN_PAGE);
		servletBean.setName("LoginServlet");
		return servletBean;
	}

}
