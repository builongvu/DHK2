export class DeclareVDSModel {
  child: number;
  vdsChannelCode: string;
  shopCode: string;
  shopName: string;
  shortName: string;
  parentShopCode: string;
  status: number;
  fromDate: number;
  toDate: number;
  id: number;


  constructor(vdsChannelCode: string, shopCode: string, shopName: string,
              shortName: string, parentShopCode: string, status: number,
              fromDate: number, toDate: number, child?: number, id?: number) {
    this.child = child;
    this.vdsChannelCode = vdsChannelCode;
    this.shopCode = shopCode;
    this.shopName = shopName;
    this.shortName = shortName;
    this.parentShopCode = parentShopCode;
    this.status = status;
    this.fromDate = fromDate;
    this.toDate = toDate;
    this.id = id;
  }
}
