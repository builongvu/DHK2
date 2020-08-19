import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { ObjectconfigComponent } from './objectconfig.component';

const routes: Routes = [
  {
    path: '',
    component: ObjectconfigComponent,
    data: {
      title: 'Cấu hình chức năng'
    },
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ObjectConfigRoutingModule { }
