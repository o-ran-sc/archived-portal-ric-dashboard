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

import java.lang.invoke.MethodHandles;

import org.oransc.ric.portal.dashboard.DashboardConstants;
import org.oransc.ric.portal.dashboard.portalapi.PortalAuthManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
@Profile("test")
public class WebSecurityMockConfiguration extends WebSecurityConfigurerAdapter {

	public static final String TEST_CRED_ADMIN = "admin";
	public static final String TEST_CRED_STANDARD = "standard";

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	// Although constructor arguments are recommended over field injection,
	// this results in fewer lines of code.
	@Value("${portalapi.security}")
	private Boolean portalapiSecurity;
	@Value("${portalapi.appname}")
	private String appName;
	@Value("${portalapi.username}")
	private String portalApiUsername;
	@Value("${portalapi.password}")
	private String portalApiPassword;
	@Value("${portalapi.decryptor}")
	private String decryptor;
	@Value("${portalapi.usercookie}")
	private String userCookie;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		logger.debug("configure");
		PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
		auth.inMemoryAuthentication() //
				.passwordEncoder(encoder) //
				// The admin user has the admin AND standard roles
				.withUser(TEST_CRED_ADMIN) //
				.password(encoder.encode(TEST_CRED_ADMIN))
				.roles(DashboardConstants.ROLE_NAME_ADMIN, DashboardConstants.ROLE_NAME_STANDARD)//
				.and()//
				// The standard user has only the standard role
				.withUser(TEST_CRED_STANDARD) //
				.password(encoder.encode(TEST_CRED_STANDARD)) //
				.roles(DashboardConstants.ROLE_NAME_STANDARD);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().anyRequest().authenticated()//
				.and().httpBasic() //
				.and().csrf().disable();
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		// This disables Spring security, but not the app's filter.
		web.ignoring().antMatchers(WebSecurityConfiguration.OPEN_PATHS);
		web.ignoring().antMatchers("/", "/csrf"); // allow swagger-ui to load
	}

	@Bean
	public PortalAuthManager portalAuthManagerBean() throws Exception {
		logger.debug("portalAuthManagerBean: app {}", appName);
		return new PortalAuthManager(appName, portalApiUsername, portalApiPassword, decryptor, userCookie);
	}

}
