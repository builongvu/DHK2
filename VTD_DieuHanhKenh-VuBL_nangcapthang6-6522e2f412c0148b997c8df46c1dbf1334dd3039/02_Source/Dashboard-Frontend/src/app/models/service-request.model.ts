import {ServiceChannel} from './ServiceChannel.model';

export class ServiceRequest {
  id: number;
  code: string;
  name: string;
  groupServiceId: number;
  parentId: number;
  dataType: string;
  assignType: number;
  importType: number;
  serviceCycle: number;
  serviceType: number;
  fromDate: string;
  toDate: string;
  serviceOrder: number;
  status: number;
  exp: string;
  user: string;
  changeDatetime: string;
  congVan: string;
  unitCode: string;
  unitType: string;
  vdsChannelCode: string;
  serviceCalcType: string;

  groupName: string;
  checkbox: boolean;
  grandchildren: number;
  no: number;
  listServiceChannel: Array<ServiceChannel> = [];

  constructor () {}

}
