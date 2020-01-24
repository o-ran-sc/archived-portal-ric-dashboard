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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Transport model for RIC region which has a list of instances.
 */
public class RicRegion implements IDashboardResponse {

	private String name;
	private List<RicInstance> instances;

	/**
	 * Builds an empty object.
	 */
	public RicRegion() {
		// no-arg constructor
	}

	/**
	 * Convenience constructor for minimal value set.
	 * 
	 * @param name
	 *                 Name
	 */
	public RicRegion(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String s) {
		this.name = s;
	}

	public RicRegion name(String name) {
		this.name = name;
		return this;
	}

	public List<RicInstance> getInstances() {
		return instances;
	}

	public void setInstances(List<RicInstance> instances) {
		this.instances = instances;
	}

	/**
	 * Gets a list of key-name pairs.
	 * 
	 * @return List of RicInstanceKeyName objects.
	 */
	public List<RicInstanceKeyName> getKeyNameList() {
		List<RicInstanceKeyName> list = new ArrayList<>();
		for (RicInstance i : instances)
			list.add(i.toKeyName());
		return list;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "[name=" + getName() + ", instances=" + instances + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = result * result + ((instances == null) ? 0 : instances.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RicRegion other = (RicRegion) obj;
		return Objects.equals(name, other.name) && instances.size() == other.instances.size();
	}

}
