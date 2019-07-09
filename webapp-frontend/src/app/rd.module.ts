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
import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { MatRadioModule } from '@angular/material/radio';
import { MatTooltipModule } from '@angular/material/tooltip';
import { ChartsModule } from 'ng2-charts';
import { MDBBootstrapModule } from 'angular-bootstrap-md';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ToastrModule } from 'ngx-toastr';

import { AcXappComponent } from './ac-xapp/ac-xapp.component';
import { AddDashboardUserDialogComponent } from './user/add-dashboard-user-dialog/add-dashboard-user-dialog.component';
import { AnrEditNcrDialogComponent } from './anr-xapp/anr-edit-ncr-dialog.component';
import { AnrXappComponent } from './anr-xapp/anr-xapp.component';
import { AppControlComponent } from './app-control/app-control.component';
import { AppMgrService } from './services/app-mgr/app-mgr.service';
import { CatalogCardComponent } from './ui/catalog-card/catalog-card.component';
import { CatalogComponent } from './catalog/catalog.component';
import { ConfigEventComponent } from './ui/config-event/config-event.component';
import { ConfirmDialogComponent } from './ui/confirm-dialog/confirm-dialog.component';
import { ControlCardComponent } from './ui/control-card/control-card.component';
import { ControlComponent } from './control/control.component';
import { DashboardService } from './services/dashboard/dashboard.service';
import { E2ManagerService } from './services/e2-mgr/e2-mgr.service';
import { EditDashboardUserDialogComponent } from './user/edit-dashboard-user-dialog/edit-dashboard-user-dialog.component';
import { ErrorDialogComponent } from './ui/error-dialog/error-dialog.component';
import { ErrorDialogService } from './services/ui/error-dialog.service';
import { FooterComponent } from './footer/footer.component';
import { MainComponent } from './main/main.component';
import { ModalEventComponent } from './ui/modal-event/modal-event.component';
import { RanControlComponent } from './ran-control/ran-control.component';
import { RanControlConnectDialogComponent } from './ran-control/ran-connection-dialog.component';
import { RdComponent } from './rd.component';
import { RdRoutingModule } from './rd-routing.module';
import { SidenavListComponent } from './navigation/sidenav-list/sidenav-list.component';
import { StatCardComponent } from './ui/stat-card/stat-card.component';
import { StatsComponent } from './stats/stats.component';
import { UiService } from './services/ui/ui.service';
import { UserComponent } from './user/user.component';

@NgModule({
  declarations: [
    AcXappComponent,
    AddDashboardUserDialogComponent,
    AnrEditNcrDialogComponent,
    AnrXappComponent,
    AppControlComponent,
    CatalogCardComponent,
    CatalogComponent,
    ConfigEventComponent,
    ConfirmDialogComponent,
    ControlCardComponent,
    ControlComponent,
    EditDashboardUserDialogComponent,
    ErrorDialogComponent,
    FooterComponent,
    MainComponent,
    ModalEventComponent,
    RanControlComponent,
    RanControlConnectDialogComponent,
    RdComponent,
    SidenavListComponent,
    StatCardComponent,
    StatsComponent,
    UserComponent
  ],
  imports: [
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
    MatTooltipModule,
    MDBBootstrapModule.forRoot(),
    RdRoutingModule,
    ReactiveFormsModule,
    ToastrModule.forRoot()
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
    RanControlConnectDialogComponent,
    ErrorDialogComponent,
  ],
  entryComponents: [
    AddDashboardUserDialogComponent,
    AnrEditNcrDialogComponent,
    ConfirmDialogComponent,
    EditDashboardUserDialogComponent,
    ErrorDialogComponent,
    RanControlConnectDialogComponent,
  ],
  providers: [
    AppMgrService,
    DashboardService,
    E2ManagerService,
    ErrorDialogService,
    UiService
  ],
  bootstrap: [RdComponent]
})
export class RdModule { }
