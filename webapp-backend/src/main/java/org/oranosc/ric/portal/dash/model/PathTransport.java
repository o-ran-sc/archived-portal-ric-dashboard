/*-
 * ========================LICENSE_START=================================
 * ORAN-OSC
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

package org.oranosc.ric.portal.dash.model;

/**
 * Trivial model to transport a path, to be serialized as JSON.
 */
public class PathTransport implements IDashboardResponse {

	private String path;

	/**
	 * Builds an empty object.
	 */
	public PathTransport() {
		// no-arg constructor
	}

	/**
	 * Builds an object with the specified value.
	 * 
	 * @param s
	 *              value to transport.
	 */
	public PathTransport(String s) {
		this.path = s;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String s) {
		this.path = s;
	}

}
