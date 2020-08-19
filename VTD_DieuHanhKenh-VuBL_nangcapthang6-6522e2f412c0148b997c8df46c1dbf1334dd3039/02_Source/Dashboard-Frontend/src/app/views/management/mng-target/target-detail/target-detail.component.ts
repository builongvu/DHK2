import { MomentDateAdapter } from '@angular/material-moment-adapter';
import { BsModalRef, BsModalService } from 'ngx-bootstrap/modal';
import { HttpHeaders, HttpClient } from '@angular/common/http';
import { config } from './../../../../config/application.config';
import { DownloadService } from './../../../../services/management/download.service';
import { error } from '@angular/compiler/src/util';
import { ToastrService } from 'ngx-toastr';
import { TranslateService } from '@ngx-translate/core';
import { UnitService } from './../../../../services/management/unit.service';
import { ServiceRequest } from './../../../../models/service-request.model';
import { TargetCreateComponent } from './../target-create/target-create.component';
import { ServiceChannel } from './../../../../models/ServiceChannel.model';
import { ServiceChannelService } from './../../../../services/serviceChannel.service';
import { Channel } from './../../../../models/Channel.model';
import { ChannelService } from './../../../../services/management/channel.service';
import { GroupModel } from './../../../../models/group.model';
import { GroupsService } from './../../../../services/management/group.service';
import { ServiceService } from './../../../../services/management/service.service';
import { Component, OnInit, ɵConsole, TemplateRef, ViewEncapsulation, ChangeDetectorRef } from '@angular/core';
import { MatDialog, MatDialogConfig, MatDialogRef } from '@angular/material/dialog';
import * as fileSaver from 'file-saver';
import { TargetEditComponent } from '../target-edit/target-edit.component';
import { DatePipe } from '@angular/common';
import { MAT_DATE_LOCALE, DateAdapter, MAT_DATE_FORMATS } from '@angular/material';
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
  selector: 'app-target-detail',
  templateUrl: './target-detail.component.html',
  styleUrls: ['./target-detail.component.scss'],
  providers: [DatePipe,
    { provide: MatDialogRef, useValue: { TargetDetailComponent } },
    { provide: DateAdapter, useClass: MomentDateAdapter, deps: [MAT_DATE_LOCALE] },
    { provide: MAT_DATE_FORMATS, useValue: MY_FORMATS }
  ],
  encapsulation: ViewEncapsulation.None
})
export class TargetDetailComponent implements OnInit {
  service: any;
  unitOfService: any;
  parentService: any;
  marrGroups: GroupModel[] = [];
  mobjGroup: any;
  serviceChild: any;
  codeChannels: any[] = [];
  marrChannels: Channel[] = [];
  marrChannelsOfSerive: ServiceChannel[] = [];
  marrChannelsServiceFull: any[] = [];
  strTooltipChannels = '';
  marrChannelCode: string[] = [];
  marrChildenService = [];
  mobjChildGroup: GroupModel;
  check = false;
  mblUnlockHidden: boolean;
  mblLockHidden: boolean;
  marrServiceLock: number[] = [];
  status: number;
  nmbLengthOfChildInPage: number;

  numberCount = 0;
  mstrServiceCycle: number;
  mblCreateButton: boolean;

  parentServiceChoose: any;
  mobjGroupChoose: any;
  mobjUnitOfService: any;

  mobjDataUpload;
  mnbrCode;
  mstrMessageUpload;
  mobjFileList: FileList;
  mblnIsSelectFile = false;
  mblnCheckFileNull = false;
  mobjModalRef: BsModalRef;
  mblnIsClickHere = false;
  mnbrSumSuccessfulRecord;
  mnbrSumRecord;
  mstrResultFileName;
  mobjServiceChoice: any;
  hindChild: boolean;

  recordInPage: number;
  recordInPageMin: number;
  p = 1;
  perPage: number;
  totalRecord: number;

  serviceInPage: any[] = [];
  checkBoxInPage: boolean;
  checkInPageOrAll: boolean;
  dialogCheckAll: boolean;

