import { SqlModelDTO } from './../../../models/sqldto.model';
import { ToastrService } from 'ngx-toastr';
import { DatePipe } from '@angular/common';
import { DashboardModel } from './../../../models/dashboard.model';
import { Partner } from './../../../models/Partner';
import { WarningReceiveService } from './../../../services/management/warning-receive.service';
import { Component, OnInit } from '@angular/core';
import { TreeviewConfig, TreeviewItem, TreeItem, TreeviewI18n } from 'ngx-treeview';
import { DateAdapter, MAT_DATE_LOCALE, MAT_DATE_FORMATS, MatDatepicker } from '@angular/material';
import { MomentDateAdapter } from '@angular/material-moment-adapter';
import { UnitTreeviewI18n } from '../vtt-target/unit-treeview-i18n';
import { FormControl, Validators, FormBuilder, FormGroup } from '@angular/forms';
import * as _moment from 'moment';
import * as fileSaver from 'file-saver';
import { SqlQueryModel } from '../../../models/SqlQuery.model';
import { SqlQueryService } from '../../../services/management/sql.service';
import { UnitModel } from '../../../models/unit.model';
import { UnitService } from '../../../services/management/unit.service';
import { DownloadService } from '../../../services/management/download.service';
import { TranslateService } from '@ngx-translate/core';

// format date
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
  selector: 'app-details-report',
  templateUrl: './details-report.component.html',
  styleUrls: ['./details-report.component.scss'],
  providers: [
    DatePipe,
    { provide: DateAdapter, useClass: MomentDateAdapter, deps: [MAT_DATE_LOCALE] },
    { provide: MAT_DATE_FORMATS, useValue: MY_FORMATS },
    { provide: TreeviewI18n, useClass: UnitTreeviewI18n }
  ],
})
export class DetailsReportComponent implements OnInit {

  mdtDateNow = new Date(new Date().setDate(new Date().getDate()));
  mdtMinDate = new Date(1900, 0, 1);
  mdtMaxDate = new Date(2100, 0, 1);
  mdtFromDate = new FormControl(this.mdtDateNow);
  mdtToDate = new FormControl(this.mdtDateNow);

  nodeTreeView: TreeviewItem;
  nodeTreeViews: TreeviewItem[] = [];
  TreeShopCode: TreeviewItem[] = [];
  dataTree = [];
  dataUnit = [];
  nodeTrees: UnitModel[] = [];
  nodeItem: TreeItem;
  nodeItems: TreeItem[] = [];
  unitIitem: UnitModel;
  marrIndexNodeService = [];
  marrParther: Partner[] = [];
  dashModel: DashboardModel;
  mobjNodeItemService: TreeItem;
  cycleId;
  groupId;
  shopCode;
  date = new FormControl(_moment());
  marrSqlNew: TreeviewItem[] = [];

  marrUnits: UnitModel[] = [];
  marrSqlQuerys: SqlQueryModel[] = [];
  index = 1;
  vblnCheckService: boolean;

  mnbrSqlQuery: number = null;

  mstrShopCode: string;
  mstrSqlCode: string;
  mnmbShopId: number;
  marrShops: string[] = [];
  newSqlForm: FormGroup;

  fromDateValidator: boolean;
  fromDatePattern: boolean;
  fromDateNull: boolean;
  fromDateEndlessToDate: boolean;
  fromDateYear: boolean;

  toDateValidator: boolean;
  toDatePattern: boolean;
  toDateNull: boolean;
  toDateEndlessToDate: boolean;
  toDateYear: boolean;

  sameMonthYearTo: boolean;
  sameMonthYear: boolean;

  value: any;
  marrNodeTreeviewServices: TreeviewItem[] = [];

  newServiceForm: FormGroup;

  mobjConfig = TreeviewConfig.create({
    hasFilter: true,
    hasCollapseExpand: false,
    maxHeight: 140,
  });

  valueSql: any;

  constructor(
    private sqlQueryService: SqlQueryService,
    private unitService: UnitService,
    private downloadService: DownloadService,
    private warningReceiveService: WarningReceiveService,
    private datePipe: DatePipe,
    private toastr: ToastrService,
    private fb: FormBuilder,
    private translate: TranslateService) { }

