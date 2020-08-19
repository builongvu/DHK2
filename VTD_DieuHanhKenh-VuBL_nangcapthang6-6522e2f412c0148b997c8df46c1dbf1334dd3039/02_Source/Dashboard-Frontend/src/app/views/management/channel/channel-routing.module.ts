import { NgModule, ModuleWithProviders } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { ChannelComponent } from './channel.component';
import { CEChannelComponent } from './create.edit.channel.component';

const routes: Routes = [
  {
    path: '',
    // component: ChannelComponent,
    data: {
      title: 'Quản lý kênh'
    },
    children: [
      {
        path: '',
        redirectTo: 'channel-management'
      },
      {
        path: 'channel-management',
        component: ChannelComponent,
        data: {
          title: ''
        }
      },
      {
        path: 'ce-channel-management/:id',
        component: CEChannelComponent,
        data: {
          title: '',
          myId: 'cechannel'
        }
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ChannelRoutingModule {}
