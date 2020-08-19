package vn.vissoft.dashboard.services.impl;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.collections.map.MultiKeyMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.vissoft.dashboard.config.I18N;
import vn.vissoft.dashboard.dto.DashboardRequestDTO;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.chart.*;
import vn.vissoft.dashboard.helper.Constant;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.model.*;
import vn.vissoft.dashboard.repo.*;
import vn.vissoft.dashboard.repo.impl.ChartRepoImpl;
import vn.vissoft.dashboard.services.ChartService;
import vn.vissoft.dashboard.services.TableService;

import javax.transaction.Transactional;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
public class TableServiceImpl implements TableService {

    private static final Logger LOGGER = LogManager.getLogger(TableServiceImpl.class);

    @Autowired
    private TableRepoCustom tableRepoCustom;

    @Autowired
    private ChartService chartService;

    @Autowired
    private PartnerRepo partnerRepo;

    @Autowired
    private ChartRepoImpl chartRepo;

    @Autowired
    private ServiceWarningConfigRepo serviceWarningConfigRepo;

    @Autowired
    private ApParamRepo apParamRepo;

    @Autowired
    private ServiceRepo serviceRepo;

    @Autowired
    private StaffRepo staffRepo;

    private DecimalFormat df = new DecimalFormat("###.##");
    private NumberFormat format = NumberFormat.getInstance();


    /**
     * set gia tri cho cac cell tren row theo ngay trong bang
     *
     * @param plstColumns
     * @param pObjs
     * @param dashboardRequestDTO
     * @return danh sach cac cell tren row theo ngay
     * @throws Exception
     * @author DatNT
     * @version 1.0
     * @since 15/10/2019
     */
    private List<TableCellBaseDTO> setCellValueDaily(String pstrTrend, List<TableColumnDTO> plstColumns, Object[] pObjs, DashboardRequestDTO dashboardRequestDTO, int pintChartId, MultiKeyMap multiKeyMap,List<String> idReverse) throws Exception {

        int vintIndex = 0;

        List<TableCellBaseDTO> vlstCellBaseDTOS = new ArrayList<>();
        String vstrServiceName = DataUtil.safeToString(pObjs[0]);
        Double vdblCompleteUnitQuantity = (Double) pObjs[1];
        Double vdblChildUnitQuantity = (Double) pObjs[2];
        Double vdblDailyAccumPerform = (Double) pObjs[3];
        Double vdblDailyAccumSchedule = (Double) pObjs[4];
        Double vdblDailyAccumComplete = (Double) pObjs[5];
        Double vdblMonthSchedule = (Double) pObjs[6];
        Double vdblMonthComplete = (Double) pObjs[7];
        Double vdblLastmonthFvalue = (Double) pObjs[8];
        Long vlngServiceId = DataUtil.safeToLong(pObjs[9]);
        String vstrCodeDVT = DataUtil.safeToString(pObjs[10]);
        String vstrChannelCode = (String) pObjs[11];
        String vstrShopCode = (String) pObjs[12];
        String vstrDVT = DataUtil.safeToString(pObjs[16]);
        Integer vdblOldRate = DataUtil.safeToInt(pObjs[18]);
        String vstrNewDVT = DataUtil.safeToString(pObjs[19]);
        Integer vdblNewRate = DataUtil.safeToInt(pObjs[20]);
        Double vdblPerform = (Double) pObjs[22];

        //ten dvt
        String vstrDvtUse = null;
        if (!DataUtil.isNullOrEmpty(vstrNewDVT)) {
            vstrDvtUse = vstrNewDVT;
        } else {
            if (!DataUtil.isNullOrEmpty(vstrDVT)) {
                vstrDvtUse = vstrDVT;
            }
        }

        for (int i = 0; i < plstColumns.size(); i++) {
            if (Constants.TEXT.equals(plstColumns.get(i).getType())) {
                TableCellBaseDTO cellBaseDTO = new TableCellBaseDTO();
                cellBaseDTO.setColumnId(vintIndex++);
                if (Constants.SERVICE_NAME.equals(plstColumns.get(i).getValue())) {
                    if (DataUtil.isNullOrEmpty(vstrDvtUse))
                        cellBaseDTO.setViewValue(vstrServiceName);
                    else
                        cellBaseDTO.setViewValue(vstrServiceName + " (" + vstrDvtUse + ")");
                    cellBaseDTO.setColor(Constants.COLORS.BLACK);
                }
                if (Constants.ACHIEVED_UNIT_QUANTITY.equals(plstColumns.get(i).getValue())) {
                    if (vdblCompleteUnitQuantity != null && vdblChildUnitQuantity != null) {
                        cellBaseDTO.setViewValue(df.format(vdblCompleteUnitQuantity) + "/" + df.format(vdblChildUnitQuantity));
                    } else if (vdblCompleteUnitQuantity == null && vdblChildUnitQuantity != null) {
                        cellBaseDTO.setViewValue("-/" + df.format(vdblChildUnitQuantity));
                    }
                    cellBaseDTO.setColor(Constants.COLORS.BLACK);
                }
                if (Constants.PERFORM.equals(plstColumns.get(i).getValue())) {
                    if (vdblPerform != null && !DataUtil.isNullOrZero(vdblNewRate) && !DataUtil.isNullOrZero(vdblOldRate)) {
                        vdblPerform = vdblPerform * vdblOldRate / vdblNewRate;
                        Double vdblValue = Double.valueOf(df.format(vdblPerform));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    } else if (vdblPerform != null) {
                        Double vdblValue = Double.valueOf(df.format(vdblPerform));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    }
                    cellBaseDTO.setColor(Constants.COLORS.BLACK);
                }
                if (Constants.ACCUMULATE_PERFORM.equals(plstColumns.get(i).getValue())) {
                    if (vdblDailyAccumPerform != null && !DataUtil.isNullOrZero(vdblNewRate) && !DataUtil.isNullOrZero(vdblOldRate)) {
                        vdblDailyAccumPerform = vdblDailyAccumPerform * vdblOldRate / vdblNewRate;
                        Double vdblValue = Double.valueOf(df.format(vdblDailyAccumPerform));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    } else if (vdblDailyAccumPerform != null) {
                        Double vdblValue = Double.valueOf(df.format(vdblDailyAccumPerform));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    }
                    cellBaseDTO.setColor(Constants.COLORS.BLACK);
                }
                if (Constants.DAILY_ACCUMULATE_SCHEDULE.equals(plstColumns.get(i).getValue())) {
                    if (vdblDailyAccumSchedule != null && !DataUtil.isNullOrZero(vdblNewRate) && !DataUtil.isNullOrZero(vdblOldRate)) {
                        vdblDailyAccumSchedule = vdblDailyAccumSchedule * vdblOldRate / vdblNewRate;
                        Double vdblValue = Double.valueOf(df.format(vdblDailyAccumSchedule));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    } else if (vdblDailyAccumSchedule != null) {
                        Double vdblValue = Double.valueOf(df.format(vdblDailyAccumSchedule));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    }
                    cellBaseDTO.setColor(Constants.COLORS.BLACK);
                }
                if (Constants.DAILY_ACCUMULATE_COMPLETE.equals(plstColumns.get(i).getValue())) {
                    if (vdblDailyAccumComplete != null) {
                        String vstrWarningColor = "";
                        Double vdblValue = Double.valueOf(df.format(vdblDailyAccumComplete));
                        cellBaseDTO.setViewValue(format.format(vdblValue) + "%");
                        String vstrWarningLevel = setWarninglevel(vdblDailyAccumComplete, vstrChannelCode, vlngServiceId);
                        if (!DataUtil.isNullOrEmpty(vstrWarningLevel))
                            vstrWarningColor = (String) multiKeyMap.get(Constants.WARNING_LEVEL, vstrWarningLevel);
                        cellBaseDTO.setColor(vstrWarningColor);
                    }
                    else{
                        cellBaseDTO.setViewValue("");
                    }
                }
                if (Constants.MONTH_SCHEDULE.equals(plstColumns.get(i).getValue())) {
                    if (vdblMonthSchedule != null && !DataUtil.isNullOrZero(vdblNewRate) && !DataUtil.isNullOrZero(vdblOldRate)) {
                        vdblMonthSchedule = vdblMonthSchedule * vdblOldRate / vdblNewRate;
                        Double vdblValue = Double.valueOf(df.format(vdblMonthSchedule));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    } else if (vdblMonthSchedule != null) {
                        Double vdblValue = Double.valueOf(df.format(vdblMonthSchedule));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    }
                    cellBaseDTO.setColor(Constants.COLORS.BLACK);
                }
                if (Constants.MONTH_COMPLETE.equals(plstColumns.get(i).getValue())) {
                    if (vdblMonthComplete != null) {
                        String vstrColor = "";
                        Double vdblValue = Double.valueOf(df.format(vdblMonthComplete));
                        cellBaseDTO.setViewValue(format.format(vdblValue) + "%");
                        if (vdblMonthComplete >= 0 && vdblMonthComplete < 100) {
                            vstrColor = (String) multiKeyMap.get(Constants.COMPLETED_COLOUR, Constants.PARAM_STATUS_0);
                        } else if (vdblMonthComplete >= 100) {
                            vstrColor = (String) multiKeyMap.get(Constants.COMPLETED_COLOUR, Constants.PARAM_STATUS);
                        }
                        cellBaseDTO.setColor(vstrColor);
                    }
                    else{
                        cellBaseDTO.setViewValue("");
                    }
                }
                if (Constants.LAST_ACCUMULATE_PERFORM.equals(plstColumns.get(i).getValue())) {
                    if (vdblLastmonthFvalue != null && !DataUtil.isNullOrZero(vdblNewRate) && !DataUtil.isNullOrZero(vdblOldRate)) {
                        vdblLastmonthFvalue = vdblLastmonthFvalue * vdblOldRate / vdblNewRate;
                        Double vdblValue = Double.valueOf(df.format(vdblLastmonthFvalue));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    } else if (vdblLastmonthFvalue != null) {
                        Double vdblValue = Double.valueOf(df.format(vdblLastmonthFvalue));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    }
                    cellBaseDTO.setColor(Constants.COLORS.BLACK);
                }

                vlstCellBaseDTOS.add(cellBaseDTO);
            }
            if (Constants.CHART.equals(plstColumns.get(i).getType())) {
                JsonArray lines = plstColumns.get(i).getMetaData();
                TableCellChartDTO cellChartDTO = setChartTable(pstrTrend, lines, vintIndex++, pintChartId, dashboardRequestDTO);
                vlstCellBaseDTOS.add(cellChartDTO);

            }

            if (Constants.GROWTH_MONTH.equals(plstColumns.get(i).getType())) {
                TableCellGrowthDTO cellGrowthDTO = setGrowth(vlngServiceId, vdblDailyAccumPerform, vdblLastmonthFvalue, vintIndex++, vdblOldRate, vdblNewRate,idReverse);
                vlstCellBaseDTOS.add(cellGrowthDTO);
            }
            if (Constants.BAR.equals(plstColumns.get(i).getType())) {
                TableCellBarDTO cellBarDTO = setBar(vdblDailyAccumPerform, vdblMonthSchedule, vdblDailyAccumSchedule, vdblMonthComplete, vintIndex++, vdblNewRate, vdblOldRate, vstrDvtUse);
                vlstCellBaseDTOS.add(cellBarDTO);
            }
        }

        return vlstCellBaseDTOS;
    }

