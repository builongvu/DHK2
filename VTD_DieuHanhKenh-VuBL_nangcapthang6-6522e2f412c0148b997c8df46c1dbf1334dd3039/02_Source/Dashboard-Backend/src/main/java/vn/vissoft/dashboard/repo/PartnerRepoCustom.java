package vn.vissoft.dashboard.repo;

import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.model.ManageInfoPartner;

import java.util.List;

public interface PartnerRepoCustom {

    String findShopCodeVDS();

    String findShopCodeByChannelCode(String pstrChannelCode) throws Exception;

    List<String> findShopByChannel(String pstrChannelCode) throws Exception;

    boolean checkExistedUnitInChannel(String pstrShopCode, String pstrChannelCode) throws Exception;

    boolean checkShopByShopCode(String shopCode) throws Exception;

    List<ManageInfoPartner> findByParentShopCode(String pstrParentCode) throws Exception;

    List<ManageInfoPartner> findManageInfoPartnerLevel(StaffDTO staffDTO) throws Exception;

    List<ManageInfoPartner> findPartnerDashboard(Long plngGroupId, StaffDTO staffDTO) throws Exception;

    boolean checkExistedUnitCode(String pstrCode) throws Exception;

    List<ManageInfoPartner> findActiveUnit();

    String getNameByShopAndChannel(String pastrShopCode);

    List<String> getShopCodeLevel(List<ManageInfoPartner> plstPartners) throws Exception;

    List<String> getChannelCodeLevel(List<ManageInfoPartner> plstPartners) throws Exception;

    List<String> getShopCodeVDS(List<String> plstChannelCode) throws Exception;

    String getChannelByShopCode(String pstrShopCode, String pstrParentShopCode) throws Exception;

    List<Object[]> getPartner();

    List<ManageInfoPartner> findPartnerReport(StaffDTO staffDTO) throws Exception;

    List<Object[]> findByStatusAndDate() throws Exception;

    List<Object[]> findByCodeOrParentCode(String pstrShopCode, int pintChild) throws Exception;

    List<Object[]> findByCondition(String pstrKeySearch) throws Exception;

    Boolean checkDuplicate(String pstrShopCode) throws Exception;

    Boolean checkParentShopCode(String pstrShopCode, String pstrParentShopCode) throws Exception;

    List<Object[]> findByCondionAll(ManageInfoPartner manageInfoPartner) throws Exception;

    String getVdsChannelByShopCode(String shopCode) throws Exception;

    List<String> getPartnerOfProvinceChannel() throws Exception;

}
