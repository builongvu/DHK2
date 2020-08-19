import { config } from './../../../../config/application.config';
import { DownloadService } from './../../../../services/management/download.service';
import { MomentDateAdapter } from '@angular/material-moment-adapter';
import { MAT_DATE_FORMATS, MAT_DATE_LOCALE, DateAdapter } from '@angular/material/core';
import { BsModalRef, BsModalService } from 'ngx-bootstrap';
import { TranslateService } from '@ngx-translate/core';
import { ToastrService } from 'ngx-toastr';
import { ServiceRequest } from './../../../../models/service-request.model';
import { TreeviewConfig, TreeviewI18n, TreeviewItem, TreeviewComponent, TreeItem } from 'ngx-treeview';
import { ServiceService } from './../../../../services/management/service.service';
import { GroupModel } from './../../../../models/group.model';
import { GroupsService } from './../../../../services/management/group.service';
import { ChannelService } from './../../../../services/management/channel.service';
import { Channel } from './../../../../models/Channel.model';
import { DVT } from './../../../../models/dvt.model';
import { UnitService } from './../../../../services/management/unit.service';
import { FormGroup, FormBuilder, Validators, FormControl } from '@angular/forms';
import { Component, OnInit, Inject, TemplateRef, ViewChild, ChangeDetectorRef, AfterViewChecked, ViewEncapsulation } from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogRef,
  MatDatepickerModule
} from '@angular/material';
import { EndDateAfterOrEqualValidator } from '../../../../helpers/function.share';
import { DatePipe } from '@angular/common';
import { UnitTreeviewI18n } from '../config-tree-select/unit-treeview-i18n';
import * as fileSaver from 'file-saver';
import { apParam } from '../../../../models/apParam.model';
import { ServiceModel } from '../../../../models/service.model';

export const MY_FORMATS = {
  parse: {
    dateInput: 'DD/MM/YYYY',
  },
  display: {
    dateInput: 'DD/MM/YYYY',
    monthYearLabel: 'MMM YYYY',
    dateA11yLabel: 'LL',
    monthYearA11yLabel: 'MMMM YYYY',
  },
};
@Component({
  selector: 'app-target-edit',
  templateUrl: './target-edit.component.html',
  styleUrls: ['./target-edit.component.scss'],
  providers: [MatDatepickerModule, DatePipe,
    { provide: TreeviewI18n, useClass: UnitTreeviewI18n },
    { provide: MAT_DATE_FORMATS, useValue: MY_FORMATS },
    { provide: DateAdapter, useClass: MomentDateAdapter, deps: [MAT_DATE_LOCALE] }
  ],
  encapsulation: ViewEncapsulation.None
})
export class TargetEditComponent implements OnInit, AfterViewChecked {


  @ViewChild('multiSelectChannels') multiSelectChannels;
  public name = 'Cricketers1';
  public channels = [];
  public settings = {};
  public selectedItems = [];


  @ViewChild(TreeviewComponent) treeviewComponent: TreeviewComponent;
  items: TreeviewItem[];
  rows: string[] = [];

  mdtMinDate = new Date(1900, 0, 1);
  mdtMaxDate = new Date(2100, 0, 1);

  serviceCyclee;
  conflictCode: boolean;
  conflictOrderService: boolean;
  serviceCode = '';
  editServiceForm: FormGroup;
  marrUnits: DVT[] = [];
  marrChannels: Channel[] = [];
  marrGroups: any[] = [];
  marrNodeTreeviewServicess;
  marrChannelOfServices: Channel[] = [];
  marrServiceCycle: apParam[] = [];
  marrServiceCalc: apParam[] = [];
  marrServiceChildrenID: any[] = [];
  value;
  values: any;

