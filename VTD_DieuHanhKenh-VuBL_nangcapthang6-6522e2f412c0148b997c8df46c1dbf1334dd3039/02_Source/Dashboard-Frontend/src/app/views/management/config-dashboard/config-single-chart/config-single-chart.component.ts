import {Component, OnInit, TemplateRef} from '@angular/core';
import {ConfigGroupCardModel, GroupCardCycle} from '../../../../models/config.group.card.model';
import {ConfigGroupCardService} from '../../../../services/management/config.group.card.service';
import {config} from '../../../../config/application.config';
import {TreeItem, TreeviewConfig, TreeviewI18n, TreeviewItem} from 'ngx-treeview';
import {ServiceService} from '../../../../services/management/service.service';
import {UnitModel} from '../../../../models/unit.model';
import {BsModalRef, BsModalService} from 'ngx-bootstrap';
import {ToastrService} from 'ngx-toastr';
import {TranslateService} from '@ngx-translate/core';
import {GroupModel} from '../../../../models/group.model';
import {Pager} from '../../../../models/Pager';
import {FormBuilder, FormGroup} from '@angular/forms';
import {ConfigCardSize, ConfigSingleCardModel} from '../../../../models/config.single.card.model';
import {ConfigChartType, ConfigSingleChartModel} from '../../../../models/ConfigSingleChart.model';
import {ConfigSingleCardService} from '../../../../services/management/config.single.card.service';
import {DateAdapter, MAT_DATE_FORMATS, MAT_DATE_LOCALE, MatDialog} from '@angular/material';
import {ConfigSingleChartServiceS} from '../../../../services/management/config.single.chart.service';
import {SingleChartAddComponent} from './single-chart-add/single-chart-add.component';
import {ServiceModel} from '../../../../models/service.model';
import {SingleChartUpdateComponent} from './single-chart-update/single-chart-update.component';
import {MomentDateAdapter} from '@angular/material-moment-adapter';
import {MY_FORMATS} from '../../vtt-target/vtt-target.component';
import {WarningReceiveService} from '../../../../services/management/warning-receive.service';
import {UnitTreeviewI18n} from './single-chart-update/unit-treeview-i18n';

@Component({
  selector: 'app-config-single-chart',
  templateUrl: './config-single-chart.component.html',
  styleUrls: ['./config-single-chart.component.scss'],
  providers: [
    {provide: TreeviewI18n, useClass: UnitTreeviewI18n}
  ],
})
export class ConfigSingleChartComponent implements OnInit {
  configSingleChart;
  singleChartForm: FormGroup;
  chartType: ConfigChartType[];
  groupCard: ConfigGroupCardModel[];
  singleCard: ConfigSingleCardModel[];
  modalRef: BsModalRef;
  total;
  mnbrP = 1;
  currentPage;
  pager: Pager;
  pageSize = config.pageSize;
  singleCharts: ConfigSingleChartModel[];
  listCardSize: ConfigCardSize[];
  mobjConfig = {
    hasAllCheckBox: true,
    hasFilter: true,
    hasCollapseExpand: false,
    maxHeight: 230,
    decoupleChildFromParent: true,
  };
  marrIndexNode = [];
  mobjNodeTreeviewService: TreeviewItem;
  marrNodeTreeviewServices: TreeviewItem[] = [];
  mobjNodeItemService: TreeItem;
  marrNodeItemServices: TreeItem[] = [];
  marrData = [];
  mobjItemService: ServiceModel;
  marrItemServices: ServiceModel[] = [];
  marrServiceIds: any[] = [];

  constructor(private configSingleChartService: ConfigSingleChartServiceS,
              private configSingleCardService: ConfigSingleCardService,
              private fb: FormBuilder,
              private dialog: MatDialog,
              private serviceService: ServiceService,
              private translate: TranslateService,
              private toast: ToastrService,
              private modalService: BsModalService,
              private groupCardService: ConfigGroupCardService,
              private warningReceiveSv: WarningReceiveService
  ) {
  }

  ngOnInit() {
    this.createForm();
    this.getActiveCard();
    this.getCardSize();
    this.getChartType();
    this.getGroupCard();
    this.pager = new Pager(1, 10);
    this.getServiceByGroupId();
    this.getDataSearch();
    this.warningReceiveSv.reloadWarning$.subscribe(page => {
      if (page == 1) {
        this.pager.page = 1;
      }
      this.getDataSearch();
    });

  }

  createForm() {
    this.singleChartForm = this.fb.group({
      title: null,
      chartSize: ['null'],
      card: ['null'],
      chartType: ['null'],
      expand: ['null'],
      service: ['null'],
      drilldown: ['null'],
      drillDownObjectId: ['null'],
      status: ['null'],
      queryParam: null,
      metaData: null
    });
  }

