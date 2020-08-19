package vn.vissoft.dashboard.repo.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vn.vissoft.dashboard.dto.SearchWarningReceiveDTO;
import vn.vissoft.dashboard.dto.SearchWarningSendDTO;
import vn.vissoft.dashboard.dto.excel.WarningServiceExcel;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.model.*;
import vn.vissoft.dashboard.repo.WarningConfigRepoCustom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;

public class WarningConfigRepoImpl implements WarningConfigRepoCustom {
    @PersistenceContext
    EntityManager entityManager;
    private static final Logger LOGGER = LogManager.getLogger(WarningConfigRepoImpl.class);

    //    Tìm kiếm cấu hình gửi cảnh báo theo điều kiện
    @Override
    public List<WarningSendConfig> getByCondition(SearchWarningSendDTO sr, int page, int size) throws Exception {
        StringBuilder vstrhql = new StringBuilder();
        if (!DataUtil.isNullOrZero(sr.getServiceId()) && sr.getServiceId() == -1) {
            vstrhql.append("select u.id, u.service_id, warning_level, email, sms, inform_level, id_content, u.user, u.create_date," +
                    " u.status  FROM warning_send_config as u where 1 = 1 ");
            if (sr.getServiceId() != null) {
                vstrhql.append(" and u.service_id = ").append(sr.getServiceId());
            }
            if (sr.getStatus() != null && !"-1".equals(sr.getStatus())) {
                vstrhql.append(" and u.status = ").append(sr.getStatus());
            }
            if (!"-1".equals(sr.getSms())) {
                if (sr.getEmail() != null) {
                    vstrhql.append(" and email = ").append(sr.getEmail());
                }
                if (sr.getSms() != null) {
                    vstrhql.append(" and sms = ").append(sr.getSms());
                }
            }
            if (sr.getInformLevel() != null) {
                vstrhql.append(" and inform_level = ").append(sr.getInformLevel());
            }
            if (sr.getWarningLevel() != null) {
                vstrhql.append(" and warning_level = ").append(sr.getWarningLevel());
            }

            vstrhql.append(" order by u.id desc");
        } else {
            vstrhql.append("select u.id, u.service_id, warning_level, email, sms, inform_level, id_content, u.user, u.create_date," +
                    " u.status  FROM warning_send_config as u join service as s on u.service_id = s.id where s.status = '1' ");
            if (sr.getServiceId() != null) {
                vstrhql.append(" and u.service_id = ").append(sr.getServiceId());
            }
            if (sr.getStatus() != null && !"-1".equals(sr.getStatus())) {
                vstrhql.append(" and u.status = ").append(sr.getStatus());
            }
            if (!"-1".equals(sr.getSms())) {
                if (sr.getEmail() != null) {
                    vstrhql.append(" and email = ").append(sr.getEmail());
                }
                if (sr.getSms() != null) {
                    vstrhql.append(" and sms = ").append(sr.getSms());
                }
            }
            if (sr.getInformLevel() != null) {
                vstrhql.append(" and inform_level = ").append(sr.getInformLevel());
            }
            if (sr.getWarningLevel() != null) {
                vstrhql.append(" and warning_level = ").append(sr.getWarningLevel());
            }

            if (DataUtil.isNullOrZero(sr.getServiceId())) {
                vstrhql.append(" union all ");
                vstrhql.append("select u.id, u.service_id, warning_level, email, sms, inform_level, id_content, u.user, u.create_date," +
                        " u.status  FROM warning_send_config as u where service_id = -1 ");
                if (sr.getStatus() != null && !"-1".equals(sr.getStatus())) {
                    vstrhql.append(" and u.status = ").append(sr.getStatus());
                }
                if (!"-1".equals(sr.getSms())) {
                    if (sr.getEmail() != null) {
                        vstrhql.append(" and email = ").append(sr.getEmail());
                    }
                    if (sr.getSms() != null) {
                        vstrhql.append(" and sms = ").append(sr.getSms());
                    }
                }
                if (sr.getInformLevel() != null) {
                    vstrhql.append(" and inform_level = ").append(sr.getInformLevel());
                }
                if (sr.getWarningLevel() != null) {
                    vstrhql.append(" and warning_level = ").append(sr.getWarningLevel());
                }
            }

            vstrhql.append(" order by id desc");
        }
        List<Object[]> vlstCOnfigs = entityManager.createNativeQuery(vstrhql.toString()).setFirstResult((page - 1) * size).setMaxResults(size).getResultList();
        List<WarningSendConfig> vlstWarning = new ArrayList<>();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (Object[] vlstCOnfig : vlstCOnfigs) {
            String date = vlstCOnfig[8].toString();
            WarningSendConfig warningSendConfig = new WarningSendConfig();
            warningSendConfig.setMstrStatus(vlstCOnfig[9].toString());
            warningSendConfig.setMlngId(Long.parseLong(vlstCOnfig[0].toString()));
            warningSendConfig.setMdtCreateDate(df.parse(date));
            warningSendConfig.setMstrUser(vlstCOnfig[7].toString());
            warningSendConfig.setMintSms(DataUtil.safeToInt(vlstCOnfig[4]));
            warningSendConfig.setMintEmail(DataUtil.safeToInt(vlstCOnfig[3]));
            warningSendConfig.setMintInformLevel(DataUtil.safeToInt(vlstCOnfig[5]));
            warningSendConfig.setMlngIdContent(DataUtil.safeToLong(vlstCOnfig[6]));
            warningSendConfig.setMintWarningLevel(DataUtil.safeToInt(vlstCOnfig[2]));
            warningSendConfig.setMlngServiceId(DataUtil.safeToLong(vlstCOnfig[1]));
            vlstWarning.add(warningSendConfig);
        }
        return vlstWarning;
    }

