package vn.vissoft.dashboard.dto.chart;

public class RenderExcelStaffDTO {

    private String staffName;
    private String accumSchedule;
    private String accumPerform;
    private String accumComplete;
    private String schedule;
    private String complete;
    private String upDown;

    public RenderExcelStaffDTO() {
    }

    public RenderExcelStaffDTO(String staffName, String accumSchedule, String accumPerform, String accumComplete, String schedule, String complete, String upDown) {
        this.staffName = staffName;
        this.accumSchedule = accumSchedule;
        this.accumPerform = accumPerform;
        this.accumComplete = accumComplete;
        this.schedule = schedule;
        this.complete = complete;
        this.upDown = upDown;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public String getAccumSchedule() {
        return accumSchedule;
    }

    public void setAccumSchedule(String accumSchedule) {
        this.accumSchedule = accumSchedule;
    }

    public String getAccumPerform() {
        return accumPerform;
    }

    public void setAccumPerform(String accumPerform) {
        this.accumPerform = accumPerform;
    }

    public String getAccumComplete() {
        return accumComplete;
    }

    public void setAccumComplete(String accumComplete) {
        this.accumComplete = accumComplete;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getComplete() {
        return complete;
    }

    public void setComplete(String complete) {
        this.complete = complete;
    }

    public String getUpDown() {
        return upDown;
    }

    public void setUpDown(String upDown) {
        this.upDown = upDown;
    }
}
