import { Component, OnInit, Output, Input, AfterViewInit, TemplateRef, ViewChild } from '@angular/core';
import { ServiceService } from '../../../../services/management/service.service';
import { ChannelService } from '../../../../services/management/channel.service';
import { warningservice } from '../../../../models/warningservice';
import { MatDialog } from '@angular/material';
import { Channel } from '../../../../models/Channel.model';
import { GroupsService } from '../../../../services/management/group.service';
import { ServiceChannelService } from '../../../../services/serviceChannel.service';
import { warningconfig } from '../../../../services/management/warning.service';
import { EventEmitter } from 'events';
import { ServiceModel } from '../../../../models/service.model';
import { apparam } from '../../../../services/management/apparram.service';
import { apParam } from '../../../../models/apParam.model';
import { BsModalRef, BsModalService } from 'ngx-bootstrap';
import { WarningCreateComponent } from '../warning-create/warning-create.component';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { config } from '../../../../config/application.config';
import * as fileSaver from 'file-saver';
import { DownloadService } from '../../../../services/management/download.service';
import { WarningEditComponent } from '../warning-edit/warning-edit.component';
import { ServiceWarningModel } from '../../../../models/ServiceWarning.model';
import { TranslateService } from '@ngx-translate/core';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-mng-warrning',
  templateUrl: './mng-warrning.component.html',
  styleUrls: ['./mng-warrning.component.scss']
})
export class MngWarrningComponent implements OnInit, AfterViewInit {

  @Output() changeService = new EventEmitter();
  @Input() serviceIdChange: any;
  @ViewChild(WarningCreateComponent) add: WarningCreateComponent;

  servicemodel: ServiceModel[] = [];
  marrChannels: Channel[] = [];
  warningc: warningservice[] = [];
  app: apParam[] = [];
  mstrstatus = null;
  mblnConfirm = false;
  showCreateButton = true;
  mblnCheckAll = false;
  sservice: any;
  localServiceName;
  serviveId = 0;
  wStatus: string;
  mobjModalRef: BsModalRef;
  mobjFileList: FileList;
  mblnCheckFileNull = false;
  mobjDataUpload;
  mnbrCode;
  mstrMessageUpload;
  mnbrSumSuccessfulRecord;
  mnbrSumRecord;
  mstrResultFileName;
  mblnIsSelectFile;
  mblnIsClickHere = false;
  showMessageUpload = false;
  p = 1;
  reloadPage = true;
  warningTarget: warningservice;
  currentPage = 1;
  pageSize = config.pageSize;
  serviceId: number;
  id: number;
  status: number;
  showStatus = false;

  constructor(
    private http: HttpClient,
    private toastr: ToastrService,
    private downloadService: DownloadService,
    private service: ServiceService,
    private serviceService: ServiceService,
    private channelService: ChannelService,
    private warning: warningconfig,
    public dialog: MatDialog,
    private Apparam: apparam,
    private groupsService: GroupsService,
    private serviceChannelService: ServiceChannelService,
    private modalService: BsModalService,
    private translate: TranslateService,
  ) {
  }

  ngAfterViewInit(): void {
  }

  ngOnInit() {
    this.showStatus = false;
    this.getData();
    this.getAllApp();
    this.getChannels();
    this.warningTarget = new warningservice(null, '', '', null, '', '');
    this.warning.setServiceWarning([]);
    this.warning.reloadWarning$.subscribe(res => {
      if (res) {
        this.currentPage = 1;
      }else{
        this.reloadPage = false;
      }
      this.loadData();
    });
  }

  getData() {
    this.service.service$.subscribe(data => {
      if (data.id !== -1) {
        this.sservice = data;
        this.showCreateButton = true;
        this.serviveId = data.id;
        this.status = data.status;
        if (this.serviveId) {
          this.localServiceName = null;
          this.showCreateButton = false;
        }
        if (data['id']) {
          this.warning.getAllWarningById(data.id).subscribe(listWarning => {
            this.warning.setServiceWarning(listWarning.data);
            if (this.reloadPage) {
              this.currentPage = 1;
            }
            this.reloadPage = true;
          });
        }
      } else {
        this.sservice = [];
      }
    });
  }
  loadData(index?: number) {
    this.showStatus = false;
    this.warning.serviceWarning$.subscribe(
      (listWarning: any) => {
        this.showStatus = false;
        this.warningTarget = new warningservice(null, '', '', null, '', '');
        this.warningc = listWarning;
        if (listWarning.length === 0 || !listWarning) {
          this.warningTarget = new warningservice(null, '', '', null, '', '');
          // this.warningTarget = null;
        }
        if (listWarning.length > 0) {
          if (!this.warningTarget) {
            return;
          } else {
            if ((index || index === 0) && this.warningTarget.wcID === this.warningc[index].wcID) {
              this.warningTarget = this.warningc[index];
            }
          }
        }

        this.getChannelName();
        this.getAppParam();

        this.warningc.forEach(element => {
          element.serviceName = this.sservice.name;
          element.check = false;

        });
        this.mblnCheckAll = false;
        this.mstrstatus = '';
      });

  }

