package vn.vissoft.dashboard.dto;

import java.sql.Date;

public class StaffUnitDTO {

    private Long mlngId;
    private Long mlngStaffId;
    private Long mlngUnitId;
    private Date mdtFromDate;
    private Date mdtToDate;
    private String mstrIsStaffOwner;
    private String mstrStatus;

    public Long getId() {
        return mlngId;
    }

    public void setId(Long plngId) {
        this.mlngId = plngId;
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

    public String getIsStaffOwner() {
        return mstrIsStaffOwner;
    }

    public void setIsStaffOwner(String pstrIsStaffOwner) {
        this.mstrIsStaffOwner = pstrIsStaffOwner;
    }

    public String getStatus() {
        return mstrStatus;
    }

    public void setStatus(String pstrStatus) {
        this.mstrStatus = pstrStatus;
    }

    public Long getStaffId() {
        return mlngStaffId;
    }

    public void setStaffId(Long plngStaffId) {
        this.mlngStaffId = plngStaffId;
    }

    public Long getUnitId() {
        return mlngUnitId;
    }

    public void setUnitId(Long plngUnitId) {
        this.mlngUnitId = plngUnitId;
    }
}
