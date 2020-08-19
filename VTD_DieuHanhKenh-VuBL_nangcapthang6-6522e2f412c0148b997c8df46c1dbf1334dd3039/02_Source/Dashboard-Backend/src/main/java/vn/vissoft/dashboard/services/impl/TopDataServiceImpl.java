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
import vn.vissoft.dashboard.controller.ChannelController;
import vn.vissoft.dashboard.dto.DashboardRequestDTO;
import vn.vissoft.dashboard.dto.chart.*;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.repo.VdsKpiDetailRepo;
import vn.vissoft.dashboard.repo.VdsScoreRankingRepo;
import vn.vissoft.dashboard.services.TopDataService;

import javax.transaction.Transactional;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
public class TopDataServiceImpl implements TopDataService {

    private static final Logger LOGGER = LogManager.getLogger(TopDataServiceImpl.class);

    @Autowired
    private VdsKpiDetailRepo vdsKpiDetailRepo;

    @Autowired
    private VdsScoreRankingRepo vdsScoreRankingRepo;

    /**
     * lay ra du lieu cho top ben trai
     *
     * @param dashboardRequestDTO, configSingleChartDTO, staffDTO: điều kiện truyền vào của dashboard, the loai chart, staffDTO.shop_code lay thong tin ma don vi
     * @return : contentTopLeftDTO
     * @author: HungNN
     * @version: 1.0 (sau khi được sửa đổi sẽ tăng thêm)
     * @since: 2019/10/25
     */
    @Override
    public ContentTopLeftDTO getLeftTop(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChartDTO) throws Exception {
        ContentTopLeftDTO contentTopLeftDTO = new ContentTopLeftDTO();
        List<TopDataDTO> vlstTopData = new ArrayList<>();
        Integer vintCountResult = vdsScoreRankingRepo.countTop(dashboardRequestDTO);

        // lay du lieu tu metadata
        String metaData = configSingleChartDTO.getMetaData();
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = (JsonObject) jsonParser.parse(metaData);
        JsonArray varrLines = null;
        if (!DataUtil.isNullObject(jsonObject))
            varrLines = (JsonArray) jsonObject.get(I18N.get(Constants.SHOWS.line));
        Type type = new TypeToken<ArrayList<TopDataDTO>>() {
        }.getType();
        List<TopDataDTO> vlstLines = new Gson().fromJson(varrLines.toString(), type);

        int vintTopNum = 0;
        boolean vblIsBest = false;
        String vstrColor = "";
//        int vintCycle = dashboardRequestDTO.getCycleId();
        if (!DataUtil.isNullOrEmpty(vlstLines)) {
            for (int i = 0; i < vlstLines.size(); i++) {
                TopDataDTO data = vlstLines.get(i);
                JsonObject object = (JsonObject) varrLines.get(i);
                if (!DataUtil.isNullObject(object)) {
                    if (DataUtil.checkJson(object.get(I18N.get(Constants.SHOWS.topNum)))) {
                        vintTopNum = object.get(I18N.get(Constants.SHOWS.topNum)).getAsInt();
                    }
                    if (DataUtil.checkJson(object.get(I18N.get(Constants.SHOWS.color)))) {
                        vstrColor = object.get(I18N.get(Constants.SHOWS.color)).getAsString();
                    }
                    if (DataUtil.checkJson(object.get(I18N.get(Constants.SHOWS.isBest)))) {
                        vblIsBest = object.get(I18N.get(Constants.SHOWS.isBest)).getAsBoolean();
                    }
                    data.setColor(vstrColor);
                    data.setTopNum(vintTopNum);
                    data.setIsBest(vblIsBest);
                    if (data.getIsBest() == true) {
                        data.setName(I18N.get(Constants.SHOWS.SHOW_BEST));
                    } else {
                        data.setName(I18N.get(Constants.SHOWS.SHOW_WORST));
                    }
                    String shopName;
                        if (data.getIsBest() == true) {
                            List<Object[]> repoTopLeftMax;
                            if (vintCountResult >= 10) {
                                repoTopLeftMax = vdsScoreRankingRepo.getTopLeft(dashboardRequestDTO, vintTopNum, true);
                            } else {
                                repoTopLeftMax = vdsScoreRankingRepo.getTopLeft(dashboardRequestDTO, 2, true);
                            }
                            List<String> lstMaxDaily = new ArrayList<>();
                            for (Object[] objects : repoTopLeftMax) {
                                shopName = String.valueOf(objects[0]);
                                lstMaxDaily.add(shopName);
                            }
                            data.setData(lstMaxDaily);
                        } else {
                            List<Object[]> repoTopLeftMin;
                            if (vintCountResult >= 10) {
                                repoTopLeftMin = vdsScoreRankingRepo.getTopLeft(dashboardRequestDTO, vintTopNum, false);
                            } else {
                                repoTopLeftMin = vdsScoreRankingRepo.getTopLeft(dashboardRequestDTO, 2, false);
                            }
                            List<String> lstMinDaily = new ArrayList<>();
                            for (Object[] objects : repoTopLeftMin) {
                                shopName = String.valueOf(objects[0]);
                                lstMinDaily.add(shopName);
                            }
                            data.setData(lstMinDaily);
                        }
                    vlstTopData.add(data);
                }
            }
        }
        if (configSingleChartDTO.getDrilldown() == 1)
            contentTopLeftDTO.setDrilldown(true);
        else if (configSingleChartDTO.getDrilldown() == 0)
            contentTopLeftDTO.setDrilldown(false);
        if (!DataUtil.isNullOrZero(configSingleChartDTO.getDrillDownType()))
            contentTopLeftDTO.setDrilldownType(configSingleChartDTO.getDrillDownType());
        if (!DataUtil.isNullOrZero(configSingleChartDTO.getDrillDownObjectId()))
            contentTopLeftDTO.setDrilldownObject(configSingleChartDTO.getDrillDownObjectId().intValue());
        contentTopLeftDTO.setType(configSingleChartDTO.getChartType());
        contentTopLeftDTO.setLink("");
        contentTopLeftDTO.setTitle(I18N.get(configSingleChartDTO.getTitleI18n()));
        contentTopLeftDTO.setListTop(vlstTopData);
        return contentTopLeftDTO;
    }

