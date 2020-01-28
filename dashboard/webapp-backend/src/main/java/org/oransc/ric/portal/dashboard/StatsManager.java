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
package org.oransc.ric.portal.dashboard;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.onap.portalsdk.core.onboarding.exception.PortalAPIException;
import org.onap.portalsdk.core.restful.domain.EcompUser;
import org.oransc.ric.portal.dashboard.model.StatsDetailsTransport;
import org.oransc.ric.portal.dashboard.model.ErrorTransport;
import org.oransc.ric.portal.dashboard.model.IDashboardResponse;
import org.oransc.ric.portal.dashboard.model.Stats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Provides simple user-management services.
 * 
 * This first implementation serializes user details to a file.
 * 
 * Migrate to a database someday?
 */

public class StatsManager {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	// This default value is only useful for development and testing.
	public static final String STATS_FILE_PATH = "app-stats.json";

	private final File statsFile;
	private final List<Stats> stats;

	/**
	 * Development/test-only constructor that uses default file path.
	 * 
	 * @param clear If true, start empty and remove any existing file.
	 * 
	 * @throws IOException On file error
	 */
	public StatsManager(boolean clear) throws IOException {
		this(STATS_FILE_PATH);
		if (clear) {
			logger.debug("ctor: removing file {}", statsFile.getAbsolutePath());
			File f = new File(StatsManager.STATS_FILE_PATH);
			if (f.exists())
				Files.delete(f.toPath());
			stats.clear();
		}
	}

	/**
	 * Constructur that accepts a file path
	 * 
	 * @param statsFilePath File path
	 * @throws IOException If file cannot be read
	 */
	public StatsManager(final String statsFilePath) throws IOException {
		logger.debug("ctor: statsfile {}", statsFilePath);
		if (statsFilePath == null)
			throw new IllegalArgumentException("Missing or empty stats file property");
		statsFile = new File(statsFilePath);
		logger.debug("ctor: managing stats in file {}", statsFile.getAbsolutePath());
		if (statsFile.exists()) {
			final ObjectMapper mapper = new ObjectMapper();
			stats = mapper.readValue(statsFile, new TypeReference<List<Stats>>() {
			});
		} else {
			stats = new ArrayList<>();
		}
	}

	/**
	 * Gets the current app metric stats.
	 * 
	 * @return List of App stat objects, possibly empty
	 */
	public List<Stats> getStats() {
		return this.stats;
	}

	/**
	 * Gets the stats with the specified app Id and instance key
	 * 
	 * @param appId Desired app Id
	 * @param instanceKey Desired instance key
	 * @return Stats object; null if Id is not known
	 */
	public Stats getStatsById(String instanceKey, int appId) {
	
		for (Stats st : this.stats) {
			if (st.getInstanceKey().equals(instanceKey) && st.getStatsDetails().getAppId() == appId) {
				logger.debug("getStat: match on app id {} with instance key {}", appId, instanceKey);
				return st;
			}
		}
		logger.debug("getStat: no match on app id with instance key {}{}", appId, instanceKey);
		return null;
	
	}

	private void saveStats() throws IOException {
		final ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(statsFile, stats);
	}

	/*
	 * Allow at most one thread to create a stats at one time.
	 */
	public synchronized IDashboardResponse createStats(String instanceKey, StatsDetailsTransport statsSetupRequest, HttpServletResponse response) {
		logger.debug("createStats: appId {}, instanceKey {}", statsSetupRequest.getAppId(), instanceKey);
		/*if (users.contains(user))
			throw new PortalAPIException("User exists: " + user.getLoginId());
		users.add(user);
		try {
			saveUsers();
		} catch (Exception ex) {
			throw new PortalAPIException("Save failed", ex);
		}*/
		if (statsSetupRequest.getAppId() > 0) {
			for (Stats st : stats) {
				if (st.getInstanceKey().equals(instanceKey) && st.getStatsDetails().getAppId() == statsSetupRequest.getAppId()) {
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST,
							"App id exists with " + statsSetupRequest.getAppId()+"on instance key "+instanceKey);
				}
			}
		}
		Stats newAppStat = null;
		try {
			int lastCreatedStatIndex = 1;
			for (int i = stats.size() - 1; i >= 0; i--) {
				if (stats.get(i).getInstanceKey().equals(instanceKey)) {
					lastCreatedStatIndex = stats.get(i).getStatsDetails().getAppId() + 1;
					break;
				}
			}
			newAppStat = new Stats(instanceKey, new StatsDetailsTransport(lastCreatedStatIndex,
					statsSetupRequest.getAppName(), statsSetupRequest.getMetricUrl()));
			stats.add(newAppStat);

			saveStats();
			response.setStatus(HttpServletResponse.SC_CREATED);
			return newAppStat;
		} catch (Exception ex) {
			logger.debug("Save stats failed ", ex);
			if (stats.indexOf(newAppStat) != -1) {
				stats.remove(newAppStat);
			}
			return new ErrorTransport(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Save stats failed");
		}
		
	}

	/*
	 * Allow at most one thread to modify a stats at one time. We still have
	 * last-edit-wins of course.
	 */
	public synchronized void updateStats(String instanceKey, StatsDetailsTransport statsSetupRequest, HttpServletResponse response) {
		logger.debug("updateStats: appId {}, instanceKey {}", statsSetupRequest.getAppId(), instanceKey);
		
		boolean editStatsObjectFound = false;
		try {
		for (Stats st : stats) {
			if (st.getInstanceKey().equals(instanceKey) && st.getStatsDetails().getAppId() == statsSetupRequest.getAppId()) {
				st.getStatsDetails().setAppName(statsSetupRequest.getAppName());
				st.getStatsDetails().setMetricUrl(statsSetupRequest.getMetricUrl());
				response.setStatus(HttpServletResponse.SC_OK);
				editStatsObjectFound = true;
				saveStats();
				return;
			}
		}
		} catch (Exception e) {
			if (editStatsObjectFound) 
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			else 
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		
	
	}

}
