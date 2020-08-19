package vn.vissoft.dashboard.dto;

import java.util.List;

public class ResultsStaffDTO {

    private Double scoreTbtt;
    private Double scoreTbm;
    private Double scoreTbrm;
    private String createTime;
    private List<BusinessResultStaffDTO> resultsStaff;

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

    public List<BusinessResultStaffDTO> getResultsStaff() {
        return resultsStaff;
    }

    public void setResultsStaff(List<BusinessResultStaffDTO> resultsStaff) {
        this.resultsStaff = resultsStaff;
    }
}
