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

import { CollectionViewer, DataSource} from '@angular/cdk/collections';
import { Observable } from 'rxjs/Observable';
import { catchError, finalize } from 'rxjs/operators';
import { of } from 'rxjs/observable/of';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { ANRNeighborCellRelation } from '../interfaces/anr-xapp.types';
import { ANRXappService } from '../services/anr-xapp/anr-xapp.service';

// https://blog.angular-university.io/angular-material-data-table/
// Comments there suggest this should EXTEND not IMPLMENT argh
export class ANRXappDataSource extends DataSource<ANRNeighborCellRelation> {

    private relationsSubject = new BehaviorSubject<ANRNeighborCellRelation[]>([]);

    private loadingSubject = new BehaviorSubject<boolean>(false);

    public loading$ = this.loadingSubject.asObservable();

    constructor(private anrXappService: ANRXappService) {
        super();
    }

    loadTable(ggnbId = '',
                startIndex = '',
                limit = 20) {
        this.loadingSubject.next(true);
        this.anrXappService.getNcrtInfo(ggnbId, startIndex, limit)
            .pipe(
                catchError(() => of([])),
                finalize(() => this.loadingSubject.next(false))
            )
            .subscribe(ncrt => this.relationsSubject.next(ncrt));
    }

    connect(collectionViewer: CollectionViewer): Observable<ANRNeighborCellRelation[]> {
        return this.relationsSubject.asObservable();
    }

    disconnect(collectionViewer: CollectionViewer): void {
        this.relationsSubject.complete();
        this.loadingSubject.complete();
    }

}
