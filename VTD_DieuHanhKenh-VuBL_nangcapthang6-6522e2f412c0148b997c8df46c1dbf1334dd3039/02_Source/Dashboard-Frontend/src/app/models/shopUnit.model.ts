import { Pager } from './Pager';
import { ServiceModel } from './service.model';
import { Partner } from './Partner';
import { DVT } from './dvt.model';

export class ShopUnitDTO {
    id: number;
    serviceId: number;
    shopCode: string;
    vdsChanelCode: string;
    unitCode: string;
    fromDate: string;
    toDate: string;
    status: string;
    pager: Pager;
}

export class ShopUnit {
    mlngId: number;
    mlngServiceId: number;
    mstrShopCode: string;
    mstrVdsChannelCode: string;
    mstrUnitCode: string;
    mdtFromDate: string;
    mdtToDate: string;
    mstrStatus: string;
    service: ServiceModel;
    partner: Partner;
    unit: DVT;
    check: boolean;
    channel: string;
}