  constructor(
    private serviceService: ServiceService,
    private groupsService: GroupsService,
    private serviceChannelService: ServiceChannelService,
    public dialog: MatDialog,
    private channelService: ChannelService,
    private unitService: UnitService,
    private toastr: ToastrService,
    private translate: TranslateService,
    private datePipe: DatePipe,
    private downLoad: DownloadService,
    private http: HttpClient,
    private downloadService: DownloadService,
    private modalService: BsModalService,
    private warningReceiveService: WarningReceiveService,
    private cd: ChangeDetectorRef

  ) { }

  ngOnInit() {
    this.checkBoxInPage = false;
    this.hindChild = true;
    this.check = false;
    this.mobjServiceChoice = null;
    this.serviceService.treeData$.subscribe(data => {
      data.forEach(element => {
        if (element.id === -1) {
          this.serviceService.setService(element);
        }
      });
    });
    this.getService();
    this.warningReceiveService.reload$.subscribe(res => {
      this.p = 1;
    });
  }

  /**
   * Nhận thông tin service từ component list-target
   *
   * @author: Manhtd
   * @version: 1.0
   * @since: 11/2019
   */
  getService() {
    this.getChannels();
    this.getGroups();

    this.mblCreateButton = false;

    this.serviceService.service$.subscribe(data => {

      this.checkBoxInPage = false;
      this.mblLockHidden = false;
      this.mblUnlockHidden = false;
      this.marrChildenService = [];
      this.marrServiceLock = [];
      this.service = data;
      this.mobjServiceChoice = data;
      this.parentService = this.findServiceParent(this.service.parentId);
      this.mobjGroup = this.findServiceGroup(this.service.groupServiceId);
      this.unitOfService = this.findServiceUnit(this.service.unitCode);

      this.parentServiceChoose = this.findServiceParentChoice(this.mobjServiceChoice.parentId);
      this.mobjGroupChoose = this.findServiceGroupChoice(this.mobjServiceChoice.groupServiceId);

      if (data.id == null) {
        this.marrChannelCode = [];
        this.marrChannelsServiceFull = [];
      } else {

        this.getServiceChannelByServiceId(data.id);
      }

      this.status = Number(data.status);
      if (this.status == 0) {
        this.mblCreateButton = true;
      } else {
        this.mblCreateButton = false;
      }

      if (data.length !== 0) {
        if (data.id !== -1) {
          this.marrChildenService.push(data);
          if (data.children.length > 0) {
            this.viewChild(data.children);
          } else {
            this.recordInPage = this.marrChildenService.length;
            this.recordInPageMin = 1;
          }
        }
      }
    });
  }

  /**
 * Mở dialog tạo mới chỉ tiêu
 *
 * @author: Manhtd
 * @version: 1.0
 * @since: 11/2019
 */
  openDialog() {

    if (this.service.status === '0') {
      return;
    }

    const dialogRef = this.dialog.open(TargetCreateComponent, {
      maxWidth: '75vw',
      maxHeight: '95vh',
      width: '70vw',
    });

    dialogRef.afterClosed().subscribe(result => {
      // this.p = 1;
    });
  }

  /**
 * Tìm thông tin chỉ tiêu cha
 *
 * @author: Manhtd
 * @version: 1.0
 * @since: 11/2019
 */
  findServiceParent(parentId: number) {
    if (parentId != null && parentId !== -1) {
      this.serviceService.getServiceByID(parentId).subscribe((data: any) => {
        this.parentService = data.data;
      });
    }
  }

  /**
 *Tìm thông tin nhóm của chỉ tiêu
 *
 * @author: Manhtd
 * @version: 1.0
 * @since: 11/2019
 */
  findServiceGroup(groupId: number) {
    this.groupsService.getGroups().subscribe((data: any) => {
      data.forEach(item => {
        if (groupId == item.id) {
          this.mobjGroup = item;
        }
      });
    });
  }

  findServiceParentChoice(parentId: number) {
    if (parentId != null && parentId !== -1) {
      this.serviceService.getServiceByID(parentId).subscribe((data: any) => {
        this.parentServiceChoose = data.data;
      });
    }
  }

  /**
 *Tìm thông tin nhóm của chỉ tiêu
 *
 * @author: Manhtd
 * @version: 1.0
 * @since: 11/2019
 */
  findServiceGroupChoice(groupId: number) {
    this.groupsService.getGroups().subscribe((data: any) => {
      data.forEach(item => {
        if (groupId == item.id) {
          this.mobjGroupChoose = item;
        }
      });
    });
  }