  getChannels() {
    this.channelService.getChannels().subscribe(
      vobjNext => {
        this.marrChannels = vobjNext;
      },
      error => (this.marrChannels = [])
    );
  }

  getAllApp() {
    this.Apparam.getApparam('WARNING_LEVEL').subscribe(vobjNext => {
      this.app = vobjNext.data;
    });
  }

  onFillData(children) {
    this.showStatus = true;
    this.warningTarget = children;
  }

  getChannelName() {
    for (const warning of this.warningc) {
      for (const channel of this.marrChannels) {
        if (warning.vdscCode === channel.code) {
          warning.channelName = channel.name;
        }
      }
    }
  }

  private getAppParam() {
    for (const warning of this.warningc) {
      for (const appParam of this.app) {
        if (warning.wlevel == appParam.code) {
          warning.appParamName = appParam.name;
          break;
        }
      }
    }
  }

  openDialog() {
    const dialogRef = this.dialog.open(WarningCreateComponent, {
      maxWidth: '90vw',
      maxHeight: '90vh',
      width: '70vw',
      autoFocus: false
    });
    dialogRef.afterClosed().subscribe(result => {

      // this.loadData();
    });
  }

  upLoad() {
    this.showMessageUpload = true;
    if (this.mobjFileList) {
      const vfileFile: File = this.mobjFileList[0];
      const vobjFormData: FormData = new FormData();
      vobjFormData.append('file', vfileFile);
      const vobjHeaders = new HttpHeaders();
      vobjHeaders.append('Content-type', 'application/json');
      const vobjOptions = { headers: vobjHeaders };
      this.http.post(config.upload_mngtarget_API, vobjFormData, vobjOptions)
        .subscribe(
          vobjData => {
            this.resultUpload(vobjData);
          },
          vobjError => (console.log(vobjError)),
          () => {
            this.mobjFileList = null;
          }
        );
    } else {
      this.mblnCheckFileNull = true;
    }
    return;
  }

  onBack() {
    this.showMessageUpload = false;
    this.mobjModalRef.hide();
    this.mblnCheckFileNull = false;
    this.mobjFileList = null;
  }

  selectFile(file) {
    this.mstrMessageUpload = false;
    this.mobjFileList = file.target.files;
    if (this.mobjFileList.length > 0) {
      this.mblnCheckFileNull = false;
    } else {
      this.mblnCheckFileNull = true;
    }
  }

  resultUpload(pobjData) {
    this.mobjDataUpload = pobjData['data'];
    this.mnbrCode = pobjData.code;
    this.mstrMessageUpload = this.mobjDataUpload['message'];
    this.showError(this.mnbrCode);
  }

  showError(pnbrCode: number) {
    if (pnbrCode === undefined) {
    } else if (pnbrCode === 200) {
      this.mnbrSumSuccessfulRecord = this.mobjDataUpload.sumSuccessfulRecord;
      this.mnbrSumRecord = this.mobjDataUpload.sumRecord;
      this.mstrResultFileName = this.mobjDataUpload.resultFileName;
    } else {
      this.mobjFileList = null;
    }
    this.mblnIsSelectFile = true;
  }

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

