import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {DisabledOnSelectorDirective} from './disabled-on-selector.directive';

@NgModule({
  declarations: [
    DisabledOnSelectorDirective
  ],
  imports: [
    CommonModule,
  ],
  exports: [
    DisabledOnSelectorDirective
  ]
})
export class ConfigTreeviewModule { }
