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
import vn.vissoft.dashboard.dto.DashboardRequestDTO;
import vn.vissoft.dashboard.dto.LineDTO;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.chart.ChartDTO;
import vn.vissoft.dashboard.dto.chart.ConfigSingleChartDTO;
import vn.vissoft.dashboard.dto.chart.PointDTO;
import vn.vissoft.dashboard.dto.chart.SeriesDTO;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.model.Unit;
import vn.vissoft.dashboard.repo.ChartRepoCustom;
import vn.vissoft.dashboard.repo.PartnerRepo;
import vn.vissoft.dashboard.repo.impl.ChartRepoImpl;
import vn.vissoft.dashboard.services.ChartService;

import javax.transaction.Transactional;
import java.lang.reflect.Type;
import java.sql.Date;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

@Transactional
@Service
public class ChartServiceImpl implements ChartService {

    private static final Logger LOGGER = LogManager.getLogger(ChartServiceImpl.class);

    @Autowired
    private ChartRepoCustom chartRepoCustom;

    @Autowired
    private ChartRepoImpl chartRepoImpl;

    @Autowired
    private PartnerRepo partnerRepo;

    private NumberFormat format = NumberFormat.getInstance();
    private DecimalFormat df = new DecimalFormat("###.##");


    /**
     * lay ra du lieu cua bieu đo
     *
     * @param dashboardRequestDTO
     * @param configSingleChart
     * @return List<ChartDTO>
     * @author VuBL
     * @since 2019/09
     */
    @Override
    public ChartDTO getChart(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChart, StaffDTO staffDTO) throws Exception {
        StringBuilder logBuilder = new StringBuilder();
        logBuilder.append("*CHART(LV1)");
        long startTime = System.currentTimeMillis();
        long stepTime = System.currentTimeMillis();
        ChartDTO data = new ChartDTO();
        List<Long> vlstServiceIds = new ArrayList<>();
        List<SeriesDTO> vlstSeries = new ArrayList<>();
        int vintCycleId = dashboardRequestDTO.getCycleId();
//        Unit unit = null;
//        Double vdblNewRate = null;
//        Double vdblOldRate = null;
        String vstrUnitType = null;
//        List<String> vlstCategoriesHaveData = new ArrayList<>();
        //Xu ly ngay thang de lay ra chu thich cac duong cua bieu do
        Date vdtDate = new Date(dashboardRequestDTO.getPrdId());
        int vintMonth = vdtDate.toLocalDate().getMonthValue();
        int vintYear = vdtDate.toLocalDate().getYear();
        Calendar cal = Calendar.getInstance();
        cal.setTime(vdtDate);
        cal.add(Calendar.MONTH, -1);
        int vintLastMonth = new Date((cal.getTime()).getTime()).toLocalDate().getMonthValue();
        int vintYearLastMonth = new Date((cal.getTime()).getTime()).toLocalDate().getYear();
        cal.add(Calendar.MONTH, -1);
        int vintLastTwoMonth = new Date((cal.getTime()).getTime()).toLocalDate().getMonthValue();
        int vintYearLastTwoMonth = new Date((cal.getTime()).getTime()).toLocalDate().getYear();
        cal.add(Calendar.MONTH, 2);
        cal.add(Calendar.YEAR, -1);
//        Date vdtDateLastYear = new Date((cal.getTime()).getTime());
        int vintLastYear = new Date((cal.getTime()).getTime()).toLocalDate().getYear();
        String vstrMonth = String.valueOf(vintMonth);
        if (vstrMonth.length() == 1) {
            vstrMonth = 0 + vstrMonth;
        }
        String vstrLastMonth = String.valueOf(vintLastMonth);
        if (vstrLastMonth.length() == 1) {
            vstrLastMonth = 0 + vstrLastMonth;
        }
        String vstrLastTwoMonth = String.valueOf(vintLastTwoMonth);
        if (vstrLastTwoMonth.length() == 1) {
            vstrLastTwoMonth = 0 + vstrLastTwoMonth;
        }

        //lay ma don vi cho title bieu do
//        String vstrShortName = partnerRepo.getNameByShopAndChannel(dashboardRequestDTO.getObjectCode());
        String vstrShopCodeOfTitle = partnerRepo.getNameByShopAndChannel(dashboardRequestDTO.getObjectCode());

        //lay service_id cua bieu do
        if (!DataUtil.isNullOrEmpty(configSingleChart.getServiceIds())) {
            String[] varrServiceIds = configSingleChart.getServiceIds().split(",");
            for (int i = 0; i < varrServiceIds.length; i++) {
                vlstServiceIds.add(Long.valueOf(varrServiceIds[i].trim()));
            }
        }
//        String vstrUnitType = chartRepoImpl.getDefaultUnitType(vlstServiceIds.get(0), vintCycleId);

        //lay line cua bieu do tu metaData
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = (JsonObject) jsonParser.parse(configSingleChart.getMetaData());
        JsonArray varrLines = (JsonArray) jsonObject.get("lines");
        Type type = new TypeToken<ArrayList<LineDTO>>() {
        }.getType();
        List<LineDTO> vlstLines = new Gson().fromJson(varrLines.toString(), type);

        //set title cua bieu do theo chu ki
        if (!DataUtil.isNullOrEmpty(configSingleChart.getTitleI18n())) {
            if (!DataUtil.isNullOrEmpty(vstrShopCodeOfTitle)) {
                switch (dashboardRequestDTO.getCycleId()) {
                    case 1:
                        data.setTitle(vstrShopCodeOfTitle + " " + I18N.get(configSingleChart.getTitleI18n()) + I18N.get("linechart.cycle.day"));
                        break;
                    case 2:
                        data.setTitle(vstrShopCodeOfTitle + " " + I18N.get(configSingleChart.getTitleI18n()) + I18N.get("linechart.cycle.month"));
                        break;
                    case 3:
                        data.setTitle(vstrShopCodeOfTitle + " " + I18N.get(configSingleChart.getTitleI18n()) + I18N.get("linechart.cycle.quarter"));
                        break;
                }
            }
        } else {
            data.setTitle(configSingleChart.getTitle());
        }

        data.setType(Constants.CHARTTYPE.LINE_CHART);
        data.setChartType(Constants.CHARTTYPE.LINE_CHART);
        data.setChartSize(configSingleChart.getChartSize());
        if (!DataUtil.isNullOrEmpty(configSingleChart.getSubtitle())) {
            data.setSubTitle(configSingleChart.getSubtitle());
        }
        List<String> categories = chartRepoImpl.getCategories(dashboardRequestDTO.getPrdId(), Constants.LINE_VALUE.MONTH, vintCycleId);

        data.setCategories(categories);

        //no data
//            for (int j = 0; j < vlstServiceIds.size(); j++) {

        logBuilder.append(" - BAT DAU QUERY "+(System.currentTimeMillis()-stepTime)+"ms");
        List<Object[]> vlstData = getAllDataChart(dashboardRequestDTO, vlstServiceIds.get(0), staffDTO, configSingleChart);
        logBuilder.append(" - QUERY XONG "+(System.currentTimeMillis()-stepTime)+"ms");
        logBuilder.append(" - BAT DAU PHAN TICH "+(System.currentTimeMillis()-stepTime)+"ms");
        if (!DataUtil.isNullOrEmpty(vlstData)) {
            data.setNoData(false);
        } else {
            data.setNoData(true);
        }
//            }

        //lay don vi tinh mac dinh trong bang tong hop
        if (!DataUtil.isNullOrEmpty(vlstData)) {
            if (vintCycleId == Constants.CYCLE_ID.DAY) {
                String vstrAllDvt = (String) vlstData.get(0)[12];
                String vstrAllDvtNew = (String) vlstData.get(0)[14];
                if (vstrAllDvtNew != null) {
                    vstrUnitType = vstrAllDvtNew;
                } else {
                    if (vstrAllDvt != null) {
                        vstrUnitType = vstrAllDvt;
                    }
                }
            }
            if (vintCycleId == Constants.CYCLE_ID.MONTH || vintCycleId == Constants.CYCLE_ID.QUARTER) {
                String vstrAllDvt = (String) vlstData.get(0)[8];
                String vstrAllDvtNew = (String) vlstData.get(0)[10];
                if (vstrAllDvtNew != null) {
                    vstrUnitType = vstrAllDvtNew;
                } else {
                    if (vstrAllDvt != null) {
                        vstrUnitType = vstrAllDvt;
                    }
                }
            }
        }

        //lay don vi tinh
//        if (!DataUtil.isNullOrEmpty(vlstData)) {
//            if (vintCycleId == Constants.CYCLE_ID.DAY) {
//                Long vlngServiceId = vlstServiceIds.get(0);
//                String vstrShopCode = String.valueOf(vlstData.get(0)[11]);
//                String vstrChannelCode;
//                if (DataUtil.isNullObject(vlstData.get(0)[10])) {
//                    vstrChannelCode = null;
//                } else {
//                    vstrChannelCode = String.valueOf(vlstData.get(0)[10]);
//                }
//                unit = chartRepoImpl.getNewUnitType(vlngServiceId, vstrShopCode, vstrChannelCode);
//            }
//            if (vintCycleId == Constants.CYCLE_ID.MONTH || vintCycleId == Constants.CYCLE_ID.QUARTER) {
//                Long vlngServiceId = vlstServiceIds.get(0);
//                String vstrShopCode = String.valueOf(vlstData.get(0)[7]);
//                String vstrChannelCode = null;
//                if (!DataUtil.isNullObject(vlstData.get(0)[6])) {
//                    vstrChannelCode = String.valueOf(vlstData.get(0)[6]);
//                }
//                unit = chartRepoImpl.getNewUnitType(vlngServiceId, vstrShopCode, vstrChannelCode);
//            }
//        }
//        if (!DataUtil.isNullObject(unit)) {
//            vstrUnitType = unit.getName();
//            vdblNewRate = unit.getRate();
//            String vstrCode = null;
//            if (vintCycleId == Constants.CYCLE_ID.DAY) {
//                vstrCode = String.valueOf(vlstData.get(0)[9]);
//            }
//            if (vintCycleId == Constants.CYCLE_ID.MONTH || vintCycleId == Constants.CYCLE_ID.QUARTER) {
//                vstrCode = String.valueOf(vlstData.get(0)[5]);
//            }
//            vdblOldRate = chartRepoImpl.getOldRate(vstrCode);
//        }

        //set series va points
        if (!DataUtil.isNullOrEmpty(vlstLines)) {
            for (int i = 0; i < vlstLines.size(); i++) {
//                    for (int j = 0; j < vlstServiceIds.size(); j++) {
//                        List<Object[]> vlstData = getAllDataChart(dashboardRequestDTO, vlstServiceIds.get(0), staffDTO);

                SeriesDTO serie = null;
                List<PointDTO> vlstPoints = new ArrayList<>();
                LineDTO line = vlstLines.get(i);
                int vintCycleIdMetaData = line.getCycle();
                List<String> vlstAllCateGories = new ArrayList<>();
                List<PointDTO> vlstPointNoData = new ArrayList<>();
                List<String> vlstCategoriesHaveData = new ArrayList<>();

                if (vintCycleId == vintCycleIdMetaData) {
                    serie = new SeriesDTO();
                }

                if (serie != null) {
                    serie.setColor(line.getColor());
                    serie.setAverage(Boolean.parseBoolean(line.getAverage()));
                    serie.setChartType(Constants.CHARTTYPE.LINE_CHART);
                    serie.setUnitType(vstrUnitType);

                    if (!DataUtil.isNullOrEmpty(vlstData)) {
                        //CHU KI NGAY
                        //th ngay
                        if (line.getValue().equals(Constants.LINE_VALUE.PERFORM)) {
                            //add categories co data
                            for (int j = 0; j < vlstData.size(); j++) {
                                if (vlstData.get(j)[5] != null) {
                                    vlstCategoriesHaveData.add(DataUtil.safeToString(vlstData.get(j)[5]));
                                }
                            }
                            vlstAllCateGories = chartRepoImpl.getCategories(dashboardRequestDTO.getPrdId(), Constants.LINE_VALUE.MONTH, vintCycleId);
                            serie.setTitle(I18N.get("note.chart.th") + " T" + vstrMonth + "/" + vintYear);
                            for (int k = 0; k < vlstData.size(); k++) {
                                //xu ly dvt
                                String vstrDvt = (String) vlstData.get(k)[12];
                                String vstrDvtNew = (String) vlstData.get(k)[14];
                                String vstrDvtUse = null;
                                if (vstrDvtNew != null) {
                                    vstrDvtUse = vstrDvtNew;
                                } else {
                                    if (vstrDvt != null) {
                                        vstrDvtUse = vstrDvt;
                                    }
                                }
                                //xu ly rate
                                //rate
                                Integer vintOldRate = null;
                                Integer vintNewRate = null;
                                if (vlstData.get(k)[13] != null) {
                                    vintOldRate = DataUtil.safeToInt(vlstData.get(k)[13]);
                                }
                                if (vlstData.get(k)[15] != null) {
                                    vintNewRate = DataUtil.safeToInt(vlstData.get(k)[15]);
                                }
                                Double vdblPerformValue = (Double) vlstData.get(k)[0];
                                if (!DataUtil.isNullOrZero(vdblPerformValue) && !DataUtil.isNullOrZero(vintNewRate) && !DataUtil.isNullOrZero(vintOldRate)) {
                                    vdblPerformValue = vdblPerformValue * vintOldRate / vintNewRate;
                                }
                                if (vdblPerformValue != null) {
                                    if (vlstData.get(k)[5] != null) {
                                        Double vdblValue = Double.valueOf(df.format(vdblPerformValue));
                                        PointDTO dataPoint = new PointDTO();
                                        dataPoint.setCategory(DataUtil.safeToString(vlstData.get(k)[5]));
                                        if (vdblValue != null) {
                                            dataPoint.setValue(vdblValue);
                                        }
                                        if (DataUtil.isNullOrEmpty(format.format(vdblValue))) {
                                            dataPoint.setViewValue(null);
                                        } else {
                                            dataPoint.setViewValue(format.format(vdblValue) + " " + (vstrDvtUse == null ? "" : vstrDvtUse));
                                        }
                                        vlstPoints.add(dataPoint);
                                    }
                                } else {
                                    if (vlstData.get(k)[5] != null) {
                                        PointDTO dataPoint = new PointDTO();
                                        dataPoint.setCategory(DataUtil.safeToString(vlstData.get(k)[5]));
                                        vlstPoints.add(dataPoint);
                                    }
                                }
                            }
                        }
                        //th ngay cua thang -1
                        if (line.getValue().equals(Constants.LINE_VALUE.PERFORM_MONTH_N1)) {
                            //add categories co data
                            for (int j = 0; j < vlstData.size(); j++) {
                                if (vlstData.get(j)[6] != null) {
                                    vlstCategoriesHaveData.add(DataUtil.safeToString(vlstData.get(j)[6]));
                                }
                            }
                            vlstAllCateGories = chartRepoImpl.getCategories(dashboardRequestDTO.getPrdId(), Constants.LINE_VALUE.LAST_MONTH, vintCycleId);
                            serie.setTitle(I18N.get("note.chart.th") + " T" + vstrLastMonth + "/" + vintYearLastMonth);
                            for (int k = 0; k < vlstData.size(); k++) {
                                //xu ly dvt
                                String vstrDvt = (String) vlstData.get(k)[12];
                                String vstrDvtNew = (String) vlstData.get(k)[14];
                                String vstrDvtUse = null;
                                if (vstrDvtNew != null) {
                                    vstrDvtUse = vstrDvtNew;
                                } else {
                                    if (vstrDvt != null) {
                                        vstrDvtUse = vstrDvt;
                                    }
                                }
                                //xu ly rate
                                //rate
                                Integer vintOldRate = null;
                                Integer vintNewRate = null;
                                if (vlstData.get(k)[13] != null) {
                                    vintOldRate = DataUtil.safeToInt(vlstData.get(k)[13]);
                                }
                                if (vlstData.get(k)[15] != null) {
                                    vintNewRate = DataUtil.safeToInt(vlstData.get(k)[15]);
                                }

                                Double vdblPerformValue = (Double) vlstData.get(k)[1];
                                if (!DataUtil.isNullOrZero(vdblPerformValue) && !DataUtil.isNullOrZero(vintNewRate) && !DataUtil.isNullOrZero(vintOldRate)) {
                                    vdblPerformValue = vdblPerformValue * vintOldRate / vintNewRate;
                                }
                                if (vdblPerformValue != null) {
                                    if (vlstData.get(k)[6] != null) {
                                        Double vdblValue = Double.valueOf(df.format(vdblPerformValue));
                                        PointDTO dataPoint = new PointDTO();
                                        dataPoint.setCategory(DataUtil.safeToString(vlstData.get(k)[6]));
                                        if (vdblValue != null) {
                                            dataPoint.setValue(vdblValue);
                                        }
                                        if (DataUtil.isNullOrEmpty(format.format(vdblValue))) {
                                            dataPoint.setViewValue(null);
                                        } else {
                                            dataPoint.setViewValue(format.format(vdblValue) + " " + (vstrDvtUse == null ? "" : vstrDvtUse));
                                        }
                                        vlstPoints.add(dataPoint);
                                    }
                                } else {
                                    if (vlstData.get(k)[6] != null) {
                                        PointDTO dataPoint = new PointDTO();
                                        dataPoint.setCategory(DataUtil.safeToString(vlstData.get(k)[6]));
                                        vlstPoints.add(dataPoint);
                                    }
                                }
                            }
                        }
                        //th ngay cua thang -2
                        if (line.getValue().equals(Constants.LINE_VALUE.PERFORM_MONTH_N2)) {
                            //add categories co data
                            for (int j = 0; j < vlstData.size(); j++) {
                                if (vlstData.get(j)[7] != null) {
                                    vlstCategoriesHaveData.add(DataUtil.safeToString(vlstData.get(j)[7]));
                                }
                            }
                            vlstAllCateGories = chartRepoImpl.getCategories(dashboardRequestDTO.getPrdId(), Constants.LINE_VALUE.LAST_TWO_MONTH, vintCycleId);
                            serie.setTitle(I18N.get("note.chart.th") + " T" + vstrLastTwoMonth + "/" + vintYearLastTwoMonth);
                            for (int k = 0; k < vlstData.size(); k++) {
                                //xu ly dvt
                                String vstrDvt = (String) vlstData.get(k)[12];
                                String vstrDvtNew = (String) vlstData.get(k)[14];
                                String vstrDvtUse = null;
                                if (vstrDvtNew != null) {
                                    vstrDvtUse = vstrDvtNew;
                                } else {
                                    if (vstrDvt != null) {
                                        vstrDvtUse = vstrDvt;
                                    }
                                }
                                //xu ly rate
                                //rate
                                Integer vintOldRate = null;
                                Integer vintNewRate = null;
                                if (vlstData.get(k)[13] != null) {
                                    vintOldRate = DataUtil.safeToInt(vlstData.get(k)[13]);
                                }
                                if (vlstData.get(k)[15] != null) {
                                    vintNewRate = DataUtil.safeToInt(vlstData.get(k)[15]);
                                }

                                Double vdblPerformValue = (Double) vlstData.get(k)[2];
                                if (!DataUtil.isNullOrZero(vdblPerformValue) && !DataUtil.isNullOrZero(vintNewRate) && !DataUtil.isNullOrZero(vintOldRate)) {
                                    vdblPerformValue = vdblPerformValue * vintOldRate / vintNewRate;
                                }
                                if (vdblPerformValue != null) {
                                    if (vlstData.get(k)[7] != null) {
                                        Double vdblValue = Double.valueOf(df.format(vdblPerformValue));
                                        PointDTO dataPoint = new PointDTO();
                                        dataPoint.setCategory(DataUtil.safeToString(vlstData.get(k)[7]));
                                        if (vdblValue != null) {
                                            dataPoint.setValue(vdblValue);
                                        }
                                        if (DataUtil.isNullOrEmpty(format.format(vdblValue))) {
                                            dataPoint.setViewValue(null);
                                        } else {
                                            dataPoint.setViewValue(format.format(vdblValue) + " " + (vstrDvtUse == null ? "" : vstrDvtUse));
                                        }
                                        vlstPoints.add(dataPoint);
                                    }
                                } else {
                                    if (vlstData.get(k)[7] != null) {
                                        PointDTO dataPoint = new PointDTO();
                                        dataPoint.setCategory(DataUtil.safeToString(vlstData.get(k)[7]));
                                        vlstPoints.add(dataPoint);
                                    }
                                }
                            }
                        }
                        //th ngay cua nam -1
                        if (line.getValue().equals(Constants.LINE_VALUE.PERFORM_YEAR_N1)) {
                            //add categories co data
                            for (int j = 0; j < vlstData.size(); j++) {
                                if (vlstData.get(j)[8] != null) {
                                    vlstCategoriesHaveData.add(DataUtil.safeToString(vlstData.get(j)[8]));
                                }
                            }
                            vlstAllCateGories = chartRepoImpl.getCategories(dashboardRequestDTO.getPrdId(), Constants.LINE_VALUE.LAST_YEAR, vintCycleId);
                            serie.setTitle(I18N.get("note.chart.th") + " T" + vstrMonth + "/" + vintLastYear);
                            for (int k = 0; k < vlstData.size(); k++) {
                                //xu ly dvt
                                String vstrDvt = (String) vlstData.get(k)[12];
                                String vstrDvtNew = (String) vlstData.get(k)[14];
                                String vstrDvtUse = null;
                                if (vstrDvtNew != null) {
                                    vstrDvtUse = vstrDvtNew;
                                } else {
                                    if (vstrDvt != null) {
                                        vstrDvtUse = vstrDvt;
                                    }
                                }
                                //xu ly rate
                                //rate
                                Integer vintOldRate = null;
                                Integer vintNewRate = null;
                                if (vlstData.get(k)[13] != null) {
                                    vintOldRate = DataUtil.safeToInt(vlstData.get(k)[13]);
                                }
                                if (vlstData.get(k)[15] != null) {
                                    vintNewRate = DataUtil.safeToInt(vlstData.get(k)[15]);
                                }

                                Double vdblPerformValue = (Double) vlstData.get(k)[3];
                                if (!DataUtil.isNullOrZero(vdblPerformValue) && !DataUtil.isNullOrZero(vintNewRate) && !DataUtil.isNullOrZero(vintOldRate)) {
                                    vdblPerformValue = vdblPerformValue * vintOldRate / vintNewRate;
                                }
                                if (vdblPerformValue != null) {
                                    if (vlstData.get(k)[8] != null) {
                                        Double vdblValue = Double.valueOf(df.format(vdblPerformValue));
                                        PointDTO dataPoint = new PointDTO();
                                        dataPoint.setCategory(DataUtil.safeToString(vlstData.get(k)[8]));
                                        if (vdblValue != null) {
                                            dataPoint.setValue(vdblValue);
                                        }
                                        if (DataUtil.isNullOrEmpty(format.format(vdblValue))) {
                                            dataPoint.setViewValue(null);
                                        } else {
                                            dataPoint.setViewValue(format.format(vdblValue) + " " + (vstrDvtUse == null ? "" : vstrDvtUse));
                                        }
                                        vlstPoints.add(dataPoint);
                                    }
                                } else {
                                    if (vlstData.get(k)[8] != null) {
                                        PointDTO dataPoint = new PointDTO();
                                        dataPoint.setCategory(DataUtil.safeToString(vlstData.get(k)[8]));
                                        vlstPoints.add(dataPoint);
                                    }
                                }
                            }
                        }
                        //kh ngay
                        if (line.getValue().equals(Constants.LINE_VALUE.SCHEDULE)) {
                            //add categories co data
                            for (int j = 0; j < vlstData.size(); j++) {
                                if (vlstData.get(j)[5] != null) {
                                    vlstCategoriesHaveData.add(DataUtil.safeToString(vlstData.get(j)[5]));
                                }
                            }
                            vlstAllCateGories = chartRepoImpl.getCategories(dashboardRequestDTO.getPrdId(), Constants.LINE_VALUE.MONTH, vintCycleId);
                            serie.setTitle(I18N.get("note.chart.kh") + " T" + vstrMonth + "/" + vintYear);
                            for (int k = 0; k < vlstData.size(); k++) {
                                //xu ly dvt
                                String vstrDvt = (String) vlstData.get(k)[12];
                                String vstrDvtNew = (String) vlstData.get(k)[14];
                                String vstrDvtUse = null;
                                if (vstrDvtNew != null) {
                                    vstrDvtUse = vstrDvtNew;
                                } else {
                                    if (vstrDvt != null) {
                                        vstrDvtUse = vstrDvt;
                                    }
                                }
                                //xu ly rate
                                //rate
                                Integer vintOldRate = null;
                                Integer vintNewRate = null;
                                if (vlstData.get(k)[13] != null) {
                                    vintOldRate = DataUtil.safeToInt(vlstData.get(k)[13]);
                                }
                                if (vlstData.get(k)[15] != null) {
                                    vintNewRate = DataUtil.safeToInt(vlstData.get(k)[15]);
                                }

                                Double vdblPerformValue = (Double) vlstData.get(k)[4];
                                if (!DataUtil.isNullOrZero(vdblPerformValue) && !DataUtil.isNullOrZero(vintNewRate) && !DataUtil.isNullOrZero(vintOldRate)) {
                                    vdblPerformValue = vdblPerformValue * vintOldRate / vintNewRate;
                                }
                                if (vdblPerformValue != null) {
                                    if (vlstData.get(k)[5] != null) {
                                        Double vdblValue = Double.valueOf(df.format(vdblPerformValue));
                                        PointDTO dataPoint = new PointDTO();
                                        dataPoint.setCategory(DataUtil.safeToString(vlstData.get(k)[5]));
                                        if (vdblValue != null) {
                                            dataPoint.setValue(vdblValue);
                                        }
                                        if (DataUtil.isNullOrEmpty(format.format(vdblValue))) {
                                            dataPoint.setViewValue(null);
                                        } else {
                                            dataPoint.setViewValue(format.format(vdblValue) + " " + (vstrDvtUse == null ? "" : vstrDvtUse));
                                        }
                                        vlstPoints.add(dataPoint);
                                    }
                                } else {
                                    if (vlstData.get(k)[5] != null) {
                                        PointDTO dataPoint = new PointDTO();
                                        dataPoint.setCategory(DataUtil.safeToString(vlstData.get(k)[5]));
                                        vlstPoints.add(dataPoint);
                                    }
                                }
                            }
                        }

                        //CHU KI THANG
                        //th thang
                        if (line.getValue().equals(Constants.LINE_VALUE.PERFORM_MONTH)) {
                            //add categories co data
                            for (int j = 0; j < vlstData.size(); j++) {
                                if (vlstData.get(j)[3] != null) {
                                    vlstCategoriesHaveData.add(DataUtil.safeToString(vlstData.get(j)[3]));
                                }
                            }
                            vlstAllCateGories = chartRepoImpl.getCategories(dashboardRequestDTO.getPrdId(), Constants.LINE_VALUE.MONTH, vintCycleId);
                            serie.setTitle(I18N.get("note.chart.th") + " " + vintYear);
                            for (int k = 0; k < vlstData.size(); k++) {
                                //xu ly dvt
                                String vstrDvt = (String) vlstData.get(k)[8];
                                String vstrDvtNew = (String) vlstData.get(k)[10];
                                String vstrDvtUse = null;
                                if (vstrDvtNew != null) {
                                    vstrDvtUse = vstrDvtNew;
                                } else {
                                    if (vstrDvt != null) {
                                        vstrDvtUse = vstrDvt;
                                    }
                                }
                                //xu ly rate
                                //rate
                                Integer vintOldRate = null;
                                Integer vintNewRate = null;
                                if (vlstData.get(k)[9] != null) {
                                    vintOldRate = DataUtil.safeToInt(vlstData.get(k)[9]);
                                }
                                if (vlstData.get(k)[11] != null) {
                                    vintNewRate = DataUtil.safeToInt(vlstData.get(k)[11]);
                                }
                                Double vdblPerformValue = (Double) vlstData.get(k)[0];
                                if (!DataUtil.isNullOrZero(vdblPerformValue) && !DataUtil.isNullOrZero(vintNewRate) && !DataUtil.isNullOrZero(vintOldRate)) {
                                    vdblPerformValue = vdblPerformValue * vintOldRate / vintNewRate;
                                }
                                if (vdblPerformValue != null) {
                                    Double vdblValue = Double.valueOf(df.format(vdblPerformValue));
                                    PointDTO dataPoint = new PointDTO();
                                    dataPoint.setCategory(DataUtil.safeToString(vlstData.get(k)[3]));
                                    if (vdblValue != null) {
                                        dataPoint.setValue(vdblValue);
                                    }
                                    if (DataUtil.isNullOrEmpty(format.format(vdblValue))) {
                                        dataPoint.setViewValue(null);
                                    } else {
                                        dataPoint.setViewValue(format.format(vdblValue) + " " + (vstrDvtUse == null ? "" : vstrDvtUse));
                                    }
                                    vlstPoints.add(dataPoint);
                                } else {
                                    PointDTO dataPoint = new PointDTO();
                                    dataPoint.setCategory(DataUtil.safeToString(vlstData.get(k)[3]));
                                    vlstPoints.add(dataPoint);
                                }
                            }
                        }
                        //th thang nam -1
                        if (line.getValue().equals(Constants.LINE_VALUE.PERFORM_MONTH_YEAR_N1)) {
                            //add categories co data
                            for (int j = 0; j < vlstData.size(); j++) {
                                if (vlstData.get(j)[4] != null) {
                                    vlstCategoriesHaveData.add(DataUtil.safeToString(vlstData.get(j)[4]));
                                }
                            }
                            vlstAllCateGories = chartRepoImpl.getCategories(dashboardRequestDTO.getPrdId(), Constants.LINE_VALUE.LAST_YEAR, vintCycleId);
                            serie.setTitle(I18N.get("note.chart.th") + " " + vintLastYear);
                            for (int k = 0; k < vlstData.size(); k++) {
                                //xu ly dvt
                                String vstrDvt = (String) vlstData.get(k)[8];
                                String vstrDvtNew = (String) vlstData.get(k)[10];
                                String vstrDvtUse = null;
                                if (vstrDvtNew != null) {
                                    vstrDvtUse = vstrDvtNew;
                                } else {
                                    if (vstrDvt != null) {
                                        vstrDvtUse = vstrDvt;
                                    }
                                }
                                //xu ly rate
                                //rate
                                Integer vintOldRate = null;
                                Integer vintNewRate = null;
                                if (vlstData.get(k)[9] != null) {
                                    vintOldRate = DataUtil.safeToInt(vlstData.get(k)[9]);
                                }
                                if (vlstData.get(k)[11] != null) {
                                    vintNewRate = DataUtil.safeToInt(vlstData.get(k)[11]);
                                }
                                Double vdblPerformValue = (Double) vlstData.get(k)[2];
                                if (!DataUtil.isNullOrZero(vdblPerformValue) && !DataUtil.isNullOrZero(vintNewRate) && !DataUtil.isNullOrZero(vintOldRate)) {
                                    vdblPerformValue = vdblPerformValue * vintOldRate / vintNewRate;
                                }
                                if (vdblPerformValue != null) {
                                    Double vdblValue = Double.valueOf(df.format(vdblPerformValue));
                                    PointDTO dataPoint = new PointDTO();
                                    dataPoint.setCategory(DataUtil.safeToString(vlstData.get(k)[4]));
                                    if (vdblValue != null) {
                                        dataPoint.setValue(vdblValue);
                                    }
                                    if (DataUtil.isNullOrEmpty(format.format(vdblValue))) {
                                        dataPoint.setViewValue(null);
                                    } else {
                                        dataPoint.setViewValue(format.format(vdblValue) + " " + (vstrDvtUse == null ? "" : vstrDvtUse));
                                    }
                                    vlstPoints.add(dataPoint);
                                } else {
                                    PointDTO dataPoint = new PointDTO();
                                    dataPoint.setCategory(DataUtil.safeToString(vlstData.get(k)[4]));
                                    vlstPoints.add(dataPoint);
                                }
                            }
                        }
                        //kh thang
                        if (line.getValue().equals(Constants.LINE_VALUE.SCHEDULE_MONTH)) {
                            //add categories co data
                            for (int j = 0; j < vlstData.size(); j++) {
                                if (vlstData.get(j)[3] != null) {
                                    vlstCategoriesHaveData.add(DataUtil.safeToString(vlstData.get(j)[3]));
                                }
                            }
                            vlstAllCateGories = chartRepoImpl.getCategories(dashboardRequestDTO.getPrdId(), Constants.LINE_VALUE.MONTH, vintCycleId);
                            serie.setTitle(I18N.get("note.chart.kh") + " " + vintYear);
                            for (int k = 0; k < vlstData.size(); k++) {
                                //xu ly dvt
                                String vstrDvt = (String) vlstData.get(k)[8];
                                String vstrDvtNew = (String) vlstData.get(k)[10];
                                String vstrDvtUse = null;
                                if (vstrDvtNew != null) {
                                    vstrDvtUse = vstrDvtNew;
                                } else {
                                    if (vstrDvt != null) {
                                        vstrDvtUse = vstrDvt;
                                    }
                                }
                                //xu ly rate
                                //rate
                                Integer vintOldRate = null;
                                Integer vintNewRate = null;
                                if (vlstData.get(k)[9] != null) {
                                    vintOldRate = DataUtil.safeToInt(vlstData.get(k)[9]);
                                }
                                if (vlstData.get(k)[11] != null) {
                                    vintNewRate = DataUtil.safeToInt(vlstData.get(k)[11]);
                                }
                                Double vdblPerformValue = (Double) vlstData.get(k)[1];
                                if (!DataUtil.isNullOrZero(vdblPerformValue) && !DataUtil.isNullOrZero(vintNewRate) && !DataUtil.isNullOrZero(vintOldRate)) {
                                    vdblPerformValue = vdblPerformValue * vintOldRate / vintNewRate;
                                }
                                if (vdblPerformValue != null) {
                                    Double vdblValue = Double.valueOf(df.format(vdblPerformValue));
                                    PointDTO dataPoint = new PointDTO();
                                    dataPoint.setCategory(DataUtil.safeToString(vlstData.get(k)[3]));
                                    if (vdblValue != null) {
                                        dataPoint.setValue(vdblValue);
                                    }
                                    if (DataUtil.isNullOrEmpty(format.format(vdblValue))) {
                                        dataPoint.setViewValue(null);
                                    } else {
                                        dataPoint.setViewValue(format.format(vdblValue) + " " + (vstrDvtUse == null ? "" : vstrDvtUse));
                                    }
                                    vlstPoints.add(dataPoint);
                                } else {
                                    PointDTO dataPoint = new PointDTO();
                                    dataPoint.setCategory(DataUtil.safeToString(vlstData.get(k)[3]));
                                    vlstPoints.add(dataPoint);
                                }
                            }
                        }

                        //CHU KI QUY
                        //th quy
                        if (line.getValue().equals(Constants.LINE_VALUE.PERFORM_QUARTER)) {
                            //add categories co data
                            for (int j = 0; j < vlstData.size(); j++) {
                                if (vlstData.get(j)[3] != null) {
                                    vlstCategoriesHaveData.add(DataUtil.safeToString(vlstData.get(j)[3]));
                                }
                            }
                            vlstAllCateGories = chartRepoImpl.getCategories(dashboardRequestDTO.getPrdId(), Constants.LINE_VALUE.MONTH, vintCycleId);
                            serie.setTitle(I18N.get("note.chart.th") + " " + vintYear);
                            for (int k = 0; k < vlstData.size(); k++) {
                                //xu ly dvt
                                String vstrDvt = (String) vlstData.get(k)[8];
                                String vstrDvtNew = (String) vlstData.get(k)[10];
                                String vstrDvtUse = null;
                                if (vstrDvtNew != null) {
                                    vstrDvtUse = vstrDvtNew;
                                } else {
                                    if (vstrDvt != null) {
                                        vstrDvtUse = vstrDvt;
                                    }
                                }
                                //xu ly rate
                                //rate
                                Integer vintOldRate = null;
                                Integer vintNewRate = null;
                                if (vlstData.get(k)[9] != null) {
                                    vintOldRate = DataUtil.safeToInt(vlstData.get(k)[9]);
                                }
                                if (vlstData.get(k)[11] != null) {
                                    vintNewRate = DataUtil.safeToInt(vlstData.get(k)[11]);
                                }
                                Double vdblPerformValue = (Double) vlstData.get(k)[0];
                                if (!DataUtil.isNullOrZero(vdblPerformValue) && !DataUtil.isNullOrZero(vintNewRate) && !DataUtil.isNullOrZero(vintOldRate)) {
                                    vdblPerformValue = vdblPerformValue * vintOldRate / vintNewRate;
                                }
                                if (vdblPerformValue != null) {
                                    Double vdblValue = Double.valueOf(df.format(vdblPerformValue));
                                    PointDTO dataPoint = new PointDTO();
                                    dataPoint.setCategory(DataUtil.safeToString(vlstData.get(k)[3]));
                                    if (vdblValue != null) {
                                        dataPoint.setValue(vdblValue);
                                    }
                                    if (DataUtil.isNullOrEmpty(format.format(vdblValue))) {
                                        dataPoint.setViewValue(null);
                                    } else {
                                        dataPoint.setViewValue(format.format(vdblValue) + " " + (vstrDvtUse == null ? "" : vstrDvtUse));
                                    }
                                    vlstPoints.add(dataPoint);
                                } else {
                                    PointDTO dataPoint = new PointDTO();
                                    dataPoint.setCategory(DataUtil.safeToString(vlstData.get(k)[3]));
                                    vlstPoints.add(dataPoint);
                                }
                            }
                        }
                        //th quy nam -1
                        if (line.getValue().equals(Constants.LINE_VALUE.PERFORM_QUARTER_YEAR_N1)) {
                            //add categories co data
                            for (int j = 0; j < vlstData.size(); j++) {
                                if (vlstData.get(j)[4] != null) {
                                    vlstCategoriesHaveData.add(DataUtil.safeToString(vlstData.get(j)[4]));
                                }
                            }
                            vlstAllCateGories = chartRepoImpl.getCategories(dashboardRequestDTO.getPrdId(), Constants.LINE_VALUE.LAST_YEAR, vintCycleId);
                            serie.setTitle(I18N.get("note.chart.th") + " " + vintLastYear);
                            for (int k = 0; k < vlstData.size(); k++) {
                                //xu ly dvt
                                String vstrDvt = (String) vlstData.get(k)[8];
                                String vstrDvtNew = (String) vlstData.get(k)[10];
                                String vstrDvtUse = null;
                                if (vstrDvtNew != null) {
                                    vstrDvtUse = vstrDvtNew;
                                } else {
                                    if (vstrDvt != null) {
                                        vstrDvtUse = vstrDvt;
                                    }
                                }
                                //xu ly rate
                                //rate
                                Integer vintOldRate = null;
                                Integer vintNewRate = null;
                                if (vlstData.get(k)[9] != null) {
                                    vintOldRate = DataUtil.safeToInt(vlstData.get(k)[9]);
                                }
                                if (vlstData.get(k)[11] != null) {
                                    vintNewRate = DataUtil.safeToInt(vlstData.get(k)[11]);
                                }
                                Double vdblPerformValue = (Double) vlstData.get(k)[2];
                                if (!DataUtil.isNullOrZero(vdblPerformValue) && !DataUtil.isNullOrZero(vintNewRate) && !DataUtil.isNullOrZero(vintOldRate)) {
                                    vdblPerformValue = vdblPerformValue * vintOldRate / vintNewRate;
                                }
                                if (vdblPerformValue != null) {
                                    Double vdblValue = Double.valueOf(df.format(vdblPerformValue));
                                    PointDTO dataPoint = new PointDTO();
                                    dataPoint.setCategory(DataUtil.safeToString(vlstData.get(k)[4]));
                                    if (vdblValue != null) {
                                        dataPoint.setValue(vdblValue);
                                    }
                                    if (DataUtil.isNullOrEmpty(format.format(vdblValue))) {
                                        dataPoint.setViewValue(null);
                                    } else {
                                        dataPoint.setViewValue(format.format(vdblValue) + " " + (vstrDvtUse == null ? "" : vstrDvtUse));
                                    }
                                    vlstPoints.add(dataPoint);
                                } else {
                                    PointDTO dataPoint = new PointDTO();
                                    dataPoint.setCategory(DataUtil.safeToString(vlstData.get(k)[4]));
                                    vlstPoints.add(dataPoint);
                                }
                            }
                        }
                        //kh quy
                        if (line.getValue().equals(Constants.LINE_VALUE.SCHEDULE_QUARTER)) {
                            //add categories co data
                            for (int j = 0; j < vlstData.size(); j++) {
                                if (vlstData.get(j)[3] != null) {
                                    vlstCategoriesHaveData.add(DataUtil.safeToString(vlstData.get(j)[3]));
                                }
                            }
                            vlstAllCateGories = chartRepoImpl.getCategories(dashboardRequestDTO.getPrdId(), Constants.LINE_VALUE.MONTH, vintCycleId);
                            serie.setTitle(I18N.get("note.chart.kh") + " " + vintYear);
                            for (int k = 0; k < vlstData.size(); k++) {
                                //xu ly dvt
                                String vstrDvt = (String) vlstData.get(k)[8];
                                String vstrDvtNew = (String) vlstData.get(k)[10];
                                String vstrDvtUse = null;
                                if (vstrDvtNew != null) {
                                    vstrDvtUse = vstrDvtNew;
                                } else {
                                    if (vstrDvt != null) {
                                        vstrDvtUse = vstrDvt;
                                    }
                                }
                                //xu ly rate
                                //rate
                                Integer vintOldRate = null;
                                Integer vintNewRate = null;
                                if (vlstData.get(k)[9] != null) {
                                    vintOldRate = DataUtil.safeToInt(vlstData.get(k)[9]);
                                }
                                if (vlstData.get(k)[11] != null) {
                                    vintNewRate = DataUtil.safeToInt(vlstData.get(k)[11]);
                                }
                                Double vdblPerformValue = (Double) vlstData.get(k)[1];
                                if (!DataUtil.isNullOrZero(vdblPerformValue) && !DataUtil.isNullOrZero(vintNewRate) && !DataUtil.isNullOrZero(vintOldRate)) {
                                    vdblPerformValue = vdblPerformValue * vintOldRate / vintNewRate;
                                }
                                if (vdblPerformValue != null) {
                                    Double vdblValue = Double.valueOf(df.format(vdblPerformValue));
                                    PointDTO dataPoint = new PointDTO();
                                    dataPoint.setCategory(DataUtil.safeToString(vlstData.get(k)[3]));
                                    if (vdblValue != null) {
                                        dataPoint.setValue(vdblValue);
                                    }
                                    if (DataUtil.isNullOrEmpty(format.format(vdblValue))) {
                                        dataPoint.setViewValue(null);
                                    } else {
                                        dataPoint.setViewValue(format.format(vdblValue) + " " + (vstrDvtUse == null ? "" : vstrDvtUse));
                                    }
                                    vlstPoints.add(dataPoint);
                                } else {
                                    PointDTO dataPoint = new PointDTO();
                                    dataPoint.setCategory(DataUtil.safeToString(vlstData.get(k)[3]));
                                    vlstPoints.add(dataPoint);
                                }
                            }
                        }
                    }

                    //loai bo nhung categories da co du lieu de lay ra list categories bi thieu trong db
                    vlstAllCateGories.removeAll(vlstCategoriesHaveData);
                    //set du lieu nhung point khong co du lieu trong db
                    for (int h = 0; h < vlstAllCateGories.size(); h++) {
                        PointDTO point = new PointDTO();
                        point.setCategory(vlstAllCateGories.get(h));
                        vlstPointNoData.add(point);
                    }
                    //them cac point khong co du lieu trong db
                    vlstPoints.addAll(vlstPointNoData);
                    //Sap xep point theo thu tu ngay tang dan
                    if (vintCycleId == Constants.CYCLE_ID.DAY) {
                        Collections.sort(vlstPoints, new Comparator<PointDTO>() {
                            @Override
                            public int compare(PointDTO p1, PointDTO p2) {
                                if (p1 != null && p2 != null) {
                                    if (p1.getCategory() != null && p2.getCategory() != null) {
                                        java.util.Date dateOne = DataUtil.convertStringToDate(p1.getCategory());
                                        java.util.Date dateTwo = DataUtil.convertStringToDate(p2.getCategory());
                                        if (dateOne != null && dateTwo != null)
                                            return dateOne.compareTo(dateTwo);
                                    } else {
                                        return 0;
                                    }
                                }
                                return 0;
                            }
                        });
                    }
                    if (vintCycleId == Constants.CYCLE_ID.MONTH || vintCycleId == Constants.CYCLE_ID.QUARTER) {
                        Collections.sort(vlstPoints, new Comparator<PointDTO>() {
                            @Override
                            public int compare(PointDTO p1, PointDTO p2) {
                                if (p1 != null && p2 != null) {
                                    if (p1.getCategory() != null && p2.getCategory() != null) {
                                        return p1.getCategory().compareTo(p2.getCategory());
                                    } else {
                                        return 0;
                                    }
                                }
                                return 0;
                            }
                        });
                    }

                    serie.setPoints(vlstPoints);

                } else {
                    continue;
                }

                vlstSeries.add(serie);
//                    }
            }
        }
        data.setSeries(vlstSeries);
//        }
        logBuilder.append(" - PHAN TICH XONG "+(System.currentTimeMillis()-stepTime)+"ms");
        logBuilder.append(" - TONG THOI GIAN "+((System.currentTimeMillis()-startTime))+"ms");
        LOGGER.info(logBuilder.toString());

        return data;
    }