    /**
     * set gia tri cho cac cell tren row theo ngay trong bang tong hop chi tiet chi tieu
     *
     * @param plstColumns
     * @param pObjs
     * @param dashboardRequestDTO
     * @return danh sach cac cell tren row theo ngay
     * @throws Exception
     * @author DatNT
     * @version 1.0
     * @since 04/11/2019
     */
    private List<TableCellBaseDTO> setCellValueDetailDaily(String pstrTrend, List<TableColumnDTO> plstColumns, Object[] pObjs, DashboardRequestDTO dashboardRequestDTO, int pintChartId, MultiKeyMap multiKeyMap,List<String> idReverse) throws Exception {

        int vintIndex = 0;
        List<TableCellBaseDTO> vlstCellBaseDTOS = new ArrayList<>();
        String vstrShopName = DataUtil.safeToString(pObjs[0]);
        Double vdblDailyAccumSchedule = (Double) pObjs[3];
        Double vdblDailyAccumPerform = (Double) pObjs[4];
        Double vdblDailyAccumComplete = (Double) pObjs[5];
        Double vdblMonthSchedule = (Double) pObjs[6];
        Double vdblMonthComplete = (Double) pObjs[7];
        Double vdblLastmonthFvalue = (Double) pObjs[8];
        Long vlngServiceId = DataUtil.safeToLong(pObjs[9]);
        String vstrShopCode = (String) (pObjs[12]);
        String vstrVdsChannelCode = (String) (pObjs[11]);
        String vstrCodeDVT = DataUtil.safeToString(pObjs[10]);
        String vstrStaffCode = pObjs[13] == null ? null : (String) pObjs[13];
        String vstrDVT = DataUtil.safeToString(pObjs[16]);
        Integer vdblOldRate = DataUtil.safeToInt(pObjs[18]);
        String vstrNewDVT = DataUtil.safeToString(pObjs[19]);
        Integer vdblNewRate = DataUtil.safeToInt(pObjs[20]);
        Double vdblPerform = (Double) pObjs[21];
        String vstrShopShortName = pObjs[22] == null ? null : (String) pObjs[22];

        //ten dvt
        String vstrDvtUse = null;
        if (!DataUtil.isNullOrEmpty(vstrNewDVT)) {
            vstrDvtUse = vstrNewDVT;
        } else {
            if (!DataUtil.isNullOrEmpty(vstrDVT)) {
                vstrDvtUse = vstrDVT;
            }
        }
        for (int i = 0; i < plstColumns.size(); i++) {
            if (Constants.TEXT.equals(plstColumns.get(i).getType())) {
                TableCellBaseDTO cellBaseDTO = new TableCellBaseDTO();
                cellBaseDTO.setColumnId(vintIndex++);
                if (Constants.SHOP_NAME.equals(plstColumns.get(i).getValue())) {
                    if (DataUtil.isNullOrEmpty(vstrDvtUse)) {
                        cellBaseDTO.setViewValue(vstrShopName);

                    } else cellBaseDTO.setViewValue(vstrShopName + " (" + vstrDvtUse + ")");
                    cellBaseDTO.setColor(Constants.COLORS.BLACK);

                }
                //them cot 25/05/20
                if (Constants.SHOP_UNIT.SHOP_CODE.equals(plstColumns.get(i).getValue())) {
                    if (vstrStaffCode != null) {
                            cellBaseDTO.setViewValue(vstrStaffCode);
                    } else {
                        cellBaseDTO.setViewValue(vstrShopCode);
                    }
                }
                if (Constants.SHOP_SHORT_NAME.equals(plstColumns.get(i).getValue())) {
                    cellBaseDTO.setViewValue(vstrShopShortName);
                }
                if (Constants.DAILY_ACCUMULATE_SCHEDULE.equals(plstColumns.get(i).getValue())) {
                    if (vdblDailyAccumSchedule != null && !DataUtil.isNullOrZero(vdblNewRate) && !DataUtil.isNullOrZero(vdblOldRate)) {
                        vdblDailyAccumSchedule = vdblDailyAccumSchedule * vdblOldRate / vdblNewRate;
                        Double vdblValue = Double.valueOf(df.format(vdblDailyAccumSchedule));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    } else if (vdblDailyAccumSchedule != null) {
                        Double vdblValue = Double.valueOf(df.format(vdblDailyAccumSchedule));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    }
                    cellBaseDTO.setColor(Constants.COLORS.BLACK);
                }
                if (Constants.PERFORM.equals(plstColumns.get(i).getValue())) {
                    if (vdblPerform != null && !DataUtil.isNullOrZero(vdblNewRate) && !DataUtil.isNullOrZero(vdblOldRate)) {
                        vdblPerform = vdblPerform * vdblOldRate / vdblNewRate;
                        Double vdblValue = Double.valueOf(df.format(vdblPerform));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    } else if (vdblPerform != null) {
                        Double vdblValue = Double.valueOf(df.format(vdblPerform));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    }
                    cellBaseDTO.setColor(Constants.COLORS.BLACK);
                }
                if (Constants.ACCUMULATE_PERFORM.equals(plstColumns.get(i).getValue())) {
                    if (vdblDailyAccumPerform != null && !DataUtil.isNullOrZero(vdblNewRate) && !DataUtil.isNullOrZero(vdblOldRate)) {
                        vdblDailyAccumPerform = vdblDailyAccumPerform * vdblOldRate / vdblNewRate;
                        Double vdblValue = Double.valueOf(df.format(vdblDailyAccumPerform));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    } else if (vdblDailyAccumPerform != null) {
                        Double vdblValue = Double.valueOf(df.format(vdblDailyAccumPerform));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    }
                    cellBaseDTO.setColor(Constants.COLORS.BLACK);
                }

                if (Constants.DAILY_ACCUMULATE_COMPLETE.equals(plstColumns.get(i).getValue())) {
                    if (vdblDailyAccumComplete != null) {
                        Double vdblValue = Double.valueOf(df.format(vdblDailyAccumComplete));
                        cellBaseDTO.setViewValue(format.format(vdblValue) + "%");
                    }
                    else{
                        cellBaseDTO.setViewValue("");
                    }
                    cellBaseDTO.setColor(Constants.COLORS.BLACK);
                }
                if (Constants.MONTH_SCHEDULE.equals(plstColumns.get(i).getValue())) {
                    if (vdblMonthSchedule != null && !DataUtil.isNullOrZero(vdblNewRate) && !DataUtil.isNullOrZero(vdblOldRate)) {
                        vdblMonthSchedule = vdblMonthSchedule * vdblOldRate / vdblNewRate;
                        Double vdblValue = Double.valueOf(df.format(vdblMonthSchedule));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    } else if (vdblMonthSchedule != null) {
                        Double vdblValue = Double.valueOf(df.format(vdblMonthSchedule));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    }
                    cellBaseDTO.setColor(Constants.COLORS.BLACK);
                }
                if (Constants.MONTH_COMPLETE.equals(plstColumns.get(i).getValue())) {
                    if (vdblMonthComplete != null) {
                        String vstrColor = "";
                        Double vdblValue = Double.valueOf(df.format(vdblMonthComplete));
                        cellBaseDTO.setViewValue(format.format(vdblValue) + "%");
                        if (vdblMonthComplete >= 0 && vdblMonthComplete < 100)
                            vstrColor = (String) multiKeyMap.get(Constants.COMPLETED_COLOUR, Constants.PARAM_STATUS_0);
                        if (vdblMonthComplete >= 100)
                            vstrColor = (String) multiKeyMap.get(Constants.COMPLETED_COLOUR, Constants.PARAM_STATUS);

                        cellBaseDTO.setColor(vstrColor);
                    }
                    else{
                        cellBaseDTO.setViewValue("");
                    }
                }
                if (Constants.LAST_ACCUMULATE_PERFORM.equals(plstColumns.get(i).getValue())) {
                    if (vdblLastmonthFvalue != null && !DataUtil.isNullOrZero(vdblNewRate) && !DataUtil.isNullOrZero(vdblOldRate)) {
                        vdblLastmonthFvalue = vdblLastmonthFvalue * vdblOldRate / vdblNewRate;
                        Double vdblValue = Double.valueOf(df.format(vdblLastmonthFvalue));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    } else if (vdblLastmonthFvalue != null) {
                        Double vdblValue = Double.valueOf(df.format(vdblLastmonthFvalue));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    }
                    cellBaseDTO.setColor(Constants.COLORS.BLACK);
                }

                vlstCellBaseDTOS.add(cellBaseDTO);
            }
            if (Constants.CHART.equals(plstColumns.get(i).getType())) {
                JsonArray lines = plstColumns.get(i).getMetaData();
                TableCellChartDTO cellChartDTO = setChartTable(pstrTrend, lines, vintIndex++, pintChartId, dashboardRequestDTO);
                vlstCellBaseDTOS.add(cellChartDTO);

            }

            if (Constants.GROWTH_MONTH.equals(plstColumns.get(i).getType())) {
                TableCellGrowthDTO cellGrowthDTO = setGrowth(vlngServiceId, vdblDailyAccumPerform, vdblLastmonthFvalue, vintIndex++, vdblOldRate, vdblNewRate,idReverse);
                vlstCellBaseDTOS.add(cellGrowthDTO);
            }
        }

        return vlstCellBaseDTOS;
    }

