export class AddStaff {
  shopCode: string;
  staffCode: string;
  staffName: string;
  email: string;
  phoneNumber: string;
  vdsChannelCode?: string;
  staffType?: string;
  shopWarning?: string;

  constructor(shopCode: string,
              staffCode: string,
              staffName: string,
              email: string,
              phoneNumber: string,
              vdsChannelCode?: string,
              staffType?: string,
              shopWarning?: string) {
    this.email = email;
    this.shopCode = shopCode;
    this.staffCode = staffCode;
    this.staffName = staffName;
    this.phoneNumber = phoneNumber;
    this.vdsChannelCode = vdsChannelCode;
    this.staffType = staffType;
    this.shopWarning = shopWarning;
  }
}
