import { apParam } from './apParam.model';
import { ServiceModel } from './service.model';

export class WarningSend {
    mdtCreateDate: Date;
    mintEmail: number;
    mintInformLevel: number;
    mintSms: number;
    mintWarningLevel: number;
    mlngId: number;
    mlngIdContent: string;
    mlngServiceId: number;
    mstrUser: string;
    mstrStatus: string;
    public constructor(){
        
    }
}
export class WarningSendTable {
    mdtCreateDate: Date;
    mintEmail: number;
    mintInformLevel: apParam;
    mintSms: number;
    mintWarningLevel: apParam;
    mlngId: number;
    mlngIdContent: string;
    mlngServiceId: ServiceModel;
    mstrUser: string;
    mstrStatus: string;
    mblnCheckbox: boolean;
    public constructor(){
        
    }
}

export class SearchWarningSend {
    serviceId: number
    status: string;
    email: string;
    warningLevel: string;
    sms: string;
    informLevel: string;
}