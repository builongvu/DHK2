export class StaffsTable {
  staffCode: string;
  staffName: string;
  phoneNumber: string;
  email: string;
  shopCode: string;
  groupChannel: string;
  unitName: string;
  vdsChannelCode?: string;
  staffType?: string;
  shopWarning?: string;
  id?: number;

  constructor(staffCode: string,
              staffName: string,
              phoneNumber: string,
              email: string,
              shopCode: string,
              groupChannel: string,
              unitName: string,
              vdsChannelCode?: string,
              staffType?: string,
              shopWarning?: string,
              id?: number) {
    this.staffCode = staffCode;
    this.staffName = staffName;
    this.phoneNumber = phoneNumber;
    this.email = email;
    this.shopCode = shopCode;
    this.groupChannel = groupChannel;
    this.unitName = unitName;
    this.vdsChannelCode = vdsChannelCode;
    this.staffType = staffType;
    this.shopWarning = shopWarning;
    this.id = id;
  }
}
