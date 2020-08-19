import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { WarningConfigComponent } from './warning-config.component';

const routes: Routes = [
  {
    path: '',
    component: WarningConfigComponent,
    data: {
      title: 'Nhận cảnh báo'
    },
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class WarningConfigRoutingModule { }
