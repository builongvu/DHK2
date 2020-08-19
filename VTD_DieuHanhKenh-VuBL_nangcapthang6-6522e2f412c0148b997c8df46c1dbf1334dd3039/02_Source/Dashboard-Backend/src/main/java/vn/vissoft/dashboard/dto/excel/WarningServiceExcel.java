package vn.vissoft.dashboard.dto.excel;

import vn.vissoft.dashboard.helper.excelreader.annotation.ExcelColumn;
import vn.vissoft.dashboard.helper.excelreader.annotation.ExcelEntity;
import vn.vissoft.dashboard.helper.excelreader.annotation.IndexColumn;

import java.util.Objects;

@ExcelEntity(dataStartRowIndex = 6, signalConstant = "IMPORT_SERVICE_WARNING_CONFIG")
public class WarningServiceExcel extends BaseExcelEntity{

    private String vdsChannelCode;
    private String serviceCode;
    private String warning;
    private Double fromValue;
    private Double toValue;
    private String formula;
    private int index;

    @ExcelColumn(name = "B", nullable = false)
    public String getVdsChannelCode() {
        return vdsChannelCode;
    }

    public void setVdsChannelCode(String vdsChannelCode) {
        this.vdsChannelCode = vdsChannelCode;
    }

    @ExcelColumn(name = "C", nullable = false)
    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    @ExcelColumn(name = "D", nullable = false)
    public String getWarning() {
        return warning;
    }

    public void setWarning(String warning) {
        this.warning = warning;
    }

    @ExcelColumn(name = "E", nullable = false, regex = "^(0(\\.\\d{1,2})?|1(\\.0)?)$")
    public Double getFromValue() {
        return fromValue;
    }

    public void setFromValue(Double fromValue) {
        this.fromValue = fromValue;
    }

    @ExcelColumn(name = "F", nullable = false, regex = "^(0(\\.\\d{1,2})?|1(\\.0)?)$")
    public Double getToValue() {
        return toValue;
    }

    public void setToValue(Double toValue) {
        this.toValue = toValue;
    }

    @ExcelColumn(name = "G", nullable = true)
    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    @IndexColumn
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WarningServiceExcel that = (WarningServiceExcel) o;
        return Objects.equals(vdsChannelCode, that.vdsChannelCode) &&
                Objects.equals(serviceCode, that.serviceCode) &&
                Objects.equals(warning, that.warning) &&
                Objects.equals(fromValue, that.fromValue) &&
                Objects.equals(toValue, that.toValue) &&
                Objects.equals(formula, that.formula);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vdsChannelCode, serviceCode, warning, fromValue, toValue, formula);
    }
}
