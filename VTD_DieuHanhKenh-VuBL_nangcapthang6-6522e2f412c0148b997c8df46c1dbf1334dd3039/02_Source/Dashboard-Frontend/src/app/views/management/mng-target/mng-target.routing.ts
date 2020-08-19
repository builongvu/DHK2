import { NgModule, ModuleWithProviders } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { MngTargetComponent } from './list-target/mng-target.component';


const routes: Routes = [
  {
    path: '',
    component: MngTargetComponent,
    data: {
      title: 'Quản lý chỉ tiêu'
    },
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class MngTargetRoutingModule { }
