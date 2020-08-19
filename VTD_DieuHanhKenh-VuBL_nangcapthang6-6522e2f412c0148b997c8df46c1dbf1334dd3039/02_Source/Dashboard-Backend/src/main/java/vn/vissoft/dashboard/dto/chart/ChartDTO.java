package vn.vissoft.dashboard.dto.chart;

import java.util.List;

public class ChartDTO extends ContentBaseDTO {
    private List<SeriesDTO> mlstSeries;
    private String mstrChartType;
    private String mstrTitle;
    private String mstrSubTitle;
    private List<String> mlstCategories;
    private int chartId;
    private boolean noData;


    public List<SeriesDTO> getSeries() {
        return mlstSeries;
    }

    public void setSeries(List<SeriesDTO> plstSeries) {
        this.mlstSeries = plstSeries;
    }

    public String getChartType() {
        return mstrChartType;
    }

    public void setChartType(String pstrChartType) {
        this.mstrChartType = pstrChartType;
    }

    public String getTitle() {
        return mstrTitle;
    }

    public void setTitle(String pstrTitle) {
        this.mstrTitle = pstrTitle;
    }

    public String getSubTitle() {
        return mstrSubTitle;
    }

    public void setSubTitle(String pstrSubTitle) {
        this.mstrSubTitle = pstrSubTitle;
    }

    public List<String> getCategories() {
        return mlstCategories;
    }

    public void setCategories(List<String> plstCategories) {
        this.mlstCategories = plstCategories;
    }

    public int getChartId() {
        return chartId;
    }

    public void setChartId(int chartId) {
        this.chartId = chartId;
    }

    public boolean isNoData() {
        return noData;
    }

    public void setNoData(boolean noData) {
        this.noData = noData;
    }
}
