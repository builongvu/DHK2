import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AssignOntimeComponent } from './assign-ontime.component';

const routes: Routes = [
  {
    path: '',
    component: AssignOntimeComponent,
    data: {
      title: 'Thời hạn giao chỉ tiêu'
    },
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AssignOntimeRoutingModule { }
