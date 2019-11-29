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

package org.oransc.ric.portal.dashboard.model;

/**
 * Trivial model to transport available application details from the App
 * manager.
 */
public class AppTransport implements IDashboardResponse {

	private String name;
	private String version;

	/**
	 * Builds an empty object.
	 */
	public AppTransport() {
		// no-arg constructor
	}

	/**
	 * Builds an object with the specified value.
	 * 
	 * @param s
	 *              value to transport.
	 */
	public AppTransport(String s) {
		this.name = s;
	}

	public String getName() {
		return name;
	}

	public void setName(String s) {
		this.name = s;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[name=" + getName() + ", version=" + getVersion() + "]";
	}

}
