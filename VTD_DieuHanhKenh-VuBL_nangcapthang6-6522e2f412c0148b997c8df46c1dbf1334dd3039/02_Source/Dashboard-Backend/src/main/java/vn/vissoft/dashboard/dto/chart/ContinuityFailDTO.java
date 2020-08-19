package vn.vissoft.dashboard.dto.chart;

public class ContinuityFailDTO extends ContentBaseDTO {

    private String percent;
    private String quantity;

    public ContinuityFailDTO() {
    }

    public String getPercent() {
        return percent;
    }

    public void setPercent(String percent) {
        this.percent = percent;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
