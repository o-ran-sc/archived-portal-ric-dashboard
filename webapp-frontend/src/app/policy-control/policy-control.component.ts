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
import { MatDialog } from '@angular/material/dialog';
import { MatSort } from '@angular/material/sort';
import { Router } from '@angular/router';
import { PolicyType } from '../interfaces/policy.types';
import { PolicyService } from '../services/policy/policy.service';
import { ErrorDialogService } from '../services/ui/error-dialog.service';
import { NotificationService } from '../services/ui/notification.service';
import { PolicyControlDataSource } from './policy-control.datasource';
import { PolicyInstanceDialogComponent } from './policy-instance-dialog.component';
import { enableDebugTools } from '@angular/platform-browser';

@Component({
    selector: 'rd-policy-control',
    templateUrl: './policy-control.component.html',
    styleUrls: ['./policy-control.component.scss'],
})
export class PolicyControlComponent implements OnInit {

    displayedColumns: string[] = ['name', 'description', 'action'];
    dataSource: PolicyControlDataSource;
    @ViewChild(MatSort, { static: true }) sort: MatSort;

    constructor(
        private policySvc: PolicyService,
        private dialog: MatDialog,
        private router: Router,
        private errorDialogService: ErrorDialogService,
        private notificationService: NotificationService) { }

    ngOnInit() {
        this.dataSource = new PolicyControlDataSource(this.policySvc, this.sort, this.notificationService);
        this.dataSource.loadTable();
    }


    createPolicyInstance(policyType: PolicyType): void {
        this.dialog.open(PolicyInstanceDialogComponent, {
            maxWidth: '1200px',
            height: '1200px',
            width: '1200px',
            role: 'dialog',
            data: policyType
        });
    }

    listInstances(policyType: PolicyType): void {
        this.router.navigate(['/policyInstance',
            { policy_type_id: policyType.policy_type_id,
                name: policyType.name,
                create_schema: policyType.create_schema }]);
    }
}
