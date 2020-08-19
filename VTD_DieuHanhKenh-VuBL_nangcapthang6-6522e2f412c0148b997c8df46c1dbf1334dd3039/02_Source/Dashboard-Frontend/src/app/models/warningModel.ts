export class WarningModel{
    nameCT: string;
    channel: string;
    wLevel : string;
    wStatus: string;
    wFvalue: string;
    wOvalue: string;
    wExp: string;
    constructor(NameCT: string, Channel: string,wLevel : string,wStatus: string,wFvalue: string, wOvalue: string,wExp: string){
        this.nameCT = NameCT;
        this.channel = Channel;
        this.wLevel = wLevel;
        this.wStatus = wStatus;
        this.wFvalue = wFvalue;
        this.wOvalue = wOvalue;
        this.wExp = wExp;
    }
}