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

import { CollectionViewer, DataSource } from '@angular/cdk/collections';
import { Observable } from 'rxjs/Observable';
import { catchError, finalize } from 'rxjs/operators';
import { of } from 'rxjs/observable/of';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { XappMgrService } from '../services/xapp-mgr/xapp-mgr.service';
import { XMXapp } from '../interfaces/xapp-mgr.types';

export class CatalogDataSource extends DataSource<XMXapp> {

  private xAppsSubject = new BehaviorSubject<XMXapp[]>([]);

  private loadingSubject = new BehaviorSubject<boolean>(false);

  public loading$ = this.loadingSubject.asObservable();

  constructor(private xappMgrSvc: XappMgrService) {
    super();
  };

  loadTable() {
    this.loadingSubject.next(true);
    this.xappMgrSvc.getAll()
      .pipe(
        catchError(() => of([])),
        finalize(() => this.loadingSubject.next(false))
      )
      .subscribe(xApps => this.xAppsSubject.next(xApps) )
  }

  connect(collectionViewer: CollectionViewer): Observable<XMXapp[]> {
    return this.xAppsSubject.asObservable();
  }

  disconnect(collectionViewer: CollectionViewer): void {
    this.xAppsSubject.complete();
    this.loadingSubject.complete();
  }
}
