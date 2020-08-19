import {Component, EventEmitter, OnInit, Output, TemplateRef} from '@angular/core';
import {ConfigGroupCardModel, GroupCardCycle} from '../../../../models/config.group.card.model';
import {ConfigGroupCardService} from '../../../../services/management/config.group.card.service';
import {config} from '../../../../config/application.config';
import {TreeItem, TreeviewConfig, TreeviewItem} from 'ngx-treeview';
import {ServiceService} from '../../../../services/management/service.service';
import {UnitModel} from '../../../../models/unit.model';
import {BsModalRef, BsModalService} from 'ngx-bootstrap';
import {ToastrService} from 'ngx-toastr';
import {TranslateService} from '@ngx-translate/core';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {MatDialog} from '@angular/material';
import {CreateGroupCardComponent} from './create-group-card/create-group-card.component';
import {UpdateGroupCardComponent} from './update-group-card/update-group-card.component';
import {Partner} from '../../../../models/Partner';
import {DashboardModel} from '../../../../models/dashboard.model';
import * as _moment from 'moment';
import {ShopUnitDTO} from '../../../../models/shopUnit.model';
import {WarningReceiveService} from '../../../../services/management/warning-receive.service';
import {DVT} from '../../../../models/dvt.model';
import {ChannelService} from '../../../../services/management/channel.service';
import {Observable} from 'rxjs';
import {VdschannelService} from '../../../../services/management/vdschannel.service';


@Component({
  selector: 'app-config-group-card',
  templateUrl: './config-group-card.component.html',
  styleUrls: ['./config-group-card.component.scss']
})
export class ConfigGroupCardComponent implements OnInit {

