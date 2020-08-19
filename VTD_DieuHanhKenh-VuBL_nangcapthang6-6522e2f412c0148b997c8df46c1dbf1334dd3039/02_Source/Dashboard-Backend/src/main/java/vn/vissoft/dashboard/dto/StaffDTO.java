package vn.vissoft.dashboard.dto;

import viettel.passport.client.UserToken;

import java.util.List;

public class StaffDTO {

    private String staffCode;
    private UserToken vsaToken;
    private String shopCode;
    private String vdsChannelCode;
    private Long shopId;
    private List<MenuDTO> objects;
    private List<MenuDTO> allObjects;
    private Long defaultObjectId;
    private UserToken userName;

    public UserToken getUserName() {
        return userName;
    }

    public void setUserName(UserToken userName) {
        this.userName = userName;
    }

    public String getStaffCode() {
        return staffCode;
    }

    public void setStaffCode(String staffCode) {
        this.staffCode = staffCode;
    }


    public UserToken getVsaToken() {
        return vsaToken;
    }

    public void setVsaToken(UserToken vsaToken) {
        this.vsaToken = vsaToken;
    }

    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }

    public String getVdsChannelCode() {
        return vdsChannelCode;
    }

    public void setVdsChannelCode(String vdsChannelCode) {
        this.vdsChannelCode = vdsChannelCode;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public List<MenuDTO> getObjects() {
        return objects;
    }

    public void setObjects(List<MenuDTO> objects) {
        this.objects = objects;
    }

    public List<MenuDTO> getAllObjects() {
        return allObjects;
    }

    public void setAllObjects(List<MenuDTO> allObjects) {
        this.allObjects = allObjects;
    }

    public Long getDefaultObjectId() {
        return defaultObjectId;
    }

    public void setDefaultObjectId(Long defaultObjectId) {
        this.defaultObjectId = defaultObjectId;
    }
}
