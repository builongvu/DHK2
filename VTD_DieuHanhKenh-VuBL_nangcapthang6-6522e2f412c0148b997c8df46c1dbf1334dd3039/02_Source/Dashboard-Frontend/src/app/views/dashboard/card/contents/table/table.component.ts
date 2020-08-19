import {AfterViewInit, Component, ElementRef, EventEmitter, Input, OnChanges, OnInit, Output, ViewChild} from '@angular/core';
import {Workbook} from 'exceljs';
import * as fs from 'file-saver';
import {el} from '@angular/platform-browser/testing/src/browser_util';
import {DrilldownModel} from '../../../../../models/Drilldown.model';
import {DashboardModel} from '../../../../../models/dashboard.model';
import {DashboardService} from '../../../../../services/management/dashboard.service';
import {HttpClient} from '@angular/common/http';
import {config} from '../../../../../config/application.config';
import {ActivatedRoute} from '@angular/router';
import {TableModel} from '../../../../../models/table.model';
import {ColumnModel} from '../../../../../models/Column.model';
import {RowModel} from '../../../../../models/Row.model';
import {CellModel} from '../../../../../models/Cell.model';

@Component({
  // tslint:disable-next-line:component-selector
  selector: 'table-card',
  templateUrl: 'table.component.html',
  styleUrls: ['table.component.scss']
})

export class TableComponent implements OnInit, AfterViewInit, OnChanges {
  // tslint:disable-next-line:no-input-rename
  @Input('data-table') dataTable: any;
  @Input() isZoomCard: boolean;
  @Input() cardLength: number;
  @Input() drilldownLength: number;
  @Input() isExcelShow: boolean;
  @Input() contentsLength: number;
  @Input() dashboardModel: DashboardModel;
  @ViewChild('tableDashboard') table: ElementRef;

  @Output() clickService = new EventEmitter<DrilldownModel>();

  constructor(private dashboardService: DashboardService,
              private http: HttpClient,
              private route: ActivatedRoute) {
  }

  isClickSort = false;
  typeSort = 'desc';
  groupId = this.route.snapshot.params['groupId'];
  tableModel;
  columns: ColumnModel[];
  dataTableShow;
  totalItems;
  curentPage = 1;

  ngOnInit(): void {
    this.dataTableShow = this.dataTable;
  }
  ngOnChanges(): void {
    // console.log(this.isExcelShow, ' excel');
  }

  DownloadByApi() {
    if (this.typeSort == 'desc') {
      this.dashboardModel.sorted = 'desc';
    } else {
      this.dashboardModel.sorted = 'asc';
    }
    this.dashboardService.getDataTableDownload(this.dashboardModel).subscribe(
      data => {
        // console.log('data request: ', this.dashboardModel);
        // console.log('data table top: ', data['data']);
        this.ExportToExcel(data['data']);
      }
    );
  }

