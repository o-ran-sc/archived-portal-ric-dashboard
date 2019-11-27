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
import { shareReplay, map, tap } from 'rxjs/operators';
import { ErrorDialogService } from '../../services/ui/error-dialog.service';

@Injectable({
  providedIn: 'root'
})
export class InstanceSelectorService {
  private selectedInstanceKeyObservable: Observable<BehaviorSubject<string>>
  private selectedInstanceKey: BehaviorSubject<string>;
  private instanceArray: Observable<RicInstance[]>;
  private instanceBasePath = 'api/admin/instance';

  constructor(
    private httpClient: HttpClient,
    private errorDiaglogService: ErrorDialogService,) {
  }

  getInstanceArray(): Observable<RicInstance[]> {
    if (this.instanceArray) {
      return this.instanceArray;
    }
    return this.instanceArray = this.httpClient.get<RicInstance[]>(this.instanceBasePath)
      .pipe(
        tap(ricInstanceArray => {
          try {
            this.initSelectedInstanceKey(ricInstanceArray[0].key);
          }
          catch (err) {
            this.errorDiaglogService.displayError(err)
          }
        }),
        shareReplay(1)
      );
  }

  private initSelectedInstanceKey(instanceKey:string) {
    if (!this.selectedInstanceKey) {
      this.selectedInstanceKey = new BehaviorSubject<string>(instanceKey)
    }
  }

  getSelectedInstanceKeyObservable(): Observable<BehaviorSubject<string>> {
    if (this.selectedInstanceKeyObservable) {
      return this.selectedInstanceKeyObservable
    }
    else {
      return this.selectedInstanceKeyObservable=this.getInstanceArray().pipe(
        map(ricInstanceArray => {
          try {
            return this.selectedInstanceKey = new BehaviorSubject<string>(ricInstanceArray[0].key)
          }
          catch (err) {
            this.errorDiaglogService.displayError(err)
          }
        })
      )
    }
  }

  getSelectedInstancekey(): string {
    return this.selectedInstanceKey.value;
  }


  getApiBasePath(): string {
    return ('api/ric/' + this.selectedInstanceKey.value)
  }

  updateSelectedInstance(instanceKey: string) {
    this.selectedInstanceKey.next(instanceKey)
  }

}
