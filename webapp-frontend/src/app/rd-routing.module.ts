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
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Routes, RouterModule } from '@angular/router';

import { AcXappComponent } from './ac-xapp/ac-xapp.component';
import { AnrXappComponent } from './anr-xapp/anr-xapp.component';
import { CatalogComponent } from './catalog/catalog.component';
import { ControlComponent } from './control/control.component';
import { LoginComponent } from './login/login.component';
import { StatsComponent } from './stats/stats.component';
import { UserComponent } from './user/user.component';

const routes: Routes = [
    {path: '', component: LoginComponent},
    {path: 'login', component: LoginComponent},
    {path: 'catalog', component: CatalogComponent},
    {path: 'control', component: ControlComponent},
    {path: 'ac', component: AcXappComponent},
    {path: 'anr', component: AnrXappComponent},
    {path: 'stats', component: StatsComponent},
    {path: 'user', component: UserComponent},
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

export class RdRoutingModule { }
