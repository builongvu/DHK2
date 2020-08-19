import {DataService} from './../../../services/data.service';
import {AfterViewInit, Component, EventEmitter, Input, OnDestroy, OnInit, Output, ViewChild} from '@angular/core';
import {TreeItem, TreeviewConfig, TreeviewItem} from 'ngx-treeview';
import {DashboardModel} from '../../../models/dashboard.model';
import {DateAdapter, MAT_DATE_FORMATS, MAT_DATE_LOCALE, MatDatepicker} from '@angular/material';
import {MomentDateAdapter} from '@angular/material-moment-adapter';
import {FormControl} from '@angular/forms';
import * as _moment from 'moment';
import {GroupDashboardService} from '../../../services/management/group-dashboard.service';
import {UnittreeService} from '../../../services/management/unittree.service';
import {Moment} from 'moment';
import {UnitService} from '../../../services/management/unit.service';
import {UnitModel} from '../../../models/unit.model';
import {ActivatedRoute, Router, RouterEvent} from '@angular/router';
import {Subject} from 'rxjs';
import {takeUntil} from 'rxjs/operators';


export const DATE_FORMATS = {
  parse: {
    dateInput: 'DD/MM/YYYY',
  },
  display: {
    dateInput: 'DD/MM/YYYY',
    monthYearLabel: 'DD MMM YYYY',
    dateA11yLabel: 'LL',
    monthYearA11yLabel: 'DDDD MMMM YYYY',
  },
};
export const MONTH_FORMATS = {
  parse: {
    dateInput: 'MM/YYYY',
  },
  display: {
    dateInput: 'MM/YYYY',
    monthYearLabel: 'MMM YYYY',
    dateA11yLabel: 'LL',
    monthYearA11yLabel: 'MMMM YYYY',
  },
};

export const YEAR_FORMATS = {
  parse: {
    dateInput: 'YYYY',
  },
  display: {
    dateInput: 'YYYY',
    monthYearLabel: 'YYYY',
    dateA11yLabel: 'LL',
    monthYearA11yLabel: 'YYYY',
  },
};

let choseDate;
choseDate = 'month';

function chose(cycle) {
  if (cycle == '1') {
    choseDate = 'month';
  } else if (cycle == '2') {
    choseDate = 'year';
  } else {
    choseDate = 'multi-year';
  }
}

@Component({
  // tslint:disable-next-line:component-selector
  selector: 'togger',
  templateUrl: 'togger.component.html',
  styleUrls: ['togger.component.scss'],
  providers: [
    {provide: DateAdapter, useClass: MomentDateAdapter, deps: [MAT_DATE_LOCALE]},
    // tslint:disable-next-line:max-line-length
    // {provide: MAT_DATE_FORMATS, useValue: choseDate == 'month' ? DATE_FORMATS : choseDate == 'year' ? MONTH_FORMATS : YEAR_FORMATS},
    {provide: MAT_DATE_FORMATS, useValue: DATE_FORMATS},
  ],
})

export class ToggerComponent implements OnInit, AfterViewInit, OnDestroy {
  @Output() filter = new EventEmitter<DashboardModel>();
  @Output() valueChange = new EventEmitter();
  // @ViewChild(DashboardComponent) dashboard: DashboardComponent;
  @Input() childMessage: boolean;
  @Input() defaultCycle: string;
  @Input() drilldown: DashboardModel;
  @Output() reLoad = new EventEmitter();
  b = false;
  message: boolean;

  constructor(private groupDashboardService: GroupDashboardService,
              private unitTreeService: UnittreeService,
              private unitService: UnitService,
              private route: ActivatedRoute,
              private router: Router,
              private data1: DataService) {
  }

  // forMatDate: MY

