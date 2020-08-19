export class ConfigGroupCardModel {
  groupId: number;
  groupCode: string;
  channelId: number;
  groupName: string;
  defaultCycle: string;
  vdsChannelCode: string;
  shopCode: string;
  shopName: string;
  defaultCycleName: string;
  vdsChannelName: string;

  constructor(groupId?: number, groupCode?: string, channelId?: number, groupName?: string, defaultCycle?: string, vdsChannelCode?: string, shopCode?: string, shopName?: string, defaultCycleName?: string) {
    this.groupId = groupId;
    this.groupCode = groupCode;
    this.channelId = channelId;
    this.groupName = groupName;
    this.defaultCycle = defaultCycle;
    this.vdsChannelCode = vdsChannelCode;
    this.shopCode = shopCode;
    this.shopName = shopName;
    this.defaultCycleName = defaultCycleName;
  }
}

export class GroupCardCycle {
  code: string;
  name: string;
}
