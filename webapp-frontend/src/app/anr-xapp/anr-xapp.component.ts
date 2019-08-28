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

import { HttpResponse } from '@angular/common/http';
import { AfterViewInit, Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { MatSort } from '@angular/material';
import { MatDialog } from '@angular/material/dialog';
import { fromEvent } from 'rxjs/observable/fromEvent';
import { debounceTime, distinctUntilChanged, finalize, tap } from 'rxjs/operators';
import { ANRNeighborCellRelation } from '../interfaces/anr-xapp.types';
import { ANRXappService } from '../services/anr-xapp/anr-xapp.service';
import { ErrorDialogService } from '../services/ui/error-dialog.service';
import { LoadingDialogService } from '../services/ui/loading-dialog.service';
import { ConfirmDialogService } from './../services/ui/confirm-dialog.service';
import { NotificationService } from './../services/ui/notification.service';
import { AnrEditNcrDialogComponent } from './anr-edit-ncr-dialog.component';
import { ANRXappDataSource } from './anr-xapp.datasource';

@Component({
  selector: 'rd-anr',
  templateUrl: './anr-xapp.component.html',
  styleUrls: ['./anr-xapp.component.scss']
})
export class AnrXappComponent implements AfterViewInit, OnInit {

  dataSource: ANRXappDataSource;
  gNodeBIds: string[];
  @ViewChild('ggNodeB', {static: true}) ggNodeB: ElementRef;
  @ViewChild('servingCellNrcgi', {static: true}) servingCellNrcgi: ElementRef;
  @ViewChild('neighborCellNrpci', {static: true}) neighborCellNrpci: ElementRef;
  @ViewChild(MatSort, {static: true}) sort: MatSort;

  displayedColumns = ['cellIdentifierNrcgi', 'neighborCellNrpci', 'neighborCellNrcgi',
    'flagNoHo', 'flagNoXn', 'flagNoRemove', 'action'];

  constructor(
    private anrXappService: ANRXappService,
    private dialog: MatDialog,
    private confirmDialogService: ConfirmDialogService,
    private errorDialogService: ErrorDialogService,
    private loadingDialogService: LoadingDialogService,
    private notificationService: NotificationService) { }

  ngOnInit() {
    this.dataSource = new ANRXappDataSource(this.anrXappService, this.sort, this.notificationService);
    this.dataSource.loadTable();
    // Empty string occurs first in the array of gNodeBIds
    this.anrXappService.getgNodeBs().subscribe((res: string[]) => this.gNodeBIds = res);
  }

  ngAfterViewInit() {
    // the selector event calls loadNcrtPage() directly.
    fromEvent(this.servingCellNrcgi.nativeElement, 'keyup')
      .pipe(
        debounceTime(150),
        distinctUntilChanged(),
        tap(() => {
          this.loadNcrtPage();
        })
      )
      .subscribe();
    fromEvent(this.neighborCellNrpci.nativeElement, 'keyup')
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
    this.dataSource.loadTable(
      this.ggNodeB.nativeElement.value,
      this.servingCellNrcgi.nativeElement.value,
      this.neighborCellNrpci.nativeElement.value);
  }

  modifyNcr(ncr: ANRNeighborCellRelation): void {
    const dialogRef = this.dialog.open(AnrEditNcrDialogComponent, {
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
          this.loadingDialogService.startLoading("Deleting");
          this.anrXappService.deleteNcr(ncr.servingCellNrcgi, ncr.neighborCellNrpci)
            .pipe(
              finalize(() => this.loadingDialogService.stopLoading())
            )
            .subscribe(
              (response: HttpResponse<Object>) => {
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
