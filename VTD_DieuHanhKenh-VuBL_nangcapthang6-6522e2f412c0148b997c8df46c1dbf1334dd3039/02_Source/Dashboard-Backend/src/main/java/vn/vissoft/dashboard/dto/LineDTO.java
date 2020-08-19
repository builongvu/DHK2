package vn.vissoft.dashboard.dto;

public class LineDTO {

    private String title;
    private String color;
    private String isAverage;
    private String value;
    private int cycle;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getAverage() {
        return isAverage;
    }

    public void setAverage(String average) {
        isAverage = average;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getCycle() {
        return cycle;
    }

    public void setCycle(int cycle) {
        this.cycle = cycle;
    }
}
