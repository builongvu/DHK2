package vn.vissoft.dashboard.dto;

public class BusinessResultDetailDTO {

    private String serviceName;
    private Double scheduleMonth;
    private Double performAccumulatedN1;
    private Double performAccumulatedN;
    private Double delta;
    private Double complete;
    private Double scorePass;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Double getScheduleMonth() {
        return scheduleMonth;
    }

    public void setScheduleMonth(Double scheduleMonth) {
        this.scheduleMonth = scheduleMonth;
    }

    public Double getPerformAccumulatedN1() {
        return performAccumulatedN1;
    }

    public void setPerformAccumulatedN1(Double performAccumulatedN1) {
        this.performAccumulatedN1 = performAccumulatedN1;
    }

    public Double getPerformAccumulatedN() {
        return performAccumulatedN;
    }

    public void setPerformAccumulatedN(Double performAccumulatedN) {
        this.performAccumulatedN = performAccumulatedN;
    }

    public Double getDelta() {
        return delta;
    }

    public void setDelta(Double delta) {
        this.delta = delta;
    }

    public Double getComplete() {
        return complete;
    }

    public void setComplete(Double complete) {
        this.complete = complete;
    }

    public Double getScorePass() {
        return scorePass;
    }

    public void setScorePass(Double scorePass) {
        this.scorePass = scorePass;
    }
}
