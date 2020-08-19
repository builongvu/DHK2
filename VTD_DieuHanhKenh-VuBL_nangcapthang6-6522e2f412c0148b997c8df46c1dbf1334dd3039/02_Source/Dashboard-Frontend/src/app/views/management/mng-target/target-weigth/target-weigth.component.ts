import { AfterViewInit, Component, OnDestroy, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { DateAdapter, MAT_DATE_FORMATS, MAT_DATE_LOCALE, MatCheckboxChange, MatDatepicker, MatSelect, MatDialog } from '@angular/material';
import { Moment } from 'moment';
import { ServiceService } from '../../../../services/management/service.service';
import { TreeItem, TreeviewConfig, TreeviewI18n, TreeviewItem } from 'ngx-treeview';
import { UnitModel } from '../../../../models/unit.model';
import { ServiceScoreService } from '../../../../models/serviceScore.service';
import { BsModalRef, BsModalService } from 'ngx-bootstrap';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { config } from '../../../../config/application.config';
import { FormArray, FormControl } from '@angular/forms';
import * as _moment from 'moment';
import { MomentDateAdapter } from '@angular/material-moment-adapter';
import { UnitTreeviewI18n } from '../../vtt-target/unit-treeview-i18n';
import { ShopCodesModel } from '../../../../models/shopCodes.model';
import { DownloadService } from '../../../../services/management/download.service';
import * as fileSaver from 'file-saver';
import { ToastrService } from 'ngx-toastr';
import { TargetWeigth } from '../../../../models/targetWeigth';
import { StaffModel } from '../../../../models/staff.model';
import { ReplaySubject, Subject } from 'rxjs';
import { TranslateService } from '@ngx-translate/core';
import { mod } from 'ngx-bootstrap/chronos/utils';
import { AddTargetWeigthComponent } from './add-target-weigth/add-target-weigth.component';
import { EditTargetWeigthComponent } from './edit-target-weigth/edit-target-weigth.component';

export const MY_FORMATS_DATE = {
  parse: {
    dateInput: 'DD/MM/YYYY',
  },
  display: {
    dateInput: 'DD/MM/YYYY',
    monthYearLabel: 'DDD MMM YYYY',
    dateA11yLabel: 'LL',
    monthYearA11yLabel: 'DDD MMMM YYYY',
  },
};

@Component({
  selector: 'app-target-weigth',
  templateUrl: './target-weigth.component.html',
  styleUrls: ['./target-weigth.component.scss'],
  providers: [
    { provide: DateAdapter, useClass: MomentDateAdapter, deps: [MAT_DATE_LOCALE] },
    { provide: MAT_DATE_FORMATS, useValue: MY_FORMATS_DATE },
    { provide: TreeviewI18n, useClass: UnitTreeviewI18n }
  ],
})
export class TargetWeigthComponent implements OnInit, OnDestroy, AfterViewInit {
  @ViewChild('closeModalEdit') closeModalEdit;
  @ViewChild('closeModalAdd') closeModalAdd;
  @ViewChild('singleSelect') singleSelect: MatSelect;
  @ViewChild('singleSelectEdit') singleSelectEdit: MatSelect;
  @ViewChild('singleSelectAdd') singleSelectAdd: MatSelect;
  @ViewChild(AddTargetWeigthComponent) createScore: AddTargetWeigthComponent;

  constructor(private serviceService: ServiceService,
    private modalService: BsModalService,
    private http: HttpClient,
    private toastr: ToastrService,
    public dialog: MatDialog,
    private downloadService: DownloadService,
    private translate: TranslateService) {
  }

  mblnConfirm;
  mblnCheckAll = false;
  mstrstatus = '';
  mdtDateFrom;
  mdtDateTo;
  serviceId;
  nodeTreeView: TreeviewItem;
  nodeTreeViews: TreeviewItem[] = [];
  nodeItem: TreeItem;
  nodeItems: TreeItem[] = [];

  dataTree = [];
  dataUnit = [];
  nodeTrees: UnitModel[] = [];
  unitIitem: UnitModel;
  value;
  mobjNodeItemService: TreeItem;
  marrIndexNodeService = [];
  mobjConfigScore = TreeviewConfig.create({
    hasFilter: true,
    hasCollapseExpand: false,
    maxHeight: 250,
  });
  serviceName;
  staffName;
  score;
  scoreMax;
  fromDate;
  toDate;
  dataTables: TargetWeigth[] = [];
  status;
  mobjModalRef: BsModalRef;
  mobjFileList: FileList;
  currentP = 1;
  pageSize = config.pageSize;
  staffs: any[] = [];
  mobjDataUpload;
  mnbrCode;
  mstrMessageUpload;
  mnbrSumSuccessfulRecord;
  mnbrSumRecord;
  mstrResultFileName;
  mblnIsSelectFile;
  mblnIsClickHere = false;
  staffChose;
  localServiceName;
  serviceNameNot;
  shopCode;
  vdsChannelCode;
  showMessageUpload = false;
  showCreateButton = true;
  showAddButton = true;
  showEditButton = true;
  mblnCheckFileNull = false;
  staffTrees: TreeviewItem[] = [];
  choseText;
  staffTree: TreeviewItem;
  staffTreeChose;
  fileName;

  mdtDateFromAdd;
  dateToAdd;
  scoreAdd;
  scoreMaxAdd;
  statusAdd;
  serviceIdAdd;
  servicenameAdd;
  staffAdd;
  valueAdd;

  mdtDateFromEdit;
  mdtDateToEdit;
  scoreEdit;
  scoreMaxEdit;
  statusEdit;
  idRowEdit;
  serviceIdEdit;
  servicenameEdit;
  staffEdit = 'null';
  valueEdit;
  dataEdit;

  ngOnInit() {

    this.fileName = this.translate.instant('management.weight.fileName');
    // this.choseText = this.translate.instant('management.weight.choseText');
    if (localStorage.getItem(config.user_default_language) === 'en') {
      this.choseText = 'Select';
      this.fileName = 'ServiceScore_Template.xlsx';
    } else {
      this.choseText = 'Chọn';
      this.fileName = 'Trongsochitieu_Filemau.xlsx';
    }

    this.status = null;
    this.createTreeView();
    this.getDatas();

  }

  noMethod() {

  }

  chosenMonthHandlerFrom($event, pobjDatepicker: MatDatepicker<Moment>) {
    this.mdtDateFrom.setValue($event);
    pobjDatepicker.close();
    // this.validateDate();
  }

  chosenMonthHandlerTo($event, pobjDatepicker: MatDatepicker<Moment>) {
    this.mdtDateTo.setValue($event);
    pobjDatepicker.close();
    // this.validateDate();
  }

  getDatas() {
    // console.log('lol i18n', this.choseText);
    this.staffTree = new TreeviewItem({
      text: this.choseText,
      value: 'null',
      children: []
    });
    this.serviceService.service$.subscribe(data => {
      this.serviceId = data['id'];
      if (this.serviceId === 0) {
        this.localServiceName = null;
        this.showCreateButton = true;
      }
      // console.log('status: ',typeof data['status']);

      if (data['status'] == '0') {
        this.showCreateButton = true;
      }

      if (data['status'] == '1') {
        this.showCreateButton = false;
        this.localServiceName = data['name'];
        if (this.localServiceName) {
          this.showCreateButton = false;
        }
      }

      this.mstrstatus = '';
      if (data['id']) {
        const serviceScore: ServiceScoreService = new ServiceScoreService(data['id']);
        this.serviceService.getDataWeight(serviceScore).subscribe(
          dataser => {
            this.currentP = 1;
            console.log('data all: ', dataser['data']);
            this.dataTables = dataser['data'];
            // console.log('datatables: ', this.dataTables);
            if (dataser['data'].length > 0) {
              if (dataser['data'][0]['nameService']) {
                this.serviceName = dataser['data'][0]['nameService'];
              }

              this.serviceNameNot = dataser['data'][0]['nameService'];
              this.value = dataser['data'][0]['shopCode'];
              this.staffTreeChose = dataser['data'][0]['nameService'];
              this.staffs.forEach(
                staff => {
                  if (dataser['data'][0]['staffName'] == staff['staffName']) {
                    this.staffTreeChose = staff['staffCode'];
                  }
                }
              );
              // this.staffTreeChose = 'NGOCTH';
              this.score = dataser['data'][0]['score'];
              this.scoreMax = dataser['data'][0]['scoreMax'];
              this.status = dataser['data'][0]['status'];
              if (dataser['data'][0]['fromDate']) {
                this.mdtDateFrom = new FormControl(_moment());
                this.mdtDateFrom.value._d.setTime(dataser['data'][0]['fromDate']);
              }
              if (dataser['data'][0]['toDate']) {
                this.mdtDateTo = new FormControl(_moment());
                this.mdtDateTo.value._d.setTime(dataser['data'][0]['toDate']);
              }

            } else {
              this.value = 'VDS';
              this.serviceName = null;
              this.staffTreeChose = 'null';
              this.status = 1;
              this.score = null;
              this.scoreMax = null;
              this.mdtDateFrom = new FormControl(null);
              this.mdtDateTo = new FormControl(null);
            }
            this.dataTables.forEach(targetWeight => {
              targetWeight.check = false;
            });
          }
        );
      }
    });
  }

  createTreeView() {
    try {
      this.nodeTreeViews = [];
      this.serviceService.getDataUnits().subscribe(
        next => {
          if (!next['data']) {
            // this.shopCode = null;
            // this.vdsChannelCode = null;
            return;
          }
          this.nodeTreeViews = [];
          this.dataUnit = next['data'];
          this.dataTree = next['data'];
          this.nodeTrees = this.createNode(this.nodeTrees, this.dataTree, this.unitIitem);
          this.dataTree = this.nodeTrees;
          this.createTree(this.nodeTrees, this.dataTree);
          this.nodeTrees.forEach(valuess => {
            this.nodeTreeView = new TreeviewItem(this.forwardData(valuess, this.nodeItem, this.nodeItems));
            this.nodeTreeViews.push(this.nodeTreeView);
            // console.log(this.nodeTreeViews);
          });

          for (let i = 0; i < this.nodeTreeViews.length; i++) {
            if (this.nodeTreeViews[i].children) {
              this.value = this.nodeTreeViews[i].value;
              this.dataUnit.forEach(
                tree => {
                  if (tree.shopCode === this.nodeTreeViews[i].value) {
                    // this.shopCode = tree.shopCode;
                    // this.vdsChannelCode = tree.vdsChannelCode;
                    return;
                  }
                }
              );
              break;
            } else {
              this.value = this.nodeTreeViews[0].value;
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
      const c = (a: number, b: number) => (a - b);
      this.marrIndexNodeService.sort(c);
      for (let i = this.marrIndexNodeService.length - 1; i >= 0; i--) {
        parrNodeTrees.splice(this.marrIndexNodeService[i], 1);
      }
    } catch (e) {
      console.log(e);
    }
  }

  unitChange(value: string) {
    this.value = value;
    this.showAddButton = false;
    this.dataUnit.forEach(
      tree => {
        if (tree.shopCode === value) {
          this.shopCode = tree.shopCode;
          this.vdsChannelCode = tree.vdsChannelCode;
        }
      }
    );
    // console.log('shopCode: ', this.shopCode);
    // console.log('vdsChannelCode: ', this.vdsChannelCode);
    this.getStaffs();
  }

  renderFilter(row: any) {
    this.value = row['shopCode'];
    this.scoreAdd = null;
    this.scoreMaxAdd = null;
    this.showCreateButton = false;
    const staffName = row['staffName'];
    this.staffs.forEach(
      staff => {
        if (staffName == staff['staffName']) {
          this.staffTreeChose = staff['staffCode'];
        }
      }
    );
    this.staffChose = row['staffName'];
    this.serviceIdAdd = row['serviceId'];
    this.serviceIdEdit = row['serviceId'];
    this.serviceName = row['nameService'];
    this.score = row['score'];
    this.scoreMax = row['scoreMax'];
    // this.fromDate = row['fromDate'];
    this.mdtDateFrom = new FormControl(_moment());
    this.mdtDateTo = new FormControl(_moment());
    if (!row['toDate']) {
      this.mdtDateTo = new FormControl(null);
    } else {
      this.mdtDateTo.value._d.setTime(row['toDate']);
    }

    this.mdtDateFrom.value._d.setTime(row['fromDate']);
    // console.log(this.mdtDateFrom.value._d);

    // this.toDate = row['toDate'];
    this.status = row['status'];
  }

  onBack() {
    this.showMessageUpload = false;
    this.mobjModalRef.hide();
    this.mblnCheckFileNull = false;
    this.mobjFileList = null;
  }

  selectFile(file) {
    this.mstrMessageUpload = false;
    this.mobjFileList = file.target.files;
    if (this.mobjFileList.length > 0) {
      this.mblnCheckFileNull = false;
    } else {
      this.mblnCheckFileNull = true;
    }
  }

  upLoad() {
    // this.mblnCheckFileNull = true;
    this.showMessageUpload = true;
    // this.mblnIsSelectFile = true;
    if (this.mobjFileList) {
      const vfileFile: File = this.mobjFileList[0];
      const vobjFormData: FormData = new FormData();
      vobjFormData.append('file', vfileFile);
      const vobjHeaders = new HttpHeaders();
      /** In Angular 5, including the header Content-Type can invalidate your request */
      // vobjHeaders.append('Content-type', 'multipart/form-data');
      vobjHeaders.append('Content-type', 'application/json');
      const vobjOptions = { headers: vobjHeaders };
      this.http.post(config.upload_weight_API, vobjFormData, vobjOptions)
        .subscribe(
          vobjData => {
            console.log('upload: ', vobjData);
            this.resultUpload(vobjData);
          },
          vobjError => (console.log(vobjError)),
          () => {
            console.log('ok');
            // this.search();
            // this.mobjFileList = null;
          }
        );
    } else {
      console.log('file null');
      this.mblnCheckFileNull = true;
    }
    return;
  }

  downloadResult(pstrNameFileResult: string) {
    this.mblnIsClickHere = true;
    let vstFfilename: string;
    this.http.get(`${config.download_Result_File_API}?fileName=${pstrNameFileResult}`);
    this.downloadService.downloadResult(pstrNameFileResult)
      .subscribe(vobjResponse => {
        vstFfilename = this.mstrResultFileName;
        this.saveFile(vobjResponse.body, vstFfilename);
      });
  }

  saveFile(pobjData: any, pstrFilename?: string) {
    const vobjBlob = new Blob([pobjData], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
    fileSaver.saveAs(vobjBlob, pstrFilename);
  }

  resultUpload(pobjData) {
    this.mobjDataUpload = pobjData['data'];
    this.mnbrCode = pobjData.code;
    this.mstrMessageUpload = this.mobjDataUpload['message'];
    this.showError(this.mnbrCode);
  }

  showError(pnbrCode: number) {
    if (pnbrCode === undefined) {
      console.log('Waining: code is undefine');
    } else if (pnbrCode === 200) {
      this.loadData();
      setTimeout(() => {
        this.mnbrSumSuccessfulRecord = this.mobjDataUpload.sumSuccessfulRecord;
      }, 50);

      this.mnbrSumRecord = this.mobjDataUpload.sumRecord;
      this.mstrResultFileName = this.mobjDataUpload.resultFileName;
    } else {
      this.mobjFileList = null;
    }
    this.mblnIsSelectFile = true;
  }

  confirm(pobjTemplate: TemplateRef<any>) {
    this.mobjModalRef = this.modalService.show(pobjTemplate, {
      ignoreBackdropClick: true
    }
    );
  }

  pageChange(page: number) {
    this.mblnCheckAll = false;
    let total = this.currentP * 10;
    if (this.currentP * 10 > this.dataTables.length) {
      total = this.dataTables.length;
    }
    for (let i = (this.currentP - 1) * 10; i < total; i++) {
      this.dataTables[i].check = false;
    }
    this.mstrstatus = '';
    this.currentP = page;
  }

  getCreateDate(createDate: any) {
    const dateNum = Date.parse(createDate);
    if (!createDate) {
      return null;
    } else {
      const date = new Date(createDate);
      const options = {
        year: 'numeric', month: 'numeric', day: 'numeric',
      };

      return date.toLocaleDateString('vi', options);
    }

  }

  search() {
    let serviceIdSearch;
    if (!this.serviceId) {
      serviceIdSearch = null;
    } else {
      serviceIdSearch = this.serviceId;
    }
    this.score = null;
    this.scoreMax = null;
    this.mdtDateFrom = new FormControl(null);
    this.mdtDateTo = new FormControl(null);
    // this.serviceName = null;
    // console.log('ddd: ', this.bankCtrl.value);
    let staffCodeSearch;
    if (this.staffChose === 'null') {
      staffCodeSearch = null;
    } else {
      staffCodeSearch = this.staffChose;
    }
    const serviceModel: ServiceScoreService = new ServiceScoreService(serviceIdSearch, null, this.status,
      null, null, null, null, this.shopCode, staffCodeSearch, this.vdsChannelCode);
    console.log('search: ', serviceModel);
    console.log('id: ', this.serviceId);
    this.serviceService.search(serviceModel).subscribe(
      data => {
        console.log('data search: ', data['data']);
        this.dataTables = data['data'];
      }
    );
  }

  loadData() {
    const serviceModel: ServiceScoreService = new ServiceScoreService(this.serviceId);
    console.log('search: ', serviceModel);
    this.serviceService.search(serviceModel).subscribe(
      data => {
        console.log('data search: ', data['data']);
        this.dataTables = data['data'];
        this.dataTables.forEach(targetWeight => {
          targetWeight.check = false;
        });
      }
    );
  }

  showModalCreate() {
    if (!this.serviceName && this.localServiceName) {
      this.servicenameAdd = this.localServiceName;
    } else {
      this.servicenameAdd = this.serviceName;
    }

    this.valueAdd = 'VDS';
    this.serviceIdAdd = this.serviceId;
    this.statusAdd = 1;
    this.dateToAdd = null;
    this.scoreAdd = null;
    this.scoreMaxAdd = null;
    this.mdtDateFromAdd = new FormControl(_moment());
    // this.mdtDateToAdd.value._d = null;
    this.staffAdd = 'null';
    // this.clickSave(addTemplate);
    const vdialog = this.dialog.open(AddTargetWeigthComponent, {
      data: {
        tree: this.staffTrees,
        treeUnit: this.nodeTreeViews,
        dataUnit: this.dataUnit,
        serviceid: this.serviceId,
        servicenameAdd: this.servicenameAdd
      }
    });
    this.serviceService.reloadWarning$.subscribe(res => {
      if (res) {
        this.currentP = 1;
      }
    });
    vdialog.afterClosed().subscribe(result => {

      this.loadData();
      // this.mnbrP = 1;
      // this.getAllMenu();
    });
  }

  showModalEdit(row: any) {
    this.dataEdit = row;
    // console.log(row['staffName']);
    // this.staffTreeChoseEdit = row
    this.valueEdit = row['shopCode'];
    // console.log('value2', this.valueEdit);
    this.showEditButton = false;
    this.serviceIdEdit = row['serviceId'];
    this.idRowEdit = row['id'];
    this.scoreEdit = row['score'];
    this.scoreMaxEdit = row['scoreMax'];
    this.servicenameEdit = row['nameService'];
    this.statusEdit = row['status'];
    this.mdtDateFromEdit = new FormControl(_moment());
    this.mdtDateFromEdit.value._d.setTime(row['fromDate']);
    if (row['fromDate']) {
      this.mdtDateFromEdit = new FormControl(_moment());
      this.mdtDateFromEdit.value._d.setTime(row['fromDate']);
    } else {
      this.mdtDateFromEdit = new FormControl(null);
    }
    if (row['toDate']) {
      this.mdtDateToEdit = new FormControl(_moment());
      this.mdtDateToEdit.value._d.setTime(row['toDate']);
    } else {
      this.mdtDateToEdit = new FormControl(null);
    }
    // this.staffEdit = row['staffName'];
    // console.log('staff', row['staffName']);
    // setTimeout(() => {
    if (!row['staffName']) {
      this.staffEdit = 'null';
    } else {
      this.staffs.forEach(
        staff => {
          if (row['staffName'] == staff['staffName']) {
            this.staffEdit = staff['staffCode'];
            this.getStaffs();
          }
        }
      );
    }
    // console.log('staff 1: ', this.staffEdit);
    // }, 500);
    const vdialog = this.dialog.open(EditTargetWeigthComponent, {
      data: {
        tree: this.staffTrees,
        treeUnit: this.nodeTreeViews,
        dataEdit: this.dataEdit,
        dataUnit: this.dataUnit,
        serviceid: this.serviceId,
        servicenameAdd: this.servicenameAdd,
        valueEdit: this.valueEdit,
        showEditButton: this.showEditButton,
        serviceIdEdit: this.serviceIdEdit,
        idRowEdit: this.idRowEdit,
        scoreEdit: this.scoreEdit,
        scoreMaxEdit: this.scoreMaxEdit,
        servicenameEdit: this.servicenameEdit,
        statusEdit: this.statusEdit,
        mdtDateFromEdit: this.mdtDateFromEdit,
        mdtDateToEdit: this.mdtDateToEdit,
        staffEditName: row['staffName']
      }
    });
    vdialog.afterClosed().subscribe(result => {
      this.loadData();
      // this.mnbrP = 1;
      // this.getAllMenu();
    });
    // this.clickSave(editTemplate);

  }

  ngAfterViewInit() {
  }

  ngOnDestroy() {
  }

  getStaffs() {
    this.staffTree = new TreeviewItem({
      text: this.choseText,
      value: 'null',
      children: []
    });
    this.staffs = [];
    this.staffTrees = [];
    // const shopCodes = [];
    try {
      let vdsChannelCodeCst;
      if (this.vdsChannelCode == 'null') {
        vdsChannelCodeCst = '';
      } else {
        vdsChannelCodeCst = this.vdsChannelCode;
      }
      // const shopCodemodel: ShopCodesModel = new ShopCodesModel(this.shopCodes);
      this.serviceService.getStaffs(this.shopCode, vdsChannelCodeCst).subscribe(
        data => {
          this.staffTrees = [];
          // console.log('staff: ', data['data']);
          this.staffs = data['data'];
          this.staffs.forEach(
            staff => {
              const staffTree = new TreeviewItem({
                text: staff['staffName'],
                value: staff['staffCode'],
                children: []
              });
              this.staffTrees.push(staffTree);
            }
          );
          this.staffTrees.unshift(this.staffTree);
          setTimeout(() => {
            this.staffTreeChose = 'null';
          }, 100);
        }
      );
      // console.log('staff: ', this.staffTrees);
    } catch (e) {
      console.log('loi in get Staffs()');
    }

  }

  dateChange(value: any) {
    console.log('date change', value.value._d);
    this.dateToAdd = value;

    if (!value) {
      return;
    }
  }

  select(id: number) {
    for (let i = 0; i < this.dataTables.length; i++) {
      if (this.dataTables[i].id === id) {
        if (this.dataTables[i].check === true) {
          this.dataTables[i].check = false;
        } else {
          this.dataTables[i].check = true;
        }
        break;
      }
    }
    let vblncheck = true;
    let total = this.currentP * 10;
    if (this.currentP * 10 > this.dataTables.length) {
      total = this.dataTables.length;
    }
    for (let i = (this.currentP - 1) * 10; i < total; i++) {
      if (this.dataTables[i].check === false) {
        vblncheck = false;
      }
    }
    ;
    if (vblncheck) {
      this.mblnCheckAll = true;
    } else {
      this.mblnCheckAll = false
        ;
    }
    this.CheckStatus();
  }

  checkAll() {
    let vblnCheck = false;
    if (this.mblnCheckAll === false) {
      vblnCheck = true;
    }
    let total = this.currentP * 10;
    if (this.currentP * 10 > this.dataTables.length) {
      total = this.dataTables.length;
    }
    for (let i = (this.currentP - 1) * 10; i < total; i++) {
      if (vblnCheck === true) {
        this.dataTables[i].check = true;
      } else {
        this.dataTables[i].check = false;
      }
    }
    this.CheckStatus();
  }

  CheckStatus() {
    console.log(this.dataTables)
    this.mstrstatus = '0';
    let marr = this.dataTables.filter(item => item.check === true);
    if (marr.length > 0) {
      this.mstrstatus = marr[0].status;
      for (let i = 1; i < marr.length; i++) {
        if (marr[i].status !== this.mstrstatus) {
          this.mstrstatus = '-1';
          break;
        }
      }
    } else {
      this.mstrstatus = '';
    }
  }

  LockUnlock(status: number) {
    const varrLock = this.dataTables.filter(item => item.check === true);
    for (let i = 0; i < varrLock.length; i++) {
      const serviceModel: ServiceScoreService = new ServiceScoreService(varrLock[i].serviceId, varrLock[i].nameService, status,
        varrLock[i].fromDate, varrLock[i].toDate, varrLock[i].score, varrLock[i].scoreMax,
        varrLock[i].id, varrLock[i].shopCode, varrLock[i].staffCode);
      console.log(serviceModel);
      if (i === varrLock.length - 1) {
        this.serviceService.updateScoreService(serviceModel, varrLock[i].id).subscribe(
          data => {
            console.log(data);
          },
          error => {
            console.log(error);
          },
          () => {
            this.toastr.success(this.translate.instant(status === 0 ? 'management.service.message.successLock' : 'management.service.message.successUnlock'),
              this.translate.instant('management.weight.infor'), {
              timeOut: 3000,
              positionClass: 'toast-top-center',
            });
            this.back();
            this.mstrstatus = '';
            this.loadData();
            this.mblnCheckAll = false;
          }
        );
      } else {
        this.serviceService.updateScoreService(serviceModel, varrLock[i].id).subscribe(
          data => {
          },
          error => {
            console.log(error);
          },
          () => {
          }
        );
      }
    }
  }

  openConfirm(pobjTemplate: TemplateRef<any>, type: number) {
    if (type === 1) {
      this.mblnConfirm = true;
    } else {
      this.mblnConfirm = false;
    }
    this.mobjModalRef = this.modalService.show(pobjTemplate, {
      ignoreBackdropClick: true
    }
    );
  }

  back() {
    this.mobjModalRef.hide();
  }

  getScore(rowElement: number) {
    // console.log(rowElement);
    return Math.round(rowElement * 100) / 100;
  }

  getScoreMaxFilter(rowElement: number) {
    // console.log(rowElement);
    return Math.round(rowElement * 100) / 100 ? Math.round(rowElement * 100) / 100 : null;
  }


  fileDownload() {
    this.serviceService.downloadFile()
      .subscribe(vobjResponse => {
        const vstrFilename = this.fileName;
        this.saveFile(vobjResponse.body, vstrFilename);
      });
  }

  staffChange(value: any) {
    this.staffChose = value;
  }

}
