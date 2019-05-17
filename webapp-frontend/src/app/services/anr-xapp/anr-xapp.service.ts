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
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DashboardSuccessTransport } from '../../interfaces/dashboard.types';
import { ANRNeighborCellRelation } from '../../interfaces/anr-xapp.types';

@Injectable({
  providedIn: 'root'
})
export class AnrXappService {

  private basePath = 'api/xapp/anr/';

  constructor(private httpClient: HttpClient) {
    // injects to variable httpClient
  }

  /**
   * Gets ANR xApp client version details
   * @returns Observable that should yield a DashboardSuccessTransport
   */
  getVersion(): Observable<DashboardSuccessTransport> {
    return this.httpClient.get<DashboardSuccessTransport>(this.basePath + 'version');
  }

  /**
   * Performs a liveness probe
   * @returns Observable that should yield a response code (no data)
   */
  getHealthAlive(): Observable<any> {
    return this.httpClient.get(this.basePath + 'health/alive');
  }

  /**
   * Performs a readiness probe
   * @returns Observable that should yield a response code (no data)
   */
  getHealthReady(): Observable<any> {
    return this.httpClient.get(this.basePath + 'health/ready');
  }

  /**
   * Query NCRT of all cells, all or one gNB(s)
   * @param ggnbid Optional parameter for the gNB ID
   * @param startIndex Optional parameter for the start index
   * @param limit Optional parameter for the limit (page size)
   * @returns Observable
   */
  getNcrtInfo(ggnbid: string, startIndex: string, limit: number): Observable<ANRNeighborCellRelation> {
    const queryParams = new HttpParams();
    if (ggnbid) {
      queryParams.set('ggnbid', ggnbid);
    }
    if (startIndex) {
      queryParams.set('startIndex', startIndex);
    }
    if (limit) {
      queryParams.set('limit', limit.toString());
    }
    return this.httpClient.get<ANRNeighborCellRelation>(this.basePath + 'cell', { params: queryParams } );
  }

}