  /**
 * Tìm đơn vị tính
 *
 * @author: Manhtd
 * @version: 1.0
 * @since: 11/2019
 */
  findServiceUnit(unitCode: string) {
    this.unitService.getAllUnit().subscribe((data: any) => {
      data.data.forEach(item => {
        if (unitCode == item.code) {
          this.unitOfService = item;
        }
      });
    });
  }

  /**
 * Từ code lấy toàn bộ thông tin của kênh để hiện thị
 *
 * @author: Manhtd
 * @version: 1.0
 * @since: 11/2019
 */
  getChannelsServiceFull() {
    this.marrChannelCode = [];
    this.marrChannelsServiceFull = [];
    this.strTooltipChannels = '';
    const varrToolTipChannels: string[] = [];
    if (this.marrChannels && this.marrChannelsOfSerive) {
      if (this.marrChannels.length > 0 && this.marrChannelsOfSerive.length > 0) {
        for (let i = 0; i < this.marrChannels.length; i++) {
          for (let j = 0; j < this.marrChannelsOfSerive.length; j++) {
            if (this.marrChannels[i].code === this.marrChannelsOfSerive[j].vdsChannelCode) {
              this.marrChannelsServiceFull.push(this.marrChannels[i]);
              this.marrChannelCode.push(this.marrChannels[i].code);
              varrToolTipChannels.push(this.marrChannels[i].name);
            }
          }
        }
      }
    }

    this.strTooltipChannels = varrToolTipChannels.toString();
  }

  /**
 * Lấy toàn bộ kênh từ api
 *
 * @author: Manhtd
 * @version: 1.0
 * @since: 11/2019
 */
  getChannels() {
    this.channelService.getChannels().subscribe(
      vobjNext => {
        this.marrChannels = vobjNext;
      },
      error => (this.marrChannels = [])
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
      error => console.error('không có group')
    );
  }

  /**
 * Lấy toàn bộ kênh của chỉ tiêu theo id
 *
 * @author: Manhtd
 * @version: 1.0
 * @since: 11/2019
 * @param: idService
 */
  getServiceChannelByServiceId(idService: number) {
    if (idService > 0) {
      this.serviceChannelService
        .getServiceChannelByServiceId(idService).subscribe((data: any) => {
          this.marrChannelsOfSerive = data.data;
          this.getChannelsServiceFull();
        });
    } else {
      this.marrChannelsOfSerive = [];
    }
  }

  /**
 * Lấy toàn bộ chỉ tiêu con của service được chọn
 *
 * @author: Manhtd
 * @version: 1.0
 * @since: 11/2019
 */

  viewChild(arrChildren: any[]) {
    let no = 2;

    for (let i = 0; i < arrChildren.length; i++) {
      let serviceRequest = new ServiceRequest();
      serviceRequest = this.convertToServiceRequest(arrChildren[i]);
      serviceRequest.no = no++;

      this.marrChildenService.push(serviceRequest);

    }
    if (this.marrChildenService.length > 10) {

      this.recordInPageMin = ((this.p - 1) * 10) + 1;
      if (this.marrChildenService.length - ((this.p - 1) * 10) < 10) {
        this.recordInPage = this.marrChildenService.length;
      } else {
        this.recordInPage = this.p * 10;
      }
    } else {
      this.recordInPage = this.marrChildenService.length;
      this.recordInPageMin = 1;
    }
  }

  convertToServiceRequest(vobjChild): ServiceRequest {
    const serviceRequest = new ServiceRequest();
    serviceRequest.id = vobjChild.id;
    serviceRequest.name = vobjChild.name;
    serviceRequest.exp = vobjChild.exp;
    serviceRequest.groupServiceId = vobjChild.groupServiceId;
    serviceRequest.code = vobjChild.code;
    serviceRequest.serviceType = vobjChild.serviceType;
    serviceRequest.serviceCycle = vobjChild.serviceCycle;
    serviceRequest.toDate = vobjChild.toDate;
    serviceRequest.fromDate = vobjChild.fromDate;
    // serviceRequest.serviceOrder = vobjChild.serviceOrder;
    serviceRequest.unitCode = vobjChild.unitCode;
    serviceRequest.parentId = vobjChild.parentId;
    serviceRequest.id = vobjChild.id;
    serviceRequest.serviceCalcType = vobjChild.serviceCalcType;
    serviceRequest.grandchildren = 1;

    this.marrGroups.forEach((item: any) => {
      if (vobjChild.groupServiceId === item.id) {
        serviceRequest.groupName = item.name;
      }
    });
    serviceRequest.status = Number(vobjChild.status);
    serviceRequest.checkbox = false;
    return serviceRequest;
  }

