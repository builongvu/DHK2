package vn.vissoft.dashboard.dto.chart;

public class TableCellGrowthDTO extends TableCellBaseDTO {
    private int isGrowth;
    private  String delta;

    public int getIsGrowth() {
        return isGrowth;
    }

    public void setIsGrowth(int isGrowth) {
        this.isGrowth = isGrowth;
    }

    public String getDelta() {
        return delta;
    }

    public void setDelta(String delta) {
        this.delta = delta;
    }
}