    /**
     * lay ra du lieu cho top ben phai
     *
     * @param dashboardRequestDTO, configSingleChartDTO, staffDTO: điều kiện truyền vào của dashboard, the loai chart, staffDTO.shop_code lay thong tin ma don vi
     * @return : contentTopRightDTO
     * @author: HungNN
     * @version: 1.0 (sau khi được sửa đổi sẽ tăng thêm)
     * @since: 2019/10/27
     */
    @Override
    public ContentTopRightDTO getRightTop(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChartDTO) throws Exception {

        ContentTopRightDTO contentTopRightDTO = new ContentTopRightDTO();
        List<SummaryDTO> summaryDTOList = new ArrayList<>();

        Integer a = 0;
        Integer b = 0;
        Integer c = 0;

        //lay du lieu tu metadata
        String metaData = configSingleChartDTO.getMetaData();
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = (JsonObject) jsonParser.parse(metaData);
        JsonArray varrLines = null;
        if (!DataUtil.isNullObject(jsonObject))
            varrLines = (JsonArray) jsonObject.get(I18N.get(Constants.SHOWS.line));
        Type type = new TypeToken<ArrayList<SummaryDTO>>() {
        }.getType();
        List<SummaryDTO> vlstLines = new Gson().fromJson(varrLines.toString(), type);

        String vstrColor = "";
        boolean vblIsTarget = false;
        int vintCycle = dashboardRequestDTO.getCycleId();
        Object[] vobjData = vdsKpiDetailRepo.getTopRight(dashboardRequestDTO, vintCycle);
        if (vobjData != null) {
            a = (Integer) vobjData[0];
            b = (Integer) vobjData[1];
            c = a + b;
        }

        try {
            if (!DataUtil.isNullOrEmpty(vlstLines)) {
                for (int i = 0; i < varrLines.size(); i++) {
                    SummaryDTO summaryDTO = vlstLines.get(i);
                    JsonObject object = (JsonObject) varrLines.get(i);
                    if (DataUtil.checkJson(object.get(I18N.get(Constants.SHOWS.color)))) {
                        vstrColor = object.get(I18N.get(Constants.SHOWS.color)).getAsString();
                    }
                    if (DataUtil.checkJson(object.get(I18N.get(Constants.SHOWS.isTarget)))) {
                        vblIsTarget = object.get(I18N.get(Constants.SHOWS.isTarget)).getAsBoolean();
                    }
                    summaryDTO.setColor(vstrColor);
                    summaryDTO.setIsTarget(vblIsTarget);
                    if (summaryDTO.getIsTarget() == true) {
                        summaryDTO.setName(I18N.get(Constants.SHOWS.SHOW_TARGET));
                    } else {
                        summaryDTO.setName(I18N.get(Constants.SHOWS.SHOW_NOT_TARGET));
                    }
                    // ngay
                    if (!DataUtil.isNullOrZero(c)) {
                        Integer result = a * 100 / c;
                        if (summaryDTO.getIsTarget() == true) {
                            summaryDTO.setNum(a);
                            if (a * 100 % c < 50) {
                                summaryDTO.setViewPercent(result + "%");
                            } else {
                                summaryDTO.setViewPercent(result + 1 + "%");
                            }
                        } else {
                            summaryDTO.setNum(b);
                            if (a * 100 % c < 50) {
                                summaryDTO.setViewPercent(100 - result + "%");
                            } else {
                                summaryDTO.setViewPercent(100 - (result + 1) + "%");
                            }
                        }
                    }
                    summaryDTOList.add(summaryDTO);
                }
            }
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        if (configSingleChartDTO.getDrilldown() == 1)
            contentTopRightDTO.setDrilldown(true);
        else if (configSingleChartDTO.getDrilldown() == 0)
            contentTopRightDTO.setDrilldown(false);
        if (!DataUtil.isNullOrZero(configSingleChartDTO.getDrillDownType()))
            contentTopRightDTO.setDrilldownType(configSingleChartDTO.getDrillDownType());
        if (!DataUtil.isNullOrZero(configSingleChartDTO.getDrillDownObjectId()))
            contentTopRightDTO.setDrilldownObject(configSingleChartDTO.getDrillDownObjectId().intValue());
        contentTopRightDTO.setType(configSingleChartDTO.getChartType());
        contentTopRightDTO.setImage("");
        contentTopRightDTO.setMlstSummary(summaryDTOList);
        return contentTopRightDTO;
    }
}
