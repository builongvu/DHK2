package vn.vissoft.dashboard.services.impl;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ma.glasnost.orika.converter.builtin.StringToEnumConverter;
import org.apache.commons.collections.map.MultiKeyMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.vissoft.dashboard.config.I18N;
import vn.vissoft.dashboard.dto.ApParamDTO;
import vn.vissoft.dashboard.dto.DashboardRequestDTO;
import vn.vissoft.dashboard.dto.LineDTO;
import vn.vissoft.dashboard.dto.chart.*;
import vn.vissoft.dashboard.helper.Constant;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.helper.DashboardRequest;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.model.ConfigSingleChart;
import vn.vissoft.dashboard.repo.*;
import vn.vissoft.dashboard.services.ApParamService;
import vn.vissoft.dashboard.services.TableService;
import vn.vissoft.dashboard.services.TopSecondLevelService;

import javax.transaction.Transactional;
import java.sql.Date;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Transactional
@Service
public class TopSecondLevelServiceImpl implements TopSecondLevelService {

    private static final Logger LOGGER = LogManager.getLogger(DashboardRequest.class);

    @Autowired
    private VdsScoreRankingRepo vdsScoreRankingRepo;

    @Autowired
    private VdsKpiDetailRepo vdsKpiDetailRepo;

    @Autowired
    private PartnerRepo partnerRepo;

    @Autowired
    private TableService tableService;

    @Autowired
    private ApParamRepo apParamRepo;

    @Autowired
    private VdsScoreServiceRepoCustom vdsScoreServiceRepoCustom;

    @Autowired
    private ConfigSingleChartRepo configSingleChartRepo;

    @Autowired
    private ApParamService apParamService;

    @Autowired
    private ServiceScoreRepo serviceScoreRepo;

    private DecimalFormat df = new DecimalFormat("###.##");
    private NumberFormat format = NumberFormat.getInstance();


