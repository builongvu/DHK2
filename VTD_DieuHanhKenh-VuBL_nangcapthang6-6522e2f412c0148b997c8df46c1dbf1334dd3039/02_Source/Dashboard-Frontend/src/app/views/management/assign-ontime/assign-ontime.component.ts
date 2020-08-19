import { Component, OnInit, TemplateRef } from '@angular/core';
import { AssignOntimeService } from '../../../services/management/assign-ontime.service';
import { TranslateService } from '@ngx-translate/core';
import { ToastrService } from 'ngx-toastr';
import { BsModalRef, BsModalService } from 'ngx-bootstrap';
import { FormBuilder, Validators } from '@angular/forms';
import { apParam } from '../../../models/apParam.model';

@Component({
  selector: 'app-assign-ontime',
  templateUrl: './assign-ontime.component.html',
  styleUrls: ['./assign-ontime.component.scss']
})
export class AssignOntimeComponent implements OnInit {
  mobjBefore: apParam = new apParam();
  mobjAfter: apParam = new apParam();
  marr: apParam[] = [];
  status = '-1';
  statusAfter = '-1';
  form;
  mobjModalRef: BsModalRef;

  constructor(
    private assignontimeservice: AssignOntimeService,
    private toastr: ToastrService,
    private fb: FormBuilder,
    private modalService: BsModalService,
    private translate: TranslateService
  ) {
  }

  ngOnInit() {
    this.LoadData();
  }


  /**
   * Tải dữ liệu từ api và truyền vào input
   * Nếu chưa có thời hạn, sẽ thực hiện thêm mới thời hạn giao
   *
   * @author: CuongDT
   * @version: 1.0
   * @since: 2019/11/18
   */
  LoadData() {
    this.assignontimeservice.getOntime().subscribe(res => {
      if (res.errors === 'content is not found') {
        this.mobjBefore.value = '';
        this.mobjAfter.value = '';
        this.mobjBefore.id = -1;
        this.mobjBefore.code = 'BEFORE';
        this.mobjBefore.type = 'ASSIGN_ONTIME';
        this.mobjAfter.id = -1;
        this.mobjAfter.code = 'AFTER';
        this.mobjAfter.type = 'ASSIGN_ONTIME';
        this.mobjBefore.name = 'Số ngày trước kỳ';
        this.mobjAfter.name = 'Số ngày sau ngày đầu kỳ';
        this.mobjAfter.description = 'Khai báo khoảng thời gian giao KH';
        this.mobjBefore.description = 'Khai báo khoảng thời gian giao KH';
        this.mobjAfter.status = '1';
        this.mobjBefore.status = '1';
      } else {
        this.marr = res.data;
        if (this.marr.filter(item => item.code === 'BEFORE')[0]) {
          this.mobjBefore = this.marr.filter(item => item.code === 'BEFORE')[0];
        } else {
          this.mobjBefore.id = -1;
          this.mobjBefore.code = 'BEFORE';
          this.mobjBefore.type = 'ASSIGN_ONTIME';
          this.mobjBefore.name = 'Số ngày trước kỳ';
          this.mobjBefore.description = 'Khai báo khoảng thời gian giao KH';
          this.mobjBefore.status = '1';
        }
        if (this.marr.filter(item => item.code === 'AFTER')[0]) {
          this.mobjAfter = this.marr.filter(item => item.code === 'AFTER')[0];
        } else {
          this.mobjAfter.id = -1;
          this.mobjAfter.code = 'AFTER';
          this.mobjAfter.type = 'ASSIGN_ONTIME';
          this.mobjAfter.status = '1';
          this.mobjAfter.name = 'Số ngày sau ngày đầu kỳ';
          this.mobjAfter.description = 'Khai báo khoảng thời gian giao KH';
        }
        this.check();
        this.checkAfter();
      };
      this.create();
    });
  }

  hasError(controlName: string, errorName: string) {
    return this.form.controls[controlName].hasError(errorName);
  }
  create() {
    this.form = this.fb.group(
      {
        before: [this.mobjBefore.value, [Validators.required, Validators.pattern('^[0-9]{0,2}$'), Validators.max(30)]],
        after: [this.mobjAfter.value, [Validators.required, Validators.pattern('^[0-9]{0,2}$'), Validators.max(30)]]
      });
  }
  /**
   * Lưu thay đổi/ Thêm mới thời hạn giao chỉ tiêu
   *
   * @author: CuongDT
   * @version: 1.0
   * @since: 2019/11/18
   */
  save() {
    if (this.mobjBefore.value === '') {
      return this.status = '1';
    } else if (this.mobjAfter.value === '') {
      return this.statusAfter = '1';
    }
    this.mobjAfter.status = '1';
    this.mobjBefore.status = '1';
    this.assignontimeservice.saveOntime(this.mobjAfter).subscribe(res => {
      this.assignontimeservice.saveOntime(this.mobjBefore).subscribe(res => {
        this.showSuccess(this.translate.instant('management.warningconfig.updatesuccess'));
        this.create();
      });
    });
    if (this.mobjModalRef) {
      this.mobjModalRef.hide();
    }
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

  check() {
    if (this.mobjBefore.value === '') {
      this.status = '2';
    } else if (this.mobjBefore.value.indexOf(' ') !== -1 || this.mobjBefore.value === '-') {
      this.status = '2';
    } else if (isNaN(Number(this.mobjBefore.value))) {
      this.status = '2';
    } else {
      this.mobjBefore.value = (Number(this.mobjBefore.value) + 0) + '';
      if (Number(this.mobjBefore.value) > 30 && Number(this.mobjBefore.value)<99) {
        this.status = '1';
      } else {
        this.status = '0';
      }
    }
  }

  checkAfter() {
    if (this.mobjAfter.value === '') {
      this.statusAfter = '2';
    } else if (this.mobjAfter.value.indexOf(' ') !== -1 || this.mobjAfter.value === '-') {
      this.statusAfter = '2';
    } else if (isNaN(Number(this.mobjAfter.value))) {
      this.statusAfter = '2';
    } else {
      this.mobjAfter.value = (Number(this.mobjAfter.value) + 0) + '';
      if (Number(this.mobjAfter.value) > 30 && Number(this.mobjAfter.value)<99) {
        this.statusAfter = '1';
      } else {
        this.statusAfter = '0';
      }
    }
  }

  confirm(pobjTemplate: TemplateRef<any>) {
    if (this.mobjModalRef) {
      this.mobjModalRef.hide();
    }
    this.mobjModalRef = this.modalService.show(pobjTemplate, {
      ignoreBackdropClick: true
    }
    );
  }
  closeModal(pobjTemplate?) {
    if (this.mobjModalRef) {
      this.mobjModalRef.hide();
    }
  }
}
