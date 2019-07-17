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
package org.oransc.ric.portal.dashboard.portalapi;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.invoke.MethodHandles;
import java.net.URLEncoder;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.onap.portalsdk.core.onboarding.util.PortalApiConstants;
import org.onap.portalsdk.core.onboarding.util.PortalApiProperties;
import org.onap.portalsdk.core.restful.domain.EcompUser;
import org.oransc.ric.portal.dashboard.DashboardConstants;
import org.oransc.ric.portal.dashboard.model.EcompUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

/**
 * This filter checks every request for the cookie set by the ONAP Portal single
 * sign on process. The possible paths and actions:
 * <OL>
 * <LI>User starts at an app page via a bookmark. No Portal cookie is set.
 * Redirect there to get one; then continue as below.
 * <LI>User starts at Portal and goes to app. Alternately, the user's session
 * times out and the user hits refresh. The Portal cookie is set, but there is
 * no valid session. Create one and publish info.
 * <LI>User has valid Portal cookie and session. Reset the max idle in that
 * session.
 * </OL>
 * <P>
 * Notes:
 * <UL>
 * <LI>While redirecting, the cookie "redirectUrl" should also be set so that
 * Portal knows where to forward the request to once the Portal Session is
 * created and EPService cookie is set.
 * </UL>
 * 
 * TODO: What about sessions? Will this be stateless?
 * 
 * This filter uses no annotations to avoid Spring's automatic registration,
 * which add this filter in the chain in the wrong order.
 */
public class PortalAuthenticationFilter implements Filter {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public static final String REDIRECT_URL_KEY = "redirectUrl";

	private final PortalAuthManager authManager;

	private final DashboardUserManager userManager;

	public PortalAuthenticationFilter(PortalAuthManager authManager, DashboardUserManager userManager) {
		this.authManager = authManager;
		this.userManager = userManager;
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// complain loudly if this key property is missing
		String url = PortalApiProperties.getProperty(PortalApiConstants.ECOMP_REDIRECT_URL);
		logger.debug("init: Portal redirect URL {}", url);
		if (url == null)
			logger.error(
					"init: Failed to find property in portal.properties: " + PortalApiConstants.ECOMP_REDIRECT_URL);
	}

	@Override
	public void destroy() {
		// No resources to release
	}

	/**
	 * Checks for valid cookies and allows request to be served if found; redirects
	 * to Portal otherwise. Requests for pages ignored in the web security config do
	 * not hit this filter.
	 */
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		logger.debug("doFilter {}", req);
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		// Need to authenticate the request
		final String userId = authManager.valdiateEcompSso(request);
		final EcompUser ecompUser = (userId == null ? null : userManager.getUser(userId));
		if (userId == null || ecompUser == null) {
			String redirectURL = buildLoginPageUrl(request);
			logger.trace("doFilter: unauthorized, redirecting to {}", redirectURL);
			response.sendRedirect(redirectURL);
		} else {
			EcompUserDetails userDetails = new EcompUserDetails(ecompUser);
			// Using portal session as credentials is a hack
			PreAuthenticatedAuthenticationToken authToken = new PreAuthenticatedAuthenticationToken(userDetails,
					getPortalSessionId(request), userDetails.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(authToken);
			// Pass request back down the filter chain
			chain.doFilter(request, response);
		}
	}

	private String buildLoginPageUrl(HttpServletRequest request) {
		logger.trace("buildLoginPageUrl");
		// Why so much work to recover the original request?
		final StringBuffer sb = request.getRequestURL();
		sb.append(request.getQueryString() == null ? "" : "?" + request.getQueryString());
		final String requestedUrl = sb.toString();
		String encodedUrl = null;
		try {
			encodedUrl = URLEncoder.encode(requestedUrl, "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			logger.error("buildLoginPageUrl: Failed to encode {}", requestedUrl);
		}
		return DashboardConstants.LOGIN_PAGE + "?" + REDIRECT_URL_KEY + "=" + encodedUrl;
	}

	/**
	 * Searches the request for a cookie with the specified name.
	 *
	 * @param request
	 *                       HttpServletRequest
	 * @param cookieName
	 *                       Cookie name
	 * @return Cookie, or null if not found.
	 */
	private Cookie getCookie(HttpServletRequest request, String cookieName) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null)
			for (Cookie cookie : cookies)
				if (cookie.getName().equals(cookieName))
					return cookie;
		return null;
	}

	/**
	 * Gets the ECOMP Portal service cookie value.
	 * 
	 * @param request
	 * @return Cookie value, or null if not found.
	 */
	private String getPortalSessionId(HttpServletRequest request) {
		Cookie ep = getCookie(request, PortalApiConstants.EP_SERVICE);
		if (ep == null)
			return null;
		return ep.getValue();
	}

}