    /**
     * lay duong xu the cho table chi tiet
     *
     * @param plngServiceId
     * @param plngPrdId
     * @param pMetaData
     * @return ChartDTO
     * @throws Exception
     * @author VuBL
     * @since 2019/10
     */
    @Override
    public ChartDTO getTrendChart(Long plngServiceId, Long plngPrdId, String pstrShopCode, JsonArray pMetaData, int pintCycleId, String pstrChannelCode, String pstrStaffCode) throws Exception {
        ChartDTO data = new ChartDTO();
        List<PointDTO> vlstPoints;
        List<SeriesDTO> vlstSeries = new ArrayList<>();
        List<String> vlstCategories = new ArrayList<>();

        Type type = new TypeToken<ArrayList<LineDTO>>() {
        }.getType();
        List<LineDTO> vlstLines = new Gson().fromJson(pMetaData.toString(), type);
        LineDTO vLines = vlstLines.get(0);

        if (!DataUtil.isNullObject(vLines)) {
            SeriesDTO serie = new SeriesDTO();
//            vlstPoints = null;
            serie.setColor(vLines.getColor());
            serie.setAverage(Boolean.parseBoolean(vLines.getAverage()));
            serie.setChartType(Constants.CHARTTYPE.LINE_CHART);
            vlstPoints = chartRepoCustom.findDataTrendChart(plngServiceId, plngPrdId, pstrShopCode, pintCycleId, pstrChannelCode, pstrStaffCode);
            serie.setPoints(vlstPoints);
            vlstSeries.add(serie);

            for (int i = 0; i < vlstPoints.size(); i++) {
                vlstCategories.add(vlstPoints.get(i).getCategory());
            }
        }
        data.setSeries(vlstSeries);
        data.setCategories(vlstCategories);

        return data;
    }

