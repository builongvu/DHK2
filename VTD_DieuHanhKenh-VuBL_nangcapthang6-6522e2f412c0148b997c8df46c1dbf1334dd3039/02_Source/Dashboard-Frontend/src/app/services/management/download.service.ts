import {HttpClient, HttpHeaders, HttpResponse} from '@angular/common/http';
import {Injectable} from '@angular/core';

import {config} from '../../config/application.config';
import {Observable} from 'rxjs';
import {DownloadModel} from '../../models/download.model';
import {SearchModel} from '../../models/Search.model';



@Injectable()
export class DownloadService {

  constructor(private http: HttpClient) {
  }

 downloadVDS(fil: SearchModel): Observable<HttpResponse<Object>> {
   let headers = new HttpHeaders();
   // headers = headers.append('Accept', '*');
   headers = headers.append('Content-Type', 'application/json');
   headers = headers.append('Access-Control-Allow-Headers', '*');

   return this.http.post(config.downloadVDS_API, fil, {
     headers: headers,
     observe: 'response',
     responseType: 'arraybuffer'
   });
 }

  downloadLevel(fil: SearchModel): Observable<HttpResponse<Object>> {
    let headers = new HttpHeaders();
    // headers = headers.append('Accept', '*');
    headers = headers.append('Content-Type', 'application/json');
    headers = headers.append('Access-Control-Allow-Headers', '*');

    return this.http.post(config.downloadLevel_API, fil, {
      headers: headers,
      observe: 'response',
      responseType: 'arraybuffer'
    });
  }

  downloadResult(nameFileResult: string): Observable<HttpResponse<Object>> {
    let headers = new HttpHeaders();
    // headers = headers.append('Accept', '*');
    headers = headers.append('Content-Type', 'application/json');
    headers = headers.append('Access-Control-Allow-Headers', '*');

    return this.http.get(`${config.download_Result_File_API}?fileName=${nameFileResult}`, {
      headers: headers,
      observe: 'response',
      responseType: 'arraybuffer'
    });
  }

  downloadExampleService(): Observable<HttpResponse<Object>> {
    let headers = new HttpHeaders();
    headers = headers.append('Content-Type', 'application/json');
    headers = headers.append('Access-Control-Allow-Headers', '*');

    return this.http.get(config.downloadService_API, {
      headers: headers,
      observe: 'response',
      responseType: 'arraybuffer'
    });
  }


  downloaGuildEXP(): Observable<HttpResponse<Object>> {
    let headers = new HttpHeaders();
    headers = headers.append('Content-Type', 'application/json');
    headers = headers.append('Access-Control-Allow-Headers', '*');

    return this.http.get(config.downloadGuildEXP_API, {
      headers: headers,
      observe: 'response',
      responseType: 'arraybuffer'
    });
  }

  downloadReport(fil: Object): Observable<HttpResponse<Object>> {
    let headers = new HttpHeaders();
    // headers = headers.append('Accept', '*');
    headers = headers.append('Content-Type', 'application/json');
    headers = headers.append('Access-Control-Allow-Headers', '*');

    return this.http.post(config.downloadDetailsReport_API, fil, {
      headers: headers,
      observe: 'response',
      responseType: 'arraybuffer'
    });
  }

  downloadTemplateReport(fil: Object): Observable<HttpResponse<Object>> {
    let headers = new HttpHeaders();
    // headers = headers.append('Accept', '*');
    headers = headers.append('Content-Type', 'application/json');
    headers = headers.append('Access-Control-Allow-Headers', '*');

    return this.http.post(config.downloadTemplateReport_API, fil, {
      headers: headers,
      observe: 'response',
      responseType: 'arraybuffer'
    });
  }
}
