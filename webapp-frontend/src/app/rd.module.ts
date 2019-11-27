/*-
 * ========================LICENSE_START=================================
 * O-RAN-SC
 * %%
 * Copyright (C) 2019 AT&T Intellectual Property
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
import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatCardModule } from '@angular/material/card';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDialogModule } from '@angular/material/dialog';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatRadioModule } from '@angular/material/radio';
import { MatSelectModule } from '@angular/material/select';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatSliderModule } from '@angular/material/slider';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatSortModule } from '@angular/material/sort';
import { MatTableModule } from '@angular/material/table';
import { MatTabsModule } from '@angular/material/tabs';
import { MatTooltipModule } from '@angular/material/tooltip';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MDBBootstrapModule } from 'angular-bootstrap-md';
import { MaterialDesignFrameworkModule } from 'angular6-json-schema-form';
import { ChartsModule } from 'ng2-charts';
import { ToastrModule } from 'ngx-toastr';

import { AcXappComponent } from './ac-xapp/ac-xapp.component';
import { AppConfigurationComponent } from './app-configuration/app-configuration.component';
import { AppControlComponent } from './app-control/app-control.component';
import { CaasIngressComponent } from './caas-ingress/caas-ingress.component';
import { CatalogComponent } from './catalog/catalog.component';
import { ControlComponent } from './control/control.component';
import { FooterComponent } from './footer/footer.component';
import { MainComponent } from './main/main.component';
import { SidenavListComponent } from './navigation/sidenav-list/sidenav-list.component';
import { PlatformComponent } from './platform/platform.component';
import { RanControlConnectDialogComponent } from './ran-control/ran-connection-dialog.component';
import { RanControlComponent } from './ran-control/ran-control.component';
import { RdRoutingModule } from './rd-routing.module';
import { RdComponent } from './rd.component';
import { AppMgrService } from './services/app-mgr/app-mgr.service';
import { DashboardService } from './services/dashboard/dashboard.service';
import { E2ManagerService } from './services/e2-mgr/e2-mgr.service';
import { InstanceSelectorService } from './services/instance-selector/instance-selector.service';
import { ErrorDialogService } from './services/ui/error-dialog.service';
import { UiService } from './services/ui/ui.service';
import { StatsComponent } from './stats/stats.component';
import { CatalogCardComponent } from './ui/catalog-card/catalog-card.component';
import { ConfirmDialogComponent } from './ui/confirm-dialog/confirm-dialog.component';
import { ControlCardComponent } from './ui/control-card/control-card.component';
import { ErrorDialogComponent } from './ui/error-dialog/error-dialog.component';
import { LoadingDialogComponent } from './ui/loading-dialog/loading-dialog.component';
import { StatCardComponent } from './ui/stat-card/stat-card.component';
import { AddDashboardUserDialogComponent } from './user/add-dashboard-user-dialog/add-dashboard-user-dialog.component';
import { EditDashboardUserDialogComponent } from './user/edit-dashboard-user-dialog/edit-dashboard-user-dialog.component';
import { UserComponent } from './user/user.component';



@NgModule({
  declarations: [
    AcXappComponent,
    AddDashboardUserDialogComponent,
    AppConfigurationComponent,
    AppControlComponent,
    CaasIngressComponent,
    CatalogCardComponent,
    CatalogComponent,
    ConfirmDialogComponent,
    ControlCardComponent,
    ControlComponent,
    EditDashboardUserDialogComponent,
    ErrorDialogComponent,
    FooterComponent,
    LoadingDialogComponent,
    MainComponent,
    PlatformComponent,
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
    FlexLayoutModule,
    FormsModule,
    HttpClientModule,
    MatButtonModule,
    MatButtonToggleModule,
    MatCardModule,
    MatCheckboxModule,
    MatDialogModule,
    MaterialDesignFrameworkModule,
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
    ErrorDialogComponent,
    FormsModule,
    MatButtonModule,
    MatButtonToggleModule,
    MatCardModule,
    MatDialogModule,
    MatExpansionModule,
    MatFormFieldModule,
    MatGridListModule,
    MatIconModule,
    MatInputModule,
    MatListModule,
    MatSidenavModule,
    MatSliderModule,
    MatSlideToggleModule,
    MatTabsModule,
    RanControlConnectDialogComponent
  ],
  entryComponents: [
    AddDashboardUserDialogComponent,
    AppConfigurationComponent,
    ConfirmDialogComponent,
    EditDashboardUserDialogComponent,
    ErrorDialogComponent,
    LoadingDialogComponent,
    RanControlConnectDialogComponent
  ],
  providers: [
    AppMgrService,
    DashboardService,
    E2ManagerService,
    ErrorDialogService,
    InstanceSelectorService,
    UiService
  ],
  bootstrap: [RdComponent]
})
export class RdModule { }
