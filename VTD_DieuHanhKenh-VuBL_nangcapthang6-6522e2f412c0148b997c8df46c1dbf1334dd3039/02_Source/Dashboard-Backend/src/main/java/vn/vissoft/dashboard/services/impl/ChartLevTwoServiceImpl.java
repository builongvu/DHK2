package vn.vissoft.dashboard.services.impl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.vissoft.dashboard.config.I18N;
import vn.vissoft.dashboard.dto.ColumnsDTO;
import vn.vissoft.dashboard.dto.DashboardRequestDTO;
import vn.vissoft.dashboard.dto.LineDTO;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.chart.*;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.model.ManageInfoPartner;
import vn.vissoft.dashboard.model.Unit;
import vn.vissoft.dashboard.repo.*;
import vn.vissoft.dashboard.repo.impl.ChartRepoImpl;
import vn.vissoft.dashboard.services.ChartLevTwoService;

import javax.persistence.criteria.CriteriaBuilder;
import javax.transaction.Transactional;
import javax.xml.crypto.Data;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

@Transactional
@Service
public class ChartLevTwoServiceImpl implements ChartLevTwoService {

    private static final Logger LOGGER = LogManager.getLogger(ChartLevTwoServiceImpl.class);

    @Autowired
    private ChartRepoImpl chartRepoImpl;

    @Autowired
    private ChartRepoCustom chartRepoCustom;

    @Autowired
    private TableServiceImpl tableService;

    @Autowired
    private ServiceRepo serviceRepo;

    @Autowired
    private ApParamRepo apParamRepo;

    private NumberFormat format = NumberFormat.getInstance();
    private DecimalFormat df = new DecimalFormat("###.##");

    /**
     * lay bieu do cot cac don vi con theo don vi cha (dashboard level 2)
     *
     * @param dashboardRequestDTO
     * @param configSingleChartDTO
     * @param staffDTO
     * @return
     * @throws Exception
     * @author VuBL
     * @since 2019/11
     */
    @Override
    public ChartDTO getColChartChildShop(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChartDTO, StaffDTO staffDTO, Boolean pblnZoom) throws Exception {
        ChartDTO data = new ChartDTO();
        List<SeriesDTO> vlstSeries = new ArrayList<>();
        SeriesDTO serie = new SeriesDTO();
        List<PointDTO> vlstPoints = new ArrayList<>();
        List<String> vlstCategories = new ArrayList<>();
        Unit unit;
        Boolean vblnCheckUnitType = false;
        List<String> vlstUnitTypes = new ArrayList<>();
        List<String> idNoPlanDaily = apParamRepo.findCodeByType(Constants.NO_PLAN_DAILY);
        List<Object[]> vlstData = chartRepoCustom.findDataChildShop(dashboardRequestDTO, staffDTO, configSingleChartDTO, Constants.REDUCTION);

        //lay cau hinh cua bieu do (line) tu metaData trong db
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = (JsonObject) jsonParser.parse(configSingleChartDTO.getMetaData());
        JsonArray varrLines = (JsonArray) jsonObject.get("lines");
        Type type = new TypeToken<ArrayList<LineDTO>>() { 
        }.getType();
        List<LineDTO> vlstLines = new Gson().fromJson(varrLines.toString(), type);
        LineDTO line = vlstLines.get(0);

        //set du lieu cua bieu do
        data.setType(Constants.CHARTTYPE.COLUMN_CHART);
        data.setChartSize(configSingleChartDTO.getChartSize());
        data.setChartType(configSingleChartDTO.getChartType());
        if (configSingleChartDTO.getExpand() == 1) {
            data.setExpand(true);
        } else {
            data.setExpand(false);
        }

        if (configSingleChartDTO.getTitleI18n() != null) {
            data.setTitle(I18N.get(configSingleChartDTO.getTitleI18n()));
        } else {
            data.setTitle(configSingleChartDTO.getTitle());
        }
        for (int i = 0; i < vlstData.size(); i++) {
            vlstCategories.add(DataUtil.safeToString(vlstData.get(i)[0]));
            if (pblnZoom == true) {
                if (vlstCategories.size() == 5) break;
            }
        }
        data.setCategories(vlstCategories);

        //set noData
        if (!DataUtil.isNullOrEmpty(vlstData)) {
            data.setNoData(false);
        } else {
            data.setNoData(true);
        }

        //set series
        serie.setColor(line.getColor());
        serie.setAverage(Boolean.parseBoolean(line.getAverage()));
        serie.setChartType(configSingleChartDTO.getChartType());

        //set point
        for (int i = 0; i < vlstData.size(); i++) {
//            String vstrUnitType;
//            Double vdblOldRate = null;
//            Double vdblNewRate = null;
            //don vi tinh mac dinh
//            vstrUnitType = (String) vlstData.get(i)[10];
            //lay don vi tinh
            Long vlngServiceId = dashboardRequestDTO.getServiceId();
            String vstrShopCode = DataUtil.safeToString(vlstData.get(i)[5]);
            String vstrChannelCode = null;
            if (!DataUtil.isNullObject(vlstData.get(i)[4])) {
                vstrChannelCode = String.valueOf(vlstData.get(i)[4]);
            }
            unit = chartRepoImpl.getNewUnitType(vlngServiceId, vstrShopCode, vstrChannelCode);
            //lay du lieu de tinh toan
//            if (!DataUtil.isNullObject(unit)) {
//                vstrUnitType = unit.getName();
//                vdblNewRate = unit.getRate();
//                String vstrCode = String.valueOf(vlstData.get(i)[6]);
//                vdblOldRate = chartRepoImpl.getOldRate(vstrCode);
//            }
//            vlstUnitTypes.add(vstrUnitType);

            Double vdblPerformValue;
            Long vstrServiceId = DataUtil.safeToLong(vlstData.get(i)[8]);
            if (!DataUtil.isNullOrEmpty(idNoPlanDaily) && idNoPlanDaily.contains(vstrServiceId.toString())) {
                vdblPerformValue = (Double) vlstData.get(i)[11];
            } else {
                vdblPerformValue = (Double) vlstData.get(i)[12];
            }
//            if (!DataUtil.isNullOrZero(vdblPerformValue) && !DataUtil.isNullOrZero(vdblNewRate) && !DataUtil.isNullOrZero(vdblOldRate)) {
//                vdblPerformValue = vdblPerformValue * vdblOldRate / vdblNewRate;
//            }
            if (vdblPerformValue != null) {
                Double vdblValue = Double.valueOf(df.format(vdblPerformValue));
                PointDTO dataPoint = new PointDTO();
                dataPoint.setCategory(DataUtil.safeToString(vlstData.get(i)[0]));
                if (vdblValue != null) {
                    dataPoint.setValue(vdblValue);
                }
                if (DataUtil.isNullOrEmpty(format.format(vdblValue))) {
                    dataPoint.setViewValue(null);
                } else {
                    dataPoint.setViewValue(format.format(vdblValue) + "%");
                }
                vlstPoints.add(dataPoint);
                if (pblnZoom == true) {
                    if (vlstPoints.size() == 5) break;
                }
            } else {
                PointDTO dataPoint = new PointDTO();
                dataPoint.setCategory(DataUtil.safeToString(vlstData.get(i)[0]));
                vlstPoints.add(dataPoint);
                if (pblnZoom == true) {
                    if (vlstPoints.size() == 5) break;
                }
            }
        }
        serie.setPoints(vlstPoints);

        //check don vi tinh chung
        if (!DataUtil.isNullOrEmpty(vlstUnitTypes)) {
            if (vlstUnitTypes.size() == 1) {
                vblnCheckUnitType = true;
            } else {
                for (int i = 0; i < vlstUnitTypes.size(); i++) {
                    if (i == vlstUnitTypes.size() - 1) {
                        break;
                    } else {
                        if (vlstUnitTypes.get(i) != null && vlstUnitTypes.get(i + 1) != null && vlstUnitTypes.get(i).equals(vlstUnitTypes.get(i + 1))) {
                            vblnCheckUnitType = true;
                        } else {
                            vblnCheckUnitType = false;
                            break;
                        }
                    }
                }
            }
        }
//        if (vblnCheckUnitType == true) {
//            serie.setUnitType(vlstUnitTypes.get(0));
//        }
        serie.setUnitType(Constants.PARAM_PERCENT);

        vlstSeries.add(serie);
        data.setSeries(vlstSeries);

        return data;
    }

