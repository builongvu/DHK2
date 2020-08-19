import { WarningReceiveService } from './../../../../services/management/warning-receive.service';
import { WarningReceive } from './../../../../models/Warning-Receive';
import { CreateGroupShopComponent } from './../create-group-shop/create-group-shop.component';
import { CreateGroupPoisitionComponent } from './../create-group-poisition/create-group-poisition.component';
import { CreateGroupComponent } from './../create-group/create-group.component';
import { MatDialog, MatDialogRef } from '@angular/material';
import { VttGroupChannelDTO } from './../../../../models/VttGroupChannelDTO.model';
import { config } from './../../../../config/application.config';
import { TreeviewItem, TreeviewConfig } from 'ngx-treeview';
import { GroupVttChannelService } from './../../../../services/management/group-vtt-channel.service';
import { VTTGroupChannel } from './../../../../models/vttgroupchannel.model';
import { Component, OnInit } from '@angular/core';
import { Pager } from '../../../../models/Pager';

@Component({
  selector: 'app-list-group-vtt-channel',
  templateUrl: './list-group-vtt-channel.component.html',
  styleUrls: ['./list-group-vtt-channel.component.scss'],
  providers: [
    { provide: MatDialogRef, useValue: { CreateGroupComponent } },
    { provide: MatDialogRef, useValue: { CreateGroupPoisitionComponent } },
    { provide: MatDialogRef, useValue: { CreateGroupShopComponent } }
  ],
})
export class ListGroupVttChannelComponent implements OnInit {

  marrVTTGroupChannel: VTTGroupChannel[] = [];
  marrVttGroupChannelDTO: VttGroupChannelDTO[] = [];
  valueVttChannel: any;
  marrChannelVTTNew: TreeviewItem[] = [];
  mnbrChannel: any = null;
  mobjConfig = TreeviewConfig.create({
    hasFilter: true,
    hasCollapseExpand: false,
    maxHeight: 140,
  });
  statusSelected: any;
  statusSearch: any;

  totalRow: number;
  page: number;
  no: number;
  minItemPerpage: number;
  maxItemPerpage: number;
  reload = false;

  constructor(
    private groupVttChannelService: GroupVttChannelService,
    private warningReceive: WarningReceiveService,
    public dialog: MatDialog
  ) { }

  ngOnInit() {
    this.page = 1;
    this.statusSelected = -1;
    this.getChannels();
    this.warningReceive.reloadWarning$.subscribe(res => {
      this.page = 1;
      this.statusSelected = -1;
      this.searchByCondition();
    });
  }

  onChange(value: any) {
    this.mnbrChannel = value;
  }

  getChannels() {
    this.groupVttChannelService.getActiveVttChannel().subscribe(
      (vobjNext: any) => {
        this.marrVTTGroupChannel = vobjNext.data;
        const vttGroupChannel = new VTTGroupChannel();
        vttGroupChannel.groupChannelCode = 'null';
        if (localStorage.getItem(config.user_default_language) === 'en') {
          vttGroupChannel.groupChannelName = 'Select';
        } else {
          vttGroupChannel.groupChannelName = 'Chọn';
        }
        this.marrVTTGroupChannel.reverse();
        this.marrVTTGroupChannel.push(vttGroupChannel);
        this.marrVTTGroupChannel.reverse();
        if (vobjNext) {
          this.marrVTTGroupChannel.forEach(
            valueUnit => {
              const text = valueUnit['groupChannelName'];
              const value = valueUnit['groupChannelCode'];
              this.marrChannelVTTNew.push(new TreeviewItem({ text, value, checked: false }));
            }
          );
        }
      },
      error => (this.marrChannelVTTNew = [])
    );
  }

  search() {
    this.page = 1;
    this.searchByCondition();
  }

  searchByCondition() {
    if (this.statusSelected == -1) {
      this.statusSearch = null;
    } else {
      this.statusSearch = this.statusSelected;
    }

    if (this.mnbrChannel === 'null') {
      this.mnbrChannel = null;
    }

    const vttGroupChannelDTO = new VttGroupChannelDTO(this.mnbrChannel, this.statusSearch);
    vttGroupChannelDTO.pager = new Pager(this.page, config.pageSize);

    this.no = (this.page * config.pageSize) - (config.pageSize - 1);

    this.groupVttChannelService.getvttGroupChannelByCondition(vttGroupChannelDTO).subscribe((data: any) => {
      this.marrVttGroupChannelDTO = data.data;
      this.totalRow = data.totalRow;
      for (let i = 0; i < this.marrVttGroupChannelDTO.length; i++) {
        this.marrVttGroupChannelDTO[i].no = this.no++;
        if (i === 0) {
          this.minItemPerpage = this.no - 1;
        }

        if (i === this.marrVttGroupChannelDTO.length - 1) {
          this.maxItemPerpage = this.no - 1;
        }

      }
    });


  }

  pageChanged(page) {
    this.page = page;
    this.searchByCondition();
  }

  openDialogCreateGroup() {

    const dialogRef = this.dialog.open(CreateGroupComponent, {
      maxWidth: '75vw',
      maxHeight: '45vh',
      width: '75vw',
    });

    dialogRef.afterClosed().subscribe(result => {
      this.marrChannelVTTNew = [];
      this.getChannels();
    });
  }

  openDialogCreateGroupShop() {

    const dialogRef = this.dialog.open(CreateGroupShopComponent, {
      maxWidth: '75vw',
      maxHeight: '45vh',
      width: '70vw',
    });
  }

  openDialogCreateGroupPoisition() {

    const dialogRef = this.dialog.open(CreateGroupPoisitionComponent, {
      maxWidth: '75vw',
      maxHeight: '45vh',
      width: '70vw',
    });
  }

}
