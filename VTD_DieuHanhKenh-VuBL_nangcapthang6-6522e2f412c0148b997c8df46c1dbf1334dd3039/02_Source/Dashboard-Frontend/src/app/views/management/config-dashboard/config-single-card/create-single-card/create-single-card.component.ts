import {Component, Inject, OnInit, TemplateRef} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';
import {ConfigSingleCardService} from '../../../../../services/management/config.single.card.service';
import {ServiceService} from '../../../../../services/management/service.service';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ConfigCardSize, ConfigSingleCardModel} from '../../../../../models/config.single.card.model';
import {ServiceModel} from '../../../../../models/service.model';
import {BsModalRef, BsModalService} from 'ngx-bootstrap';
import {ConfigGroupCardService} from '../../../../../services/management/config.group.card.service';
import {ConfigGroupCardModel} from '../../../../../models/config.group.card.model';
import {ToastrService} from 'ngx-toastr';
import {TranslateService} from '@ngx-translate/core';
import {config} from '../../../../../config/application.config';
import {TreeItem, TreeviewItem} from 'ngx-treeview';
import {WarningSendService} from '../../../../../services/management/warning-send.service';
import {WarningReceiveService} from '../../../../../services/management/warning-receive.service';

@Component({
  selector: 'app-create-single-card',
  templateUrl: './create-single-card.component.html',
  styleUrls: ['./create-single-card.component.scss']
})
export class CreateSingleCardComponent implements OnInit {

  constructor(@Inject(MAT_DIALOG_DATA) public data: any,
              private dialogRef: MatDialogRef<CreateSingleCardComponent>,
              private serviceService: ServiceService,
              private fb: FormBuilder,
              private modalService: BsModalService,
              private translate: TranslateService,
              private toastr: ToastrService,
              private warningReceiveSv: WarningReceiveService,
              private warningSendService: WarningSendService,
              private singleCardService: ConfigSingleCardService,
              private groupCardService: ConfigGroupCardService) {
    this.formCreate();
  }

  isClosed = false;
  createSingleCardForm: FormGroup;
  added = false;
  listCardSize: ConfigCardSize[];
  listCardType: ConfigCardSize[];
  listGroupCard: ConfigGroupCardModel[];
  private mobjModalRef: BsModalRef;
  value = null;
  marrNodeTreeviewServices: TreeviewItem[] = [];
  mobjConfig = {
    hasFilter: true,
    hasCollapseExpand: true,
    maxHeight: 130,
  };
  singleCardModel: ConfigSingleCardModel = new ConfigSingleCardModel();
  marrData: ServiceModel[] = [];
  mobjService: ServiceModel;
  marrItemServices: ServiceModel[] = [];
  mobjNodeTreeviewService: TreeviewItem;
  mobjItemService: ServiceModel;
  marrIndexNode = [];
  mobjNodeItemService: TreeItem;
  marrNodeItemServices: TreeItem[] = [];
  isPreSpace = false;

  validation_messages = {
    'cardName': [
      {type: 'required', message: this.translate.instant('management.group.table.validate.notNull')}
    ],
    'cardType': [
      {type: 'required', message: this.translate.instant('management.group.table.validate.notNull')}
      ],
    'cardSize': [
      {type: 'required', message: this.translate.instant('management.group.table.validate.notNull')}
    ],
    'groupParent': [
      {type: 'required', message: this.translate.instant('management.group.table.validate.notNull')}
    ],
    'drillDown': [
      {type: 'required', message: this.translate.instant('management.group.table.validate.notNull')}
    ],
    'cardZoom': [
      {type: 'required', message: this.translate.instant('management.group.table.validate.notNull')}
    ]
  };

  ngOnInit() {
    this.getCardSize();
    this.getCardType();
    this.getGroupCard();
    this.getService();
  }

  close() {
    this.isClosed = true;
    this.dialogRef.close();
    this.createSingleCardForm.reset();
    if (this.added) {
      this.serviceService.setReloadWarning(this.isClosed);
    }
  }

