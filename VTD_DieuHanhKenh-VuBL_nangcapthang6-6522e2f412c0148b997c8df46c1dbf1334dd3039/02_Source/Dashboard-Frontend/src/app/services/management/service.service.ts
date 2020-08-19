import { DownloadModel } from './../../models/download.model';
import { ServiceRequest } from './../../models/service-request.model';
import { config } from './../../config/application.config';
import { ServiceModel } from './../../models/service.model';
import { HttpClient, HttpResponse, HttpHeaders } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { Injectable } from '@angular/core';
import { warningservice } from '../../models/warningservice';
import { map } from 'rxjs/operators';
import { ServiceScoreService } from '../../models/serviceScore.service';
import { ShopCodesModel } from '../../models/shopCodes.model';
import { SearchModel } from '../../models/Search.model';
import {ServiceWarningModel} from "../../models/ServiceWarning.model";

@Injectable()
export class ServiceService {
  constructor(private http: HttpClient) { }

  service: BehaviorSubject<any> = new BehaviorSubject<any>([]);
  service$: Observable<any> = this.service.asObservable();

  serviceTree: BehaviorSubject<any> = new BehaviorSubject<any>([]);
  serviceTree$: Observable<any> = this.serviceTree.asObservable();

  allService: BehaviorSubject<any> = new BehaviorSubject<any>([]);
  allService$: Observable<any> = this.allService.asObservable();

  allServiceStatus: BehaviorSubject<any> = new BehaviorSubject<any>([]);
  allServiceStatus$: Observable<any> = this.allServiceStatus.asObservable();

  treeData: BehaviorSubject<any> = new BehaviorSubject<any>([]);
  treeData$: Observable<any> = this.treeData.asObservable();

  reloadWarning: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  reloadWarning$: Observable<boolean> = this.reloadWarning.asObservable();

  setService(service: any) {
    this.service.next(service);
  }

  setServiceTree(serviceTree: any) {
    this.serviceTree.next(serviceTree);
  }

  setAllService(allService: any) {
    this.allService.next(allService);
  }

  setAllServiceStatus(allServiceStatus: any) {
    this.allServiceStatus.next(allServiceStatus);
  }

  setTreeData(treeData: any) {
    this.treeData.next(treeData);
  }

  getServices(): Observable<ServiceModel[]> {
    return this.http.get<ServiceModel[]>(config.service_API);
  }

  getServicesByGroupId(serviceModel: ServiceModel) {
    return this.http.post<ServiceModel>(config.groupId_API, serviceModel);
  }

  getAllService(): Observable<ServiceModel[]> {
    return this.http.get<ServiceModel[]>(config.service_getAll_API);
  }

  getAllServiceByStatus(): Observable<ServiceModel[]> {
    return this.http.get<ServiceModel[]>(config.getServiceByStatus_API);
  }

  createNewService(serviceRequest: ServiceRequest): Observable<ServiceRequest> {
    return this.http.post<ServiceRequest>(config.service_CREATE_API, serviceRequest);
  }

  editService(serviceRequest: ServiceRequest): Observable<ServiceRequest> {
    return this.http.put<ServiceRequest>(config.editService_API, serviceRequest);
  }

  getAllWarning(count = -1): Observable<warningservice[]> {
    return this.http.get<warningservice[]>(config.channel_API).pipe(
      map(response => response['data'].filter((post, i) => i > count))
    );
  }

  getLogOfServiceByServiceId(idService: number) {
    return this.http.get(config.getLogOfService_API + '/' + idService);
  }

  getServiceCycle(type: string, status: string) {
    return this.http.get<any>(`${config.apparam_getbytypeandstatus_API}/${type}/${status}`);
  }

  getServiceByOrder(order: number) {
    return this.http.get<any>(config.getServiceByOrder_API + '/' + order);
  }

  getDataWeight(serviceId: ServiceScoreService) {
    return this.http.post(config.mngTarget_weight_API, serviceId);
  }

  getUnits() {
    return this.http.get<any>(config.partner_getAll_API);
  }

  createScoreService(serivce: ServiceScoreService) {
    return this.http.post(config.create_weight_API, serivce);
  }

  updateScoreService(serivce: ServiceScoreService, id: number) {
    return this.http.put(config.update_weight_API + '/' + id, serivce);
  }

  getStaffs(shopCode: string, vdsChannelCode: string) {
    return this.http.post<any>(config.staffs_weight_API + '?shopCode=' + shopCode
      + '&vdsChannelCode=' + vdsChannelCode, null);
  }

  search(searchModel: ServiceScoreService) {
    return this.http.post<any>(config.search_weight_API, searchModel);
  }

  getDataUnits() {
    return this.http.get<any>(config.units_weight_API);
  }
  getDataUnitsVDS() {
    return this.http.get<any>(config.units_VDS_API);
  }

  downloadFile(): Observable<HttpResponse<Object>> {
    let headers = new HttpHeaders();
    // headers = headers.append('Accept', '*');
    headers = headers.append('Content-Type', 'application/json');
    headers = headers.append('Access-Control-Allow-Headers', '*');

    return this.http.get(config.download_weight_API, {
      headers: headers,
      observe: 'response',
      responseType: 'arraybuffer'
    });
  }
  createServiceWarning(serviceWarning: ServiceWarningModel) {
    return this.http.post<any>(config.createWarning_API, serviceWarning);
  }

  updateServiceWarning(serviceWarning: ServiceWarningModel) {
    return this.http.put<any>(config.editWarning_API, serviceWarning);
  }

  getServiceByID(idService: number) {
    return this.http.get(config.getServiceByID_API + '/' + idService);
  }

  setReloadWarning(reload: boolean) {
    this.reloadWarning.next(reload);
  }

  checkParentID(idService: number) {
    return this.http.get(config.checkServiceParenID_API + '/' + idService);
  }
}
