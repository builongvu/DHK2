package vn.vissoft.dashboard.repo;

import org.springframework.stereotype.Service;
import vn.vissoft.dashboard.dto.SearchWarningReceiveDTO;
import vn.vissoft.dashboard.dto.SearchWarningSendDTO;
import vn.vissoft.dashboard.dto.excel.WarningServiceExcel;
import vn.vissoft.dashboard.model.*;

import java.math.BigInteger;
import java.util.List;
@Service
public interface WarningConfigRepoCustom {
    List<WarningSendConfig> getByCondition(SearchWarningSendDTO sr, int page, int size) throws Exception;

    BigInteger countWarningSendGetByCondition(SearchWarningSendDTO sr);

    List<WarningReceiveConfig> getReceiveByCondition(SearchWarningReceiveDTO swr, int page, int size) throws Exception;

    BigInteger countWarningReceiveGetByCondition(SearchWarningReceiveDTO swr);

    List<WarningSendConfig> checkDuplicateWarningSend(Long ServiceId, int WarningLevel, Long id);

    List<WarningSendConfig> getAllOrderBy(String column, String orderby);

    List<WarningReceiveConfig> getAllReceiveOrderBy();

   List<ServiceWarningConfig> checkDuplicateWarningConfig(Long ServiceId, int WarningLevel, String vdsCode,String status);
    List<ServiceWarningConfig> checkDuplicateWarningConfig1(Long id,Long ServiceId, int WarningLevel, String vdsCode,String status);
    List<ServiceWarningConfig> checkDuplicateValue(Long ServiceId, String vdsCode, Double fromvalue,Double tovalue,String status );
    List<ServiceWarningConfig> checkDuplicateValue1(Long id,Long ServiceId, String vdsCode, Double fromvalue,Double tovalue,String status );
    List<WarningReceiveConfig> checkDuplicateWarningReceive(String shopCode, int WarningLevel, Long id);

    WarningContent getFirst() throws Exception;

    ApParam getById(Long id);

    WarningSendConfig getWarningSendByID(Long id);

    List<WarningContent> getContentByContent(String content);

    ManageInfoPartner getPartnerByShopCode(String shopcode);

    List<WarningSendConfig> getAllWarningSend(int page, int size);

    WarningConfig findWarningConfigFromFile(WarningServiceExcel warningServiceExcel) throws Exception;

    void persist(WarningConfig warningConfig) throws Exception;

    void update(WarningConfig warningConfig) throws Exception;

    List<WarningConfig> findByChannelServiceNotLv(String pstrChannelCode,Long plngServiceId,Integer pintWarning);

    List<Object[]> getAllAndSortById(Long serviceId) throws Exception;
}
