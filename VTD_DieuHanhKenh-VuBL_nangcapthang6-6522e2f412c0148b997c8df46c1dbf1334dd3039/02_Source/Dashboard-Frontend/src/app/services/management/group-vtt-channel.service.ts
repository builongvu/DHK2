import { Observable } from 'rxjs';
import { VTTGroupChannel } from './../../models/vttgroupchannel.model';
import { config } from './../../config/application.config';
import { VttGroupChannelDTO } from './../../models/VttGroupChannelDTO.model';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class GroupVttChannelService {

  constructor(private http: HttpClient) { }

  getvttGroupChannelByCondition(vttGroupChannelDTO: VttGroupChannelDTO) {
    return this.http.post<any>(config.getvttGroupChannelByCondition_API, vttGroupChannelDTO);
  }

  getActiveVttChannel(): Observable<VTTGroupChannel[]> {
    return this.http.get<VTTGroupChannel[]>(config.getActiveVttChannel_API);
  }

  addVttGroupChannel(vttGroupChannel) {
    return this.http.post<any>(config.addVttGroupChannel_API, vttGroupChannel);
  }

  addVttPosition(vttGroupChannel) {
    return this.http.post<any>(config.addVttPosition_API, vttGroupChannel);
  }

  addVttGroupChannelSale(vttGroupChannel) {
    return this.http.post<any>(config.addVttGroupChannelSale_API, vttGroupChannel);
  }

  checkCodeConfilic(code: string) {
    return this.http.get(config.checkCodeConflict_API + '/' + code);
  }


}
