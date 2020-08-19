import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {config} from '../../config/application.config';
import {BehaviorSubject, Observable} from 'rxjs';
import {ConfigSingleCardModel} from '../../models/config.single.card.model';
import {ConfigGroupCardModel} from '../../models/config.group.card.model';

@Injectable({
  providedIn: 'root'
})
export class ConfigSingleCardService {

  constructor(private http: HttpClient) {
  }

  service: BehaviorSubject<any> = new BehaviorSubject<any>([]);
  service$: Observable<any> = this.service.asObservable();

  setService(service: any) {
    this.service.next(service);
  }

  getActiveCard(): Observable<ConfigSingleCardModel[]> {
    return this.http.get<ConfigSingleCardModel[]>(config.getActiveCard_API);
  }

  getCardSizeAPI(apType: string) {
    return this.http.get(config.apparam_getbytype_API + '/' + apType);
  }

  getAllSingleCard(): Observable<ConfigSingleCardModel[]> {
    return this.http.get<ConfigSingleCardModel[]>(config.getAllSingleCard_API);
  }

  deleteSingleCard(cardId: ConfigSingleCardModel) {
    return this.http.post(config.deleteSingleCard_API, cardId);
  }

  searchSingleCard(singleCard: ConfigSingleCardModel) {
    return this.http.post(config.searchSingleCard_API, singleCard);
  }

  createSingleCard(singleCard: ConfigSingleCardModel) {
    return this.http.post(config.createSingleCard_API, singleCard);
  }

  updateSingleCard(singleCard: ConfigSingleCardModel) {
    return this.http.post(config.updateSingleCard_API, singleCard);
  }

  getGroupCardOrderName(): Observable<ConfigGroupCardModel[]> {
    return this.http.get<ConfigGroupCardModel[]>(config.getGroupCardOrder_API);
  }
}
