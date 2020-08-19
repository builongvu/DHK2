import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { GroupVdsChannelComponent } from './group-vds-channel.component';

const routes: Routes = [
  {
    path: '',
    component: GroupVdsChannelComponent,
    data: {
      title: 'Quản lý nhóm kênh điều hành VDS'
    },
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class GroupVDSChannelRoutingModule { }