    /**
     * set gia tri cho cac cell tren row theo thang trong bang tong hop chi tiet chi tieu
     *
     * @param plstColumns
     * @param pObjs
     * @param dashboardRequestDTO
     * @return danh sach cac cell tren row theo thang
     * @throws Exception
     * @author DatNT
     * @version 1.0
     * @since 04/11/2019
     */
    private List<TableCellBaseDTO> setCellValueDetailMonthly(String pstrTrend, List<TableColumnDTO> plstColumns, Object[] pObjs, DashboardRequestDTO dashboardRequestDTO, int pintChartId, MultiKeyMap multiKeyMap,List<String> idReverse) throws Exception {

        int vintIndex = 0;
        List<TableCellBaseDTO> vlstCellBaseDTOS = new ArrayList<>();
        String vstrShopName = DataUtil.safeToString(pObjs[0]);
        Double vdblMonthlyAccumSchedule = (Double) pObjs[3];
        Double vdblMonthlyAccumPerform = (Double) pObjs[4];
        Double vdblMonthlyAccumComplete = (Double) pObjs[5];
        Double vdblYearSchedule = (Double) pObjs[6];
        Double vdblYearComplete = (Double) pObjs[7];
        Double vdblLastYearFvalue = (Double) pObjs[8];
        Long vlngServiceId = DataUtil.safeToLong(pObjs[9]);
        String vstrShopCode = (String) (pObjs[12]);
        String vstrVdsChannelCode = (String) (pObjs[11]);
        String vstrCodeDVT = DataUtil.safeToString(pObjs[10]);
        String vstrStaffCode = pObjs[13] == null ? null : (String) pObjs[13];
        String vstrDVT = DataUtil.safeToString(pObjs[16]);
        Integer vdblOldRate = DataUtil.safeToInt(pObjs[18]);
        String vstrNewDVT = DataUtil.safeToString(pObjs[19]);
        Integer vdblNewRate = DataUtil.safeToInt(pObjs[20]);
        Double vdblPerform = (Double) pObjs[21];
        String vstrShopShortName = pObjs[22] == null ? null : (String) pObjs[22];

        //ten dvt
        String vstrDvtUse = null;
        if (!DataUtil.isNullOrEmpty(vstrNewDVT)) {
            vstrDvtUse = vstrNewDVT;
        } else {
            if (!DataUtil.isNullOrEmpty(vstrDVT)) {
                vstrDvtUse = vstrDVT;
            }
        }
        for (int i = 0; i < plstColumns.size(); i++) {
            if (Constants.TEXT.equals(plstColumns.get(i).getType())) {
                TableCellBaseDTO cellBaseDTO = new TableCellBaseDTO();
                cellBaseDTO.setColumnId(vintIndex++);
                if (Constants.SHOP_NAME.equals(plstColumns.get(i).getValue())) {
                    if (DataUtil.isNullOrEmpty(vstrDvtUse)) {
                        if (DataUtil.isNullOrEmpty(vstrCodeDVT))
                            cellBaseDTO.setViewValue(vstrShopName);
                    } else cellBaseDTO.setViewValue(vstrShopName + " (" + vstrDvtUse + ")");
                    cellBaseDTO.setColor(Constants.COLORS.BLACK);

                }
                //them cot 25/05/20
                if (Constants.SHOP_UNIT.SHOP_CODE.equals(plstColumns.get(i).getValue())) {
                    if (vstrStaffCode != null) {
                        cellBaseDTO.setViewValue(vstrStaffCode);
                    } else {
                        cellBaseDTO.setViewValue(vstrShopCode);
                    }
                }
                if (Constants.SHOP_SHORT_NAME.equals(plstColumns.get(i).getValue())) {
                    cellBaseDTO.setViewValue(vstrShopShortName);
                }
                if (Constants.MONTHLY_ACCUMULATE_SCHEDULE.equals(plstColumns.get(i).getValue())) {
                    if (vdblMonthlyAccumSchedule != null && !DataUtil.isNullOrZero(vdblNewRate) && !DataUtil.isNullOrZero(vdblOldRate)) {
                        vdblMonthlyAccumSchedule = vdblMonthlyAccumSchedule * vdblOldRate / vdblNewRate;
                        Double vdblValue = Double.valueOf(df.format(vdblMonthlyAccumSchedule));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    } else if (vdblMonthlyAccumSchedule != null) {
                        Double vdblValue = Double.valueOf(df.format(vdblMonthlyAccumSchedule));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    }
                    cellBaseDTO.setColor(Constants.COLORS.BLACK);
                }
                if (Constants.PERFORM.equals(plstColumns.get(i).getValue())) {
                    if (vdblPerform != null && !DataUtil.isNullOrZero(vdblNewRate) && !DataUtil.isNullOrZero(vdblOldRate)) {
                        vdblPerform = vdblPerform * vdblOldRate / vdblNewRate;
                        Double vdblValue = Double.valueOf(df.format(vdblPerform));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    } else if (vdblPerform != null) {
                        Double vdblValue = Double.valueOf(df.format(vdblPerform));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    }
                    cellBaseDTO.setColor(Constants.COLORS.BLACK);
                }
                if (Constants.ACCUMULATE_PERFORM.equals(plstColumns.get(i).getValue())) {
                    if (vdblMonthlyAccumPerform != null && !DataUtil.isNullOrZero(vdblNewRate) && !DataUtil.isNullOrZero(vdblOldRate)) {
                        vdblMonthlyAccumPerform = vdblMonthlyAccumPerform * vdblOldRate / vdblNewRate;
                        Double vdblValue = Double.valueOf(df.format(vdblMonthlyAccumPerform));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    } else if (vdblMonthlyAccumPerform != null) {
                        Double vdblValue = Double.valueOf(df.format(vdblMonthlyAccumPerform));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    }
                    cellBaseDTO.setColor(Constants.COLORS.BLACK);
                }

                if (Constants.MONTHLY_ACCUMULATE_COMPLETE.equals(plstColumns.get(i).getValue())) {
                    if (vdblMonthlyAccumComplete != null) {
                        Double vdblValue = Double.valueOf(df.format(vdblMonthlyAccumComplete));
                        cellBaseDTO.setViewValue(format.format(vdblValue) + "%");
                    }
                    else{
                        cellBaseDTO.setViewValue("");
                    }
                    cellBaseDTO.setColor(Constants.COLORS.BLACK);
                }
                if (Constants.YEAR_SCHEDULE.equals(plstColumns.get(i).getValue())) {
                    if (vdblYearSchedule != null && !DataUtil.isNullOrZero(vdblNewRate) && !DataUtil.isNullOrZero(vdblOldRate)) {
                        vdblYearSchedule = vdblYearSchedule * vdblOldRate / vdblNewRate;
                        Double vdblValue = Double.valueOf(df.format(vdblYearSchedule));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    } else if (vdblYearSchedule != null) {
                        Double vdblValue = Double.valueOf(df.format(vdblYearSchedule));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    }
                    cellBaseDTO.setColor(Constants.COLORS.BLACK);
                }
                if (Constants.YEAR_COMPLETE.equals(plstColumns.get(i).getValue())) {
                    if (vdblYearComplete != null) {
                        String vstrColor = "";
                        Double vdblValue = Double.valueOf(df.format(vdblYearComplete));
                        cellBaseDTO.setViewValue(format.format(vdblValue) + "%");
                        if (vdblYearComplete >= 0 && vdblYearComplete < 100)
                            vstrColor = (String) multiKeyMap.get(Constants.COMPLETED_COLOUR, Constants.PARAM_STATUS_0);
                        if (vdblYearComplete >= 100)
                            vstrColor = (String) multiKeyMap.get(Constants.COMPLETED_COLOUR, Constants.PARAM_STATUS);

                        cellBaseDTO.setColor(vstrColor);
                    }
                    else{
                        cellBaseDTO.setViewValue("");
                    }
                }
                if (Constants.LAST_ACCUMULATE_PERFORM.equals(plstColumns.get(i).getValue())) {
                    if (vdblLastYearFvalue != null && !DataUtil.isNullOrZero(vdblNewRate) && !DataUtil.isNullOrZero(vdblOldRate)) {
                        vdblLastYearFvalue = vdblLastYearFvalue * vdblOldRate / vdblNewRate;
                        Double vdblValue = Double.valueOf(df.format(vdblLastYearFvalue));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    } else if (vdblLastYearFvalue != null) {
                        Double vdblValue = Double.valueOf(df.format(vdblLastYearFvalue));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    }
                    cellBaseDTO.setColor(Constants.COLORS.BLACK);
                }
                vlstCellBaseDTOS.add(cellBaseDTO);
            }
            if (Constants.CHART.equals(plstColumns.get(i).getType())) {
                JsonArray lines = plstColumns.get(i).getMetaData();
                TableCellChartDTO cellChartDTO = setChartTable(pstrTrend, lines, vintIndex++, pintChartId, dashboardRequestDTO);
                vlstCellBaseDTOS.add(cellChartDTO);
            }

            if (Constants.GROWTH_YEAR.equals(plstColumns.get(i).getType())) {
                TableCellGrowthDTO cellGrowthDTO = setGrowth(vlngServiceId, vdblMonthlyAccumPerform, vdblLastYearFvalue, vintIndex++, vdblOldRate, vdblNewRate,idReverse);
                vlstCellBaseDTOS.add(cellGrowthDTO);
            }
        }

        return vlstCellBaseDTOS;
    }

