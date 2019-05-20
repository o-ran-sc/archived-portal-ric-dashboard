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
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ANRXappService } from '../services/anr-xapp/anr-xapp.service';
import { ErrorDialogService } from '../services/ui/error-dialog.service';
import { ANRNeighborCellRelation, ANRNeighborCellRelationMod } from '../interfaces/anr-xapp.types';

@Component({
    selector: 'app-ncr-edit-dialog',
    templateUrl: './anr-edit-ncr-dialog.component.html',
    styleUrls: ['./anr-edit-ncr-dialog.component.scss']
})

export class ANREditNCRDialogComponent implements OnInit {

    private ncrDialogForm: FormGroup;

    constructor(
        private dialogRef: MatDialogRef<ANREditNCRDialogComponent>,
        private dataService: ANRXappService, private errorService: ErrorDialogService,
        @Inject(MAT_DIALOG_DATA) private data: ANRNeighborCellRelation) {
        console.log('constructed with data ' + data);
    }

    ngOnInit() {
        const namePattern = /^([A-Z])+([0-9])+$/;
        this.ncrDialogForm = new FormGroup({
            cellIdentifierNrcgi: new FormControl(this.data.cellIdentifierNrcgi, [Validators.required, Validators.pattern(namePattern)]),
            neighborCellNrpci: new FormControl(this.data.neighborCellNrpci, [Validators.pattern(namePattern)]),
            neighborCellNrcgi: new FormControl(this.data.neighborCellNrcgi, [Validators.pattern(namePattern)]),
            flagNoHo: new FormControl(this.data.flagNoHo),
            flagNoXn: new FormControl(this.data.flagNoXn),
            flagNoRemove: new FormControl(this.data.flagNoRemove)
        });
    }

    onCancel() {
        this.dialogRef.close();
    }

    modifyNcr = (ncrFormValue: ANRNeighborCellRelation) => {
      if (this.ncrDialogForm.valid) {
        const table = new Array<ANRNeighborCellRelationMod>();
        this.dataService.modifyNcrt(ncrFormValue.cellIdentifierNrcgi, table).subscribe((val: any[]) => {},
            (error => {
                this.errorService.displayError('NCR update failed: ' + error.message);
            })
        );
        this.dialogRef.close();
      }
    }

    public hasError(controlName: string, errorName: string) {
        if (this.ncrDialogForm.controls[controlName].hasError(errorName)) {
          return true;
        }
        return false;
    }

    public validateControl(controlName: string) {
        if (this.ncrDialogForm.controls[controlName].invalid && this.ncrDialogForm.controls[controlName].touched) {
            return true;
        }
        return false;
    }

}