  getCardSize() {
    this.singleCardService.getCardSizeAPI('size').subscribe(
      dataCardSize => {
        this.listCardSize = dataCardSize['data'];
      }
    );
  }

  getCardType() {
    this.singleCardService.getCardSizeAPI('card_type').subscribe(
      dataCardType => {
        this.listCardType = dataCardType['data'];
      }
    );
  }

  getGroupCard() {
    this.singleCardService.getGroupCardOrderName().subscribe(
      dataGroupCard => {
        this.listGroupCard = dataGroupCard['data'];
      }
    );
  }

  clickSave(pobjTemplate: TemplateRef<any>) {
    this.mobjModalRef = this.modalService.show(pobjTemplate, {
        ignoreBackdropClick: true
      }
    );
  }

  onBack() {
    this.mobjModalRef.hide();
  }

  formCreate() {
    this.createSingleCardForm = this.fb.group({
      cardName: ['', {validators: [Validators.required]}],
      cardSize: [null, {validators: [Validators.required]}],
      groupParent: [null, {validators: [Validators.required]}],
      cardType: [null, {validators: [Validators.required]}],
      cardZoom: [null, {validators: [Validators.required]}],
      cardService: [''],
      drillDown: [null, {validators: [Validators.required]}],
      groupChild: ['null'],
      cardStatus: {value: 1, disabled: true}
    });
  }

  saveSingleCard() {
    let cardName;
    if (this.createSingleCardForm.controls['cardName'].value === '') {
      cardName = null;
    } else {
      cardName = this.createSingleCardForm.controls['cardName'].value;
    }
    const cardType = this.createSingleCardForm.controls['cardType'].value === 'null' ? null :
      this.createSingleCardForm.controls['cardType'].value;
    const cardSize = this.createSingleCardForm.controls['cardSize'].value === 'null' ? null :
      this.createSingleCardForm.controls['cardSize'].value;
    const groupParent = this.createSingleCardForm.controls['groupParent'].value === 'null' ? null :
      (this.createSingleCardForm.controls['groupParent'].value === '0' ? 0 : this.createSingleCardForm.controls['groupParent'].value);
    const cardZoom = this.createSingleCardForm.controls['cardZoom'].value;
    let cardService;
    if (this.createSingleCardForm.controls['cardService'].value === 'null') {
      cardService = null;
    } else {
      cardService = this.singleCardModel.serviceId;
    }
    const drillDown = this.createSingleCardForm.controls['drillDown'].value === 'null' ? null :
      (this.createSingleCardForm.controls['drillDown'].value === '0' ? 0 : 1);
    const groupChild = this.createSingleCardForm.controls['groupChild'].value === 'null' ? null :
      (this.createSingleCardForm.controls['groupChild'].value === '0' ? 0 : this.createSingleCardForm.controls['groupChild'].value);
    const cardStatus = this.createSingleCardForm.controls['cardStatus'].value === 'null' ? null :
      (this.createSingleCardForm.controls['cardStatus'].value === '0' ? 0 : 1);

    const modelCreate: ConfigSingleCardModel = new ConfigSingleCardModel(null,
      cardName, null, cardSize, null, drillDown, null, groupChild, cardType, groupParent, cardService, cardStatus, cardZoom);
    this.singleCardService.createSingleCard(modelCreate).subscribe(
      data => {
        if (data['code'] === 500) {
          this.dialogRef.close();
          this.toastr.error(this.translate.instant('management.warningconfig.serverError'),
            this.translate.instant('management.weight.infor'), {
              timeOut: 3000,
              positionClass: 'toast-top-center',
            });
        } else {
          if (data['data'] === 'Success') {
            this.showSuccess(this.translate.instant('management.weight.addOk'));
            this.createSingleCardForm.controls['cardName'].reset();
            this.createSingleCardForm.controls['cardType'].reset();
            this.createSingleCardForm.controls['cardSize'].reset();
            this.createSingleCardForm.controls['groupParent'].reset();
            this.createSingleCardForm.controls['cardZoom'].reset();
            this.value = -1;
            this.createSingleCardForm.controls['drillDown'].reset();
            this.createSingleCardForm.controls['groupChild'].reset();
            this.createSingleCardForm.controls['cardStatus'].reset();
            this.added = true;
            this.warningReceiveSv.setReloadWarning(1);
          } else if (data['data'] === 'DUPLICATE_GROUP') {
            this.showError(this.translate.instant('management.single-card.duplicate-group'));
          } else {
            this.showError(this.translate.instant('management.warningconfig.duplicate'));
          }
        }
        this.mobjModalRef.hide();
      }
    );
  }

