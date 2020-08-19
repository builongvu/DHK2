import {GroupVttChannelModule} from './views/management/group-vtt-channel/group-vtt-channel.module';
import {ObjectRoleConfigModule} from './views/management/object-role-config/object-role-config.module';
import {MngTargetModule} from './views/management/mng-target/mng-target.module';
import {BrowserModule} from '@angular/platform-browser';
import {CUSTOM_ELEMENTS_SCHEMA, NgModule, NO_ERRORS_SCHEMA} from '@angular/core';
import {LocationStrategy, HashLocationStrategy, CommonModule} from '@angular/common';
import {PerfectScrollbarModule} from 'ngx-perfect-scrollbar';
import {PerfectScrollbarConfigInterface} from 'ngx-perfect-scrollbar';
import {FormsModule} from '@angular/forms';
import {HttpClientModule, HTTP_INTERCEPTORS, HttpClient} from '@angular/common/http';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {ToastrModule} from 'ngx-toastr';

const DEFAULT_PERFECT_SCROLLBAR_CONFIG: PerfectScrollbarConfigInterface = {
  suppressScrollX: true
};

import {AppComponent} from './app.component';
// Import multi select option
// Import containers
import {DefaultLayoutComponent} from './containers';

import {P404Component} from './views/error/404.component';
import {P500Component} from './views/error/500.component';
import {LoginComponent} from './views/login/login.component';
import {RegisterComponent} from './views/register/register.component';
import {AuthGuard} from './guards/auth.guard';
import {OauthService} from './services/oauth.service';
import {UserService} from './services/user.service';
import {ReactiveFormsModule} from '@angular/forms';

export function createTranslateLoader(http: HttpClient) {
  return new TranslateHttpLoader(http, './assets/i18n/', '.json');
}

const APP_CONTAINERS = [
  DefaultLayoutComponent,
];

import {
  AppAsideModule,
  AppBreadcrumbModule,
  AppHeaderModule,
  AppFooterModule,
  AppSidebarModule,
} from '@coreui/angular';

// Import routing module
import {AppRoutingModule} from './app.routing';

// Import 3rd party components
import {BsDropdownModule} from 'ngx-bootstrap/dropdown';
import {TabsModule} from 'ngx-bootstrap/tabs';
import {TokenInterceptor} from './interceptor/token.interceptor';
import {ApiResponseInterceptor} from './interceptor/apiresponse.interceptor';
import {I18n} from './views/management/vtt-target/i18n';
import {DefaultTreeviewEventParser, TreeviewEventParser, TreeviewI18n, TreeviewI18nDefault, TreeviewModule} from 'ngx-treeview';
import {DisabledOnSelectorDirective} from './config-treeview/disabled-on-selector.directive';
import {VttTargetModule} from './views/management/vtt-target/vtt-target.module';
import {
    MAT_DATE_LOCALE,
    MatDatepickerModule, MatIconModule,
    MatInputModule, MatListModule,
    MatProgressBarModule, MatProgressSpinnerModule,
    MatSidenavModule, MatTabsModule,
    MatToolbarModule, MatTreeModule
} from '@angular/material';
import {TranslateHttpLoader} from '@ngx-translate/http-loader';
import {TranslateLoader, TranslateModule} from '@ngx-translate/core';
import {LanguageSelectorComponent} from './i18n/language-selector';
import {ConfigTreeviewModule} from './config-treeview/config-treeview.module';
import {DataLayoutService} from './services/management/data-layout.service';
import {DashboardService} from './services/management/dashboard.service';
import {DashboardComponent} from './views/dashboard/dashboard.component';
import {GroupDashboardService} from './services/management/group-dashboard.service';
import {TargetGroupModule} from './views/management/vtt-target-group-management/target-group.module';
import {WarningConfigModule} from './views/management/warning-config/warning-config.module';
import {ObjectConfigModule} from './views/management/objectconfig/objectconfig.module';
import {DetailsReportModule} from './views/management/details-report/details-report.module';
import {HttpModule} from '@angular/http';
import {GroupVdsChannelComponent} from './views/management/group-vds-channel/group-vds-channel.component';
import {StaffVttComponent} from './views/management/staff-vtt/staff-vtt.component';
import {DeclareVdsComponent} from './views/management/staff-vds/declare-vds/declare-vds.component';
import {StaffVdsComponent} from './views/management/staff-vds/staff-vds.component';
import {DashboardModule} from './views/dashboard/dashboard.module';
import {TooltipModule} from 'ngx-bootstrap';
import {NgxPaginationModule} from 'ngx-pagination';
import {StaffVdsListComponent} from './views/management/staff-vds/staff-vds-list/staff-vds-list.component';
import {StaffVdsCreateComponent} from './views/management/staff-vds/staff-vds-list/staff-vds-create/staff-vds-create.component';
import {ConfigTreeComponent} from './views/management/staff-vds/config-tree-select/config-tree-select.component';
import {TreeVDSService} from './services/management/tree-VDS.service';
import {UpdateDeclareVdsComponent} from './views/management/staff-vds/declare-vds/update-declare-vds/update-declare-vds.component';
import {AddDeclareVdsComponent} from './views/management/staff-vds/declare-vds/add-declare-vds/add-declare-vds.component';
import {ListGroupVttChannelComponent} from './views/management/group-vtt-channel/list-group-vtt-channel/list-group-vtt-channel.component';
import {StaffVdsEditComponent} from './views/management/staff-vds/staff-vds-list/staff-vds-edit/staff-vds-edit.component';
import {ChartsModule} from 'ng2-charts';
import {ShopUnitModule} from './views/management/shop-unit/shop-unit.module';
import {ConfigDashboardComponent} from './views/management/config-dashboard/config-dashboard.component';
import {ConfigSingleCardComponent} from './views/management/config-dashboard/config-single-card/config-single-card.component';
import {ConfigGroupCardComponent} from './views/management/config-dashboard/config-group-card/config-group-card.component';
import {CreateSingleCardComponent} from './views/management/config-dashboard/config-single-card/create-single-card/create-single-card.component';
import {UpdateSingleCardComponent} from './views/management/config-dashboard/config-single-card/update-single-card/update-single-card.component';
import {ConfigSingleChartComponent} from './views/management/config-dashboard/config-single-chart/config-single-chart.component';
import {SingleChartAddComponent} from './views/management/config-dashboard/config-single-chart/single-chart-add/single-chart-add.component';
import {CreateGroupCardComponent} from './views/management/config-dashboard/config-group-card/create-group-card/create-group-card.component';
import {UpdateGroupCardComponent} from './views/management/config-dashboard/config-group-card/update-group-card/update-group-card.component';
import {SingleChartUpdateComponent} from './views/management/config-dashboard/config-single-chart/single-chart-update/single-chart-update.component';
// import {WidgetComponent} from './views/widget';

