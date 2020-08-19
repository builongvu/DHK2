package vn.vissoft.dashboard.dto.chart;

import vn.vissoft.dashboard.dto.BaseDTO;
import vn.vissoft.dashboard.helper.DataUtil;

import java.util.List;
import java.util.Objects;

public class ConfigSingleChartDTO extends BaseDTO {

    private Long chartId;
    private Long cardId;
    private String chartType;
    private String chartSize;
    private String metaData;
    private String serviceIds;
    private String queryData;
    private String title;
    private String titleI18n;
    private String subtitle;
    private String subtitleI18n;
    private Integer drilldown;
    private Integer zoom;
    private Integer expand;
    private Long createDate;
    private Long status;
    private String queryParam;
    private Integer drillDownType;
    private Long drillDownObjectId;
    private List<Long> lstServiceIds;
    private String cardName;
    private String groupName;
    private String chartSizeName;
    private String showStatus;
    private String showDrillDown;
    private String showExpand;
    private String chartTypeName;

    public Long getChartId() {
        return chartId;
    }

    public void setChartId(Long chartId) {
        this.chartId = chartId;
    }

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public String getChartType() {
        return chartType;
    }

    public void setChartType(String chartType) {
        this.chartType = chartType;
    }

    public String getChartSize() {
        return chartSize;
    }

    public void setChartSize(String chartSize) {
        this.chartSize = chartSize;
    }

    public String getMetaData() {
        return metaData;
    }

    public void setMetaData(String metaData) {
        this.metaData = metaData;
    }

    public String getServiceIds() {
        return serviceIds;
    }

    public void setServiceIds(String serviceIds) {
        this.serviceIds = serviceIds;
    }

    public String getQueryData() {
        return queryData;
    }

    public void setQueryData(String queryData) {
        this.queryData = queryData;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitleI18n() {
        return titleI18n;
    }

    public void setTitleI18n(String titleI18n) {
        this.titleI18n = titleI18n;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getSubtitleI18n() {
        return subtitleI18n;
    }

    public void setSubtitleI18n(String subtitleI18n) {
        this.subtitleI18n = subtitleI18n;
    }

    public Integer getDrilldown() {
        return drilldown;
    }

    public void setDrilldown(Integer drilldown) {
        this.drilldown = drilldown;
    }

    public Integer getZoom() {
        return zoom;
    }

    public void setZoom(Integer zoom) {
        this.zoom = zoom;
    }

    public Integer getExpand() {
        return expand;
    }

    public void setExpand(Integer expand) {
        this.expand = expand;
    }

    public Long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Long createDate) {
        this.createDate = createDate;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public String getQueryParam() {
        return queryParam;
    }

    public void setQueryParam(String queryParam) {
        this.queryParam = queryParam;
    }

    public Integer getDrillDownType() {
        return drillDownType;
    }

    public void setDrillDownType(Integer drillDownType) {
        this.drillDownType = drillDownType;
    }

    public Long getDrillDownObjectId() {
        return drillDownObjectId;
    }

    public void setDrillDownObjectId(Long drillDownObjectId) {
        this.drillDownObjectId = drillDownObjectId;
    }

    public List<Long> getLstServiceIds() {
        return lstServiceIds;
    }

    public void setLstServiceIds(List<Long> lstServiceIds) {
        this.lstServiceIds = lstServiceIds;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getChartSizeName() {
        return chartSizeName;
    }

    public void setChartSizeName(String chartSizeName) {
        this.chartSizeName = chartSizeName;
    }

    public String getShowStatus() {
        return showStatus;
    }

    public void setShowStatus(String showStatus) {
        this.showStatus = showStatus;
    }

    public String getShowDrillDown() {
        return showDrillDown;
    }

    public void setShowDrillDown(String showDrillDown) {
        this.showDrillDown = showDrillDown;
    }

    public String getShowExpand() {
        return showExpand;
    }

    public void setShowExpand(String showExpand) {
        this.showExpand = showExpand;
    }

    public String getChartTypeName() {
        return chartTypeName;
    }

    public void setChartTypeName(String chartTypeName) {
        this.chartTypeName = chartTypeName;
    }

    public ConfigSingleChartDTO(Object[] object) {
        this.chartId = (Long) object[0];
        this.cardId = (Long) object[1];
        this.chartSize = String.valueOf(object[2]);
        this.chartType = String.valueOf(object[3]);
        this.createDate = (Long) object[4];
        this.drilldown = (int) object[5];
        this.expand = (int) object[6];
        this.metaData = String.valueOf(object[7]);
        this.queryData = String.valueOf(object[8]);
        this.serviceIds = String.valueOf(object[9]);
        this.status = (Long) object[10];
        this.subtitle = String.valueOf(object[11]);
        this.subtitleI18n = String.valueOf(object[12]);
        this.title = String.valueOf(object[13]);
        this.titleI18n = String.valueOf(object[14]);
        this.zoom = (int) object[15];
    }

    public ConfigSingleChartDTO(Long mlngChartId, Long mlngCardId, String mstrChartType, String mstrChartSize, String mstrMetaData, String mstrServiceIds, String mstrQueryData, String mstrTitle, String mstrTitleI18n, String mstrSubtitle, String mstrSubtitleI18n, int mintDrilldown, int mintZoom, int mintExpand, Long mlngCreateDate, Long mlngStatus, String queryParam, int drillDownType, Long drillDownObjectId) {
        this.chartId = mlngChartId;
        this.cardId = mlngCardId;
        this.chartType = mstrChartType;
        this.chartSize = mstrChartSize;
        this.metaData = mstrMetaData;
        this.serviceIds = mstrServiceIds;
        this.queryData = mstrQueryData;
        this.title = mstrTitle;
        this.titleI18n = mstrTitleI18n;
        this.subtitle = mstrSubtitle;
        this.subtitleI18n = mstrSubtitleI18n;
        this.drilldown = mintDrilldown;
        this.zoom = mintZoom;
        this.expand = mintExpand;
        this.createDate = mlngCreateDate;
        this.status = mlngStatus;
        this.queryParam = queryParam;
        this.drillDownType = drillDownType;
        this.drillDownObjectId = drillDownObjectId;
    }

    public ConfigSingleChartDTO() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfigSingleChartDTO that = (ConfigSingleChartDTO) o;
        return Objects.equals(cardId, that.cardId) &&
                Objects.equals(chartType, that.chartType) &&
                Objects.equals(chartSize, that.chartSize) &&
                Objects.equals(metaData, that.metaData) &&
                Objects.equals(title, that.title) &&
                Objects.equals(drilldown, that.drilldown) &&
                Objects.equals(expand, that.expand) &&
                Objects.equals(status, that.status) &&
                Objects.equals(queryParam, that.queryParam) &&
                Objects.equals(drillDownObjectId == null ? 0 : drillDownObjectId, that.drillDownObjectId == null ? 0 : that.drillDownObjectId) &&
                Objects.equals(DataUtil.isNullOrEmpty(serviceIds)  ? "a" : serviceIds, DataUtil.isNullOrEmpty(that.serviceIds) ? "a" : that.serviceIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardId, chartType, chartSize, metaData, title, drilldown, expand, status, queryParam, drillDownObjectId, lstServiceIds);
    }
}
