import { AfterViewInit, Component, ElementRef, OnInit, TemplateRef, ViewChild, ViewEncapsulation } from '@angular/core';
import { DateAdapter, MAT_DATE_FORMATS, MAT_DATE_LOCALE, MatDatepickerModule, MatDialogRef } from '@angular/material';
import { DatePipe } from '@angular/common';
import { MomentDateAdapter } from '@angular/material-moment-adapter';

import { TreeviewI18n, TreeviewItem } from 'ngx-treeview';
import { UnitTreeviewI18n } from '../config-tree-select/unit-treeview-i18n';
import { MY_FORMATS } from '../target-create/target-create.component';

import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { GroupsService } from '../../../../services/management/group.service';
import { ChannelService } from '../../../../services/management/channel.service';
import { UnitService } from '../../../../services/management/unit.service';
import { ToastrService } from 'ngx-toastr';
import { TranslateService } from '@ngx-translate/core';
import { BsModalRef, BsModalService } from 'ngx-bootstrap';
import { DownloadService } from '../../../../services/management/download.service';
import { Channel } from '../../../../models/Channel.model';
import { apparam } from '../../../../services/management/apparram.service';
import { apParam } from '../../../../models/apParam.model';
import { ServiceChannel } from '../../../../models/ServiceChannel.model';
import { ServiceChannelService } from '../../../../services/serviceChannel.service';
import { warningservice } from '../../../../models/warningservice';
import { warningconfig } from '../../../../services/management/warning.service';
import { ServiceWarningModel } from '../../../../models/ServiceWarning.model';
import { ServiceService } from '../../../../services/management/service.service';

@Component({
  selector: 'app-warning-create',
  templateUrl: './warning-create.component.html',
  styleUrls: ['./warning-create.component.scss'],
  providers: [
    MatDatepickerModule,
    DatePipe,

    { provide: DateAdapter, useClass: MomentDateAdapter, deps: [MAT_DATE_LOCALE] },
    { provide: TreeviewI18n, useClass: UnitTreeviewI18n },
    { provide: MAT_DATE_FORMATS, useValue: MY_FORMATS }
  ],
  encapsulation: ViewEncapsulation.None
})
export class WarningCreateComponent implements OnInit {

  @ViewChild("select") namField: ElementRef;
  newTargetWarningForm: FormGroup;

  service: any;
  marrChannels: Channel[] = [];
  app: apParam[] = [];
  mobjModalRef: BsModalRef;
  validChannel: '';
  listServiceChannel: ServiceChannel[] = [];
  checkValidChannel: boolean;
  serviceWarning: ServiceWarningModel;
  conflictCode: boolean;
  mstrServiceCode = '';
  currentPage = 1;
  created = false;
  vblnCheckChannel = null;

  constructor(
    private serviceService: ServiceService,
    private groupsService: GroupsService,
    private channelService: ChannelService,
    private unitService: UnitService,
    private fb: FormBuilder,
    private datePipe: DatePipe,
    private toastr: ToastrService,
    private translate: TranslateService,
    private dialogRef: MatDialogRef<WarningCreateComponent>,
    private modalService: BsModalService,
    private downLoad: DownloadService,
    private appParam: apparam,
    private serviceChannelService: ServiceChannelService,
    private warning: warningconfig
  ) {
    dialogRef.disableClose = true;
  }

  ngOnInit() {

    this.validChannel = null;
    this.getChannels();
    this.getAllApp();
    this.createWarningForm();
    this.serviceService.service$.subscribe(data => {

      this.service = data;
    });
    this.namField.nativeElement.focus();
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
    this.appParam.getApparam('WARNING_LEVEL').subscribe(vobjNext => {
      this.app = vobjNext.data;
    });
  }

  checkChannels() {
    if (this.newTargetWarningForm.get('channelCode').invalid) {
      this.vblnCheckChannel = true;
    }else{
      this.vblnCheckChannel = false;
    }
  }

  createWarningForm() {
    this.vblnCheckChannel = null;
    this.newTargetWarningForm = this.fb.group(
      {
        serviceName: [''],
        wId: [''],
        exp: [''],
        channelCode: [null, Validators.required],
        appParamCode: [null, Validators.required],
        status: [1, [Validators.required]],
        fromvalue: ['', [Validators.required, Validators.pattern('^\\s{0,10}?([0-9]\\.?[0-9]*)\\s{0,10}$')]],
        tovalue: ['', [Validators.required, Validators.pattern('^\\s{0,10}?([0-9]\\.?[0-9]*)\\s{0,10}$')]],
      },
      {}
    );
  }

