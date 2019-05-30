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
import { BrowserModule } from '@angular/platform-browser';
// tslint:disable-next-line:max-line-length
import {MatButtonModule, MatButtonToggleModule, MatCardModule, MatCheckboxModule,
    MatDialogModule, MatExpansionModule, MatFormFieldModule, MatGridListModule,
    MatIconModule, MatInputModule, MatListModule, MatPaginatorModule,
    MatProgressSpinnerModule, MatSelectModule, MatSidenavModule, MatSliderModule,
    MatSlideToggleModule, MatSnackBarModule, MatSortModule, MatTableModule,
    MatTabsModule} from '@angular/material';
import { BrowserAnimationsModule} from '@angular/platform-browser/animations';
import { NgModule } from '@angular/core';
import { MatRadioModule } from '@angular/material/radio';
import { ChartsModule } from 'ng2-charts';
import { MDBBootstrapModule } from 'angular-bootstrap-md';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

// RETIRE THIS
import { Ng2SmartTableModule } from 'ng2-smart-table';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './login/login.component';
import { CatalogComponent } from './catalog/catalog.component';
import { UiService } from './services/ui/ui.service';
import { AdminService } from './services/admin/admin.service';
import { XappMgrService } from './services/xapp-mgr/xapp-mgr.service';
import { DashboardService } from './services/dashboard/dashboard.service';
import { E2ManagerService } from './services/e2-mgr/e2-mgr.service';
import { SidenavListComponent } from './navigation/sidenav-list/sidenav-list.component';
import { ControlComponent } from './control/control.component';
import { RANConnectionDialogComponent } from './ran-connection/ran-connection-dialog.component';
import { RANConnectionComponent } from './ran-connection/ran-connection.component';
import { ANREditNCRDialogComponent } from './anr-xapp/anr-edit-ncr-dialog.component';
import { StatsComponent } from './stats/stats.component';
import { AdminComponent } from './admin/admin.component';
import { CatalogCardComponent } from './ui/catalog-card/catalog-card.component';
import { ControlCardComponent } from './ui/control-card/control-card.component';
import { StatCardComponent } from './ui/stat-card/stat-card.component';
import { ModalEventComponent } from './ui/modal-event/modal-event.component';
import { ConfigEventComponent } from './ui/config-event/config-event.component';
import { ConfirmDialogComponent } from './ui/confirm-dialog/confirm-dialog.component';
import { FooterComponent } from './footer/footer.component';
import { AnrXappComponent } from './anr-xapp/anr-xapp.component';
import { ErrorDialogComponent } from './ui/error-dialog/error-dialog.component';
import { ErrorDialogService } from './services/ui/error-dialog.service';
import { AcXappComponent } from './ac-xapp/ac-xapp.component';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    CatalogComponent,
    SidenavListComponent,
    CatalogCardComponent,
    ControlCardComponent,
    StatCardComponent,
    ControlComponent,
    RANConnectionComponent,
    StatsComponent,
    AdminComponent,
    ModalEventComponent,
    ConfigEventComponent,
    AnrXappComponent,
    RANConnectionDialogComponent,
    ANREditNCRDialogComponent,
    ConfirmDialogComponent,
    FooterComponent,
    ErrorDialogComponent,
    AcXappComponent
  ],
    imports: [
    AppRoutingModule,
    BrowserModule,
    BrowserAnimationsModule,
    ChartsModule,
    FormsModule,
    MatButtonModule,
    MatButtonToggleModule,
    MatCardModule,
    MatCheckboxModule,
    MatDialogModule,
    MatExpansionModule,
    MatFormFieldModule,
    MatGridListModule,
    MatIconModule,
    MatInputModule,
    MatListModule,
    MatPaginatorModule,
    MatProgressSpinnerModule,
    MatRadioModule,
    MatSelectModule,
    MatSliderModule,
    MatSidenavModule,
    MatSlideToggleModule,
    MatSnackBarModule,
    MatSortModule,
    MatTableModule,
    MatTabsModule,
    Ng2SmartTableModule,
    ReactiveFormsModule,
    MDBBootstrapModule.forRoot(),
  ],
    exports: [
    FormsModule,
    MatDialogModule,
    MatButtonToggleModule,
    MatExpansionModule,
    MatSliderModule,
    MatCardModule,
    MatIconModule,
    MatGridListModule,
    MatListModule,
    MatSidenavModule,
    MatSlideToggleModule,
    MatTabsModule,
    MatFormFieldModule,
    MatButtonModule,
    MatInputModule,
    RANConnectionDialogComponent,
    ErrorDialogComponent
    ],
    entryComponents: [
    RANConnectionDialogComponent,
    ANREditNCRDialogComponent,
    ConfirmDialogComponent,
    ErrorDialogComponent
    ],
  providers: [
      UiService,
      AdminService,
      XappMgrService,
      DashboardService,
      E2ManagerService,
      ErrorDialogService
    ],
  bootstrap: [AppComponent]
})
export class AppModule { }

