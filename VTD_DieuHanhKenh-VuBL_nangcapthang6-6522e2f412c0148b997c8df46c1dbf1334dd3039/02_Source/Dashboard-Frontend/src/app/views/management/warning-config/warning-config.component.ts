import { Component, OnInit, ViewChild, ElementRef, TemplateRef } from '@angular/core';
import { WarningSend, WarningSendTable, SearchWarningSend } from '../../../models/Warning-config';
import { WarningSendService } from '../../../services/management/warning-send.service';
import { apParam } from '../../../models/apParam.model';
import { TreeviewItem, TreeItem, TreeviewI18n, TreeviewConfig } from 'ngx-treeview';
import { ServiceModel } from '../../../models/service.model';
import { UnitTreeviewI18n } from '../vtt-target/unit-treeview-i18n';
import { WarningContentService } from '../../../services/management/warning-content.service';
import { WarningContent } from '../../../models/WarningContent';
import { ToastrService } from 'ngx-toastr';
import { TranslateService } from '@ngx-translate/core';
import * as fileSaver from 'file-saver';
import { MatDialog } from '@angular/material';
import { WarningReceiveService } from '../../../services/management/warning-receive.service';
import { Pager } from '../../../models/Pager';
import { DialogSendComponent } from './dialog-send/dialog-send.component';
import { BsModalRef, BsModalService } from 'ngx-bootstrap';
import { config } from '../../../config/application.config';
import { HttpHeaders, HttpClient } from '@angular/common/http';
import { DropdownTreeview } from './togger/config-tree/dropdown-treeview';

@Component({
  selector: 'app-warning-config',
  templateUrl: './warning-config.component.html',
  styleUrls: ['./warning-config.component.scss'],
  providers: [
    { provide: TreeviewI18n, useClass: DropdownTreeview }
  ],
})
export class WarningConfigComponent implements OnInit {

  @ViewChild("serviceEl") serviceEl: ElementRef;

  marrWarninglevels: apParam[] = [];
  marrInformlevels: apParam[] = [];
  marrAllWarninglevels: apParam[] = [];
  marrAllInformlevels: apParam[] = [];
  marrWarningSends: WarningSend[] = [];
  marrWarningDataSends: WarningSendTable[] = [];
  marrNodeTreeviewServices: TreeviewItem[] = [];
  marrData = [];
  mobjService: ServiceModel;
  marrItemServices: ServiceModel[] = [];
  mobjNodeTreeviewService: TreeviewItem;
  mobjNodeItemService: TreeItem;
  mobjItemService: ServiceModel;
  mobjSearchWarning: SearchWarningSend = new SearchWarningSend();
  marrIndexNode = [];
  mstrKeyWord: string;
  marrService: ServiceModel[] = [];
  marrServiceOfUser: ServiceModel[] = [];
  marrNodeItemServices: TreeItem[] = [];
  marrNewNodeItemServices: TreeItem[] = [];
  marrServiceIds: number[] = [];
  mblnCheckEmail: boolean = false;
  mblnCheckSms: boolean = false;
  mnbrWarninglevel: string;
  mstrInformlevel: string;
  marrWarningContent: WarningContent[] = [];
  mobjNewWarning: WarningSend = new WarningSend();
  mnbrNewServiceId: number;
  mnbrNewWarningLv: number;
  mblnNewEmail: boolean;
  mblnNewSms: boolean;
  mnbrNewContent: number;
  mnbrNewStatus: number;
  mnbrNewInformLv: number;
  mstrstatus: string;
  mblnCheckAll: boolean = false;
  mnbrCheckStatus: number = 0;
  invalid = 0;
  mnbrP = 1;
  mobjPager: Pager;
  value: any;
  mnbrPageSize = 10;
  mnbrTotal = 0;
  valueChange: any;
  mblnCheckSmsEmail: boolean = true;
  mblnDuplicate: boolean;
  vblnCheckunlok: boolean;
  vblnCheckService: boolean;
  vblnCheckWarninglv: boolean;
  vblnCheckInformlv: boolean;
  vblnCheckSmsemail: boolean;
  mblnConfirm: boolean;
  mnbr;
  uploadfile: string;
  mobjFileList: FileList;
  filenull: boolean = false;
  vstrFileName: string = "";
  status: string = "";
  errors: string = "";
  mobjConfig = TreeviewConfig.create({
    hasFilter: true,
    hasCollapseExpand: false,
    maxHeight: 116,
  });
  config = TreeviewConfig.create({
    hasFilter: true,
    hasCollapseExpand: false,
    maxHeight: 176,
  });
  mobjModalRef: BsModalRef;

