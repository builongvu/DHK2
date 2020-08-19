package vn.vissoft.dashboard.dto;

import java.sql.Date;

public class ServiceDTO {

    private Long mlngId;
    private Long mlngParentId;
    private String mstrCode;
    private String mstrName;
    private Long mlngGroupServiceId;
    private String mstrDataType;
    private int mintAssignType;
    private int mintImportType;
    private int mintServiceCycle;
    private String mstrServiceType;
    private Date mdtFromDate;
    private Date mdtToDate;
    private Long mlngServiceOrder;
    private String mstrStatus;
    private String mstrExp;
    private String mstrUser;
    private Date mdtChangeDatetime;
    private String mstrCongVan;
    private String mstrUnitCode;
    private String vdsChannelCode;
    private String serviceCalcType;

    public Long getId() {
        return mlngId;
    }

    public void setId(Long plngId) {
        this.mlngId = plngId;
    }

    public Long getParentId() {
        return mlngParentId;
    }

    public void setParentId(Long plngParentId) {
        this.mlngParentId = plngParentId;
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

    public String getDataType() {
        return mstrDataType;
    }

    public void setDataType(String pstrDataType) {
        this.mstrDataType = pstrDataType;
    }

    public int getAssignType() {
        return mintAssignType;
    }

    public void setAssignType(int pintAssignType) {
        this.mintAssignType = pintAssignType;
    }

    public int getImportType() {
        return mintImportType;
    }

    public void setImportType(int pintImportType) {
        this.mintImportType = pintImportType;
    }

    public int getServiceCycle() {
        return mintServiceCycle;
    }

    public void setServiceCycle(int pintServiceCycle) {
        this.mintServiceCycle = pintServiceCycle;
    }

    public String getServiceType() {
        return mstrServiceType;
    }

    public void setServiceType(String pstrServiceType) {
        this.mstrServiceType = pstrServiceType;
    }

    public Date getFromDate() {
        return mdtFromDate;
    }

    public void setFromDate(Date pdtFromDate) {
        this.mdtFromDate = pdtFromDate;
    }

    public Date getToDate() {
        return mdtToDate;
    }

    public void setToDate(Date pdtToDate) {
        this.mdtToDate = pdtToDate;
    }

    public Long getServiceOrder() {
        return mlngServiceOrder;
    }

    public void setServiceOrder(Long plngServiceOrder) {
        this.mlngServiceOrder = plngServiceOrder;
    }

    public String getStatus() {
        return mstrStatus;
    }

    public void setStatus(String pstrStatus) {
        this.mstrStatus = pstrStatus;
    }

    public String getExp() {
        return mstrExp;
    }

    public void setExp(String pstrExp) {
        this.mstrExp = pstrExp;
    }

    public String getUser() {
        return mstrUser;
    }

    public void setUser(String pstrUser) {
        this.mstrUser = pstrUser;
    }

    public Date getChangeDatetime() {
        return mdtChangeDatetime;
    }

    public void setChangeDatetime(Date pdtChangeDatetime) {
        this.mdtChangeDatetime = pdtChangeDatetime;
    }

    public Long getGroupServiceId() {
        return mlngGroupServiceId;
    }

    public void setGroupServiceId(Long plngGroupServiceId) {
        this.mlngGroupServiceId = plngGroupServiceId;
    }

    public String getCongVan() {
        return mstrCongVan;
    }

    public void setCongVan(String pstrCongVan) {
        this.mstrCongVan = pstrCongVan;
    }

    public String getVdsChannelCode() {
        return vdsChannelCode;
    }

    public void setVdsChannelCodeFromSC(String vdsChannelCode) {
        this.vdsChannelCode = vdsChannelCode;
    }

    public String getUnitCode() {
        return mstrUnitCode;
    }

    public void setUnitCode(String mstrUnitCode) {
        this.mstrUnitCode = mstrUnitCode;
    }

    public String getServiceCalcType() {
        return serviceCalcType;
    }

    public void setServiceCalcType(String serviceCalcType) {
        this.serviceCalcType = serviceCalcType;
    }
}
