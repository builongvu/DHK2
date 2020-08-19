import { DownloadService } from './../../../../services/management/download.service';
import { MomentDateAdapter } from '@angular/material-moment-adapter';
import { BsModalRef, BsModalService } from 'ngx-bootstrap';
import { apParam } from './../../../../models/apParam.model';
import { TreeviewConfig, TreeviewItem, TreeviewI18n } from 'ngx-treeview';
import { TranslateService } from '@ngx-translate/core';
import { ToastrService } from 'ngx-toastr';
import { ServiceRequest } from './../../../../models/service-request.model';
import { DVT } from './../../../../models/dvt.model';
import { UnitService } from './../../../../services/management/unit.service';
import { MatDatepickerModule } from '@angular/material/datepicker';

import { Channel } from './../../../../models/Channel.model';
import { ChannelService } from './../../../../services/management/channel.service';
import { GroupModel } from './../../../../models/group.model';
import { GroupsService } from './../../../../services/management/group.service';
import { ServiceService } from './../../../../services/management/service.service';
import { Component, OnInit, TemplateRef, ViewEncapsulation } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import {
  EndDateAfterOrEqualValidator,
} from '../../../../helpers/function.share';
import { DatePipe } from '@angular/common';
import { MatDialogRef, MAT_DATE_FORMATS, DateAdapter, MAT_DATE_LOCALE } from '@angular/material';
import { ifStmt } from '@angular/compiler/src/output/output_ast';
import { UnitTreeviewI18n } from '../config-tree-select/unit-treeview-i18n';
import * as fileSaver from 'file-saver';
import { WarningReceive } from '../../../../models/Warning-Receive';
import { WarningReceiveService } from '../../../../services/management/warning-receive.service';

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
  selector: 'app-target-create',
  templateUrl: './target-create.component.html',
  styleUrls: ['./target-create.component.scss'],
  providers: [
    MatDatepickerModule,
    DatePipe,

    { provide: DateAdapter, useClass: MomentDateAdapter, deps: [MAT_DATE_LOCALE] },
    { provide: TreeviewI18n, useClass: UnitTreeviewI18n },
    { provide: MAT_DATE_FORMATS, useValue: MY_FORMATS }
  ],
  encapsulation: ViewEncapsulation.None
})


export class TargetCreateComponent implements OnInit {
  mdtMinDate = new Date(new Date().setDate(new Date().getDate()));
  mdtMaxDate = new Date(2100, 0, 1);

  mobjService: any;
  parentService: any;
  marrData: any[] = [];
  mstrServiceCode = '';
  conflictCode: boolean;
  conflictOrderService: boolean;
  marrGroups: GroupModel[] = [];
  marrChannels: Channel[] = [];
  marrChannelsNewService: Channel[] = [];
  marrUnits: DVT[] = [];
  marrServiceCycle: apParam[] = [];
  marrServiceCalc: apParam[] = [];
  marrNodeTreeviewServices;
  value;

  newServiceForm: FormGroup;
  mobjNewService: ServiceRequest;
  serviceCyclee;

  marrUnitsNew: TreeviewItem[] = [];
  // serviceOrderFormat: any;
  marrShops: string[] = [];

  mobjModalRef: BsModalRef;

  mobjConfig = TreeviewConfig.create({
    hasAllCheckBox: true,
    hasFilter: true,
    hasCollapseExpand: false,
    maxHeight: 200,
    decoupleChildFromParent: true
  });
  mblnChec = false;

  fromDateValidator: boolean;
  fromDatePattern: boolean;
  fromDateNull: boolean;
  fromDateYear: boolean;
  fromDateEndlessToDate: boolean;

  toDateValidator: boolean;
  toDateValidPatter: boolean;
  toDateValidToday: boolean;
  toDateYear: boolean;

  mblCheckSubmit: boolean;
  vblnCheckService: boolean;
  index = 1;

  mstrServiceName = '';
  mblnServiceName: boolean;


