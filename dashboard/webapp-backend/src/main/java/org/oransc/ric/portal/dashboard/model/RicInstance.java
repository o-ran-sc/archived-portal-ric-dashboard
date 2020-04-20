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
 * POJO for RIC instance details.
 */
public class RicInstance extends RicInstanceKeyName {

	/* Entry point for all applications */
	private String appUrlPrefix;
	/* Entry point for all platform components */
	private String pltUrlPrefix;
	/* Entry point for CAAS-Ingress */
	private String caasUrlPrefix;

	/**
	 * Builds an empty object.
	 */
	public RicInstance() {
		super();
	}

	@Override
	public RicInstance key(String key) {
		super.key(key);
		return this;
	}

	@Override
	public RicInstance name(String name) {
		super.name(name);
		return this;
	}

	public RicInstanceKeyName toKeyName() {
		return new RicInstanceKeyName(getKey(), getName());
	}

	public String getAppUrlPrefix() {
		return appUrlPrefix;
	}

	public void setAppUrlPrefix(String urlPrefix) {
		this.appUrlPrefix = urlPrefix;
	}

	public RicInstance appUrlPrefix(String prefix) {
		this.appUrlPrefix = prefix;
		return this;
	}

	public String getPltUrlPrefix() {
		return pltUrlPrefix;
	}

	public void setPltUrlPrefix(String pltUrlPrefix) {
		this.pltUrlPrefix = pltUrlPrefix;
	}

	public RicInstance pltUrlPrefix(String prefix) {
		this.pltUrlPrefix = prefix;
		return this;
	}

	public String getCaasUrlPrefix() {
		return caasUrlPrefix;
	}

	public void setCaasUrlPrefix(String caasUrlPrefix) {
		this.caasUrlPrefix = caasUrlPrefix;
	}

	public RicInstance caasUrlPrefix(String prefix) {
		this.caasUrlPrefix = prefix;
		return this;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "[key=" + getKey() + ", name=" + getName() + ", appUrlPrefix="
				+ appUrlPrefix + ", pltUrlPrefix=" + pltUrlPrefix + ", caasUrlPrefix=" + caasUrlPrefix + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((appUrlPrefix == null) ? 0 : appUrlPrefix.hashCode());
		result = prime * result + ((caasUrlPrefix == null) ? 0 : caasUrlPrefix.hashCode());
		result = prime * result + ((pltUrlPrefix == null) ? 0 : pltUrlPrefix.hashCode());
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
		RicInstance other = (RicInstance) obj;
		return Objects.equals(appUrlPrefix, other.appUrlPrefix) && Objects.equals(pltUrlPrefix, other.pltUrlPrefix);
	}

}
