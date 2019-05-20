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

import { AfterViewInit, Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { fromEvent } from 'rxjs/observable/fromEvent';
import { debounceTime, distinctUntilChanged, tap } from 'rxjs/operators';
import { ANRNeighborCellRelation } from '../interfaces/anr-xapp.types';
import { ANRXappDataSource } from './anr-xapp.datasource';
import { ANRXappService } from '../services/anr-xapp/anr-xapp.service';
import { ANREditNCRDialogComponent } from './anr-edit-ncr-dialog.component';
import { ConfirmDialogService } from './../services/ui/confirm-dialog.service';
import { ErrorDialogService } from '../services/ui/error-dialog.service';
import { NotificationService } from './../services/ui/notification.service';

@Component({
  selector: 'app-anr',
  templateUrl: './anr-xapp.component.html',
  styleUrls: ['./anr-xapp.component.scss']
})
export class AnrXappComponent implements AfterViewInit, OnInit {

  dataSource: ANRXappDataSource;
  @ViewChild('ggnbid') ggnbid: ElementRef;

  displayedColumns = ['cellIdentifierNrcgi', 'neighborCellNrpci', 'neighborCellNrcgi',
    'flagNoHo', 'flagNoXn', 'flagNoRemove', 'action'];

  constructor(
    private anrXappService: ANRXappService,
    private dialog: MatDialog,
    private confirmDialogService: ConfirmDialogService,
    private errorDialogService: ErrorDialogService,
    private notificationService: NotificationService) { }

  ngOnInit() {
    this.dataSource = new ANRXappDataSource(this.anrXappService);
    this.dataSource.loadTable();
  }

  ngAfterViewInit() {
    fromEvent(this.ggnbid.nativeElement, 'keyup')
      .pipe(
        debounceTime(150),
        distinctUntilChanged(),
        tap(() => {
          this.loadNcrtPage();
        })
      )
      .subscribe();
  }

  loadNcrtPage() {
    this.dataSource.loadTable(this.ggnbid.nativeElement.value, '', '');
  }

  modifyNcr(ncr: ANRNeighborCellRelation): void {
    const dialogRef = this.dialog.open(ANREditNCRDialogComponent, {
      width: '300px',
      data: ncr
    });
    dialogRef.afterClosed().subscribe(result => {
      this.loadNcrtPage();
    });
  }

  deleteNcr(ncr: ANRNeighborCellRelation): void {
    this.confirmDialogService
      .openConfirmDialog('Are you sure you want to delete this relation?')
      .afterClosed().subscribe(res => {
        if (res) {
          this.anrXappService.deleteNcr(ncr.servingCellNrcgi, ncr.neighborCellNrpci)
            .subscribe(
              response => {
                switch (response.status) {
                  case 200:
                    this.notificationService.success('Delete succeeded!');
                    break;
                  default:
                    this.notificationService.warn('Delete failed.');
                }
              },
              error => {
                this.errorDialogService.displayError(error.message);
              });
        }
      });
  }

}
