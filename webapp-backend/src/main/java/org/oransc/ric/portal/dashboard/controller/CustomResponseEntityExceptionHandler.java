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

import org.oransc.ric.portal.dashboard.model.ErrorTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
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
public class CustomResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

	// Superclass has "logger" that is exposed here, so use a different name
	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	/*
	 * Generates the response when a REST controller method takes an
	 * HttpStatusCodeException.
	 * 
	 * It appears that the container internally redirects to /error because the web
	 * request that arrives here has URI /error, and {@link
	 * org.oransc.ric.portal.dashboard.controller.SimpleErrorController} runs before
	 * this.
	 * 
	 * @param ex The exception
	 * 
	 * @param request The original request
	 * 
	 * @return A response entity with status code 502 plus some details in the body.
	 */
	@ExceptionHandler(HttpStatusCodeException.class)
	public final ResponseEntity<ErrorTransport> handleHttpStatusCodeException(HttpStatusCodeException ex,
			WebRequest request) {
		log.warn("handleHttpStatusCodeException: request {}, status code {}", request.getDescription(false),
				ex.getStatusCode());
		return new ResponseEntity<>(new ErrorTransport(ex.getRawStatusCode(), ex.getResponseBodyAsString(), ex),
				HttpStatus.BAD_GATEWAY);
	}

	/*
	 * This exception also happens when Spring security denies access to a method
	 * due to missing/wrong roles (granted authorities). Override the method to
	 * answer permission denied, even though that may obscure a genuine developer
	 * error.
	 * 
	 * The web request that arrives here has URI /error; how to obtain the URI of
	 * the original request?!?
	 */
	@Override
	public final ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		log.warn("handleHttpRequestMethodNotSupported: answering 'permission denied' for method {}", ex.getMethod());
		return new ResponseEntity<Object>(new ErrorTransport(HttpStatus.UNAUTHORIZED.value(),
				"Permission denied for method " + ex.getMethod(), ex), HttpStatus.UNAUTHORIZED);
	}

}
