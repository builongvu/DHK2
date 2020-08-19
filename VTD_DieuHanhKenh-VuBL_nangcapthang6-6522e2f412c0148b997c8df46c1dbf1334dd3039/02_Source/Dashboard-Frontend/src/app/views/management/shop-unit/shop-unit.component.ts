import { Component, OnInit, TemplateRef } from '@angular/core';
import { ServiceModel } from '../../../models/service.model';
import { TreeviewItem, TreeItem } from 'ngx-treeview';
import { config } from '../../../config/application.config';
import { WarningSendService } from '../../../services/management/warning-send.service';
import { Partner } from '../../../models/Partner';
import { UnitModel } from '../../../models/unit.model';
import { DashboardModel } from '../../../models/dashboard.model';
import { FormControl } from '@angular/forms';
import * as _moment from 'moment';
import { WarningReceiveService } from '../../../services/management/warning-receive.service';
import { UnitService } from '../../../services/management/unit.service';
import { DVT } from '../../../models/dvt.model';
import { ShopunitService } from '../../../services/management/shopunit.service';
import { ShopUnitDTO, ShopUnit } from '../../../models/shopUnit.model';
import { Pager } from '../../../models/Pager';
import { DialogShopUnitComponent } from './dialog-shop-unit/dialog-shop-unit.component';
import { MatDialog } from '@angular/material';
import { BsModalService, BsModalRef } from 'ngx-bootstrap';
import { ToastrService } from 'ngx-toastr';
import { TranslateService } from '@ngx-translate/core';
import * as fileSaver from 'file-saver';
import { HttpHeaders, HttpClient } from '@angular/common/http';
import { VdschannelService } from '../../../services/management/vdschannel.service';

@Component({
  selector: 'app-shop-unit',
  templateUrl: './shop-unit.component.html',
  styleUrls: ['./shop-unit.component.scss']
})
export class ShopUnitComponent implements OnInit {


  mobjService: ServiceModel;
  marrItemServices: ServiceModel[] = [];
  mobjNodeTreeviewService: TreeviewItem;
  mobjNodeItemService: TreeItem;
  mobjItemService: ServiceModel;
  marrNodeTreeviewServices: TreeviewItem[] = [];
  marrNodeItemServices: TreeItem[] = [];
  marrData: ServiceModel[] = [];
  marrIndexNode = [];
  value = null;
  mobjConfig = {
    hasFilter: true,
    hasCollapseExpand: true,
    maxHeight: 130,
  };

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
  groupId;
  shopCodeUnit;
  dashModelUnit;
  date = new FormControl(_moment());

  units: DVT[] = [];
  unitAll: DVT[] = [];
  shopunitDTO: ShopUnitDTO = new ShopUnitDTO();
  shopUnits: ShopUnit[] = [];
  channel: string;
  mblnCheckAll = false;
  mstrunlock = '0';
  mstrlock = '0';
  mobjModalRef: BsModalRef;
  mblnConfirm;
  uploadfile: string;
  mobjFileList: FileList;
  filenull: boolean = false;
  vstrFileName: string = "";
  status: string = "";
  errors: string = "";
  channels = [];
  constructor(
    private warningSendService: WarningSendService,
    private warningReceiveService: WarningReceiveService,
    private unitService: UnitService,
    public dialog: MatDialog,
    private modalService: BsModalService,
    private toastr: ToastrService,
    private translate: TranslateService,
    private http: HttpClient,
    private vdsChannelService: VdschannelService,
    private shopunitService: ShopunitService
  ) { }

  ngOnInit() {
    this.shopunitDTO.status = '-1';
    this.shopunitDTO.unitCode = '-1';
    this.shopunitDTO.pager = new Pager(1, 10);
    this.vdsChannelService.getAllChannel().subscribe(res => {
      this.channels = res.data;
      this.getService();
    })
    this.warningReceiveService.reloadWarning$.subscribe(page => {
      if (page == 1) {
        this.shopunitDTO.pager.page = 1;
      }
      this.getShopUnit();
    });
  }


