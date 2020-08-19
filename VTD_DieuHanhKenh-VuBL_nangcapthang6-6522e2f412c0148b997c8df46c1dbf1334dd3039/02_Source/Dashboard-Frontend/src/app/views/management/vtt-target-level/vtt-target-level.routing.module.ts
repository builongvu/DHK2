import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {VttTargetLevelComponent} from './vtt-target-level.component';

const routes: Routes = [
  {
    path: '',
    component: VttTargetLevelComponent,
    data: {
      title: 'Giao Chỉ Tiêu Các Cấp'
    },
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class VttTargetLevelRoutingModule { }
