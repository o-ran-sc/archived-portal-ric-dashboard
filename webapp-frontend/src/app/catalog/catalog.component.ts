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
import { Component, Inject } from '@angular/core';
import { LocalDataSource } from 'ng2-smart-table';
import { CatalogService } from '../services/catalog/catalog.service';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

export interface DialogData {
    name: string;
}


@Component({
  selector: 'app-catalog',
  templateUrl: './catalog.component.html',
  styleUrls: ['./catalog.component.css']
})
export class CatalogComponent {

  settings = {
    hideSubHeader: true,
    actions: {
      columnTitle: 'Actions',
      add: false,
      edit: false,
      delete: false,
      custom: [
      { name: 'deployxApp', title: 'Deploy'},
    ],
      position: 'right'

    },
    columns: {
      name: {
        title: 'xApp Name',
        type: 'string',
      },
      version: {
        title: 'xApp Version',
        type: 'string',
      },
      status: {
        title: 'Status',
        type: 'string',
      },
    },
  };

  source: LocalDataSource = new LocalDataSource();

    constructor(private service: CatalogService, public dialog: MatDialog) {
    this.service.getAll().subscribe((val:any[]) => this.source.load(val));
  }


    onDeployxApp(event): void {
        const dialogRef = this.dialog.open(AppCatalogDeployDialog, {
            width: '400px',
            data: { name: event.data.name }
        });

        dialogRef.afterClosed().subscribe(result => {
            console.log('The dialog was closed');
        });
    }

}

@Component({
    selector: 'app-catalog-deploy-dialog',
    templateUrl: 'catalog.component.deploy-dialog.html',
    styleUrls: ['./catalog.component.css']
})

export class AppCatalogDeployDialog{

    constructor(
        public dialogRef: MatDialogRef<AppCatalogDeployDialog>,
        private service: CatalogService,
        @Inject(MAT_DIALOG_DATA) public data: DialogData) { }

    onNoClick(): void {
        this.dialogRef.close();
    }

    deployXapp(): void {
        this.service.deployXapp(this.data.name).subscribe((val: any[]) => { });
        this.dialogRef.close();
    }

}
