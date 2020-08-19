import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { config } from '../../config/application.config';
import { ShopUnitDTO, ShopUnit } from '../../models/shopUnit.model';

@Injectable({
  providedIn: 'root'
})
export class ShopunitService {

  constructor(private http: HttpClient) {
  }

  getShopUnitByCondition(shopunit: ShopUnitDTO) {
    return this.http.post<any>(config.shopUnitByCondition, shopunit);
  }

  save(shopUnit: ShopUnit, type: number) {
    if(type == 1){
      return this.http.post<any>(config.shopUnitSavenew, shopUnit);
    }
    return this.http.post<any>(config.shopUnitUpdate, shopUnit);
  }

  getByID(id: number) {
    return this.http.get<any>(config.getByID + "/" + id);
  }

  lockUnlock(id: string[], status: string){
    return this.http.post<any>(config.changeStatus + '/' + status, id);
  }

}
