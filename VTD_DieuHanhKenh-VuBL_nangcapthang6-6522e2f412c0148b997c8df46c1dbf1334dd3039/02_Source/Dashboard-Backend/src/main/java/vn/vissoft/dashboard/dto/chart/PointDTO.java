package vn.vissoft.dashboard.dto.chart;

public class PointDTO {
    private String mstrCategory;
    private Double mdblValue;
    private String mstrViewValue;
    private String color;

    public PointDTO() {
    }

    public PointDTO(String mstrCategory, Double mdblValue, String mstrViewValue) {
        this.mstrCategory = mstrCategory;
        this.mdblValue = mdblValue;
        this.mstrViewValue = mstrViewValue;
    }

    public String getCategory() {
        return mstrCategory;
    }

    public void setCategory(String pstrCategory) {
        this.mstrCategory = pstrCategory;
    }

    public Double getValue() {
        return mdblValue;
    }

    public void setValue(Double pdblValue) {
        this.mdblValue = pdblValue;
    }

    public String getViewValue() {
        return mstrViewValue;
    }

    public void setViewValue(String pstrViewValue) {
        this.mstrViewValue = pstrViewValue;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
