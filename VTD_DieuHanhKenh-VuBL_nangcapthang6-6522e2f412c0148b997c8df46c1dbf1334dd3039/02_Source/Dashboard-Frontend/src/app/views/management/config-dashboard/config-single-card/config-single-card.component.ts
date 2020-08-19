import {Component, OnInit, TemplateRef} from '@angular/core';
import {ConfigCardSize, ConfigSingleCardModel} from '../../../../models/config.single.card.model';
import {ConfigSingleCardService} from '../../../../services/management/config.single.card.service';
import {ToastrService} from 'ngx-toastr';
import {config} from '../../../../config/application.config';
import {BsModalRef, BsModalService} from 'ngx-bootstrap';
import {TranslateService} from '@ngx-translate/core';
import {FormBuilder, FormGroup} from '@angular/forms';
import {ServiceService} from '../../../../services/management/service.service';
import {ServiceModel} from '../../../../models/service.model';
import {CreateSingleCardComponent} from './create-single-card/create-single-card.component';
import {MatDialog} from '@angular/material';
import {ConfigGroupCardService} from '../../../../services/management/config.group.card.service';
import {ConfigGroupCardModel} from '../../../../models/config.group.card.model';
import {TreeItem, TreeviewItem} from 'ngx-treeview';
import {WarningSendService} from '../../../../services/management/warning-send.service';
import {Partner} from '../../../../models/Partner';
import {UpdateSingleCardComponent} from './update-single-card/update-single-card.component';
import {WarningReceiveService} from '../../../../services/management/warning-receive.service';

@Component({
  selector: 'app-config-single-card',
  templateUrl: './config-single-card.component.html',
  styleUrls: ['./config-single-card.component.scss']
})
export class ConfigSingleCardComponent implements OnInit {

  cardName;
  groupParent;
  cardType;
  cardZoom;
  cardSize;
  drillDown;
  cardStatus;
  listCardSize: ConfigCardSize[];
  listSingleCard: ConfigSingleCardModel[];
  listCardType: ConfigCardSize[];
  listGroupCard: ConfigGroupCardModel[];
  pageSize = config.pageSize;
  currentP = 1;
  cardId;
  private mobjModalRef: BsModalRef;
  conditionSingleCard;
  message;
  formSingleCard: FormGroup;
  dataCreate;
  dataUpate;
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
  added: boolean;
  updated: boolean;

  constructor(private singleCardService: ConfigSingleCardService, private fb: FormBuilder,
              private modalService: BsModalService,
              private toastr: ToastrService,
              private translate: TranslateService,
              public dialog: MatDialog,
              private warningSendService: WarningSendService,
              private serviceService: ServiceService,
              private warningReceiveSv: WarningReceiveService) {
  }

  ngOnInit() {
    this.formSearch();
    this.search();
    this.getCardSize();
     // this.getAllSingleCard(1);
    this.getCardType();
    this.getGroupCard();
    this.getService();
    this.warningReceiveSv.reloadWarning$.subscribe(page => {
      if (page === 1) {
        this.currentP = 1;
      }
      this.search();
    });
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

  // getAllSingleCard(curr: number) {
  //   this.currentP = curr;
  //   this.singleCardService.getAllSingleCard().subscribe(
  //     dataAllSingleCard => {
  //       this.listSingleCard = dataAllSingleCard['data'];
  //     }
  //   );
  // }

  getGroupCard() {
    this.singleCardService.getGroupCardOrderName().subscribe(
      dataGroupCard => {
        this.listGroupCard = dataGroupCard['data'];
      }
    );
  }

  pageChange(value: number) {
    this.currentP = value;
  }

  formSearch() {
    this.formSingleCard = this.fb.group({
      cardName: [''],
      cardSize: ['null'],
      groupParent: ['null'],
      cardType: ['null'],
      cardZoom: ['null'],
      cardService: [null],
      drillDown: ['null'],
      groupChild: ['null'],
      cardStatus: ['null']
    });
  }

  removeSingleCard() {
    this.conditionSingleCard = new ConfigSingleCardModel(this.cardId);
    this.singleCardService.deleteSingleCard(this.conditionSingleCard).subscribe(
      deleteSingleCard => {
        this.message = deleteSingleCard['data'];
        if (this.message === 'SUCCESS') {
          this.showSuccess(this.translate.instant('management.menu.deleteSuccess'));
        } else if (this.message === 'ERROR') {
          this.showError(this.translate.instant('management.single-card.delete-access-deny'));
        } else {
          this.showError(this.translate.instant('management.warningconfig.serverError'));
        }
        this.mobjModalRef.hide();
        // this.getAllSingleCard(1);
        this.search();
      }
    );
  }

  openConfirm(pobjTemplate: TemplateRef<any>, id: number) {
    this.cardId = id;
    this.mobjModalRef = this.modalService.show(pobjTemplate, {
        ignoreBackdropClick: true
      }
    );
  }

  onBack() {
    this.mobjModalRef.hide();
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
      this.translate.instant('management.service.message.fail'), {
        timeOut: 2000,
        positionClass: 'toast-top-center',
      });
  }

