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

import org.oransc.ricplt.e2mgr.client.model.GetNodebResponse;
import org.oransc.ricplt.e2mgr.client.model.NodebIdentity;

public class RanDetailsTransport implements IDashboardResponse {

	private NodebIdentity nodebIdentity;
	private GetNodebResponse nodebStatus;

	public RanDetailsTransport() {
	}

	public RanDetailsTransport(NodebIdentity nodebIdentity, GetNodebResponse nodebResponse) {
		this.nodebIdentity = nodebIdentity;
		this.nodebStatus = nodebResponse;
	}

	public NodebIdentity getNodebIdentity() {
		return nodebIdentity;
	}

	public void setNodebIdentity(NodebIdentity nodebIdentity) {
		this.nodebIdentity = nodebIdentity;
	}

	public GetNodebResponse getNodebStatus() {
		return nodebStatus;
	}

	public void setNodebStatus(GetNodebResponse nodebStatus) {
		this.nodebStatus = nodebStatus;
	}

	public RanDetailsTransport nodebIdentity(NodebIdentity n) {
		this.nodebIdentity = n;
		return this;
	}

	public RanDetailsTransport nodebStatus(GetNodebResponse s) {
		this.nodebStatus = s;
		return this;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "[nodebIdentity=" + getNodebIdentity() + ", nodebStatus="
				+ getNodebStatus() + "]";
	}

}