  /**
 * Mở dialog sửa thông tin chỉ tiêu
 *
 * @author: Manhtd
 * @version: 1.0
 * @since: 11/2019
 */
  editService(serviceChild) {
    this.serviceChild = serviceChild;
    if (serviceChild.id) {
      this.serviceChannelService.getServiceChannelByServiceId(serviceChild.id).subscribe((data: any) => {
        if (data.data) {
          this.codeChannels = [];
          for (let i = 0; i < this.marrChannels.length; i++) {
            for (let j = 0; j < data.data.length; j++) {
              if (this.marrChannels[i].code === data.data[j].vdsChannelCode) {
                this.codeChannels.push(this.marrChannels[i]);
                break;
              }
            }
          }
          this.openEditDialog();
        }
      });
    }
  }

  /**
* trong dialog truyền đi chỉ tiêu con, kênh của chỉ tiêu, chỉ tiêu cha của service
*
* @author: Manhtd
* @version: 1.0
* @since: 11/2019
*/
  openEditDialog() {
    const dialogRef = this.dialog.open(TargetEditComponent, {
      maxWidth: '75vw',
      maxHeight: '95vh',
      width: '70vw',
      data: { service: this.serviceChild, channel: this.codeChannels, serviceParent: this.service }
    });

    dialogRef.afterClosed().subscribe(result => {
    });
  }

  /**
* Click vào nút check box trong chỉ tiêu cha
*
* @author: Manhtd
* @version: 1.0
* @since: 11/2019
*/
  checkAll() {
    this.marrServiceLock = [];
    this.dialogCheckAll = true;

    if (this.checkBoxInPage === false) {
      this.checkInPageOrAll = true;
      this.check = true;
      this.checkBoxInPage = true;
      this.marrChildenService.forEach(item => {
        item.checkbox = true;
        this.marrServiceLock.push(item.id);
      });

      if (this.marrServiceLock[0] == null && this.marrServiceLock.length === 1) {
        this.mblLockHidden = false;
        this.mblUnlockHidden = false;
        return;
      }

      if (this.marrServiceLock.length === 1) {
        this.dialogCheckAll = false;
      }

      this.mblLockHidden = true;
      this.mblUnlockHidden = true;
    } else {
      this.check = false;
      this.checkBoxInPage = false;
      this.checkInPageOrAll = false;
      this.dialogCheckAll = false;

      this.marrChildenService.forEach(item => {
        item.checkbox = false;
      });
      this.marrServiceLock = [];
      this.mblLockHidden = false;
      this.mblUnlockHidden = false;
    }

  }

