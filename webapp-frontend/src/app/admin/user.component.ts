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
import { Component, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSort } from '@angular/material/sort';
import { DashboardService } from '../services/dashboard/dashboard.service';
import { ErrorDialogService } from '../services/ui/error-dialog.service';
import { DashboardUser } from './../interfaces/dashboard.types';
import { ConfirmDialogService } from './../services/ui/confirm-dialog.service';
import { NotificationService } from './../services/ui/notification.service';
import { UserDataSource } from './user.datasource';
import { AddDashboardUserDialogComponent } from './add-dashboard-user-dialog/add-dashboard-user-dialog.component';
import { EditDashboardUserDialogComponent } from './edit-dashboard-user-dialog/edit-dashboard-user-dialog.component';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})

export class UserComponent implements OnInit {

  displayedColumns: string[] = ['id', 'firstName', 'lastName', 'status','action'];
  dataSource: UserDataSource;
  @ViewChild(MatSort) sort: MatSort;

  constructor(
    private dashboardSvc: DashboardService,
    private confirmDialogService: ConfirmDialogService,
    private errorService: ErrorDialogService,
    private notification: NotificationService,
    public dialog: MatDialog) { }

  ngOnInit() {
    this.dataSource = new UserDataSource(this.dashboardSvc, this.sort);
    this.dataSource.loadTable();
  }

  editUser(user: DashboardUser) {
    const dialogRef = this.dialog.open(EditDashboardUserDialogComponent, {
      width: '450px',
      data: user
    });
    dialogRef.afterClosed().subscribe(result => {
      this.dataSource.loadTable();
    });
  }
    

  deleteUser() {
    const aboutError = 'Not implemented yet';
    this.errorService.displayError(aboutError);
  }

  addUser() {
    const dialogRef = this.dialog.open(AddDashboardUserDialogComponent, {
      width: '450px'
    });
    dialogRef.afterClosed().subscribe(result => {
      this.dataSource.loadTable();
    });
  }
}

