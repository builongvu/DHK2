import { VttTargetGroupManagementComponent } from './views/management/vtt-target-group-management/vtt-target-group-management.component';
import { ListGroupVttChannelComponent } from './views/management/group-vtt-channel/list-group-vtt-channel/list-group-vtt-channel.component';
import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

// Import Containers
import { DefaultLayoutComponent } from './containers';

import { P404Component } from './views/error/404.component';
import { P500Component } from './views/error/500.component';
import { LoginComponent } from './views/login/login.component';
import { RegisterComponent } from './views/register/register.component';
import { AuthGuard } from './guards/auth.guard';
import {GroupVdsChannelComponent} from './views/management/group-vds-channel/group-vds-channel.component';
import {StaffVttComponent} from './views/management/staff-vtt/staff-vtt.component';
import {DeclareVdsComponent} from './views/management/staff-vds/declare-vds/declare-vds.component';
import {StaffVdsComponent} from './views/management/staff-vds/staff-vds.component';
import {ConfigDashboardComponent} from './views/management/config-dashboard/config-dashboard.component';
import {RpUnitSumStaffComponent} from './views/management/reports/rp-unit-sum-staff/rp-unit-sum-staff.component';
import {RpUnitSumProvinceComponent} from './views/management/reports/rp-unit-sum-province/rp-unit-sum-province.component';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'dashboard',
    pathMatch: 'full',
    canActivate: [AuthGuard],
  },
  {
    path: '404',
    component: P404Component,
    data: {
      title: 'Page 404'
    }
  },
  {
    path: '500',
    component: P500Component,
    data: {
      title: 'Page 500'
    }
  },
  {
    path: 'login',
    component: LoginComponent,
    data: {
      title: 'Login Page'
    }
  },
  {
    path: 'register',
    component: RegisterComponent,
    data: {
      title: 'Register Page'
    }
  },
  {
    path: '',
    component: DefaultLayoutComponent,
    data: {
      title: 'Trang chủ'
    },

    canActivate: [AuthGuard],
    children: [
      {
        path: 'dashboard',
        loadChildren: './views/dashboard/dashboard.module#DashboardModule'
      },
      {
        path: 'charts',
        loadChildren: './views/chartjs/chartjs.module#ChartJSModule'
      },
      {
        path: 'channel-management',
        loadChildren: './views/management/channel/channel.module#ChannelModule'
      },
      {
        path: 'vtt-target',
        loadChildren: './views/management/vtt-target/vtt-target.module#VttTargetModule'
      },
      {
        path: 'vtt-target-level',
        loadChildren: './views/management/vtt-target-level/vtt-target-level.module#VttTargetLevelModule'
      },
      {
        path: 'target-management',
        loadChildren: './views/management/mng-target/mng-target.module#MngTargetModule'
      }, {
        path: 'warning-config',
        loadChildren: './views/management/warning-config/warning-config.module#WarningConfigModule'
      },
      {
        path: 'assign-ontime',
        loadChildren: './views/management/assign-ontime/assign-ontime.module#AssignOntimeModule'
      },
      {
        path: 'object-config',
        loadChildren: './views/management/objectconfig/objectconfig.module#ObjectConfigModule'
      },
      {
        path: 'theme',
        loadChildren: './views/theme/theme.module#ThemeModule'
      },
      {
        path: 'vtt-target-group-management',
        component: VttTargetGroupManagementComponent
      },
      {
        path: 'details-report',
        loadChildren: './views/management/details-report/details-report.module#DetailsReportModule'
      },
      {
        path: 'object-role-config',
        loadChildren: './views/management/object-role-config/object-role-config.module#ObjectRoleConfigModule'
      },
      {
        path: 'config-role-staff',
        loadChildren: './views/management/role-staff/role-staff.module#RoleStaffModule'
      },
      {
        path: 'group-vtt-channel',
        component: ListGroupVttChannelComponent
      },
      {
        path: 'group-vds-channel',
        loadChildren: './views/management/group-vds-channel/group-vds-channel.module#GroupVDSChannelModule'
      },
      {
        path: 'shop-unit',
        loadChildren: './views/management/shop-unit/shop-unit.module#ShopUnitModule'
      },
      {
        path: 'staff-vtt',
        component: StaffVttComponent
      },
      {
        path: 'staff-vds',
        component: StaffVdsComponent
      },
      {
        path: 'config-dashboard',
        component: ConfigDashboardComponent
      },
      {
        path: 'rp-unit-sum-province',
        loadChildren: './views/management/reports/rp-unit-sum-province/rp-unit-sum-province.module#RpUnitSumProvinceModule'
      },
      {
        path: 'rp-unit-sum-staff',
        loadChildren: './views/management/reports/rp-unit-sum-staff/rp-unit-sum-staff.module#RpUnitSumStaffModule'
      }

    ]
  },
  { path: '**', component: P404Component },


];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
