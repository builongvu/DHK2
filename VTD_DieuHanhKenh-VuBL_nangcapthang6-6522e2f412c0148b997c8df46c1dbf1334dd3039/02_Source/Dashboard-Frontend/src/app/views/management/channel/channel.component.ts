import {Component, OnInit, TemplateRef} from '@angular/core';
import {User} from '../../../models/User.model';
import {UserService} from '../../../services/user.service';
import {first, catchError} from 'rxjs/operators';
import {BsModalService, BsModalRef} from 'ngx-bootstrap/modal';
import {error} from '@angular/compiler/src/util';
import {ToastrService} from 'ngx-toastr';
import {config} from '../../../config/application.config';
import {ResponseObject} from '../../../models/Response.model';
import {Pager} from '../../../models/Pager';
import {Channel} from '../../../models/Channel.model';
import {ChannelService} from '../../../services/management/channel.service';

@Component({
  templateUrl: 'channel.component.html'
})
export class ChannelComponent implements OnInit {
  channels: Channel[] = [];
  modalRef: BsModalRef;
  channelSearch: Channel = new Channel();
  channelStatus = {value: '1'};
  p: number = 1;
  total: number;
  pageSize: number = 5;

  status = [
    {
      name: 'Chọn trạng thái',
      value: -1
    },
    {
      name: 'Hoạt động',
      value: 1
    },
    {
      name: 'Ngừng hoạt động',
      value: 0
    }
  ];

  constructor(private channelService: ChannelService,
              private modalService: BsModalService,
              private toastrService: ToastrService) {
  }

  ngOnInit(): void {
    this.getPage(1);
  }

  getPage(page: number) {
    // @ts-ignore
    let pager: Pager = new Pager();
    pager.page = page;
    pager.pageSize = this.pageSize;
    this.channelSearch.page = pager;
    this.channelService.getChannelsByCondition(this.channelSearch).pipe(first()).subscribe((respons: any) => {
      this.channels = respons.data;
      this.total = respons.totalRow;
      this.p = page;
    });
  }

  confirmDel(template: TemplateRef<any>, channelId: string) {
    this.modalRef = this.modalService.show(template);
    this.modalRef.content = channelId;
  }

  deleteChannel() {
    // console.log("Start delete: ", this.modalRef.content);
    this.channelService.deleteById(this.modalRef.content).subscribe(res => {
      this.showSuccess('Xóa thành công');
      this.getPage(1);
    }, catchError => {
      console.log("result: ", catchError);
    });
    this.modalRef.hide();
  }

  showSuccess(mes: string) {
    this.toastrService.success('', mes);
  }

  search() {
    // console.log("userSearch: ", this.userSearch);
    this.getPage(1);
  }
}
