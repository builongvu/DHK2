package vn.vissoft.dashboard.repo.impl;

import org.springframework.stereotype.Component;

import vn.vissoft.dashboard.dto.chart.ConfigGroupCardDTO;
import vn.vissoft.dashboard.dto.chart.ConfigSingleCardDTO;
import vn.vissoft.dashboard.dto.chart.ConfigSingleChartDTO;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.mapper.BaseMapper;
import vn.vissoft.dashboard.model.ConfigGroupCard;
import vn.vissoft.dashboard.model.ConfigSingleCard;
import vn.vissoft.dashboard.model.ConfigSingleChart;
import vn.vissoft.dashboard.repo.DashboardRepoCustom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import java.util.List;

@Component
public class DashboardRepoCustomImpl implements DashboardRepoCustom {

    @PersistenceContext
    EntityManager entityManager;
    private BaseMapper<ConfigSingleCard, ConfigSingleCardDTO> mapperCard = new BaseMapper<>(ConfigSingleCard.class, ConfigSingleCardDTO.class);
    private BaseMapper<ConfigGroupCard, ConfigGroupCardDTO> mapperGroupCard = new BaseMapper<>(ConfigGroupCard.class, ConfigGroupCardDTO.class);
    private BaseMapper<ConfigSingleChart, ConfigSingleChartDTO> mapperSingleChart = new BaseMapper<>(ConfigSingleChart.class, ConfigSingleChartDTO.class);

    /**
     * lay ra config group card theo group id
     *
     * @param plngGroupId
     * @return config group card
     * @author HungNN
     * @version 1.0
     * @since 15/10/2019
     */
    @Override
    public ConfigGroupCardDTO getConfigGroup(Long plngGroupId) {

        StringBuilder vsbdSqlBuilder = new StringBuilder();
        vsbdSqlBuilder.append("select c from ConfigGroupCard c where c.groupId = :groupId ");
        Query query = entityManager.createQuery(vsbdSqlBuilder.toString());
        query.setParameter("groupId", Long.valueOf(plngGroupId));

        ConfigGroupCard configGroupCardDTO = (ConfigGroupCard) query.getSingleResult();
        return mapperGroupCard.toDtoBean(configGroupCardDTO);
    }

    /**
     * lay ra config single card theo group id
     *
     * @param plngGroupId
     * @return config single card
     * @author HungNN
     * @version 1.0
     * @since 15/10/2019
     */
    @Override
    public List<ConfigSingleCardDTO> getSingleCard(Long plngGroupId) {

        StringBuilder vsbdSqlBuilder = new StringBuilder();
        vsbdSqlBuilder.append("select cs from ConfigSingleCard cs where cs.groupId = :groupId and (cs.serviceId is null or cs.serviceId in (select id from Service where status='1')) and cs.status = 1 order by cardOrder");
        Query query = entityManager.createQuery(vsbdSqlBuilder.toString());
        if (!DataUtil.isNullOrZero(plngGroupId))
            query.setParameter("groupId", plngGroupId.intValue());
        List<ConfigSingleCard> result = query.getResultList();
        return mapperCard.toDtoBean(result);
    }

    /**
     * lay ra config single chart theo card id
     *
     * @param plngCardId
     * @return config single chart
     * @author HungNN
     * @version 1.0
     * @since 15/10/2019
     */
    @Override
    public List<ConfigSingleChartDTO> getSingleChart(Long plngCardId) {

        StringBuilder vsbdSqlBuilder = new StringBuilder();
        vsbdSqlBuilder.append("select sc from ConfigSingleChart sc ");
        vsbdSqlBuilder.append(" where sc.cardId = :cardId");
        Query query = entityManager.createQuery(vsbdSqlBuilder.toString());
        query.setParameter("cardId", plngCardId);
        List<ConfigSingleChart> cardDTOS = query.getResultList();
        return mapperSingleChart.toDtoBean(cardDTOS);
    }


}
