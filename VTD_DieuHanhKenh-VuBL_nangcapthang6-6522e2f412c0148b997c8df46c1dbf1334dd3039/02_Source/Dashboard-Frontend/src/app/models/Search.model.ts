import {Pager} from './Pager';
import {SortTable} from './SortTable';

export class SearchModel {
  receivedDate: string;
  channelCode: string;
  groupServiceId: number;
  productId: number;
  serviceIds: Array<number>;
  congVan: string;
  shops: Array<string>;
  staffs: Array<string>;
  typeSearch: string;
  pager: Pager;
  sort: SortTable;
  cycleCode?: string;

  // tslint:disable-next-line:max-line-length
  constructor(receivedDate: string, channelCode: string,
              groupServiceId: number, productId: number,
              serviceIds: Array<number>, congVan: string,
              shops: Array<string>,
              staffs: Array<string>,
              typeSearch: string,
              pager: Pager, sort: SortTable,
              cycleCode?: string) {
    this.channelCode = channelCode;
    this.groupServiceId = groupServiceId;
    this.productId = productId;
    this.serviceIds = serviceIds;
    this.pager = pager;
    this.receivedDate = receivedDate;
    this.congVan = congVan;
    this.shops = shops;
    this.staffs = staffs;
    this.sort = sort;
    this.typeSearch = typeSearch;
    this.cycleCode = cycleCode;
  }
}
