package vn.vissoft.dashboard.repo.impl;

import org.springframework.stereotype.Repository;
import vn.vissoft.dashboard.dto.chart.ConfigSingleChartDTO;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.model.ConfigSingleChart;
import vn.vissoft.dashboard.repo.ConfigSingleChartRepo;
import vn.vissoft.dashboard.repo.ConfigSingleChartRepoCustom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ConfigSingleChartRepoImpl implements ConfigSingleChartRepoCustom {

    @PersistenceContext
    EntityManager entityManager;

    /**
     * lay du lieu trong metadata cua contentop
     *
     * @return du lieu trong metadata
     * @throws Exception
     * @author HungNN
     * @version 1.0
     * @since 25/10/2019
     */
    @Override
    public String getJsonArray() {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("select metaData from ConfigSingleChart where chartType = 'CONTENT_TOP'");
        Query query = entityManager.createQuery(sqlBuilder.toString());
        return (String) query.getSingleResult();
    }

    /**
     * lay du lieu cua ban ghi co chart_type la DOWNLOAD_EXCEL_TABLE
     *
     * @return ConfigSingleChart
     * @throws Exception
     * @author HungNN
     * @version 1.0
     * @since 13/12/2019
     */
    @Override
    public ConfigSingleChart getConfigChart() {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("select csc from ConfigSingleChart csc where chartType = 'DOWNLOAD_EXCEL_TABLE'");
        Query query = entityManager.createQuery(sqlBuilder.toString());
        return (ConfigSingleChart) query.getSingleResult();
    }

    @Override
    public List<Object[]> findByCondition(ConfigSingleChartDTO configSingleChart) throws Exception {
        StringBuilder sbdBuilder = new StringBuilder();
        HashMap vmhMap = new HashMap();
        int vintFirstResult = 0;
        int vintMaxResult = 0;
        if (configSingleChart.getPager() != null) {
            vintFirstResult = (configSingleChart.getPager().getPage() - 1) * configSingleChart.getPager().getPageSize();
            vintMaxResult = configSingleChart.getPager().getPageSize();
        }
        sbdBuilder.append("select csch.title,csch.chart_size,(select card_name from config_single_card where ifnull(card_id,'a') = ifnull(csch.card_id,'a')),csch.chart_type,csch.expand,");
        sbdBuilder.append("csch.service_ids,csch.drilldown,csch.drilldown_object_id,csch.status,csch.chart_id,");
        sbdBuilder.append("csch.card_id,csch.query_param,csch.meta_data,(select group_name from config_group_card where group_id=csch.drilldown_object_id), ");
        sbdBuilder.append("(select name from ap_param where code = csch.chart_size), ");
        sbdBuilder.append("(select name from ap_param where code = csch.chart_type and type = 'CHART_TYPE') ");
        sbdBuilder.append("from config_single_chart csch ");
        sbdBuilder.append("where 1=1   ");
        sbdBuilder.append(buildSQL(configSingleChart, vmhMap));
        sbdBuilder.append(" order by csch.chart_id desc");

        Query query = entityManager.createNativeQuery(sbdBuilder.toString());
        vmhMap.forEach((k, v) -> {
            query.setParameter(k.toString(), v);
        });
        query.setFirstResult(vintFirstResult);
        query.setMaxResults(vintMaxResult);
        List<Object[]> vlstObjects = query.getResultList();
        if (!DataUtil.isNullOrEmpty(vlstObjects))
            return vlstObjects;
        else return null;

    }

    @Override
    public BigInteger countByCondition(ConfigSingleChartDTO configSingleChartDTO) throws Exception {
        HashMap vhmpMapParams = new HashMap();
        StringBuilder vsbdSqlBuilder = new StringBuilder();
        vsbdSqlBuilder.append("select count(*) ");
        vsbdSqlBuilder.append("from config_single_chart csch ");
        vsbdSqlBuilder.append("where 1=1 ");
        vsbdSqlBuilder.append(buildSQL(configSingleChartDTO, vhmpMapParams));
        Query query = entityManager.createNativeQuery(vsbdSqlBuilder.toString());
        vhmpMapParams.forEach((k, v) -> {
            query.setParameter(k.toString(), v);
        });
        return (BigInteger) query.getSingleResult();
    }

    @Override
    public List<String> findListServiceSearch(List<Long> serviceIds) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        List<String> vlstServices = new ArrayList<>();
        stringBuilder.append("select sc.serviceIds from ConfigSingleChart sc");
        Query query = entityManager.createQuery(stringBuilder.toString());
        List<String> vlstServiceIds = query.getResultList();
        if (!DataUtil.isNullOrEmpty(vlstServiceIds)) {
            for (String s : vlstServiceIds) {
                if (!DataUtil.isNullObject(s)) {
                    String[] serviceArrays = s.split(",");
                    List<String> serviceConverts = new ArrayList<>(Arrays.asList(serviceArrays));
                    for (Long serviceId : serviceIds) {
                        if (serviceConverts.contains(serviceId.toString())) {
                            vlstServices.add(s);
                            break;
                        }
                    }
                }
            }
        }
        return vlstServices;
    }

    @Transactional
    @Override
    public void addChart(ConfigSingleChart configSingleChart) throws Exception {
        entityManager.persist(configSingleChart);
    }

    @Transactional
    @Override
    public void updateChart(ConfigSingleChart configSingleChart) throws Exception {
        entityManager.merge(configSingleChart);
    }

    private StringBuilder buildSQL(ConfigSingleChartDTO configSingleChart, HashMap pmhMap) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        if (!DataUtil.isNullOrEmpty(configSingleChart.getTitle())) {
            stringBuilder.append(" and LOWER(csch.title) like concat('%',CONVERT(LOWER(:title),BINARY),'%')");
            pmhMap.put("title", configSingleChart.getTitle().trim());
        }
        if (!DataUtil.isNullOrEmpty(configSingleChart.getChartSize())) {
            stringBuilder.append(" and csch.chart_size =:chartSize");
            pmhMap.put("chartSize", configSingleChart.getChartSize());
        }
        if (!DataUtil.isNullOrZero(configSingleChart.getCardId())) {
            stringBuilder.append(" and csch.card_id =:cardId");
            pmhMap.put("cardId", configSingleChart.getCardId());
        }
        if (!DataUtil.isNullOrEmpty(configSingleChart.getChartType())) {
            stringBuilder.append(" and csch.chart_type =:chartType");
            pmhMap.put("chartType", configSingleChart.getChartType());
        }
        if (!DataUtil.isNullOrZero(configSingleChart.getExpand())) {
            stringBuilder.append(" and csch.expand =:expand");
            pmhMap.put("expand", configSingleChart.getExpand());
        }
        if (!DataUtil.isNullOrEmpty(configSingleChart.getLstServiceIds())) {
            List<String> serviceIds = findListServiceSearch(configSingleChart.getLstServiceIds());
            if (!DataUtil.isNullOrEmpty(serviceIds)) {
                stringBuilder.append(" and csch.service_ids in :serviceIds");
                pmhMap.put("serviceIds", serviceIds);
            } else {
                stringBuilder.append(" and csch.service_ids in (:service)");
                pmhMap.put("service", configSingleChart.getLstServiceIds().get(0));
            }
        }

        if (!DataUtil.isNullOrZero(configSingleChart.getDrilldown())) {
            stringBuilder.append(" and csch.drilldown =:drilldown");
            pmhMap.put("drilldown", configSingleChart.getDrilldown());
        }
        if (!DataUtil.isNullOrZero(configSingleChart.getDrillDownObjectId())) {
            stringBuilder.append(" and csch.drilldown_object_id =:drillDownObjectId");
            pmhMap.put("drillDownObjectId", configSingleChart.getDrillDownObjectId());
        }
        if (configSingleChart.getStatus() != null) {
            stringBuilder.append(" and csch.status =:status");
            pmhMap.put("status", configSingleChart.getStatus());
        }
        if (!DataUtil.isNullOrEmpty(configSingleChart.getMetaData())) {
            stringBuilder.append(" and csch.meta_data like :metaData");
            pmhMap.put("metaData", "%" + configSingleChart.getMetaData().trim() + "%");
        }
        if (!DataUtil.isNullOrEmpty(configSingleChart.getQueryParam())) {
            stringBuilder.append(" and csch.query_param like :queryParam");
            pmhMap.put("queryParam", "%" + configSingleChart.getQueryParam().trim() + "%");
        }
        return stringBuilder;
    }

}
