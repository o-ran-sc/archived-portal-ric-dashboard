/*-
 * ========================LICENSE_START=================================
 * O-RAN-SC
 * %%
 * Copyright (C) 2019 - 2020 AT&T Intellectual Property
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

public class StatsResponse {
	
    //private String failureType; 
    private String appName;
    private String metricUrl;
	public StatsResponse(String appName, String metricUrl) {
		super();
		//this.failureType = failureType;
		this.appName = appName;
		this.metricUrl = metricUrl;
	}
	/*public String getFailureType() {
		return failureType;
	}
	public void setFailureType(String failureType) {
		this.failureType = failureType;
	}*/
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getMetricUrl() {
		return metricUrl;
	}
	public void setMetricUrl(String metricUrl) {
		this.metricUrl = metricUrl;
	}
    
    
    
    
}
