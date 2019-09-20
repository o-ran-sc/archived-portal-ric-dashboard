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
package org.oransc.ric.portal.dashboard.controller;

import java.lang.invoke.MethodHandles;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.servlet.http.HttpServletResponse;

import org.oransc.ric.portal.dashboard.DashboardConstants;
import org.oransc.ric.portal.dashboard.k8sapi.SimpleKubernetesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;

/**
 * Proxies calls from the front end to a CAAS-Ingress API, which is a proxy for
 * a Kubernetes API.
 * 
 * If a method throws RestClientResponseException, it is handled by
 * {@link CustomResponseEntityExceptionHandler#handleProxyMethodException(Exception, org.springframework.web.context.request.WebRequest)}
 * which returns status 502. All other exceptions are handled by Spring which
 * returns status 500.
 */
@Configuration
@RestController
@RequestMapping(value = CaasIngressController.CONTROLLER_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class CaasIngressController {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	// Publish constants so tests are easy to write
	public static final String CONTROLLER_PATH = DashboardConstants.ENDPOINT_PREFIX + "/caas-ingress";
	// Endpoints
	public static final String PODS_METHOD = "/pods";
	// Path parameters
	public static final String PP_NAMESPACES = "namespaces";
	// Parameter values
	public static final String NAMESPACE_RICAUX = "ricaux";
	public static final String NAMESPACE_RICPLT = "ricplt";

	private final SimpleKubernetesClient ciAuxClient;
	private final SimpleKubernetesClient ciPltClient;

	@Autowired
	public CaasIngressController(final SimpleKubernetesClient ciAuxApi, final SimpleKubernetesClient ciPltApi)
			throws KeyManagementException, NoSuchAlgorithmException {
		Assert.notNull(ciAuxApi, "aux must not be null");
		Assert.notNull(ciPltApi, "plt must not be null");
		this.ciAuxClient = ciAuxApi;
		this.ciPltClient = ciPltApi;
		if (logger.isDebugEnabled())
			logger.debug("ctor: configured with aux client {}, plt client {}", ciAuxClient.getClass().getName(),
					ciPltClient.getClass().getName());
	}

	/*
	 * No need to parse the V1PodList, just pass thru as a string.
	 */
	@ApiOperation(value = "Gets list of pods in the Kubernetes cluster", response = String.class)
	@GetMapping(PODS_METHOD + "/" + PP_NAMESPACES + "/{" + PP_NAMESPACES + "}")
	@Secured({ DashboardConstants.ROLE_STANDARD })
	public String listPods(@PathVariable(PP_NAMESPACES) String namespace, HttpServletResponse response) {
		logger.debug("listPods: namespace {}", namespace);
		if (NAMESPACE_RICAUX.equalsIgnoreCase(namespace)) {
			return ciAuxClient.listPods(namespace);
		} else if (NAMESPACE_RICPLT.equalsIgnoreCase(namespace)) {
			return ciPltClient.listPods(namespace);
		} else {
			logger.warn("listPods: unknown namespace " + namespace);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			return null;
		}
	}

}
