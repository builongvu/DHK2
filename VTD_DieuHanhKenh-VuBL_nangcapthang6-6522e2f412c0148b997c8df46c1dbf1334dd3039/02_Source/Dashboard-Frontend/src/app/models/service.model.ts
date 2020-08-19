export class ServiceModel {
  id: number;
  name: string;
  groupServiceId: number;
  vdsChannelCode: string;
  congVan: string;
  parentId: number;
  code: string;
  children: ServiceModel[];


  constructor(groupServiceId: number, vdsChannelCode: string ) {
    this.groupServiceId = groupServiceId;
    this.vdsChannelCode = vdsChannelCode;
  
  }

}
