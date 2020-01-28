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
package org.oransc.ric.portal.dashboard.config;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.onap.portalsdk.core.onboarding.exception.PortalAPIException;
import org.onap.portalsdk.core.restful.domain.EcompRole;
import org.onap.portalsdk.core.restful.domain.EcompUser;
import org.oransc.ric.portal.dashboard.DashboardUserManager;
import org.oransc.ric.portal.dashboard.StatsManager;
import org.oransc.ric.portal.dashboard.model.Stats;
import org.oransc.ric.portal.dashboard.model.StatsDetailsTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * Creates a user manager with mock data.
 */
@Configuration
@Profile("test")
public class AdminMockConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired
	public AdminMockConfiguration() {
	}

	@Bean
	// The bean (method) name must be globally unique
	public DashboardUserManager userManager() throws IOException, PortalAPIException {
		logger.debug("userManager: adding mock data");
		DashboardUserManager mgr = new DashboardUserManager(true);
		String[] firsts = { "John", "Alice", "Pierce", "Paul", "Jack" };
		String[] lasts = { "Doe", "Nolan", "King", "Smith", "Reacher" };
		String[] logins = { "jdoe", "anolan", "pking", "psmith", "jreacher" };
		boolean[] actives = { true, true, false, false, true };
		EcompRole role = new EcompRole();
		role.setName("view");
		Set<EcompRole> roles = new HashSet<>();
		roles.add(role);
		for (int i = 0; i < firsts.length; ++i) {
			EcompUser eu = new EcompUser();
			eu.setFirstName(firsts[i]);
			eu.setLastName(lasts[i]);
			eu.setLoginId(logins[i]);
			eu.setActive(actives[i]);
			eu.setRoles(roles);
			mgr.createUser(eu);
		}
		return mgr;
	}

	@Bean
	// The bean (method) name must be globally unique
	public StatsManager statsManager() throws IOException {
		logger.debug("statsManager: adding mock data");
		StatsManager mgr = new StatsManager(true);
		String instanceKey = "i1";
		HttpServletResponse response = new HttpServletResponse() {

			@Override
			public void setLocale(Locale loc) {
				// TODO Auto-generated method stub

			}

			@Override
			public void setContentType(String type) {
				// TODO Auto-generated method stub

			}

			@Override
			public void setContentLengthLong(long len) {
				// TODO Auto-generated method stub

			}

			@Override
			public void setContentLength(int len) {
				// TODO Auto-generated method stub

			}

			@Override
			public void setCharacterEncoding(String charset) {
				// TODO Auto-generated method stub

			}

			@Override
			public void setBufferSize(int size) {
				// TODO Auto-generated method stub

			}

			@Override
			public void resetBuffer() {
				// TODO Auto-generated method stub

			}

			@Override
			public void reset() {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean isCommitted() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public PrintWriter getWriter() throws IOException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public ServletOutputStream getOutputStream() throws IOException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Locale getLocale() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getContentType() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getCharacterEncoding() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int getBufferSize() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public void flushBuffer() throws IOException {
				// TODO Auto-generated method stub

			}

			@Override
			public void setStatus(int sc, String sm) {
				// TODO Auto-generated method stub

			}

			@Override
			public void setStatus(int sc) {
				// TODO Auto-generated method stub

			}

			@Override
			public void setIntHeader(String name, int value) {
				// TODO Auto-generated method stub

			}

			@Override
			public void setHeader(String name, String value) {
				// TODO Auto-generated method stub

			}

			@Override
			public void setDateHeader(String name, long date) {
				// TODO Auto-generated method stub

			}

			@Override
			public void sendRedirect(String location) throws IOException {
				// TODO Auto-generated method stub

			}

			@Override
			public void sendError(int sc, String msg) throws IOException {
				// TODO Auto-generated method stub

			}

			@Override
			public void sendError(int sc) throws IOException {
				// TODO Auto-generated method stub

			}

			@Override
			public int getStatus() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public Collection<String> getHeaders(String name) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Collection<String> getHeaderNames() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getHeader(String name) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String encodeUrl(String url) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String encodeURL(String url) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String encodeRedirectUrl(String url) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String encodeRedirectURL(String url) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean containsHeader(String name) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void addIntHeader(String name, int value) {
				// TODO Auto-generated method stub

			}

			@Override
			public void addHeader(String name, String value) {
				// TODO Auto-generated method stub

			}

			@Override
			public void addDateHeader(String name, long date) {
				// TODO Auto-generated method stub

			}

			@Override
			public void addCookie(Cookie cookie) {
				// TODO Auto-generated method stub

			}
		};

		StatsDetailsTransport statsDetails = new StatsDetailsTransport();
		statsDetails.setAppId(0);
		statsDetails.setAppName("Dual");
		statsDetails.setMetricUrl("example.com");
		Stats st = new Stats(instanceKey, statsDetails);
		mgr.createStats(instanceKey, statsDetails, response);

		return mgr;
	}

}