// @ts-ignore
@NgModule({
    imports: [
        BrowserModule,
        AppRoutingModule,
        AppAsideModule,
        AppBreadcrumbModule.forRoot(),
        AppFooterModule,
        AppHeaderModule,
        AppSidebarModule,
        HttpModule,
        PerfectScrollbarModule,
        BsDropdownModule.forRoot(),
        TabsModule.forRoot(),
        ChartsModule,
        FormsModule,
        HttpClientModule,
        CommonModule,
        BrowserAnimationsModule,
        ToastrModule.forRoot(),
        ReactiveFormsModule,
        TranslateModule.forRoot({
            loader: {
                provide: TranslateLoader,
                useFactory: (createTranslateLoader),
                deps: [HttpClient]
            },
            isolate: true
        }),
        VttTargetModule,
        TargetGroupModule,
        TreeviewModule,
        MatDatepickerModule,
        MatInputModule,
        ConfigTreeviewModule,
        MatProgressBarModule,
        MngTargetModule,
        WarningConfigModule,
        ObjectConfigModule,
        MatToolbarModule,
        MatSidenavModule,
        MatTreeModule,
        MatListModule,
        MatIconModule,
        DetailsReportModule,
        MngTargetModule,
        ObjectRoleConfigModule,
        DashboardModule,
        MatTabsModule,
        TooltipModule,
        NgxPaginationModule,
        GroupVttChannelModule,
        ShopUnitModule,
        MatProgressSpinnerModule
    ],
  declarations: [
    AppComponent,
    APP_CONTAINERS,
    P404Component,
    P500Component,
    LoginComponent,
    RegisterComponent,
    LanguageSelectorComponent,
    StaffVttComponent,
    DeclareVdsComponent,
    StaffVdsComponent,
    StaffVdsListComponent,
    StaffVdsCreateComponent,
    StaffVdsListComponent,
    ConfigTreeComponent,
    StaffVdsEditComponent,
    UpdateDeclareVdsComponent,
    AddDeclareVdsComponent,
    ConfigDashboardComponent,
    ConfigSingleCardComponent,
    ConfigSingleChartComponent,
    ConfigGroupCardComponent,
    CreateSingleCardComponent,
    UpdateSingleCardComponent,
    SingleChartAddComponent,
    UpdateSingleCardComponent,
    CreateGroupCardComponent,
    UpdateGroupCardComponent,
    SingleChartUpdateComponent
  ],
  providers: [
    {provide: TreeviewI18n, useClass: TreeviewI18nDefault},
    {provide: TreeviewEventParser, useClass: DefaultTreeviewEventParser}, {
      provide: LocationStrategy,
      useClass: HashLocationStrategy
    },
    {provide: MAT_DATE_LOCALE, useValue: 'vi-VI'},
    AuthGuard,
    OauthService,
    UserService,
    {provide: HTTP_INTERCEPTORS, useClass: TokenInterceptor, multi: true},
    {provide: HTTP_INTERCEPTORS, useClass: ApiResponseInterceptor, multi: true},
    I18n,
    DataLayoutService,
    DashboardService,
    DashboardComponent,
    GroupDashboardService,
    TreeVDSService
  ],
  bootstrap: [AppComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA],
  entryComponents: [StaffVdsCreateComponent, UpdateDeclareVdsComponent, AddDeclareVdsComponent, StaffVdsEditComponent, CreateSingleCardComponent, UpdateSingleCardComponent, CreateGroupCardComponent, UpdateGroupCardComponent
    , SingleChartAddComponent, SingleChartUpdateComponent]

})
export class AppModule {
}
