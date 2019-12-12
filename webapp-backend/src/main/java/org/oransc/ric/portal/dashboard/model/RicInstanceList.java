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

import org.oransc.ric.portal.dashboard.exception.UnknownInstanceException;

public class RicInstanceList {

	private final List<RicInstance> instances;

	public RicInstanceList() {
		this.instances = new ArrayList<>();
	}

	public RicInstanceList(List<RicInstance> list) {
		this.instances = list;
	}

	public List<RicInstance> getInstances() {
		return instances;
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

	/**
	 * Gets the instance with the specified key
	 * 
	 * @param instanceKey
	 *                        Key to fetch
	 * @return Instance
	 * @throws UnknownInstanceException
	 *                                      If the key is not known
	 */
	public RicInstance getInstance(String instanceKey) {
		for (RicInstance i : instances)
			if (i.getKey().equals(instanceKey))
				return i;
		throw new UnknownInstanceException(instanceKey);
	}
}
