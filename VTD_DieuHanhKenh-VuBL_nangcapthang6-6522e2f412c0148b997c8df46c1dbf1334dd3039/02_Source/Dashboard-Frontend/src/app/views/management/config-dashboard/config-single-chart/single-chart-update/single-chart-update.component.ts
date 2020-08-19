import { Component, Inject, OnInit, TemplateRef } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ConfigSingleChartServiceS } from '../../../../../services/management/config.single.chart.service';
import { ToastrService } from 'ngx-toastr';
import { BsModalRef, BsModalService } from 'ngx-bootstrap';
import { TranslateService } from '@ngx-translate/core';
import { ServiceService } from '../../../../../services/management/service.service';
import { TreeItem, TreeviewI18n, TreeviewItem } from 'ngx-treeview';
import { ServiceModel } from '../../../../../models/service.model';
import { ConfigSingleChartModel } from '../../../../../models/ConfigSingleChart.model';
import { UnitTreeviewI18n } from './unit-treeview-i18n';
import { WarningReceiveService } from '../../../../../services/management/warning-receive.service';

@Component({
  selector: 'app-single-chart-update',
  templateUrl: './single-chart-update.component.html',
  styleUrls: ['./single-chart-update.component.scss'],
  providers: [
    { provide: TreeviewI18n, useClass: UnitTreeviewI18n }
  ],
})
export class SingleChartUpdateComponent implements OnInit {

  singleChartUpdateForm: FormGroup;
  service: string;
  chartId;
  title;
  chartSize;
  card;
  chartType;
  expand;
  serviceIds;
  drilldown;
  drilldownObject;
  status;
  metaData;
  queryParam;
  chartSizes;
  chartTypes;
  drilldownObjects;
  cards;
  arrServiceIds: any[];
  modalRef: BsModalRef;
  isAllSpaceOne = false;
  isAllSpaceTwo = false;
  isAllSpaceThree = false;

