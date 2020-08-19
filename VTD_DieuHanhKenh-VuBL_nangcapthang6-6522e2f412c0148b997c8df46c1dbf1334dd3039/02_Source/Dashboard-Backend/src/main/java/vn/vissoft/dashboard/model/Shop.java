package vn.vissoft.dashboard.model;

import javax.persistence.*;

/**
 * Created by DESKTOP-6ORLP4T on 18/10/2019 17:25
 */

@Entity
@Table(name="shop")
public class Shop {

    private String province;
    private String shopCode;
    private Long channelTypeId;
    private String district;
    private String shopName;
    private Long shopId;
    private String status;
    private Long staffOwnerId;

    
    
    @Column(name="province")
    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }
    
    
    @Column(name="shop_code")
    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }
    
    
    @Column(name="channel_type_id")
    public Long getChannelTypeId() {
        return channelTypeId;
    }

    public void setChannelTypeId(Long channelTypeId) {
        this.channelTypeId = channelTypeId;
    }
    
    
    @Column(name="district")
    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }
    
    
    @Column(name="shop_name")
    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="shop_id")
    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }
    
    
    @Column(name="status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    
    @Column(name="staff_owner_id")
    public Long getStaffOwnerId() {
        return staffOwnerId;
    }

    public void setStaffOwnerId(Long staffOwnerId) {
        this.staffOwnerId = staffOwnerId;
    }

}
