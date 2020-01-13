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


public class StatsDetailsTransport implements IDashboardResponse {

	private StatsIdentity statsIdentity;
	private StatsResponse statsStatus;

	public StatsDetailsTransport() {
	}

	public StatsDetailsTransport(StatsIdentity statsIdentity, StatsResponse statsResponse) {
		this.statsIdentity = statsIdentity;
		this.statsStatus = statsResponse;
	}

	public StatsIdentity getStatsIdentity() {
		return statsIdentity;
	}

	public void setStatsIdentity(StatsIdentity statsIdentity) {
		this.statsIdentity = statsIdentity;
	}

	public StatsResponse getStatsStatus() {
		return statsStatus;
	}

	public void setNodebStatus(StatsResponse statsStatus) {
		this.statsStatus = statsStatus;
	}

	public StatsDetailsTransport statsIdentity(StatsIdentity n) {
		this.statsIdentity = n;
		return this;
	}

	public StatsDetailsTransport nodebStatus(StatsResponse s) {
		this.statsStatus = s;
		return this;
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[statsIdentity=" + getStatsIdentity() + ", statsStatus=" + getStatsStatus()
				+ "]";
	}

}