  showError(message: string) {
    this.toastr.error(message,
      this.translate.instant('management.weight.infor'), {
      timeOut: 3000,
        positionClass: 'toast-top-center'
      });
  }

  showSuccess(message: string) {
    this.toastr.success(message,
      this.translate.instant('management.service.message.success'), {
        timeOut: 2000,
        positionClass: 'toast-top-center',
      });
  }

  onValueChange(value: number) {
    this.singleCardModel.serviceId = value;
  }

  getService() {
    this.marrNodeTreeviewServices = [];
    this.marrData = [];
    this.mobjService = new ServiceModel(null, null);
    this.warningSendService.getAllService().subscribe(
      vobjNext => {
        const ServiceAll: ServiceModel = new ServiceModel(null, null);
        ServiceAll.id = -1;
        if (localStorage.getItem(config.user_default_language) === 'en') {
          ServiceAll.name = 'Select';
        } else {
          ServiceAll.name = 'Chọn';
        }
        this.marrData.push(ServiceAll);
        let marr: ServiceModel[] = [];
        marr = vobjNext.data;
        for (let i = 0; i < marr.length; i++) {
          this.marrData.push(marr[i]);
        }
        this.marrItemServices = this.createNodeService(this.marrItemServices, this.marrData);
        this.createTreeService(this.marrItemServices, this.marrData);
        this.marrItemServices.forEach(vobjValue => {
          this.mobjNodeTreeviewService = new TreeviewItem(this.forwardData(vobjValue, this.mobjNodeItemService, this.marrNodeItemServices));
          this.marrNodeTreeviewServices.push(this.mobjNodeTreeviewService);
        });
        this.value = -1;
      },
      vobjErorr => { console.log('no data'); }
    );
  }

  createNodeService(parrItems: ServiceModel[] = [], parrDataTree: ServiceModel[]) {
    parrItems = parrDataTree.map(vobjValue => {
      this.mobjItemService = vobjValue;
      this.mobjItemService.children = [];
      return this.mobjItemService;
    });
    return parrItems;
  }

  createTreeService(parrNodeTrees: ServiceModel[], parrDataTree: ServiceModel[]) {
    const vnbrLen = parrNodeTrees.length;
    for (let i = 0; i < vnbrLen; i++) {
      for (let j = 0; j < parrDataTree.length; j++) {
        if (parrNodeTrees[i].id === parrDataTree[j].parentId) {
          parrNodeTrees[i].children.push(parrDataTree[j]);
          this.marrIndexNode.push(j);
        }
      }
    }
    const vnbrC = (pnbrA: number, pnbrB: number) => (pnbrA - pnbrB);
    this.marrIndexNode.sort(vnbrC);
    for (let vnbrI = this.marrIndexNode.length - 1; vnbrI >= 0; vnbrI--) {
      parrNodeTrees.splice(this.marrIndexNode[vnbrI], 1);
    }
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
        text: pobjNodeTree.name,
        children: parrItems,
        checked: false
      };
    } else {
      parrItems = [];
      pobjitem = {
        value: pobjNodeTree.id,
        text: pobjNodeTree.name,
        children: null,
        checked: false
      };
    }
    return pobjitem;
  }

  checkSpace(event) {
    if ((event.target.value || '').trim().length === 0) {
      this.isPreSpace = true;
    } else {
      this.isPreSpace = false;
    }
  }
}
