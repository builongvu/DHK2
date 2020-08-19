package vn.vissoft.dashboard.dto.chart;

import java.util.List;

public class  CardDTO {
    private Long groupId;
    private Long cardId;
    private String title;
    private String cardSize;
    private boolean drilldown;
    private int drilldownType;
    private int drilldownObject;
    private boolean zoom;
    private Long serviceId;
    private String cardType;
    private List<ContentBaseDTO> contents;

    public CardDTO() {
    }

    public CardDTO(Long groupId, Long cardId, String title, String cardSize, boolean drilldown, boolean zoom, Long serviceId, String cardType, List<ContentBaseDTO> contents) {
        this.groupId = groupId;
        this.cardId = cardId;
        this.title = title;
        this.cardSize = cardSize;
        this.drilldown = drilldown;
        this.zoom = zoom;
        this.serviceId = serviceId;
        this.cardType = cardType;
        this.contents = contents;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCardSize() {
        return cardSize;
    }

    public void setCardSize(String cardSize) {
        this.cardSize = cardSize;
    }

    public boolean isDrilldown() {
        return drilldown;
    }

    public void setDrilldown(boolean drilldown) {
        this.drilldown = drilldown;
    }

    public boolean isZoom() {
        return zoom;
    }

    public void setZoom(boolean zoom) {
        this.zoom = zoom;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public List<ContentBaseDTO> getContents() {
        return contents;
    }

    public void setContents(List<ContentBaseDTO> contents) {
        this.contents = contents;
    }

    public int getDrilldownType() {
        return drilldownType;
    }

    public void setDrilldownType(int drilldownType) {
        this.drilldownType = drilldownType;
    }

    public int getDrilldownObject() {
        return drilldownObject;
    }

    public void setDrilldownObject(int drilldownObject) {
        this.drilldownObject = drilldownObject;
    }
}