  /**
* Click vào từng checkbox trong chỉ tiêu con
*
* @author: Manhtd
* @version: 1.0
* @since: 11/2019
*/
  checkUnit(children: ServiceRequest) {

    // this.checkInPageOrAll = false;

    if (children.grandchildren === 1) {
      this.check = false;

      if (children.checkbox === true) {
        children.checkbox = false;
      } else {
        children.checkbox = true;
      }


      const marrChild = this.marrChildenService.filter(
        item => item.checkbox === true
      );

      if (marrChild.length > 1) {
        for (let i = 0; i < marrChild.length - 1; i++) {
          if (marrChild[0].status != marrChild[i + 1].status) {
            this.mblLockHidden = true;
            this.mblUnlockHidden = true;
            break;
          } else if (marrChild[0].status == 1) {
            this.mblLockHidden = true;
            this.mblUnlockHidden = false;
          } else if (marrChild[0].status == 0) {
            this.mblLockHidden = false;
            this.mblUnlockHidden = true;
          }
        }
      }

      if (marrChild.length === 1) {
        if (marrChild[0].status == 1) {
          this.mblLockHidden = true;
          this.mblUnlockHidden = false;
        }

        if (marrChild[0].status == 0) {
          this.mblLockHidden = false;
          this.mblUnlockHidden = true;
        }
      }

      if (marrChild.length === 0) {
        this.mblLockHidden = false;
        this.mblUnlockHidden = false;
      }

      if (children.checkbox === true) {
        this.marrServiceLock.push(children.id);
      } else {
        this.marrServiceLock = this.marrServiceLock.filter(
          item => item !== children.id
        );
        this.marrServiceLock = this.marrServiceLock.filter(
          item => item !== this.service.id
        );
      }

      if (this.marrServiceLock.length === (this.recordInPage - this.recordInPageMin + 1)) {
        this.checkBoxInPage = true;
      } else {
        this.checkBoxInPage = false;
      }

      if (this.checkInPageOrAll === true) {

        const lengthOfChildLock = this.marrServiceLock.length;
        const lengthOfChild = this.marrChildenService.length;

        if (lengthOfChildLock < lengthOfChild) {
          this.marrChildenService[0].checkbox = false;
          this.checkBoxInPage = false;
          this.dialogCheckAll = false;
        }

        if (lengthOfChildLock === lengthOfChild - 1) {
          this.marrChildenService[0].checkbox = true;
          this.checkBoxInPage = true;
          this.dialogCheckAll = true;
        }

      } else {
        if (children.checkbox === true) {
          this.serviceInPage.push(children);
          if (this.serviceInPage.length === (this.recordInPage - this.recordInPageMin + 1)) {
            this.checkBoxInPage = true;
          }
        } else {
          this.serviceInPage = this.serviceInPage.filter(item => item.id !== children.id);
          this.checkBoxInPage = false;
        }
      }
    } else {
      this.checkAll();
    }
  }

  /**
 * Check vào header của trang
 *
 * @author: Manhtd
 * @version: 1.0
 * @since: 11/2019
 */
  checkInPage() {
    this.nmbLengthOfChildInPage = 0;
    this.serviceInPage = [];
    this.checkInPageOrAll = false;
    this.dialogCheckAll = false;
    for (let i = (this.p - 1) * 10; i < this.p * 10; i++) {
      if (this.marrChildenService[i] != null) {
        this.serviceInPage.push(this.marrChildenService[i]);
      }
      this.nmbLengthOfChildInPage = this.serviceInPage.length;
    }

    if (this.checkBoxInPage === false) {
      this.checkBoxInPage = true;
      this.serviceInPage.forEach(data => {
        data.checkbox = true;
      });
      this.mblLockHidden = true;
      this.mblUnlockHidden = true;
    } else {
      this.checkBoxInPage = false;
      this.serviceInPage.forEach(data => {
        data.checkbox = false;
      });

      this.marrChildenService.forEach(item => {
        item.checkbox = false;
      });
      this.serviceInPage = [];
      this.marrServiceLock = [];
      this.mblLockHidden = false;
      this.mblUnlockHidden = false;
      this.nmbLengthOfChildInPage = 0;
    }
  }