  listDefaultCycle: GroupCardCycle[];
  listGroupCard: ConfigGroupCardModel[];
  pageSize = config.pageSize;
  currentP = 1;
  groupId;
  private mobjModalRef: BsModalRef;
  conditionGroupCard;
  message;
  nodeTreeView: TreeviewItem;
  nodeTreeViewDefault: TreeviewItem;
  nodeTreeViews: TreeviewItem[] = [];
  nodeItem: TreeItem;
  nodeItemDefault: TreeItem;
  nodeItems: TreeItem[] = [];
  nodeItemsDefault: TreeItem[] = [];
  dataUnit = [];
  dataTree = [];
  nodeTrees: UnitModel[] = [];
  nodeTreesDefault: UnitModel[] = [];
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
    maxHeight: 200,
  });
  formGroupCard: FormGroup;
  dataCreate;
  searchDefaultCycle = null;
  dataUpate;
  dataUnitDefault;
  dataTreeDefault;
  mblnClickAdd = false;
  added: boolean;

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

  constructor(private groupCardService: ConfigGroupCardService,
              private serviceService: ServiceService,
              private modalService: BsModalService,
              private toastr: ToastrService,
              private translate: TranslateService,
              private fb: FormBuilder,
              private addDialog: MatDialog,
              private warningReceiveService: WarningReceiveService,
              private channelService: ChannelService,
              private vdsChannelService: VdschannelService,
              private warningReceiveSv: WarningReceiveService) {
  }

  ngOnInit() {
    this.formSearch();
    this.getGroupCardCycle();
    this.searchGroupCard();
    this.getPartner();
    this.formSearch();

    this.vdsChannelService.getAllChannel().subscribe(res => {
      this.channels = res.data;
    });

    this.warningReceiveSv.reloadWarning$.subscribe(page => {
      if(page == 1){
        this.currentP = 1;
      }
      this.searchGroupCard();
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

  pageChange(value: number) {
    this.currentP = value;
  }

  openConfirm(pobjTemplate: TemplateRef<any>, id: number) {
    this.groupId = id;
    this.mobjModalRef = this.modalService.show(pobjTemplate, {
        ignoreBackdropClick: true
      }
    );
  }

  onBack() {
    this.mobjModalRef.hide();
  }

  removeGroupCard() {
    this.groupCardService.deleteGroupCard(this.groupId).subscribe(
      deleteGroupCard => {
        this.message = deleteGroupCard['data'];
        if (this.message === 'SUCCESS') {
          this.showSuccess(this.translate.instant('management.menu.deleteSuccess'));
        } else if (this.message === 'GROUP_HAVE_CARD') {
          this.showError(this.translate.instant('management.group-card.group-have-card'));
        } else {
          this.showError(this.translate.instant('management.warningconfig.serverError'));
        }
        this.mobjModalRef.hide();
        this.searchGroupCard();
        // this.getAllGroupCard();
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
    this.toastr.error(message,
      this.translate.instant('management.service.message.fail'), {
        timeOut: 2000,
        positionClass: 'toast-top-center',
      });
  }

  formSearch() {
    this.formGroupCard = this.fb.group({
      groupName: null,
      defaultCycle: null,
      vdsChannelCode: null,
      shopCode: null
    });
  }

  searchGroupCard() {
    // if (this.added) {
    //   this.currentP = 1;
    // }
    const groupName = this.formGroupCard.get('groupName').value;
    const defaultCycle = this.formGroupCard.get('defaultCycle').value === 'null' ? null
      : this.formGroupCard.get('defaultCycle').value;
    let shopCode;
    if (this.shopunitDTO.shopCode == '-1') {
      shopCode = null;
    } else {
      shopCode = this.shopunitDTO.shopCode;
    }
    let vdsChannelCode;
    if (this.vdsChannelCode === 'null') {
      vdsChannelCode = null;
    } else {
      vdsChannelCode = this.vdsChannelCode;
    }
    // if (this.shopunitDTO.vdsChanelCode == '-1') {
    //   vdsChannelCode = null;
    // } else {
    //   vdsChannelCode = this.shopunitDTO.vdsChanelCode;
    // }

    const modelSearch: ConfigGroupCardModel = new ConfigGroupCardModel(null, null, null, groupName, defaultCycle, vdsChannelCode, shopCode);
    this.groupCardService.searchGroupCard(modelSearch).subscribe(
      dataSearch => {
        this.listGroupCard = dataSearch['data'];
      }
    );
  }

  formAddGroupCard() {
    const dialogRef = this.addDialog.open(CreateGroupCardComponent, {
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
          this.groupCardService.service$.subscribe(
            dataAdd => {
              this.added = dataAdd;
            }
          );
          // if (this.added) {
          //   this.currentP = 1;
          //   this.searchGroupCard();
          // }
        }
      });
    });
  }

  formUpdateGroupCard(groupCard: ConfigGroupCardModel) {
    const dialogRef = this.addDialog.open(UpdateGroupCardComponent, {
      maxWidth: '85vw',
      maxHeight: '95vh',
      width: '80vw',
      data: {
        groupId: groupCard.groupId,
        groupCode: groupCard.groupCode,
        groupName: groupCard.groupName,
        defaultCycle: groupCard.defaultCycle,
        vdsChannelCode: groupCard.vdsChannelCode,
        vdsChannelName: groupCard.vdsChannelName,
        shopCode: groupCard.shopCode,
        shopName: groupCard.shopName,
        defaultCycleName: groupCard.defaultCycleName
        // dataUpdate: this.dataUpate
      }
    });
    dialogRef.afterClosed().subscribe(result => {
      this.serviceService.reloadWarning$.subscribe(res => {
        if (res) {
          this.groupCardService.service$.subscribe(
            dataAdd => {
              this.added = dataAdd;
            }
          );
          // if (this.added) {
          //   this.currentP = 1;
          // }
        }
      });
      // this.searchGroupCard();
    });
  }

  // createTreeView() {
  //   try {
  //     this.nodeTreeViews = [];
  //
  //     const treeItem: TreeItem = new class implements TreeItem {
  //       checked = false;
  //       children = null;
  //       collapsed = false;
  //       disabled = false;
  //       text = 'Chọn';
  //       value: any = null;
  //     }
  //     const itemDefault: TreeviewItem = new TreeviewItem(treeItem, false);
  //
  //     this.serviceService.getDataUnits().subscribe(
  //       next => {
  //         if (!next['data']) {
  //           return;
  //         }
  //         this.nodeTreeViews = [];
  //         this.dataUnit = next['data'];
  //         this.dataTree = next['data'];
  //         this.nodeTrees = this.createNode(this.nodeTrees, this.dataTree, this.unitIitem);
  //         this.dataTree = this.nodeTrees;
  //         this.createTree(this.nodeTrees, this.dataTree);
  //         this.nodeTrees.forEach(valuess => {
  //           this.nodeTreeView = new TreeviewItem(this.forwardData(valuess, this.nodeItem, this.nodeItems));
  //           this.nodeTreeViews.push(this.nodeTreeView);
  //           this.nodeTreeViews.push(itemDefault);
  //         });
  //
  //         for (let i = 0; i < this.nodeTreeViews.length; i++) {
  //           if (this.nodeTreeViews[i].children) {
  //             this.value = this.nodeTreeViews[i].value;
  //             this.dataUnit.forEach(
  //               tree => {
  //                 if (tree.shopCode === this.nodeTreeViews[i].value) {
  //                   this.shopCode = tree.shopCode;
  //                   this.vdsChannelCode = tree.vdsChannelCode;
  //                   return;
  //                 }
  //               }
  //             );
  //             break;
  //           } else {
  //             this.value = this.nodeTreeViews[0].value;
  //           }
  //         }
  //       }
  //     );
  //   } catch (e) {
  //   }
  // }
  //
  // createNode(parrItems: UnitModel[] = [], parrDataTree: UnitModel[], pobjItem: UnitModel) {
  //   try {
  //     pobjItem = null;
  //     parrItems = parrDataTree.map(value => {
  //       pobjItem = {
  //         id: value.id,
  //         shopName: value.shopName,
  //         parentShopCode: value.parentShopCode,
  //         shopCode: value.shopCode,
  //         vdsChannelCode: value.vdsChannelCode,
  //         children: [],
  //         groupId: value.groupId
  //       };
  //       return pobjItem;
  //     });
  //     return parrItems;
  //   } catch (e) {
  //   }
  // }
  //
  // createTree(parrNodeTrees: UnitModel[], parrDataTree: UnitModel[]) {
  //   try {
  //     const len = parrNodeTrees.length;
  //     for (let i = 0; i < len; i++) {
  //       for (let j = 0; j < len; j++) {
  //         if (parrNodeTrees[i].shopCode === parrDataTree[j].parentShopCode) {
  //           parrNodeTrees[i].children.push(parrDataTree[j]);
  //           this.marrIndexNodeService.push(j);
  //         }
  //       }
  //     }
  //     const c = (a: number, b: number) => (a - b);
  //     this.marrIndexNodeService.sort(c);
  //     for (let i = this.marrIndexNodeService.length - 1; i >= 0; i--) {
  //       parrNodeTrees.splice(this.marrIndexNodeService[i], 1);
  //     }
  //   } catch (e) {
  //   }
  // }
  //
  // forwardData(pobjNodeTree: UnitModel, pobjitem: TreeItem, parrItems: TreeItem[] = []) {
  //   try {
  //     pobjitem = null;
  //     parrItems = [];
  //     // pobjNodeTree = null;
  //     if (pobjNodeTree.children) {
  //       pobjNodeTree.children.forEach(value => {
  //         this.mobjNodeItemService = this.forwardData(value, null, []);
  //         parrItems.push(this.mobjNodeItemService);
  //       });
  //     }
  //     if (pobjNodeTree.children) {
  //       pobjitem = {
  //         value: pobjNodeTree.shopCode,
  //         text: pobjNodeTree.shopName,
  //         children: parrItems,
  //         checked: false
  //       };
  //     } else {
  //       parrItems = [];
  //       pobjitem = {
  //         value: pobjNodeTree.shopCode,
  //         text: pobjNodeTree.shopName,
  //         children: null,
  //         checked: false
  //       };
  //     }
  //     return pobjitem;
  //   } catch (e) {
  //   }
  // }
  //
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
  //     this.formGroupCard.controls['vdsChannelCode'].setValue(this.vdsChannelCode);
  //   } else {
  //     this.formGroupCard.controls['vdsChannelCode'].setValue('');
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
    //   this.formGroupCard.controls['vdsChannelCode'].setValue(this.vdsChannelCode);
    // } else {
    //   this.formGroupCard.controls['vdsChannelCode'].setValue('');
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
  }

}
