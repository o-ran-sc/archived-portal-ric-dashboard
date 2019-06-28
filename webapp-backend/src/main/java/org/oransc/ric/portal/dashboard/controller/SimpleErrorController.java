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
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import springfox.documentation.annotations.ApiIgnore;

/**
 * Provides an endpoint that returns JSON, which is invoked following any error
 * within the Spring-managed context. This is NOT called for errors outside the
 * context; e.g., resource not found.
 * 
 * If trace is requested via request parameter ("?trace=true") and available,
 * adds stack trace information to the standard JSON error response.
 * 
 * Excluded from Swagger API documentation.
 * 
 * https://stackoverflow.com/questions/25356781/spring-boot-remove-whitelabel-error-page
 * https://www.baeldung.com/spring-boot-custom-error-page
 */
@ApiIgnore
@RestController
public class SimpleErrorController implements ErrorController {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private static final String ERROR_PATH = "/error";
	private static final String TRACE = "trace";
	private final ErrorAttributes errorAttributes;

	/**
	 * Constructor
	 * 
	 * @param errorAttributes
	 *                            error attributes must not be null
	 */
	@Autowired
	public SimpleErrorController(ErrorAttributes errorAttributes) {
		Assert.notNull(errorAttributes, "ErrorAttributes must not be null");
		this.errorAttributes = errorAttributes;
	}

	@Override
	public String getErrorPath() {
		return ERROR_PATH;
	}

	/**
	 * Builds a map with error details
	 * 
	 * @param aRequest
	 *                     HttpServletRequest
	 * @return Map of String to Object
	 */
	@RequestMapping(ERROR_PATH)
	public Map<String, Object> error(HttpServletRequest aRequest) {
		Map<String, Object> body = getErrorAttributes(aRequest, getTraceParameter(aRequest));
		logger.warn("Failed in request for {}", body.get("path"));
		body.put("decorated-by", SimpleErrorController.class.getName());
		body.computeIfPresent(TRACE, (key, value) -> body.put(TRACE, ((String) value).split("\n\t")));
		return body;
	}

	private boolean getTraceParameter(HttpServletRequest request) {
		String parameter = request.getParameter(TRACE);
		if (parameter == null)
			return false;
		return !"false".equalsIgnoreCase(parameter);
	}

	private Map<String, Object> getErrorAttributes(HttpServletRequest aRequest, boolean includeStackTrace) {
		WebRequest webRequest = new ServletWebRequest(aRequest);
		return errorAttributes.getErrorAttributes(webRequest, includeStackTrace);
	}

}
