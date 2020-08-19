export class PlanmonthlyModel {
  id: number;
  prdId: number;
  channelName: string; // kenh
  productName: string; // san pham
  groupServiceName: string; // nhom
  serviceName: string; // chi tieu
  fScheduleLastMonth: number; // ke hoach T-1
  fValueLastMonth: number; // thuc hien T-1
  completedLastMonth: number; // hoan thanh T-1
  compareMonthAndLastMonth: number; // so sanh T&T-1
  month: string; // thang
  fSchedule: number; // ke hoach T
  density: number; // Tỉ trọng
  unitName: string; // đơn vị
  staffName: string; // nhân viên
  serviceId: number;
  update: boolean;
  channelCode: string;
  shopCode: string;
  staffCode: string;
  checked?: boolean;

  constructor(id: number, fSchedule: number, serviceId: number,
              channelCode: string, shopCode: string, staffCode: string, prdId: number, checked?: boolean) {
    this.id = id;
    this.fSchedule = fSchedule;
    this.serviceId = serviceId;
    this.channelCode = channelCode;
    this.shopCode = shopCode;
    this.staffCode =  staffCode;
    this.prdId =  prdId;
    this.checked =  checked;
  }
}
