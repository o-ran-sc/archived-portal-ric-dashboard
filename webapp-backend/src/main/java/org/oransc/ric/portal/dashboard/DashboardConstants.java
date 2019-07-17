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
package org.oransc.ric.portal.dashboard;

public abstract class DashboardConstants {

	private DashboardConstants() {
		// Sonar insists on hiding the constructor
	}

	public static final String ENDPOINT_PREFIX = "/api";

	public static final String LOGIN_PAGE = "/login.html";

	// Factor out method names used in multiple controllers
	public static final String VERSION_METHOD = "version";

	// The role names are defined by ONAP Portal.
	// The prefix "ROLE_" is required by Spring
	public static final String ROLE_NAME_USER = "standard";
	public static final String ROLE_NAME_ADMIN = "admin";
	private static final String ROLE_PREFIX = "ROLE_";
	public static final String ROLE_ADMIN = ROLE_PREFIX + ROLE_NAME_ADMIN;
	public static final String ROLE_USER = ROLE_PREFIX + ROLE_NAME_USER;

}
