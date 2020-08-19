import {AfterViewInit, ChangeDetectorRef, Component, ElementRef, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {config} from '../../../config/application.config';
import {DownloadService} from '../../../services/management/download.service';
import {ChannelService} from '../../../services/management/channel.service';
import {Channel} from '../../../models/Channel.model';
import {FormArray, FormBuilder, FormControl, FormGroup} from '@angular/forms';
import {DateAdapter, MAT_DATE_FORMATS, MAT_DATE_LOCALE} from '@angular/material/core';
import {MatDatepicker} from '@angular/material/datepicker';
import {MomentDateAdapter} from '@angular/material-moment-adapter';
import * as _moment from 'moment';
import {Moment} from 'moment';
import {ServiceModel} from '../../../models/service.model';
import {ServiceService} from '../../../services/management/service.service';
import {Pager} from '../../../models/Pager';
import {BsModalRef, BsModalService} from 'ngx-bootstrap';
import {PlanmonthlyService} from '../../../services/management/planmonthly.service';
import {PlanmonthlyModel} from '../../../models/Planmonthly.model';
import {SearchModel} from '../../../models/Search.model';
import {Router} from '@angular/router';
import {ToastrService} from 'ngx-toastr';
import * as fileSaver from 'file-saver';
import {SortTable} from '../../../models/SortTable';
import {UnitStaffModel} from '../../../models/UnitStaff.model';
import {UnitStaffService} from '../../../services/management/unitStaff.service';
import {TreeItem, TreeviewConfig, TreeviewI18n, TreeviewItem} from 'ngx-treeview';
import {UnitTreeviewI18n} from '../vtt-target/unit-treeview-i18n';
import {TranslateService} from '@ngx-translate/core';
import {UnitModel} from '../../../models/unit.model';
import {UnitService} from '../../../services/management/unit.service';
import {StaffService} from '../../../services/management/staff.service';
import {DownloadModel} from '../../../models/download.model';
import {DeleteService} from '../../../services/management/delete.service';
import {MatCheckboxChange} from '@angular/material';
import {CycleService} from '../../../services/management/cycle.service';

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
  templateUrl: 'vtt-target-level.component.html',
  styleUrls: ['vtt-target-level.component.scss'],
  providers: [
    {provide: DateAdapter, useClass: MomentDateAdapter, deps: [MAT_DATE_LOCALE]},
    {provide: MAT_DATE_FORMATS, useValue: MY_FORMATS},
    {provide: TreeviewI18n, useClass: UnitTreeviewI18n}
  ],
})

export class VttTargetLevelComponent implements OnInit, AfterViewInit {
  @ViewChild('focus') focus: ElementRef;
  @ViewChild('error') error: ElementRef;

  constructor(private http: HttpClient,
              private downloadService: DownloadService,
              private channelService: ChannelService,
              private serviceService: ServiceService,
              private modalService: BsModalService,
              private planmonthlyService: PlanmonthlyService,
              private router: Router,
              private fb: FormBuilder,
              private toastr: ToastrService,
              private unittreeService: UnitStaffService,
              private cdRef: ChangeDetectorRef,
              private unitService: UnitService,
              private staffService: StaffService,
              private deleteService: DeleteService,
              private translate: TranslateService,
              private cycleService: CycleService) {
  }

  mdtMinDate = new Date(1899, 0, 1);
  mdtMaxDate = new Date(2101, 0, 1);
  mobjForm: FormGroup;
  mnbrP = 1;
  mnbrTotal;
  ckall = false;
  btnUpdate = false;
  mnbrPageSize = config.pageSize;
  marrChannels: Channel[] = [];
  marrDataTable: PlanmonthlyModel[] = [];
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
  marrUnitStaff: UnitStaffModel[] = [];
  marrUnitStaffs: UnitStaffModel[] = [];
  marrUnits: any[] = [];
  marrStaffs: UnitStaffModel[] = [];
  marrStaffsShow: UnitStaffModel[] = [];
  isNullDate = false;
  mstrDateInput;
  mnbrUnitId: number;
  marrUnitIds: number[] = [];
  mnbrStaffId: number;
  marrStaffIds: number[] = [];

