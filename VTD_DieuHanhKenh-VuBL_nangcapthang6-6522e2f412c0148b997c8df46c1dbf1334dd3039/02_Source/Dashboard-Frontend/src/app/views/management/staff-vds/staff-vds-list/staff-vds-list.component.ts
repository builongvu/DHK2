import {Component, OnInit, TemplateRef} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {StaffVdsCreateComponent} from './staff-vds-create/staff-vds-create.component';
import {MatDialog} from '@angular/material/dialog';
import {BsModalRef} from 'ngx-bootstrap/modal';
import {TreeVDSService} from '../../../../services/management/tree-VDS.service';
import {BsModalService} from 'ngx-bootstrap';
import * as fileSaver from 'file-saver';
import {StaffsTable} from '../../../../models/StaffsTable.model';
import {StaffVDS} from '../../../../models/StaffVDS.model';
import {UnitModel} from '../../../../models/unit.model';
import {StaffSearchVDS} from '../../../../models/StaffSearchVDS.model';
import {TreeItem, TreeviewConfig, TreeviewItem} from 'ngx-treeview';
import {ServiceService} from '../../../../services/management/service.service';
import {DelStaff} from '../../../../models/DelStaff.model';
import {ToastrService} from 'ngx-toastr';
import {TranslateService} from '@ngx-translate/core';
import {config} from '../../../../config/application.config';
import {StaffVdsEditComponent} from './staff-vds-edit/staff-vds-edit.component';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {DownloadService} from '../../../../services/management/download.service';

@Component({
  selector: 'app-staff-vds-list',
  templateUrl: './staff-vds-list.component.html',
  styleUrls: ['./staff-vds-list.component.scss']
})
export class StaffVdsListComponent implements OnInit {
  // begin paging
  currentP = 1;
  pageSize = config.pageSize;
  mstrstatus = '';
  // end paging
  value = 'VDS';
  // begin get ID to delelte
  idToDel: DelStaff;
  del: number;
  // end del
  // begin form
  vdsForm: FormGroup;
  // end form
  // begin list on click
  staffsTable: StaffsTable[] = [];
  // end
  // list on add
  staffsVDS: StaffVDS[] = [];
  // list on search
  staffSearch: StaffSearchVDS[] = [];
  // object to send search
  staffSearchObj: StaffSearchVDS;
  // check when radio button select all unit
  isAllChecked = false;
  // end check
  service: any;
  staffUnit: any[] = [];
  shopCode: any;
  unitCode: any;
  mobjModalRef: BsModalRef;
  checkSearchAll: boolean;
  // begin tree Unit
  nodeTreeView: TreeviewItem;
  nodeTreeViews: TreeviewItem[] = [];
  nodeItem: TreeItem;
  nodeItems: TreeItem[] = [];
  nodeTrees: UnitModel[] = [];
  unitIitem: UnitModel;
  mobjNodeItemService: TreeItem;
  mobjConfigScore = TreeviewConfig.create({
    hasFilter: true,
    hasCollapseExpand: false,
    maxHeight: 250
  });
  dataUnit: any[] = [];
  dataTree: any[] = [];
  marrIndexNodeService: any[] = [];
  vdsChannelCode = null;
  // end tree

  // check toan bo nhan vien cac cap or nhan vien truc thuoc don vi
  position = true;

  // check shopWarning
  shopWarning = '0';

  // check array data search Null
  isDataSearch = false;

  // up load
  mstrMessageUpload;
  mnbrSumSuccessfulRecord;
  mnbrSumRecord;
  mstrResultFileName;
  mblnIsSelectFile;
  mblnIsClickHere = false;
  showMessageUpload = false;
  mblnCheckFileNull = false;
  mobjFileList: FileList;
  mobjDataUpload;
  mnbrCode;

  constructor(private fb: FormBuilder,
              public dialog: MatDialog,
              private http: HttpClient,
              private downloadService: DownloadService,
              private toastr: ToastrService,
              private translate: TranslateService,
              private serviceService: ServiceService,
              private modalService: BsModalService,
              private treeVDSService: TreeVDSService) {
    this.createForm();
  }

  validation_messages = {
    'staffCode': [
      {type: 'required', message: 'Unit code is required'},
      {type: 'minlength', message: 'Username must be at least 2 characters long'},
      {type: 'maxlength', message: 'Unit code cannot be more than 5 characters long'}
    ],
    'staffName': [
      {type: 'required', message: 'Unit code is required'}
    ],
    'phoneNumber': [
      {type: 'required', message: 'Phone\'s Number is required'},
      {type: 'pattern', message: 'What are you trying to do?'}
    ]
  };


