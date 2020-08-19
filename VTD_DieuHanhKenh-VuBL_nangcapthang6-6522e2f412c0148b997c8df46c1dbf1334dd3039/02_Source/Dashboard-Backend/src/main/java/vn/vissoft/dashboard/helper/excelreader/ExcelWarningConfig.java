package vn.vissoft.dashboard.helper.excelreader;

import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.*;
import vn.vissoft.dashboard.config.I18N;
import vn.vissoft.dashboard.dto.excel.WarningReceiveServiceExcel;
import vn.vissoft.dashboard.dto.excel.WarningSendServiceExcel;
import vn.vissoft.dashboard.helper.excelreader.annotation.ExcelEntity;

import java.io.*;
import java.lang.annotation.Annotation;

public class ExcelWarningConfig {


    /**
     * Tạo file mẫu Excel cấu hình nhận
     *
     * @return ByteArrayInputStream
     * @throws Exception
     * @author CuongDT
     * version 1.1
     * @since 18/11/2019
     */
    public ByteArrayInputStream writeTemplate() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(I18N.get("common.table.warning.warningsend"));
        sheet.setColumnWidth(1, 4400);
        sheet.setColumnWidth(2, 6800);
        sheet.setColumnWidth(3, 4000);
        sheet.setColumnWidth(4, 4000);
        sheet.setColumnWidth(5, 7000);
        sheet.setColumnWidth(6, 6000);

        CellStyle cellStyle;
        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 11);
        cellStyle = createBorderedStyle(workbook);
        cellStyle.setAlignment(CellStyle.ALIGN_LEFT);
        cellStyle.setFont(headerFont);
        CellStyle cellStyleW = workbook.createCellStyle();
        Font headerFontW = workbook.createFont();
        headerFontW.setFontHeightInPoints((short) 11);
        cellStyleW.setAlignment(CellStyle.ALIGN_LEFT);
        headerFontW.setItalic(true);
        cellStyleW.setFont(headerFontW);

//      CONTRAINTS
        Row rowconstrain = sheet.createRow(0);
        Cell cellconstrain = rowconstrain.createCell(0);
        Annotation ann1 = WarningSendServiceExcel.class.getAnnotation(ExcelEntity.class);
        ExcelEntity entityAnn = (ExcelEntity) ann1;
        cellconstrain.setCellValue(entityAnn.signalConstant());

//            lƯU Ý
        Row row4 = sheet.createRow(3);
        Cell cell0 = row4.createCell(6);
        cell0.setCellStyle(cellStyleW);
        cell0.setCellValue(I18N.get("common.table.warning.warning"));

//        GỘP Ô
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 4));
//            TÊN BẢNG
        Row firstRow = sheet.createRow(2);
        Cell firstCell = firstRow.createCell(0);
        firstCell.setCellStyle(tablenameStyle(workbook));
        firstCell.setCellValue(I18N.get("common.table.warning.warningsend"));
