import { Component, OnInit, TemplateRef } from '@angular/core';
import { WarningSendService } from '../../../services/management/warning-send.service';
import { apParam } from '../../../models/apParam.model';
import { WarningReceiveService } from '../../../services/management/warning-receive.service';
import { WarningReceive, WarningReceiveData, SearchReceive } from '../../../models/Warning-Receive';
import { Partner } from '../../../models/Partner';
import { DialogWarningReceiveComponent } from './dialog-warning-receive/dialog-warning-receive.component';
import { MatDialog } from '@angular/material';
import { TreeviewItem, TreeviewConfig, TreeItem, TreeviewI18n } from 'ngx-treeview';
import { ToastrService } from 'ngx-toastr';
import { TranslateService } from '@ngx-translate/core';
import * as fileSaver from 'file-saver';
import { UnitModel } from '../../../models/unit.model';
import { DashboardModel } from '../../../models/dashboard.model';
import * as _moment from 'moment';
import { FormControl } from '@angular/forms';
import { Pager } from '../../../models/Pager';
import { BsModalService, BsModalRef } from 'ngx-bootstrap';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { config } from '../../../config/application.config';
import { UnitTreeviewI18n } from '../mng-target/config-tree-select/unit-treeview-i18n';

@Component({
  selector: 'app-warning-receive-config',
  templateUrl: './warning-receive-config.component.html',
  styleUrls: ['./warning-receive-config.component.scss'],
  providers: [
    { provide: TreeviewI18n, useClass: UnitTreeviewI18n }
  ]
})
export class WarningReceiveConfigComponent implements OnInit {
  marrWarninglevels: apParam[] = [];
  marrInformlevels: apParam[] = [];
  marrAllWarninglevels: apParam[] = [];
  marrAllInformlevels: apParam[] = [];
  mstrWarninglevel: string = "-1";
  mstrInformlevel: string = "-1";
  mstrNewWarninglevel: string = "0";
  mstrNewInformlevel: string = "0";
  mstrNewShopCode: string = "0";

  marrParther: Partner[] = [];
  marrWarningReceive: WarningReceive[] = [];
  marrWarningDataTable: WarningReceiveData[] = [];
  mobjSearchReceive: SearchReceive = new SearchReceive();
  mobjNewWarningReceive: WarningReceive = new WarningReceive;
  value: any;
  mstrstatus: string;
  vblnCheckunlok: boolean;
  nodeTreeView: TreeviewItem;
  nodeTreeViews: TreeviewItem[] = [];
  TreeShopCode: TreeviewItem[] = [];
  dataTree = [];
  dataUnit = [];
  nodeTrees: UnitModel[] = [];
  nodeItem: TreeItem;
  nodeItems: TreeItem[] = [];
  unitIitem: UnitModel;
  marrIndexNodeService = [];
  mobjNodeItemService: TreeItem;
  cycleId;
  groupId;
  shopCode;
  mblnConfirm: boolean;
  mnbrPageSize = 10;
  selected;
  mnbrP = 1;
  mnbrTotal = 0;
  mobjPager: Pager;
  dashModel: DashboardModel;
  mblnCheckAll: boolean = false;
  config = TreeviewConfig.create({
    hasFilter: true,
    hasCollapseExpand: false,
    maxHeight: 120,
  });
  uploadfile: string;
  mobjFileList: FileList;
  filenull: boolean = false;
  vstrFileName: string = "";
  status: string = "";
  errors: string = "";
  mobjModalRef: BsModalRef;
  date = new FormControl(_moment());
  constructor(
    private warningSendService: WarningSendService,
    private warningReceiveService: WarningReceiveService,
    public dialog: MatDialog,
    private toastr: ToastrService,
    private translate: TranslateService,
    private http: HttpClient,
    private modalService: BsModalService
  ) { }

  ngOnInit() {
    this.mobjSearchReceive.mstrStatus = "-1";
    this.warningSendService.getWarningLevelNoStatus("WARNING_LEVEL").subscribe(res => {
      this.marrAllWarninglevels = res.data;
      this.warningSendService.getWarningLevelNoStatus("INFORM_LEVEL").subscribe(res => {
        this.marrAllInformlevels = res.data;
        this.LoadData();
      });
    });
  }



