import { ToastrService } from 'ngx-toastr';
import { DatePipe } from '@angular/common';
import { WarningReceiveService } from './../../../../services/management/warning-receive.service';
import { Component, OnInit } from '@angular/core';
import { TreeviewI18n } from 'ngx-treeview';

import { UnitTreeviewI18n } from '../../vtt-target/unit-treeview-i18n';
import { FormControl, Validators, FormBuilder, FormGroup } from '@angular/forms';
import * as fileSaver from 'file-saver';
import { DownloadService } from '../../../../services/management/download.service';
import { TranslateService } from '@ngx-translate/core';
import {MomentDateAdapter, MAT_MOMENT_DATE_ADAPTER_OPTIONS} from '@angular/material-moment-adapter';
import {DateAdapter, MAT_DATE_FORMATS, MAT_DATE_LOCALE} from '@angular/material/core';
import {MatDatepicker} from '@angular/material/datepicker';

import {config} from '../../../../config/application.config';

import * as _moment from 'moment';
import { ReportFilterSqlDTO } from '../../../../models/reportFilterSqlDto.model';

export const MY_FORMATS = {
  parse: {
    dateInput: 'DD/MM/YYYY',
  },
  display: {
    dateInput: 'DD/MM/YYYY',
    monthYearLabel: 'DD MMM YYYY',
    dateA11yLabel: 'LL',
    monthYearA11yLabel: 'DD MMMM YYYY',
  },
};
@Component({
  selector: 'rp-unit-sum-province',
  templateUrl: './rp-unit-sum-province.component.html',
  styleUrls: ['./rp-unit-sum-province.component.scss'],
  providers: [
    DatePipe,
    { provide: DateAdapter, useClass: MomentDateAdapter, deps: [MAT_DATE_LOCALE, MAT_MOMENT_DATE_ADAPTER_OPTIONS] },
    { provide: MAT_DATE_FORMATS, useValue: MY_FORMATS },
    { provide: TreeviewI18n, useClass: UnitTreeviewI18n }
  ],
})
export class RpUnitSumProvinceComponent implements OnInit {
  rpMonth = new FormControl(_moment());

  isDisabled = false; // <- create variable to determine state
  fileDownload() {
    this.isDisabled = true; // <- toggle the state
    if(localStorage && localStorage.currentUser){
      console.log(localStorage.currentUser);
      const dto = new ReportFilterSqlDTO(
        this.datePipe.transform(new Date(this.rpMonth.value), config.DATE_FORMAT),
        (JSON.parse(localStorage.currentUser)).username,
        config.REPORT_UNIT_SUM_PROVINCE
      );
      this.downloadService.downloadTemplateReport(dto)
        .subscribe(res => {
          this.saveFile(res.body, dto.type + "_NGAY_" + dto.month);
        });
    }
  }

  saveFile(pobjData: any, pstrFilename?: string) {
    const blob = new Blob([pobjData], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
    fileSaver.saveAs(blob, pstrFilename);
    this.isDisabled = false; // <- toggle the state
  }

  isValidForm() {
    return (this.rpMonth !== null);
  }

  showError(message: string) {
    this.toastr.error(message,
      this.translate.instant('management.service.message.fail'), {
      timeOut: 2000,
      positionClass: 'toast-top-center',
    });
  }

  chosenYearHandler(normalizedYear: _moment.Moment) {
    const ctrlValue = this.rpMonth.value;
    ctrlValue.year(normalizedYear.year());
    this.rpMonth.setValue(ctrlValue);
  }

  chosenMonthHandler(normalizedMonth: _moment.Moment, datepicker: MatDatepicker<_moment.Moment>) {
    const ctrlValue = this.rpMonth.value;
    ctrlValue.month(normalizedMonth.month());
    this.rpMonth.setValue(ctrlValue);
    datepicker.close();
  }

  constructor(
    private downloadService: DownloadService,
    private warningReceiveService: WarningReceiveService,
    private datePipe: DatePipe,
    private toastr: ToastrService,
    private fb: FormBuilder,
    private translate: TranslateService) { }

  ngOnInit() {
  }

  noMethod() {
  }

  onChange(value: any) {
    
  }

  
}
