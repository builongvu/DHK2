import { ListRoleComponent } from './list-role/list-role.component';
import { NgModule, ModuleWithProviders } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';


const routes: Routes = [
  {
    path: '',
    component: ListRoleComponent,
    data: {
      title: 'Quản lý phân quền'
    },
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ObjectRoleRoutingConfig { }
