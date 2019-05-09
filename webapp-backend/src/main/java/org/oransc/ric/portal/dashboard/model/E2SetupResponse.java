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

import java.time.Instant;

import org.oransc.ric.e2mgr.client.model.SetupRequest;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response to an E2 Manager setupRequest message carries the original
 * information plus request type, timestamp and HTTP response code.
 */
public class E2SetupResponse extends SetupRequest implements IDashboardResponse {

	public E2SetupResponse() {
	}

	public E2SetupResponse(E2SetupRequestType type, SetupRequest request, int responseCode) {
		super.ranName(request.getRanName()).ranIp(request.getRanIp()).ranPort(request.getRanPort());
		this.requestType = type;
		this.timeStamp = Instant.now();
		this.responseCode = responseCode;
	}

	@JsonProperty("requestType")
	private E2SetupRequestType requestType = null;

	public SetupRequest requestType(E2SetupRequestType type) {
		this.requestType = type;
		return this;
	}

	/**
	 * Get requestType
	 * 
	 * @return requestType
	 **/
	public E2SetupRequestType getRequestType() {
		return requestType;
	}

	public void setRequestType(E2SetupRequestType type) {
		this.requestType = type;
	}

	@JsonProperty("timeStamp")
	private Instant timeStamp = null;

	public SetupRequest timeStamp(Instant timeStamp) {
		this.timeStamp = timeStamp;
		return this;
	}

	/**
	 * Get timeStamp
	 * 
	 * @return timeStamp
	 **/
	public Instant getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Instant timeStamp) {
		this.timeStamp = timeStamp;
	}

	@JsonProperty("responseCode")
	private Integer responseCode = null;

	public SetupRequest responseCode(Integer responseCode) {
		this.responseCode = responseCode;
		return this;
	}

	/**
	 * Get responseCode
	 * 
	 * @return responseCode
	 **/
	public Integer getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(Integer responseCode) {
		this.responseCode = responseCode;
	}

}