  /**
  * Mở modal tải lên dữ liệu từ file Excel
  *
  * @author: CuongDT
  * @version: 1.0
  * @since: 2020/02/10
  */
  newUpload(pobjTemplate: TemplateRef<any>) {
    this.mobjFileList = null;
    this.uploadfile = null;
    this.filenull = false;
    this.vstrFileName = "";
    this.errors = "";
    this.status = "";
    this.mobjModalRef = this.modalService.show(pobjTemplate, {
      ignoreBackdropClick: true
    }
    );
  }


  
  /**
  * Sự kiện khi chọn file
  *
  * @author: CuongDT
  * @version: 1.0
  * @since: 2020/02/10
  */
  selectFile(pobjEvent) {
    this.mobjFileList = pobjEvent.target.files;
    this.filenull = false;
    this.vstrFileName = "";
    this.errors = "";
    this.status = "";
  }


  
  /**
  * Download file kết quả
  *
  * @author: CuongDT
  * @version: 1.0
  * @since: 2020/02/10
  */
  downloadResult() {
    this.warningSendService.DownloadWarningSend(this.vstrFileName)
      .subscribe(vobjResponse => {
        this.saveFile(vobjResponse.body, this.vstrFileName);
      });
  }

  
  /**
  * Gọi service upload file
  *
  * @author: CuongDT
  * @version: 1.0
  * @since: 2020/02/10
  */
  upLoad() {
    this.vstrFileName = "";
    if (this.mobjFileList) {
      this.filenull = false;
      const vfileFile: File = this.mobjFileList[0];
      const vobjFormData: FormData = new FormData();
      vobjFormData.append('file', vfileFile);
      const vobjHeaders = new HttpHeaders();
      vobjHeaders.append('Content-type', 'multipart/form-data');
      vobjHeaders.append('Accept', 'application/json');
      const options = { headers: vobjHeaders };
      this.http.post(`${config.apiUrl}/management/shopunit/upload`, vobjFormData, options)
        .subscribe((respon: any) => {
          if (respon.code === 200) {
            this.status = this.translate.instant('management.warningconfig.UploadSuccess') + respon.data.sumSuccessfulRecord + "/" + respon.data.total + " " + this.translate.instant('management.warningconfig.record');
            this.errors = "";
            this.search();
            this.vstrFileName = respon.data.fileName;
          } else {
            if (respon.errors === "null") {
              this.errors = this.translate.instant('management.warningconfig.importempty');
            } else {
              this.errors = respon.errors;
            }
            this.status = "";
          }
        });
    } else {
      this.filenull = true;
    }
  }


  
  /**
  * Lưu file
  *
  * @author: CuongDT
  * @version: 1.0
  * @since: 2020/02/10
  */
  saveFile(pobjData: any, pstrFilename?: string) {
    const blob = new Blob([pobjData], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
    fileSaver.saveAs(blob, pstrFilename);
  }

  
  /**
  * Mở modal thêm mới
  *
  * @author: CuongDT
  * @version: 1.0
  * @since: 2020/02/10
  */
  openNew() {
    const vdialog = this.dialog.open(DialogShopUnitComponent, {
      data: {
        service: this.marrNodeTreeviewServices,
        shop: this.TreeShopCode,
        unit: this.units,
        channels: this.channels,
        marrParther: this.marrParther,
        shopUnit: new ShopUnit(),
        type: 1
      }
    });
    vdialog.afterClosed().subscribe(result => {

    });
  }

  
  /**
  * Mở modal sửa
  *
  * @author: CuongDT
  * @version: 1.0
  * @since: 2020/02/10
  */
  openEdit(shopUnit: ShopUnit) {
    const vdialog = this.dialog.open(DialogShopUnitComponent, {
      data: {
        service: this.marrNodeTreeviewServices,
        shop: this.TreeShopCode,
        unit: this.units,
        marrParther: this.marrParther,
        shopUnit: shopUnit,
        type: 0
      }
    });
    vdialog.afterClosed().subscribe(result => {

    });
  }

  
  /**
  * Sự kiện khi click checkbox
  *
  * @author: CuongDT
  * @version: 1.0
  * @since: 2020/02/10
  */
  select(id: number) {
    this.mstrunlock = '0';
    this.mstrlock = '0';
    this.shopUnits.forEach(shopUnit => {
      if (shopUnit.mlngId == id) {
        shopUnit.check = !shopUnit.check;
      }
      if (shopUnit.check) {
        if (shopUnit.mstrStatus == '0') {
          this.mstrunlock = '1';
        } else {
          this.mstrlock = '1';
        }
      }
    });
    let varrCheck = this.shopUnits.filter(item => item.check == true);
    if (varrCheck.length == this.shopUnits.length) {
      this.mblnCheckAll = true;
    } else {
      this.mblnCheckAll = false;
    }
  }

  
  /**
  * Khóa, mở khóa bản ghi
  *
  * @author: CuongDT
  * @version: 1.0
  * @since: 2020/02/10
  */
  lockUnlock(status: string) {
    let marr = this.shopUnits.filter(item => item.check);
    let id = [];
    marr.forEach((shopunit: ShopUnit) => {
      id.push(shopunit.mlngId.toString());
    });
    this.shopunitService.lockUnlock(id, status).subscribe(res => {
      if (res.code === 200) {
        this.showSuccess(res.data);
      } else {
        this.showError(res.errors);
      };
      this.mblnCheckAll = false;
      this.mstrunlock = '0';
      this.mstrlock = '0';
      this.getShopUnit();
      this.back();
    })
  }
  showSuccess(message: string) {
    this.toastr.success(message,
      this.translate.instant('management.warningconfig.success'), {
      timeOut: 2000,
      positionClass: 'toast-top-center',
    });
  }


  /**
  * Thông báo thất bại
  *
  * @author: CuongDT
  * @version: 1.0
  * @since: 2019/11/18
  */
  showError(message: string) {
    this.toastr.error(message,
      this.translate.instant('management.warningconfig.fail'), {
      timeOut: 2000,
      positionClass: 'toast-top-center',
    });
  }

  
  /**
  * Mở popup confirm
  *
  * @author: CuongDT
  * @version: 1.0
  * @since: 2020/02/10
  */
  openConfirm(pobjTemplate: TemplateRef<any>, status: string) {
    if (status == '1') {
      this.mblnConfirm = true;
    } else {
      this.mblnConfirm = false;
    }
    this.mobjModalRef = this.modalService.show(pobjTemplate, {
      ignoreBackdropClick: true
    }
    );
  }
  
  /**
  * Đóng popup 
  *
  * @author: CuongDT
  * @version: 1.0
  * @since: 2020/02/10
  */
  back() {
    this.mobjModalRef.hide();
  }

  
  /**
  * Sự kiện khi click checkbox chọn tất cả
  *
  * @author: CuongDT
  * @version: 1.0
  * @since: 2020/02/10
  */
  checkAll() {
    this.mstrunlock = '0';
    this.mstrlock = '0';
    this.shopUnits.forEach(shopUnit => {
      shopUnit.check = !this.mblnCheckAll;
      if (shopUnit.check) {
        if (shopUnit.mstrStatus == '0') {
          this.mstrunlock = '1';
        } else {
          this.mstrlock = '1';
        }
      }
    });
  }

  
  /**
  * Sự kiện chuyển trang
  *
  * @author: CuongDT
  * @version: 1.0
  * @since: 2020/02/10
  */
  pageChange(p: any) {
    this.shopunitDTO.pager.page = p;
    this.getShopUnit();
  }

  
  /**
  * Sự kiện click tìm kiếm
  *
  * @author: CuongDT
  * @version: 1.0
  * @since: 2020/02/10
  */
  search() {
    this.shopunitDTO.pager.page = 1;
    this.getShopUnit();
  }

  
  /**
  * Gọi api tìm kiếm
  *
  * @author: CuongDT
  * @version: 1.0
  * @since: 2020/02/10
  */
  getShopUnit() {
    let shopUnit = new ShopUnitDTO();
    shopUnit.pager = this.shopunitDTO.pager;
    if (this.shopunitDTO.status == '-1') {
      shopUnit.status = null;
    } else {
      shopUnit.status = this.shopunitDTO.status;
    }
    if (this.shopunitDTO.shopCode == '-1') {
      shopUnit.shopCode = null;
    } else {
      shopUnit.shopCode = this.shopunitDTO.shopCode;
    }
    if (this.shopunitDTO.unitCode == '-1') {
      shopUnit.unitCode = null;
    } else {
      shopUnit.unitCode = this.shopunitDTO.unitCode;
    }
    if (this.shopunitDTO.serviceId == -1) {
      shopUnit.serviceId = null;
    } else {
      shopUnit.serviceId = this.shopunitDTO.serviceId;
    }
    this.shopunitService.getShopUnitByCondition(shopUnit).subscribe(res => {
      if (res.data) {
        this.shopUnits = res.data;
      } else {
        this.shopUnits = [];
      }
      this.fillDataTable();
      this.shopunitDTO.pager.totalRow = res.totalRow;
    })
  }

  
  /**
  * Fill dữ liệu vào bảng
  *
  * @author: CuongDT
  * @version: 1.0
  * @since: 2020/02/10
  */
  fillDataTable() {
    this.mblnCheckAll = false;
    this.mstrunlock = '0';
    this.mstrlock = '0';
    if (this.shopUnits.length > 0) {
      this.shopUnits.forEach(shopunit => {
        shopunit.service = this.marrData.filter(item => item.id == shopunit.mlngServiceId)[0];
        shopunit.partner = this.marrParther.filter(item => item.shopCode == shopunit.mstrShopCode)[0];
        if (shopunit.partner == undefined) {
          shopunit.partner = new Partner();
          shopunit.partner.shopName = '';
        }
        let channel = this.channels.filter(item => item.code == shopunit.mstrVdsChannelCode);
        if (channel && channel.length > 0) {
          shopunit.channel = channel[0].name;
        }
        shopunit.unit = this.unitAll.filter(item => item.code == shopunit.mstrUnitCode)[0];
        shopunit.check = false;
      });
    }
  }

  
  /**
  * Lấy ra tất cả đơn vị
  *
  * @author: CuongDT
  * @version: 1.0
  * @since: 2020/02/10
  */
  getUnit() {
    this.unitService.getUnit().subscribe((res: any) => {
      this.units = res.data.filter(item => item.status == '1');
      this.unitAll = res.data;
      this.unitAll.sort((left, right) => {
        if (left.name < right.name) { return -1; }
        if (left.name > right.name) { return 1; }
        return 0;
      });
      this.getShopUnit();
    })
  }

  
  /**
  * Lấy ra tất cả Shop
  *
  * @author: CuongDT
  * @version: 1.0
  * @since: 2020/02/10
  */
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

  onValueChange(value: number) {
    this.shopunitDTO.serviceId = value;
  }
  onValueChangeUnit(value: string) {
    this.shopunitDTO.shopCode = value;
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
  }

  
  /**
  * Lấy ra tất cả chỉ tiêu
  *
  * @author: CuongDT
  * @version: 1.0
  * @since: 2020/02/10
  */
  getService() {
    this.marrNodeTreeviewServices = [];
    this.marrData = [];
    this.mobjService = new ServiceModel(null, null);
    this.warningSendService.getAllService().subscribe(
      vobjNext => {
        var ServiceAll: ServiceModel = new ServiceModel(null, null);
        ServiceAll.id = -1;
        if (localStorage.getItem(config.user_default_language) === 'en') {
          ServiceAll.name = "Select";
        } else {
          ServiceAll.name = "Chọn";
        }
        this.marrData.push(ServiceAll);
        var marr: ServiceModel[] = [];
        marr = vobjNext.data;
        for (let i = 0; i < marr.length; i++) {
          this.marrData.push(marr[i]);
        };
        this.marrItemServices = this.createNode(this.marrItemServices, this.marrData);
        this.createTree(this.marrItemServices, this.marrData);
        this.marrItemServices.forEach(vobjValue => {
          this.mobjNodeTreeviewService = new TreeviewItem(this.forwardData(vobjValue, this.mobjNodeItemService, this.marrNodeItemServices));
          this.marrNodeTreeviewServices.push(this.mobjNodeTreeviewService);
        });
        this.value = -1;
        this.getPartner();
      },
      vobjErorr => { console.log('no data'); }
    );
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
      this.getUnit();
    } catch (e) {
      console.error('error');
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

}