  marrNodeTreeviewServices: TreeviewItem[] = [];
  marrNodeTreeviewServicesStatus: TreeviewItem[] = [];
  mobjItemService: ServiceModel;
  marrDataStatus = [];
  mobjServiceStatus: ServiceModel;
  marrItemServicesStatus: ServiceModel[] = [];
  mobjNodeItemServiceStatus: TreeItem;
  marrNodeItemServicesStatus: TreeItem[] = [];
  marrIndexNode = [];
  mobjNodeItemService: TreeItem;
  marrNodeItemServices: TreeItem[] = [];
  marrServiceIds: number[] = [];
  mstrKeyWord: string;
  mobjNodeTreeviewService: TreeviewItem;
  mblnCheck = false;
  mobTarget;
  mobjService: ServiceModel;
  config = TreeviewConfig.create({
    hasFilter: true,
    hasCollapseExpand: false,
    maxHeight: 200
  });

  newService: ServiceRequest;

  toDateValidator: boolean;
  toDateValidPatter: boolean;
  toDateValidToday: boolean;
  toDateYear: boolean;

  fromDateInput: string;

  fromDateValidator: boolean;
  fromDatePattern: boolean;
  fromDateNull: boolean;
  fromDateEndlessToDate: boolean;
  fromDateYear: boolean;

  marrUnitsNew: TreeviewItem[] = [];
  mblnChec = false;
  marrShops: string[] = [];
  marrChannelsBinding: string[] = [];
  mobjModalRef: BsModalRef;

  // serviceOrderFormat: any;
  mblnServiceName: boolean;

  checkParentIDService: boolean;

  constructor(
    public dialogRef: MatDialogRef<TargetEditComponent>,
    @Inject(MAT_DIALOG_DATA) public data,
    private fb: FormBuilder,
    private unitService: UnitService,
    private channelService: ChannelService,
    private groupsService: GroupsService,
    private serviceService: ServiceService,
    private toastr: ToastrService,
    private translate: TranslateService,
    private datePipe: DatePipe,
    private modalService: BsModalService,
    private cdRef: ChangeDetectorRef,
    private downLoad: DownloadService
  ) { dialogRef.disableClose = true; }

  ngOnInit() {
    this.mblnServiceName = false;
    this.getAllChannels();
    this.getChannelTree();
    this.getAllServiceByStatus();

    this.settings = {
      singleSelection: false,
      idField: 'code',
      textField: 'name',
      enableCheckAll: false,
      selectAllText: this.translate.instant('management.service.form.All'),
      unSelectAllText: this.translate.instant('management.service.form.All'),
      allowSearchFilter: true,
      limitSelection: -1,
      clearSearchFilter: true,
      maxHeight: 197,
      itemsShowLimit: 1,
      searchPlaceholderText: this.translate.instant('dashboard.global.button.search'),
      noDataAvailablePlaceholderText: this.translate.instant('management.table.body.noData'),
      closeDropDownOnSelection: false,
      showSelectedItemsAtTop: false,
      defaultOpen: false
    };

    this.getServiceCycle();
    this.getGroups();
    this.getServiceCalc();
    this.getUnits();

    this.createServiceForm();

    this.serviceService.checkParentID(this.data.service.id).subscribe((data: any) => {
      this.marrServiceChildrenID = data.data;
    });

    this.selectedItems = this.data.channel;
    this.serviceService.serviceTree$.subscribe(tree => {
      this.marrNodeTreeviewServicess = tree;
    });
  }

  ngAfterViewChecked(): void {
    this.serviceCode = this.editServiceForm.get('code').value;
    this.cdRef.detectChanges();
  }

