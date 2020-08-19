package vn.vissoft.dashboard.dto;

public class SearchWarningSendDTO extends BaseDTO{
    Integer serviceId;
    String WarningLevel;
    String Email;
    String Sms;
    String InformLevel;
    String status;

    public SearchWarningSendDTO() {
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public String getWarningLevel() {
        return WarningLevel;
    }

    public String getEmail() {
        return Email;
    }

    public String getSms() {
        return Sms;
    }

    public String getInformLevel() {
        return InformLevel;
    }

    public String getStatus() {
        return status;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public void setWarningLevel(String warningLevel) {
        WarningLevel = warningLevel;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public void setSms(String sms) {
        Sms = sms;
    }

    public void setInformLevel(String informLevel) {
        InformLevel = informLevel;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
