import {Component, Inject, OnInit, TemplateRef} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ServiceService} from '../../../../../services/management/service.service';
import {ConfigGroupCardService} from '../../../../../services/management/config.group.card.service';
import {ConfigGroupCardModel} from '../../../../../models/config.group.card.model';
import {ConfigSingleCardService} from '../../../../../services/management/config.single.card.service';
import {ConfigCardSize, ConfigSingleCardModel} from '../../../../../models/config.single.card.model';
import {BsModalRef, BsModalService} from 'ngx-bootstrap';
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from 'ngx-toastr';
import {ServiceModel} from '../../../../../models/service.model';
import {config} from '../../../../../config/application.config';
import {TreeItem, TreeviewItem} from 'ngx-treeview';
import {WarningSendService} from '../../../../../services/management/warning-send.service';
import {WarningReceiveService} from '../../../../../services/management/warning-receive.service';

@Component({
  selector: 'app-update-single-card',
  templateUrl: './update-single-card.component.html',
  styleUrls: ['./update-single-card.component.scss']
})
export class UpdateSingleCardComponent implements OnInit {

  constructor(private dialogRef: MatDialogRef<UpdateSingleCardComponent>,
              private serviceService: ServiceService,
              private groupCardService: ConfigGroupCardService,
              private singleCardService: ConfigSingleCardService,
              private modalService: BsModalService,
              private fb: FormBuilder,
              private warningReceiveSv: WarningReceiveService,
              private translate: TranslateService,
              private toastr: ToastrService,
              private warningSendService: WarningSendService,
              @Inject(MAT_DIALOG_DATA) public data: any,
  ) { }

  isClosed = false;
  updateSingleCardForm: FormGroup;
  added = false;
  listGroupCard: ConfigGroupCardModel[];
  listCardType: ConfigCardSize[];
  listCardSize: ConfigCardSize[];
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
    ]
  };

  ngOnInit() {
    this.getGroupCard();
    this.getCardType();
    this.getCardSize();
    this.formUpdate();
    this.getService();

  }

  close() {
    this.isClosed = true;
    this.dialogRef.close();
    this.updateSingleCardForm.reset();
    if (this.added) {
      this.serviceService.setReloadWarning(this.isClosed);
    }
  }

  getGroupCard() {
    this.singleCardService.getGroupCardOrderName().subscribe(
      dataGroupCard => {
        this.listGroupCard = dataGroupCard['data'];
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

  getCardSize() {
    this.singleCardService.getCardSizeAPI('size').subscribe(
      dataCardSize => {
        this.listCardSize = dataCardSize['data'];
      }
    );
  }

  onBack() {
    this.mobjModalRef.hide();
  }

  clickUpdate(pobjTemplate: TemplateRef<any>) {
    this.mobjModalRef = this.modalService.show(pobjTemplate, {
        ignoreBackdropClick: true
      }
    );
  }

  formUpdate() {
    this.updateSingleCardForm = this.fb.group({
      cardId: [this.data.cardId],
      cardName: [this.data.cardName, {validators: [Validators.required]}],
      cardSize: [this.data.cardSize, {validators: [Validators.required]}],
      groupParent: [this.data.groupId, {validators: [Validators.required]}],
      cardType: [this.data.cardType, {validators: [Validators.required]}],
      cardZoom: [this.data.zoom, {validators: [Validators.required]}],
      cardService: [this.data.serviceId],
      drillDown: [this.data.drillDown],
      groupChild: [this.data.drillDownObjectId],
      cardStatus: [this.data.status, {validators: [Validators.required]}]
    });
  }

  showSuccess(message: string) {
    this.toastr.success(message,
      this.translate.instant('management.service.message.success'), {
        timeOut: 2000,
        positionClass: 'toast-top-center',
      });
  }

  showError(message: string) {
    this.toastr.error(message,
      this.translate.instant('management.weight.infor'), {
        timeOut: 3000,
        positionClass: 'toast-top-center',
      });
  }

  saveUpdate() {
    let cardName;
    if (this.updateSingleCardForm.controls['cardName'].value === '') {
      cardName = null;
    } else {
      cardName = this.updateSingleCardForm.controls['cardName'].value;
    }
    const cardType = this.updateSingleCardForm.controls['cardType'].value === 'null' ? null :
      this.updateSingleCardForm.controls['cardType'].value;
    const cardSize = this.updateSingleCardForm.controls['cardSize'].value === 'null' ? null :
      this.updateSingleCardForm.controls['cardSize'].value;
    const groupParent = this.updateSingleCardForm.controls['groupParent'].value === 'null' ? null :
      (this.updateSingleCardForm.controls['groupParent'].value === '0' ? 0 : this.updateSingleCardForm.controls['groupParent'].value);
    const cardZoom = this.updateSingleCardForm.controls['cardZoom'].value;
    let cardService;
    if (this.updateSingleCardForm.controls['cardService'].value === 'null') {
      cardService = null;
    } else {
      cardService = this.singleCardModel.serviceId;
    }
    const drillDown = this.updateSingleCardForm.controls['drillDown'].value;
    const groupChild = this.updateSingleCardForm.controls['groupChild'].value === 'null' ? null :
      (this.updateSingleCardForm.controls['groupChild'].value === '0' ? 0 : this.updateSingleCardForm.controls['groupChild'].value);
    const cardStatus = this.updateSingleCardForm.controls['cardStatus'].value;

    const modelUpdate: ConfigSingleCardModel = new ConfigSingleCardModel(this.data.cardId,
      cardName, null, cardSize, null, drillDown, null, groupChild, cardType, groupParent, cardService, cardStatus, cardZoom);
    this.singleCardService.updateSingleCard(modelUpdate).subscribe(
      data => {
        if (data['code'] === 500) {
          this.dialogRef.close();
          this.showError(this.translate.instant('management.warningconfig.serverError'));
        } else {
          if (data['data'] === 'SUCCESS') {
            this.showSuccess(this.translate.instant('management.warningconfig.updatesuccess'));
            this.warningReceiveSv.setReloadWarning(0);
          } else if (data['data'] === 'DUPLICATE_GROUP') {
            this.showError(this.translate.instant('management.single-card.duplicate-group'));
          } else {
            this.showError(this.translate.instant('management.warningconfig.duplicate'));
          }
        }
        this.added = false;
        this.singleCardService.setService(this.added);
        this.mobjModalRef.hide();
        this.close();
      }
    );
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
        this.value = this.data.serviceId;
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
