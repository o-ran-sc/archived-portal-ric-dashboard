import { Injectable } from '@angular/core';
import { MatDialog } from '@angular/material';
import { ConfirmDialogComponent } from './../../ui/confirm-dialog/confirm-dialog.component'

@Injectable({
  providedIn: 'root'
})
export class ConfirmDialogService {

    constructor(private dialog: MatDialog) { }

    openConfirmDialog(msg) {
        return this.dialog.open(ConfirmDialogComponent, {
            width: '390px',
            panelClass: 'confirm-dialog-container',
            disableClose: true,
            position: { top: "10px" },
            data: {
                message: msg
            }
        });
    }
}