  getActiveCard() {
    this.configSingleCardService.getActiveCard().subscribe(
      card => {
        this.singleCard = card['data'];
      }
    );
  }

  getCardSize() {
    this.configSingleCardService.getCardSizeAPI('size').subscribe(
      dataCardSize => {
        this.listCardSize = dataCardSize['data'];
      }
    );
  }

  getGroupCard() {
    this.configSingleCardService.getGroupCardOrderName().subscribe(
      dataGroupCard => {
        this.groupCard = dataGroupCard['data'];
      }
    );
  }

  getChartType() {
    this.configSingleChartService.getChartTypeAPI('CHART_TYPE').subscribe(
      chartType => {
        this.chartType = chartType['data'];
      }
    );
  }

  check(pobjNodeTree: TreeviewItem) {
    this.marrServiceIds.push(pobjNodeTree.value);

    if (pobjNodeTree.children) {
      pobjNodeTree.children.forEach(value => {
        this.check(value);
      });
    }
  }

  getDataSearch() {
    const title = this.singleChartForm.get('title').value;
    const chartSize = this.singleChartForm.get('chartSize').value === 'null' ? null
      : this.singleChartForm.get('chartSize').value;
    const card = this.singleChartForm.get('card').value === 'null' ? null
      : this.singleChartForm.get('card').value;
    const chartType = this.singleChartForm.get('chartType').value === 'null' ? null
      : this.singleChartForm.get('chartType').value;
    const expand = this.singleChartForm.get('expand').value === 'null' ? null
      : this.singleChartForm.get('expand').value;
    const drilldown = this.singleChartForm.get('drilldown').value === 'null' ? null
      : this.singleChartForm.get('drilldown').value;
    const drillDownObjectId = this.singleChartForm.get('drillDownObjectId').value === 'null' ? null
      : this.singleChartForm.get('drillDownObjectId').value;
    const status = this.singleChartForm.get('status').value === 'null' ? null
      : this.singleChartForm.get('status').value;
    const queryParam = this.singleChartForm.get('queryParam').value;
    const metaData = this.singleChartForm.get('metaData').value;

    const singleSearch = new ConfigSingleChartModel(card, chartType, chartSize, metaData,
      this.marrServiceIds, title, drilldown, expand, status, queryParam, drillDownObjectId, this.pager);

    this.configSingleChartService.getSingleChartByCondition(singleSearch).subscribe(
      singleChart => {
        this.singleCharts = singleChart['data'];
        this.total = singleChart['totalRow'];
      }
    );
  }

  openConfirm(pobjTemplate: TemplateRef<any>, singleChart: ConfigSingleChartModel) {
    this.configSingleChart = singleChart;
    this.modalRef = this.modalService.show(pobjTemplate, {
        ignoreBackdropClick: true
      }
    );
  }

  deleteSingleChart() {
    if (this.configSingleChart.groupName && this.configSingleChart.groupName.length > 0) {
      this.showError(this.translate.instant('management.single-chart.cantdelete'));
      this.onBack();
    } else {
      this.configSingleChartService.deleteSingChart(this.configSingleChart).subscribe(
        deleteSingleChart => {
          if (deleteSingleChart['data'] === 'SUCCESS') {
            this.showSuccess(this.translate.instant('management.menu.deleteSuccess'));
          } else {
            this.showError(this.translate.instant('management.warningconfig.serverError'));
          }
          this.onBack();
          this.getDataSearch();
        }
      );
    }
  }

  search() {
    this.pager.page = 1;
    this.getDataSearch();
  }

  onBack() {
    this.modalRef.hide();
  }

  showSuccess(message: string) {
    this.toast.success(message,
      this.translate.instant('management.warningconfig.success'), {
        timeOut: 2000,
        positionClass: 'toast-top-center',
      });
  }

  showError(message: string) {
    this.toast.error(message,
      this.translate.instant('management.warningconfig.fail'), {
        timeOut: 2000,
        positionClass: 'toast-top-center',
      });
  }

  pageChange(page: number) {
    this.pager.page = page;
    this.getDataSearch();
  }