  mdtMinDate = new Date(1899, 0, 1);
  mdtMaxDate = new Date(2101, 0, 1);
  isActivated: boolean;
  date = new FormControl(_moment());
  dataTree = [];
  dataUnit = [];
  counter = this.isActivated;
  nodeTrees: UnitModel[] = [];
  nodeTreeView: TreeviewItem;
  nodeTreeViews: TreeviewItem[] = [];
  nodeItem: TreeItem;
  nodeItems: TreeItem[] = [];
  config = TreeviewConfig.create({
    hasFilter: true,
    hasCollapseExpand: false,
    maxHeight: 250,
  });
  unitIitem: UnitModel;
  marrIndexNodeService = [];
  mobjNodeItemService: TreeItem;
  cycleId;
  groupId;
  value1: number;
  shopCode;
  value;
  dashModel: DashboardModel;
  startView: 'month' | 'year' | 'multi-year';
  vdsChannelCode;
  hidden = false;
  dateTest = new Date();
  changeToggle = false;
  parentShopCode;
  unsubscribe$ = new Subject();

  ngOnInit(): void {
    this.data1.currentMessage.pipe(takeUntil(this.unsubscribe$)).subscribe(message => this.message = message);
    this.changeToggle = false;
    this.startView = 'month';
    this.cycleId = 1;
    if (this.childMessage || this.message) {
      this.isActivated = false;
    }
    this.setDefaultValues();
    // this.isActivated = false;
  }

  reLoadToggle() {
    this.changeToggle = false;
    this.cycleId = this.drilldown.cycleId;
    if (this.cycleId == '1') {
      this.startView = 'month';
      // this.hidden = false;
    } else if (this.cycleId == '2') {
      this.startView = 'year';
      // this.hidden = false;
    } else {
      this.startView = 'multi-year';
      // this.hidden = true;
    }
    // this.date = new FormControl(new Date(this.drilldown.prdId));
    this.date.value._d.setTime(this.drilldown.prdId);
    this.value = this.drilldown.objectCode;
  }

  ngAfterViewInit(): void {
    this.groupId = Number(this.route.snapshot.params['groupId']);
    this.cycleId = 1;
    // this.createTreeView();
  }

  // pick month
  chosenMonthHandler($event, pobjDatepicker: MatDatepicker<Moment>) {
    // this.isActivated = true;
    if (this.date.valid === false) {
      return;
    }
    this.changeToggle = true;
    this.date.setValue($event);
    this.groupId = Number(this.route.snapshot.params['groupId']);
    let vdsChannelCode;
    if (this.vdsChannelCode == '') {
      vdsChannelCode = null;
    } else {
      vdsChannelCode = this.vdsChannelCode;
    }
    let dashModel: DashboardModel;
    if (!this.drilldown) {
      dashModel = new DashboardModel(this.groupId, this.date.value._d.getTime(),
        this.cycleId, this.shopCode, vdsChannelCode);
    } else {
      dashModel = new DashboardModel(this.drilldown.groupId, this.date.value._d.getTime(), this.cycleId,
        this.shopCode, vdsChannelCode, this.drilldown.serviceId, this.drilldown.parentShopCode, this.drilldown.cardId,
        this.drilldown.isTarget, this.drilldown.expand);
      dashModel.codeWarning = this.drilldown.codeWarning;
      dashModel.pRow = this.drilldown.pRow;
    }
    this.filter.emit(dashModel);
    pobjDatepicker.close();
    setTimeout(() => {
      // this.isActivated = true;
    }, 50);

  }

  onClickOut() {
    // this.isActivated = true;
  }

  reloadDate(dateMili: number) {
    this.date = new FormControl(_moment());
    this.date.value._d.setTime(dateMili);
  }

  onToggle() {
    this.counter = this.isActivated;
    this.message = this.isActivated;
    this.valueChange.emit(this.counter);
    this.isActivated = !this.isActivated;
    if (this.isActivated) {
      this.data1.changeMessage(false);
    } else {
      this.data1.changeMessage(true);
    }

    // console.log('tai sao '+ this.childMessage);
    // this.childMessage = !this.childMessage;
    // console.log('tai sao '+ this.childMessage);
  }