  constructor(
    private warningSendService: WarningSendService,
    private warningContentService: WarningContentService,
    private toastr: ToastrService,
    private translate: TranslateService,
    private warningReceiveService: WarningReceiveService,
    public dialog: MatDialog,
    private http: HttpClient,
    private modalService: BsModalService
  ) { }

  ngOnInit() {
    this.warningSendService.getWarningLevelNoStatus("WARNING_LEVEL").subscribe(res => {
      this.marrAllWarninglevels = res.data;
      this.warningSendService.getWarningLevelNoStatus("INFORM_LEVEL").subscribe(res => {
        this.marrAllInformlevels = res.data;
        this.getService();
      });
    });
    this.mnbrWarninglevel = "-1";
    this.mstrInformlevel = "-1";
    this.mobjSearchWarning.status = "-1";
    this.mobjSearchWarning.serviceId = -1;
  }

  /**
 * Tải dữ liệu từ api rồi đưa lên combobox và bảng
 *
 * @author: CuongDT
 * @version: 1.0
 * @since: 2019/11/18
 */
  LoadData() {
    this.marrWarningSends = [];
    this.marrWarningDataSends = [];
    this.warningSendService.getWarningLevel("WARNING_LEVEL", "1").subscribe(resw => {
      if (resw.code !== 200) {
        this.showError(resw.errors);
      } else {
        if (resw.data.length === 0) {
          this.showEmpty(this.translate.instant('management.warningconfig.WarningEmpty'));
        }
        this.marrWarninglevels = resw.data;
        this.warningSendService.getWarningLevel("INFORM_LEVEL", "1").subscribe(resi => {
          if (resi.code !== 200) {
            this.showError(resi.errors);
          } else {
            if (resi.data.length === 0) {
              this.showEmpty(this.translate.instant('management.warningconfig.InformEmpty'));
            }
            this.marrInformlevels = resi.data;
            this.warningSendService.getAllService().subscribe(ress => {
              if (ress.code !== 200) {
                this.showError(ress.errors);
              } else {
                if (ress.data.length === 0) {
                  this.showEmpty(this.translate.instant('management.warningconfig.ServiceEmpty'));
                }
                this.marrService = ress.data;
                this.marrServiceOfUser = ress.data;
                this.warningReceiveService.reloadWarning$.subscribe(res => {
                  if (res != 0) {
                    this.mnbrP = 1;
                  }
                  this.Search();
                });
              }
            })
          }
        });
      }
    });
    this.warningContentService.getAll().subscribe(res => {
      if (res.data.length === 0) {
        this.showEmpty(this.translate.instant('management.warningconfig.ContentEmpty'));
      }
      this.marrWarningContent = res.data
    });
  };



