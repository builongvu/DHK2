export class UnitModel {
  id: number;
  groupId: number;
  shopName: string;
  shopCode: string;
  parentShopCode: string;
  vdsChannelCode: string;
  children: UnitModel[];
  shortName?: string;
  position?: boolean;

  constructor(shopName?: string, shopCode?: string, vdsChannelCode?: string, position?: boolean, shortName?: string) {
    this.shopName = shopName;
    this.shopCode = shopCode;
    this.vdsChannelCode = vdsChannelCode;
    this.position = position;
    this.shortName = shortName;
  }
}

