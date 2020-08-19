package vn.vissoft.dashboard.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.annotation.Transactional;
import vn.vissoft.dashboard.config.I18N;
import vn.vissoft.dashboard.dto.GroupServiceDTO;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.chart.ConfigSingleChartDTO;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.mapper.BaseMapper;
import vn.vissoft.dashboard.model.ActionAudit;
import vn.vissoft.dashboard.model.ConfigSingleChart;
import vn.vissoft.dashboard.repo.ConfigSingleChartRepo;
import vn.vissoft.dashboard.services.ActionAuditService;
import vn.vissoft.dashboard.services.ActionDetailService;
import vn.vissoft.dashboard.services.ConfigSingleChartService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.TransactionRolledbackException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

@Service
public class ConfigSingleChartServiceImpl implements ConfigSingleChartService {

    private BaseMapper<ConfigSingleChart, ConfigSingleChartDTO> mapper = new BaseMapper<ConfigSingleChart, ConfigSingleChartDTO>(ConfigSingleChart.class, ConfigSingleChartDTO.class);

    @Autowired
    private ConfigSingleChartRepo configSingleChartRepo;

    @Autowired
    private ActionAuditService actionAuditService;

    @Autowired
    private ActionDetailService actionDetailService;

    @Transactional
    @Override
    public List<ConfigSingleChartDTO> getByCondition(ConfigSingleChartDTO configSingleChartDTO) throws Exception {
        List<ConfigSingleChartDTO> vlstConfigSingleCharts = new ArrayList<>();
        List<Object[]> vlstSingleCharts = configSingleChartRepo.findByCondition(configSingleChartDTO);
        if (!DataUtil.isNullOrEmpty(vlstSingleCharts)) {
            for (Object[] object : vlstSingleCharts) {

                String vstrTitle = DataUtil.safeToString(object[0]);
                String vstrChartSize = DataUtil.safeToString(object[1]);
                String vstrCardName = DataUtil.safeToString(object[2]);
                String vstrChartType = DataUtil.safeToString(object[3]);
                Integer vintExpand = (Integer) (object[4]);
                String vstrServiceIds = DataUtil.safeToString(object[5]);
                Integer vintDrilldown = (Integer) (object[6]);
                Long vlngDrilldownObjectId = DataUtil.safeToLong(object[7]);
                Long vlngStatus = DataUtil.safeToLong(object[8]);
                Long vlngChartId = DataUtil.safeToLong(object[9]);
                Long vlngCardId = DataUtil.safeToLong(object[10]);
                String vstrQueryParam = DataUtil.safeToString(object[11]);
                String vstrMetaData = DataUtil.safeToString(object[12]);
                String vstrGroupName = DataUtil.safeToString(object[13]);
                String vstrChartSizeName = DataUtil.safeToString(object[14]);
                String vstrChartTypeName = DataUtil.safeToString(object[15]);

                ConfigSingleChartDTO configSingleChart = new ConfigSingleChartDTO();
                configSingleChart.setTitle(vstrTitle);
                configSingleChart.setChartSize(vstrChartSize);
                configSingleChart.setCardName(vstrCardName);
                configSingleChart.setChartType(vstrChartType);
                if (vintExpand != null)
                    configSingleChart.setExpand(vintExpand);
                else
                    configSingleChart.setExpand(null);
                configSingleChart.setServiceIds(vstrServiceIds);
                if (vintDrilldown != null)
                    configSingleChart.setDrilldown(vintDrilldown);
                else
                    configSingleChart.setDrilldown(null);
                configSingleChart.setDrillDownObjectId(vlngDrilldownObjectId);
                configSingleChart.setStatus(vlngStatus);
                configSingleChart.setChartId(vlngChartId);
                configSingleChart.setCardId(vlngCardId);
                configSingleChart.setQueryParam(vstrQueryParam);
                configSingleChart.setMetaData(vstrMetaData);
                configSingleChart.setGroupName(vstrGroupName);
                configSingleChart.setChartSizeName(vstrChartSizeName);
                configSingleChart.setChartTypeName(vstrChartTypeName);
                if (vintExpand != null) {
                    if (vintExpand == 1)
                        configSingleChart.setShowExpand(I18N.get(Constants.COMMON_TABLE_WARNING_YES));
                    else configSingleChart.setShowExpand(I18N.get(Constants.COMMON_TABLE_WARNING_NO));
                }
                if (vintDrilldown != null) {
                    if (vintDrilldown == 1)
                        configSingleChart.setShowDrillDown(I18N.get(Constants.COMMON_TABLE_WARNING_YES));
                    else configSingleChart.setShowDrillDown(I18N.get(Constants.COMMON_TABLE_WARNING_NO));
                }
                if (vlngStatus != null) {
                    if (vlngStatus == 1)
                        configSingleChart.setShowStatus(I18N.get(Constants.COMMON_MESSAGE_STATUS_WORK));
                    else configSingleChart.setShowStatus(I18N.get(Constants.COMMON_MESSAGE_STATUS_NOT_WORK));
                }

                vlstConfigSingleCharts.add(configSingleChart);

            }
        }
        return vlstConfigSingleCharts;
    }

