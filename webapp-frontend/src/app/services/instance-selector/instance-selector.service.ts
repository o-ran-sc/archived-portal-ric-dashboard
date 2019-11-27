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
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class InstanceSelectorService {

  private selectedInstanceIndex: BehaviorSubject<number> = new BehaviorSubject<number>(0);
  private instanceArray: RicInstance[] = [];
  private basePath = 'api/admin/instance';
  private isInited = new BehaviorSubject<boolean>(false);

  constructor(private httpClient: HttpClient) {
  }

  getInstanceArray(): Observable<RicInstance[]> {
    return this.httpClient.get<RicInstance[]>(this.basePath).pipe(map((instanceArray: RicInstance[]) => {
      this.instanceArray = instanceArray;
      this.isInited.next(true)
      return instanceArray;
    }));
  }

  initSelectedInstance(instanceIndex: number) {
    this.selectedInstanceIndex = new BehaviorSubject<number>(instanceIndex)
  }


  isInstanceSelectorInited(): BehaviorSubject<boolean> {
    return this.isInited;
  }

  getSelectedInstance(): BehaviorSubject<number> {
    return this.selectedInstanceIndex;
  }

  getBasePath(): string {
    return  ('api/ric/' + this.instanceArray[this.selectedInstanceIndex.value].key)
  }

  updateSelectedInstance(instanceIndex: number) {
    this.selectedInstanceIndex.next(instanceIndex)
  }

}
