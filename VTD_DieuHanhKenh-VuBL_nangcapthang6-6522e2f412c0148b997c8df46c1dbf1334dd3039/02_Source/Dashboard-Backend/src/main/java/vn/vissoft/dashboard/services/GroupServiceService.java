package vn.vissoft.dashboard.services;

import org.springframework.web.multipart.MultipartFile;
import vn.vissoft.dashboard.dto.GroupServiceDTO;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.excel.BaseUploadEntity;
import vn.vissoft.dashboard.dto.excel.BaseUploadGroupEntity;
import vn.vissoft.dashboard.model.GroupService;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.sql.Date;
import java.util.List;

public interface GroupServiceService {

    List<GroupServiceDTO> getGroupServicesByCondition(GroupServiceDTO groupServiceDTO) throws Exception;

    List<GroupService> getGroupServicesByProductIdAndStatus(Long plngProductId, String pstrStatus);

    public List<GroupService> getAllGroupServices();

    public List<GroupService> getActiveGroupService();

    public GroupService addNewGroupService(GroupService groupService, StaffDTO staffDTO, Date date) throws Exception;

    public List<GroupServiceDTO> findAllreturnProductName();

    public List<GroupServiceDTO> findGroupServiceByCondition(GroupServiceDTO groupServiceDTO);

    void update(GroupService groupServiceDTO, StaffDTO staffDTO) throws Exception;

    public List<String> getColumnList(GroupServiceDTO groupService);

    GroupService saveAndFlush(GroupService groupService);

    public List<String> getValue(GroupServiceDTO groupService);

    //ExcelTempalte
    ByteArrayInputStream getTemplate() throws Exception;

    //UploadExcel
    BaseUploadEntity upload(MultipartFile file, StaffDTO user) throws Exception;

    GroupService getById(Long Id);

    List<GroupService> findAllByStatusNotLike(String status);

    String unLockGroupService(String[] arrId, StaffDTO user) throws Exception;

    List<GroupServiceDTO> getByCondition(GroupService groupServiceDTO, int page, int size) throws Exception;

    BigInteger countByCondition(GroupService groupServiceDTO, int page, int size) throws Exception;

    void saveActionDetail(GroupService oldgroup, GroupService newgroup, Long actionId) throws Exception;

    void saveActionDetail(Long actionID, String oldValue, String newValue, String column);

    String lockGroupService(String[] arrId, StaffDTO user) throws Exception;

    void saveNewActionDetail(Object newObject, Long plngActionId, StaffDTO staffDTO, Long plngProductId, Date date) throws Exception;

    boolean validateGroupService(GroupService groupService);
}
