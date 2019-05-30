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
 import { map } from 'rxjs/operators';
import { ACAdmissionIntervalControl, ACAdmissionIntervalControlAck } from '../../interfaces/ac-xapp.types';
import { DashboardSuccessTransport } from '../../interfaces/dashboard.types';

/**
 * Services for calling the Dashboard's AC endpoints.
 */
@Injectable({
  providedIn: 'root'
})
export class AcXappService {

  private basePath = 'api/xapp/ac';

  private buildPath(...args: any[]) {
    let result = this.basePath;
    args.forEach(part => {
      result = result + '/' + part;
    });
    return result;
  }

  constructor(private httpClient: HttpClient) {
    // injects to variable httpClient
  }

  /**
   * Gets version details
   * @returns Observable that should yield a String
   */
  getVersion(): Observable<string> {
    const url = this.buildPath('version');
    return this.httpClient.get<DashboardSuccessTransport>(url).pipe(
      // Extract the string here
      map(res => res['data'])
    );
  }

  /**
   * Puts admission control parameters.
   * @param policy an instance of ACAdmissionIntervalControl
   * @returns Observable that should yield a response code, no data
   */
  putPolicy(policy: ACAdmissionIntervalControl): Observable<any> {
    const url = this.buildPath('catime');
    return this.httpClient.put<ACAdmissionIntervalControlAck>(url, policy, { observe: 'response' });
  }

}
