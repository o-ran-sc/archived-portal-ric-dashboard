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
/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
 * ===================================================================================
 * This Acumos software file is distributed by AT&T and Tech Mahindra
 * under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * This file is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ===============LICENSE_END=========================================================
 */
package org.oransc.ric.portal.dashboard.controller;

import java.lang.invoke.MethodHandles;

import org.oransc.ric.portal.dashboard.model.ErrorTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Catches Http status code exceptions and builds a response with code 502 and
 * some details wrapped in an ErrorTransport object. This factors out try-catch
 * blocks in many controller methods.
 * 
 * Why 502? I quote: <blockquote>HTTP server received an invalid response from a
 * server it consulted when acting as a proxy or gateway.</blockquote>
 *
 * This class and the methods are not strictly necessary, the
 * SimpleErrorController is invoked when any controller method takes an uncaught
 * exception, but this approach provides a better response to the front end and
 * doesn't signal internal server error.
 * 
 * Also see:<br>
 * https://www.baeldung.com/exception-handling-for-rest-with-spring
 * https://www.springboottutorial.com/spring-boot-exception-handling-for-rest-services
 */
@ControllerAdvice
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	/**
	 * Generates the response when a REST controller method takes an
	 * HttpStatusCodeException. Confusingly, the container first redirects to /error
	 * which invokes the
	 * {@link org.oransc.ric.portal.dashboard.controller.SimpleErrorController}
	 * method, and that response arrives here as the response body.
	 * 
	 * @param ex
	 *                    The exception
	 * @param request
	 *                    The orignal request
	 * @return A response entity with status code 502 plus some details in the body.
	 */
	@ExceptionHandler(HttpStatusCodeException.class)
	public final ResponseEntity<?> handleHttpStatusCodeException(HttpStatusCodeException ex, WebRequest request) {
		logger.warn("Request {} failed, status code {}", request.getDescription(false), ex.getStatusCode());
		return new ResponseEntity<ErrorTransport>(
				new ErrorTransport(ex.getRawStatusCode(), ex.getResponseBodyAsString(), ex), HttpStatus.BAD_GATEWAY);
	}

}
