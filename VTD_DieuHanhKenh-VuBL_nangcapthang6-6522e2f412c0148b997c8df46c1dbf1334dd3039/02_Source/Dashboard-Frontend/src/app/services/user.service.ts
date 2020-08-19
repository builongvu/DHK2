import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { User } from '../models/User.model';
import { config } from '../config/application.config';
import { Pager } from '../models/Pager';

@Injectable()
export class UserService{
    constructor(private http: HttpClient) { }

    getAll() {
        return this.http.get<any>(`${config.apiUrl}/user/getAlls`);
    }

    getUsersByCondition(userCondition: User, pager: Pager){
        if(!pager) {
            // @ts-ignore
          pager = new Pager();
        }
        var url = `${config.apiUrl}/user/getUsersByCondition?`;
        url = url + "page=" + pager.page + "&pageSize=" + pager.pageSize;
        if(userCondition.username){
            url = url + "&username=" + userCondition.username;
        }

        if(userCondition.email){
            url = url + "&email=" + userCondition.email;
        }

        // if(userCondition.role !=null && userCondition.role!= '-1'){
        //     url = url + "&role=" + userCondition.role;
        // }

        // if(userCondition.activeFlg !=null && userCondition.activeFlg !== -1){
        //     url = url + "&activeFlg=" + userCondition.activeFlg;
        // }
        return this.http.get<any>(url);
    }

    getById(id: number) {
        return this.http.get(`${config.apiUrl}/user/${id}`);
    }

    register(user: User) {
        return this.http.post(`${config.apiUrl}/user/add`, user);
    }

    update(user: User) {
        return this.http.put(`${config.apiUrl}/user/update`, user);
    }

    deleteById(id: number) {
        return this.http.delete(`${config.apiUrl}/user/deleteById/${id}`);
    }

    deleteByUsername(username: string) {
        return this.http.delete(`${config.apiUrl}/user/deleteByName/${username}`);
    }
}