    @Override
    public BigInteger countWarningSendGetByCondition(SearchWarningSendDTO sr) {
        StringBuilder vstrhql = new StringBuilder();
        StringBuilder vstrCount = new StringBuilder();
        BigInteger vbintCount ;
        if (!DataUtil.isNullOrZero(sr.getServiceId()) && sr.getServiceId() == -1) {
            vstrhql.append("select count(*) FROM warning_send_config as u where 1 = 1 ");
            if (sr.getServiceId() != null) {
                vstrhql.append(" and u.service_id = ").append(sr.getServiceId());
            }
            if (sr.getStatus() != null && !"-1".equals(sr.getStatus())) {
                vstrhql.append(" and u.status = ").append(sr.getStatus());
            }
            if (!"-1".equals(sr.getSms())) {
                if (sr.getEmail() != null) {
                    vstrhql.append(" and email = ").append(sr.getEmail());
                }
                if (sr.getSms() != null) {
                    vstrhql.append(" and sms = ").append(sr.getSms());
                }
            }
            if (sr.getInformLevel() != null) {
                vstrhql.append(" and inform_level = ").append(sr.getInformLevel());
            }
            if (sr.getWarningLevel() != null) {
                vstrhql.append(" and warning_level = ").append(sr.getWarningLevel());
            }
            vbintCount = (BigInteger) entityManager.createNativeQuery(vstrhql.toString()).getSingleResult();
        } else {
            vstrhql.append("select count(*)  FROM warning_send_config as u join service as s on u.service_id = s.id where s.status = '1' ");
            if (sr.getServiceId() != null) {
                vstrhql.append(" and u.service_id = ").append(sr.getServiceId());
            }
            if (sr.getStatus() != null && !"-1".equals(sr.getStatus())) {
                vstrhql.append(" and u.status = ").append(sr.getStatus());
            }
            if (!"-1".equals(sr.getSms())) {
                if (sr.getEmail() != null) {
                    vstrhql.append(" and email = ").append(sr.getEmail());
                }
                if (sr.getSms() != null) {
                    vstrhql.append(" and sms = ").append(sr.getSms());
                }
            }
            if (sr.getInformLevel() != null) {
                vstrhql.append(" and inform_level = ").append(sr.getInformLevel());
            }
            if (sr.getWarningLevel() != null) {
                vstrhql.append(" and warning_level = ").append(sr.getWarningLevel());
            }
            vbintCount = (BigInteger) entityManager.createNativeQuery(vstrhql.toString()).getSingleResult();

            if (DataUtil.isNullOrZero(sr.getServiceId())) {
                vstrCount.append("select count(*) FROM warning_send_config as u where service_id = -1 ");
                if (sr.getStatus() != null && !"-1".equals(sr.getStatus())) {
                    vstrCount.append(" and u.status = ").append(sr.getStatus());
                }
                if (!"-1".equals(sr.getSms())) {
                    if (sr.getEmail() != null) {
                        vstrCount.append(" and email = ").append(sr.getEmail());
                    }
                    if (sr.getSms() != null) {
                        vstrCount.append(" and sms = ").append(sr.getSms());
                    }
                }
                if (sr.getInformLevel() != null) {
                    vstrCount.append(" and inform_level = ").append(sr.getInformLevel());
                }
                if (sr.getWarningLevel() != null) {
                    vstrCount.append(" and warning_level = ").append(sr.getWarningLevel());
                }
                vbintCount = vbintCount.add((BigInteger) entityManager.createNativeQuery(vstrCount.toString()).getSingleResult());
            }
//            vstrhql.append(" order by u.id desc");
        }
//        return (BigInteger) entityManager.createNativeQuery(vstrhql.toString()).getSingleResult();
        return vbintCount;
    }

