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
import { HttpClientModule } from '@angular/common/http'; 

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './login/login.component';
import { CatalogComponent } from './catalog/catalog.component';
import { UiService } from './services/ui/ui.service';
import { AppMgrService } from './services/app-mgr/app-mgr.service';
import { DashboardService } from './services/dashboard/dashboard.service';
import { E2ManagerService } from './services/e2-mgr/e2-mgr.service';
import { SidenavListComponent } from './navigation/sidenav-list/sidenav-list.component';
import { AppControlComponent } from './app-control/app-control.component';
import { ControlComponent } from './control/control.component';
import { RANConnectionDialogComponent } from './ran-control/ran-connection-dialog.component';
import { RanControlComponent } from './ran-control/ran-control.component';
import { ANREditNCRDialogComponent } from './anr-xapp/anr-edit-ncr-dialog.component';
import { StatsComponent } from './stats/stats.component';
import { UserComponent } from './admin/user.component';
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
import { AddDashboardUserDialogComponent } from './admin/add-dashboard-user-dialog/add-dashboard-user-dialog.component';
import { EditDashboardUserDialogComponent } from './admin/edit-dashboard-user-dialog/edit-dashboard-user-dialog.component';



@NgModule({
  declarations: [
    AcXappComponent,
    UserComponent,
    ANREditNCRDialogComponent,
    AnrXappComponent,
    AppComponent,
    CatalogComponent,
    CatalogCardComponent,
    ConfigEventComponent,
    ConfirmDialogComponent,
    ControlCardComponent,
    AppControlComponent,
    ErrorDialogComponent,
    FooterComponent,
    LoginComponent,
    ModalEventComponent,
    RanControlComponent,
    RANConnectionDialogComponent,
    SidenavListComponent,
    StatCardComponent,
    StatsComponent,
    AddDashboardUserDialogComponent,
    EditDashboardUserDialogComponent,
    ControlComponent
  ],
    imports: [
    AppRoutingModule,
    BrowserModule,
    BrowserAnimationsModule,
    ChartsModule,
    FormsModule,
    HttpClientModule,
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
    ErrorDialogComponent,
    AddDashboardUserDialogComponent,
    EditDashboardUserDialogComponent
    ],
  providers: [
      UiService,
      AppMgrService,
      DashboardService,
      E2ManagerService,
      ErrorDialogService
    ],
  bootstrap: [AppComponent]
})
export class AppModule { }

