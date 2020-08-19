import {Pager} from './Pager';

export class Channel {
  id: number;
  code: string;
  name: string;
  vdsChannelCode: string;
  vdsChannelName: string;
  status: number;
  page: Pager;
  public constructor() {}
}