  /**
 * lấy dữ liệu từ api và truyền vào combobox
 *
 * @author: CuongDT
 * @version: 1.0
 * @since: 2019/11/18
 */
  LoadData() {
    this.warningReceiveService.getAllPartner().subscribe(res => {
      var vobjNewPartner: Partner = new Partner();
      vobjNewPartner.shopCode = "-1";
      if (localStorage.getItem(config.user_default_language) === 'en') {
        vobjNewPartner.shopName = "All";
      } else {
        vobjNewPartner.shopName = "Tất cả";
      }
      var vobjNewPartner1: Partner = new Partner();
      vobjNewPartner1.shopCode = "-2";
      if (localStorage.getItem(config.user_default_language) === 'en') {
        vobjNewPartner1.shopName = "Select";
      } else {
        vobjNewPartner1.shopName = "Chọn";
      }
      this.marrParther = res.data;
      this.marrParther = this.marrParther.reverse();
      this.marrParther.push(vobjNewPartner);
      this.marrParther.push(vobjNewPartner1);
      this.marrParther = this.marrParther.reverse();
      this.createTreeView();
      this.value = "-2"
    });
    this.warningSendService.getWarningLevel("WARNING_LEVEL", "1").subscribe(res => {
      this.marrWarninglevels = res.data;
      this.warningSendService.getWarningLevel("INFORM_LEVEL", "1").subscribe(res => {
        this.marrInformlevels = res.data;
        this.warningReceiveService.getAll().subscribe(res => {
          this.warningReceiveService.setListWarningReceives(res.data)
          this.warningReceiveService.warningReceives$.subscribe(res => {
            this.marrWarningReceive = res;
            this.warningReceiveService.reloadWarning$.subscribe(res => {
              if (res != 0) {
                this.mnbrP = 1;
              }
              this.Search();
            });
          });
        });
      });
    });
  }