  marrDataTree = [];
  marrIndexNode = [];
  marrNodeTrees: UnitStaffModel[] = [];
  mobjNodeTree: UnitStaffModel;
  mobjNodeTreeview: TreeviewItem;
  marrNodeTreeviews: TreeviewItem[] = [];
  mobjNodeItem: TreeItem;
  marrNodeItems: TreeItem[] = [];
  updateRecord;

  marrIndexNodeService = [];
  mobjNodeTreeviewService: TreeviewItem;
  marrNodeTreeviewServices: TreeviewItem[] = [];
  mobjNodeItemService: TreeItem;
  marrNodeItemServices: TreeItem[] = [];

  marrData = [];
  mobjItemService: ServiceModel;
  marrItemServices: ServiceModel[] = [];

  mobjConfig = TreeviewConfig.create({
    hasAllCheckBox: true,
    hasFilter: true,
    hasCollapseExpand: false,
    maxHeight: 200,
    decoupleChildFromParent: true,
  });
  mblnIsNaNumber: boolean;
  mblnIsClickSearch = false;
  mnbrSumSuccessfulRecord;
  mnbrSumRecord;
  mstrResultFileName;
  mstrMessageUpload;
  mstrTypeSearch = 'Level';
  mblnIsSelectFile = false;
  mblnIsClickHere = false;
  marrUnitsNew: TreeviewItem[] = [];
  mnbrShop: UnitModel;
  marrStaffsNew: TreeviewItem[] = [];

  marrStaffCodes: string[] = [];
  mstrStaffCode: string;
  mstrShopCode: string;
  mnbrShopId: number;
  marrShops: string[] = [];
  mblnvaLi = false;
  disable = true;
  arrTemp: PlanmonthlyModel[] = [];
  marrDataDelte: PlanmonthlyModel[] = [];
  checked = true;
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
    this.mobjForm = this.fb.group({
      formArray: this.fb.array([])
    });

    this.marrDataTable = null;
    this.mnbrChannelId = null;
    this.mstrColumnName = null;
    this.mstrTypeSort = null;
    this.mobjSortTable = null;
    this.mnbrUnitId = null;
    this.marrStaffIds = [];
    this.marrUnitIds = [];
    this.mnbrStaffId = null;
    this.mstrColumnName = 'month';
    this.mstrTypeSort = 'desc';
    this.marrUnitStaffs = [];
    this.mnbrShop = null;
    this.mstrStaffCode = null;
    this.mnbrShopId = null;
    this.mstrShopCode = null;
    this.cycleCodeSearch = '02';
    setTimeout(() => {
      this.cycleCode = '02';
    }, 100);