  /**
* Tạo reactive form sửa thông tin chỉ tiêu
*
* @author: Manhtd
* @version: 1.0
* @since: 11/2019
*/
  createServiceForm() {
    // this.serviceOrderFormat = this.data.service.serviceOrder;
    this.serviceCyclee = this.data.service.serviceCycle;
    this.fromDateInput = this.data.service.fromDate;

    this.editServiceForm = this.fb.group(
      {
        name: [
          this.data.service.name,
          [Validators.required, Validators.maxLength(100)]
        ],
        groupId: [this.data.service.groupServiceId, Validators.required],
        parentId: [this.data.service.parentId],
        code: [this.data.service.code, [Validators.required, Validators.pattern('^\\s{0,10}?[_A-Za-z0-9]{1,50}\\s{0,10}$')]],
        serviceType: [this.data.service.serviceType, Validators.required],
        serviceCycle: [(this.data.service.serviceCycle).toString(), Validators.required],
        fromDate: [this.data.service.fromDate, Validators.required],
        toDate: [this.data.service.toDate],
        // serviceOrder: [this.data.service.serviceOrder,
        // [
        //   Validators.min(1),
        //   Validators.pattern('^\\s{0,10}?\\-?[0-9]{1,20}\\s{0,10}$')
        // ]
        // ],
        status: [Number(this.data.service.status), Validators.required],
        unitCode: [this.data.service.unitCode, Validators.required],
        exp: [this.data.service.exp],
        calcType: [this.data.service.serviceCalcType, Validators.required],
        channels: [this.selectedItems, Validators.required]
      },
      {
        // validator: [EndDateAfterOrEqualValidator('fromDate', 'toDate')]
      }
    );

    this.serviceCode = this.editServiceForm.get('code').value;

    if (this.editServiceForm.get('parentId').value == null) {
      this.value = null;
    } else {
      this.value = this.editServiceForm.get('parentId').value;
    }
  }

  /**
* Validator trong reactive form
*
* @author: Manhtd
* @version: 1.0
* @since: 11/2019
*/
  hasError(controlName: string, errorName: string) {
    return this.editServiceForm.controls[controlName].hasError(errorName);
  }

  /**
* Lấy nhóm chỉ tiêu từ API
*
* @author: Manhtd
* @version: 1.0
* @since: 11/2019
*/
  getGroups() {
    this.groupsService.getGroupActive().subscribe(vobjNext => {
      this.marrGroups = vobjNext.data;
    },
      error => console.error('không có group')
    );
  }

  /**
* Lấy đơn vị tính từ API
*
* @author: Manhtd
* @version: 1.0
* @since: 11/2019
*/
  getUnits() {
    this.unitService.getAllUnit().subscribe((data: any) => {
      this.marrUnits = data.data;
    });
  }

  /**
* Lấy chu kỳ từ appram
*
* @author: Manhtd
* @version: 1.0
* @since: 11/2019
*/
  getServiceCycle() {
    this.serviceService.getServiceCycle('SERVICE_CYCLE', '1').subscribe((data: any) => {
      this.marrServiceCycle = data.data;
    });
  }

  /**
* Lấy cách tính từ appram
*
* @author: Manhtd
* @version: 1.0
* @since: 11/2019
*/
  getServiceCalc() {
    this.serviceService.getServiceCycle('SERVICE_CALC_TYPE', '1').subscribe((data: any) => {
      this.marrServiceCalc = data.data;
    });
  }

  /**
* Kiểm tra code trùng
*
* @author: Manhtd
* @version: 1.0
* @since: 11/2019
*/
  checkCodeConflict() {
    this.serviceCode = this.serviceCode.toUpperCase();
    this.serviceService.getAllService().subscribe((res: any) => {
      if (this.serviceCode.trim().toUpperCase().replace(/ /g, '') !== this.data.service.code) {
        for (let i = 0; i < res.data.length; i++) {
          if (this.serviceCode.trim().toUpperCase().replace(/ /g, '') === res.data[i].code) {
            this.conflictCode = true;
            break;
          } else {
            this.conflictCode = false;
          }
        }
      } else {
        this.conflictCode = false;
      }
    });
  }

