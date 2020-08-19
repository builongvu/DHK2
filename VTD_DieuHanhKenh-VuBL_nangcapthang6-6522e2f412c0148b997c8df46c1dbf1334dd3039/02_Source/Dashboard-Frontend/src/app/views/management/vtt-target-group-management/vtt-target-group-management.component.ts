import { Component, Inject, OnInit, TemplateRef } from '@angular/core';
import { ProductService } from '../../../services/management/product.service';
import { GroupsService } from '../../../services/management/group.service';
import { BsModalService, BsModalRef } from 'ngx-bootstrap';
import * as _moment from 'moment';
import { FormBuilder, FormGroup, FormControl, Validators } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { TranslateService } from '@ngx-translate/core';
import { ProductModel, ProductTree } from '../../../models/Product.model';
import { GroupModel, Searchgroup } from '../../../models/group.model';
import { TargetGroupSearchModel } from '../../../models/TargetGroupSearchModel';
import { Pager } from '../../../models/Pager';
import { SortTable } from '../../../models/SortTable';
import { HttpHeaders, HttpClient } from '@angular/common/http';
import { config } from '../../../config/application.config';
import * as fileSaver from 'file-saver';
import { Template } from '@angular/compiler/src/render3/r3_ast';
import { WarningSendService } from '../../../services/management/warning-send.service';
import { ServiceModel } from '../../../models/service.model';
import { TreeviewItem, TreeItem, TreeviewConfig } from 'ngx-treeview';
import { DialogGroupComponent } from './dialog-group/dialog-group.component';
import { MAT_DIALOG_DATA, MatDialog } from '@angular/material';
import { DialogSendComponent } from '../warning-config/dialog-send/dialog-send.component';
import { WarningSendTable } from '../../../models/Warning-config';
import { WarningReceiveService } from '../../../services/management/warning-receive.service';

@Component({
  selector: 'app-vtt-target-group-management',
  templateUrl: './vtt-target-group-management.component.html',
  styleUrls: ['./vtt-target-group-management.component.scss']
})
export class VttTargetGroupManagementComponent implements OnInit {

  constructor(private http: HttpClient, private productService: ProductService,
    private groupsService: GroupsService,
    private modalService: BsModalService,
    private fb: FormBuilder,
    public dialog: MatDialog,
    private toastr: ToastrService,
    private warningReceiveService: WarningReceiveService,
    private warningSendService: WarningSendService,
    private translate: TranslateService) {
  }

  mstrServiceCode = '';
  conflictCode: boolean;

  mobjConfig = TreeviewConfig.create({
    hasFilter: true,
    hasCollapseExpand: false,
    maxHeight: 116,
  });
  mobjformInsert: FormGroup;
  mobjFormUpdate: FormGroup;
  marrProducts: ProductModel[] = [];
  mnbrP = 1;
  marrNodeItemServices: TreeItem[] = [];
  mnbrTotal = 0;
  mnbrPageSize = 10;
  isDisable = true;
  marrDataTable: GroupModel[] = [];
  mnbrProductIdcbo;
  mstrNameGroup;
  mobjModalRef: BsModalRef;
  mobjSearchModel: TargetGroupSearchModel;
  groupMode: GroupModel;
  mobjPager: Pager;
  mobjFileList: FileList;
  mblnIsSelectFile = false;
  mstrCodeGroup;
  mdtDate = new FormControl(_moment());
  mblnCheckFileNull = false;
  mobjDataUpload;
  mblnIsAdd = false;
  mstrstatus = '';
  mblnIsValid = true;
  // mstrDateString;
  changeDatetime1;
  mobjdataForm;
  mblnIsValidProductField = true;
  mblnIsValidCodeField = true;
  mblnIsValidNameField = true;
  mnbrCode;
  order = true;
  mstrColumnName: string;
  mstrTypeSort: string;
  mobjSortTable: SortTable;
  mobjIsClickSearch = false;
  mnbrSumSuccessfulRecord;
  mnbrSumRecord;
  mstrResultFileName;
  mstrMessageUpload;
  mblnConfirm: boolean;
  sstatus = '';
  cCheckbox: boolean;
  mstrSNameGroup;
  mstrSCodeGroup;
  newgroup: GroupModel = new GroupModel();
  cCheckAll = false;
  searchStatus = '-1';
  mnbrSProductIdcbo = -1;
  marrItemServices: ProductTree[] = [];
  mobjNodeTreeviewService: TreeviewItem;
  mobjNodeItemService: TreeItem;
  mobjItemService: ProductTree;
  marrNodeTreeviewServices: TreeviewItem[] = [];
  searchGroup: Searchgroup = new Searchgroup();
  marrIndexNode = [];
  value = -1;
  value1 = -1;
  fileName: string;

