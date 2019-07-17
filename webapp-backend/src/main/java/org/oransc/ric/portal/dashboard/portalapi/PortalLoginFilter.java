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
import javax.servlet.http.HttpSession;

import org.onap.portalsdk.core.onboarding.listener.PortalTimeoutHandler;
import org.onap.portalsdk.core.onboarding.util.PortalApiConstants;
import org.onap.portalsdk.core.onboarding.util.PortalApiProperties;
import org.oransc.ric.portal.dashboard.DashboardConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This filter checks every request for proper Portal single sign on
 * initialization. The possible paths and actions:
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
 * <LI>Portal Session should be up prior to App Session</LI>
 * <LI>If App Session Expires or if EPService cookie is unavailable, we need to
 * redirect to Portal.
 * <LI>Method {@link #initiateSessionMgtHandler(HttpServletRequest)} should be
 * called for Session management when the initial session is created
 * <LI>While redirecting, the cookie "redirectUrl" should also be set so that
 * Portal knows where to forward the request to once the Portal Session is
 * created and EPService cookie is set.
 * <LI>Method {@link #resetSessionMaxIdleTimeOut(HttpServletRequest)} should be
 * called for every request to reset the MaxInactiveInterval to the right value.
 * </UL>
 * <P>
 * This filter incorporates most features of the SDK application's
 * SessionTimeoutInterceptor and SingleSignOnController classes
 */
public class PortalLoginFilter implements Filter {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public static final String REDIRECT_URL_KEY = "redirectUrl";

	private final PortalAuthManager authManager;

	public PortalLoginFilter(final PortalAuthManager authManager) {
		this.authManager = authManager;
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		logger.debug("init");
		// complain loudly if this key property is missing
		String url = PortalApiProperties.getProperty(PortalApiConstants.ECOMP_REDIRECT_URL);
		if (url == null)
			logger.error(
					"init: Failed to find property in portal.properties: " + PortalApiConstants.ECOMP_REDIRECT_URL);
	}

	@Override
	public void destroy() {
		// No resources to release
	}

	/**
	 * Checks for valid cookies and allows request to be served if found, redirects
	 * to Portal otherwise.
	 */
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		logger.trace("doFilter {}", req);
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		// Choose authentication appropriate for the request.
		final String restApiURI = request.getContextPath() + PortalApiConstants.API_PREFIX;
		final String loginURI = request.getContextPath() + DashboardConstants.LOGIN_PAGE;
		if (request.getRequestURI().startsWith(restApiURI) //
				|| request.getRequestURI().startsWith(loginURI)) {
			// Do not enforce Portal security on these endpoints
			logger.trace("doFilter: delegating auth for URI {}", request.getRequestURI());
			chain.doFilter(request, response);
		} else {
			// Need to authenticate the request
			if (authManager.valdiateEcompSso(request) == null) {
				String redirectURL = buildLoginPageUrl(request);
				logger.trace("doFilter: unauthorized, redirecting to {}", redirectURL);
				response.sendRedirect(redirectURL);
			} else {
				HttpSession session = request.getSession(false);
				if (session == null) {
					session = request.getSession(true);
					initiateSessionMgtHandler(request);
					logger.trace("doFilter: created new session {}", session);
				} else {
					// Existing session
					resetSessionMaxIdleTimeOut(request);
					logger.trace("doFilter: reset timeout in existing session {} ", session);
				}
				// Pass request back down the filter chain
				chain.doFilter(request, response);
			}
		}
	}

	// TODO: make login page configurable
	private String buildLoginPageUrl(HttpServletRequest request) {
		logger.debug("buildLoginPageUrl");
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
	 * Publishes information about the session.
	 * 
	 * @param request
	 */
	private void initiateSessionMgtHandler(HttpServletRequest request) {
		String portalSessionId = getPortalSessionId(request);
		String localSessionId = getLocalSessionId(request);
		storeMaxInactiveTime(request);
		PortalTimeoutHandler.sessionCreated(portalSessionId, localSessionId, request.getSession(false));
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

	/**
	 * Gets the container session ID.
	 * 
	 * @param request
	 * @return Session ID, or null if no session.
	 */
	private String getLocalSessionId(HttpServletRequest request) {
		HttpSession session = request.getSession();
		if (session == null)
			return null;
		return session.getId();
	}

	/**
	 * Sets the global session's max idle time to the session's max inactive
	 * interval.
	 * 
	 * @param request
	 */
	private void storeMaxInactiveTime(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null)
			return;
		if (session.getAttribute(PortalApiConstants.GLOBAL_SESSION_MAX_IDLE_TIME) == null)
			session.setAttribute(PortalApiConstants.GLOBAL_SESSION_MAX_IDLE_TIME, session.getMaxInactiveInterval());
	}

	/**
	 * Sets the session's max inactive interval.
	 * 
	 * @param request
	 */
	private void resetSessionMaxIdleTimeOut(HttpServletRequest request) {
		logger.debug("resetSessionMaxIdleTimeOut");
		try {
			HttpSession session = request.getSession(false);
			final Object maxIdleAttribute = session.getAttribute(PortalApiConstants.GLOBAL_SESSION_MAX_IDLE_TIME);
			if (session != null && maxIdleAttribute != null)
				session.setMaxInactiveInterval(Integer.parseInt(maxIdleAttribute.toString()));
		} catch (Exception e) {
			logger.error("resetSessionMaxIdleTimeOut: failed to set session max inactive interval", e);
		}
	}

}
