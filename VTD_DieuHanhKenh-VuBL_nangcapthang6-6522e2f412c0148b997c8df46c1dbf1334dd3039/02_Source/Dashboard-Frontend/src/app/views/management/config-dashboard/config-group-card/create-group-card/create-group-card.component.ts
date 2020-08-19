import {Component, Inject, OnInit, TemplateRef} from '@angular/core';
import {ConfigGroupCardService} from '../../../../../services/management/config.group.card.service';
import {ConfigGroupCardModel, GroupCardCycle} from '../../../../../models/config.group.card.model';
import {TreeItem, TreeviewConfig, TreeviewItem} from 'ngx-treeview';
import {UnitModel} from '../../../../../models/unit.model';
import {ServiceService} from '../../../../../services/management/service.service';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {BsModalRef, BsModalService} from 'ngx-bootstrap';
import {ConfigSingleCardModel} from '../../../../../models/config.single.card.model';
import {ToastrService} from 'ngx-toastr';
import {TranslateService} from '@ngx-translate/core';
import {Partner} from '../../../../../models/Partner';
import * as _moment from 'moment';
import {ShopUnit, ShopUnitDTO} from '../../../../../models/shopUnit.model';
import {DVT} from '../../../../../models/dvt.model';
import {config} from '../../../../../config/application.config';
import {DashboardModel} from '../../../../../models/dashboard.model';
import {WarningReceiveService} from '../../../../../services/management/warning-receive.service';
import {VdschannelService} from '../../../../../services/management/vdschannel.service';

@Component({
  selector: 'app-create-group-card',
  templateUrl: './create-group-card.component.html',
  styleUrls: ['./create-group-card.component.scss']
})
export class CreateGroupCardComponent implements OnInit {

  listDefaultCycle: GroupCardCycle[];
  nodeTreeView: TreeviewItem;
  nodeTreeViews: TreeviewItem[] = [];
  nodeItem: TreeItem;
  nodeItems: TreeItem[] = [];
  dataUnit = [];
  dataTree = [];
  nodeTrees: UnitModel[] = [];
  unitIitem: UnitModel;
  value;
  marrIndexNodeService = [];
  mobjNodeItemService: TreeItem;
  showAddButton = true;
  shopCode;
  vdsChannelCode;
  staffTree: TreeviewItem;
  choseText;
  staffs: any[] = [];
  staffTreeChose;
  staffTrees: TreeviewItem[] = [];
  mobjConfigScore = TreeviewConfig.create({
    hasFilter: true,
    hasCollapseExpand: false,
    maxHeight: 85,
  });
  isClosed = false;
  createGroupCardForm: FormGroup;
  added = false;
  private mobjModalRef: BsModalRef;
  createSingleCardForm: FormGroup;
  searchDefaultCycle = "null";
  listGroupCard: ConfigGroupCardModel[];
  isAllSpace = false;
  validation_messages = {
    'groupName': [
      {type: 'required', message: this.translate.instant('management.group.table.validate.notNull')}
    ],
    'shopCode': [
      {type: 'required', message: this.translate.instant('management.group.table.validate.notNull')}
    ]
  };
  mblnGroupName: boolean;
  mblnCheckService;
  clickService;
  newShopUnit: ShopUnit = new ShopUnit();
  clickShop = false;
  mblnCheckShop = null;
  isAllSpaceOne = false;

  marrParther: Partner[] = [];
  nodeTreeViewUnit: TreeviewItem;
  nodeTreeViewsUnit: TreeviewItem[] = [];
  marrIndexNodeUnit = [];
  dataTreeUnit = [];
  dataUnitUnit = [];
  nodeTreesUnit: UnitModel[] = [];
  nodeItemUnit: TreeItem;
  nodeItemsUnit: TreeItem[] = [];
  unitIitemUnit: UnitModel;
  mobjNodeItemUnit: TreeItem;
  TreeShopCode: TreeviewItem[] = [];
  valueUnit = null;
  groupIdUnit;
  cycleId;
  shopCodeUnit;
  dashModelUnit;
  date = new FormControl(_moment());
  marrNodeTreeviewServices: TreeviewItem[] = [];
  shopunitDTO: ShopUnitDTO = new ShopUnitDTO();
  channel: string;
  channels = [];
  units: DVT[] = [];
  unitAll: DVT[] = [];

  constructor(@Inject(MAT_DIALOG_DATA) public data: any,
              private groupCardService: ConfigGroupCardService,
              private serviceService: ServiceService,
              private dialogRef: MatDialogRef<CreateGroupCardComponent>,
              private modalService: BsModalService,
              private toastr: ToastrService,
              private translate: TranslateService,
              private fb: FormBuilder,
              private warningReceiveService: WarningReceiveService,
              private vdsChannelService: VdschannelService,
              private toast: ToastrService,
              private warningReceiveSv: WarningReceiveService) {
    this.formCreate();
  }

