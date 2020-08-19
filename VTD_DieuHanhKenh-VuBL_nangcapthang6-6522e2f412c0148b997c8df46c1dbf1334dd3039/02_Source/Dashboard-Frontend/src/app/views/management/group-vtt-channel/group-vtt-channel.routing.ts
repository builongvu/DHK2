import { GroupVdsChannelComponent } from './../group-vds-channel/group-vds-channel.component';
import { NgModule, ModuleWithProviders } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';


const routes: Routes = [
  {
    path: '',
    component: GroupVdsChannelComponent,
    data: {
      title: 'Nhóm kênh vtt'
    },
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class GroupVttRoutingConfig { }