    @Override
    @Transactional
    public BigInteger countByCondition(ConfigSingleChartDTO configSingleChartDTO) throws Exception {
        return configSingleChartRepo.countByCondition(configSingleChartDTO);
    }

    @Override
    public String addChart(ConfigSingleChartDTO configSingleChart, StaffDTO staffDTO) throws Exception {
        List<ConfigSingleChartDTO> vlstSingleCharts = getByCondition(configSingleChart);
        if (!DataUtil.isNullOrEmpty(configSingleChart.getLstServiceIds())) {
            StringJoiner stringJoiner = new StringJoiner(",");
            configSingleChart.getLstServiceIds().forEach(serviceId -> {
                stringJoiner.add(serviceId.toString());
                configSingleChart.setServiceIds(stringJoiner.toString());
            });
        } else {
            configSingleChart.setServiceIds(null);
        }
        for (ConfigSingleChartDTO configSingleChartDTO : vlstSingleCharts) {
            if (DataUtil.isNullOrEmpty(configSingleChartDTO.getServiceIds())) {
                configSingleChartDTO.setServiceIds(null);
            }
            if (configSingleChartDTO.equals(configSingleChart))
                return Constants.ERROR;
        }
        configSingleChart.setTitle(configSingleChart.getTitle().trim());
        ConfigSingleChart configSingleChart1 = mapper.toPersistenceBean(configSingleChart);
        configSingleChartRepo.addChart(configSingleChart1);
        saveNewAction(configSingleChart1, staffDTO);
        return Constants.SUCCESS;
    }

    @Override
    public String updateChart(ConfigSingleChartDTO configSingleChart, StaffDTO staffDTO) throws Exception {
        ConfigSingleChart oldSingleChart = configSingleChartRepo.getOne(configSingleChart.getChartId());
        if (!DataUtil.isNullOrEmpty(configSingleChart.getLstServiceIds())) {
            StringJoiner stringJoiner = new StringJoiner(",");
            configSingleChart.getLstServiceIds().forEach(serviceId -> {
                stringJoiner.add(serviceId.toString());
                configSingleChart.setServiceIds(stringJoiner.toString());
            });
        } else {
            configSingleChart.setServiceIds(null);
        }
        List<ConfigSingleChartDTO> vlstSingleCharts = getByCondition(configSingleChart);
        for (ConfigSingleChartDTO configSingleChartDTO : vlstSingleCharts) {
            if (DataUtil.isNullOrEmpty(configSingleChartDTO.getServiceIds())) {
                configSingleChartDTO.setServiceIds(null);
            }
            if (configSingleChartDTO.equals(configSingleChart) && !configSingleChartDTO.getChartId().toString().equals(configSingleChart.getChartId().toString()))
                return Constants.ERROR;
        }
        saveUpdateAction(configSingleChart, oldSingleChart, staffDTO);
        configSingleChart.setTitle(configSingleChart.getTitle().trim());
        configSingleChartRepo.updateChart(mapper.toPersistenceBean(configSingleChart));

        return Constants.SUCCESS;
    }

    @Override
    public String deleteChart(ConfigSingleChartDTO configSingleChart, StaffDTO staffDTO) throws Exception {
        ConfigSingleChart singleChart = configSingleChartRepo.getOne(configSingleChart.getChartId());
        configSingleChartRepo.delete(singleChart);
        saveDeleteAction(singleChart, staffDTO);
        return Constants.SUCCESS;
    }

