import {Component, HostListener, OnInit} from '@angular/core';
import {Event, Router, NavigationEnd, NavigationCancel, NavigationStart, NavigationError} from '@angular/router';
import {TranslateService} from '@ngx-translate/core';
import {config} from './config/application.config';
import {ResizedEvent} from 'angular-resize-event';

@Component({
  // tslint:disable-next-line
  selector: 'body',
  templateUrl: 'app.component.html'
})
export class AppComponent implements OnInit {
  loading = false;

  constructor(private router: Router, private translate: TranslateService) {
    let lang = localStorage.getItem(config.user_default_language);
    if (lang == undefined) {
      lang = config.defaultLanguage;
    }
    translate.setDefaultLang(lang);
    translate.use(lang);
  }

  ngOnInit() {
    this.router.events.subscribe((event: Event) => {
      switch (true) {
        case event instanceof NavigationStart: {
          this.loading = true;
          // console.log(this.loading,'hayahay');
          break;
        }
        case event instanceof NavigationEnd:
        case event instanceof NavigationCancel:
        case event instanceof NavigationError: {
          setTimeout(() => {
            this.loading = false;
          }, 1000);
          // console.log('load', this.loading);
          break;
        }
        default: {
          break;
        }
      }
    });
    // this.router.events.subscribe((evt) => {
    //   if (!(evt instanceof NavigationEnd)) {
    //     return;
    //   }
    //   window.scrollTo(0, 0);
    // });

  }

}