  /**
* valid fromDate
*
* @author: Manhtd
* @version: 1.0
* @since: 11/2019
*/
  checkFromDate(event) {

    this.fromDateValidator = false;
    this.fromDatePattern = false;
    this.fromDateNull = false;
    this.fromDateEndlessToDate = false;
    this.toDateYear = false;
    this.fromDateYear = false;

    const fromDate = this.datePipe.transform(new Date(this.data.service.fromDate), 'yyyy-MM-dd');
    const dateChange = this.datePipe.transform(new Date(event.value), 'yyyy-MM-dd');

    if (dateChange == fromDate) {
      this.fromDateValidator = false;
      this.fromDatePattern = false;
      this.fromDateNull = false;
      this.fromDateEndlessToDate = false;
      return;
    } else if (event.targetElement.value.toString().trim().replace(/ /g, '') !== '') {
      const toDateYear = this.datePipe.transform(new Date(event.value), 'yyyy');
      if (Number(toDateYear) >= 2100 || Number(toDateYear) <= 1900) {
        this.fromDateYear = true;
        return;
      } else if (event.value == null) {
        this.fromDatePattern = true;
        return;
      } else {
        const toDay = this.datePipe.transform(new Date(), 'yyyy-MM-dd');
        if (dateChange < toDay) {
          this.fromDateValidator = true;
          return;
        } else if (this.editServiceForm.get('toDate').value != null && this.editServiceForm.get('toDate').value != '') {
          const toDate = this.datePipe.transform(new Date(this.editServiceForm.get('toDate').value), 'yyyy-MM-dd');
          if (dateChange > toDate) {
            this.fromDateEndlessToDate = true;
            return;
          }
        }
      }
    } else if (event.targetElement.value.toString() !== '' && event.value == null) {
      this.fromDatePattern = true;
      return;
    } else {
      this.fromDateNull = true;
      return;
    }
  }

  /**
* valid toDate
*
* @author: Manhtd
* @version: 1.0
* @since: 11/2019
*/
  checkToDate(event) {

    this.toDateValidator = false;
    this.toDateValidPatter = false;
    this.toDateValidToday = false;
    this.toDateYear = false;

    const toDay = this.datePipe.transform(new Date(), 'yyyy-MM-dd');
    const dateChange = this.datePipe.transform(new Date(event.value), 'yyyy-MM-dd');

    const toDate = this.datePipe.transform(new Date(this.data.service.toDate), 'yyyy-MM-dd');

    if (event.targetElement.value.toString().trim().replace(/ /g, '') !== '') {
      const toDateYear = this.datePipe.transform(new Date(event.value), 'yyyy');
      if (dateChange == toDate && dateChange != '1970-01-01' && toDate != '1970-01-01') {
        this.toDateValidator = false;
        this.toDateValidPatter = false;
        this.fromDateEndlessToDate = false;
        this.toDateValidToday = false;
        this.toDateYear = false;
        return;
      } else if (Number(toDateYear) >= 2100 || Number(toDateYear) <= 1900) {
        this.toDateYear = true;
        return;
      } else if (dateChange < toDay) {
        if (dateChange == '1970-01-01') {
          this.toDateValidPatter = true;
          return;
        } else {
          this.toDateValidToday = true;
          return;
        }
      } else if (this.editServiceForm.get('fromDate').value != null && this.editServiceForm.get('fromDate').value != '') {

        const fromDate = this.datePipe.transform(new Date(this.editServiceForm.get('fromDate').value), 'yyyy-MM-dd');

        if (fromDate > dateChange) {
          this.fromDateValidator = false;
          this.fromDatePattern = false;
          this.fromDateNull = false;
          this.fromDateEndlessToDate = true;
          return;
        } else if (dateChange == '1970-01-01') {
          this.toDateValidPatter = true;
          return;
        } else {
          this.fromDateValidator = false;
          this.fromDatePattern = false;
          this.fromDateNull = false;
          this.fromDateEndlessToDate = false;
          return;
        }
      } else {
        if (dateChange == '1970-01-01') {
          this.toDateValidPatter = true;
          return;
        }
      }
    } else if (event.targetElement.value.toString() !== '' && event.value == null) {
      this.toDateValidPatter = true;
      return;
    }
  }


