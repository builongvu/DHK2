import { AfterViewInit, Component, ElementRef, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { ProductService } from '../../../services/management/product.service';
import { ProductModel } from '../../../models/Product.model';
import { config } from '../../../config/application.config';
import { DownloadService } from '../../../services/management/download.service';
import { ChannelService } from '../../../services/management/channel.service';
import { Channel } from '../../../models/Channel.model';
import { FormArray, FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { DateAdapter, MAT_DATE_FORMATS, MAT_DATE_LOCALE } from '@angular/material/core';
import { MatDatepicker } from '@angular/material/datepicker';
import { MomentDateAdapter } from '@angular/material-moment-adapter';
import * as _moment from 'moment';
import { Moment } from 'moment';
import { GroupsService } from '../../../services/management/group.service';
import { GroupModel } from '../../../models/group.model';
import { ServiceModel } from '../../../models/service.model';
import { ServiceService } from '../../../services/management/service.service';
import { Pager } from '../../../models/Pager';
import { BsModalRef, BsModalService } from 'ngx-bootstrap';
import { PlanmonthlyService } from '../../../services/management/planmonthly.service';
import { PlanmonthlyModel } from '../../../models/Planmonthly.model';
import { SearchModel } from '../../../models/Search.model';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import * as fileSaver from 'file-saver';
import { SortTable } from '../../../models/SortTable';
import { UnitStaffService } from '../../../services/management/unitStaff.service';
import { TreeItem, TreeviewI18n, TreeviewItem } from 'ngx-treeview';
import { UnitTreeviewI18n } from './unit-treeview-i18n';
import { TranslateService } from '@ngx-translate/core';
import { DownloadModel } from '../../../models/download.model';
import { MatCheckboxChange } from '@angular/material';
import { DeleteService } from '../../../services/management/delete.service';
import { CycleService } from '../../../services/management/cycle.service';
import { DashboardModel } from '../../../models/dashboard.model';
import { el } from '@angular/platform-browser/testing/src/browser_util';

// format date
export const MY_FORMATS = {
  parse: {
    dateInput: 'MM/YYYY',
  },
  display: {
    dateInput: 'MM/YYYY',
    monthYearLabel: 'MMM YYYY',
    dateA11yLabel: 'LL',
    monthYearA11yLabel: 'MMMM YYYY',
  },
};

@Component({
  templateUrl: 'vtt-target.component.html',
  styleUrls: ['vtt-target.component.scss'],
  providers: [
    { provide: DateAdapter, useClass: MomentDateAdapter, deps: [MAT_DATE_LOCALE] },
    { provide: MAT_DATE_FORMATS, useValue: MY_FORMATS },
    { provide: TreeviewI18n, useClass: UnitTreeviewI18n }
  ],
})

export class VttTargetComponent implements OnInit, AfterViewInit {
  @ViewChild('focus') focus: ElementRef;

  constructor(private http: HttpClient,
    private productService: ProductService,
    private downloadService: DownloadService,
    private channelService: ChannelService,
    private groupsService: GroupsService,
    private serviceService: ServiceService,
    private modalService: BsModalService,
    private planmonthlyService: PlanmonthlyService,
    private router: Router,
    private fb: FormBuilder,
    private toastr: ToastrService,
    private deleteService: DeleteService,
    private unittreeService: UnitStaffService,
    private translate: TranslateService,
    private cycleService: CycleService) {
  }

  mdtMinDate = new Date(1899, 0, 1);
  mdtMaxDate = new Date(2101, 0, 1);

  mobjForm: FormGroup;
  mobjProducts: ProductModel[] = [];
  mnbrP = 1;
  mnbrTotal;
  ckall = false;
  mnbrPageSize = config.pageSize;
  marrChannels: Channel[] = [];
  marrGroups: GroupModel[] = [];
  marrServices: ServiceModel[] = [];
  marrDataTable: PlanmonthlyModel[] = [];
  mnbrProductId;
  mnbrGroupId;
  mobjModalRef: BsModalRef;
  mobjSearchModel: SearchModel;
  mnbrChannelId: number;
  marrServiceIds: number[] = [];
  mobjPager: Pager;
  mobjFileList: FileList;
  mnbrCode;
  mdtDate = new FormControl(_moment());
  mobjDataUpload;
  marrArrReadponly: boolean[] = [];
  mblnIsReadOnly: boolean;
  mblnShowSaveButton = false;
  mblnCheckFileNull = false;
  mblnChec = false;
  mstrCongVan: string;
  mobjSortTable: SortTable;
  mstrColumnName: string;
  mstrTypeSort: string;
  mstrKeyWord: string;
  mblnIsClickHere = false;

  marrIndexNode = [];
  mobjNodeTreeviewService: TreeviewItem;
  marrNodeTreeviewServices: TreeviewItem[] = [];
  mobjNodeItemService: TreeItem;
  marrNodeItemServices: TreeItem[] = [];
  mobjConfig = {
    hasAllCheckBox: true,
    hasFilter: true,
    hasCollapseExpand: false,
    maxHeight: 230,
    decoupleChildFromParent: true,
  };

  marrData = [];
  mobjItemService: ServiceModel;
  marrItemServices: ServiceModel[] = [];

  ONE_DAY_IN_MILISECONDS = 86400000;
  mdtDateInput;
  mblnIsNaNumber: boolean;
  mblnIsClickSearch = false;
  mnbrSumSuccessfulRecord;
  mnbrSumRecord;
  mstrResultFileName;
  mstrMessageUpload;
  mstrTypeSearch = 'VDS';
  mblnIsSelectFile = false;
  mobjService: ServiceModel;
  vdsChannelCode: string;
  wlavel: string;
  updateRecord;
  updateDatas: PlanmonthlyModel[] = [];
  updateValue;
  isClick = false;
  mblnvaLi = false;
  isNullDate = false;
  disable = true;
  hle = 0;
  arrTemp: PlanmonthlyModel[] = [];
  marrDataDelte: PlanmonthlyModel[] = [];
  checked = false;
  cycles: any[] = [];
  cycleCode: string;
  cycleCodeSearch;
  quarter;
  startView: 'month' | 'year' | 'multi-year';
  yearOut: boolean;

  ngOnInit() {
    this.yearOut = false;
    this.quarter = 1;
    this.startView = 'year';
    this.getCycles();
    this.mobjForm = this.fb.group({
      formArray: this.fb.array([])
    });
    // this.mdtDate = new FormControl(null);
    this.mdtDate = new FormControl(_moment());
    this.marrDataDelte = [];
    // this.marrDataTable = null;
    this.mnbrChannelId = null;
    this.mnbrGroupId = null;
    this.mnbrProductId = null;
    this.mstrColumnName = null;
    this.mstrTypeSort = null;
    this.mobjSortTable = null;
    this.mstrColumnName = 'month';
    this.mstrTypeSort = 'desc';
    this.vdsChannelCode = null;
    // this.cycleCode = '02';
    setTimeout(() => {
      this.cycleCode = '02';
    }, 50);
    this.cycleCodeSearch = '02';


    this.getChannels();
    this.getProducts();
    this.getGroups();
    // this.getServices();
    // this.showServices();
    this.getServiceByGroupId();
    setTimeout(() => {
      this.search();
    }, 100);

    this.setDefaultQuarter();

  }

  ngAfterViewInit(): void {
    // this.focus.nativeElement.focus;
  }

  /**
   * Không Xử lý gì, dùng để cho tooltip trong table hiện đúng chỗ
   *
   * @author: NgocTH
   * @version: 1.0
   * @since: 2019/09/13
   */
  noMethod() {
  }

  /**
   * Nhận tháng được chọn trong combobox month
   *
   * @author: TuanND
   * @version: 1.0
   * @since: 2019/09/13
   * @param: $event: lấy tháng nhập
   * @param: pobjDatepicker: lấy tháng chọn
   */
  chosenMonthHandler($event, pobjDatepicker: MatDatepicker<Moment>) {
    this.mdtDate.setValue($event);
    pobjDatepicker.close();
    this.validateDate();
  }

  /**
   * Xử lý khi click 'tìm kiếm'
   *
   * @author: NgocTH
   * @version: 1.0
   * @since: 2019/09/13
   */
  clickSearch() {
    this.mblnIsClickSearch = true;
    this.mstrColumnName = 'month';
    this.mstrTypeSort = 'desc';
    this.mnbrP = 1;
    this.search();
  }

  /**
   * Thay đổi dữ liệu theo trang khi thay đổi trang
   *
   * @author: NgocTH
   * @version: 1.0
   * @since: 2019/09/13
   * @param: pnbrP: số trang
   */
  pageChange(pnbrP: number) {
    this.mnbrP = pnbrP;
    this.search();
  }

  /**
   * Lấy toàn bộ kênh từ api hiển thị lên combobox kênh
   *
   * @author: NgocTH
   * @version: 1.0
   * @since: 2019/09/13
   */
  getChannels() {
    this.channelService.getChannels().subscribe(
      vobjNext => (this.marrChannels = vobjNext),
      error => (this.marrChannels = [])
    );
  }

  /**
   * Lấy toàn bộ sản phẩm từ api hiển thị lên combobox sản phẩm
   *
   * @author: NgocTH
   * @version: 1.0
   * @since: 2019/09/13
   */
  getProducts() {
    this.productService.getProducts().subscribe(
      vobjNext => (this.mobjProducts = vobjNext),
      error => (console.error('không có product'))
    );
  }

  /**
   * Lấy toàn bộ nhóm chỉ tiêu từ api
   *
   * @author: NgocTH
   * @version: 1.0
   * @since: 2019/09/13
   */
  getGroups() {
    this.groupsService.getGroups().subscribe(
      vobjNext => (this.marrGroups = vobjNext),
      error => (console.error('không có group'))
    );
  }

  /**
   * Lấy toàn bộ nhóm chỉ tiêu theo productId đã chọn từ api hiển thị lên combobox nhóm chỉ tiêu
   *
   * @author: NgocTH
   * @version: 1.0
   * @since: 2019/09/13
   * @return: Dữ liệu nhóm chỉ tiêu theo productId
   */
  getGroupByProductId() {
    this.groupsService.getGroupById(this.mnbrProductId).subscribe(
      vobjNext => (this.marrGroups = vobjNext['data']),
      error => (console.error('không có group'))
    );
  }

  /**
   * Hiển thị danh sách nhóm chỉ tiêu lên combobox nhóm chỉ tiêu
   *
   * @author: NgocTH
   * @version: 1.0
   * @since: 2019/09/13
   * @return: Dữ liệu nhóm chỉ tiêu
   */
  showGroups() {
    if (!this.mnbrProductId) {
      this.getGroups();
    } else {
      this.getGroupByProductId();
    }
  }

  /**
   * Lấy toàn bộ chỉ tiêu theo groupId đã chọn từ api
   *
   * @author: NgocTH
   * @version: 1.0
   * @since: 2019/09/13
   * @return: Dữ liệu chỉ tiêu theo productId
   */
  getServiceByGroupId() {
    this.marrNodeTreeviewServices = [];
    this.marrData = [];
    this.mobjService = new ServiceModel(this.mnbrGroupId, this.vdsChannelCode);
    this.serviceService.getServicesByGroupId(this.mobjService).subscribe(
      vobjNext => {
        this.marrData = vobjNext['data'];
        this.marrItemServices = this.createNode(this.marrItemServices, this.marrData);
        // this.data = this.itemServices;
        this.createTree(this.marrItemServices, this.marrData);
        this.marrItemServices.forEach(vobjValue => {
          this.mobjNodeTreeviewService = new TreeviewItem(this.forwardData(vobjValue, this.mobjNodeItemService, this.marrNodeItemServices));
          this.marrNodeTreeviewServices.push(this.mobjNodeTreeviewService);
        });
      },
      vobjErorr => {
        console.log('no data');
      }
    );
  }

  /**
   * Hiển thị danh sách chỉ tiêu lên combobox chỉ tiêu
   *
   * @author: NgocTH
   * @version: 1.0
   * @since: 2019/09/13
   * @return: Dữ liệu chỉ tiêu
   */
  showServices() {
    // this.marrNodeTreeviewServices = [];
    this.getServiceByGroupId();
  }

  /**
   * Đưa dữ liệu theo form tìm kiếm vào file xlsx rồi download về máy
   *
   * @author: NgocTH
   * @version: 1.0
   * @since: 2019/09/13
   */
  fileDownload() {
    this.cycleCodeSearch = this.cycleCode;
    this.mnbrPageSize = null;
    this.searchModalForm();
    const vobjFileRequest: SearchModel = this.mobjSearchModel;
    let download: DownloadModel;
    download = new DownloadModel(this.mobjSearchModel.receivedDate);
    console.log(vobjFileRequest);
    this.downloadService.downloadVDS(vobjFileRequest)
      .subscribe(vobjResponse => {
        let vstrFilename: string;
        if (this.cycleCode == '02') {
          vstrFilename = this.translate.instant('management.target.table.header.month')
            + (this.mdtDate.value._d.getMonth() + 1) + '/'
            + (this.mdtDate.value._d.getFullYear()) + '_' + this.translate.instant('management.target.file.nameVDS');
        } else if (this.cycleCode == '03') {
          vstrFilename = this.translate.instant('management.target.table.header.quarter')
            + (this.quarter == 1 ? 'Q1' : this.quarter == 2 ? 'Q2' : this.quarter == 3 ? 'Q3' : 'Q4') + '/'
            + (this.mdtDate.value._d.getFullYear()) + '_' + this.translate.instant('management.target.file.nameVDS');
        } else {
          vstrFilename = this.translate.instant('management.target.table.header.year')
            + (this.mdtDate.value._d.getFullYear()) + '_' + this.translate.instant('management.target.file.nameVDS');
        }
        // }
        this.saveFile(vobjResponse.body, vstrFilename);
      });
    this.mnbrPageSize = config.pageSize;
    this.search();
  }

  /**
   * Tạo Object chứa dữ liệu trên form tìm kiếm
   *
   * @author: NgocTH
   * @version: 1.0
   * @since: 2019/09/13
   * @return: Object theo form tìm kiếm
   */
  searchModalForm() {
    if (!this.mnbrProductId) {
      this.mnbrProductId = null;
    }
    if (!this.marrServiceIds) {
      this.marrServiceIds = null;
    }

    if (!this.mnbrGroupId) {
      this.mnbrGroupId = null;
    }

    if (!this.mnbrChannelId) {
      this.mnbrChannelId = null;
    }

    if (!this.mstrCongVan) {
      this.mstrCongVan = null;
    }
    this.mobjPager = new Pager(this.mnbrP, this.mnbrPageSize);
    this.mobjSortTable = new SortTable(this.mstrColumnName, this.mstrTypeSort);
    if (!this.mdtDate.value) {
      // tslint:disable-next-line:max-line-length
      this.mobjSearchModel = new SearchModel(null, this.vdsChannelCode, this.mnbrGroupId, this.mnbrProductId, this.marrServiceIds, this.mstrCongVan,
        null, null, this.mstrTypeSearch, this.mobjPager, this.mobjSortTable, this.cycleCodeSearch);
    } else {
      this.mdtDateInput = this.mdtDate.value._d.getTime();
      // tslint:disable-next-line:max-line-length
      this.mobjSearchModel = new SearchModel(this.mdtDateInput, this.vdsChannelCode, this.mnbrGroupId, this.mnbrProductId, this.marrServiceIds, this.mstrCongVan,
        null, null, this.mstrTypeSearch, this.mobjPager, this.mobjSortTable, this.cycleCodeSearch);
    }
    console.log('model search: ', this.mobjSearchModel);
  }

  /**
   * Lưu file download theo tên Tháng_ChiTieuVDS.xlsx
   *
   * @author: NgocTH
   * @version: 1.0
   * @since: 2019/09/13
   */
  saveFile(pobjData: any, pstrFilename?: string) {
    const vobjBlob = new Blob([pobjData], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
    fileSaver.saveAs(vobjBlob, pstrFilename);
  }

  /**
   * Lưu file đã chọn vào mobjFileList để upload
   *
   * @author: NgocTH
   * @version: 1.0
   * @since: 2019/09/13
   * @param: pobjEvent - file chọn từ máy
   */
  selectFile(pobjEvent) {
    this.mnbrCode = null;
    this.mblnIsSelectFile = false;
    if (!this.mobjFileList) {
      this.mblnIsSelectFile = true;
    } else if (this.mobjFileList[0] !== pobjEvent.target.files[0]) {
      this.mblnIsSelectFile = false;
    }
    this.mobjFileList = pobjEvent.target.files;
    this.mblnCheckFileNull = false;
  }

  /**
   * Ẩn dialog upload và reset dữ liệu
   *
   * @author: NgocTH
   * @version: 1.0
   * @since: 2019/09/13
   */
  onBack() {
    this.mblnIsClickHere = false;
    this.mnbrCode = null;
    this.mobjFileList = null;
    this.mblnCheckFileNull = false;
    this.mobjModalRef.hide();
  }

  /**
   * Upload dữ liệu theo file đã chọn
   *
   * @author: NgocTH
   * @version: 1.0
   * @since: 2019/09/13
   */
  upLoad() {
    this.mblnIsSelectFile = true;
    if (this.mobjFileList) {
      const vfileFile: File = this.mobjFileList[0];
      const vobjFormData: FormData = new FormData();
      vobjFormData.append('file', vfileFile);
      const vobjHeaders = new HttpHeaders();
      /** In Angular 5, including the header Content-Type can invalidate your request */
      vobjHeaders.append('Content-type', 'multipart/form-data');
      vobjHeaders.append('Accept', 'application/json');
      const vobjOptions = { headers: vobjHeaders };
      this.http.post(config.uploadFileVDS_API, vobjFormData, vobjOptions)
        .subscribe(
          vobjData => (this.resultUpload(vobjData)),
          vobjError => (console.log(vobjError)),
          () => {
            this.search();
          }
        );
    } else {
      this.mblnCheckFileNull = true;
    }
    return this.mobjDataUpload;
  }

  /**
   * Download file tổng hợp lỗi sau khi upload
   *
   * @author: NgocTH
   * @version: 1.0
   * @since: 2019/09/13
   * @param: pstrNameFileResult - Tên file kết quả lấy từ api
   */
  downloadResult(pstrNameFileResult: string) {
    this.mblnIsClickHere = true;
    let vstFfilename: string;
    this.http.get(`${config.download_Result_File_API}?fileName=${pstrNameFileResult}`);
    this.downloadService.downloadResult(pstrNameFileResult)
      .subscribe(vobjResponse => {
        vstFfilename = this.mstrResultFileName;
        this.saveFile(vobjResponse.body, vstFfilename);
      });
  }

  /**
   * Tách thông tin từ api sau khi upload: message, code
   *
   * @author: NgocTH
   * @version: 1.0
   * @since: 2019/09/13
   * @param: pobjData - dữ liệu sau khi upload
   */
  resultUpload(pobjData) {
    this.mobjDataUpload = pobjData['data'];
    this.mnbrCode = pobjData.code;
    this.mstrMessageUpload = this.mobjDataUpload['message'];
    this.showError(this.mnbrCode);
  }

  /**
   * Hiển thị thông báo upload
   *
   * @author: NgocTH
   * @version: 1.0
   * @since: 2019/09/13
   * @param: pnbrCode - status code từ api
   */
  showError(pnbrCode: number) {
    if (pnbrCode === undefined) {
      console.log('Waining: code is undefine');
    } else if (pnbrCode === 200) {
      this.mnbrSumSuccessfulRecord = this.mobjDataUpload.sumSuccessfulRecord;
      this.mnbrSumRecord = this.mobjDataUpload.sumRecord;
      this.mstrResultFileName = this.mobjDataUpload.resultFileName;
    } else {
      this.mobjFileList = null;
    }
    this.mblnIsSelectFile = true;
  }

  /**
   * Mở dialog upload
   *
   * @author: NgocTH
   * @version: 1.0
   * @since: 2019/09/13
   */
  confirm(pobjTemplate: TemplateRef<any>) {
    this.mobjModalRef = this.modalService.show(pobjTemplate, {
      ignoreBackdropClick: true
    }
    );
  }

  /**
   * Mở dialog update
   *
   * @author: NgocTH
   * @version: 1.0
   * @since: 2019/09/13
   */
  confirmUpdate(pobjTemplate: TemplateRef<any>) {
    if (this.mblnIsNaNumber) {
      this.showIsNaN();
      return;
    }
    this.mobjModalRef = this.modalService.show(pobjTemplate, {
      ignoreBackdropClick: true
    }
    );
  }

  /**
   * Tìm kiếm dữ liệu theo thông tin nhập từ giao diện
   *
   * @author: NgocTH
   * @version: 1.0
   * @since: 2019/09/13
   */
  search() {
    this.cycleCodeSearch = this.cycleCode;
    // console.log('cycleOcde: ', this.cycleCode);
    this.hle = 0;
    this.disable = true;
    this.checked = false;
    this.ckall = false;
    this.marrDataDelte = [];
    // this.marrDataTable = null;
    if (!this.mdtDate.invalid) {
      this.mnbrPageSize = config.pageSize;
      this.searchModalForm();
      this.showDataSearch(this.mobjSearchModel);
    }
  }

  /**
   * Thông báo khi tháng nhập sai dữ liệu hoặc nhập ngoài khoảng cho phép
   *
   * @author: NgocTH
   * @version: 1.0
   * @since: 2019/09/13
   */
  checkMonthInput() {
    this.toastr.warning(this.translate.instant('management.target.toastr.month'),
      this.translate.instant('management.target.toastr.title'),
      {
        timeOut: 3000,
        positionClass: 'toast-top-center',
      });
    this.focus.nativeElement.focus();
  }

  /**
   * Lấy dữ liệu từ api theo form tìm kiếm
   *
   * @author: NgocTH
   * @version: 1.0
   * @since: 2019/09/13
   * @param: pobjData - Object chứa thông tin nhập tìm kiếm
   */
  showDataSearch(pobjData) {
    this.planmonthlyService.getDatas(pobjData).subscribe(
      vobjNext => {
        console.log(vobjNext);
        this.marrDataTable = vobjNext['data'];
        this.marrDataTable.map(
          table => {
            table.checked = false;
            return table;
          }
        );
        this.mnbrTotal = vobjNext['totalRow'];
        this.setFormGroup();
      }, () => this.marrDataTable = null);
  }

  /**
   * Validate dữ liệu nhập vào lúc update
   *
   * @author: TuanND
   * @version: 1.0
   * @since: 2019/09/13
   * @param: pstrInput - Dữ liệu nhập vào
   * @param: pnbrIndex - Vị trí dữ liệu trong table
   */
  validateInput(pstrInput: any, pnbrIndex) {
    this.mblnShowSaveButton = true;
    this.mblnChec = true;
    const vstrValue = pstrInput.value;
    const vstrA = vstrValue.split(',').join('');
    if (isNaN(vstrA) === true || vstrA === '' || vstrA < 0) {
      this.mblnIsNaNumber = true;
      return;
    } else {
      const vobjFormArray = this.mobjForm.get('formArray') as FormArray;
      vobjFormArray.controls[pnbrIndex].patchValue({ fSchedule: vstrA });
    }
    for (const vobjData of this.formArray.value) {
      if (isNaN(vobjData.fSchedule || vobjData.fSchedule === '' || vobjData.fSchedule < 0)) {
        this.mblnIsNaNumber = true;
        return;
      } else {
        this.mblnIsNaNumber = false;
      }
    }
  }

  /**
   * Hiển thị thông báo khi dữ liệu update nhập vào lỗi
   *
   * @author: TuanND
   * @version: 1.0
   * @since: 2019/09/13
   */
  showIsNaN() {
    this.toastr.warning(this.translate.instant('management.target.toastr.updateData'),
      this.translate.instant('management.target.toastr.title'), {
      timeOut: 3000,
      positionClass: 'toast-top-center'
    });
  }

  /**
   * Đưa toàn bộ dữ liệu trong table trên giao diện vào form để thực hiện update
   *
   * @author: TuanND
   * @version: 1.0
   * @since: 2019/09/13
   */
  setFormGroup() {
    this.marrArrReadponly.length = 0;
    this.mobjForm = this.fb.group({
      formArray: this.fb.array([])
    });
    if (this.marrDataTable) {
      // tslint:disable-next-line:no-shadowed-variable
      this.marrDataTable.forEach(vobjData => {
        const vobjFormData = this.fb.group({
          id: vobjData.id,
          fSchedule: vobjData.fSchedule,
          serviceId: vobjData.serviceId,
          vdsChannelCode: vobjData.channelCode,
          shopCode: vobjData.shopCode,
          staffCode: vobjData.staffCode,
          update: vobjData.update,
          prdId: vobjData.prdId
        });
        this.formArray.push(vobjFormData);
      });
    }
    const vobjData = this.formArray.value;
    let vnbrCount = 0;
    for (const vnbrIndex of vobjData) {
      if (vnbrIndex.update === true) {
        this.mblnIsReadOnly = false;
        this.marrArrReadponly.push(this.mblnIsReadOnly);
        vnbrCount++;
      } else {
        this.mblnIsReadOnly = true;
        this.marrArrReadponly.push(this.mblnIsReadOnly);
      }
    }
  }

  /**
   * Update dữ liệu theo dữ liệu nhập vào
   *
   * @author: TuanNd
   * @version: 1.0
   * @since: 2019/09/13
   */
  onSave() {
    if (this.mobjForm.invalid) {
      this.showIsNaN();
      this.showUploadedFailse();
      return;
    }
    const vobjData = this.formArray.value;
    let index = 0;
    for (let i = 0; i < this.marrDataTable.length; i++) {
      if (vobjData[i].fSchedule != this.marrDataTable[i].fSchedule) {
        this.arrTemp.push(vobjData[i]);
        index++;
      }
      console.log('show log', this.arrTemp);
    }
    if (this.cycleCodeSearch == '02') {
      this.planmonthlyService.updateMonthFschedule(this.arrTemp)
        .subscribe(
          next => {
            this.updateRecord = next['data'];
            this.showUploadedOk();
            this.arrTemp = [];
            this.search();
          },
          error => {
            this.showUploadedFailse();
          }
        );
    } else if (this.cycleCodeSearch == '03') {
      this.planmonthlyService.updateQuarterFschedule(this.arrTemp)
        .subscribe(
          next => {
            this.updateRecord = next['data'];
            this.showUploadedOk();
            this.arrTemp = [];
            this.search();
          },
          error => {
            this.showUploadedFailse();
          }
        );
    } else {
      this.planmonthlyService.updateYearFschedule(this.arrTemp)
        .subscribe(
          next => {
            this.updateRecord = next['data'];
            this.showUploadedOk();
            this.arrTemp = [];
            this.search();
          },
          error => {
            this.showUploadedFailse();
          }
        );
    }
    this.mobjModalRef.hide();
    // this.showUploadedOk();
    this.showUI();
  }

  /**
   * Chặn các chức năng khác khi đang update
   *
   * @author: TuanND
   * @version: 1.0
   * @since: 2019/09/13
   */
  showUI() {
    this.mblnShowSaveButton = false;
    this.mblnChec = false;
  }

  /**
   * Quay lại màn hình tìm kiếm
   *
   * @author: NgocTH
   * @version: 1.0
   * @since: 2019/09/13
   * @param: pnbrCode - status code từ api
   */
  clickCancel() {
    if (this.hle > 0) {
      this.disable = false;
    }
    this.mblnShowSaveButton = false;
    this.mblnChec = false;
    this.setFormGroup();
  }

  /**
   * Tạo danh sách form dữ liệu update
   *
   * @author: TuanND
   * @version: 1.0
   * @since: 2019/09/13
   */
  get formArray(): FormArray {
    return this.mobjForm.get('formArray') as FormArray;
  }

  /**
   * Hiển thị thông báo update thành công
   *
   * @author: NgocTH
   * @version: 1.0
   * @since: 2019/09/13
   */
  showUploadedOk() {
    // tslint:disable-next-line:max-line-length
    this.toastr.success(this.translate.instant('management.target.toastr.inforUpdate') + ' ' + this.updateRecord + ' ' + this.translate.instant('management.target.toastr.record'),
      this.translate.instant('management.target.toastr.title'), {
      timeOut: 2000,
      positionClass: 'toast-top-center',
    });
  }

  /**
   * Hiển thị thông báo update thất bại
   *
   * @author: NgocTH
   * @version: 1.0
   * @since: 2019/09/13
   */
  showUploadedFailse() {
    this.toastr.error(this.translate.instant('management.target.toastr.updateFalse'),
      this.translate.instant('management.target.toastr.title'), {
      timeOut: 3000,
      positionClass: 'toast-top-center',
    });
  }


  /**
   * Set lại Date khi thay đổi tháng nhập
   *
   * @author: NgocTH
   * @version: 1.0
   * @since: 2019/09/13
   * @param: $event - tháng nhập
   */
  dateChange(value: any) {
    console.log('event: ', value.target.value);
    const valueString = value.target.value;
    if (valueString == '') {
      this.yearOut = false;
      this.mblnvaLi = false;
      this.isNullDate = true;
    } else {
      const arrayMonths = valueString.split('/');
      const monthS = arrayMonths[0];
      const yearS = arrayMonths[1];

      // tslint:disable-next-line:radix
      const yearN = parseInt(yearS);
      // tslint:disable-next-line:radix
      const monthN = parseInt(monthS);

      if (isNaN(yearN)) {
        this.mblnvaLi = true;
        this.yearOut = false;
        this.isNullDate = false;
      } else if (isNaN(monthN)) {
        this.mblnvaLi = true;
        this.yearOut = false;
        this.isNullDate = false;
      } else if (yearN < 1900 || yearN > 2100) {
        this.mblnvaLi = false;
        this.yearOut = true;
        this.isNullDate = false;
      } else if (monthN < 1 || monthN > 12) {
        this.yearOut = false;
        this.mblnvaLi = true;
        this.isNullDate = false;
      } else {
        this.mdtDate = new FormControl(_moment());
        this.mdtDate.value._d.setFullYear(yearN);
        this.mdtDate.value._d.setMonth(monthN - 1);
        this.yearOut = false;
        this.mblnvaLi = false;
        this.isNullDate = false;
      }
    }

    if (!value) {
      this.mdtDate = null;
    }

  }

  yearChange(value: any) {
    const valueString = value.target.value;
    // tslint:disable-next-line:radix
    const year = parseInt(valueString);
    if (valueString == '') {
      this.isNullDate = true;
      this.yearOut = false;
      this.mblnvaLi = false;
    } else if (isNaN(year)) {
      this.mblnvaLi = true;
      this.yearOut = false;
      this.isNullDate = false;
    } else if (year < 1900 || year > 2100) {
      this.mblnvaLi = false;
      this.yearOut = true;
      this.isNullDate = false;
    } else {
      this.mdtDate = new FormControl(_moment());
      this.mdtDate.value._d.setFullYear(year);
      this.yearOut = false;
      this.mblnvaLi = false;
      this.isNullDate = false;
    }
    if (!value) {
      this.mdtDate = null;
    }

  }

  /**
   * Sắp xếp dữ liệu theo cột trong table
   *
   * @author: NgocTH
   * @version: 1.0
   * @since: 2019/09/13
   * @param: pstrColumnName - tên cột trong table
   */
  setSort(pstrColumnName: string) {
    if (this.mstrColumnName !== pstrColumnName) {
      this.mstrTypeSort = 'desc';
    }
    this.mstrColumnName = pstrColumnName;
    if (this.mstrTypeSort === null) {
      this.mstrTypeSort = 'asc';
    } else if (this.mstrTypeSort === 'asc') {
      this.mstrTypeSort = 'desc';
    } else {
      this.mstrTypeSort = 'asc';
    }
    this.mobjSortTable = new SortTable(this.mstrColumnName, this.mstrTypeSort);
    // if (!this.mblnIsClickSearch) {
    //   this.marrDataTable = null;
    // } else {
    this.search();
    // }
  }

  /**
   * Lấy danh sách chỉ tiêu từ api và hiển thị lên treeview
   *
   * @author: TruyenNH
   * @version: 1.0
   * @since: 2019/09/13
   */
  // getServices() {
  //   this.marrNodeTreeviewServices = [];
  //   this.mobjService = new ServiceModel(this.mnbrGroupId);
  //   this.serviceService.getServicesByGroupId(this.mobjService).subscribe(
  //     vobjNext => {
  //       this.marrData = vobjNext['data'];
  //       this.marrItemServices = this.createNode(this.marrItemServices, this.marrData);
  //       // this.data = this.itemServices;
  //       this.createTree(this.marrItemServices, this.marrData);
  //       this.marrItemServices.forEach(vobjValue => {
  // tslint:disable-next-line:max-line-length
  //         this.mobjNodeTreeviewService = new TreeviewItem(this.forwardData(vobjValue, this.mobjNodeItemService, this.marrNodeItemServices));
  //         this.marrNodeTreeviewServices.push(this.mobjNodeTreeviewService);
  //       });
  //     }
  //   );
  // }

  /**
   * Tạo Mảng các đối tướng trong tree
   *
   * @author: TruyenNH
   * @version: 1.0
   * @since: 2019/09/13
   * @param: parrItems - danh sách Object mới
   * @param: parrDataTree - danh sách Object chỉ tiêu từ api
   * @return: parrItems - danh sách Object mới
   */
  createNode(parrItems: ServiceModel[] = [], parrDataTree: ServiceModel[]) {
    parrItems = parrDataTree.map(vobjValue => {
      this.mobjItemService = vobjValue;
      this.mobjItemService.children = [];
      return this.mobjItemService;
    });
    return parrItems;
  }

  /**
   * Tạo tree theo Object ServiceModel
   *
   * @author: TruyenNH
   * @version: 1.0
   * @since: 2019/09/13
   * @param: parrNodeTrees - Danh sách Object mới sau khi xử lý
   * @param: parrDataTree - Danh sách Object chưa xử lý
   */
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

  /**
   * Chuyển đổi tree: ServiceModel sang TreeViewItem
   *
   * @author: TruyenNH
   * @version: 1.0
   * @since: 2019/09/13
   * @param: pobjNodeTree - Object tham chiếu hiện tại
   * @param: pobjitem - Object TreeItem
   * @param: parrItems - Danh sách Object TreeViewItem
   */
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

  /**
   * Lấy danh sách serviceId chọn từ Treeview
   *
   * @author: NgocTH
   * @version: 1.0
   * @since: 2019/09/13
   * @param: $event - Danh sách serviceId chọn từ tree
   */
  selectTree($event: number[]) {
    this.marrServiceIds = [];
    this.marrServiceIds = $event;
    this.marrNodeTreeviewServices.forEach(
      vobjValue => {
        // console.log(vobjValue);
        if (vobjValue.checked) {
          this.marrServiceIds.push(vobjValue.value);
        }
        if (!vobjValue.children) {
          // console.log('no children');
        } else {
          vobjValue.children.forEach(
            vobjChid => {
              if (vobjChid.checked) {
                this.marrServiceIds.push(vobjChid.value);
              }
            }
          );
        }
      }
    );
    this.uniqueElementInArray(this.marrServiceIds);
    // console.log('serviceIds: ', this.uniqueElementInArray(this.marrServiceIds));
  }

  /**
   * Xóa bỏ những phần tử lặp trong mảng
   *
   * @author: NgocTH
   * @version: 1.0
   * @since: 2019/09/13
   * @param: parrArray - Mảng cần xóa phần tử lặp
   */
  uniqueElementInArray(parrArray: number[]) {
    for (let i = 0; i < parrArray.length - 1; i++) {
      for (let j = i + 1; j < parrArray.length; j++) {
        if (parrArray[i] === parrArray[j]) {
          parrArray.splice(j, 1);
        }
      }
    }
    return parrArray;
  }

  /**
   * Filter trong treeview chỉ tiêu
   *
   * @author: NgocTH
   * @version: 1.0
   * @since: 2019/09/13
   * @param: parrArray - Mảng dùng để tìm kiếm
   * @param: $event - keyword tìm kiếm
   */
  filterTreeView(parrArray: any[], $event: string) {
    this.mstrKeyWord = $event;

    if (!parrArray) {
      return [];
    }

    return parrArray.filter(
      vstrData => vstrData[this.mstrKeyWord].toLowerCase() > -1);
  }

  // updateChange(event: any, id: number, serviceId: number) {
  //   const value = event.target.value.split(',').join('');
  //   const updateModel: PlanmonthlyModel = new PlanmonthlyModel(id, value, serviceId, vdsChannelCode, shopCode, staffCode);
  //   this.updateDatas.push(updateModel);
  //   this.mblnShowSaveButton = true;
  // }

  validateUpdate(valuee: any) {
    this.mblnShowSaveButton = true;
    this.mblnChec = true;
    const value = valuee.target.value.split(',').join('');
    if (isNaN(value) === true || value === '' || value < 0) {
      console.log('ko hop le');
      this.toastr.error(this.translate.instant('management.target.toastr.updateData'),
        this.translate.instant('management.target.toastr.title'), {
        timeOut: 3000,
        positionClass: 'toast-top-center',
      });
    }
    console.log(typeof value);
    // value.split(',').join('');
    // console.log(value.split(',').join(''));

  }

  validateDate() {
    if (!this.mdtDate.valid) {
      this.mblnvaLi = true;
      this.isNullDate = false;
    } else if (!this.mdtDate.value) {
      this.mblnvaLi = false;
      this.isNullDate = true;
    } else if (this.mdtDate.value && this.mdtDate.valid) {
      this.mblnvaLi = false;
      this.isNullDate = false;
    }
  }

  onChange($event: MatCheckboxChange, index) {
    const vobjFormArray = this.mobjForm.get('formArray') as FormArray;
    if ($event.checked) {
      vobjFormArray.controls[index].value.checked = true;
      console.log('dhjhjhj dfdfgfg ', vobjFormArray.controls[index].value.checked);
      this.marrDataTable[index].checked = true;
      this.marrDataDelte.push(vobjFormArray.controls[index].value);
    } else {
      vobjFormArray.controls[index].value.checked = false;
      this.marrDataTable[index].checked = false;
      this.marrDataDelte.splice(index, 1);
    }
    if (this.marrDataDelte.length === vobjFormArray.length) {
      this.ckall = true;
    } else {
      this.ckall = false;
    }
    this.disable = false;
    if (this.marrDataDelte.length <= 0) {
      this.disable = true;
    } else {
      this.disable = false;
    }
  }

  delete(pobjTemplate: TemplateRef<any>) {
    this.mobjModalRef = this.modalService.show(pobjTemplate, {
      ignoreBackdropClick: true
    }
    );
  }

  onChangeAll($event) {
    this.marrDataDelte = [];
    const vobjData = this.formArray.value;
    this.ckall = !this.ckall;
    for (let i = 0; i < vobjData.length; i++) {
      if ($event.checked) {
        this.marrDataDelte.push(vobjData[i]);
        this.marrDataTable[i].checked = true;
        vobjData.map(
          table => {
            table.checked = true;
            return table;
          }
        );
        this.disable = false;
      } else {
        this.marrDataDelte = [];
        this.marrDataTable[i].checked = false;
        vobjData.map(
          table => {
            table.checked = false;
            return table;
          }
        );
        this.disable = true;
      }
    }
  }

  onDelete() {
    if (this.cycleCodeSearch == '02') {
      this.deleteService.deleteMonthRecords(this.marrDataDelte).subscribe(
        del => {
          console.log('delete ok');
          this.checked = false;
          this.disable = true;
          this.toastr.success(this.translate.instant('management.target.toastr.delete') + ' ' + del['data'] + ' ' +
            this.translate.instant('management.target.toastr.record'),
            this.translate.instant('management.target.toastr.title'), {
            timeOut: 3000,
            positionClass: 'toast-top-center'
          });
          this.search();
        }
      );
    } else if (this.cycleCodeSearch == '03') {
      this.deleteService.deleteQuarterRecords(this.marrDataDelte).subscribe(
        del => {
          console.log('delete ok');
          this.checked = false;
          this.disable = true;
          this.toastr.success(this.translate.instant('management.target.toastr.delete') + ' ' + del['data'] + ' ' +
            this.translate.instant('management.target.toastr.record'),
            this.translate.instant('management.target.toastr.title'), {
            timeOut: 3000,
            positionClass: 'toast-top-center'
          });
          this.search();
        }
      );
    } else {
      this.deleteService.deleteYearRecords(this.marrDataDelte).subscribe(
        del => {
          console.log('delete ok');
          this.checked = false;
          this.disable = true;
          this.toastr.success(this.translate.instant('management.target.toastr.delete') + ' ' + del['data'] + ' ' +
            this.translate.instant('management.target.toastr.record'),
            this.translate.instant('management.target.toastr.title'), {
            timeOut: 3000,
            positionClass: 'toast-top-center'
          });
          this.search();
        }
      );
    }
    this.mobjModalRef.hide();
    // this.showUploadedOk();
  }

  getCreateDate(createDate: any) {
    const dateNum = Date.parse(createDate);
    const date = new Date(dateNum);
    const options = {
      year: 'numeric', month: 'numeric', day: 'numeric',
    };

    return date.toLocaleDateString('en', options);
  }

  getCycles() {
    this.cycleService.getCycles().subscribe(
      data => {
        this.cycles = data['data'];
      }
    );
  }

  changeYear() {
    // const oldDate = new Date(this.dashModel.prdId);
    // console.log(oldDate.getDate(), 'khkhkhkh');
    if (this.quarter == 1) {
      this.mdtDate.value._d.setMonth(0, 1);
    } else if (this.quarter == 2) {
      this.mdtDate.value._d.setMonth(3, 1);
    } else if (this.quarter == 3) {
      this.mdtDate.value._d.setMonth(6, 1);
    } else {
      this.mdtDate.value._d.setMonth(9, 1);
    }
    // console.log(this.date.value._d);
    this.mdtDate.setValue(this.mdtDate.value);
  }

  cycleChange() {
    this.isNullDate = false;
    this.mdtDate = new FormControl(_moment());
    if (this.cycleCode == '02') {
      this.startView = 'year';
    } else {
      this.startView = 'multi-year';
    }
  }

  setDefaultQuarter() {
    if (this.mdtDate.valid === false) {
      return;
    } else {
      const a = this.mdtDate.value._d.getMonth() + 1;
      if (a <= 3) {
        this.quarter = 1;
      } else if (a >= 4 && a < 7) {
        this.quarter = 2;
      } else if (a >= 7 && a < 10) {
        this.quarter = 3;
      } else {
        this.quarter = 4;
      }
    }
  }
}
