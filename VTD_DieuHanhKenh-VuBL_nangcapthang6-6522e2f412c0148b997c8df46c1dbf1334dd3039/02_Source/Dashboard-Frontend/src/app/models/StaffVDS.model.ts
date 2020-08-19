export class StaffVDS {
  vdsChannelCode: string;
  shopCode: string;
  staffCode: string;
  staffName: string;
  phoneNumber: string;
  email: string;

  constructor(vdsChannelCode: string, shopCode: string, staffCode: string, staffName: string, phoneNumber: string, email: string) {
    this.vdsChannelCode = vdsChannelCode;
    this.shopCode = shopCode;
    this.staffCode = staffCode;
    this.staffName = staffName;
    this.phoneNumber = phoneNumber;
    this.email = email;
  }
}