  ngOnInit() {
    // this.onChanges();
    this.vdsForm.controls['position'].valueChanges.subscribe(value => {
      this.position = value;
    });
    // edit shop warning 1/5/2020
    this.vdsForm.controls['shopWarning'].valueChanges.subscribe(value => {
      this.shopWarning = value;
    });
    this.createTreeView();
    this.treeVDSService.service$.subscribe(
      data => {
        this.resetForm();
        this.vdsChannelCode = data['vdsChannelCode'];
        this.staffsTable = [];
        if (!data['shopCode']) {
          return;
        } else {
          this.value = data['shopCode'];
          const a = {vdsChannelCode: data['vdsChannelCode']};
          // edit shop warning 1/5/2020
          this.staffSearchObj = new StaffSearchVDS(this.value, this.position, this.shopWarning);
          Object.assign(this.staffSearchObj, a);
          this.treeVDSService.searchStaffVDS(this.staffSearchObj).subscribe(table => {
            this.staffsTable = table['data'];
            this.currentP = 1;
            if (this.staffsTable.length === 0) {
              this.isDataSearch = true;
            } else {
              this.isDataSearch = false;
            }
          });
          this.vdsForm.controls['vdsChannelCode'].setValue(this.vdsChannelCode);
        }
      }
    );

    // check if click all unit tree
    const position = this.vdsForm.value.position;
    position ? this.isAllChecked = true : this.isAllChecked = false;
    // end check
  }

  createForm() {
    this.vdsForm = this.fb.group({
      vdsChannelCode: [null],
      staffCode: [null, {validators: [Validators.required, Validators.maxLength(100)], updateOn: 'blur'}],
      staffName: [null, {validators: [Validators.required, Validators.maxLength(100)], updateOn: 'blur'}],
      staffType: [null],
      email: [null],
      phoneNumber: [null, {validators: [Validators.required, Validators.pattern('^[0-9]*$')], updateOn: 'blur'}],
      position: [true],
      shopWarning: ['0']
    });
  }

  // submit to search
  submit() {
    this.currentP = 1;
    Object.assign(this.staffSearchObj, this.vdsForm.value);
    this.treeVDSService.searchStaffVDS(this.staffSearchObj).subscribe(data => {
      this.staffsTable = data['data'];
      if (this.staffsTable.length === 0) {
        this.isDataSearch = true;
      } else {
        this.isDataSearch = false;
      }
    });
  }
  submitUpDate() {
    this.currentP = this.currentP;
    Object.assign(this.staffSearchObj, this.vdsForm.value);
    this.treeVDSService.searchStaffVDS(this.staffSearchObj).subscribe(data => {
      this.staffsTable = data['data'];
    });
  }

  checkInPage() {

  }

  checkUnit(children: any) {
  }

  // Confirm create form
  confirmCreate(pobjTemplate: TemplateRef<any>) {
    const {channel, codeNv, nameNv, phone, position} = this.vdsForm.value;
    const a: any[] = [channel, codeNv, nameNv, phone, position];
    a.forEach(ele => {
      this.staffUnit.push(ele);
    });
    this.mobjModalRef = this.modalService.show(pobjTemplate, {
        ignoreBackdropClick: true
      }
    );
  }

  // create Staff to Unit
  createSaffToUnit() {
  }

  // colse dialog confirm
  onBackConfirm() {
    this.mobjModalRef.hide();
  }

  // event change Tree unit
  unitChange(value: string) {
    this.value = value;
    this.dataUnit.forEach(
      tree => {
        if (tree.shopCode === value) {
          this.shopCode = tree.shopCode;
          this.vdsChannelCode = tree.vdsChannelCode;
          this.vdsForm.controls['vdsChannelCode'].setValue(this.vdsChannelCode);
        }
      }
    );
    // console.log('shopCode: ', this.shopCode);
    // console.log('vdsChannelCode: ', this.vdsChannelCode);
  }

