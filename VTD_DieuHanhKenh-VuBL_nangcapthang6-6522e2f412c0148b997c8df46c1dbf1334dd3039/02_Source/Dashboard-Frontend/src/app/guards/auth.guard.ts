import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { config } from '../config/application.config';
import { OauthService } from '../services/oauth.service';

@Injectable()
export class AuthGuard implements CanActivate {

  constructor(private router: Router, private oauthService: OauthService) {}

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {
        return this.checkLogin();
  }

  checkLogin(): boolean{
      let isLogin = localStorage.getItem(config.session);
      if(isLogin){
          return true;
      }
    //   this.oauthService.redirectUrl = url;
      this.router.navigate(['/login']);
      return false;
  }
}
