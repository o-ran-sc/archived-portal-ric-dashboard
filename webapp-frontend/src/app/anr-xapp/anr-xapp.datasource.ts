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
import { ANRNeighborCellRelation } from '../interfaces/anr-xapp.types';
import { ANRXappService } from '../services/anr-xapp/anr-xapp.service';

// https://blog.angular-university.io/angular-material-data-table/
export class ANRXappDataSource extends DataSource<ANRNeighborCellRelation> {

    private relationsSubject = new BehaviorSubject<ANRNeighborCellRelation[]>([]);

    private loadingSubject = new BehaviorSubject<boolean>(false);

    public loading$ = this.loadingSubject.asObservable();

  constructor(private anrXappService: ANRXappService, private sort: MatSort) {
        super();
    }

    loadTable(ggnodeb = '', servingCellNrcgi = '', neighborCellNrpci = '') {
        this.loadingSubject.next(true);
        this.anrXappService.getNcrtInfo(ggnodeb, servingCellNrcgi, neighborCellNrpci)
            .pipe(
                catchError(() => of([])),
                finalize(() => this.loadingSubject.next(false))
            )
            .subscribe(ncrt => this.relationsSubject.next(ncrt));
    }

    connect(collectionViewer: CollectionViewer): Observable<ANRNeighborCellRelation[]> {
      const dataMutations = [
        this.relationsSubject.asObservable(),
        this.sort.sortChange
      ];
      return merge(...dataMutations).pipe(map(() => {
        return this.getSortedData([...this.relationsSubject.getValue()]);
      }));
    }

    disconnect(collectionViewer: CollectionViewer): void {
        this.relationsSubject.complete();
        this.loadingSubject.complete();
    }

  private getSortedData(data: ANRNeighborCellRelation[]) {
    if (!this.sort.active || this.sort.direction === '') {
      return data;
    }

    return data.sort((a, b) => {
      const isAsc = this.sort.direction === 'asc';
      switch (this.sort.active) {
        case 'cellIdentifierNrcgi': return compare(a.servingCellNrcgi, b.servingCellNrcgi, isAsc);
        case 'neighborCellNrpci': return compare(a.neighborCellNrpci, b.neighborCellNrpci, isAsc);
        case 'neighborCellNrcgi': return compare(a.neighborCellNrcgi, b.neighborCellNrcgi, isAsc);
        case 'flagNoHo': return compare(a.flagNoHo, b.flagNoHo, isAsc);
        case 'flagNoXn': return compare(a.flagNoXn, b.flagNoXn, isAsc);
        case 'flagNoRemove': return compare(a.flagNoRemove, b.flagNoRemove, isAsc);
        default: return 0;
      }
    });
  }

}

function compare(a, b, isAsc) {
  return (a < b ? -1 : 1) * (isAsc ? 1 : -1);
}
