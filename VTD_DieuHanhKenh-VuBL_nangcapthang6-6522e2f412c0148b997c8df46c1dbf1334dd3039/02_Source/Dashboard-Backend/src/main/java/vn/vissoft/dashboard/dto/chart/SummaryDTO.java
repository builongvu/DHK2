package vn.vissoft.dashboard.dto.chart;

public class SummaryDTO {

    private String mstrViewPercent;
    private Integer mbigNum;
    private String mstrColor;
    private boolean blnIsTarget;
    private String mstrName;

    public String getViewPercent() {
        return mstrViewPercent;
    }

    public void setViewPercent(String mstrViewPercent) {
        this.mstrViewPercent = mstrViewPercent;
    }

    public Integer getNum() {
        return mbigNum;
    }

    public void setNum(Integer mbigNum) {
        this.mbigNum = mbigNum;
    }

    public String getColor() {
        return mstrColor;
    }

    public void setColor(String mstrColor) {
        this.mstrColor = mstrColor;
    }

    public boolean getIsTarget() {
        return blnIsTarget;
    }

    public void setIsTarget(boolean blnIsTarget) {
        this.blnIsTarget = blnIsTarget;
    }

    public String getName() {
        return mstrName;
    }

    public void setName(String mstrName) {
        this.mstrName = mstrName;
    }
}