    /**
     * set gia tri cho cac cell tren row theo quy trong bang tong hop chi tiet chi tieu
     *
     * @param plstColumns
     * @param pObjs
     * @param dashboardRequestDTO
     * @return danh sach cac cell tren row theo quy
     * @throws Exception
     * @author DatNT
     * @version 1.0
     * @since 04/11/2019
     */
    private List<TableCellBaseDTO> setCellValueDetailQuarterly(String pstrTrend, List<TableColumnDTO> plstColumns, Object[] pObjs, DashboardRequestDTO dashboardRequestDTO, int pintChartId, MultiKeyMap multiKeyMap,List<String> idReverse) throws Exception {

        int vintIndex = 0;
        List<TableCellBaseDTO> vlstCellBaseDTOS = new ArrayList<>();
        String vstrShopName = DataUtil.safeToString(pObjs[0]);
        Double vdblQuarterAccumSchedule = (Double) pObjs[3];
        Double vdblQuarterAccumPerform = (Double) pObjs[4];
        Double vdblQuarterAccumComplete = (Double) pObjs[5];
        Double vdblYearSchedule = (Double) pObjs[6];
        Double vdblYearComplete = (Double) pObjs[7];
        Double vdblLastYearFvalue = (Double) pObjs[8];
        Long vlngServiceId = DataUtil.safeToLong(pObjs[9]);
        String vstrShopCode = (String) (pObjs[12]);
        String vstrVdsChannelCode = (String) (pObjs[11]);
        String vstrCodeDVT = DataUtil.safeToString(pObjs[10]);
        String vstrStaffCode = pObjs[13] == null ? null : (String) pObjs[13];
        String vstrDVT = DataUtil.safeToString(pObjs[16]);
        Integer vdblOldRate = DataUtil.safeToInt(pObjs[18]);
        String vstrNewDVT = DataUtil.safeToString(pObjs[19]);
        Integer vdblNewRate = DataUtil.safeToInt(pObjs[20]);
        Double vdblPerform = (Double) pObjs[21];
        String vstrShopShortName = pObjs[22] == null ? null : (String) pObjs[22];

        //ten dvt
        String vstrDvtUse = null;
        if (!DataUtil.isNullOrEmpty(vstrNewDVT)) {
            vstrDvtUse = vstrNewDVT;
        } else {
            if (!DataUtil.isNullOrEmpty(vstrDVT)) {
                vstrDvtUse = vstrDVT;
            }
        }
        for (int i = 0; i < plstColumns.size(); i++) {
            if (Constants.TEXT.equals(plstColumns.get(i).getType())) {
                TableCellBaseDTO cellBaseDTO = new TableCellBaseDTO();
                cellBaseDTO.setColumnId(vintIndex++);
                if (Constants.SHOP_NAME.equals(plstColumns.get(i).getValue())) {
                    if (DataUtil.isNullOrEmpty(vstrDvtUse)) {
                        cellBaseDTO.setViewValue(vstrShopName);
                    } else cellBaseDTO.setViewValue(vstrShopName + " (" + vstrDvtUse + ")");
                    cellBaseDTO.setColor(Constants.COLORS.BLACK);

                }
                //them cot 25/05/20
                if (Constants.SHOP_UNIT.SHOP_CODE.equals(plstColumns.get(i).getValue())) {
                    if (vstrStaffCode != null) {
                        cellBaseDTO.setViewValue(vstrStaffCode);
                    } else {
                        cellBaseDTO.setViewValue(vstrShopCode);
                    }
                }
                if (Constants.SHOP_SHORT_NAME.equals(plstColumns.get(i).getValue())) {
                    cellBaseDTO.setViewValue(vstrShopShortName);
                }
                if (Constants.QUARTERLY_ACCUMULATE_SCHEDULE.equals(plstColumns.get(i).getValue())) {
                    if (vdblQuarterAccumSchedule != null && !DataUtil.isNullOrZero(vdblNewRate) && !DataUtil.isNullOrZero(vdblOldRate)) {
                        vdblQuarterAccumSchedule = vdblQuarterAccumSchedule * vdblOldRate / vdblNewRate;
                        Double vdblValue = Double.valueOf(df.format(vdblQuarterAccumSchedule));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    } else if (vdblQuarterAccumSchedule != null) {
                        Double vdblValue = Double.valueOf(df.format(vdblQuarterAccumSchedule));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    }
                    cellBaseDTO.setColor(Constants.COLORS.BLACK);
                }
                if (Constants.PERFORM.equals(plstColumns.get(i).getValue())) {
                    if (vdblPerform != null && !DataUtil.isNullOrZero(vdblNewRate) && !DataUtil.isNullOrZero(vdblOldRate)) {
                        vdblPerform = vdblPerform * vdblOldRate / vdblNewRate;
                        Double vdblValue = Double.valueOf(df.format(vdblPerform));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    } else if (vdblPerform != null) {
                        Double vdblValue = Double.valueOf(df.format(vdblPerform));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    }
                    cellBaseDTO.setColor(Constants.COLORS.BLACK);
                }
                if (Constants.ACCUMULATE_PERFORM.equals(plstColumns.get(i).getValue())) {
                    if (vdblQuarterAccumPerform != null && !DataUtil.isNullOrZero(vdblNewRate) && !DataUtil.isNullOrZero(vdblOldRate)) {
                        vdblQuarterAccumPerform = vdblQuarterAccumPerform * vdblOldRate / vdblNewRate;
                        Double vdblValue = Double.valueOf(df.format(vdblQuarterAccumPerform));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    } else if (vdblQuarterAccumPerform != null) {
                        Double vdblValue = Double.valueOf(df.format(vdblQuarterAccumPerform));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    }
                    cellBaseDTO.setColor(Constants.COLORS.BLACK);
                }

                if (Constants.QUARTERLY_ACCUMULATE_COMPLETE.equals(plstColumns.get(i).getValue())) {
                    if (vdblQuarterAccumComplete != null) {
                        Double vdblValue = Double.valueOf(df.format(vdblQuarterAccumComplete));
                        cellBaseDTO.setViewValue(format.format(vdblValue) + "%");
                    }
                    else{
                        cellBaseDTO.setViewValue("");
                    }
                    cellBaseDTO.setColor(Constants.COLORS.BLACK);
                }
                if (Constants.YEAR_SCHEDULE.equals(plstColumns.get(i).getValue())) {
                    if (vdblYearSchedule != null && !DataUtil.isNullOrZero(vdblNewRate) && !DataUtil.isNullOrZero(vdblOldRate)) {
                        vdblYearSchedule = vdblYearSchedule * vdblOldRate / vdblNewRate;
                        Double vdblValue = Double.valueOf(df.format(vdblYearSchedule));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    } else if (vdblYearSchedule != null) {
                        Double vdblValue = Double.valueOf(df.format(vdblYearSchedule));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    }
                    cellBaseDTO.setColor(Constants.COLORS.BLACK);
                }
                if (Constants.YEAR_COMPLETE.equals(plstColumns.get(i).getValue())) {
                    if (vdblYearComplete != null) {
                        String vstrColor = "";
                        Double vdblValue = Double.valueOf(df.format(vdblYearComplete));
                        cellBaseDTO.setViewValue(format.format(vdblValue) + "%");
                        if (vdblYearComplete >= 0 && vdblYearComplete < 100)
                            vstrColor = (String) multiKeyMap.get(Constants.COMPLETED_COLOUR, Constants.PARAM_STATUS_0);
                        if (vdblYearComplete >= 100)
                            vstrColor = (String) multiKeyMap.get(Constants.COMPLETED_COLOUR, Constants.PARAM_STATUS);

                        cellBaseDTO.setColor(vstrColor);
                    }
                    else{
                        cellBaseDTO.setViewValue("");
                    }
                }
                if (Constants.LAST_ACCUMULATE_PERFORM.equals(plstColumns.get(i).getValue())) {
                    if (vdblLastYearFvalue != null && !DataUtil.isNullOrZero(vdblNewRate) && !DataUtil.isNullOrZero(vdblOldRate)) {
                        vdblLastYearFvalue = vdblLastYearFvalue * vdblOldRate / vdblNewRate;
                        Double vdblValue = Double.valueOf(df.format(vdblLastYearFvalue));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    } else if (vdblLastYearFvalue != null) {
                        Double vdblValue = Double.valueOf(df.format(vdblLastYearFvalue));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    }
                    cellBaseDTO.setColor(Constants.COLORS.BLACK);
                }

                vlstCellBaseDTOS.add(cellBaseDTO);
            }
            if (Constants.CHART.equals(plstColumns.get(i).getType())) {
                JsonArray lines = plstColumns.get(i).getMetaData();
                TableCellChartDTO cellChartDTO = setChartTable(pstrTrend, lines, vintIndex++, pintChartId, dashboardRequestDTO);
                vlstCellBaseDTOS.add(cellChartDTO);

            }

            if (Constants.GROWTH_YEAR.equals(plstColumns.get(i).getType())) {
                TableCellGrowthDTO cellGrowthDTO = setGrowth(vlngServiceId, vdblQuarterAccumPerform, vdblLastYearFvalue, vintIndex++, vdblOldRate, vdblNewRate,idReverse);
                vlstCellBaseDTOS.add(cellGrowthDTO);
            }
        }

        return vlstCellBaseDTOS;
    }

    /**
     * set gia tri cho cac cell tren row theo thang trong bang
     *
     * @param plstColumns
     * @param pObjs
     * @param dashboardRequestDTO
     * @return danh sach cac cell tren row theo thang
     * @throws Exception
     * @author DatNT
     * @version 1.0
     * @since 15/10/2019
     */
    private List<TableCellBaseDTO> setCellValueMonthly(String pstrTrend, List<TableColumnDTO> plstColumns, Object[] pObjs, DashboardRequestDTO dashboardRequestDTO, int pintChartId, MultiKeyMap multiKeyMap,List<String> idReverse) throws Exception {

        int vintIndex = 0;
        List<TableCellBaseDTO> vlstCellBaseDTOS = new ArrayList<>();
        String vstrServiceName = DataUtil.safeToString(pObjs[0]);
        Double vdblCompleteUnitQuantity = (Double) pObjs[1];
        Double vdblChildUnitQuantity = (Double) pObjs[2];
        Double vdblMonthlyAccumPerform = (Double) pObjs[3];
        Double vdblMonthlyAccumuSchedule = (Double) pObjs[4];
        Double vdblMonthlyAccumComplete = (Double) pObjs[5];
        Double vdblYearSchedule = (Double) pObjs[6];
        Double vdblYearComplete = (Double) pObjs[7];
        Double vdblLastYearFvalue = (Double) pObjs[8];
        Long vlngServiceId = DataUtil.safeToLong(pObjs[9]);
        String vstrCodeDVT = DataUtil.safeToString(pObjs[10]);
        String vstrChannelCode = (String) pObjs[11];
        String vstrShopCode = (String) pObjs[12];
        String vstrDVT = DataUtil.safeToString(pObjs[16]);
        Integer vdblOldRate = DataUtil.safeToInt(pObjs[18]);
        String vstrNewDVT = DataUtil.safeToString(pObjs[19]);
        Integer vdblNewRate = DataUtil.safeToInt(pObjs[20]);
        Double vdblPerform = (Double) pObjs[22];

        //ten dvt
        String vstrDvtUse = null;
        if (!DataUtil.isNullOrEmpty(vstrNewDVT)) {
            vstrDvtUse = vstrNewDVT;
        } else {
            if (!DataUtil.isNullOrEmpty(vstrDVT)) {
                vstrDvtUse = vstrDVT;
            }
        }

        for (int i = 0; i < plstColumns.size(); i++) {
            if (Constants.TEXT.equals(plstColumns.get(i).getType())) {
                TableCellBaseDTO cellBaseDTO = new TableCellBaseDTO();
                cellBaseDTO.setColumnId(vintIndex++);
                if (Constants.SERVICE_NAME.equals(plstColumns.get(i).getValue())) {
                    if (DataUtil.isNullOrEmpty(vstrDvtUse)) {
                        cellBaseDTO.setViewValue(vstrServiceName);

                    } else cellBaseDTO.setViewValue(vstrServiceName + " (" + vstrDvtUse + ")");
                    cellBaseDTO.setColor(Constants.COLORS.BLACK);
                }
                if (Constants.ACHIEVED_UNIT_QUANTITY.equals(plstColumns.get(i).getValue())) {
                    if (vdblCompleteUnitQuantity != null && vdblChildUnitQuantity != null) {
                        cellBaseDTO.setViewValue(df.format(vdblCompleteUnitQuantity) + "/" + df.format(vdblChildUnitQuantity));
                    } else if (vdblCompleteUnitQuantity == null && vdblChildUnitQuantity != null) {
                        cellBaseDTO.setViewValue("-/" + df.format(vdblChildUnitQuantity));
                    }
                    cellBaseDTO.setColor(Constants.COLORS.BLACK);
                }
                if (Constants.PERFORM.equals(plstColumns.get(i).getValue())) {
                    if (vdblPerform != null && !DataUtil.isNullOrZero(vdblNewRate) && !DataUtil.isNullOrZero(vdblOldRate)) {
                        vdblPerform = vdblPerform * vdblOldRate / vdblNewRate;
                        Double vdblValue = Double.valueOf(df.format(vdblPerform));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    } else if (vdblPerform != null) {
                        Double vdblValue = Double.valueOf(df.format(vdblPerform));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    }
                    cellBaseDTO.setColor(Constants.COLORS.BLACK);
                }
                if (Constants.ACCUMULATE_PERFORM.equals(plstColumns.get(i).getValue())) {
                    if (vdblMonthlyAccumPerform != null && !DataUtil.isNullOrZero(vdblNewRate) && !DataUtil.isNullOrZero(vdblOldRate)) {
                        vdblMonthlyAccumPerform = vdblMonthlyAccumPerform * vdblOldRate / vdblNewRate;
                        Double vdblValue = Double.valueOf(df.format(vdblMonthlyAccumPerform));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    } else if (vdblMonthlyAccumPerform != null) {
                        Double vdblValue = Double.valueOf(df.format(vdblMonthlyAccumPerform));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    }
                    cellBaseDTO.setColor(Constants.COLORS.BLACK);
                }
                if (Constants.MONTHLY_ACCUMULATE_SCHEDULE.equals(plstColumns.get(i).getValue())) {
                    if (vdblMonthlyAccumuSchedule != null && !DataUtil.isNullOrZero(vdblNewRate) && !DataUtil.isNullOrZero(vdblOldRate)) {
                        vdblMonthlyAccumuSchedule = vdblMonthlyAccumuSchedule * vdblOldRate / vdblNewRate;
                        Double vdblValue = Double.valueOf(df.format(vdblMonthlyAccumuSchedule));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    } else if (vdblMonthlyAccumuSchedule != null) {
                        Double vdblValue = Double.valueOf(df.format(vdblMonthlyAccumuSchedule));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    }
                    cellBaseDTO.setColor(Constants.COLORS.BLACK);
                }
                if (Constants.MONTHLY_ACCUMULATE_COMPLETE.equals(plstColumns.get(i).getValue())) {
                    if (vdblMonthlyAccumComplete != null) {
                        String vstrWarningColor = "";
                        Double vdblValue = Double.valueOf(df.format(vdblMonthlyAccumComplete));
                        cellBaseDTO.setViewValue(format.format(vdblValue) + "%");
                        String vstrWarningLevel = setWarninglevel(vdblMonthlyAccumComplete, vstrChannelCode, vlngServiceId);
                        if (!DataUtil.isNullOrEmpty(vstrWarningLevel))
                            vstrWarningColor = (String) multiKeyMap.get(Constants.WARNING_LEVEL, vstrWarningLevel);
                        cellBaseDTO.setColor(vstrWarningColor);
                    }
                    else{
                        cellBaseDTO.setViewValue("");
                    }

                }
                if (Constants.YEAR_SCHEDULE.equals(plstColumns.get(i).getValue())) {
                    if (vdblYearSchedule != null && !DataUtil.isNullOrZero(vdblNewRate) && !DataUtil.isNullOrZero(vdblOldRate)) {
                        vdblYearSchedule = vdblYearSchedule * vdblOldRate / vdblNewRate;
                        Double vdblValue = Double.valueOf(df.format(vdblYearSchedule));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    } else if (vdblYearSchedule != null) {
                        Double vdblValue = Double.valueOf(df.format(vdblYearSchedule));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    }
                    cellBaseDTO.setColor(Constants.COLORS.BLACK);
                }
                if (Constants.YEAR_COMPLETE.equals(plstColumns.get(i).getValue())) {
                    if (vdblYearComplete != null) {
                        String vstrColor = "";
                        Double vdblValue = Double.valueOf(df.format(vdblYearComplete));
                        cellBaseDTO.setViewValue(format.format(vdblValue) + "%");
                        if (vdblYearComplete >= 0 && vdblYearComplete < 100) {
                            vstrColor = apParamRepo.getCompletedColor(Constants.PARAM_STATUS_0);
                        } else if (vdblYearComplete >= 100) {
                            vstrColor = apParamRepo.getCompletedColor(Constants.PARAM_STATUS);
                        }
                        cellBaseDTO.setColor(vstrColor);
                    }
                    else{
                        cellBaseDTO.setViewValue("");
                    }
                }
                if (Constants.LAST_ACCUMULATE_PERFORM.equals(plstColumns.get(i).getValue())) {
                    if (vdblLastYearFvalue != null && !DataUtil.isNullOrZero(vdblNewRate) && !DataUtil.isNullOrZero(vdblOldRate)) {
                        vdblLastYearFvalue = vdblLastYearFvalue * vdblOldRate / vdblNewRate;
                        Double vdblValue = Double.valueOf(df.format(vdblLastYearFvalue));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    } else if (vdblLastYearFvalue != null) {
                        Double vdblValue = Double.valueOf(df.format(vdblLastYearFvalue));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    }
                    cellBaseDTO.setColor(Constants.COLORS.BLACK);
                }

                vlstCellBaseDTOS.add(cellBaseDTO);
            }
            if (Constants.CHART.equals(plstColumns.get(i).getType())) {
                JsonArray lines = plstColumns.get(i).getMetaData();
                TableCellChartDTO cellChartDTO = setChartTable(pstrTrend, lines, vintIndex++, pintChartId, dashboardRequestDTO);
                vlstCellBaseDTOS.add(cellChartDTO);

            }

            if (Constants.GROWTH_YEAR.equals(plstColumns.get(i).getType())) {
                TableCellGrowthDTO cellGrowthDTO = setGrowth(vlngServiceId, vdblMonthlyAccumPerform, vdblLastYearFvalue, vintIndex++, vdblOldRate, vdblNewRate,idReverse);
                vlstCellBaseDTOS.add(cellGrowthDTO);
            }
            if (Constants.BAR.equals(plstColumns.get(i).getType())) {
                TableCellBarDTO cellBarDTO = setBar(vdblMonthlyAccumPerform, vdblYearSchedule, vdblMonthlyAccumuSchedule, vdblYearComplete, vintIndex++, vdblNewRate, vdblOldRate, vstrDvtUse);
                vlstCellBaseDTOS.add(cellBarDTO);
            }
        }

        return vlstCellBaseDTOS;
    }


