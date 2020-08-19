package vn.vissoft.dashboard.dto;

public class UnitTreeDTO {

    private Long mlngObjectId;
    private String mstrObjectType;
    private String mstrObjectName;
    private Long mlngParentId;

    public Long getObjectId() {
        return mlngObjectId;
    }

    public void setObjectId(Long plngObjectId) {
        this.mlngObjectId = plngObjectId;
    }

    public String getObjectType() {
        return mstrObjectType;
    }

    public void setObjectType(String pstrObjectType) {
        this.mstrObjectType = pstrObjectType;
    }

    public String getObjectName() {
        return mstrObjectName;
    }

    public void setObjectName(String pstrObjectName) {
        this.mstrObjectName = pstrObjectName;
    }

    public Long getParentId() {
        return mlngParentId;
    }

    public void setParentId(Long plngParentId) {
        this.mlngParentId = plngParentId;
    }
}