  saveFile(pobjData: any, pstrFilename?: string) {
    const vobjBlob = new Blob([pobjData], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
    fileSaver.saveAs(vobjBlob, pstrFilename);
  }

  downloadWarningTemplate() {
    this.warning.downloadWarningTemplate().subscribe(pobjData => {
      this.saveFile(pobjData.body, this.translate.instant('management.group.message.template'));
    });
  }

  confirm(pobjTemplate: TemplateRef<any>) {
    this.mobjModalRef = this.modalService.show(pobjTemplate, {
      ignoreBackdropClick: true
    }
    );
  }

  pageChangeMng(page: number) {
    this.mblnCheckAll = false;
    let total = this.currentPage * 10;
    if (this.currentPage * 10 > this.warningc.length) {
      total = this.warningc.length;
    }
    for (let i = (this.currentPage - 1) * 10; i < total; i++) {
      this.warningc[i].check = false;
    }
    this.mstrstatus = '';
    this.currentPage = page;
  }

  editService(childen, index) {
    const dialogRef = this.dialog.open(WarningEditComponent, {
      maxWidth: '70vw',
      width: '60vw',
      maxHeight: '570px',
      data: { serviceWarning: childen },
      autoFocus: false
    });

    // dialogRef.afterClosed().subscribe(result => {
    //   this.loadData(index);
    // });
  }

  select(id: number) {
    let total = this.currentPage * 10;
    for (let i = (this.currentPage - 1) * 10; i < total; i++) {
      if (this.warningc[i].wcID === id) {
        if (this.warningc[i].check === true) {
          this.warningc[i].check = false;
        } else {
          this.warningc[i].check = true;
        }
        break;
      }
    }
    let vblncheck = true;
    if (this.currentPage * 10 > this.warningc.length) {
      total = this.warningc.length;
    }
    for (let i = (this.currentPage - 1) * 10; i < total; i++) {
      if (this.warningc[i].check === false) {
        vblncheck = false;
      }
    }
    if (vblncheck) {
      this.mblnCheckAll = true;
    } else {
      this.mblnCheckAll = false
        ;
    }
    this.CheckStatus();
  }

  checkAll() {
    let vblnCheck = false;
    if (this.mblnCheckAll === false) {
      vblnCheck = true;
    }
    let total = this.currentPage * 10;
    if (this.currentPage * 10 > this.warningc.length) {
      total = this.warningc.length;
    }
    for (let i = (this.currentPage - 1) * 10; i < total; i++) {
      if (vblnCheck === true) {
        this.warningc[i].check = true;
      } else {
        this.warningc[i].check = false;
      }
    }
    this.CheckStatus();
  }

  CheckStatus() {
    this.mstrstatus = '0';
    const marr = this.warningc.filter(item => item.check === true);
    if (marr.length > 0) {
      this.mstrstatus = marr[0].wStatus;
      for (let i = 1; i < marr.length; i++) {
        if (marr[i].wStatus !== this.mstrstatus) {
          this.mstrstatus = '-1';
          break;
        }
      }
    } else {
      this.mstrstatus = '';
    }
  }

  openConfirm(pobjTemplate: TemplateRef<any>, type: number) {
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

  back() {
    this.mobjModalRef.hide();
  }

  LockUnlock(status: string) { 
    let varrLock = this.warningc.filter(item => item.check === true);

    varrLock.reverse();
    for (let i = 0; i < varrLock.length; i++) {
      let check = true;
      const a: number[] = [];
      for (let j = i + 1; j < varrLock.length; j++) {
        if (varrLock[i].channelName == varrLock[j].channelName && varrLock[i].wlevel == varrLock[j].wlevel) {
          check = false;
        }
      }
      if (check) {
        const serviceWarning = new ServiceWarningModel();
        serviceWarning.serviceId = varrLock[i].svID;
        serviceWarning.vdsChannelCode = varrLock[i].vdscCode;
        serviceWarning.waningLevel = varrLock[i].wlevel;
        serviceWarning.status = status;
        serviceWarning.fromValue = parseFloat(varrLock[i].wfvalue);
        serviceWarning.toValue = parseFloat(varrLock[i].wovalue);
        if (varrLock[i].wexp == '') {
          serviceWarning.exp = null;
        } else {
          serviceWarning.exp = varrLock[i].wexp;
        }
        serviceWarning.id = varrLock[i].wcID;
        this.serviceService.updateServiceWarning(serviceWarning).subscribe(data => {
          if (i === varrLock.length - 1) {
            if (data.code == 200) {
              this.warning.getAllWarningById(this.serviveId).subscribe(listWarning => {
                this.warning.setServiceWarning(listWarning.data);
                if (status === '1') {
                  this.showSuccess(this.translate.instant('management.group.message.unlockSuccess'));
                } else {
                  this.showSuccess(this.translate.instant('management.group.message.lockSuccess'));
                }
                this.mobjModalRef.hide();
              });
            } else {
              this.serviceService.getAllService().subscribe((res: any) => {
                this.serviceService.setAllService(res);
              });
              if (data.errors == 'Duplicate') {
                this.showErrors(this.translate.instant('management.warningconfig.duplicate'));
                this.mobjModalRef.hide();
              } else if (data.errors == 'DuplicateFromAndTovalue') {
                this.showErrors(this.translate.instant('management.warningconfig.duplicatevalue'));
                this.mobjModalRef.hide();
              } else {
                this.showErrors(data.errors);
                this.mobjModalRef.hide();
              }
            }
            this.reloadPage = false;
          }
        });
      }
    }
  }
  showErrors(message: string) {
    this.toastr.error(message,
      this.translate.instant('management.service.message.fail'), {
      timeOut: 2000,
      positionClass: 'toast-top-center',
    });
  }
  showSuccess(message: string) {
    this.toastr.success(message,
      this.translate.instant('management.service.message.success'), {
      timeOut: 2000,
      positionClass: 'toast-top-center',
    });
  }
}