    /**
     * set gia tri cho cac cell tren row theo quy trong bang
     *
     * @param plstColumns
     * @param pObjs
     * @param dashboardRequestDTO
     * @return danh sach cac cell tren row theo quy
     * @throws Exception
     * @author DatNT
     * @version 1.0
     * @since 15/10/2019
     */
    private List<TableCellBaseDTO> setCellValueQuarterly(String pstrTrend, List<TableColumnDTO> plstColumns, Object[] pObjs, DashboardRequestDTO dashboardRequestDTO, int pintChartId, MultiKeyMap multiKeyMap,List<String> idReverse) throws Exception {

        int vintIndex = 0;
        List<TableCellBaseDTO> vlstCellBaseDTOS = new ArrayList<>();
        String vstrServiceName = DataUtil.safeToString(pObjs[0]);
        Double vdblCompleteUnitQuantity = (Double) pObjs[1];
        Double vdblChildUnitQuantity = (Double) pObjs[2];
        Double vdblQuarterAccumPerform = (Double) pObjs[3];
        Double vdblQuarterAccumSchedule = (Double) pObjs[4];
        Double vdblQuarterlyAcculateComplete = (Double) pObjs[5];
        Double vdblYearSchedule = (Double) pObjs[6];
        Double vdblYearComplete = (Double) pObjs[7];
        Double vdblLastYearFvalue = (Double) pObjs[8];
        Long vlngServiceId = DataUtil.safeToLong(pObjs[9]);
        String vstrCodeDVT = DataUtil.safeToString(pObjs[10]);
        String vstrChannelCode = (String) pObjs[11];
        String vstrShopCode = (String) pObjs[12];
        String vstrDVT = DataUtil.safeToString(pObjs[16]);
        Integer vdblOldRate = DataUtil.safeToInt(pObjs[18]);
        String vstrNewDVT = DataUtil.safeToString(pObjs[19]);
        Integer vdblNewRate = DataUtil.safeToInt(pObjs[20]);
        Double vdblPerform = (Double) pObjs[22];

        //ten dvt
        String vstrDvtUse = null;
        if (!DataUtil.isNullOrEmpty(vstrNewDVT)) {
            vstrDvtUse = vstrNewDVT;
        } else {
            if (!DataUtil.isNullOrEmpty(vstrDVT)) {
                vstrDvtUse = vstrDVT;
            }
        }

        for (int i = 0; i < plstColumns.size(); i++) {
            if (Constants.TEXT.equals(plstColumns.get(i).getType())) {
                TableCellBaseDTO cellBaseDTO = new TableCellBaseDTO();
                cellBaseDTO.setColumnId(vintIndex++);
                if (Constants.SERVICE_NAME.equals(plstColumns.get(i).getValue())) {
                    if (DataUtil.isNullOrEmpty(vstrDvtUse)) {
                        cellBaseDTO.setViewValue(vstrServiceName);
                    } else cellBaseDTO.setViewValue(vstrServiceName + " (" + vstrDvtUse + ")");
                    cellBaseDTO.setColor(Constants.COLORS.BLACK);
                }
                if (Constants.ACHIEVED_UNIT_QUANTITY.equals(plstColumns.get(i).getValue())) {
                    if (vdblCompleteUnitQuantity != null && vdblChildUnitQuantity != null) {
                        cellBaseDTO.setViewValue(df.format(vdblCompleteUnitQuantity) + "/" + df.format(vdblChildUnitQuantity));
                    } else if (vdblCompleteUnitQuantity == null && vdblChildUnitQuantity != null) {
                        cellBaseDTO.setViewValue("-/" + df.format(vdblChildUnitQuantity));
                    }
                    cellBaseDTO.setColor(Constants.COLORS.BLACK);
                }
                if (Constants.PERFORM.equals(plstColumns.get(i).getValue())) {
                    if (vdblPerform != null && !DataUtil.isNullOrZero(vdblNewRate) && !DataUtil.isNullOrZero(vdblOldRate)) {
                        vdblPerform = vdblPerform * vdblOldRate / vdblNewRate;
                        Double vdblValue = Double.valueOf(df.format(vdblPerform));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    } else if (vdblPerform != null) {
                        Double vdblValue = Double.valueOf(df.format(vdblPerform));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    }
                    cellBaseDTO.setColor(Constants.COLORS.BLACK);
                }
                if (Constants.ACCUMULATE_PERFORM.equals(plstColumns.get(i).getValue())) {
                    if (vdblQuarterAccumPerform != null && !DataUtil.isNullOrZero(vdblNewRate) && !DataUtil.isNullOrZero(vdblOldRate)) {
                        vdblQuarterAccumPerform = vdblQuarterAccumPerform * vdblOldRate / vdblNewRate;
                        Double vdblValue = Double.valueOf(df.format(vdblQuarterAccumPerform));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    } else if (vdblQuarterAccumPerform != null) {
                        Double vdblValue = Double.valueOf(df.format(vdblQuarterAccumPerform));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    }
                    cellBaseDTO.setColor(Constants.COLORS.BLACK);
                }
                if (Constants.QUARTERLY_ACCUMULATE_SCHEDULE.equals(plstColumns.get(i).getValue())) {
                    if (vdblQuarterAccumSchedule != null && !DataUtil.isNullOrZero(vdblNewRate) && !DataUtil.isNullOrZero(vdblOldRate)) {
                        vdblQuarterAccumSchedule = vdblQuarterAccumSchedule * vdblOldRate / vdblNewRate;
                        Double vdblValue = Double.valueOf(df.format(vdblQuarterAccumSchedule));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    } else if (vdblQuarterAccumSchedule != null) {
                        Double vdblValue = Double.valueOf(df.format(vdblQuarterAccumSchedule));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    }
                    cellBaseDTO.setColor(Constants.COLORS.BLACK);
                }
                if (Constants.QUARTERLY_ACCUMULATE_COMPLETE.equals(plstColumns.get(i).getValue())) {
                    if (vdblQuarterlyAcculateComplete != null) {
                        String vstrWarningColor = "";
                        Double vdblValue = Double.valueOf(df.format(vdblQuarterlyAcculateComplete));
                        cellBaseDTO.setViewValue(format.format(vdblValue) + "%");
                        String vstrWarningLevel = setWarninglevel(vdblQuarterlyAcculateComplete, vstrChannelCode, vlngServiceId);
                        if (!DataUtil.isNullOrEmpty(vstrWarningLevel))
                            vstrWarningColor = (String) multiKeyMap.get(Constants.WARNING_LEVEL, vstrWarningLevel);
                        cellBaseDTO.setColor(vstrWarningColor);
                    }
                    else{
                        cellBaseDTO.setViewValue("");
                    }
                }
                if (Constants.YEAR_SCHEDULE.equals(plstColumns.get(i).getValue())) {
                    if (vdblYearSchedule != null && !DataUtil.isNullOrZero(vdblNewRate) && !DataUtil.isNullOrZero(vdblOldRate)) {
                        vdblYearSchedule = vdblYearSchedule * vdblOldRate / vdblNewRate;
                        Double vdblValue = Double.valueOf(df.format(vdblYearSchedule));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    } else if (vdblYearSchedule != null) {
                        Double vdblValue = Double.valueOf(df.format(vdblYearSchedule));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    }
                    cellBaseDTO.setColor(Constants.COLORS.BLACK);
                }
                if (Constants.YEAR_COMPLETE.equals(plstColumns.get(i).getValue())) {
                    if (vdblYearComplete != null) {
                        String vstrColor = "";
                        Double vdblValue = Double.valueOf(df.format(vdblYearComplete));
                        cellBaseDTO.setViewValue(format.format(vdblValue) + "%");
                        if (vdblYearComplete >= 0 && vdblYearComplete < 100) {
                            vstrColor = apParamRepo.getCompletedColor(Constants.PARAM_STATUS_0);
                        } else if (vdblYearComplete >= 100) {
                            vstrColor = apParamRepo.getCompletedColor(Constants.PARAM_STATUS);
                        }
                        cellBaseDTO.setColor(vstrColor);
                    }
                    else{
                        cellBaseDTO.setViewValue("");
                    }
                }
                if (Constants.LAST_ACCUMULATE_PERFORM.equals(plstColumns.get(i).getValue())) {
                    if (vdblLastYearFvalue != null && !DataUtil.isNullOrZero(vdblNewRate) && !DataUtil.isNullOrZero(vdblOldRate)) {
                        vdblLastYearFvalue = vdblLastYearFvalue * vdblOldRate / vdblNewRate;
                        Double vdblValue = Double.valueOf(df.format(vdblLastYearFvalue));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    } else if (vdblLastYearFvalue != null) {
                        Double vdblValue = Double.valueOf(df.format(vdblLastYearFvalue));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    }
                    cellBaseDTO.setColor(Constants.COLORS.BLACK);
                }

                vlstCellBaseDTOS.add(cellBaseDTO);
            }
            if (Constants.CHART.equals(plstColumns.get(i).getType())) {
                JsonArray lines = plstColumns.get(i).getMetaData();
                TableCellChartDTO cellChartDTO = setChartTable(pstrTrend, lines, vintIndex++, pintChartId, dashboardRequestDTO);
                vlstCellBaseDTOS.add(cellChartDTO);

            }

            if (Constants.GROWTH_YEAR.equals(plstColumns.get(i).getType())) {
                TableCellGrowthDTO cellGrowthDTO = setGrowth(vlngServiceId, vdblQuarterAccumPerform, vdblLastYearFvalue, vintIndex++, vdblOldRate, vdblNewRate,idReverse);
                vlstCellBaseDTOS.add(cellGrowthDTO);
            }
            if (Constants.BAR.equals(plstColumns.get(i).getType())) {
                TableCellBarDTO cellBarDTO = setBar(vdblQuarterAccumPerform, vdblYearSchedule, vdblQuarterAccumSchedule, vdblYearComplete, vintIndex++, vdblNewRate, vdblOldRate, vstrDvtUse);
                vlstCellBaseDTOS.add(cellBarDTO);
            }
        }

        return vlstCellBaseDTOS;
    }