  ExportToExcel(table: any) {
    // console.log(table);
    const Excel = require('exceljs');
    const workbook = new Excel.Workbook();
    const worksheet = workbook.addWorksheet('TONG_HOP');
    //
    const title = table['title'].toUpperCase();
    // const title = 'ddddd';
    // worksheet.addRow(title);
    const columns: any[] = [];
    // tslint:disable-next-line:no-unused-expression
    table['columns'].forEach(
      column => {
        if (column['type'] !== 'CHART' && column['type'] !== 'BAR') {
          columns.push(column);
        }
      }
    );

    const header: any[] = ['STT'];
    const subHeader: any[] = [''];
    columns.forEach(
      (column, index) => {
        if (column['columns'] && column['columns'].length > 0) {
          header.push(column['name']);
          header.push('');
          header.push('');
          column['columns'].forEach(
            (subH, index2) => {
              subHeader.push(subH['name']);
            }
          );
        } else {
          header.push(column['name']);
          subHeader.push('');
        }

      }
    );

    const rows: any[] = table['rows'];

    const titleRow = worksheet.addRow([title]);
    // worksheet.mergeCells(3, 10, 3, 12);
    const date = new Date();
    const dateStr = date.getDate() + '/' + (date.getMonth() + 1) + '/' + date.getFullYear();

    titleRow.font = {name: 'TONG_HOP', size: 13, bold: true};
    const authorRow = [];
    subHeader.forEach(
      (subH, index) => {
        if (index === subHeader.length - 4) {
          authorRow.push('Ngày kết xuất');
        } else if (index === subHeader.length - 2) {
          authorRow.push('Người kết xuất');
        } else if (index === subHeader.length - 3) {
          authorRow.push(dateStr);
        } else if (index === subHeader.length - 1) {
          authorRow.push(JSON.parse(localStorage.getItem(config.currentUser)).username);
        } else {
          authorRow.push('');
        }
      }
    );
    worksheet.addRow(authorRow);

    const headerRow = worksheet.addRow(header);
    const subHeaderRow = worksheet.addRow(subHeader);

    // worksheet.mergeCells(3, 9, 3, 11);
    worksheet.getRow(3).values.forEach(
      (rowHeader, index) => {
        if (worksheet.getRow(3).values[index] === worksheet.getRow(3).values[index + 1]
          && index <= header.length) {
          worksheet.mergeCells(3, index - 1, 3, index + 1);
        }
        if (worksheet.getRow(3).values[index] && !worksheet.getRow(4).values[index]) {
          worksheet.mergeCells(3, index, 4, index);
        }
      }
    );
    headerRow.font = {size: 12, bold: true};
    subHeaderRow.font = {size: 12, bold: true};
    // if (columns.length === 5) {
    //   worksheet.mergeCells(`A1:F1`);
    // } else if (columns.length === 7) {
    //   worksheet.mergeCells(`A1:H1`);
    // } else if (columns.length === 4) {
    //   worksheet.mergeCells(`A1:E1`);
    // } else if (columns.length === 6) {
    //   worksheet.mergeCells(`A1:G1`);
    // } else {
    //   worksheet.mergeCells(`A1:I1`);
    // }

    headerRow.eachCell((cell, number) => {
      cell.fill = {
        type: 'pattern',
        pattern: 'solid',
        fgColor: {argb: 'F1F7FF'},
        bgColor: {argb: 'F1F7FF'}
      };
      cell.border = {top: {style: 'thin'}, left: {style: 'thin'}, bottom: {style: 'thin'}, right: {style: 'thin'}};
    });
    subHeaderRow.eachCell((cell, number) => {
      if (number === subHeader.length - 4 || subHeader.length - 2) {
        worksheet.getColumn(number).width = 13;
      }
      if (number === subHeader.length - 3 || subHeader.length - 1) {
        worksheet.getColumn(number).width = 15;
      }
      cell.fill = {
        type: 'pattern',
        pattern: 'solid',
        fgColor: {argb: 'F1F7FF'},
        bgColor: {argb: 'F1F7FF'}
      };
      cell.border = {top: {style: 'thin'}, left: {style: 'thin'}, bottom: {style: 'thin'}, right: {style: 'thin'}};
    });

    rows.forEach(
      (row, index) => {
        const cells: any[] = [];
        cells.push(row['cells']);
        cells.forEach(
          (celldd) => {
            const viewValues: any[] = [index + 1];
            celldd.forEach(
              cell => {
                if (cell['isGrowth'] != null) {
                  viewValues.push(cell['delta']);
                } else if (cell['chart']) {
                  return;
                } else if (cell['performValue']) {
                  return;
                }
                if (!cell['viewValue'] && !(cell['performValue'] || cell['performValue'] == 0) && !cell['chart'] && !(cell['delta'] || cell['delta'] == 0)) {                  viewValues.push('');
                  return;
                }
                if (cell['viewValue']) {
                  viewValues.push(cell['viewValue']);
                }
              }
            );
            const rowdd = worksheet.addRow(viewValues);
            rowdd.eachCell((cell, number) => {
              if (cell.value) {
                cell.border = {top: {style: 'thin'}, left: {style: 'thin'}, bottom: {style: 'thin'}, right: {style: 'thin'}};
              } else {
                cell.border = {top: {style: 'thin'}, left: {style: 'thin'}, bottom: {style: 'thin'}, right: {style: 'thin'}};
              }
            });
          }
        );
      }
    );
    // worksheet.columns.forEach(column => {
    //   if (column.values[3]) {
    //     column.width = column.values[3].length < 10 ? 12 : (column.values[3].length + 5);
    //   }
    // });
    subHeaderRow.eachCell((cell, number) => {
      if (number === subHeader.length - 4 || subHeader.length - 2) {
        worksheet.getColumn(number).width = 13;
      }
      if (number === subHeader.length - 3 || subHeader.length - 1) {
        worksheet.getColumn(number).width = 15;
      }
      cell.fill = {
        type: 'pattern',
        pattern: 'solid',
        fgColor: {argb: 'F1F7FF'},
        bgColor: {argb: 'F1F7FF'}
      };
      cell.border = {top: {style: 'thin'}, left: {style: 'thin'}, bottom: {style: 'thin'}, right: {style: 'thin'}};
    });

    subHeader.forEach(
      (subHeade, index) => {
        if (subHeade) {
          worksheet.getColumn(index).width = 10;
        } else {
          worksheet.columns.forEach(column => {
            if (column.values[4]) {
              column.width = column.values[3].length < 10 ? 12 : (column.values[3].length + 2);
            }
          });
        }
      }
    );
    // worksheet.columns.forEach(column => {
    //   console.log(column.values[4]);
    //   if (!column.values[4]) {
    //     console.log(column.values[4]);
    //     column.width = column.values[3].length < 10 ? 12 : (column.values[3].length + 2);
    //   }
    // });
    worksheet.getColumn(1).width = 5;
    worksheet.getColumn(2).width = 30;
    worksheet.getRow(1).height = 30;
    worksheet.mergeCells(1, 1, 1, worksheet.getRow(4).values.length - 1);

    worksheet.getRow(3).height = 30;

    columns.forEach(
      (column, indexColumn) => {
        if (column['align'] == 'left' && column['type'] != 'GROWTH_MONTH') {
          worksheet.getColumn(indexColumn + 2).alignment = {vertical: 'middle', horizontal: 'left'};
        } else if (column['align'] == 'right' && column['type'] != 'GROWTH_MONTH') {
          worksheet.getColumn(indexColumn + 2).alignment = {vertical: 'middle', horizontal: 'right'};
        } else if (column['align'] == 'center' && column['type'] != 'GROWTH_MONTH') {
          worksheet.getColumn(indexColumn + 2).alignment = {vertical: 'middle', horizontal: 'center'};
        } else if (column['type'] == 'GROWTH_MONTH' && column['align'] == 'center') {
          worksheet.getColumn(indexColumn + 2).alignment = {vertical: 'middle', horizontal: 'right'};
        } else {
          worksheet.getColumn(indexColumn + 2).alignment = {vertical: 'middle', horizontal: 'center'};
        }
      }
    );
    worksheet.getColumn(1).alignment = {vertical: 'middle', horizontal: 'center'};

    headerRow.alignment = {vertical: 'middle', horizontal: 'center'};
    subHeaderRow.alignment = {vertical: 'middle', horizontal: 'center'};
    titleRow.alignment = {vertical: 'middle', horizontal: 'center'};
    worksheet.getRow(3).alignment = { wrapText: true, vertical: 'middle', horizontal: 'center' };

    // tslint:disable-next-line:no-shadowed-variable
    workbook.xlsx.writeBuffer().then((data) => {
      const fileName = this.dataTable['title'].toUpperCase() + '.xlsx';
      const blob = new Blob([data], {type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'});
      fs.saveAs(blob, fileName);
    });

  }


  detail(drilldownObject: any, serviceId: any, shopCode: any, pRow: any) {
    if (serviceId === null) {
      if (!this.dashboardModel.serviceId) {
        // return;
      } else {
        serviceId = this.dashboardModel.serviceId;
      }
    }
    const drilldown: DrilldownModel = new DrilldownModel(serviceId, drilldownObject, shopCode);
    drilldown.pRow = pRow;
    this.clickService.emit(drilldown);
  }

  ngAfterViewInit(): void {
    let isRank = false;
    this.dataTable['columns'].forEach(
      column => {
        if (column['value'] === 'RANK') {
          isRank = true;
        }
      }
    );
    if (isRank) {
      setTimeout(() => {
        this.dataTableShow['rows'].sort(this.compareValues('cells', 'viewValue', 2, 'asc'));
        this.pageChange(1);
      }, 100);
    } else if (this.dataTable['rows'][0]) {
      if (this.dataTable['rows'][0]['rowIndex'] === 0 || this.dataTable['rows'][0]['rowIndex']) {
        setTimeout(() => {
          this.dataTableShow['rows'].sort(this.compareValuesSpark3('rowIndex', 'asc'));
          this.pageChange(1);
        }, 300);
      }
    } else {
      setTimeout(() => {
        this.dataTableShow['rows'].sort(this.compareValues('cells', 'viewValue', 0));
        this.pageChange(1);
      }, 100);
    }
    if (this.dashboardModel.pRow || this.dashboardModel.pRow == 0) {
      setTimeout(() => {
        this.dataTableShow['rows'].sort(this.compareValuesSpark3('rowIndex', 'asc'));
        this.pageChange(1);
      }, 200);
    }
  }

  compareValues(key, viewValue, index, order = 'asc') {
    return (a, b) => {
      if (!a.hasOwnProperty(key) || !b.hasOwnProperty(key)) {
        // không tồn tại tính chất trên cả hai object
        return 0;
      }
      // console.log(a[key][0][viewValue], '---', b[key][0][viewValue]);
      if (index == 2) {
        a[key][index][viewValue] = parseFloat(a[key][index][viewValue]);
        b[key][index][viewValue] = parseFloat(b[key][index][viewValue]);
      }

      const varA = (typeof a[key][index][viewValue] === 'string') ? a[key][index][viewValue].toUpperCase() : a[key][index][viewValue];
      const varB = (typeof b[key][index][viewValue] === 'string') ? b[key][index][viewValue].toUpperCase() : b[key][index][viewValue];

      let comparison;
      if (index == 2) {
        comparison = 0;
        if (varA > varB) {
          comparison = 1;
        } else if (varA < varB) {
          comparison = -1;
        }
      } else {
        comparison = varA.localeCompare(varB, 'vi');
      }
      // if (varA > varB) {
      //   comparison = 1;
      // } else if (varA < varB) {
      //   comparison = -1;
      // }
      return (
        (order == 'desc') ? (comparison * -1) : comparison
      );
    };
  }

  compareValuesSpark3(viewValue, order = 'asc') {
    return (a, b) => {
      if (!a || !b) {
        // không tồn tại tính chất trên cả hai object
        return 0;
      }
      const varA = a['rowIndex'];
      const varB = b['rowIndex'];

      let comparison;
      if (varA > varB) {
        comparison = 1;
      } else if (varA < varB) {
        comparison = -1;
      }
      return (
        (order == 'desc') ? (comparison * -1) : comparison
      );
    };
  }

  setSort() {
    // this.typeSort = 'asc';
    this.isClickSort = true;
    this.dataTable['rows'].sort(this.compareValues('cells', 'viewValue', 2, this.typeSort));
    if (this.typeSort === 'asc') {
      this.typeSort = 'desc';
    } else {
      this.typeSort = 'asc';
    }
  }

  getDataLv2() {
    this.tableModel = new TableModel();
    this.columns = [];
    const rows: RowModel[] = [];
    this.http.post(config.download_lv2_API, this.dashboardModel).subscribe(
      data => {
        if (data['data']) {
          // this.tableModel.columns = data['data'][0];
          this.columns.push(new ColumnModel('left', data['data'][0]['staffName']));
          this.columns.push(new ColumnModel('right', data['data'][0]['accumSchedule']));
          this.columns.push(new ColumnModel('right', data['data'][0]['accumPerform']));
          this.columns.push(new ColumnModel('right', data['data'][0]['accumComplete']));
          this.columns.push(new ColumnModel('right', data['data'][0]['complete']));
          this.columns.push(new ColumnModel('right', data['data'][0]['schedule']));
          this.columns.push(new ColumnModel('right', data['data'][0]['upDown']));
          this.tableModel.columns = this.columns;
          this.tableModel.title = this.dataTable['title'];
          data['data'].forEach(
            (row, index) => {
              if (index > 0) {
                const cells: CellModel[] = [];
                cells.push(new CellModel(row['staffName']));
                cells.push(new CellModel(row['accumSchedule']));
                cells.push(new CellModel(row['accumPerform']));
                cells.push(new CellModel(row['accumComplete']));
                cells.push(new CellModel(row['complete']));
                cells.push(new CellModel(row['schedule']));
                cells.push(new CellModel(row['upDown']));
                const rowTable: RowModel = new RowModel();
                rowTable.cells = cells;
                rows.push(rowTable);
              }
            }
          );
          this.tableModel.rows = rows;
          this.ExportToExcel(this.tableModel);
          // console.log('this.tableModel', this.tableModel);
        }
      }
    );
  }

  paginate(array, page_size, page_number) {
    return array.slice((page_number - 1) * page_size, page_number * page_size);
  }

  pageChange(page: number) {
    this.curentPage = page;
    this.totalItems = (this.dataTable && this.dataTable['rows'].length > 70) ? this.dataTable['rows'].length : 0;
    this.paginate(this.dataTableShow['rows'], 70, page);
  }

  trackByFn(index, item) {
    return index;
  }
}
