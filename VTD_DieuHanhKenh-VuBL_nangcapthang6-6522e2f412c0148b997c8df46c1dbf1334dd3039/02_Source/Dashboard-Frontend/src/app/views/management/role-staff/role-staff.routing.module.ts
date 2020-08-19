import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { RoleStaffComponent } from './role-staff.component';

const routes: Routes = [
  {
    path: '',
    component: RoleStaffComponent,
    data: {
      title: 'Gán vai trò người dùng'
    },
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class RoleStaffRoutingModule { }