  dateChange(event: any) {
    this.changeToggle = true;
    // this.date.setValue(event.value);
    const monthOK = this.value1 == 1 ? 1 : this.value1 == 2 ? 4 : this.value1 == 3 ? 7 : 11;

    if (!event.value) {
      const a = event.target.value.toString().split('/');
      const b = this.startView === 'month' ? a[1] + '/' + a[0] + '/' + a[2] :
        this.startView === 'year' ? a[0] + '/' + '01' + '/' + a[1] :
          monthOK + '/' + '01' + '/' + a[0];
      const c = new Date(b).getTime();
      this.date.value._d.setTime(c);
      // this.date.setValue(this.date.value);
    } else if (event.value._i) {
      const f = event.value._i.month + 1 + '/' + event.value._i.date + '/' + event.value._i.year;
      const g = new Date(f).getTime();
      this.date.value._d.setTime(g);
      // this.date.setValue(this.date.value);
    }

    if (this.date.valid === false) {
      return;
    }
    let dashModel: DashboardModel;
    let vdsChannelCode;
    if (this.vdsChannelCode == '') {
      vdsChannelCode = null;
    } else {
      vdsChannelCode = this.vdsChannelCode;
    }
    if (!event) {
      this.date = null;
    } else {
      this.groupId = Number(this.route.snapshot.params['groupId']);
      if (!this.drilldown) {
        dashModel = new DashboardModel(this.groupId, this.date.value._d.getTime(),
          this.cycleId, this.shopCode, vdsChannelCode);
        dashModel.codeWarning = this.drilldown.codeWarning;
        dashModel.pRow = this.drilldown.pRow;
      } else {
        dashModel = new DashboardModel(this.drilldown.groupId, this.date.value._d.getTime(), this.cycleId,
          this.shopCode, vdsChannelCode, this.drilldown.serviceId, this.drilldown.parentShopCode, this.drilldown.cardId,
          this.drilldown.isTarget, this.drilldown.expand);
        dashModel.codeWarning = this.drilldown.codeWarning;
        dashModel.pRow = this.drilldown.pRow;
      }
      this.filter.emit(dashModel);
    }
    // setTimeout(() => {
    //   this.isActivated = true;
    // }, 50);

    this.setDefaultValues();
  }

  setDefaultValues() {
    if (this.date.valid === false) {
      return;
    } else {
      const a = this.date.value._d.getMonth() + 1;
      if (a <= 3) {
        this.value1 = 1;
      } else if (a >= 4 && a < 7) {
        this.value1 = 2;
      } else if (a >= 7 && a < 10) {
        this.value1 = 3;
      } else {
        this.value1 = 4;
      }
    }

  }