    /**
     * set gia tri cho chart xu the trong bang tong hop
     *
     * @param pstrTrend
     * @param lines
     * @param pintCoulumnIndex
     * @param pintChartId
     * @return
     * @throws Exception
     * @author DatNT
     * @version 1.0
     * @since 24/10/2019
     */
    public TableCellChartDTO setChartTable(String pstrTrend, JsonArray lines, int pintCoulumnIndex, int pintChartId, DashboardRequestDTO dashboardRequestDTO) throws Exception {
        TableCellChartDTO cellChartDTO = new TableCellChartDTO();
        ChartDTO chartDTO = null;
        if (lines != null)
            chartDTO = chartService.getTrendChartTable(pstrTrend, lines, dashboardRequestDTO);
        if (chartDTO != null)
            chartDTO.setChartId(pintChartId);
        cellChartDTO.setColumnId(pintCoulumnIndex);
        cellChartDTO.setChart(chartDTO);
        return cellChartDTO;
    }

    /**
     * set gia tri cho chart xu the trong bang chi tiet level 2 cua chart
     *
     * @param pstrTrend
     * @param lines
     * @param pintCoulumnIndex
     * @param pintChartId
     * @param dashboardRequestDTO
     * @return
     * @throws Exception
     */
    public TableCellChartDTO setChartTableChildShop(String pstrTrend, JsonArray lines, int pintCoulumnIndex, int pintChartId, DashboardRequestDTO dashboardRequestDTO) throws Exception {
        TableCellChartDTO cellChartDTO = new TableCellChartDTO();
        ChartDTO chartDTO = null;
        if (lines != null)
            chartDTO = chartService.getTrendChildShop(pstrTrend, lines, dashboardRequestDTO);
        if (chartDTO != null)
            chartDTO.setChartId(pintChartId);
        cellChartDTO.setColumnId(pintCoulumnIndex);
        cellChartDTO.setChart(chartDTO);
        return cellChartDTO;
    }

    /**
     * set gia tri cho chart xu the  bang tong hop trong bieu do
     *
     * @param plngServiceId
     * @param lines
     * @param pintCoulumnIndex
     * @param pintChartId
     * @return * @throws Exception
     * @version 1.0
     * @since 24/10/2019
     */
    public TableCellChartDTO setChart(Long plngServiceId, Long plngPrdId, String pstrShopCode, int pintCycleId, JsonArray lines, int pintCoulumnIndex, int pintChartId, String pstrChannelCode, String pstrStaffCode) throws Exception {
        TableCellChartDTO cellChartDTO = new TableCellChartDTO();
        ChartDTO chartDTO = null;
        if (lines != null)
            chartDTO = chartService.getTrendChart(plngServiceId, plngPrdId, pstrShopCode, lines, pintCycleId, pstrChannelCode, pstrStaffCode);
        if (chartDTO != null) {
            chartDTO.setChartId(pintChartId);
        }
        cellChartDTO.setColumnId(pintCoulumnIndex);
        cellChartDTO.setChart(chartDTO);
        return cellChartDTO;
    }

    /**
     * xac dinh cung ky  thang, nam
     *
     * @param pdblPerform
     * @param pdblLastPerform
     * @param pintColumnIndex
     * @return
     * @author DatNT
     * @version 1.0
     * @since 24/10/2019
     */
    public TableCellGrowthDTO setGrowth(Long plngServiceId, Double pdblPerform, Double pdblLastPerform, int pintColumnIndex, Integer pdblOldValue, Integer pdblNewValue,List<String> idReverse) throws Exception {
        TableCellGrowthDTO cellGrowthDTO = new TableCellGrowthDTO();
        cellGrowthDTO.setColumnId(pintColumnIndex);
        Double vdblDelta = null;
        if (pdblPerform != null) {
            if (pdblLastPerform != null) {
                if (!DataUtil.isNullOrZero(pdblOldValue) && !DataUtil.isNullOrZero(pdblNewValue))
                    vdblDelta = Double.valueOf(df.format((pdblPerform - pdblLastPerform) * pdblOldValue / pdblNewValue));
                else
                    vdblDelta = Double.valueOf(df.format(pdblPerform - pdblLastPerform));
            } else {
                if (!DataUtil.isNullOrZero(pdblOldValue) && !DataUtil.isNullOrZero(pdblNewValue))
                    vdblDelta = Double.valueOf(df.format(pdblPerform * pdblOldValue / pdblNewValue));
                else
                    vdblDelta = Double.valueOf(df.format(pdblPerform));
            }
        } else {
            if (pdblLastPerform != null) {
                if (!DataUtil.isNullOrZero(pdblOldValue) && !DataUtil.isNullOrZero(pdblNewValue))
                    vdblDelta = Double.valueOf(df.format((0 - pdblLastPerform) * pdblOldValue / pdblNewValue));
                else
                    vdblDelta = Double.valueOf(df.format(0 - pdblLastPerform));
            } else vdblDelta = 0d;
        }

        if (vdblDelta > 0) {
           if (!DataUtil.isNullOrEmpty(idReverse) && idReverse.contains(plngServiceId.toString())) {
                cellGrowthDTO.setIsGrowth(-1);  //1:mui ten di xuong
                cellGrowthDTO.setColor(Constants.COLORS.RED);
            }
            else {
                cellGrowthDTO.setIsGrowth(1);  //1:mui ten di len
                cellGrowthDTO.setColor(Constants.COLORS.GREEN);
            }
            cellGrowthDTO.setDelta(format.format(vdblDelta));
        } else if (vdblDelta < 0) {
            if (!DataUtil.isNullOrEmpty(idReverse) && idReverse.contains(plngServiceId.toString())) {
                cellGrowthDTO.setIsGrowth(1);   //-1: mui ten di len
                cellGrowthDTO.setColor(Constants.COLORS.GREEN);
            }
            else {
                cellGrowthDTO.setIsGrowth(-1);   //-1: mui ten di xuong
                cellGrowthDTO.setColor(Constants.COLORS.RED);
            }
            cellGrowthDTO.setDelta(format.format(vdblDelta));
        } else if (vdblDelta == 0) {
            cellGrowthDTO.setIsGrowth(0); //0: hien thi dau tru
            cellGrowthDTO.setColor(Constants.COLORS.YELLOW);
            cellGrowthDTO.setDelta("0");
        }
        return cellGrowthDTO;
    }

