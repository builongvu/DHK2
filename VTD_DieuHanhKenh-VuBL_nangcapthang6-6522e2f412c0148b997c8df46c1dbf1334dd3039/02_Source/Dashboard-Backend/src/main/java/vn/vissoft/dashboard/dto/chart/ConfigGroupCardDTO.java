package vn.vissoft.dashboard.dto.chart;

import java.util.Objects;

public class ConfigGroupCardDTO {

    private Long mlngGroupId;
    private String mstrGroupCode;
    private String mstrDefaultCycle;
    private String mstrGroupName;
    private String mstrGroupNameI18n;
    private String vdsChannelCode;
    private String shopCode;
    private Long channelId;
    private String shopName;
    private String defaultCycleName;
    private String vdsChannelName;

    public Long getGroupId() {
        return mlngGroupId;
    }

    public void setGroupId(Long groupId) {
        this.mlngGroupId = groupId;
    }

    public String getGroupCode() {
        return mstrGroupCode;
    }

    public void setGroupCode(String groupCode) {
        this.mstrGroupCode = groupCode;
    }

    public String getDefaultCycle() {
        return mstrDefaultCycle;
    }

    public void setDefaultCycle(String defaultCycle) {
        this.mstrDefaultCycle = defaultCycle;
    }

    public String getGroupName() {
        return mstrGroupName;
    }

    public void setGroupName(String groupName) {
        this.mstrGroupName = groupName;
    }

    public String getGroupNameI18n() {
        return mstrGroupNameI18n;
    }

    public void setGroupNameI18n(String groupNameI18n) {
        this.mstrGroupNameI18n = groupNameI18n;
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

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getDefaultCycleName() {
        return defaultCycleName;
    }

    public void setDefaultCycleName(String defaultCycleName) {
        this.defaultCycleName = defaultCycleName;
    }

    public String getVdsChannelName() {
        return vdsChannelName;
    }

    public void setVdsChannelName(String vdsChannelName) {
        this.vdsChannelName = vdsChannelName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfigGroupCardDTO that = (ConfigGroupCardDTO) o;
        return Objects.equals(mstrDefaultCycle, that.mstrDefaultCycle) &&
                Objects.equals(mstrGroupName, that.mstrGroupName) &&
                Objects.equals(vdsChannelCode, that.vdsChannelCode) &&
                Objects.equals(shopCode, that.shopCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mstrDefaultCycle, mstrGroupName, vdsChannelCode, shopCode);
    }
}
