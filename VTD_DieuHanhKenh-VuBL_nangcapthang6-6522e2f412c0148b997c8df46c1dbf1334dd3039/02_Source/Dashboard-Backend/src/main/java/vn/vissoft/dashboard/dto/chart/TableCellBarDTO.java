package vn.vissoft.dashboard.dto.chart;

public class TableCellBarDTO extends TableCellBaseDTO{
    private double mdblPerformValue;
    private double mdblPlanMonthValue;
    private double mdblPlanAccumulateValue;
    private double mdblPerformPercent;
    private String mstrPerformViewValue;
    private String mstrPlanMonthViewValue;
    private String mstrPlanAccumulateViewValue;
    private String mstrPerformPercentView;
    private String mstrStackLineColor;

    public double getPerformValue() {
        return mdblPerformValue;
    }

    public void setPerformValue(double pdblPerformValue) {
        this.mdblPerformValue = pdblPerformValue;
    }

    public double getPlanMonthValue() {
        return mdblPlanMonthValue;
    }

    public void setPlanMonthValue(double pdblPlanMonthValue) {
        this.mdblPlanMonthValue = pdblPlanMonthValue;
    }

    public double getPlanAccumulateValue() {
        return mdblPlanAccumulateValue;
    }

    public void setPlanAccumulateValue(double pdblPlanAccumulateValue) {
        this.mdblPlanAccumulateValue = pdblPlanAccumulateValue;
    }

    public double getPerformPercent() {
        return mdblPerformPercent;
    }

    public void setPerformPercent(double pdblPerformPercent) {
        this.mdblPerformPercent = pdblPerformPercent;
    }

    public String getPerformViewValue() {
        return mstrPerformViewValue;
    }

    public void setPerformViewValue(String pstrPerformViewValue) {
        this.mstrPerformViewValue = pstrPerformViewValue;
    }

    public String getPlanMonthViewValue() {
        return mstrPlanMonthViewValue;
    }

    public void setPlanMonthViewValue(String pstrPlanMonthViewValue) {
        this.mstrPlanMonthViewValue = pstrPlanMonthViewValue;
    }

    public String getPlanAccumulateViewValue() {
        return mstrPlanAccumulateViewValue;
    }

    public void setPlanAccumulateViewValue(String pstrPlanAccumulateViewValue) {
        this.mstrPlanAccumulateViewValue = pstrPlanAccumulateViewValue;
    }

    public String getPerformPercentView() {
        return mstrPerformPercentView;
    }

    public void setPerformPercentView(String pstrPerformPercentView) {
        this.mstrPerformPercentView = pstrPerformPercentView;
    }

    public String getStackLineColor() {
        return mstrStackLineColor;
    }

    public void setStackLineColor(String pstrStackLineColor) {
        this.mstrStackLineColor = pstrStackLineColor;
    }

}
