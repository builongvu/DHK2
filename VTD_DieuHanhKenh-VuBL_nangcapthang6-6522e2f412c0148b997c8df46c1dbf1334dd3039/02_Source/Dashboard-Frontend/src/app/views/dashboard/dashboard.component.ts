/* tslint:disable:triple-equals */
import {AfterViewChecked, AfterViewInit, ChangeDetectorRef, Component, HostListener, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {UserService} from '../../services/user.service';
import {HttpClient} from '@angular/common/http';
import {DataLayoutService} from '../../services/management/data-layout.service';
import {DashboardService} from '../../services/management/dashboard.service';
import {DashboardModel} from '../../models/dashboard.model';
import {ActivatedRoute} from '@angular/router';
import {ToggerComponent} from './togger/togger.component';
import {DrilldownModel} from '../../models/Drilldown.model';
import {config} from '../../config/application.config';
import {TranslateService} from '@ngx-translate/core';
import * as Highcharts from 'highcharts';

export class ChartDetail {
  groupId: number;
  chartId: number;
  isZoom: boolean;

  constructor(groupId: number, chartId: number, isZoom: boolean) {
    this.groupId = groupId;
    this.chartId = chartId;
    this.isZoom = isZoom;
  }
}

@Component({
  templateUrl: 'dashboard.component.html',
  styleUrls: ['dashboard.component.scss']
})

export class DashboardComponent implements OnInit, AfterViewInit, AfterViewChecked, OnDestroy {
  @ViewChild(ToggerComponent) toggle: ToggerComponent;
  isToggle: boolean;
  isExcel = false;
  userStatus = '';

  constructor(private userService: UserService,
              private httpService: HttpClient,
              private cdr: ChangeDetectorRef,
              private translate: TranslateService,
              private dataLayoutService: DataLayoutService,
              private dashboardService: DashboardService,
              private route: ActivatedRoute) {
  }

  parentMessage = true;
  mobjDataDashboard;
  marrCards: any[] = [];
  groupId = Number(this.route.snapshot.params['groupId']);
  dashModel: DashboardModel;
  shopCode: string;
  defaultCycle;
  isShow = false;
  month;
  groupName;
  subGroupName;
  dashboardModels: DashboardModel[] = [];
  isShowBack = false;
  chartDetail: ChartDetail[] = [];
  topPosToStartShowing = 100;
  codeWarning;
  dataSekectSpark2;
  btnVisibility: boolean;
  isRadioBtn: any;
  unitValue = '0';
  sttViewValue = '0';
  isLvThree: boolean;
  isNational = false;
  textBtn = '';
  isBtnView = true;
  monthSpark3 = undefined;
  isLv3Table2: boolean;
  isLv2: boolean;
  isClickTable: boolean;

  ngOnInit(): void {
    // this.dashModel.codeWarning = '3';
    this.textBtn = this.translate.instant('management.group.table.validate.nationalStaffView');
    this.groupId = Number(this.route.snapshot.params['groupId']);
    this.route.params.subscribe(params => {
      this.shopCode = null;
      this.isShow = false;
      this.groupId = Number(params['groupId']);
      this.testFunction();
      this.initParam();
    });
  }

  testFunction() {
    this.httpService.get(config.apparam_getbytype_API + '/CONSECUTIVE_WARNING').subscribe(
      data => {
        this.dataSekectSpark2 = data['data'][0]['code'];
      }
    );
  }

  ngAfterViewInit(): void {
  }

  ngAfterViewChecked() {
    // your code to update the model
    this.cdr.detectChanges();
  }

  initParam() {
    this.mobjDataDashboard = null;
    this.shopCode = null;
    if (this.groupId == 1 || this.groupId == 2 || this.groupId == 3 || this.groupId == 4) {
      // this.dashModel.prdId = new Date().getTime();
      this.dashModel = null;
    }
    // try {
    // if (!this.toggle) {
    //   this.toggle.ngAfterViewInit();
    // }
    this.toggle.createTreeView();
    // } catch (e) {
    // }
  }

  getDataByToggle(event) {
    this.dashModel = event;
    // this.dashboardModels.push(this.dashModel);
    this.ckeckOnClickBack(this.dashModel);
    this.setViewStatusInitial();
    this.dashModel.month = this.monthSpark3;
    this.getDataDashboard();
    this.isToggle = true;

  }

  getDataDashboard() {
    this.resetStatus();
    this.chartDetail = [];
    setTimeout(() => {
      if (this.dashboardModels.length === 1) {
        this.isShowBack = false;
      } else if (this.dashboardModels.length > 1) {
        this.isShowBack = true;
      }
    }, 100);
    this.isShow = false;
    this.mobjDataDashboard = null;
    this.dashModel.expand = 0;
    if (this.isRadioBtn && (this.dashModel.serviceId !== null)) {
      this.dashModel.nationalStaff = this.unitValue;
    } else {
      this.dashModel.nationalStaff = this.sttViewValue;
    }
    // this.dashModel.groupId = this.groupId;
    if (this.groupId == 1 && this.dashboardModels.length === 1) {
      // tslint:disable-next-line:radix
      this.dashModel.codeWarning = this.codeWarning ? parseInt(this.codeWarning) : parseInt(this.dataSekectSpark2);
    }
    this.dashboardService.getDashboard(this.dashModel).subscribe(
      data => {
        this.checkLv();
        this.btnVisibility = (this.groupId === 1) ? true : false;
        if (!data['data']) {
          this.mobjDataDashboard = null;
          this.isShow = false;
          this.groupName = '';
          this.subGroupName = '';
        } else {
          this.isLvThree = data['data']['lvThree'];
          this.mobjDataDashboard = data['data'];
          this.marrCards = this.mobjDataDashboard['cards'];
          this.marrCards.forEach(
            card => {
              card['contents'].forEach(
                content => {
                  if (content['chartId'] == null) {
                    return;
                  }
                  const chartdetail: ChartDetail = new ChartDetail(Number(this.dashModel.groupId), content['chartId'], false);
                  this.chartDetail.push(chartdetail);
                }
              );
            }
          );
          this.defaultCycle = this.mobjDataDashboard['defaultCycle'];
          this.groupName = this.mobjDataDashboard['groupName'];
          this.subGroupName = this.mobjDataDashboard['subGroupName'];
          this.userStatus = this.mobjDataDashboard['shopOfStaff'];
          this.isShow = true;
        }
      },
      error => {
      },
      () => {
        this.toggle.reLoadToggle();
        // setTimeout(() => {
        //   this.cardComponent.reloadSelect();
        // }, 500);
        this.checkExcelShow();
        this.isShow = true;
        setTimeout(() => {
          window.dispatchEvent(new Event('resize'));
        }, 150);
      }
    );
  }

  checkLv() {
    console.log(this.dashModel.serviceId);
    if (!this.dashModel.parentShopCode
      && this.isClickTable && this.isLv2 && !this.isToggle) {
      this.isLv3Table2 = true;
    } else {
      this.isLv3Table2 = false;
    }
    if (!this.dashModel.serviceId && this.dashModel.serviceId !== 0) {
      this.isLv2 = false;
    } else {
      this.isLv2 = true;
    }
  }

  onResized() {
    setTimeout(() => {
      window.dispatchEvent(new Event('resize'));
    }, 400);
  }

  // click top-left
  clickTopLeft(value: string) {
    this.dashModel = new DashboardModel(5, this.dashModel.prdId,
      this.dashModel.cycleId, this.dashModel.objectCode, this.dashModel.vdsChannelCode);
    this.dashModel.nationalStaff = this.sttViewValue;
    this.isBtnView = false;
    this.isRadioBtn = false;
    this.ckeckOnClickBack(this.dashModel);
    this.getDataDashboardSttView();
  }

  onslickBack() {
    // if (this.dashboardModels) {
    //   this.dashboardModels.splice(this.dashboardModels.length - 1, 1);
    //   this.dashModel = this.dashboardModels[this.dashboardModels.length - 1];
    //   this.getDataDashboard();
    if (this.dashboardModels) {
      this.dashboardModels.splice(this.dashboardModels.length - 1, 1);
      this.dashModel = this.dashboardModels[this.dashboardModels.length - 1];
      this.toggle.reloadDate(this.dashModel.prdId);
      // if (!this.isLvThree) {
      //   this.unitValue = this.sttViewValue;
      // }
      if (this.isLvThree) {
        this.getDataDashboard();
      } else {
        this.getDataDashboardSttView();
      }
    }
    this.isBtnView = true;
    this.btnVisibility = true;
    this.isClickTable = false;
  }

  ckeckOnClickBack(model: DashboardModel) {
    if (model.groupId == 1 || model.groupId == 2 || model.groupId == 3 || model.groupId == 4) {
      this.dashboardModels = [];
      this.dashboardModels.push(model);
    } else if (model.groupId != 1 && model.groupId != 2 && model.groupId != 3 && model.groupId != 4) {
      // this.dashboardModels.push(model);
      if (this.dashboardModels.length < 2) {
        this.dashboardModels.push(model);
      } else if (!this.toggle.changeToggle) {
        this.dashboardModels.push(model);
      } else {
      }
    }
  }

  clickRow(drilldownModel) {
    const drilldownObject = drilldownModel.drilldownObject;
    const serviceId = drilldownModel.serviceId;
    const shopCode = drilldownModel.shopCode;
    const allStaff = drilldownModel.allStaff;
    const prow = drilldownModel.pRow;
    // this.dashModel.groupId = 7;
    // this.dashModel.serviceId = 1;
    // if ()
    this.dashModel = new DashboardModel(
      drilldownObject, this.dashModel.prdId, this.dashModel.cycleId,
      this.dashModel.objectCode, this.dashModel.vdsChannelCode, serviceId, shopCode
    );
    this.dashModel.pRow = drilldownModel.pRow;
    this.ckeckOnClickBack(this.dashModel);
    // this.dashboardModels.push(this.dashModel);
    this.isRadioBtn = drilldownModel;
    this.isBtnView = false;
    this.getDataDashboard();
    // if (this.isLvThree) {
    //     //   this.isRadioBtn = undefined;
    //     // }
    this.isClickTable = true;
    if (this.isLv2) {
      this.isToggle = false;
    }
  }

  clickChartLv2(drilldownModel: DrilldownModel) {
    this.dashModel = new DashboardModel(drilldownModel.drilldownObject, this.dashModel.prdId, this.dashModel.cycleId,
      this.dashModel.objectCode, this.dashModel.vdsChannelCode, drilldownModel.serviceId);
    this.ckeckOnClickBack(this.dashModel);
    this.isRadioBtn = false;
    this.getDataDashboard();
    const main = document.getElementById('main');
    main.scroll(0, 0);
  }

  onClickTopRight(value: DrilldownModel) {
    this.dashModel = new DashboardModel(value.drilldownObject, this.dashModel.prdId, this.dashModel.cycleId,
      this.dashModel.objectCode, this.dashModel.vdsChannelCode, null, null, null, value.isTarget);
    this.ckeckOnClickBack(this.dashModel);
    this.isRadioBtn = false;
    this.getDataDashboard();
  }

  clickShowDetail() {
    this.dashModel.expand = 1;
    this.ckeckOnClickBack(this.dashModel);
    this.getDataDashboard();
    this.isRadioBtn = false;
    // this.dashModel = new DashboardModel(this.dashModel.prdId, this.dashModel.prdId, this.dashModel.cycleId,
    //   this.dashModel.objectCode, this.dashModel.vdsChannelCode, null, null, null, this.dashModel.isTarget);
  }

  g() {
    this.parentMessage = false;
  }

  displayCounter($event: any) {
    if ($event) {
      this.parentMessage = false;
    } else {
      this.parentMessage = true;
    }
  }

  showDetailSpark2(drilldownModel: DrilldownModel) {
    // this.codeWarning = drilldownModel.codeWarning;
    // this.dashModel.groupId = drilldownModel.drilldownObject;
    // this.dashModel.serviceId = 9;
    this.dashModel = new DashboardModel(drilldownModel.drilldownObject, this.dashModel.prdId, this.dashModel.cycleId,
      this.dashModel.objectCode, this.dashModel.vdsChannelCode, 9);
    this.dashModel.codeWarning = drilldownModel.codeWarning;
    this.ckeckOnClickBack(this.dashModel);
    this.getDataDashboard();
    this.isRadioBtn = false;
  }

  detailSpark3(drilldownModel: DrilldownModel) {
    this.dashModel = new DashboardModel(
      drilldownModel.drilldownObject,
      this.dashModel.prdId,
      this.dashModel.cycleId,
      this.dashModel.objectCode,
      this.dashModel.vdsChannelCode
    );
    this.dashModel.pRow = drilldownModel.index;
    this.dashModel.month = drilldownModel.month;
    this.monthSpark3 = drilldownModel.month;
    this.isRadioBtn = false;
    this.ckeckOnClickBack(this.dashModel);
    this.getDataDashboard();
    this.isBtnView = false;
  }

  changeCodeSpark2(code: any) {
    this.codeWarning = code;
    this.dashModel = new DashboardModel(1, this.dashModel.prdId, this.dashModel.cycleId,
      this.dashModel.objectCode, this.dashModel.vdsChannelCode);
    // this.dashModel.codeWarning = code;
    this.dashModel.codeWarning = code;
    this.getDataDashboard();
  }

  unitChange(event: any) {
    const value = event.target.value;
    this.unitValue = value;
    this.getDataDashboard();
  }

  changeView() {
    this.isNational = !this.isNational;
    this.sttViewValue = this.isNational ? '1' : '0';
    this.textBtn = this.isNational ? this.translate.instant('management.group.table.validate.unitView') : this.translate.instant('management.group.table.validate.nationalStaffView');
    this.getDataDashboardSttView();
  }

  setViewStatusInitial() {
    this.sttViewValue = '0';
    this.isNational = false;
    this.textBtn = this.translate.instant('management.group.table.validate.nationalStaffView');
  }

  getDataDashboardSttView() {
    this.resetStatus();
    this.chartDetail = [];
    setTimeout(() => {
      if (this.dashboardModels.length === 1) {
        this.isShowBack = false;
      } else if (this.dashboardModels.length > 1) {
        this.isShowBack = true;
      }
    }, 100);
    this.isShow = false;
    this.mobjDataDashboard = null;
    this.dashModel.expand = 0;
    this.dashModel.nationalStaff = this.sttViewValue;
    // this.dashModel.groupId = this.groupId;
    if (this.groupId == 1 && this.dashboardModels.length === 1) {
      // tslint:disable-next-line:radix
      this.dashModel.codeWarning = this.codeWarning ? parseInt(this.codeWarning) : parseInt(this.dataSekectSpark2);
    }
    this.dashboardService.getDashboard(this.dashModel).subscribe(
      data => {
        this.checkLv();
        if (!data['data']) {
          this.mobjDataDashboard = null;
          this.isShow = false;
          this.groupName = '';
          this.subGroupName = '';
        } else {
          this.isLvThree = data['data']['lvThree'];
          this.mobjDataDashboard = data['data'];
          this.marrCards = this.mobjDataDashboard['cards'];
          this.marrCards.forEach(
            card => {
              card['contents'].forEach(
                content => {
                  if (content['chartId'] == null) {
                    return;
                  }
                  const chartdetail: ChartDetail = new ChartDetail(Number(this.dashModel.groupId), content['chartId'], false);
                  this.chartDetail.push(chartdetail);
                }
              );
            }
          );
          this.defaultCycle = this.mobjDataDashboard['defaultCycle'];
          this.groupName = this.mobjDataDashboard['groupName'];
          this.subGroupName = this.mobjDataDashboard['subGroupName'];
          this.isShow = true;
        }
      },
      error => {
      },
      () => {
        this.toggle.reLoadToggle();
        // setTimeout(() => {
        //   this.cardComponent.reloadSelect();
        // }, 500);
        this.checkExcelShow();

        this.isShow = true;
        setTimeout(() => {
          window.dispatchEvent(new Event('resize'));
        }, 150);
      }
    );
  }

  ngOnDestroy(): void {
  }

  // khi chuyển kênh các trạng thái được reset
  resetStatus() {
    if (this.groupId !== 1) {
      this.btnVisibility = false;
      this.isBtnView = true;
      this.isLv2 = false;
      this.isLv3Table2 = false;
      this.isLvThree = false;
      this.isClickTable = false;
    }
  }

  checkExcelShow() {
    if (this.isLv2 || this.isLvThree || this.isLv3Table2) {
      this.isExcel = true;
    } else {
      this.isExcel = false;
    }
  }
}
