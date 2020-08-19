package vn.vissoft.dashboard.dto;

import java.sql.Timestamp;

public class MappingGroupChannelDTO extends BaseDTO {
    private Long groupId;
    private String groupChannelCode;
    private String groupChannelName;
    private String vdsChannelCode;
    private String vdsChannelName;
    private String status;
    private String user;
    private Timestamp createDate;

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getGroupChannelCode() {
        return groupChannelCode;
    }

    public void setGroupChannelCode(String groupChannelCode) {
        this.groupChannelCode = groupChannelCode;
    }

    public String getGroupChannelName() {
        return groupChannelName;
    }

    public void setGroupChannelName(String groupChannelName) {
        this.groupChannelName = groupChannelName;
    }

    public String getVdsChannelCode() {
        return vdsChannelCode;
    }

    public void setVdsChannelCode(String vdsChannelCode) {
        this.vdsChannelCode = vdsChannelCode;
    }

    public String getVdsChannelName() {
        return vdsChannelName;
    }

    public void setVdsChannelName(String vdsChannelName) {
        this.vdsChannelName = vdsChannelName;
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

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }
}