    /**
     * xu li du lieu cho bar chart trong level 2 cua top ben trai
     *
     * @param dashboardRequestDTO, configSingleChartDTO: điều kiện truyền vào của dashboard, the loai chart
     * @return : chartDTO
     * @author: HungNN
     * @version: 1.0 (sau khi được sửa đổi sẽ tăng thêm)
     * @since: 2019/11/07
     */
    @Override
    public ChartDTO detailRankShopBarChart(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChart) throws Exception {
        ChartDTO chartDTO = new ChartDTO();
        List<PointDTO> mlstPoints = new ArrayList<>();
        List<String> vlstString = new ArrayList<>();
        List<LineDTO> vlstLines = new ArrayList<>();
        List<SeriesDTO> vlstSeries = new ArrayList<>();
        SeriesDTO seriesDTO = new SeriesDTO();

        String metaData = configSingleChart.getMetaData();
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = (JsonObject) jsonParser.parse(metaData);
        JsonArray lines = null;
        if (!DataUtil.isNullObject(jsonObject))
            lines = (JsonArray) jsonObject.get(Constants.JSON_OBJECT_KEY.LINES);
        String vstrColor = "", vstrValue = "";

        for (int i = 0; i < lines.size(); i++) {
            JsonObject object = (JsonObject) lines.get(i);
            if (!DataUtil.isNullObject(object)) {
                JsonArray cycles = null;
                LineDTO lineDTO = new LineDTO();
                if (DataUtil.checkJson(object.get(Constants.JSON_OBJECT_KEY.CYCLE))) {
                    cycles = (JsonArray) object.get(Constants.JSON_OBJECT_KEY.CYCLE);
                }
                if (DataUtil.checkJson(object.get(Constants.JSON_OBJECT_KEY.COLOR))) {
                    vstrColor = object.get(Constants.JSON_OBJECT_KEY.COLOR).getAsString();
                }
                if (DataUtil.checkJson(object.get(Constants.JSON_OBJECT_KEY.VALUE))) {
                    vstrValue = object.get(Constants.JSON_OBJECT_KEY.VALUE).getAsString();
                }
                if (cycles != null) {
                    if (cycles.toString().contains(Constants.CYCLE.ALL)) {
                        lineDTO.setColor(vstrColor);
                        lineDTO.setValue(vstrValue);
                        vlstLines.add(lineDTO);
                    }
                }
            }
        }
        String str;
        // lay so don vi tu top level 1 truyen vao
        List<Integer> lstTopNum = new ArrayList<>();
        int topNum;
        JsonArray jsonArray;
        String mstrMetaData = configSingleChartRepo.getJsonArray();
        JsonObject jsonElements = (JsonObject) jsonParser.parse(mstrMetaData);
        jsonArray = (JsonArray) jsonElements.get(Constants.SHOWS.line);
        if (jsonElements != null)
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject json = (JsonObject) jsonArray.get(i);
                topNum = json.get(Constants.SHOWS.topNum).getAsInt();
                lstTopNum.add(topNum);
            }
        int numMax = lstTopNum.get(0);
        int numMin = lstTopNum.get(1);
//            int vintCycleId = dashboardRequestDTO.getCycleId();
        // neu expand = 1 thi hien thi toan bo du lieu
        if (dashboardRequestDTO.getExpand() == 1) {
            List<Object[]> lstDetail = vdsScoreRankingRepo.getScoreRanking(dashboardRequestDTO, true);
            if (DataUtil.isNullOrEmpty(lstDetail)) {
                chartDTO.setNoData(true);
            } else {
                chartDTO.setNoData(false);
            }
            for (Object[] objects : lstDetail) {
                str = String.valueOf(objects[0]);
                vlstString.add(str);
            }
            if (!DataUtil.isNullObject(lstDetail)) {
                for (int i = 0; i < lstDetail.size(); i++) {
                    PointDTO pointDTO = new PointDTO();
                    String vstrCategories = String.valueOf(lstDetail.get(i)[0]);
                    Double vdblViewValue = (Double) lstDetail.get(i)[1];
                    if (vdblViewValue != null) {
                        if (vdblViewValue == 0) {
                            vdblViewValue = 0d;
                            pointDTO.setValue(vdblViewValue);
                            pointDTO.setViewValue(" ");
                        } else {
                            pointDTO.setValue(Double.parseDouble(df.format(vdblViewValue)));
                            pointDTO.setViewValue(vdblViewValue + " " + I18N.get(Constants.POINTS));
                        }
                    }
                    pointDTO.setCategory(vstrCategories);
                    mlstPoints.add(pointDTO);
                }
            }
        } else {

            List<Object[]> lstResultRight = vdsScoreRankingRepo.getScoreRanking(dashboardRequestDTO, true);
            if (DataUtil.isNullOrEmpty(lstResultRight)) {
                chartDTO.setNoData(true);
            } else {
                chartDTO.setNoData(false);
            }
            // neu select so ban ghi <= numMax + numMin thi tra ve so ban ghi select ra, nguoc lai limit numMax+numMin ban ghi
            if (lstResultRight.size() <= (numMax + numMin)) {
                for (Object[] obj : lstResultRight) {
                    str = String.valueOf(obj[0]);
                    vlstString.add(str);
                }

                // tach list de set lai mau
                if (lstResultRight.size() > numMax) {
                    int frIndex2 = 0;
                    int toIndex2 = numMax;
                    List<Object[]> listMax = lstResultRight.subList(frIndex2, toIndex2); // list max
                    if (!DataUtil.isNullObject(lstResultRight)) {
                        for (int i = 0; i < listMax.size(); i++) {
                            PointDTO pointDTO = new PointDTO();
                            String vstrCategories = String.valueOf(listMax.get(i)[0]);
                            Double vdblViewValue = (Double) listMax.get(i)[1];
                            if (vdblViewValue != null) {
                                if (vdblViewValue == 0) {
                                    vdblViewValue = 0d;
                                    pointDTO.setValue(vdblViewValue);
                                    pointDTO.setViewValue(" ");
                                } else {
                                    pointDTO.setValue(Double.parseDouble(df.format(vdblViewValue)));
                                    pointDTO.setViewValue(vdblViewValue + " " + I18N.get(Constants.POINTS));
                                }
                            }
                            pointDTO.setCategory(vstrCategories);
                            pointDTO.setColor(Constants.COLORS.XANH_DAM);
                            mlstPoints.add(pointDTO);
                        }
                    }
                    int frIndex = numMax;
                    int toIndex = lstResultRight.size();
                    List<Object[]> lstSub = lstResultRight.subList(frIndex, toIndex); // list min
                    if (lstSub != null) {
                        for (int j = 0; j < lstSub.size(); j++) {
                            PointDTO pointDTO = new PointDTO();
                            String vstrCategories = String.valueOf(lstSub.get(j)[0]);
                            Double vdblViewValue = (Double) lstSub.get(j)[1];
                            if (vdblViewValue != null) {
                                if (vdblViewValue == 0) {
                                    vdblViewValue = 0d;
                                    pointDTO.setValue(vdblViewValue);
                                    pointDTO.setViewValue(" ");
                                } else {
                                    pointDTO.setValue(Double.parseDouble(df.format(vdblViewValue)));
                                    pointDTO.setViewValue(vdblViewValue + " " + I18N.get(Constants.POINTS));
                                }
                            }
                            pointDTO.setColor(Constants.COLORS.VANG_CAM);
                            pointDTO.setCategory(vstrCategories);
                            mlstPoints.add(pointDTO);
                        }
                    }
                } else {
                    if (!DataUtil.isNullObject(lstResultRight)) {
                        for (int i = 0; i < lstResultRight.size(); i++) {
                            PointDTO pointDTO = new PointDTO();
                            String vstrCategories = String.valueOf(lstResultRight.get(i)[0]);
                            Double vdblViewValue = (Double) lstResultRight.get(i)[1];
                            if (vdblViewValue != null) {
                                if (vdblViewValue == 0) {
                                    vdblViewValue = 0d;
                                    pointDTO.setValue(vdblViewValue);
                                    pointDTO.setViewValue(" ");
                                } else {
                                    pointDTO.setValue(Double.parseDouble(df.format(vdblViewValue)));
                                    pointDTO.setViewValue(vdblViewValue + " " + I18N.get(Constants.POINTS));
                                }
                            }
                            pointDTO.setCategory(vstrCategories);
                            pointDTO.setColor(Constants.COLORS.XANH_DAM);
                            mlstPoints.add(pointDTO);
                        }
                    }
                }
            } else {

                List<String> vlstStringTam1 = new ArrayList<>();
                List<String> vlstStringTam2 = new ArrayList<>();
                List<Object[]> lstShopMax = vdsScoreRankingRepo.getTopLeft(dashboardRequestDTO, numMax, true);
                // lay list 5 don vi kem nhat
                int frmIndex = lstResultRight.size() - numMin;
                int toIndex = lstResultRight.size();
                List<Object[]> lstShopMin = lstResultRight.subList(frmIndex, toIndex);
                for (Object[] vobjShopMax : lstShopMax) {
                    str = (String) vobjShopMax[0];
                    vlstStringTam1.add(str);
                }
                for (Object[] vobjShopMin : lstShopMin) {
                    str = String.valueOf(vobjShopMin[0]);
                    vlstStringTam2.add(str);
                }
                vlstString.addAll(vlstStringTam1);
                vlstString.addAll(vlstStringTam2);

                if (!DataUtil.isNullObject(lstShopMax)) {
                    for (int i = 0; i < lstShopMax.size(); i++) {
                        PointDTO pointDTO = new PointDTO();
                        String vstrCategories = String.valueOf(lstShopMax.get(i)[0]);
                        Double vdblViewValue = (Double) lstShopMax.get(i)[1];
                        if (vdblViewValue != null) {
                            if (vdblViewValue == 0) {
                                vdblViewValue = 0d;
                                pointDTO.setValue(vdblViewValue);
                                pointDTO.setViewValue(" ");
                            } else {
                                pointDTO.setValue(Double.parseDouble(df.format(vdblViewValue)));
                                pointDTO.setViewValue(vdblViewValue + " " + I18N.get(Constants.POINTS));
                            }
                        }
                        pointDTO.setColor(Constants.COLORS.XANH_DAM);
                        pointDTO.setCategory(vstrCategories);
                        mlstPoints.add(pointDTO);
                    }
                }

                if (!DataUtil.isNullObject(lstShopMin)) {
                    for (int i = 0; i < lstShopMin.size(); i++) {
                        PointDTO pointDTO = new PointDTO();
                        String vstrCategories = String.valueOf(lstShopMin.get(i)[0]);
                        Double vdblViewValue = (Double) lstShopMin.get(i)[1];
                        if (vdblViewValue != null) {
                            if (vdblViewValue == 0) {
                                vdblViewValue = 0d;
                                pointDTO.setValue(vdblViewValue);
                                pointDTO.setViewValue(" ");
                            } else {
                                pointDTO.setValue(Double.parseDouble(df.format(vdblViewValue)));
                                pointDTO.setViewValue(vdblViewValue + " " + I18N.get(Constants.POINTS));
                            }
                        }
                        pointDTO.setColor(Constants.COLORS.VANG_CAM);
                        pointDTO.setCategory(vstrCategories);
                        mlstPoints.add(pointDTO);
                    }
                }
            }
        }
        seriesDTO.setColor(Constants.COLORS.XANH_DAM);
        seriesDTO.setPoints(mlstPoints);
        seriesDTO.setUnitType(I18N.get(Constants.POINTS));
        vlstSeries.add(seriesDTO);
        chartDTO.setSeries(vlstSeries);
        chartDTO.setCategories(vlstString);
        chartDTO.setChartSize(configSingleChart.getChartSize());
        chartDTO.setChartType(configSingleChart.getChartType());
        chartDTO.setType(configSingleChart.getChartType());
        List<String> vlstTitleParams = Lists.newArrayList();
        if (dashboardRequestDTO.getParentShopCode() == null) {
            vlstTitleParams.add(partnerRepo.getNameByShopAndChannel(dashboardRequestDTO.getObjectCode()));
        } else {
            vlstTitleParams.add(partnerRepo.getNameByShopAndChannel(dashboardRequestDTO.getParentShopCode()));
        }
        vlstTitleParams.add(format.format(numMax));
        if (dashboardRequestDTO.getParentShopCode() == null) {
            vlstTitleParams.add(I18N.get(Constants.UNIT));
        } else {
            vlstTitleParams.add(I18N.get(Constants.STAFF));
        }
        vlstTitleParams.add(format.format(numMin));
        vlstTitleParams.add(DataUtil.convertLocalDateToString(dashboardRequestDTO.getPrdId(), dashboardRequestDTO.getCycleId()));
        chartDTO.setTitle(I18N.get(Constants.DASHBOARD_DETAIL_BAR_CHART, (String[]) vlstTitleParams.toArray(new String[vlstTitleParams.size()])));
        if (configSingleChart.getExpand() == 1) {
            if (chartDTO.isNoData() == false)
                chartDTO.setExpand(true);
            else
                chartDTO.setExpand(false);
        } else {
            chartDTO.setExpand(false);
        }
        chartDTO.setSubTitle("");
        if (configSingleChart.getDrilldown() == 1)
            chartDTO.setDrilldown(true);
        else if (configSingleChart.getDrilldown() == 0)
            chartDTO.setDrilldown(false);
        if (!DataUtil.isNullOrZero(configSingleChart.getDrillDownType()))
            chartDTO.setDrilldownType(configSingleChart.getDrillDownType());
        if (!DataUtil.isNullOrZero(configSingleChart.getDrillDownObjectId()))
            chartDTO.setDrilldownObject(configSingleChart.getDrillDownObjectId().intValue());
        return chartDTO;
    }

    /**
     * xu li du lieu cho table trong level 2 cua top ben trai
     *
     * @param dashboardRequestDTO, configSingleChartDTO: điều kiện truyền vào của dashboard, the loai chart
     * @return : tableDTO
     * @author: HungNN
     * @version: 1.0 (sau khi được sửa đổi sẽ tăng thêm)
     * @since: 2019/11/06
     */
    @Override
    public TableDTO detailRankShopTable(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChartDTO) throws Exception {
        int vintIndex = 0;
        TableDTO tableDTO = new TableDTO();
        List<TableRowDTO> vlstRows = new ArrayList<>();
        List<TableColumnDTO> vlstColumns = new ArrayList<>();
        String vstrNationalStaff = dashboardRequestDTO.getNationalStaff();

        String metaData = configSingleChartDTO.getMetaData();
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = (JsonObject) jsonParser.parse(metaData);
        JsonArray columns = null;
        if (!DataUtil.isNullObject(jsonObject))
            columns = (JsonArray) jsonObject.get(Constants.JSON_OBJECT_KEY.COLUMNS);

        String vstrType = "", vstrAlign = "", vstrValue = "", vstrTitle = "", vstrStaff = "";
        JsonArray cycles = null;
        for (int i = 0; i < columns.size(); i++) {
            JsonObject object = (JsonObject) columns.get(i);
            if (!DataUtil.isNullObject(object)) {

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
                if (DataUtil.checkJson(object.get(Constants.JSON_OBJECT_KEY.TITLE))) {
                    vstrTitle = object.get(Constants.JSON_OBJECT_KEY.TITLE).getAsString();
                }
                if (DataUtil.checkJson(object.get(Constants.JSON_OBJECT_KEY.STAFF))) {
                    vstrStaff = object.get(Constants.JSON_OBJECT_KEY.STAFF).getAsString();
                }
                //cycle bang 0 hoac 1 la set column theo ngay
                if (cycles.toString().contains(Constants.CYCLE.ALL) && (vstrStaff.equals(Constants.CYCLE.ALL) || vstrStaff.equals(vstrNationalStaff))) {
                    column.setColumnId(vintIndex++);
                    column.setType(vstrType);
                    column.setName(I18N.get(vstrTitle));
                    column.setAlign(vstrAlign);
                    column.setValue(vstrValue);

                    vlstColumns.add(column);
                }
            }
        }
//        int cycleId = dashboardRequestDTO.getCycleId();
        List<Object[]> vlstObjects = vdsScoreRankingRepo.getScoreRanking(dashboardRequestDTO, true);
        for (Object[] vobjCellBase : vlstObjects) {
            List<TableCellBaseDTO> data = setTableCell(vobjCellBase, vlstColumns);
            TableRowDTO tableRowDTO = new TableRowDTO();
            tableRowDTO.setShopCode(String.valueOf(vobjCellBase[5]));
            tableRowDTO.setCells(data);

            if (dashboardRequestDTO.getParentShopCode() != null)
                tableRowDTO.setClicked(false);
            else tableRowDTO.setClicked(true);
            vlstRows.add(tableRowDTO);
        }
        tableDTO.setColumns(vlstColumns);
        tableDTO.setRows(vlstRows);
        if (configSingleChartDTO.getDrilldown() == 1)
            tableDTO.setDrilldown(true);
        else if (configSingleChartDTO.getDrilldown() == 0)
            tableDTO.setDrilldown(false);
        if (!DataUtil.isNullOrZero(configSingleChartDTO.getDrillDownType()))
            tableDTO.setDrilldownType(configSingleChartDTO.getDrillDownType());
        if (!DataUtil.isNullOrZero(configSingleChartDTO.getDrillDownObjectId()))
            tableDTO.setDrilldownObject(configSingleChartDTO.getDrillDownObjectId().intValue());
        tableDTO.setType(Constants.CHARTTYPE.TABLE_VIEW);
        List<String> vlstTitleParams = Lists.newArrayList();
        if (dashboardRequestDTO.getParentShopCode() == null) {
            vlstTitleParams.add(partnerRepo.getNameByShopAndChannel(dashboardRequestDTO.getObjectCode()));
            vlstTitleParams.add(I18N.get(Constants.UNIT));
        } else {
            vlstTitleParams.add(partnerRepo.getNameByShopAndChannel(dashboardRequestDTO.getParentShopCode()));
            vlstTitleParams.add(I18N.get(Constants.STAFF));
        }
        vlstTitleParams.add(DataUtil.convertLocalDateToString(dashboardRequestDTO.getPrdId(), dashboardRequestDTO.getCycleId()));
        tableDTO.setTitle(I18N.get(Constants.DASHBOARD_DETAIL_TABLE, (String[]) vlstTitleParams.toArray(new String[vlstTitleParams.size()])));
        tableDTO.setDownloadDetail(true);
        return tableDTO;
    }

    /**
     * chi tiet bang content top ben phai
     *
     * @param dashboardRequestDTO
     * @param configSingleChartDTO
     * @return
     * @throws Exception
     * @author DatNT
     * @since 18/11/2019
     */
    @Override
    public TableDTO detailShopTargetTable(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChartDTO) throws Exception {
        String vstrType = "", vstrAlign = "", vstrValue = "", vstrTitle = "";
        int vintIndex = 0;
        int vintRowIndex = 0;

        TableDTO tableDTO = new TableDTO();
        List<TableColumnDTO> vlstColumns = new ArrayList<>();
        List<TableRowDTO> vlstRows = new ArrayList<>();

        MultiKeyMap multiKeyMap = new MultiKeyMap();
        Set<String> shopNames = new LinkedHashSet<>();
        Set<String> serviceNames = new LinkedHashSet<>();
        Map<String, Long> serviceIds = new HashMap<>();

        String metaData = configSingleChartDTO.getMetaData();
        String vstrShortName = partnerRepo.getNameByShopAndChannel(dashboardRequestDTO.getObjectCode());
        List<Object[]> vlstObjects = vdsKpiDetailRepo.tableDetailTopRight(dashboardRequestDTO);
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = (JsonObject) jsonParser.parse(metaData);
        JsonObject color;
        JsonArray columns = null;
        if (!DataUtil.isNullObject(jsonObject)) {
            columns = (JsonArray) jsonObject.get(Constants.JSON_OBJECT_KEY.COLUMNS);
        }

        for (int i = 0; i < columns.size(); i++) {
            TableColumnDTO column = new TableColumnDTO();
            JsonObject object = (JsonObject) columns.get(i);
            if (!DataUtil.isNullObject(object)) {
                if (DataUtil.checkJson(object.get(Constants.JSON_OBJECT_KEY.TYPE))) {
                    vstrType = object.get(Constants.JSON_OBJECT_KEY.TYPE).getAsString();
                }
                if (DataUtil.checkJson(object.get(Constants.JSON_OBJECT_KEY.ALIGN))) {
                    vstrAlign = object.get(Constants.JSON_OBJECT_KEY.ALIGN).getAsString();
                }
                if (DataUtil.checkJson(object.get(Constants.JSON_OBJECT_KEY.VALUE))) {
                    vstrValue = object.get(Constants.JSON_OBJECT_KEY.VALUE).getAsString();
                }
                if (DataUtil.checkJson(object.get(Constants.JSON_OBJECT_KEY.TITLE))) {
                    vstrTitle = object.get(Constants.JSON_OBJECT_KEY.TITLE).getAsString();
                }

                if (Constants.NORMAL.equalsIgnoreCase(vstrType)) {
                    column.setColumnId(vintIndex);
                    column.setType(vstrType);
                    column.setAlign(vstrAlign);
                    column.setName(I18N.get(vstrTitle));
                    column.setValue(vstrValue);
                    vlstColumns.add(column);
                } else if (Constants.PIVOT.equalsIgnoreCase(vstrType)) {
                    for (int k = 0; k < vlstObjects.size(); k++) {
                        String vstrServiceName = DataUtil.safeToString(vlstObjects.get(k)[0]);
                        Double vdblPercent = DataUtil.safeToDouble(vlstObjects.get(k)[1]);
                        String vstrShopName = DataUtil.safeToString(vlstObjects.get(k)[2]);
                        Long vlngServiceId = DataUtil.safeToLong(vlstObjects.get(k)[3]);
                        Double vdblPerform = (Double) vlstObjects.get(k)[4];
                        if (!DataUtil.isNullOrEmpty(vstrServiceName))
                            serviceNames.add(vstrServiceName.trim());
                        shopNames.add(vstrShopName.trim());
                        serviceIds.put(vstrServiceName, vlngServiceId);
                        multiKeyMap.put(vstrShopName.trim(), vlngServiceId, vdblPerform);
                        multiKeyMap.put(vstrShopName.trim(), vlngServiceId, vdblPerform, vdblPercent);
                    }
                    for (String serviceName : serviceNames) {
                        column = new TableColumnDTO();
                        column.setColumnId(vintIndex++);
                        column.setType(vstrType);
                        column.setName(serviceName);
                        column.setAlign(vstrAlign);
                        column.setValue(vstrValue);

                        vlstColumns.add(column);
                    }
                } else if (Constants.VALUE.equalsIgnoreCase(vstrType)) break;

            }
        }
        for (String shopName : shopNames) {
            int vintColumnIndex = 0;
            vintRowIndex++;
            List<TableCellBaseDTO> vlstCellBases = new ArrayList<>();

            for (int j = 0; j < vlstColumns.size(); j++) {
                if (Constants.NORMAL.equalsIgnoreCase(vlstColumns.get(j).getType().trim())) {
                    TableCellBaseDTO cellBaseDTO = new TableCellBaseDTO();
                    cellBaseDTO.setColumnId(vintColumnIndex++);
                    cellBaseDTO.setViewValue(shopName);
                    cellBaseDTO.setColor(Constants.COLORS.BLACK);
                    vlstCellBases.add(cellBaseDTO);
                } else if (Constants.PIVOT.equalsIgnoreCase(vlstColumns.get(j).getType().trim())) {
                    TableCellBaseDTO cellBaseDTO = new TableCellBaseDTO();
                    cellBaseDTO.setColumnId(vintColumnIndex++);
                    Double vdblPercent = 0d;
                    Double vdblComplete;
                    Long vlngServiceId = serviceIds.get(vlstColumns.get(j).getName().trim());
                    Double vdblAccumPerform = (Double) multiKeyMap.get(shopName.trim(), vlngServiceId);

                    if (vdblAccumPerform != null) {
                        vdblComplete = DataUtil.safeToDouble(multiKeyMap.get(shopName.trim(), vlngServiceId, vdblAccumPerform));
                        if (!DataUtil.isNullOrZero(vdblComplete)) {
                            vdblPercent = Double.valueOf(df.format(vdblComplete));
                            cellBaseDTO.setViewValue(format.format(vdblPercent) + "%");
                        } else {
                            vdblPercent = null;
                            cellBaseDTO.setViewValue(null);
                        }
                    } else if (multiKeyMap.containsKey(shopName.trim(), vlngServiceId) && vdblAccumPerform == null) {
                        cellBaseDTO.setViewValue("0%");
                    }

                    if (!DataUtil.isNullOrZero(vdblPercent)) {
                        if (dashboardRequestDTO.getIsTarget() == 1) {  //1:don vi dat chi tieu
                            cellBaseDTO.setColor(Constants.COLORS.GREEN);
                            if (vdblPercent > 100) cellBaseDTO.setBold(true);
                        } else if (dashboardRequestDTO.getIsTarget() == -1) {//-1: don vi chua dat chi tieu
                            String vstrWarningColor = "";
                            String vstrWarningLevel = tableService.setWarninglevel(vdblPercent, dashboardRequestDTO.getVdsChannelCode(), vlngServiceId);
                            if (!DataUtil.isNullOrEmpty(vstrWarningLevel))
                                vstrWarningColor = apParamRepo.getServiceWarningColor(vstrWarningLevel);
                            cellBaseDTO.setColor(vstrWarningColor);
                        }
                    }
                    vlstCellBases.add(cellBaseDTO);
                }
            }
            TableRowDTO tableRowDTO = new TableRowDTO();
            tableRowDTO.setRowIndex(vintRowIndex);
            tableRowDTO.setCells(vlstCellBases);
            vlstRows.add(tableRowDTO);

        }
        tableDTO.setType(configSingleChartDTO.getChartType());
        tableDTO.setColumns(vlstColumns);
        tableDTO.setRows(vlstRows);
        tableDTO.setChartSize(configSingleChartDTO.getChartSize());

        //build title
        if (!DataUtil.isNullOrEmpty(vstrShortName)) {
            List<String> listParamTitle = Lists.newArrayList();
            listParamTitle.add(vstrShortName);

            switch (dashboardRequestDTO.getCycleId()) {
                case Constants.CYCLE_ID.DAY:
                    if (DataUtil.isNullOrZero(dashboardRequestDTO.getServiceId()))
                        listParamTitle.add(I18N.get(Constants.TABLE_DETAIL_DAY));
                    listParamTitle.add(DataUtil.convertLocalDateToString(dashboardRequestDTO.getPrdId(), Constants.CYCLE_ID.DAY));
                    break;
                case Constants.CYCLE_ID.MONTH:
                    if (DataUtil.isNullOrZero(dashboardRequestDTO.getServiceId()))
                        listParamTitle.add(I18N.get(Constants.TABLE_DETAIL_MONTH));
                    listParamTitle.add(DataUtil.convertLocalDateToString(dashboardRequestDTO.getPrdId(), Constants.CYCLE_ID.MONTH));
                    break;
                case Constants.CYCLE_ID.QUARTER:
                    if (DataUtil.isNullOrZero(dashboardRequestDTO.getServiceId()))
                        listParamTitle.add(I18N.get(Constants.TABLE_DETAIL_YEAR));
                    listParamTitle.add(DataUtil.convertLocalDateToString(dashboardRequestDTO.getPrdId(), Constants.CYCLE_ID.QUARTER));
                    break;
            }
            tableDTO.setTitle(I18N.get(configSingleChartDTO.getTitleI18n().trim(), (String[]) listParamTitle.toArray(new String[listParamTitle.size()])));

        }
        return tableDTO;
    }


    /**
     * xu li du lieu cho cac cell trong table
     *
     * @param plstColumns, objects
     * @return : chuoi TableCellBaseDTO
     * @author: HungNN
     * @version: 1.0 (sau khi được sửa đổi sẽ tăng thêm)
     * @since: 2019/11/07
     */
    List<TableCellBaseDTO> setTableCell(Object[] objects, List<TableColumnDTO> plstColumns) {
        int vintIndex = 0;
        List<TableCellBaseDTO> vlstCellBaseDTOS = new ArrayList<>();
        String nameUnit = DataUtil.safeToString(objects[0]);
        Double scoreComplete = (Double) objects[1];
        String vstrShopShortName = (String) objects[11];
        int rank = 0;
        if (objects[2] != null) {
            rank = (int) objects[2];
        }
        Double scoreCompleteN1 = (Double) objects[3];

        int rankN1 = 0;
        if (objects[4] != null) {
            rankN1 = (int) objects[4];
        }
        Double scoreMax = (Double) objects[6];
        Double scoreMaxN1 = (Double) objects[7];

        //Vu update them
        Double scoreCompleteN2 = (Double) objects[8];
        Double scoreMaxN2 = (Double) objects[9];
        int rankN2 = 0;
        if (objects[10] != null) {
            rankN2 = (int) objects[10];
        }

        if (!DataUtil.isNullOrEmpty(plstColumns)) {
            for (int i = 0; i < plstColumns.size(); i++) {
                TableCellBaseDTO cellBaseDTO = new TableCellBaseDTO();
                cellBaseDTO.setColumnId(vintIndex++);
                if (Constants.TEXT.equals(plstColumns.get(i).getType())) {
                    if (Constants.SHOP_NAME.equals(plstColumns.get(i).getValue())) {
                        cellBaseDTO.setViewValue(nameUnit);
                    }
                    if (Constants.SHOP_SHORT_NAME.equals(plstColumns.get(i).getValue())) {
                        cellBaseDTO.setViewValue(vstrShopShortName);
                    }
                    if (Constants.COMPLETED_POINTS.equals(plstColumns.get(i).getValue())) {
                        if (scoreMax != null) {
                            if (scoreComplete != null) {
                                cellBaseDTO.setViewValue(format.format(scoreComplete) + "/" + format.format(scoreMax));
                            } else {
                                cellBaseDTO.setViewValue("-/" + format.format(scoreMax));
                            }
                        } else {
                            cellBaseDTO.setViewValue("/");
                        }
                    }
                    if (Constants.RANK.equals(plstColumns.get(i).getValue())) {
                        if (!DataUtil.isNullOrZero(rank))
                            cellBaseDTO.setViewValue(format.format(rank));
                    }
                    if (Constants.SEMESTER_POINTS_LAST_MONHTH.equals(plstColumns.get(i).getValue())) {
                        if (scoreMaxN1 != null) {
                            if (scoreCompleteN1 != null) {
                                cellBaseDTO.setViewValue(format.format(scoreCompleteN1) + "/" + format.format(scoreMaxN1));
                            } else {
                                cellBaseDTO.setViewValue("-/" + format.format(scoreMaxN1));
                            }
                        } else {
                            cellBaseDTO.setViewValue("/");
                        }
                    }
                    if (Constants.SEMESTER_RANK.equals(plstColumns.get(i).getValue())) {
                        if (!DataUtil.isNullOrZero(rankN1))
                            cellBaseDTO.setViewValue(format.format(rankN1));
                    }
                    //Vu update them
                    if (Constants.SEMESTER_POINTS_LAST_TWOMONHTH.equals(plstColumns.get(i).getValue())) {
                        if (scoreMaxN2 != null) {
                            if (scoreCompleteN2 != null) {
                                cellBaseDTO.setViewValue(format.format(scoreCompleteN2) + "/" + format.format(scoreMaxN2));
                            } else {
                                cellBaseDTO.setViewValue("-/" + format.format(scoreMaxN2));
                            }
                        } else {
                            cellBaseDTO.setViewValue("/");
                        }
                    }
                    if (Constants.SEMESTER_RANK_N2.equals(plstColumns.get(i).getValue())) {
                        if (!DataUtil.isNullOrZero(rankN2))
                            cellBaseDTO.setViewValue(format.format(rankN2));
                    }
                    vlstCellBaseDTOS.add(cellBaseDTO);
                }
                if (Constants.GROWTH_YEAR.equals(plstColumns.get(i).getValue())) {
                    TableCellGrowthDTO setDataCellGrowth = setTableCellGrowDTO(rank, rankN1, vintIndex++);
                    vlstCellBaseDTOS.add(setDataCellGrowth);
                }
            }
        }
        return vlstCellBaseDTOS;
    }

    /**
     * set du lieu cho TableCellGrowthDTO
     *
     * @param pdblRankN1
     * @param pdblRank
     * @param pintColumnIndex
     * @return tableCellGrowthDTO
     * @throws Exception
     * @author HungNN
     * @version 1.0
     * @since 09/11/2019
     */
    TableCellGrowthDTO setTableCellGrowDTO(Integer pdblRank, Integer pdblRankN1, int pintColumnIndex) {
        TableCellGrowthDTO tableCellGrowthDTO = new TableCellGrowthDTO();
        tableCellGrowthDTO.setColumnId(pintColumnIndex);
        if (pdblRank != null && pdblRankN1 != null) {
            int result = pdblRankN1 - pdblRank;
            if (result > 0) {
                tableCellGrowthDTO.setIsGrowth(1);
                tableCellGrowthDTO.setColor(Constants.COLORS.GREEN);
            } else if (result == 0) {
                tableCellGrowthDTO.setIsGrowth(0);
                tableCellGrowthDTO.setColor(Constants.COLORS.YELLOW);
            } else {
                tableCellGrowthDTO.setIsGrowth(-1);
                tableCellGrowthDTO.setColor(Constants.COLORS.RED);
            }
        }
        if (pdblRank != null && pdblRankN1 == null) {
            tableCellGrowthDTO.setIsGrowth(1);
            tableCellGrowthDTO.setColor(Constants.COLORS.GREEN);
        }
        if (pdblRank == null && pdblRankN1 != null) {
            tableCellGrowthDTO.setIsGrowth(-1);
            tableCellGrowthDTO.setColor(Constants.COLORS.RED);
        }
        if (DataUtil.isNullOrZero(pdblRankN1)) {
            tableCellGrowthDTO.setIsGrowth(0);
            tableCellGrowthDTO.setColor(Constants.COLORS.YELLOW);
        }
        return tableCellGrowthDTO;
    }

    /**
     * bang du lieu chi tiet diem hoan thanh chi tieu cua tung don vi khi tai file excel
     *
     * @param dashboardRequestDTO
     * @return tableDTO
     * @throws Exception
     * @author HungNN
     * @version 1.0
     * @since 16/12/2019
     */
    @Override
    public TableDTO detailTableWithService(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChart configSingleChart) throws Exception {
        TableDTO tableDTO = new TableDTO();
        String vstrType = "", vstrAlign = "", vstrValue = "", vstrTitle = "", vstrStaff = "";
        int vintIndex = 0;
        String vstrNationalStaff = dashboardRequestDTO.getNationalStaff();
//        JsonArray cycles = null;
        List<TableColumnDTO> columnDTOList = new ArrayList<>();
        List<TableColumnDTO> columnForFrondend = new ArrayList<>();
        List<TableRowDTO> tableRowDTOList = new ArrayList<>();

        MultiKeyMap multiKeyMap = new MultiKeyMap();
        Set<String> shopNames = new LinkedHashSet<>();
        Set<String> staffCodes = new LinkedHashSet<>();
        Set<Long> lstId = new LinkedHashSet<>();
        List<String> serviceNames = new ArrayList<>();
        Set<String> strNameService = new LinkedHashSet<>();
        Map<String, Long> serviceIds = new HashMap<>();
        Map<String, Long> mapServiceId = new HashMap<>();
        Map<String, String> mapStaffCodeService = new HashMap<>();
        Map<Long, String> mapServices = new HashMap<>();
        String metaData = configSingleChart.getMetaData();
        String shortName = null;
        if (DataUtil.isNullOrEmpty(dashboardRequestDTO.getParentShopCode())) {
            shortName = partnerRepo.getNameByShopAndChannel(dashboardRequestDTO.getObjectCode());
        } else {
            shortName = partnerRepo.getNameByShopAndChannel(dashboardRequestDTO.getParentShopCode());
        }
        List<Object[]> lstData = vdsScoreServiceRepoCustom.getDataDetail(dashboardRequestDTO);
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = (JsonObject) jsonParser.parse(metaData);
        JsonArray columns = null;
        if (jsonObject != null)
            columns = (JsonArray) jsonObject.get(Constants.JSON_OBJECT_KEY.COLUMNS);

        for (int i = 0; i < columns.size(); i++) {
            TableColumnDTO tableColumnDTO = new TableColumnDTO();
            TableColumnDTO columnForFE = new TableColumnDTO();
            JsonObject object = (JsonObject) columns.get(i);
            if (object != null) {
                if (DataUtil.checkJson(object.get(Constants.JSON_OBJECT_KEY.TYPE))) {
                    vstrType = object.get(Constants.JSON_OBJECT_KEY.TYPE).getAsString();
                }
                if (DataUtil.checkJson(object.get(Constants.JSON_OBJECT_KEY.ALIGN))) {
                    vstrAlign = object.get(Constants.JSON_OBJECT_KEY.ALIGN).getAsString();
                }
                if (DataUtil.checkJson(object.get(Constants.JSON_OBJECT_KEY.VALUE))) {
                    vstrValue = object.get(Constants.JSON_OBJECT_KEY.VALUE).getAsString();
                }
                if (DataUtil.checkJson(object.get(Constants.JSON_OBJECT_KEY.TITLE))) {
                    vstrTitle = object.get(Constants.JSON_OBJECT_KEY.TITLE).getAsString();
                }
                if (DataUtil.checkJson(object.get(Constants.JSON_OBJECT_KEY.STAFF))) {
                    vstrStaff = object.get(Constants.JSON_OBJECT_KEY.STAFF).getAsString();
                }

                if (vstrStaff.equals(Constants.CYCLE.ALL) || vstrStaff.equals(vstrNationalStaff)) {
                    if (Constants.NORMAL.equalsIgnoreCase(vstrType)) {
                        tableColumnDTO.setColumnId(vintIndex);
                        tableColumnDTO.setType(vstrType);
                        tableColumnDTO.setAlign(vstrAlign);
                        tableColumnDTO.setName(I18N.get(vstrTitle));
                        tableColumnDTO.setValue(vstrValue);
                        columnDTOList.add(tableColumnDTO);

                        columnForFE.setColumnId(vintIndex);
                        columnForFE.setType(vstrType);
                        columnForFE.setAlign(vstrAlign);
                        columnForFE.setName(I18N.get(vstrTitle));
                        columnForFE.setValue(vstrValue);
                        columnForFrondend.add(tableColumnDTO);
                    } else if (Constants.PIVOT.equalsIgnoreCase(vstrType)) {
                        for (int j = 0; j < lstData.size(); j++) {
//                        Long id = DataUtil.safeToLong(lstData.get(j)[0]);
                            String staffCode = DataUtil.safeToString(lstData.get(j)[1]);
                            String vstrShopName = DataUtil.safeToString(lstData.get(j)[2]);
                            Double vdblScore = (Double) (lstData.get(j)[3]);
                            Double vdblScoreMax = (Double) (lstData.get(j)[4]);
                            Integer vintRank = (Integer) (lstData.get(j)[5]);
                            Double vdblScoreN1 = (Double) (lstData.get(j)[6]);
                            Double vdblScoreMaxN1 = (Double) (lstData.get(j)[7]);
                            Integer vintRankN1 = (Integer) (lstData.get(j)[8]);
                            String vstrServiceName = DataUtil.safeToString(lstData.get(j)[9]);
                            Long vlngServiceId = DataUtil.safeToLong(lstData.get(j)[10]);
                            Double vdblScoreDetail = (Double) (lstData.get(j)[11]);
                            //Vu update them
                            Double vdblScoreN2 = (Double) (lstData.get(j)[13]);
                            Double vdblScoreMaxN2 = (Double) (lstData.get(j)[14]);
                            Integer vintRankN2 = (Integer) (lstData.get(j)[15]);
                            Double vdblPerform = DataUtil.safeToDouble(lstData.get(j)[16]);
                            Double vdblCompletePercent = DataUtil.safeToDouble(lstData.get(j)[17]);
                            String vstrShopShortName = (String) lstData.get(j)[18];

                            mapStaffCodeService.put(staffCode, vstrServiceName);
                            mapServices.put(vlngServiceId, vstrServiceName);
                            strNameService.add(vstrServiceName);
                            staffCodes.add(staffCode);
                            lstId.add(vlngServiceId);
                            serviceNames.add(vstrServiceName);
                            shopNames.add(vstrShopName);
                            //lay key theo shop_name cua bang vds_score_servcie_daily
                            if (DataUtil.isNullOrEmpty(dashboardRequestDTO.getParentShopCode()) && dashboardRequestDTO.getNationalStaff().equals(Constants.PARAM_STATUS_0)) {

                                serviceIds.put(vstrServiceName, vlngServiceId);
//                            multiKeyMap.put(vstrShopName, Constants.SHOP_NAME);
                                if (vdblScoreMax != null) {
                                    if (vdblScore != null) {
                                        multiKeyMap.put(vstrShopName, Constants.COMPLETED_POINTS, format.format(vdblScore) + "/" + format.format(vdblScoreMax));
                                    } else {
                                        multiKeyMap.put(vstrShopName, Constants.COMPLETED_POINTS, "-/" + format.format(vdblScoreMax));
                                    }
                                } else {
                                    multiKeyMap.put(vstrShopName, Constants.COMPLETED_POINTS, "/");
                                }
                                multiKeyMap.put(vstrShopName, Constants.RANK, vintRank);
                                if (vdblScoreMaxN1 != null) {
                                    if (vdblScoreMax != null) {
                                        multiKeyMap.put(vstrShopName, Constants.SEMESTER_POINTS_LAST_MONHTH, format.format(vdblScoreN1) + "/" + format.format(vdblScoreMaxN1));
                                    } else {
                                        multiKeyMap.put(vstrShopName, Constants.SEMESTER_POINTS_LAST_MONHTH, "-/" + format.format(vdblScoreMaxN1));
                                    }
                                } else {
                                    multiKeyMap.put(vstrShopName, Constants.SEMESTER_POINTS_LAST_MONHTH, "/");
                                }
                                if (vintRankN1 != null) {
                                    multiKeyMap.put(vstrShopName, Constants.SEMESTER_RANK, vintRankN1);
                                }
                                if (vdblScoreDetail != null) {
                                    multiKeyMap.put(vstrShopName, vlngServiceId, vdblScoreDetail);
                                }
                                //Vu update them
                                if (vdblScoreMaxN2 != null) {
                                    if (vdblScoreMax != null) {
                                        multiKeyMap.put(vstrShopName, Constants.SEMESTER_POINTS_LAST_TWOMONHTH, format.format(vdblScoreN2) + "/" + format.format(vdblScoreMaxN2));
                                    } else {
                                        multiKeyMap.put(vstrShopName, Constants.SEMESTER_POINTS_LAST_TWOMONHTH, "-/" + format.format(vdblScoreMaxN2));
                                    }
                                } else {
                                    multiKeyMap.put(vstrShopName, Constants.SEMESTER_POINTS_LAST_TWOMONHTH, "/");
                                }
                                if (vintRankN2 != null) {
                                    multiKeyMap.put(vstrShopName, Constants.SEMESTER_RANK_N2, vintRankN2);
                                }
                                if (!DataUtil.isNullOrZero(vdblPerform)) {
                                    multiKeyMap.put(vstrShopName, vlngServiceId, Constants.PERFORM, vdblPerform);
                                }
                                if (!DataUtil.isNullOrZero(vdblCompletePercent)) {
                                    multiKeyMap.put(vstrShopName, vlngServiceId, Constants.COMPLETE_PERCENT, vdblCompletePercent);
                                }

                            } else { //lay key theo service_name
                                multiKeyMap.put(staffCode, Constants.SHOP_SHORT_NAME, vstrShopShortName);
                                mapServiceId.put(vstrServiceName, vlngServiceId);
                                multiKeyMap.put(staffCode, Constants.SHOP_NAME, vstrShopName);
                                if (vdblScoreMax != null) {
                                    if (vdblScore != null) {
                                        multiKeyMap.put(staffCode, Constants.COMPLETED_POINTS, format.format(vdblScore) + "/" + format.format(vdblScoreMax));
                                    } else {
                                        multiKeyMap.put(staffCode, Constants.COMPLETED_POINTS, "-/" + format.format(vdblScoreMax));
                                    }
                                } else {
                                    multiKeyMap.put(staffCode, Constants.COMPLETED_POINTS, "/");
                                }
                                multiKeyMap.put(staffCode, Constants.RANK, vintRank);
                                if (vdblScoreMaxN1 != null) {
                                    if (vdblScoreMax != null) {
                                        multiKeyMap.put(staffCode, Constants.SEMESTER_POINTS_LAST_MONHTH, format.format(vdblScoreN1) + "/" + format.format(vdblScoreMaxN1));
                                    } else {
                                        multiKeyMap.put(staffCode, Constants.SEMESTER_POINTS_LAST_MONHTH, "-/" + format.format(vdblScoreMaxN1));
                                    }
                                } else {
                                    multiKeyMap.put(staffCode, Constants.SEMESTER_POINTS_LAST_MONHTH, "/");
                                }
                                if (vintRankN1 != null) {
                                    multiKeyMap.put(staffCode, Constants.SEMESTER_RANK, vintRankN1);
                                }
                                if (vdblScoreDetail != null) {
                                    multiKeyMap.put(staffCode, vlngServiceId, vdblScoreDetail);
                                }
                                //Vu update them
                                if (vdblScoreMaxN2 != null) {
                                    if (vdblScoreMax != null) {
                                        multiKeyMap.put(staffCode, Constants.SEMESTER_POINTS_LAST_TWOMONHTH, format.format(vdblScoreN2) + "/" + format.format(vdblScoreMaxN2));
                                    } else {
                                        multiKeyMap.put(staffCode, Constants.SEMESTER_POINTS_LAST_TWOMONHTH, "-/" + format.format(vdblScoreMaxN2));
                                    }
                                } else {
                                    multiKeyMap.put(staffCode, Constants.SEMESTER_POINTS_LAST_TWOMONHTH, "/");
                                }
                                if (vintRankN2 != null) {
                                    multiKeyMap.put(staffCode, Constants.SEMESTER_RANK_N2, vintRankN2);
                                }
                                if (!DataUtil.isNullOrZero(vdblPerform)) {
                                    multiKeyMap.put(staffCode, vlngServiceId, Constants.PERFORM, vdblPerform);
                                }
                                if (!DataUtil.isNullOrZero(vdblCompletePercent)) {
                                    multiKeyMap.put(staffCode, vlngServiceId, Constants.COMPLETE_PERCENT, vdblCompletePercent);
                                }
                            }
                        }

                    } else if (Constants.VALUE.equalsIgnoreCase(vstrType))
                        break;
                }
            }

            int columnId = 0;
            if (DataUtil.isNullOrEmpty(dashboardRequestDTO.getParentShopCode())) {
                for (Long serviceId : lstId) {
                    tableColumnDTO = new TableColumnDTO();
                    tableColumnDTO.setColumnId(columnId++);
                    tableColumnDTO.setType(vstrType);
                    tableColumnDTO.setAlign(vstrAlign);
                    tableColumnDTO.setValue(vstrValue);
                    tableColumnDTO.setName(mapServices.get(serviceId));
                    columnDTOList.add(tableColumnDTO);

                    //column cho FE
                    columnForFE = new TableColumnDTO();
                    columnForFE.setColumnId(columnId++);
                    columnForFE.setType(vstrType);
                    columnForFE.setAlign(vstrAlign);
                    columnForFE.setValue(vstrValue);
                    columnForFE.setName(mapServices.get(serviceId));
                    columnForFrondend.add(columnForFE);

                    //them cot de BE dung
                    TableColumnDTO columnScore = new TableColumnDTO();
                    columnScore.setAlign(Constants.CODE_WARNING.RIGHT);
                    columnScore.setName(I18N.get(Constants.DIEM));
                    columnScore.setValue(Constants.SCORE);
                    columnScore.setStaffCode(mapServices.get(serviceId));
                    columnDTOList.add(columnScore);
                    TableColumnDTO columnPerform = new TableColumnDTO();
                    columnPerform.setAlign(Constants.CODE_WARNING.RIGHT);
                    columnPerform.setName(I18N.get(Constants.THUC_HIEN));
                    columnPerform.setValue(Constants.PERFORM_SERVICE);
                    columnPerform.setStaffCode(mapServices.get(serviceId));
                    columnDTOList.add(columnPerform);
                    TableColumnDTO columnComplete = new TableColumnDTO();
                    columnComplete.setAlign(Constants.CODE_WARNING.RIGHT);
                    columnComplete.setName(I18N.get(Constants.HOAN_THANH));
                    columnComplete.setValue(Constants.COMPLETE_SERVICE);
                    columnComplete.setStaffCode(mapServices.get(serviceId));
                    columnDTOList.add(columnComplete);

                    //column child cho FE
                    List<TableColumnDTO> columnsChild = new ArrayList<>();
                    columnsChild.add(columnScore);
                    columnsChild.add(columnPerform);
                    columnsChild.add(columnComplete);
                    columnForFE.setColumns(columnsChild);

                }
            } else {
                for (String serviceName : strNameService) {
                    tableColumnDTO = new TableColumnDTO();
                    tableColumnDTO.setType(vstrType);
                    tableColumnDTO.setAlign(vstrAlign);
                    tableColumnDTO.setValue(vstrValue);
                    tableColumnDTO.setName(serviceName);
                    columnDTOList.add(tableColumnDTO);

                    //column cho FE
                    columnForFE.setType(vstrType);
                    columnForFE.setAlign(vstrAlign);
                    columnForFE.setValue(vstrValue);
                    columnForFE.setName(serviceName);
                    columnForFrondend.add(columnForFE);

                    //them cot
                    //them cot de BE dung
                    TableColumnDTO columnScore = new TableColumnDTO();
                    columnScore.setAlign(Constants.CODE_WARNING.RIGHT);
                    columnScore.setName(I18N.get(Constants.DIEM));
                    columnScore.setValue(Constants.SCORE);
                    columnScore.setStaffCode(serviceName);
                    columnDTOList.add(columnScore);
                    TableColumnDTO columnPerform = new TableColumnDTO();
                    columnPerform.setAlign(Constants.CODE_WARNING.RIGHT);
                    columnPerform.setName(I18N.get(Constants.THUC_HIEN));
                    columnPerform.setValue(Constants.PERFORM_SERVICE);
                    columnPerform.setStaffCode(serviceName);
                    columnDTOList.add(columnPerform);
                    TableColumnDTO columnComplete = new TableColumnDTO();
                    columnComplete.setAlign(Constants.CODE_WARNING.RIGHT);
                    columnComplete.setName(I18N.get(Constants.HOAN_THANH));
                    columnComplete.setValue(Constants.COMPLETE_SERVICE);
                    columnComplete.setStaffCode(serviceName);
                    columnDTOList.add(columnComplete);

                    //column child cho FE
                    List<TableColumnDTO> columnsChild = new ArrayList<>();
                    columnsChild.add(columnScore);
                    columnsChild.add(columnPerform);
                    columnsChild.add(columnComplete);
                    columnForFE.setColumns(columnsChild);
                }
            }
        }

        if (DataUtil.isNullOrEmpty(dashboardRequestDTO.getParentShopCode()) && dashboardRequestDTO.getNationalStaff().equals(Constants.PARAM_STATUS_0)) {
            int vintRowIndex = 0;
            int vintColumnIndex = 0;
            for (String shopName : shopNames) {
                vintRowIndex++;
                List<TableCellBaseDTO> cellBaseDTOS = new ArrayList<>();
                for (int k = 0; k < columnDTOList.size(); k++) {
                    if (Constants.SHOP_NAME.equalsIgnoreCase(columnDTOList.get(k).getValue().trim())) {
                        TableCellBaseDTO cellBaseDTO = new TableCellBaseDTO();
                        cellBaseDTO.setColumnId(vintColumnIndex++);
                        cellBaseDTO.setViewValue(shopName);
                        cellBaseDTO.setColor(Constants.COLORS.BLACK);
                        cellBaseDTOS.add(cellBaseDTO);
                    } else if (Constants.COMPLETED_POINTS.equalsIgnoreCase(columnDTOList.get(k).getValue().trim())) {
                        TableCellBaseDTO cellBaseDTO = new TableCellBaseDTO();
                        cellBaseDTO.setColumnId(vintColumnIndex++);
                        String vstrCompletePoint = (String) multiKeyMap.get(shopName, Constants.COMPLETED_POINTS);
                        cellBaseDTO.setViewValue((vstrCompletePoint != null ? vstrCompletePoint : null));
                        cellBaseDTO.setColor(Constants.COLORS.BLACK);
                        cellBaseDTOS.add(cellBaseDTO);
                    } else if (Constants.RANK.equalsIgnoreCase(columnDTOList.get(k).getValue().trim())) {
                        TableCellBaseDTO cellBaseDTO = new TableCellBaseDTO();
                        cellBaseDTO.setColumnId(vintColumnIndex++);
                        Integer vintRank = (Integer) multiKeyMap.get(shopName, Constants.RANK);
                        if (!DataUtil.isNullOrZero(vintRank))
                            cellBaseDTO.setViewValue(String.valueOf(vintRank));
                        else
                            cellBaseDTO.setViewValue("");
                        cellBaseDTO.setColor(Constants.COLORS.BLACK);
                        cellBaseDTOS.add(cellBaseDTO);
                    } else if (Constants.SEMESTER_POINTS_LAST_MONHTH.equalsIgnoreCase(columnDTOList.get(k).getValue().trim())) {
                        TableCellBaseDTO cellBaseDTO = new TableCellBaseDTO();
                        cellBaseDTO.setColumnId(vintColumnIndex++);
                        String vstrPointLastMonth = (String) multiKeyMap.get(shopName, Constants.SEMESTER_POINTS_LAST_MONHTH);
                        cellBaseDTO.setViewValue((vstrPointLastMonth != null ? vstrPointLastMonth : null));
                        cellBaseDTO.setColor(Constants.COLORS.BLACK);
                        cellBaseDTOS.add(cellBaseDTO);
                    } else if (Constants.SEMESTER_RANK.equalsIgnoreCase(columnDTOList.get(k).getValue().trim())) {
                        TableCellBaseDTO cellBaseDTO = new TableCellBaseDTO();
                        cellBaseDTO.setColumnId(vintColumnIndex++);
                        if (multiKeyMap.get(shopName, Constants.SEMESTER_RANK) != null) {
                            Integer vintSemesterRank = (Integer) multiKeyMap.get(shopName, Constants.SEMESTER_RANK);
                            cellBaseDTO.setViewValue(String.valueOf(vintSemesterRank));
                        }
                        cellBaseDTO.setColor(Constants.COLORS.BLACK);
                        cellBaseDTOS.add(cellBaseDTO);
                    }
//                    else if (Constants.SERVICE.equalsIgnoreCase(columnDTOList.get(k).getValue().trim())) {
//                        TableCellBaseDTO tableCellBaseDTO = new TableCellBaseDTO();
//                        tableCellBaseDTO.setColumnId(vintColumnIndex++);
//                        Long vlngServiceId = serviceIds.get(columnDTOList.get(k).getName());
//                        Double vdblScoreDetail = (Double) multiKeyMap.get(shopName, vlngServiceId);
//                        //Vu them
//                        String vstrPerformComplete = "";
//                        Double vdblPerform = (Double) multiKeyMap.get(shopName, vlngServiceId, Constants.PERFORM);
//                        Double vdblCompletePercent = (Double) multiKeyMap.get(shopName, vlngServiceId, Constants.COMPLETE_PERCENT);
//                        if (vdblPerform != null && vdblCompletePercent == null) {
//                            vstrPerformComplete = "(TH: " + vdblPerform + "; %HT:_)";
//                        } else if (vdblPerform == null && vdblCompletePercent != null) {
//                            vstrPerformComplete = "(TH:_; %HT: " + vdblCompletePercent + "%)";
//                        } else if (vdblPerform != null && vdblCompletePercent != null) {
//                            vstrPerformComplete = "(TH: " + vdblPerform + "; %HT: " + vdblCompletePercent + "%)";
//                        } else {
//                            vstrPerformComplete = "";
//                        }
//
//                        if (vdblScoreDetail != null && !vstrPerformComplete.equals("")) {
//                            tableCellBaseDTO.setViewValue(format.format(vdblScoreDetail) + " " + vstrPerformComplete);
//                        } else if (vdblScoreDetail != null && vstrPerformComplete.equals("")) {
//                            tableCellBaseDTO.setViewValue(format.format(vdblScoreDetail));
//                        } else if (vdblScoreDetail == null && !vstrPerformComplete.equals("")) {
//                            tableCellBaseDTO.setViewValue(vstrPerformComplete);
//                        }
//
//                        if (multiKeyMap.containsKey(shopName, vlngServiceId) && vdblScoreDetail == null) {
//                            tableCellBaseDTO.setViewValue("-");
//                        }
//
//                        cellBaseDTOS.add(tableCellBaseDTO);
//                    }
                    //them cot 30/07/2020
                    else if (Constants.SCORE.equalsIgnoreCase(columnDTOList.get(k).getValue().trim())) {
                        TableCellBaseDTO cellBaseDTO = new TableCellBaseDTO();
                        Long vlngServiceId = serviceIds.get(columnDTOList.get(k).getStaffCode());
                        Double vdblScoreDetail = (Double) multiKeyMap.get(shopName, vlngServiceId);
                        cellBaseDTO.setViewValue(vdblScoreDetail == null ? "" : String.valueOf(vdblScoreDetail));
                        cellBaseDTOS.add(cellBaseDTO);
                    }
                    else if (Constants.PERFORM_SERVICE.equalsIgnoreCase(columnDTOList.get(k).getValue().trim())) {
                        TableCellBaseDTO cellBaseDTO = new TableCellBaseDTO();
                        Long vlngServiceId = serviceIds.get(columnDTOList.get(k).getStaffCode());
                        Double vdblPerform = (Double) multiKeyMap.get(shopName, vlngServiceId, Constants.PERFORM);
                        cellBaseDTO.setViewValue(vdblPerform == null ? "" : String.valueOf(vdblPerform));
                        cellBaseDTOS.add(cellBaseDTO);
                    }
                    else if (Constants.COMPLETE_SERVICE.equalsIgnoreCase(columnDTOList.get(k).getValue().trim())) {
                        TableCellBaseDTO cellBaseDTO = new TableCellBaseDTO();
                        Long vlngServiceId = serviceIds.get(columnDTOList.get(k).getStaffCode());
                        Double vdblCompletePercent = (Double) multiKeyMap.get(shopName, vlngServiceId, Constants.COMPLETE_PERCENT);
                        cellBaseDTO.setViewValue(vdblCompletePercent == null ? "" : vdblCompletePercent+"%");
                        cellBaseDTOS.add(cellBaseDTO);
                    }
                    //Vu update them
                    else if (Constants.SEMESTER_POINTS_LAST_TWOMONHTH.equalsIgnoreCase(columnDTOList.get(k).getValue().trim())) {
                        TableCellBaseDTO cellBaseDTO = new TableCellBaseDTO();
                        cellBaseDTO.setColumnId(vintColumnIndex++);
                        String vstrPointLastMonth = (String) multiKeyMap.get(shopName, Constants.SEMESTER_POINTS_LAST_TWOMONHTH);
                        cellBaseDTO.setViewValue((vstrPointLastMonth != null ? vstrPointLastMonth : null));
                        cellBaseDTO.setColor(Constants.COLORS.BLACK);
                        cellBaseDTOS.add(cellBaseDTO);
                    } else if (Constants.SEMESTER_RANK_N2.equalsIgnoreCase(columnDTOList.get(k).getValue().trim())) {
                        TableCellBaseDTO cellBaseDTO = new TableCellBaseDTO();
                        cellBaseDTO.setColumnId(vintColumnIndex++);
                        if (multiKeyMap.get(shopName, Constants.SEMESTER_RANK_N2) != null) {
                            Integer vintSemesterRank = (Integer) multiKeyMap.get(shopName, Constants.SEMESTER_RANK_N2);
                            cellBaseDTO.setViewValue(String.valueOf(vintSemesterRank));
                        }
                        cellBaseDTO.setColor(Constants.COLORS.BLACK);
                        cellBaseDTOS.add(cellBaseDTO);
                    }
                }
                TableRowDTO tableRowDTO = new TableRowDTO();
                tableRowDTO.setRowIndex(vintRowIndex);
                tableRowDTO.setCells(cellBaseDTOS);
                tableRowDTOList.add(tableRowDTO);
            }
        } else {
            int vintColumnIndex = 0;
            int vintRowIndex = 0;
            for (String strStaffCode : staffCodes) {
                vintRowIndex++;
                List<TableCellBaseDTO> cellBaseDTOS = new ArrayList<>();
                for (int k = 0; k < columnDTOList.size(); k++) {
                    if (Constants.SHOP_NAME.equalsIgnoreCase(columnDTOList.get(k).getValue().trim())) {
                        TableCellBaseDTO cellBaseDTO = new TableCellBaseDTO();
                        cellBaseDTO.setColumnId(vintColumnIndex++);
                        String shopName = (String) multiKeyMap.get(strStaffCode, Constants.SHOP_NAME);
                        cellBaseDTO.setViewValue(shopName);
                        cellBaseDTO.setColor(Constants.COLORS.BLACK);
                        cellBaseDTOS.add(cellBaseDTO);
                    } else if (Constants.SHOP_SHORT_NAME.equalsIgnoreCase(columnDTOList.get(k).getValue().trim())) {
                        TableCellBaseDTO cellBaseDTO = new TableCellBaseDTO();
                        cellBaseDTO.setColumnId(vintColumnIndex++);
                        String shopShortName = (String) multiKeyMap.get(strStaffCode, Constants.SHOP_SHORT_NAME);
                        cellBaseDTO.setViewValue(shopShortName);
                        cellBaseDTO.setColor(Constants.COLORS.BLACK);
                        cellBaseDTOS.add(cellBaseDTO);
                    }
                    else if (Constants.COMPLETED_POINTS.equalsIgnoreCase(columnDTOList.get(k).getValue().trim())) {
                        TableCellBaseDTO cellBaseDTO = new TableCellBaseDTO();
                        cellBaseDTO.setColumnId(vintColumnIndex++);
                        String vstrCompletePoint = (String) multiKeyMap.get(strStaffCode, Constants.COMPLETED_POINTS);
                        cellBaseDTO.setViewValue((vstrCompletePoint != null ? vstrCompletePoint : null));
                        cellBaseDTO.setColor(Constants.COLORS.BLACK);
                        cellBaseDTOS.add(cellBaseDTO);
                    } else if (Constants.RANK.equalsIgnoreCase(columnDTOList.get(k).getValue().trim())) {
                        TableCellBaseDTO cellBaseDTO = new TableCellBaseDTO();
                        cellBaseDTO.setColumnId(vintColumnIndex++);
                        Integer vintRank = (Integer) multiKeyMap.get(strStaffCode, Constants.RANK);
                        if (!DataUtil.isNullOrZero(vintRank))
                            cellBaseDTO.setViewValue(String.valueOf(vintRank));
                        else
                            cellBaseDTO.setViewValue("");

                        cellBaseDTO.setColor(Constants.COLORS.BLACK);
                        cellBaseDTOS.add(cellBaseDTO);
                    } else if (Constants.SEMESTER_POINTS_LAST_MONHTH.equalsIgnoreCase(columnDTOList.get(k).getValue().trim())) {
                        TableCellBaseDTO cellBaseDTO = new TableCellBaseDTO();
                        cellBaseDTO.setColumnId(vintColumnIndex++);
                        String vstrPointLastMonth = (String) multiKeyMap.get(strStaffCode, Constants.SEMESTER_POINTS_LAST_MONHTH);
                        cellBaseDTO.setViewValue((vstrPointLastMonth != null ? vstrPointLastMonth : null));
                        cellBaseDTO.setColor(Constants.COLORS.BLACK);
                        cellBaseDTOS.add(cellBaseDTO);
                    } else if (Constants.SEMESTER_RANK.equalsIgnoreCase(columnDTOList.get(k).getValue().trim())) {
                        TableCellBaseDTO cellBaseDTO = new TableCellBaseDTO();
                        cellBaseDTO.setColumnId(vintColumnIndex++);
                        if (multiKeyMap.get(strStaffCode, Constants.SEMESTER_RANK) != null) {
                            int vintSemesterRank = (int) multiKeyMap.get(strStaffCode, Constants.SEMESTER_RANK);
                            cellBaseDTO.setViewValue(String.valueOf(vintSemesterRank));
                        }
                        cellBaseDTO.setColor(Constants.COLORS.BLACK);
                        cellBaseDTOS.add(cellBaseDTO);
                    }
//                    else if (Constants.SERVICE.equalsIgnoreCase(columnDTOList.get(k).getValue().trim())) {
//                        TableCellBaseDTO tableCellBaseDTO = new TableCellBaseDTO();
//                        tableCellBaseDTO.setColumnId(vintColumnIndex++);
//                        Long vlngServiceId = mapServiceId.get(columnDTOList.get(k).getName());
//                        Double vdblScoreDetail = (Double) multiKeyMap.get(strStaffCode, vlngServiceId);
//                        //Vu them
//                        String vstrPerformComplete;
//                        Double vdblPerform = (Double) multiKeyMap.get(strStaffCode, vlngServiceId, Constants.PERFORM);
//                        Double vdblCompletePercent = (Double) multiKeyMap.get(strStaffCode, vlngServiceId, Constants.COMPLETE_PERCENT);
//                        if (vdblPerform != null && vdblCompletePercent == null) {
//                            vstrPerformComplete = "(TH: " + vdblPerform + "; %HT:_)";
//                        } else if (vdblPerform == null && vdblCompletePercent != null) {
//                            vstrPerformComplete = "(TH:_; %HT: " + vdblCompletePercent + "%)";
//                        } else if (vdblPerform != null && vdblCompletePercent != null) {
//                            vstrPerformComplete = "(TH: " + vdblPerform + "; %HT: " + vdblCompletePercent + "%)";
//                        } else {
//                            vstrPerformComplete = "";
//                        }
//
//                        if (vdblScoreDetail != null && !vstrPerformComplete.equals("")) {
//                            tableCellBaseDTO.setViewValue(format.format(vdblScoreDetail) + " " + vstrPerformComplete);
//                        } else if (vdblScoreDetail != null && vstrPerformComplete.equals("")) {
//                            tableCellBaseDTO.setViewValue(format.format(vdblScoreDetail));
//                        } else if (vdblScoreDetail == null && !vstrPerformComplete.equals("")) {
//                            tableCellBaseDTO.setViewValue(vstrPerformComplete);
//                        }
//
//                        if (multiKeyMap.containsKey(strStaffCode, vlngServiceId) && vdblScoreDetail == null) {
//                            tableCellBaseDTO.setViewValue("-");
//                        }
//
//                        cellBaseDTOS.add(tableCellBaseDTO);
//                    }
                    //them cot 30/07/2020
                    else if (Constants.SCORE.equalsIgnoreCase(columnDTOList.get(k).getValue().trim())) {
                        TableCellBaseDTO cellBaseDTO = new TableCellBaseDTO();
                        Long vlngServiceId = mapServiceId.get(columnDTOList.get(k).getStaffCode());
                        Double vdblScoreDetail = (Double) multiKeyMap.get(strStaffCode, vlngServiceId);
                        cellBaseDTO.setViewValue(vdblScoreDetail == null ? "" : String.valueOf(vdblScoreDetail));
                        cellBaseDTOS.add(cellBaseDTO);
                    }
                    else if (Constants.PERFORM_SERVICE.equalsIgnoreCase(columnDTOList.get(k).getValue().trim())) {
                        TableCellBaseDTO cellBaseDTO = new TableCellBaseDTO();
                        Long vlngServiceId = mapServiceId.get(columnDTOList.get(k).getStaffCode());
                        Double vdblPerform = (Double) multiKeyMap.get(strStaffCode, vlngServiceId, Constants.PERFORM);
                        cellBaseDTO.setViewValue(vdblPerform == null ? "" : String.valueOf(vdblPerform));
                        cellBaseDTOS.add(cellBaseDTO);
                    }
                    else if (Constants.COMPLETE_SERVICE.equalsIgnoreCase(columnDTOList.get(k).getValue().trim())) {
                        TableCellBaseDTO cellBaseDTO = new TableCellBaseDTO();
                        Long vlngServiceId = mapServiceId.get(columnDTOList.get(k).getStaffCode());
                        Double vdblCompletePercent = (Double) multiKeyMap.get(strStaffCode, vlngServiceId, Constants.COMPLETE_PERCENT);
                        cellBaseDTO.setViewValue(vdblCompletePercent == null ? "" : vdblCompletePercent+"%");
                        cellBaseDTOS.add(cellBaseDTO);
                    }
                    //Vu update them
                    else if (Constants.SEMESTER_POINTS_LAST_TWOMONHTH.equalsIgnoreCase(columnDTOList.get(k).getValue().trim())) {
                        TableCellBaseDTO cellBaseDTO = new TableCellBaseDTO();
                        cellBaseDTO.setColumnId(vintColumnIndex++);
                        String vstrPointLastMonth = (String) multiKeyMap.get(strStaffCode, Constants.SEMESTER_POINTS_LAST_TWOMONHTH);
                        cellBaseDTO.setViewValue((vstrPointLastMonth != null ? vstrPointLastMonth : null));
                        cellBaseDTO.setColor(Constants.COLORS.BLACK);
                        cellBaseDTOS.add(cellBaseDTO);
                    } else if (Constants.SEMESTER_RANK_N2.equalsIgnoreCase(columnDTOList.get(k).getValue().trim())) {
                        TableCellBaseDTO cellBaseDTO = new TableCellBaseDTO();
                        cellBaseDTO.setColumnId(vintColumnIndex++);
                        if (multiKeyMap.get(strStaffCode, Constants.SEMESTER_RANK_N2) != null) {
                            Integer vintSemesterRank = (Integer) multiKeyMap.get(strStaffCode, Constants.SEMESTER_RANK_N2);
                            cellBaseDTO.setViewValue(String.valueOf(vintSemesterRank));
                        }
                        cellBaseDTO.setColor(Constants.COLORS.BLACK);
                        cellBaseDTOS.add(cellBaseDTO);
                    }
                }
                TableRowDTO tableRowDTO = new TableRowDTO();
                tableRowDTO.setRowIndex(vintRowIndex);
                tableRowDTO.setCells(cellBaseDTOS);
                tableRowDTOList.add(tableRowDTO);
            }
        }
        tableDTO.setType(configSingleChart.getChartType());
        tableDTO.setColumns(columnForFrondend);
        tableDTO.setRows(tableRowDTOList);
        tableDTO.setChartSize(configSingleChart.getChartSize());

        //build title
        List<String> vlstTitleParams = Lists.newArrayList();
        if (dashboardRequestDTO.getParentShopCode() == null) {
            vlstTitleParams.add(shortName);
            vlstTitleParams.add(I18N.get(Constants.UNIT));
        } else {
            vlstTitleParams.add(shortName);
            vlstTitleParams.add(I18N.get(Constants.STAFF));
        }
        vlstTitleParams.add(DataUtil.convertLocalDateToString(dashboardRequestDTO.getPrdId(), dashboardRequestDTO.getCycleId()));
        tableDTO.setTitle(I18N.get(Constants.DASHBOARD_DETAIL_TABLE, (String[]) vlstTitleParams.toArray(new String[vlstTitleParams.size()])));
        tableDTO.setDownloadDetail(true);
        return tableDTO;
    }

    @Override
    public TableDTO detailScoreRanking(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChartDTO) throws Exception {
        String vstrType = "", vstrAlign = "", vstrValue = "", vstrTitle = "", vstrStaff = "";
        String vstrBadColor = null;
        String vstrGoodColor = null;
        String vstrPerformValue = null;
        String vstrPercentValue = null;
        String vstrPerformValueN1 = null;
        String vstrPercentValueN1 = null;
        String vstrPerformValueN2 = null;
        String vstrPercentValueN2 = null;
        int vintIndex = 0;
        int vintRowIndex = 0;
        int vintMucMot = 0;
        int vintMucHai = 0;
        int vintMucBa = 0;
        int vintMucBon = 0;
        String vstrNationalStaff = dashboardRequestDTO.getNationalStaff();


        TableDTO tableDTO = new TableDTO();
        List<TableColumnDTO> vlstColumns = new ArrayList<>();
        List<TableRowDTO> vlstRows = new ArrayList<>();
        MultiKeyMap multiKeyMap = new MultiKeyMap();
        Set<String> shopNames = new LinkedHashSet<>();
        Set<String> serviceNames = new LinkedHashSet<>();
        Map<String, String> serviceIds = new HashMap<>();
        List<String> vlstParamTitleChart = Lists.newArrayList();

        List<ApParamDTO> vlstApParam = apParamService.findByType(Constants.TOP_BY_SERVICE.EVALUATED_RATE);
        String vstrPRow = dashboardRequestDTO.getpRow();
        //lay ra 3 muc tu cau hinh
        for (ApParamDTO ap : vlstApParam) {
            if (ap.getCode().trim().equals(Constants.TOP_BY_SERVICE.ONE)) {
                vintMucMot = Integer.parseInt(ap.getValue());
            }
            if (ap.getCode().trim().equals(Constants.TOP_BY_SERVICE.TWO)) {
                vintMucHai = Integer.parseInt(ap.getValue());
            }
            if (ap.getCode().trim().equals(Constants.TOP_BY_SERVICE.THREE)) {
                vintMucBa = Integer.parseInt(ap.getValue());
            }
            if (ap.getCode().trim().equals(Constants.TOP_BY_SERVICE.FOUR)) {
                vintMucBon = Integer.parseInt(ap.getValue());
            }
        }

        String metaData = configSingleChartDTO.getMetaData();
        String vstrShortName = partnerRepo.getNameByShopAndChannel(dashboardRequestDTO.getObjectCode());
        List<Object[]> vlstObjects = serviceScoreRepo.findDetailEvaluateScore(dashboardRequestDTO, configSingleChartDTO);
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = (JsonObject) jsonParser.parse(metaData);
        JsonObject color;
        JsonArray columns = null;
        JsonArray params = null;
        if (!DataUtil.isNullObject(jsonObject)) {
            columns = (JsonArray) jsonObject.get(Constants.JSON_OBJECT_KEY.COLUMNS);
            params = (JsonArray) jsonObject.get(Constants.JSON_OBJECT_KEY.PARAM);
            //lay mau
            if (!DataUtil.isNullObject(params.get(0))) {
                color = (JsonObject) params.get(0);
                vstrBadColor = color.get("bad").getAsString();
                vstrGoodColor = color.get("good").getAsString();
            }
        }
        for (int i = 0; i < columns.size(); i++) {
            TableColumnDTO column = new TableColumnDTO();
            JsonObject object = (JsonObject) columns.get(i);
            if (!DataUtil.isNullObject(object)) {
                if (DataUtil.checkJson(object.get(Constants.JSON_OBJECT_KEY.TYPE))) {
                    vstrType = object.get(Constants.JSON_OBJECT_KEY.TYPE).getAsString();
                }
                if (DataUtil.checkJson(object.get(Constants.JSON_OBJECT_KEY.ALIGN))) {
                    vstrAlign = object.get(Constants.JSON_OBJECT_KEY.ALIGN).getAsString();
                }
                if (DataUtil.checkJson(object.get(Constants.JSON_OBJECT_KEY.VALUE))) {
                    vstrValue = object.get(Constants.JSON_OBJECT_KEY.VALUE).getAsString();
                }
                if (DataUtil.checkJson(object.get(Constants.JSON_OBJECT_KEY.TITLE))) {
                    vstrTitle = object.get(Constants.JSON_OBJECT_KEY.TITLE).getAsString();
                }
                if (DataUtil.checkJson(object.get(Constants.JSON_OBJECT_KEY.STAFF))) {
                    vstrStaff = object.get(Constants.JSON_OBJECT_KEY.STAFF).getAsString();
                }

                if (vstrStaff.equals(Constants.CYCLE.ALL) || vstrStaff.equals(vstrNationalStaff)) {
                    if (Constants.NORMAL.equalsIgnoreCase(vstrType) || Constants.NATIONAL.equalsIgnoreCase(vstrType)) {
                        column.setColumnId(vintIndex);
                        column.setType(vstrType);
                        column.setAlign(vstrAlign);
                        column.setName(I18N.get(vstrTitle));
                        column.setValue(vstrValue);
                        vlstColumns.add(column);
                    } else if (Constants.PIVOT.equalsIgnoreCase(vstrType)) {
                        for (int k = 0; k < vlstObjects.size(); k++) {
                            String vstrShopName = DataUtil.safeToString(vlstObjects.get(k)[0]);
                            Double vdblScore = DataUtil.safeToDouble(vlstObjects.get(k)[1]);
                            Double vdblScoreMax = (Double) (vlstObjects.get(k)[2]);
                            Double vdblPerform = (Double) (vlstObjects.get(k)[3]);
                            Double vdblPercent = (Double) (vlstObjects.get(k)[5]);
                            String vstrServiceName = DataUtil.safeToString(vlstObjects.get(k)[6]);
                            Long vlngServiceId = DataUtil.safeToLong(vlstObjects.get(k)[7]);
                            Double vdblScoreN1 = (Double) (vlstObjects.get(k)[9]);
                            Double vdblScoreMaxN1 = (Double) (vlstObjects.get(k)[10]);
                            Double vdblTHN1 = (Double) (vlstObjects.get(k)[11]);
                            Double vdblKHN1 = (Double) (vlstObjects.get(k)[12]);
                            Double vdblPercentN1 = (Double) (vlstObjects.get(k)[13]);
                            Double vdblScoreN2 = (Double) (vlstObjects.get(k)[14]);
                            Double vdblScoreMaxN2 = (Double) (vlstObjects.get(k)[15]);
                            Double vdblTHN2 = (Double) (vlstObjects.get(k)[16]);
                            Double vdblKHN2 = (Double) (vlstObjects.get(k)[17]);
                            Double vdblPercentN2 = (Double) (vlstObjects.get(k)[18]);
                            String vstrShopShortName = DataUtil.safeToString(vlstObjects.get(k)[19]);
                            if (vdblPerform == null) {
                                vstrPerformValue = "_";
                            } else {
                                vstrPerformValue = df.format(vdblPerform);
                            }
                            if (vdblPercent == null) {
                                vstrPercentValue = "_";
                            } else {
                                vstrPercentValue = df.format(vdblPercent);
                            }
                            if (vdblTHN1 == null) {
                                vstrPerformValueN1 = "_";
                            } else {
                                vstrPerformValueN1 = df.format(vdblTHN1);
                            }
                            if (vdblPercentN1 == null) {
                                vstrPercentValueN1 = "_";
                            } else {
                                vstrPercentValueN1 = df.format(vdblPercentN1);
                            }
                            if (vdblTHN2 == null) {
                                vstrPerformValueN2 = "_";
                            } else {
                                vstrPerformValueN2 = df.format(vdblTHN2);
                            }
                            if (vdblPercentN2 == null) {
                                vstrPercentValueN2 = "_";
                            } else {
                                vstrPercentValueN2 = df.format(vdblPercentN2);
                            }

                            String vstrScoreDetail = null;
                            String vstrScoreDetailN1 = null;
                            String vstrScoreDetailN2 = null;
                            switch (dashboardRequestDTO.getMonth()) {
                                case Constants.TOP_BY_SERVICE.ONE:
                                    if (vdblScore != null && vdblScoreMax != null) {
                                        vstrScoreDetail = df.format(vdblScore) + "/" + df.format(vdblScoreMax) + "(TH: " + vstrPerformValue + " ; %HT: " + vstrPercentValue + "%)";
                                    } else {
                                        if (vdblScore == null && vdblScoreMax != null) {
                                            vstrScoreDetail = "_ /" + df.format(vdblScoreMax) + "(TH: " + vstrPerformValue + " ; %HT: " + vstrPercentValue + "%)";
                                        } else if (vdblScore != null && vdblScoreMax == null) {
                                            vstrScoreDetail = df.format(vdblScore) + "/ _" + "(TH: " + vstrPerformValue + " ; %HT: " + vstrPercentValue + "%)";
                                        } else {
                                            vstrScoreDetail = "_ / _" + "(TH: " + vstrPerformValue + " ; %HT: " + vstrPercentValue + "%)";
                                        }
                                    }
                                    break;
                                case Constants.TOP_BY_SERVICE.TWO:
                                    if (vdblScore != null && vdblScoreMax != null) {
                                        vstrScoreDetail = df.format(vdblScore) + "/" + df.format(vdblScoreMax) + "(TH: " + vstrPerformValue + " ; %HT: " + vstrPercentValue + "%)";
                                    } else {
                                        if (vdblScore == null && vdblScoreMax != null) {
                                            vstrScoreDetail = "_ /" + df.format(vdblScoreMax) + "(TH: " + vstrPerformValue + " ; %HT: " + vstrPercentValue + "%)";
                                        } else if (vdblScore != null && vdblScoreMax == null) {
                                            vstrScoreDetail = df.format(vdblScore) + "/ _" + "(TH: " + vstrPerformValue + " ; %HT: " + vstrPercentValue + "%)";
                                        } else {
                                            vstrScoreDetail = "_ / _" + "(TH: " + vstrPerformValue + " ; %HT: " + vstrPercentValue + "%)";
                                        }
                                    }
                                    if (vdblScoreN1 != null && vdblScoreMaxN1 != null) {
                                        vstrScoreDetailN1 = df.format(vdblScoreN1) + "/" + df.format(vdblScoreMaxN1) + "(TH: " + vstrPerformValueN1 + " ; %HT: " + vstrPercentValueN1 + "%)";
                                    } else {
                                        if (vdblScoreN1 == null && vdblScoreMaxN1 != null) {
                                            vstrScoreDetailN1 = "_ /" + df.format(vdblScoreMaxN1) + "(TH: " + vstrPerformValueN1 + " ; %HT: " + vstrPercentValueN1 + "%)";
                                        } else if (vdblScoreN1 != null && vdblScoreMaxN1 == null) {
                                            vstrScoreDetailN1 = df.format(vdblScoreN1) + "/ _" + "(TH: " + vstrPerformValueN1 + " ; %HT: " + vstrPercentValueN1 + "%)";
                                        } else {
                                            vstrScoreDetailN1 = "_ / _" + "(TH: " + vstrPerformValueN1 + " ; %HT: " + vstrPercentValueN1 + "%)";
                                        }
                                    }
                                    break;

                                case Constants.TOP_BY_SERVICE.THREE:
                                    if (vdblScore != null && vdblScoreMax != null) {
                                        vstrScoreDetail = df.format(vdblScore) + "/" + df.format(vdblScoreMax) + "(TH: " + vstrPerformValue + " ; %HT: " + vstrPercentValue + "%)";
                                    } else {
                                        if (vdblScore == null && vdblScoreMax != null) {
                                            vstrScoreDetail = "_ /" + df.format(vdblScoreMax) + "(TH: " + vstrPerformValue + " ; %HT: " + vstrPercentValue + "%)";
                                        } else if (vdblScore != null && vdblScoreMax == null) {
                                            vstrScoreDetail = df.format(vdblScore) + "/ _" + "(TH: " + vstrPerformValue + " ; %HT: " + vstrPercentValue + "%)";
                                        } else {
                                            vstrScoreDetail = "_ / _" + "(TH: " + vstrPerformValue + " ; %HT: " + vstrPercentValue + "%)";
                                        }
                                    }
                                    if (vdblScoreN1 != null && vdblScoreMaxN1 != null) {
                                        vstrScoreDetailN1 = df.format(vdblScoreN1) + "/" + df.format(vdblScoreMaxN1) + "(TH: " + vstrPerformValueN1 + " ; %HT: " + vstrPercentValueN1 + "%)";
                                    } else {
                                        if (vdblScoreN1 == null && vdblScoreMaxN1 != null) {
                                            vstrScoreDetailN1 = "_ /" + df.format(vdblScoreMaxN1) + "(TH: " + vstrPerformValueN1 + " ; %HT: " + vstrPercentValueN1 + "%)";
                                        } else if (vdblScoreN1 != null && vdblScoreMaxN1 == null) {
                                            vstrScoreDetailN1 = df.format(vdblScoreN1) + "/ _" + "(TH: " + vstrPerformValueN1 + " ; %HT: " + vstrPercentValueN1 + "%)";
                                        } else {
                                            vstrScoreDetailN1 = "_ / _" + "(TH: " + vstrPerformValueN1 + " ; %HT: " + vstrPercentValueN1 + "%)";
                                        }
                                    }
                                    if (vdblScoreN2 != null && vdblScoreMaxN2 != null) {
                                        vstrScoreDetailN2 = df.format(vdblScoreN2) + "/" + df.format(vdblScoreMaxN2) + "(TH: " + vstrPerformValueN2 + " ; %HT: " + vstrPercentValueN2 + "%)";
                                    } else {
                                        if (vdblScoreN2 == null && vdblScoreMaxN2 != null) {
                                            vstrScoreDetailN2 = "_ /" + df.format(vdblScoreMaxN2) + "(TH: " + vstrPerformValueN2 + " ; %HT: " + vstrPercentValueN2 + "%)";
                                        } else if (vdblScoreN2 != null && vdblScoreMaxN2 == null) {
                                            vstrScoreDetailN2 = df.format(vdblScoreN2) + "/ _" + "(TH: " + vstrPerformValueN2 + " ; %HT: " + vstrPercentValueN2 + "%)";
                                        } else {
                                            vstrScoreDetailN2 = "_ / _" + "(TH: " + vstrPerformValueN2 + " ; %HT: " + vstrPercentValueN2 + "%)";
                                        }
                                    }
                                    break;
                            }


                            shopNames.add(vstrShopName.trim());
                            if (!DataUtil.isNullOrEmpty(vstrServiceName)) {
                                multiKeyMap.put(vstrShopName.trim(), Constants.SHOP_NAME, vstrShopShortName);
                                switch (dashboardRequestDTO.getMonth()) {
                                    case Constants.TOP_BY_SERVICE.ONE:
                                        if (dashboardRequestDTO.getCycleId() == Constants.CYCLE_ID.DAY || dashboardRequestDTO.getCycleId() == Constants.CYCLE_ID.MONTH) {
                                            serviceNames.add(vstrServiceName.trim() + " " + I18N.get("ranking.service.n"));
                                            serviceIds.put(vstrServiceName.trim() + " " + I18N.get("ranking.service.n"), vlngServiceId + Constants.TOP_BY_SERVICE.ONE);
                                        } else if (dashboardRequestDTO.getCycleId() == Constants.CYCLE_ID.QUARTER) {
                                            serviceNames.add(vstrServiceName.trim() + " " + I18N.get("ranking.service.quarter.n"));
                                            serviceIds.put(vstrServiceName.trim() + " " + I18N.get("ranking.service.quarter.n"), vlngServiceId + Constants.TOP_BY_SERVICE.ONE);
                                        }
                                        multiKeyMap.put(vstrShopName.trim(), vlngServiceId + Constants.TOP_BY_SERVICE.ONE, vstrScoreDetail);
                                        break;
                                    case Constants.TOP_BY_SERVICE.TWO:
                                        if (dashboardRequestDTO.getCycleId() == Constants.CYCLE_ID.DAY || dashboardRequestDTO.getCycleId() == Constants.CYCLE_ID.MONTH) {
                                            serviceNames.add(vstrServiceName.trim() + " " + I18N.get("ranking.service.n"));
                                            serviceNames.add(vstrServiceName.trim() + " " + I18N.get("ranking.service.n1.title"));
                                        } else if (dashboardRequestDTO.getCycleId() == Constants.CYCLE_ID.QUARTER) {
                                            serviceNames.add(vstrServiceName.trim() + " " + I18N.get("ranking.service.quarter.n"));
                                            serviceNames.add(vstrServiceName.trim() + " " + I18N.get("ranking.service.quarter.n1.title"));
                                        }

                                        if (dashboardRequestDTO.getCycleId() == Constants.CYCLE_ID.DAY || dashboardRequestDTO.getCycleId() == Constants.CYCLE_ID.MONTH) {
                                            serviceIds.put(vstrServiceName.trim() + " " + I18N.get("ranking.service.n"), vlngServiceId + Constants.TOP_BY_SERVICE.ONE);
                                        } else if (dashboardRequestDTO.getCycleId() == Constants.CYCLE_ID.QUARTER) {
                                            serviceIds.put(vstrServiceName.trim() + " " + I18N.get("ranking.service.quarter.n"), vlngServiceId + Constants.TOP_BY_SERVICE.ONE);
                                        }
                                        multiKeyMap.put(vstrShopName.trim(), vlngServiceId + Constants.TOP_BY_SERVICE.ONE, vstrScoreDetail);

                                        if (dashboardRequestDTO.getCycleId() == Constants.CYCLE_ID.DAY || dashboardRequestDTO.getCycleId() == Constants.CYCLE_ID.MONTH) {
                                            serviceIds.put(vstrServiceName.trim() + " " + I18N.get("ranking.service.n1.title"), vlngServiceId + Constants.TOP_BY_SERVICE.TWO);
                                        } else if (dashboardRequestDTO.getCycleId() == Constants.CYCLE_ID.QUARTER) {
                                            serviceIds.put(vstrServiceName.trim() + " " + I18N.get("ranking.service.quarter.n1.title"), vlngServiceId + Constants.TOP_BY_SERVICE.TWO);
                                        }
                                        multiKeyMap.put(vstrShopName.trim(), vlngServiceId + Constants.TOP_BY_SERVICE.TWO, vstrScoreDetailN1);

                                        break;
                                    case Constants.TOP_BY_SERVICE.THREE:
                                        if (dashboardRequestDTO.getCycleId() == Constants.CYCLE_ID.DAY || dashboardRequestDTO.getCycleId() == Constants.CYCLE_ID.MONTH) {
                                            serviceNames.add(vstrServiceName.trim() + " " + I18N.get("ranking.service.n"));
                                            serviceNames.add(vstrServiceName.trim() + " " + I18N.get("ranking.service.n1.title"));
                                            serviceNames.add(vstrServiceName.trim() + " " + I18N.get("ranking.service.n2.title"));
                                        } else if (dashboardRequestDTO.getCycleId() == Constants.CYCLE_ID.QUARTER) {
                                            serviceNames.add(vstrServiceName.trim() + " " + I18N.get("ranking.service.quarter.n"));
                                            serviceNames.add(vstrServiceName.trim() + " " + I18N.get("ranking.service.quarter.n1.title"));
                                            serviceNames.add(vstrServiceName.trim() + " " + I18N.get("ranking.service.quarter.n2.title"));
                                        }

                                        if (dashboardRequestDTO.getCycleId() == Constants.CYCLE_ID.DAY || dashboardRequestDTO.getCycleId() == Constants.CYCLE_ID.MONTH) {
                                            serviceIds.put(vstrServiceName.trim() + " " + I18N.get("ranking.service.n"), vlngServiceId + Constants.TOP_BY_SERVICE.ONE);
                                        } else if (dashboardRequestDTO.getCycleId() == Constants.CYCLE_ID.QUARTER) {
                                            serviceIds.put(vstrServiceName.trim() + " " + I18N.get("ranking.service.quarter.n"), vlngServiceId + Constants.TOP_BY_SERVICE.ONE);
                                        }
                                        multiKeyMap.put(vstrShopName.trim(), vlngServiceId + Constants.TOP_BY_SERVICE.ONE, vstrScoreDetail);

                                        if (dashboardRequestDTO.getCycleId() == Constants.CYCLE_ID.DAY || dashboardRequestDTO.getCycleId() == Constants.CYCLE_ID.MONTH) {
                                            serviceIds.put(vstrServiceName.trim() + " " + I18N.get("ranking.service.n1.title"), vlngServiceId + Constants.TOP_BY_SERVICE.TWO);
                                        } else if (dashboardRequestDTO.getCycleId() == Constants.CYCLE_ID.QUARTER) {
                                            serviceIds.put(vstrServiceName.trim() + " " + I18N.get("ranking.service.quarter.n1.title"), vlngServiceId + Constants.TOP_BY_SERVICE.TWO);
                                        }
                                        multiKeyMap.put(vstrShopName.trim(), vlngServiceId + Constants.TOP_BY_SERVICE.TWO, vstrScoreDetailN1);

                                        if (dashboardRequestDTO.getCycleId() == Constants.CYCLE_ID.DAY || dashboardRequestDTO.getCycleId() == Constants.CYCLE_ID.MONTH) {
                                            serviceIds.put(vstrServiceName.trim() + " " + I18N.get("ranking.service.n2.title"), vlngServiceId + Constants.TOP_BY_SERVICE.THREE);
                                        } else if (dashboardRequestDTO.getCycleId() == Constants.CYCLE_ID.QUARTER) {
                                            serviceIds.put(vstrServiceName.trim() + " " + I18N.get("ranking.service.quarter.n2.title"), vlngServiceId + Constants.TOP_BY_SERVICE.THREE);
                                        }
                                        multiKeyMap.put(vstrShopName.trim(), vlngServiceId + Constants.TOP_BY_SERVICE.THREE, vstrScoreDetailN2);

                                        break;
                                    default:
                                        serviceNames = null;
                                }
                            }

                        }
                        for (String serviceName : serviceNames) {
                            column = new TableColumnDTO();
                            column.setColumnId(vintIndex++);
                            column.setType(vstrType);
                            column.setName(serviceName);
                            column.setAlign(vstrAlign);
                            column.setValue(vstrValue);

                            vlstColumns.add(column);
                        }
                    }
                }

            } else if (Constants.VALUE.equalsIgnoreCase(vstrType)) break;

        }

        for (String shopName : shopNames) {
            int vintColumnIndex = 0;
            vintRowIndex++;
            List<TableCellBaseDTO> vlstCellBases = new ArrayList<>();

            for (int j = 0; j < vlstColumns.size(); j++) {
                    if (Constants.NORMAL.equalsIgnoreCase(vlstColumns.get(j).getType().trim())) {
                        TableCellBaseDTO cellBaseDTO = new TableCellBaseDTO();
                        cellBaseDTO.setColumnId(vintColumnIndex++);
                        cellBaseDTO.setViewValue(shopName);
                        cellBaseDTO.setColor(Constants.COLORS.BLACK);
                        vlstCellBases.add(cellBaseDTO);
                    } else if (Constants.PIVOT.equalsIgnoreCase(vlstColumns.get(j).getType().trim())) {
                        TableCellBaseDTO cellBaseDTO = new TableCellBaseDTO();
                        cellBaseDTO.setColumnId(vintColumnIndex++);

                        String vstrServiceId = serviceIds.get(vlstColumns.get(j).getName().trim());
                        String vdblAccumPerform = (String) multiKeyMap.get(shopName.trim(), vstrServiceId);

                        if (vdblAccumPerform != null) {
                            cellBaseDTO.setViewValue(vdblAccumPerform.toString());
                        }

                        vlstCellBases.add(cellBaseDTO);
                    } else if (Constants.NATIONAL.equalsIgnoreCase(vlstColumns.get(j).getType().trim())) {
                        TableCellBaseDTO cellBaseDTO = new TableCellBaseDTO();
                        cellBaseDTO.setColumnId(vintColumnIndex++);
                        String vstrServiceId = serviceIds.get(vlstColumns.get(j).getName().trim());
                        String abc = (String) multiKeyMap.get(shopName.trim(), Constants.SHOP_NAME);
                        cellBaseDTO.setViewValue(abc);
                        cellBaseDTO.setColor(Constants.COLORS.BLACK);
                        vlstCellBases.add(cellBaseDTO);
                    }
            }
            TableRowDTO tableRowDTO = new TableRowDTO();
            tableRowDTO.setRowIndex(vintRowIndex);
            tableRowDTO.setCells(vlstCellBases);
            vlstRows.add(tableRowDTO);

        }
        if (Constants.TOP_BY_SERVICE.ZERO.equals(dashboardRequestDTO.getpRow())) {
            tableDTO.setTitleColor(vstrBadColor);
        } else if (Constants.TOP_BY_SERVICE.THREE.equals(dashboardRequestDTO.getpRow()) ||
                Constants.TOP_BY_SERVICE.FOUR.equals(dashboardRequestDTO.getpRow())) {
            tableDTO.setTitleColor(vstrGoodColor);
        }
        tableDTO.setType(configSingleChartDTO.getChartType());
        tableDTO.setColumns(vlstColumns);
        tableDTO.setRows(vlstRows);
        tableDTO.setChartSize(configSingleChartDTO.getChartSize());

        //build title
        if (!DataUtil.isNullOrEmpty(vstrShortName)) {
            vlstParamTitleChart.add(vstrShortName);

            if (vstrPRow.equals(Constants.TOP_BY_SERVICE.ZERO)) {
                vlstParamTitleChart.add(String.valueOf(vintMucMot));
                vlstParamTitleChart.add(DataUtil.convertLocalDateToString(dashboardRequestDTO.getPrdId(), dashboardRequestDTO.getCycleId()));
                tableDTO.setTitle(I18N.get(Constants.TOP_BY_SERVICE.SCORE_PROWZERO, (String[]) vlstParamTitleChart.toArray(new String[vlstParamTitleChart.size()])));
            }
            if (vstrPRow.equals(Constants.TOP_BY_SERVICE.ONE)) {
                vlstParamTitleChart.add(String.valueOf(vintMucMot));
                vlstParamTitleChart.add(String.valueOf(vintMucHai));
                vlstParamTitleChart.add(DataUtil.convertLocalDateToString(dashboardRequestDTO.getPrdId(), dashboardRequestDTO.getCycleId()));
                tableDTO.setTitle(I18N.get(Constants.TOP_BY_SERVICE.SCORE_PROWONE, (String[]) vlstParamTitleChart.toArray(new String[vlstParamTitleChart.size()])));
            }
            if (vstrPRow.equals(Constants.TOP_BY_SERVICE.TWO)) {
                vlstParamTitleChart.add(String.valueOf(vintMucHai));
                vlstParamTitleChart.add(String.valueOf(vintMucBa));
                vlstParamTitleChart.add(DataUtil.convertLocalDateToString(dashboardRequestDTO.getPrdId(), dashboardRequestDTO.getCycleId()));
                tableDTO.setTitle(I18N.get(Constants.TOP_BY_SERVICE.SCORE_PROWONE, (String[]) vlstParamTitleChart.toArray(new String[vlstParamTitleChart.size()])));
            }
            if (vstrPRow.equals(Constants.TOP_BY_SERVICE.THREE)) {
                vlstParamTitleChart.add(String.valueOf(vintMucBa));
                vlstParamTitleChart.add(String.valueOf(vintMucBon));
                vlstParamTitleChart.add(DataUtil.convertLocalDateToString(dashboardRequestDTO.getPrdId(), dashboardRequestDTO.getCycleId()));
                tableDTO.setTitle(I18N.get(Constants.TOP_BY_SERVICE.SCORE_PROWONE, (String[]) vlstParamTitleChart.toArray(new String[vlstParamTitleChart.size()])));
            }
            if (vstrPRow.equals(Constants.TOP_BY_SERVICE.FOUR)) {
                vlstParamTitleChart.add(String.valueOf(vintMucBon));
                vlstParamTitleChart.add(DataUtil.convertLocalDateToString(dashboardRequestDTO.getPrdId(), dashboardRequestDTO.getCycleId()));
                tableDTO.setTitle(I18N.get(Constants.TOP_BY_SERVICE.SCORE_PROWTWO, (String[]) vlstParamTitleChart.toArray(new String[vlstParamTitleChart.size()])));
            }

        }
        return tableDTO;
    }
}