    /**
     * lay ra du lieu bang chi tiet theo don vi con (dashboard level 2)
     *
     * @param dashboardRequestDTO
     * @param configSingleChartDTO
     * @param staffDTO
     * @return
     * @throws Exception
     * @author VuBL
     * @since 2019/11
     */
    @Override
    public TableDTO getTableChildShop(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChartDTO, StaffDTO staffDTO) throws Exception {
        StringBuilder logBuilder = new StringBuilder();
        logBuilder.append("*CHART(TABLE LV2 - CARD1)");
        long startTime = System.currentTimeMillis();
        long stepTime = System.currentTimeMillis();
        TableDTO data = new TableDTO();
        List<TableColumnDTO> vlstColumns = new ArrayList<>();
        List<TableRowDTO> vlstRows = new ArrayList<>();
//        List<Object[]> vlstTrends = new ArrayList<>();
//        List<Object[]> vlstContents = new ArrayList<>();
//        List<Object[]> vlstTrendInput;
        logBuilder.append(" - BAT DAU QUERY " + (System.currentTimeMillis() - stepTime) + "ms");
        List<String> idReverse = apParamRepo.findCodeByType(Constants.REVERSE_SERVICE);
        List<Object[]> vlstData = chartRepoCustom.findDataChildShopTable(dashboardRequestDTO, staffDTO, configSingleChartDTO);
        logBuilder.append(" - QUERY XONG " + (System.currentTimeMillis() - stepTime) + "ms");
        logBuilder.append(" - BAT DAU PHAN TICH " + (System.currentTimeMillis() - stepTime) + "ms");
        int vintCycleId = dashboardRequestDTO.getCycleId();
        int vintIndex = 0;

        //lay cau hinh cua bang chi tiet (column) tu metaData trong db
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = (JsonObject) jsonParser.parse(configSingleChartDTO.getMetaData());
        JsonArray varrLines = (JsonArray) jsonObject.get("columns");
        Type type = new TypeToken<ArrayList<ColumnsDTO>>() {
        }.getType();
        List<ColumnsDTO> vlstColumnsMeta = new Gson().fromJson(varrLines.toString(), type);

        //set Columns
        if (!DataUtil.isNullOrEmpty(vlstColumnsMeta)) {
            for (int i = 0; i < vlstColumnsMeta.size(); i++) {
                ColumnsDTO column = vlstColumnsMeta.get(i);
                JsonObject object = (JsonObject) varrLines.get(i);
                JsonArray lines = null;
                if (DataUtil.checkJson(object.get(Constants.JSON_OBJECT_KEY.LINES))) {
                    lines = object.get(Constants.JSON_OBJECT_KEY.LINES).getAsJsonArray();
                }
                String cycle = object.get("cycle").toString();
                TableColumnDTO tableColumnDTO = new TableColumnDTO();
                switch (vintCycleId) {
                    case Constants.CYCLE_ID.DAY:
                        if (cycle.contains(Constants.CYCLE.ALL) || cycle.contains(Constants.CYCLE.DAY)) {
                            tableColumnDTO.setName(I18N.get(column.getTitle()));
                            tableColumnDTO.setAlign(column.getAlign());
                            tableColumnDTO.setColumnId(column.getColumnId());
                            tableColumnDTO.setType(column.getType());
                            tableColumnDTO.setValue(column.getValue());
                            tableColumnDTO.setMetaData(lines);
                            vlstColumns.add(tableColumnDTO);
                        }
                        break;
                    case Constants.CYCLE_ID.MONTH:
                        if (cycle.contains(Constants.CYCLE.ALL) || cycle.contains(Constants.CYCLE.MONTH)) {
                            tableColumnDTO.setName(I18N.get(column.getTitle()));
                            tableColumnDTO.setAlign(column.getAlign());
                            tableColumnDTO.setColumnId(column.getColumnId());
                            tableColumnDTO.setType(column.getType());
                            tableColumnDTO.setValue(column.getValue());
                            tableColumnDTO.setMetaData(lines);
                            vlstColumns.add(tableColumnDTO);
                        }
                        break;
                    case Constants.CYCLE_ID.QUARTER:
                        if (cycle.contains(Constants.CYCLE.ALL) || cycle.contains(Constants.CYCLE.QUARTER)) {
                            tableColumnDTO.setName(I18N.get(column.getTitle()));
                            tableColumnDTO.setAlign(column.getAlign());
                            tableColumnDTO.setColumnId(column.getColumnId());
                            tableColumnDTO.setType(column.getType());
                            tableColumnDTO.setValue(column.getValue());
                            tableColumnDTO.setMetaData(lines);
                            vlstColumns.add(tableColumnDTO);
                        }
                        break;
                }
            }
        }

        //set data for rows
        if (!DataUtil.isNullOrEmpty(vlstData)) {
//            for (Object[] obj : vlstData) {
//                String rowCode = DataUtil.safeToString(obj[12]);
//                if (!DataUtil.isNullOrEmpty(rowCode)) {
//                    if (Constants.XU_THE_CODE.equals(rowCode.trim())) {
//                        vlstTrends.add(obj);
//                    } else
//                    if (Constants.TONG_HOP_CODE.equals(rowCode.trim())) {
//                        vlstContents.add(obj);
//                    }
//
//                }
//            }

            for (Object[] obj : vlstData) {
//                Long vlngServiceId = DataUtil.safeToLong(obj[8]);
//                String vstrChannelCode = DataUtil.safeToString(obj[4]);
//                String vstrShopCode = DataUtil.safeToString(obj[5]);
//                String vstrStaffCode = DataUtil.safeToString(obj[9]);
                //them 01/06/2020
                String vstrTrend = DataUtil.safeToString(obj[13]);
//                vlstTrendInput = new ArrayList<>();
//                for (Object[] objectTrend : vlstTrends) {
//                    Long vlngServiceIdTrend = DataUtil.safeToLong(objectTrend[8]);
//                    String vstrChannelCodeTrend = DataUtil.safeToString(objectTrend[4]);
//                    String vstrShopCodeTrend = DataUtil.safeToString(objectTrend[5]);
//                    String vstrStaffCodeTrend = DataUtil.safeToString(objectTrend[9]);
//                    if (vlngServiceId.equals(vlngServiceIdTrend) && vstrChannelCode.equals(vstrChannelCodeTrend) &&
//                            vstrShopCode.equals(vstrShopCodeTrend) && vstrStaffCode.equals(vstrStaffCodeTrend)) {
//                        vlstTrendInput.add(objectTrend);
//                    }
//                }

                //set cell value
                List<TableCellBaseDTO> cellBaseDTOs = setCellChildShop(vstrTrend, vlstColumns, obj, vintCycleId, dashboardRequestDTO, vintIndex, idReverse);
                TableRowDTO row = new TableRowDTO();
                row.setRowIndex(vintIndex++);
                row.setCells(cellBaseDTOs);
                row.setShopCode(DataUtil.safeToString(obj[5]));
                row.setStaffCode(DataUtil.safeToString(obj[9]));
                row.setServiceId(dashboardRequestDTO.getServiceId());

                //set Clicked
                if (!DataUtil.isNullOrEmpty(row.getStaffCode())) {
//                    String vstrStaffName = staffRepo.findStaffNameByCode(row.getStaffCode().trim());
//                    if (row.getCells().get(0).getViewValue().equalsIgnoreCase(vstrStaffName))
                    row.setClicked(false);
                } else row.setClicked(true);
                if (DataUtil.isNullOrZero(configSingleChartDTO.getDrillDownObjectId())) row.setClicked(false);

                vlstRows.add(row);
            }
        }

        //check chi kenh tinh moi duoc click xem chi tiet tu bang chi tiet
        if (!DataUtil.isNullOrZero(configSingleChartDTO.getDrillDownObjectId())) {
            data.setDrilldownObject(configSingleChartDTO.getDrillDownObjectId().intValue());
        }

        //set du lieu table
        data.setChartSize(configSingleChartDTO.getChartSize());
        data.setType(Constants.CHARTTYPE.TABLE_VIEW);
        data.setRows(vlstRows);
        data.setColumns(vlstColumns);
        data.setTitle(null);

        logBuilder.append(" - PHAN TICH XONG " + (System.currentTimeMillis() - stepTime) + "ms");
        logBuilder.append(" - TONG THOI GIAN " + ((System.currentTimeMillis() - startTime)) + "ms");
        LOGGER.info(logBuilder.toString());

        return data;
    }

    /**
     * lay bieu do cot theo goi thue bao(chi tieu con) o dashboard level (chart)
     *
     * @param dashboardRequestDTO
     * @param configSingleChartDTO
     * @param staffDTO
     * @return
     * @throws Exception
     * @author VuBL
     * @since 2019/11
     */
    @Override
    public ChartDTO getColChartChildService(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChartDTO, StaffDTO staffDTO, Boolean pblnZoom) throws Exception {
        ChartDTO data = new ChartDTO();
        List<SeriesDTO> vlstSeries = new ArrayList<>();
        SeriesDTO serie = new SeriesDTO();
        List<PointDTO> vlstPoints = new ArrayList<>();
        List<String> vlstCategories = new ArrayList<>();
        Unit unit;
        String vstrUnitType;
        Boolean vblnCheckUnitType = false;
        List<String> vlstUnitTypes = new ArrayList<>();
        List<Object[]> vlstData = chartRepoCustom.finDataChildService(dashboardRequestDTO, configSingleChartDTO, staffDTO, "reduction");

        //lay cau hinh cua bieu do (line) tu metaData trong db
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = (JsonObject) jsonParser.parse(configSingleChartDTO.getMetaData());
        JsonArray varrLines = (JsonArray) jsonObject.get("lines");
        Type type = new TypeToken<ArrayList<LineDTO>>() {
        }.getType();
        List<LineDTO> vlstLines = new Gson().fromJson(varrLines.toString(), type);
        LineDTO line = vlstLines.get(0);

        //set du lieu bieu do
        data.setType(Constants.CHARTTYPE.COLUMN_CHART);
        data.setChartSize(configSingleChartDTO.getChartSize());
        data.setChartType(configSingleChartDTO.getChartType());

        if (configSingleChartDTO.getTitleI18n() != null) {
            data.setTitle(I18N.get(configSingleChartDTO.getTitleI18n()));
        } else {
            data.setTitle(configSingleChartDTO.getTitle());
        }
        if (configSingleChartDTO.getExpand() == 1) {
            data.setExpand(true);
        } else {
            data.setExpand(false);
        }
        for (int i = 0; i < vlstData.size(); i++) {
            vlstCategories.add(DataUtil.safeToString(vlstData.get(i)[0]));
            if (pblnZoom == true) {
                if (vlstCategories.size() == 5) break;
            }
        }
        data.setCategories(vlstCategories);

        //set noData
        if (!DataUtil.isNullOrEmpty(vlstData)) {
            data.setNoData(false);
        } else {
            data.setNoData(true);
        }

        //set series
        serie.setColor(line.getColor());
        serie.setAverage(Boolean.parseBoolean(line.getAverage()));
        serie.setChartType(configSingleChartDTO.getChartType());
        serie.setUnitType(null);

        if (!DataUtil.isNullOrEmpty(vlstData)) {
            for (int i = 0; i < vlstData.size(); i++) {
                Double vdblOldRate = null;
                Double vdblNewRate = null;
                //don vi tinh mac dinh
                vstrUnitType = (String) vlstData.get(i)[10];
                //don vi tinh
                Long vlngServiceId = DataUtil.safeToLong(vlstData.get(i)[8]);
                String vstrShopCode = String.valueOf(vlstData.get(i)[5]);
                String vstrChannelCode = null;
                if (!DataUtil.isNullObject(vlstData.get(i)[4])) {
                    vstrChannelCode = String.valueOf(vlstData.get(i)[4]);
                }
                unit = chartRepoImpl.getNewUnitType(vlngServiceId, vstrShopCode, vstrChannelCode);
//                unit = chartRepoImpl.getUnitType(DataUtil.safeToLong(vlstData.get(i)[8]), DataUtil.safeToString(vlstData.get(i)[4]), DataUtil.safeToString(vlstData.get(i)[6]), DataUtil.safeToString(vlstData.get(i)[5]), vintCycleId);
                if (!DataUtil.isNullObject(unit)) {
                    vstrUnitType = unit.getName();
                    vdblNewRate = unit.getRate();
                    String vstrCode = String.valueOf(vlstData.get(i)[6]);
                    vdblOldRate = chartRepoImpl.getOldRate(vstrCode);
                }
                vlstUnitTypes.add(vstrUnitType);
                Double vdblPerformValue = (Double) vlstData.get(i)[1];
                if (!DataUtil.isNullOrZero(vdblPerformValue) && !DataUtil.isNullOrZero(vdblNewRate) && !DataUtil.isNullOrZero(vdblOldRate)) {
                    vdblPerformValue = vdblPerformValue * vdblOldRate / vdblNewRate;
                }
                if (vdblPerformValue != null) {
                    Double vdblValue = Double.valueOf(df.format(vdblPerformValue));
                    PointDTO dataPoint = new PointDTO();
                    dataPoint.setCategory(DataUtil.safeToString(vlstData.get(i)[0]));
                    if (vdblValue != null) {
                        dataPoint.setValue(vdblValue);
                    }
                    if (DataUtil.isNullOrEmpty(format.format(vdblValue))) {
                        dataPoint.setViewValue(null);
                    } else {
                        dataPoint.setViewValue(format.format(vdblValue) + " " + (!DataUtil.isNullOrEmpty(vstrUnitType) ? vstrUnitType : DataUtil.safeToString(vlstData.get(i)[10])));
                    }
                    vlstPoints.add(dataPoint);
                    if (pblnZoom == true) {
                        if (vlstPoints.size() == 5) break;
                    }
                } else {
                    PointDTO dataPoint = new PointDTO();
                    dataPoint.setCategory(DataUtil.safeToString(vlstData.get(i)[0]));
                    vlstPoints.add(dataPoint);
                    if (pblnZoom == true) {
                        if (vlstPoints.size() == 5) break;
                    }
                }
            }
        }
        serie.setPoints(vlstPoints);

        //check don vi tinh chung
        if (!DataUtil.isNullOrEmpty(vlstUnitTypes)) {
            if (vlstUnitTypes.size() == 1) {
                vblnCheckUnitType = true;
            } else {
                for (int i = 0; i < vlstUnitTypes.size(); i++) {
                    if (i == vlstUnitTypes.size() - 1) {
                        break;
                    } else {
                        if (vlstUnitTypes.get(i) != null && vlstUnitTypes.get(i + 1) != null && vlstUnitTypes.get(i).equals(vlstUnitTypes.get(i + 1))) {
                            vblnCheckUnitType = true;
                        } else {
                            vblnCheckUnitType = false;
                            break;
                        }
                    }
                }
            }
        }
        if (vblnCheckUnitType == true) {
            serie.setUnitType(vlstUnitTypes.get(0));
        }

        vlstSeries.add(serie);
        data.setSeries(vlstSeries);

        return data;
    }

