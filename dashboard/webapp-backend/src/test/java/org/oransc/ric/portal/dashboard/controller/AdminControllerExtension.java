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
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientResponseException;

/**
 * Extends the Admin controller with methods that throw exceptions to support
 * testing.
 */
@Profile("test")
@RestController
@RequestMapping(value = AdminController.CONTROLLER_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminControllerExtension {

	public static final String HTTP_STATUS_CODE_EXCEPTION_METHOD = "hscexception";
	public static final String REST_CLIENT_RESPONSE_EXCEPTION_METHOD = "rcrexception";
	public static final String RUNTIME_EXCEPTION_METHOD = "rexception";

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@GetMapping(HTTP_STATUS_CODE_EXCEPTION_METHOD)
	public void throwHttpStatusCodeException() {
		logger.warn("throwing HttpClientErrorException");
		final String mockResponseBody = "mock http status code exception";
		throw new HttpClientErrorException(HttpStatus.CHECKPOINT, "mock status", mockResponseBody.getBytes(),
				Charset.defaultCharset());
	}

	@GetMapping(REST_CLIENT_RESPONSE_EXCEPTION_METHOD)
	public void throwRestClientResponseException() {
		logger.warn("throwing RestClientResponseException");
		throw new RestClientResponseException("simulate remote client failure", 0, "bar", null, null, null);
	}

	@GetMapping(RUNTIME_EXCEPTION_METHOD)
	public void throwRuntimeException() {
		logger.warn("throwing RuntimeException");
		throw new RuntimeException("simulate runtime failure");
	}

}
