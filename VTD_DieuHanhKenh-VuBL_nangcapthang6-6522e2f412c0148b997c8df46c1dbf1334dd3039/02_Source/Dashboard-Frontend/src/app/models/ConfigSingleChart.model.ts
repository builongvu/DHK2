import {Pager} from './Pager';

export class ConfigSingleChartModel {
  chartId: number;
  cardId: number;
  chartType: string;
  chartSize: string;
  metaData: string;
  serviceIds: string;
  title: string;
  titleI18n: string;
  subtitle: string;
  subtitleI18n: string;
  createDate: number;
  drilldown: number;
  expand: number;
  status: number;
  queryParam: string;
  drillDownType: number;
  drillDownObjectId: number;
  cardName: string;
  groupName: string;
  queryData: string;
  zoom: number;
  lstServiceIds: any[];
  chartSizeName: string;
  showStatus: string;
  showDrillDown: string;
  showExpand: string;
  chartTypeName: string;
  pager: Pager;


  constructor(mlngCardId?: number, mstrChartType?: string, mstrChartSize?: string, mstrMetaData?: string, mstrServiceIds?: any[],
              mstrTitle?: string, mintDrilldown?: number, mintExpand?: number, mlngStatus?: number,
              queryParam?: string, drillDownObjectId?: number, pager?: Pager, mlngChartId?: number, serviceIds?: string) {
    this.chartId = mlngChartId;
    this.cardId = mlngCardId;
    this.chartType = mstrChartType;
    this.chartSize = mstrChartSize;
    this.metaData = mstrMetaData;
    this.lstServiceIds = mstrServiceIds;
    this.title = mstrTitle;
    this.drilldown = mintDrilldown;
    this.expand = mintExpand;
    this.status = mlngStatus;
    this.queryParam = queryParam;
    this.drillDownObjectId = drillDownObjectId;
    this.pager = pager;
    this.serviceIds = serviceIds;
  }
}

export class ConfigChartType {
  value: string;
  name: string;
}
