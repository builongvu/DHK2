package vn.vissoft.dashboard.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.vissoft.dashboard.dto.ManageInfoPartnerDTO;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.model.ActionAudit;
import vn.vissoft.dashboard.model.ManageInfoPartner;
import vn.vissoft.dashboard.repo.ActionAuditRepo;
import vn.vissoft.dashboard.repo.PartnerRepo;
import vn.vissoft.dashboard.services.ActionDetailService;
import vn.vissoft.dashboard.services.PartnerService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
public class PartnerServiceImpl implements PartnerService {

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    private PartnerRepo partnerRepo;

    @Autowired
    private ActionDetailService actionDetailService;

    @Autowired
    private ActionAuditRepo actionAuditRepo;

    @Override
    public List<ManageInfoPartner> getAllUnits() {
        return partnerRepo.findAll();
    }

    @Override
    public List<ManageInfoPartner> getByParentShopCode(String parentShopCode) {
        return partnerRepo.findByParentShopCode(parentShopCode);
    }

    @Override
    public List<ManageInfoPartner> getActiveUnit() {
        return partnerRepo.findActiveUnit();
    }

    @Override
    public List<ManageInfoPartner> getManageInfoPartnerLevel(StaffDTO staffDTO) throws Exception {
        return partnerRepo.findManageInfoPartnerLevel(staffDTO);
    }

    @Override
    public List<ManageInfoPartner> getPartnerDashboard(Long plngGroupId, StaffDTO staffDTO) throws Exception {
        return partnerRepo.findPartnerDashboard(plngGroupId, staffDTO);
    }

    public String getNameByShopAndChannel(String pastrShopCode) {
        return partnerRepo.getNameByShopAndChannel(pastrShopCode);
    }

    @Override
    public List<ManageInfoPartner> findPartnerReport(StaffDTO staffDTO) throws Exception {
        return partnerRepo.findPartnerReport(staffDTO);
    }

    /**
     * menu don vi ben trai (chuc nang khai bao cay dieu hanh VDS)
     *
     * @author VuBL
     * @since 2019/12
     * @return
     * @throws Exception
     */
    @Override
    public List<ManageInfoPartner> getByStatusAndDate() throws Exception {
        List<Object[]> vlstObject = partnerRepo.findByStatusAndDate();
        List<ManageInfoPartner> vlstData = new ArrayList<>();

        if (!DataUtil.isNullOrEmpty(vlstObject)) {
            for (Object[] obj : vlstObject) {
                ManageInfoPartner data = new ManageInfoPartner();
                data.setId(DataUtil.safeToLong(obj[0]));
                data.setVdsChannelCode((String) obj[1]);
                data.setShopCode((String) obj[2]);
                data.setShopName((String) obj[3]);
                data.setShortName((String) obj[4]);
                data.setParentShopCode((String) obj[5]);
                data.setAssignKpi((String) obj[6]);
                data.setStatus((String) obj[7]);
                data.setUser((String) obj[8]);
                data.setCreateDate((java.util.Date) obj[9]);
                data.setFromDate((java.util.Date) obj[10]);
                data.setToDate((java.util.Date) obj[11]);
                vlstData.add(data);
            }
        }

        if (DataUtil.isNullOrEmpty(vlstData)) return null;
        return vlstData;
    }

    /**
     * lay ra don vi theo shop_code hoac parent_shop_code
     *
     * @param pstrShopCode
     * @return
     * @throws Exception
     * @author VuBL
     * @since 2019/12
     */
    @Override
    public List<ManageInfoPartnerDTO> getByCodeOrParentCode(String pstrShopCode, int pintChild) throws Exception {
        List<Object[]> vlstObject = partnerRepo.findByCodeOrParentCode(pstrShopCode, pintChild);
        List<ManageInfoPartnerDTO> vlstData = new ArrayList<>();

        if (!DataUtil.isNullOrEmpty(vlstObject)) {
            for (Object[] obj : vlstObject) {
                ManageInfoPartnerDTO data = new ManageInfoPartnerDTO();
                data.setId(DataUtil.safeToLong(obj[0]));
                data.setVdsChannelCode((String) obj[1]);
                data.setShopCode((String) obj[2]);
                data.setShopName((String) obj[3]);
                data.setShortName((String) obj[4]);
                data.setParentShopCode((String) obj[5]);
                data.setAssignKpi((String) obj[6]);
                data.setStatus((String) obj[7]);
                data.setUser((String) obj[8]);
                data.setCreateDate((java.util.Date) obj[9]);
                data.setFromDate((java.util.Date) obj[10]);
                data.setToDate((java.util.Date) obj[11]);
                data.setParentShopName((String) obj[12]);
                vlstData.add(data);
            }
        }

        if (DataUtil.isNullOrEmpty(vlstData)) return null;
        return vlstData;
    }