  createTreeView() {
    try {
      this.nodeTrees = [];
      this.nodeTreeViews = [];
      this.groupId = Number(this.route.snapshot.params['groupId']);
      this.unitService.getUnitsDashboard(this.groupId).subscribe(
        next => {
          this.nodeTrees = [];
          this.nodeTreeViews = [];
          if (!next['data']) {
            this.value = null;
            this.shopCode = null;
            this.vdsChannelCode = null;
            this.dashModel = new DashboardModel(this.groupId, this.date.value._d.getTime() - 86400000,
              this.cycleId, this.shopCode, this.vdsChannelCode);
            // dashModel.codeWarning = this.drilldown.codeWarning;
            this.date = new FormControl(_moment());
            this.date.value._d.setTime(new Date().getTime() - 86400000);
            this.filter.emit(this.dashModel);
            return;
          }
          this.nodeTreeViews = [];
          this.dataUnit = next['data'];
          this.dataTree = next['data'];
          // console.log('data: ', this.dataTree);
          // this.createNode(this.nodeTrees, this.dataTree, this.unitIitem).forEach(
          //   note => {
          //     this.nodeTrees.push(note);
          //   }
          // );
          this.nodeTrees = this.createNode(this.nodeTrees, this.dataTree, this.unitIitem);
          this.dataTree = this.nodeTrees;
          // console.log(this.nodeTrees);
          this.createTree(this.nodeTrees, this.dataTree);
          this.nodeTrees.forEach(valuess => {
            this.nodeTreeView = new TreeviewItem(this.forwardData(valuess, this.nodeItem, this.nodeItems));
            this.nodeTreeViews.push(this.nodeTreeView);
          });


          for (let i = 0; i < this.nodeTreeViews.length; i++) {
            if (this.nodeTreeViews[i].children) {
              this.value = this.nodeTreeViews[i].value;
              this.dataUnit.forEach(
                tree => {
                  if (tree.shopCode === this.nodeTreeViews[i].value) {
                    this.shopCode = tree.shopCode;
                    this.vdsChannelCode = tree.vdsChannelCode;
                    let vdsChannelCode;
                    if (this.vdsChannelCode == '') {
                      vdsChannelCode = null;
                    } else {
                      vdsChannelCode = this.vdsChannelCode;
                    }
                    this.dashModel = new DashboardModel(this.groupId, this.date.value._d.getTime() - 86400000,
                      this.cycleId, this.shopCode, vdsChannelCode);
                    // dashModel.codeWarning = this.drilldown.codeWarning;
                    if (this.groupId == 1 || this.groupId == 2 || this.groupId == 3 || this.groupId == 4) {
                      // this.date = new FormControl(new Date(new Date().getTime() - 86400000));
                      this.date = new FormControl(_moment());
                      this.date.value._d.setDate(new Date().getDate() - 1);
                      this.dashModel.cycleId = 1;
                      this.cycleId = 1;
                      this.startView = 'month';
                      this.hidden = false;
                    }
                    return;
                  }
                }
              );
              break;
            } else {
              this.value = this.nodeTreeViews[0].value;
              this.dataUnit.forEach(
                tree => {
                  if (tree.shopCode === this.nodeTreeViews[0].value) {
                    this.shopCode = tree.shopCode;
                    this.vdsChannelCode = tree.vdsChannelCode;
                    let vdsChannelCode;
                    if (this.vdsChannelCode == '') {
                      vdsChannelCode = null;
                    } else {
                      vdsChannelCode = this.vdsChannelCode;
                    }
                    this.dashModel = new DashboardModel(this.groupId, this.date.value._d.getTime() - 86400000,
                      this.cycleId, this.shopCode, vdsChannelCode);
                    if (this.groupId == 1 || this.groupId == 2 || this.groupId == 3 || this.groupId == 4) {
                      // this.date = new FormControl(new Date(new Date().getTime() - 86400000));
                      this.date = new FormControl(_moment());
                      this.date.value._d.setDate(new Date().getDate() - 1);
                      this.dashModel.cycleId = 1;
                      this.cycleId = 1;
                      this.startView = 'month';
                      this.hidden = false;
                    }
                    return;
                  }
                }
              );
            }
          }
        }
      );
    } catch (e) {
      console.error(e);
    }

  }

  forwardData(pobjNodeTree: UnitModel, pobjitem: TreeItem, parrItems: TreeItem[] = []) {
    try {
      pobjitem = null;
      parrItems = [];
      // pobjNodeTree = null;
      if (pobjNodeTree.children) {
        pobjNodeTree.children.forEach(value => {
          this.mobjNodeItemService = this.forwardData(value, null, []);
          parrItems.push(this.mobjNodeItemService);
        });
      }
      if (pobjNodeTree.children) {
        pobjitem = {
          value: pobjNodeTree.shopCode,
          text: pobjNodeTree.shopName,
          children: parrItems,
          checked: false
        };
      } else {
        parrItems = [];
        pobjitem = {
          value: pobjNodeTree.shopCode,
          text: pobjNodeTree.shopName,
          children: null,
          checked: false
        };
      }
      return pobjitem;
    } catch (e) {
      console.log(e);
    }
  }

  createNode(parrItems: UnitModel[] = [], parrDataTree: UnitModel[], pobjItem: UnitModel) {
    try {
      pobjItem = null;
      parrItems = parrDataTree.map(value => {
        pobjItem = {
          id: value.id,
          shopName: value.shopName,
          parentShopCode: value.parentShopCode,
          shopCode: value.shopCode,
          vdsChannelCode: value.vdsChannelCode,
          children: [],
          groupId: value.groupId
        };
        return pobjItem;
      });
      return parrItems;
    } catch (e) {
      console.log(e);
    }
  }

