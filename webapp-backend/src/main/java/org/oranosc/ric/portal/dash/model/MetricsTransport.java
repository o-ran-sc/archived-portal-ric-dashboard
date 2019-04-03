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
 * Trivial model to transport a batch of numbers, to be serialized as JSON.
 */
public class MetricsTransport implements IDashboardResponse {

	private Integer latency;
	private Integer load;
	private Integer time;

	public MetricsTransport() {
	}

	public MetricsTransport(Integer latency, Integer load, Integer time) {
		this.latency = latency;
		this.load = load;
		this.time = time;
	}

	public Integer getLatency() {
		return latency;
	}

	public void setLatency(Integer latency) {
		this.latency = latency;
	}

	public Integer getLoad() {
		return load;
	}

	public void setLoad(Integer load) {
		this.load = load;
	}

	public Integer getTime() {
		return time;
	}

	public void setTime(Integer time) {
		this.time = time;
	}

}
