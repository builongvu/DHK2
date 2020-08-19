export class EditStaffVDSModel {
  id: number;
  staffCode: string;
  staffName: string;
  phoneNumber: string;
  email: string;
  shopCode: string;
  vdsChannelCode?: string;
  staffType?: string;

  constructor(id: number,
              staffCode: string,
              staffName: string,
              phoneNumber: string,
              email: string,
              shopCode: string,
              vdsChannelCode?: string,
              staffType?: string) {
    this.id = id;
    this.staffCode = staffCode;
    this.staffName = staffName;
    this.phoneNumber = phoneNumber;
    this.email = email;
    this.shopCode = shopCode;
    this.vdsChannelCode = vdsChannelCode;
    this.staffType = staffType;
  }
}