  ngOnInit() {
    this.getChannels();
    // this.getSqlQuerys();
    this.getUnits();
    this.unitService.getUnitsReport().subscribe((res: any) => {
      this.marrParther = res.data;
      this.createTreeView();
    });

    this.createSqlForm();
  }

  /**
* Tạo form reactive sql
*
* @author: Manhtd
* @version: 1.0
* @since: 11/2019
*/
  createSqlForm() {
    this.newSqlForm = this.fb.group(
      {
        name: ['', [Validators.required]],
        sql: [null, [Validators.required]],
        fromDate: [this.mdtMinDate],
        toDate: [this.mdtMinDate],
      },
    );
  }

  hasError(controlName: string, errorName: string) {
    return this.newSqlForm.controls[controlName].hasError(errorName);
  }


  /**
   * Không Xử lý gì
   *
   * @author: QuangND
   * @version: 1.0
   * @since: 2019/10/17
   */
  noMethod() {
  }

  /**
   * Lấy danh sách đơn vị qua api
   *
   * @author: QuangND
   * @version: 1.0
   * @since: 2019/10/17
   */
  getUnits() {
    this.unitService.getUnitsReport().subscribe(
      units => {
        this.marrUnits = units['data'];
      }
    );
  }

  onChange(value: any) {
    this.mstrShopCode = value;
  }

  onChangeSQL(value: any) {
    this.mnbrSqlQuery = value;
    this.checkService();
  }

  /**
   * Gửi dữ liệu từ form cho api xử lý và download file xls về máy
   *
   * @author: QuangND
   * @version: 1.0
   * @since: 2019/10/17
   */
  fileDownload() {

    const toDate = this.datePipe.transform(new Date(this.mdtToDate.value), 'yyyy-MM-dd');
    const fromDate = this.datePipe.transform(new Date(this.mdtFromDate.value), 'yyyy-MM-dd');
    const toDay = this.datePipe.transform(new Date(), 'yyyy-MM-dd');

    if (this.fromDateValidator || this.fromDatePattern ||
      this.fromDateNull || this.fromDateEndlessToDate || this.sameMonthYear
      || this.toDateValidator || this.toDatePattern || this.toDateNull || this.sameMonthYearTo || this.toDateYear || this.fromDateYear) {

      // this.showError(this.translate.instant('management.error.errorDate'));
      return;
    }

    if (this.vblnCheckService != false) {
      this.vblnCheckService = true;
      return;
    }

    if (this.mdtToDate.value != null && this.mdtFromDate.value != null) {
      if (toDate < fromDate) {
        return;
      }

      if (toDate > toDay) {
        return;
      }
    }

    const staffModelDTO = new SqlModelDTO();
    staffModelDTO.shopCode = this.mstrShopCode ? this.mstrShopCode : null;
    staffModelDTO.idReport = this.mnbrSqlQuery ? this.mnbrSqlQuery : null;
    staffModelDTO.fromDate = this.mdtFromDate.value ? this.datePipe.transform(new Date(this.mdtFromDate.value), 'yyyyMMdd') : null;
    staffModelDTO.toDate = this.mdtToDate.value ? this.datePipe.transform(new Date(this.mdtToDate.value), 'yyyyMMdd') : null;

    let stringNameSQL = '';
    this.marrSqlQuerys.forEach(data => {
      console.log(data);
        if (staffModelDTO.idReport == data.id) {
          stringNameSQL = data.name;
        }
    });

    this.downloadService.downloadReport(staffModelDTO)
      .subscribe(vobjResponse => {
        let vstrFilename: string;
        vstrFilename = this.translate.instant('management.report.file.name') + '_' + stringNameSQL;
        this.saveFile(vobjResponse.body, vstrFilename);
      });
  }

