package vn.vissoft.dashboard.helper.excelgenerator;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import vn.vissoft.dashboard.config.I18N;
import vn.vissoft.dashboard.dto.SqlReportDetailDTO;
import vn.vissoft.dashboard.helper.Constant;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.helper.excelreader.annotation.ExcelColumn;
import vn.vissoft.dashboard.helper.excelreader.annotation.ExcelEntity;
import vn.vissoft.dashboard.model.ReportSql;
import vn.vissoft.dashboard.repo.PartnerRepo;
import vn.vissoft.dashboard.services.ReportSqlService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DetailsReportExcelWriter {
    private String mstrStartCol;
    private String mstrEndCol;

    @Autowired
    ReportSqlService reportSqlService;

    @Autowired
    PartnerRepo partnerRepo;

    /**
     * download file detailsReport
     *
     * @param pclsClazz
     * @param plstPlanMonthlys
     * @return stream file
     * @throws Exception
     * @author QuangND
     * @version 1.0
     * @since 2019/11/03
     */
    public ByteArrayInputStream write(Class pclsClazz, List<Object[]> plstPlanMonthlys,
                                      SqlReportDetailDTO sqlReportDetailDTO, ReportSql reportSql, List<String> listColumn, String shopName) throws Exception {
        int vintStartRowIndex;

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        if (pclsClazz.isAnnotationPresent(ExcelEntity.class)) {
            Annotation ann1 = pclsClazz.getAnnotation(ExcelEntity.class);
            ExcelEntity entityAnn = (ExcelEntity) ann1;
            entityAnn.signalConstant();
            vintStartRowIndex = ((ExcelEntity) ann1).dataStartRowIndex();

        } else {
            throw new Exception("You must annotate class with @ExcelEntity");
        }

        determineColRange(pclsClazz);

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet();
        Map<String, CellStyle> styles = createStyles(workbook);

        //row for header

        //row for table name
        Row tableName = sheet.createRow(vintStartRowIndex - 7);
        Cell tableCell = tableName.createCell(0);
        //create header row
        Row headerRow = sheet.createRow(vintStartRowIndex - 2);

        // convert date Long yyyyMMdd to String dd/MM/yyyyy
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.PATTERNDATESQL);
        Date formDateSqlText = sdf.parse((sqlReportDetailDTO.getFromDate()).toString());
        Date toDateSqlText = sdf.parse((sqlReportDetailDTO.getToDate()).toString());
        SimpleDateFormat sdf1 = new SimpleDateFormat(Constants.PATTERNDATE);
        String fromDateParse = sdf1.format(formDateSqlText);
        String toDateParse = sdf1.format(toDateSqlText);

        tableCell.setCellValue(I18N.get("common.table.title.details") + " " + reportSql.getName().toUpperCase());
        tableCell.setCellStyle(styles.get("tableName"));
        sheet.addMergedRegion(new CellRangeAddress(vintStartRowIndex - 7, vintStartRowIndex - 7, 0, listColumn.size() - 1));
        //set row note details
        Row unit = sheet.createRow(vintStartRowIndex - 5);
        unit.createCell(1).setCellValue(I18N.get("common.detailreport.unit")+ " " + shopName);
        Row time = sheet.createRow(vintStartRowIndex - 4);
        time.createCell(1).setCellValue(I18N.get("common.detailreport.time") + " " + fromDateParse + " - " + toDateParse);

        //header
        for (int col = 0; col < listColumn.size(); col++) {
            Cell cell = headerRow.createCell(col);
            cell.setCellValue(listColumn.get(col));
            cell.setCellStyle(styles.get("header"));
            sheet.autoSizeColumn(col);
        }


        writeDetailsReport(sheet, plstPlanMonthlys, vintStartRowIndex, styles, listColumn);

        workbook.write(out);
        return new ByteArrayInputStream(out.toByteArray());
    }

    /**
     * ghi du lieu vao file details report
     *
     * @param sheet
     * @param list
     * @param pintStartRowIndex
     * @param pmpStyles
     * @param listColumn
     * @throws Exception
     * @author QuangND
     * @version 1.0
     * @since 2019/11/03
     */
    private void writeDetailsReport(XSSFSheet sheet, List<Object[]> listData, int pintStartRowIndex,
                                    Map<String, CellStyle> pmpStyles, List<String> listColumn
    ) throws Exception {


        int vintRowIdx = pintStartRowIndex - 1;
        int vintStt = 0;
        if (!DataUtil.isNullOrEmpty(listData)) {
            for (Object[] planMonthly : listData) {
                Row row = sheet.createRow(vintRowIdx++);
                vintStt++;

                row.createCell(0).setCellValue(vintStt);

                for (int i = 0; i < listColumn.size() - 1; i++) {
                    if (planMonthly[i] != null) {
                        row.createCell(i + 1).setCellValue(planMonthly[i].toString());
                    } else {
                        row.createCell(i + 1).setCellValue("");
                    }
                }

                for (int i = 0; i < row.getLastCellNum(); i++) {
                    row.getCell(i).setCellStyle(pmpStyles.get("content"));
                    sheet.autoSizeColumn(i);
                }

            }
            sheet.setColumnWidth(0, 1600);
        }

    }

    /**
     * tao style cho file excel
     *
     * @param workbook
     * @return map cellstyles for excel file
     * @author DatNT
     * @version 1.0
     * @since 2019/09/13
     */
    public Map<String, CellStyle> createStyles(XSSFWorkbook workbook) {
        Map<String, CellStyle> styles = new HashMap<>();
        CellStyle cellStyle;

        //table name
        Font tableName = workbook.createFont();
        tableName.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
        tableName.setFontHeightInPoints((short) 13);
        cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        cellStyle.setFont(tableName);
        cellStyle.setWrapText(true);
        styles.put("tableName", cellStyle);

        //header style
        Font headerFont = workbook.createFont();
        headerFont.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
        cellStyle = createBorderedStyle(workbook);
        cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        cellStyle.setFont(headerFont);
        cellStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        styles.put("header", cellStyle);

        //content style
        cellStyle = createBorderedStyle(workbook);
        styles.put("content", cellStyle);

        //date style
        cellStyle = createBorderedStyle(workbook);
        cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        styles.put("date", cellStyle);

        return styles;

    }

    /**
     * tao border cho bang trong file excel
     *
     * @param workbook
     * @return cellstyle for table in excel
     * @author DatNT
     * @version 1.0
     * @since 2019/09/13
     */
    private CellStyle createBorderedStyle(XSSFWorkbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);
        return cellStyle;
    }

    /**
     * Nhan dien xem vung du lieu tu cot nao den cot nao.
     *
     * @param pclsClazz
     */
    private void determineColRange(Class pclsClazz) {
        for (Method method : pclsClazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(ExcelColumn.class)) {
                ExcelColumn colAnn = (ExcelColumn) method.getAnnotation(ExcelColumn.class);
                String colName = colAnn.name();
                if (mstrStartCol == null || mstrEndCol == null) {
                    if (mstrStartCol == null) {
                        mstrStartCol = colName;
                    }
                    if (mstrEndCol == null) {
                        mstrEndCol = colName;
                    }
                } else {
                    if (colName != null) {
                        if (colName.compareTo(mstrStartCol) < 0) {
                            mstrStartCol = colName;
                        }

                        if (colName.compareTo(mstrEndCol) > 0) {
                            mstrEndCol = colName;
                        }
                    }
                }
            }
        }

    }

}
