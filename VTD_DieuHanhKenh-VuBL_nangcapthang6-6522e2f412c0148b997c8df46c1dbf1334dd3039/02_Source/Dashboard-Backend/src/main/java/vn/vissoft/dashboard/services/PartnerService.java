package vn.vissoft.dashboard.services;

import vn.vissoft.dashboard.dto.ManageInfoPartnerDTO;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.UnitDTO;
import vn.vissoft.dashboard.model.ManageInfoPartner;

import java.util.List;

public interface PartnerService {

    List<ManageInfoPartner> getAllUnits();

    List<ManageInfoPartner> getByParentShopCode(String parentShopCode);

    List<ManageInfoPartner> getActiveUnit();

    List<ManageInfoPartner> getManageInfoPartnerLevel(StaffDTO staffDTO) throws Exception;

    List<ManageInfoPartner> getPartnerDashboard(Long plngGroupId,StaffDTO staffDTO) throws Exception;

    String getNameByShopAndChannel(String pastrShopCode);

    List<ManageInfoPartner> findPartnerReport(StaffDTO staffDTO) throws Exception;

    List<ManageInfoPartner> getByStatusAndDate() throws Exception;

    List<ManageInfoPartnerDTO> getByCodeOrParentCode(String pstrShopCode, int pintChild) throws Exception;

    List<ManageInfoPartner> getByCondition(String pstrKeySearch) throws Exception;

    String addPartner(ManageInfoPartner manageInfoPartner, StaffDTO staffDTO) throws Exception;

    String updatePartner(ManageInfoPartner manageInfoPartner, StaffDTO staffDTO) throws Exception;

    void updateStatus(ManageInfoPartner manageInfoPartner, StaffDTO staffDTO) throws Exception;

    List<ManageInfoPartnerDTO> getByConditionAll(ManageInfoPartner manageInfoPartner) throws Exception;

}
