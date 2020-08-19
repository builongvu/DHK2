package vn.vissoft.dashboard.dto;

import java.util.List;

public class BusinessResultProvincialDTO {

    private String provincialName;
    private String provincialCode;
    private Double totalScore;
    private Integer rank;
    private List<BusinessResultDetailDTO> listDetail;

    public String getProvincialName() {
        return provincialName;
    }

    public void setProvincialName(String provincialName) {
        this.provincialName = provincialName;
    }

    public String getProvincialCode() {
        return provincialCode;
    }

    public void setProvincialCode(String provincialCode) {
        this.provincialCode = provincialCode;
    }

    public Double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Double totalScore) {
        this.totalScore = totalScore;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public List<BusinessResultDetailDTO> getListDetail() {
        return listDetail;
    }

    public void setListDetail(List<BusinessResultDetailDTO> listDetail) {
        this.listDetail = listDetail;
    }
}
