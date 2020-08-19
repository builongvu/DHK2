package vn.vissoft.dashboard.dto;

import vn.vissoft.dashboard.model.ConfigRolesObjects;

import java.util.List;

public class ConfigRoleDTO {

    private Long idRole;
    private String mstrRoleCode;
    private String mstrRoleDescription;
    private Long mlngStatus;
    private String mobjConfigRolesObjects;
    private String mobjNameObjects;
    private String mstrRoleName;
    private int no;

    public Long getId() {
        return idRole;
    }

    public void setId(Long idRole) {
        this.idRole = idRole;
    }

    public String getRoleCode() {
        return mstrRoleCode;
    }

    public void setRoleCode(String roleCode) {
        this.mstrRoleCode = roleCode;
    }

    public String getRoleDescription() {
        return mstrRoleDescription;
    }

    public void setRoleDescription(String roleDescription) {
        this.mstrRoleDescription = roleDescription;
    }

    public Long getStatus() {
        return mlngStatus;
    }

    public void setStatus(Long status) {
        this.mlngStatus = status;
    }

    public String getMobjConfigRolesObjects() {
        return mobjConfigRolesObjects;
    }

    public void setMobjConfigRolesObjects(String mobjConfigRolesObjects) {
        this.mobjConfigRolesObjects = mobjConfigRolesObjects;
    }

    public String getMobjNameObjects() {
        return mobjNameObjects;
    }

    public void setMobjNameObjects(String mobjNameObjects) {
        this.mobjNameObjects = mobjNameObjects;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public String getRoleName() {
        return mstrRoleName;
    }

    public void setRoleName(String roleName) {
        this.mstrRoleName = roleName;
    }
}