    /**
     * lay ra du lieu bang chi tiet theo goi thue bao(chi tieu con) o dashboard level 2 (chart)
     *
     * @param dashboardRequestDTO
     * @param configSingleChartDTO
     * @param staffDTO
     * @return
     * @throws Exception
     * @author VuBL
     * @since 2019/11
     */
    @Override
    public TableDTO getTableChildService(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChartDTO, StaffDTO staffDTO) throws Exception {
        TableDTO data = new TableDTO();
        List<TableColumnDTO> vlstColumns = new ArrayList<>();
        List<TableRowDTO> vlstRows = new ArrayList<>();
        List<String> idReverse = apParamRepo.findCodeByType(Constants.REVERSE_SERVICE);
        List<Object[]> vlstData = chartRepoCustom.finDataChildService(dashboardRequestDTO, configSingleChartDTO, staffDTO, null);
        int vintCycleId = dashboardRequestDTO.getCycleId();
        int vintIndex = 0;

        //lay cau hinh cua bang chi tiet (column) tu metaData trong db
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = (JsonObject) jsonParser.parse(configSingleChartDTO.getMetaData());
        JsonArray varrLines = (JsonArray) jsonObject.get("columns");
        Type type = new TypeToken<ArrayList<ColumnsDTO>>() {
        }.getType();
        List<ColumnsDTO> vlstColumnsMeta = new Gson().fromJson(varrLines.toString(), type);

        //set Columns
        if (!DataUtil.isNullOrEmpty(vlstColumnsMeta)) {
            for (int i = 0; i < vlstColumnsMeta.size(); i++) {
                ColumnsDTO column = vlstColumnsMeta.get(i);
                JsonObject object = (JsonObject) varrLines.get(i);
                JsonArray lines = null;
                if (DataUtil.checkJson(object.get(Constants.JSON_OBJECT_KEY.LINES))) {
                    lines = object.get(Constants.JSON_OBJECT_KEY.LINES).getAsJsonArray();
                }
                String cycle = object.get("cycle").toString();
                TableColumnDTO tableColumnDTO = new TableColumnDTO();
                switch (vintCycleId) {
                    case Constants.CYCLE_ID.DAY:
                        if (cycle.contains(Constants.CYCLE.ALL) || cycle.contains(Constants.CYCLE.DAY)) {
                            tableColumnDTO.setName(I18N.get(column.getTitle()));
                            tableColumnDTO.setAlign(column.getAlign());
                            tableColumnDTO.setColumnId(column.getColumnId());
                            tableColumnDTO.setType(column.getType());
                            tableColumnDTO.setValue(column.getValue());
                            tableColumnDTO.setMetaData(lines);
                            vlstColumns.add(tableColumnDTO);
                        }
                        break;
                    case Constants.CYCLE_ID.MONTH:
                        if (cycle.contains(Constants.CYCLE.ALL) || cycle.contains(Constants.CYCLE.MONTH)) {
                            tableColumnDTO.setName(I18N.get(column.getTitle()));
                            tableColumnDTO.setAlign(column.getAlign());
                            tableColumnDTO.setColumnId(column.getColumnId());
                            tableColumnDTO.setType(column.getType());
                            tableColumnDTO.setValue(column.getValue());
                            tableColumnDTO.setMetaData(lines);
                            vlstColumns.add(tableColumnDTO);
                        }
                        break;
                    case Constants.CYCLE_ID.QUARTER:
                        if (cycle.contains(Constants.CYCLE.ALL) || cycle.contains(Constants.CYCLE.QUARTER)) {
                            tableColumnDTO.setName(I18N.get(column.getTitle()));
                            tableColumnDTO.setAlign(column.getAlign());
                            tableColumnDTO.setColumnId(column.getColumnId());
                            tableColumnDTO.setType(column.getType());
                            tableColumnDTO.setValue(column.getValue());
                            tableColumnDTO.setMetaData(lines);
                            vlstColumns.add(tableColumnDTO);
                        }
                        break;
                }
            }
        }

        //set cac Row va Cell
        if (!DataUtil.isNullOrEmpty(vlstData)) {
            for (Object[] obj : vlstData) {
                List<TableCellBaseDTO> cellBaseDTOs = setCellChildService(vlstColumns, obj, vintCycleId, dashboardRequestDTO, vintIndex, staffDTO, idReverse);
                TableRowDTO row = new TableRowDTO();
                row.setRowIndex(vintIndex++);
                row.setCells(cellBaseDTOs);
                row.setServiceId(obj[8] == null ? null : DataUtil.safeToLong(obj[8]));
                row.setClicked(true);
                vlstRows.add(row);
            }
        }

        //set du lieu bang chi tiet
        data.setChartSize(configSingleChartDTO.getChartSize());
        data.setType(Constants.CHARTTYPE.TABLE_VIEW);
        data.setRows(vlstRows);
        data.setColumns(vlstColumns);
        data.setTitle(null);
        if (configSingleChartDTO.getDrilldown() != null) {
            data.setDrilldown(configSingleChartDTO.getDrilldown() == 1 ? true : false);
        }
        data.setDrilldownType(configSingleChartDTO.getDrillDownType() == null ? null : configSingleChartDTO.getDrillDownType());
        data.setDrilldownObject(configSingleChartDTO.getDrillDownObjectId() == null ? null : Math.toIntExact(configSingleChartDTO.getDrillDownObjectId()));

        return data;
    }

