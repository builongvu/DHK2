package vn.vissoft.dashboard.repo;

import org.springframework.data.repository.query.Param;
import vn.vissoft.dashboard.dto.ServiceDTO;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.model.Service;

import java.math.BigInteger;
import java.sql.Date;
import java.util.List;
import java.util.Optional;

public interface ServiceRepoCustom {

    List<Service> findServicesByCondition(ServiceDTO serviceDTO) throws Exception;

    List<Service> findByChannelCodeOfUser(StaffDTO staffDTO) throws Exception;

    Long findServiceIdByCode(String pstrCode) throws Exception;

    String findNameById(Long plngServiceId) throws Exception;

    boolean checkExistedServiceCode(String pstrCode) throws Exception;

    List<Service> findActiveService() throws Exception;

    List<Service> findActiveServiceAssign() throws Exception;

    boolean checkExistedServiceAssign(String pstrCode) throws Exception;

    List<String> findActiveServiceCodeByGrCode(String pstrGroupServiceCode) throws Exception;

    boolean checkExistedServiceInGr(String pstrGroupServiceCode, String pstrServiceCode) throws Exception;

    boolean checkServiceInChannel(String pstrChannelCode, String pstrServiceCode) throws Exception;

    Date getFromDate(Long plngServiceId) throws Exception;

    Date getToDate(Long plngServiceId) throws Exception;

    Date getValueBefore(Long plngServiceId) throws Exception;

    Date getValueBeforeFirstDay(Long plngServiceId, Long pdtprd);

    Date getValueAfterFirstDay(Long plngServiceId, Long pdtPrd);

    Date getValueAfter(Long plngServiceId) throws Exception;

    Date getValueValidateBefore(Long plngServiceId,Date pdtDate) throws Exception;

    Date getValueBeforeByQuarter(Long plngServiceId,Date pdtDate) throws Exception;

    Date getValueBeforeByYear(Long plngServiceId,Date pdtDate) throws Exception;

    Date getValueValidateAfter(Long plngServiceId,Date pdtDate) throws Exception;

    Date getValueAfterByQuerter(Long plngServiceId,Date pdtDate) throws Exception;

    Date getValueAfterByYear(Long plngServiceId,Date pdtDate) throws Exception;

    BigInteger countService();

    List<Object[]> getLogOfServiceByServiceId(Long idService);

    List<Service> findAllByCode(@Param("code") String mstrCode);

    List<Service> findServiceByOrder(Long serviceOrder);

    List<Service> checkServiceParenID(Long serviceID);

    List<Object[]> findChildServiceNew(Long plngServiceId) throws Exception;

    Long findParentIdById(Long id) throws Exception;

}
