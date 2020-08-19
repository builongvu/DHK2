import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {config} from '../../config/application.config';
import {Channel} from '../../models/Channel.model';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';

@Injectable()
export class ChannelService {
  constructor(private http: HttpClient) {
  }

  getAll() {
    return this.http.get<any>(`${config.apiUrl}/management/channel/getAlls`);
  }

  getChannelsByCondition(channelCondition: Channel) {
    return this.http.post<any>(`${config.apiUrl}/management/channel/getChannelsByCondition`, channelCondition);
  }

  getById(id: number) {
    return this.http.get(`${config.apiUrl}/management/channel/${id}`);
  }


  update(channel: Channel) {
    return this.http.put(`${config.apiUrl}/management/channel/update`, channel);
  }

  deleteById(id: number) {
    return this.http.delete(`${config.apiUrl}/management/channel/deleteById/${id}`);
  }

  add(channel: Channel) {
    return this.http.put(`${config.apiUrl}/management/channel/add`, channel);
  }

  getChannels(count = -1): Observable<Channel[]> {
    return this.http.get<Channel[]>(config.channel_API).pipe(
      map(response => response['data'].filter((post, i) => i > count))
    );
  }

  getNameByCode(vdsChannelCode: string) {
    return this.http.get(config.getChannelName_API + '/' + vdsChannelCode);
  }
}
