export class StaffSearchVDS {
  shopCode?: string;
  position?: boolean;
  // edit shop warning 1/5/2020
  shopWarning?: string;
  // @ts-ignore
  constructor(shopCode?: string, position?: boolean,  shopWarning?: string) {
    this.shopCode = shopCode;
    this.position = position;
    this.shopWarning = shopWarning;
  }

}
