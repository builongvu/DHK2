import { NgModule, ModuleWithProviders } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { VttTargetComponent } from './vtt-target.component';


const routes: Routes = [
  {
    path: '',
    component: VttTargetComponent,
    data: {
      title: 'Giao Chỉ tiêu VDS'
    },
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class VttTargetRoutingModule { }
