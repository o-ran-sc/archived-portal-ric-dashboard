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
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DashboardSuccessTransport, DashboardUser } from '../../interfaces/dashboard.types';

@Injectable({
  providedIn: 'root'
})

/**
 * Services to query the dashboard's admin endpoints.
 */
export class DashboardService {

  private basePath = 'api/admin/';
  private tokenName = 'usertoken';

  constructor(private httpClient: HttpClient) {
    // injects to variable httpClient
  }

 /**
   * Checks app health
   * @returns Observable that should yield a DashboardSuccessTransport
   */
  getHealth(): Observable<DashboardSuccessTransport> {
    return this.httpClient.get<DashboardSuccessTransport>(this.basePath + 'health');
  }

  /**
   * Gets Dashboard version details
   * @returns Observable that should yield a DashboardSuccessTransport object
   */
  getVersion(): Observable<DashboardSuccessTransport> {
    return this.httpClient.get<DashboardSuccessTransport>(this.basePath + 'version');
  }

  /**
   * Submits login request
   * @param username user name
   * @param password password
   */
  login(username: string, password: string): Observable<DashboardSuccessTransport> {
    const body = {
      username: username,
      password: password
    };
    return this.httpClient.post<DashboardSuccessTransport>(this.basePath + 'login', body);
  }

  setUserToken(username: string) {
    sessionStorage.setItem(this.tokenName, username);
  }

  clearUserToken() {
    sessionStorage.setItem(this.tokenName, '');
  }

  /**
   * Gets Dashboard users
   * @returns Observable that should yield a DashboardUser array
   */
  getUsers(): Observable<DashboardUser[]> {
    return this.httpClient.get<DashboardUser[]>(this.basePath + 'user');
  }

}
