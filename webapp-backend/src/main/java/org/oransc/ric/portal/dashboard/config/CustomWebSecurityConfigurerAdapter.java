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

import java.lang.invoke.MethodHandles;

import org.oransc.ric.portal.dashboard.DashboardConstants;
import org.oransc.ric.portal.dashboard.controller.AdminController;
import org.oransc.ric.portal.dashboard.controller.SimpleErrorController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Profile("!test") // Do not use this when testing
public class CustomWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private static final String REALM_NAME = "RIC-Dashboard";

	/**
	 * Provide open access to the Swagger documentation.
	 */
	@Override
	public void configure(WebSecurity web) throws Exception {
		logger.debug("configure {}", web);
		web.ignoring().antMatchers("/v2/api-docs", "/swagger-resources/**", "/swagger-ui.html", "/webjars/**");
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		logger.debug("configure {}", auth);
		auth //
				.inMemoryAuthentication() //
				.withUser("demo") // TODO
				.password(passwordEncoder().encode("demo")) // TODO
				.roles(DashboardConstants.USER_ROLE_UNPRIV) //
				.and() //
				.withUser("admin") // TODO
				.password(passwordEncoder().encode("admin")) // TODO
				.roles(DashboardConstants.USER_ROLE_UNPRIV, DashboardConstants.USER_ROLE_PRIV);
	}

	/**
	 * Open access to some endpoints, authenticate requests to all others.
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable() //
				.authorizeRequests() //
				.antMatchers(AdminController.CONTROLLER_PATH + "/" + AdminController.LOGIN_METHOD,
						AdminController.CONTROLLER_PATH + "/" + AdminController.VERSION_METHOD,
						SimpleErrorController.ERROR_PATH)
				.permitAll() //
				.anyRequest().authenticated() //
				.and() //
				.httpBasic() //
				.realmName(REALM_NAME) //
				.authenticationEntryPoint(getBasicAuthEntryPoint()) //
		;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * This defines a custom realmn name and logs auth failures.
	 * 
	 * @return Basic authentication entry point
	 */
	@Bean
	public BasicAuthenticationEntryPoint getBasicAuthEntryPoint() {
		BasicAuthenticationEntryPoint baep = new CustomBasicAuthenticationEntryPoint();
		baep.setRealmName(REALM_NAME);
		return baep;
	}

}
