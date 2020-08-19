package vn.vissoft.dashboard.repo.impl;

import vn.vissoft.dashboard.dto.chart.ConfigSingleCardDTO;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.model.ConfigSingleCard;
import vn.vissoft.dashboard.repo.ConfigSingleCardRepoCustom;
import vn.vissoft.dashboard.helper.Constants;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;

public class ConfigSingleCardRepoImpl implements ConfigSingleCardRepoCustom {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<ConfigSingleCard> getActiveCard() throws Exception {
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append("select sc from ConfigSingleCard sc where sc.status = 1 order by case when sc.cardName like 'đ%' then 'd' else sc.cardName end" );
        Query query=entityManager.createQuery(stringBuilder.toString());
        return query.getResultList();
    }

    @Override
    public List<Object[]> getAllOrderById() throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("select csc.*, ap.name , cgc.group_name" +
                " from config_single_card csc, ap_param ap, config_group_card cgc" +
                " where ap.`type` = 'CARD_TYPE'" +
                " and ifnull(ap.code,'a') = ifnull(csc.card_type,'a')" +
                " and ifnull(cgc.group_id,'a') = ifnull(csc.group_id,'a')" +
                " order by card_id desc");
        Query query = entityManager.createNativeQuery(stringBuilder.toString());
        return query.getResultList();
    }

    /**
     * lay ra kieu dong cua card, de xet card dong cho dashboard level 3
     *
     * @param groupId
     * @return chuoi cardType theo groupId truyen vao
     * @throws Exception
     * @author HungNN
     * @version 1.0
     * @since 20/11/2019
     */
    @Override
    public List<String> lstCardDynamic(Integer groupId) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("select cardType from ConfigSingleCard where groupId = :groupId");
        Query query = entityManager.createQuery(sqlBuilder.toString());
        query.setParameter("groupId",groupId);
        return  query.getResultList();
    }

    /**
     * tim kiem du lieu trong bang config-single-card
     *
     * @param configSingleCardDTO
     * @return du lieu sau khi tim kiem
     * @throws Exception
     * @author HungNN
     * @version 1.0
     * @since 05/02/2020
     */
    @Override
    public List<Object[]> searchSingleCard(ConfigSingleCardDTO configSingleCardDTO) throws Exception {
        StringBuilder sqlBuilder = new StringBuilder();
        HashMap hashMap = new HashMap();
        sqlBuilder.append("select csc.*, ap.name , cgc.group_name" +
                " from config_single_card csc, ap_param ap, config_group_card cgc");
        sqlBuilder.append(appendSearchSingleCard(configSingleCardDTO, hashMap));
        sqlBuilder.append(" order by csc.card_id desc");
        Query query = entityManager.createNativeQuery(sqlBuilder.toString());
        hashMap.forEach((k, v) -> {
            query.setParameter(k.toString(), v);
        });
        return query.getResultList();
    }

    @Transactional
    @Override
    public void updateSingleCard(ConfigSingleCard configSingleCard) throws Exception {
        entityManager.merge(configSingleCard);
    }

    /**
     * chuoi dieu kien tim kiem
     *
     * @param configSingleCardDTO
     * @return chuoi dieu kien
     * @throws Exception
     * @author HungNN
     * @version 1.0
     * @since 05/02/2020
     */
    StringBuilder appendSearchSingleCard(ConfigSingleCardDTO configSingleCardDTO, HashMap hashMap) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" where ap.`type` = 'CARD_TYPE'");
        stringBuilder.append(" and ifnull(ap.code,'a') = ifnull(csc.card_type,'a')");
        stringBuilder.append(" and ifnull(cgc.group_id,'a') = ifnull(csc.group_id,'a')");
        if(!DataUtil.isNullOrEmpty(configSingleCardDTO.getCardName())) {
            stringBuilder.append(" and LOWER(csc.card_name) like concat('%',CONVERT(LOWER(:cardName),BINARY),'%')");
            hashMap.put("cardName", configSingleCardDTO.getCardName().trim());
        }
        if(!DataUtil.isNullOrEmpty(configSingleCardDTO.getCardType())) {
            stringBuilder.append(" and LOWER(csc.card_type) like concat('%',CONVERT(LOWER(:cardType),BINARY),'%')");
            hashMap.put("cardType", configSingleCardDTO.getCardType());
        }
        if(configSingleCardDTO.getDrilldown() != null) {
            stringBuilder.append(" and csc.drilldown = :drillDown");
            hashMap.put("drillDown", configSingleCardDTO.getDrilldown());
        }
        if(!DataUtil.isNullOrEmpty(configSingleCardDTO.getCardSize())) {
            stringBuilder.append(" and csc.card_size = :cardSize");
            hashMap.put("cardSize", configSingleCardDTO.getCardSize());
        }
        if(configSingleCardDTO.getZoom() != null) {
            stringBuilder.append(" and csc.zoom = :zoomCard");
            hashMap.put("zoomCard", configSingleCardDTO.getZoom());
        }
        if(configSingleCardDTO.getDrillDownObjectId() != null) {
            stringBuilder.append(" and csc.drilldown_object_id = :drillDownObjectId");
            hashMap.put("drillDownObjectId", configSingleCardDTO.getDrillDownObjectId());
        }
        if(!DataUtil.isNullOrZero(configSingleCardDTO.getGroupId())) {
            stringBuilder.append(" and csc.group_id = :groupId");
            hashMap.put("groupId", configSingleCardDTO.getGroupId());
        }
        if(configSingleCardDTO.getServiceId() != null) {
            if (configSingleCardDTO.getServiceId() != -1) {
                stringBuilder.append(" and csc.service_id = :serviceId");
                hashMap.put("serviceId", configSingleCardDTO.getServiceId());
            }
        }
        if(configSingleCardDTO.getStatus() != null) {
            stringBuilder.append(" and csc.status = :statusCard");
            hashMap.put("statusCard", configSingleCardDTO.getStatus());
        }
        return stringBuilder;
    }

    @Override
    public List<Object[]> checkDuplicate(ConfigSingleCard configSingleCard, String condition) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("select * from config_single_card where card_name = :cardName and" +
                " card_size = :cardSize and drilldown = :drillDown and group_id = :groupId and card_type = :cardType and zoom = :zoom");
        if(condition.equals(Constants.UPDATE)) {
            stringBuilder.append(" and card_id <> :id");
        }
        Query query = entityManager.createNativeQuery(stringBuilder.toString());
        query.setParameter("cardName", configSingleCard.getCardName());
        query.setParameter("cardSize", configSingleCard.getCardSize());
        query.setParameter("drillDown", configSingleCard.getDrilldown());
        query.setParameter("groupId", configSingleCard.getGroupId());
        query.setParameter("cardType", configSingleCard.getCardType());
        query.setParameter("zoom", configSingleCard.getZoom());
        if(condition.equals(Constants.UPDATE)) {
            query.setParameter("id", configSingleCard.getCardId());
        }
        return query.getResultList();
    }
}
