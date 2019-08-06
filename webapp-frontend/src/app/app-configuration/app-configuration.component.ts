import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { AppMgrService } from '../services/app-mgr/app-mgr.service';
import { ErrorDialogService } from '../services/ui/error-dialog.service';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { finalize } from 'rxjs/operators';


@Component({
  selector: 'rd-app-configuration',
  templateUrl: './app-configuration.component.html',
  styleUrls: ['./app-configuration.component.scss']
})
export class AppConfigurationComponent implements OnInit {

  private loadingSubject = new BehaviorSubject<boolean>(false);
  public loading$ = this.loadingSubject.asObservable();

  constructor(
    private dialogRef: MatDialogRef<AppConfigurationComponent>,
    private appMgrService: AppMgrService,
    private errorDiaglogService: ErrorDialogService,
    @Inject(MAT_DIALOG_DATA) private data
  ) { }

  xappmetadata: any;
  xappconfigschema: any;
  xappconfigdata: any; 

  ngOnInit() {
    //load schema and data
    this.loadingSubject.next(true);
    this.appMgrService.getConfig(this.data.name)
      .pipe(
        finalize(() => this.loadingSubject.next(false))
      )
      .subscribe(
      (allConfig: any) => {
        this.loadConfigForm(this.data.name, allConfig)
      }
    )
  }

  updateconfig(event) {
    var config = {
      metadata: this.xappmetadata,
      descriptor: this.xappconfigschema,
      config: event
    }
    this.appMgrService.putConfig(config)
    this.dialogRef.close();
  }

  loadConfigForm(name: string, allConfig: any) {
    var xappConfig = allConfig.find(xapp => xapp.metadata.name == name)
    if (xappConfig != null) {
      this.xappmetadata = xappConfig.metadata
      this.xappconfigschema = xappConfig.descriptor;
      this.xappconfigdata = xappConfig.config;
    } else {
      this.errorDiaglogService.displayError("Cannot find configration data for " + name);
      this.dialogRef.close();
    }
  }
}
