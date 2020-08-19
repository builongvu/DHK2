package vn.vissoft.dashboard.dto;

public class VdsStaffDTO {

    private Long id;
    private String staffCode;
    private String staffName;
    private String phoneNumber;
    private String shopCode;
    private String status;
    private String vdsChannelName;
    private String shopName;
    private String email;
    private String vdsChannelCode;
    private String staffType;
    private String shopWarning;
    private boolean position;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStaffCode() {
        return staffCode;
    }

    public void setStaffCode(String staffCode) {
        this.staffCode = staffCode;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }

    public String getVdsChannelName() {
        return vdsChannelName;
    }

    public void setVdsChannelName(String vdsChannelName) {
        this.vdsChannelName = vdsChannelName;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getVdsChannelCode() {
        return vdsChannelCode;
    }

    public void setVdsChannelCode(String vdsChannelCode) {
        this.vdsChannelCode = vdsChannelCode;
    }

    public String getStaffType() {
        return staffType;
    }

    public void setStaffType(String staffType) {
        this.staffType = staffType;
    }

    public String getShopWarning() {
        return shopWarning;
    }

    public void setShopWarning(String shopWarning) {
        this.shopWarning = shopWarning;
    }

    public boolean isPosition() {
        return position;
    }

    public void setPosition(boolean position) {
        this.position = position;
    }
}
