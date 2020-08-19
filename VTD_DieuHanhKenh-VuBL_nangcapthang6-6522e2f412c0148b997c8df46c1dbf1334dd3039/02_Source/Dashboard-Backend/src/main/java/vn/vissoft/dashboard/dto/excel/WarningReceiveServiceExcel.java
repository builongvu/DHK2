package vn.vissoft.dashboard.dto.excel;

import vn.vissoft.dashboard.helper.excelreader.annotation.ExcelColumn;
import vn.vissoft.dashboard.helper.excelreader.annotation.ExcelEntity;

import java.util.Objects;

@ExcelEntity(dataStartRowIndex = 6, signalConstant = "IMPORT_WARNING_RECEIVE")
public class WarningReceiveServiceExcel extends BaseExcelEntity{

    private String mintWarningLevel;
    private String mintInformLevel;
    private String mstrShopCode;

    @ExcelColumn(name = "C",nullable = true)
    public String getMintWarningLevel() {
        return mintWarningLevel;
    }

    public void setMintWarningLevel(String mintWarningLevel) {
        this.mintWarningLevel = mintWarningLevel;
    }

    @ExcelColumn(name = "D",nullable = true)
    public String getMintInformLevel() {
        return mintInformLevel;
    }

    public void setMintInformLevel(String mintInformLevel) {
        this.mintInformLevel = mintInformLevel;
    }

    @ExcelColumn(name = "B")
    public String getMstrShopCode() {
        return mstrShopCode;
    }

    public void setMstrShopCode(String mstrShopCode) {
        this.mstrShopCode = mstrShopCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WarningReceiveServiceExcel that = (WarningReceiveServiceExcel) o;
        return Objects.equals(mintWarningLevel, that.mintWarningLevel) &&
                Objects.equals(mintInformLevel, that.mintInformLevel) &&
                Objects.equals(mstrShopCode, that.mstrShopCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mintWarningLevel, mintInformLevel, mstrShopCode);
    }
}
