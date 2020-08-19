import { NgModule } from '@angular/core';
import {Routes, RouterModule, ActivatedRoute, Router, ActivationStart} from '@angular/router';

import { DashboardComponent } from './dashboard.component';

const routes: Routes = [
  {
    path: ':groupId',
    component: DashboardComponent,
    data: {
      title: 'Dashboard'
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class DashboardRoutingModule {
  ddd;
  // constructor(private router: Router) {
    // this.router.events.subscribe((event) => {
    //   if (event instanceof ActivationStart) {
    //     this.ddd = event.snapshot.params.groupId;
    //     console.log(event.snapshot.params.groupId);
    //
    //   }
    // });
  // }
}
