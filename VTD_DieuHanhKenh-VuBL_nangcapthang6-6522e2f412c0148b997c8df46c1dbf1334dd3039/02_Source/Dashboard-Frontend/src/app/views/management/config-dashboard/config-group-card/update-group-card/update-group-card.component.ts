import {Component, Inject, OnInit, TemplateRef} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {ServiceService} from '../../../../../services/management/service.service';
import {ConfigGroupCardModel, GroupCardCycle} from '../../../../../models/config.group.card.model';
import {ConfigGroupCardService} from '../../../../../services/management/config.group.card.service';
import {TreeItem, TreeviewConfig, TreeviewItem} from 'ngx-treeview';
import {UnitModel} from '../../../../../models/unit.model';
import {BsModalRef, BsModalService} from 'ngx-bootstrap';
import {ConfigSingleChartModel} from '../../../../../models/ConfigSingleChart.model';
import {ToastrService} from 'ngx-toastr';
import {TranslateService} from '@ngx-translate/core';
import {Partner} from '../../../../../models/Partner';
import * as _moment from 'moment';
import {ShopUnitDTO} from '../../../../../models/shopUnit.model';
import {DVT} from '../../../../../models/dvt.model';
import {config} from '../../../../../config/application.config';
import {DashboardModel} from '../../../../../models/dashboard.model';
import {WarningReceiveService} from '../../../../../services/management/warning-receive.service';
import {VdschannelService} from '../../../../../services/management/vdschannel.service';

@Component({
  selector: 'app-update-group-card',
  templateUrl: './update-group-card.component.html',
  styleUrls: ['./update-group-card.component.scss']
})
export class UpdateGroupCardComponent implements OnInit {

  isClosed = false;
  updateGroupCardForm: FormGroup;
  added = false;
  searchDefaultCycle = "day";
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
  vdsChannelName;
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
  groupId;
  groupCode;
  groupName;
  defaultCycle;
  newVdsChannelCode;
  newShopCode;
  modalRef: BsModalRef;
  isAllSpace = false;
  validation_messages = {
    'groupName': [
      {type: 'required', message: this.translate.instant('management.group.table.validate.notNull')}
    ]
  };
  clickShop = false;
  mblnCheckShop = null;
  checkShopNotNull = null;
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
              private dialogRef: MatDialogRef<UpdateGroupCardComponent>,
              private serviceService: ServiceService,
              private groupCardService: ConfigGroupCardService,
              private fb: FormBuilder,
              private modalService: BsModalService,
              private toast: ToastrService,
              private translate: TranslateService,
              private warningReceiveService: WarningReceiveService,
              private vdsChannelService: VdschannelService,
              private warningReceiveSv: WarningReceiveService) {
  }

  ngOnInit() {
    this.clickToShop();
    this.getGroupCardCycle();
    this.getPartner();
    this.getDataUpdate();
    this.vdsChannelService.getAllChannel().subscribe(res => {
      this.channels = res.data;
    });

    this.formUpdate();
  }

  getGroupCardCycle() {
    this.groupCardService.getDefaultCycle('DEFAULT_CYCLE').subscribe(
      dataDefaultCycle => {
        this.listDefaultCycle = dataDefaultCycle['data'];
      }
    );
  }

  close() {
    this.isClosed = true;
    this.dialogRef.close();
    this.updateGroupCardForm.reset();
    if (this.added) {
      this.serviceService.setReloadWarning(this.isClosed);
    }
  }

  formUpdate() {
    this.updateGroupCardForm = this.fb.group({
      groupId: [''],
      // groupName: [this.groupName, {validators: [Validators.required], updateOn: 'blur'}],
      groupName: [this.groupName, [Validators.required]],
      defaultCycle: [this.defaultCycle, [Validators.required]],
      vdsChannelCode: [this.vdsChannelName],
      shopCode: [this.newShopCode]
    });
  }

  getDataUpdate() {
    this.groupId = this.data.groupId;
    this.groupCode = this.data.groupCode;
    this.groupName = this.data.groupName;
    this.defaultCycle = this.data.defaultCycle;
    this.newVdsChannelCode = this.data.vdsChannelCode;
    this.newShopCode = this.data.shopCode;
    this.vdsChannelName = this.data.vdsChannelName;
  }

  clickUpdate(edit: TemplateRef<any>) {
    this.modalRef = this.modalService.show(edit, {
      ignoreBackdropClick: true // click ra ngoai khong dong modal
    });
  }

  onBack() {
    this.modalRef.hide();
  }

  updateGroupCard() {
    let shopCode;
    let vdsChannelCode;
    const groupId = this.groupId;
    const groupName = this.updateGroupCardForm.get('groupName').value;
    const defaultCycle = this.updateGroupCardForm.get('defaultCycle').value === 'null' ? null
      : this.updateGroupCardForm.get('defaultCycle').value;
    if (this.newShopCode === '-1') {
      shopCode = null;
    } else {
      shopCode = this.shopCode;
    }
    if (this.vdsChannelCode === 'null') {
      vdsChannelCode = null;
    } else {
      vdsChannelCode = this.vdsChannelCode;
    }
    // const vdsChannelCode = this.updateGroupCardForm.get('vdsChannelCode').value === '' ? null
    //   : this.updateGroupCardForm.get('vdsChannelCode').value;

    const groupCard = new ConfigGroupCardModel(groupId, null, null, groupName, defaultCycle, vdsChannelCode, shopCode, null, null);
    this.groupCardService.updateGroupCard(groupCard).subscribe(
      message => {
        if (message['data'] === 'SUCCESS') {
          // this.added = true;
          this.warningReceiveSv.setReloadWarning(0);
          this.showSuccess(this.translate.instant('management.warningconfig.updatesuccess'));
        } else if (message['data'] === 'DUPLICATE') {
          this.showError(this.translate.instant('management.group-card.duplicate'));
        } else {
          this.showError(this.translate.instant('management.warningconfig.serverError'));
        }
      }
    );
    this.onBack();
    this.closeDialog();
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

  closeDialog() {
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
    this.clickShop = true;
    this.checkShop();
  }

  checkShop() {
    if (this.clickShop == true) {
      if (this.shopCode == '-1' || this.shopCode == null) {
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
  //     this.updateGroupCardForm.controls['vdsChannelCode'].setValue(this.vdsChannelCode);
  //   } else {
  //     this.updateGroupCardForm.controls['vdsChannelCode'].setValue('');
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
      this.valueUnit = "-1";
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
    //   this.updateGroupCardForm.controls['vdsChannelCode'].setValue(this.vdsChannelCode);
    // } else {
    //   this.updateGroupCardForm.controls['vdsChannelCode'].setValue('');
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
    // this.updateGroupCardForm.controls['vdsChannelCode'].setValue(this.channel);
    this.checkShop();
  }

}