    /**
     * tim kiem nhanh tren cay don vi (chuc nang khai bao cay dieu hanh VDS)
     *
     * @param pstrKeySearch
     * @return
     * @throws Exception
     * @author VuBL
     * @since 2019/12
     */
    @Override
    public List<ManageInfoPartner> getByCondition(String pstrKeySearch) throws Exception {
        List<Object[]> vlstObject = partnerRepo.findByCondition(pstrKeySearch);
        List<ManageInfoPartner> vlstData = new ArrayList<>();

        if (!DataUtil.isNullOrEmpty(vlstObject)) {
            for (Object[] obj : vlstObject) {
                ManageInfoPartner data = new ManageInfoPartner();
                data.setId(DataUtil.safeToLong(obj[0]));
                data.setVdsChannelCode((String) obj[1]);
                data.setShopCode((String) obj[2]);
                data.setShopName((String) obj[3]);
                data.setShortName((String) obj[4]);
                data.setParentShopCode((String) obj[5]);
                data.setAssignKpi((String) obj[6]);
                data.setStatus((String) obj[7]);
                data.setUser((String) obj[8]);
                data.setCreateDate((java.util.Date) obj[9]);
                data.setFromDate((java.util.Date) obj[10]);
                data.setToDate((java.util.Date) obj[11]);
                vlstData.add(data);
            }
        }

        if (DataUtil.isNullOrEmpty(vlstData)) return null;
        return vlstData;
    }

    /**
     * them moi don vi (chuc nang khai bao cay dieu hanh VDS)
     *
     * @param manageInfoPartner
     * @return
     * @throws Exception
     * @author VuBL
     * @since 2019/12
     */
    @Override
    public String addPartner(ManageInfoPartner manageInfoPartner, StaffDTO staffDTO) throws Exception {
        String vstrShopCode = manageInfoPartner.getShopCode();

        if (partnerRepo.checkDuplicate(vstrShopCode)) {
            String vstrUser = staffDTO.getStaffCode();
            long vlngDateTime = System.currentTimeMillis();
            Date vdtDateTime = new Date(vlngDateTime);
            Date vdtFromDateCheck;
            manageInfoPartner.setShopCode(manageInfoPartner.getShopCode().trim().toUpperCase());
            manageInfoPartner.setShopName(manageInfoPartner.getShopName().trim());
            manageInfoPartner.setShortName(manageInfoPartner.getShortName().trim());
            manageInfoPartner.setUser(vstrUser);
            manageInfoPartner.setCreateDate(vdtDateTime);
            if (manageInfoPartner.getFromDate() != null) {
                vdtFromDateCheck = new Date(manageInfoPartner.getFromDate().getTime());
                if (vdtFromDateCheck.toLocalDate().compareTo(vdtDateTime.toLocalDate()) > 0) {
                    manageInfoPartner.setStatus(Constants.MANAGE_INFO_PARTNER.OFFLINE);
                }
            }
            manageInfoPartner.setAssignKpi(Constants.PARAM_STATUS);

            //them moi
            entityManager.persist(manageInfoPartner);

            //luu action audit
            ActionAudit actionAudit = new ActionAudit();
            actionAudit.setActionCode(Constants.CREATE);
            actionAudit.setActionDateTime(vdtDateTime);
            actionAudit.setPkID(manageInfoPartner.getId());
            actionAudit.setObjectCode(Constants.MANAGER_INFO_PARTNER);
            actionAudit.setUser(staffDTO.getStaffCode());
            actionAudit.setShopCode(staffDTO.getShopCode());
            actionAuditRepo.save(actionAudit);

            //luu action detail
            if (manageInfoPartner.getVdsChannelCode() != null) {
                actionDetailService.createActionDetail(Constants.MANAGE_INFO_PARTNER.VDS_CHANNEL_CODE, manageInfoPartner.getVdsChannelCode().trim(), actionAudit.getId(), null);
            }
            actionDetailService.createActionDetail(Constants.MANAGE_INFO_PARTNER.SHOP_CODE, manageInfoPartner.getShopCode().trim(), actionAudit.getId(), null);
            actionDetailService.createActionDetail(Constants.MANAGE_INFO_PARTNER.SHOP_NAME, manageInfoPartner.getShopName().trim(), actionAudit.getId(), null);
            actionDetailService.createActionDetail(Constants.MANAGE_INFO_PARTNER.SHORT_NAME, manageInfoPartner.getShortName().trim(), actionAudit.getId(), null);
            if (manageInfoPartner.getParentShopCode() != null) {
                actionDetailService.createActionDetail(Constants.MANAGE_INFO_PARTNER.PARENT_SHOP_CODE, manageInfoPartner.getParentShopCode().trim(), actionAudit.getId(), null);
            }
            actionDetailService.createActionDetail(Constants.MANAGE_INFO_PARTNER.STATUS, manageInfoPartner.getStatus().trim(), actionAudit.getId(), null);
            if (manageInfoPartner.getFromDate() != null) {
                Date vdtFromDate = new Date(manageInfoPartner.getFromDate().getTime());
                actionDetailService.createActionDetail(Constants.MANAGE_INFO_PARTNER.FROM_DATE, vdtFromDate.toString().trim(), actionAudit.getId(), null);
            }
            if (manageInfoPartner.getToDate() != null) {
                Date vdtToDateNew = new Date(manageInfoPartner.getToDate().getTime());
                actionDetailService.createActionDetail(Constants.MANAGE_INFO_PARTNER.TO_DATE, vdtToDateNew.toString().trim(), actionAudit.getId(), null);
            }

            return null;
        } else {
            return Constants.MANAGE_INFO_PARTNER.DUPLICATE_SHOP_CODE;
        }
    }

