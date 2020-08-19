package vn.vissoft.dashboard.dto;


import viettel.passport.client.ObjectToken;

import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class MenuDTO implements Comparable<MenuDTO>{
    private String objectIcon;
    private String objectImg;
    private String objectUrl;
    private String objectType;
    private Long parentId;
    private String objectName;
    private String objectNameI18N;
    private String objectCode;
    private Long id;
    private Long status;
    private Long ord;
    private List<MenuDTO> childObjects;
    private Integer functionType;
    private Long functionId;

    public String getObjectIcon() {
        return objectIcon;
    }

    public void setObjectIcon(String objectIcon) {
        this.objectIcon = objectIcon;
    }

    public String getObjectImg() {
        return objectImg;
    }

    public void setObjectImg(String objectImg) {
        this.objectImg = objectImg;
    }

    public String getObjectUrl() {
        return objectUrl;
    }

    public void setObjectUrl(String objectUrl) {
        this.objectUrl = objectUrl;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getObjectCode() {
        return objectCode;
    }

    public void setObjectCode(String objectCode) {
        this.objectCode = objectCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public Long getOrd() {
        return ord;
    }

    public void setOrd(Long ord) {
        this.ord = ord;
    }

    public List<MenuDTO> getChildObjects() {
        return childObjects;
    }

    public void setChildObjects(List<MenuDTO> childObjects) {
        this.childObjects = childObjects;
    }

    @Override
    public int compareTo(MenuDTO o) {
        if(this.ord==null)
            return -1;
        if(o.ord==null)
            return 1;
        return this.ord.compareTo(o.ord);
    }

    @Override
    public int hashCode() {
        return Objects.hash(objectIcon, objectImg, objectUrl, objectType, parentId, objectName, objectNameI18N, objectCode, id, status, ord, childObjects, functionType, functionId);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    public String getObjectNameI18N() {
        return objectNameI18N;
    }

    public void setObjectNameI18N(String objectNameI18N) {
        this.objectNameI18N = objectNameI18N;
    }

    public Integer getFunctionType() {
        return functionType;
    }

    public void setFunctionType(Integer functionType) {
        this.functionType = functionType;
    }

    public Long getFunctionId() {
        return functionId;
    }

    public void setFunctionId(Long functionId) {
        this.functionId = functionId;
    }
}
