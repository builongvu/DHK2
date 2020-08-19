package vn.vissoft.dashboard.helper.excelgenerator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.vissoft.dashboard.config.I18N;
import vn.vissoft.dashboard.helper.excelreader.annotation.ExcelEntity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.annotation.Annotation;
import java.util.Map;

@Component
public class ServiceScoreWriter {

    @Autowired
    private VTTExcelWriter vttExcelWriter;

    /**
     * download file mau trong so chi tieu
     *
     * @param pclsClazz
     * @return stream file
     * @throws Exception
     * @author DatNT
     * @version 1.0
     * @since 2019/12/12
     */
    public ByteArrayInputStream write(Class pclsClazz) throws Exception {
        String vstrSignal;
        int vintStartRowIndex;
        String[] columns = new String[]{I18N.get("common.header.order"), I18N.get("common.header.channel"),
                I18N.get("common.header.unit"), I18N.get("common.header.staff"), I18N.get("common.header.servicecode"),
                I18N.get("common.header.score"), I18N.get("common.header.service.fromDate"), I18N.get("common.header.service.toDate"), I18N.get("common.header.scoremax")};
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
        XSSFSheet sheet = workbook.createSheet();
        Map<String, CellStyle> styles = vttExcelWriter.createStyles(workbook);

        //row for header
        Row h = sheet.createRow(0);
        Cell c = h.createCell(0);
        c.setCellValue(vstrSignal);

        //row for table name
        Row tableName = sheet.createRow(vintStartRowIndex - 4);
        Cell tableCell = tableName.createCell(0);
        tableCell.setCellValue(I18N.get("common.table.title.service.weight"));
        tableCell.setCellStyle(styles.get("tableName"));

        //create header row
        Row headerRow = sheet.createRow(vintStartRowIndex - 2);


        sheet.addMergedRegion(new CellRangeAddress(vintStartRowIndex - 4, vintStartRowIndex - 4, 0, columns.length - 1));
        //set row note vds
        Row noteRow = sheet.createRow(vintStartRowIndex - 3);
        Cell noteCell = noteRow.createCell(columns.length);
        noteCell.setCellValue(I18N.get("common.service.score.note"));
        noteCell.setCellStyle(styles.get("note"));

        String[] vdsVChannel = new String[3];
        vdsVChannel[0] = "VDS_TINH-" + I18N.get("common.table.warning.config.service.channel1");
        vdsVChannel[1] = "VDS_KHDN-" + I18N.get("common.table.warning.config.service.channel2");
        vdsVChannel[2] = "VDS_BANHANGSO-" + I18N.get("common.table.warning.config.service.channel3");
        XSSFDataValidationHelper dvHelper4 = new XSSFDataValidationHelper(sheet);
        XSSFDataValidationConstraint constraint4 = (XSSFDataValidationConstraint) dvHelper4.createExplicitListConstraint(vdsVChannel);
        CellRangeAddressList addressList4 = new CellRangeAddressList(5, 5000, 1 ,1);
        XSSFDataValidation dataValidation4 = (XSSFDataValidation) dvHelper4.createValidation(constraint4, addressList4);
        dataValidation4.setShowErrorBox(true);
        dataValidation4.setEmptyCellAllowed(false);
        sheet.addValidationData(dataValidation4);

        Row blankRow = sheet.createRow(vintStartRowIndex - 1);
        //header
        for (int col = 0; col < columns.length; col++) {
            Cell cell = headerRow.createCell(col);
            cell.setCellValue(columns[col]);
            cell.setCellStyle(styles.get("header"));
            blankRow.createCell(col).setCellValue("");
            blankRow.getCell(col).setCellStyle(styles.get("content"));
            sheet.autoSizeColumn(col);
        }

        workbook.write(out);
        return new ByteArrayInputStream(out.toByteArray());
    }
}
