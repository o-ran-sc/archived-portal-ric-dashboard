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
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { XMXappInfo, XMDeployableApp, XMDeployedApp } from '../../interfaces/app-mgr.types';

@Injectable()
export class AppMgrService {

  constructor(private httpClient: HttpClient) {
    // injects to variable httpClient
  }

  private basePath = 'api/appmgr/xapps';

  getDeployable(): Observable<XMDeployableApp[]> {
    return this.httpClient.get<XMDeployableApp[]>(this.basePath + '/list');
  }

  getDeployed(): Observable<XMDeployedApp[]> {
    return this.httpClient.get<XMDeployedApp[]>(this.basePath);
  }

  deployXapp(name: string): Observable<HttpResponse<Object>> {
    const xappInfo: XMXappInfo = { name: name };
    return this.httpClient.post(this.basePath, xappInfo, { observe: 'response' });
  }

  undeployXapp(name: string): Observable<HttpResponse<Object>> {
    return this.httpClient.delete((this.basePath + '/' + name), { observe: 'response' });
  }

  getConfig(name: string) {
    //fetch json from local
    return this.httpClient.get("/assets/mockdata/config.json");
  }

  putConfig(config: any): Observable<HttpResponse<Object>> {
    return this.httpClient.post((this.basePath + '/config' ), config, { observe: 'response' });
  }


}
