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
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { XMDeployableApp, XMDeployedApp, XMXappInfo } from '../../interfaces/app-mgr.types';
import { DashboardService } from '../dashboard/dashboard.service';

@Injectable()
export class AppMgrService {

  private component = 'appmgr';
  private xappsPath = 'xapps';

  constructor(
    private dashboardSvc: DashboardService,
    private httpClient: HttpClient) {
  }

  getDeployable(instanceKey: string): Observable<XMDeployableApp[]> {
    const path = this.dashboardSvc.buildPath(this.component, instanceKey, this.xappsPath, 'list');
    return this.httpClient.get<XMDeployableApp[]>(path);
  }

  getDeployed(instanceKey: string): Observable<XMDeployedApp[]> {
    const path = this.dashboardSvc.buildPath(this.component, instanceKey, this.xappsPath);
    return this.httpClient.get<XMDeployedApp[]>(path);
  }

  deployXapp(instanceKey: string, name: string): Observable<HttpResponse<Object>> {
    const xappInfo: XMXappInfo = { name: name };
    const path = this.dashboardSvc.buildPath(this.component, instanceKey, this.xappsPath);
    return this.httpClient.post(path, xappInfo, { observe: 'response' });
  }

  undeployXapp(instanceKey: string, name: string): Observable<HttpResponse<Object>> {
    const path = this.dashboardSvc.buildPath(this.component, instanceKey, this.xappsPath, name);
    return this.httpClient.delete(path, { observe: 'response' });
  }

  getConfig(instanceKey: string): Observable<any[]> {
    // For demo purpose, pull example config from local
    return this.httpClient.get<any[]>("/assets/mockdata/config.json");
    // Once Xapp manager contains layout, should call backend to get xapp config 
    //const path = this.dashboardSvc.buildPath(this.component, instanceKey, 'config');
    //return this.httpClient.get<any[]>(path);
  }

  putConfig(instanceKey: string, config: any): Observable<HttpResponse<Object>> {
    const path = this.dashboardSvc.buildPath(this.component, instanceKey, 'config');
    return this.httpClient.put(path, config, { observe: 'response' });
  }

}