    /**
     * lay bieu do duong theo goi thue bao(chi tieu con) o dashboard level 2 (chart)
     *
     * @param dashboardRequestDTO
     * @param configSingleChartDTO
     * @param staffDTO
     * @return
     * @throws Exception
     * @author VuBL
     * @since 2019/11
     */
    @Override
    public ChartDTO getLineChartChildService(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChartDTO, StaffDTO staffDTO) throws Exception {
        StringBuilder logBuilder = new StringBuilder();
        logBuilder.append("*CHART(LINE_CHART LV2 - CARD2)");
        long startTime = System.currentTimeMillis();
        long stepTime = System.currentTimeMillis();
        ChartDTO data = new ChartDTO();
        List<SeriesDTO> vlstSeries = new ArrayList<>();
        int vintCycleId = dashboardRequestDTO.getCycleId();
        Unit unit = null;
        List<Long> vlstServiceId = chartRepoImpl.getChildServiceId(dashboardRequestDTO.getServiceId());
        //tat ca categories cua bieu do
        List<String> vlstAllCategories = chartRepoImpl.getCategories(dashboardRequestDTO.getPrdId(), Constants.LINE_VALUE.LEVEL_TWO, vintCycleId);
        //cac categories khong co du lieu trong db
        List<String> vlstAllCategoriesNotDb = chartRepoImpl.getCategories(dashboardRequestDTO.getPrdId(), Constants.LINE_VALUE.LEVEL_TWO, vintCycleId);
        //cac categories co du lieu trong db
        List<String> vlstCategoriesDb = new ArrayList<>();

        //set title
        if (configSingleChartDTO.getTitleI18n() != null) {
            if (vintCycleId == Constants.CYCLE_ID.DAY) {
                data.setTitle(I18N.get(configSingleChartDTO.getTitleI18n()) + I18N.get("linechart.cycle.day"));
            }
            if (vintCycleId == Constants.CYCLE_ID.MONTH) {
                data.setTitle(I18N.get(configSingleChartDTO.getTitleI18n()) + I18N.get("linechart.cycle.month"));
            }
            if (vintCycleId == Constants.CYCLE_ID.QUARTER) {
                data.setTitle(I18N.get(configSingleChartDTO.getTitleI18n()) + I18N.get("linechart.cycle.quarter"));
            }
        } else {
            data.setTitle(configSingleChartDTO.getTitle());
        }

        if (!DataUtil.isNullOrEmpty(vlstServiceId)) {
            for (int i = 0; i < vlstServiceId.size(); i++) {
                List<Object[]> vlstData = chartRepoCustom.findDataLChartChildService(dashboardRequestDTO, vlstServiceId.get(i));
                String vstrUnitType = null;
                Double vdblNewRate = null;
                Double vdblOldRate = null;

                //lay ra doi tuong unit chua don vi tinh va rate
                if (!DataUtil.isNullOrEmpty(vlstData)) {
                    Long vlngServiceId = vlstServiceId.get(i);
                    String vstrShopCode = String.valueOf(vlstData.get(0)[4]);
                    String vstrChannelCode = null;
                    if (!DataUtil.isNullObject(vlstData.get(0)[3])) {
                        vstrChannelCode = String.valueOf(vlstData.get(0)[3]);
                    }
                    unit = chartRepoImpl.getNewUnitType(vlngServiceId, vstrShopCode, vstrChannelCode);
//                    unit = chartRepoImpl.getUnitType(vlstServiceId.get(i), DataUtil.safeToString(vlstData.get(0)[3]), DataUtil.safeToString(vlstData.get(0)[5]), DataUtil.safeToString(vlstData.get(0)[4]), vintCycleId);
                }

                data.setType(Constants.CHARTTYPE.LINE_CHART);
                data.setChartSize(configSingleChartDTO.getChartSize());

                //lay ra don vi tinh va rate
                if (!DataUtil.isNullObject(unit)) {
                    vstrUnitType = unit.getName();
                    vdblNewRate = unit.getRate();
                    if (!DataUtil.isNullOrEmpty(vlstData)) {
                        String vstrCode = String.valueOf(vlstData.get(0)[5]);
                        vdblOldRate = chartRepoImpl.getOldRate(vstrCode);
                    }
                }

                //set Series va Points
                if (DataUtil.isNullOrEmpty(vlstData)) {
                    continue;
                } else {
                    SeriesDTO serie = new SeriesDTO();
                    List<PointDTO> vlstPoints = new ArrayList<>();

                    for (int j = 0; j < vlstData.size(); j++) {
                        //lay ra 1 list categories co du lieu trong db
                        vlstCategoriesDb.add(DataUtil.safeToString(vlstData.get(j)[0]));
                        Double vdblPerformValue = (Double) vlstData.get(j)[2];
                        if (!DataUtil.isNullOrZero(vdblPerformValue) && !DataUtil.isNullOrZero(vdblNewRate) && !DataUtil.isNullOrZero(vdblOldRate)) {
                            vdblPerformValue = vdblPerformValue * vdblOldRate / vdblNewRate;
                        }
                        if (vdblPerformValue != null) {
                            Double vdblValue = Double.valueOf(df.format(vdblPerformValue));
                            PointDTO dataPoint = new PointDTO();
                            dataPoint.setCategory(DataUtil.safeToString(vlstData.get(j)[0]));
                            if (vdblValue != null) {
                                dataPoint.setValue(vdblValue);
                            }
                            if (DataUtil.isNullOrEmpty(format.format(vdblValue))) {
                                dataPoint.setViewValue(null);
                            } else {
                                dataPoint.setViewValue(format.format(vdblValue) + " " + (!DataUtil.isNullOrEmpty(vstrUnitType) ? vstrUnitType : DataUtil.safeToString(vlstData.get(j)[6])));
                            }
                            vlstPoints.add(dataPoint);
                        } else {
                            PointDTO dataPoint = new PointDTO();
                            dataPoint.setCategory(DataUtil.safeToString(vlstData.get(j)[0]));
                            vlstPoints.add(dataPoint);
                        }
                    }

                    //categories ko co du lieu trong db = tat ca categories - categories co du lieu
                    vlstAllCategoriesNotDb.removeAll(vlstCategoriesDb);

                    //tao cac point ko co du lieu trong db
                    for (int k = 0; k < vlstAllCategoriesNotDb.size(); k++) {
                        PointDTO dataPoint = new PointDTO();
                        dataPoint.setCategory(vlstAllCategories.get(k));
                        vlstPoints.add(dataPoint);
                    }

                    //sap xep point theo thu tu categories tang dan
                    Collections.sort(vlstPoints, new Comparator<PointDTO>() {
                        @Override
                        public int compare(PointDTO o1, PointDTO o2) {
                            return o1.getCategory().compareTo(o2.getCategory());
                        }
                    });

                    if (DataUtil.isNullOrEmpty(vstrUnitType)) {
                        vstrUnitType = (String) vlstData.get(0)[6];
                    }

                    serie.setPoints(vlstPoints);
                    serie.setTitle(DataUtil.safeToString(vlstData.get(0)[1]));
                    serie.setAverage(false);
                    serie.setUnitType(vstrUnitType);
                    vlstSeries.add(serie);
                }

                //set du lieu bieu do
                data.setCategories(vlstAllCategories);
                data.setSeries(vlstSeries);
                if (configSingleChartDTO.getExpand() == 1) {
                    data.setExpand(true);
                } else {
                    data.setExpand(false);
                }
            }
        } else {
            data.setNoData(true);
        }

        logBuilder.append(" - TONG THOI GIAN " + ((System.currentTimeMillis() - startTime)) + "ms");
        LOGGER.info(logBuilder.toString());

        return data;
    }

    /**
     * lay du lieu bieu do chi tieu theo nam (dashboard level 2 chart)
     *
     * @param dashboardRequestDTO
     * @param configSingleChartDTO
     * @param staffDTO
     * @return
     * @throws Exception
     * @author VuBL
     * @since 2019/11
     */
    @Override
    public ChartDTO getLineChartServiceByYear(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChartDTO, StaffDTO staffDTO) throws Exception {
        ChartDTO data = new ChartDTO();
        List<SeriesDTO> vlstSeries = new ArrayList<>();
        List<String> vlstCategories = new ArrayList<>();
        Unit unit = null;
        Double vdblOldRate = null;
        Double vdblNewRate = null;
        //categories cua bieu do
        List<String> vlstAllCategories = new ArrayList<>();
        List<String> vlstYears = new ArrayList<>();
        String vstrUnitType = null;
        List<Object[]> vlstData = chartRepoCustom.findDataServiceByYear(dashboardRequestDTO, configSingleChartDTO, staffDTO);

        //lay don vi tinh mac dinh tu bang tong hop
        if (!DataUtil.isNullOrEmpty(vlstData)) {
            vstrUnitType = (String) vlstData.get(0)[7];
        }

        //lay cau hinh cua bieu do (line) tu metaData trong db
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = (JsonObject) jsonParser.parse(configSingleChartDTO.getMetaData());
        JsonArray varrLines = (JsonArray) jsonObject.get("lines");
        Type type = new TypeToken<ArrayList<LineDTO>>() {
        }.getType();
        List<LineDTO> vlstLines = new Gson().fromJson(varrLines.toString(), type);

        //lay ra cac nam cua bieu do
        Date vdtMaxDate = new Date(dashboardRequestDTO.getPrdId());
        Calendar cal = Calendar.getInstance();
        cal.setTime(vdtMaxDate);
        cal.add(Calendar.YEAR, -4);
        java.sql.Date vdtMinDate = new java.sql.Date((cal.getTime()).getTime());
        int vintMinYear = vdtMinDate.toLocalDate().getYear();
        int vintMaxYear = new java.sql.Date(dashboardRequestDTO.getPrdId()).toLocalDate().getYear();
        for (int i = vintMinYear; i <= vintMaxYear; i++) {
            vlstYears.add(String.valueOf("Năm " + i));
            vlstAllCategories.add(String.valueOf("Năm " + i));
        }

        //set noData
        if (!DataUtil.isNullOrEmpty(vlstData)) {
            data.setNoData(false);
        } else {
            data.setNoData(true);
        }

        //set du lieu
        data.setCategories(vlstAllCategories);
        data.setType(Constants.CHARTTYPE.LINE_CHART);
        data.setChartSize(configSingleChartDTO.getChartSize());
        data.setChartType(configSingleChartDTO.getChartType());

        if (configSingleChartDTO.getTitleI18n() != null) {
            data.setTitle(I18N.get(configSingleChartDTO.getTitleI18n()));
        } else {
            data.setTitle(configSingleChartDTO.getTitle());
        }

        //lay ra cac nam co du lieu trong db
        for (int i = 0; i < vlstData.size(); i++) {
            vlstCategories.add(DataUtil.safeToString(vlstData.get(i)[2]));
        }

        //loai bo nhung nam co du lieu trong db de set nhung nam ko co du lieu la null
        vlstYears.removeAll(vlstCategories);

        //lay don vi tinh
        if (!DataUtil.isNullOrEmpty(vlstData)) {
            Long vlngServiceId = DataUtil.safeToLong(vlstData.get(0)[4]);
            String vstrShopCode = String.valueOf(vlstData.get(0)[5]);
            String vstrChannelCode = null;
            if (!DataUtil.isNullObject(vlstData.get(0)[3])) {
                vstrChannelCode = String.valueOf(vlstData.get(0)[3]);
            }
            unit = chartRepoImpl.getNewUnitType(vlngServiceId, vstrShopCode, vstrChannelCode);
//            unit = chartRepoImpl.getUnitType(DataUtil.safeToLong(vlstData.get(0)[4]), DataUtil.safeToString(vlstData.get(0)[3]), DataUtil.safeToString(vlstData.get(0)[6]), DataUtil.safeToString(vlstData.get(0)[5]), 3);
        }
        if (!DataUtil.isNullObject(unit)) {
            vstrUnitType = unit.getName();
            vdblNewRate = unit.getRate();
            String vstrCode = String.valueOf(vlstData.get(0)[6]);
            vdblOldRate = chartRepoImpl.getOldRate(vstrCode);
        }

        //set Series va Point
        if (!DataUtil.isNullOrEmpty(vlstLines)) {
            for (int i = 0; i < vlstLines.size(); i++) {
                List<PointDTO> vlstPoints = new ArrayList<>();
                LineDTO line = vlstLines.get(i);
                SeriesDTO serie = new SeriesDTO();
                serie.setColor(line.getColor());
                serie.setAverage(Boolean.parseBoolean(line.getAverage()));
                serie.setChartType(configSingleChartDTO.getChartType());
                serie.setUnitType(vstrUnitType);

                for (int a = 0; a < vlstYears.size(); a++) {
                    PointDTO dataPoint = new PointDTO();
                    dataPoint.setCategory(vlstYears.get(a));
                    vlstPoints.add(dataPoint);
                }

                if (!DataUtil.isNullOrEmpty(vlstData)) {
                    //th
                    if (line.getValue().equals(Constants.LINE_VALUE.PERFORM)) {
                        serie.setTitle(I18N.get("note.chart.th"));
                        for (int k = 0; k < vlstData.size(); k++) {
                            Double vdblPerformValue = (Double) vlstData.get(k)[0];
                            if (!DataUtil.isNullOrZero(vdblPerformValue) && !DataUtil.isNullOrZero(vdblNewRate) && !DataUtil.isNullOrZero(vdblOldRate)) {
                                vdblPerformValue = vdblPerformValue * vdblOldRate / vdblNewRate;
                            }
                            if (vdblPerformValue != null) {
                                Double vdblValue = Double.valueOf(df.format(vdblPerformValue));
                                PointDTO dataPoint = new PointDTO();
                                dataPoint.setCategory(DataUtil.safeToString(vlstData.get(k)[2]));
                                if (vdblValue != null) {
                                    dataPoint.setValue(vdblValue);
                                }
                                if (DataUtil.isNullOrEmpty(format.format(vdblValue))) {
                                    dataPoint.setViewValue(null);
                                } else {
                                    dataPoint.setViewValue(format.format(vdblValue) + " " + (!DataUtil.isNullOrEmpty(vstrUnitType) ? vstrUnitType : DataUtil.safeToString(vlstData.get(k)[7])));
                                }
                                vlstPoints.add(dataPoint);
                            } else {
                                PointDTO dataPoint = new PointDTO();
                                dataPoint.setCategory(DataUtil.safeToString(vlstData.get(k)[2]));
                                vlstPoints.add(dataPoint);
                            }
                        }
                    }
                    //kh
                    if (line.getValue().equals(Constants.LINE_VALUE.SCHEDULE)) {
                        serie.setTitle(I18N.get("note.chart.kh"));
                        for (int k = 0; k < vlstData.size(); k++) {
                            Double vdblPerformValue = (Double) vlstData.get(k)[1];
                            if (!DataUtil.isNullOrZero(vdblPerformValue) && !DataUtil.isNullOrZero(vdblNewRate) && !DataUtil.isNullOrZero(vdblOldRate)) {
                                vdblPerformValue = vdblPerformValue * vdblOldRate / vdblNewRate;
                            }
                            if (vdblPerformValue != null) {
                                Double vdblValue = Double.valueOf(df.format(vdblPerformValue));
                                PointDTO dataPoint = new PointDTO();
                                dataPoint.setCategory(DataUtil.safeToString(vlstData.get(k)[2]));
                                if (vdblValue != null) {
                                    dataPoint.setValue(vdblValue);
                                }
                                if (DataUtil.isNullOrEmpty(format.format(vdblValue))) {
                                    dataPoint.setViewValue(null);
                                } else {
                                    dataPoint.setViewValue(format.format(vdblValue) + " " + (!DataUtil.isNullOrEmpty(vstrUnitType) ? vstrUnitType : DataUtil.safeToString(vlstData.get(k)[7])));
                                }
                                vlstPoints.add(dataPoint);
                            } else {
                                PointDTO dataPoint = new PointDTO();
                                dataPoint.setCategory(DataUtil.safeToString(vlstData.get(k)[2]));
                                vlstPoints.add(dataPoint);
                            }
                        }
                    }
                }

                //sap xep list point theo thu tu tang dan cua category
                Collections.sort(vlstPoints, new Comparator<PointDTO>() {
                    @Override
                    public int compare(PointDTO o1, PointDTO o2) {
                        return o1.getCategory().compareTo(o2.getCategory());
                    }
                });
                serie.setPoints(vlstPoints);
                vlstSeries.add(serie);
            }
        }
        data.setSeries(vlstSeries);

        return data;
    }