  openUpdateDialog(chart: ConfigSingleChartModel) {
    const dialogRef = this.dialog.open(SingleChartUpdateComponent, {
        maxWidth: '95vw',
        maxHeight: '95vh',
        width: '80vw',
        data: {
          chartId: chart.chartId,
          title: chart.title,
          chartSize: chart.chartSize,
          card: chart.cardId,
          chartType: chart.chartType,
          expand: chart.expand,
          serviceIDs: chart.serviceIds,
          drillDown: chart.drilldown,
          drillDownObject: chart.drillDownObjectId,
          status: chart.status,
          metaData: chart.metaData,
          queryParam: chart.queryParam,
          chartSizes: this.listCardSize,
          chartTypes: this.chartType,
          drilldownObjects: this.groupCard,
          cards: this.singleCard
        }
      }
    );
    dialogRef.afterClosed().subscribe(result => {
      // this.getDataSearch();
    });
  }

  openDialog() {
    const dialogRef = this.dialog.open(SingleChartAddComponent, {
        maxWidth: '95vw',
        maxHeight: '95vh',
        width: '80vw',
        data: {
          chartSize: this.listCardSize,
          chartType: this.chartType,
          drilldownObject: this.groupCard,
          card: this.singleCard
        }
      }
    );
  }

  getServiceByGroupId() {
    this.marrNodeTreeviewServices = [];
    this.marrData = [];
    this.serviceService.getAllServiceByStatus().subscribe(
      vobjNext => {
        this.marrData = vobjNext['data'];
        this.marrItemServices = this.createNode(this.marrItemServices, this.marrData);
        // this.data = this.itemServices;
        this.createTree(this.marrItemServices, this.marrData);
        this.marrItemServices.forEach(vobjValue => {
          this.mobjNodeTreeviewService = new TreeviewItem(this.forwardData(vobjValue, this.mobjNodeItemService, this.marrNodeItemServices));
          this.marrNodeTreeviewServices.push(this.mobjNodeTreeviewService);
        });
        // console.log(this.marrNodeTreeviewServices);
        this.marrServiceIds = [];
        this.marrNodeTreeviewServices.forEach(value => {
          this.check(value);
        });

      },
      vobjErorr => {
      }
    );
  }

  createNode(parrItems: ServiceModel[] = [], parrDataTree: ServiceModel[]) {
    parrItems = parrDataTree.map(vobjValue => {
      this.mobjItemService = vobjValue;
      this.mobjItemService.children = [];
      return this.mobjItemService;
    });
    return parrItems;
  }

  createTree(parrNodeTrees: ServiceModel[], parrDataTree: ServiceModel[]) {
    const vnbrLen = parrNodeTrees.length;
    for (let i = 0; i < vnbrLen; i++) {
      for (let j = 0; j < parrDataTree.length; j++) {
        if (parrNodeTrees[i].id === parrDataTree[j].parentId) {
          parrNodeTrees[i].children.push(parrDataTree[j]);
          this.marrIndexNode.push(j);
        }
      }
    }
    // console.log('arrrrr: ', this.marrIndexNode);
    const vnbrC = (pnbrA: number, pnbrB: number) => (pnbrA - pnbrB);
    this.marrIndexNode.sort(vnbrC);
    for (let vnbrI = this.marrIndexNode.length - 1; vnbrI >= 0; vnbrI--) {
      parrNodeTrees.splice(this.marrIndexNode[vnbrI], 1);
    }
    // console.log('arrrrr222: ', this.marrIndexNode);
  }

  forwardData(pobjNodeTree: ServiceModel, pobjitem: TreeItem, parrItems: TreeItem[] = []) {
    pobjitem = null;
    parrItems = [];
    if (pobjNodeTree.children) {
      pobjNodeTree.children.forEach(value => {
        this.mobjNodeItemService = this.forwardData(value, null, []);
        parrItems.push(this.mobjNodeItemService);
      });
    }
    if (pobjNodeTree.children) {
      pobjitem = {
        value: pobjNodeTree.id,
        text: pobjNodeTree.id + '-' + pobjNodeTree.name,
        children: parrItems,
        checked: false
      };
    } else {
      parrItems = [];
      pobjitem = {
        value: pobjNodeTree.id,
        text: pobjNodeTree.id + '-' + pobjNodeTree.name,
        children: null,
        checked: false
      };
    }
    // pobjitem.checked = true;
    return pobjitem;
  }

  selectTree($event: number[]) {
    // console.log($event);
    this.marrServiceIds = [];
    // this.marrServiceIds = $event;
    this.marrNodeTreeviewServices.forEach(
      vobjValue => {
        this.getSelect(vobjValue);
      }
    );
  }

  getSelect(pobjNodeTree: TreeviewItem) {
    if (pobjNodeTree.checked) {
      this.marrServiceIds.push(pobjNodeTree.value);
    }
    if (pobjNodeTree.children) {
      pobjNodeTree.children.forEach(value => {
        this.getSelect(value);
      });
    }
  }
}