    /**
     * lay duong xu the cho table chi tiet
     *
     * @param dashboardRequestDTO
     * @param pstrTrend
     * @param pMetaData
     * @return ChartDTO
     * @throws Exception
     * @author VuBL
     * @since 2019/10
     */
    @Override
    public ChartDTO getTrendChartTable(String pstrTrend, JsonArray pMetaData, DashboardRequestDTO dashboardRequestDTO) throws Exception {
        ChartDTO data = new ChartDTO();
        List<PointDTO> vlstPoints;
        List<SeriesDTO> vlstSeries = new ArrayList<>();
        List<String> vlstCategories = new ArrayList<>();

        Type type = new TypeToken<ArrayList<LineDTO>>() {
        }.getType();
        List<LineDTO> vlstLines = new Gson().fromJson(pMetaData.toString(), type);
        LineDTO vLines = vlstLines.get(0);

        if (!DataUtil.isNullObject(vLines)) {
            SeriesDTO serie = new SeriesDTO();
            serie.setColor(vLines.getColor());
            serie.setAverage(Boolean.parseBoolean(vLines.getAverage()));
            serie.setChartType(Constants.CHARTTYPE.LINE_CHART);
            vlstPoints = chartRepoCustom.findDataTrendChartTable(pstrTrend, dashboardRequestDTO);
            serie.setPoints(vlstPoints);
            vlstSeries.add(serie);

            for (int i = 0; i < vlstPoints.size(); i++) {
                vlstCategories.add(vlstPoints.get(i).getCategory());
            }
        }
        data.setSeries(vlstSeries);
        data.setCategories(vlstCategories);

        return data;
    }