  // make tree
  createTreeView() {
    try {
      this.nodeTreeViews = [];
      this.serviceService.getDataUnitsVDS().subscribe(
        next => {
          if (!next['data']) {
            this.shopCode = null;
            this.vdsChannelCode = null;
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
          });
          for (let i = 0; i < this.nodeTreeViews.length; i++) {
            if (this.nodeTreeViews[i].children) {
              this.value = this.nodeTreeViews[i].value;
              this.dataUnit.forEach(
                tree => {
                  if (tree.shopCode === this.nodeTreeViews[i].value) {
                    this.shopCode = tree.shopCode;
                    this.vdsChannelCode = tree.vdsChannelCode;
                    return;
                  }
                }
              );
              break;
            } else {
              this.value = this.nodeTreeViews[0].value;
            }
          }
          const {shopCode, vdsChannelCode} = this.dataUnit[0];
          const a = {vdsChannelCode: vdsChannelCode};
          // edit shop warning 1/5/2020
          this.staffSearchObj = new StaffSearchVDS(shopCode, this.position, this.shopWarning);
          Object.assign(this.staffSearchObj, a);
          this.treeVDSService.searchStaffVDS(this.staffSearchObj).subscribe(dataSearch => {
            this.staffsTable = dataSearch['data'];
            if (this.staffsTable.length === 0) {
              this.isDataSearch = true;
            } else {
              this.isDataSearch = false;
            }
          });
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
          groupId: value.groupId,
          shortName: value.shortName
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

  // end tree

  // Confirm delete Staff
  delStaffConfirm(data: any, pobjTemplate: TemplateRef<any>) {
    const a = data['id'];
    this.del = a;
    this.mobjModalRef = this.modalService.show(pobjTemplate, {
        ignoreBackdropClick: true
      }
    );
  }

  // Delete Staff from Unit
  delSaffFromUnit() {
    this.idToDel = new DelStaff(this.del);
    this.treeVDSService.deleleStaff(this.idToDel).subscribe(dataDel => {
      if (dataDel['data'] === 'Success') {
        this.resetForm();
        this.submit();
        this.showSuccess(this.translate.instant('management.group.message.deleteSuccess'));
      } else {
        this.showFail(this.translate.instant('management.group.message.deleteFail'));
      }
      this.mobjModalRef.hide();
    });
  }

  // show success message
  showSuccess(message: string) {
    this.toastr.success(message,
      this.translate.instant('management.warningconfig.success'), {
        timeOut: 2000,
        positionClass: 'toast-top-center',
      });
  }

  // show fail message
  showFail(message: string) {
    this.toastr.error(message,
      this.translate.instant('management.warningconfig.success'), {
        timeOut: 2000,
        positionClass: 'toast-top-center',
      });
  }

  // paging
  pageChange(page: number) {
    let total = this.currentP * 10;
    if (this.currentP * 10 > this.staffsTable.length) {
      total = this.staffsTable.length;
    }
    this.mstrstatus = '';
    this.currentP = page;
  }

  // open dialog create
  openDialogCreate() {
    const dialogRef = this.dialog.open(StaffVdsEditComponent, {
      data: {
        valueEdit: this.value,
        vdsChannelCode: this.vdsChannelCode,
        treeUnit: this.nodeTreeViews,
        dataUnit: this.dataUnit
      },
      maxWidth: '85vw',
      maxHeight: '95vh',
      width: '75vw',
    });

    dialogRef.afterClosed().subscribe(result => {
      this.currentP = 1;
      this.resetForm();
      this.submit();
    });
  }

  // open Edit dialog
  openDialog(staffTanle: StaffsTable) {
    const dialogRef = this.dialog.open(StaffVdsCreateComponent, {
      data: {
        staffsTable: staffTanle,
        treeUnit: this.nodeTreeViews,
        dataUnit: this.dataUnit
      },
      maxWidth: '85vw',
      maxHeight: '95vh',
      width: '75vw',
    });

    dialogRef.afterClosed().subscribe(result => {
      this.resetForm();
      this.submitUpDate();
    });
  }

  setRowtoForm(data: any) {
    this.vdsForm.patchValue({
      staffCode: data['staffCode'],
      staffName: data['staffName'],
      staffType: data['staffType'],
      email: data['email'],
      phoneNumber: data['phoneNumber'],
      shopWarning: data['shopWarning']
    });
  }

  resetForm() {
    this.vdsForm.patchValue({
      staffCode: null,
      staffName: null,
      staffType: '',
      email: null,
      phoneNumber: null
    });
  }

  //   upload
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
      const vobjOptions = {headers: vobjHeaders};
      this.http.post(config.search_VDS_Staff_UpLoad_API, vobjFormData, vobjOptions)
        .subscribe(
          vobjData => {
            this.resultUpload(vobjData);
          },
          vobjError => (console.log(vobjError)),
          () => {
            // this.search();
            // this.mobjFileList = null;
          }
        );
    } else {
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
    const vobjBlob = new Blob([pobjData], {type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'});
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
      // this.loadData();
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
}
