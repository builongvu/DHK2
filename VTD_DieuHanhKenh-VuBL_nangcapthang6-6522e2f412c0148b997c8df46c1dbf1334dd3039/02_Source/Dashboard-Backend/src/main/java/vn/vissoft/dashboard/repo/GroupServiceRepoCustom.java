package vn.vissoft.dashboard.repo;

import vn.vissoft.dashboard.dto.GroupServiceDTO;
import vn.vissoft.dashboard.dto.excel.GroupServiceExcel;
import vn.vissoft.dashboard.model.GroupService;

import java.math.BigInteger;
import java.util.List;

public interface GroupServiceRepoCustom {
    List<GroupServiceDTO> getByCondition(GroupService groupServiceDTO,int page, int size) throws Exception;
    BigInteger countByCondition(GroupService groupServiceDTO, int page, int size) throws Exception;

    List<GroupServiceDTO> findAllReturnProductName();

    List<GroupServiceDTO> findGroupServiceByCondition(GroupServiceDTO groupServiceDTO);

    List<GroupService> findGroupServicesByCondition(GroupServiceDTO groupServiceDTO) throws Exception;

    List<GroupService> findActiveGroupService();

    List<String> findGrCodeByProductCode(String pstrProductCode);

    boolean checkExistedGroupServiceCode(String pstrCode);

    List<String> findActiveGroupServiceCodeByProductCode(String pstrProductCode);

    boolean checkExistedGroupServiceInProduct(String pstrProductCode, String pstrGroupServiceCode);

    void persist(GroupService groupService) throws Exception;

    void update(GroupService groupService) throws Exception;

    Long findIdByCode(String pstrCode) throws Exception;

    List<GroupService> findActiveGroupCode(String pstrCode) throws Exception;
}
