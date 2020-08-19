package vn.vissoft.dashboard.dto.excel;

import vn.vissoft.dashboard.helper.excelreader.annotation.ExcelColumn;
import vn.vissoft.dashboard.helper.excelreader.annotation.ExcelEntity;
import vn.vissoft.dashboard.helper.excelreader.annotation.IndexColumn;

import java.util.Objects;

@ExcelEntity(dataStartRowIndex = 8, signalConstant = "DETAILS_REPORT_SERVICES")
public class DetailsReportServiceExcel extends BaseExcelEntity{

//    private Long mlngServiceId;
//    private double mdblSchedule;
    private int mintIndex;
//
//    @ExcelColumn(name = "B", nullable = false)
//    public Long getServiceId() {
//        return mlngServiceId;
//    }
//
//    public void Long(Long serviceId) {
//        this.mlngServiceId = serviceId;
//    }
//    @ExcelColumn(name = "C", nullable = true,regex = "^[+]?(([0-9]\\d*))(\\.\\d+)?$")
//    public double getSchedule() {
//        return mdblSchedule;
//    }
//
//    public void setSchedule(double schedule) {
//        this.mdblSchedule = schedule;
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        DetailsReportServiceExcel that = (DetailsReportServiceExcel) o;
//        return Double.compare(that.mdblSchedule, mdblSchedule) == 0 &&
//                Objects.equals(mlngServiceId, that.mlngServiceId);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(mlngServiceId, mdblSchedule);
//    }
//
    @IndexColumn
    public int getIndex() {
        return mintIndex;
    }

    public void setIndex(int index) {
        this.mintIndex = index;
    }
}