    private void saveNewAction(ConfigSingleChart configSingleChart, StaffDTO staffDTO) {
        ActionAudit actionAudit = actionAuditService.log(Constants.CONFIG_SINGLE_CHART, Constants.CREATE, staffDTO.getStaffCode(), configSingleChart.getChartId(), staffDTO.getShopCode());
        if (!DataUtil.isNullOrEmpty(configSingleChart.getTitle()))
            actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CHARTS.TITLE, configSingleChart.getTitle().trim(), actionAudit.getId(), null);
        actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CHARTS.CHART_SIZE, configSingleChart.getChartSize().trim(), actionAudit.getId(), null);
        actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CHARTS.CARD_ID, configSingleChart.getCardId().toString(), actionAudit.getId(), null);
        actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CHARTS.CHART_TYPE, configSingleChart.getChartType().trim(), actionAudit.getId(), null);
        actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CHARTS.EXPAND, configSingleChart.getExpand().toString(), actionAudit.getId(), null);
        if (!DataUtil.isNullOrEmpty(configSingleChart.getServiceIds()))
            actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CHARTS.SERVICE_IDS, configSingleChart.getServiceIds().trim(), actionAudit.getId(), null);
        actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CHARTS.DRILLDOWN, configSingleChart.getDrilldown().toString(), actionAudit.getId(), null);
        if (!DataUtil.isNullOrZero(configSingleChart.getDrillDownObjectId()))
            actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CHARTS.DRILLDOWN_OBJECT_ID, configSingleChart.getDrillDownObjectId().toString(), actionAudit.getId(), null);
        actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CHARTS.STATUS, configSingleChart.getStatus().toString(), actionAudit.getId(), null);
        actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CHARTS.QUERY_PARAM, configSingleChart.getQueryParam().trim(), actionAudit.getId(), null);
        actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CHARTS.META_DATE, configSingleChart.getMetaData().trim(), actionAudit.getId(), null);

    }

    private void saveDeleteAction(ConfigSingleChart oldConfigSingleChart, StaffDTO staffDTO) {
        ActionAudit actionAudit = actionAuditService.log(Constants.CONFIG_SINGLE_CHART, Constants.ACTION_CODE_DELETE, staffDTO.getStaffCode(), oldConfigSingleChart.getChartId(), staffDTO.getShopCode());
        if (!DataUtil.isNullOrEmpty(oldConfigSingleChart.getTitle()))
            actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CHARTS.TITLE, null, actionAudit.getId(), oldConfigSingleChart.getTitle());
        actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CHARTS.CHART_SIZE, null, actionAudit.getId(), oldConfigSingleChart.getChartSize());
        actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CHARTS.CARD_ID, null, actionAudit.getId(), oldConfigSingleChart.getCardId().toString());
        actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CHARTS.CHART_TYPE, null, actionAudit.getId(), oldConfigSingleChart.getChartType());
        actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CHARTS.EXPAND, null, actionAudit.getId(), oldConfigSingleChart.getExpand().toString());
        if (!DataUtil.isNullOrEmpty(oldConfigSingleChart.getServiceIds()))
            actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CHARTS.SERVICE_IDS, null, actionAudit.getId(), oldConfigSingleChart.getServiceIds().trim());
        actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CHARTS.DRILLDOWN, null, actionAudit.getId(), oldConfigSingleChart.getDrilldown().toString());
        if (!DataUtil.isNullOrZero(oldConfigSingleChart.getDrillDownObjectId()))
            actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CHARTS.DRILLDOWN_OBJECT_ID, null, actionAudit.getId(), oldConfigSingleChart.getDrillDownObjectId().toString());
        actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CHARTS.STATUS, null, actionAudit.getId(), oldConfigSingleChart.getStatus().toString());
        actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CHARTS.QUERY_PARAM, null, actionAudit.getId(), oldConfigSingleChart.getQueryParam().trim());
        actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CHARTS.META_DATE, null, actionAudit.getId(), oldConfigSingleChart.getMetaData().trim());

    }

    private void saveUpdateAction(ConfigSingleChartDTO configSingleChart, ConfigSingleChart oldConfigSingleChart, StaffDTO staffDTO) {
        ActionAudit actionAudit = actionAuditService.log(Constants.CONFIG_SINGLE_CHART, Constants.EDIT, staffDTO.getStaffCode(), configSingleChart.getChartId(), staffDTO.getShopCode());
        if (DataUtil.isNullOrEmpty(configSingleChart.getTitle()))
            actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CHARTS.TITLE, null, actionAudit.getId(), oldConfigSingleChart.getTitle().trim());
        else {
            if (!DataUtil.safeEqualIgnoreCase(oldConfigSingleChart.getTitle().trim(), configSingleChart.getTitle().trim()))
                actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CHARTS.TITLE, configSingleChart.getTitle().trim(), actionAudit.getId(), oldConfigSingleChart.getTitle().trim());
        }
        if (!DataUtil.safeEqualIgnoreCase(oldConfigSingleChart.getChartSize().trim(), configSingleChart.getChartSize().trim()))
            actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CHARTS.CHART_SIZE, configSingleChart.getChartSize().trim(), actionAudit.getId(), oldConfigSingleChart.getChartSize().trim());
        if (!oldConfigSingleChart.getCardId().equals(configSingleChart.getCardId()))
            actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CHARTS.CARD_ID, configSingleChart.getCardId().toString(), actionAudit.getId(), oldConfigSingleChart.getCardId().toString());
        if (!DataUtil.safeEqualIgnoreCase(oldConfigSingleChart.getChartType().trim(), configSingleChart.getChartType().trim()))
            actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CHARTS.CHART_TYPE, configSingleChart.getChartType().trim(), actionAudit.getId(), oldConfigSingleChart.getChartType().trim());
        if (!oldConfigSingleChart.getCardId().equals(configSingleChart.getCardId()))
            actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CHARTS.EXPAND, configSingleChart.getExpand().toString(), actionAudit.getId(), oldConfigSingleChart.getExpand().toString());
        if (DataUtil.isNullOrEmpty(configSingleChart.getLstServiceIds())) {
            if (!DataUtil.isNullOrEmpty(oldConfigSingleChart.getServiceIds()))
                actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CHARTS.SERVICE_IDS, null, actionAudit.getId(), oldConfigSingleChart.getServiceIds().trim());
        } else {
            StringJoiner stringJoiner = new StringJoiner(",");
            configSingleChart.getLstServiceIds().stream().forEach(serviceId -> {
                stringJoiner.add(serviceId.toString());
            });
            if (DataUtil.isNullOrEmpty(oldConfigSingleChart.getServiceIds()))
                actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CHARTS.SERVICE_IDS, stringJoiner.toString().trim(), actionAudit.getId(), null);
            else {
                if (!DataUtil.safeEqualIgnoreCase(oldConfigSingleChart.getServiceIds().trim(), stringJoiner.toString()))
                    actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CHARTS.SERVICE_IDS, configSingleChart.getServiceIds().trim(), actionAudit.getId(), oldConfigSingleChart.getServiceIds().trim());
            }
        }
        if (!oldConfigSingleChart.getDrilldown().equals(configSingleChart.getDrilldown()))
            actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CHARTS.DRILLDOWN, configSingleChart.getDrilldown().toString(), actionAudit.getId(), oldConfigSingleChart.getDrilldown().toString());

        if (DataUtil.isNullOrZero(configSingleChart.getDrillDownObjectId())) {
            if (!DataUtil.isNullOrZero(oldConfigSingleChart.getDrillDownObjectId()))
                actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CHARTS.DRILLDOWN_OBJECT_ID, null, actionAudit.getId(), oldConfigSingleChart.getDrillDownObjectId().toString());
        } else {
            if (DataUtil.isNullOrZero(oldConfigSingleChart.getDrillDownObjectId()))
                actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CHARTS.DRILLDOWN_OBJECT_ID, configSingleChart.getDrillDownObjectId().toString(), actionAudit.getId(), null);
            else {
                if (!oldConfigSingleChart.getDrillDownObjectId().equals(configSingleChart.getDrillDownObjectId()))
                    actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CHARTS.DRILLDOWN_OBJECT_ID, configSingleChart.getDrillDownObjectId().toString(), actionAudit.getId(), oldConfigSingleChart.getDrillDownObjectId().toString());
            }
        }

        if (!oldConfigSingleChart.getStatus().equals(configSingleChart.getStatus()))
            actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CHARTS.STATUS, configSingleChart.getStatus().toString(), actionAudit.getId(), oldConfigSingleChart.getStatus().toString());
        if (!DataUtil.safeEqualIgnoreCase(oldConfigSingleChart.getQueryParam().trim(), configSingleChart.getQueryParam().trim()))
            actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CHARTS.QUERY_PARAM, configSingleChart.getQueryParam().trim(), actionAudit.getId(), oldConfigSingleChart.getQueryParam().trim());
        if (!DataUtil.safeEqualIgnoreCase(oldConfigSingleChart.getMetaData().trim(), configSingleChart.getMetaData().trim()))
            actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CHARTS.META_DATE, configSingleChart.getMetaData().trim(), actionAudit.getId(), oldConfigSingleChart.getMetaData().trim());
    }
}