  loadForm() {
    this.newTargetWarningForm = this.fb.group(
      {
        serviceName: [this.service.name],
        wId: [''],
        exp: [''],
        channelCode: [null, Validators.required],
        appParamCode: [null, Validators.required],
        status: [1, [Validators.required]],
        fromvalue: ['', [Validators.required, Validators.pattern('^\\s{0,10}?([0-9]\\.?[0-9]*)\\s{0,10}$')]],
        tovalue: ['', [Validators.required, Validators.pattern('^\\s{0,10}?([0-9]\\.?[0-9]*)\\s{0,10}$')]],
      }
    );
  }

  close() {
    this.dialogRef.close();

  }

  hasError(controlName: string, errorName: string) {
    return this.newTargetWarningForm.controls[controlName].hasError(errorName);
  }

  confirmCreate(pobjTemplate: TemplateRef<any>) {

    this.mobjModalRef = this.modalService.show(pobjTemplate, {
      ignoreBackdropClick: true
    }
    );
  }

  onBackConfirm() {
    this.mobjModalRef.hide();
  }

  checkChannel() {
    if (this.service && this.service.id != null) {
      this.serviceChannelService
        .getServiceChannelByServiceId(this.service.id)
        .subscribe((data: any) => {
          for (const serviceChannel of data.data) {
            console.log('serviceChannel', serviceChannel);
            console.log('this.validChannel', this.validChannel);
            if (serviceChannel.vdsChannelCode == this.validChannel) {
              this.checkValidChannel = true;
              break;
            }
          }
        });
    }
  }

  save() {
    this.mobjModalRef.hide();
    if (!this.checkValidChannel) {
      this.showError(this.translate.instant('management.service.error.invalidChannel'));
      return;
    }
    if (!this.service && this.service.id == null) {
      this.showError(this.translate.instant('management.service.error.invalidChannel'));
      return;
    }
    this.serviceWarning = new ServiceWarningModel();
    this.serviceWarning.serviceId = this.service.id;
    this.serviceWarning.serviceName = this.service.name;
    this.serviceWarning.vdsChannelCode = this.newTargetWarningForm.get('channelCode').value;
    this.serviceWarning.waningLevel = this.newTargetWarningForm.get('appParamCode').value;
    this.serviceWarning.status = this.newTargetWarningForm.get('status').value;
    this.serviceWarning.fromValue = this.newTargetWarningForm.get('fromvalue').value;
    this.serviceWarning.toValue = this.newTargetWarningForm.get('tovalue').value;
    const checkExp = this.newTargetWarningForm.get('exp').value.trim();
    if (checkExp == '') {
      this.serviceWarning.exp = null;
    } else {
      this.serviceWarning.exp = checkExp;
    }
    if (this.newTargetWarningForm.get('fromvalue').value < this.newTargetWarningForm.get('tovalue').value &&
      !(this.newTargetWarningForm.get('tovalue').value > 1)) {
      this.serviceService.createServiceWarning(this.serviceWarning).subscribe(data => {
        if (data.code === 500) {
          if (data.errors == 'DuplicateFromAndTovalue') {
            this.showError(this.translate.instant('management.warningconfig.duplicatevalue'));
          } else {
            this.showError(this.translate.instant('management.warningconfig.duplicate'));
          }
          this.mobjModalRef.hide();

        } else {
          this.created = true;
          this.showSuccess(this.translate.instant('management.group.message.insertSuccess'));
          this.warning.getAllWarningById(this.serviceWarning.serviceId).subscribe((res: any) => {
            this.warning.setServiceWarning(res.data);
            this.warning.setReloadWarning(this.created);
          });
          this.getChannels();
          this.loadForm();
        }
        this.mobjModalRef.hide();
      });
    } else {
      this.mobjModalRef.hide();
      this.showError(this.translate.instant('management.warningconfig.fromandto'));
    }
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

  checkconFlick() {
    this.groupsService.getAllGroupService().subscribe((res: any) => {
      for (let i = 0; i < res.data.length; i++) {
        if (this.mstrServiceCode === res.data[i].code) {
          this.conflictCode = true;
          break;
        } else {
          this.conflictCode = false;
        }
      }
    });
  }
}
