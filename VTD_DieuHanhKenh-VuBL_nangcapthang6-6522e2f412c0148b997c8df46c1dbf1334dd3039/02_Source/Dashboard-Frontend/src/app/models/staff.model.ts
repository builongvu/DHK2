export class StaffModel {
  shopId: number;
  staffCode: string;
  staffId: number;
  name: string;
  staffName: string;
  constructor(shopId: number) {
    this.shopId = shopId;
  }
}

export class StaffDTO {
  id: number;
  code: string;
  name: string;
  status: string;
  shopcode: string;
  role: string;
  isHaveRole: number;
}