  newSerivceGroupForm: FormGroup;
  mstrNewName = '';

  ngOnInit() {
    this.marrDataTable = null;
    this.mstrColumnName = 'id';
    this.mstrTypeSort = 'asc';
    this.mobjSortTable = new SortTable(this.mstrColumnName, this.mstrTypeSort);
    this.createFormUpdate();

    this.changeDatetime1 = this.formatDateToString(this.mdtDate.value._d);
    this.getProducts();
    this.warningReceiveService.reloadWarning$.subscribe(res => {
      if(res != 0){
        this.getSearch(true);
      }else{
        this.getSearch(false);
      }
    });
  }

  formatDateToString(pdtDate: Date): String {
    const vstrDay = this.format_ddMM(pdtDate.getDate().toString());
    const vstrMonth = this.format_ddMM(pdtDate.getMonth().toString());
    const vstrYear = pdtDate.getFullYear().toString();

    return vstrDay + '/' + vstrMonth + '/' + vstrYear;
  }

  format_ddMM(pstrArg: String) {
    return pstrArg = pstrArg.length == 1 ? '0' + pstrArg : pstrArg;
  }

  getProducts() {
    this.productService.getProducts().subscribe(
      next => {
        this.marrNodeTreeviewServices = [];
        this.marrProducts = [];
        this.marrProducts = next;
        const product: ProductTree = new ProductTree();
        product.mlngId = -1;
        if (localStorage.getItem(config.user_default_language) === 'en') {
          product.mstrName = 'Select';
        } else {
          product.mstrName = 'Chọn';
        }
        this.marrProducts.unshift(product);
        this.marrProducts.forEach(
          valueUnit => {
            const text = valueUnit['mstrName'];
            const value = valueUnit['mlngId'];
            this.marrNodeTreeviewServices.push(new TreeviewItem({ text, value, checked: false }));
          }
        );
        this.value = -1;
        this.getSearch(true);
      },

      error => (this.marrProducts = [], console.log(error))
    );
  }

  createFormUpdate() {
    this.mobjFormUpdate = this.fb.group({
      id: [''],
      code: [''],
      name: ['', Validators.required],
      productId: ['', Validators.required],
      changeDatetime1: [''],
      userUpdate: [''],
      status: ['', Validators.required],
    });
  }


  noMethod() {
  }

  onBackConfirm() {
    this.mobjModalRef.hide();
    this.mstrNewName = '';
    this.mstrServiceCode = '';
  }

  Onchange(value: any) {
    this.mnbrSProductIdcbo = value;
  }

  Onchange1(value: any) {
    this.newSerivceGroupForm.controls['product'].setValue(value);
  }

  pageChange(pnbrP: number) {
    this.mnbrP = pnbrP;
    this.sstatus = '';
    this.cCheckAll = false;
    this.getSearch(false);
  }

  search() {
    this.getDataFormSearch();
    this.showDataSearch(this.mobjSearchModel);
  }

  getDataFormSearch() {
    if (!this.mnbrProductIdcbo) {
      this.mnbrProductIdcbo = null;
    }

    if (!this.mstrCodeGroup) {
      this.mstrCodeGroup = null;
    }

    if (!this.mstrNameGroup) {
      this.mstrNameGroup = null;
    }
    this.mobjPager = new Pager(this.mnbrP, this.mnbrPageSize);
    this.mobjSortTable = new SortTable(this.mstrColumnName, this.mstrTypeSort);
    this.mobjSearchModel = new TargetGroupSearchModel(this.mnbrProductIdcbo, this.mstrCodeGroup, this.mstrNameGroup, this.mobjPager);
  }


  showDataSearch(pobjModel) {
    this.groupsService.getDatas(pobjModel).subscribe(
      vobjNext => {
        this.marrDataTable = vobjNext['data'];
        this.mnbrTotal = vobjNext['totalRow'];
        this.marrDataTable.forEach(mobj => {
          mobj.cCheckBox = false;
        });
      }, () => this.marrDataTable = []);
  }

  clickSearch() {
    this.mobjIsClickSearch = true;
    this.mstrColumnName = 'id';
    this.mstrTypeSort = 'asc';
    this.mnbrP = 1;
    this.search();
  }

