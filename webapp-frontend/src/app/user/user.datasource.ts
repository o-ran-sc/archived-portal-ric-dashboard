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
import { HttpErrorResponse } from '@angular/common/http';
import { MatSort } from '@angular/material';
import { Observable } from 'rxjs/Observable';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { merge } from 'rxjs';
import { of } from 'rxjs/observable/of';
import { catchError, finalize, map } from 'rxjs/operators';
import { DashboardUser } from '../interfaces/dashboard.types';
import { DashboardService } from '../services/dashboard/dashboard.service';
import { NotificationService } from '../services/ui/notification.service';

export class UserDataSource extends DataSource<DashboardUser> {

  private userSubject = new BehaviorSubject<DashboardUser[]>([]);

  private loadingSubject = new BehaviorSubject<boolean>(false);

  public loading$ = this.loadingSubject.asObservable();

  public rowCount = 1; // hide footer during intial load

  constructor(private dashboardSvc: DashboardService,
    private sort: MatSort,
    private notificationService: NotificationService) {
    super();
  }

  loadTable() {
    this.loadingSubject.next(true);
    this.dashboardSvc.getUsers()
      .pipe(
        catchError( (her: HttpErrorResponse) => {
          console.log('UserDataSource failed: ' + her.message);
          this.notificationService.error('Failed to get users: ' + her.message);
          return of([]);
        }),
        finalize(() => this.loadingSubject.next(false))
      )
      .subscribe( (users: DashboardUser[]) => {
        this.rowCount = users.length;
        this.userSubject.next(users);
      });
  }

  connect(collectionViewer: CollectionViewer): Observable<DashboardUser[]> {
    const dataMutations = [
      this.userSubject.asObservable(),
      this.sort.sortChange
    ];
    return merge(...dataMutations).pipe(map(() => {
      return this.getSortedData([...this.userSubject.getValue()]);
    }));
  }

  disconnect(collectionViewer: CollectionViewer): void {
    this.userSubject.complete();
    this.loadingSubject.complete();
  }

  private getSortedData(data: DashboardUser[]) {
    if (!this.sort.active || this.sort.direction === '') {
      return data;
    }

    return data.sort((a: DashboardUser, b: DashboardUser) => {
      const isAsc = this.sort.direction === 'asc';
      switch (this.sort.active) {
        case 'id': return this.compare(a.id, b.id, isAsc);
        case 'firstName': return this.compare(a.firstName, b.firstName, isAsc);
        case 'lastName': return this.compare(a.lastName, b.lastName, isAsc);
        case 'status': return this.compare(a.status, b.status, isAsc);
        default: return 0;
      }
    });
  }

  private compare(a: any, b: any, isAsc: boolean) {
    return (a < b ? -1 : 1) * (isAsc ? 1 : -1);
  }

}
