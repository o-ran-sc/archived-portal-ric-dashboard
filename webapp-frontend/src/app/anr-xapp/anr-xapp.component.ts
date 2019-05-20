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
import { fromEvent } from 'rxjs/observable/fromEvent';
import { debounceTime, distinctUntilChanged, tap } from 'rxjs/operators';
import { ANRXappDataSource } from './anr-xapp.datasource';
import { ANRXappService } from '../services/anr-xapp/anr-xapp.service';

@Component({
  selector: 'app-anr',
  templateUrl: './anr-xapp.component.html',
  styleUrls: ['./anr-xapp.component.scss']
})
export class AnrXappComponent implements AfterViewInit, OnInit {

  dataSource: ANRXappDataSource;
  @ViewChild('ggnbid') ggnbid: ElementRef;

  displayedColumns = [ 'cellIdentifierNrcgi', 'neighborCellNrpci', 'neighborCellNrcgi',
    'flagNoHo', 'flagNoXn', 'flagNoRemove' ];

  constructor(private anrXappService: ANRXappService) { }

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
    this.dataSource.loadTable(
        this.ggnbid.nativeElement.value,
        '',
        20);
  }

}