  /**
   * Lưu file download theo tên được truyền vào
   *
   * @author: QuangND
   * @version: 1.0
   * @since: 2019/10/17
   * @param: pobjData - dữ liệu file download
   * @param: pstrFilename - tên file download
   */
  saveFile(pobjData: any, pstrFilename?: string) {
    const blob = new Blob([pobjData], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
    fileSaver.saveAs(blob, pstrFilename);
  }

  /**
   * Lấy danh sách sql query từ api
   *
   * @author: QuangND
   * @version: 1.0
   * @since: 2019/10/17
   */
  getSqlQuerys() {
    this.sqlQueryService.getSqlQuerys().subscribe(
      vobjNext => (this.marrSqlQuerys = vobjNext),
      vobjError => (console.error('không có sql query'))
    );
  }

  getChannels() {
    this.sqlQueryService.getSqlQuerys().subscribe(
      vobjNext => {
        this.marrSqlQuerys = vobjNext;
        if (vobjNext) {
          vobjNext.forEach(
            valueUnit => {
              const text = valueUnit['name'];
              const value = valueUnit['id'];
              this.marrSqlNew.push(new TreeviewItem({ text, value, checked: false }));
            }
          );
        }
      },
      error => (this.marrSqlNew = [])
    );
  }

  /**
   * Kiểm tra
   *
   * @author: QuangND
   * @version: 1.0
   * @since: 2019/10/17
   */
  isValidForm() {
    return (this.mstrShopCode !== null && this.mdtFromDate.valid && this.mdtFromDate !== null &&
      this.mdtToDate !== null && this.mdtToDate.valid && this.mnbrSqlQuery !== null);
  }

  checkFromDate(event) {

    this.fromDateValidator = false;
    this.fromDatePattern = false;
    this.fromDateNull = false;
    this.fromDateEndlessToDate = false;
    this.sameMonthYear = false;
    this.fromDateYear = false;
    this.sameMonthYearTo = false;

    if (event.targetElement.value.toString().trim().replace(/ /g, '') !== '') {
      const toDateYear = this.datePipe.transform(new Date(event.value), 'yyyy');
      if (Number(toDateYear) >= 2100 || Number(toDateYear) <= 1900) {
        this.fromDateYear = true;
        return;
      }
      if (event.value == null) {
        this.fromDatePattern = true;
        return;
      } else {
        const toDay = this.datePipe.transform(new Date(), 'yyyy-MM-dd');
        const dateChange = this.datePipe.transform(new Date(event.value), 'yyyy-MM-dd');
        if (dateChange > toDay) {
          this.fromDateValidator = true;
          return;
        } else if (this.mdtToDate.value != null && this.mdtToDate.value != '') {
          const toDate = this.datePipe.transform(new Date(this.mdtToDate.value), 'yyyy-MM-dd');
          const monthYearFromDate = (this.datePipe.transform(new Date(this.mdtFromDate.value), 'MMyyyy'));
          const monthYearToDate = (this.datePipe.transform(new Date(this.mdtToDate.value), 'MMyyyy'));

          const yearToDate = (this.datePipe.transform(new Date(this.mdtToDate.value), 'yyyy'));

          // tslint:disable-next-line: radix
          if (parseInt(yearToDate) < 1900 || parseInt(yearToDate) > 2100) {
            // ngafy thangs
          } else if (monthYearFromDate !== monthYearToDate) {
            this.sameMonthYear = true;
            return;
          } else if (dateChange > toDate) {
            this.fromDateEndlessToDate = true;
            return;
          }
        } else if (this.mdtToDate.value == null || this.mdtToDate.value == '') {
          this.fromDateValidator = false;
          this.fromDatePattern = false;
          this.fromDateNull = false;
          this.fromDateEndlessToDate = false;
          this.sameMonthYear = false;
          return;
        }
      }
    } else {
      this.fromDateNull = true;
      return;
    }
  }

  checkToDate(event) {

    this.toDatePattern = false;
    this.toDateValidator = false;
    this.toDateNull = false;
    this.fromDateEndlessToDate = false;
    this.sameMonthYearTo = false;
    this.toDateYear = false;
    this.sameMonthYear = false;

    if (event.targetElement.value.toString().trim().replace(/ /g, '') !== '') {
      const toDateYear = this.datePipe.transform(new Date(event.value), 'yyyy');
      if (Number(toDateYear) >= 2100 || Number(toDateYear) <= 1900) {
        this.toDateYear = true;
        return;
      }
      if (event.value == null) {
        this.toDatePattern = true;
        return;
      } else {
        const toDay = this.datePipe.transform(new Date(), 'yyyy-MM-dd');
        const dateChange = this.datePipe.transform(new Date(event.value), 'yyyy-MM-dd');
        if (dateChange > toDay) {
          this.toDateValidator = true;
          return;
        } else if (this.mdtFromDate.value != null && this.mdtFromDate.value != '') {
          const fromDate = this.datePipe.transform(new Date(this.mdtFromDate.value), 'yyyy-MM-dd');
          const monthYearFromDate = (this.datePipe.transform(new Date(this.mdtFromDate.value), 'MMyyyy'));
          const monthYearToDate = (this.datePipe.transform(new Date(this.mdtToDate.value), 'MMyyyy'));
          if (monthYearFromDate != monthYearToDate) {
            this.sameMonthYearTo = true;
            return;
          } else if (dateChange < fromDate) {
            this.fromDateEndlessToDate = true;
            return;
          }
        } else if (this.mdtFromDate.value == null || this.mdtFromDate.value == '') {
          this.toDateValidator = false;
          this.toDatePattern = false;
          this.toDateNull = false;
          this.fromDateEndlessToDate = false;
          this.sameMonthYear = false;
          return;
        }
      }
    } else {
      this.toDateNull = true;
      return;
    }
  }



  showError(message: string) {
    this.toastr.error(message,
      this.translate.instant('management.service.message.fail'), {
      timeOut: 2000,
      positionClass: 'toast-top-center',
    });
  }


  createTreeView() {
    try {
      this.nodeTreeViews = [];
      this.groupId = null;
      this.nodeTreeViews = [];
      this.dataUnit = this.marrParther;
      this.dataTree = this.marrParther;
      this.nodeTrees = this.createNode(this.nodeTrees, this.dataTree, this.unitIitem);
      this.dataTree = this.nodeTrees;
      this.createTree(this.nodeTrees, this.dataTree);
      this.nodeTrees.forEach(valuess => {
        this.nodeTreeView = new TreeviewItem(this.forwardData(valuess, this.nodeItem, this.nodeItems));
        this.nodeTreeViews.push(this.nodeTreeView);
      });
      for (let i = 0; i < this.nodeTreeViews.length; i++) {
        if (this.nodeTreeViews[i].children) {
          this.value = this.nodeTreeViews[i].value;
          this.dataUnit.forEach(
            tree => {
              if (tree.shopCode === this.nodeTreeViews[i].value) {
                this.shopCode = tree.shopCode;
                this.dashModel = new DashboardModel(this.groupId, null, this.date.value._d.getTime(), this.cycleId, this.shopCode);
                return;
              }
            }
          );
          break;
        }
      }
      this.value = this.nodeTreeViews[0].value;
      this.mstrShopCode = this.value;
    } catch (e) {
      console.error('error');
    }

  }


  forwardData(pobjNodeTree: UnitModel, pobjitem: TreeItem, parrItems: TreeItem[] = []) {
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
        value: pobjNodeTree.shopCode,
        text: pobjNodeTree.shopName,
        children: parrItems
      };
    } else {
      parrItems = [];
      pobjitem = {
        value: pobjNodeTree.shopCode,
        text: pobjNodeTree.shopName,
        children: null
      };
    }
    return pobjitem;
  }

  createNode(parrItems: UnitModel[] = [], parrDataTree: UnitModel[], pobjItem: UnitModel) {
    pobjItem = null;
    parrItems = parrDataTree.map(value => {
      pobjItem = {
        id: value.id,
        shopName: value.shopName,
        parentShopCode: value.parentShopCode,
        shopCode: value.shopCode,
        children: [],
        groupId: value.groupId,
        vdsChannelCode: null
      };
      return pobjItem;
    });
    return parrItems;
  }

  createTree(parrNodeTrees: UnitModel[], parrDataTree: UnitModel[]) {
    const len = parrNodeTrees.length;
    for (let i = 0; i < len; i++) {
      for (let j = 0; j < len; j++) {
        if (parrNodeTrees[i].shopCode === parrDataTree[j].parentShopCode) {
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

  checkService() {
    if (this.mnbrSqlQuery == null) {
      this.vblnCheckService = true;
    } else {
      this.vblnCheckService = false;
    }
  }


}
