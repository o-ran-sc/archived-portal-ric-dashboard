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
import { Component, OnInit, Inject } from '@angular/core';
import { LocalDataSource } from 'ng2-smart-table';
import { SignalService } from '../services/signal/signal.service';
import { Router } from '@angular/router';
import { MatDialog, MatDialogConfig, MatDialogRef, MAT_DIALOG_DATA} from '@angular/material/dialog';
import {MatFormFieldModule} from '@angular/material';
import { FormGroup, FormControl, FormBuilder } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs/Rx';

export interface DialogData {
    ranName: string;
    ranIp: number;
    ranPort: number;
}

@Component({
    selector: 'app-signal',
    templateUrl: 'signal.component.html',
    styleUrls: ['signal.component.css']
})
export class SignalComponent {

    settings = {
        hideSubHeader: true,
        actions: {
            columnTitle: 'Actions',
            add: false,
            edit: false,
            delete: false,
            position: 'right'
        },
        columns: {
            requestType: {
                title: 'Request Type',
                type: 'string',
            },
            ranName: {
                title: 'eNodeB/gNodeB Name',
                type: 'string',
            },
            ranIp: {
                title: 'IP',
                type: 'number',
            },
            ranPort: {
                title: 'Port',
                type: 'number',
            },
            responseCode: {
                title: 'Response',
                type: 'number',
            },
            timeStamp: {
                title: 'Time Stamp',
                type: 'string',
            }
        }
    };

    source: LocalDataSource = new LocalDataSource();

    ranName: string;

    ranIp: number;

    ranPort: number;

    constructor(private service: SignalService, public dialog: MatDialog, private http: HttpClient) {
        this.service.getAll().subscribe((val: any[]) => this.source.load(val));
    }

    openRanConnectDialog() {

        const dialogRef = this.dialog.open(AppRANConnectDialog,  {
            width: '450px',
            data: {ranName: this.ranName, ranIp: this.ranIp, ranPort: this.ranPort}
    })

        dialogRef.afterClosed().subscribe(result => {
            this.service.getAll().subscribe((val: any[]) => this.source.load(val));
        });

    }
}

@Component({
    selector: 'app-signal-ranconnect-dialog',
    templateUrl: 'signal.component.ranconnect-dialog.html',
    styleUrls: ['signal.component.css']
})

export class AppRANConnectDialog implements OnInit {

    constructor(public dialogRef: MatDialogRef<AppRANConnectDialog>,
          private service: SignalService,
          @Inject(MAT_DIALOG_DATA) public data: DialogData) { 
    }

    ngOnInit() {
    }

    onNoClick(): void {
        this.dialogRef.close();
    }

    connectRAN(): void {        
        this.service.x2Setup(this.data).subscribe((val: any[]) => {});
        this.dialogRef.close();
    }

}
