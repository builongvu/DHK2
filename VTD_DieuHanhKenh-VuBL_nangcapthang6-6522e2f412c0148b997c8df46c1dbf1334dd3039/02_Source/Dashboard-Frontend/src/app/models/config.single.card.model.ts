export class ConfigSingleCardModel {
  cardId: number;
  cardName: string;
  cardNameI18n: string;
  cardSize: string;
  createDate: string;
  drilldown: number;
  drillDownType: number;
  drillDownObjectId: number;
  cardType: string;
  groupId: number;
  serviceId: number;
  status: number;
  zoom: number;
  sizeName: string;
  groupName: string;
  nameCardType: string;
  showZoom: string;
  serviceName: string;
  showDrillDown: string;
  showDrillDownObjectId: string;
  showStatus: string;

  constructor(cardId?: number,
              cardName?: string,
              cardNameI18n?: string,
              cardSize?: string,
              createDate?: string,
              drilldown?: number,
              drillDownType?: number,
              drillDownObjectId?: number,
              cardType?: string,
              groupId?: number,
              serviceId?: number,
              status?: number,
              zoom?: number) {
    this.cardId = cardId;
    this.cardName = cardName;
    this.cardNameI18n = cardNameI18n;
    this.cardSize = cardSize;
    this.createDate = createDate;
    this.drilldown = drilldown;
    this.drillDownType = drillDownType;
    this.drillDownObjectId = drillDownObjectId;
    this.cardType = cardType;
    this.groupId = groupId;
    this.serviceId = serviceId;
    this.status = status;
    this.zoom = zoom;
  }
}

export class ConfigCardSize {
  value: string;
  name: string;
}
