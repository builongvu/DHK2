package vn.vissoft.dashboard.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "CONFIG_GROUP_CARD")
public class ConfigGroupCard {

    private Long mlngGroupId;
    private String vdsChannelCode;
    private String mstrGroupCode;
    private String mstrGroupName;
    private String mstrGroupNameI18n;
    private String mstrDefaultCycle;
    private String shopCode;
    private Long channelId;

    @Column(name = "GROUP_ID")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getGroupId() {
        return mlngGroupId;
    }


    public void setGroupId(Long plngId) {
        this.mlngGroupId = plngId;
    }

    @Basic
    @Column(name = "VDS_CHANNEL_CODE")
    public String getVdsChannelCode() {
        return vdsChannelCode;
    }

    public void setVdsChannelCode(String vdsChannelCode) {
        this.vdsChannelCode = vdsChannelCode;
    }

    @Basic
    @Column(name = "SHOP_CODE")
    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }

    @Basic
    @Column(name = "GROUP_CODE")
    public String getGroupCode() {
        return mstrGroupCode;
    }

    public void setGroupCode(String pstrCode) {
        this.mstrGroupCode = pstrCode;
    }

    @Basic
    @Column(name = "GROUP_NAME")
    public String getGroupName() {
        return mstrGroupName;
    }

    public void setGroupName(String pstrName) {
        this.mstrGroupName = pstrName;
    }

    @Basic
    @Column(name = "GROUP_NAME_I18N")
    public String getGroupNameI18n() {
        return mstrGroupNameI18n;
    }

    public void setGroupNameI18n(String pstrGroupNameI18n) {
        this.mstrGroupNameI18n = pstrGroupNameI18n;
    }

    @Basic
    @Column(name = "DEFAULT_CYCLE")
    public String getDefaultCycle() {
        return mstrDefaultCycle;
    }

    public void setDefaultCycle(String pstrDefaultCycle) {
        this.mstrDefaultCycle = pstrDefaultCycle;
    }

    @Basic
    @Column(name = "CHANNEL_ID")
    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }
}
