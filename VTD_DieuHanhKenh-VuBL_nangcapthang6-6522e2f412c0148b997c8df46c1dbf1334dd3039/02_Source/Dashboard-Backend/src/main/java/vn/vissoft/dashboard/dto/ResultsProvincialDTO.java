package vn.vissoft.dashboard.dto;

import java.util.List;

public class ResultsProvincialDTO {

    private Double scoreTbtt;
    private Double scoreTbm;
    private Double scoreTbrm;
    private String createTime;
    private List<BusinessResultProvincialDTO> resultsProvincial;

    public Double getScoreTbtt() {
        return scoreTbtt;
    }

    public void setScoreTbtt(Double scoreTbtt) {
        this.scoreTbtt = scoreTbtt;
    }

    public Double getScoreTbm() {
        return scoreTbm;
    }

    public void setScoreTbm(Double scoreTbm) {
        this.scoreTbm = scoreTbm;
    }

    public Double getScoreTbrm() {
        return scoreTbrm;
    }

    public void setScoreTbrm(Double scoreTbrm) {
        this.scoreTbrm = scoreTbrm;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public List<BusinessResultProvincialDTO> getResultsProvincial() {
        return resultsProvincial;
    }

    public void setResultsProvincial(List<BusinessResultProvincialDTO> resultsProvincial) {
        this.resultsProvincial = resultsProvincial;
    }
}