  mobjConfig = {
    hasAllCheckBox: true,
    hasFilter: true,
    hasCollapseExpand: false,
    maxHeight: 150,
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

  validation_messages = {
    'title': [
      { type: 'required', message: this.translate.instant('management.declareVDS.dataNull') }
    ],
    'queryParam': [
      { type: 'required', message: this.translate.instant('management.declareVDS.dataNull') }
    ],
    'metaData': [
      { type: 'required', message: this.translate.instant('management.declareVDS.dataNull') }
    ]
  };

  constructor(@Inject(MAT_DIALOG_DATA) public data: any,
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<SingleChartUpdateComponent>,
    private configSingleChartService: ConfigSingleChartServiceS,
    private toast: ToastrService,
    private modalService: BsModalService,
    private translate: TranslateService,
    private serviceService: ServiceService,
    private warningReceiveSv: WarningReceiveService) {
  }

  ngOnInit() {
    this.getDataUpdate();
    this.getServiceByGroupId();
    this.createForm();
  }

  createForm() {
    this.singleChartUpdateForm = this.fb.group({
      title: [this.title, { validators: [Validators.required] }],
      chartSize: [this.chartSize, [Validators.required]],
      card: [this.card, [Validators.required]],
      chartType: [this.chartType, [Validators.required]],
      expand: [this.expand, [Validators.required]],
      service: [null],
      drilldown: [this.drilldown, [Validators.required]],
      drillDownObjectId: [this.drilldownObject],
      status: [this.status, [Validators.required]],
      queryParam: [this.queryParam, { validators: [Validators.required] }],
      metaData: [this.metaData, { validators: [Validators.required] }]
    });
  }

  checkSpaceOne(event) {
    if ((event.target.value || '').trim().length === 0) {
      this.isAllSpaceOne = true;
    } else {
      this.isAllSpaceOne = false;
    }
  }

  checkSpaceTwo(event) {
    if ((event.target.value || '').trim().length === 0) {
      this.isAllSpaceTwo = true;
    } else {
      this.isAllSpaceTwo = false;
    }
  }

  checkSpaceThree(event) {
    if ((event.target.value || '').trim().length === 0) {
      this.isAllSpaceThree = true;
    } else {
      this.isAllSpaceThree = false;
    }
  }

  check(pobjNodeTree: TreeviewItem) {
    this.arrServiceIds.forEach(id => {
      if (id == pobjNodeTree.value) {
        pobjNodeTree.checked = true;
      }
    });
    if (pobjNodeTree.children) {
      pobjNodeTree.children.forEach(value => {
        this.arrServiceIds.forEach(id => {
          if (id == value.value) {
            value.checked = true;
          }
        });
        this.check(value);
      });
    }
  }

  getDataUpdate() {
    this.chartId = this.data.chartId;
    this.title = this.data.title;
    this.chartSize = this.data.chartSize;
    this.card = this.data.card;
    this.chartType = this.data.chartType;
    this.expand = this.data.expand;
    this.serviceIds = this.data.serviceIDs;
    this.drilldown = this.data.drillDown;
    this.drilldownObject = this.data.drillDownObject;
    this.status = this.data.status;
    this.metaData = this.data.metaData;
    this.queryParam = this.data.queryParam;
    this.chartSizes = this.data.chartSizes;
    this.chartTypes = this.data.chartTypes;
    this.drilldownObjects = this.data.drilldownObjects;
    this.cards = this.data.cards;
  }

  clickUpdate(edit: TemplateRef<any>) {
    this.modalRef = this.modalService.show(edit, {
      ignoreBackdropClick: true // click ra ngoai khong dong modal
    });
  }

  closeDialog() {
    this.dialogRef.close();
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

  updateSingleChart() {
    const chartId = this.chartId;
    const title = this.singleChartUpdateForm.get('title').value;
    const chartSize = this.singleChartUpdateForm.get('chartSize').value === 'null' ? null
      : this.singleChartUpdateForm.get('chartSize').value;
    const card = this.singleChartUpdateForm.get('card').value === 'null' ? null
      : this.singleChartUpdateForm.get('card').value;
    const chartType = this.singleChartUpdateForm.get('chartType').value === 'null' ? null
      : this.singleChartUpdateForm.get('chartType').value;
    const expand = this.singleChartUpdateForm.get('expand').value === 'null' ? null
      : this.singleChartUpdateForm.get('expand').value;
    const drilldown = this.singleChartUpdateForm.get('drilldown').value === 'null' ? null
      : this.singleChartUpdateForm.get('drilldown').value;
    const drillDownObjectId = this.singleChartUpdateForm.get('drillDownObjectId').value === 'null' ? null
      : this.singleChartUpdateForm.get('drillDownObjectId').value;
    const status = this.singleChartUpdateForm.get('status').value === 'null' ? null
      : this.singleChartUpdateForm.get('status').value;
    const queryParam = this.singleChartUpdateForm.get('queryParam').value;
    const metaData = this.singleChartUpdateForm.get('metaData').value;
    this.service = this.marrServiceIds.join();
    this.selectTree([]);
    const singleChart = new ConfigSingleChartModel(card, chartType, chartSize, metaData,
      this.marrServiceIds, title, drilldown, expand, status, queryParam, drillDownObjectId, null, chartId, this.service);
    this.configSingleChartService.updateSingleChart(singleChart).subscribe(
      message => {
        if (message['data'] === 'SUCCESS') {
          this.showSuccess(this.translate.instant('management.warningconfig.updatesuccess'));
          this.warningReceiveSv.setReloadWarning(0);
          this.closeDialog();
        } else if (message['data'] === 'ERROR') {
          this.showError(this.translate.instant('management.warningconfig.duplicate'));
        } else {
          this.showError(this.translate.instant('management.warningconfig.serverError'));
        }
      });
    this.onBack();

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
        if (this.serviceIds !== null) {
          this.arrServiceIds = this.serviceIds.split(',');
          this.marrNodeTreeviewServices.forEach(element => {
            this.check(element);
          });
        }
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
