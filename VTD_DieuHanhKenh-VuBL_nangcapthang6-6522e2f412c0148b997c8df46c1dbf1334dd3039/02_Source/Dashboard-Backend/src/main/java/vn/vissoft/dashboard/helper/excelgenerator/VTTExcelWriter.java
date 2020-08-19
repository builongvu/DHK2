package vn.vissoft.dashboard.helper.excelgenerator;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.vissoft.dashboard.config.I18N;
import vn.vissoft.dashboard.dto.PlanMonthlyDTO;
import vn.vissoft.dashboard.dto.PlanQuarterlyDTO;
import vn.vissoft.dashboard.dto.PlanYearlyDTO;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.helper.excelreader.annotation.ExcelColumn;
import vn.vissoft.dashboard.helper.excelreader.annotation.ExcelEntity;
import vn.vissoft.dashboard.repo.StaffRepo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class VTTExcelWriter {
    private String mstrStartCol;
    private String mstrEndCol;

    @Autowired
    private StaffRepo staffRepo;

    /**
     * download file chi tieu vds va cac cap
     *
     * @param pclsClazz
     * @param plstPlanMonthlys
     * @return stream file
     * @throws Exception
     * @author DatNT
     * @version 1.0
     * @since 2019/09/13
     */
    public ByteArrayInputStream write(Class pclsClazz, List plstPlanMonthlys, String pstrCycleCode) throws Exception {
        String vstrSignal;
        int vintStartRowIndex;

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        if (pclsClazz.isAnnotationPresent(ExcelEntity.class)) {
            Annotation ann1 = pclsClazz.getAnnotation(ExcelEntity.class);
            ExcelEntity entityAnn = (ExcelEntity) ann1;

            vstrSignal = entityAnn.signalConstant();
            vintStartRowIndex = ((ExcelEntity) ann1).dataStartRowIndex();

        } else {
            throw new Exception("You must annotate class with @ExcelEntity");
        }

        determineColRange(pclsClazz);

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet();
        Map<String, CellStyle> styles = createStyles(workbook);

        //row for header
        Row h = sheet.createRow(0);
        Cell c = h.createCell(0);
        c.setCellValue(vstrSignal);

        //row for table name
        Row tableName = sheet.createRow(vintStartRowIndex - 4);
        Cell tableCell = tableName.createCell(0);
        //create header row
        Row headerRow = sheet.createRow(vintStartRowIndex - 2);

        if (Constants.VDS_SIGNAL.equals(vstrSignal.trim())) {
            String columns[] = null;
            switch (pstrCycleCode) {
                case Constants.CYCLE_CODE.MONTH:
                    columns = setColumns(Constants.CYCLE_CODE.MONTH);
                    tableCell.setCellValue(I18N.get("common.table.title.month.cycle.vds"));
                    break;
                case Constants.CYCLE_CODE.QUARTER:
                    columns = setColumns(Constants.CYCLE_CODE.QUARTER);
                    tableCell.setCellValue(I18N.get("common.table.title.quarter.cycle.vds"));
                    break;
                case Constants.CYCLE_CODE.YEAR:
                    columns = setColumns(Constants.CYCLE_CODE.YEAR);
                    tableCell.setCellValue(I18N.get("common.table.title.year.cycle.vds"));
                    break;
            }

            tableCell.setCellStyle(styles.get("tableName"));
            if (columns != null) {
                sheet.addMergedRegion(new CellRangeAddress(vintStartRowIndex - 4, vintStartRowIndex - 4, 0, columns.length - 1));
            }
            //set row note vds
            Row noteRow = sheet.createRow(vintStartRowIndex - 3);
            Cell noteCell;
            if (columns != null) {
                noteCell = noteRow.createCell(columns.length + 1);
                noteCell.setCellValue(I18N.get("common.vds.note"));
                noteCell.setCellStyle(styles.get("note"));

                //header
                for (int col = 0; col < columns.length; col++) {
                    Cell cell = headerRow.createCell(col);
                    cell.setCellValue(columns[col]);
                    cell.setCellStyle(styles.get("header"));
                    sheet.autoSizeColumn(col);
                }
            }
            switch (pstrCycleCode) {
                case Constants.CYCLE_CODE.MONTH:
                    writeVTTMonth(sheet, plstPlanMonthlys, vintStartRowIndex, styles);
                    break;
                case Constants.CYCLE_CODE.QUARTER:
                    writeVTTQuarter(sheet, plstPlanMonthlys, vintStartRowIndex, styles);
                    break;
                case Constants.CYCLE_CODE.YEAR:
                    writeVTTYear(sheet, plstPlanMonthlys, vintStartRowIndex, styles);
                    break;
            }

        }
        if (Constants.LEVEL_SIGNAL.equals(vstrSignal.trim())) {
            String columns[] = null;
            switch (pstrCycleCode) {
                case Constants.CYCLE_CODE.MONTH:
                    columns = setColumnsLevel(Constants.CYCLE_CODE.MONTH);
                    tableCell.setCellValue(I18N.get("common.table.title.month.cycle.level"));
                    break;
                case Constants.CYCLE_CODE.QUARTER:
                    columns = setColumnsLevel(Constants.CYCLE_CODE.QUARTER);
                    tableCell.setCellValue(I18N.get("common.table.title.quarter.cycle.level"));
                    break;
                case Constants.CYCLE_CODE.YEAR:
                    columns = setColumnsLevel(Constants.CYCLE_CODE.YEAR);
                    tableCell.setCellValue(I18N.get("common.table.title.year.cycle.level"));
                    break;
            }

            tableCell.setCellStyle(styles.get("tableName"));
            if (columns != null) {
                sheet.addMergedRegion(new CellRangeAddress(vintStartRowIndex - 4, vintStartRowIndex - 4, 0, columns.length - 1));
            }

            //set row note level
            Row noteRow = sheet.createRow(vintStartRowIndex - 3);
            Cell noteCell;
            if (columns != null) {
                noteCell = noteRow.createCell(columns.length + 1);
                noteCell.setCellValue(I18N.get("common.level.note"));
                noteCell.setCellStyle(styles.get("note"));

                //header
                for (int col = 0; col < columns.length; col++) {
                    Cell cell = headerRow.createCell(col);
                    cell.setCellValue(columns[col]);
                    cell.setCellStyle(styles.get("header"));
                    sheet.autoSizeColumn(col);
                }
            }
            switch (pstrCycleCode) {
                case Constants.CYCLE_CODE.MONTH:
                    writeMonthLevel(sheet, plstPlanMonthlys, vintStartRowIndex, styles);
                    break;
                case Constants.CYCLE_CODE.QUARTER:
                    writeQuarterLevel(sheet, plstPlanMonthlys, vintStartRowIndex, styles);
                    break;
                case Constants.CYCLE_CODE.YEAR:
                    writeYearLevel(sheet, plstPlanMonthlys, vintStartRowIndex, styles);
                    break;
            }
        }
        workbook.write(out);
        return new ByteArrayInputStream(out.toByteArray());
    }

    /**
     * ghi du lieu vao file chi tieu vds theo thang
     *
     * @param sheet
     * @param plstPlanMonthlys
     * @param pintStartRowIndex
     * @param pmpStyles
     * @throws Exception
     * @author DatNT
     * @version 1.0
     * @since 2019/09/13
     */
    private void writeVTTMonth(XSSFSheet sheet, List<PlanMonthlyDTO> plstPlanMonthlys, int pintStartRowIndex, Map<String, CellStyle> pmpStyles) throws Exception {
        int vintRowIdx = pintStartRowIndex - 1;
        int vintStt = 0;
        if (!DataUtil.isNullOrEmpty(plstPlanMonthlys)) {
            for (PlanMonthlyDTO planMonthly : plstPlanMonthlys) {
                Row row = sheet.createRow(vintRowIdx++);
                vintStt++;

                row.createCell(0).setCellValue(vintStt);
                row.createCell(1).setCellValue(planMonthly.getCycleCode());
                row.createCell(2).setCellValue(String.valueOf(DataUtil.formatPrdId(planMonthly.getPrdId())));
                row.createCell(3).setCellValue(planMonthly.getChannelCode());
                row.createCell(4).setCellValue(planMonthly.getServiceCode());
                row.createCell(5).setCellValue(planMonthly.getServiceName());
                row.createCell(6).setCellValue("");


                Cell cell1 = row.getCell(1);
                Cell scheduleCell=row.getCell(6);

                for (int i = 0; i < row.getLastCellNum(); i++) {
                    row.getCell(i).setCellStyle(pmpStyles.get("content"));
                    cell1.setCellStyle(pmpStyles.get("date"));
                    scheduleCell.setCellStyle(pmpStyles.get("unlock"));
                    sheet.autoSizeColumn(i);
                }
            }
            sheet.setColumnWidth(0, 1600);
            sheet.enableLocking();
            sheet.lockSelectLockedCells();
        }
    }

    /**
     * ghi du lieu vao file chi tieu vds theo quy
     *
     * @param sheet
     * @param plstPlanMonthlys
     * @param pintStartRowIndex
     * @param pmpStyles
     * @throws Exception
     * @author DatNT
     * @version 1.0
     * @since 2019/09/13
     */
    private void writeVTTQuarter(XSSFSheet sheet, List<PlanQuarterlyDTO> plstPlanMonthlys, int pintStartRowIndex, Map<String, CellStyle> pmpStyles) throws Exception {
        int vintRowIdx = pintStartRowIndex - 1;
        int vintStt = 0;
        if (!DataUtil.isNullOrEmpty(plstPlanMonthlys)) {
            for (PlanQuarterlyDTO planMonthly : plstPlanMonthlys) {
                Row row = sheet.createRow(vintRowIdx++);
                vintStt++;

                row.createCell(0).setCellValue(vintStt);
                row.createCell(1).setCellValue(planMonthly.getCycleCode());
                row.createCell(2).setCellValue(String.valueOf(DataUtil.formatPrdId(planMonthly.getPrdId())));
                row.createCell(3).setCellValue(planMonthly.getChannelCode());
                row.createCell(4).setCellValue(planMonthly.getServiceCode());
                row.createCell(5).setCellValue(planMonthly.getServiceName());
                row.createCell(6).setCellValue("");


                Cell cell1 = row.getCell(1);
                Cell scheduleCell=row.getCell(6);


                for (int i = 0; i < row.getLastCellNum(); i++) {
                    row.getCell(i).setCellStyle(pmpStyles.get("content"));
                    cell1.setCellStyle(pmpStyles.get("date"));
                    scheduleCell.setCellStyle(pmpStyles.get("unlock"));
                    sheet.autoSizeColumn(i);
                }
            }
            sheet.setColumnWidth(0, 1600);
            sheet.enableLocking();
            sheet.lockSelectLockedCells();
        }
    }

    /**
     * ghi du lieu vao file chi tieu vds theo quy
     *
     * @param sheet
     * @param plstPlanMonthlys
     * @param pintStartRowIndex
     * @param pmpStyles
     * @throws Exception
     * @author DatNT
     * @version 1.0
     * @since 2019/09/13
     */
    private void writeVTTYear(XSSFSheet sheet, List<PlanYearlyDTO> plstPlanMonthlys, int pintStartRowIndex, Map<String, CellStyle> pmpStyles) throws Exception {
        int vintRowIdx = pintStartRowIndex - 1;
        int vintStt = 0;
        if (!DataUtil.isNullOrEmpty(plstPlanMonthlys)) {
            for (PlanYearlyDTO planMonthly : plstPlanMonthlys) {
                Row row = sheet.createRow(vintRowIdx++);
                vintStt++;

                row.createCell(0).setCellValue(vintStt);
                row.createCell(1).setCellValue(planMonthly.getCycleCode());
                row.createCell(2).setCellValue(String.valueOf(DataUtil.formatPrdId(planMonthly.getPrdId())));
                row.createCell(3).setCellValue(planMonthly.getChannelCode());
                row.createCell(4).setCellValue(planMonthly.getServiceCode());
                row.createCell(5).setCellValue(planMonthly.getServiceName());
                row.createCell(6).setCellValue("");


                Cell cell1 = row.getCell(1);
                Cell scheduleCell=row.getCell(6);

                for (int i = 0; i < row.getLastCellNum(); i++) {
                    row.getCell(i).setCellStyle(pmpStyles.get("content"));
                    cell1.setCellStyle(pmpStyles.get("date"));
                    scheduleCell.setCellStyle(pmpStyles.get("unlock"));
                    sheet.autoSizeColumn(i);
                }
            }
            sheet.setColumnWidth(0, 1600);
            sheet.enableLocking();
            sheet.lockSelectLockedCells();
        }
    }

    /**
     * ghi du lieu vao file chi tieu cac cap theo thang
     *
     * @param sheet
     * @param plstPlanMonthlys
     * @param pintStartRowIndex
     * @param pmpStyles
     * @throws Exception
     * @author DatNT
     * @version 1.0
     * @since 2019/09/13
     */
    private void writeMonthLevel(XSSFSheet sheet, List<PlanMonthlyDTO> plstPlanMonthlys, int pintStartRowIndex, Map<String, CellStyle> pmpStyles) throws Exception {
        int vintRowIdx = pintStartRowIndex - 1;
        int vintStt = 0;

        if (!DataUtil.isNullOrEmpty(plstPlanMonthlys)) {
            for (PlanMonthlyDTO planMonthly : plstPlanMonthlys) {
                Row row = sheet.createRow(vintRowIdx++);
                vintStt++;
                String vstrStaffName = "";
                if (!DataUtil.isNullOrEmpty(planMonthly.getStaffCode()))
                    vstrStaffName = staffRepo.findStaffNameByCode(planMonthly.getStaffCode());

                row.createCell(0).setCellValue(vintStt);
                row.createCell(1).setCellValue(planMonthly.getCycleCode());
                row.createCell(2).setCellValue(String.valueOf(DataUtil.formatPrdId(planMonthly.getPrdId())));
                row.createCell(3).setCellValue(planMonthly.getShopCode());
                row.createCell(4).setCellValue(planMonthly.getShopName());
                row.createCell(5).setCellValue(planMonthly.getStaffCode());
                row.createCell(6).setCellValue(vstrStaffName);
                row.createCell(7).setCellValue(planMonthly.getServiceCode());
                row.createCell(8).setCellValue(planMonthly.getServiceName());
                row.createCell(9).setCellValue("");

                Cell cell1 = row.getCell(1);
                Cell staffCodeCell=row.getCell(5);
                Cell staffNameCell=row.getCell(6);
                Cell scheduleCell=row.getCell(9);

                for (int i = 0; i < row.getLastCellNum(); i++) {
                    row.getCell(i).setCellStyle(pmpStyles.get("content"));
                    cell1.setCellStyle(pmpStyles.get("date"));
                    scheduleCell.setCellStyle(pmpStyles.get("unlock"));
                    staffCodeCell.setCellStyle(pmpStyles.get("unlock"));
                    staffNameCell.setCellStyle(pmpStyles.get("unlock"));
                    sheet.autoSizeColumn(i);

                }
            }
            sheet.setColumnWidth(0, 1600);
            sheet.enableLocking();
            sheet.lockSelectLockedCells();
        }

    }

    /**
     * ghi du lieu vao file chi tieu cac cap theo quy
     *
     * @param sheet
     * @param plstPlanQuarterlys
     * @param pintStartRowIndex
     * @param pmpStyles
     * @throws Exception
     * @author DatNT
     * @version 1.0
     * @since 2019/09/13
     */
    private void writeQuarterLevel(XSSFSheet sheet, List<PlanQuarterlyDTO> plstPlanQuarterlys, int pintStartRowIndex, Map<String, CellStyle> pmpStyles) throws Exception {
        int vintRowIdx = pintStartRowIndex - 1;
        int vintStt = 0;

        if (!DataUtil.isNullOrEmpty(plstPlanQuarterlys)) {
            for (PlanQuarterlyDTO planQuarter : plstPlanQuarterlys) {
                Row row = sheet.createRow(vintRowIdx++);
                vintStt++;
                String vstrStaffName = "";
                if (!DataUtil.isNullOrEmpty(planQuarter.getStaffCode()))
                    vstrStaffName = staffRepo.findStaffNameByCode(planQuarter.getStaffCode());

                row.createCell(0).setCellValue(vintStt);
                row.createCell(1).setCellValue(planQuarter.getCycleCode());
                row.createCell(2).setCellValue(String.valueOf(DataUtil.formatPrdId(planQuarter.getPrdId())));
                row.createCell(3).setCellValue(planQuarter.getShopCode());
                row.createCell(4).setCellValue(planQuarter.getShopName());
                row.createCell(5).setCellValue(planQuarter.getStaffCode());
                row.createCell(6).setCellValue(vstrStaffName);
                row.createCell(7).setCellValue(planQuarter.getServiceCode());
                row.createCell(8).setCellValue(planQuarter.getServiceName());
                row.createCell(9).setCellValue("");

                Cell cell1 = row.getCell(1);
                Cell staffCodeCell=row.getCell(5);
                Cell staffNameCell=row.getCell(6);
                Cell scheduleCell=row.getCell(9);

                for (int i = 0; i < row.getLastCellNum(); i++) {
                    row.getCell(i).setCellStyle(pmpStyles.get("content"));
                    cell1.setCellStyle(pmpStyles.get("date"));
                    scheduleCell.setCellStyle(pmpStyles.get("unlock"));
                    staffCodeCell.setCellStyle(pmpStyles.get("unlock"));
                    staffNameCell.setCellStyle(pmpStyles.get("unlock"));
                    sheet.autoSizeColumn(i);

                }
            }
            sheet.setColumnWidth(0, 1600);
            sheet.enableLocking();
            sheet.lockSelectLockedCells();
        }

    }

    /**
     * ghi du lieu vao file chi tieu cac cap theo nam
     *
     * @param sheet
     * @param plstPlanYearlys
     * @param pintStartRowIndex
     * @param pmpStyles
     * @throws Exception
     * @author DatNT
     * @version 1.0
     * @since 2019/09/13
     */
    private void writeYearLevel(XSSFSheet sheet, List<PlanYearlyDTO> plstPlanYearlys, int pintStartRowIndex, Map<String, CellStyle> pmpStyles) throws Exception {
        int vintRowIdx = pintStartRowIndex - 1;
        int vintStt = 0;

        if (!DataUtil.isNullOrEmpty(plstPlanYearlys)) {
            for (PlanYearlyDTO planYearly : plstPlanYearlys) {
                Row row = sheet.createRow(vintRowIdx++);
                vintStt++;
                String vstrStaffName = "";
                if (!DataUtil.isNullOrEmpty(planYearly.getStaffCode()))
                    vstrStaffName = staffRepo.findStaffNameByCode(planYearly.getStaffCode());

                row.createCell(0).setCellValue(vintStt);
                row.createCell(1).setCellValue(planYearly.getCycleCode());
                row.createCell(2).setCellValue(String.valueOf(DataUtil.formatPrdId(planYearly.getPrdId())));
                row.createCell(3).setCellValue(planYearly.getShopCode());
                row.createCell(4).setCellValue(planYearly.getShopName());
                row.createCell(5).setCellValue(planYearly.getStaffCode());
                row.createCell(6).setCellValue(vstrStaffName);
                row.createCell(7).setCellValue(planYearly.getServiceCode());
                row.createCell(8).setCellValue(planYearly.getServiceName());
                row.createCell(9).setCellValue("");

                Cell cell1 = row.getCell(1);
                Cell staffCodeCell=row.getCell(5);
                Cell staffNameCell=row.getCell(6);
                Cell scheduleCell=row.getCell(9);

                for (int i = 0; i < row.getLastCellNum(); i++) {
                    row.getCell(i).setCellStyle(pmpStyles.get("content"));
                    cell1.setCellStyle(pmpStyles.get("date"));
                    scheduleCell.setCellStyle(pmpStyles.get("unlock"));
                    staffCodeCell.setCellStyle(pmpStyles.get("unlock"));
                    staffNameCell.setCellStyle(pmpStyles.get("unlock"));
                    sheet.autoSizeColumn(i);

                }
            }
            sheet.setColumnWidth(0, 1600);
            sheet.enableLocking();
            sheet.lockSelectLockedCells();
        }

    }

    private String[] setColumns(String pstrCycleCode) {
        String[] columns = null;
        if (!DataUtil.isNullOrEmpty(pstrCycleCode)) {
            if (Constants.CYCLE_CODE.MONTH.equals(pstrCycleCode)) {
                columns = new String[]{I18N.get("common.header.order"), I18N.get("common.header.cycle"), I18N.get("common.header.month"), I18N.get("common.header.channel"),
                        I18N.get("common.header.servicecode"), I18N.get("common.header.servicename"), I18N.get("common.header.fschedule")};
            } else if (Constants.CYCLE_CODE.QUARTER.equals(pstrCycleCode)) {
                columns = new String[]{I18N.get("common.header.order"), I18N.get("common.header.cycle"), I18N.get("common.header.month"), I18N.get("common.header.channel"),
                        I18N.get("common.header.servicecode"), I18N.get("common.header.servicename"), I18N.get("common.header.quarter.fschedule")};
            } else if (Constants.CYCLE_CODE.YEAR.equals(pstrCycleCode)) {
                columns = new String[]{I18N.get("common.header.order"), I18N.get("common.header.cycle"), I18N.get("common.header.month"), I18N.get("common.header.channel"),
                        I18N.get("common.header.servicecode"), I18N.get("common.header.servicename"), I18N.get("common.header.year.fschedule")};
            }
        }
        return columns;
    }

    private String[] setColumnsLevel(String pstrCycleCode) {
        String[] columns = null;
        if (!DataUtil.isNullOrEmpty(pstrCycleCode)) {
            if (Constants.CYCLE_CODE.MONTH.equals(pstrCycleCode)) {
                columns = new String[]{I18N.get("common.header.order"), I18N.get("common.header.cycle"), I18N.get("common.header.month"), I18N.get("common.header.unit"),I18N.get("common.header.shopname"), I18N.get("common.header.staff"),
                        I18N.get("common.header.staff.name"), I18N.get("common.header.servicecode"), I18N.get("common.header.servicename"), I18N.get("common.header.fschedule")};
            } else if (Constants.CYCLE_CODE.QUARTER.equals(pstrCycleCode)) {
                columns = new String[]{I18N.get("common.header.order"), I18N.get("common.header.cycle"), I18N.get("common.header.month"), I18N.get("common.header.unit"),I18N.get("common.header.shopname"), I18N.get("common.header.staff"),
                        I18N.get("common.header.staff.name"), I18N.get("common.header.servicecode"), I18N.get("common.header.servicename"), I18N.get("common.header.quarter.fschedule")};
            } else if (Constants.CYCLE_CODE.YEAR.equals(pstrCycleCode)) {
                columns = new String[]{I18N.get("common.header.order"), I18N.get("common.header.cycle"), I18N.get("common.header.month"), I18N.get("common.header.unit"), I18N.get("common.header.shopname"),I18N.get("common.header.staff"),
                        I18N.get("common.header.staff.name"), I18N.get("common.header.servicecode"), I18N.get("common.header.servicename"), I18N.get("common.header.year.fschedule")};
            }
        }
        return columns;
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
        tableName.setFontHeightInPoints((short) 15);
        cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        cellStyle.setFont(tableName);
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

        //note style
        Font noteFont = workbook.createFont();
        noteFont.setFontHeightInPoints((short) 11);
        noteFont.setItalic(true);
        cellStyle = workbook.createCellStyle();
        cellStyle.setFont(noteFont);
        cellStyle.setAlignment(CellStyle.ALIGN_LEFT);
        styles.put("note", cellStyle);

        //enable edit cell
        cellStyle=createBorderedStyle(workbook);
        cellStyle.setLocked(false);
        styles.put("unlock",cellStyle);

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
    public CellStyle createBorderedStyle(XSSFWorkbook workbook) {
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
    public void determineColRange(Class pclsClazz) {
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