  search() {
    let cardName;
    if (this.formSingleCard.controls['cardName'].value === '') {
      cardName = null;
    } else {
      cardName = this.formSingleCard.controls['cardName'].value;
    }
    const cardType = this.formSingleCard.controls['cardType'].value === 'null' ? null :
      this.formSingleCard.controls['cardType'].value;
    const cardSize = this.formSingleCard.controls['cardSize'].value === 'null' ? null :
      this.formSingleCard.controls['cardSize'].value;
    const groupParent = this.formSingleCard.controls['groupParent'].value === 'null' ? null :
      (this.formSingleCard.controls['groupParent'].value === '0' ? 0 : this.formSingleCard.controls['groupParent'].value);
    const cardZoom = this.formSingleCard.controls['cardZoom'].value === 'null' ? null :
      (this.formSingleCard.controls['cardZoom'].value === '0' ? 0 : 1);
    let cardService;
    if (this.formSingleCard.controls['cardService'].value === 'null' || this.formSingleCard.controls['cardService'].value === '-1') {
      cardService = null;
    } else {
      cardService = this.singleCardModel.serviceId;
    }
    const drillDown = this.formSingleCard.controls['drillDown'].value === 'null' ? null :
      (this.formSingleCard.controls['drillDown'].value === '0' ? 0 : 1);
    const groupChild = this.formSingleCard.controls['groupChild'].value === 'null' ? null :
      (this.formSingleCard.controls['groupChild'].value === '0' ? 0 : this.formSingleCard.controls['groupChild'].value);
    const cardStatus = this.formSingleCard.controls['cardStatus'].value === 'null' ? null :
      (this.formSingleCard.controls['cardStatus'].value === '0' ? 0 : 1);

    const modelSearch: ConfigSingleCardModel = new ConfigSingleCardModel(null,
      cardName, null, cardSize, null, drillDown, null, groupChild, cardType, groupParent, cardService, cardStatus, cardZoom);
    this.singleCardService.searchSingleCard(modelSearch).subscribe(
      dataSearch => {
        this.listSingleCard = dataSearch['data'];
      }
    );
  }

  fetchAdded() {
    this.singleCardService.service$.subscribe(
      dataAdd => {
        this.added = dataAdd;
      }
    );
  }

  showModalAdd() {
    const dialogRef = this.dialog.open(CreateSingleCardComponent, {
      maxWidth: '85vw',
      maxHeight: '95vh',
      width: '80vw',
      data: {
        dataCreate: this.dataCreate
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      this.serviceService.reloadWarning$.subscribe(res => {
        if (res) {
        }
      });
    });
  }

  showModalUpdate(row: ConfigSingleCardModel) {
    const dialogRef = this.dialog.open(UpdateSingleCardComponent, {
      maxWidth: '85vw',
      maxHeight: '95vh',
      width: '80vw',
      data: {
        cardId: row.cardId,
        cardName: row.cardName,
        cardType: row.cardType,
        cardSize: row.cardSize,
        drillDown: row.drilldown,
        drillDownObjectId: row.drillDownObjectId,
        groupId: row.groupId,
        serviceId: row.serviceId,
        status: row.status,
        zoom: row.zoom
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      this.serviceService.reloadWarning$.subscribe(res => {
        if (!res) {
        }
      });
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
      vobjErorr => {
      }
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
}
