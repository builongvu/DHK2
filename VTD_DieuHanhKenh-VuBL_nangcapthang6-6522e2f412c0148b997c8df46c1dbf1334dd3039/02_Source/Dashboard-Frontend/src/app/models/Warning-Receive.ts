import { apParam } from './apParam.model';
import { Partner } from './Partner';

export class WarningReceive {
    mdtCreateDate: Date;
    mintInformLevel: number;
    mintWarningLevel: number;
    mlngId: number;
    mstrShopCode: string;
    mstrStatus: string;
    mstrUser: string;
    mstrChannel: string;
    constructor() {

    }
}

export class WarningReceiveData {
    mdtCreateDate: Date;
    mintInformLevel: apParam;
    mintWarningLevel: apParam;
    mlngId: number;
    mstrShopCode: Partner;
    mstrStatus: string;
    mstrUser: string;
    mblnCheckbox: boolean;
    constructor() {

    }
}

export class SearchReceive {
    mstrShopCode: string;
    mstrStatus: string;
    mintWarningLevel: string;
    mintInformLevel: string;
    constructor() {

    }
}
