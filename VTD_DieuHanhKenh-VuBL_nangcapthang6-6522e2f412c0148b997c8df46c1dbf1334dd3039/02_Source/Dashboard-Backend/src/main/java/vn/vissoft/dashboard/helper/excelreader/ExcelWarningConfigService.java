package vn.vissoft.dashboard.helper.excelreader;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.*;
import vn.vissoft.dashboard.config.I18N;
import vn.vissoft.dashboard.dto.excel.GroupServiceExcel;
import vn.vissoft.dashboard.dto.excel.WarningServiceExcel;
import vn.vissoft.dashboard.helper.excelreader.annotation.ExcelEntity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.annotation.Annotation;

public class ExcelWarningConfigService {
    public ByteArrayInputStream writeTemplate() throws Exception{
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(I18N.get("common.table.warning.config.service"));
        sheet.setColumnWidth(0, 1500);
        sheet.setColumnWidth(1, 3500);
        sheet.setColumnWidth(2, 5200);
        sheet.setColumnWidth(3, 4200);
        sheet.setColumnWidth(4, 4000);
        sheet.setColumnWidth(6, 6200);
        sheet.setColumnWidth(5, 4000);
        sheet.setColumnWidth(7, 4400);
        sheet.setColumnWidth(8, 4000);
        sheet.setColumnWidth(9, 4000);

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


        Row rowconstrain = sheet.createRow(0);
        Cell cellconstrain = rowconstrain.createCell(0);
        cellconstrain.setCellStyle(cellStyle);
        Annotation ann1 = WarningServiceExcel.class.getAnnotation(ExcelEntity.class);
        ExcelEntity entityAnn = (ExcelEntity) ann1;
        cellconstrain.setCellValue(entityAnn.signalConstant());

        Row row1 = sheet.createRow(3);
        Cell cell0 = row1.createCell(7);
        cell0.setCellStyle(noteStyle(workbook));
        cell0.setCellValue(I18N.get("common.table.warning.config.service.mess"));

        sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 6));
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 8, 9));
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 6, 7));

        Row firstRow = sheet.createRow(2);
        Cell firstCell = firstRow.createCell(0);
        firstCell.setCellStyle(tablenameStyle(workbook));
        firstCell.setCellValue(I18N.get("common.table.warning.config.service").toUpperCase());

        Row row5 = sheet.createRow(4);

        Cell cell1 = row5.createCell(0);
        cell1.setCellStyle(headerStyle(workbook));
        cell1.setCellValue(I18N.get("common.header.order"));
        Cell cell2 = row5.createCell(2);
        cell2.setCellStyle(headerStyle(workbook));
        cell2.setCellValue(I18N.get("common.header.service.code"));
        Cell cell3 = row5.createCell(1);
        cell3.setCellStyle(headerStyle(workbook));
        cell3.setCellValue(I18N.get("common.header.channel"));
        Cell cell6 = row5.createCell(3);
        cell6.setCellStyle(headerStyle(workbook));
        cell6.setCellValue(I18N.get("common.table.warning.warninglevel"));
        Cell cell7 = row5.createCell(4);
        cell7.setCellStyle(headerStyle(workbook));
        cell7.setCellValue(I18N.get("common.table.warning.config.service.from.value"));
        Cell cell8 = row5.createCell(5);
        cell8.setCellStyle(headerStyle(workbook));
        cell8.setCellValue(I18N.get("common.table.warning.config.service.to.value"));
        Cell cell9 = row5.createCell(6);
        cell9.setCellStyle(headerStyle(workbook));
        cell9.setCellValue(I18N.get("common.header.service.exp"));

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
        Cell ex7 = row6.createCell(6);
        ex7.setCellStyle(cells);
//        Cell ex8 = row6.createCell(7);
////        ex8.setCellStyle(cells);
        String[] vobjWarningLV = new String[4];
        vobjWarningLV[0]="0-"+ I18N.get("common.table.warning.blue");
        vobjWarningLV[1]="1-"+ I18N.get("common.table.warning.yellow");
        vobjWarningLV[2]="2-"+ I18N.get("common.table.warning.orange");
        vobjWarningLV[3]="3-"+ I18N.get("common.table.warning.red");
        String[] vdsVChannel = new String[3];
        vdsVChannel[0] = "VDS_TINH-" + I18N.get("common.table.warning.config.service.channel1");
        vdsVChannel[1] = "VDS_KHDN-" + I18N.get("common.table.warning.config.service.channel2");
        vdsVChannel[2] = "VDS_BANHANGSO-" + I18N.get("common.table.warning.config.service.channel3");
        XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper(sheet);
        XSSFDataValidationConstraint constraint = (XSSFDataValidationConstraint)dvHelper.createExplicitListConstraint(vobjWarningLV);
        CellRangeAddressList addressList = new CellRangeAddressList(5,5000,3,3);
        XSSFDataValidation dataValidation = (XSSFDataValidation)dvHelper.createValidation(constraint, addressList);
        dataValidation.setShowErrorBox(true);
        dataValidation.setEmptyCellAllowed(false);
        sheet.addValidationData(dataValidation);

        XSSFDataValidationHelper dvHelper1 = new XSSFDataValidationHelper(sheet);
        XSSFDataValidationConstraint constraint1 = (XSSFDataValidationConstraint)dvHelper1.createExplicitListConstraint(vdsVChannel);
        CellRangeAddressList addressList1 = new CellRangeAddressList(5,5000,1,1);
        XSSFDataValidation dataValidation1 = (XSSFDataValidation)dvHelper1.createValidation(constraint1, addressList1);
        dataValidation1.setShowErrorBox(true);
        dataValidation1.setEmptyCellAllowed(false);
        sheet.addValidationData(dataValidation1);

        workbook.write(out);
        return new ByteArrayInputStream(out.toByteArray());
    }
    public CellStyle createBorderedStyle(XSSFWorkbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);
        return cellStyle;
    }
    public CellStyle noteStyle(XSSFWorkbook workbook) {
        CellStyle cellStyle;
        Font noteFont = workbook.createFont();
        noteFont.setFontHeightInPoints((short) 11);
        noteFont.setItalic(true);
        cellStyle = workbook.createCellStyle();
        cellStyle.setFont(noteFont);
        return cellStyle;
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

}