  createTree(parrNodeTrees: UnitModel[], parrDataTree: UnitModel[]) {
    this.marrIndexNodeService = [];
    try {
      const len = parrNodeTrees.length;
      for (let i = 0; i < len; i++) {
        for (let j = 0; j < len; j++) {
          if (parrNodeTrees[i].shopCode === parrDataTree[j].parentShopCode) {
            parrNodeTrees[i].children.push(parrDataTree[j]);
            this.marrIndexNodeService.push(j);
          }
        }
      }
      // console.log('truoc', parrNodeTrees);
      const c = (a: number, b: number) => (a - b);
      this.marrIndexNodeService.sort(c);
      for (let i = this.marrIndexNodeService.length - 1; i >= 0; i--) {
        parrNodeTrees.splice(this.marrIndexNodeService[i], 1);
      }
      // console.log('sau', parrNodeTrees);
    } catch (e) {
      console.log(e);
    }
  }

  noMethod(value: any) {
    // this.date.setValue(value);
  }

  cycleChange() {
    this.changeToggle = true;
    let dashModel: DashboardModel;
    let vdsChannelCode;
    if (this.vdsChannelCode == '') {
      vdsChannelCode = null;
    } else {
      vdsChannelCode = this.vdsChannelCode;
    }
    this.groupId = Number(this.route.snapshot.params['groupId']);
    // this.date.value._d.setTime(new Date().getTime() - 86400000);

    if (this.cycleId == '1') {
      this.date = new FormControl(_moment());
      this.date.value._d.setTime(new Date().getTime() - (86400000));
      dashModel = new DashboardModel(this.drilldown.groupId, new Date().getTime() - 86400000, this.cycleId,
        this.shopCode, vdsChannelCode, this.drilldown.serviceId, this.drilldown.parentShopCode, this.drilldown.cardId,
        this.drilldown.isTarget, this.drilldown.expand);
      dashModel.codeWarning = this.drilldown.codeWarning;
      dashModel.pRow = this.drilldown.pRow;
      this.startView = 'month';
      this.hidden = false;
    } else if (this.cycleId == '2') {
      dashModel = new DashboardModel(this.drilldown.groupId, new Date().getTime() - (86400000 * 30), this.cycleId,
        this.shopCode, vdsChannelCode, this.drilldown.serviceId, this.drilldown.parentShopCode, this.drilldown.cardId,
        this.drilldown.isTarget, this.drilldown.expand);
      dashModel.codeWarning = this.drilldown.codeWarning;
      dashModel.pRow = this.drilldown.pRow;
      this.date = new FormControl(_moment());
      if (this.date.value._d.getMonth() === 0) {
        this.date.value._d.setFullYear(this.date.value._d.getFullYear() - 1, 11, 1);
      } else {
        this.date.value._d.setMonth(this.date.value._d.getMonth() - 1, 1);
      }
      this.startView = 'year';
      this.hidden = false;
    } else {
      dashModel = new DashboardModel(this.drilldown.groupId, new Date().getTime() - (86400000 * 90), this.cycleId,
        this.shopCode, vdsChannelCode, this.drilldown.serviceId, this.drilldown.parentShopCode, this.drilldown.cardId,
        this.drilldown.isTarget, this.drilldown.expand);
      dashModel.codeWarning = this.drilldown.codeWarning;
      dashModel.pRow = this.drilldown.pRow;
      this.date = new FormControl(_moment());
      if (this.date.value._d.getMonth() === 0 || this.date.value._d.getMonth() === 1 || this.date.value._d.getMonth() === 2) {
        this.date.value._d.setFullYear(this.date.value._d.getFullYear() - 1, 9, 1);
      } else {
        this.date.value._d.setMonth(this.date.value._d.getMonth() - 3, 1);
      }
      this.startView = 'multi-year';
      this.hidden = true;
      setTimeout(() => {
        this.setDefaultValues();
      }, 200);


    }
    // console.log(this.hidden, ' heheeh');
    // chose(this.cycleId);
    this.filter.emit(dashModel);
  }

