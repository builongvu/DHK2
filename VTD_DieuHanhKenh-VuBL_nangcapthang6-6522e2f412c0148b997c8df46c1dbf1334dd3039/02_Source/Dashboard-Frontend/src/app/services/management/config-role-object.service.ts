import { config } from './../../config/application.config';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ConfigRoleObjectService {

  constructor(private http: HttpClient) { }

  getAllRoles() {
    return this.http.get<any>(config.roles_API);
  }

  getAllRoleActive() {
    return this.http.get<any>(config.roles_Active);
  }

  createRole(role) {
    return this.http.post<any>(config.create_role_API, role);
  }

  editRole(role) {
    return this.http.put<any>(config.edit_role_API, role);
  }

  getRoleByCondition(code: string, codeObjcet: string) {

    let url = '?1=1';
    if (code != null || code !== '') {
      url = url + '&code=' + code;
    }

    if (codeObjcet != null || codeObjcet !== '') {
      url = url + '&codeObject=' + codeObjcet;
    }

    if (codeObjcet === '' && code === '') {
      return this.http.get<any>(config.roles_API);
    }

    return this.http.get<any>(config.roles_codition_API + url);
  }

  getActionOfRole(roleId: number) {
    return this.http.get(config.getActionOfRole_API + '/' + roleId);
  }

  createRoleObject(roleObject) {
    return this.http.post<any>(config.create_role_object_API, roleObject);
  }
}
