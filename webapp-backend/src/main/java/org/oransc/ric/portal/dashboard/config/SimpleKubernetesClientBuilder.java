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
package org.oransc.ric.portal.dashboard.config;

import java.lang.invoke.MethodHandles;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.oransc.ric.portal.dashboard.k8sapi.SimpleKubernetesClient;
import org.oransc.ric.portal.dashboard.model.RicInstance;
import org.oransc.ric.portal.dashboard.model.RicRegionList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.DefaultUriBuilderFactory;

/**
 * Creates, caches and serves out instances of SimpleKubernetesClient for a
 * given instance. That class seems to be thread safe.
 */
public class SimpleKubernetesClientBuilder {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	private Map<String, SimpleKubernetesClient> cache = new ConcurrentHashMap<>();

	private final String urlSuffix;
	private final RicRegionList instanceConfig;

	public SimpleKubernetesClientBuilder(final RicRegionList instanceConfig, final String urlSuffix) {
		logger.debug("ctor: suffix {}", urlSuffix);
		this.instanceConfig = instanceConfig;
		this.urlSuffix = urlSuffix;
	}

	public SimpleKubernetesClient getSimpleKubernetesClient(String instanceKey) {
		logger.debug("getSimpleKubernetesClient instance {}", instanceKey);
		if (cache.containsKey(instanceKey))
			return cache.get(instanceKey);
		RicInstance instance = instanceConfig.getInstance(instanceKey);
		String url = new DefaultUriBuilderFactory(instance.getCaasUrlPrefix().trim()).builder()
				.path(this.urlSuffix.trim()).build().normalize().toString();
		SimpleKubernetesClient client = new SimpleKubernetesClient(url);
		cache.put(instanceKey, client);
		return client;
	}

}