    //    Tìm kiếm cấu hình nhận cảnh báo theo điều kiện
    @Override
    public List<WarningReceiveConfig> getReceiveByCondition(SearchWarningReceiveDTO swr, int page, int size) throws Exception {
        StringBuilder vstrhql = new StringBuilder();
        if (!DataUtil.isNullOrEmpty(swr.getMstrShopCode()) && "-1".equals(swr.getMstrShopCode())) {
            vstrhql.append("select w.id, w.shop_code, w.warning_level, w.inform_level, w.status, w.user, w.create_date from warning_receive_config as w where 1 = 1 ");
            if (swr.getMstrStatus() != null && !"-1".equals(swr.getMstrStatus())) {
                vstrhql.append(" and w.status = ").append(swr.getMstrStatus());
            }
            if (swr.getMintInformLevel() != null) {
                vstrhql.append(" and w.inform_level = ").append(swr.getMintInformLevel());
            }
            if (swr.getMintWarningLevel() != null) {
                vstrhql.append(" and w.warning_level = ").append(swr.getMintWarningLevel());
            }
            if (swr.getMstrShopCode() != null) {
                vstrhql.append(" and w.shop_code = '").append(swr.getMstrShopCode()).append("'");
            }

            vstrhql.append(" order by w.id desc");
        } else {
            vstrhql.append("select w.id, w.shop_code, w.warning_level, w.inform_level, w.status, w.user, w.create_date from warning_receive_config as w join manage_info_partner as m on w.shop_code = m.shop_code and m.status = '1' ");
            if (swr.getMstrStatus() != null && !"-1".equals(swr.getMstrStatus())) {
                vstrhql.append(" and w.status = ").append(swr.getMstrStatus());
            }
            if (swr.getMintInformLevel() != null) {
                vstrhql.append(" and w.inform_level = ").append(swr.getMintInformLevel());
            }
            if (swr.getMintWarningLevel() != null) {
                vstrhql.append(" and w.warning_level = ").append(swr.getMintWarningLevel());
            }

            if (swr.getMstrShopCode() != null) {
                vstrhql.append(" and w.shop_code = '").append(swr.getMstrShopCode()).append("'");
            }
            if (DataUtil.isNullOrEmpty(swr.getMstrShopCode())) {
                vstrhql.append(" union all ");
                vstrhql.append(" select w.id, w.shop_code, w.warning_level, w.inform_level, w.status, w.user, w.create_date from warning_receive_config as w where shop_code = '-1' ");

                if (swr.getMstrStatus() != null && !"-1".equals(swr.getMstrStatus())) {
                    vstrhql.append(" and w.status = ").append(swr.getMstrStatus());
                }
                if (swr.getMintInformLevel() != null) {
                    vstrhql.append(" and w.inform_level = ").append(swr.getMintInformLevel());
                }
                if (swr.getMintWarningLevel() != null) {
                    vstrhql.append(" and w.warning_level = ").append(swr.getMintWarningLevel());
                }
            }

            vstrhql.append(" order by id desc");
        }
        List<Object[]> vlistObjs = entityManager.createNativeQuery(vstrhql.toString()).setFirstResult((page - 1) * size).setMaxResults(size).getResultList();
        List<WarningReceiveConfig> vlstWarning = new ArrayList<>();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (Object[] vlistObj : vlistObjs) {
            String date = vlistObj[6].toString();
            WarningReceiveConfig warningReceiveConfig = new WarningReceiveConfig();
            warningReceiveConfig.setMdtCreateDate(df.parse(date));
            warningReceiveConfig.setMstrStatus(vlistObj[4].toString());
            warningReceiveConfig.setMstrUser(vlistObj[5].toString());
            warningReceiveConfig.setMlngId(Long.parseLong(vlistObj[0].toString()));
            warningReceiveConfig.setMstrShopCode(vlistObj[1].toString());
            warningReceiveConfig.setMintInformLevel(DataUtil.safeToInt(vlistObj[3]));
            warningReceiveConfig.setMintWarningLevel(DataUtil.safeToInt(vlistObj[2]));
            vlstWarning.add(warningReceiveConfig);
        }
        return vlstWarning;
    }

