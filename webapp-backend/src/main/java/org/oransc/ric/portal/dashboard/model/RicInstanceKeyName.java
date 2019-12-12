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

import java.util.Objects;

/**
 * Transport model for RIC instance key-name pairs.
 */
public class RicInstanceKeyName implements IDashboardResponse {

	private String key;
	private String name;

	/**
	 * Builds an empty object.
	 */
	public RicInstanceKeyName() {
		// no-arg constructor
	}

	/**
	 * Convenience constructor for minimal value set.
	 * 
	 * @param key
	 *                 Key
	 * @param name
	 *                 Name
	 */
	public RicInstanceKeyName(String key, String name) {
		this.key = key;
		this.name = name;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public RicInstanceKeyName key(String key) {
		this.key = key;
		return this;
	}

	public String getName() {
		return name;
	}

	public void setName(String s) {
		this.name = s;
	}

	public RicInstanceKeyName name(String name) {
		this.name = name;
		return this;
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[key=" + getKey() + ", name=" + getName() + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		RicInstanceKeyName other = (RicInstanceKeyName) obj;
		return Objects.equals(key, other.key);
	}

}
