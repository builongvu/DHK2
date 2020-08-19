package vn.vissoft.dashboard.services.impl;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.vissoft.dashboard.config.I18N;
import vn.vissoft.dashboard.dto.DashboardRequestDTO;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.chart.*;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.repo.*;
import vn.vissoft.dashboard.repo.impl.ChartRepoImpl;
import vn.vissoft.dashboard.services.*;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private DashboardRepoCustom dashboardRepo;

    @Autowired
    private TableService tableService;

    @Autowired
    private ChartService chartService;

    @Autowired
    private TopDataService topDataService;

    @Autowired
    private PartnerRepo partnerRepo;

    @Autowired
    private TopSecondLevelService topSecondLevelService;

    @Autowired
    private ServiceRepo serviceRepo;

    @Autowired
    private ChartLevTwoService chartLevTwoService;

    @Autowired
    private ChartRepoImpl chartRepo;

    @Autowired
    private ConfigSingleCardRepo configSingleCardRepo;

    @Autowired
    ContentTopService contentTopService;

    /**
     * lay ra du lieu tong hop dashboard cua kenh
     *
     * @param dashboardRequestDTO
     * @param plngGroupId
     * @return dashboarddto
     * @throws Exception
     * @author HungNN
     * @version 1.0
     * @since 15/10/2019
     */
    @Override
    @Transactional
    public DashboardDTO getDashboard(DashboardRequestDTO dashboardRequestDTO, Long plngGroupId, StaffDTO staffDTO) throws Exception {
        DashboardDTO dashboardDTO = new DashboardDTO();
        dashboardDTO.setShopOfStaff(staffDTO.getShopCode());
        String vstrServiceName = serviceRepo.findNameById(dashboardRequestDTO.getServiceId());
        //lay param cho title chart level 2, 3
        List<String> vlstParamsTitleChart = Lists.newArrayList();
        String vstrShopCodeTitleChart = partnerRepo.getNameByShopAndChannel(dashboardRequestDTO.getObjectCode());
        if (!DataUtil.isNullOrEmpty(dashboardRequestDTO.getParentShopCode())) {
            vstrShopCodeTitleChart = partnerRepo.getNameByShopAndChannel(dashboardRequestDTO.getParentShopCode());
        }
        vlstParamsTitleChart.add(vstrShopCodeTitleChart);
        vlstParamsTitleChart.add(DataUtil.convertLocalDateToString(dashboardRequestDTO.getPrdId(), dashboardRequestDTO.getCycleId()));

        //lay param title cho title chart lv3 cua card 2 lv2
        List<String> vlstParamTitleLevThree = Lists.newArrayList();
        vlstParamTitleLevThree.add(vstrShopCodeTitleChart);
        vlstParamTitleLevThree.add(serviceRepo.findNameById(serviceRepo.findParentIdById(dashboardRequestDTO.getServiceId())));
        vlstParamTitleLevThree.add(vstrServiceName);
        vlstParamTitleLevThree.add(DataUtil.convertLocalDateToString(dashboardRequestDTO.getPrdId(), dashboardRequestDTO.getCycleId()));

        if (dashboardRequestDTO.getObjectCode() == null) return null;
        else {
            dashboardDTO.setFilterMetaData(dashboardRequestDTO);
            List<ConfigSingleCardDTO> vlstConfigCard = dashboardRepo.getSingleCard(plngGroupId);
            List<CardDTO> vlstCard = new ArrayList<>();
            int vintChartId = 0;

            ConfigGroupCardDTO groupCardDTO = dashboardRepo.getConfigGroup(plngGroupId);
            // set du lieu cho DashboardDTO
            dashboardDTO.setGroupId(groupCardDTO.getGroupId());
            dashboardDTO.setVdsChannelCode(groupCardDTO.getVdsChannelCode());
            dashboardDTO.setGroupName(groupCardDTO.getGroupNameI18n() == null ? groupCardDTO.getGroupName() : I18N.get(groupCardDTO.getGroupNameI18n()));

            dashboardDTO.setSubGroupName(partnerRepo.getNameByShopAndChannel(dashboardRequestDTO.getObjectCode()) + " - " + DataUtil.convertLocalDateToString(dashboardRequestDTO.getPrdId(), dashboardRequestDTO.getCycleId()));
            if (DataUtil.isNullOrZero(dashboardRequestDTO.getCycleId())) {
                dashboardDTO.setDefaultCycle(groupCardDTO.getDefaultCycle());
            } else {
                dashboardDTO.setDefaultCycle(null);
            }
            //moi
            List<String> strCardType = configSingleCardRepo.lstCardDynamic(plngGroupId.intValue());
            if (strCardType.contains(Constants.DYNAMIC)) {
                List<Long> lstServiceChild = chartRepo.getChildServiceId(dashboardRequestDTO.getServiceId());
                for (Long data : lstServiceChild) {
                    //lay ra ten service de cho len title card
                    String vstrServiceNameParam = serviceRepo.findNameById(data);
                    if (vlstParamsTitleChart.size() == 2) {
                        vlstParamsTitleChart.add(1, vstrServiceNameParam);
                    } else {
                        vlstParamsTitleChart.set(1, vstrServiceNameParam);
                    }

                    dashboardRequestDTO.setServiceId(data);
                    for (ConfigSingleCardDTO configSingleCardDTO : vlstConfigCard) {
                        List<ContentBaseDTO> contentBaseDTOS = new ArrayList<>();
                        List<ConfigSingleChartDTO> vlstConfigSingleChartDTOS = dashboardRepo.getSingleChart(configSingleCardDTO.getCardId());
                        CardDTO cardDTO1 = new CardDTO();
                        for (ConfigSingleChartDTO configSingleChartDTO : vlstConfigSingleChartDTOS) {
                            if (Constants.CHARTTYPE.COLUMN_CHART_CHILDSERVICE.equals(configSingleChartDTO.getChartType())) {
                                ChartDTO chartDTO = chartLevTwoService.getColChartChildService(dashboardRequestDTO, configSingleChartDTO, staffDTO, true);
                                chartDTO.setChartId(vintChartId++);
                                contentBaseDTOS.add(chartDTO);
                            }
                            if (Constants.CHARTTYPE.TABLE_DETAIL_CHILDSERVICE.equals(configSingleChartDTO.getChartType())) {
                                TableDTO tableDTO = chartLevTwoService.getTableChildService(dashboardRequestDTO, configSingleChartDTO, staffDTO);
                                contentBaseDTOS.add(tableDTO);
                            }
                            cardDTO1.setCardSize(Constants.CARD_SIZE.HALF);
                            cardDTO1.setContents(contentBaseDTOS);
                            cardDTO1.setTitle(I18N.get(configSingleCardDTO.getCardNameI18n().trim(), (String[]) vlstParamsTitleChart.toArray(new String[vlstParamsTitleChart.size()])));
                        }
                        vlstCard.add(cardDTO1);
                    }
                }
            } else {
                for (ConfigSingleCardDTO singleCardDTO : vlstConfigCard) {
                    List<ContentBaseDTO> vlist = new ArrayList<>();
                    List<ConfigSingleChartDTO> vlstConfigSingleChartDTOS = dashboardRepo.getSingleChart(singleCardDTO.getCardId());
                    CardDTO cardDTO1 = new CardDTO();
                    cardDTO1.setGroupId((long) singleCardDTO.getGroupId());
                    cardDTO1.setCardId(singleCardDTO.getCardId());
                    List<String> listCardTitle = Lists.newArrayList();
                    if (!DataUtil.isNullOrEmpty(vstrServiceName))
                        listCardTitle.add(vstrServiceName);
                    if (singleCardDTO.getCardNameI18n() != null) {
                        cardDTO1.setTitle(I18N.get(singleCardDTO.getCardNameI18n().trim(), (String[]) listCardTitle.toArray(new String[listCardTitle.size()])));
                    } else {
                        cardDTO1.setTitle(singleCardDTO.getCardName().trim());
                    }
                    cardDTO1.setCardSize(singleCardDTO.getCardSize());
                    if (singleCardDTO.getDrilldown() != null) {
                        if (singleCardDTO.getDrilldown().equals(1)) {
                            cardDTO1.setDrilldown(true);
                        } else {
                            cardDTO1.setDrilldown(false);
                        }
                    }
                    if (!DataUtil.isNullOrZero(singleCardDTO.getDrillDownType())) {
                        cardDTO1.setDrilldownType(singleCardDTO.getDrillDownType());
                    }
                    if (!DataUtil.isNullOrZero(singleCardDTO.getDrillDownObjectId())) {
                        cardDTO1.setDrilldownObject(singleCardDTO.getDrillDownObjectId());
                    }
                    if (singleCardDTO.getZoom() == 1) {
                        cardDTO1.setZoom(true);
                    } else {
                        cardDTO1.setZoom(false);
                    }
                    cardDTO1.setServiceId(singleCardDTO.getServiceId());
                    cardDTO1.setCardType(singleCardDTO.getCardType());
                    for (ConfigSingleChartDTO configSingleChartDTO : vlstConfigSingleChartDTOS) {
                        if (DataUtil.isNullOrZero(dashboardRequestDTO.getServiceId())) {
                            if (Constants.CHARTTYPE.CONTENT_TOP.equals(configSingleChartDTO.getChartType().trim())) {
                                ContentTopLeftDTO contentTopLeftDTO = topDataService.getLeftTop(dashboardRequestDTO, configSingleChartDTO);
//                                if (DataUtil.isNullOrZero(dashboardRequestDTO.getParentShopCode())) {
//                                    dashboardDTO.setGroupName(I18N.get(groupCardDTO.getGroupNameI18n()) + I18N.get(Constants.SHOWS.TITLE_UNIT));
//                                } else {
//                                    dashboardDTO.setGroupName(I18N.get(groupCardDTO.getGroupNameI18n()) + I18N.get(Constants.SHOWS.TITLE_STAFF));
//                                }
                                vlist.add(contentTopLeftDTO);
                            }
                            if (Constants.CHARTTYPE.SUMMARY_VIEW.equals(configSingleChartDTO.getChartType().trim())) {
                                ContentTopRightDTO contentTopRightDTO = topDataService.getRightTop(dashboardRequestDTO, configSingleChartDTO);
                                vlist.add(contentTopRightDTO);
                            }
                            //top moi 26/05/2020
                            if (Constants.CHARTTYPE.EVALUATE_POINT.equals(configSingleChartDTO.getChartType().trim())) {
                                List<TopByServiceDTO> vlstData = contentTopService.getEvaluatePoint(dashboardRequestDTO, configSingleChartDTO);
                                ListTopByServiceDTO listTopByServiceDTO = new ListTopByServiceDTO();
                                listTopByServiceDTO.setLstData(vlstData);
                                List<String> vlstParamTitleCard = new ArrayList<>();
                                int vintCycleId = dashboardRequestDTO.getCycleId();
                                if (configSingleChartDTO.getChartType() != null) {
                                    listTopByServiceDTO.setType(configSingleChartDTO.getChartType());
                                }
                                if (configSingleChartDTO.getDrilldown() != null) {
                                    if (configSingleChartDTO.getDrilldown() == 1) {
                                        listTopByServiceDTO.setDrilldown(true);
                                    }
                                    if (configSingleChartDTO.getDrilldown() == 0) {
                                        listTopByServiceDTO.setDrilldown(false);
                                    }
                                }
                                if (configSingleChartDTO.getDrillDownObjectId() != null) {
                                    listTopByServiceDTO.setDrilldownObject(Math.toIntExact(configSingleChartDTO.getDrillDownObjectId()));
                                }
                                if (singleCardDTO.getCardNameI18n() != null) {
                                    if (Constants.CYCLE_ID.DAY == vintCycleId || Constants.CYCLE_ID.MONTH == vintCycleId) {
                                        vlstParamTitleCard.add(I18N.get(Constants.TOP_BY_SERVICE.TITLE_MONTH));
                                    } else if (Constants.CYCLE_ID.QUARTER == vintCycleId) {
                                        vlstParamTitleCard.add(I18N.get(Constants.TOP_BY_SERVICE.TITLE_QUARTER));
                                    }
                                    cardDTO1.setTitle(I18N.get(singleCardDTO.getCardNameI18n(), (String[]) vlstParamTitleCard.toArray(new String[vlstParamTitleCard.size()])));
                                } else {
                                    cardDTO1.setTitle(singleCardDTO.getCardName());
                                }
                                listTopByServiceDTO.setType(Constants.CHARTTYPE.TOP_BY_SERVICE);
                                vlist.add(listTopByServiceDTO);
                            }
                            if (Constants.CHARTTYPE.CONTINUITY_FAIL.equals(configSingleChartDTO.getChartType().trim())) {
                                ContinuityFailDTO continuityFailDTO = contentTopService.getContinuityFail(dashboardRequestDTO, configSingleChartDTO);
                                vlist.add(continuityFailDTO);
                            }
                            if (Constants.CHARTTYPE.TOP_BY_SERVICE.equals(configSingleChartDTO.getChartType().trim())) {
                                List<TopByServiceDTO> vlstData = contentTopService.getTopByService(dashboardRequestDTO, configSingleChartDTO);
                                ListTopByServiceDTO listTopByServiceDTO = new ListTopByServiceDTO();
                                listTopByServiceDTO.setLstData(vlstData);
                                if (configSingleChartDTO.getChartType() != null) {
                                    listTopByServiceDTO.setType(configSingleChartDTO.getChartType());
                                }
                                if (configSingleChartDTO.getDrilldown() != null) {
                                    if (configSingleChartDTO.getDrilldown() == 1) {
                                        listTopByServiceDTO.setDrilldown(true);
                                    }
                                    if (configSingleChartDTO.getDrilldown() == 0) {
                                        listTopByServiceDTO.setDrilldown(false);
                                    }
                                }
                                if (configSingleChartDTO.getDrillDownObjectId() != null) {
                                    listTopByServiceDTO.setDrilldownObject(Math.toIntExact(configSingleChartDTO.getDrillDownObjectId()));
                                }
                                vlist.add(listTopByServiceDTO);
                            }
                            if (Constants.CHARTTYPE.TABLE_VIEW.equals(configSingleChartDTO.getChartType())) {
                                TableDTO tableDTO = tableService.buildTable(dashboardRequestDTO, configSingleChartDTO, staffDTO);
                                vlist.add(tableDTO);
                            }
                            if (Constants.CHARTTYPE.LINE_CHART.equals(configSingleChartDTO.getChartType())) {
                                ChartDTO chartDTO = chartService.getChart(dashboardRequestDTO, configSingleChartDTO, staffDTO);
                                chartDTO.setChartId(vintChartId++);
                                vlist.add(chartDTO);
                            }
                            if (Constants.CHARTTYPE.BAR_CHART.equals(configSingleChartDTO.getChartType().trim())) {
                                ChartDTO chartDTO = topSecondLevelService.detailRankShopBarChart(dashboardRequestDTO, configSingleChartDTO);
                                vlist.add(chartDTO);
                            }
                            if (Constants.CHARTTYPE.TABLE_DETAIL.equals(configSingleChartDTO.getChartType().trim())) {
                                TableDTO tableDTO = topSecondLevelService.detailRankShopTable(dashboardRequestDTO, configSingleChartDTO);
                                if (DataUtil.isNullOrZero(dashboardRequestDTO.getParentShopCode())) {
                                    if (!DataUtil.isNullOrEmpty(groupCardDTO.getGroupNameI18n())) {
                                        dashboardDTO.setGroupName(I18N.get(groupCardDTO.getGroupNameI18n()) + " " + I18N.get(Constants.SHOWS.TITLE_UNIT));
                                    }
                                } else {
                                    if (!DataUtil.isNullOrEmpty(groupCardDTO.getGroupNameI18n())) {
                                        dashboardDTO.setGroupName(I18N.get(groupCardDTO.getGroupNameI18n()) + " " + I18N.get(Constants.SHOWS.TITLE_STAFF));
                                    }
                                }
                                vlist.add(tableDTO);
                            }
                            if (Constants.CHARTTYPE.TABLE_DETAIL_ALL_SERVICE.equals(configSingleChartDTO.getChartType().trim())) {
                                List<String> vlstCardTitles = Lists.newArrayList();
                                if (dashboardRequestDTO.getIsTarget() == 1) {
                                    vlstCardTitles.add(I18N.get("achieved.all.service.title"));
                                } else if (dashboardRequestDTO.getIsTarget() == -1) {
                                    vlstCardTitles.add(I18N.get("unachieved.all.service.title"));
                                }
                                String vstrTitle = I18N.get(singleCardDTO.getCardNameI18n().trim(), (String[]) vlstCardTitles.toArray(new String[vlstCardTitles.size()]));
                                cardDTO1.setTitle(vstrTitle);
                                dashboardDTO.setGroupName(vstrTitle);
                                configSingleChartDTO.setChartType(Constants.CHARTTYPE.TABLE_VIEW);
                                TableDTO tableDTO = topSecondLevelService.detailShopTargetTable(dashboardRequestDTO, configSingleChartDTO);

                                vlist.add(tableDTO);
                            }
                            if (Constants.CHARTTYPE.TABLE_DETAIL_SCORE.equals(configSingleChartDTO.getChartType().trim())) {

                                String vstrTitleI18N = I18N.get(singleCardDTO.getCardNameI18n().trim());
                                String vstrTitle = singleCardDTO.getCardName().trim();
                                if (!DataUtil.isNullOrEmpty(vstrTitleI18N)) {
                                    switch (dashboardRequestDTO.getMonth()) {
                                        case Constants.TOP_BY_SERVICE.ONE:
                                            if (dashboardRequestDTO.getCycleId() == Constants.CYCLE_ID.DAY || dashboardRequestDTO.getCycleId() == Constants.CYCLE_ID.MONTH) {
                                                cardDTO1.setTitle(vstrTitleI18N + " " + I18N.get("ranking.service.n").toUpperCase());
                                                dashboardDTO.setGroupName(vstrTitleI18N + " " + I18N.get("ranking.service.n").toUpperCase());
                                            } else if (dashboardRequestDTO.getCycleId() == Constants.CYCLE_ID.QUARTER) {
                                                cardDTO1.setTitle(vstrTitleI18N + " " + I18N.get("ranking.service.quarter.n").toUpperCase());
                                                dashboardDTO.setGroupName(vstrTitleI18N + " " + I18N.get("ranking.service.quarter.n").toUpperCase());
                                            }
                                            break;
                                        case Constants.TOP_BY_SERVICE.TWO:
                                            if (dashboardRequestDTO.getCycleId() == Constants.CYCLE_ID.DAY || dashboardRequestDTO.getCycleId() == Constants.CYCLE_ID.MONTH) {
                                                cardDTO1.setTitle(vstrTitleI18N + " " + I18N.get("ranking.service.n1").toUpperCase());
                                                dashboardDTO.setGroupName(vstrTitleI18N + " " + I18N.get("ranking.service.n1").toUpperCase());
                                            } else if (dashboardRequestDTO.getCycleId() == Constants.CYCLE_ID.QUARTER) {
                                                cardDTO1.setTitle(vstrTitleI18N + " " + I18N.get("ranking.service.quarter.n1").toUpperCase());
                                                dashboardDTO.setGroupName(vstrTitleI18N + " " + I18N.get("ranking.service.quarter.n1").toUpperCase());
                                            }
                                            break;
                                        case Constants.TOP_BY_SERVICE.THREE:
                                            if (dashboardRequestDTO.getCycleId() == Constants.CYCLE_ID.DAY || dashboardRequestDTO.getCycleId() == Constants.CYCLE_ID.MONTH) {
                                                cardDTO1.setTitle(vstrTitleI18N + " " + I18N.get("ranking.service.n2").toUpperCase());
                                                dashboardDTO.setGroupName(vstrTitleI18N + " " + I18N.get("ranking.service.n2").toUpperCase());
                                            } else if (dashboardRequestDTO.getCycleId() == Constants.CYCLE_ID.QUARTER) {
                                                cardDTO1.setTitle(vstrTitleI18N + " " + I18N.get("ranking.service.quarter.n2").toUpperCase());
                                                dashboardDTO.setGroupName(vstrTitleI18N + " " + I18N.get("ranking.service.quarter.n2").toUpperCase());
                                            }
                                            break;
                                    }
                                } else {
                                    switch (dashboardRequestDTO.getMonth()) {
                                        case Constants.TOP_BY_SERVICE.ONE:
                                            if (dashboardRequestDTO.getCycleId() == Constants.CYCLE_ID.DAY || dashboardRequestDTO.getCycleId() == Constants.CYCLE_ID.MONTH) {
                                                cardDTO1.setTitle(vstrTitle + " " + I18N.get("ranking.service.n").toUpperCase());
                                                dashboardDTO.setGroupName(vstrTitle + " " + I18N.get("ranking.service.n").toUpperCase());
                                            } else if (dashboardRequestDTO.getCycleId() == Constants.CYCLE_ID.QUARTER) {
                                                cardDTO1.setTitle(vstrTitle + " " + I18N.get("ranking.service.quarter.n").toUpperCase());
                                                dashboardDTO.setGroupName(vstrTitle + " " + I18N.get("ranking.service.quarter.n").toUpperCase());
                                            }
                                            break;
                                        case Constants.TOP_BY_SERVICE.TWO:
                                            if (dashboardRequestDTO.getCycleId() == Constants.CYCLE_ID.DAY || dashboardRequestDTO.getCycleId() == Constants.CYCLE_ID.MONTH) {
                                                cardDTO1.setTitle(vstrTitle + " " + I18N.get("ranking.service.n1").toUpperCase());
                                                dashboardDTO.setGroupName(vstrTitle + " " + I18N.get("ranking.service.n1").toUpperCase());
                                            } else if (dashboardRequestDTO.getCycleId() == Constants.CYCLE_ID.QUARTER) {
                                                cardDTO1.setTitle(vstrTitle + " " + I18N.get("ranking.service.quarter.n1").toUpperCase());
                                                dashboardDTO.setGroupName(vstrTitle + " " + I18N.get("ranking.service.quarter.n1").toUpperCase());
                                            }
                                            break;
                                        case Constants.TOP_BY_SERVICE.THREE:
                                            if (dashboardRequestDTO.getCycleId() == Constants.CYCLE_ID.DAY || dashboardRequestDTO.getCycleId() == Constants.CYCLE_ID.MONTH) {
                                                cardDTO1.setTitle(vstrTitle + " " + I18N.get("ranking.service.n2").toUpperCase());
                                                dashboardDTO.setGroupName(vstrTitle + " " + I18N.get("ranking.service.n2").toUpperCase());
                                            } else if (dashboardRequestDTO.getCycleId() == Constants.CYCLE_ID.QUARTER) {
                                                cardDTO1.setTitle(vstrTitle + " " + I18N.get("ranking.service.quarter.n2").toUpperCase());
                                                dashboardDTO.setGroupName(vstrTitle + " " + I18N.get("ranking.service.quarter.n2").toUpperCase());
                                            }
                                            break;
                                    }
                                }
                                configSingleChartDTO.setChartType(Constants.CHARTTYPE.TABLE_VIEW);
                                TableDTO tableDTO = topSecondLevelService.detailScoreRanking(dashboardRequestDTO, configSingleChartDTO);

                                vlist.add(tableDTO);
                            }
                        } else {
                            if (Constants.TABLE_UNIT_STAFF_BYSERVICE.equals(configSingleChartDTO.getChartType().trim())) {
                                if (singleCardDTO.getCardNameI18n() != null) {
                                    String titleCard = (I18N.get(singleCardDTO.getCardNameI18n().trim(), (String[]) vlstParamTitleLevThree.toArray(new String[vlstParamTitleLevThree.size()]))).toUpperCase();
                                    cardDTO1.setTitle(titleCard);
                                } else {
                                    cardDTO1.setTitle(singleCardDTO.getCardName());
                                }
                                configSingleChartDTO.setChartType(Constants.CHARTTYPE.TABLE_VIEW);
                                TableDTO tableDTO = chartLevTwoService.getUnitStaffByService(dashboardRequestDTO, configSingleChartDTO);
                                tableDTO.setType(Constants.CHARTTYPE.TABLE_VIEW);
                                if (configSingleChartDTO.getDrilldown() != null) {
                                    if (configSingleChartDTO.getDrilldown() == 1) {
                                        cardDTO1.setDrilldown(true);
                                    }
                                    if (configSingleChartDTO.getDrilldown() == 0) {
                                        cardDTO1.setDrilldown(false);
                                    }
                                }
                                if (configSingleChartDTO.getDrillDownObjectId() != null) {
                                    cardDTO1.setDrilldownObject(Math.toIntExact(configSingleChartDTO.getDrillDownObjectId()));
                                }
                                if (configSingleChartDTO.getDrilldown() != null) {
                                    if (configSingleChartDTO.getDrilldown() == 1) {
                                        tableDTO.setDrilldown(true);
                                    }
                                    if (configSingleChartDTO.getDrilldown() == 0) {
                                        tableDTO.setDrilldown(false);
                                    }
                                }
                                if (configSingleChartDTO.getDrillDownObjectId() != null) {
                                    tableDTO.setDrilldownObject(Math.toIntExact(configSingleChartDTO.getDrillDownObjectId()));
                                }
                                dashboardDTO.setLvThree(true);
                                vlist.add(tableDTO);
                            }
                            if (Constants.TOP_BY_SERVICE.THRESHOLD_SERVICE.equals(configSingleChartDTO.getChartType().trim())) {
                                if (dashboardRequestDTO.getParentShopCode() == null) {
                                    if (singleCardDTO.getCardNameI18n() != null) {
                                        cardDTO1.setTitle(I18N.get(singleCardDTO.getCardNameI18n().trim(), (String[]) vlstParamsTitleChart.toArray(new String[vlstParamsTitleChart.size()])));
                                    } else {
                                        cardDTO1.setTitle(singleCardDTO.getCardName());
                                    }
                                    configSingleChartDTO.setChartType(Constants.CHARTTYPE.TABLE_VIEW);
                                    TableDTO tableDTO = contentTopService.getComplThresholdService(dashboardRequestDTO, configSingleChartDTO);
                                    tableDTO.setType(Constants.CHARTTYPE.TABLE_VIEW);
                                    vlist.add(tableDTO);
                                } else {
                                    cardDTO1 = null;
                                }

                            }
                            if (Constants.CHARTTYPE.TABLE_COMPL_PERCENT_BY_SERVICE.equals(configSingleChartDTO.getChartType().trim())) {
                                TableDTO tableDTO = contentTopService.getTableComplThreshold(dashboardRequestDTO, configSingleChartDTO);
                                tableDTO.setType(Constants.CHARTTYPE.TABLE_VIEW);
                                cardDTO1.setTitle(tableDTO.getTitle());
                                dashboardDTO.setLvThree(true);
                                dashboardDTO.setGroupName(StringUtils.upperCase(vstrServiceName));
                                vlist.add(tableDTO);
                            }
                            if (Constants.CHARTTYPE.TABLE_CONTINUITY_FAIL.equals(configSingleChartDTO.getChartType().trim())) {
                                if (singleCardDTO.getCardNameI18n() != null) {
                                    cardDTO1.setTitle(I18N.get(singleCardDTO.getCardNameI18n().trim(), (String[]) vlstParamsTitleChart.toArray(new String[vlstParamsTitleChart.size()])));
                                } else {
                                    cardDTO1.setTitle(singleCardDTO.getCardName());
                                }
                                configSingleChartDTO.setChartType(Constants.CHARTTYPE.TABLE_VIEW);
                                TableDTO tableDTO = contentTopService.getContinuityFailLvTwo(dashboardRequestDTO, configSingleChartDTO);
                                vlist.add(tableDTO);
                            }
                            if (Constants.CHARTTYPE.TABLE_BY_SERVICE.equals(configSingleChartDTO.getChartType().trim())) {
                                TableDTO tableDTO = contentTopService.getTableByService(dashboardRequestDTO, configSingleChartDTO);
                                tableDTO.setType(Constants.CHARTTYPE.TABLE_VIEW);
                                vlist.add(tableDTO);
                            }
                            if (Constants.CHARTTYPE.TABLE_DETAIL_SERVICE.equals(configSingleChartDTO.getChartType().trim())) {
                                List<String> listParamTitle = Lists.newArrayList();
                                if (!DataUtil.isNullOrEmpty(vstrServiceName))
                                    listParamTitle.add(vstrServiceName);
                                dashboardDTO.setGroupName(I18N.get("table_card_detail_title", (String[]) listParamTitle.toArray(new String[listParamTitle.size()])));
                                configSingleChartDTO.setChartType(Constants.CHARTTYPE.TABLE_VIEW);
                                TableDTO tableDTO = tableService.buildTable(dashboardRequestDTO, configSingleChartDTO, staffDTO);
                                tableDTO.setAllStaff(true);

                                vlist.add(tableDTO);
                            }
                            if (Constants.CHARTTYPE.COLUMN_CHART_CHILDSHOP.equals(configSingleChartDTO.getChartType().trim())) {
                                if (singleCardDTO.getCardNameI18n() != null) {
                                    cardDTO1.setTitle(I18N.get(singleCardDTO.getCardNameI18n().trim(), (String[]) vlstParamsTitleChart.toArray(new String[vlstParamsTitleChart.size()])));
                                } else {
                                    cardDTO1.setTitle(singleCardDTO.getCardName());
                                }
                                configSingleChartDTO.setChartType(Constants.CHARTTYPE.COLUMN_CHART);
                                ChartDTO chartDTO = chartLevTwoService.getColChartChildShop(dashboardRequestDTO, configSingleChartDTO, staffDTO, true);
                                chartDTO.setChartId(vintChartId++);
                                vlist.add(chartDTO);
                            }
                            if (Constants.CHARTTYPE.BAR_CHART_CHILDSHOP.equals(configSingleChartDTO.getChartType().trim())) {
                                if (singleCardDTO.getCardNameI18n() != null) {
                                    cardDTO1.setTitle(I18N.get(singleCardDTO.getCardNameI18n().trim(), (String[]) vlstParamsTitleChart.toArray(new String[vlstParamsTitleChart.size()])));
                                } else {
                                    cardDTO1.setTitle(singleCardDTO.getCardName());
                                }
                                configSingleChartDTO.setChartType(Constants.CHARTTYPE.COLUMN_CHART);
                                ChartDTO chartDTO = chartLevTwoService.getColChartChildShopSort(dashboardRequestDTO, configSingleChartDTO, staffDTO);
                                chartDTO.setChartId(vintChartId++);
                                vlist.add(chartDTO);
                            }
                            if (Constants.CHARTTYPE.TABLE_DETAIL_CHILDSHOP.equals(configSingleChartDTO.getChartType().trim())) {
                                if (singleCardDTO.getCardNameI18n() != null) {
                                    cardDTO1.setTitle(I18N.get(singleCardDTO.getCardNameI18n().trim(), (String[]) vlstParamsTitleChart.toArray(new String[vlstParamsTitleChart.size()])));
                                } else {
                                    cardDTO1.setTitle(singleCardDTO.getCardName());
                                }
                                configSingleChartDTO.setChartType(Constants.CHARTTYPE.TABLE_VIEW);
                                TableDTO tableDTO = chartLevTwoService.getTableChildShop(dashboardRequestDTO, configSingleChartDTO, staffDTO);
                                dashboardDTO.setLvThree(true);
                                vlist.add(tableDTO);
                            }
                            if (Constants.CHARTTYPE.COLUMN_CHART_CHILDSERVICE.equals(configSingleChartDTO.getChartType().trim())) {
                                if (singleCardDTO.getCardNameI18n() != null) {
                                    cardDTO1.setTitle(I18N.get(singleCardDTO.getCardNameI18n().trim(), (String[]) vlstParamsTitleChart.toArray(new String[vlstParamsTitleChart.size()])));
                                } else {
                                    cardDTO1.setTitle(singleCardDTO.getCardName());
                                }
                                configSingleChartDTO.setChartType(Constants.CHARTTYPE.COLUMN_CHART);
                                ChartDTO chartDTO = chartLevTwoService.getColChartChildService(dashboardRequestDTO, configSingleChartDTO, staffDTO, true);
                                chartDTO.setChartId(vintChartId++);
                                vlist.add(chartDTO);
                            }
                            if (Constants.CHARTTYPE.TABLE_DETAIL_CHILDSERVICE.equals(configSingleChartDTO.getChartType().trim())) {
                                if (singleCardDTO.getCardNameI18n() != null) {
                                    cardDTO1.setTitle(I18N.get(singleCardDTO.getCardNameI18n().trim(), (String[]) vlstParamsTitleChart.toArray(new String[vlstParamsTitleChart.size()])));
                                } else {
                                    cardDTO1.setTitle(singleCardDTO.getCardName());
                                }
                                configSingleChartDTO.setChartType(Constants.CHARTTYPE.TABLE_VIEW);
                                TableDTO tableDTO = chartLevTwoService.getTableChildService(dashboardRequestDTO, configSingleChartDTO, staffDTO);
                                dashboardDTO.setLvThree(true);
                                vlist.add(tableDTO);
                            }
                            if (Constants.CHARTTYPE.LINE_CHART_CHILDSERVICE.equals(configSingleChartDTO.getChartType().trim())) {
                                if (singleCardDTO.getCardNameI18n() != null) {
                                    cardDTO1.setTitle(I18N.get(singleCardDTO.getCardNameI18n().trim(), (String[]) vlstParamsTitleChart.toArray(new String[vlstParamsTitleChart.size()])));
                                } else {
                                    cardDTO1.setTitle(singleCardDTO.getCardName());
                                }
                                configSingleChartDTO.setChartType(Constants.CHARTTYPE.LINE_CHART);
//                                ChartDTO chartDTO = chartLevTwoService.getLineChartChildService(dashboardRequestDTO, configSingleChartDTO, staffDTO);
                                ChartDTO chartDTO = chartLevTwoService.getLChartChildServiceNew(dashboardRequestDTO, configSingleChartDTO);
                                chartDTO.setChartId(vintChartId++);
                                vlist.add(chartDTO);
                            }
                            if (Constants.CHARTTYPE.LINE_CHART_SERVICE_YEAR.equals(configSingleChartDTO.getChartType().trim())) {
                                if (dashboardRequestDTO.getCycleId() == Constants.CYCLE_ID.MONTH) {
                                    if (singleCardDTO.getCardNameI18n() != null) {
                                        cardDTO1.setTitle(I18N.get(singleCardDTO.getCardNameI18n().trim(), (String[]) vlstParamsTitleChart.toArray(new String[vlstParamsTitleChart.size()])));
                                    } else {
                                        cardDTO1.setTitle(singleCardDTO.getCardName());
                                    }
                                    configSingleChartDTO.setChartType(Constants.CHARTTYPE.LINE_CHART);
                                    ChartDTO chartDTO = chartLevTwoService.getLineChartServiceByYear(dashboardRequestDTO, configSingleChartDTO, staffDTO);
                                    chartDTO.setChartId(vintChartId++);
                                    vlist.add(chartDTO);
                                } else {
                                    cardDTO1 = null;
                                }
                            }
                        }
                    }
                    if (cardDTO1 != null) {
                        cardDTO1.setContents(vlist);
                        vlstCard.add(cardDTO1);
                    }
                }
            }

            if (strCardType.contains(Constants.DYNAMIC)) {
                if (vlstCard.size() % 2 != 0) {
                    vlstCard.get(vlstCard.size() - 1).setCardSize(Constants.CARD_SIZE.FULL);
                }
            }
            dashboardDTO.setCards(vlstCard);
            return dashboardDTO;
        }
    }

    /**
     * phong to du lieu cho cac card can hien thi toan bo du lieu
     *
     * @param dashboardRequestDTO
     * @param groupId
     * @param staffDTO
     * @return dashboarddto
     * @throws Exception
     * @author HungNN
     * @version 1.0
     * @since 18/11/2019
     */
    @Override
    @Transactional
    public DashboardDTO zoomConfigSingleCard(DashboardRequestDTO dashboardRequestDTO, Long groupId, StaffDTO staffDTO) throws Exception {
        DashboardDTO dashboardDTO = new DashboardDTO();
        String vstrServiceName = serviceRepo.findNameById(dashboardRequestDTO.getServiceId());
        //lay param cho title chart level 2
        List<String> vlstParamsTitleChart = Lists.newArrayList();
        String vstrShopCodeTitleChart = partnerRepo.getNameByShopAndChannel(dashboardRequestDTO.getObjectCode());
        vlstParamsTitleChart.add(vstrShopCodeTitleChart);
        vlstParamsTitleChart.add(DataUtil.convertLocalDateToString(dashboardRequestDTO.getPrdId(), dashboardRequestDTO.getCycleId()));

        if (dashboardRequestDTO.getObjectCode() == null) return null;
        else {
            dashboardDTO.setFilterMetaData(dashboardRequestDTO);
            List<ConfigSingleCardDTO> vlstConfigCard = dashboardRepo.getSingleCard(groupId);
            List<CardDTO> vlstCard = new ArrayList<>();
            int vintChartId = 0;

            ConfigGroupCardDTO groupCardDTO = dashboardRepo.getConfigGroup(groupId);
            // set du lieu cho DashboardDTO
            dashboardDTO.setGroupId(groupCardDTO.getGroupId());
            dashboardDTO.setVdsChannelCode(groupCardDTO.getVdsChannelCode());
            dashboardDTO.setGroupName(groupCardDTO.getGroupNameI18n() == null ? groupCardDTO.getGroupName() : I18N.get(groupCardDTO.getGroupNameI18n()));
            dashboardDTO.setSubGroupName(partnerRepo.getNameByShopAndChannel(dashboardRequestDTO.getObjectCode()) + " - " + DataUtil.convertLocalDateToString(dashboardRequestDTO.getPrdId(), dashboardRequestDTO.getCycleId()));
            if (DataUtil.isNullOrZero(dashboardRequestDTO.getCycleId())) {
                dashboardDTO.setDefaultCycle(groupCardDTO.getDefaultCycle());
            } else {
                dashboardDTO.setDefaultCycle(null);
            }
            List<String> strCardType = configSingleCardRepo.lstCardDynamic(dashboardRequestDTO.getGroupId().intValue());
            if (strCardType.contains(Constants.DYNAMIC)) {
                List<Long> lstServiceChild = chartRepo.getChildServiceId(dashboardRequestDTO.getServiceId());
                for (Long data : lstServiceChild) {
                    //lay ra ten service de cho len title card
                    String vstrServiceNameParam = serviceRepo.findNameById(data);
                    if (vlstParamsTitleChart.size() == 2) {
                        vlstParamsTitleChart.add(1, vstrServiceNameParam);
                    } else {
                        vlstParamsTitleChart.set(1, vstrServiceNameParam);
                    }

                    dashboardRequestDTO.setServiceId(data);
                    for (ConfigSingleCardDTO configSingleCardDTO : vlstConfigCard) {
                        List<ContentBaseDTO> contentBaseDTOS = new ArrayList<>();
                        List<ConfigSingleChartDTO> vlstConfigSingleChartDTOS = dashboardRepo.getSingleChart(configSingleCardDTO.getCardId());
                        CardDTO cardDTO1 = new CardDTO();
                        for (ConfigSingleChartDTO configSingleChartDTO : vlstConfigSingleChartDTOS) {
                            if (Constants.CHARTTYPE.COLUMN_CHART_CHILDSERVICE.equals(configSingleChartDTO.getChartType())) {
                                ChartDTO chartDTO = chartLevTwoService.getColChartChildService(dashboardRequestDTO, configSingleChartDTO, staffDTO, false);
                                chartDTO.setChartId(vintChartId++);
                                contentBaseDTOS.add(chartDTO);
                            }
                            if (Constants.CHARTTYPE.TABLE_DETAIL_CHILDSERVICE.equals(configSingleChartDTO.getChartType())) {
                                TableDTO tableDTO = chartLevTwoService.getTableChildService(dashboardRequestDTO, configSingleChartDTO, staffDTO);
                                contentBaseDTOS.add(tableDTO);
                            }
                            cardDTO1.setCardSize(Constants.CARD_SIZE.HALF);
                            cardDTO1.setContents(contentBaseDTOS);
                            cardDTO1.setTitle(I18N.get(configSingleCardDTO.getCardNameI18n().trim(), (String[]) vlstParamsTitleChart.toArray(new String[vlstParamsTitleChart.size()])));
                        }
                        vlstCard.add(cardDTO1);
                    }
                }
            } else {
                for (ConfigSingleCardDTO singleCardDTO : vlstConfigCard) {
                    List<ContentBaseDTO> vlist = new ArrayList<>();
                    List<ConfigSingleChartDTO> vlstConfigSingleChartDTOS = dashboardRepo.getSingleChart(singleCardDTO.getCardId());
                    CardDTO cardDTO1 = new CardDTO();
                    cardDTO1.setGroupId((long) singleCardDTO.getGroupId());
                    cardDTO1.setCardId(singleCardDTO.getCardId());
                    List<String> listCardTitle = Lists.newArrayList();
                    if (!DataUtil.isNullOrEmpty(vstrServiceName))
                        listCardTitle.add(vstrServiceName);
                    if (singleCardDTO.getCardNameI18n() != null) {
                        cardDTO1.setTitle(I18N.get(singleCardDTO.getCardNameI18n().trim(), (String[]) listCardTitle.toArray(new String[listCardTitle.size()])));
                    } else {
                        cardDTO1.setTitle(singleCardDTO.getCardName().trim());
                    }
                    cardDTO1.setCardSize(singleCardDTO.getCardSize());
                    if (singleCardDTO.getDrilldown() != null) {
                        if (singleCardDTO.getDrilldown().equals(1)) {
                            cardDTO1.setDrilldown(true);
                        } else {
                            cardDTO1.setDrilldown(false);
                        }
                    }
                    if (!DataUtil.isNullOrZero(singleCardDTO.getDrillDownType())) {
                        cardDTO1.setDrilldownType(singleCardDTO.getDrillDownType());
                    }
                    if (!DataUtil.isNullOrZero(singleCardDTO.getDrillDownObjectId())) {
                        cardDTO1.setDrilldownObject(singleCardDTO.getDrillDownObjectId());
                    }
                    if (singleCardDTO.getZoom() == 1) {
                        cardDTO1.setZoom(true);
                    } else {
                        cardDTO1.setZoom(false);
                    }
                    cardDTO1.setServiceId(singleCardDTO.getServiceId());
                    cardDTO1.setCardType(singleCardDTO.getCardType());
                    for (ConfigSingleChartDTO configSingleChartDTO : vlstConfigSingleChartDTOS) {
                        if (DataUtil.isNullOrZero(dashboardRequestDTO.getServiceId())) {
                            if (Constants.CHARTTYPE.BAR_CHART.equals(configSingleChartDTO.getChartType().trim())) {
                                ChartDTO chartDTO = topSecondLevelService.detailRankShopBarChart(dashboardRequestDTO, configSingleChartDTO);
                                chartDTO.setTitle(I18N.get(Constants.RANK_UNIT));
                                vlist.add(chartDTO);
                            }
                        } else {
                            if (Constants.CHARTTYPE.COLUMN_CHART_CHILDSHOP.equals(configSingleChartDTO.getChartType().trim())) {
                                if (singleCardDTO.getCardNameI18n() != null) {
                                    cardDTO1.setTitle(I18N.get(singleCardDTO.getCardNameI18n().trim(), (String[]) vlstParamsTitleChart.toArray(new String[vlstParamsTitleChart.size()])));
                                } else {
                                    cardDTO1.setTitle(singleCardDTO.getCardName());
                                }
                                configSingleChartDTO.setChartType(Constants.CHARTTYPE.COLUMN_CHART);
                                ChartDTO chartDTO = chartLevTwoService.getColChartChildShop(dashboardRequestDTO, configSingleChartDTO, staffDTO, false);
                                vlist.add(chartDTO);
                            }
                            if (Constants.CHARTTYPE.COLUMN_CHART_CHILDSERVICE.equals(configSingleChartDTO.getChartType().trim())) {
                                if (singleCardDTO.getCardNameI18n() != null) {
                                    cardDTO1.setTitle(I18N.get(singleCardDTO.getCardNameI18n().trim(), (String[]) vlstParamsTitleChart.toArray(new String[vlstParamsTitleChart.size()])));
                                } else {
                                    cardDTO1.setTitle(singleCardDTO.getCardName());
                                }
                                configSingleChartDTO.setChartType(Constants.CHARTTYPE.COLUMN_CHART);
                                ChartDTO chartDTO = chartLevTwoService.getColChartChildService(dashboardRequestDTO, configSingleChartDTO, staffDTO, false);
                                vlist.add(chartDTO);
                            }
                            if (Constants.CHARTTYPE.BAR_CHART_CHILDSHOP.equals(configSingleChartDTO.getChartType().trim())) {
                                if (singleCardDTO.getCardNameI18n() != null) {
                                    cardDTO1.setTitle(I18N.get(singleCardDTO.getCardNameI18n().trim(), (String[]) vlstParamsTitleChart.toArray(new String[vlstParamsTitleChart.size()])));
                                } else {
                                    cardDTO1.setTitle(singleCardDTO.getCardName());
                                }
                                configSingleChartDTO.setChartType(Constants.CHARTTYPE.COLUMN_CHART);
                                ChartDTO chartDTO = chartLevTwoService.getColChartRankUnitZoom(dashboardRequestDTO, configSingleChartDTO, staffDTO, false);
                                chartDTO.setTitle(I18N.get(Constants.RANK_UNIT));
                                vlist.add(chartDTO);
                            }
                        }
                    }

                    cardDTO1.setContents(vlist);
                    vlstCard.add(cardDTO1);
                }
            }

            dashboardDTO.setCards(vlstCard);
            return dashboardDTO;
        }
    }
}