    /**
     * bieu do cot 5 don vị tot nhat va kem nhat o dashboard level 2 kenh tinh (chart)
     *
     * @param dashboardRequestDTO
     * @param configSingleChartDTO
     * @param staffDTO
     * @return
     * @throws Exception
     * @author VuBL
     * @since 2019/11
     */
    @Override
    public ChartDTO getColChartChildShopSort(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChartDTO, StaffDTO staffDTO) throws Exception {
        ChartDTO data = new ChartDTO();
        List<SeriesDTO> vlstSeries = new ArrayList<>();
        SeriesDTO serie = new SeriesDTO();
        List<PointDTO> vlstPoints = new ArrayList<>();
        List<String> vlstCategories = new ArrayList<>();
        Unit unit;
        Boolean vblnCheckUnitType = false;
        List<String> vlstUnitTypes = new ArrayList<>();
        List<String> idNoPlanDaily = apParamRepo.findCodeByType(Constants.NO_PLAN_DAILY);
        //truong hop tat ca chi co 5 don vi tro xuong
        List<Object[]> vlstData = chartRepoCustom.findDataChildShop(dashboardRequestDTO, staffDTO, configSingleChartDTO, null);
        //truong hop co nhieu hon 5 don vi
        if (vlstData.size() > 5) {
            vlstData.clear();
            List<Object[]> vlstDataMax = chartRepoCustom.findDataChildShop(dashboardRequestDTO, staffDTO, configSingleChartDTO, Constants.MAX);
            List<Object[]> vlstDataMin = chartRepoCustom.findDataChildShop(dashboardRequestDTO, staffDTO, configSingleChartDTO, Constants.MIN);
            vlstData.addAll(vlstDataMax);
            vlstData.addAll(vlstDataMin);
        }

        //lay cau hinh cua bieu do (line) tu metaData trong db
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = (JsonObject) jsonParser.parse(configSingleChartDTO.getMetaData());
        JsonArray varrLines = (JsonArray) jsonObject.get("lines");
        Type type = new TypeToken<ArrayList<LineDTO>>() {
        }.getType();
        List<LineDTO> vlstLines = new Gson().fromJson(varrLines.toString(), type);
        LineDTO line = vlstLines.get(0);

        //set du lieu bieu do
        data.setType(Constants.CHARTTYPE.COLUMN_CHART);
        data.setChartSize(configSingleChartDTO.getChartSize());
        data.setChartType(configSingleChartDTO.getChartType());
        if (configSingleChartDTO.getTitleI18n() != null) {
            data.setTitle(I18N.get(configSingleChartDTO.getTitleI18n()));
        } else {
            data.setTitle(configSingleChartDTO.getTitle());
        }
        if (configSingleChartDTO.getExpand() == 1) {
            data.setExpand(true);
        } else {
            data.setExpand(false);
        }
        for (int i = 0; i < vlstData.size(); i++) {
            vlstCategories.add(DataUtil.safeToString(vlstData.get(i)[0]));
        }
        data.setCategories(vlstCategories);

        //set noData
        if (!DataUtil.isNullOrEmpty(vlstData)) {
            data.setNoData(false);
        } else {
            data.setNoData(true);
        }

        //set series
        serie.setColor(line.getColor());
        serie.setAverage(Boolean.parseBoolean(line.getAverage()));
        serie.setChartType(configSingleChartDTO.getChartType());

        //set Point
        for (int i = 0; i < vlstData.size(); i++) {
//            Double vdblOldRate = null;
//            Double vdblNewRate = null;
//            String vstrUnitType;
            //don vi tinh mac dinh
//            vstrUnitType = (String) vlstData.get(i)[10];
            //lay don vi tinh
            Long vlngServiceId = dashboardRequestDTO.getServiceId();
            String vstrShopCode = String.valueOf(vlstData.get(i)[5]);
            String vstrChannelCode = null;
            if (!DataUtil.isNullObject(vlstData.get(i)[4])) {
                vstrChannelCode = String.valueOf(vlstData.get(i)[4]);
            }
            unit = chartRepoImpl.getNewUnitType(vlngServiceId, vstrShopCode, vstrChannelCode);
            //lay du lieu tinh ti le don vi tinh
//            if (!DataUtil.isNullObject(unit)) {
//                vstrUnitType = unit.getName();
//                vdblNewRate = unit.getRate();
//                vdblOldRate = chartRepoImpl.getOldRate(DataUtil.safeToString(vlstData.get(i)[6]));
//            }
//            vlstUnitTypes.add(vstrUnitType);

            Double vdblPerformValue;
            Long vstrServiceId = DataUtil.safeToLong(vlstData.get(i)[8]);
            if (!DataUtil.isNullOrEmpty(idNoPlanDaily) && idNoPlanDaily.contains(vstrServiceId.toString())) {
                vdblPerformValue = (Double) vlstData.get(i)[11];
            } else {
                vdblPerformValue = (Double) vlstData.get(i)[12];
            }
//            Double vdblPerformValue = (Double) vlstData.get(i)[1];
//            if (!DataUtil.isNullOrZero(vdblPerformValue) && !DataUtil.isNullOrZero(vdblNewRate) && !DataUtil.isNullOrZero(vdblOldRate)) {
//                vdblPerformValue = vdblPerformValue * vdblOldRate / vdblNewRate;
//            }
            if (vdblPerformValue != null) {
                Double vdblValue = Double.valueOf(df.format(vdblPerformValue));
                PointDTO dataPoint = new PointDTO();
                dataPoint.setCategory(DataUtil.safeToString(vlstData.get(i)[0]));
                if (vdblValue != null) {
                    dataPoint.setValue(vdblValue);
                }
                if (DataUtil.isNullOrEmpty(format.format(vdblValue))) {
                    dataPoint.setViewValue(null);
                } else {
                    dataPoint.setViewValue(format.format(vdblValue) + "%");
                }
                vlstPoints.add(dataPoint);
            } else {
                PointDTO dataPoint = new PointDTO();
                dataPoint.setCategory(DataUtil.safeToString(vlstData.get(i)[0]));
                vlstPoints.add(dataPoint);
            }
        }
        serie.setPoints(vlstPoints);

        //check don vi tinh chung
        if (!DataUtil.isNullOrEmpty(vlstUnitTypes)) {
            if (vlstUnitTypes.size() == 1) {
                vblnCheckUnitType = true;
            } else {
                for (int i = 0; i < vlstUnitTypes.size(); i++) {
                    if (i == vlstUnitTypes.size() - 1) {
                        break;
                    } else {
                        if (vlstUnitTypes.get(i) != null && vlstUnitTypes.get(i + 1) != null && vlstUnitTypes.get(i).equals(vlstUnitTypes.get(i + 1))) {
                            vblnCheckUnitType = true;
                        } else {
                            vblnCheckUnitType = false;
                            break;
                        }
                    }
                }
            }
        }
//        if (vblnCheckUnitType == true) {
//            serie.setUnitType(vlstUnitTypes.get(0));
//        }
        serie.setUnitType(Constants.PARAM_PERCENT);

        vlstSeries.add(serie);
        data.setSeries(vlstSeries);

        return data;
    }

