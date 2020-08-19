import {ColumnModel} from './Column.model';
import {RowModel} from './Row.model';

export class TableModel {
  chartSize: '1_1';
  columns: ColumnModel[];
  downloadDetail: false;
  drilldown: true;
  drilldownObject: 7;
  drilldownType: null;
  expand: false;
  pageNum: 0;
  pageSize: 0;
  rows: RowModel[];
  subTitle: null;
  title: 'VDS - TÌNH HÌNH TRIỂN KHAI CHỈ TIÊU NĂM, LŨY KẾ ĐẾN 12/2019';
  titleColor: null;
  total: 0;
  type: 'TABLE_VIEW';
}