    this.getChannels();
    this.getServices();
    this.filterStaffs();
    setTimeout(() => {
      this.search();
    }, 100);
    this.getUnits();
    this.getCycles();
    this.setDefaultQuarter();
  }

  ngAfterViewInit(): void {
    // this.focus.nativeElement.focus();
    this.cdRef.detectChanges();
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
      vobjError => (console.error('không có channel'))
    );
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
    this.downloadService.downloadLevel(vobjFileRequest)
      .subscribe(vobjResponse => {
        let vstrFilename: string;
        if (this.cycleCode == '02') {
          vstrFilename = this.translate.instant('management.target.table.header.month')
            + (this.mdtDate.value._d.getMonth() + 1) + '/'
            + (this.mdtDate.value._d.getFullYear()) + '_' + this.translate.instant('management.target.file.nameLevel');
        } else if (this.cycleCode == '03') {
          vstrFilename = this.translate.instant('management.target.table.header.quarter')
            + (this.quarter == 1 ? 'Q1' : this.quarter == 2 ? 'Q2' : this.quarter == 3 ? 'Q3' : 'Q4') + '/'
            + (this.mdtDate.value._d.getFullYear()) + '_' + this.translate.instant('management.target.file.nameLevel');
        } else {
          vstrFilename = this.translate.instant('management.target.table.header.year')
            + (this.mdtDate.value._d.getFullYear()) + '_' + this.translate.instant('management.target.file.nameLevel');
        }
        this.saveFile(vobjResponse.body, vstrFilename);
      });
    this.mnbrPageSize = config.pageSize;
    this.search();
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
    this.http.get(`${config.download_Result_File_API}?fileName=${pstrNameFileResult}`);
    this.downloadService.downloadResult(pstrNameFileResult)
      .subscribe(vobjResponse => {
        let vstrFilename: string;
        vstrFilename = this.mstrResultFileName;
        this.saveFile(vobjResponse.body, vstrFilename);
      });
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

    if (!this.marrServiceIds) {
      this.marrServiceIds = null;
    }

    if (!this.mnbrChannelId) {
      this.mnbrChannelId = null;
    }

    if (!this.mstrCongVan) {
      this.mstrCongVan = null;
    }

    if (!this.marrUnitIds) {
      this.marrUnitIds = null;
    }

    // if (!this.marrStaffIds) {
    //   this.marrStaffIds = null;
    // }

    // if (!this.mstrShopCode) {
    //   this.marrShops = [];
    // }

    // if (!this.mstrStaffCode) {
    //   this.marrStaffCodes = [];
    // } else {
    //   this.marrStaffCodes = [this.mstrStaffCode];
    // }

    this.mobjPager = new Pager(this.mnbrP, this.mnbrPageSize);
    this.mobjSortTable = new SortTable(this.mstrColumnName, this.mstrTypeSort);
    if (!this.mdtDate.value) {
      this.mobjPager = new Pager(this.mnbrP, this.mnbrPageSize);
      this.mobjSearchModel = new SearchModel(null, null, null, null,
        // tslint:disable-next-line:max-line-length
        this.marrServiceIds, this.mstrCongVan, this.marrShops, this.marrStaffCodes, this.mstrTypeSearch, this.mobjPager, this.mobjSortTable, this.cycleCodeSearch);
    } else {
      this.mobjPager = new Pager(this.mnbrP, this.mnbrPageSize);
      this.mstrDateInput = this.mdtDate.value._d.getTime();
      this.mobjSearchModel = new SearchModel(this.mstrDateInput, null, null, null,
        // tslint:disable-next-line:max-line-length
        this.marrServiceIds, this.mstrCongVan, this.marrShops, this.marrStaffCodes, this.mstrTypeSearch, this.mobjPager, this.mobjSortTable, this.cycleCodeSearch);
    }

  }

  /**
   * Lưu file download theo tên Tháng_ChiTieuVDS.xlsx
   *
   * @author: NgocTH
   * @version: 1.0
   * @since: 2019/09/13
   * @param: pobjData - dữ liệu file download
   * @param: pstrFilename - tên file download
   */
  saveFile(pobjData: any, pstrFilename?: string) {
    const blob = new Blob([pobjData], {type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'});
    fileSaver.saveAs(blob, pstrFilename);
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
    if (this.mobjFileList) {
      const vfileFile: File = this.mobjFileList[0];
      const vobjFormData: FormData = new FormData();
      vobjFormData.append('file', vfileFile);
      const vobjHeaders = new HttpHeaders();
      /** In Angular 5, including the header Content-Type can invalidate your request */
      vobjHeaders.append('Content-type', 'multipart/form-data');
      vobjHeaders.append('Accept', 'application/json');
      const options = {headers: vobjHeaders};
      this.http.post(config.uploadFileLevel_API, vobjFormData, options)
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
    this.mblnIsSelectFile = true;
    return this.mobjDataUpload;
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
    this.mstrMessageUpload = this.mobjDataUpload.message;
    this.showError(this.mnbrCode);
    // this.modalRef.hide();
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
    } else if (pnbrCode === 200) {
      this.mnbrSumSuccessfulRecord = this.mobjDataUpload.sumSuccessfulRecord;
      this.mnbrSumRecord = this.mobjDataUpload.sumRecord;
      this.mstrResultFileName = this.mobjDataUpload.resultFileName;
    } else {
      console.log('Lỗi server');
    }
  }

  /**
   * Mở dialog upload
   *
   * @author: NgocTH
   * @version: 1.0
   * @since: 2019/09/13
   * @param: pobjTemplate - id template modal
   */
  confirm(pobjTemplate: TemplateRef<any>) {
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
    this.disable = true;
    this.checked = false;
    this.ckall = false;
    this.marrDataDelte = [];
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
        this.marrDataTable = vobjNext['data'];
        this.mnbrTotal = vobjNext['totalRow'];
        this.marrDataTable.map(
          table => {
            table.checked = false;
            return table;
          }
        );
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
    const value = pstrInput.value;
    const a = value.split(',').join('');
    if (isNaN(a) === true || a === '' || a < 0) {
      this.mblnIsNaNumber = true;
      return;
    } else {
      const vobjFormArray = this.mobjForm.get('formArray') as FormArray;
      vobjFormArray.controls[pnbrIndex].patchValue({fSchedule: a});
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
          prdId: vobjData.prdId,
          checked: vobjData.checked
        });
        this.formArray.push(vobjFormData);
      });
    }
    const vobjData = this.formArray.value;
    // for(let i = 0; i < vobjData.length; i++ ){
    //     this.arrChecked.push(vobjData.checked[i]);
    // }

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
      this.showUploadedFailse(this.translate.instant('management.target.toastr.updateFalse'));
      return;
    }
    const vobjData = this.formArray.value;
    // tslint:disable-next-line:triple-equals
    for (let i = 0; i < this.marrDataTable.length; i++) {
      if (vobjData[i].fSchedule != this.marrDataTable[i].fSchedule) {
        this.arrTemp.push(vobjData[i]);
      }
    }

    if (this.cycleCodeSearch == '02') {
      this.planmonthlyService.updateMonthFschedule(this.arrTemp)
        .subscribe(
          next => {
            if (next['code'] === 200) {
              this.showUploadedOk(next['data']);
              this.arrTemp = [];
              this.search();
            }
            if (next['code'] !== 200) {
              this.showUploadedFailse(next['data']);
              this.arrTemp = [];
              this.search();
            }
          },
          error => {
          }
        );
    } else if (this.cycleCodeSearch == '03') {
      this.planmonthlyService.updateQuarterFschedule(this.arrTemp)
        .subscribe(
          next => {
            if (next['code'] === 200) {
              this.showUploadedOk(next['data']);
              this.arrTemp = [];
              this.search();
            }
            if (next['code'] !== 200) {
              this.showUploadedFailse(next['data']);
              this.arrTemp = [];
              this.search();
            }
          },
          error => {
          }
        );
    } else {
      this.planmonthlyService.updateYearFschedule(this.arrTemp)
        .subscribe(
          next => {
            if (next['code'] === 200) {
              this.showUploadedOk(next['data']);
              this.arrTemp = [];
              this.search();
            }
            if (next['code'] !== 200) {
              this.showUploadedFailse(next['data']);
              this.arrTemp = [];
              this.search();
            }
          },
          error => {
          }
        );
    }
    this.mobjModalRef.hide();
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
    if (this.marrDataDelte.length > 0) {
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
   *
   * @author: NgocTH
   * @version: 1.0
   * @since: 2019/09/13
   */
  showUploadedOk(message: string) {
    this.toastr.success(message,
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
  showUploadedFailse(message: string) {
    this.toastr.error(message,
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
   * Lấy danh sách nhân viên theo danh sách đơn vị đã chọn
   *
   * @author: NgocTH
   * @version: 1.0
   * @since: 2019/09/13
   */
  filterStaffs() {
    try {
      if (!this.mstrShopCode) {
        this.mnbrShopId = null;
      }
      //   const staffModel: StaffModel = new StaffModel(this.mnbrShopId);
      this.staffService.getStaffs().subscribe(
        staff => {
          const staffViews = staff['data'];
          if (!staffViews) {
            return;
          }
          staffViews.forEach(
            valueUnit => {
              const text: string = valueUnit['name'];
              const value = valueUnit['staffCode'];
              this.marrStaffsNew.push(new TreeviewItem({text, value, checked: false}));
            }
          );
        }
      );
    } catch (e) {
    }

  }

  /**
   * Lấy danh sách đơn vị từ api và hiển thị
   *
   * @author: NgocTH
   * @version: 1.0
   * @since: 2019/10/18
   */
  getUnits() {
    try {
      this.unitService.getUnitsLevel().subscribe(
        units => {
          const unitViews = units['data'];
          if (unitViews) {
            unitViews.forEach(
              valueUnit => {
                const text: string = valueUnit['shopName'];
                const value = valueUnit['shopCode'];
                this.marrUnitsNew.push(new TreeviewItem({text, value, checked: false}));
              }
            );
          }

        }
      );
    } catch (e) {
    }

  }

  /**
   * Chuyển đổi tree: UnitStaffModel sang TreeViewItem
   *
   * @author: TruyenNH
   * @version: 1.0
   * @since: 2019/09/13
   * @param: pobjNodeTree - Object tham chiếu hiện tại
   * @param: pobjItem - Object TreeItem
   * @param: parrItems - Danh sách Object TreeViewItem
   */
  forwarddata(pobjNodeTree: UnitStaffModel, pobjItem: TreeItem, parrItems: TreeItem[] = []) {
    pobjItem = null;
    parrItems = [];
    if (pobjNodeTree.children) {
      pobjNodeTree.children.forEach(
        value => {
          this.mobjNodeItem = this.forwarddata(value, null, []);
          parrItems.push(this.mobjNodeItem);
        });

      pobjItem = {
        value: pobjNodeTree.objectId,
        text: pobjNodeTree.objectName,
        children: parrItems,
        checked: false
      };
    } else {
      parrItems = [];
      pobjItem = {
        value: pobjNodeTree.objectId,
        text: pobjNodeTree.objectName,
        children: null,
        checked: false
      };
    }
    return pobjItem;
  }

  /**
   * Tạo Mảng các đối tướng trong tree
   *
   * @author: TruyenNH
   * @version: 1.0
   * @since: 2019/09/13
   * @param: parrNodeTrees - danh sách Object mới
   * @param: parrDataTree - danh sách Object chỉ tiêu từ api
   * @return: parrNodeTrees - danh sách Object mới
   */
  arrId(parrNodeTrees: UnitStaffModel[] = [], parrDataTree: UnitStaffModel[]) {
    parrNodeTrees = parrDataTree.map(vobjValue => {
        this.mobjNodeTree = vobjValue;
        this.mobjNodeTree.children = [];
        return this.mobjNodeTree;
      }
    );
    return parrNodeTrees;
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
  tree(parrNodeTrees: UnitStaffModel[], parrDataTree: UnitStaffModel[]) {
    for (let i = 0; i < parrNodeTrees.length; i++) {
      if (parrNodeTrees[i].objectType !== 'staff') {
        for (let j = 0; j < parrDataTree.length; j++) {
          if (parrNodeTrees[i].objectId === parrDataTree[j].parentId) {
            parrNodeTrees[i].children.push(parrDataTree[j]);
            this.marrIndexNode.push(j);
          }
        }
      }
    }
    const vnbrc = (pnbrA: number, pnbrB: number) => (pnbrA - pnbrB);
    this.marrIndexNode.sort(vnbrc);
    for (let vnbrI = this.marrIndexNode.length - 1; vnbrI >= 0; vnbrI--) {
      parrNodeTrees.splice(this.marrIndexNode[vnbrI], 1);
    }
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
    this.marrUnits = [];
    this.marrStaffs = [];
    this.marrUnitIds = [];
    this.marrStaffsShow = [];
    this.marrUnitIds = $event;
    this.marrNodeTreeviews.forEach(
      vobjValue => {
        if (vobjValue.checked) {
          this.marrUnitIds.push(vobjValue.value);
        }
        if (!vobjValue.children) {

        } else {
          vobjValue.children.forEach(
            vobjChid => {
              if (vobjChid.checked) {
                this.marrUnitIds.push(vobjChid.value);
              }
            }
          );
        }
      }
    );
    this.uniqueElementInArray(this.marrUnitIds);
    // this.staffsShow = [];
    // this.filterStaffs();
  }

  /**
   * Lấy Nhân viên chọn từ giao diện
   *
   * @author: NgocTH
   * @version: 1.0
   * @since: 2019/09/13
   */
  selectStaff(staff: any) {
    // console.log(staff);
    this.marrStaffCodes = staff;
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
    if (this.mstrColumnName !== pstrColumnName) {
      this.mstrTypeSort = 'desc';
    }
    // if (!this.mblnIsClickSearch) {
    //   this.marrDataTable = null;
    // } else {
    this.search();
    // }
  }

  /**
   * Xóa bỏ những phần tử lặp trong mảng
   *
   * @author: NgocTH
   * @version: 1.0
   * @since: 2019/09/13
   * @param: parrArray - Mảng cần xóa phần tử lặp
   */
  uniqueElementInArray(parrArray: any[]) {
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
   * Lấy danh sách chỉ tiêu từ api và hiển thị lên treeview
   *
   * @author: TruyenNH
   * @version: 1.0
   * @since: 2019/09/13
   */
  getServices() {
    // const serviceModel: ServiceModel = new ServiceModel(null, null);
    this.serviceService.getServices().subscribe(
      vobjNext => {
        this.marrData = vobjNext['data'];
        this.marrItemServices = this.createNode(this.marrItemServices, this.marrData, this.mobjItemService);
        // console.log(this.marrItemServices);
        this.marrData = this.marrItemServices;
        this.createTree(this.marrItemServices, this.marrData);
        this.marrItemServices.forEach(vobjValue => {
          // tslint:disable-next-line:max-line-length
          this.mobjNodeTreeviewService = new TreeviewItem(this.forwardData(vobjValue, this.mobjNodeItemService, this.marrNodeItemServices));
          this.marrNodeTreeviewServices.push(this.mobjNodeTreeviewService);
        });
      }
    );
  }

  /**
   * Tạo Mảng các đối tướng trong tree
   *
   * @author: TruyenNH
   * @version: 1.0
   * @since: 2019/09/13
   * @param: parrItems - danh sách Object mới
   * @param: parrDataTree - danh sách Object chỉ tiêu từ api
   * @param: pobjItem - Object ServiceModel
   * @return: parrItems - danh sách Object mới
   */
  createNode(parrItems: ServiceModel[] = [], parrDataTree: ServiceModel[], pobjItem: ServiceModel) {
    pobjItem = null;
    parrItems = parrDataTree.map(value => {

      pobjItem = {
        id: value.id,
        name: value.name,
        parentId: value.parentId,
        groupServiceId: value.groupServiceId,
        vdsChannelCode: value.vdsChannelCode,
        congVan: value.congVan,
        code: value.code,
        children: [],
      };
      return pobjItem;
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
    const len = parrNodeTrees.length;
    for (let i = 0; i < len; i++) {
      for (let j = 0; j < len; j++) {
        if (parrNodeTrees[i].id === parrDataTree[j].parentId) {
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
  selectServiceTree($event: number[]) {
    this.marrServiceIds = [];
    this.marrServiceIds = $event;
    this.marrNodeTreeviewServices.forEach(
      vobjValue => {
        if (vobjValue.checked) {
          this.marrServiceIds.push(vobjValue.value);
        }
        if (!vobjValue.children) {
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

  selectUnit(unit: any) {
    // console.log(unit);

    this.marrShops = unit;
    // console.log('event: ', this.marrShops);
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

  onDelete() {
    if (this.cycleCodeSearch == '02') {
      this.deleteService.deleteMonthRecords(this.marrDataDelte).subscribe(
        del => {
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
