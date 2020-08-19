package vn.vissoft.dashboard.services;

import org.springframework.web.multipart.MultipartFile;
import vn.vissoft.dashboard.dto.ServiceScoreDTO;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.excel.BaseUploadEntity;
import vn.vissoft.dashboard.model.GroupService;
import vn.vissoft.dashboard.model.ManageInfoPartner;
import vn.vissoft.dashboard.model.ServiceScore;

import java.util.List;

public interface ServiceScoreService {

    List<ServiceScoreDTO> getServiceTable(ServiceScoreDTO serviceScoreDTO) throws Exception;

    String addServiceScore(ServiceScoreDTO serviceScoreDTO, StaffDTO staffDTO) throws Exception;

    List<ServiceScoreDTO> getStaffByCondition(String shopCode, String pstrVdsChannelCode) throws Exception;

    List<ManageInfoPartner> getUnitChild(String type) throws Exception;

    String updateServiceScore(ServiceScoreDTO serviceScoreDTO, Long id, StaffDTO staffDTO) throws Exception;

    BaseUploadEntity upload(MultipartFile file, StaffDTO staffDTO) throws Exception;

    boolean validateGroupService(ServiceScoreDTO serviceScoreDTO);

}
