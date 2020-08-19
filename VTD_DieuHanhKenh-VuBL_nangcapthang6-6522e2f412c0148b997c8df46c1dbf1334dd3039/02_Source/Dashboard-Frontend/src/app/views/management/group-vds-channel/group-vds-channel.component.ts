import { Component, OnInit } from '@angular/core';
import { TreeviewConfig, TreeviewItem } from 'ngx-treeview';
import { ChannelService } from '../../../services/management/channel.service';
import { Channel } from '../../../models/Channel.model';
import { VttgroupchannelService } from '../../../services/management/vttgroupchannel.service';
import { MappinggroupchannelService } from '../../../services/management/mappinggroupchannel.service';
import { config } from '../../../config/application.config';
import { MappingGroupChannel } from '../../../models/mappinggroupchannel.model';
import { Pager } from '../../../models/Pager';
import { AddVdsComponent } from './add-vds/add-vds.component';
import { MatDialog } from '@angular/material';
import { VdsService } from '../../../services/management/vds-service.service';
import { MappingVdsVttComponent } from './mapping-vds-vtt/mapping-vds-vtt.component';
import { WarningReceiveService } from '../../../services/management/warning-receive.service';
import { warningservice } from '../../../models/warningservice';

@Component({
  selector: 'app-group-vds-channel',
  templateUrl: './group-vds-channel.component.html',
  styleUrls: ['./group-vds-channel.component.scss']
})
export class GroupVdsChannelComponent implements OnInit {

  valueVDS;
  marrNodeVDS: TreeviewItem[] = [];
  marrVDSGroupChannel: Channel[] = [];
  valueVTT;
  marrNodeVTT: TreeviewItem[] = [];
  marrVTTGroupChannel: MappingGroupChannel[] = [];
  searchMapping: MappingGroupChannel = new MappingGroupChannel();
  marrMapping: MappingGroupChannel[] = [];
  config = TreeviewConfig.create({
    hasFilter: true,
    hasCollapseExpand: false,
    maxHeight: 80,
  });
  status = '-1';
  total;
  constructor(
    private vdsSerivce: VdsService,
    private vttgroupchannelService: VttgroupchannelService,
    public dialog: MatDialog,
    private mappingservice: MappinggroupchannelService,
    private warning: WarningReceiveService

  ) { }

  ngOnInit() {
    this.getVTTChannel();
    this.searchMapping.pager = new Pager(1, 10);
    this.warning.reloadWarning$.subscribe(p => {
      if (p == 1) {
        this.searchMapping.pager.page = 1;
      }
      this.search();
    });
    this.warning.reload$.subscribe(res => {
      this.getVDSChannel();
    });
  }

  clickSearch() {
    this.searchMapping.pager.page = 1;
    this.search();
  }

  search() {
    this.searchMapping.status = this.status;
    if (this.searchMapping.groupChannelCode == 'null') {
      this.searchMapping.groupChannelCode = null;
    }
    if (this.searchMapping.vdsChannelCode == 'null') {
      this.searchMapping.vdsChannelCode = null;
    }
    this.mappingservice.getByCondition(this.searchMapping).subscribe(res => {
      this.marrMapping = res.data;
      this.searchMapping.pager.totalRow = res.totalRow;
      this.searchMapping.pager.page = res.page;
    });
  }

  pageChange(p: any) {
    this.searchMapping.pager.page = p;
    this.search();
  }

  getVDSChannel() {
    this.vdsSerivce.getActiveVDSGroup().subscribe(
      next => {
        this.marrNodeVDS = [];
        this.marrVDSGroupChannel = next.data;
        const channel = new Channel();
        if (localStorage.getItem(config.user_default_language) === 'en') {
          channel.name = 'Select';
        } else {
          channel.name = 'Chọn';
        }
        channel.code = 'null';
        this.marrVDSGroupChannel.reverse();
        this.marrVDSGroupChannel.push(channel);
        this.marrVDSGroupChannel.reverse();
        this.marrVDSGroupChannel.forEach(
          valueUnit => {
            const text = valueUnit['name'];
            const value = valueUnit['code'];
            this.marrNodeVDS.push(new TreeviewItem({ text, value, checked: false }));
          }
        );
        this.valueVDS = 'null';
      },
      error => (this.marrNodeVDS = [], console.log(error))
    );
  }
  getVTTChannel() {
    this.vttgroupchannelService.getActiveVttGroup().subscribe(
      next => {
        this.marrNodeVTT = [];
        this.marrVTTGroupChannel = next.data;
        const channel = new MappingGroupChannel();
        if (localStorage.getItem(config.user_default_language) === 'en') {
          channel.groupChannelName = 'Select';
        } else {
          channel.groupChannelName = 'Chọn';
        }
        channel.groupChannelCode = 'null';
        this.marrVTTGroupChannel.reverse();
        this.marrVTTGroupChannel.push(channel);
        this.marrVTTGroupChannel.reverse();
        this.marrVTTGroupChannel.forEach(
          valueUnit => {
            const text = valueUnit['groupChannelName'];
            const value = valueUnit['groupChannelCode'];
            this.marrNodeVTT.push(new TreeviewItem({ text, value, checked: false }));
          }
        );
        this.valueVTT = 'null';
      },
      error => (this.marrNodeVTT = [], console.log(error))
    );
  }

  OnchangeVDS(value: any) {
    this.searchMapping.vdsChannelCode = value;
  }

  OnchangeVTT(value: any) {
    this.searchMapping.groupChannelCode = value;
  }

  openAddVDS() {
    const vdialog = this.dialog.open(AddVdsComponent, {
      data: {
      }
    });
  }

  openMapping() {
    const mobj = new MappingGroupChannel();
    mobj.id = -1;
    mobj.status = '1';
    const vdialog = this.dialog.open(MappingVdsVttComponent, {
      data: {
        marrVDS: this.marrNodeVDS,
        marrVTT: this.marrNodeVTT,
        mobjMap: mobj,
        type: 0
      }
    });
  }

  openEdit(mapping: MappingGroupChannel) {
    const vdialog = this.dialog.open(MappingVdsVttComponent, {
      data: {
        marrVDS: this.marrNodeVDS,
        marrVTT: this.marrNodeVTT,
        mobjMap: mapping,
        type: 1
      }
    });
  }
}
