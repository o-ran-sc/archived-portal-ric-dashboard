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

import org.onap.portalsdk.core.onboarding.crossapi.PortalRestAPIProxy;
import org.onap.portalsdk.core.onboarding.util.PortalApiConstants;
import org.oransc.ric.portal.dashboard.portalapi.PortalAuthManager;
import org.oransc.ric.portal.dashboard.portalapi.DashboardUserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
public class PortalApiConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final String appName;
	private final String userName;
	private final String password;
	private final String decryptor;
	private final String userCookie;
	private final String userFilePath;

	public PortalApiConfiguration(@Value("${portalapi.appname}") final String appname, //
			@Value("${portalapi.username}") final String username, //
			@Value("${portalapi.password}") final String password, //
			@Value("${portalapi.decryptor}") final String decryptor, //
			@Value("${portalapi.usercookie}") final String usercookie, //
			@Value("${portalapi.userfile}") final String userFilePath) {
		logger.debug("ctor: user file path {}", userFilePath);
		this.appName = appname;
		this.userName = username;
		this.password = password;
		this.decryptor = decryptor;
		this.userCookie = usercookie;
		this.userFilePath = userFilePath;
	}

	/**
	 * Instantiates the EPSDK-FW servlet manually. Needed because Spring-Boot does
	 * not automatically process @WebServlet annotations.
	 * 
	 * @return Servlet registration bean for the Portal Rest API proxy servlet.
	 */
	@Bean
	public ServletRegistrationBean<PortalRestAPIProxy> portalApiProxy() {
		final ServletRegistrationBean<PortalRestAPIProxy> servlet = new ServletRegistrationBean<>(
				new PortalRestAPIProxy(), PortalApiConstants.API_PREFIX + "/*");
		servlet.setName("PortalRestApiProxy");
		return servlet;
	}

	@Bean
	public PortalAuthManager portalAuthManager()
			throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		return new PortalAuthManager(appName, userName, password, decryptor, userCookie);
	}

	@Bean
	public DashboardUserManager portalUserManager() throws IOException {
		return new DashboardUserManager(userFilePath);
	}

}
