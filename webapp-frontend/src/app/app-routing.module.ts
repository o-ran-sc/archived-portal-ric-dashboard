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
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Routes, RouterModule } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { CatalogComponent } from './catalog/catalog.component';
import { ControlComponent } from './control/control.component';
import { SignalComponent } from './signal/signal.component';
import { StatsComponent } from './stats/stats.component';
import { AdminComponent } from './admin/admin.component';
import { XappComponent } from './xapp/xapp.component';

const routes: Routes = [
    {path: '', component: LoginComponent},
    {path: 'login', component: LoginComponent},
    {path: 'catalog', component: CatalogComponent},
    {path: 'control', component: ControlComponent},
    {path: 'signal', component: SignalComponent},
    {path: 'stats', component: StatsComponent},
    {path: 'admin', component: AdminComponent},
    {path: 'xapp', component: XappComponent},
];

@NgModule({
  imports: [
      CommonModule,
      RouterModule.forRoot(routes)],      
  exports: [
      RouterModule
    ],
    declarations: []
})
export class AppRoutingModule { }
