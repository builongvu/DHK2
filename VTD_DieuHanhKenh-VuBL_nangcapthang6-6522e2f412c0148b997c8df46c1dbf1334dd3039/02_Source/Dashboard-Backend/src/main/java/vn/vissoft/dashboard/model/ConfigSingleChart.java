package vn.vissoft.dashboard.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "CONFIG_SINGLE_CHART")
public class ConfigSingleChart {

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

    @Column(name = "CHART_ID")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getChartId() {
        return chartId;
    }

    public void setChartId(Long plngChartId) {
        this.chartId = plngChartId;
    }

    @Basic
    @Column(name = "CARD_ID")
    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long plngCardId) {
        this.cardId = plngCardId;
    }

    @Basic
    @Column(name = "CHART_TYPE")
    public String getChartType() {
        return chartType;
    }

    public void setChartType(String pstrChartType) {
        this.chartType = pstrChartType;
    }

    @Basic
    @Column(name = "CHART_SIZE")
    public String getChartSize() {
        return chartSize;
    }

    public void setChartSize(String pstrChartSize) {
        this.chartSize = pstrChartSize;
    }

    @Basic
    @Column(name = "META_DATA")
    public String getMetaData() {
        return metaData;
    }

    public void setMetaData(String pstrMetaData) {
        this.metaData = pstrMetaData;
    }

    @Basic
    @Column(name = "SERVICE_IDS")
    public String getServiceIds() {
        return serviceIds;
    }

    public void setServiceIds(String pstrServiceIds) {
        this.serviceIds = pstrServiceIds;
    }

    @Basic
    @Column(name = "QUERY_DATA")
    public String getQueryData() {
        return queryData;
    }

    public void setQueryData(String pstrQueryData) {
        this.queryData = pstrQueryData;
    }

    @Basic
    @Column(name = "TITLE")
    public String getTitle() {
        return title;
    }

    public void setTitle(String pstrTitle) {
        this.title = pstrTitle;
    }

    @Basic
    @Column(name = "TITLE_I18N")
    public String getTitleI18n() {
        return titleI18n;
    }

    public void setTitleI18n(String pstrTitleI18n) {
        this.titleI18n = pstrTitleI18n;
    }

    @Basic
    @Column(name = "SUBTITLE")
    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String pstrSubtitle) {
        this.subtitle = pstrSubtitle;
    }

    @Basic
    @Column(name = "SUBTITLE_I18N")
    public String getSubtitleI18n() {
        return subtitleI18n;
    }

    public void setSubtitleI18n(String pstrSubtitleI18n) {
        this.subtitleI18n = pstrSubtitleI18n;
    }

    @Basic
    @Column(name = "DRILLDOWN")
    public Integer getDrilldown() {
        return drilldown;
    }

    public void setDrilldown(Integer pintDrillDown) {
        this.drilldown = pintDrillDown;
    }

    @Basic
    @Column(name = "ZOOM")
    public Integer getZoom() {
        return zoom;
    }

    public void setZoom(Integer pintZoom) {
        this.zoom = pintZoom;
    }

    @Basic
    @Column(name = "EXPAND")
    public Integer getExpand() {
        return expand;
    }

    public void setExpand(Integer pintExpand) {
        this.expand = pintExpand;
    }

    @Basic
    @Column(name = "CREATE_DATE")
    public Long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Long plngCreateDate) {
        this.createDate = plngCreateDate;
    }

    @Basic
    @Column(name = "STATUS")
    public Long getStatus() {
        return status;
    }

    public void setStatus(Long plngStatus) {
        this.status = plngStatus;
    }

    @Basic
    @Column(name = "QUERY_PARAM")
    public String getQueryParam() {
        return queryParam;
    }

    public void setQueryParam(String queryParam) {
        this.queryParam = queryParam;
    }

    @Basic
    @Column(name = "DRILLDOWN_TYPE")
    public Integer getDrillDownType() {
        return drillDownType;
    }

    public void setDrillDownType(Integer drillDownType) {
        this.drillDownType = drillDownType;
    }

    @Basic
    @Column(name = "DRILLDOWN_OBJECT_ID")
    public Long getDrillDownObjectId() {
        return drillDownObjectId;
    }

    public void setDrillDownObjectId(Long drillDownObjectId) {
        this.drillDownObjectId = drillDownObjectId;
    }
}