    /**
     * set cac gia tri cho bar toc do trong bang tong hop
     *
     * @param pdblAccumulatePerform
     * @param pdblSchedule
     * @param pdblAccmulateSchedule
     * @param pdblComplete
     * @param pintColumnIndex
     * @return
     * @author DatNT
     * @version 1.0
     * @since 24/10/2019
     */
    private TableCellBarDTO setBar(Double pdblAccumulatePerform, Double pdblSchedule, Double pdblAccmulateSchedule, Double pdblComplete, int pintColumnIndex, Integer pintNewRate, Integer pintOldRate, String pstrDVTName) {
        TableCellBarDTO cellBarDTO = new TableCellBarDTO();
        cellBarDTO.setColumnId(pintColumnIndex);

        if (pdblAccumulatePerform != null) {
            Double vdblValue;
            if (DataUtil.isNullOrEmpty(pstrDVTName)) {
                vdblValue = Double.valueOf(df.format(pdblAccumulatePerform));
                if (vdblValue != null)
                    cellBarDTO.setPerformViewValue(format.format(vdblValue));
            } else {
                if (!DataUtil.isNullOrZero(pintOldRate) && !DataUtil.isNullOrZero(pintNewRate)) {
                    vdblValue = Double.valueOf(df.format(pdblAccumulatePerform * pintOldRate / pintNewRate));
                } else {
                    vdblValue = Double.valueOf(df.format(pdblAccumulatePerform));
                }
                if (vdblValue != null)
                    cellBarDTO.setPerformViewValue(format.format(vdblValue) + " " + pstrDVTName);
            }
            cellBarDTO.setPerformValue(vdblValue);
        }
        if (pdblSchedule != null) {
            Double vdblValue;
            if (DataUtil.isNullOrEmpty(pstrDVTName)) {
                vdblValue = Double.valueOf(df.format(pdblSchedule));
                if (vdblValue != null)
                    cellBarDTO.setPlanMonthViewValue(format.format(vdblValue));
            } else {
                if (!DataUtil.isNullOrZero(pintOldRate) && !DataUtil.isNullOrZero(pintNewRate)) {
                    vdblValue = Double.valueOf(df.format(pdblSchedule * pintOldRate / pintNewRate));
                } else {
                    vdblValue = Double.valueOf(df.format(pdblSchedule));
                }
                if (vdblValue != null)
                    cellBarDTO.setPlanMonthViewValue(format.format(vdblValue) + " " + pstrDVTName);
            }
            cellBarDTO.setPlanMonthValue(vdblValue);
        }
        if (pdblAccmulateSchedule != null) {
            Double vdblValue;
            if (DataUtil.isNullOrEmpty(pstrDVTName)) {
                vdblValue = Double.valueOf(df.format(pdblAccmulateSchedule));
                if (vdblValue != null)
                    cellBarDTO.setPlanAccumulateViewValue(format.format(vdblValue));
            } else {
                if (!DataUtil.isNullOrZero(pintOldRate) && !DataUtil.isNullOrZero(pintNewRate)) {
                    vdblValue = Double.valueOf(df.format(pdblAccmulateSchedule * pintOldRate / pintNewRate));
                } else {
                    vdblValue = Double.valueOf(df.format(pdblAccmulateSchedule));
                }
                if (vdblValue != null)
                    cellBarDTO.setPlanAccumulateViewValue(format.format(vdblValue) + " " + pstrDVTName);
            }
            cellBarDTO.setPlanAccumulateValue(vdblValue);
        }
        if (pdblComplete != null) {
            Double vdblValue = Double.valueOf(df.format(pdblComplete));
            cellBarDTO.setPerformPercent(vdblValue);
            cellBarDTO.setPerformPercentView(format.format(vdblValue) + "%");
        }
        cellBarDTO.setStackLineColor(Constants.COLORS.DARK_GRAY);

        return cellBarDTO;
    }

    public String setWarninglevel(Double pdblComplete, String pstrChannelCode, Long plngServiceId) throws Exception {

        String vstrWarningLevel = "";
        List<ServiceWarningConfig> vlstServiceWarningConfig = serviceWarningConfigRepo.getWarningLevel(pstrChannelCode, plngServiceId);
        Double vdblPercent = pdblComplete / 100;
        if (!DataUtil.isNullOrEmpty(vlstServiceWarningConfig)) {
            for (ServiceWarningConfig serviceWarningConfig : vlstServiceWarningConfig) {
                if (vdblPercent == 0) {
                    vstrWarningLevel = Constants.WARNING_LV3;
                    break;
                } else if (vdblPercent > serviceWarningConfig.getFromValue() && vdblPercent <= serviceWarningConfig.getToValue()) {
                    vstrWarningLevel = String.valueOf(serviceWarningConfig.getWaningLevel());
                    break;
                } else if (vdblPercent > 1) {
                    vstrWarningLevel = Constants.PARAM_STATUS_0;
                    break;
                }
            }
        }
        return vstrWarningLevel;
    }

    /**
     * xay dung table cho dashboard
     *
     * @param dashboardRequestDTO
     * @param singleChart
     * @return tabledto
     * @throws Exception
     * @author DatNT
     * @version 1.0
     * @since 15/10/2019
     */
    @Override
    public TableDTO buildTable(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO singleChart, StaffDTO staffDTO) throws Exception {
        TableDTO tableDTO = summarizeData(dashboardRequestDTO, singleChart, staffDTO);
        return tableDTO;
    }


