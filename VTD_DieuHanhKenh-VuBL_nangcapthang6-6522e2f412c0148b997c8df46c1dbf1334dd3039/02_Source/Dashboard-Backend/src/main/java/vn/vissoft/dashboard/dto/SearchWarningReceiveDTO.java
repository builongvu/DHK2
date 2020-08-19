package vn.vissoft.dashboard.dto;

public class SearchWarningReceiveDTO {
    String mstrShopCode;
    String mstrStatus;
    String mintWarningLevel;
    String mintInformLevel;

    public void setMstrShopCode(String mstrShopCode) {
        this.mstrShopCode = mstrShopCode;
    }

    public void setMstrStatus(String mstrStatus) {
        this.mstrStatus = mstrStatus;
    }

    public void setMintWarningLevel(String mintWarningLevel) {
        this.mintWarningLevel = mintWarningLevel;
    }

    public void setMintInformLevel(String mintInformLevel) {
        this.mintInformLevel = mintInformLevel;
    }

    public String getMstrShopCode() {
        return mstrShopCode;
    }

    public String getMstrStatus() {
        return mstrStatus;
    }

    public String getMintWarningLevel() {
        return mintWarningLevel;
    }

    public String getMintInformLevel() {
        return mintInformLevel;
    }

    public SearchWarningReceiveDTO() {
    }
}
