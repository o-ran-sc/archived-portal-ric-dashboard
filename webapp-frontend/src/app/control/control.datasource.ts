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
import { catchError, finalize, map } from 'rxjs/operators';
import { of } from 'rxjs/observable/of';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { XappMgrService } from '../services/xapp-mgr/xapp-mgr.service';
import { XMXapp, XappControlRow } from '../interfaces/xapp-mgr.types';
import { MatSort } from '@angular/material';
import { merge } from 'rxjs';

export class ControlDataSource extends DataSource<XappControlRow> {

  private xAppInstancesSubject = new BehaviorSubject<XappControlRow[]>([]);

  private loadingSubject = new BehaviorSubject<boolean>(false);

  public loading$ = this.loadingSubject.asObservable();

  constructor(private xappMgrSvc: XappMgrService, private sort: MatSort) {
    super();
  };

  loadTable() {
    this.loadingSubject.next(true);
    this.xappMgrSvc.getAll()
      .pipe(
        catchError(() => of([])),
        finalize(() => this.loadingSubject.next(false))
      )
      .subscribe(xApps => this.xAppInstancesSubject.next(this.getInstance(xApps)) )
  }

  connect(collectionViewer: CollectionViewer): Observable<XappControlRow[]> {
    const dataMutations = [
      this.xAppInstancesSubject.asObservable(),
      this.sort.sortChange
    ];
    return merge(...dataMutations).pipe(map(() => {
      return this.getSortedData([...this.xAppInstancesSubject.getValue()]);
    }));
  }

  disconnect(collectionViewer: CollectionViewer): void {
    this.xAppInstancesSubject.complete();
    this.loadingSubject.complete();
  }

  getInstance(allxappdata: XMXapp[]) {
    const xAppInstances: XappControlRow[]= [];
    for (const xappindex in allxappdata) {
      const instancelist = allxappdata[xappindex].instances;
      for (const instanceindex in instancelist) {
        var instance: XappControlRow = {
          xapp: allxappdata[xappindex].name,
          instance: instancelist[instanceindex]
        }
        xAppInstances.push(instance);
      }
    }
    return xAppInstances;
  }

  private getSortedData(data: XappControlRow[]) {
    if (!this.sort.active || this.sort.direction === '') {
      return data;
    }

    return data.sort((a, b) => {
      const isAsc = this.sort.direction === 'asc';
      switch (this.sort.active) {
        case 'xapp': return compare(a.xapp, b.xapp, isAsc);
        case 'name': return compare(+a.instance.name, +b.instance.name, isAsc);
        case 'status': return compare(+a.instance.status, +b.instance.status, isAsc);
        default: return 0;
      }
    });
  }
}

function compare(a, b, isAsc) {
  return (a < b ? -1 : 1) * (isAsc ? 1 : -1);
}