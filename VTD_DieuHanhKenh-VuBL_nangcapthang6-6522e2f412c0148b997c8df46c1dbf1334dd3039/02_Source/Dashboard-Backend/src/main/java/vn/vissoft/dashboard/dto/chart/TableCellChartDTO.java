package vn.vissoft.dashboard.dto.chart;

public class TableCellChartDTO extends TableCellBaseDTO {
    private ChartDTO chart;

    public ChartDTO getChart() {
        return chart;
    }

    public void setChart(ChartDTO chart) {
        this.chart = chart;
    }
}