  constructor(
    private serviceService: ServiceService,
    private groupsService: GroupsService,
    private channelService: ChannelService,
    private unitService: UnitService,
    private fb: FormBuilder,
    private datePipe: DatePipe,
    private toastr: ToastrService,
    private translate: TranslateService,
    private dialogRef: MatDialogRef<TargetCreateComponent>,
    private modalService: BsModalService,
    private downLoad: DownloadService,
    private warningReceiveService: WarningReceiveService
  ) {
    dialogRef.disableClose = true;
  }

  ngOnInit() {

    this.mblnServiceName = false;
    this.getAllService();
    this.getChannels();
    this.getGroups();
    this.getUnits();
    this.createServiceForm();
    this.getServiceCycle();
    this.getServiceCalc();
    this.parentService = null;
    this.serviceService.serviceTree$.subscribe(tree => {
      this.marrNodeTreeviewServices = tree;
    });

    this.serviceService.service$.subscribe(data => {
      this.mobjService = data;
      this.value = data.parentId;
      this.findServiceParent(this.mobjService.parentId);
    });

    // this.serviceOrderFormat = '';
  }

  /**
   * Tạo form reactive
   *
   * @author: Manhtd
   * @version: 1.0
   * @since: 11/2019
   */
  createServiceForm() {
    this.newServiceForm = this.fb.group(
      {
        name: ['', [Validators.required]],
        groupId: [null, Validators.required],
        code: ['', [Validators.required, Validators.pattern('^\\s{0,10}?[_A-Za-z0-9]{1,50}\\s{0,10}$')]],
        serviceType: [null, Validators.required],
        // channels: [null, Validators.required],
        serviceCycle: [null, Validators.required],
        fromDate: [this.mdtMinDate],
        toDate: [''],
        // serviceOrder: [
        //   '',
        //   [
        //     Validators.pattern('^\\s{0,10}?\\-?[0-9]{1,20}\\s{0,10}$'),
        //     Validators.min(1)
        //   ]
        // ],
        status: [1, [Validators.required]],
        calcType: [null, [Validators.required]],
        unitCode: [null, [Validators.required, Validators.maxLength(50)]],
        exp: [''],
        conflictCode: ['']
      },
      {
        // validator: [EndDateAfterOrEqualValidator('fromDate', 'toDate')],
      }
    );
  }

  /**
   * Validator trong form reactive
   *
   * @author: Manhtd
   * @version: 1.0
   * @since: 11/2019
   */
  hasError(controlName: string, errorName: string) {
    return this.newServiceForm.controls[controlName].hasError(errorName);
  }

  /**
   * Tìm chiêu tiêu cha của service nhận được
   *
   * @author: Manhtd
   * @version: 1.0
   * @since: 11/2019
   * @param: parentId
   */
  findServiceParent(parentId: number) {
    this.serviceService.getAllService().subscribe((data: any) => {
      data.data.forEach(item => {
        if (parentId === item.id) {
          this.parentService = item;
        } else {
          this.parentService = null;
        }
      });
    });
  }

  /**
   * Lấy tất cả chỉ tiêu
   *
   * @author: Manhtd
   * @version: 1.0
   * @since: 11/2019
   *
   */
  getAllService() {
    this.serviceService.getAllService().subscribe((data: any) => {
      this.marrData = data.data;
    });
  }

  /**
   * Lấy toàn bộ nhóm chỉ tiêu
   *
   * @author: Manhtd
   * @version: 1.0
   * @since: 11/2019
   */
  getGroups() {
    this.groupsService
      .getGroupActive()
      .subscribe(vobjNext => { this.marrGroups = vobjNext.data; },
        error => console.error('không có group')
      );
  }

