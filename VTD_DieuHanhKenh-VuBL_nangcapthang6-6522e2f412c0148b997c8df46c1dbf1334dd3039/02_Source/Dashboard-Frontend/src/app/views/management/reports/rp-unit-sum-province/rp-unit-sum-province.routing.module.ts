import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { RpUnitSumProvinceComponent } from './rp-unit-sum-province.component';

const routes: Routes = [
  {
    path: '',
    component: RpUnitSumProvinceComponent,
    data: {
      title: 'Báo cáo chi tiết'
    },
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class RpUnitSumProvinceRoutingModule { }
