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

import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Component, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import { PolicyService } from '../services/policy/policy.service';
import { ErrorDialogService } from '../services/ui/error-dialog.service';
import { debounceTime, distinctUntilChanged, finalize, tap } from 'rxjs/operators';
import { PolicyInstanceDataSource } from './policy-instance.datasource';
import { NotificationService } from './../services/ui/notification.service';
import { PolicyInstance } from '../interfaces/policy.types';
import { MatSort } from '@angular/material';
import { PolicyInstanceDialogComponent } from './policy-instance-dialog.component';
import { ConfirmDialogService } from './../services/ui/confirm-dialog.service';
import { LoadingDialogService } from '../services/ui/loading-dialog.service';

@Component( {
    selector: 'rd-policy-instance',
    templateUrl: './policy-instance.component.html',
    styleUrls: ['./policy-instance.component.scss']
} )
export class PolicyInstanceComponent implements OnInit {

    displayedColumns: string[] = ['instanceId', 'instance', 'action'];
    dataSource: PolicyInstanceDataSource;
    @ViewChild( MatSort, { static: true } ) sort: MatSort;

    private policyTypeId: number;
    private createSchema: string;

    // this is probably the A1 version string
    nonrrVersion: string;

    constructor(
        private policySvc: PolicyService,
        private dialog: MatDialog,
        private route: ActivatedRoute,
        private confirmDialogService: ConfirmDialogService,
        private loadingDialogService: LoadingDialogService,
        private errorDialogService: ErrorDialogService,
        private notificationService: NotificationService ) {
        this.policyTypeId = parseInt(this.route.snapshot.paramMap.get( 'policyTypeId' ), 10);
        this.createSchema = this.route.snapshot.paramMap.get( 'createSchema' );
    }

    ngOnInit() {
        this.dataSource = new PolicyInstanceDataSource( this.policySvc, this.route, this.sort, this.notificationService );
        this.dataSource.loadTable();
    }

    modifyInstance( instance: PolicyInstance ): void {
        const policyTypeId = this.policyTypeId;
        const createSchema = this.createSchema;
        const instanceId = instance.instanceId;
        const instanceJson = instance.instance;
        const name = this.route.snapshot.paramMap.get( 'name' ); // policyTypeName

        const dialogRef = this.dialog.open(PolicyInstanceDialogComponent, {
                maxWidth: '1200px',
                height: '1200px',
                width: '1200px',
                role: 'dialog',
                data: {
                    policyTypeId,
                    createSchema,
                    instanceId,
                    instanceJson,
                    name
                }
            });
        dialogRef.afterClosed().subscribe(
                (result: any) => {
                  this.loadInstancePage();
                }
              );
    }

    loadInstancePage() {
        this.dataSource.loadTable();
    }

    deleteInstance( instance: PolicyInstance ): void {
        this.confirmDialogService
        .openConfirmDialog('Are you sure you want to delete this policy instance?')
        .afterClosed().subscribe(
          (res: any) => {
            if (res) {
              this.loadingDialogService.startLoading("Deleting");
              this.policySvc.deletePolicy(this.policyTypeId, instance.instanceId)
                .pipe(
                  finalize(() => this.loadingDialogService.stopLoading())
                )
                .subscribe(
                  (response: HttpResponse<Object>) => {
                    switch (response.status) {
                      case 200:
                        this.notificationService.success('Delete succeeded!');
                        this.loadInstancePage();
                        break;
                      default:
                        this.notificationService.warn('Delete failed.');
                    }
                  },
                  (error: HttpErrorResponse) => {
                    this.errorDialogService.displayError(error.message);
                  });
            }
          });
    }
}