    /**
     * cap nhat don vi (chuc nang khai bao cay dieu hanh VDS)
     *
     * @param manageInfoPartner
     * @param staffDTO
     * @return
     * @throws Exception
     * @author VuBL
     * @since 2019/12
     */
    @Override
    public String updatePartner(ManageInfoPartner manageInfoPartner, StaffDTO staffDTO) throws Exception {
        ManageInfoPartner oldValue = partnerRepo.getOne(manageInfoPartner.getId());
        String vstrUser = staffDTO.getStaffCode();
        long vlngDateTime = System.currentTimeMillis();
        Date vdtDateTime = new Date(vlngDateTime);
        Date vdtFromDateCheck;

        if (partnerRepo.checkParentShopCode(manageInfoPartner.getShopCode(), manageInfoPartner.getParentShopCode()) == false) {
            return Constants.MANAGE_INFO_PARTNER.INVALID_PARENT_SHOP;
        } else if (manageInfoPartner.getShopCode() != null &&
                manageInfoPartner.getParentShopCode() != null &&
        manageInfoPartner.getShopCode().trim().equals(manageInfoPartner.getParentShopCode().trim())) {
            return Constants.MANAGE_INFO_PARTNER.INVALID_PARENT_SHOP;
        } else {
            manageInfoPartner.setShopName(manageInfoPartner.getShopName().trim());
            manageInfoPartner.setShortName(manageInfoPartner.getShortName().trim());
            manageInfoPartner.setUser(vstrUser);
            manageInfoPartner.setCreateDate(vdtDateTime);
            manageInfoPartner.setId(oldValue.getId());
            manageInfoPartner.setVdsChannelCode(oldValue.getVdsChannelCode());
            manageInfoPartner.setAssignKpi(oldValue.getAssignKpi());
            manageInfoPartner.setShopCode(oldValue.getShopCode());
            if (manageInfoPartner.getFromDate() != null) {
                vdtFromDateCheck = new Date(manageInfoPartner.getFromDate().getTime());
                if (vdtFromDateCheck.toLocalDate().compareTo(vdtDateTime.toLocalDate()) > 0) {
                    manageInfoPartner.setStatus(Constants.MANAGE_INFO_PARTNER.OFFLINE);
                }
            }

            //luu action audit
            ActionAudit actionAudit = new ActionAudit();
            actionAudit.setActionCode(Constants.EDIT);
            actionAudit.setActionDateTime(vdtDateTime);
            actionAudit.setPkID(manageInfoPartner.getId());
            actionAudit.setObjectCode(Constants.MANAGER_INFO_PARTNER);
            actionAudit.setUser(staffDTO.getStaffCode());
            actionAudit.setShopCode(staffDTO.getShopCode());
            actionAuditRepo.save(actionAudit);

            //luu action detail
            if (!manageInfoPartner.getShopName().trim().equals(oldValue.getShopName().trim())) {
                actionDetailService.createActionDetail(Constants.MANAGE_INFO_PARTNER.SHOP_NAME, manageInfoPartner.getShopName().trim(), actionAudit.getId(), oldValue.getShopName().trim());
            }
            if (!manageInfoPartner.getShortName().trim().equals(oldValue.getShortName().trim())) {
                actionDetailService.createActionDetail(Constants.MANAGE_INFO_PARTNER.SHORT_NAME, manageInfoPartner.getShortName().trim(), actionAudit.getId(), oldValue.getShortName().trim());
            }
            if (!manageInfoPartner.getStatus().trim().equals(oldValue.getStatus().trim())) {
                actionDetailService.createActionDetail(Constants.MANAGE_INFO_PARTNER.STATUS, manageInfoPartner.getStatus().trim(), actionAudit.getId(), oldValue.getStatus().trim());
            }
            if (manageInfoPartner.getParentShopCode() != null && oldValue.getParentShopCode() == null) {
                actionDetailService.createActionDetail(Constants.MANAGE_INFO_PARTNER.PARENT_SHOP_CODE, manageInfoPartner.getParentShopCode().trim(), actionAudit.getId(), null);
            } else if (manageInfoPartner.getParentShopCode() == null && oldValue.getParentShopCode() != null) {
                actionDetailService.createActionDetail(Constants.MANAGE_INFO_PARTNER.PARENT_SHOP_CODE, null, actionAudit.getId(), oldValue.getParentShopCode().trim());
            } else if (manageInfoPartner.getParentShopCode() != null && oldValue.getParentShopCode() != null) {
                if (!manageInfoPartner.getParentShopCode().trim().equals(oldValue.getParentShopCode().trim())) {
                    actionDetailService.createActionDetail(Constants.MANAGE_INFO_PARTNER.PARENT_SHOP_CODE, manageInfoPartner.getParentShopCode().trim(), actionAudit.getId(), oldValue.getParentShopCode().trim());
                }
            }
            if (manageInfoPartner.getFromDate() != null && oldValue.getFromDate() != null) {
                Date vdtFromDate = new Date(manageInfoPartner.getFromDate().getTime());
                Date vdtFromDateOld = new Date(oldValue.getFromDate().getTime());
                if (vdtFromDate.toLocalDate().compareTo(vdtFromDateOld.toLocalDate()) != 0) {
                    actionDetailService.createActionDetail(Constants.MANAGE_INFO_PARTNER.FROM_DATE, vdtFromDate.toString().trim(), actionAudit.getId(), vdtFromDateOld.toString().trim());
                }
            }

            //update
            entityManager.merge(manageInfoPartner);
        }

        return null;
    }

