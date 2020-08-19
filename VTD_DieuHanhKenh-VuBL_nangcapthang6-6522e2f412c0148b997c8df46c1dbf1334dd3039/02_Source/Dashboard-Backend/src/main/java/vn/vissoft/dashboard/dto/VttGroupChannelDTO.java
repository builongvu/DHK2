package vn.vissoft.dashboard.dto;

import java.math.BigInteger;

public class VttGroupChannelDTO extends BaseDTO{
    private String groupChannelCode;
    private String groupChannelName;
    private BigInteger positionId;
    private String positionCode;
    private String positionName;
    private Long channelTypeId;
    private String classification;
    private String status;

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

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public BigInteger getPositionId() {
        return positionId;
    }

    public void setPositionId(BigInteger positionId) {
        this.positionId = positionId;
    }

    public String getPositionCode() {
        return positionCode;
    }

    public void setPositionCode(String positionCode) {
        this.positionCode = positionCode;
    }

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public Long getChannelTypeId() {
        return channelTypeId;
    }

    public void setChannelTypeId(Long channelTypeId) {
        this.channelTypeId = channelTypeId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
