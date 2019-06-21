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
import { MatSort } from '@angular/material';
import { merge } from 'rxjs';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { Observable } from 'rxjs/Observable';
import { of } from 'rxjs/observable/of';
import { catchError, finalize, map } from 'rxjs/operators';
import { AppMgrService } from '../services/app-mgr/app-mgr.service';
import { XMDeployableApp } from '../interfaces/app-mgr.types';

export class CatalogDataSource extends DataSource<XMDeployableApp> {

  private xAppsSubject = new BehaviorSubject<XMDeployableApp[]>([]);

  private loadingSubject = new BehaviorSubject<boolean>(false);

  public loading$ = this.loadingSubject.asObservable();

  constructor(private appMgrSvc: AppMgrService, private sort: MatSort) {
    super();
  }

  loadTable() {
    this.loadingSubject.next(true);
    this.appMgrSvc.getDeployable()
      .pipe(
        catchError(() => of([])),
        finalize(() => this.loadingSubject.next(false))
      )
      .subscribe(xApps => this.xAppsSubject.next(xApps));
  }

  connect(collectionViewer: CollectionViewer): Observable<XMDeployableApp[]> {
    const dataMutations = [
      this.xAppsSubject.asObservable(),
      this.sort.sortChange
    ];
    return merge(...dataMutations).pipe(map(() => {
      return this.getSortedData([...this.xAppsSubject.getValue()]);
    }));
  }

  disconnect(collectionViewer: CollectionViewer): void {
    this.xAppsSubject.complete();
    this.loadingSubject.complete();
  }

  private getSortedData(data: XMDeployableApp[]) {
    if (!this.sort.active || this.sort.direction === '') {
      return data;
    }
    return data.sort((a: XMDeployableApp, b: XMDeployableApp) => {
      const isAsc = this.sort.direction === 'asc';
      switch (this.sort.active) {
        case 'name': return this.compare(a.name, b.name, isAsc);
        case 'version': return this.compare(a.version, b.version, isAsc);
        default: return 0;
      }
    });
  }

  private compare(a: string, b: string, isAsc: boolean) {
    return (a < b ? -1 : 1) * (isAsc ? 1 : -1);
  }

}

