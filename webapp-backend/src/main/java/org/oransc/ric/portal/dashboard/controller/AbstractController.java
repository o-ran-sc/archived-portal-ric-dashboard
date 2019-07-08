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

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.access.AccessDeniedException;

public abstract class AbstractController {

	/**
	 * Uses the request object to test if the user is in any of the named roles.
	 * 
	 * @param request
	 *                      HttpServletRequest
	 * @param roleNames
	 *                      Role names
	 * @throws AccessDeniedException
	 *                                   If the user is not in any of the named
	 *                                   roles
	 */
	protected void checkRoles(final HttpServletRequest request, final String... roleNames)
			throws AccessDeniedException {
		for (String name : roleNames) {
			if (request.isUserInRole(name))
				return;
		}
		throw new AccessDeniedException("Expected role not found");
	}
}