  ngOnInit() {
    this.getGroupCardCycle();
    this.getPartner();
    this.getAllGroupCard();

    this.vdsChannelService.getAllChannel().subscribe(res => {
      this.channels = res.data;
    });
  }

  getGroupCardCycle() {
    this.groupCardService.getDefaultCycle('DEFAULT_CYCLE').subscribe(
      dataDefaultCycle => {
        this.listDefaultCycle = dataDefaultCycle['data'];
      }
    );
  }

  getAllGroupCard() {
    this.groupCardService.getAllGroupCard().subscribe(
      dataAllGroupCard => {
        this.listGroupCard = dataAllGroupCard['data'];
      }
    );
  }

  close() {
    this.isClosed = true;
    this.dialogRef.close();
    this.createGroupCardForm.reset();
    if (this.added) {
      this.serviceService.setReloadWarning(this.isClosed);
    }
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
    this.createGroupCardForm = this.fb.group({
      // groupName: [null, {validators: [Validators.required], updateOn: 'blur'}],
      groupName: [null, [Validators.required]],
      defaultCycle: [null, [Validators.required]],
      vdsChannelCode: [null],
      shopCode: [null]
    });
  }

  saveGroupCard() {
    let groupName;
    let vdsChannelCode;
    let shopCode;
    if (this.createGroupCardForm.controls['groupName'].value === '') {
      groupName = null;
    } else {
      groupName = this.createGroupCardForm.controls['groupName'].value;
    }
    const defaultCycle = this.createGroupCardForm.controls['defaultCycle'].value === 'null' ? null :
      this.createGroupCardForm.controls['defaultCycle'].value;
    // const shopCode: string = this.shopCode;
    if (this.shopCode === '-1') {
      shopCode = null;
    } else {
      shopCode = this.shopCode;
    }
    if (this.vdsChannelCode === 'null') {
      vdsChannelCode = null;
    } else {
      vdsChannelCode = this.vdsChannelCode;
    }
    const modelCreate: ConfigGroupCardModel = new ConfigGroupCardModel(null, null, null, groupName, defaultCycle, vdsChannelCode, shopCode, null, null);
    this.groupCardService.addGroupCard(modelCreate).subscribe(
      data => {
        if (data['code'] === 500) {
          this.dialogRef.close();
          this.mobjModalRef.hide();
          this.toastr.error('lỗi server',
            this.translate.instant('management.weight.infor'), {
              timeOut: 3000,
              positionClass: 'toast-top-center',
            });
        } else if (data['data'] === 'DUPLICATE') {
          this.dialogRef.close();
          this.mobjModalRef.hide();
          this.showError(this.translate.instant('management.group-card.duplicate'));
        } else {
          this.showSuccess(this.translate.instant('management.weight.addOk'));
          this.added = true;
          this.mobjModalRef.hide();
          this.createGroupCardForm.reset();
          this.valueUnit = '-1';
          this.warningReceiveSv.setReloadWarning(1);
        }
      }
    );
  }