    @Override
    public BigInteger countWarningReceiveGetByCondition(SearchWarningReceiveDTO swr) {
        StringBuilder vstrhql = new StringBuilder();
        StringBuilder vstrCount = new StringBuilder();
        BigInteger vbintCount;
        if (!DataUtil.isNullOrEmpty(swr.getMstrShopCode()) && "-1".equals(swr.getMstrShopCode())) {
            vstrhql.append("select count(*) from warning_receive_config as w where 1 = 1 ");
            if (swr.getMstrStatus() != null && !"-1".equals(swr.getMstrStatus())) {
                vstrhql.append(" and w.status = ").append(swr.getMstrStatus());
            }
            if (swr.getMintInformLevel() != null) {
                vstrhql.append(" and w.inform_level = ").append(swr.getMintInformLevel());
            }
            if (swr.getMintWarningLevel() != null) {
                vstrhql.append(" and w.warning_level = ").append(swr.getMintWarningLevel());
            }
            if (swr.getMstrShopCode() != null) {
                vstrhql.append(" and w.shop_code = '").append(swr.getMstrShopCode()).append("'");
            }
            vbintCount = (BigInteger) entityManager.createNativeQuery(vstrhql.toString()).getSingleResult();
        } else {
            vstrhql.append("select count(*) from warning_receive_config as w join manage_info_partner as m on w.shop_code = m.shop_code ");
            if (swr.getMstrStatus() != null && !"-1".equals(swr.getMstrStatus())) {
                vstrhql.append(" and w.status = ").append(swr.getMstrStatus());
            }
            if (swr.getMintInformLevel() != null) {
                vstrhql.append(" and w.inform_level = ").append(swr.getMintInformLevel());
            }
            if (swr.getMintWarningLevel() != null) {
                vstrhql.append(" and w.warning_level = ").append(swr.getMintWarningLevel());
            }
            if (swr.getMstrShopCode() != null) {
                vstrhql.append(" and w.shop_code = '").append(swr.getMstrShopCode()).append("'");
            }
            vbintCount = (BigInteger) entityManager.createNativeQuery(vstrhql.toString()).getSingleResult();

            if (DataUtil.isNullOrEmpty(swr.getMstrShopCode())) {
                vstrCount.append(" select count(*) from warning_receive_config as w where shop_code = '-1' ");

                if (swr.getMstrStatus() != null && !"-1".equals(swr.getMstrStatus())) {
                    vstrCount.append(" and w.status = ").append(swr.getMstrStatus());
                }
                if (swr.getMintInformLevel() != null) {
                    vstrCount.append(" and w.inform_level = ").append(swr.getMintInformLevel());
                }
                if (swr.getMintWarningLevel() != null) {
                    vstrCount.append(" and w.warning_level = ").append(swr.getMintWarningLevel());
                }
                vbintCount = vbintCount.add((BigInteger) entityManager.createNativeQuery(vstrCount.toString()).getSingleResult());
            }

//        vstrhql.append(" order by w.id desc");
        }
//        return (BigInteger) entityManager.createNativeQuery(vstrhql.toString()).getSingleResult();
        return vbintCount;
    }