    /**
     * cau hinh hoat dong/ngung hoat dong (chuc nang khai bao cay dieu hanh VDS)
     *
     * @author VuBL
     * @since 2019/12
     * @param manageInfoPartner
     * @param staffDTO
     * @throws Exception
     */
    @Override
    public void updateStatus(ManageInfoPartner manageInfoPartner, StaffDTO staffDTO) throws Exception {
        ManageInfoPartner oldValue = partnerRepo.getOne(manageInfoPartner.getId());
        String vstrOldStatus = manageInfoPartner.getStatus().trim();
        long vlngCurrentDate = System.currentTimeMillis();
        java.util.Date vdtCurrentDate = new Date(vlngCurrentDate);
        java.util.Date vdtToDate = manageInfoPartner.getToDate();

        manageInfoPartner.setId(oldValue.getId());
        manageInfoPartner.setShopCode(oldValue.getShopCode());
        manageInfoPartner.setAssignKpi(oldValue.getAssignKpi());
        manageInfoPartner.setCreateDate(oldValue.getCreateDate());
        manageInfoPartner.setFromDate(oldValue.getFromDate());
        manageInfoPartner.setUser(oldValue.getUser());
        manageInfoPartner.setParentShopCode(oldValue.getParentShopCode());
        manageInfoPartner.setShortName(oldValue.getShortName());
        manageInfoPartner.setShopName(oldValue.getShopName());

        //truong hop don vi dang ngung hoat dong => hoat dong
        if (vstrOldStatus.equals(Constants.MANAGE_INFO_PARTNER.OFFLINE)) {
            if (vdtToDate != null) {
                if (new Date(vdtToDate.getTime()).toLocalDate().compareTo(new Date(vdtCurrentDate.getTime()).toLocalDate()) <= 0) {
                    manageInfoPartner.setToDate(null);
                }
            }
            manageInfoPartner.setStatus(Constants.MANAGE_INFO_PARTNER.ONLINE);

            //luu action audit
            ActionAudit actionAudit = new ActionAudit();
            actionAudit.setActionCode(Constants.EDIT);
            actionAudit.setActionDateTime(vdtCurrentDate);
            actionAudit.setPkID(manageInfoPartner.getId());
            actionAudit.setObjectCode(Constants.MANAGER_INFO_PARTNER);
            actionAudit.setUser(staffDTO.getStaffCode());
            actionAudit.setShopCode(staffDTO.getShopCode());
            actionAuditRepo.save(actionAudit);

            //luu action detail
            actionDetailService.createActionDetail(Constants.MANAGE_INFO_PARTNER.STATUS, manageInfoPartner.getStatus().trim(), actionAudit.getId(), oldValue.getStatus().trim());

            entityManager.merge(manageInfoPartner);
        }

        //truong hop don vi dang hoat dong => ngung hoat dong
        if (vstrOldStatus.equals(Constants.MANAGE_INFO_PARTNER.ONLINE)) {
            List<ManageInfoPartner> vlstChildShop = partnerRepo.findByParentShopCode(manageInfoPartner.getShopCode());
            List<ManageInfoPartner> vlstData = new ArrayList<>();
            vlstData.add(manageInfoPartner);
            vlstData.addAll(vlstChildShop);

            for (ManageInfoPartner man : vlstData) {
                String vstrOldStatusOnline = man.getStatus().trim();
                if (man.getStatus().equals(Constants.MANAGE_INFO_PARTNER.ONLINE)) {
                    if (man.getToDate() == null) {
                        man.setToDate(vdtCurrentDate);
                    }
                    man.setStatus(Constants.MANAGE_INFO_PARTNER.OFFLINE);

                    //luu action audit
                    ActionAudit actionAudit = new ActionAudit();
                    actionAudit.setActionCode(Constants.EDIT);
                    actionAudit.setActionDateTime(vdtCurrentDate);
                    actionAudit.setPkID(manageInfoPartner.getId());
                    actionAudit.setObjectCode(Constants.MANAGER_INFO_PARTNER);
                    actionAudit.setUser(staffDTO.getStaffCode());
                    actionAudit.setShopCode(staffDTO.getShopCode());
                    actionAuditRepo.save(actionAudit);

                    //luu action detail
                    actionDetailService.createActionDetail(Constants.MANAGE_INFO_PARTNER.STATUS, manageInfoPartner.getStatus().trim(), actionAudit.getId(), vstrOldStatusOnline);

                    entityManager.merge(man);
                }
            }
        }

    }

