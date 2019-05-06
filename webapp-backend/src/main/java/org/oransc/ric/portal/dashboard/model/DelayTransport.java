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

package org.oransc.ric.portal.dashboard.model;

/**
 * Trivial model to transport a number, to be serialized as JSON.
 */
public class DelayTransport implements IDashboardResponse {

	private Integer delay;

	/**
	 * Builds an empty object.
	 */
	public DelayTransport() {
		// no-arg constructor
	}

	/**
	 * Builds an object with the specified value.
	 * 
	 * @param i
	 *              value to transport.
	 */
	public DelayTransport(Integer i) {
		this.delay = i;
	}

	public Integer getDelay() {
		return delay;
	}

	public void setDelay(Integer i) {
		this.delay = i;
	}

}