    /**
     * tong hop du lieu bang dashboard theo chu ky
     *
     * @param dashboardRequestDTO
     * @param singleChart
     * @return tabledto bang du lieu
     * @throws Exception
     * @author DatNT
     * @version 1.0
     * @since 15/10/2019
     */
    private TableDTO summarizeData(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO singleChart, StaffDTO staffDTO) throws Exception {
        StringBuilder logBuilder = new StringBuilder();
        MultiKeyMap multiKeyMap = new MultiKeyMap();
        logBuilder.append("*TABLE");
        long startTime = System.currentTimeMillis();
        long stepTime = System.currentTimeMillis();
        int vintIndex = 0;
        TableDTO tableDTO = new TableDTO();
        List<TableColumnDTO> vlstColumns = new ArrayList<>();
        List<TableRowDTO> vlstRows = new ArrayList<>();
        List<Object[]> vlst;
        String vstrShortName;
        String vstrNationalStaff = dashboardRequestDTO.getNationalStaff();
        logBuilder.append(" - BAT DAU QUERY " + (System.currentTimeMillis() - stepTime) + "ms");

        List<ApParam> vlstApprams = apParamRepo.getByType(Constants.WARNING_LEVEL);
        List<String> idReverse  = apParamRepo.findCodeByType(Constants.REVERSE_SERVICE);
        vlstApprams.forEach(apParam -> {
            multiKeyMap.put(apParam.getType(), apParam.getCode(), apParam.getValue());
        });

        List<ApParam> vlstCompleteColors = apParamRepo.getByType(Constants.COMPLETED_COLOUR);
        vlstCompleteColors.forEach(apParam -> {
            multiKeyMap.put(apParam.getType(), apParam.getCode(), apParam.getValue());
        });

        switch (dashboardRequestDTO.getCycleId()) {
            case Constants.CYCLE_ID.DAY:
                if (DataUtil.isNullOrZero(dashboardRequestDTO.getServiceId()))
                    vlst = tableRepoCustom.sumarizeByDay(dashboardRequestDTO, staffDTO, singleChart);
                else vlst = tableRepoCustom.sumarizeDetailByDay(dashboardRequestDTO, singleChart);
                break;
            case Constants.CYCLE_ID.MONTH:
                if (DataUtil.isNullOrZero(dashboardRequestDTO.getServiceId()))
                    vlst = tableRepoCustom.sumarizeByMonth(dashboardRequestDTO, staffDTO, singleChart);
                else vlst = tableRepoCustom.sumarizeDetailByMonth(dashboardRequestDTO, singleChart);
                break;
            case Constants.CYCLE_ID.QUARTER:
                if (DataUtil.isNullOrZero(dashboardRequestDTO.getServiceId()))
                    vlst = tableRepoCustom.sumarizeByQuarter(dashboardRequestDTO, staffDTO, singleChart);
                else vlst = tableRepoCustom.sumarizeDetailByQuarter(dashboardRequestDTO, singleChart);
                break;
            default:
                vlst = new ArrayList();
        }
        logBuilder.append(" - QUERY XONG " + (System.currentTimeMillis() - stepTime) + "ms");
        logBuilder.append(" - BAT DAU PHAN TICH " + (System.currentTimeMillis() - stepTime) + "ms");

        if (DataUtil.isNullOrEmpty(dashboardRequestDTO.getParentShopCode()))
            vstrShortName = partnerRepo.getNameByShopAndChannel(dashboardRequestDTO.getObjectCode());
        else vstrShortName = partnerRepo.getNameByShopAndChannel(dashboardRequestDTO.getParentShopCode());
        String metadata = singleChart.getMetaData();

        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = (JsonObject) jsonParser.parse(metadata);
        JsonArray columns = null;
        if (!DataUtil.isNullObject(jsonObject))
            columns = (JsonArray) jsonObject.get(Constants.JSON_OBJECT_KEY.COLUMNS);

        String vstrType = "", vstrAlign = "", vstrValue = "", vstrTitle = "", vstrStaff = "";
        JsonArray lines = null;
        //set column
        for (int i = 0; i < columns.size(); i++) {
            JsonObject object = (JsonObject) columns.get(i);
            if (!DataUtil.isNullObject(object)) {
                JsonArray cycles = null;
                TableColumnDTO column = new TableColumnDTO();
                if (DataUtil.checkJson(object.get(Constants.JSON_OBJECT_KEY.CYCLE))) {
                    cycles = (JsonArray) object.get(Constants.JSON_OBJECT_KEY.CYCLE);
                }
                if (DataUtil.checkJson(object.get(Constants.JSON_OBJECT_KEY.TYPE))) {
                    vstrType = object.get(Constants.JSON_OBJECT_KEY.TYPE).getAsString();
                }
                if (DataUtil.checkJson(object.get(Constants.JSON_OBJECT_KEY.ALIGN))) {
                    vstrAlign = object.get(Constants.JSON_OBJECT_KEY.ALIGN).getAsString();
                }
                if (DataUtil.checkJson(object.get(Constants.JSON_OBJECT_KEY.VALUE))) {
                    vstrValue = object.get(Constants.JSON_OBJECT_KEY.VALUE).getAsString();
                }
                if (DataUtil.checkJson(object.get(Constants.JSON_OBJECT_KEY.LINES))) {
                    lines = object.get(Constants.JSON_OBJECT_KEY.LINES).getAsJsonArray();
                }
                if (DataUtil.checkJson(object.get(Constants.JSON_OBJECT_KEY.TITLE))) {
                    vstrTitle = object.get(Constants.JSON_OBJECT_KEY.TITLE).getAsString();
                }
                if (DataUtil.checkJson(object.get(Constants.JSON_OBJECT_KEY.STAFF))) {
                    vstrStaff = object.get(Constants.JSON_OBJECT_KEY.STAFF).getAsString();
                }

                switch (dashboardRequestDTO.getCycleId()) {
                    case Constants.CYCLE_ID.DAY:
                        if (cycles != null && vstrStaff != null) {
                            if ((cycles.toString().contains(Constants.CYCLE.ALL) || cycles.toString().contains(Constants.CYCLE.DAY)) && (vstrStaff.equals(Constants.CYCLE.ALL) || vstrStaff.equals(vstrNationalStaff))) {
                                column.setColumnId(vintIndex++);
                                column.setType(vstrType);
                                column.setName(I18N.get(vstrTitle));
                                column.setAlign(vstrAlign);
                                column.setValue(vstrValue);
                                column.setMetaData(lines);

                                vlstColumns.add(column);
                            }
                        }
                        break;
                    case Constants.CYCLE_ID.MONTH:
                        if (cycles != null && vstrStaff != null) {
                            if ((cycles.toString().contains(Constants.CYCLE.ALL) || cycles.toString().contains(Constants.CYCLE.MONTH)) && (vstrStaff.equals(Constants.CYCLE.ALL) || vstrStaff.equals(vstrNationalStaff))) {
                                column.setColumnId(vintIndex++);
                                column.setType(vstrType);
                                column.setName(I18N.get(vstrTitle));
                                column.setAlign(vstrAlign);
                                column.setValue(vstrValue);
                                column.setMetaData(lines);

                                vlstColumns.add(column);
                            }
                        }
                        break;
                    case Constants.CYCLE_ID.QUARTER:
                        if (cycles != null && vstrStaff != null) {
                            if ((cycles.toString().contains(Constants.CYCLE.ALL) || cycles.toString().contains(Constants.CYCLE.QUARTER)) && (vstrStaff.equals(Constants.CYCLE.ALL) || vstrStaff.equals(vstrNationalStaff))) {
                                column.setColumnId(vintIndex++);
                                column.setType(vstrType);
                                column.setName(I18N.get(vstrTitle));
                                column.setAlign(vstrAlign);
                                column.setValue(vstrValue);
                                column.setMetaData(lines);

                                vlstColumns.add(column);
                            }
                        }
                        break;
                    default:
                        vlstColumns = new ArrayList();
                }

            }
        }

        //set data for rows
        if (!DataUtil.isNullOrEmpty(vlst)) {
            for (Object[] obj : vlst) {
                Long vlngServiceId = DataUtil.safeToLong(obj[9]);
//                String vstrChannelCode = DataUtil.safeToString(obj[11]);
                String vstrShopCode = DataUtil.safeToString(obj[12]);
                String vstrStaffCode = DataUtil.safeToString(obj[13]);
                String vstrTrend = DataUtil.safeToString(obj[17]);

                //set cell value
                List<TableCellBaseDTO> cellBaseDTOS;
                switch (dashboardRequestDTO.getCycleId()) {
                    case Constants.CYCLE_ID.DAY:
                        if (DataUtil.isNullOrZero(dashboardRequestDTO.getServiceId()))
                            cellBaseDTOS = setCellValueDaily(vstrTrend, vlstColumns, obj, dashboardRequestDTO, vintIndex, multiKeyMap,idReverse);
                        else
                            cellBaseDTOS = setCellValueDetailDaily(vstrTrend, vlstColumns, obj, dashboardRequestDTO, vintIndex, multiKeyMap,idReverse);
                        break;
                    case Constants.CYCLE_ID.MONTH:
                        if (DataUtil.isNullOrZero(dashboardRequestDTO.getServiceId()))
                            cellBaseDTOS = setCellValueMonthly(vstrTrend, vlstColumns, obj, dashboardRequestDTO, vintIndex, multiKeyMap,idReverse);
                        else
                            cellBaseDTOS = setCellValueDetailMonthly(vstrTrend, vlstColumns, obj, dashboardRequestDTO, vintIndex, multiKeyMap,idReverse);
                        break;
                    case Constants.CYCLE_ID.QUARTER:
                        if (DataUtil.isNullOrZero(dashboardRequestDTO.getServiceId()))
                            cellBaseDTOS = setCellValueQuarterly(vstrTrend, vlstColumns, obj, dashboardRequestDTO, vintIndex, multiKeyMap,idReverse);
                        else
                            cellBaseDTOS = setCellValueDetailQuarterly(vstrTrend, vlstColumns, obj, dashboardRequestDTO, vintIndex, multiKeyMap,idReverse);
                        break;
                    default:
                        cellBaseDTOS = new ArrayList();
                }

                //set data row
                TableRowDTO row = new TableRowDTO();
                row.setRowIndex(vintIndex++);
                row.setCells(cellBaseDTOS);
                if (DataUtil.isNullOrZero(dashboardRequestDTO.getServiceId())) {
                    row.setServiceId(vlngServiceId);

                } else {
                    row.setServiceId(vlngServiceId);
                    row.setShopCode(vstrShopCode);
                    if (!DataUtil.isNullOrEmpty(vstrStaffCode))
                        row.setStaffCode(vstrStaffCode);
                }

                if (!DataUtil.isNullOrEmpty(row.getStaffCode())) {
                    row.setClicked(false);
                } else row.setClicked(true);

                vlstRows.add(row);

            }
        }

        tableDTO.setType(singleChart.getChartType());
        if (singleChart.getDrilldown() == 1)
            tableDTO.setDrilldown(true);
        else if (singleChart.getDrilldown() == 0)
            tableDTO.setDrilldown(false);
        if (!DataUtil.isNullOrZero(singleChart.getDrillDownType()))
            tableDTO.setDrilldownType(singleChart.getDrillDownType());
        if (!DataUtil.isNullOrZero(singleChart.getDrillDownObjectId()))
            tableDTO.setDrilldownObject(singleChart.getDrillDownObjectId().intValue());
        tableDTO.setColumns(vlstColumns);
        tableDTO.setRows(vlstRows);
        tableDTO.setChartSize(singleChart.getChartSize());

        //build title
        if (!DataUtil.isNullOrEmpty(vstrShortName)) {
            List<String> listParamTitle = Lists.newArrayList();
            listParamTitle.add(vstrShortName);
            if (!DataUtil.isNullOrZero(dashboardRequestDTO.getServiceId())) {
                String vstrServiceName = serviceRepo.findNameById(dashboardRequestDTO.getServiceId());
                if (!DataUtil.isNullOrEmpty(vstrServiceName)) listParamTitle.add(vstrServiceName);
            }
            switch (dashboardRequestDTO.getCycleId()) {
                case Constants.CYCLE_ID.DAY:
                    if (DataUtil.isNullOrZero(dashboardRequestDTO.getServiceId()))
                        listParamTitle.add(I18N.get("table.title.month"));
                    listParamTitle.add(DataUtil.convertLocalDateToString(dashboardRequestDTO.getPrdId(), Constants.CYCLE_ID.DAY));
                    break;
                case Constants.CYCLE_ID.MONTH:
                    if (DataUtil.isNullOrZero(dashboardRequestDTO.getServiceId()))
                        listParamTitle.add(I18N.get("table.title.year"));
                    listParamTitle.add(DataUtil.convertLocalDateToString(dashboardRequestDTO.getPrdId(), Constants.CYCLE_ID.MONTH));
                    break;
                case Constants.CYCLE_ID.QUARTER:
                    if (DataUtil.isNullOrZero(dashboardRequestDTO.getServiceId()))
                        listParamTitle.add(I18N.get("table.title.year"));
                    listParamTitle.add(DataUtil.convertLocalDateToString(dashboardRequestDTO.getPrdId(), Constants.CYCLE_ID.QUARTER));
                    break;
            }

            listParamTitle.add(DataUtil.convertLocalDateToString(dashboardRequestDTO.getPrdId(), dashboardRequestDTO.getCycleId()));
            tableDTO.setTitle(DataUtil.isNullOrEmpty(singleChart.getTitleI18n()) ? singleChart.getTitle().trim() :
                    I18N.get(singleChart.getTitleI18n().trim(), (String[]) listParamTitle.toArray(new String[listParamTitle.size()])));
        }
        logBuilder.append(" - PHAN TICH XONG " + (System.currentTimeMillis() - stepTime) + "ms");
        logBuilder.append(" - TONG THOI GIAN " + ((System.currentTimeMillis() - startTime)) + "ms");
        LOGGER.info(logBuilder.toString());
        return tableDTO;
    }

    @Override
    public List<RenderExcelStaffDTO> renderExcelAllStaff(DashboardRequestDTO dashboardRequestDTO) throws Exception {
        List<RenderExcelStaffDTO> vlstDatas = new ArrayList<>();
        List<Object[]> vlstObjects = tableRepoCustom.renderExcelAllStaff(dashboardRequestDTO);
        int vintCycleId = dashboardRequestDTO.getCycleId();
        RenderExcelStaffDTO titles = new RenderExcelStaffDTO();

        //tieu de bang excel linh dong theo chu ky
        if (vintCycleId == Constants.CYCLE_ID.DAY) {
            titles.setStaffName(I18N.get("title.render.excel.allstaff.staffname"));
            titles.setAccumSchedule(I18N.get("title.render.excel.allstaff.accumscheduleday"));
            titles.setAccumPerform(I18N.get("title.render.excel.allstaff.accumperform"));
            titles.setAccumComplete(I18N.get("title.render.excel.allstaff.accumcompleteday"));
            titles.setSchedule(I18N.get("title.render.excel.allstaff.scheduleday"));
            titles.setComplete(I18N.get("title.render.excel.allstaff.completeday"));
            titles.setUpDown(I18N.get("title.render.excel.allstaff.updown"));
        }
        if (vintCycleId == Constants.CYCLE_ID.MONTH) {
            titles.setStaffName(I18N.get("title.render.excel.allstaff.staffname"));
            titles.setAccumSchedule(I18N.get("title.render.excel.allstaff.accumschedulemonth"));
            titles.setAccumPerform(I18N.get("title.render.excel.allstaff.accumperform"));
            titles.setAccumComplete(I18N.get("title.render.excel.allstaff.accumcompletemonth"));
            titles.setSchedule(I18N.get("title.render.excel.allstaff.schedulemonth"));
            titles.setComplete(I18N.get("title.render.excel.allstaff.completemonth"));
            titles.setUpDown(I18N.get("title.render.excel.allstaff.updown"));
        }
        if (vintCycleId == Constants.CYCLE_ID.QUARTER) {
            titles.setStaffName(I18N.get("title.render.excel.allstaff.staffname"));
            titles.setAccumSchedule(I18N.get("title.render.excel.allstaff.accumschedulequarter"));
            titles.setAccumPerform(I18N.get("title.render.excel.allstaff.accumperform"));
            titles.setAccumComplete(I18N.get("title.render.excel.allstaff.accumcompletequarter"));
            titles.setSchedule(I18N.get("title.render.excel.allstaff.schedulequarter"));
            titles.setComplete(I18N.get("title.render.excel.allstaff.completequarter"));
            titles.setUpDown(I18N.get("title.render.excel.allstaff.updown"));
        }

        vlstDatas.add(titles);

        if (vlstObjects != null && !vlstObjects.isEmpty()) {
            for (Object[] obj : vlstObjects) {
                RenderExcelStaffDTO data = new RenderExcelStaffDTO();
                data.setStaffName((String) obj[0]);
                data.setAccumSchedule(DataUtil.safeToString(obj[1] == null ? obj[1] : format.format(obj[1])));
                data.setAccumPerform(DataUtil.safeToString(obj[2] == null ? obj[2] : format.format(obj[2])));
                data.setAccumComplete(DataUtil.safeToString(obj[3] == null ? "" : obj[3] + "%"));
                data.setSchedule(DataUtil.safeToString(obj[4] == null ? obj[4] : format.format(obj[4])));
                data.setComplete(DataUtil.safeToString(obj[5] == null ? "" : obj[5] + "%"));
                data.setUpDown(DataUtil.safeToString(obj[6] == null ? obj[6] : format.format(obj[6])));
                vlstDatas.add(data);
            }
        }

        return vlstDatas;
    }
}
