export class DrilldownModel {
  serviceId: number;
  drilldownObject: number;
  shopCode?: string;
  cardId: number;
  isTarget?: number;
  codeWarning: any;
  index: any;
  nationalStaff?: string;
  pRow?: any;
  month?: string;
  constructor(serviceId: number, drilldownObject: number,
    shopCode?: string, cardId?: number, isTarget?: number,
    codeWarning?: any,
    index?: any, nationalStaff?: string, pRow?: any, month?: string) {
    this.serviceId = serviceId;
    this.drilldownObject = drilldownObject;
    this.shopCode = shopCode;
    this.cardId = cardId;
    this.isTarget = isTarget;
    this.codeWarning = codeWarning;
    this.index = index;
    this.nationalStaff = nationalStaff;
    this.pRow = pRow;
    this.month = month;
  }
}
