package vn.vissoft.dashboard.dto.chart;

public class ConfigSingleCardDTO {

    private Long mlngCardId;
    private String mstrCardName;
    private String mstrCardNameI18n;
    private String mstrCardSize;
    private String mdtCreateDate;
    private Integer mintDrilldown;
    private Integer drillDownType;
    private Integer drillDownObjectId;
    private String mstrCardType;
    private Integer groupId;
    private Long mlngServiceId;
    private Integer mintStatus;
    private Integer mintZoom;
    private String sizeName;
    private String groupName;
    private String nameCardType;
    private String showZoom;
    private String serviceName;
    private String showDrillDown;
    private String showDrillDownObjectId;
    private String showStatus;
    private int cardOrder;

    public Long getCardId() {
        return mlngCardId;
    }

    public void setCardId(Long cardId) {
        this.mlngCardId = cardId;
    }

    public String getCardName() {
        return mstrCardName;
    }

    public void setCardName(String cardName) {
        this.mstrCardName = cardName;
    }

    public String getCardNameI18n() {
        return mstrCardNameI18n;
    }

    public void setCardNameI18n(String cardNameI18n) {
        this.mstrCardNameI18n = cardNameI18n;
    }

    public String getCardSize() {
        return mstrCardSize;
    }

    public void setCardSize(String cardSize) {
        this.mstrCardSize = cardSize;
    }

    public String getCreateDate() {
        return mdtCreateDate;
    }

    public void setCreateDate(String createDate) {
        this.mdtCreateDate = createDate;
    }

    public Integer getDrilldown() {
        return mintDrilldown;
    }

    public void setDrilldown(Integer drillDown) {
        this.mintDrilldown = drillDown;
    }

    public String getCardType() {
        return mstrCardType;
    }

    public void setCardType(String cardType) {
        this.mstrCardType = cardType;
    }

    public Long getServiceId() {
        return mlngServiceId;
    }

    public void setServiceId(Long serviceId) {
        this.mlngServiceId = serviceId;
    }

    public Integer getStatus() {
        return mintStatus;
    }

    public void setStatus(Integer mintStatus) {
        this.mintStatus = mintStatus;
    }

    public Integer getZoom() {
        return mintZoom;
    }

    public void setZoom(Integer zoom) {
        this.mintZoom = zoom;
    }

    public Integer getDrillDownType() {
        return drillDownType;
    }

    public void setDrillDownType(Integer drillDownType) {
        this.drillDownType = drillDownType;
    }

    public Integer getDrillDownObjectId() {
        return drillDownObjectId;
    }

    public void setDrillDownObjectId(Integer drillDownObjectId) {
        this.drillDownObjectId = drillDownObjectId;
    }

    public String getSizeName() {
        return sizeName;
    }

    public void setSizeName(String sizeName) {
        this.sizeName = sizeName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getNameCardType() {
        return nameCardType;
    }

    public void setNameCardType(String nameCardType) {
        this.nameCardType = nameCardType;
    }

    public String getShowZoom() {
        return showZoom;
    }

    public void setShowZoom(String showZoom) {
        this.showZoom = showZoom;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getShowDrillDown() {
        return showDrillDown;
    }

    public void setShowDrillDown(String showDrillDown) {
        this.showDrillDown = showDrillDown;
    }

    public String getShowStatus() {
        return showStatus;
    }

    public void setShowStatus(String showStatus) {
        this.showStatus = showStatus;
    }

    public String getShowDrillDownObjectId() {
        return showDrillDownObjectId;
    }

    public void setShowDrillDownObjectId(String showDrillDownObjectId) {
        this.showDrillDownObjectId = showDrillDownObjectId;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public int getCardOrder() {
        return cardOrder;
    }

    public void setCardOrder(int cardOrder) {
        this.cardOrder = cardOrder;
    }
}
