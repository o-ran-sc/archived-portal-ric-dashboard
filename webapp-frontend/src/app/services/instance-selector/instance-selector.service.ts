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

@Injectable({
  providedIn: 'root'
})
export class InstanceSelectorService {

  private selectedInstance: BehaviorSubject<RicInstance> ;
  private basePath = 'api/admin/instance';
  private isSelected = new BehaviorSubject<boolean>(false);

  constructor(private httpClient: HttpClient) {
  }

  getInstanceList(): Observable<RicInstance[]> {
    return this.httpClient.get<RicInstance[]>(this.basePath);
  }

  initSelectedInstance(instance: RicInstance) {
    this.selectedInstance = new BehaviorSubject<RicInstance>(instance)
    this.isSelected.next(true)
  }


  isInstanceSelected(): BehaviorSubject<boolean> {
    return this.isSelected;
  }

  getSelectedInstance(): BehaviorSubject<RicInstance> {
    return this.selectedInstance;
  }

  getBasePath(): string{
    var basePath = 'api/ric/' + this.selectedInstance.value.key
    return basePath
  }

  updateSelectedInstance(instance: RicInstance) {
    this.selectedInstance.next(instance)
  }

}
