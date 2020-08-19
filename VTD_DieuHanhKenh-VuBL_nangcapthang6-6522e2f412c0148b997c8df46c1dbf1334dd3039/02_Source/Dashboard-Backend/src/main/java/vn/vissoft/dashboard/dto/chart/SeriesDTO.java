package vn.vissoft.dashboard.dto.chart;

import java.util.List;

public class SeriesDTO {
    private List<PointDTO> mlstPoints;
    private boolean mblnIsAverage;
    private String mstrColor;
    private String mstrUnitType;
    private String mstrChartType;
    private String mstrTitle;

    public SeriesDTO(List<PointDTO> mlstPoints, boolean mblnIsAverage, String mstrColor, String mstrUnitType, String mstrChartType, String mstrTitle) {
        this.mlstPoints = mlstPoints;
        this.mblnIsAverage = mblnIsAverage;
        this.mstrColor = mstrColor;
        this.mstrUnitType = mstrUnitType;
        this.mstrChartType = mstrChartType;
        this.mstrTitle = mstrTitle;
    }

    public SeriesDTO() {
    }

    public List<PointDTO> getPoints() {
        return mlstPoints;
    }

    public void setPoints(List<PointDTO> plstPoints) {
        this.mlstPoints = plstPoints;
    }

    public boolean isAverage() {
        return mblnIsAverage;
    }

    public void setAverage(boolean pblnAverage) {
        mblnIsAverage = pblnAverage;
    }

    public String getColor() {
        return mstrColor;
    }

    public void setColor(String pstrColor) {
        this.mstrColor = pstrColor;
    }

    public String getUnitType() {
        return mstrUnitType;
    }

    public void setUnitType(String pstrUnitType) {
        this.mstrUnitType = pstrUnitType;
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

}