  onValueChange(value: string) {
    this.changeToggle = true;
    this.dataUnit.forEach(
      tree => {
        if (tree.shopCode === value) {
          this.shopCode = tree.shopCode;
          this.vdsChannelCode = tree.vdsChannelCode;
          // this.parentShopCode = tree.parentShopCode;
        }
      }
    );

    let dashModel: DashboardModel;
    let vdsChannelCode;
    if (this.vdsChannelCode == '') {
      vdsChannelCode = null;
    } else {
      vdsChannelCode = this.vdsChannelCode;
    }
    this.groupId = Number(this.route.snapshot.params['groupId']);
    if (!this.drilldown) {
      dashModel = new DashboardModel(this.groupId, this.date.value._d.getTime(),
        this.cycleId, this.shopCode, vdsChannelCode);
    } else {
      dashModel = new DashboardModel(this.drilldown.groupId, this.date.value._d.getTime(), this.cycleId,
        this.shopCode, vdsChannelCode, this.drilldown.serviceId, this.shopCode, this.drilldown.cardId,
        this.drilldown.isTarget, this.drilldown.expand);
      dashModel.codeWarning = this.drilldown.codeWarning;
      dashModel.pRow = this.drilldown.pRow;
    }
    // dashModel = new DashboardModel(this.groupId, this.date.value._d.getTime(), this.cycleId,
    //   this.shopCode, this.vdsChannelCode);
    // dashModel = new DashboardModel(this.drilldown.groupId, this.date.value._d.getTime(), this.cycleId,
    //   this.shopCode, this.vdsChannelCode, this.drilldown.serviceId, this.drilldown.parentShopCode, this.drilldown.cardId,
    //   this.drilldown.isTarget, this.drilldown.expand);
    this.filter.emit(dashModel);
  }

  // onClickOut() {
  //   this.isActivated = false;
  // }

  changeYear() {
    this.changeToggle = true;
    const oldDate = new Date(this.dashModel.prdId);
    // console.log(oldDate.getDate(), 'khkhkhkh');
    if (this.value1 == 1) {
      this.date.value._d.setMonth(0, 1);
    } else if (this.value1 == 2) {
      this.date.value._d.setMonth(3, 1);
    } else if (this.value1 == 3) {
      this.date.value._d.setMonth(6, 1);
    } else {
      this.date.value._d.setMonth(9, 1);
    }
    let vdsChannelCode;
    if (this.vdsChannelCode == '') {
      vdsChannelCode = null;
    } else {
      vdsChannelCode = this.vdsChannelCode;
    }
    // console.log(this.date.value._d);
    this.date.setValue(this.date.value);
    if (!this.drilldown) {
      this.dashModel = new DashboardModel(this.groupId, this.date.value._d.getTime(),
        this.cycleId, this.shopCode, vdsChannelCode);
    } else {
      this.dashModel = new DashboardModel(this.drilldown.groupId, this.date.value._d.getTime(), this.cycleId,
        this.shopCode, vdsChannelCode, this.drilldown.serviceId, this.drilldown.parentShopCode, this.drilldown.cardId,
        this.drilldown.isTarget, this.drilldown.expand);
      this.dashModel.codeWarning = this.drilldown.codeWarning;
      this.dashModel.pRow = this.drilldown.pRow;
    }
    // this.dashModel = new DashboardModel(this.groupId, this.date.value._d.getTime(),
    //   this.cycleId, this.shopCode, this.vdsChannelCode);
    // this.dashModel = new DashboardModel(this.drilldown.groupId, this.date.value._d.getTime(), this.cycleId,
    //   this.shopCode, this.vdsChannelCode, this.drilldown.serviceId, this.drilldown.parentShopCode, this.drilldown.cardId,
    //   this.drilldown.isTarget, this.drilldown.expand);
    this.filter.emit(this.dashModel);
  }

  shutdown() {
    // console.log(this.message,' la gi');
    if (!this.childMessage || this.message) {
      this.isActivated = false;
    }
  }

  ngOnDestroy(): void {
    this.unsubscribe$.next();
    this.unsubscribe$.complete();
  }
}
