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
import { Component, OnInit, ViewChild } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatSort } from '@angular/material/sort';
import { PolicyType } from '../interfaces/policy.types';
import { PolicyService } from '../services/policy/policy.service';
import { ErrorDialogService } from '../services/ui/error-dialog.service';
import { NotificationService } from '../services/ui/notification.service';
import { PolicyControlDataSource } from './policy-control.datasource';
import { PolicyInstanceDataSource } from './policy-instance.datasource';
import { PolicyInstanceDialogComponent } from './policy-instance-dialog.component';
import { animate, state, style, transition, trigger } from '@angular/animations';
import { PolicyInstance } from '../interfaces/policy.types';
import { ConfirmDialogService } from './../services/ui/confirm-dialog.service';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';

@Component({
    selector: 'rd-policy-control',
    templateUrl: './policy-control.component.html',
    styleUrls: ['./policy-control.component.scss'],
    animations: [
        trigger('detailExpand', [
            state('collapsed', style({ height: '0px', minHeight: '0', visibility: 'hidden' })),
            state('expanded', style({ height: '*', visibility: 'visible' })),
            transition('expanded <=> collapsed', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')),
        ]),
    ],
})
export class PolicyControlComponent implements OnInit {

    policyTypesDataSource: PolicyControlDataSource;
    @ViewChild(MatSort, { static: true }) sort: MatSort;
    expandedTypes = new Map<string, PolicyType>();
    instanceDataSources = new Map<string, PolicyInstanceDataSource>();

    constructor(
        private policySvc: PolicyService,
        private dialog: MatDialog,
        private errorDialogService: ErrorDialogService,
        private notificationService: NotificationService,
        private confirmDialogService: ConfirmDialogService) { }

    ngOnInit() {
        this.policyTypesDataSource = new PolicyControlDataSource(this.policySvc, this.sort, this.notificationService);
        this.policyTypesDataSource.loadTable();

    }

    hasInstances(policyType: PolicyType): boolean {
        return this.instanceDataSources.get(policyType.name).rowCount > 0;
    }

    instanceDataSource(policyType: PolicyType): PolicyInstanceDataSource {
        if (!this.instanceDataSources.has(policyType.name)) {
            const src = new PolicyInstanceDataSource(this.policySvc, null, this.sort, this.notificationService);
            this.instanceDataSources.set(policyType.name, src);
        }
        return this.instanceDataSources.get(policyType.name);
    }

    private getPolicyDialogProperties(policyType: PolicyType, instance: PolicyInstance): MatDialogConfig {
        const policyTypeId = policyType.policy_type_id;
        const createSchema = policyType.create_schema;
        const instanceId = instance ? instance.instanceId : null;
        const instanceJson = instance ? instance.instance : null;
        const name = policyType.name;

        return {
            maxWidth: '1200px',
            height: '1200px',
            width: '900px',
            role: 'dialog',
            data: {
                policyTypeId,
                createSchema,
                instanceId,
                instanceJson,
                name
            }
        };
    }

    createPolicyInstance(policyType: PolicyType): void {
        const dialogRef = this.dialog.open(PolicyInstanceDialogComponent, this.getPolicyDialogProperties(policyType, null));
        if (this.isInstancesShown(policyType)) {
            dialogRef.afterClosed().subscribe(
                () => {
                    this.instanceDataSources.get(policyType.name).loadTable(policyType);
                }
            );
        }
    }

    toggleListInstances(policyType: PolicyType): void {
        if (this.expandedTypes.has(policyType.name)) {
            this.expandedTypes.delete(policyType.name);
            this.instanceDataSources.delete(policyType.name);
        } else {
            this.expandedTypes.set(policyType.name, policyType);
            this.instanceDataSources.get(policyType.name).loadTable(policyType);
        }
    }

    isInstancesShown(policyType: PolicyType) {
        return this.expandedTypes.has(policyType.name);
    }


    deleteInstance(policyType: PolicyType, instance: PolicyInstance): void {
        this.confirmDialogService
            .openConfirmDialog('Are you sure you want to delete this policy instance?')
            .afterClosed().subscribe(
                (res: any) => {
                    if (res) {
                        this.policySvc.deletePolicy(policyType.policy_type_id, instance.instanceId)
                            .subscribe(
                                (response: HttpResponse<Object>) => {
                                    switch (response.status) {
                                        case 200:
                                            this.notificationService.success('Delete succeeded!');
                                            this.instanceDataSources.get(policyType.name).loadTable(policyType);
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

    modifyInstance(policyType: PolicyType, instance: PolicyInstance): void {
        this.policySvc.getPolicy(policyType.policy_type_id, instance.instanceId).subscribe(
            (refreshedJson: any) => {
                instance.instance = JSON.stringify(refreshedJson);
                this.dialog.open(PolicyInstanceDialogComponent, this.getPolicyDialogProperties(policyType, instance));
            },
            (httpError: HttpErrorResponse) => {
                this.notificationService.error('Could not refresh instance ' + httpError.message);
                this.dialog.open(PolicyInstanceDialogComponent, this.getPolicyDialogProperties(policyType, instance));
            }
        );
    }

    getPolicyTypeName(type: PolicyType) {
        const schema = JSON.parse(type.create_schema);
        if (schema.title) {
            return schema.title;
        }
        return type.name;
    }

}
