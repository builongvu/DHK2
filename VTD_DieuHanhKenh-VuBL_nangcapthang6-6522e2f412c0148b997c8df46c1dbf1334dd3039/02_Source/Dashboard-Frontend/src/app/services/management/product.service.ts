import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {Injectable} from '@angular/core';

import {config} from '../../config/application.config';
import { ProductModel } from '../../models/Product.model';

@Injectable()
export class ProductService {

  constructor(private http: HttpClient) {
  }

  getProducts(count = -1): Observable<ProductModel[]> {
    return this.http.get<ProductModel[]>(config.productAPI).pipe(
      map(response => response['data'].filter((post, i) => i > count))
    );
  }

}
