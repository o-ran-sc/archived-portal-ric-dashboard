/*-
 * ========================LICENSE_START=================================
 * O-RAN-SC
 * %%
 * Copyright (C) 2019 Nordix Foundation
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
import { ActivatedRoute } from '@angular/router';
import { MatSort } from '@angular/material';
import { Observable } from 'rxjs/Observable';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { merge } from 'rxjs';
import { of } from 'rxjs/observable/of';
import { catchError, finalize, map } from 'rxjs/operators';
import { PolicyInstance } from '../interfaces/policy.types';
import { PolicyService } from '../services/policy/policy.service';
import { NotificationService } from '../services/ui/notification.service';

export class PolicyInstanceDataSource extends DataSource<PolicyInstance> {

  private policyInstanceSubject = new BehaviorSubject<PolicyInstance[]>([]);

  private loadingSubject = new BehaviorSubject<boolean>(false);

  public loading$ = this.loadingSubject.asObservable();

  public rowCount = 1; // hide footer during intial load

  private policyTypeId: string;

  constructor(
    private policySvc: PolicyService,
    private route: ActivatedRoute,
    private sort: MatSort,
    private notificationService: NotificationService) {
    super();
  }

  loadTable() {
    this.loadingSubject.next(true);
    this.policyTypeId = this.route.snapshot.paramMap.get('policy_type_id');
    this.policySvc.getPolicyInstances(this.policyTypeId)
      .pipe(
        catchError( (her: HttpErrorResponse) => {
          console.log('PolicyDataSource failed: ' + her.message);
          this.notificationService.error('Failed to get policy instances: ' + her.message);
          return of([]);
        }),
        finalize(() => this.loadingSubject.next(false))
      )
      .subscribe( (instances: PolicyInstance[]) => {
        this.rowCount = instances.length;
        this.policyInstanceSubject.next(instances);
      });
  }

  connect(collectionViewer: CollectionViewer): Observable<PolicyInstance[]> {
    const dataMutations = [
      this.policyInstanceSubject.asObservable(),
      this.sort.sortChange
    ];
    return merge(...dataMutations).pipe(map(() => {
      return this.getSortedData([...this.policyInstanceSubject.getValue()]);
    }));
  }

  disconnect(collectionViewer: CollectionViewer): void {
    this.policyInstanceSubject.complete();
    this.loadingSubject.complete();
  }

  private getSortedData(data: PolicyInstance[]) {
    if (!this.sort.active || this.sort.direction === '') {
      return data;
    }

    return data.sort((a, b) => {
      const isAsc = this.sort.direction === 'asc';
      switch (this.sort.active) {
        case 'name': return compare(a.instanceId, b.instanceId, isAsc);
        default: return 0;
      }
    });
  }
}

function compare(a: any, b: any, isAsc: boolean) {
  return (a < b ? -1 : 1) * (isAsc ? 1 : -1);
}