  showSuccess(message: string) {
    this.toastr.success(message,
      this.translate.instant('management.service.message.success'), {
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

  closeFromCreate() {
    this.dialogRef.close();
    this.added = false;
  }

  checkSpace(event) {
    if ((event.target.value || '').trim().length === 0) {
      this.isAllSpace = true;
    } else {
      this.isAllSpace = false;
    }
  }

  clickToShop() {
    this.added = false;
    this.clickShop = true;
    this.checkShop();
  }

  checkShop() {
    if (this.clickShop == true) {
      if (this.shopCode == '-1') {
        this.mblnCheckShop = true;
      } else {
        this.mblnCheckShop = false;
      }
    }
  }

  checkSpaceOne(event) {
    if ((event.target.value || '').trim().length === 0) {
      this.isAllSpaceOne = true;
    } else {
      this.isAllSpaceOne = false;
    }
  }

  // unitChange(value: string) {
  //   this.value = value;
  //   this.showAddButton = false;
  //   this.dataUnit.forEach(
  //     tree => {
  //       if (tree.shopCode === value) {
  //         this.shopCode = tree.shopCode;
  //         this.vdsChannelCode = tree.vdsChannelCode;
  //       }
  //     }
  //   );
  //   if (this.vdsChannelCode !== 'null') {
  //     this.createGroupCardForm.controls['vdsChannelCode'].setValue(this.vdsChannelCode);
  //   } else {
  //     this.createGroupCardForm.controls['vdsChannelCode'].setValue('');
  //   }
  // }

  getPartner() {
    this.warningReceiveService.getAllPartner().subscribe(res => {
      var vobjNewPartner1: Partner = new Partner();
      vobjNewPartner1.shopCode = "-1";
      if (localStorage.getItem(config.user_default_language) === 'en') {
        vobjNewPartner1.shopName = "Select";
      } else {
        vobjNewPartner1.shopName = "Chọn";
      }
      this.marrParther = res.data;
      this.marrParther = this.marrParther.reverse();
      this.marrParther.push(vobjNewPartner1);
      this.marrParther = this.marrParther.reverse();
      this.createTreeView();
      this.valueUnit = "-1"
    });
  }

  createTreeView() {
    try {
      this.nodeTreeViewsUnit = [];
      this.groupIdUnit = null;
      this.nodeTreeViewsUnit = [];
      this.dataUnitUnit = this.marrParther;
      this.dataTreeUnit = this.marrParther;
      this.nodeTreesUnit = this.createNodeUnit(this.nodeTreesUnit, this.dataTreeUnit, this.unitIitemUnit);
      this.dataTreeUnit = this.nodeTreesUnit;
      this.createTreeUnit(this.nodeTreesUnit, this.dataTreeUnit);
      this.nodeTreesUnit.forEach(valuess => {
        this.nodeTreeViewUnit = new TreeviewItem(this.forwardDataUnit(valuess, this.nodeItemUnit, this.nodeItemsUnit));
        this.nodeTreeViewsUnit.push(this.nodeTreeViewUnit);
      });
      for (let i = 0; i < this.nodeTreeViewsUnit.length; i++) {
        if (this.nodeTreeViewsUnit[i].children) {
          this.valueUnit = this.nodeTreeViewsUnit[i].value;
          this.dataUnitUnit.forEach(
            tree => {
              if (tree.shopCode === this.nodeTreeViewsUnit[i].value) {
                this.shopCodeUnit = tree.shopCode;
                this.dashModelUnit = new DashboardModel(this.groupIdUnit, null, this.date.value._d.getTime(), this.cycleId, this.shopCodeUnit);
                return;
              }
            }
          );
          break;
        }
      }
      this.TreeShopCode = this.nodeTreeViewsUnit;
      // this.getUnit();
    } catch (e) {
    }
  }

  createNodeUnit(parrItems: UnitModel[] = [], parrDataTree: UnitModel[], pobjItem: UnitModel) {
    pobjItem = null;
    parrItems = parrDataTree.map(value => {
      pobjItem = {
        id: value.id,
        shopName: value.shopName,
        parentShopCode: value.parentShopCode,
        shopCode: value.shopCode,
        shortName: value.shortName,
        children: [],
        groupId: value.groupId,
        vdsChannelCode: null
      };
      return pobjItem;
    });
    return parrItems;
  }

  createTreeUnit(parrNodeTrees: UnitModel[], parrDataTree: UnitModel[]) {
    const len = parrNodeTrees.length;
    for (let i = 0; i < len; i++) {
      for (let j = 0; j < len; j++) {
        if (parrNodeTrees[i].shopCode === parrDataTree[j].parentShopCode) {
          parrNodeTrees[i].children.push(parrDataTree[j]);
          this.marrIndexNodeUnit.push(j);
        }
      }
    }
    const c = (a: number, b: number) => (a - b);
    this.marrIndexNodeUnit.sort(c);
    for (let i = this.marrIndexNodeUnit.length - 1; i >= 0; i--) {
      parrNodeTrees.splice(this.marrIndexNodeUnit[i], 1);
    }
  }

  forwardDataUnit(pobjNodeTree: UnitModel, pobjitem: TreeItem, parrItems: TreeItem[] = []) {
    pobjitem = null;
    parrItems = [];
    if (pobjNodeTree.children) {
      pobjNodeTree.children.forEach(value => {
        this.mobjNodeItemUnit = this.forwardDataUnit(value, null, []);
        parrItems.push(this.mobjNodeItemUnit);
      });
    }
    if (pobjNodeTree.children) {
      pobjitem = {
        value: pobjNodeTree.shopCode,
        text: pobjNodeTree.shopName,
        children: parrItems
      };
    } else {
      parrItems = [];
      pobjitem = {
        value: pobjNodeTree.shopCode,
        text: pobjNodeTree.shopName,
        children: null
      };
    }
    return pobjitem;
  }

  onValueChangeUnit(value: string) {
    this.shopunitDTO.shopCode = value;
    this.marrParther.forEach(
      tree => {
        if (tree.shopCode === value) {
          this.shopCode = tree.shopCode;
          this.vdsChannelCode = tree.vdsChannelCode;
          // this.vdsChannelName = this.channelService.getNameByCode(tree.vdsChannelCode);
        }
      });

    // if (this.vdsChannelCode !== 'null') {
    //   this.createGroupCardForm.controls['vdsChannelCode'].setValue(this.vdsChannelCode);
    // } else {
    //   this.createGroupCardForm.controls['vdsChannelCode'].setValue('');
    // }

    if (value != '-1') {
      let partner = this.marrParther.filter(item => item.shopCode == value)[0];
      if (partner) {
        if (partner.vdsChannelCode == 'null') {
          partner.vdsChannelCode = null;
        }
        this.channel = this.channels.filter(item => item.code == partner.vdsChannelCode)[0].name;
      }
    } else {
      this.channel = '';
    }
    // this.formGroupCard.controls['vdsChannelCode'].setValue(this.channel);
    this.checkShop();
  }

}
