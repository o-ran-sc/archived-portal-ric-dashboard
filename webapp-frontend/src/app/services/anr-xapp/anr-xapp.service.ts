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
import { map } from 'rxjs/operators';
import { DashboardSuccessTransport } from '../../interfaces/dashboard.types';
import { ANRNeighborCellRelationTable, ANRNeighborCellRelation, ANRNeighborCellRelationMod } from '../../interfaces/anr-xapp.types';

@Injectable({
  providedIn: 'root'
})
export class ANRXappService {

  // Trailing slashes are important
  private basePath = 'api/xapp/anr/';
  private cellPath = 'cell/cellIdentifier/';

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
   * @param ggnbId Optional parameter for the gNB ID
   * @param startIndex Optional parameter for the start index
   * @param limit Optional parameter for the limit (page size), defaults to 100.
   * @returns Observable of ANRNeighborCellRelationTable
   */
  getNcrtInfo(ggnbId: string = '', startIndex: string = '', limit: number = 100): Observable<ANRNeighborCellRelation[]> {
    return this.httpClient.get<ANRNeighborCellRelation[]>(this.basePath + 'cell', {
      params: new HttpParams()
        .set('ggnbId', ggnbId)
        .set('startIndex', startIndex)
        .set('limit', limit.toString())
    }).pipe(
      // Extract the array of relations here
      // TODO: what about the start index?
      map(res => res['ncrtRelations'])
    );
  }

  /**
   * Query NCRT of a single serving cell, all or one gNB(s)
   * @param cellId cell ID
   * @param ggnbid Optional parameter for the gNB ID
   * @param startIndex Optional parameter for the start index
   * @param limit Optional parameter for the limit (page size)
   * @returns Observable of ANRNeighborCellRelationTable
   */
  getCellNcrtInfo(cellId: string, ggnbId?: string, startIndex?: string, limit?: number): Observable<ANRNeighborCellRelation[]> {
    return this.httpClient.get<ANRNeighborCellRelation[]>(this.basePath + this.cellPath + cellId, {
      params: new HttpParams()
        .set('ggnbId', ggnbId)
        .set('startIndex', startIndex)
        .set('limit', limit.toString())
      }).pipe(
        // Extract the array of relations here
        // TODO: what about the start index?
        map(res => res['ncrtRelations'])
      );
    }

  /**
   * Modify neighbor cell relations based on Source Cell NR CGI and Target Cell NR PCI / NR CGI
   * @param cellId cell ID
   * @param table Array of ANRNeighborCellRelationMod
   * @returns Observable that should yield a response code (no data)
   */
  modifyNcrt(cellId: string, table: ANRNeighborCellRelationMod []): Observable<any> {
    return this.httpClient.put(this.basePath + this.cellPath + cellId, table);
  }

  /** TODO: deleteNcrt */

}
