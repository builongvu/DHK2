package vn.vissoft.dashboard.dto.excel;

import vn.vissoft.dashboard.helper.excelreader.annotation.ExcelColumn;
import vn.vissoft.dashboard.helper.excelreader.annotation.ExcelEntity;
import vn.vissoft.dashboard.helper.excelreader.annotation.IndexColumn;

import java.util.Objects;

@ExcelEntity(dataStartRowIndex = 6, signalConstant = "IMPORT_WARNING_SEND")
public class WarningSendServiceExcel extends BaseExcelEntity {

    private String mintWarningLevel;
    private String mintInformLevel;
    private String mstrServiceCode;
    private String mintSms;
    private String mintEmail;
    private int mintIndex;

    @ExcelColumn(name = "C", nullable = true)
    public String getMintWarningLevel() {
        return mintWarningLevel;
    }

    public void setMintWarningLevel(String mintWarningLevel) {
        this.mintWarningLevel = mintWarningLevel;
    }

    @ExcelColumn(name = "F", nullable = true)
    public String getMintInformLevel() {
        return mintInformLevel;
    }

    public void setMintInformLevel(String mintInformLevel) {
        this.mintInformLevel = mintInformLevel;
    }

    @ExcelColumn(name = "B", nullable = true)
    public String getMstrServiceCode() {
        return mstrServiceCode;
    }

    public void setMstrServiceCode(String mstrServiceCode) {
        this.mstrServiceCode = mstrServiceCode;
    }

    @ExcelColumn(name = "E", nullable = true)
    public String getMintSms() {
        return mintSms;
    }

    public void setMintSms(String mintSms) {
        this.mintSms = mintSms;
    }

    @ExcelColumn(name = "D", nullable = true)
    public String getMintEmail() {
        return mintEmail;
    }

    public void setMintEmail(String mintEmail) {
        this.mintEmail = mintEmail;
    }

    @IndexColumn
    public int getMintIndex() {
        return mintIndex;
    }

    public void setMintIndex(int mintIndex) {
        this.mintIndex = mintIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WarningSendServiceExcel that = (WarningSendServiceExcel) o;
        return Objects.equals(mintWarningLevel, that.mintWarningLevel) &&
                Objects.equals(mintInformLevel, that.mintInformLevel) &&
                Objects.equals(mstrServiceCode, that.mstrServiceCode) &&
                Objects.equals(mintSms, that.mintSms) &&
                Objects.equals(mintEmail, that.mintEmail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mintWarningLevel, mintInformLevel, mstrServiceCode, mintSms);
    }
}
