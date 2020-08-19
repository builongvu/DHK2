import { config } from './../config/application.config';
import { Pager } from './Pager';
export class VttGroupChannelDTO {
  no: number;
  groupChannelCode: string;
  groupChannelName?: string;
  classification?: string;
  positionId?: number;
  positionCode?: string;
  positionName?: string;
  channelTypeId?: string;
  status: string;
  pager: Pager;

  public constructor(groupChannelCode, status) {
    this.groupChannelCode = groupChannelCode;
    this.status = status;
  }
}
