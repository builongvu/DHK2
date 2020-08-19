package vn.vissoft.dashboard.helper.excelreader;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import vn.vissoft.dashboard.config.I18N;
import vn.vissoft.dashboard.dto.excel.BaseExcelEntity;
import vn.vissoft.dashboard.dto.excel.GroupServiceExcel;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.helper.excelreader.annotation.ExcelEntity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.annotation.Annotation;

public class GroupServiceTemplate {
    public ByteArrayInputStream writeTemplate() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(I18N.get("common.table.group.service"));
        sheet.setColumnWidth(1, 4000);
        sheet.setColumnWidth(2, 6200);
        sheet.setColumnWidth(3, 8200);
        sheet.setColumnWidth(4, 5000);
        sheet.setColumnWidth(6, 4400);
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
        Annotation ann1 = GroupServiceExcel.class.getAnnotation(ExcelEntity.class);
        ExcelEntity entityAnn = (ExcelEntity) ann1;
        cellconstrain.setCellValue(entityAnn.signalConstant());

        Row row1 = sheet.createRow(3);
        Cell cell0 = row1.createCell(4);
        cell0.setCellStyle(noteStyle(workbook));
        cell0.setCellValue(I18N.get("common.table.group.service.warning"));

        sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 3));
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 8, 9));
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 6, 7));

        Row firstRow = sheet.createRow(2);
        Cell firstCell = firstRow.createCell(0);
        firstCell.setCellStyle(tablenameStyle(workbook));
        firstCell.setCellValue(I18N.get("common.table.group.service").toUpperCase());

        Row row5 = sheet.createRow(4);

        Cell cell1 = row5.createCell(0);
        cell1.setCellStyle(headerStyle(workbook));
        cell1.setCellValue(I18N.get("common.header.order"));
        Cell cell2 = row5.createCell(1);
        cell2.setCellStyle(headerStyle(workbook));
        cell2.setCellValue(I18N.get("common.table.group.service.codeproduct"));
        Cell cell3 = row5.createCell(2);
        cell3.setCellStyle(headerStyle(workbook));
        cell3.setCellValue(I18N.get("common.header.group.service.code"));
        Cell cell6 = row5.createCell(3);
        cell6.setCellStyle(headerStyle(workbook));
        cell6.setCellValue(I18N.get("common.header.group.service.name"));


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

//tao template sheet tham chieu
        XSSFSheet sheetReference = workbook.createSheet(I18N.get("common.sheet.reference.name"));
        sheetReference.setColumnWidth(1, 4000);
        sheetReference.setColumnWidth(2, 4200);

        sheetReference.addMergedRegion(new CellRangeAddress(1, 1, 0, 2));
        Row titleRow = sheetReference.createRow(1);
        Cell refenceTitleCell = titleRow.createCell(0);
        refenceTitleCell.setCellStyle(tablenameStyle(workbook));
        refenceTitleCell.setCellValue(I18N.get("common.header.product"));


        Row headerRow = sheetReference.createRow(3);
        Cell cellReference1 = headerRow.createCell(0);
        cellReference1.setCellStyle(headerStyle(workbook));
        cellReference1.setCellValue(I18N.get("common.header.order"));
        Cell cellReference2 = headerRow.createCell(1);
        cellReference2.setCellStyle(headerStyle(workbook));
        cellReference2.setCellValue(I18N.get("common.table.group.service.codeproduct").toUpperCase());
        Cell cellReference3 = headerRow.createCell(2);
        cellReference3.setCellStyle(headerStyle(workbook));
        cellReference3.setCellValue(I18N.get("common.table.product.name.title"));

        Row rowContent1 = sheetReference.createRow(4);
        Cell cellContent1 = rowContent1.createCell(0);
        cellContent1.setCellStyle(cells);
        cellContent1.setCellValue(Constants.NO1);
        Cell cellContent2 = rowContent1.createCell(1);
        cellContent2.setCellStyle(cells);
        cellContent2.setCellValue(Constants.VTPAY_CODE);
        Cell cellContent3 = rowContent1.createCell(2);
        cellContent3.setCellStyle(cells);
        cellContent3.setCellValue(Constants.VTPAY_NAME);

        Row rowContent2 = sheetReference.createRow(5);
        Cell cellContent21 = rowContent2.createCell(0);
        cellContent21.setCellStyle(cells);
        cellContent21.setCellValue(Constants.NO2);
        Cell cellContent22 = rowContent2.createCell(1);
        cellContent22.setCellStyle(cells);
        cellContent22.setCellValue(Constants.BANKPLUS_CODE);
        Cell cellContent23 = rowContent2.createCell(2);
        cellContent23.setCellStyle(cells);
        cellContent23.setCellValue(Constants.BANKPLUS_NAME);

        Row rowContent3 = sheetReference.createRow(6);
        Cell cellContent31 = rowContent3.createCell(0);
        cellContent31.setCellStyle(cells);
        cellContent31.setCellValue(Constants.NO3);
        Cell cellContent32 = rowContent3.createCell(1);
        cellContent32.setCellStyle(cells);
        cellContent32.setCellValue(Constants.KPP_CODE);
        Cell cellContent33 = rowContent3.createCell(2);
        cellContent33.setCellStyle(cells);
        cellContent33.setCellValue(Constants.KPP_CODE);

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

    public CellStyle noteStyle(XSSFWorkbook workbook) {
        CellStyle cellStyle;
        Font noteFont = workbook.createFont();
        noteFont.setFontHeightInPoints((short) 11);
        noteFont.setItalic(true);
        cellStyle = workbook.createCellStyle();
        cellStyle.setFont(noteFont);
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
