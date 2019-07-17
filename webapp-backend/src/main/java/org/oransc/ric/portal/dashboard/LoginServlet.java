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
package org.oransc.ric.portal.dashboard;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.URLEncoder;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.onap.portalsdk.core.onboarding.util.PortalApiConstants;
import org.onap.portalsdk.core.onboarding.util.PortalApiProperties;
import org.oransc.ric.portal.dashboard.portalapi.PortalLoginFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

/**
 * Serves a login page that contains a link from configuration to ONAP Portal.
 * This avoids the immediate redirect to Portal that is confusing to users and
 * infuriating to developers.
 * 
 * Basically this is do-it-yourself JSP :)
 */
public class LoginServlet extends HttpServlet {

	private static final long serialVersionUID = 1191385178190976568L;

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		logger.debug("init");
		super.init(servletConfig);
		final String portalURL = PortalApiProperties.getProperty(PortalApiConstants.ECOMP_REDIRECT_URL);
		if (portalURL == null || portalURL.length() == 0)
			throw new ServletException("Failed to get property " + PortalApiConstants.ECOMP_REDIRECT_URL);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		logger.debug("doGet {}", request.getRequestURI());
		// The original page URL should arrive as a query parameter
		String appUrl = request.getParameter(PortalLoginFilter.REDIRECT_URL_KEY);
		// If a user bookmarks the login page, then nothing arrives;
		// use the original URL without the login suffix.
		if (appUrl == null || appUrl.isEmpty()) {
			String loginUrl = request.getRequestURL().toString();
			int indexOfLogin = loginUrl.indexOf(DashboardConstants.LOGIN_PAGE);
			appUrl = loginUrl.substring(0, indexOfLogin);
		}
		String encodedAppUrl = URLEncoder.encode(appUrl,  "UTF-8");
		String portalBaseUrl = PortalApiProperties.getProperty(PortalApiConstants.ECOMP_REDIRECT_URL);
		String redirectUrl = portalBaseUrl + "?" + PortalLoginFilter.REDIRECT_URL_KEY + "=" + encodedAppUrl;
		String aHref = "<a href=\"" + redirectUrl + "\">";
		// If only Java had "here" documents.
		String body = String.join(//
				System.getProperty("line.separator"), //
				"<html>", //
				"<head>", //
				"<title>RIC Dashboard</title>", //
				"<style>", //
				"html, body { ",//
				"  font-family: Helvetica, Arial, sans-serif;",//
				"}",//
				"</style>",//
				"</head>", //
				"<body>", //
				"<h2>RIC Dashboard</h2>", //
				"<h4>Please log in.</h4>", //
				"<p>", //
				aHref,
				"Click here to authenticate at the ONAP Portal</a>", //
				"</p>", //
				"</body>", //
				"</html>");
		writeAndFlush(response, MediaType.TEXT_HTML_VALUE, body);
	}

	/**
	 * Sets the content type and writes the response.
	 * 
	 * @param response
	 * @param contentType
	 * @param responseBody
	 * @throws IOException
	 */
	private void writeAndFlush(HttpServletResponse response, String contentType, String responseBody)
			throws IOException {
		response.setContentType(contentType);
		response.getWriter().print(responseBody);
		response.getWriter().flush();
	}

}
