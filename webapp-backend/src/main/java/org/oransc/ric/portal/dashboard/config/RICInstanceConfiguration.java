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

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

import org.oransc.ric.portal.dashboard.model.RicRegion;
import org.oransc.ric.portal.dashboard.model.RicRegionList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Publishes a list of RIC instances from configuration, written as a YAML list
 * in application properties like this:
 * 
 * <pre>
  ricinstances:
    regions:
      -
        name: Region ABC
        instances:
            -
              key: key1
              name: Friendly Name One
              appUrlPrefix: http://foo.bar.one/
            -
              key: key2
              name: Friendly Name Two
              appUrlPrefix: http://foo.bar.two/
      -
        name: Region DEF
        instances:
            -
              key: key3
              name: Friendly Name Three
              appUrlPrefix: http://foo.bar.three/
 * </pre>
 */
@Configuration
@ConfigurationProperties(prefix = "ricinstances")
@Profile("!test")
public class RICInstanceConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private List<RicRegion> regions = new ArrayList<>();

	// Called by spring with config data
	public void setRegions(List<RicRegion> regions) {
		this.regions = regions;
	}

	@Bean
	public RicRegionList ricRegions() {
		logger.debug("Creating bean ricRegions");
		return new RicRegionList(regions);
	}

}