  /**
   * Lấy toàn bộ kênh từ api hiển thị lên combobox kênh
   *
   * @author: Manhtd
   * @version: 1.0
   * @since: 11/2019
   */
  getChannels() {
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
      },
      error => (this.marrChannels = [])
    );
  }

  selectUnit(unit: any) {
    this.marrShops = unit;
    this.checkService();
  }




  /**
   * Lấy toàn bộ đơn vị tính
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
   * Kiểm tra mã code đã tồn tại hay chưa
   *
   * @author: Manhtd
   * @version: 1.0
   * @since: 11/2019
   *
   */
  checkCodeConflict() {
    this.mstrServiceCode = this.mstrServiceCode.toUpperCase();
    this.serviceService.getAllService().subscribe((res: any) => {
      for (let i = 0; i < res.data.length; i++) {
        if (this.mstrServiceCode.trim().toUpperCase().replace(/ /g, '') === res.data[i].code) {
          this.conflictCode = true;
          break;
        } else {
          this.conflictCode = false;
        }
      }
    });
  }

  /**
   * Đóng dialog
   *
   * @author: Manhtd
   * @version: 1.0
   * @since: 11/2019
   */
  close() {
    this.dialogRef.close();
  }

  /**
   * Tạo mới chỉ tiêu lấy thông tin từ reactive form
   *
   * @author: Manhtd
   * @version: 1.0
   * @since: 11/2019
   */
  createNewService() {


    if (this.conflictCode) {
      this.mobjModalRef.hide();
      this.conflictCode = true;
      return;
    }

    if (this.marrShops.length == 0) {
      this.mobjModalRef.hide();
      this.vblnCheckService = true;
      return;
    }

    // if (this.conflictOrderService === true) {
    //   this.mobjModalRef.hide();
    //   this.conflictOrderService = true;
    //   return;
    // }

    if (this.newServiceForm.get('name').value.toString().trim() == '') {
      this.mobjModalRef.hide();
      this.mblnServiceName = true;
      return;
    }

    if (this.fromDatePattern === true) {
      this.mobjModalRef.hide();
      this.fromDatePattern = true;
      return;
    }

    if (this.fromDateValidator === true) {
      this.mobjModalRef.hide();
      this.fromDateValidator = true;
      return;
    }

    if (this.fromDateNull === true) {
      this.mobjModalRef.hide();
      this.fromDateNull = true;
      return;
    }

    if (this.fromDateYear === true) {
      this.mobjModalRef.hide();
      this.fromDateYear = true;
      return;
    }

    if (this.fromDateEndlessToDate === true) {
      this.mobjModalRef.hide();
      this.fromDateEndlessToDate = true;
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

    if (this.toDateYear === true) {
      this.mobjModalRef.hide();
      this.toDateYear = true;
      return;
    }

    if (this.toDateValidToday === true) {
      this.mobjModalRef.hide();
      this.toDateValidToday = true;
      return;
    }

    this.mobjNewService = new ServiceRequest();
    this.mobjNewService.name = this.newServiceForm.get('name').value.toString().trim();
    this.mobjNewService.groupServiceId = Number(
      this.newServiceForm.get('groupId').value
    );
    this.mobjNewService.serviceType = Number(
      this.newServiceForm.get('serviceType').value
    );
    this.mobjNewService.serviceCycle = Number(
      this.newServiceForm.get('serviceCycle').value
    );


    this.mobjNewService.fromDate = this.datePipe.transform(new Date(this.newServiceForm.get('fromDate').value), 'yyyy-MM-dd');

    if (this.newServiceForm.get('toDate').value != '' && this.newServiceForm.get('toDate').value != null) {
      this.mobjNewService.toDate = this.datePipe.transform(
        new Date(this.newServiceForm.get('toDate').value),
        'yyyy-MM-dd'
      );
    }

    this.mobjNewService.changeDatetime = this.datePipe.transform(
      new Date(),
      'yyyy-MM-dd'
    );

    // const formatServiceOrder = Number(this.newServiceForm.get('serviceOrder').value.toString().trim().toUpperCase().replace(/ /g, ''));

    // if (formatServiceOrder === 0) {
    //   this.mobjNewService.serviceOrder = null;
    // } else {
    //   this.mobjNewService.serviceOrder = formatServiceOrder;
    // }

    this.mobjNewService.status = this.newServiceForm.get('status').value;
    this.mobjNewService.serviceCalcType = this.newServiceForm.get('calcType').value;
    this.mobjNewService.unitCode = this.newServiceForm.get('unitCode').value;
    this.mobjNewService.serviceOrder = null;

    if (this.newServiceForm.get('exp').value != null && this.newServiceForm.get('exp').value != '') {
      if (this.newServiceForm.get('exp').value.toString().trim() == '') {
        this.mobjNewService.exp = null;
      } else {
        this.mobjNewService.exp = this.newServiceForm.get('exp').value.toString().trim();
      }
    } else {
      this.mobjNewService.exp = null;
    }

    this.mobjNewService.code = this.newServiceForm.get('code').value.toString().trim().toUpperCase().replace(/ /g, '');

    if (this.mobjService.id == null || this.mobjService.id === -1) {
      this.mobjNewService.parentId = 0;
    } else {
      this.mobjNewService.parentId = this.mobjService.id;
    }

    this.mobjNewService.vdsChannelCode = this.marrShops.toString();

    // ************************** //
    this.mobjNewService.importType = null;
    this.mobjNewService.user = null;
    this.mobjNewService.assignType = 0;
    this.mobjNewService.congVan = null;
    this.mobjNewService.name.trim();
    this.mobjNewService.code.toUpperCase().trim();

    this.serviceService.createNewService(this.mobjNewService).subscribe(data => {
      this.showSuccess(this.translate.instant('management.warningconfig.addsuccess'));
      this.serviceService.getAllService().subscribe((res: any) => {
        this.serviceService.setAllService(res);
        this.serviceService.getAllServiceByStatus().subscribe((res: any) => {
          this.serviceService.setAllServiceStatus(res);
        });
      });

      
      const marr: WarningReceive[] = [];
      this.warningReceiveService.setReload(marr);
     
      this.marrUnitsNew = [];
      this.mobjModalRef.hide();
      this.getChannels();
    });

    this.createServiceForm();
    this.index = 1;
  }

  /**
 * Hiện thông báo thành công
 * @author: Manhtd
 * @version: 1.0
 * @since: 11/2019
 */
  showSuccess(message: string) {
    this.toastr.success(message,
      this.translate.instant('management.service.message.success'), {
      timeOut: 2000,
      positionClass: 'toast-top-center',
    });
  }

  /**
 * Hiện thông báo lỗi
 *
 * @author: Manhtd
 * @version: 1.0
 * @since: 11/2019
 */
  showError(message: string) {
    this.toastr.error(message,
      this.translate.instant('management.service.message.fail'), {
      timeOut: 2000,
      positionClass: 'toast-top-center',
    });
  }

  /**
 * Comfirm template
 *
 * @author: Manhtd
 * @version: 1.0
 * @since: 11/2019
 */
  confirmCreate(pobjTemplate: TemplateRef<any>) {
    this.mobjModalRef = this.modalService.show(pobjTemplate, {
      ignoreBackdropClick: true
    }
    );
  }

  /**
* Đóng dialog
*
* @author: Manhtd
* @version: 1.0
* @since: 11/2019
*/
  onBackConfirm() {
    this.mobjModalRef.hide();
  }

  /**
* Valid từ ngày (fromDate)
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
    this.fromDateYear = false;

    if (event.targetElement.value.toString().trim().replace(/ /g, '') !== '') {
      const fromDateYear = this.datePipe.transform(new Date(event.value), 'yyyy');
      if (Number(fromDateYear) >= 2100 || Number(fromDateYear) <= 1900) {
        this.fromDateYear = true;
        return;
      } else if (event.value == null) {
        this.fromDatePattern = true;
        return;
      } else {
        const toDay = this.datePipe.transform(new Date(), 'yyyy-MM-dd');
        const dateChange = this.datePipe.transform(new Date(event.value), 'yyyy-MM-dd');

        if (dateChange < toDay) {
          this.fromDateValidator = true;
          return;
        } else if (this.newServiceForm.get('toDate').value != null && this.newServiceForm.get('toDate').value != '') {
          const toDate = this.datePipe.transform(new Date(this.newServiceForm.get('toDate').value), 'yyyy-MM-dd');
          if (dateChange > toDate) {
            this.fromDateEndlessToDate = true;
            return;
          }
        } else if (this.newServiceForm.get('toDate').value == null || this.newServiceForm.get('toDate').value == '') {
          this.fromDateValidator = false;
          this.fromDatePattern = false;
          this.fromDateNull = false;
          this.fromDateEndlessToDate = false;
          return;
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
* Valid đến ngày (toDate)
*
* @author: Manhtd
* @version: 1.0
* @since: 11/2019
*/
  checkToDate(event) {

    this.toDateValidToday = false;
    this.toDateValidPatter = false;
    this.toDateValidator = false;
    this.toDateYear = false;

    const toDay = this.datePipe.transform(new Date(), 'yyyy-MM-dd');
    const dateChange = this.datePipe.transform(new Date(event.value), 'yyyy-MM-dd');

    if (event.targetElement.value.toString().trim().replace(/ /g, '') !== '') {
      const toDateYear = this.datePipe.transform(new Date(event.value), 'yyyy');
      if (Number(toDateYear) >= 2100 || Number(toDateYear) <= 1900) {
        this.toDateYear = true;
        return;
      }
      if (dateChange < toDay) {
        if (dateChange == '1970-01-01') {
          this.toDateValidPatter = true;
          return;
        } else {
          this.toDateValidToday = true;
          return;
        }
      } else if (this.newServiceForm.get('fromDate').value != null && this.newServiceForm.get('fromDate').value != '') {
        const fromDate = this.datePipe.transform(new Date(this.newServiceForm.get('fromDate').value), 'yyyy-MM-dd');
        if (fromDate > dateChange) {
          this.fromDateValidator = false;
          this.fromDatePattern = false;
          this.fromDateNull = false;
          this.fromDateEndlessToDate = true;
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
    } else {
      this.fromDateEndlessToDate = false;
      return;
    }
  }


  /**
* Kiểm tra ServiceOrder unique (fromDate)
*
* @author: Manhtd
* @version: 1.0
* @since: 11/2019
*/
  // checkUniqueOrderService() {
  //   this.conflictOrderService = false;
  //   if (Number(this.serviceOrderFormat) !== 0 && !isNaN(this.serviceOrderFormat)) {
  //     this.serviceService.getServiceByOrder(this.serviceOrderFormat).subscribe((res: any) => {
  //       if (res.data.length > 0) {
  //         this.conflictOrderService = true;
  //         return;
  //       } else {
  //         this.conflictOrderService = false;
  //         return;
  //       }
  //     });
  //   } if (this.serviceOrderFormat == '') {
  //     this.conflictOrderService = false;
  //     return;
  //   }
  // }


  /**
* Tải file hướng dẫn công thức (fromDate)
*
* @author: Manhtd
* @version: 1.0
* @since: 11/2019
*/
  downloadGuildEXP() {
    this.downLoad.downloaGuildEXP().subscribe(data => {
      this.saveFile(data.body, 'GuildEXP.docx');
    });
  }

  saveFile(pobjData: any, pstrFilename?: string) {
    const vobjBlob = new Blob([pobjData], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
    fileSaver.saveAs(vobjBlob, pstrFilename);
  }

  checkService() {
    if (this.marrShops.length == 0) {
      if (this.index !== 1) {
        this.vblnCheckService = true;
      }
      this.index = 2;
    } else {
      this.vblnCheckService = false;
    }
  }

  checkNameEmpty() {
    if (this.newServiceForm.get('name').value.toString().trim() == '' && this.newServiceForm.get('name').value != '') {
      this.mblnServiceName = true;
    } else {
      this.mblnServiceName = false;
    }
  }

}