    /**
     * lay duong xu the cho table chi tiet
     *
     * @param plstObjects
     * @param pMetaData
     * @param dashboardRequestDTO
     * @return
     * @throws Exception
     * @author VuBL
     * @since 2019/11
     */
    @Override
    public ChartDTO getTrendChildShop(String pstrTrend, JsonArray pMetaData, DashboardRequestDTO dashboardRequestDTO) throws Exception {
        ChartDTO data = new ChartDTO();
        List<PointDTO> vlstPoints;
        List<SeriesDTO> vlstSeries = new ArrayList<>();
        List<String> vlstCategories = new ArrayList<>();

        Type type = new TypeToken<ArrayList<LineDTO>>() {
        }.getType();
        List<LineDTO> vlstLines = new Gson().fromJson(pMetaData.toString(), type);
        LineDTO vLines = vlstLines.get(0);

        if (!DataUtil.isNullObject(vLines)) {
            SeriesDTO serie = new SeriesDTO();
            serie.setColor(vLines.getColor());
            serie.setAverage(Boolean.parseBoolean(vLines.getAverage()));
            serie.setChartType(Constants.CHARTTYPE.LINE_CHART);
            vlstPoints = chartRepoCustom.findDataTrendChildShop(pstrTrend, dashboardRequestDTO);
            serie.setPoints(vlstPoints);
            vlstSeries.add(serie);

            for (int i = 0; i < vlstPoints.size(); i++) {
                vlstCategories.add(vlstPoints.get(i).getCategory());
            }
        }
        data.setSeries(vlstSeries);
        data.setCategories(vlstCategories);

        return data;
    }

    /**
     * lay toan bo du lieu bieu do duong dashboard level 1
     *
     * @param dashboardRequestDTO
     * @param plngServiceId
     * @param staffDTO
     * @param configSingleChartDTO
     * @return
     * @throws Exception
     * @author VuBL
     * @since 2019/10
     */
    @Override
    public List<Object[]> getAllDataChart(DashboardRequestDTO dashboardRequestDTO, Long plngServiceId, StaffDTO staffDTO, ConfigSingleChartDTO configSingleChartDTO) throws Exception {
        return chartRepoCustom.findAllDataChart(dashboardRequestDTO, plngServiceId, staffDTO, configSingleChartDTO);
    }

}