  /**
* Khóa hoặc mở khóa chỉ tiêu
*
* @author: Manhtd
* @version: 1.0
* @since: 11/2019
* @param: status (1 hoặc 0)
* @param: serviceParent (1 xóa chỉ tiêu cha, 0 không xóa)
*/
  lock(status, serviceParent) {
    if (this.checkInPageOrAll === true) {
      for (let i = 0; i < this.marrChildenService.length; i++) {
        for (let j = 0; j < this.marrServiceLock.length; j++) {

          if (serviceParent === 0) {
            if (this.marrServiceLock[j] === this.service.id) {
              continue;
            }
          }

          if (this.marrChildenService[i].id === this.marrServiceLock[j]) {
            this.marrChildenService[i].status = status;
            this.marrChildenService[i].changeDatetime = this.datePipe.transform(new Date(), 'yyyy-MM-dd');
            this.serviceService.editService(this.marrChildenService[i]).subscribe(data => {
              if (i === this.marrChildenService.length - 1) {
                this.serviceService.getAllService().subscribe((res: any) => {
                  this.serviceService.setAllService(res);
                });

                this.serviceService.getAllServiceByStatus().subscribe((res: any) => {
                  this.serviceService.setAllServiceStatus(res);
                });
              }
            });
          }
        }
      }
    } else {
      if (this.serviceInPage.length > 0) {
        for (let j = 0; j < this.serviceInPage.length; j++) {
          this.serviceInPage[j].status = status;
          this.serviceInPage[j].changeDatetime = this.datePipe.transform(new Date(), 'yyyy-MM-dd');
          this.serviceService.editService(this.serviceInPage[j]).subscribe(data => {

            this.serviceService.getAllService().subscribe((res: any) => {
              this.serviceService.setAllService(res);
            })
            this.serviceService.getAllServiceByStatus().subscribe((res: any) => {
              this.serviceService.setAllServiceStatus(res);
            });
            ;

          });

        }
      }
    }

    if (status == 1) {
      this.showSuccess(this.translate.instant('management.service.message.successUnlock'));
    } else {
      this.showSuccess(this.translate.instant('management.service.message.successLock'));
    }

    this.marrServiceLock = [];
    this.mblLockHidden = false;
    this.mblUnlockHidden = false;
    this.check = false;
    this.checkBoxInPage = false;
    this.dialogCheckAll = false;
    this.marrChildenService.forEach(item => {
      item.checkbox = false;
    });

    this.serviceInPage.forEach(item => {
      item.checkbox = false;
    });

    this.serviceInPage = [];

  }

  /**
* Tải file excel mẫu của chỉ tiêu
*
* @author: Manhtd
* @version: 1.0
* @since: 11/2019
*/
  download() {
    this.downLoad.downloadExampleService().subscribe(data => {
      this.saveFile(data.body, this.translate.instant('management.service.servicetemplate') + '.xlsx');
    });
  }

  /**
* Tải file word mẫu của công thức
*
* @author: Manhtd
* @version: 1.0
* @since: 11/2019
*/
  downloadGuildEXP() {
    this.downLoad.downloaGuildEXP().subscribe(data => {
      this.saveFile(data.body, this.translate.instant('management.service.guideexp'));
    });
  }

  /**
* Upload file excel của chỉ tiêu
*
* @author: Manhtd
* @version: 1.0
* @since: 11/2019
*/
  upLoad() {
    this.mblnIsSelectFile = true;
    if (this.mobjFileList) {
      const vfileFile: File = this.mobjFileList[0];
      const vobjFormData: FormData = new FormData();
      vobjFormData.append('file', vfileFile);
      const vobjHeaders = new HttpHeaders();
      vobjHeaders.append('Content-type', 'multipart/form-data');
      vobjHeaders.append('Accept', 'application/json');
      const vobjOptions = { headers: vobjHeaders };
      this.http.post(config.uploadFileService_API, vobjFormData, vobjOptions)
        .subscribe(
          vobjData => {
            this.resultUpload(vobjData);
          },
          vobjError => (console.log(vobjError)),
        );
    } else {
      this.mblnCheckFileNull = true;
    }

    return this.mobjDataUpload;
  }


  /**
* Chọn file
*
* @author: Manhtd
* @version: 1.0
* @since: 11/2019
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
* Kết quả sau khi upload chỉ tiêu
*
* @author: Manhtd
* @version: 1.0
* @since: 11/2019
*/
  resultUpload(pobjData) {
    this.mobjDataUpload = pobjData['data'];
    this.mnbrCode = pobjData.code;
    this.mstrMessageUpload = this.mobjDataUpload['message'];
    if (this.mnbrCode == 200) {
      this.serviceService.getAllService().subscribe((res: any) => {
        this.serviceService.setAllService(res);
        this.serviceService.getAllServiceByStatus().subscribe((res: any) => {
          this.serviceService.setAllServiceStatus(res);
        });
      });
      this.mnbrSumSuccessfulRecord = this.mobjDataUpload.sumSuccessfulRecord;
      this.mnbrSumRecord = this.mobjDataUpload.sumRecord;
      this.mstrResultFileName = this.mobjDataUpload.resultFileName;
      if (this.mnbrSumSuccessfulRecord > 0) {
        // this.showSuccess(this.translate.instant('management.service.message.successImport'));
      }
    } else {
      // this.showError(this.translate.instant('management.service.message.failImport'));
      this.mobjFileList = null;
    }
    this.mblnIsSelectFile = true;
  }

