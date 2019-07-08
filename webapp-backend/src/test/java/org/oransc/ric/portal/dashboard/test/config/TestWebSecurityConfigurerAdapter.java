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
package org.oransc.ric.portal.dashboard.test.config;

import org.oransc.ric.portal.dashboard.DashboardConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Defines a well-known user and password for use in automated tests.
 */
@Configuration
@EnableWebSecurity
@Profile("test")
public class TestWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

	public static final String TESTUSER = "testuser";
	public static final String TESTPASS = "testpass";

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth //
				.inMemoryAuthentication() //
				.withUser(TESTUSER) //
				.password(passwordEncoder().encode(TESTPASS)) //
				.roles(DashboardConstants.USER_ROLE_PRIV); // read must work for priv also!
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable() //
				.authorizeRequests() //
				.anyRequest().authenticated() //
				.and().httpBasic();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
