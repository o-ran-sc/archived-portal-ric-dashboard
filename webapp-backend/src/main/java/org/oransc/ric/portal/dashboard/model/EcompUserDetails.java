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
package org.oransc.ric.portal.dashboard.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.onap.portalsdk.core.restful.domain.EcompRole;
import org.onap.portalsdk.core.restful.domain.EcompUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class EcompUserDetails implements UserDetails {

	private static final long serialVersionUID = 1L;
	private final EcompUser ecompUser;

	public EcompUserDetails(EcompUser ecompUser) {
		this.ecompUser = ecompUser;
	}

	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> roleList = new ArrayList<>();
		Iterator<EcompRole> roleIter = ecompUser.getRoles().iterator();
		while (roleIter.hasNext()) {
			EcompRole role = roleIter.next();
			roleList.add(new SimpleGrantedAuthority(role.getName()));
		}
		return roleList;
	}

	public String getPassword() {
		return null;
	}

	public String getUsername() {
		return ecompUser.getLoginId();
	}

	public boolean isAccountNonExpired() {
		return true;
	}

	public boolean isAccountNonLocked() {
		return true;
	}

	public boolean isCredentialsNonExpired() {
		return true;
	}

	public boolean isEnabled() {
		return ecompUser.isActive();
	}

}
