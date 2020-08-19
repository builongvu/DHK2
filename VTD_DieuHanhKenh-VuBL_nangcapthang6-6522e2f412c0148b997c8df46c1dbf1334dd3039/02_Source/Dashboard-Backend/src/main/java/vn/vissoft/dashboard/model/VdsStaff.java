package vn.vissoft.dashboard.model;

import java.util.Date;
import javax.persistence.*;

/**
 * Created by DESKTOP-6ORLP4T on 18/10/2019 17:50
 */

@Entity
@Table(name="vds_staff")
public class VdsStaff {

    private String shopWarning;
    private String vdsChannelCode;
    private String shopCode;
    private String staffCode;
    private String staffName;
    private String phoneNumber;
    private String email;
    private String status;
    private String user;
    private Date createDate;
    private String staffType;
    private Long id;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    @Column(name="STATUS")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    
    @Column(name="shop_code")
    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }
    
    
    @Column(name="vds_channel_code")
    public String getVdsChannelCode() {
        return vdsChannelCode;
    }

    public void setVdsChannelCode(String vdsChannelCode) {
        this.vdsChannelCode = vdsChannelCode;
    }
    
    
    @Column(name="staff_code")
    public String getStaffCode() {
        return staffCode;
    }

    public void setStaffCode(String staffCode) {
        this.staffCode = staffCode;
    }

    @Column(name = "staff_name")
    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    @Column(name="create_date")
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
    
    
    @Column(name="created_user")
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Column(name ="phone_number")
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Column(name = "email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "staff_type")
    public String getStaffType() {
        return staffType;
    }

    public void setStaffType(String staffType) {
        this.staffType = staffType;
    }

    @Column(name = "shop_warning")
    public String getShopWarning() {
        return shopWarning;
    }

    public void setShopWarning(String shopWarning) {
        this.shopWarning = shopWarning;
    }


    public VdsStaff(String vdsChannelCode, String shopCode, String staffCode, String staffName, String staffType, String phoneNumber, String email, String shopWarning, String status, String user, Date createDate) {
        this.vdsChannelCode = vdsChannelCode;
        this.shopCode = shopCode;
        this.staffCode = staffCode;
        this.staffName = staffName;
        this.staffType = staffType;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.shopWarning = shopWarning;
        this.status = status;
        this.user = user;
        this.createDate = createDate;
    }

    public VdsStaff() {
    }
}
