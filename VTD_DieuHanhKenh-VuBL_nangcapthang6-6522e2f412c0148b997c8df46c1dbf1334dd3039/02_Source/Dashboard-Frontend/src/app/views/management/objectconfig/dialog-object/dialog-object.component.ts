import { Component, OnInit, Inject, TemplateRef, AfterViewChecked, ChangeDetectorRef } from '@angular/core';
import { Validators, FormBuilder, FormGroup, FormControl } from '@angular/forms';
import { TranslateService } from '@ngx-translate/core';
import { ConfigobjectService } from '../../../../services/management/configobject.service';
import { ToastrService } from 'ngx-toastr';
import { ObjectConfig, ObjectConfigModel } from '../../../../models/objectconfig';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material';
import { BsModalService, BsModalRef } from 'ngx-bootstrap';
import { TreeviewItem, TreeItem } from 'ngx-treeview';
import { HttpHeaders, HttpClient } from '@angular/common/http';
import { config } from '../../../../config/application.config';
import { DomSanitizer } from '@angular/platform-browser';

@Component({
  selector: 'app-dialog-object',
  templateUrl: './dialog-object.component.html',
  styleUrls: ['./dialog-object.component.scss']
})
export class DialogObjectComponent implements OnInit, AfterViewChecked {
  form;
  mobjNewObject: ObjectConfig = new ObjectConfig();
  parent: any;
  marrObject: ObjectConfig[] = [];
  checkfunction = -1;
  checkobject = -1;
  vstrcheckOrd = '0';
  vstrCheckCode = '0';
  vblnUnlock = false;
  vblnLock = false;
  trimname = null;
  trimkey = null;
  trimurl = null;
  trimicon = null;
  trimimage = null;
  ord = '';
  myord: number;
  child: ObjectConfigModel[] = [];
  mobjModalRef: BsModalRef;
  mobjNodeItemService;
  marrNodeTreeviewServices;
  marrIndexNode;
  marrParent;
  marrItemServices;
  mobjNodeTreeviewService;
  marrNodeItemServices;
  mobjItemService;
  checkParent = 2;
  marrData;
  parents;
  image = '/abcc';
  added = false;
  urlempty = true;
  mobjFileList: FileList;
  mobjConfig = {
    hasFilter: true,
    hasCollapseExpand: true,
    maxHeight: 230,
  };
  value;
  checkSelectParent;
  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    private configobjectService: ConfigobjectService,
    private toastr: ToastrService,
    private fb: FormBuilder,
    private http: HttpClient,
    private cdRef: ChangeDetectorRef,
    private translate: TranslateService,
    private dialogRef: MatDialogRef<DialogObjectComponent>,
    private modalService: BsModalService,
    private domSanitizer: DomSanitizer
  ) {
    dialogRef.disableClose = true;
  }
  url: any;
  ngOnInit() {
    this.marrData = this.data.marr.filter(item => item.status == 1);
    this.parents = this.data.marr;
    this.value = this.data.obj.parentId;
    this.getAllMenu();
    this.getAllObject();
    this.mobjNewObject = this.data.obj;
    this.parent = this.data.parent.children[0];
    this.createForm();
    this.getChildren();
    this.checkUrlSpace();
    this.checkiconSpace();
    if (this.mobjNewObject.objectImg && this.mobjNewObject.objectImg.length > 0) {
      this.configobjectService.getImage(this.mobjNewObject.objectImg).subscribe(res => {
        if (res.data != null) {
          this.url = this.domSanitizer.bypassSecurityTrustResourceUrl('data:image/jpg;base64,' + res.data);
        }
      });
    }
  }


  ngAfterViewChecked(): void {
    if (this.form.get('code').value) {
      this.mobjNewObject.objectCode = this.form.get('code').value.toUpperCase();
    } else {
      this.mobjNewObject.objectCode = this.form.get('code').value;
    }
    this.cdRef.detectChanges();
  }


  onValueChange(value: number) {
    this.mobjNewObject.parentId = value;
    this.configobjectService.checkSelectParent(this.mobjNewObject.id, value).subscribe(res => {
      this.checkSelectParent = res.data;
    });
    this.getChildren();
  }

  getAllMenu() {
    this.marrIndexNode = [];
    this.marrNodeTreeviewServices = [];
    this.marrData.forEach((arr: ObjectConfigModel) => {
      if (arr.parentId === null) {
        arr.parentId = -1;
      }
    });
    this.marrItemServices = this.createNode(
      this.marrItemServices,
      this.marrData
    );
    this.createTree(this.marrItemServices, this.marrData);
    this.marrItemServices.forEach(vobjValue => {
      this.mobjNodeTreeviewService = new TreeviewItem(
        this.forwardData(
          vobjValue,
          this.mobjNodeItemService,
          this.marrNodeItemServices
        )
      );
      this.marrNodeTreeviewServices.push(this.mobjNodeTreeviewService);
    });
  }

  createNode(parrItems: ObjectConfigModel[] = [], parrDataTree: ObjectConfigModel[]) {
    parrItems = parrDataTree.map(vobjValue => {
      this.mobjItemService = vobjValue;
      this.mobjItemService.children = [];
      return this.mobjItemService;
    });
    return parrItems;
  }

  /**
   * Tạo tree theo Object ServiceModel
   *
   * @author: TruyenNH
   * @version: 1.0
   * @since: 2019/09/13
   * @param: parrNodeTrees - Danh sách Object mới sau khi xử lý
   * @param: parrDataTree - Danh sách Object chưa xử lý
   */
  createTree(parrNodeTrees: ObjectConfigModel[], parrDataTree: ObjectConfigModel[]) {
    const vnbrLen = parrNodeTrees.length;
    for (let i = 0; i < vnbrLen; i++) {
      for (let j = 0; j < parrDataTree.length; j++) {
        if (parrNodeTrees[i].id === parrDataTree[j].parentId) {
          parrNodeTrees[i].children.push(parrDataTree[j]);
          this.marrIndexNode.push(j);
        }
      }
    }
    // console.log('arrrrr: ', this.marrIndexNode);
    const vnbrC = (pnbrA: number, pnbrB: number) => pnbrA - pnbrB;
    this.marrIndexNode.sort(vnbrC);
    for (let vnbrI = this.marrIndexNode.length - 1; vnbrI >= 0; vnbrI--) {
      parrNodeTrees.splice(this.marrIndexNode[vnbrI], 1);
    }
    // console.log('arrrrr222: ', this.marrIndexNode);
  }

  /**
   * Chuyển đổi tree: ServiceModel sang TreeViewItem
   *
   * @author: TruyenNH
   * @version: 1.0
   * @since: 2019/09/13
   * @param: pobjNodeTree - Object tham chiếu hiện tại
   * @param: pobjitem - Object TreeItem
   * @param: parrItems - Danh sách Object TreeViewItem
   */
  forwardData(
    pobjNodeTree: ObjectConfigModel,
    pobjitem: TreeItem,
    parrItems: TreeItem[] = []
  ) {
    pobjitem = null;
    parrItems = [];
    if (pobjNodeTree.children) {
      pobjNodeTree.children.sort((left, right) => {
        if (left.ord < right.ord) { return -1; }
        if (left.ord > right.ord) { return 1; }
        return 0;
      });
      pobjNodeTree.children.forEach(value => {
        this.mobjNodeItemService = this.forwardData(value, null, []);
        parrItems.push(this.mobjNodeItemService);
      });
    }
    if (pobjNodeTree.children) {
      pobjitem = {
        value: pobjNodeTree.id,
        text: pobjNodeTree.objectName + '',
        children: parrItems,
        checked: false,
        disabled: true
      };
    } else {
      parrItems = [];
      pobjitem = {
        value: pobjNodeTree.id,
        text: pobjNodeTree.objectName + '',
        children: null,
        checked: false,
        disabled: true
      };
    }
    return pobjitem;
  }


  getChildren() {
    this.configobjectService.getAllByParentId(this.mobjNewObject.parentId).subscribe(res => {
      this.child = res.data.filter(item => item.id != this.mobjNewObject.id);
      this.checkOrd();
    });
  }

  getAllObject() {
    this.configobjectService.getAllNotDelete().subscribe(res => {
      this.marrObject = [];
      this.marrObject = res.data;
    });
  }


  createForm() {
    if (this.mobjNewObject.id != -1) {
      this.ord = this.mobjNewObject.ord + '';
      this.myord = this.mobjNewObject.ord;
      this.checkfunction = 0;
      this.checkobject = 0;
      this.vstrCheckCode = '0';
    } else {
      this.mobjNewObject = new ObjectConfig();
      this.mobjNewObject.id = -1;
      this.mobjNewObject.status = 1;
      this.mobjNewObject.objectType = '-1';
      this.mobjNewObject.functionType = -1;
      this.checkfunction = -1;
      this.checkobject = -1;
      this.mobjNewObject.parentId = this.data.parent.children[0].id;
    }
    if (this.mobjNewObject.id != -1) {
      if (this.mobjNewObject.objectType == '0') {
        this.checkObj();
      } else {
        this.form = this.fb.group(
          {
            code: [{ value: '', disabled: true }],
            name: ['', Validators.required],
            key: [''],
            url: ['', Validators.required],
            icon: ['', Validators.required],
            parent: [this.mobjNewObject.parentId],
            ord: ['', Validators.required]
          });
      }
    } else {
      this.mobjNewObject.ord = null;
      this.form = this.fb.group(
        {
          code: ['', [Validators.required, Validators.pattern('^\\s{0,50}?[_A-Za-z0-9]{1,50}\\s{0,50}$')]],
          name: ['', Validators.required],
          key: [''],
          url: ['', Validators.required],
          icon: ['', Validators.required],
          parent: [this.mobjNewObject.parentId],
          ord: ['', Validators.required]
        });
    }
  }

  input() {
    this.added = false;
    this.checkOrd();
  }

  checkOrd() {
    if (this.added == false) {
      if (this.ord.indexOf(' ') !== -1) {
        this.vstrcheckOrd = '1';
      } else if (this.ord === '-') {
        this.vstrcheckOrd = '1';
      } else if (isNaN(Number(this.ord))) {
        if (this.ord !== '') {
          this.vstrcheckOrd = '1';
        }
      } else {
        if (this.ord !== '') {
          this.ord = (Number(this.ord) + 0) + '';
          if (this.ord.length < 15) {
            this.mobjNewObject.ord = parseInt(this.ord);
            if (this.mobjNewObject.ord <= 0) {
              this.vstrcheckOrd = '2';
            } else {
              for (let i = 0; i < this.child.length; i++) {
                this.vstrcheckOrd = '0';
                if (this.child[i].ord == this.mobjNewObject.ord) {
                  this.vstrcheckOrd = '3';
                  break;
                }
              }
            }
          } else {
            this.vstrcheckOrd = '1';
          }
        } else {
          this.vstrcheckOrd = '0';
        }
      }
    }
  }

  checkCode() {
    const marr = this.marrObject.filter(item => item.id !== this.mobjNewObject.id);
    if (this.mobjNewObject.objectCode && this.mobjNewObject.objectCode.trim().length > 0) {
      this.mobjNewObject.objectCode = this.mobjNewObject.objectCode.toUpperCase();
      const mobjObject = marr.filter(item => item.objectCode.toUpperCase() === this.mobjNewObject.objectCode.trim())[0];
      if (mobjObject) {
        this.vstrCheckCode = '1';
      } else {
        this.vstrCheckCode = '0';
      }
    } else {
      this.vstrCheckCode = '0';
    }
  }

  hasError(controlName: string, errorName: string) {
    return this.form.controls[controlName].hasError(errorName);
  }

  checkNameSpace() {
    if (this.mobjNewObject.objectName && this.mobjNewObject.objectName != '') {
      if (this.mobjNewObject.objectName.trim().length == 0) {
        this.trimname = false;
      } else {
        this.trimname = true;
      }
    } else {
      this.trimname = true;
    }
  }

  checkUrlSpace() {
    this.urlempty = true;
    if (this.mobjNewObject.objectUrl && this.mobjNewObject.objectUrl != '') {
      if (this.mobjNewObject.objectUrl.trim().length == 0) {
        this.trimurl = false;
      } else {
        this.trimurl = true;
      }
    } else {
      this.trimurl = true;
    }
  }

  checkiconSpace() {
    if (this.mobjNewObject.objectIcon && this.mobjNewObject.objectIcon != '') {
      if (this.mobjNewObject.objectIcon.trim().length == 0) {
        this.trimicon = false;
      } else {
        this.trimicon = true;
      }
    } else {
      this.trimicon = true;
    }
  }

  back() {
    this.mobjModalRef.hide();
  }
  close() {
    this.dialogRef.close();
  }

  openConfirm(pobjTemplate: TemplateRef<any>) {
    this.mobjModalRef = this.modalService.show(pobjTemplate, {
      ignoreBackdropClick: true
    }
    );
  }

  save() {
    this.mobjNewObject.objectIcon = this.mobjNewObject.objectIcon.trim();
    if (this.mobjNewObject.objectUrl) {
      this.mobjNewObject.objectUrl = this.mobjNewObject.objectUrl.trim();
      if (this.mobjNewObject.objectUrl.length == 0) {
        this.mobjNewObject.objectUrl = null;
      }
    }
    this.mobjNewObject.objectCode = this.mobjNewObject.objectCode.trim();
    this.mobjNewObject.objectName = this.mobjNewObject.objectName.trim();
    if (this.mobjNewObject.objectNameI18N != null) {
      if (this.mobjNewObject.objectNameI18N.trim() == '') {
        this.mobjNewObject.objectNameI18N = null;
      } else {
        this.mobjNewObject.objectNameI18N = this.mobjNewObject.objectNameI18N.trim();
      }
    }
    if (this.mobjNewObject.parentId === -1) {
      this.mobjNewObject.parentId = null;
    }
    const list: ObjectConfig[] = [];
    list.push(this.mobjNewObject);
    if (this.mobjNewObject.id !== -1) {
      this.upLoad('01', this.mobjNewObject.id, list);
    } else {
      this.upLoad('00', this.mobjNewObject.id, list);
      this.mobjFileList = null;
      this.value = this.data.obj.parentId;
      this.getAllMenu();
      this.getAllObject();
      this.mobjNewObject = this.data.obj;
      this.parent = this.data.parent.children[0];
      this.added = true;
      this.createForm();
      this.getChildren();
      this.url = null;
    }
  }

  check() {
    if (this.mobjNewObject.functionType === -1) {
      this.checkfunction = 1;
    } else {
      this.checkfunction = 0;
    }
  }

  checkObjEmp() {
    if (this.mobjNewObject.objectType == '1') {
      if (this.mobjNewObject.objectUrl && this.mobjNewObject.objectUrl.length > 0) {
        this.checkUrlSpace();
        this.urlempty = true;
      } else {
        this.urlempty = false;
      }
    } else {
      this.urlempty = true;
    }
    this.checkObj();
  }

  checkObj() {
    if (this.mobjNewObject.objectType === '-1') {
      this.checkobject = 1;
    } else {
      this.checkobject = 0;
    }
    let disable = true;
    if (this.mobjNewObject.id != -1) {
      disable = true;
    } else {
      disable = false;
    }
    if (this.mobjNewObject.objectType != '0') {
      this.form = this.fb.group(
        {
          code: [{ value: this.mobjNewObject.objectCode, disabled: disable }, [Validators.required, Validators.pattern('^\\s{0,50}?[_A-Za-z0-9]{1,50}\\s{0,50}$')]],
          name: [this.mobjNewObject.objectName, Validators.required],
          key: [this.mobjNewObject.objectNameI18N],
          url: [this.mobjNewObject.objectUrl, Validators.required],
          icon: [this.mobjNewObject.objectIcon, Validators.required],
          parent: [this.mobjNewObject.parentId],
          ord: [this.mobjNewObject.ord, Validators.required]
        });
    } else {
      this.form = this.fb.group(
        {
          code: [{ value: this.mobjNewObject.objectCode, disabled: disable }, [Validators.required, Validators.pattern('^\\s{0,50}?[_A-Za-z0-9]{1,50}\\s{0,50}$')]],
          name: [this.mobjNewObject.objectName, Validators.required],
          key: [this.mobjNewObject.objectNameI18N],
          url: [this.mobjNewObject.objectUrl],
          icon: [this.mobjNewObject.objectIcon, Validators.required],
          parent: [this.mobjNewObject.parentId],
          ord: [this.mobjNewObject.ord, Validators.required]
        });
    }
  }

  /**
 * Thông báo thành công
 *
 * @author: CuongDT
 * @version: 1.0
 * @since: 2019/11/18
 */
  showSuccess(message: string) {
    this.toastr.success(message,
      this.translate.instant('management.warningconfig.success'), {
      timeOut: 2000,
      positionClass: 'toast-top-center',
    });
  }

  /**
  * Thông báo thất bại(Lỗi)
  *
  * @author: CuongDT
  * @version: 1.0
  * @since: 2019/11/18
  */
  showError(message: string) {
    this.toastr.error(message,
      this.translate.instant('management.warningconfig.fail'), {
      timeOut: 2000,
      positionClass: 'toast-top-center',
    });
  }
  selectFile(pobjEvent) {
    if (pobjEvent.target.files && pobjEvent.target.files.length > 0) {
      this.mobjFileList = pobjEvent.target.files;
      const vfileFile: File = this.mobjFileList[0];
      const reader = new FileReader();
      reader.readAsDataURL(this.mobjFileList[0]);
      reader.onload = _event => {
        this.url = reader.result;
      };
      this.mobjNewObject.objectImg = vfileFile.name;
    }
  }

  deleteImage() {
    this.url = null;
    this.mobjNewObject.objectImg = null;
  }

  upLoad(type: String, id: number, list: any) {
    if (this.mobjFileList && this.mobjFileList.length > 0) {
      const vfileFile: File = this.mobjFileList[0];
      const actionid = '17';
      const vobjFormData: FormData = new FormData();
      vobjFormData.append('file', vfileFile);
      vobjFormData.append('actionId', actionid);
      const vobjHeaders = new HttpHeaders();
      vobjHeaders.append('Content-type', 'multipart/form-data');
      vobjHeaders.append('Accept', 'application/json');
      const options = { headers: vobjHeaders };
      let code = list[0].objectCode;
      this.http.post(`${config.apiUrl}/management/objectconfig/upload/${code}/${id}`, vobjFormData, options)
        .subscribe((respon: any) => {
          if (respon.code === 200) {
            list[0].objectImg = respon.data;
            this.configobjectService.saveObject(list).subscribe(res => {
              if (res.code === 200) {
                if (this.mobjNewObject.id !== -1) {
                  this.dialogRef.close();
                } else {
                  this.mobjFileList = null;
                  this.value = this.data.obj.parentId;
                  this.getAllMenu();
                  this.getAllObject();
                  this.mobjNewObject = this.data.obj;
                  this.parent = this.data.parent.children[0];
                  this.added = true;
                  this.createForm();
                  this.getChildren();
                  this.url = null;
                }
                if (type == '00') {
                  this.showSuccess(this.translate.instant('management.warningconfig.addsuccess'));
                } else {
                  this.showSuccess(this.translate.instant('management.warningconfig.updatesuccess'));
                }
                this.dialogRef.close();
              } else {
                this.showError(res.errors);
              }
            });
          } else {
            this.showError(respon.errors);
          }
          this.back();
        });
    } else {
      this.configobjectService.saveObject(list).subscribe(res => {
        if (res.code === 200) {
          if (this.mobjNewObject.id !== -1) {
            this.dialogRef.close();
          } else {
            this.mobjFileList = null;
            this.value = this.data.obj.parentId;
            this.getAllMenu();
            this.getAllObject();
            this.mobjNewObject = this.data.obj;
            this.parent = this.data.parent.children[0];
            this.added = true;
            this.createForm();
            this.getChildren();
            this.url = null;
          }
          if (type == '00') {
            this.showSuccess(this.translate.instant('management.warningconfig.addsuccess'));
          } else {
            this.showSuccess(this.translate.instant('management.warningconfig.updatesuccess'));
          }
          this.dialogRef.close();
        } else {
          this.showError(res.errors);
        }
        this.back();
      });
    }
  }
}
