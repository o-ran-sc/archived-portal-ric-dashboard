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
package org.oransc.ric.portal.dashboard.k8sapi;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

public class CaasIngressTest {

	@Test
	public void coverHttpsUtils() throws Exception {
		// Get IP address from REC deployment team for testing
		final String podsUrl = "https://localhost:16443/api/v1/namespaces/ricaux/pods";
		RestTemplate rt = new RestTemplate();
		Assertions.assertThrows(Exception.class, () -> {
			rt.getForEntity(podsUrl, String.class);
		});

	}

}
