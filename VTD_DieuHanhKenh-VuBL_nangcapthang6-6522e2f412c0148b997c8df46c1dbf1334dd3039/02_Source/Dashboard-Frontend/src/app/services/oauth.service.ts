import { Injectable } from '@angular/core';
import { Http, RequestOptions, Headers, Response } from '@angular/http';
import { HttpClient, HttpHeaders, HttpParams, HttpResponse } from '@angular/common/http';
import { config } from '../config/application.config';
import { Router } from '@angular/router';
import { map } from 'rxjs/operators';
import { Observable, BehaviorSubject } from 'rxjs';
import { catchError, retry } from 'rxjs/operators';
import { User } from '../models/User.model';

@Injectable({
    providedIn: 'root'
})
export class OauthService {
    private currentUserSubject: BehaviorSubject<User>;
    public currentUser: Observable<User>;
    redirectUrl: string;
    private url = config.apiLogin;

    constructor(private router: Router, private http: HttpClient) {
        this.currentUserSubject = new BehaviorSubject<User>(JSON.parse(localStorage.getItem('currentUser')));
        this.currentUser = this.currentUserSubject.asObservable();
    }

    public get currentUserValue(): User {
        return this.currentUserSubject.value;
    }

    login(username: string, password: string) {

        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type': config.contentType,
                'Authorization': config.requestAuthorization
            })
        };

        let creds = 'username=' + username + '&password=' + password + '&grant_type=password';
        return this.http.post<any>(this.url, creds, httpOptions).pipe(map(token => {
            localStorage.setItem(config.session, JSON.stringify(token));
            if (token && token.access_token) {
                var mUser: User = new User();
                mUser.username = token.userInfo.userName;
                mUser.role = token.authorities;
                mUser.token = token.access_token;
                mUser.defaulLink = token.userInfo.defaultLink;
                // store user details and jwt token in local storage to keep user logged in between page refreshes
                localStorage.setItem(config.currentUser, JSON.stringify(mUser));
                localStorage.setItem(config.access_token, token.access_token);
                localStorage.setItem(config.refresh_token, token.refresh_token);
                localStorage.setItem(config.token_expired_time, '' + token.expires_in * 1000);
                localStorage.setItem(config.token_get_time, '' + Date.now());
                this.currentUserSubject.next(mUser);
            }
            return mUser;
        }));
    }

    logout() {
        localStorage.removeItem(config.currentUser);
        localStorage.removeItem(config.session);
        this.currentUserSubject.next(null);
        this.router.navigate(['/login']);
    }

  public getToken(): string {
    return localStorage.getItem(config.access_token);
  }
  public tokenExpired() {
    let tokenExpiredTime : number = Number.parseInt(localStorage.getItem(config.token_expired_time), 0);
    let tokenGetTime : number = Number.parseInt(localStorage.getItem(config.token_get_time), 0);
    if (tokenExpiredTime && tokenGetTime) {
      return (tokenGetTime + tokenExpiredTime) < Date.now();
    } else {
      return true;
    }

  }


  get accessToken(): any {
    return localStorage.getItem(config.access_token);
  }
  set accessToken(value) {
    localStorage.setItem(config.access_token, value);
  }
  get refreshToken(): any {
    return localStorage.getItem(config.refresh_token);
  }

  set refreshToken(value) {
    localStorage.setItem(config.refresh_token, value);
  }

  public doRefreshToken() {

    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': config.contentType,
        'Authorization': config.requestAuthorization,
        'Access-Control-Allow-Origin' : '*'
      })
    };

    let creds = 'refresh_token=' + this.refreshToken + '&grant_type=refresh_token';
    return this.http.put(config.apiLogin, creds, httpOptions);
  }

  public isUserAuthenticated() {
    return !this.tokenExpired();
  }
}
