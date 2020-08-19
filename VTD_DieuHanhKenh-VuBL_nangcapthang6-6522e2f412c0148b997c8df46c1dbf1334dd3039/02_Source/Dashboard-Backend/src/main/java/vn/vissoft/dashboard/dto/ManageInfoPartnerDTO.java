package vn.vissoft.dashboard.dto;

import java.util.Date;

public class ManageInfoPartnerDTO {

    private Long id;
    private String vdsChannelCode;
    private String shopCode;
    private String shopName;
    private String shortName;
    private String parentShopCode;
    private String assignKpi;
    private String status;
    private String user;
    private Date createDate;
    private String unitType;
    private Date fromDate;
    private Date toDate;
    private String parentShopName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVdsChannelCode() {
        return vdsChannelCode;
    }

    public void setVdsChannelCode(String vdsChannelCode) {
        this.vdsChannelCode = vdsChannelCode;
    }

    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getParentShopCode() {
        return parentShopCode;
    }

    public void setParentShopCode(String parentShopCode) {
        this.parentShopCode = parentShopCode;
    }

    public String getAssignKpi() {
        return assignKpi;
    }

    public void setAssignKpi(String assignKpi) {
        this.assignKpi = assignKpi;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getUnitType() {
        return unitType;
    }

    public void setUnitType(String unitType) {
        this.unitType = unitType;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public String getParentShopName() {
        return parentShopName;
    }

    public void setParentShopName(String parentShopName) {
        this.parentShopName = parentShopName;
    }
}