    /**
     * lay ra bieu do xep hang 5 don vi tot nhat, kem nhat khi zoom
     *
     * @param dashboardRequestDTO
     * @param configSingleChartDTO
     * @param staffDTO
     * @return
     * @throws Exception
     * @author VuBL
     * @since 2019/12
     */
    @Override
    public ChartDTO getColChartRankUnitZoom(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChartDTO, StaffDTO staffDTO, Boolean pblnZoom) throws Exception {
        ChartDTO data = new ChartDTO();
        List<SeriesDTO> vlstSeries = new ArrayList<>();
        SeriesDTO serie = new SeriesDTO();
        List<PointDTO> vlstPoints = new ArrayList<>();
        List<String> vlstCategories = new ArrayList<>();
        Unit unit = null;
        Boolean vblnCheckUnitType = false;
        List<String> vlstUnitTypes = new ArrayList<>();
        List<String> idNoPlanDaily = apParamRepo.findCodeByType(Constants.NO_PLAN_DAILY);
        List<Object[]> vlstData = chartRepoCustom.findDataChildShop(dashboardRequestDTO, staffDTO, configSingleChartDTO, Constants.REDUCTION);

        //lay cau hinh cua bieu do (line) tu metaData trong db
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = (JsonObject) jsonParser.parse(configSingleChartDTO.getMetaData());
        JsonArray varrLines = (JsonArray) jsonObject.get("lines");
        Type type = new TypeToken<ArrayList<LineDTO>>() {
        }.getType();
        List<LineDTO> vlstLines = new Gson().fromJson(varrLines.toString(), type);
        LineDTO line = vlstLines.get(0);

        //set du lieu cua bieu do
        data.setType(Constants.CHARTTYPE.COLUMN_CHART);
        data.setChartSize(configSingleChartDTO.getChartSize());
        data.setChartType(configSingleChartDTO.getChartType());
        if (configSingleChartDTO.getExpand() == 1) {
            data.setExpand(true);
        } else {
            data.setExpand(false);
        }

        if (configSingleChartDTO.getTitleI18n() != null) {
            data.setTitle(I18N.get(configSingleChartDTO.getTitleI18n()));
        } else {
            data.setTitle(configSingleChartDTO.getTitle());
        }
        for (int i = 0; i < vlstData.size(); i++) {
            vlstCategories.add(DataUtil.safeToString(vlstData.get(i)[0]));
            if (pblnZoom == true) {
                if (vlstCategories.size() == 5) break;
            }
        }
        data.setCategories(vlstCategories);

        //set noData
        if (!DataUtil.isNullOrEmpty(vlstData)) {
            data.setNoData(false);
        } else {
            data.setNoData(true);
        }

        //set series
        serie.setColor(line.getColor());
        serie.setAverage(Boolean.parseBoolean(line.getAverage()));
        serie.setChartType(configSingleChartDTO.getChartType());

        //set point
        for (int i = 0; i < vlstData.size(); i++) {
//            Double vdblOldRate = null;
//            Double vdblNewRate = null;
//            String vstrUnitType;
            //don vi tinh mac dinh
//            vstrUnitType = (String) vlstData.get(i)[10];
            //lay don vi tinh
            if (!DataUtil.isNullOrEmpty(vlstData)) {
                Long vlngServiceId = dashboardRequestDTO.getServiceId();
                String vstrShopCode = DataUtil.safeToString(vlstData.get(i)[5]);
                String vstrChannelCode = null;
                if (!DataUtil.isNullObject(vlstData.get(i)[4])) {
                    vstrChannelCode = String.valueOf(vlstData.get(i)[4]);
                }
                unit = chartRepoImpl.getNewUnitType(vlngServiceId, vstrShopCode, vstrChannelCode);
//            unit = chartRepoImpl.getUnitType(dashboardRequestDTO.getServiceId(), DataUtil.safeToString(vlstData.get(0)[4]), DataUtil.safeToString(vlstData.get(0)[6]), DataUtil.safeToString(vlstData.get(0)[5]), vintCycleId);
            }
            //tinh ti le don vi tinh
//            if (!DataUtil.isNullObject(unit)) {
//                vstrUnitType = unit.getName();
//                vdblNewRate = unit.getRate();
//                String vstrCode = String.valueOf(vlstData.get(i)[6]);
//                vdblOldRate = chartRepoImpl.getOldRate(vstrCode);
//            }
//            vlstUnitTypes.add(vstrUnitType);

            Double vdblPerformValue;
            Long vstrServiceId = DataUtil.safeToLong(vlstData.get(i)[8]);
            if (!DataUtil.isNullOrEmpty(idNoPlanDaily) && idNoPlanDaily.contains(vstrServiceId.toString())) {
                vdblPerformValue = (Double) vlstData.get(i)[11];
            } else {
                vdblPerformValue = (Double) vlstData.get(i)[12];
            }
//            Double vdblPerformValue = (Double) vlstData.get(i)[1];
//            if (!DataUtil.isNullOrZero(vdblPerformValue) && !DataUtil.isNullOrZero(vdblNewRate) && !DataUtil.isNullOrZero(vdblOldRate)) {
//                vdblPerformValue = vdblPerformValue * vdblOldRate / vdblNewRate;
//            }
            if (vdblPerformValue != null) {
                Double vdblValue = Double.valueOf(df.format(vdblPerformValue));
                PointDTO dataPoint = new PointDTO();
                dataPoint.setCategory(DataUtil.safeToString(vlstData.get(i)[0]));
                if (vdblValue != null) {
                    dataPoint.setValue(vdblValue);
                }
                if (DataUtil.isNullOrEmpty(format.format(vdblValue))) {
                    dataPoint.setViewValue(null);
                } else {
                    dataPoint.setViewValue(format.format(vdblValue) + "%");
                }
                vlstPoints.add(dataPoint);
                if (pblnZoom == true) {
                    if (vlstPoints.size() == 5) break;
                }
            } else {
                PointDTO dataPoint = new PointDTO();
                dataPoint.setCategory(DataUtil.safeToString(vlstData.get(i)[0]));
                vlstPoints.add(dataPoint);
                if (pblnZoom == true) {
                    if (vlstPoints.size() == 5) break;
                }
            }
        }
        serie.setPoints(vlstPoints);

        //check don vi tinh chung
        if (!DataUtil.isNullOrEmpty(vlstUnitTypes)) {
            if (vlstUnitTypes.size() == 1) {
                vblnCheckUnitType = true;
            } else {
                for (int i = 0; i < vlstUnitTypes.size(); i++) {
                    if (i == vlstUnitTypes.size() - 1) {
                        break;
                    } else {
                        if (vlstUnitTypes.get(i) != null && vlstUnitTypes.get(i + 1) != null && vlstUnitTypes.get(i).equals(vlstUnitTypes.get(i + 1))) {
                            vblnCheckUnitType = true;
                        } else {
                            vblnCheckUnitType = false;
                            break;
                        }
                    }
                }
            }
        }
//        if (vblnCheckUnitType == true) {
//            serie.setUnitType(vlstUnitTypes.get(0));
//        }
        serie.setUnitType(Constants.PARAM_PERCENT);

        vlstSeries.add(serie);
        data.setSeries(vlstSeries);

        return data;
    }

    /**
     * bieu do duong theo chi tieu con lv2 chart (new 02/06/2020)
     *
     * @param dashboardRequestDTO
     * @param configSingleChartDTO
     * @return
     * @throws Exception
     * @author VuBL
     */
    @Override
    public ChartDTO getLChartChildServiceNew(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChartDTO) throws Exception {
        ChartDTO data = new ChartDTO();
        List<SeriesDTO> vlstSeries = new ArrayList<>();
        int vintCycleId = dashboardRequestDTO.getCycleId();
        int vintNumberService = 0;
        List<Object[]> vlstServices = serviceRepo.findChildServiceNew(dashboardRequestDTO.getServiceId());
        List<Object[]> vlstObjs = chartRepoCustom.findDataLChartServiceNew(dashboardRequestDTO, configSingleChartDTO, vlstServices);
        //tat ca categories cua bieu do
        List<String> vlstAllCategories = chartRepoImpl.getCategories(dashboardRequestDTO.getPrdId(), Constants.LINE_VALUE.LEVEL_TWO, vintCycleId);
        //cac categories khong co du lieu trong db
        List<String> vlstAllCategoriesNotDb = chartRepoImpl.getCategories(dashboardRequestDTO.getPrdId(), Constants.LINE_VALUE.LEVEL_TWO, vintCycleId);
        //cac categories co du lieu trong db
        List<String> vlstCategoriesDb = new ArrayList<>();

        //set title
        if (configSingleChartDTO.getTitleI18n() != null) {
            if (vintCycleId == Constants.CYCLE_ID.DAY) {
                data.setTitle(I18N.get(configSingleChartDTO.getTitleI18n()) + I18N.get("linechart.cycle.day"));
            }
            if (vintCycleId == Constants.CYCLE_ID.MONTH) {
                data.setTitle(I18N.get(configSingleChartDTO.getTitleI18n()) + I18N.get("linechart.cycle.month"));
            }
            if (vintCycleId == Constants.CYCLE_ID.QUARTER) {
                data.setTitle(I18N.get(configSingleChartDTO.getTitleI18n()) + I18N.get("linechart.cycle.quarter"));
            }
        } else {
            data.setTitle(configSingleChartDTO.getTitle());
        }

        if (vlstObjs != null && !vlstObjs.isEmpty()) {
            //lay ra list point co du lieu
            for (Object[] obj : vlstObjs) {
//                vlstCategoriesDb.add(DataUtil.convertPrdToString(DataUtil.safeToLong(obj[0])));
                vlstCategoriesDb.add((String) obj[0]);
            }


            //set du lieu cho chart
            for (int j = 5; j < vlstServices.size() + 5; j++) {
                SeriesDTO serie = new SeriesDTO();
                List<PointDTO> vlstPoints = new ArrayList<>();

                for (int i = 0; i < vlstObjs.size(); i++) {
                    //ten dvt
                    String vstrDvt = (String) vlstObjs.get(i)[1];
                    String vstrDvtNew = (String) vlstObjs.get(i)[3];
                    String vstrDvtUse = null;
                    if (vstrDvtNew != null) {
                        vstrDvtUse = vstrDvtNew;
                    } else {
                        if (vstrDvt != null) {
                            vstrDvtUse = vstrDvt;
                        }
                    }
                    //rate
                    Integer vintOldRate = null;
                    Integer vintNewRate = null;
                    if (vlstObjs.get(i)[2] != null) {
                        vintOldRate = DataUtil.safeToInt(vlstObjs.get(i)[2]);
                    }
                    if (vlstObjs.get(i)[4] != null) {
                        vintNewRate = DataUtil.safeToInt(vlstObjs.get(i)[4]);
                    }
                    //du lieu
                    Double vdblPerformValue = (Double) vlstObjs.get(i)[j];
                    if (!DataUtil.isNullOrZero(vdblPerformValue) && !DataUtil.isNullOrZero(vintNewRate) && !DataUtil.isNullOrZero(vintOldRate)) {
                        vdblPerformValue = vdblPerformValue * vintOldRate / vintNewRate;
                    }
                    Double vdblValue = null;
                    if (vdblPerformValue != null) {
                        vdblValue = Double.valueOf(df.format(vdblPerformValue));
                    }
                    PointDTO dataPoint = new PointDTO();
                    dataPoint.setCategory((String) vlstObjs.get(i)[0]);
                    if (vdblValue != null) {
                        dataPoint.setValue(vdblValue);
                    }
                    if (vdblValue == null) {
                        dataPoint.setViewValue(null);
                    } else {
                        dataPoint.setViewValue(format.format(vdblValue) + " " + (vstrDvtUse == null ? "" : vstrDvtUse));
                    }
                    vlstPoints.add(dataPoint);
                }

                vlstAllCategoriesNotDb.removeAll(vlstCategoriesDb);

                //tao cac point ko co du lieu trong db
                for (int k = 0; k < vlstAllCategoriesNotDb.size(); k++) {
                    PointDTO dataPoint = new PointDTO();
                    dataPoint.setCategory(vlstAllCategoriesNotDb.get(k));
                    vlstPoints.add(dataPoint);
                }

                //sap xep point theo thu tu categories tang dan
                Collections.sort(vlstPoints, new Comparator<PointDTO>() {
                    @Override
                    public int compare(PointDTO o1, PointDTO o2) {
                        return o1.getCategory().compareTo(o2.getCategory());
                    }
                });

                String vstrUnitType = (String) vlstObjs.get(0)[1];
                serie.setPoints(vlstPoints);
                serie.setTitle(DataUtil.safeToString(vlstServices.get(vintNumberService)[1]));
                serie.setAverage(false);
                serie.setUnitType(vstrUnitType);
                vlstSeries.add(serie);
                vintNumberService = vintNumberService + 1;
            }

            //set du lieu bieu do
            data.setType(Constants.CHARTTYPE.LINE_CHART);
            data.setChartSize(configSingleChartDTO.getChartSize());
            data.setCategories(vlstAllCategories);
            data.setSeries(vlstSeries);
            if (configSingleChartDTO.getExpand() == 1) {
                data.setExpand(true);
            } else {
                data.setExpand(false);
            }

        } else {
            data.setNoData(true);
            data.setType(Constants.CHARTTYPE.LINE_CHART);
            data.setChartSize(configSingleChartDTO.getChartSize());
        }

        return data;
    }

