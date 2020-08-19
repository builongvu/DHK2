package vn.vissoft.dashboard.dto.excel;

import vn.vissoft.dashboard.helper.excelreader.annotation.ExcelColumn;
import vn.vissoft.dashboard.helper.excelreader.annotation.ExcelEntity;
import vn.vissoft.dashboard.helper.excelreader.annotation.IndexColumn;

import java.util.Objects;

@ExcelEntity(dataStartRowIndex = 6, signalConstant = "IMPORT_VDS_SERVICES")
public class VTTServiceExcel extends BaseExcelEntity {

    private String cycleCode;
    private String mstrMonth;
    private String mstrChannelCode;
    private String mstrServiceCode;
    private String mstrServiceName;
    private double mdblSchedule;
    private int mintIndex;

    @ExcelColumn(name = "B", nullable = false)
    public String getCycleCode() {
        return cycleCode;
    }

    public void setCycleCode(String cycleCode) {
        this.cycleCode = cycleCode;
    }

    @ExcelColumn(name = "C", regex = "^(((0)[0-9])|((1)[0-2]))(\\/)\\d{4}$")
    public String getMonth() {
        return mstrMonth;
    }

    public void setMonth(String month) {
        this.mstrMonth = month;
    }

    @ExcelColumn(name = "D")
    public String getChannelCode() {
        return mstrChannelCode;
    }

    public void setChannelCode(String channelCode) {
        this.mstrChannelCode = channelCode;
    }

    @ExcelColumn(name = "E")
    public String getServiceCode() {
        return mstrServiceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.mstrServiceCode = serviceCode;
    }

    @ExcelColumn(name = "F", nullable = true)
    public String getServiceName() {
        return mstrServiceName;
    }

    public void setServiceName(String serviceName) {
        this.mstrServiceName = serviceName;
    }

    @ExcelColumn(name = "G", nullable = true, regex = "^[+]?(([0-9]\\d*))(\\.\\d+)?$")
    public double getSchedule() {
        return mdblSchedule;
    }

    public void setSchedule(double schedule) {
        this.mdblSchedule = schedule;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VTTServiceExcel that = (VTTServiceExcel) o;
        return Double.compare(that.mdblSchedule, mdblSchedule) == 0 &&
                Objects.equals(cycleCode.trim(), that.cycleCode.trim()) &&
                Objects.equals(mstrMonth.trim(), that.mstrMonth.trim()) &&
                Objects.equals(mstrChannelCode.trim(), that.mstrChannelCode.trim()) &&
                Objects.equals(mstrServiceCode.trim(), that.mstrServiceCode.trim()) &&
                Objects.equals(mstrServiceName.trim(), that.mstrServiceName.trim());
    }

    @Override
    public int hashCode() {
        return Objects.hash(cycleCode, mstrMonth, mstrChannelCode, mstrServiceCode, mstrServiceName, mdblSchedule);
    }

    @IndexColumn
    public int getIndex() {
        return mintIndex;
    }

    public void setIndex(int index) {
        this.mintIndex = index;
    }
}
