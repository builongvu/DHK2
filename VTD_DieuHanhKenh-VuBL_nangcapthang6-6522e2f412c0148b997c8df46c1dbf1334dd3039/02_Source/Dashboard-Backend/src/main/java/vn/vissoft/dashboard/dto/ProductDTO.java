package vn.vissoft.dashboard.dto;

import java.sql.Date;

public class ProductDTO {

    private Long mlngId;
    private String mstrCode;
    private String mstrName;
    private String mstrStatus;
    private String mstrUserUpdate;
    private Date mdtChangeDatetime;

    public Long getId() {
        return mlngId;
    }

    public void setId(Long plngId) {
        this.mlngId = plngId;
    }

    public String getCode() {
        return mstrCode;
    }

    public void setCode(String pstrCode) {
        this.mstrCode = pstrCode;
    }

    public String getName() {
        return mstrName;
    }

    public void setName(String pstrName) {
        this.mstrName = pstrName;
    }

    public String getStatus() {
        return mstrStatus;
    }

    public void setStatus(String pstrStatus) {
        this.mstrStatus = pstrStatus;
    }

    public String getUserUpdate() {
        return mstrUserUpdate;
    }

    public void setUserUpdate(String pstrUserUpdate) {
        this.mstrUserUpdate = pstrUserUpdate;
    }

    public Date getChangeDatetime() {
        return mdtChangeDatetime;
    }

    public void setChangeDatetime(Date pdtChangeDatetime) {
        this.mdtChangeDatetime = pdtChangeDatetime;
    }
}