  close() {
    this.dialogRef.close();
  }

  selectUnit(unit: any) {
    this.marrShops = unit;
  }

  /**
* Sửa thông tin chỉ tiêu
*
* @author: Manhtd
* @version: 1.0
* @since: 11/2019
*/
  editService() {
    this.rows = [];

    if (this.editServiceForm.get('name').value.toString().trim() == '') {
      // this.showSuccess(this.translate.instant('management.warningconfig.updatesuccess'));
      this.mobjModalRef.hide();
      this.mblnServiceName = true;
      return;
    }

    if (this.checkParentIDService === true) {
      this.mobjModalRef.hide();
      this.checkParentIDService = true;
      return;
    }

    if (this.conflictCode === true) {
      this.mobjModalRef.hide();
      this.conflictCode = true;
      return;
    }

    // if (this.conflictOrderService === true) {
    //   this.mobjModalRef.hide();
    //   this.conflictOrderService = true;
    //   return;
    // }

    if (this.fromDateValidator === true) {
      this.mobjModalRef.hide();
      this.fromDateValidator = true;
      return;
    }

    if (this.fromDatePattern === true) {
      this.mobjModalRef.hide();
      this.fromDatePattern = true;
      return;
    }

    if (this.fromDateNull === true) {
      this.mobjModalRef.hide();
      this.fromDateNull = true;
      return;
    }

    if (this.fromDateEndlessToDate === true) {
      this.mobjModalRef.hide();
      this.fromDateEndlessToDate = true;
      return;
    }

    if (this.fromDateYear === true) {
      this.mobjModalRef.hide();
      this.fromDateYear = true;
      return;
    }

    if (this.toDateValidator === true) {
      this.mobjModalRef.hide();
      this.toDateValidator = true;
      return;
    }

    if (this.toDateValidPatter === true) {
      this.mobjModalRef.hide();
      this.toDateValidPatter = true;
      return;
    }

    if (this.toDateValidToday === true) {
      this.mobjModalRef.hide();
      this.toDateValidToday = true;
      return;
    }

    if (this.toDateYear === true) {
      this.mobjModalRef.hide();
      this.toDateYear = true;
      return;
    }

    this.newService = new ServiceRequest();
    this.newService.name = this.editServiceForm.get('name').value.toString().trim();
    this.newService.groupServiceId = Number(this.editServiceForm.get('groupId').value);
    this.newService.serviceType = Number(this.editServiceForm.get('serviceType').value);
    this.newService.serviceCycle = Number(this.editServiceForm.get('serviceCycle').value);

    if (this.editServiceForm.get('fromDate').value != '' && this.editServiceForm.get('fromDate').value != null) {
      this.newService.fromDate = this.datePipe.transform(
        new Date(this.editServiceForm.get('fromDate').value),
        'yyyy-MM-dd'
      );
    }

    if (this.editServiceForm.get('toDate').value != null && this.editServiceForm.get('toDate').value != '') {
      this.newService.toDate = this.datePipe.transform(
        new Date(this.editServiceForm.get('toDate').value),
        'yyyy-MM-dd'
      );
    }

    this.newService.changeDatetime = this.datePipe.transform(
      new Date(),
      'yyyy-MM-dd'
    );

    // if (this.editServiceForm.get('serviceOrder').value != null && this.editServiceForm.get('serviceOrder').value != '') {
    // tslint:disable-next-line: max-line-length
    //   const formatServiceOrder = Number(this.editServiceForm.get('serviceOrder').value.toString().trim().toUpperCase().replace(/ /g, ''));
    //   if (formatServiceOrder === 0) {
    //     this.newService.serviceOrder = null;
    //   } else {
    //     this.newService.serviceOrder = formatServiceOrder;
    //   }
    // }

    this.newService.serviceOrder = null;

    this.newService.status = Number(this.editServiceForm.get('status').value);
    this.newService.serviceCalcType = this.editServiceForm.get('calcType').value;
    this.newService.unitCode = this.editServiceForm.get('unitCode').value;

    if (this.editServiceForm.get('exp').value != null && this.editServiceForm.get('exp').value != '') {
      if (this.editServiceForm.get('exp').value.toString().trim() == '') {
        this.newService.exp = null;
      } else {
        this.newService.exp = this.editServiceForm.get('exp').value.toString().trim();
      }
    } else {
      this.newService.exp = null;
    }

    this.newService.code = this.editServiceForm.get('code').value.toString().trim().toUpperCase().replace(/ /g, '');
    this.newService.parentId = this.value;
    this.newService.id = this.data.service.id;

    this.marrUnitsNew.forEach(item => {
      if (item.checked == true) {
        this.rows.push(item.value);
      }
    });


    const serviceChannesCode = [];
    this.editServiceForm.get('channels').value.forEach(element => {
      serviceChannesCode.push(element.code);
    });

    this.newService.vdsChannelCode = serviceChannesCode.toString();

    this.serviceService.editService(this.newService).subscribe(data => {
      this.showSuccess(this.translate.instant('management.warningconfig.updatesuccess'));

      this.serviceService.getAllService().subscribe((res: any) => {
        this.serviceService.setAllService(res);
        this.serviceService.getAllServiceByStatus().subscribe((res: any) => {
          this.serviceService.setAllServiceStatus(res);
        });
      });



      this.mobjModalRef.hide();
      this.dialogRef.close();


    }, err => {
      this.showError(this.translate.instant('management.service.message.fail'));
      console.log(err);
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
      this.translate.instant('management.service.message.fail'), {
      timeOut: 2000,
      positionClass: 'toast-top-center',
    });
  }

  onBackConfirm() {
    this.mobjModalRef.hide();
  }

  confirmEdit(pobjTemplate: TemplateRef<any>) {
    this.mobjModalRef = this.modalService.show(pobjTemplate, {
      ignoreBackdropClick: true
    }
    );
  }

  downloadGuildEXP() {
    this.downLoad.downloaGuildEXP().subscribe(data => {
      this.saveFile(data.body, 'GuildEXP.docx');
    });
  }

  saveFile(pobjData: any, pstrFilename?: string) {
    const vobjBlob = new Blob([pobjData], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
    fileSaver.saveAs(vobjBlob, pstrFilename);
  }

  /**
* Lấy tất cả kênh từ API
*
* @author: Manhtd
* @version: 1.0
* @since: 11/2019
*/
  getAllChannels() {
    this.channelService.getChannels().subscribe(vobjNext => {
      this.channels = vobjNext;
      this.getSelectedChannels();
    });

  }

  /**
* Lấy các kênh được chọn
*
* @author: Manhtd
* @version: 1.0
* @since: 11/2019
*/
  getSelectedChannels() {
    for (let i = 0; i < this.channels.length; i++) {
      for (let j = 0; j < this.data.channel.length; j++) {
        if (this.channels[i].code === this.data.channel[j]) {
          this.selectedItems.push(this.channels[i]);
        }
      }
    }

  }

  getChannelTree() {
    this.channelService.getChannels().subscribe(
      vobjNext => {
        this.marrChannels = vobjNext;
        if (vobjNext) {
          vobjNext.forEach(
            valueUnit => {
              const text = valueUnit['name'];
              const value = valueUnit['code'];
              this.marrUnitsNew.push(new TreeviewItem({ text, value, checked: false }));
            }
          );
        }

        this.marrUnitsNew.forEach(item => {
          this.data.channel.forEach(element => {
            if (item.value == element.code) {
              item.checked = true;
            }
          });
        });

      },
      error => (this.marrChannels = [])
    );
  }

  checkNameEmpty() {
    if (this.editServiceForm.get('name').value.toString().trim() == '' && this.editServiceForm.get('name').value != '') {
      this.mblnServiceName = true;
    } else {
      this.mblnServiceName = false;
    }
  }

  onChange(value: any) {
    if (this.marrServiceChildrenID) {
      for (let i = 0; i < this.marrServiceChildrenID.length; i++) {
        if (value === this.marrServiceChildrenID[i].id) {
          this.checkParentIDService = true;
          break;
        } else {
          this.checkParentIDService = false;
        }
      }
    }
  }

  checkParentID(parentID) {
    this.serviceService.checkParentID(parentID).subscribe((data: any) => {
      // if (data.data)
    });
  }

  getAllServiceByStatus() {
    this.marrNodeTreeviewServicesStatus = [];
    this.marrDataStatus = [];
    this.mobjService = new ServiceModel(null, '');
    this.serviceService.getAllServiceByStatus().subscribe(
      vobjNext => {
        this.marrNodeTreeviewServicesStatus = [];
        this.marrDataStatus = [];
        this.marrIndexNode = [];
        this.marrDataStatus = vobjNext['data'];
        const vobjService: ServiceModel = new ServiceModel(null, null);
        vobjService.id = 0;
        if (localStorage.getItem(config.user_default_language) === 'en') {
          vobjService.name = 'Origin';
        } else {
          vobjService.name = 'Chỉ tiêu gốc';
        }
        vobjService.code = '';
        this.marrDataStatus = this.marrDataStatus.filter(item => item.id !== 0);
        this.marrDataStatus.push(vobjService);

        this.marrDataStatus.sort((left, right) => {
          if (left.id < right.id) { return -1; }
          if (left.id > right.id) { return 1; }
          return 0;
        });

        this.marrItemServicesStatus = this.createNode(
          this.marrItemServicesStatus,
          this.marrDataStatus
        );
        this.createTree(this.marrItemServicesStatus, this.marrDataStatus);

        this.marrItemServicesStatus.forEach(vobjValue => {
          this.mobjNodeTreeviewService = new TreeviewItem(
            this.forwardData(
              vobjValue,
              this.mobjNodeItemServiceStatus,
              this.marrNodeItemServicesStatus
            )
          );
          this.marrNodeTreeviewServicesStatus.push(this.mobjNodeTreeviewService);
        });
        this.serviceService.setServiceTree(this.marrNodeTreeviewServicesStatus);

      },
      vobjErorr => {
        console.log('no data');
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
    const vnbrC = (pnbrA: number, pnbrB: number) => pnbrA - pnbrB;
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
  forwardData(
    pobjNodeTree: ServiceModel,
    pobjitem: TreeItem,
    parrItems: TreeItem[] = []
  ) {
    pobjitem = null;
    parrItems = [];
    if (pobjNodeTree.children) {
      pobjNodeTree.children.forEach(value => {
        this.mobjNodeItemService = this.forwardData(value, null, []);
        parrItems.push(this.mobjNodeItemService);
      });
    }
    if (pobjNodeTree.children) {
      if (pobjNodeTree.id > 0) {
        pobjitem = {
          value: pobjNodeTree.id,
          text: pobjNodeTree.code + ' - ' + pobjNodeTree.name,
          children: parrItems,
          checked: false
        };
      } else {
        pobjitem = {
          value: pobjNodeTree.id,
          text: pobjNodeTree.code + pobjNodeTree.name,
          children: parrItems,
          checked: false
        };
      }
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

  selectTree($event: number[]) {
    this.marrServiceIds = [];
    this.marrServiceIds = $event;
    this.marrNodeTreeviewServices.forEach(vobjValue => {
      if (vobjValue.checked) {
        this.marrServiceIds.push(vobjValue.value);
      }
      if (!vobjValue.children) {
        console.log('no children');
      } else {
        vobjValue.children.forEach(vobjChid => {
          if (vobjChid.checked) {
            this.marrServiceIds.push(vobjChid.value);
          }
        });
      }
    });
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
      vstrData => vstrData[this.mstrKeyWord].toLowerCase() > -1
    );
  }

}