    //    Check dữ liệu đã tồn tại cấu hình gửi cảnh báo
    @Override
    public List<WarningSendConfig> checkDuplicateWarningSend(Long ServiceId, int WarningLevel, Long id) {
        String vstrhql = "FROM WarningSendConfig as u where mlngServiceId = :serviceId and mintWarningLevel = :warningLevel and mlngId != :id";
        Query query = entityManager.createQuery(vstrhql);
        query.setParameter("serviceId", ServiceId);
        query.setParameter("warningLevel", WarningLevel);
        query.setParameter("id", id);
        return (List<WarningSendConfig>) query.getResultList();
    }
    @Override
    public List<ServiceWarningConfig> checkDuplicateWarningConfig(Long ServiceId, int WarningLevel, String vdsCode ,String status) {

        String vstrhql= "FROM ServiceWarningConfig as u where serviceId = :serviceId and waningLevel = :warningLevel and vdsChannelCode =:VDS_CHANNEL_CODE and status  =:status ";
        Query query = entityManager.createQuery(vstrhql);
        query.setParameter("serviceId",ServiceId);
        query.setParameter("warningLevel",WarningLevel);
        query.setParameter("VDS_CHANNEL_CODE",vdsCode);
        query.setParameter("status",status);
        return (List<ServiceWarningConfig>) query.getResultList();

    }

    @Override
    public List<ServiceWarningConfig> checkDuplicateWarningConfig1(Long id, Long ServiceId, int WarningLevel, String vdsCode,String status) {
        String vstrhql = "FROM ServiceWarningConfig as u where u.serviceId = :serviceId and waningLevel = :warningLevel and vdsChannelCode =:VDS_CHANNEL_CODE and id <>:id and status =:status ";
        Query query = entityManager.createQuery(vstrhql);
        query.setParameter("id",id);
        query.setParameter("serviceId",ServiceId);
        query.setParameter("warningLevel",WarningLevel);
        query.setParameter("VDS_CHANNEL_CODE",vdsCode);
        query.setParameter("status",status);
        return (List<ServiceWarningConfig>) query.getResultList();
    }

    @Override
    public List<ServiceWarningConfig> checkDuplicateValue(Long ServiceId, String vdsCode, Double fromvalue, Double tovalue,String status) {
        String vstrhql = "from ServiceWarningConfig as u where serviceId = :serviceId and vdsChannelCode = :VDS_CHANNEL_CODE and  (( (:from_value <= u.fromValue and  :from_value >= u.toValue) or :from_value <= u.toValue ) and ((:to_value <= u.fromValue and :to_value >= u.toValue) or :to_value >= u.fromValue)) and status = :status  ";
      try {
          Query query = entityManager.createQuery(vstrhql);
          query.setParameter("serviceId", ServiceId);
          query.setParameter("VDS_CHANNEL_CODE",vdsCode);
          query.setParameter("from_value",fromvalue);
          query.setParameter("to_value",tovalue);
          query.setParameter("status",status);
          return (List<ServiceWarningConfig>) query.getResultList();
      }catch (Exception e){
          LOGGER.error(e.getMessage(),e);
          return new ArrayList<>();
      }
    }

    @Override
    public List<ServiceWarningConfig> checkDuplicateValue1(Long id, Long ServiceId, String vdsCode, Double fromvalue, Double tovalue,String status) {
        String vstrhql = "from ServiceWarningConfig as u where serviceId = :serviceId and vdsChannelCode = :VDS_CHANNEL_CODE and  (( (:from_value <= u.fromValue and  :from_value >= u.toValue) or :from_value <= u.toValue ) and ((:to_value <= u.fromValue and :to_value >= u.toValue) or :to_value >= u.fromValue)) and id <>:id and status = :status  ";

        Query query = entityManager.createQuery(vstrhql);
        query.setParameter("id",id);
        query.setParameter("serviceId", ServiceId);
        query.setParameter("VDS_CHANNEL_CODE",vdsCode);
        query.setParameter("from_value",fromvalue);
        query.setParameter("to_value",tovalue);
        query.setParameter("status",status);
        return (List<ServiceWarningConfig>) query.getResultList();
    }

