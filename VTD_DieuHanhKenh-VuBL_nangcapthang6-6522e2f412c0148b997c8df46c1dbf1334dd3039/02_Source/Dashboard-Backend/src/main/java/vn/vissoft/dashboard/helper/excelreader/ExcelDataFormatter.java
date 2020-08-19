package vn.vissoft.dashboard.helper.excelreader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import vn.vissoft.dashboard.controller.ChannelController;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.helper.excelreader.annotation.Strategy;
import vn.vissoft.dashboard.helper.excelreader.annotation.TypeGenerator;
import vn.vissoft.dashboard.helper.excelreader.exception.RegexException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExcelDataFormatter {
    private static final Logger LOGGER = LogManager.getLogger(ExcelDataFormatter.class);
    private TypeGenerator typeGenerator;
    private DataFormatter dataFormatter = new DataFormatter();
    private String columnName;
    private int rowNum;
    public ExcelDataFormatter(TypeGenerator generator, String pstrColumnName, int pintRowNum)
    {
        typeGenerator = generator;
        this.columnName = pstrColumnName;
        this.rowNum = pintRowNum;
    }

    public String formatCellValue(Cell cell) throws Exception{
        if(typeGenerator!=null)
        {
            String strData = dataFormatter.formatCellValue(cell);
            if(DataUtil.isNullOrEmpty(strData))
                return "";
            //neu co chuyen dinh dang
            if(Strategy.DATE.equalsIgnoreCase(typeGenerator.strategy())&&typeGenerator.format()!=null){
                try {

                    if (DateUtil.isCellDateFormatted(cell)) {
                        Date date = cell.getDateCellValue();
                        if (date != null) {
                            return new SimpleDateFormat(typeGenerator.format()).format(date);
                        } else {
                            return null;
                        }

                    } else {
                        new SimpleDateFormat(typeGenerator.format()).parse(strData);
                        return strData;
                    }
                }catch(Exception e)
                {
                    LOGGER.error(e.getMessage(), e);
                    RegexException regexException = new RegexException(String.format("Cell %s%d is not valid", this.columnName, this.rowNum + 1));
                    regexException.setColumnName(this.columnName);
                    regexException.setRow(this.rowNum + 1);
                    throw regexException;

                }
            }
            return dataFormatter.formatCellValue(cell);
        }
        else
        {
            return dataFormatter.formatCellValue(cell);
        }
    }
}
