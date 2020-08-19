package vn.vissoft.dashboard.dto.chart;

import java.util.List;

public class ContentTopLeftDTO extends ContentBaseDTO {

    private List<TopDataDTO> mlstListTop;
    private String mstrLink;
    private String mstrTitle;

    public String getTitle() {
        return mstrTitle;
    }

    public void setTitle(String mstrTitle) {
        this.mstrTitle = mstrTitle;
    }

    public String getLink() {
        return mstrLink;
    }

    public void setLink(String mstrLink) {
        this.mstrLink = mstrLink;
    }

    public List<TopDataDTO> getListTop() {
        return mlstListTop;
    }

    public void setListTop(List<TopDataDTO> plstListTop) {
        this.mlstListTop = plstListTop;
    }
}
