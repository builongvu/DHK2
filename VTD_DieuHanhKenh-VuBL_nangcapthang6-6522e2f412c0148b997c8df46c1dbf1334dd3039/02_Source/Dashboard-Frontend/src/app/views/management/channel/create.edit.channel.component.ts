import { Component, OnInit } from '@angular/core';
import { User } from '../../../models/User.model';
import { UserService } from '../../../services/user.service';
import { first } from 'rxjs/operators';
import { Router, ActivatedRoute } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { config } from '../../../config/application.config';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MustMatch, RequireCombo } from '../../../helpers/function.share';
import {ChannelService} from "../../../services/management/channel.service";
import {Channel} from "../../../models/Channel.model";

@Component({
    templateUrl: 'create.edit.channel.component.html'
})
export class CEChannelComponent implements OnInit {
    channel: Channel;
    sub: any;
    id: any;
    addChannelForm: FormGroup;
    submitted = false;
    editChannelForm: FormGroup;
    status = [
        {
            name: 'Hoạt động',
            value: 1
        },
        {
            name: 'Ngừng hoạt động',
            value: 0
        }
    ];

    constructor(private route: ActivatedRoute, private channelService: ChannelService, private router: Router, private toastrService: ToastrService, private fb: FormBuilder) {
        this.createForm();
    }

    ngOnInit(): void {
        // this.sub = this.route
        //     .data
        //     .subscribe(v => console.log(v));
        this.id = this.route.snapshot.params['id'];
        if (this.id > 0) {
            this.channelService.getById(this.id).subscribe((res: Channel) => {
                this.channel = res;
                // console.log("current user: ", this.user);
                this.createEditForm();
            })
        } else {
            this.channel = new Channel();
        }
    }

    createForm() {
        this.addChannelForm = this.fb.group({
            code: ['', [Validators.required, Validators.maxLength(10)]],
            name: ['', [Validators.required, Validators.maxLength(50)]],
            status: [1, Validators.required]
        });
    }

    createEditForm() {
        this.editChannelForm = this.fb.group({
            eCode: [{value: this.channel.code, disabled: true}, Validators.required],
            eName: [this.channel.name, Validators.required],
            eStatus: [this.channel.status, Validators.required]
        });
    }

    saveChannel(channel: Channel) {
        if (this.id > 0) {
            this.channelService.update(channel).subscribe(res => {
                this.showSuccess('Cập nhật thành công');
                this.router.navigate(['/channel-management']);
            }, (err) => {
                this.showError('Cập nhật channel không thành công!');
                console.log(err);
            });
        } else {
            this.channelService.add(channel).subscribe(res => {
                this.showSuccess('Thêm mới thành công');
                this.router.navigate(['/channel-management']);
            }, (err) => {
                this.showError('Thêm mới channel không thành công!');
                console.log(err);
            });
        }
    }

    get addForm() { return this.addChannelForm.controls; }

    get editForm() { return this.editChannelForm.controls; }

    onAddSubmit() {
        this.submitted = true;
        // stop here if form is invalid
        if (this.addChannelForm.invalid) {
            return;
        }
        this.channel.code = this.addChannelForm.value.code;
        this.channel.name = this.addChannelForm.value.name;
        this.saveChannel(this.channel);
    }

    onEditSubmit() {
        if (this.editChannelForm.invalid) {
            return;
        }

        this.channel.code = this.editChannelForm.value.eCode;
        // this.user.activeFlg = this.editChannelForm.value.eStatus;
        this.channel.name = this.editChannelForm.value.eName;
        this.channel.status = this.editChannelForm.value.eStatus;
        this.saveChannel(this.channel);
    }

    showSuccess(mes: string) {
        // this.toastrService.success('', mes, {
        //     timeOut: config.timeoutToast
        // });
    }

    showError(mes: string) {
        // this.toastrService.error('', mes, {
        //     timeOut: config.timeoutToast
        // });
    }
}
