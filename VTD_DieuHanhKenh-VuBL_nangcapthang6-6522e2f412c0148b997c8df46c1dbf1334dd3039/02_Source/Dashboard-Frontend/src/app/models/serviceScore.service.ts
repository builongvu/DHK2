export class ServiceScoreService {
  id: number;
  serviceId: number;
  serviceName: string;
  status: number;
  fromDate: any;
  toDate: any;
  score: number;
  scoreMax: number;
  shopCode: any;
  staffCode: any;
  vdsChannelCode: string;

  constructor(serviceId?: number, serviceName?: string, status?: number, fromDate?: any, toDate?: any,
              score?: number,
              scoreMax?: number,
              id?: number,
              shopCode?: any, staffCode?: any, vdsChannelCode?: string) {
    this.serviceId = serviceId;
    this.serviceName = serviceName;
    this.status = status;
    this.fromDate = fromDate;
    this.toDate = toDate;
    this.score = score;
    this.scoreMax = scoreMax;
    this.id = id;
    this.shopCode = shopCode;
    this.staffCode = staffCode;
    this.vdsChannelCode = vdsChannelCode;
  }
}
