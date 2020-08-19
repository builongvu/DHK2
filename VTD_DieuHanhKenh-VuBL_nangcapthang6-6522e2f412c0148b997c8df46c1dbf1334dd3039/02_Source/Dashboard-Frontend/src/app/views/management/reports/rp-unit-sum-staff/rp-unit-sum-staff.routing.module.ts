import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { RpUnitSumStaffComponent } from './rp-unit-sum-staff.component';

const routes: Routes = [
  {
    path: '',
    component: RpUnitSumStaffComponent,
    data: {
      title: 'Báo cáo chi tiết'
    },
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class RpUnitSumStaffRoutingModule { }
