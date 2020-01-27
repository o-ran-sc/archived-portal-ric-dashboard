/*-
 * ========================LICENSE_START=================================
 * O-RAN-SC
 * %%
 * Copyright (C) 2020 AT&T Intellectual Property
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

import org.oransc.ric.portal.dashboard.exception.UnknownInstanceException;

/**
 * Used as a bean to publish configuration data in a convenient way.
 */
public class RicRegionList {

	private final List<RicRegion> regions;

	public RicRegionList() {
		this.regions = new ArrayList<>();
	}

	public RicRegionList(List<RicRegion> list) {
		this.regions = list;
	}

	public List<RicRegion> getRegions() {
		return regions;
	}

	/**
	 * Builds a response that has only key-name pairs.
	 * 
	 * @return List of RicRegionTransport objects
	 */
	public List<RicRegionTransport> getSimpleInstances() {
		List<RicRegionTransport> response = new ArrayList<>();
		for (RicRegion r : regions)
			response.add(new RicRegionTransport().name(r.getName()).instances(r.getKeyNameList()));
		return response;
	}

	/**
	 * Gets the instance with the specified key in any region
	 * 
	 * @param instanceKey
	 *                        Key to fetch
	 * @return Instance
	 * @throws UnknownInstanceException
	 *                                      If the key is not known
	 */
	public RicInstance getInstance(String instanceKey) {
		for (RicRegion r : regions)
			for (RicInstance i : r.getInstances())
				if (i.getKey().equals(instanceKey))
					return i;
		throw new UnknownInstanceException(instanceKey);
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "[regions=" + regions + "]";
	}

}
