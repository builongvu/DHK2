export class warningservice {
     wcID?: number;
     svID?: number;
     vdscCode?: string;
     wlevel?: string;
     wStatus?: number;
     wfvalue?: string;
     wovalue?: string;
     wexp?: string;
     name: string;
     serviceName: string;
     appParamName: string;
     warningStatus: number;
     channelName: string;
     check?: boolean;

     constructor( wId?: number,  vdscCode?: string, wlevel?: string, wStatus?: number, wfvalue?: string, wovalue?: string, wexp?:string) {
         this.vdscCode=vdscCode, 
         this.wcID=wId,
               this.wlevel = wlevel,
               this.wStatus = wStatus,
               this.wfvalue = wfvalue,
               this.wovalue = wovalue,
               this.wexp = wexp
              
     }



}