    /**
     * tim kiem don vi (chuc nang khai bao cay dieu hanh VDS)
     *
     * @author VuBL
     * @since 2019/12
     * @param manageInfoPartner
     * @return
     * @throws Exception
     */
    @Override
    public List<ManageInfoPartnerDTO> getByConditionAll(ManageInfoPartner manageInfoPartner) throws Exception {
        List<Object[]> vlstObject = partnerRepo.findByCondionAll(manageInfoPartner);
        List<ManageInfoPartnerDTO> vlstData = new ArrayList<>();

        if(!DataUtil.isNullOrEmpty(vlstObject)) {
            for (Object[] obj : vlstObject) {
                ManageInfoPartnerDTO data = new ManageInfoPartnerDTO();
                data.setId(DataUtil.safeToLong(obj[0]));
                data.setVdsChannelCode((String) obj[1]);
                data.setShopCode((String) obj[2]);
                data.setShopName((String) obj[3]);
                data.setShortName((String) obj[4]);
                data.setParentShopCode((String) obj[5]);
                data.setAssignKpi((String) obj[6]);
                data.setStatus((String) obj[7]);
                data.setUser((String) obj[8]);
                data.setCreateDate((java.util.Date) obj[9]);
                data.setFromDate((java.util.Date) obj[10]);
                data.setToDate((java.util.Date) obj[11]);
                data.setParentShopName((String) obj[12]);
                vlstData.add(data);
            }
        }

        if (DataUtil.isNullOrEmpty(vlstData)) return null;
        return vlstData;
    }

}
