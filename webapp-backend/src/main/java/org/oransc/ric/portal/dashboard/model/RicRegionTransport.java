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

import java.util.List;

/**
 * Transport model for RIC region which has a list of instances with ONLY
 * key-name pairs.
 */
public class RicRegionTransport implements IDashboardResponse {

	private String name;
	private List<RicInstanceKeyName> instances;

	public String getName() {
		return name;
	}

	public RicRegionTransport name(String name) {
		this.name = name;
		return this;
	}

	public List<RicInstanceKeyName> getInstances() {
		return instances;
	}

	public RicRegionTransport instances(List<RicInstanceKeyName> instances) {
		this.instances = instances;
		return this;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "[name=" + getName() + ", instances=" + instances + "]";
	}

}
