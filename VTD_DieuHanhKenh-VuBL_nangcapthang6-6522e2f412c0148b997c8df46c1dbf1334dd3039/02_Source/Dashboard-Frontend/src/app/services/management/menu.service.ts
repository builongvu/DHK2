import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {map} from 'rxjs/operators';
import {config} from '../../config/application.config';


@Injectable({
  providedIn: 'root'
})
export class MenuService {
  private url = config.apiUrl + '/authen/getMenu';

  constructor(private http: HttpClient) {
  }

  checkRole(access_token: string) {
    return this.http.post<any>(this.url, access_token).pipe(map(response => {
      return response;
    }));
  }

}
