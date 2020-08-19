import {DataService} from './../../services/data.service';
import {Component, OnDestroy, Inject, OnInit, HostListener, AfterViewInit, ViewChild, ChangeDetectorRef, ElementRef} from '@angular/core';
import {DOCUMENT} from '@angular/common';
import {OauthService} from '../../services/oauth.service';
import {MenuService} from '../../services/management/menu.service';
import {config} from '../../config/application.config';
import {User} from '../../models/User.model';
import {NavData} from '../../_nav';
import {map, takeUntil} from 'rxjs/operators';
import {MediaMatcher} from '@angular/cdk/layout';
import {ChannelService} from '../../services/management/channel.service';
import {GroupsService} from '../../services/management/group.service';
import {DataLayoutService} from '../../services/management/data-layout.service';
import {ActivatedRoute, NavigationEnd, Router, RouterEvent} from '@angular/router';
import {filter} from 'rxjs/operators';
import {FlatTreeControl} from '@angular/cdk/tree';
import {HttpClient} from '@angular/common/http';
import {MatTreeFlatDataSource, MatTreeFlattener} from '@angular/material';
import {animate, style, transition, trigger} from '@angular/animations';
import {Subject, Subscription} from 'rxjs';
import {DomSanitizer} from '@angular/platform-browser';

// -----------------------------------------
interface MenuItemNode {
  icon: string;
  img: string;
  name: string;
  objectType: string;
  routerLink?: string;
  children?: MenuItemNode[];
}


interface FlatNode {
  expandable: boolean;
  name: string;
  icon: string;
  img: string;
  routerLink?: string;
  level: number;
}

// -----------------------------------------
// @ts-ignore
@Component({
  selector: 'app-dashboard',
  templateUrl: './default-layout.component.html',
  styleUrls: ['./ui.scss'],
  providers: [DataService],
  animations: [
    trigger('slideInOut', [
      transition(':enter', [
        style({transform: 'translateX(-100%)'}),
        animate('700ms ease-in-out', style({transform: 'translateX(0%)'}))
      ]),
      transition(':leave', [
        animate('700ms ease-in-out', style({transform: 'translateX(-230px)'}))
      ])
    ])
  ]
})
export class DefaultLayoutComponent implements OnDestroy, OnInit, AfterViewInit {
  // -----------------------------------------
  treeIndent = 15;
  treeControl: FlatTreeControl<FlatNode>;
  treeFlattener;
  dataSource;
  menuItems: MenuItemNode[];
  menuItemNs: MenuItemNode[];
  menuItemN: MenuItemNode;
  // -----------------------------------------
  public username = '';
  data = [];
  navItems: NavData[] = [];
  navItem: NavData;
  public NavItems;
  public sidebarMinimized = true;
  private changes: MutationObserver;
  public element: HTMLElement;
  public defaultLink: string;
  @ViewChild('drawer') snav;
  // @ViewChild('body') myBody: ElementRef<HTMLElement>;
  mobileQuery: MediaQueryList;
  isExpanded: boolean = true;
  message: boolean;

  constructor(private data1: DataService,
              private oauthService: OauthService,
              private http: HttpClient,
              private changeDetectorRef: ChangeDetectorRef,
              private media: MediaMatcher,
              private menuService: MenuService,
              private groupsService: GroupsService,
              private channelService: ChannelService,
              private dataLayoutService: DataLayoutService,
              private route: ActivatedRoute,
              private router: Router,
              private sanitizer: DomSanitizer,
              @Inject(DOCUMENT) _document?: any) {
    this.mobileQuery = media.matchMedia('(max-width: 600px)');
    this._mobileQueryListener = () => changeDetectorRef.detectChanges();
    this.mobileQuery.addListener(this._mobileQueryListener);

    this.changes = new MutationObserver((mutations) => {
      this.sidebarMinimized = _document.body.classList.contains('sidebar-minimized');
    });
    this.element = _document.body;
    this.changes.observe(<Element>this.element, {
      attributes: true,
      attributeFilter: ['class']
    });
  }

  private _mobileQueryListener: () => void;

  breadcum;
  items: string[] = [];
  sub;
  urlmenu;
  groupId;
  isDashboard = false;
  visible = true;
  unsubscribe$ = new Subject();