  selectFile(pobjEvent) {
    this.mnbrCode = null;
    this.mblnIsSelectFile = false;
    if (!this.mobjFileList) {
      this.mblnIsSelectFile = true;
    } else if (this.mobjFileList[0] !== pobjEvent.target.files[0]) {
      this.mblnIsSelectFile = false;
    }
    this.mstrstatus = '';
    this.mstrMessageUpload = '';
    this.mstrResultFileName = '';
    this.mobjFileList = pobjEvent.target.files;
    this.mblnCheckFileNull = false;
  }

  onBack() {
    this.mnbrCode = null;
    this.mobjFileList = null;
    this.mblnCheckFileNull = false;
    if (this.mobjModalRef) {
      this.mobjModalRef.hide();
    }
    this.getSearch(true);
  }

  downloadResult() {
    this.warningSendService.DownloadWarningSend(this.mstrResultFileName)
      .subscribe(vobjResponse => {
        this.saveFile(vobjResponse.body, this.mstrResultFileName);
      });
  }

  upLoad() {
    this.mblnIsSelectFile = true;
    const messageSuccess = onmessage + this.translate.instant('management.group.message.successupload');
    const messageFail = messageSuccess + this.translate.instant('management.group.message.fail');
    if (this.mobjFileList) {
      const vfileFile: File = this.mobjFileList[0];
      const vobjFormData: FormData = new FormData();
      vobjFormData.append('file', vfileFile);
      const vobjHeaders = new HttpHeaders();
      /** In Angular 5, including the header Content-Type can invalidate your request */
      vobjHeaders.append('Content-type', 'multipart/form-data');
      vobjHeaders.append('Accept', 'application/json');
      const vobjOptions = { headers: vobjHeaders };
      this.http.post(config.uploadFileGroupServce_API, vobjFormData, vobjOptions)
        .subscribe(
          vobjData => {
            this.getRespond(vobjData);
          },
          vobjError => {
          },
        );
    } else {
      this.mblnCheckFileNull = true;
    }
    return this.mobjDataUpload;
  }

  getRespond(pobjData) {
    this.mobjDataUpload = pobjData['data'];
    this.mnbrCode = pobjData.code;
    this.mstrstatus = this.translate.instant('management.warningconfig.UploadSuccess')
      + pobjData.data.sumSuccessfulRecord + '/' + pobjData.data.sumRecord + ' ' +
      this.translate.instant('management.warningconfig.record');
    this.mstrMessageUpload = this.mobjDataUpload['message'];
    this.showError(this.mnbrCode);
  }

  showError(pnbrCode: number) {
    if (pnbrCode === undefined) {
    } else if (pnbrCode === 200) {
      this.mnbrSumSuccessfulRecord = this.mobjDataUpload.sumSuccessfulRecord;
      this.mnbrSumRecord = this.mobjDataUpload.sumRecord;
      this.mstrResultFileName = this.mobjDataUpload.resultFileName;
    } else {
      this.mobjFileList = null;
    }
    this.mblnIsSelectFile = true;
  }

  confirm(pobjTemplate: TemplateRef<any>) {
    this.value = -1;
    this.createNewSerivceGroupForm();
    this.mstrstatus = '';
    this.mstrMessageUpload = '';
    this.mstrResultFileName = '';
    if (this.mobjModalRef) {
      this.mobjModalRef.hide();
    }
    this.mobjModalRef = this.modalService.show(pobjTemplate, {
      ignoreBackdropClick: true
    }
    );
  }

