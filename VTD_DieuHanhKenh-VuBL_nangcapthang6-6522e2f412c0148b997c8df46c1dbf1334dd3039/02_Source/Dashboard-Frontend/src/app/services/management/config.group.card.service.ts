import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {config} from '../../config/application.config';
import {BehaviorSubject, Observable} from 'rxjs';
import {ConfigGroupCardModel} from '../../models/config.group.card.model';

@Injectable({
  providedIn: 'root'
})
export class ConfigGroupCardService {

  constructor(private http: HttpClient) {}

  service: BehaviorSubject<any> = new BehaviorSubject<any>([]);
  service$: Observable<any> = this.service.asObservable();

  getDefaultCycle(apType: string) {
    return this.http.get(config.apparam_getbytype_API + '/' + apType);
  }

  getAllGroupCard(): Observable<ConfigGroupCardModel[]> {
    return this.http.get<ConfigGroupCardModel[]>(config.getAllGroupCard_API);
  }

  deleteGroupCard(groupId: number) {
    return this.http.get(config.deleteGroupCard_API + '/' + groupId);
  }

  searchGroupCard(groupCard: ConfigGroupCardModel) {
    return this.http.post(config.searchGroupCard_API, groupCard);
  }

  addGroupCard(groupCard: ConfigGroupCardModel) {
    return this.http.post(config.addGroupCard_API, groupCard);
  }

  updateGroupCard(groupCard: ConfigGroupCardModel) {
    return this.http.post(config.updateGroupCard_API, groupCard);
  }

}