  ngOnInit() {
    this.data1.currentMessage
      .pipe(takeUntil(this.unsubscribe$))
      .subscribe(message => this.message = message);

    this.treeControl = new FlatTreeControl<FlatNode>(node => node.level, node => node.expandable);
    this.treeFlattener = new MatTreeFlattener(this.transformer,
      node => node.level,
      node => node.expandable,
      node => node.children
    );
    this.dataSource = new MatTreeFlatDataSource(this.treeControl, this.treeFlattener);
    const mUser: User = JSON.parse(localStorage.getItem(config.currentUser));
    this.defaultLink = mUser.defaulLink;
    this.username = mUser.username;
    this.menuService.checkRole(mUser.token).subscribe(
      dataMenu => {
        this.menuItems = dataMenu.data;
        this.menuItemNs = this.convertMenu(this.menuItemNs, this.menuItemN, dataMenu.data);
        this.dataSource.data = this.menuItemNs;
        this.getUrlName();
        this.treeControl.expandAll();
      });

    this.urlmenu = this.router.routerState.snapshot.url;
    this.groupId = this.router.routerState.snapshot['groupId'];

    this.router.events.pipe(
      filter(e => e instanceof RouterEvent)
    ).subscribe(e => {
        this.urlmenu = this.router.routerState.snapshot.url;
        this.items = [];
        this.getUrlName();
      }
    );
    this.getUrl();
  }

  getUrl() {
    this.router.events.pipe(
      filter(e => e instanceof RouterEvent)
    ).subscribe(e => {
        localStorage.setItem('url', this.router.routerState.snapshot.url);
      }
    );
  }

  convertMenu(menuItems: MenuItemNode[] = [], menuItem: MenuItemNode, data: any[]) {
    menuItems = data.map(val => {
      menuItem = {
        children: val['childObjects'] == '' ? null : val['childObjects'],
        name: val['objectName'],
        objectType: val['objectType'],
        routerLink: val['objectUrl'],
        icon: val['objectIcon'],
        img: val['objectImg']
      };
      if (menuItem.children) {
        menuItem.children = this.convertMenu(menuItems, null, menuItem.children);
      }
      return menuItem;
    });
    return menuItems;
  }

  hasChild(_: number, node: FlatNode) {
    return node.expandable;
  }

  transformer(node: MenuItemNode, level: number): FlatNode {
    return {
      expandable: !!node.children && node.children.length > 0,
      name: node.name,
      icon: node.icon,
      img: node.img,
      routerLink: node.routerLink,
      level,
    };
  }

  getUrlName() {

    if (this.urlmenu.substring(0, 5) != '/dash') {
      this.isDashboard = true;
      this.items.push(this.menuItemNs[1]['name']);
      for (let i = 1; i < this.menuItemNs.length; i++) {
        this.menuItemNs[i]['children'].forEach(
          child => {
            if (this.urlmenu == child['routerLink']) {
              this.items.push(child['name']);
            }
            if (child['objectType'] == '0') {
              child['children'].forEach(children => {
                if (this.urlmenu == children['routerLink']) {
                  this.items.push(child['name']);
                  this.items.push(children['name']);
                }
              });
            }
          }
        );
      }
    } else {
      this.isDashboard = false;
      // this.items.push(this.data[0]['objectName']);
      // this.data[0]['childObjects'].forEach(
      // child => {
      // if (this.urlmenu == child['objectUrl']) {
      // this.items.push(child['objectName']);
      // }
      // }
      // );
    }

  }

  buildMenu(navItems: NavData[] = [], navItem: NavData, data: NavData[]) {
    navItems = data.map(val => {
      navItem = {
        children: val['childObjects'] == '' ? null : val['childObjects'],
        name: val['objectName'],
        url: val['objectUrl'],
        icon: val['objectIcon'],
        img: val['objectImg']
      };
      if (navItem.children) {
        navItem.children = this.buildMenu(navItems, null, navItem.children);
      }
      return navItem;
    });
    return navItems;
  }

  ngOnDestroy(): void {
    this.mobileQuery.removeListener(this._mobileQueryListener);
    this.changes.disconnect();
    this.unsubscribe$.next();
    this.unsubscribe$.complete();
  }

  openSideNav() {
    if (this.mobileQuery.matches) {
      this.isExpanded = true;
      this.snav.opened = true;
    }
  }


  logout() {
    this.oauthService.logout();
  }

  ngAfterViewInit(): void {
    // setTimeout(() => {
    // const a = document.getElementsByClassName('nav-dropdown-items');
    //
    // // const a = document.getElementsByClassName('nav-dropdown-items');
    //
    // for (let i = 0; i < a.length; i++) {
    // a[i].setAttribute('style', 'padding-left: .6rem; box-sizing: border-box');
    // }
    // }, 500);
  }

  toggle() {
    this.visible = !this.visible;
  }

  newMessage() {
    this.data1.changeMessage(!this.message);
  }

  // checkToggle() {
  // if(!this.visible){
  // this.visible = true;
  // }
  // }
  // checkToggleFalse(){
  // if(this.visible){
  // this.visible = false;
  // }
  // }
  onResize($event: any) {
    if ($event.target.innerWidth <= 500) {
      if (this.visible) {
        // const el = this.myBody.nativeElement;
        // el.click();
        this.visible = false;
      }
    }
  }

  getSantizeUrl(url: string) {
    return this.sanitizer.bypassSecurityTrustResourceUrl('data:image/png;base64, ' + url);
  }
}
