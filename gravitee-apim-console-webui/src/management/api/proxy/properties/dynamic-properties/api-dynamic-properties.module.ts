/*
 * Copyright (C) 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import {
  GioBannerModule,
  GioFormCronModule,
  GioFormHeadersModule,
  GioFormSlideToggleModule,
  GioIconsModule,
  GioMonacoEditorModule,
  GioSaveBarModule,
} from '@gravitee/ui-particles-angular';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatOptionModule } from '@angular/material/core';
import { MatSelectModule } from '@angular/material/select';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBarModule } from '@angular/material/snack-bar';

import { ApiDynamicPropertiesComponent } from './api-dynamic-properties.component';

@NgModule({
  imports: [
    CommonModule,
    ReactiveFormsModule,

    MatCardModule,
    MatButtonModule,
    MatSlideToggleModule,
    MatFormFieldModule,
    MatOptionModule,
    MatSelectModule,
    MatInputModule,
    MatSnackBarModule,

    GioSaveBarModule,
    GioIconsModule,
    GioFormSlideToggleModule,
    GioBannerModule,
    GioFormCronModule,
    GioFormCronModule,
    GioFormHeadersModule,
    GioMonacoEditorModule,
  ],
  declarations: [ApiDynamicPropertiesComponent],
  exports: [ApiDynamicPropertiesComponent],
})
export class ApiDynamicPropertiesModule {}
