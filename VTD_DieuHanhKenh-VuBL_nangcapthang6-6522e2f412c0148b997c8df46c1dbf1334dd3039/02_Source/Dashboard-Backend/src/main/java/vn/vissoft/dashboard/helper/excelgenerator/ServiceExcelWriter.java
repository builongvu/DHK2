package vn.vissoft.dashboard.helper.excelgenerator;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.vissoft.dashboard.config.I18N;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.helper.excelreader.annotation.ExcelEntity;
import vn.vissoft.dashboard.model.GroupService;
import vn.vissoft.dashboard.model.Unit;
import vn.vissoft.dashboard.repo.GroupServiceRepo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

@Service
public class ServiceExcelWriter {

    public static final Logger LOGGER = LogManager.getLogger(ServiceExcelWriter.class);

    public ByteArrayInputStream write(Class pclsClazz, List<GroupService> vlstGroupService, List<Unit> vlstUnits) throws Exception {

        VTTExcelWriter vttExcelWriter = new VTTExcelWriter();

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

        vttExcelWriter.determineColRange(pclsClazz);

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(I18N.get("common.table.title.configuration"));
        Map<String, CellStyle> styles = vttExcelWriter.createStyles(workbook);

        //row for header
        Row h = sheet.createRow(0);
        Cell c = h.createCell(0);
        c.setCellValue(vstrSignal);

        //row for table name
        Row tableName = sheet.createRow(vintStartRowIndex - 4);
        Cell tableCell = tableName.createCell(0);

        //create header row
        Row headerRow = sheet.createRow(vintStartRowIndex - 2);

        if (Constants.SERVICE_SIGNAL.equals(vstrSignal.trim())) {
            String[] columns = new String[]{
                    I18N.get("common.header.order"),
                    I18N.get("common.header.service.code"),
                    I18N.get("common.header.service.name"),
                    I18N.get("common.header.service.parentID"),
                    I18N.get("common.header.service.groupID"),
                    I18N.get("common.header.service.channels"),
                    I18N.get("common.header.service.serviceType"),
                    I18N.get("common.header.service.serviceCycle"),
                    I18N.get("common.header.service.fromDate"),
                    I18N.get("common.header.service.toDate"),
                    I18N.get("common.header.service.unitCode"),
                    I18N.get("common.header.service.exp"),
                    I18N.get("common.header.service.calc")};

            tableCell.setCellValue(I18N.get("common.table.title.service"));
            tableCell.setCellStyle(styles.get("tableName"));

            CellStyle cellStyleW = workbook.createCellStyle();
            Font headerFontW = workbook.createFont();
            headerFontW.setFontHeightInPoints((short) 11);
            cellStyleW.setAlignment(CellStyle.ALIGN_LEFT);
            headerFontW.setItalic(true);
            cellStyleW.setFont(headerFontW);

            sheet.addMergedRegion(new CellRangeAddress(vintStartRowIndex - 4, vintStartRowIndex - 4, 0, columns.length - 1));

            Cell cellNote = headerRow.createCell(columns.length + 1);
            cellNote.setCellStyle(cellStyleW);
            cellNote.setCellValue(I18N.get("common.level.service.note"));

            Cell cellParent = headerRow.createCell(columns.length + 2);
            cellParent.setCellStyle(cellStyleW);
            cellParent.setCellValue(I18N.get("common.level.service.parentID"));
            sheet.addMergedRegion(new CellRangeAddress(vintStartRowIndex - 2, vintStartRowIndex - 2, columns.length + 2, columns.length + 8));


            Row noteRowChannels = sheet.createRow(5);
            Cell cellChannels = noteRowChannels.createCell(columns.length + 2);
            cellChannels.setCellValue(I18N.get("common.level.service.channels"));
            cellChannels.setCellStyle(cellStyleW);
            sheet.addMergedRegion(new CellRangeAddress(5, 5, columns.length + 2, columns.length + 9));


            Row noteRowDate = sheet.createRow(6);
            Cell cellServiceDate = noteRowDate.createCell(columns.length + 2);
            cellServiceDate.setCellValue(I18N.get("common.level.service.serviceDate"));
            cellServiceDate.setCellStyle(cellStyleW);
            sheet.addMergedRegion(new CellRangeAddress(6, 6, columns.length + 2, columns.length + 6));

            Row emptyField = sheet.createRow(7);
            Cell cellEmptyField = emptyField.createCell(columns.length + 2);
            cellEmptyField.setCellValue(I18N.get("common.level.service.emptyField"));
            cellEmptyField.setCellStyle(cellStyleW);
            sheet.addMergedRegion(new CellRangeAddress(7, 7, columns.length + 2, columns.length + 6));


            String[] vobjType = new String[2];
            vobjType[0] = "0-" + I18N.get("common.table.service.type.nonePlan");
            vobjType[1] = "1-" + I18N.get("common.table.service.type.plan");
            XSSFDataValidationHelper dvHelper2 = new XSSFDataValidationHelper(sheet);
            XSSFDataValidationConstraint constraint2 = (XSSFDataValidationConstraint) dvHelper2.createExplicitListConstraint(vobjType);
            CellRangeAddressList addressList2 = new CellRangeAddressList(4, 5000, 6, 6);
            XSSFDataValidation dataValidation2 = (XSSFDataValidation) dvHelper2.createValidation(constraint2, addressList2);
            dataValidation2.setShowErrorBox(true);
            dataValidation2.setEmptyCellAllowed(false);
            sheet.addValidationData(dataValidation2);

            String[] vobjCycle = new String[2];
            vobjCycle[0] = "0-" + I18N.get("common.table.service.cycle.plan");
            vobjCycle[1] = "1-" + I18N.get("common.table.service.cycle.nonePlan");
            XSSFDataValidationHelper dvHelper3 = new XSSFDataValidationHelper(sheet);
            XSSFDataValidationConstraint constraint3 = (XSSFDataValidationConstraint) dvHelper3.createExplicitListConstraint(vobjCycle);
            CellRangeAddressList addressList3 = new CellRangeAddressList(4, 5000, 7, 7);
            XSSFDataValidation dataValidation3 = (XSSFDataValidation) dvHelper3.createValidation(constraint3, addressList3);
            dataValidation3.setShowErrorBox(true);
            dataValidation3.setEmptyCellAllowed(false);
            sheet.addValidationData(dataValidation3);

            String[] vobjCalc = new String[2];
            vobjCalc[0] = "0-" + I18N.get("common.table.service.calc.plan");
            vobjCalc[1] = "1-" + I18N.get("common.table.service.calc.nonePlan");
            XSSFDataValidationHelper dvHelper4 = new XSSFDataValidationHelper(sheet);
            XSSFDataValidationConstraint constraint4 = (XSSFDataValidationConstraint) dvHelper4.createExplicitListConstraint(vobjCalc);
            CellRangeAddressList addressList4 = new CellRangeAddressList(4, 5000, 12, 12);
            XSSFDataValidation dataValidation4 = (XSSFDataValidation) dvHelper4.createValidation(constraint4, addressList4);
            dataValidation4.setShowErrorBox(true);
            dataValidation4.setEmptyCellAllowed(false);
            sheet.addValidationData(dataValidation4);


            for (int col = 0; col < columns.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(columns[col]);
                cell.setCellStyle(styles.get("header"));
                sheet.autoSizeColumn(col);
            }

            XSSFSheet sheet2 = workbook.createSheet(I18N.get("common.table.title.reference"));
            String[] columnsGroup = new String[]{
                    I18N.get("common.header.order"),
                    I18N.get("common.header.group.service.code"),
                    I18N.get("common.header.group.service.name"),
                    I18N.get(""),
                    I18N.get(""),
                    I18N.get(""),
                    I18N.get("common.header.order"),
                    I18N.get("common.header.unit.code"),
                    I18N.get("common.header.unit.name")
            };

            Row titleTable = sheet2.createRow(2);
            titleTable.createCell(2);


            Row headerRowGroup = sheet2.createRow(vintStartRowIndex - 2);
            int noGroup = 0;

            for (int col = 0; col < columnsGroup.length; col++) {
                if (col == 3 || col == 4 || col == 5) {
                    continue;
                }
                Cell cell = headerRowGroup.createCell(col);
                cell.setCellValue(columnsGroup[col]);
                cell.setCellStyle(styles.get("header"));
                sheet2.autoSizeColumn(col);
            }

            Row tableReferenceName = sheet2.createRow(vintStartRowIndex - 4);
            Cell tableCellGroup = tableReferenceName.createCell(0);
            tableCellGroup.setCellValue(I18N.get("common.table.title.group.sheet"));
            tableCellGroup.setCellStyle(styles.get("tableName"));
            sheet2.addMergedRegion(new CellRangeAddress(vintStartRowIndex - 4, vintStartRowIndex - 4, 0, 2));

            Cell tableCellUnit = tableReferenceName.createCell(6);
            tableCellUnit.setCellValue(I18N.get("common.table.title.unit.sheet"));
            tableCellUnit.setCellStyle(styles.get("tableName"));
            sheet2.addMergedRegion(new CellRangeAddress(vintStartRowIndex - 4, vintStartRowIndex - 4, 6, 8));


            if (vlstGroupService.size() > vlstUnits.size()) {
                for (int i = 0; i < vlstGroupService.size(); i++) {
                    noGroup++;
                    Row dataRow = sheet2.createRow(vintStartRowIndex - 2 + noGroup);
                    Cell cellNoGroup = dataRow.createCell(0);
                    cellNoGroup.setCellStyle(styles.get("content"));
                    cellNoGroup.setCellValue(noGroup);
                    Cell cellCodeGroup = dataRow.createCell(1);
                    cellCodeGroup.setCellValue(vlstGroupService.get(i).getCode());
                    cellCodeGroup.setCellStyle(styles.get("content"));
                    Cell cellNameGroup = dataRow.createCell(2);
                    cellNameGroup.setCellValue(vlstGroupService.get(i).getName());
                    cellNameGroup.setCellStyle(styles.get("content"));

                    if (i < vlstUnits.size()) {
                        Cell cellNo = dataRow.createCell(6);
                        cellNo.setCellValue(noGroup);
                        cellNo.setCellStyle(styles.get("content"));
                        Cell cellCode = dataRow.createCell(7);
                        cellCode.setCellValue(vlstUnits.get(i).getCode());
                        cellCode.setCellStyle(styles.get("content"));
                        Cell cellName = dataRow.createCell(8);
                        cellName.setCellValue(vlstUnits.get(i).getName());
                        cellName.setCellStyle(styles.get("content"));
                    }
                }
            } else {
                for (int i = 0; i < vlstUnits.size(); i++) {
                    noGroup++;
                    Row dataRow = sheet2.createRow(vintStartRowIndex - 2 + noGroup);
                    Cell cellNo = dataRow.createCell(6);
                    cellNo.setCellStyle(styles.get("content"));
                    cellNo.setCellValue(noGroup);
                    Cell cellCode = dataRow.createCell(7);
                    cellCode.setCellValue(vlstUnits.get(i).getCode());
                    cellCode.setCellStyle(styles.get("content"));
                    Cell cellName = dataRow.createCell(8);
                    cellName.setCellValue(vlstUnits.get(i).getName());
                    cellName.setCellStyle(styles.get("content"));

                    if (i < vlstGroupService.size()) {
                        Cell cellNoGroup = dataRow.createCell(0);
                        cellNoGroup.setCellValue(noGroup);
                        cellNoGroup.setCellStyle(styles.get("content"));
                        Cell cellCodeGroup = dataRow.createCell(1);
                        cellCodeGroup.setCellValue(vlstGroupService.get(i).getCode());
                        cellCodeGroup.setCellStyle(styles.get("content"));
                        Cell cellNameGroup = dataRow.createCell(2);
                        cellNameGroup.setCellValue(vlstGroupService.get(i).getName());
                        cellNameGroup.setCellStyle(styles.get("content"));
                    }
                }
            }

            sheet2.autoSizeColumn(0);
            sheet2.autoSizeColumn(1);
            sheet2.autoSizeColumn(2);
            sheet2.autoSizeColumn(6);
            sheet2.autoSizeColumn(7);
            sheet2.autoSizeColumn(8);

            writeServiceExample(sheet, vintStartRowIndex, styles);
        }
        workbook.write(out);
        return new ByteArrayInputStream(out.toByteArray());
    }

