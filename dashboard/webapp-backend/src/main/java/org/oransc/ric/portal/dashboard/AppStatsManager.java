/*-
 * ========================LICENSE_START=================================
 * O-RAN-SC
 * %%
 * Copyright (C) 2020 AT&T Intellectual Property
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
import java.util.ListIterator;

import javax.servlet.http.HttpServletResponse;

import org.onap.portalsdk.core.onboarding.exception.PortalAPIException;
import org.onap.portalsdk.core.restful.domain.EcompUser;
import org.oransc.ric.portal.dashboard.model.StatsDetailsTransport;
import org.oransc.ric.portal.dashboard.exception.StatsManagerException;
import org.oransc.ric.portal.dashboard.model.IDashboardResponse;
import org.oransc.ric.portal.dashboard.model.AppStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Provides simple xApp stats-management services.
 * 
 * This first implementation serializes xApp stat details to a file.
 * 
 * Migrate to a database someday?
 */

public class AppStatsManager {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	// This default value is only useful for development and testing.
	public static final String STATS_FILE_PATH = "app-stats.json";

	private final File statsFile;
	private final List<AppStats> stats;
	private int appMaxId;

	/**
	 * Development/test-only constructor that uses default file path.
	 * 
	 * @param clear If true, start empty and remove any existing file.
	 * 
	 * @throws IOException On file error
	 */
	public AppStatsManager(boolean clear) throws IOException {
		this(STATS_FILE_PATH);
		if (clear) {
			logger.debug("ctor: removing file {}", statsFile.getAbsolutePath());
			File f = new File(AppStatsManager.STATS_FILE_PATH);
			if (f.exists())
				Files.delete(f.toPath());
			stats.clear();
		}
	}

	/**
	 * Constructor that accepts a file path
	 * 
	 * @param statsFilePath File path
	 * @throws IOException If file cannot be read
	 */
	public AppStatsManager(final String statsFilePath) throws IOException {
		appMaxId = 0;
		logger.debug("ctor: statsfile {}", statsFilePath);
		if (statsFilePath == null)
			throw new IllegalArgumentException("Missing or empty stats file property");
		statsFile = new File(statsFilePath);
		logger.debug("ctor: managing stats in file {}", statsFile.getAbsolutePath());
		if (statsFile.exists()) {
			final ObjectMapper mapper = new ObjectMapper();
			stats = mapper.readValue(statsFile, new TypeReference<List<AppStats>>() {
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
	public List<AppStats> getStats() {
		return this.stats;
	}

	/**
	 * Gets the current app metric stats by instance key.
	 * 
	 * @param instanceKey Desired instance key
	 * @return List of App stat objects by instance key, possibly empty
	 */
	public List<AppStats> getStatsByInstance(String instanceKey) {
		List<AppStats> statsByInstance = new ArrayList<AppStats>();
		for (AppStats st : this.stats) {
			if (st.getInstanceKey().equals(instanceKey)) {
				logger.debug("getStatsByInstance: match on instance key {}", instanceKey);
				statsByInstance.add(st);
			}
		}
		return statsByInstance;
	}

	/**
	 * Gets the stats with the specified app Id and instance key
	 * 
	 * @param appId       Desired app Id
	 * @param instanceKey Desired instance key
	 * @return Stats object; null if Id is not known
	 */
	public AppStats getStatsById(String instanceKey, int appId) {

		for (AppStats st : this.stats) {
			if (st.getInstanceKey().equals(instanceKey) && st.getStatsDetails().getAppId() == appId) {
				logger.debug("getStatsById: match on app id {} with instance key {}", appId, instanceKey);
				return st;
			}
		}
		logger.debug("getStatsById: no match on app id with instance key {}{}", appId, instanceKey);
		return null;

	}

	private void saveStats() throws IOException {
		final ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(statsFile, stats);
	}

	/*
	 * Allow at most one thread to create a stats at one time.
	 * Before creating new stat, checks for composite key (appname,url) uniqueness for an instance key
	 */
	public synchronized AppStats createStats(String instanceKey, StatsDetailsTransport statsSetupRequest)
			throws StatsManagerException, IOException {
		logger.debug("createStats: appId {}, instanceKey {}", statsSetupRequest.getAppId(), instanceKey);

		for (AppStats st : stats) {
			if (st.getInstanceKey().equals(instanceKey)
					&& st.getStatsDetails().getAppName().equals(statsSetupRequest.getAppName())
					&& st.getStatsDetails().getMetricUrl().equals(statsSetupRequest.getMetricUrl())) {
				String msg = "App exists with name " + statsSetupRequest.getAppName() + " and url "+statsSetupRequest.getMetricUrl()+ " on instance key " + instanceKey;
				logger.warn(msg);
				throw new StatsManagerException(msg);
			}
		}

		AppStats newAppStat = null;
		//Assigns appId to be 1 more than the largest value stored in memory
		appMaxId = appMaxId+1;
		newAppStat = new AppStats(instanceKey,
				new StatsDetailsTransport(appMaxId, statsSetupRequest.getAppName(), statsSetupRequest.getMetricUrl()));
		stats.add(newAppStat);
		saveStats();
		return newAppStat;
	}

	/*
	 * Allow at most one thread to modify a stats at one time. We still have
	 * last-edit-wins of course.
	 */
	public synchronized void updateStats(String instanceKey, StatsDetailsTransport statsSetupRequest)
			throws StatsManagerException, IOException {
		logger.debug("updateStats: appId {}, instanceKey {}", statsSetupRequest.getAppId(), instanceKey);
		boolean editStatsObjectFound = false;

		for (AppStats st : stats) {
			if (st.getInstanceKey().equals(instanceKey)
					&& st.getStatsDetails().getAppId() == statsSetupRequest.getAppId()) {
				AppStats newAppStat = new AppStats(instanceKey, statsSetupRequest);
				stats.remove(st);
				stats.add(newAppStat);
				editStatsObjectFound = true;
				saveStats();
				break;
			}
		}
		if (!editStatsObjectFound) {
			String msg = "Stats to be updated does not exist ";
			logger.warn(msg);
			throw new StatsManagerException(msg);
		}
	}

	public synchronized AppStats deleteStats(String instanceKey, int appId) throws StatsManagerException, IOException {
		logger.debug("deleteStats: appId {}, instanceKey {}", appId, instanceKey);
		boolean deleteStatsObjectFound = false;
		AppStats stat = null;
		for (AppStats st : stats) {
			if (st.getInstanceKey().equals(instanceKey) && st.getStatsDetails().getAppId() == appId) {
				stat = st;
				deleteStatsObjectFound = stats.remove(stat);
				try {
					saveStats();
					break;
				} catch (Exception e) {
					throw new StatsManagerException(e.toString());
				}

			}
		}
		if (!deleteStatsObjectFound) {
			String msg = "deleteUser: no match on app id {} of instance key {}";
			logger.debug(msg, appId, instanceKey);
			throw new StatsManagerException(msg);
		}
		return stat;
	}
}
