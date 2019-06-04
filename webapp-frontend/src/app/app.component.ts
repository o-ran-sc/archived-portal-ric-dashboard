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
import {Component, OnInit, ViewChild} from '@angular/core';
import {UiService} from './services/ui/ui.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  showMenu = false;
  darkModeActive: boolean;
  @ViewChild('mainName') mainContainer; 
  @ViewChild('footerName') footerContainer;

  constructor(public ui: UiService) {
  }

  ngOnInit() {
    this.ui.darkModeState.subscribe((value) => {
      this.darkModeActive = value;
    });
  }

  toggleMenu() {
    const htmlElementMain = this.mainContainer.nativeElement as HTMLElement;
    const htmlElementFooter = this.footerContainer.nativeElement as HTMLElement;
    this.showMenu = !this.showMenu;
    if (this.showMenu) {
      htmlElementMain.style.marginLeft = "420px";
      htmlElementFooter.style.marginLeft = "420px";
    } else {
        htmlElementMain.style.marginLeft = "0px";
        htmlElementFooter.style.marginLeft = "10px";
    }
  }

  modeToggleSwitch() {
    this.ui.darkModeState.next(!this.darkModeActive);
  }

}