    /**
     * level 3 chart level 2 card 2 (table)
     *
     * @param dashboardRequestDTO
     * @return
     * @throws Exception
     * @author VuBL
     * @since 2020/07
     */
    @Override
    public TableDTO getUnitStaffByService(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChartDTO) throws Exception {
        TableDTO tableDTO = new TableDTO();
        List<TableColumnDTO> tableColumnDTOs = new ArrayList<>();
        List<TableRowDTO> tableRowDTOs = new ArrayList<>();
        List<Object[]> vlstDatas = chartRepoImpl.findDataUnitStaffByService(dashboardRequestDTO);
        int vintCycleId = dashboardRequestDTO.getCycleId();
        Long vlngServiceId = dashboardRequestDTO.getServiceId();
        List<String> idReverse = apParamRepo.findCodeByType(Constants.REVERSE_SERVICE);
        int vintColumnId = 0;
        int vintRowId = 0;
        int vintCellId = 0;

        //tao va set column cho table
        TableColumnDTO column1 = new TableColumnDTO();
        column1.setAlign(Constants.CODE_WARNING.LEFT);
        column1.setColumnId(vintColumnId);
        column1.setType(Constants.CODE_WARNING.TEXT);
        column1.setName(I18N.get(Constants.CODE_WARNING.UNIT_STAFF));
        tableColumnDTOs.add(column1);

        TableColumnDTO column2 = new TableColumnDTO();
        column2.setAlign(Constants.CODE_WARNING.RIGHT);
        column2.setColumnId(++vintColumnId);
        column2.setType(Constants.CODE_WARNING.TEXT);
        column2.setName(I18N.get(Constants.ACCUMULATED_PERFORM));
        tableColumnDTOs.add(column2);

        TableColumnDTO column3 = new TableColumnDTO();
        column3.setAlign(Constants.CENTER);
        column3.setColumnId(++vintColumnId);
        column3.setType(Constants.CODE_WARNING.TEXT);
        column3.setName(I18N.get(Constants.UP_DOWN));
        tableColumnDTOs.add(column3);

        //tao va set row cho table
        if (!vlstDatas.isEmpty() && vlstDatas != null) {
            for (Object[] obj : vlstDatas) {
                TableRowDTO row = new TableRowDTO();
                List<TableCellBaseDTO> cells = new ArrayList<>();
                String vstrDvt = DataUtil.safeToString(obj[4]);
                Integer vdblOldRate = DataUtil.safeToInt(obj[5]);
                String vstrNewDVT = DataUtil.safeToString(obj[6]);
                Integer vdblNewRate = DataUtil.safeToInt(obj[7]);
                Double vdblAccumulatedPerform = (Double) obj[2];
                Double vdblAccumulatedPerformN1 = (Double) obj[3];
                String vstrShopCode = (String) obj[8];
                String vstrStaffCode = (String) obj[9];
                String vstrDvtUse = null;

                //ten dvt
                if (!DataUtil.isNullOrEmpty(vstrNewDVT)) {
                    vstrDvtUse = vstrNewDVT;
                } else {
                    if (!DataUtil.isNullOrEmpty(vstrDvt)) {
                        vstrDvtUse = vstrDvt;
                    }
                }

                //cell don vi/nhan vien
                TableCellBaseDTO cell1 = new TableCellBaseDTO();
                cell1.setColor(Constants.COLORS.BLACK);
                cell1.setColumnId(vintCellId);
                if (obj[0] == null) {
                    if (vstrDvtUse == null) {
                        cell1.setViewValue((String) obj[1]);
                    } else {
                        cell1.setViewValue(obj[1] + " (" + vstrDvtUse + ")");
                    }
                } else {
                    if (vstrDvtUse == null) {
                        cell1.setViewValue(obj[1] + " - " + obj[0]);
                    } else {
                        cell1.setViewValue(obj[1] + " - " + obj[0] + " (" + vstrDvtUse + ")");
                    }
                }
                cells.add(cell1);

                //cell TH den ngay
                TableCellBaseDTO cell2 = new TableCellBaseDTO();
                cell2.setColor(Constants.COLORS.BLACK);
                cell2.setColumnId(++vintCellId);
                if (vdblAccumulatedPerform != null && !DataUtil.isNullOrZero(vdblNewRate) && !DataUtil.isNullOrZero(vdblOldRate)) {
                    Double vdblValue = Double.valueOf(df.format(vdblAccumulatedPerform * vdblOldRate / vdblNewRate));
                    cell2.setViewValue(format.format(vdblValue));
                } else if (vdblAccumulatedPerform != null) {
                    Double vdblValue = Double.valueOf(df.format(vdblAccumulatedPerform));
                    cell2.setViewValue(format.format(vdblValue));
                }
                cells.add(cell2);

                //cell +/-
                TableCellBaseDTO cell3 = new TableCellBaseDTO();
//                cell3.setColumnId(++vintCellId);
                if (vintCycleId == Constants.CYCLE_ID.DAY) {
                    TableCellGrowthDTO cellGrowthDTO = tableService.setGrowth(vlngServiceId, vdblAccumulatedPerform, vdblAccumulatedPerformN1, ++vintCellId, vdblOldRate, vdblNewRate, idReverse);
                    cells.add(cellGrowthDTO);
                }
                if (vintCycleId == Constants.CYCLE_ID.MONTH || vintCycleId == Constants.CYCLE_ID.QUARTER) {
                    TableCellGrowthDTO cellGrowthDTO = tableService.setGrowth(vlngServiceId, vdblAccumulatedPerform, vdblAccumulatedPerformN1, ++vintCellId, vdblOldRate, vdblNewRate, idReverse);
                    cells.add(cellGrowthDTO);
                }

                vintCellId = 0;
                row.setCells(cells);
                row.setRowIndex(vintRowId);
                row.setClicked(true);
                if (vstrStaffCode != null) {
                    row.setClicked(false);
                }
                row.setShopCode(vstrShopCode);
                tableRowDTOs.add(row);
                vintRowId++;
            }
        }

        //set du lieu cho table
        tableDTO.setType(configSingleChartDTO.getChartType().trim());
        tableDTO.setColumns(tableColumnDTOs);
        tableDTO.setRows(tableRowDTOs);

        return tableDTO;
    }

