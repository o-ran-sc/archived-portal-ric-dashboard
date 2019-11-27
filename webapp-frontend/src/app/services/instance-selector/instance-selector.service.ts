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

import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { RicInstance } from '../../interfaces/dashboard.types';
import { shareReplay, tap} from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class InstanceSelectorService {
  private selectedInstanceKey: BehaviorSubject<string>;
  private instanceArray: Observable<RicInstance[]>;
  private basePath = 'api/admin/instance';

  constructor(private httpClient: HttpClient) {
  }

  getInstanceArray(): Observable<RicInstance[]> {
    if (this.instanceArray) {
      return this.instanceArray;
    }
    return this.instanceArray = this.httpClient.get<RicInstance[]>(this.basePath)
      .pipe(
        tap(ricInstanceArray => {
          this.initselectedInstanceKey(ricInstanceArray[0].key);
        }),
        shareReplay(1)
      );
  }

  private initselectedInstanceKey(instanceKey:string) {
    if (!this.selectedInstanceKey) {
      this.selectedInstanceKey = new BehaviorSubject<string>(instanceKey)
    }
  }

  getSelectedInstancekey(): BehaviorSubject<string> {
    return this.selectedInstanceKey;
  }

  getApiBasePath(): string {
    return ('api/ric/' + this.selectedInstanceKey.value)
  }

  updateSelectedInstance(instanceKey: string) {
    this.selectedInstanceKey.next(instanceKey)
  }

}
