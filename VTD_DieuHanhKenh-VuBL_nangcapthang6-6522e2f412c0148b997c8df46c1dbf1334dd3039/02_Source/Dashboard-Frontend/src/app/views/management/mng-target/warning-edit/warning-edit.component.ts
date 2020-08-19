import {Component, ElementRef, Inject, OnInit, TemplateRef, ViewChild, ViewEncapsulation} from '@angular/core';
import {
  DateAdapter,
  MAT_DATE_FORMATS,
  MAT_DATE_LOCALE,
  MAT_DIALOG_DATA,
  MatDatepickerModule,
  MatDialogRef
} from '@angular/material';
import { DatePipe } from '@angular/common';
import { MomentDateAdapter } from '@angular/material-moment-adapter';
import { TreeviewI18n, TreeviewItem } from 'ngx-treeview';
import { UnitTreeviewI18n } from '../config-tree-select/unit-treeview-i18n';
import { MY_FORMATS } from '../target-create/target-create.component';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ServiceService } from '../../../../services/management/service.service';
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

@Component({
  selector: 'app-warning-create',
  templateUrl: './warning-edit.component.html',
  styleUrls: ['./warning-edit.component.scss'],
  providers: [
    MatDatepickerModule,
    DatePipe,

    { provide: DateAdapter, useClass: MomentDateAdapter, deps: [MAT_DATE_LOCALE] },
    { provide: TreeviewI18n, useClass: UnitTreeviewI18n },
    { provide: MAT_DATE_FORMATS, useValue: MY_FORMATS }
  ],
  encapsulation: ViewEncapsulation.None
})
export class WarningEditComponent implements OnInit {
  @ViewChild('select') namField: ElementRef;
  editTargetWarningForm: FormGroup;
  service: any;
  marrChannels: Channel[] = [];
  app: apParam[] = [];
  mobjModalRef: BsModalRef;
  validChannel: '';
  listServiceChannel: ServiceChannel[] = [];
  checkValidChannel: boolean;
  serviceWarning: ServiceWarningModel;
  constructor(
    public dialogRef: MatDialogRef<WarningEditComponent>,

    @Inject(MAT_DIALOG_DATA) public data,
    private serviceService: ServiceService,
    private groupsService: GroupsService,
    private channelService: ChannelService,
    private unitService: UnitService,
    private fb: FormBuilder,
    private datePipe: DatePipe,
    private toastr: ToastrService,
    private translate: TranslateService,
    private modalService: BsModalService,
    private downLoad: DownloadService,
    private appParam: apparam,
    private serviceChannelService: ServiceChannelService,
    private warning: warningconfig
  ) {
    dialogRef.disableClose = true;
  }
  ngOnInit() {
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

  createWarningForm() {
    this.editTargetWarningForm = this.fb.group(
      {
        serviceName: [''],
        wId: [this.data.serviceWarning.wcID],
        exp: [this.data.serviceWarning.wexp],
        channelCode: [this.data.serviceWarning.vdscCode, Validators.required],
        appParamCode: [this.data.serviceWarning.wlevel, Validators.required],
        status: [this.data.serviceWarning.wStatus, [Validators.required]],
        fromvalue: [this.data.serviceWarning.wfvalue, [Validators.required, Validators.pattern('^\\s{0,10}?([0-9]\\.?[0-9]*)\\s{0,10}$')]],
        tovalue: [this.data.serviceWarning.wovalue, [Validators.required, Validators.pattern('^\\s{0,10}?([0-9]\\.?[0-9]*)\\s{0,10}$')]],
      },
      {
      }
    );
  }

  close() {
    this.dialogRef.close();
  }

  hasError(controlName: string, errorName: string) {
    return this.editTargetWarningForm.controls[controlName].hasError(errorName);
  }
  confirmCreate(pobjTemplate: TemplateRef<any>) {
    this.mobjModalRef = this.modalService.show(pobjTemplate, {
      ignoreBackdropClick: true
    }
    );
    this.checkChannel();
  }

  onBackConfirm() {
    this.mobjModalRef.hide();
  }

  checkChannel() {
    this.validChannel = this.editTargetWarningForm.get('channelCode').value;
    if (this.service && this.service.id != null) {
      this.serviceChannelService
        .getServiceChannelByServiceId(this.service.id)
        .subscribe((data: any) => {
          for (const serviceChannel of data.data) {
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
    this.serviceWarning.vdsChannelCode = this.editTargetWarningForm.get('channelCode').value;
    this.serviceWarning.waningLevel = this.editTargetWarningForm.get('appParamCode').value;
    this.serviceWarning.status = this.editTargetWarningForm.get('status').value;
    this.serviceWarning.fromValue = this.editTargetWarningForm.get('fromvalue').value;
    this.serviceWarning.toValue = this.editTargetWarningForm.get('tovalue').value;
    const checkExp = this.editTargetWarningForm.get('exp').value.trim();
    if (checkExp == '') {
      this.serviceWarning.exp = null;
    } else {
      this.serviceWarning.exp = checkExp;
    }
    this.serviceWarning.id = this.editTargetWarningForm.get('wId').value;
    if (this.editTargetWarningForm.get('fromvalue').value < this.editTargetWarningForm.get('tovalue').value && !(this.editTargetWarningForm.get('tovalue').value > 1)) {
      this.serviceService.updateServiceWarning(this.serviceWarning).subscribe(data => {
        if (data.code === 500) {
          if (data.errors == 'DuplicateFromAndTovalue') {
            //
            this.showError(this.translate.instant('management.warningconfig.duplicatevalue'));
          } else {
            this.showError(this.translate.instant('management.warningconfig.duplicate'));
          }
          this.mobjModalRef.hide();
        }
        if (data.code == 200) {
          this.showSuccess(this.translate.instant('management.group.message.updateSuccess'));
          this.warning.getAllWarningById(this.serviceWarning.serviceId).subscribe((res: any) => {
            this.warning.setServiceWarning(res.data);
            let edit = false;
            this.warning.setReloadWarning(edit);
            this.mobjModalRef.hide();
            this.dialogRef.close();
          });
        }

        this.getChannels();
        //
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
}
