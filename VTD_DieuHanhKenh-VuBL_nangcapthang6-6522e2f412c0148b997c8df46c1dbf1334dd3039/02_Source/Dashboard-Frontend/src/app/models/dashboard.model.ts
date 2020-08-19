export class DashboardModel {
  groupId: number;
  prdId: number;
  cycleId: number;
  objectCode: string;
  vdsChannelCode: string;
  serviceId?: number;
  parentShopCode?: string;
  cardId?: number;
  isTarget?: number;
  expand?: number;
  sorted?: string;
  codeWarning?: any;
  pRow?: any;
  month?: string;
  nationalStaff?: string;


  constructor(groupId: number, prdId: number, cycleId: number,
              objectCode: string, vdsChannelCode: string,
              serviceId?: number, parentShopCode?: string,
              cardId?: number, isTarget?: number, expand?: number,
              sorted?: string,
              pRow?: string,
              codeWarning?: any,
              nationalStaff?: string,
              month?: string
  ) {
    this.groupId = groupId;
    this.prdId = prdId;
    this.cycleId = cycleId;
    this.objectCode = objectCode;
    this.serviceId = serviceId;
    this.parentShopCode = parentShopCode;
    this.vdsChannelCode = vdsChannelCode;
    this.cardId = cardId;
    this.isTarget = isTarget;
    this.expand = expand;
    this.sorted = sorted;
    this.pRow = pRow;
    this.codeWarning = codeWarning;
    this.nationalStaff = nationalStaff;
    this.month = month;
  }
}
