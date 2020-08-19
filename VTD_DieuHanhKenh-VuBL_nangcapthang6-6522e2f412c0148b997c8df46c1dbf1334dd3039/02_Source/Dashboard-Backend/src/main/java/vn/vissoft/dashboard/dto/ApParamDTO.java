package vn.vissoft.dashboard.dto;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Date;

public class ApParamDTO {

    private Long mlngId;
    private String mstrType;
    private String mstrCode;
    private String mstrName;
    private String mstrDescription;
    private String mstrStatus;
    private Date mdtCreateDatetime;
    private String value;

    public String getType() {
        return mstrType;
    }

    public void setType(String pstrType) {
        this.mstrType = pstrType;
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

    public String getDescription() {
        return mstrDescription;
    }

    public void setDescription(String pstrDescription) {
        this.mstrDescription = pstrDescription;
    }

    public String getStatus() {
        return mstrStatus;
    }

    public void setStatus(String pstrStatus) {
        this.mstrStatus = pstrStatus;
    }

    public Date getCreateDatetime() {
        return mdtCreateDatetime;
    }

    public void setCreateDatetime(Date pdtCreateDatetime) {
        this.mdtCreateDatetime = pdtCreateDatetime;
    }

    public Long getId() {
        return mlngId;
    }

    public void setId(Long plngId) {
        this.mlngId = plngId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