//TÊN CỘT
        Row row5 = sheet.createRow(4);

        Cell cell1 = row5.createCell(0);
        cell1.setCellStyle(headerStyle(workbook));
        cell1.setCellValue(I18N.get("common.header.order"));
        Cell cell2 = row5.createCell(1);
        cell2.setCellStyle(headerStyle(workbook));
        cell2.setCellValue(I18N.get("common.header.servicecode"));
        Cell cell3 = row5.createCell(2);
        cell3.setCellStyle(headerStyle(workbook));
        cell3.setCellValue(I18N.get("common.table.warning.warninglevel"));
        Cell cell4 = row5.createCell(3);
        cell4.setCellStyle(headerStyle(workbook));
        cell4.setCellValue("Email");
        Cell cell5 = row5.createCell(4);
        cell5.setCellStyle(headerStyle(workbook));
        cell5.setCellValue("SMS");
        Cell cell6 = row5.createCell(5);
        cell6.setCellStyle(headerStyle(workbook));
        cell6.setCellValue(I18N.get("common.table.warning.informlevel"));

        Row row6 = sheet.createRow(5);

        CellStyle cells = createBorderedStyle(workbook);
        Cell ex1 = row6.createCell(0);
        ex1.setCellStyle(cells);
        Cell ex2 = row6.createCell(1);
        ex2.setCellStyle(cells);
        Cell ex3 = row6.createCell(2);
        ex3.setCellStyle(cells);
        Cell ex4 = row6.createCell(3);
        ex4.setCellStyle(cells);
        Cell ex5 = row6.createCell(4);
        ex5.setCellStyle(cells);
        Cell ex6 = row6.createCell(5);
        ex6.setCellStyle(cells);


        String[] vobjWarningLV = new String[4];
        vobjWarningLV[0]="0-"+ I18N.get("common.table.warning.blue");
        vobjWarningLV[1]="1-"+ I18N.get("common.table.warning.yellow");
        vobjWarningLV[2]="2-"+ I18N.get("common.table.warning.orange");
        vobjWarningLV[3]="3-"+ I18N.get("common.table.warning.red");
        String[] vobjILV = new String[5];
        vobjILV[0]="0-"+ I18N.get("common.table.warning.inform0");
        vobjILV[1]="1-"+ I18N.get("common.table.warning.inform1");
        vobjILV[2]="2-"+ I18N.get("common.table.warning.inform2");
        vobjILV[3]="3-"+ I18N.get("common.table.warning.inform3");
        vobjILV[4]="4-"+ I18N.get("common.table.warning.inform4");
        String[] vobjEM = new String[2];
        vobjEM[0]="0-"+ I18N.get("common.table.warning.no");
        vobjEM[1]="1-"+ I18N.get("common.table.warning.yes");

        XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper(sheet);
        XSSFDataValidationConstraint constraint = (XSSFDataValidationConstraint)dvHelper.createExplicitListConstraint(vobjWarningLV);
        CellRangeAddressList addressList = new CellRangeAddressList(5,5000,2,2);
        XSSFDataValidation dataValidation = (XSSFDataValidation)dvHelper.createValidation(constraint, addressList);
        dataValidation.setShowErrorBox(true);
        dataValidation.setEmptyCellAllowed(false);
        sheet.addValidationData(dataValidation);

        XSSFDataValidationHelper dvHelper1 = new XSSFDataValidationHelper(sheet);
        XSSFDataValidationConstraint constraint1 = (XSSFDataValidationConstraint)dvHelper1.createExplicitListConstraint(vobjILV);
        CellRangeAddressList addressList1 = new CellRangeAddressList(5,5000,5,5);
        XSSFDataValidation dataValidation1 = (XSSFDataValidation)dvHelper1.createValidation(constraint1, addressList1);
        dataValidation1.setShowErrorBox(true);
        dataValidation1.setEmptyCellAllowed(false);
        sheet.addValidationData(dataValidation1);

        XSSFDataValidationHelper dvHelper2 = new XSSFDataValidationHelper(sheet);
        XSSFDataValidationConstraint constraint2 = (XSSFDataValidationConstraint)dvHelper2.createExplicitListConstraint(vobjEM);
        CellRangeAddressList addressList2 = new CellRangeAddressList(5,5000,3,4);
        XSSFDataValidation dataValidation2 = (XSSFDataValidation)dvHelper2.createValidation(constraint2, addressList2);
        dataValidation2.setShowErrorBox(true);
        dataValidation2.setEmptyCellAllowed(false);
        sheet.addValidationData(dataValidation2);

        workbook.write(out);
        return new ByteArrayInputStream(out.toByteArray());
    }


    public CellStyle tablenameStyle(XSSFWorkbook workbook) {
        CellStyle cellStyle;
        Font tableName = workbook.createFont();
        tableName.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
        tableName.setFontHeightInPoints((short) 15);
        cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        cellStyle.setFont(tableName);
        return cellStyle;
    }

    public CellStyle headerStyle(XSSFWorkbook workbook) {
        CellStyle cellStyle;
        Font headerFont = workbook.createFont();
        headerFont.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
        headerFont.setFontHeightInPoints((short) 11);
        cellStyle = createBorderedStyle(workbook);
        cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        cellStyle.setFont(headerFont);
        cellStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        return cellStyle;
    }

    public CellStyle createBorderedStyle(XSSFWorkbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);
        return cellStyle;
    }


    /**
     * Tạo file mẫu Excel cấu hình nhận
     *
     * @return ByteArrayInputStream
     * @throws Exception
     * @author CuongDT
     * version 1.1
     * @since 18/11/2019
     */
    public ByteArrayInputStream writeReveiceTemplate() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(I18N.get("common.table.warning.receive"));
        sheet.setColumnWidth(1, 4000);
        sheet.setColumnWidth(2, 6200);
        sheet.setColumnWidth(3, 8200);
        sheet.setColumnWidth(4, 5000);

        CellStyle cellStyle;
        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 11);
        cellStyle = createBorderedStyle(workbook);
        cellStyle.setAlignment(CellStyle.ALIGN_LEFT);
        cellStyle.setFont(headerFont);

        CellStyle cellStyleW = workbook.createCellStyle();
        Font headerFontW = workbook.createFont();
        headerFontW.setFontHeightInPoints((short) 11);
        cellStyleW.setAlignment(CellStyle.ALIGN_LEFT);
        headerFontW.setItalic(true);
        cellStyleW.setFont(headerFontW);

