package vn.vissoft.dashboard.services;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;
import vn.vissoft.dashboard.dto.LogHistoryServiceDTO;
import vn.vissoft.dashboard.dto.ServiceDTO;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.excel.BaseUploadEntity;
import vn.vissoft.dashboard.dto.excel.ShopUnitExcel;
import vn.vissoft.dashboard.model.Service;

import java.math.BigInteger;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;

public interface ServiceService {

    List<Service> getServicesByGroupServiceIdAndStatus(Long plngGroupServiceId, String pstrStatus) throws Exception;

    List<Service> getAllServices();

    List<ServiceDTO> getServicesByCondition(ServiceDTO serviceDTO) throws Exception;

    List<Service> getByChannelCodeOfUser(StaffDTO staffDTO) throws Exception;

    boolean checkActiveService(Long plngServiceId,Long plngMonth, String pstrCycleCode) throws Exception;

    BigInteger countService();

    void createService(ServiceDTO serviceDTO, StaffDTO staffDTO);

    boolean checkCodeConflict(String code);

    void editService(ServiceDTO serviceDTO, StaffDTO staffDTO);

    List<Object[]> getLogOfServiceByServiceId(Long idService);

    List<LogHistoryServiceDTO> convertToLogHistory(Long idService);

    BaseUploadEntity upLoadServices(MultipartFile file, Class pclsClazz,  StaffDTO staffDTO) throws Exception;

    Optional<Service> getServiceById(Long idService);

    List<Service> findServiceByOrder(Long serviceOrder);

    List<Service> findAllByStatusNotLike(String status);

    List<Service> checkServiceParenID(Long serviceID);

    ShopUnitExcel checkDate(ShopUnitExcel shopUnitExcel) throws ParseException;

    Long getParentIdById(Long id) throws Exception;

}
