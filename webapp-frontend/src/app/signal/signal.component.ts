/*-
 * ========================LICENSE_START=================================
 * ORAN-OSC
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
    ranIp: string;
    ranPort: string;
}

@Component({
    selector: 'app-signal',
    templateUrl: './signal.component.html',
    styleUrls: ['./signal.component.css']
})
export class SignalComponent {

    settings = {
        hideSubHeader: true,
        actions: {
            columnTitle: 'Actions',
            add: false,
            edit: false,
            delete: true,
            position: 'right'
        },
        columns: {
            ranName: {
                title: 'eNodeB/gNodeB',
                type: 'string',
            },
            ranIp: {
                title: 'IP',
                type: 'number',
            },
            ranPort: {
                title: 'Port',
                type: 'number',
            }
           /* k8Status: {
                title: 'Status',
                type: 'string',
                editable: false
            }*/

        },
    };

    source: LocalDataSource = new LocalDataSource();

    ranName: string;

    ranIp: number;

    ranPort: number;

    constructor(private service: SignalService, public dialog: MatDialog, private http: HttpClient) {
        this.service.getAll().subscribe((val: any[]) => this.source.load(val));
    }

    openRanConnectDialog() {
        // const dialogConfig = new MatDialogConfig();
        //dialogConfig.width = '500px';
        
       /* dialogConfig.data = {
            id: 1,
            rannodename: ''
                   
            //title: 'Angular For Beginners'
        };*/
        
        //this.dialog.afterAllClosed.subscribe(()=> this.http.get('api/e2mgr/setup'));
        
        const dialogRef = this.dialog.open(AppRANConnectDialog,  {
            width: '450px',
            data: {ranName: this.ranName, ranIp: this.ranIp, ranPort: this.ranPort}
    })
    
    //subscribe(()=> this.http.get('api/e2mgr/setup'));
        //this.service.getAll().subscribe((val: any[]) => this.source.load(val));
        dialogRef.afterClosed().subscribe(result => {
            //console.log('The dialog was closed');
            //data => console.log("Dialog output:", data);
            //return this.http.get('api/e2mgr/setup');
            //const val[:any] = this.service.getAll();
            //this.source.l
            this.service.getAll().subscribe((val: any[]) => this.source.load(val));
            //this.source.load(this.service.getAll());
            
        });

        
    }
}

@Component({
    selector: 'app-signal-ranconnect-dialog',
    templateUrl: 'signal.component.ranconnect-dialog.html',
    styleUrls: ['./signal.component.css']
})

export class AppRANConnectDialog implements OnInit {
    

    //myGroup: FormGroup;
    
    //rannodename:string;
    
    
    constructor(
        
        
        //private fb: FormBuilder,
        public dialogRef: MatDialogRef<AppRANConnectDialog>,
        private service: SignalService,
        @Inject(MAT_DIALOG_DATA) public data: DialogData) { 

           /* this.myGroup = new FormGroup({
                rannodename: new FormControl()
            });*/
        
            //console.log(data);
            //this.rannodename = data.rannodename;
            //console.log('My passed data', this.rannodename);
        }
       
        ngOnInit() {
         /*   this.myGroup = this.fb.group({
            rannodename: [this.rannodename, []]
           
        });*/
      }
    
        onNoClick(): void {
        this.dialogRef.close();
    }

    connectRAN(): void {
        console.log('My passed data', this.data);
        this.service.connectRAN(this.data).subscribe((val: any[]) => console.log(val));
        //this.service.getAll().subscribe((val: any[]) => SignalComponent
        this.dialogRef.close();
    }

}