    /**
     * lay ra du lieu bang (List<TableCellBaseDTO>) cua table theo don vi dashboard level (chart)
     *
     * @param plstColumns
     * @param pObj
     * @return
     * @author VuBL
     * @since 2019/11
     */
    private List<TableCellBaseDTO> setCellChildShop(String pstrTrend, List<TableColumnDTO> plstColumns, Object[] pObj, int pintCycleId, DashboardRequestDTO dashboardRequestDTO, int pintChartId, List<String> idReverse) throws Exception {
        int vintIndex = 0;
        List<TableCellBaseDTO> vlstTableCellBaseDTOS = new ArrayList<>();
        String vstrUnitName = DataUtil.safeToString(pObj[0]);
        Double vdblPerform = (Double) pObj[1];
        Double vdblSchedule = (Double) pObj[2];
        Double vdblPerformBefore = (Double) pObj[3];
        String vstrChannelCode = DataUtil.safeToString(pObj[4]);
        String vstrShopCode = DataUtil.safeToString(pObj[5]);
        String vstrCodeDvt = DataUtil.safeToString(pObj[6]);
        Double vdblSameperiod = (Double) pObj[7];
        Long vlngServiceId = DataUtil.safeToLong(pObj[8]);
        String vstrDvt = DataUtil.safeToString(pObj[10]);
        Integer vdblOldRate = DataUtil.safeToInt(pObj[14]);
        String vstrNewDVT = DataUtil.safeToString(pObj[15]);
        Integer vdblNewRate = DataUtil.safeToInt(pObj[16]);
        Double vdblPerformOne = (Double) pObj[17];
        Double vdblScheduleOne = (Double) pObj[18];
        Double vdblSameperiodOne = (Double) pObj[19];

        //ten dvt
        String vstrDvtUse = null;
        if (!DataUtil.isNullOrEmpty(vstrNewDVT)) {
            vstrDvtUse = vstrNewDVT;
        } else {
            if (!DataUtil.isNullOrEmpty(vstrDvt)) {
                vstrDvtUse = vstrDvt;
            }
        }

        for (int i = 0; i < plstColumns.size(); i++) {
            if (Constants.TEXT.equals(plstColumns.get(i).getType())) {
                TableCellBaseDTO cellBaseDTO = new TableCellBaseDTO();
                cellBaseDTO.setColumnId(vintIndex++);
                if (Constants.SHOP_NAME.equals(plstColumns.get(i).getValue())) {
                    if (DataUtil.isNullOrEmpty(vstrDvtUse)) {
                        cellBaseDTO.setViewValue(vstrUnitName);
                    } else {
                        cellBaseDTO.setViewValue(vstrUnitName + " (" + vstrDvtUse + ")");
                    }
                    cellBaseDTO.setColor(Constants.COLORS.BLACK);
                }
                if (Constants.SCHEDULE_ONE.equals(plstColumns.get(i).getValue())) {
                    if (vdblScheduleOne != null && !DataUtil.isNullOrZero(vdblNewRate) && !DataUtil.isNullOrZero(vdblOldRate)) {
                        Double vdblValue = Double.valueOf(df.format(vdblScheduleOne * vdblOldRate / vdblNewRate));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    } else if (vdblScheduleOne != null) {
                        Double vdblValue = Double.valueOf(df.format(vdblScheduleOne));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    }
                    cellBaseDTO.setColor(Constants.COLORS.BLACK);
                }
                if (Constants.SCHEDULE.equals(plstColumns.get(i).getValue())) {
                    if (vdblSchedule != null && !DataUtil.isNullOrZero(vdblNewRate) && !DataUtil.isNullOrZero(vdblOldRate)) {
                        Double vdblValue = Double.valueOf(df.format(vdblSchedule * vdblOldRate / vdblNewRate));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    } else if (vdblSchedule != null) {
                        Double vdblValue = Double.valueOf(df.format(vdblSchedule));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    }
                    cellBaseDTO.setColor(Constants.COLORS.BLACK);
                }
                if (Constants.PERFORM_N1.equals(plstColumns.get(i).getValue())) {
                    if (vdblPerformBefore != null && !DataUtil.isNullOrZero(vdblNewRate) && !DataUtil.isNullOrZero(vdblOldRate)) {
                        Double vdblValue = Double.valueOf(df.format(vdblPerformBefore * vdblOldRate / vdblNewRate));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    } else if (vdblPerformBefore != null) {
                        Double vdblValue = Double.valueOf(df.format(vdblPerformBefore));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    }
                    cellBaseDTO.setColor(Constants.COLORS.BLACK);
                }
                if (Constants.PERFORM_ONE.equals(plstColumns.get(i).getValue())) {
                    if (vdblPerformOne != null && !DataUtil.isNullOrZero(vdblNewRate) && !DataUtil.isNullOrZero(vdblOldRate)) {
                        Double vdblValue = Double.valueOf(df.format(vdblPerformOne * vdblOldRate / vdblNewRate));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    } else if (vdblPerformOne != null) {
                        Double vdblValue = Double.valueOf(df.format(vdblPerformOne));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    }
                    cellBaseDTO.setColor(Constants.COLORS.BLACK);
                }
                if (Constants.PERFORM.equals(plstColumns.get(i).getValue())) {
                    if (vdblPerform != null && !DataUtil.isNullOrZero(vdblNewRate) && !DataUtil.isNullOrZero(vdblOldRate)) {
                        Double vdblValue = Double.valueOf(df.format(vdblPerform * vdblOldRate / vdblNewRate));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    } else if (vdblPerform != null) {
                        Double vdblValue = Double.valueOf(df.format(vdblPerform));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    }
                    cellBaseDTO.setColor(Constants.COLORS.BLACK);
                }
                if (Constants.COMPLETE_ONE.equals(plstColumns.get(i).getValue())) {
                    if (vdblSameperiodOne == null) {
                        cellBaseDTO.setViewValue("");
                    } else {
                        cellBaseDTO.setViewValue(df.format(vdblSameperiodOne) + "%");
                    }
                    cellBaseDTO.setColor(Constants.COLORS.BLACK);
                }
                if (Constants.COMPLETE.equals(plstColumns.get(i).getValue())) {
                    if (vdblSameperiod == null) {
                        cellBaseDTO.setViewValue("");
                    } else {
                        cellBaseDTO.setViewValue(df.format(vdblSameperiod) + "%");
                    }
//                    if (vdblPerform == null) {
//                        vdblSameperiod = 0d;
//                        cellBaseDTO.setViewValue(df.format(vdblSameperiod) + "%");
//                    } else if (vdblSchedule == null) {
//                        cellBaseDTO.setViewValue(null);
//                    } else {
//                        Double vdblValue = Double.valueOf(df.format(vdblSameperiod));
//                        cellBaseDTO.setViewValue(format.format(vdblValue) + "%");
//                    }
                    cellBaseDTO.setColor(Constants.COLORS.BLACK);
                }
                vlstTableCellBaseDTOS.add(cellBaseDTO);
            }

            if (Constants.CHART.equals(plstColumns.get(i).getType())) {
                JsonArray lines = plstColumns.get(i).getMetaData();
                TableCellChartDTO cellChartDTO = tableService.setChartTableChildShop(pstrTrend, lines, vintIndex++, pintChartId, dashboardRequestDTO);
                vlstTableCellBaseDTOS.add(cellChartDTO);
            }

            if (pintCycleId == Constants.CYCLE_ID.DAY) {
                if (Constants.GROWTH_MONTH.equals(plstColumns.get(i).getType())) {
                    TableCellGrowthDTO cellGrowthDTO = tableService.setGrowth(vlngServiceId, vdblPerform, vdblPerformBefore, vintIndex++, vdblOldRate, vdblNewRate, idReverse);
                    vlstTableCellBaseDTOS.add(cellGrowthDTO);
                }
            }

            if (pintCycleId == Constants.CYCLE_ID.MONTH || pintCycleId == Constants.CYCLE_ID.QUARTER) {
                if (Constants.GROWTH_YEAR.equals(plstColumns.get(i).getType())) {
                    TableCellGrowthDTO cellGrowthDTO = tableService.setGrowth(vlngServiceId, vdblPerform, vdblPerformBefore, vintIndex++, vdblOldRate, vdblNewRate, idReverse);
                    vlstTableCellBaseDTOS.add(cellGrowthDTO);
                }
            }
        }

        return vlstTableCellBaseDTOS;
    }

    /**
     * lay ra du lieu bang (List<TableCellBaseDTO>) cua table theo goi thue bao(chi tieu con) o dashboard level 2 (chart)
     *
     * @param plstColumns
     * @param pObj
     * @param pintCycleId
     * @param dashboardRequestDTO
     * @param pintChartId
     * @param staffDTO
     * @return
     * @throws Exception
     * @author VuBL
     * @since 2019/11
     */
    private List<TableCellBaseDTO> setCellChildService(List<TableColumnDTO> plstColumns, Object[] pObj, int pintCycleId, DashboardRequestDTO dashboardRequestDTO, int pintChartId, StaffDTO staffDTO, List<String> idReverse) throws Exception {
        int vintIndex = 0;

        List<TableCellBaseDTO> vlstTableCellBaseDTOS = new ArrayList<>();
        String vstrServiceName = DataUtil.safeToString(pObj[0]);
        Double vdblPerform = (Double) pObj[1];
        Double vdblPerformBefore = (Double) (pObj[3]);
        String vstrChannelCode = DataUtil.safeToString(pObj[4]);
        String vstrShopCode = DataUtil.safeToString(pObj[5]);
        String vstrCodeDvt = DataUtil.safeToString(pObj[6]);
        Long vlngServiceId = DataUtil.safeToLong(pObj[8]);
        Double vdblDensity = DataUtil.safeToDouble(pObj[9]);
        String vstrDvt = DataUtil.safeToString(pObj[10]);
        Integer vdblOldRate = DataUtil.safeToInt(pObj[11]);
        String vstrNewDVT = DataUtil.safeToString(pObj[12]);
        Integer vdblNewRate = DataUtil.safeToInt(pObj[13]);

        //ten dvt
        String vstrDvtUse = null;
        if (!DataUtil.isNullOrEmpty(vstrNewDVT)) {
            vstrDvtUse = vstrNewDVT;
        } else {
            if (!DataUtil.isNullOrEmpty(vstrDvt)) {
                vstrDvtUse = vstrDvt;
            }
        }

        for (int i = 0; i < plstColumns.size(); i++) {
            if (Constants.TEXT.equals(plstColumns.get(i).getType())) {
                TableCellBaseDTO cellBaseDTO = new TableCellBaseDTO();
                cellBaseDTO.setColumnId(vintIndex++);
                if (Constants.SERVICE_NAME.equals(plstColumns.get(i).getValue())) {
                    if (DataUtil.isNullOrEmpty(vstrDvtUse)) {
                        cellBaseDTO.setViewValue(vstrServiceName);
                    } else {
                        cellBaseDTO.setViewValue(vstrServiceName + " (" + vstrDvtUse + ")");
                    }
                    cellBaseDTO.setColor(Constants.COLORS.BLACK);
                }
                if (Constants.PERFORM.equals(plstColumns.get(i).getValue())) {
                    if (vdblPerform != null && !DataUtil.isNullOrZero(vdblNewRate) && !DataUtil.isNullOrZero(vdblOldRate)) {
                        Double vdblValue = Double.valueOf(df.format(vdblPerform * vdblOldRate / vdblNewRate));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    } else if (vdblPerform != null) {
                        Double vdblValue = Double.valueOf(df.format(vdblPerform));
                        cellBaseDTO.setViewValue(format.format(vdblValue));
                    }
                    cellBaseDTO.setColor(Constants.COLORS.BLACK);
                }
                if (Constants.DENSITY.equals(plstColumns.get(i).getValue())) {
                    Double vdblValue = Double.valueOf(df.format(vdblDensity));
                    cellBaseDTO.setViewValue(format.format(vdblValue) + "%");
                }
                vlstTableCellBaseDTOS.add(cellBaseDTO);
            }

            if (pintCycleId == Constants.CYCLE_ID.DAY) {
                if (Constants.GROWTH_MONTH.equals(plstColumns.get(i).getType())) {
                    TableCellGrowthDTO cellGrowthDTO = tableService.setGrowth(vlngServiceId, vdblPerform, vdblPerformBefore, vintIndex++, vdblOldRate, vdblNewRate, idReverse);
                    vlstTableCellBaseDTOS.add(cellGrowthDTO);
                }
            }

            if (pintCycleId == Constants.CYCLE_ID.MONTH || pintCycleId == Constants.CYCLE_ID.QUARTER) {
                if (Constants.GROWTH_YEAR.equals(plstColumns.get(i).getType())) {
                    TableCellGrowthDTO cellGrowthDTO = tableService.setGrowth(vlngServiceId, vdblPerform, vdblPerformBefore, vintIndex++, vdblOldRate, vdblNewRate, idReverse);
                    vlstTableCellBaseDTOS.add(cellGrowthDTO);
                }
            }
        }

        return vlstTableCellBaseDTOS;
    }

}
