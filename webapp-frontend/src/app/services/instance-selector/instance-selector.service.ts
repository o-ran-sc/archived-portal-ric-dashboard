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

import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { shareReplay, tap } from 'rxjs/operators';
import { RicInstance } from '../../interfaces/dashboard.types';
import { ErrorDialogService } from '../../services/ui/error-dialog.service';

@Injectable({
  providedIn: 'root'
})
export class InstanceSelectorService {
  private selectedInstanceKey: BehaviorSubject<string> = new BehaviorSubject<string>('');
  private instanceArray: Observable<RicInstance[]>;
  private basePath = 'api/admin/instance';

  constructor(
    private httpClient: HttpClient,
    private errorDiaglogService: ErrorDialogService,) {
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

  private initselectedInstanceKey(instanceKey: string) {
    if (!this.selectedInstanceKey.value) {
      this.selectedInstanceKey.next(instanceKey)
    }
  }

  // This function may return the BehaviorSubject with empty string
  // Afther subscribe that BehaviorSubject, please make sure this BehaviorSubject has non empty value, before load data from ric instance.
  getSelectedInstancekey(): BehaviorSubject<string> {
    return this.selectedInstanceKey;
  }

  getApiBasePath(component: string): string {
    if (this.selectedInstanceKey.value) {
      return ('api/' + component + '/ric/' + this.selectedInstanceKey.value)
    }
    else {
      throw ("Instance not selected")
    }
  }

  updateSelectedInstance(instanceKey: string) {
    this.selectedInstanceKey.next(instanceKey)
  }

}
