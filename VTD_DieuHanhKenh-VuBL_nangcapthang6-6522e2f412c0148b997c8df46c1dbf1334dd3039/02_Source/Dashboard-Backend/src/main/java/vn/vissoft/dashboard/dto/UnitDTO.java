package vn.vissoft.dashboard.dto;

import java.sql.Date;

public class UnitDTO extends BaseDTO{

    private Long mlngId;
    private String mstrCode;
    private String mstrName;
    private Long mlngParentId;
    private String mstrShortName;
    private String mstrStatus;
    private Date mdtImportDatetime;
    private Double rate;

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

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

    public Long getParentId() {
        return mlngParentId;
    }

    public void setParentId(Long plngParentId) {
        this.mlngParentId = plngParentId;
    }

    public String getShortName() {
        return mstrShortName;
    }

    public void setShortName(String pstrShortName) {
        this.mstrShortName = pstrShortName;
    }

    public String getStatus() {
        return mstrStatus;
    }

    public void setStatus(String pstrStatus) {
        this.mstrStatus = pstrStatus;
    }

    public Date getImportDatetime() {
        return mdtImportDatetime;
    }

    public void setImportDatetime(Date pdtImportDatetime) {
        this.mdtImportDatetime = pdtImportDatetime;
    }
}