  /**
 * Tải dữ liệu chỉ tiêu từ api rồi đưa lên combobox
 *
 * @author: CuongDT
 * @version: 1.0
 * @since: 2019/11/18
 */
  getService() {
    this.marrNodeTreeviewServices = [];
    this.marrData = [];
    this.mobjService = new ServiceModel(null, null);
    this.warningSendService.getAllService().subscribe(
      vobjNext => {
        var ServiceAll: ServiceModel = new ServiceModel(null, null);
        ServiceAll.id = -2;
        if (localStorage.getItem(config.user_default_language) === 'en') {
          ServiceAll.name = "Select";
        } else {
          ServiceAll.name = "Chọn";
        }
        this.marrData.push(ServiceAll);
        var Service: ServiceModel = new ServiceModel(null, null);
        Service.id = -1;
        if (localStorage.getItem(config.user_default_language) === 'en') {
          Service.name = "All";
        } else {
          Service.name = "Tất cả";
        }
        this.marrData.push(Service);
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
        this.valueChange = -2;
        this.mobjSearchWarning.serviceId = -1;
        this.LoadData();
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

  clickSearch() {
    this.mnbrP = 1;
    this.Search();
  }
  /**
 * Tìm kiếm cấu hình gửi cảnh báo theo điều kiện lấy từ form tìm kiếm và hiển thị lên bảng
 *
 * @author: CuongDT
 * @version: 1.0
 * @since: 2019/11/18
 */
  Search() {
    this.mblnCheckAll = false;
    this.mstrstatus = "";
    if (this.mblnCheckEmail === false) {
      this.mobjSearchWarning.email = "0";
    } else {
      this.mobjSearchWarning.email = "1";
    }
    if (this.mblnCheckSms === false) {
      this.mobjSearchWarning.sms = "0";
    } else {
      this.mobjSearchWarning.sms = "1";
    }
    if (this.mblnCheckEmail === false && this.mblnCheckSms === false) {
      this.mobjSearchWarning.sms = "-1";
    }
    if (this.mstrInformlevel !== "-1") {
      this.mobjSearchWarning.informLevel = this.mstrInformlevel;
    } else {
      this.mobjSearchWarning.informLevel = null;
    }
    if (this.mnbrWarninglevel !== "-1") {
      this.mobjSearchWarning.warningLevel = this.mnbrWarninglevel;
    } else {
      this.mobjSearchWarning.warningLevel = null;
    }
    if (this.mobjSearchWarning.serviceId === -2) {
      this.mobjSearchWarning.serviceId = null;
    }
    this.warningSendService.getByCondition(this.mobjSearchWarning, this.mnbrP, this.mnbrPageSize).subscribe(res => {
      this.marrWarningSends = res.data;
      this.marrWarningDataSends = [];
      this.mnbrTotal = res.totalRow;
      for (let i = 0; i < this.marrWarningSends.length; i++) {
        this.marrWarningDataSends.push(new WarningSendTable());
        let b = this.marrWarningSends[i].mintInformLevel + "";
        this.marrWarningDataSends[i].mintInformLevel = this.marrAllInformlevels.filter(item => item.code === b)[0];
        this.marrWarningDataSends[i].mintEmail = this.marrWarningSends[i].mintEmail;
        this.marrWarningDataSends[i].mintSms = this.marrWarningSends[i].mintSms;
        let c = this.marrWarningSends[i].mintWarningLevel + "";
        this.marrWarningDataSends[i].mintWarningLevel = this.marrAllWarninglevels.filter(item => item.code === c)[0];
        this.marrWarningDataSends[i].mlngId = this.marrWarningSends[i].mlngId;
        this.marrWarningDataSends[i].mlngIdContent = this.marrWarningSends[i].mlngIdContent;
        for (let j = 0; j < this.marrService.length; j++) {
          if (this.marrWarningSends[i].mlngServiceId === -1) {
            let mobjAllWarningSV = new ServiceModel(null, null);
            mobjAllWarningSV.id = -1;
            mobjAllWarningSV.name = "Tất cả";
            this.marrWarningDataSends[i].mlngServiceId = mobjAllWarningSV;
          } else if (this.marrWarningSends[i].mlngServiceId === this.marrService[j].id) {
            this.marrWarningDataSends[i].mlngServiceId = this.marrService[j];
            break;
          }
        }
        this.marrWarningDataSends[i].mstrUser = this.marrWarningSends[i].mstrUser;
        this.marrWarningDataSends[i].mstrStatus = this.marrWarningSends[i].mstrStatus;
        this.marrWarningDataSends[i].mblnCheckbox = false;
      };
    }, error => {
      console.log(error)
    });
    this.mobjPager = new Pager(this.mnbrP, this.mnbrPageSize);
  }



  /**
 * Tạo mới dữ liệu và mở modal thêm mới
 *
 * @author: CuongDT
 * @version: 1.0
 * @since: 2019/11/18
 */
  OpenNew() {
    this.marrNewNodeItemServices = [];
    this.marrNewNodeItemServices = this.marrNodeTreeviewServices;
    this.mobjNewWarning = new WarningSend();
    this.mobjNewWarning.mlngId = -1;
    const vdialog = this.dialog.open(DialogSendComponent, {
      data: {
        warning: this.mobjNewWarning,
        marr: this.marrNewNodeItemServices,
        content: this.marrWarningContent,
        inform: this.marrInformlevels,
        warninglv: this.marrWarninglevels
      }
    });
    // vdialog.afterClosed().subscribe(result => {
    //   this.mnbrP = 1;
    //   this.Search();
    // });
  }


  /**
 * Truyền các dữ liệu lên modal sửa cấu hình cảnh báo
 *
 * @author: CuongDT
 * @version: 1.0
 * @since: 2019/11/18
 */
  Edit(w: WarningSendTable) {
    this.marrNewNodeItemServices = this.marrNodeTreeviewServices.filter(item => item.value !== -2);
    this.mobjNewWarning = this.marrWarningSends.filter(item => item.mlngId === w.mlngId)[0];
    if (this.mobjNewWarning.mintEmail === 1) {
      this.mblnNewEmail = true;
    } else {
      this.mblnNewEmail = false;
    }
    if (this.mobjNewWarning.mintSms === 1) {
      this.mblnNewSms = true;
    } else {
      this.mblnNewSms = false;
    }
    this.mnbrNewContent = parseInt(this.mobjNewWarning.mlngIdContent);
    this.mnbrNewWarningLv = this.mobjNewWarning.mintWarningLevel;
    this.mnbrNewInformLv = this.mobjNewWarning.mintInformLevel;
    this.value = this.mobjNewWarning.mlngServiceId;
    const vdialog = this.dialog.open(DialogSendComponent, {
      data: {
        warning: this.mobjNewWarning,
        marr: this.marrNewNodeItemServices,
        content: this.marrWarningContent,
        inform: this.marrInformlevels,
        warninglv: this.marrWarninglevels,
        service: w.mlngServiceId.name
      }
    });
    // vdialog.afterClosed().subscribe(result => {
    //   this.Search();
    // });
  }

  /**
  * Sự kiện khi click button khoá
  *
  * @author: CuongDT
  * @version: 1.0
  * @since: 2019/11/18
  */
  lock() {
    var varrWarningLock = this.marrWarningDataSends.filter(item => item.mblnCheckbox === true);
    var varrId: string[] = [];
    for (let i = 0; i < varrWarningLock.length; i++) {
      varrId.push(varrWarningLock[i].mlngId + "");
    }
    this.warningSendService.Lock(varrId).subscribe(res => {
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
    var varrWarningUnLock = this.marrWarningDataSends.filter(item => item.mblnCheckbox === true);
    var id: string[] = [];
    for (let i = 0; i < varrWarningUnLock.length; i++) {
      for (let j = i + 1; j < varrWarningUnLock.length; j++) {
        if (varrWarningUnLock[i].mlngServiceId === varrWarningUnLock[j].mlngServiceId) {
          if (varrWarningUnLock[i].mintWarningLevel === varrWarningUnLock[j].mintWarningLevel) {
            this.showError(this.translate.instant('management.warningconfig.error') + this.translate.instant('management.warningconfig.Nono'));
            return false;
          }
        }
      }
      id.push(varrWarningUnLock[i].mlngId + "");
    };
    this.warningSendService.Unlock(id).subscribe(res => {
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
  * Sự kiện khi click chọn checkbox tất cả (Bảng dữ liệu)
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
    for (let i = 0; i < this.marrWarningDataSends.length; i++) {
      if (vblnCheck === true) {
        this.marrWarningDataSends[i].mblnCheckbox = true;
      } else {
        this.marrWarningDataSends[i].mblnCheckbox = false;
      }
    }
    this.CheckStatus();
  }



  /**
  * Sự kiện khi click checkbox chọn các cấu hình cảnh báo
  *
  * @author: CuongDT
  * @version: 1.0
  * @since: 2019/11/18
  */
  select(id: number) {
    for (let i = 0; i < this.marrWarningDataSends.length; i++) {
      if (this.marrWarningDataSends[i].mlngId === id) {
        if (this.marrWarningDataSends[i].mblnCheckbox === true) {
          this.marrWarningDataSends[i].mblnCheckbox = false;
        } else {
          this.marrWarningDataSends[i].mblnCheckbox = true;
        }
        break;
      }
    }
    var vblncheck = true;
    this.marrWarningDataSends.forEach(mobj => {
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
  * Kiểm tra các cấu hình được chọn và cho phép mở khoá/khoá
  *
  * @author: CuongDT
  * @version: 1.0
  * @since: 2019/11/18
  */
  CheckStatus() {
    this.mstrstatus = "0";
    var marr = this.marrWarningDataSends.filter(item => item.mblnCheckbox === true);
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
  * Lấy dữ liệu của tree chỉ tiêu
  *
  * @author: CuongDT
  * @version: 1.0
  * @since: 2019/11/18
  */
  Onchange(value: any) {
    this.mobjSearchWarning.serviceId = value;
  }


  /**
  * Lấy dữ liệu của tree chỉ tiêu(Modal)
  *
  * @author: CuongDT
  * @version: 1.0
  * @since: 2019/11/18
  */
  OnchangeNew(value: any) {
    this.mobjNewWarning.mlngServiceId = value;
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
  * Thông báo trống dữ liệu
  *
  * @author: CuongDT
  * @version: 1.0
  * @since: 2019/11/18
  */
  showEmpty(message: string) {
    this.toastr.error(message,
      this.translate.instant('management.warningconfig.Empty'), {
      timeOut: 2000,
      positionClass: 'toast-top-center',
    });
  }



  /**
  * Tải về mẫu Excel và cho phép tải về
  *
  * @author: CuongDT
  * @version: 1.0
  * @since: 2019/11/18
  */
  download() {
    this.warningSendService.DownloadTemplate().subscribe(pobjData => {
      this.saveFile(pobjData.body, this.translate.instant('management.warningconfig.sendtemplate') + ".xlsx");
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


  pageChange(pnbrP: number) {
    this.mnbrP = pnbrP;
    this.Search();
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
      this.http.post(`${config.apiUrl}/management/warningsend/upload`, vobjFormData, options)
        .subscribe((respon: any) => {
          if (respon.code === 200) {
            this.status = this.translate.instant('management.warningconfig.UploadSuccess') + respon.data.sumSuccessfulRecord + "/" + respon.data.mintTotal + " " + this.translate.instant('management.warningconfig.record');
            this.errors = "";
            this.mnbrP = 1;
            this.Search();
            this.vstrFileName = respon.data.filename;
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
}
