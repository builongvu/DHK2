import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { DetailsReportComponent } from './details-report.component';

const routes: Routes = [
  {
    path: '',
    component: DetailsReportComponent,
    data: {
      title: 'Báo cáo chi tiết'
    },
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class DetailsReportRoutingModule { }
