import { Pager } from './Pager';

export class MappingGroupChannel {
    id: number;
    groupChannelCode: string;
    groupChannelName: string;
    vdsChannelCode: string;
    vdsChannelName: string;
    status: string;
    user: string;
    createDate: string;
    pager: Pager;
}