  /**
    * Tạo cây đơn vị
    *
    * @author: CuongDT
    * @version: 1.0
    * @since: 2019/11/18
    */
  createTreeView() {
    try {
      this.nodeTreeViews = [];
      this.groupId = null;
      this.nodeTreeViews = [];
      this.dataUnit = this.marrParther;
      this.dataTree = this.marrParther;
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
                this.dashModel = new DashboardModel(this.groupId, null, this.date.value._d.getTime(), this.cycleId, this.shopCode);
                return;
              }
            }
          );
          break;
        }
      }
      this.TreeShopCode = this.nodeTreeViews;
    } catch (e) {
    }

  }


  forwardData(pobjNodeTree: UnitModel, pobjitem: TreeItem, parrItems: TreeItem[] = []) {
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

  createNode(parrItems: UnitModel[] = [], parrDataTree: UnitModel[], pobjItem: UnitModel) {
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

  createTree(parrNodeTrees: UnitModel[], parrDataTree: UnitModel[]) {
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
  }


  /**
 * Đổ dữ liệu ra bảng
 *
 * @author: CuongDT
 * @version: 1.0
 * @since: 2019/11/18
 */
  LoadDataTable() {
    this.marrWarningDataTable = [];
    for (let i = 0; i < this.marrWarningReceive.length; i++) {
      this.marrWarningDataTable.push(new WarningReceiveData());
      let b = this.marrWarningReceive[i].mintInformLevel + "";
      this.marrWarningDataTable[i].mintInformLevel = this.marrAllInformlevels.filter(item => item.code === b)[0];
      let c = this.marrWarningReceive[i].mintWarningLevel + "";
      this.marrWarningDataTable[i].mintWarningLevel = this.marrAllWarninglevels.filter(item => item.code === c)[0];
      this.marrWarningDataTable[i].mstrStatus = this.marrWarningReceive[i].mstrStatus;
      this.marrWarningDataTable[i].mstrShopCode = this.marrParther.filter(item => item.shopCode === this.marrWarningReceive[i].mstrShopCode)[0];
      this.marrWarningDataTable[i].mlngId = this.marrWarningReceive[i].mlngId;
      this.marrWarningDataTable[i].mblnCheckbox = false;
    }
  }


  /**
 * Lấy giá trị được chọn của combobox đơn vị khi thay đổi
 *
 * @author: CuongDT
 * @version: 1.0
 * @since: 2019/11/18
 */

  OnchangeNew(value: any) {
    this.mobjSearchReceive.mstrShopCode = value;
  }


  clickSearch() {
    this.mnbrP = 1;
    this.Search();
  }

  /**
 * Tìm kiếm các cấu hình theo điều kiện và đổ dữ liệu tìm được ra bảng
 *
 * @author: CuongDT
 * @version: 1.0
 * @since: 2019/11/18
 */
  Search() {
    this.mblnCheckAll = false;
    this.mstrstatus = "";
    if (this.mstrInformlevel !== "-1") {
      this.mobjSearchReceive.mintInformLevel = this.mstrInformlevel;
    } else {
      this.mobjSearchReceive.mintInformLevel = null;
    }
    if (this.mstrWarninglevel !== "-1") {
      this.mobjSearchReceive.mintWarningLevel = this.mstrWarninglevel;
    } else {
      this.mobjSearchReceive.mintWarningLevel = null;
    }
    if (this.mobjSearchReceive.mstrShopCode === "-2") {
      this.mobjSearchReceive.mstrShopCode = null;
    }
    this.warningReceiveService.getByCondition(this.mobjSearchReceive, this.mnbrP, this.mnbrPageSize).subscribe(res => {
      this.marrWarningReceive = res.data;
      this.mnbrTotal = res.totalRow;
      this.LoadDataTable();
    });
    this.mobjPager = new Pager(this.mnbrP, this.mnbrPageSize);
  }



  /**
 * Mở ra modal thêm mới/cập nhật
 *
 * @author: CuongDT
 * @version: 1.0
 * @since: 2019/11/18
 */
  openDialog(id: number) {
    let vstrShopcode: string;
    if (id === 0) {
      this.mobjNewWarningReceive = new WarningReceive();
      this.mobjNewWarningReceive.mstrStatus = "1";
      this.mobjNewWarningReceive.mintInformLevel = 0;
      this.mobjNewWarningReceive.mintWarningLevel = 0;
      this.mobjNewWarningReceive.mstrShopCode = "-1";
      this.mobjNewWarningReceive.mlngId = -1;
    } else {
      this.mobjNewWarningReceive = this.marrWarningReceive.filter(item => item.mlngId === id)[0];
      vstrShopcode = this.marrParther.filter(item => item.shopCode === this.mobjNewWarningReceive.mstrShopCode)[0].shopName;
    }
    const vdialog = this.dialog.open(DialogWarningReceiveComponent, {
      data: {
        obj: this.mobjNewWarningReceive,
        shopcode: this.TreeShopCode,
        warninglv: this.marrWarninglevels,
        informlv: this.marrInformlevels,
        shopname: vstrShopcode
      }
    });
    // vdialog.afterClosed().subscribe(result => {
    //   if (id !== 0) {
    //     this.Search();
    //   }
    // });
  }



  /**
 * Sự kiện khi click checkbox chọn các cấu hình cảnh báo
 *
 * @author: CuongDT
 * @version: 1.0
 * @since: 2019/11/18
 */
  select(id: number) {
    for (let i = 0; i < this.marrWarningDataTable.length; i++) {
      if (this.marrWarningDataTable[i].mlngId === id) {
        if (this.marrWarningDataTable[i].mblnCheckbox === true) {
          this.marrWarningDataTable[i].mblnCheckbox = false;
        } else {
          this.marrWarningDataTable[i].mblnCheckbox = true;
        }
        break;
      }
    }
    var vblncheck = true;
    this.marrWarningDataTable.forEach(mobj => {
      if (mobj.mblnCheckbox === false) {
        vblncheck = false;
      }
    });
    if (vblncheck) {
      this.mblnCheckAll = true;
    } else {
      this.mblnCheckAll = false
        ;
    }
    this.CheckStatus();
  }



  /**
 * Sự kiện khi click checkbox chọn Tất cả (Bảng dữ liệu)
 *
 * @author: CuongDT
 * @version: 1.0
 * @since: 2019/11/18
 */
  SelectAll() {
    var vblnCheck = false;
    if (this.mblnCheckAll === false) {
      vblnCheck = true;
    }
    for (let i = 0; i < this.marrWarningDataTable.length; i++) {
      if (vblnCheck === true) {
        this.marrWarningDataTable[i].mblnCheckbox = true;
      } else {
        this.marrWarningDataTable[i].mblnCheckbox = false;
      }
    }
    this.CheckStatus();
  }



  /**
 * Kiểm tra các cấu hình đang được chọn và cho phép khoá hoặc mở khoá
 *
 * @author: CuongDT
 * @version: 1.0
 * @since: 2019/11/18
 */
  CheckStatus() {
    this.mstrstatus = "0";
    var marr = this.marrWarningDataTable.filter(item => item.mblnCheckbox === true);
    if (marr.length > 0) {
      this.mstrstatus = marr[0].mstrStatus;
      for (let i = 1; i < marr.length; i++) {
        if (marr[i].mstrStatus !== this.mstrstatus) {
          this.mstrstatus = "-1";
          break;
        }
      }
    } else {
      this.mstrstatus = "";
    }
  }



  /**
 * Sự kiện khi click button khoá
 *
 * @author: CuongDT
 * @version: 1.0
 * @since: 2019/11/18
 */
  lock() {
    var varrWarningLock = this.marrWarningDataTable.filter(item => item.mblnCheckbox === true);
    var varrId: string[] = [];
    for (let i = 0; i < varrWarningLock.length; i++) {
      varrId.push(varrWarningLock[i].mlngId + "");
    }
    this.warningReceiveService.Lock(varrId).subscribe(res => {
      if (res.code === 200) {
        this.showSuccess(res.data);
      } else {
        this.showError(res.errors);
      };
      this.mblnCheckAll = false;
      this.mstrstatus = "";
      this.Search();
      this.back();
    });
  }



  /**
 * Sự kiện khi click button mở khoá
 *
 * @author: CuongDT
 * @version: 1.0
 * @since: 2019/11/18
 */
  unlock() {
    var varrWarningUnLock = this.marrWarningDataTable.filter(item => item.mblnCheckbox === true);
    var varrId: string[] = [];
    for (let i = 0; i < varrWarningUnLock.length; i++) {
      for (let j = i + 1; j < varrWarningUnLock.length; j++) {
        if (varrWarningUnLock[i].mstrShopCode === varrWarningUnLock[j].mstrShopCode) {
          if (varrWarningUnLock[i].mintWarningLevel === varrWarningUnLock[j].mintWarningLevel) {
            this.showError(this.translate.instant('management.warningconfig.error') + this.translate.instant('management.warningconfig.Nono'));
            return false;
          }
        }
      }
      varrId.push(varrWarningUnLock[i].mlngId + "");
    }
    this.warningReceiveService.Unlock(varrId).subscribe(res => {
      if (res.code === 200) {
        this.showSuccess(res.data);
      } else {
        this.showError(res.errors);
      };
      this.mblnCheckAll = false;
      this.mstrstatus = "";
      this.Search();
      this.back();
    });
  }





  /**
 * Thông báo thành công
 *
 * @author: CuongDT
 * @version: 1.0
 * @since: 2019/11/18
 */
  showSuccess(message: string) {
    this.toastr.success(message,
      this.translate.instant('management.warningconfig.success'), {
      timeOut: 2000,
      positionClass: 'toast-top-center',
    });
  }


  /**
 * Thông báo thất bại(Lỗi)
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
 * Tải về file mẫu Excel
 *
 * @author: CuongDT
 * @version: 1.0
 * @since: 2019/11/18
 */
  download() {
    this.warningReceiveService.DownloadTemplate().subscribe(pobjData => {
      this.saveFile(pobjData.body, this.translate.instant('management.warningconfig.receivetemplate') + ".xlsx");
    });
  }
  saveFile(pobjData: any, pstrFilename?: string) {
    const blob = new Blob([pobjData], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
    fileSaver.saveAs(blob, pstrFilename);
  }



  /**
 * Mở modal tải lên dữ liệu từ file Excel
 *
 * @author: CuongDT
 * @version: 1.0
 * @since: 2019/11/18
 */
  newUpload(pobjTemplate: TemplateRef<any>, type: number) {
    this.mobjFileList = null;
    this.uploadfile = null;
    this.filenull = false;
    this.vstrFileName = "";
    this.errors = "";
    this.status = "";
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

  pageChange(pnbrP: number) {
    this.mnbrP = pnbrP;
    this.Search();
  }

  /**
 * Lấy file đang được chọn từ Input
 *
 * @author: CuongDT
 * @version: 1.0
 * @since: 2019/11/18
 */
  selectFile(pobjEvent) {
    this.mobjFileList = pobjEvent.target.files;
    this.filenull = false;
    this.vstrFileName = "";
    this.errors = "";
    this.status = "";
  }



  back() {
    this.mobjModalRef.hide();
  }


  downloadResult() {
    this.warningSendService.DownloadWarningSend(this.vstrFileName)
      .subscribe(vobjResponse => {
        this.saveFile(vobjResponse.body, this.vstrFileName);
      });
  }

  /**
 * Truyền file vào backend để thực hiện tải dữ liệu từ file Excel
 *
 * @author: CuongDT
 * @version: 1.0
 * @since: 2019/11/18
 */
  upLoad() {
    this.vstrFileName = "";
    if (this.mobjFileList) {
      this.filenull = false;
      const vfileFile: File = this.mobjFileList[0];
      const vobjFormData: FormData = new FormData();
      vobjFormData.append('file', vfileFile);
      const vobjHeaders = new HttpHeaders();
      /** In Angular 5, including the header Content-Type can invalidate your request */
      vobjHeaders.append('Content-type', 'multipart/form-data');
      vobjHeaders.append('Accept', 'application/json');
      const options = { headers: vobjHeaders };

      this.http.post(`${config.apiUrl}/management/warningreceive/upload`, vobjFormData, options)
        .subscribe((respon: any) => {
          if (respon.code === 200) {
            this.status = this.translate.instant('management.warningconfig.UploadSuccess') + respon.data.mintSumSuccessfulRecord + "/" + respon.data.mintTotal + " " + this.translate.instant('management.warningconfig.record');
            this.errors = "";
            this.mnbrP = 1;
            this.Search();
            this.vstrFileName = respon.data.fileName;
          } else if (respon.code === 500) {
            this.errors = respon.errors;
            this.status = "";
          } else {
            this.errors = respon.data.message;
            this.status = "";
          }
        });
    } else {
      this.filenull = true;
    }
  }
}