//      CONTRAINTS
        Row rowconstrain = sheet.createRow(0);
        Cell cellconstrain = rowconstrain.createCell(0);
        Annotation ann1 = WarningReceiveServiceExcel.class.getAnnotation(ExcelEntity.class);
        ExcelEntity entityAnn = (ExcelEntity) ann1;
        cellconstrain.setCellValue(entityAnn.signalConstant());

//            lƯU Ý
        Row row1 = sheet.createRow(3);
        Cell cell0 = row1.createCell(4);
        cell0.setCellStyle(cellStyleW);
        cell0.setCellValue(I18N.get("common.table.warning.warning_Receive"));

//        GỘP Ô
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 3));
//            TÊN BẢNG
        Row firstRow = sheet.createRow(2);
        Cell firstCell = firstRow.createCell(0);
        firstCell.setCellStyle(tablenameStyle(workbook));
        firstCell.setCellValue(I18N.get("common.table.warning.warningreceive"));
//TÊN CỘT
//        Row row4 = sheet.createRow(4);
        Row row5 = sheet.createRow(4);

        Cell cell1 = row5.createCell(0);
        cell1.setCellStyle(headerStyle(workbook));
        cell1.setCellValue(I18N.get("common.header.order"));
        Cell cell2 = row5.createCell(1);
        cell2.setCellStyle(headerStyle(workbook));
        cell2.setCellValue(I18N.get("common.table.warning.shopCode"));
        Cell cell3 = row5.createCell(2);
        cell3.setCellStyle(headerStyle(workbook));
        cell3.setCellValue(I18N.get("common.table.warning.warninglevel"));

        Cell cell6 = row5.createCell(3);
        cell6.setCellStyle(headerStyle(workbook));
        cell6.setCellValue(I18N.get("common.table.warning.informlevel"));


        Row row6 = sheet.createRow(5);

        CellStyle cells = createBorderedStyle(workbook);
        Cell ex1 = row6.createCell(0);
        ex1.setCellStyle(cells);
        Cell ex2 = row6.createCell(1);
        ex2.setCellStyle(cells);
        Cell ex3 = row6.createCell(2);
        ex3.setCellStyle(cells);
        Cell ex4 = row6.createCell(3);
        ex4.setCellStyle(cells);

        String[] vobjWarningLV = new String[4];
        vobjWarningLV[0]="0-"+ I18N.get("common.table.warning.blue");
        vobjWarningLV[1]="1-"+ I18N.get("common.table.warning.yellow");
        vobjWarningLV[2]="2-"+ I18N.get("common.table.warning.orange");
        vobjWarningLV[3]="3-"+ I18N.get("common.table.warning.red");
        String[] vobjILV = new String[5];
        vobjILV[0]="0-"+ I18N.get("common.table.warning.inform0");
        vobjILV[1]="1-"+ I18N.get("common.table.warning.inform1");
        vobjILV[2]="2-"+ I18N.get("common.table.warning.inform2");
        vobjILV[3]="3-"+ I18N.get("common.table.warning.inform3");
        vobjILV[4]="4-"+ I18N.get("common.table.warning.inform4");

        XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper(sheet);
        XSSFDataValidationConstraint constraint = (XSSFDataValidationConstraint)dvHelper.createExplicitListConstraint(vobjWarningLV);
        CellRangeAddressList addressList = new CellRangeAddressList(5,5000,2,2);
        XSSFDataValidation dataValidation = (XSSFDataValidation)dvHelper.createValidation(constraint, addressList);
        dataValidation.setShowErrorBox(true);
        dataValidation.setEmptyCellAllowed(false);
        sheet.addValidationData(dataValidation);

        XSSFDataValidationHelper dvHelper1 = new XSSFDataValidationHelper(sheet);
        XSSFDataValidationConstraint constraint1 = (XSSFDataValidationConstraint)dvHelper1.createExplicitListConstraint(vobjILV);
        CellRangeAddressList addressList1 = new CellRangeAddressList(5,5000,3,3);
        XSSFDataValidation dataValidation1 = (XSSFDataValidation)dvHelper1.createValidation(constraint1, addressList1);
        dataValidation1.setShowErrorBox(true);
        dataValidation1.setEmptyCellAllowed(false);
        sheet.addValidationData(dataValidation1);

        workbook.write(out);
        return new ByteArrayInputStream(out.toByteArray());
    }

}
