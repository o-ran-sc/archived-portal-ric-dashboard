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
package org.oranosc.ric.portal.dash.config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.oranosc.ric.e2mgr.client.api.DefaultApi;
import org.oranosc.ric.e2mgr.client.invoker.ApiClient;
import org.oranosc.ric.e2mgr.client.model.RanSetupRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;

/**
 * Creates an implementation of the E2 manager client that answers requests with
 * mock data.
 */
@Profile("mock")
@Configuration
public class E2ManagerMockConfiguration {

	@Bean
	@Primary
	public DefaultApi e2ManagerMockClient() {
		ApiClient mockClient = mock(ApiClient.class);
		when(mockClient.getStatusCode()).thenReturn(HttpStatus.OK);

		DefaultApi mockApi = mock(DefaultApi.class);
		when(mockApi.getApiClient()).thenReturn(mockClient);

		doAnswer(i -> {
			return null;
		}).when(mockApi).getHealth();

		doAnswer(i -> {
			return null;
		}).when(mockApi).setupRan(any(RanSetupRequest.class));

		return mockApi;
	}

}