  confirmAd(pobjTemplate: TemplateRef<any>) {
    if (this.mobjModalRef) {
      this.mobjModalRef.hide();
    }
    this.mobjModalRef = this.modalService.show(pobjTemplate, {
      ignoreBackdropClick: true
    }
    );
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

  clickSort(pstrColumnName: string) {
    if (this.mstrTypeSort == null) {
      this.mstrTypeSort = 'desc';
    } else if (this.mstrTypeSort === 'asc') {
      this.mstrTypeSort = 'desc';
    } else {
      this.mstrTypeSort = 'asc';
    }
    this.mstrColumnName = pstrColumnName;
    this.mobjSortTable = new SortTable(this.mstrColumnName, this.mstrTypeSort);
    if (!this.mobjIsClickSearch) {
      this.marrDataTable = null;
    } else {
      this.search();
    }
  }

  insertData() {
    const message = this.translate.instant('management.group.message.insert');
    const messageSuccess = message + this.translate.instant('management.group.message.success');
    const messageFail = messageSuccess + this.translate.instant('management.group.message.fail');
    // const groupModelN: GroupModel = new GroupModel(this.mstrCodeGroup, this.mstrNameGroup, this.mnbrProductIdcbo);

    const code = (this.newSerivceGroupForm.get('code').value.toString().trim().toUpperCase().replace(/ /g, ''));


    this.groupMode = new GroupModel(
      code,
      this.newSerivceGroupForm.get('name').value,
      this.newSerivceGroupForm.get('product').value,
      '1');

    this.groupsService.insertData(this.groupMode).subscribe(data => {
      this.mobjModalRef.hide();
      this.mnbrP = 1;
      this.getSearch(true);
      this.mstrServiceCode = '';
      this.mstrNewName = '';
      this.value1 = -1;
      this.createFormUpdate();
      this.showSuccess(this.translate.instant('management.group.message.insertSuccess'));
    });

  }


  get productId() {
    return this.mobjFormUpdate.get('productId');
  }

  get name() {
    return this.mobjFormUpdate.get('name');
  }

  get status() {
    return this.mobjFormUpdate.get('status');
  }

  callMessage(pnbrStatus, pstrMessage, plngtime?) {
    const vstrTitle = this.translate.instant('management.group.message.title');
    const vstrError = this.translate.instant('management.group.message.error');
    const vstrWarning = this.translate.instant('management.group.message.warning');
    const vobjConfig = {
      timeOut: plngtime ? plngtime : 5000,
      positionClass: 'toast-top-center',
    };
    if (pnbrStatus == -1) {
      this.toastr.error(pstrMessage, vstrError, vobjConfig);
    } else if (pnbrStatus == 0) {
      this.toastr.warning(pstrMessage, vstrWarning, vobjConfig);
    } else if (pnbrStatus == 1) {
      this.toastr.success(pstrMessage, vstrTitle, vobjConfig);
    }
  }

  showSuccess(message: string) {
    this.toastr.success(
      this.translate.instant('management.group.message.insertSuccess'),
      this.translate.instant('management.warningconfig.success'), {
      timeOut: 2000,
      positionClass: 'toast-top-center',
    });
  }

  validateInsert() {
    const vstrMessage = this.translate.instant('management.group.message.invalid');
    let vstrShowMessage = vstrMessage;
    this.mblnIsValid = true;
    if (!this.mnbrProductIdcbo) {
      this.mblnIsValid = false;
      this.mblnIsValidProductField = false;
      vstrShowMessage += this.translate.instant('management.group.message.product');
    } else {
      this.mblnIsValidProductField = true;
    }
    if (!this.mstrCodeGroup) {
      this.mblnIsValid = false;
      this.mblnIsValidCodeField = false;
      vstrShowMessage += vstrShowMessage ===
        vstrMessage ? this.translate.instant('management.group.message.code') : ', ' +
        this.translate.instant('management.group.message.code');
    } else {
      this.mblnIsValidCodeField = true;
    }

    if (!this.mstrNameGroup) {
      this.mblnIsValid = false;
      this.mblnIsValidNameField = false;
      vstrShowMessage += vstrShowMessage === vstrMessage ?
        this.translate.instant('management.group.message.name') : ', ' +
        this.translate.instant('management.group.message.name');
    } else {
      this.mblnIsValidNameField = true;
    }
    if (!this.mblnIsValid) {
      this.callMessage(0, vstrShowMessage, 5000);
    }
  }

  getDataToUpdateForm(pobjTemplate: TemplateRef<any>, pobjGroup: GroupModel) {
    this.confirm(pobjTemplate);
    this.mobjFormUpdate.setValue({
      id: pobjGroup.id,
      code: pobjGroup.code,
      name: pobjGroup.name,
      productId: pobjGroup.productId,
      changeDatetime1: pobjGroup.changeDatetime1,
      userUpdate: pobjGroup.userUpdate,
      status: pobjGroup.status,
    });
  }

  confirmUpdate(pobjTemplate: TemplateRef<any>, pobjForm) {
    this.confirm(pobjTemplate);
    const data = pobjForm.value;
    this.mobjdataForm = data;

  }

  confirmAdd(pobjTemplate: TemplateRef<any>, pobjForm) {
    this.confirm(pobjTemplate);
    const data = pobjForm.value;
    this.mobjdataForm = data;
  }

  update() {
    const vstrMessage = this.translate.instant('management.group.message.update');
    const vstrSuccessMessage = vstrMessage + this.translate.instant('management.group.message.success');
    const vstrFailMessage = vstrMessage + this.translate.instant('management.group.message.fail');
    const dataFormUpdate = this.mobjdataForm;
    this.groupsService.updateData(dataFormUpdate).subscribe(
      next => {
        this.callMessage(1, vstrSuccessMessage, 2000);
        this.getSearch(false);
      },
      error => (
        this.callMessage(-1, vstrFailMessage + '\n' + error.message, 5000), console.log(error)
      )
    );

    if (this.mobjModalRef) {
      this.mobjModalRef.hide();
    }

  }

  focusField(pstrField) {
    if (pstrField == 'productField') {
      this.mblnIsValidProductField = true;
    } else if (pstrField == 'codeField') {
      this.mblnIsValidCodeField = true;
    } else if (pstrField == 'nameField') {
      this.mblnIsValidNameField = true;
    }
  }

  closeModal(pobjTemplate?) {
    if (this.mobjModalRef) {
      this.mobjModalRef.hide();
    }
    if (pobjTemplate) {
      this.getDataToUpdateForm(pobjTemplate, this.mobjdataForm);
    }
  }

  downloadGroupTemplate() {
    this.fileName = this.translate.instant('management.group.fileName');
    this.groupsService.downloadGroupTemplate().subscribe(pobjData => {
      this.saveFile(pobjData.body, this.fileName);
    });
  }

  saveFile(pobjData: any, pstrFilename?: string) {
    const blob = new Blob([pobjData], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
    fileSaver.saveAs(blob, pstrFilename);
  }

  chectStatus() {
    this.sstatus = '0';
    const marr = this.marrDataTable.filter(item => item.cCheckBox === true);
    if (marr.length > 0) {
      this.sstatus = marr[0].status;
      for (let i = 1; i < marr.length; i++) {
        if (marr[i].status !== this.sstatus) {
          this.sstatus = '-1';
          break;
        }
      }
    } else {
      this.sstatus = '';
    }
  }

  selectAll() {
    let vblnCheck = false;
    if (this.cCheckAll === false) {
      vblnCheck = true;
    }
    for (let i = 0; i < this.marrDataTable.length; i++) {
      if (vblnCheck === true) {
        this.marrDataTable[i].cCheckBox = true;
      } else {
        this.marrDataTable[i].cCheckBox = false;
      }
    }
    this.chectStatus();
  }

  select(id: number) {
    for (let i = 0; i < this.marrDataTable.length; i++) {
      if (this.marrDataTable[i].id === id) {
        if (this.marrDataTable[i].cCheckBox === true) {
          this.marrDataTable[i].cCheckBox = false;
        } else {
          this.marrDataTable[i].cCheckBox = true;
        }
        break;
      }
    }
    let vblncheck = true;
    for (let i = 0; i < this.marrDataTable.length; i++) {
      if (this.marrDataTable[i].cCheckBox === false) {
        vblncheck = false;
      }
    }
    if (vblncheck) {
      this.cCheckAll = true;
    } else {
      this.cCheckAll = false
        ;
    }
    this.chectStatus();
  }

  lock() {
    const varrGroupLock = this.marrDataTable.filter(item => item.cCheckBox === true);
    const varrId: string[] = [];
    for (let i = 0; i < varrGroupLock.length; i++) {
      varrId.push(varrGroupLock[i].id + '');
    }
    this.groupsService.lock(varrId).subscribe(res => {
      if (res.code === 200) {
        this.sstatus = '';
        this.getSearch(false);
        this.onBack();
        this.showSuccess1(res.data);
      } else {
        this.showErro1(res.data);
        this.sstatus = '';
        this.getSearch(false);
        this.onBack();
      }
    });
    this.cCheckAll = false;
  }

  unlock() {
    const varrGroupUnlock = this.marrDataTable.filter(item => item.cCheckBox === true);
    const varrId: string[] = [];
    for (let i = 0; i < varrGroupUnlock.length; i++) {
      varrId.push(varrGroupUnlock[i].id + '');
    }
    this.groupsService.unlock(varrId).subscribe(res => {
      if (res.code === 200) {
        this.sstatus = '';
        this.getSearch(false);
        this.onBack();
        this.showSuccess2(res.data);
      } else {
        this.sstatus = '';
        this.onBack();
        this.getSearch(false);
        this.showErro2(res.data);

      }
    });
    this.cCheckAll = false;
  }

  showSuccess1(message: string) {
    this.toastr.success(
      this.translate.instant('management.group.message.lockSuccess'),
      this.translate.instant('management.warningconfig.success'), {
      timeOut: 2000,
      positionClass: 'toast-top-center',
    });
  }

  showErro1(message: string) {
    this.toastr.error(message,
      this.translate.instant('management.group.message.lockfail'), {
      timeOut: 2000,
      positionClass: 'toast-top-center',
    });
  }

  showSuccess2(message: string) {
    this.toastr.success(
      this.translate.instant('management.group.message.unlockSuccess'),
      this.translate.instant('management.warningconfig.success'), {
      timeOut: 2000,
      positionClass: 'toast-top-center',
    });
  }

  showErro2(message: string) {
    this.toastr.error(message,
      this.translate.instant('management.group.message.unlockfail'), {
      timeOut: 2000,
      positionClass: 'toast-top-center',
    });
  }

  getSearch(check: boolean) {
    this.cCheckAll = false;
    this.sstatus = '';
    let groupModel;
    let status;
    let productId;
    if (this.searchStatus === '-1') {
      status = null;
    } else {
      status = this.searchStatus;
    }
    if (this.mnbrSProductIdcbo === -1) {
      productId = null;
    } else {
      productId = this.mnbrSProductIdcbo;
    }
    let code = "";
    if (this.mstrSCodeGroup && this.mstrSCodeGroup.length > 0) {
      code = this.mstrSCodeGroup.toUpperCase();
    }
    groupModel = new GroupModel(code, this.mstrSNameGroup, productId, status);
    if (check === true) {
      this.mnbrP = 1;
    }
    this.groupsService.getSearch(groupModel, this.mnbrP, this.mnbrPageSize).subscribe(res => {
      this.marrDataTable = res['data'];
      this.mnbrTotal = res['totalRow'];
      this.marrDataTable.forEach(mobj => {
        mobj.cCheckBox = false;
      });
    });
    this.mobjPager = new Pager(this.mnbrP, this.mnbrPageSize);
  }

  createNewSerivceGroupForm() {
    this.newSerivceGroupForm = this.fb.group({
      product: [null, Validators.required],
      code: ['', [Validators.required, Validators.pattern('^\\s{0,10}?[_A-Za-z0-9]{1,50}\\s{0,10}$')]],
      name: ['', Validators.required],
      status: ['1']
    });
  }

  hasError(controlName: string, errorName: string) {
    return this.newSerivceGroupForm.controls[controlName].hasError(errorName);
  }

  create() {

  }

  checkCodeConflict() {
    this.groupsService.getAllGroupService().subscribe((res: any) => {
      for (let i = 0; i < res.data.length; i++) {
        if (this.mstrServiceCode === res.data[i].code) {
          this.conflictCode = true;
          break;
        } else {
          this.conflictCode = false;
        }
      }
    });
  }

  openDialog(groupsv: GroupModel, h: boolean): void {
    const dialogRef = this.dialog.open(DialogGroupComponent, {
      data: {
        products: this.marrNodeTreeviewServices,
        groupservice: groupsv,
        h: h,
      },
      maxWidth: '85vw',
      maxHeight: '85vh',
      width: '77vw',
    });
    // dialogRef.afterClosed().subscribe(result => {
    //   if (h) {
    //     setTimeout(() => {
    //     }, 150);
    //   } else {
    //     setTimeout(() => {
    //       this.getSearch(false);
    //     }, 150);
    //   }

    // });
  }

  openUpdate(w: GroupModel) {
    const vdialog = this.dialog.open(DialogGroupComponent, {
      data: {},
      maxWidth: '75vw',
      maxHeight: '85vh',
      width: '70vw',
    });
  }

  // toggleOrder() {
  //   this.order = !this.order;
  //   if(this.order){
  //     this.marrDataTable.sort(this.compareValues('productName'));
  //   }else{
  //     this.marrDataTable.sort(this.compareValues('productName','desc'));
  //   }
  //
  // }
  // compareValues(key, order = 'asc') {
  //   return (a, b) => {
  //     if (!a.hasOwnProperty(key) || !b.hasOwnProperty(key)) {
  //       return 0;
  //     }
  //     const varA = (typeof a[key] === 'string') ? a[key].toUpperCase() : a[key];
  //     const varB = (typeof b[key] === 'string') ? b[key].toUpperCase() : b[key];
  //     let comparison;
  //       comparison = varA.localeCompare(varB, 'vi');
  //     return (
  //       (order == 'desc') ? (comparison * -1) : comparison
  //     );
  //   };
  // }
}
