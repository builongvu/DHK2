import { NgModule, ModuleWithProviders } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { VttTargetGroupManagementComponent } from './vtt-target-group-management.component';


const routes: Routes = [
  {
    path: '',
    component: VttTargetGroupManagementComponent,
    data: {
      title: 'Nhóm chỉ tiêu'
    },

  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class VttTargetManagementRouting { }
