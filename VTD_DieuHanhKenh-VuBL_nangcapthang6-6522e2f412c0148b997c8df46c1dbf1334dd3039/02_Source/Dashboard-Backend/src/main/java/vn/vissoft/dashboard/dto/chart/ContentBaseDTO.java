package vn.vissoft.dashboard.dto.chart;

public class ContentBaseDTO {

    private String mstrType;
    private boolean drilldown;
    private Integer drilldownType;
    private Integer drilldownObject;
    private String chartSize;
    private boolean expand;

    public String getType() {
        return mstrType;
    }

    public void setType(String pstrType) {
        this.mstrType = pstrType;
    }

    public boolean isDrilldown() {
        return drilldown;
    }

    public void setDrilldown(boolean drilldown) {
        this.drilldown = drilldown;
    }

    public Integer getDrilldownType() {
        return drilldownType;
    }

    public void setDrilldownType(Integer drilldownType) {
        this.drilldownType = drilldownType;
    }

    public Integer getDrilldownObject() {
        return drilldownObject;
    }

    public void setDrilldownObject(Integer drilldownObject) {
        this.drilldownObject = drilldownObject;
    }

    public String getChartSize() {
        return chartSize;
    }

    public void setChartSize(String chartSize) {
        this.chartSize = chartSize;
    }

    public boolean isExpand() {
        return expand;
    }

    public void setExpand(boolean expand) {
        this.expand = expand;
    }
}
