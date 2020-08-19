import {Channel} from './Channel.model';
import {apParam} from './apParam.model';

export class ServiceChannel {
  id: number;
  serviceId: number;
  vdsChannelCode: string;
  channel?: Channel = new Channel();
  appParam?: apParam = new apParam();
  constructor() {}
}