    //    lấy toàn bộ cấu hình gửi cảnh báo có sắp xếp thứ tự
    @Override
    public List<WarningSendConfig> getAllOrderBy(String column, String orderby) {
        String vstrhql = "FROM WarningSendConfig as u  order by :column  :orderby";
        Query query = entityManager.createQuery(vstrhql);
        query.setParameter("column", column);
        query.setParameter("orderby", orderby);
        return (List<WarningSendConfig>) query.getResultList();
    }

    //    lấy toàn bộ cấu hình nhận cảnh báo có sắp xếp thứ tự
    @Override
    public List<WarningReceiveConfig> getAllReceiveOrderBy() {
        String vstrhql = "FROM WarningReceiveConfig as u  order by mlngId desc";
        Query query = entityManager.createQuery(vstrhql);
        return (List<WarningReceiveConfig>) query.getResultList();
    }



    //    Check dữ liệu đã tồn tại cấu hình nhận cảnh báo
    @Override
    public List<WarningReceiveConfig> checkDuplicateWarningReceive(String shopCode, int WarningLevel, Long id) {
        String vstrhql = "FROM WarningReceiveConfig as u where mstrShopCode = :shopCode and mintWarningLevel = :WarningLevel and mlngId != :id";
        Query query = entityManager.createQuery(vstrhql);
        query.setParameter("shopCode", shopCode);
        query.setParameter("WarningLevel", WarningLevel);
        query.setParameter("id", id);
        return (List<WarningReceiveConfig>) query.getResultList();
    }

    //    lấy ra bản ghi đầu tiên của nội dung cảnh báo
    @Override
    public WarningContent getFirst() throws Exception {
        String vstrhql = "FROM WarningContent as u where mstrStatus = 1";
        Query query = entityManager.createQuery(vstrhql).setFirstResult(0).setMaxResults(1);
        return (WarningContent) query.getSingleResult();
    }

    //    tìm kiếm ApParam theo mã Id
    @Override
    public ApParam getById(Long id) {
        String vstrhql = "FROM ApParam as u where id = :id";
        Query query = entityManager.createQuery(vstrhql);
        query.setParameter("id", id);
        return (ApParam) query.getResultList().get(0);
    }

