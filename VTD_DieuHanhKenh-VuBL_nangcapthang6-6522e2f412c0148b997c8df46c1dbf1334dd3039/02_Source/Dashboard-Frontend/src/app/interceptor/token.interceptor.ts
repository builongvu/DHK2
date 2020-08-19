import {Injectable, Injector} from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor
} from '@angular/common/http';
import {OauthService} from '../services/oauth.service';
import {Router} from '@angular/router';
import {config} from '../config/application.config';
import {Observable} from 'rxjs';
import {TranslateService} from '@ngx-translate/core';

@Injectable()
export class TokenInterceptor implements HttpInterceptor {
  constructor(public auth: OauthService, private router: Router, public injector: Injector) {
  }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

    let requestOption: any = {};
    let access_token = this.auth.getToken();
    // if(this.auth.tokenExpired())
    // {
    //     this.auth.refreshToken();
    // }
    if (request.url.match(/oauth\/token/)) {
      return next.handle(request);
    }
    if (access_token) {
      let translate = this.injector.get(TranslateService);
      let lang = '';
      if (translate != undefined) {
        lang = translate.currentLang;
      }
      lang = localStorage.getItem(config.user_default_language);
      if (lang == undefined) {
        lang = config.defaultLanguage;
      }
      if (lang == undefined) {
        lang = '';
      }
      requestOption.setHeaders = {
        Authorization: 'Bearer ' + access_token,
        'Access-Control-Allow-Origin': '*',
        'Accept-Language': lang,
        'url': localStorage.getItem('url')
      };
    }

    request = request.clone(requestOption);
    return next.handle(request);
  }
}
