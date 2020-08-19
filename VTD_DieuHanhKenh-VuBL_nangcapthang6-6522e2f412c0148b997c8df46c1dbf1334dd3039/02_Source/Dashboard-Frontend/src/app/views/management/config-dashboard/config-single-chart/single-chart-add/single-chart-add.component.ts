import {Component, Inject, OnInit, TemplateRef} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';
import {ConfigSingleChartServiceS} from '../../../../../services/management/config.single.chart.service';
import {ToastrService} from 'ngx-toastr';
import {ConfigSingleChartModel} from '../../../../../models/ConfigSingleChart.model';
import {BsModalRef, BsModalService} from 'ngx-bootstrap';
import {TranslateService} from '@ngx-translate/core';
import {TreeItem, TreeviewI18n, TreeviewItem} from 'ngx-treeview';
import {ServiceModel} from '../../../../../models/service.model';
import {ServiceService} from '../../../../../services/management/service.service';
import {WarningReceiveService} from '../../../../../services/management/warning-receive.service';
import {UnitTreeviewI18n} from '../single-chart-update/unit-treeview-i18n';

@Component({
  selector: 'app-single-chart-add',
  templateUrl: './single-chart-add.component.html',
  styleUrls: ['./single-chart-add.component.scss'],
  providers: [
    {provide: TreeviewI18n, useClass: UnitTreeviewI18n}
  ],
})
export class SingleChartAddComponent implements OnInit {

  service: string;
  isAllSpaceOne = false;
  isAllSpaceTwo = false;
  isAllSpaceThree = false;
  singleChartAddForm: FormGroup;
  chartSize;
  chartType;
  drillDownObject;
  card;
  checktext = false;
  checktexttwo = false;
  modalRef: BsModalRef;
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
      {type: 'required', message: this.translate.instant('management.declareVDS.dataNull')}
    ],
    'queryParam': [
      {type: 'required', message: this.translate.instant('management.declareVDS.dataNull')}
    ],
    'metaData': [
      {type: 'required', message: this.translate.instant('management.declareVDS.dataNull')}
    ]
  };

  constructor(@Inject(MAT_DIALOG_DATA) public data: any,
              private fb: FormBuilder,
              private dialogRef: MatDialogRef<SingleChartAddComponent>,
              private configSingleChartService: ConfigSingleChartServiceS,
              private toast: ToastrService,
              private modalService: BsModalService,
              private translate: TranslateService,
              private serviceService: ServiceService,
              private warningReceiveSv: WarningReceiveService) {
  }

  ngOnInit() {
    this.createForm();
    this.getServiceByGroupId();
    this.chartSize = this.data.chartSize;
    this.chartType = this.data.chartType;
    this.drillDownObject = this.data.drilldownObject;
    this.card = this.data.card;
  }

  createForm() {
    this.singleChartAddForm = this.fb.group({
      title: [null, {validators: [Validators.required]}],
      chartSize: [null, [Validators.required]],
      card: [null, [Validators.required]],
      chartType: [null, [Validators.required]],
      expand: [null, [Validators.required]],
      service: [null],
      drilldown: [null, [Validators.required]],
      drillDownObjectId: [null],
      queryParam: [null, {validators: [Validators.required]}],
      metaData: ['', {validators: [Validators.required]}],
      status: {value: 1, disabled: true}
    });
  }

  hasError(controlName: string, errorName: string) {
    return this.singleChartAddForm.controls[controlName].hasError(errorName);
  }

  closeDialog() {
    this.dialogRef.close();
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

  createSingleChart() {
    const title = this.singleChartAddForm.get('title').value;
    const chartSize = this.singleChartAddForm.get('chartSize').value === 'null' ? null
      : this.singleChartAddForm.get('chartSize').value;
    const card = this.singleChartAddForm.get('card').value === 'null' ? null
      : this.singleChartAddForm.get('card').value;
    const chartType = this.singleChartAddForm.get('chartType').value === 'null' ? null
      : this.singleChartAddForm.get('chartType').value;
    const expand = this.singleChartAddForm.get('expand').value === 'null' ? null
      : this.singleChartAddForm.get('expand').value;
    const drilldown = this.singleChartAddForm.get('drilldown').value === 'null' ? null
      : this.singleChartAddForm.get('drilldown').value;
    const drillDownObjectId = this.singleChartAddForm.get('drillDownObjectId').value === 'null' ? null
      : this.singleChartAddForm.get('drillDownObjectId').value;
    const queryParam = this.singleChartAddForm.get('queryParam').value;
    const metaData = this.singleChartAddForm.get('metaData').value;
    this.service = this.marrServiceIds.join();
    const singleChart = new ConfigSingleChartModel(card, chartType, chartSize, metaData,
      this.marrServiceIds, title, drilldown, expand, 1, queryParam, drillDownObjectId, null, null, this.service);
console.log(singleChart)
    this.configSingleChartService.addChart(singleChart).subscribe(
      message => {
        if (message['data'] === 'SUCCESS') {
          this.showSuccess(this.translate.instant('management.warningconfig.addsuccess'));
          this.warningReceiveSv.setReloadWarning(1);
          this.createForm();
          this.getServiceByGroupId();
          this.checktext = true;
          this.checktexttwo = true;
        } else if (message['data'] === 'ERROR') {
          this.showError(this.translate.instant('management.warningconfig.duplicate'));
        } else {
          this.showError(this.translate.instant('management.warningconfig.serverError'));
        }
      }
    );
    this.onBack();
  }


  onBack() {
    this.modalRef.hide();
  }

  clickSave(add: TemplateRef<any>) {
    this.modalRef = this.modalService.show(add, {
      ignoreBackdropClick: true // click ra ngoai khong dong modal
    });
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

  onClose() {
    this.dialogRef.close();
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