    //    Lấy Object Cấu hình gửi cảnh báo theo mã ID
    @Override
    public WarningSendConfig getWarningSendByID(Long id) {
        String vstrhql = "FROM WarningSendConfig as u where mlngId = :id";
        Query query = entityManager.createQuery(vstrhql);
        query.setParameter("id", id);
        List<WarningSendConfig> list = query.getResultList();
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    @Override
    public List<WarningContent> getContentByContent(String content) {
        String vstrhql = "FROM WarningContent as u where mstrContent = :content";
        Query query = entityManager.createQuery(vstrhql);
        query.setParameter("content", content);
        return query.getResultList();
    }

    @Override
    public ManageInfoPartner getPartnerByShopCode(String shopcode) {
        String vstrhql = "FROM ManageInfoPartner as u where shopCode = :shopcode";
        Query query = entityManager.createQuery(vstrhql);
        query.setParameter("shopCode", shopcode);
        List<ManageInfoPartner> list = query.getResultList();
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    @Override
    public List<WarningSendConfig> getAllWarningSend(int page, int size) {
        String vstrhql = "FROM WarningSendConfig as u";
        List<WarningSendConfig> list = entityManager.createQuery(vstrhql).setFirstResult((page - 1) * size).setMaxResults(size).getResultList();
        if (list.size() > 0) {
            return list;
        } else {
            return null;
        }
    }

    @Override
    public WarningConfig findWarningConfigFromFile(WarningServiceExcel warningServiceExcel) throws Exception {
        HashMap mapParams = new HashMap();
        StringBuilder sqlBuilder = new StringBuilder();

        sqlBuilder.append("select w from WarningConfig w");
        sqlBuilder.append(buildSQLFromPlan(warningServiceExcel, mapParams));

        Query query = entityManager.createQuery(sqlBuilder.toString());
        mapParams.forEach((k, v) -> {
            query.setParameter(k.toString(), v);
        });
        List<WarningConfig> vlstWarningConfigs = query.getResultList();
        if (DataUtil.isNullOrEmpty(vlstWarningConfigs)) return null;
        return vlstWarningConfigs.get(0);
    }

    @Transactional
    @Override
    public void persist(WarningConfig warningConfig) throws Exception {
        entityManager.persist(warningConfig);
    }

    @Transactional
    @Override
    public void update(WarningConfig warningConfig) throws Exception {
        entityManager.merge(warningConfig);
    }

    @Override
    public List<WarningConfig> findByChannelServiceNotLv(String pstrChannelCode, Long plngServiceId, Integer pintWarning) {
        StringBuilder sqlBuilder = new StringBuilder();
        List<WarningConfig> vlstWarningConfigs = null;

        sqlBuilder.append("select vds_channel_code,service_id,warning_level,from_value,to_value from service_warning_config where vds_channel_code =:vdsChannelCode and service_id =:serviceId and warning_level !=:warning");
        Query query = entityManager.createNativeQuery(sqlBuilder.toString());
        query.setParameter("vdsChannelCode", pstrChannelCode);
        query.setParameter("serviceId", plngServiceId);
        query.setParameter("warning", pintWarning);
        List<Object[]> vlst = query.getResultList();
        if (!DataUtil.isNullOrEmpty(vlst)) {
            vlstWarningConfigs = new ArrayList<>();
            for (int i = 0; i < vlst.size(); i++) {
                WarningConfig warningConfig = new WarningConfig();
                warningConfig.setVdscCode(DataUtil.safeToString(vlst.get(i)[0]));
                warningConfig.setSvID(DataUtil.safeToLong(vlst.get(i)[1]));
                warningConfig.setWlevel(DataUtil.safeToInt(vlst.get(i)[2]));
                warningConfig.setWfvalue(DataUtil.safeToDouble(vlst.get(i)[3]));
                warningConfig.setWovalue(DataUtil.safeToDouble(vlst.get(i)[4]));
                vlstWarningConfigs.add(warningConfig);
            }
        }

        return vlstWarningConfigs;
    }

    @Override
    public List<Object[]> getAllAndSortById(Long serviceId) throws Exception {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("select id,service_id,vds_channel_code,warning_level,status, from_value,to_value, convert(exp using utf8)as exp" +
                " from service_warning_config where service_id = :serviceId order by id desc");
        Query query = entityManager.createNativeQuery(sqlBuilder.toString());
        query.setParameter("serviceId",serviceId);
        return query.getResultList();
    }

    /**
     * dieu kien tim kiem nguong canh bao trong file excel
     *
     * @param warningServiceExcel
     * @param params
     * @return
     * @author DatNT
     * @since 15/09/2019
     */
    private StringBuilder buildSQLFromPlan(WarningServiceExcel warningServiceExcel, HashMap params) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" where 1=1 ");

        if (!DataUtil.isNullOrEmpty(warningServiceExcel.getVdsChannelCode())) {
            stringBuilder.append(" and w.vdscCode = :vdsChannelCode");
            params.put("vdsChannelCode", warningServiceExcel.getVdsChannelCode().trim());
        }
        if (!DataUtil.isNullOrEmpty(warningServiceExcel.getServiceCode())) {
            stringBuilder.append(" and w.svID = (select id from Service where code =:serviceCode)");
            params.put("serviceCode", warningServiceExcel.getServiceCode().trim());
        }
        if (!DataUtil.isNullOrZero(warningServiceExcel.getWarning())) {
            stringBuilder.append(" and w.wlevel =:warning");
            params.put("warning", Integer.parseInt(warningServiceExcel.getWarning()));
        }

        return stringBuilder;
    }

}