    private void writeServiceExample(XSSFSheet sheet, int pintStartRowIndex, Map<String, CellStyle> pmpStyles) throws Exception {
        XSSFWorkbook workbook = new XSSFWorkbook();
        Row row = sheet.createRow(pintStartRowIndex - 1);

        row.createCell(0).setCellValue("");
        row.createCell(1).setCellValue("");
        row.createCell(2).setCellValue("");
        row.createCell(3).setCellValue("");
        row.createCell(4).setCellValue("");
        row.createCell(5).setCellValue("");
        row.createCell(6).setCellValue("");
        row.createCell(7).setCellValue("");
        row.createCell(8).setCellValue("");
        row.createCell(9).setCellValue("");
        row.createCell(10).setCellValue("");
        row.createCell(11).setCellValue("");
        row.createCell(12).setCellValue("");

        Cell cell = row.getCell(1);

        for (int i = 0; i < row.getLastCellNum(); i++) {
            row.getCell(i).setCellStyle(pmpStyles.get("content"));
            cell.setCellStyle(pmpStyles.get("date"));
            sheet.autoSizeColumn(i);
        }

        CellStyle cellStyleW = workbook.createCellStyle();
        Font headerFontW = workbook.createFont();
        headerFontW.setFontHeightInPoints((short) 11);
        cellStyleW.setAlignment(CellStyle.ALIGN_LEFT);
        headerFontW.setItalic(true);
        cellStyleW.setFont(headerFontW);

        Cell cell1 = row.createCell(15);
        cell1.setCellValue(I18N.get("common.level.service.groupID"));
        sheet.addMergedRegion(new CellRangeAddress(4, 4, 15, 19));
        sheet.setColumnWidth(0, 1600);
    }

    public ByteArrayInputStream createWordGuideExp() throws IOException {
        try {
            XWPFDocument doc = new XWPFDocument();

            // create paragraph
            XWPFParagraph para = doc.createParagraph();

            // create a run to contain the content
            XWPFRun rh = para.createRun();

            // Format as desired
            rh.setFontSize(15);
            rh.setText(I18N.get(Constants.SERVICES.GUILDEXP));
            para.setAlignment(ParagraphAlignment.CENTER);

            XWPFParagraph paraFirst = doc.createParagraph();
            XWPFRun runParaFirst = paraFirst.createRun();
            runParaFirst.setText("Công thức 1: 1 + 1 = 2");
            runParaFirst.addBreak();
            runParaFirst.setText("Công thức 2: 2 + 2 = 4");

            // write the file
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            doc.write(out);
            out.close();

            return new ByteArrayInputStream(out.toByteArray());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

}
