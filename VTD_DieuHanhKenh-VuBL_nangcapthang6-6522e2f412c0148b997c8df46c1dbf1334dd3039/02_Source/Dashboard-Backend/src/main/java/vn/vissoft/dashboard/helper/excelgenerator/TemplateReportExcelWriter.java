package vn.vissoft.dashboard.helper.excelgenerator;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import vn.vissoft.dashboard.dto.*;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.helper.excelreader.annotation.ExcelColumn;
import vn.vissoft.dashboard.helper.excelreader.annotation.ExcelEntity;
import vn.vissoft.dashboard.repo.PartnerRepo;
import vn.vissoft.dashboard.services.ReportSqlService;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TemplateReportExcelWriter {
    private String mstrStartCol;
    private String mstrEndCol;

    public static String PROVINCE_UNIT_SUM = "BAO_CAO_TONG_HOP_DON_VI_TINH_TOAN_QUOC";
    public static String STAFF_UNIT_SUM = "BAO_CAO_TONG_HOP_DON_VI_NHAN_VIEN_TOAN_QUOC";

    private CellStyle cellStyle;
    public ByteArrayInputStream write(Class pclsClazz,
                                      SqlReportTemplateDTO sqlReportTemplateDTO, ResultsProvincialDTO resultsProvincialDTO) throws Exception {
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
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream resource = classLoader.getResourceAsStream("templates/excel/"+sqlReportTemplateDTO.getType()+".xlsx");

        File file = new File("demo1.txt");
        file.createNewFile();
        copyInputStreamToFile(resource,file);
        FileInputStream fis = new FileInputStream(file);
        XSSFWorkbook workbook = new XSSFWorkbook(fis);

        cellStyle = workbook.createCellStyle();
        cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
        cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
        cellStyle.setBorderRight(CellStyle.BORDER_THIN);
        cellStyle.setBorderTop(CellStyle.BORDER_THIN);

        CellStyle cellStylePercent = workbook.createCellStyle();
        cellStylePercent.setBorderBottom(CellStyle.BORDER_THIN);
        cellStylePercent.setBorderLeft(CellStyle.BORDER_THIN);
        cellStylePercent.setBorderRight(CellStyle.BORDER_THIN);
        cellStylePercent.setBorderTop(CellStyle.BORDER_THIN);
        cellStylePercent.setDataFormat(workbook.createDataFormat().getFormat("0.00%"));

        if(sqlReportTemplateDTO.getType().equals(PROVINCE_UNIT_SUM))
        {
            XSSFSheet sheet = workbook.getSheetAt(0);
            List<BusinessResultProvincialDTO> resultsProvincials= resultsProvincialDTO.getResultsProvincial();
            if(resultsProvincials!=null && resultsProvincials.size()>0) {
                //title
                XSSFRow titleRow = sheet.createRow(0);
                XSSFCell titleCell = titleRow.createCell(0);
                titleCell.setCellValue("BẢNG CHẤM ĐIỂM KẾT QUẢ KINH DOANH VIETTELPAY TỈNH/THÀNH PHỐ NGÀY " +
                        sqlReportTemplateDTO.getMonth().substring(6,sqlReportTemplateDTO.getMonth().length()) +
                        " THÁNG " + sqlReportTemplateDTO.getMonth().substring(4,6) +
                        " NĂM " + sqlReportTemplateDTO.getMonth().substring(0,4));
                CellStyle titleCellStyle = workbook.createCellStyle();
                titleCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
                titleCellStyle.setFillPattern(CellStyle.THIN_FORWARD_DIAG);
                titleCellStyle.setFillBackgroundColor(IndexedColors.WHITE.getIndex());
                Font titleFont = workbook.createFont();
                titleFont.setColor(HSSFColor.BLACK.index);
                titleFont.setFontHeightInPoints((short) 15);
                titleFont.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
                titleFont.setFontName("Times New Roman");
                titleCellStyle.setFont(titleFont);
                titleCellStyle.setAlignment(CellStyle.ALIGN_CENTER);
                titleCell.setCellStyle(titleCellStyle);

                //sub title
                XSSFRow subTitleRow = sheet.getRow(2);
                CellStyle subTitleCellStyle = workbook.createCellStyle();
                subTitleCellStyle.setBorderLeft(CellStyle.BORDER_THIN);
                subTitleCellStyle.setBorderRight(CellStyle.BORDER_THIN);
                subTitleCellStyle.setBorderTop(CellStyle.BORDER_THIN);
                subTitleCellStyle.setBorderBottom(CellStyle.BORDER_THIN);
                subTitleCellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
                subTitleCellStyle.setFillPattern(CellStyle.THIN_FORWARD_DIAG);
                subTitleCellStyle.setFillBackgroundColor(IndexedColors.YELLOW.getIndex());
                Font subTitleFont = workbook.createFont();
                subTitleFont.setColor(HSSFColor.BLACK.index);
                subTitleFont.setFontHeightInPoints((short) 12);
                subTitleFont.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
                subTitleFont.setFontName("Times New Roman");
                subTitleCellStyle.setFont(subTitleFont);
                subTitleCellStyle.setAlignment(CellStyle.ALIGN_CENTER);

                XSSFCell subTitleCell = subTitleRow.createCell(5);
                String scoreTbtt = resultsProvincialDTO.getScoreTbtt()==null?"N/A":resultsProvincialDTO.getScoreTbtt().toString();
                subTitleCell.setCellValue("THUÊ BAO TĂNG THÊM ("+scoreTbtt+" điểm)");
                subTitleCell.setCellStyle(subTitleCellStyle);
                XSSFCell subTitleCell2 = subTitleRow.createCell(11);
                String scoreTbm = resultsProvincialDTO.getScoreTbm()==null?"N/A":resultsProvincialDTO.getScoreTbm().toString();
                subTitleCell2.setCellValue("THUÊ BAO MỚI ("+scoreTbm+" điểm)");
                subTitleCell2.setCellStyle(subTitleCellStyle);
                XSSFCell subTitleCell3 = subTitleRow.createCell(15);
                String scoreTbrm = resultsProvincialDTO.getScoreTbrm()==null?"N/A":resultsProvincialDTO.getScoreTbrm().toString();
                subTitleCell3.setCellValue("THUÊ BAO RỜI MẠNG ("+scoreTbrm+" điểm)");
                subTitleCell3.setCellStyle(subTitleCellStyle);


                CellStyle logCellStyle = workbook.createCellStyle();
                logCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
                logCellStyle.setFillPattern(CellStyle.THIN_FORWARD_DIAG);
                logCellStyle.setFillBackgroundColor(IndexedColors.WHITE.getIndex());
                Font logFont = workbook.createFont();
                logFont.setColor(HSSFColor.BLACK.index);
                logFont.setFontHeightInPoints((short) 11);
                logFont.setFontName("Times New Roman");
                logFont.setItalic(true);
                logCellStyle.setFont(logFont);
                logCellStyle.setAlignment(CellStyle.ALIGN_CENTER);
                XSSFRow logRow = sheet.createRow(1);
                XSSFCell logUserCell = logRow.createCell(10);
                logUserCell.setCellValue("Người kết xuất: "+sqlReportTemplateDTO.getUser());
                logUserCell.setCellStyle(logCellStyle);
                XSSFCell logDateCell = logRow.createCell(13);
                logDateCell.setCellValue("Ngày kết xuất: "+new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
                logDateCell.setCellStyle(logCellStyle);

                //rows
                int beginIndex = 5;
                for (int i = 0; i < resultsProvincials.size(); i++) {
                    BusinessResultProvincialDTO resultsProvincial = resultsProvincials.get(i);
                    XSSFRow row = sheet.createRow(beginIndex + i);
                    int columnIndex = 0;
                    this.addCellWithNull(row,columnIndex, i + 1);
                    columnIndex++;
                    this.addCellWithNull(row,columnIndex, resultsProvincial.getProvincialCode());
                    columnIndex++;
                    this.addCellWithNull(row,columnIndex, resultsProvincial.getProvincialCode());
                    columnIndex++;
                    this.addCellWithNull(row, columnIndex, resultsProvincial.getTotalScore());
                    columnIndex++;
                    this.addCellWithNull(row, columnIndex, resultsProvincial.getRank());
                    columnIndex++;

                    List<BusinessResultDetailDTO> details = resultsProvincial.getListDetail();
                    if (details.size() == 3) {
                        //added
                        BusinessResultDetailDTO addedSub = details.get(0);

                        this.addCellWithNull(row, columnIndex, addedSub.getScheduleMonth());
                        columnIndex++;
                        this.addCellWithNull(row, columnIndex, addedSub.getPerformAccumulatedN1());
                        columnIndex++;
                        this.addCellWithNull(row, columnIndex, addedSub.getPerformAccumulatedN());
                        columnIndex++;
                        this.addCellWithNull(row, columnIndex, addedSub.getDelta());
                        columnIndex++;
                        this.addCellWithNull(row, columnIndex, addedSub.getComplete());

                        if(addedSub.getComplete()==null) {
                            XSSFCell completeCell = row.createCell(columnIndex);
                            completeCell.setCellValue("");
                            completeCell.setCellStyle(cellStylePercent);
                        }
                        else
                        {
                            XSSFCell completeCell = row.createCell(columnIndex);
                            completeCell.setCellValue(addedSub.getComplete());
                            completeCell.setCellStyle(cellStylePercent);
                        }
                        columnIndex++;
                        this.addCellWithNull(row, columnIndex, addedSub.getScorePass());
                        columnIndex++;

                        //new
                        BusinessResultDetailDTO newSub = details.get(1);

                        this.addCellWithNull(row, columnIndex, newSub.getScheduleMonth());
                        columnIndex++;
                        this.addCellWithNull(row, columnIndex, newSub.getPerformAccumulatedN());
                        columnIndex++;


                        if(newSub.getComplete()==null) {
                            XSSFCell completeCell = row.createCell(columnIndex);
                            completeCell.setCellValue("");
                            completeCell.setCellStyle(cellStylePercent);
                        }
                        else
                        {
                            XSSFCell completeCell = row.createCell(columnIndex);
                            completeCell.setCellValue(newSub.getComplete());
                            completeCell.setCellStyle(cellStylePercent);
                        }
                        columnIndex++;
                        this.addCellWithNull(row, columnIndex, newSub.getScorePass());
                        columnIndex++;

                        //leave
                        BusinessResultDetailDTO leaveSub = details.get(2);

                        this.addCellWithNull(row, columnIndex, leaveSub.getScheduleMonth());
                        columnIndex++;
                        this.addCellWithNull(row, columnIndex, leaveSub.getPerformAccumulatedN());
                        columnIndex++;

                        if(leaveSub.getComplete()==null) {
                            XSSFCell completeCell = row.createCell(columnIndex);
                            completeCell.setCellValue("");
                            completeCell.setCellStyle(cellStylePercent);
                        }
                        else
                        {
                            XSSFCell completeCell = row.createCell(columnIndex);
                            completeCell.setCellValue(leaveSub.getComplete());
                            completeCell.setCellStyle(cellStylePercent);
                        }
                        columnIndex++;
                        this.addCellWithNull(row, columnIndex, leaveSub.getScorePass());
                        columnIndex++;

                    }
                }
                XSSFCellStyle stylePercentage = workbook.createCellStyle();

                stylePercentage.setBorderBottom(CellStyle.BORDER_THIN);
                stylePercentage.setBorderLeft(CellStyle.BORDER_THIN);
                stylePercentage.setBorderRight(CellStyle.BORDER_THIN);
                stylePercentage.setBorderTop(CellStyle.BORDER_THIN);
                XSSFFont fontPercentage = workbook.createFont();
                fontPercentage.setFontHeightInPoints((short) 12);
                fontPercentage.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
                fontPercentage.setFontName("Times New Roman");
                stylePercentage.setFont(fontPercentage);
                stylePercentage.setDataFormat(workbook.createDataFormat().getFormat("0.00%"));


                XSSFCellStyle styleOtherHead = workbook.createCellStyle();

                styleOtherHead.setBorderBottom(CellStyle.BORDER_THIN);
                styleOtherHead.setBorderLeft(CellStyle.BORDER_THIN);
                styleOtherHead.setBorderRight(CellStyle.BORDER_THIN);
                styleOtherHead.setBorderTop(CellStyle.BORDER_THIN);
                XSSFFont fontOtherHead = workbook.createFont();
                fontOtherHead.setFontHeightInPoints((short) 12);
                fontOtherHead.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
                fontOtherHead.setFontName("Times New Roman");
                styleOtherHead.setFont(fontOtherHead);


                XSSFRow sumRow = sheet.createRow(beginIndex - 1);

                sumRow.createCell(0).setCellValue("Tổng");

                XSSFCell cellSumScheduleMonth = sumRow.createCell(5);
                cellSumScheduleMonth.setCellType(HSSFCell.CELL_TYPE_FORMULA);
                cellSumScheduleMonth.setCellFormula("IFERROR(SUM(F6:F" + resultsProvincials.size()+ beginIndex + "),0)");
                cellSumScheduleMonth.setCellStyle(styleOtherHead);

                XSSFCell cellSumAddedPerformAccumulatedN1 = sumRow.createCell(6);
                cellSumAddedPerformAccumulatedN1.setCellType(HSSFCell.CELL_TYPE_FORMULA);
                cellSumAddedPerformAccumulatedN1.setCellFormula("IFERROR(SUM(G6:G" + resultsProvincials.size()+ beginIndex + "),0)");
                cellSumAddedPerformAccumulatedN1.setCellStyle(styleOtherHead);

                XSSFCell cellSumAddedPerformAccumulatedN = sumRow.createCell(7);
                cellSumAddedPerformAccumulatedN.setCellType(HSSFCell.CELL_TYPE_FORMULA);
                cellSumAddedPerformAccumulatedN.setCellFormula("IFERROR(SUM(H6:H" + resultsProvincials.size()+ beginIndex + "),0)");
                cellSumAddedPerformAccumulatedN.setCellStyle(styleOtherHead);

                XSSFCell cellSumAddedDelta = sumRow.createCell(8);
                cellSumAddedDelta.setCellType(HSSFCell.CELL_TYPE_FORMULA);
                cellSumAddedDelta.setCellFormula("IFERROR(SUM(I6:I" + resultsProvincials.size()+ beginIndex + "),0)");
                cellSumAddedDelta.setCellStyle(styleOtherHead);
                XSSFCell cell9 = sumRow.createCell(9);
                cell9.setCellType(HSSFCell.CELL_TYPE_FORMULA);
                cell9.setCellFormula("IFERROR(I5/F5,0)");
                cell9.setCellStyle(stylePercentage);

                XSSFCell cellSumNewScheduleMonth = sumRow.createCell(11);
                cellSumNewScheduleMonth.setCellType(HSSFCell.CELL_TYPE_FORMULA);
                cellSumNewScheduleMonth.setCellFormula("IFERROR(SUM(L6:L" + resultsProvincials.size()+ beginIndex + "),0)");
                cellSumNewScheduleMonth.setCellStyle(styleOtherHead);

                XSSFCell cellSumNewPerformAccumulatedN = sumRow.createCell(12);
                cellSumNewPerformAccumulatedN.setCellType(HSSFCell.CELL_TYPE_FORMULA);
                cellSumNewPerformAccumulatedN.setCellFormula("IFERROR(SUM(M6:M" + resultsProvincials.size()+ beginIndex + "),0)");
                cellSumNewPerformAccumulatedN.setCellStyle(styleOtherHead);

                XSSFCell cell13 = sumRow.createCell(13);
                cell13.setCellType(HSSFCell.CELL_TYPE_FORMULA);
                cell13.setCellFormula("IFERROR(M5/L5,0)");
                cell13.setCellStyle(stylePercentage);


                XSSFCell cellSumLeaveScheduleMonth = sumRow.createCell(15);
                cellSumLeaveScheduleMonth.setCellType(HSSFCell.CELL_TYPE_FORMULA);
                cellSumLeaveScheduleMonth.setCellFormula("IFERROR(AVERAGE(P6:P" + resultsProvincials.size()+ beginIndex + "),0)");
                cellSumLeaveScheduleMonth.setCellStyle(styleOtherHead);

                XSSFCell cellSumLeavePerformAccumulatedN = sumRow.createCell(16);
                cellSumLeavePerformAccumulatedN.setCellType(HSSFCell.CELL_TYPE_FORMULA);
                cellSumLeavePerformAccumulatedN.setCellFormula("IFERROR(AVERAGE(Q6:Q" + resultsProvincials.size()+ beginIndex + "),0)");
                cellSumLeavePerformAccumulatedN.setCellStyle(styleOtherHead);

                XSSFCell cell17 = sumRow.createCell(17);
                cell17.setCellType(HSSFCell.CELL_TYPE_FORMULA);
                cell17.setCellFormula("IFERROR(2-Q5/P5,0)");
                cell17.setCellStyle(stylePercentage);
                sumRow.createCell(18).setCellStyle(styleOtherHead);
            }
        }

        workbook.write(out);
        return new ByteArrayInputStream(out.toByteArray());
    }



    public ByteArrayInputStream write(Class pclsClazz,
                                      SqlReportTemplateDTO sqlReportTemplateDTO, ResultsStaffDTO resultsStaffDTO) throws Exception {
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
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream resource = classLoader.getResourceAsStream("templates/excel/"+sqlReportTemplateDTO.getType()+".xlsx");
        File file = new File("demo1.txt");
        file.createNewFile();
        copyInputStreamToFile(resource,file);
        FileInputStream fis = new FileInputStream(file);
        XSSFWorkbook workbook = new XSSFWorkbook(fis);

        cellStyle = workbook.createCellStyle();
        cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
        cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
        cellStyle.setBorderRight(CellStyle.BORDER_THIN);
        cellStyle.setBorderTop(CellStyle.BORDER_THIN);

        CellStyle cellStylePercent = workbook.createCellStyle();
        cellStylePercent.setBorderBottom(CellStyle.BORDER_THIN);
        cellStylePercent.setBorderLeft(CellStyle.BORDER_THIN);
        cellStylePercent.setBorderRight(CellStyle.BORDER_THIN);
        cellStylePercent.setBorderTop(CellStyle.BORDER_THIN);
        cellStylePercent.setDataFormat(workbook.createDataFormat().getFormat("0.00%"));
        if(sqlReportTemplateDTO.getType().equals(STAFF_UNIT_SUM))
        {
            XSSFSheet sheet = workbook.getSheetAt(0);
            List<BusinessResultStaffDTO> resultsStaffs= resultsStaffDTO.getResultsStaff();
            if(resultsStaffs!=null && resultsStaffs.size()>0) {
                //title
                XSSFRow titleRow = sheet.createRow(0);
                XSSFCell titleCell = titleRow.createCell(0);
                titleCell.setCellValue("BẢNG CHẤM ĐIỂM KẾT QUẢ KINH DOANH NHÂN VIÊN VIETTELPAY TỈNH/THÀNH PHỐ NGÀY " +
                        sqlReportTemplateDTO.getMonth().substring(6,sqlReportTemplateDTO.getMonth().length()) +
                        " THÁNG " + sqlReportTemplateDTO.getMonth().substring(4,6) +
                        " NĂM " + sqlReportTemplateDTO.getMonth().substring(0,4));
                CellStyle titleCellStyle = workbook.createCellStyle();
                titleCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
                titleCellStyle.setFillPattern(CellStyle.THIN_FORWARD_DIAG);
                titleCellStyle.setFillBackgroundColor(IndexedColors.WHITE.getIndex());
                Font titleFont = workbook.createFont();
                titleFont.setColor(HSSFColor.BLACK.index);
                titleFont.setFontHeightInPoints((short) 15);
                titleFont.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
                titleFont.setFontName("Times New Roman");
                titleCellStyle.setFont(titleFont);
                titleCellStyle.setAlignment(CellStyle.ALIGN_CENTER);
                titleCell.setCellStyle(titleCellStyle);

                //sub title
                XSSFRow subTitleRow = sheet.getRow(3);
                CellStyle subTitleCellStyle = workbook.createCellStyle();
                subTitleCellStyle.setBorderLeft(CellStyle.BORDER_THIN);
                subTitleCellStyle.setBorderRight(CellStyle.BORDER_THIN);
                subTitleCellStyle.setBorderTop(CellStyle.BORDER_THIN);
                subTitleCellStyle.setBorderBottom(CellStyle.BORDER_THIN);
                subTitleCellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
                subTitleCellStyle.setFillPattern(CellStyle.THIN_FORWARD_DIAG);
                subTitleCellStyle.setFillBackgroundColor(IndexedColors.YELLOW.getIndex());
                Font subTitleFont = workbook.createFont();
                subTitleFont.setColor(HSSFColor.BLACK.index);
                subTitleFont.setFontHeightInPoints((short) 12);
                subTitleFont.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
                subTitleFont.setFontName("Times New Roman");
                subTitleCellStyle.setFont(subTitleFont);
                subTitleCellStyle.setAlignment(CellStyle.ALIGN_CENTER);

                XSSFCell subTitleCell = subTitleRow.createCell(7);
                String scoreTbtt = resultsStaffDTO.getScoreTbtt()==null?"N/A":resultsStaffDTO.getScoreTbtt().toString();
                subTitleCell.setCellValue("THUÊ BAO TĂNG THÊM ("+scoreTbtt+" điểm)");
                subTitleCell.setCellStyle(subTitleCellStyle);
                XSSFCell subTitleCell2 = subTitleRow.createCell(13);
                String scoreTbm = resultsStaffDTO.getScoreTbm()==null?"N/A":resultsStaffDTO.getScoreTbm().toString();
                subTitleCell2.setCellValue("THUÊ BAO MỚI ("+scoreTbm+" điểm)");
                subTitleCell2.setCellStyle(subTitleCellStyle);
                XSSFCell subTitleCell3 = subTitleRow.createCell(17);
                String scoreTbrm = resultsStaffDTO.getScoreTbrm()==null?"N/A":resultsStaffDTO.getScoreTbrm().toString();
                subTitleCell3.setCellValue("THUÊ BAO RỜI MẠNG ("+scoreTbrm+" điểm)");
                subTitleCell3.setCellStyle(subTitleCellStyle);

                CellStyle logCellStyle = workbook.createCellStyle();
                logCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
                logCellStyle.setFillPattern(CellStyle.THIN_FORWARD_DIAG);
                logCellStyle.setFillBackgroundColor(IndexedColors.WHITE.getIndex());
                Font logFont = workbook.createFont();
                logFont.setColor(HSSFColor.BLACK.index);
                logFont.setFontHeightInPoints((short) 11);
                logFont.setFontName("Times New Roman");
                logFont.setItalic(true);
                logCellStyle.setFont(logFont);
                logCellStyle.setAlignment(CellStyle.ALIGN_CENTER);
                XSSFRow logRow = sheet.createRow(2);
                XSSFCell logUserCell = logRow.createCell(12);
                logUserCell.setCellValue("Người kết xuất: "+sqlReportTemplateDTO.getUser());
                logUserCell.setCellStyle(logCellStyle);
                XSSFCell logDateCell = logRow.createCell(15);
                logDateCell.setCellValue("Ngày kết xuất: "+new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
                logDateCell.setCellStyle(logCellStyle);

                //rows
                int beginIndex = 6;
                for (int i = 0; i < resultsStaffs.size(); i++) {
                    BusinessResultStaffDTO resultsStaff = resultsStaffs.get(i);
                    XSSFRow row = sheet.createRow(beginIndex + i);
                    int columnIndex = 0;
                    this.addCellWithNull(row,columnIndex, i + 1);
                    columnIndex++;
                    this.addCellWithNull(row,columnIndex, resultsStaff.getProvincialCode());
                    columnIndex++;
                    this.addCellWithNull(row,columnIndex, resultsStaff.getProvincialCode());
                    columnIndex++;
                    this.addCellWithNull(row,columnIndex, resultsStaff.getStaffCode());
                    columnIndex++;
                    this.addCellWithNull(row,columnIndex, resultsStaff.getStaffName());
                    columnIndex++;
                    this.addCellWithNull(row, columnIndex, resultsStaff.getTotalScore());
                    columnIndex++;
                    this.addCellWithNull(row, columnIndex, resultsStaff.getRank());
                    columnIndex++;

                    List<BusinessResultDetailDTO> details = resultsStaff.getListDetail();
                    if (details.size() == 3) {
                        //added
                        BusinessResultDetailDTO addedSub = details.get(0);

                        this.addCellWithNull(row, columnIndex, addedSub.getScheduleMonth());
                        columnIndex++;
                        this.addCellWithNull(row, columnIndex, addedSub.getPerformAccumulatedN1());
                        columnIndex++;
                        this.addCellWithNull(row, columnIndex, addedSub.getPerformAccumulatedN());
                        columnIndex++;
                        this.addCellWithNull(row, columnIndex, addedSub.getDelta());
                        columnIndex++;
                        if(addedSub.getComplete()==null) {
                            XSSFCell completeCell = row.createCell(columnIndex);
                            completeCell.setCellValue("");
                            completeCell.setCellStyle(cellStylePercent);
                        }
                        else
                        {
                            XSSFCell completeCell = row.createCell(columnIndex);
                            completeCell.setCellValue(addedSub.getComplete());
                            completeCell.setCellStyle(cellStylePercent);
                        }
                        columnIndex++;
                        this.addCellWithNull(row, columnIndex, addedSub.getScorePass());
                        columnIndex++;

                        //new
                        BusinessResultDetailDTO newSub = details.get(1);

                        this.addCellWithNull(row, columnIndex, newSub.getScheduleMonth());
                        columnIndex++;
                        this.addCellWithNull(row, columnIndex, newSub.getPerformAccumulatedN());
                        columnIndex++;
                        if(newSub.getComplete()==null) {
                            XSSFCell completeCell = row.createCell(columnIndex);
                            completeCell.setCellValue("");
                            completeCell.setCellStyle(cellStylePercent);
                        }
                        else
                        {
                            XSSFCell completeCell = row.createCell(columnIndex);
                            completeCell.setCellValue(newSub.getComplete());
                            completeCell.setCellStyle(cellStylePercent);
                        }
                        columnIndex++;
                        this.addCellWithNull(row, columnIndex, newSub.getScorePass());
                        columnIndex++;

                        //leave
                        BusinessResultDetailDTO leaveSub = details.get(2);

                        this.addCellWithNull(row, columnIndex, leaveSub.getScheduleMonth());
                        columnIndex++;
                        this.addCellWithNull(row, columnIndex, leaveSub.getPerformAccumulatedN());
                        columnIndex++;
                        if(leaveSub.getComplete()==null) {
                            XSSFCell completeCell = row.createCell(columnIndex);
                            completeCell.setCellValue("");
                            completeCell.setCellStyle(cellStylePercent);
                        }
                        else
                        {
                            XSSFCell completeCell = row.createCell(columnIndex);
                            completeCell.setCellValue(leaveSub.getComplete());
                            completeCell.setCellStyle(cellStylePercent);
                        }
                        columnIndex++;
                        this.addCellWithNull(row, columnIndex, leaveSub.getScorePass());
                        columnIndex++;

                    }
                }

                XSSFCellStyle stylePercentage = workbook.createCellStyle();

                stylePercentage.setBorderBottom(CellStyle.BORDER_THIN);
                stylePercentage.setBorderLeft(CellStyle.BORDER_THIN);
                stylePercentage.setBorderRight(CellStyle.BORDER_THIN);
                stylePercentage.setBorderTop(CellStyle.BORDER_THIN);
                XSSFFont fontPercentage = workbook.createFont();
                fontPercentage.setFontHeightInPoints((short) 12);
                fontPercentage.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
                fontPercentage.setFontName("Times New Roman");
                stylePercentage.setFont(fontPercentage);
                stylePercentage.setDataFormat(workbook.createDataFormat().getFormat("0.00%"));


                XSSFCellStyle styleOtherHead = workbook.createCellStyle();

                styleOtherHead.setBorderBottom(CellStyle.BORDER_THIN);
                styleOtherHead.setBorderLeft(CellStyle.BORDER_THIN);
                styleOtherHead.setBorderRight(CellStyle.BORDER_THIN);
                styleOtherHead.setBorderTop(CellStyle.BORDER_THIN);
                XSSFFont fontOtherHead = workbook.createFont();
                fontOtherHead.setFontHeightInPoints((short) 12);
                fontOtherHead.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
                fontOtherHead.setFontName("Times New Roman");
                styleOtherHead.setFont(fontOtherHead);

                XSSFRow sumRow = sheet.createRow(beginIndex - 1);

                sumRow.createCell(0).setCellValue("Tổng");

                XSSFCell cellSumScheduleMonth = sumRow.createCell(7);
                cellSumScheduleMonth.setCellType(HSSFCell.CELL_TYPE_FORMULA);
                cellSumScheduleMonth.setCellFormula("IFERROR(SUM(H7:H" + resultsStaffs.size()+ beginIndex + "),0)");
                cellSumScheduleMonth.setCellStyle(styleOtherHead);

                XSSFCell cellSumAddedPerformAccumulatedN1 = sumRow.createCell(8);
                cellSumAddedPerformAccumulatedN1.setCellType(HSSFCell.CELL_TYPE_FORMULA);
                cellSumAddedPerformAccumulatedN1.setCellFormula("IFERROR(SUM(I7:I" + resultsStaffs.size()+ beginIndex + "),0)");
                cellSumAddedPerformAccumulatedN1.setCellStyle(styleOtherHead);

                XSSFCell cellSumAddedPerformAccumulatedN = sumRow.createCell(9);
                cellSumAddedPerformAccumulatedN.setCellType(HSSFCell.CELL_TYPE_FORMULA);
                cellSumAddedPerformAccumulatedN.setCellFormula("IFERROR(SUM(J7:J" + resultsStaffs.size()+ beginIndex + "),0)");
                cellSumAddedPerformAccumulatedN.setCellStyle(styleOtherHead);

                XSSFCell cellSumAddedDelta = sumRow.createCell(10);
                cellSumAddedDelta.setCellType(HSSFCell.CELL_TYPE_FORMULA);
                cellSumAddedDelta.setCellFormula("IFERROR(SUM(K7:K" + resultsStaffs.size()+ beginIndex + "),)");
                cellSumAddedDelta.setCellStyle(styleOtherHead);

                XSSFCell cell9 = sumRow.createCell(11);
                cell9.setCellType(HSSFCell.CELL_TYPE_FORMULA);
                cell9.setCellFormula("IFERROR(K6/H6,0)");
                cell9.setCellStyle(stylePercentage);

                XSSFCell cellSumNewScheduleMonth = sumRow.createCell(13);
                cellSumNewScheduleMonth.setCellType(HSSFCell.CELL_TYPE_FORMULA);
                cellSumNewScheduleMonth.setCellFormula("IFERROR(SUM(N7:N" + resultsStaffs.size()+ beginIndex + "),0)");
                cellSumNewScheduleMonth.setCellStyle(styleOtherHead);

                XSSFCell cellSumNewPerformAccumulatedN = sumRow.createCell(14);
                cellSumNewPerformAccumulatedN.setCellType(HSSFCell.CELL_TYPE_FORMULA);
                cellSumNewPerformAccumulatedN.setCellFormula("IFERROR(SUM(O7:O" + resultsStaffs.size()+ beginIndex + "),0)");
                cellSumNewPerformAccumulatedN.setCellStyle(styleOtherHead);

                XSSFCell cell13 = sumRow.createCell(15);
                cell13.setCellType(HSSFCell.CELL_TYPE_FORMULA);
                cell13.setCellFormula("IFERROR(O6/N6,0)");
                cell13.setCellStyle(stylePercentage);


                XSSFCell cellSumLeaveScheduleMonth = sumRow.createCell(17);
                cellSumLeaveScheduleMonth.setCellType(HSSFCell.CELL_TYPE_FORMULA);
                cellSumLeaveScheduleMonth.setCellFormula("IFERROR(AVERAGE(R7:R" + resultsStaffs.size()+ beginIndex + "),0)");
                cellSumLeaveScheduleMonth.setCellStyle(styleOtherHead);

                XSSFCell cellSumLeavePerformAccumulatedN = sumRow.createCell(18);
                cellSumLeavePerformAccumulatedN.setCellType(HSSFCell.CELL_TYPE_FORMULA);
                cellSumLeavePerformAccumulatedN.setCellFormula("IFERROR(AVERAGE(S7:S" + resultsStaffs.size()+ beginIndex + "),0)");
                cellSumLeavePerformAccumulatedN.setCellStyle(styleOtherHead);

                XSSFCell cell17 = sumRow.createCell(19);
                cell17.setCellType(HSSFCell.CELL_TYPE_FORMULA);
                cell17.setCellFormula("IFERROR(2-S6/R6,0)");
                cell17.setCellStyle(stylePercentage);

                sumRow.createCell(20).setCellStyle(styleOtherHead);
            }
        }

        workbook.write(out);
        return new ByteArrayInputStream(out.toByteArray());
    }

    private void addCellWithNull(XSSFRow row, int index, Double val){
        XSSFCell cell = row.createCell(index);
        if(val==null) {
            cell.setCellValue("");
        }
        else
        {
            cell.setCellValue(val);
        }
        cell.setCellStyle(cellStyle);
    }

    private void addCellWithNull(XSSFRow row, int index, String val){
        XSSFCell cell = row.createCell(index);
        if(val==null) {
            cell.setCellValue("");
        }
        else
        {
            cell.setCellValue(val);
        }
        cell.setCellStyle(cellStyle);
    }

    private void addCellWithNull(XSSFRow row, int index, Integer val){
        XSSFCell cell = row.createCell(index);
        if(val==null) {
            cell.setCellValue("");
        }
        else
        {
            cell.setCellValue(val);
        }
        cell.setCellStyle(cellStyle);
    }

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

    private CellStyle createBorderedStyle(XSSFWorkbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);
        return cellStyle;
    }

    private static void copyInputStreamToFile(InputStream inputStream, File file)
            throws IOException {

        try (FileOutputStream outputStream = new FileOutputStream(file)) {

            int read;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }

            // commons-io
            //IOUtils.copy(inputStream, outputStream);

        }

    }

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
