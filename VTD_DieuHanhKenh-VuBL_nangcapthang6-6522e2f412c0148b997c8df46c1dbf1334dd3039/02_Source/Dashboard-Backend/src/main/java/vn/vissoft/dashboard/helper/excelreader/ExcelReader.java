package vn.vissoft.dashboard.helper.excelreader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import vn.vissoft.dashboard.config.I18N;
import vn.vissoft.dashboard.dto.excel.BaseExcelEntity;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.helper.excelgenerator.VTTExcelWriter;
import vn.vissoft.dashboard.helper.excelreader.annotation.*;
import vn.vissoft.dashboard.helper.excelreader.exception.*;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ExcelReader<T extends BaseExcelEntity> {

    private static final Logger LOGGER = LogManager.getLogger(ExcelReader.class);

    private String mstrStartCol;
    private String mstrEndCol;

    public List<T> read(String pstrFilePath, Class pclsClazz) throws Exception {
        String vstrSignal;
        int mintStartRowIndex;

        List<T> ret = new ArrayList<>();
        if (pclsClazz.isAnnotationPresent(ExcelEntity.class)) {
            Annotation ann1 = pclsClazz.getAnnotation(ExcelEntity.class);
            ExcelEntity entityAnn = (ExcelEntity) ann1;

            vstrSignal = entityAnn.signalConstant();
            mintStartRowIndex = ((ExcelEntity) ann1).dataStartRowIndex();

        } else {
            throw new Exception("You must annotate class with @ExcelEntity");
        }

        determineColRange(pclsClazz);

        XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(pstrFilePath));
        XSSFSheet sheet = workbook.getSheetAt(0);

        if (vstrSignal != null && !checkFileTemplate(sheet, vstrSignal)) {
            throw new WrongFileTemplateException(I18N.get("common.excel.template.import.error"));
        }

        Iterator<Row> rows = sheet.iterator();

        int rowIndex = 0;
        while (rows.hasNext()) {
            if (rowIndex < mintStartRowIndex - 1) {
                rowIndex++;
                continue;
            }
            XSSFRow row = sheet.getRow(rowIndex);
            if (row == null) {
                break;
            }

            if (row != null) {
                T obj = null;
                try {
                    if (!isRowEmpty(row)) {
                        obj = (T) pclsClazz.newInstance();

                        //order lai thu tu
                        List<Method> vlst = new ArrayList<Method>();
                        for (Method method : pclsClazz.getDeclaredMethods()) {
                            if (method.isAnnotationPresent(ExcelColumn.class)) {
                                vlst.add(method);
                            } else if (method.isAnnotationPresent(IndexColumn.class)) {
                                PropertyDescriptor pd = getPropertyDescriptor(method);
                                Method setterMethod = pd.getWriteMethod();
                                setterMethod.invoke(obj, rowIndex);
                            }
                        }
                        Collections.sort(vlst, new Comparator<Method>() {
                            @Override
                            public int compare(Method o1, Method o2) {
                                ExcelColumn colAnn1 = (ExcelColumn) o1.getAnnotation(ExcelColumn.class);
                                String colName1 = colAnn1.name();
                                ExcelColumn colAnn2 = (ExcelColumn) o2.getAnnotation(ExcelColumn.class);
                                String colName2 = colAnn2.name();
                                return colName1.compareTo(colName2);
                            }
                        });
                        for (int i = 0; i < vlst.size(); i++) {
                            Method method = vlst.get(i);
                            ExcelColumn colAnn = (ExcelColumn) method.getAnnotation(ExcelColumn.class);
                            String colName = colAnn.name();
                            boolean nullable = colAnn.nullable();
                            PropertyDescriptor pd = getPropertyDescriptor(method);
                            //VinhNDQ : bo xung kiem tra maxlength
                            long vlngMaxLength = colAnn.maxLength();
                            TypeGenerator typeGenerator = null;
                            if (method.isAnnotationPresent(TypeGenerator.class)) {
                                typeGenerator = (TypeGenerator) method.getAnnotation(TypeGenerator.class);
                                if (Strategy.DATE.equalsIgnoreCase(typeGenerator.strategy()) && pd.getPropertyType().getName().equals(String.class.getName())) {
                                    if (typeGenerator.format() == null) {
                                        throw new Exception("format must be not null for use TypeGenerator");
                                    }
                                } else {
                                    throw new Exception("Strategy type generate [" + method.getReturnType().getClass().getName() + " to " + typeGenerator.strategy() + "] not support yet.");
                                }
                            }

                            readCellIntoObjectProperty(obj, sheet, rowIndex, colName, pd, nullable, vlngMaxLength, colAnn.regex(), typeGenerator);
                            //Kiem tra regular neu can
                        }
                        // System.out.println("Object: " + obj);

                    }

                } catch (InvalidCellValueException e) {
                    if (obj != null) {
                        obj.setError(I18N.get(Constants.INVALID_CELL_VALUE_ERROR) + ". " + I18N.get("common.excel.column.error") + " " + e.getColumnName());
                    }
                    LOGGER.error(e.getMessage(), e);
                } catch (EmptyCellException e) {
                    if (obj != null) {
                        obj.setError(I18N.get(Constants.EMPTY_CELL_ERROR) + ". " + I18N.get("common.excel.column.error") + " " + e.getColumnName());
                    }
                    LOGGER.error(e.getMessage(), e);
                } catch (WrongFileTemplateException e) {
                    if (obj != null) {
                        obj.setError(I18N.get(Constants.WRONG_FILE_TEMPLATE_ERROR));
                    }
                    LOGGER.error(e.getMessage(), e);
                } catch (MaxLengthCellException e) {
                    if (obj != null) {
                        obj.setError(I18N.get(Constants.MAX_LENGTH_CELL_ERROR) + " " + e.getMaxLength() + " " + I18N.get("common.excel.character.error") + ". " + I18N.get("common.excel.column.error") + " " + e.getColumnName());
                    }
                    LOGGER.error(e.getMessage(), e);
                } catch (RegexException e) {
                    if (obj != null) {
                        obj.setError(I18N.get(Constants.REGEX_ERROR) + ". " + I18N.get("common.excel.column.error") + " " + e.getColumnName());
                    }
                    LOGGER.error(e.getMessage(), e);
                } catch (FormulaException e) {
                    if (obj != null) {
                        obj.setError(I18N.get(Constants.FORMULA_ERROR) + ". " + I18N.get("common.excel.column.error") + " " + e.getColumnName());
                    }
                    LOGGER.error(e.getMessage(), e);
                } catch (Exception e) {
                    if (obj != null) {
                        obj.setError(e.getMessage());
                    }
                    LOGGER.error(e.getMessage(), e);
                }
                ret.add(obj);
            }

            rowIndex++;
        }

        return ret;
    }


    private boolean isRowEmpty(XSSFRow row) {
        int vintStartColIndex = CellReference.convertColStringToIndex(mstrStartCol);
        int vintEndColIndex = CellReference.convertColStringToIndex(mstrEndCol);
        DataFormatter dataFormatter = new DataFormatter();
        // System.out.println("Iterating from col " + startColIndex +" to col " + endColIndex);

        int i = vintStartColIndex;
        while (i <= vintEndColIndex) {
            XSSFCell cell = row.getCell(i);
            if (cell != null) {
                String cellValue = dataFormatter.formatCellValue(cell);
                if (cellValue != null && !"".equals(cellValue.trim())) {
                    return false;
                }
            }
            i++;
        }

        return true;
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

    protected boolean checkFileTemplate(XSSFSheet sheet, String pstrSignalConstants) {
        if (sheet != null && pstrSignalConstants != null) {
            XSSFRow row = sheet.getRow(0);
            if (row != null) {
                XSSFCell cellA1 = row.getCell(0);
                if (cellA1 != null) {
                    DataFormatter dataFormatter = new DataFormatter();
                    String inFileValue = dataFormatter.formatCellValue(cellA1);
                    if (inFileValue != null) {
                        if (pstrSignalConstants.equals(inFileValue.trim())) {
                            return true;
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }

                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }


    /**
     * A long method covers all supported types, read original value from excel sheet,
     * convert it into appropriate type and finally call setter method to set
     * value into object's property
     *
     * @param obj
     * @param sheet
     * @param pintRowNum
     * @param pstrColumnName
     * @param pd
     * @param vstrRegex
     * @throws Exception
     */
    private void readCellIntoObjectProperty(Object obj, XSSFSheet sheet, int pintRowNum, String pstrColumnName,
                                            PropertyDescriptor pd, boolean pblnNullable, long plngMaxLength, String vstrRegex, TypeGenerator typeGenerator) throws Exception {
        if (sheet != null && pd != null) {
            String propertyTypeName = pd.getPropertyType().getName();
            XSSFRow row = sheet.getRow(pintRowNum);
            ExcelDataFormatter dataFormatter = new ExcelDataFormatter(typeGenerator, pstrColumnName, pintRowNum);
            Method setterMethod = pd.getWriteMethod();

            if (row != null) {

                int colIndex = CellReference.convertColStringToIndex(pstrColumnName);
                XSSFCell cell = row.getCell(colIndex);
                if (cell.getCellType() == XSSFCell.CELL_TYPE_FORMULA) {
                    FormulaException fe = new FormulaException(String.format("Cell %s%d is not permited ", pstrColumnName, pintRowNum + 1));
                    fe.setColumnName(pstrColumnName);
                    fe.setRow(pintRowNum + 1);
                    throw fe;
                }

                // Primitive types
                String booleanTypeName = boolean.class.getName();
                String byteTypeName = byte.class.getName();
                String charTypeName = char.class.getName();
                String shortTypeName = char.class.getName();
                String intTypeName = int.class.getName();
                String longTypeName = long.class.getName();
                String floatTypeName = float.class.getName();
                String doubleTypeName = double.class.getName();
                String booleanClassTypeName = Boolean.class.getName();
                String longClassTypeName = Long.class.getName();
                String stringClassTypeName = String.class.getName();
                String dateClassTypeName = Date.class.getName();
                String bigIntClassTypeName = BigInteger.class.getName();
                String bigDecimalClassTypeName = BigDecimal.class.getName();
                String intClassTypeName = Integer.class.getName();
                String doubleClassTypeName = Double.class.getName();
                //kiem tra max length
                if (plngMaxLength != -1 && cell != null && dataFormatter.formatCellValue(cell) != null) {
                    String val = dataFormatter.formatCellValue(cell).trim();
                    if (val.getBytes("UTF8").length > plngMaxLength) {
                        MaxLengthCellException maxLengthException = new MaxLengthCellException(String.format("Cell %s%d is over maxlength", pstrColumnName, pintRowNum + 1));
                        maxLengthException.setColumnName(pstrColumnName);
                        maxLengthException.setRow(pintRowNum + 1);
                        maxLengthException.setMaxLength(plngMaxLength);
                        throw maxLengthException;
                    }
                }
                if (cell != null) {
                    if (typeGenerator == null) {
                        if (vstrRegex != null && !"".equals(vstrRegex)) {
                            if (dataFormatter.formatCellValue(cell) != null) {
                                String vstrData = dataFormatter.formatCellValue(cell).trim();
                                if (vstrData != null && !"".equals(vstrData)) {
                                    Pattern vpattern = Pattern.compile(vstrRegex);
                                    Matcher m = vpattern.matcher(vstrData.toString());
                                    if (!m.matches()) {
                                        RegexException regexException = new RegexException(String.format("Cell %s%d is not valid", pstrColumnName, pintRowNum + 1));
                                        regexException.setColumnName(pstrColumnName);
                                        regexException.setRow(pintRowNum + 1);
                                        throw regexException;
                                    }
                                }
                            }

                        }
                    }
                }
                if (propertyTypeName.equals(booleanTypeName)) {
                    if (cell == null || dataFormatter.formatCellValue(cell) == null || "".equals(dataFormatter.formatCellValue(cell).trim())) {
                        if (pblnNullable) {
                            setterMethod.invoke(obj, false);
                        } else {
                            EmptyCellException ex = new EmptyCellException(String.format("Cell %s%d is empty but value is required", pstrColumnName, pintRowNum + 1));
                            ex.setColumnName(pstrColumnName);
                            ex.setRow(pintRowNum);
                            throw ex;
                        }

                    } else {
                        String val = dataFormatter.formatCellValue(cell);
                        boolean boolVal = true;
                        if ("0".equals(val) || "f".equalsIgnoreCase(val) || "false".equalsIgnoreCase(val)) {
                            boolVal = false;
                        }
                        setterMethod.invoke(obj, boolVal);
                    }

                } else if (propertyTypeName.equals(byteTypeName)) {
                    throw new DataTypeNotSupportedException("byte type is not yet implemented");
                } else if (propertyTypeName.equals(charTypeName)) {
                    throw new DataTypeNotSupportedException("char type is not yet implemented");
                } else if (propertyTypeName.equals(shortTypeName)) {
                    throw new DataTypeNotSupportedException("short type is not yet implemented");
                } else if (propertyTypeName.equals(intTypeName)) {
                    if (cell == null || dataFormatter.formatCellValue(cell) == null || "".equals(dataFormatter.formatCellValue(cell).trim())) {
                        if (pblnNullable) {
                            setterMethod.invoke(obj, 0);
                        } else {
                            throwEmptyCellException(pintRowNum, pstrColumnName);
                        }
                    } else {
                        String val = dataFormatter.formatCellValue(cell).trim();
                        try {
                            int intVal = Integer.valueOf(val).intValue();
                            setterMethod.invoke(obj, intVal);
                        } catch (NumberFormatException nex) {
                            throwInvalidCellValueException(nex, pintRowNum, pstrColumnName);
                        }

                    }

                } else if (propertyTypeName.equals(longTypeName)) {
                    if (cell == null || dataFormatter.formatCellValue(cell) == null || "".equals(dataFormatter.formatCellValue(cell).trim())) {
                        if (pblnNullable) {
                            setterMethod.invoke(obj, 0L);
                        } else {
                            throwEmptyCellException(pintRowNum, pstrColumnName);
                        }
                    } else {
                        String val = dataFormatter.formatCellValue(cell).trim();
                        Long longObjVal = Long.valueOf(val);
                        long longVal = longObjVal.longValue();
                        setterMethod.invoke(obj, longVal);
                    }
                } else if (propertyTypeName.equals(floatTypeName)) {
                    if (cell == null || dataFormatter.formatCellValue(cell) == null || "".equals(dataFormatter.formatCellValue(cell).trim())) {
                        if (pblnNullable) {
                            setterMethod.invoke(obj, 0L);
                        } else {
                            throwEmptyCellException(pintRowNum, pstrColumnName);
                        }
                    } else {
                        String val = dataFormatter.formatCellValue(cell).trim();
                        Float fVal = Float.valueOf(val);
                        setterMethod.invoke(obj, fVal);
                    }

                } else if (propertyTypeName.equals(doubleTypeName)) {
                    if (cell == null || dataFormatter.formatCellValue(cell) == null || "".equals(dataFormatter.formatCellValue(cell).trim())) {
                        if (pblnNullable) {
                            setterMethod.invoke(obj, 0L);
                        } else {
                            throwEmptyCellException(pintRowNum, pstrColumnName);
                        }
                    } else {
                        String val = dataFormatter.formatCellValue(cell).trim();
                        Double dVal = Double.valueOf(val);
                        setterMethod.invoke(obj, dVal);
                    }

                }
                // End Primitive types

                else if (booleanClassTypeName.equals(propertyTypeName)) {
                    if (cell == null || dataFormatter.formatCellValue(cell) == null || "".equals(dataFormatter.formatCellValue(cell).trim())) {
                        if (!pblnNullable) {
                            throwEmptyCellException(pintRowNum, pstrColumnName);
                        }
                    } else {
                        try {
                            String val = dataFormatter.formatCellValue(cell).trim();
                            Boolean boolObj = new Boolean(val);
                            setterMethod.invoke(obj, boolObj);
                        } catch (Exception e) {
                            throwInvalidCellValueException(e, pintRowNum, pstrColumnName);
                        }
                    }
                } else if (longClassTypeName.equals(propertyTypeName)) {
                    if (cell == null || dataFormatter.formatCellValue(cell) == null || "".equals(dataFormatter.formatCellValue(cell).trim())) {
                        if (!pblnNullable) {
                            throwEmptyCellException(pintRowNum, pstrColumnName);
                        }
                    } else {
                        try {
                            String val = dataFormatter.formatCellValue(cell).trim();
                            setterMethod.invoke(obj, Long.valueOf(val));
                        } catch (NumberFormatException e) {
                            throwInvalidCellValueException(e, pintRowNum, pstrColumnName);
                        }
                    }

                } else if (stringClassTypeName.equals(propertyTypeName)) {
                    if (cell == null || dataFormatter.formatCellValue(cell) == null || "".equals(dataFormatter.formatCellValue(cell).trim())) {
                        if (!pblnNullable) {
                            throwEmptyCellException(pintRowNum, pstrColumnName);
                        }
                    } else {
                        String val = dataFormatter.formatCellValue(cell).trim();
                        setterMethod.invoke(obj, val);
                    }
                } else if (dateClassTypeName.equals(propertyTypeName)) {
                    if (cell == null || dataFormatter.formatCellValue(cell) == null || "".equals(dataFormatter.formatCellValue(cell).trim())) {
                        if (!pblnNullable) {
                            throwEmptyCellException(pintRowNum, pstrColumnName);
                        }
                    } else {
                        try {
                            Date date = cell.getDateCellValue();
                            setterMethod.invoke(obj, date);
                        } catch (Exception e) {
                            throwInvalidCellValueException(e, pintRowNum, pstrColumnName);
                        }
                    }

                } else if (bigIntClassTypeName.equals(propertyTypeName)) {
                    if (cell == null || dataFormatter.formatCellValue(cell) == null || "".equals(dataFormatter.formatCellValue(cell).trim())) {
                        if (!pblnNullable) {
                            throwEmptyCellException(pintRowNum, pstrColumnName);
                        }
                    } else {
                        try {
                            String val = dataFormatter.formatCellValue(cell).trim();
                            BigInteger bigInteger = new BigInteger(val);
                            setterMethod.invoke(obj, bigInteger);
                        } catch (Exception e) {
                            throwInvalidCellValueException(e, pintRowNum, pstrColumnName);
                        }

                    }

                } else if (bigDecimalClassTypeName.equals(propertyTypeName)) {
                    if (cell == null || dataFormatter.formatCellValue(cell) == null || "".equals(dataFormatter.formatCellValue(cell).trim())) {
                        if (!pblnNullable) {
                            throwEmptyCellException(pintRowNum, pstrColumnName);
                        }
                    } else {
                        try {
                            String val = dataFormatter.formatCellValue(cell).trim();
                            BigDecimal bigDecimal = new BigDecimal(val);
                            setterMethod.invoke(obj, bigDecimal);
                        } catch (Exception e) {
                            throwInvalidCellValueException(e, pintRowNum, pstrColumnName);
                        }
                    }
                } else if (intClassTypeName.equals(propertyTypeName)) {
                    if (cell == null || dataFormatter.formatCellValue(cell) == null || "".equals(dataFormatter.formatCellValue(cell).trim())) {
                        if (!pblnNullable) {
                            throwEmptyCellException(pintRowNum, pstrColumnName);
                        }
                    } else {
                        try {
                            String val = dataFormatter.formatCellValue(cell).trim();
                            Integer intObj = Integer.valueOf(val);
                            setterMethod.invoke(obj, intObj);
                        } catch (Exception e) {
                            throwInvalidCellValueException(e, pintRowNum, pstrColumnName);
                        }
                    }
                } else if (doubleClassTypeName.equals(propertyTypeName)) {
                    if (cell == null || dataFormatter.formatCellValue(cell) == null || "".equals(dataFormatter.formatCellValue(cell).trim())) {
                        if (!pblnNullable) {
                            throwEmptyCellException(pintRowNum, pstrColumnName);
                        }
                    } else {
                        try {
                            String val = dataFormatter.formatCellValue(cell).trim();
                            Double dblObj = Double.valueOf(val);
                            setterMethod.invoke(obj, dblObj);
                        } catch (Exception e) {
                            throwInvalidCellValueException(e, pintRowNum, pstrColumnName);
                        }
                    }
                } else {
                    throw new DataTypeNotSupportedException(String.format("%s is not supported", propertyTypeName));
                }
            } else {
                throw new Exception(String.format("Row %d is null", pintRowNum + 1));
            }
        }
    }


    private void throwEmptyCellException(int pintRowNum, String pstrColumnName) throws EmptyCellException {
        int vintHumanRowNum = pintRowNum + 1;
        EmptyCellException ex = new EmptyCellException(String.format("Cell %s%d is empty but value is required", pstrColumnName, vintHumanRowNum));
        ex.setColumnName(pstrColumnName);
        ex.setRow(vintHumanRowNum);
        throw ex;
    }

    private void throwInvalidCellValueException(Exception originalException, int pintRowNum, String pstrColumnName) throws InvalidCellValueException {
        int vintHumanRowNum = pintRowNum + 1;
        InvalidCellValueException ex =
                new InvalidCellValueException(String.format("Cell %s%d contains invalid value", pstrColumnName, vintHumanRowNum), originalException);
        ex.setRow(vintHumanRowNum);
        ex.setColumnName(pstrColumnName);

        throw ex;
    }

    private PropertyDescriptor getPropertyDescriptor(Method getter) {
        try {
            Class<?> clazz = getter.getDeclaringClass();
            BeanInfo info = Introspector.getBeanInfo(clazz);
            PropertyDescriptor[] props = info.getPropertyDescriptors();
            for (PropertyDescriptor pd : props) {
                if (getter.equals(pd.getReadMethod())) {
                    return pd;
                }
            }
        } catch (IntrospectionException e) {
            LOGGER.error("Could not get properties desciptor", e);
        } catch (Exception e) {
            LOGGER.error("Could not get properties desciptor", e);
        }
        return null;
    }

    public void writeResultFile(String pstrFilePath, String pstrFileResultPath, Class pclsClazz, List<T> plstRet, String pstrSuccessMessage) throws Exception {
        String vstrSignal;
        int vintStartRowIndex;

        VTTExcelWriter vttExcelWriter = new VTTExcelWriter();

        if (pclsClazz.isAnnotationPresent(ExcelEntity.class)) {
            Annotation ann1 = pclsClazz.getAnnotation(ExcelEntity.class);
            ExcelEntity entityAnn = (ExcelEntity) ann1;

            vstrSignal = entityAnn.signalConstant();
            vintStartRowIndex = ((ExcelEntity) ann1).dataStartRowIndex();

        } else {
            throw new Exception("You must annotate class with @ExcelEntity");
        }

        XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(pstrFilePath));
        XSSFSheet sheet = workbook.getSheetAt(0);

        Map<String, CellStyle> styles = vttExcelWriter.createStyles(workbook);

        if (vstrSignal != null && !checkFileTemplate(sheet, vstrSignal)) {
            throw new WrongFileTemplateException(I18N.get(Constants.WRONG_FILE_TEMPLATE_ERROR));
        }

        Iterator<Row> rows = sheet.iterator();

        XSSFRow headerRow = sheet.getRow(vintStartRowIndex - 2);
        int lastHeaderCellIndex = headerRow.getLastCellNum();
        XSSFCell headerCell = headerRow.createCell(lastHeaderCellIndex);
        headerCell.setCellValue(I18N.get("common.header.note"));
        headerCell.setCellStyle(styles.get("header"));

        int rowIndex = 0;
        while (rows.hasNext()) {

            if (rowIndex < vintStartRowIndex - 1) {
                rowIndex++;
                continue;
            }
            XSSFRow row = sheet.getRow(rowIndex);
            if (row == null) {
                break;
            }
            T obj = plstRet.get(rowIndex - (vintStartRowIndex - 1));
            if (!DataUtil.isNullObject(obj)) {
//            XSSFRow row = sheet.getRow(rowIndex);
                int lastCellIndex = row.getLastCellNum();
                XSSFCell cell = row.createCell(lastCellIndex);
                cell.setCellValue(obj.getError() == null ? pstrSuccessMessage : obj.getError());
                cell.setCellStyle(styles.get("content"));
                sheet.autoSizeColumn(lastCellIndex);
            }
            rowIndex++;
        }
        FileOutputStream out = new FileOutputStream(pstrFileResultPath);
        workbook.write(out);
        out.close();
    }

    public void writeResultWarning(String pstrFilePath, String pstrFileResultPath, Class pclsClazz, List<T> plstRet, String pstrSuccessMessage, int type) throws Exception {
        String vstrSignal;
        int vintStartRowIndex;

        VTTExcelWriter vttExcelWriter = new VTTExcelWriter();

        if (pclsClazz.isAnnotationPresent(ExcelEntity.class)) {
            Annotation ann1 = pclsClazz.getAnnotation(ExcelEntity.class);
            ExcelEntity entityAnn = (ExcelEntity) ann1;

            vstrSignal = entityAnn.signalConstant();
            vintStartRowIndex = ((ExcelEntity) ann1).dataStartRowIndex();

        } else {
            throw new Exception("You must annotate class with @ExcelEntity");
        }

        XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(pstrFilePath));
        XSSFSheet sheet = workbook.getSheetAt(0);

        Map<String, CellStyle> styles = vttExcelWriter.createStyles(workbook);

        if (vstrSignal != null && !checkFileTemplate(sheet, vstrSignal)) {
            throw new WrongFileTemplateException(I18N.get(Constants.WRONG_FILE_TEMPLATE_ERROR));
        }

        Iterator<Row> rows = sheet.iterator();
        XSSFRow headerRow = sheet.getRow(vintStartRowIndex - 2);
        XSSFCell headerCell;
        if (type == 1) {
            headerCell = headerRow.createCell(6);
        } else if (type == 0) {
            headerCell = headerRow.createCell(4);
        } else {
            headerCell = headerRow.createCell(13);
            sheet.setColumnWidth(14, 5000);
        }
        headerCell.setCellValue(I18N.get("common.header.note"));
        headerCell.setCellStyle(styles.get("header"));
        int rowIndex = 0;
        while (rows.hasNext()) {

            if (rowIndex < vintStartRowIndex - 1) {
                rowIndex++;
                continue;
            }
            XSSFRow row = sheet.getRow(rowIndex);
            if (row == null) {
                break;
            }
            T obj = plstRet.get(rowIndex - (vintStartRowIndex - 1));
            if (!DataUtil.isNullObject(obj)) {
//            XSSFRow row = sheet.getRow(rowIndex);
                int lastCellIndex = row.getLastCellNum();
                XSSFCell cell;
                if (type == 1) {
                    cell = row.createCell(6);
                } else if (type == 0) {
                    cell = row.createCell(4);
                } else {
                    cell = row.createCell(14);
                }
                cell.setCellValue(obj.getError() == null ? pstrSuccessMessage : obj.getError());
                cell.setCellStyle(styles.get("content"));
                sheet.autoSizeColumn(lastCellIndex);
            }
            rowIndex++;
        }
        FileOutputStream out = new FileOutputStream(pstrFileResultPath);
        workbook.write(out);
        out.close();
    }
}
