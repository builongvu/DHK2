package vn.vissoft.dashboard.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Date;
import java.util.Objects;

@Entity
@Table(name = "SERVICE")
public class Service implements Serializable {

    private Long mlngId;
    private Long mlngParentId;
    private String mstrCode;
    private String mstrName;
    private Long mlngGroupServiceId;
    private String mstrDataType;
    private int mintAssignType;
    private int mintImportType;
    private int mintServiceCycle;
    private int mstrServiceType;
    private Date mdtFromDate;
    private Date mdtToDate;
    private Long mlngServiceOrder;
    private String mstrStatus;
    private String mstrExp;
    private String mstrUser;
    private Date mdtChangeDatetime;
    private String mstrCongVan;
    private String unitCode;
    private String serviceCalcType;

    @Column(name = "ID")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return mlngId;
    }

    public void setId(Long plngId) {
        this.mlngId = plngId;
    }

    @Basic
    @Column(name = "PARENT_ID")
    public Long getParentId() {
        return mlngParentId;
    }

    public void setParentId(Long plngParentId) {
        this.mlngParentId = plngParentId;
    }

    @Basic
    @Column(name = "CODE")
    public String getCode() {
        return mstrCode;
    }

    public void setCode(String pstrCode) {
        this.mstrCode = pstrCode;
    }

    @Column(name = "NAME")
    public String getName() {
        return mstrName;
    }

    public void setName(String pstrName) {
        this.mstrName = pstrName;
    }

    @Column(name = "DATA_TYPE")
    public String getDataType() {
        return mstrDataType;
    }

    public void setDataType(String pstrDataType) {
        this.mstrDataType = pstrDataType;
    }

    @Column(name = "ASSIGN_TYPE")
    public int getAssignType() {
        return mintAssignType;
    }

    public void setAssignType(int pintAssignType) {
        this.mintAssignType = pintAssignType;
    }

    @Column(name = "IMPORT_TYPE")
    public int getImportType() {
        return mintImportType;
    }

    public void setImportType(int pintImportType) {
        this.mintImportType = pintImportType;
    }

    @Column(name = "SERVICE_CYCLE")
    public int getServiceCycle() {
        return mintServiceCycle;
    }

    public void setServiceCycle(int pintServiceCycle) {
        this.mintServiceCycle = pintServiceCycle;
    }

    @Column(name = "SERVICE_TYPE")
    public int getServiceType() {
        return mstrServiceType;
    }

    public void setServiceType(int pstrServiceType) {
        this.mstrServiceType = pstrServiceType;
    }

    @Column(name = "FROM_DATE")
    public Date getFromDate() {
        return mdtFromDate;
    }

    public void setFromDate(Date pdtFromDate) {
        this.mdtFromDate = pdtFromDate;
    }

    @Column(name = "TO_DATE")
    public Date getToDate() {
        return mdtToDate;
    }

    public void setToDate(Date pdtToDate) {
        this.mdtToDate = pdtToDate;
    }

    @Column(name = "SERVICE_ORDER")
    public Long getServiceOrder() {
        return mlngServiceOrder;
    }

    public void setServiceOrder(Long plngServiceOrder) {
        this.mlngServiceOrder = plngServiceOrder;
    }

    @Column(name = "STATUS")
    public String getStatus() {
        return mstrStatus;
    }

    public void setStatus(String pstrStatus) {
        this.mstrStatus = pstrStatus;
    }

    @Column(name = "EXP")
    public String getExp() {
        return mstrExp;
    }

    public void setExp(String pstrExp) {
        this.mstrExp = pstrExp;
    }

    @Column(name = "USER")
    public String getUser() {
        return mstrUser;
    }

    public void setUser(String pstrUser) {
        this.mstrUser = pstrUser;
    }

    @Column(name = "CHANGE_DATETIME")
    public Date getChangeDatetime() {
        return mdtChangeDatetime;
    }

    public void setChangeDatetime(Date pdtChangeDatetime) {
        this.mdtChangeDatetime = pdtChangeDatetime;
    }

    @Column(name = "GROUP_ID")
    public Long getGroupServiceId() {
        return mlngGroupServiceId;
    }

    public void setGroupServiceId(Long plngGroupServiceId) {
        this.mlngGroupServiceId = plngGroupServiceId;
    }

    @Column(name = "CONG_VAN")
    public String getCongVan() {
        return mstrCongVan;
    }

    public void setCongVan(String pstrCongVan) {
        this.mstrCongVan = pstrCongVan;
    }

    @Column(name = "unit_code")
    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    @Column(name = "SERVICE_CALC_TYPE")
    public String getServiceCalcType() {
        return serviceCalcType;
    }

    public void setServiceCalcType(String serviceCalcType) {
        this.serviceCalcType = serviceCalcType;
    }



}
