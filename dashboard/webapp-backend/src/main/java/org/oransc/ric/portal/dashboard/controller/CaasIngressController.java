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
package org.oransc.ric.portal.dashboard.controller;

import java.lang.invoke.MethodHandles;

import org.oransc.ric.portal.dashboard.DashboardConstants;
import org.oransc.ric.portal.dashboard.config.SimpleKubernetesClientBuilder;
import org.oransc.ric.portal.dashboard.k8sapi.SimpleKubernetesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;

/**
 * Proxies calls from the front end to a CAAS-Ingress API, which in turn is a
 * proxy for a Kubernetes API.
 * 
 * If a method throws RestClientResponseException, it is handled by a method in
 * {@link CustomResponseEntityExceptionHandler} which returns status 502. All
 * other exceptions are handled by Spring which returns status 500.
 */
@Configuration
@RestController
@RequestMapping(value = CaasIngressController.CONTROLLER_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class CaasIngressController {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	// Publish constants so tests are easy to write
	public static final String CONTROLLER_PATH = DashboardConstants.ENDPOINT_PREFIX + "/caas-ingress";
	// Endpoints
	public static final String PODS_METHOD = "pods";
	// Path parameters
	public static final String PP_CLUSTER = "cluster";
	public static final String PP_NAMESPACE = "namespace";
	// Parameter values
	public static final String CLUSTER_PLT = "plt";
	public static final String CLUSTER_RIC = "ric"; // alternate for PLT

	// Populated by the autowired constructor
	private final SimpleKubernetesClientBuilder simpleKubernetesClientBuilder;

	@Autowired
	public CaasIngressController(final SimpleKubernetesClientBuilder simpleKubernetesClientBuilder) {
		Assert.notNull(simpleKubernetesClientBuilder, "builder must not be null");
		this.simpleKubernetesClientBuilder = simpleKubernetesClientBuilder;
		if (logger.isDebugEnabled())
			logger.debug("ctor: configured with builder type {}", simpleKubernetesClientBuilder.getClass().getName());
	}

	/*
	 * No need to parse the V1PodList, just pass thru as a string.
	 */
	@ApiOperation(value = "Gets list of pods in the specified cluster for the specified namespace", response = String.class)
	@GetMapping(DashboardConstants.RIC_INSTANCE_KEY + "/{" + DashboardConstants.RIC_INSTANCE_KEY + "}/" + PODS_METHOD
			+ "/" + PP_CLUSTER + "/{" + PP_CLUSTER + "}" + "/" + PP_NAMESPACE + "/{" + PP_NAMESPACE + "}")
	@Secured({ DashboardConstants.ROLE_ADMIN, DashboardConstants.ROLE_STANDARD })
	public ResponseEntity<String> listPods(@PathVariable(DashboardConstants.RIC_INSTANCE_KEY) String instanceKey, //
			@PathVariable(PP_CLUSTER) String cluster, //
			@PathVariable(PP_NAMESPACE) String namespace) {
		logger.debug("listPods: instance {} cluster {} namespace {}", instanceKey, cluster, namespace);
		SimpleKubernetesClient client = simpleKubernetesClientBuilder.getSimpleKubernetesClient(instanceKey);
		if (CLUSTER_PLT.equalsIgnoreCase(cluster) || CLUSTER_RIC.equalsIgnoreCase(cluster)) {
			return ResponseEntity.ok().body(client.listPods(namespace));
		} else {
			final String msg = "listPods: unknown cluster " + cluster;
			logger.warn(msg);
			return ResponseEntity.badRequest().body(msg);
		}
	}

}
