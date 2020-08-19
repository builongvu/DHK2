package vn.vissoft.dashboard.dto.chart;

import vn.vissoft.dashboard.dto.chart.SummaryDTO;

import java.util.List;

public class ContentTopRightDTO extends ContentBaseDTO {

    private List<SummaryDTO> mlstSummary;
    private String mstrImage;

    public String getImage() {
        return mstrImage;
    }

    public void setImage(String mstrImage) {
        this.mstrImage = mstrImage;
    }

    public List<SummaryDTO> getMlstSummary() {
        return mlstSummary;
    }

    public void setMlstSummary(List<SummaryDTO> mlstSummary) {
        this.mlstSummary = mlstSummary;
    }
}
