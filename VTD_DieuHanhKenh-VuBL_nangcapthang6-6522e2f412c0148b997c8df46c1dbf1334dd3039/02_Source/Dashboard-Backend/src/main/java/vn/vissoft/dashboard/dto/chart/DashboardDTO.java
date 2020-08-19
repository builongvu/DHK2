package vn.vissoft.dashboard.dto.chart;

import vn.vissoft.dashboard.dto.DashboardRequestDTO;

import java.util.List;

public class DashboardDTO {
    private Long groupId;
    private String vdsChannelCode;
    private List<CardDTO> cards;
    private String defaultCycle;
    private String groupName;
    private String subGroupName;
    private DashboardRequestDTO filterMetaData;
    private boolean lvThree;
    private String shopOfStaff;

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getVdsChannelCode() {
        return vdsChannelCode;
    }

    public void setVdsChannelCode(String vdsChannelCode) {
        this.vdsChannelCode = vdsChannelCode;
    }

    public List<CardDTO> getCards() {
        return cards;
    }

    public void setCards(List<CardDTO> cards) {
        this.cards = cards;
    }

    public String getDefaultCycle() {
        return defaultCycle;
    }

    public void setDefaultCycle(String defaultCycle) {
        this.defaultCycle = defaultCycle;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public DashboardRequestDTO getFilterMetaData() {
        return filterMetaData;
    }

    public void setFilterMetaData(DashboardRequestDTO filterMetaData) {
        this.filterMetaData = filterMetaData;
    }

    public String getSubGroupName() {
        return subGroupName;
    }

    public void setSubGroupName(String subGroupName) {
        this.subGroupName = subGroupName;
    }

    public boolean isLvThree() {
        return lvThree;
    }

    public void setLvThree(boolean lvThree) {
        this.lvThree = lvThree;
    }

    public String getShopOfStaff() {
        return shopOfStaff;
    }

    public void setShopOfStaff(String shopOfStaff) {
        this.shopOfStaff = shopOfStaff;
    }
}
