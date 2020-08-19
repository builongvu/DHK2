import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { ShopUnitComponent } from './shop-unit.component';

const routes: Routes = [
  {
    path: '',
    component: ShopUnitComponent,
    data: {
      title: ''
    },
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ShopUnitRoutingModule { }
