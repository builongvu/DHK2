import {Injectable} from '@angular/core';


import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpResponse,
  HttpErrorResponse,
  HttpInterceptor
} from '@angular/common/http';
import {Router} from '@angular/router';
import {config} from '../config/application.config';
import {OauthService} from '../services/oauth.service';
import {Observable, throwError} from 'rxjs';
import {catchError, switchMap, tap} from 'rxjs/operators';


@Injectable()
export class ApiResponseInterceptor implements HttpInterceptor {
  isRefreshingToken = false;

  constructor(
    public auth: OauthService,
    private router: Router
  ) {
  }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

    return next.handle(request).pipe(tap(() => {
    }, (err: any) => {
      if (err instanceof HttpErrorResponse) {
        if (err instanceof HttpErrorResponse && (<HttpErrorResponse>err).status === 401) {
          return this.handle401Error(request, next);
        } else if (err instanceof HttpErrorResponse && (<HttpErrorResponse>err).status === 403) {
          return this.handle403Error(request, next);
        } else {
          return throwError(err);
        }
      }
    }));
  }

  handle401Error(req: HttpRequest<any>, next: HttpHandler) {
    if (!this.isRefreshingToken) {
      this.isRefreshingToken = true;
      return this.auth.doRefreshToken().pipe(
        switchMap(res => {
          this.auth.refreshToken = res['refresh_token'];
          this.auth.accessToken = res['access_token'];
          return next.handle(this.alterToken(req));
        })
        , catchError(error => this.logoutUser(error)))
        .subscribe(result => this.isRefreshingToken = false);
    } else {
      this.auth.logout();
    }
  }

  handle403Error(req: HttpRequest<any>, next: HttpHandler) {
    this.router.navigate(['/404']);
  }

  alterToken(req: HttpRequest<any>): HttpRequest<any> {
    return req.clone({setHeaders: {Authorization: 'Bearer ' + this.auth.accessToken}});
  }

  logoutUser(error) {
    this.auth.logout();
    return throwError(error);
  }
}