  saveFile(pobjData: any, pstrFilename?: string) {
    const vobjBlob = new Blob([pobjData], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
    fileSaver.saveAs(vobjBlob, pstrFilename);
  }

  /**
* confirm dialog
*
* @author: Manhtd
* @version: 1.0
* @since: 11/2019
*/
  confirm(pobjTemplate: TemplateRef<any>) {
    this.mobjModalRef = this.modalService.show(pobjTemplate, {
      ignoreBackdropClick: true
    }
    );
  }

  /**
* confirm dialog Lock
*
* @author: Manhtd
* @version: 1.0
* @since: 11/2019
*/
  confirmLock(pobjTemplate: TemplateRef<any>) {
    this.mobjModalRef = this.modalService.show(pobjTemplate, {
      ignoreBackdropClick: true
    }
    );
  }

  /**
* confirm dialog unlock
*
* @author: Manhtd
* @version: 1.0
* @since: 11/2019
*/
  confirmUnLock(pobjTemplate: TemplateRef<any>) {
    this.mobjModalRef = this.modalService.show(pobjTemplate, {
      ignoreBackdropClick: true
    }
    );
  }

  /**
* Thoát khỏi dialog upload
*
* @author: Manhtd
* @version: 1.0
* @since: 11/2019
*/
  onBack() {
    this.mblnIsClickHere = false;
    this.mnbrCode = null;
    this.mobjFileList = null;
    this.mblnCheckFileNull = false;
    this.mobjModalRef.hide();
  }

  /**
* thoát khỏi confirm dialog
*
* @author: Manhtd
* @version: 1.0
* @since: 11/2019
*/
  onBackConfirm() {
    this.mobjModalRef.hide();
  }

  /**
* Xác nhận khóa hoặc mở
*
* @author: Manhtd
* @version: 1.0
* @since: 11/2019
*/
  acceptLock(status, serviceParent) {
    this.lock(status, serviceParent);
    this.mobjModalRef.hide();
  }

  /**
* Download kết quả sau khi upload chỉ tiêu
*
* @author: Manhtd
* @version: 1.0
* @since: 11/2019
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
* Đẩy dữ liệu chỉ tiêu con lên tab thông tin
*
* @author: Manhtd
* @version: 1.0
* @since: 11/2019
*/
  viewService(idService: number) {
    this.serviceService.getAllService().subscribe((data: any) => {
      data.data.forEach((element: any) => {
        if (element.id == idService) {
          this.service = element;
          this.parentService = this.findServiceParent(element.parentId);
          this.mobjGroup = this.findServiceGroup(element.groupServiceId);
          this.unitOfService = this.findServiceUnit(element.unitCode);
          if (element.id == null) {
            this.marrChannelCode = [];
            this.marrChannelsServiceFull = [];
          } else {
            this.getServiceChannelByServiceId(element.id);
          }
        }
      });
    });
  }

  /**
* Hiển thị thông báo thành công
*
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
* Hiển thị thông báo thất bại
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
* Sự kiện thay đổi trang
*
* @author: Manhtd
* @version: 1.0
* @since: 11/2019
*/
  pageChanged(event) {
    this.p = event;
    if (!this.checkInPageOrAll) {
      this.checkBoxInPage = false;
      if (this.serviceInPage.length > 0) {
        this.serviceInPage.forEach(data => {
          data.checkbox = false;
        });
        this.serviceInPage = [];
        this.mblLockHidden = false;
        this.mblUnlockHidden = false;
      }
      this.marrChildenService.forEach(data => {
        data.checkbox = false;
      });
    }



    this.marrServiceLock = [];
    if (this.marrChildenService.length > 10) {
      this.recordInPage = 10;
      this.recordInPage = 10 * this.p;
      this.recordInPageMin = this.recordInPage - 9;
      if (this.recordInPage > this.marrChildenService.length) {
        this.recordInPage = this.marrChildenService.length;
      }
    } else {
      this.recordInPageMin = 1;
      this.recordInPage = this.marrChildenService.length;
    }
  }

}
