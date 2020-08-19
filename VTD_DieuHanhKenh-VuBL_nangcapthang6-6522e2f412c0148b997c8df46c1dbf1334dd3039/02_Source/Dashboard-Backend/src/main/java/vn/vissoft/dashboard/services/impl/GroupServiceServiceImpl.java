package vn.vissoft.dashboard.services.impl;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.vissoft.dashboard.config.I18N;
import vn.vissoft.dashboard.dto.GroupServiceDTO;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.excel.BaseUploadEntity;
import vn.vissoft.dashboard.dto.excel.GroupServiceExcel;
import vn.vissoft.dashboard.dto.excel.ServiceScoreExcel;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.helper.excelreader.ExcelReader;
import vn.vissoft.dashboard.helper.excelreader.GroupServiceTemplate;
import vn.vissoft.dashboard.mapper.BaseMapper;
import vn.vissoft.dashboard.model.*;
import vn.vissoft.dashboard.repo.ActionDetailRepo;
import vn.vissoft.dashboard.repo.GroupServiceRepo;
import vn.vissoft.dashboard.repo.ProductRepo;
import vn.vissoft.dashboard.services.ActionAuditService;
import vn.vissoft.dashboard.services.ActionDetailService;
import vn.vissoft.dashboard.services.GroupServiceService;
import vn.vissoft.dashboard.services.PlanMonthlyService;

import javax.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Transactional
@Service
public class GroupServiceServiceImpl implements GroupServiceService {

    private BaseMapper<GroupService, GroupServiceDTO> mapper = new BaseMapper<GroupService, GroupServiceDTO>(GroupService.class, GroupServiceDTO.class);


    @Value("${server.tomcat.basedir}")
    private String mstrUploadPath;

    @Autowired
    private GroupServiceRepo groupServiceRepo;

    public static final Logger LOGGER = LogManager.getLogger(GroupServiceServiceImpl.class);

    @Autowired
    private ActionAuditService actionAuditService;

    @Autowired
    private ActionDetailService actionDetailService;

    @Autowired
    private ActionDetailRepo actionDetailRepo;

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private PlanMonthlyService planMonthlyService;
    private GroupServiceTemplate groupServiceTemplate = new GroupServiceTemplate();

    @Override
    public List<GroupServiceDTO> getGroupServicesByCondition(GroupServiceDTO groupServiceDTO) throws Exception {
        return mapper.toDtoBean(groupServiceRepo.findGroupServicesByCondition(groupServiceDTO));
    }

    @Override
    public List<GroupService> getGroupServicesByProductIdAndStatus(Long plngProductId, String pstrStatus) {
        return groupServiceRepo.findByProductIdAndStatus(plngProductId, pstrStatus);
    }

    @Override
    public List<GroupService> getAllGroupServices() {
        return groupServiceRepo.findAll();
    }

    @Override
    public List<GroupService> getActiveGroupService() {
        return groupServiceRepo.findActiveGroupService();
    }

    @Override
    public GroupService addNewGroupService(GroupService groupService, StaffDTO staffDTO, Date date) throws Exception {

        GroupService groupService1 = groupServiceRepo.save(groupService);
        ActionAudit actionAudit = actionAuditService.log(Constants.GROUP_SERVICE, Constants.CREATE, staffDTO.getStaffCode(), groupService1.getId(), staffDTO.getShopCode());
        saveNewActionDetail(groupService, actionAudit.getId(), staffDTO, groupService.getProductId(), date);
        return groupService1;
    }
//    @Override
//    public List<GroupServiceDTO> findbyProductIdAndcode(String code, Long productId) {
//        return mapper.toDtoBean(groupServiceRepo.findByCodeAndAndProductId(code, productId));
//    }

    @Override
    public List<GroupServiceDTO> findAllreturnProductName() {
        List<GroupServiceDTO> list = groupServiceRepo.findAllReturnProductName();
        return list;
    }

    @Override
    public List<GroupServiceDTO> findGroupServiceByCondition(GroupServiceDTO groupServiceDTO) {
        return groupServiceRepo.findGroupServiceByCondition(groupServiceDTO);
    }

    @Override
    public void update(GroupService groupServiceDTO, StaffDTO staffDTO) throws Exception {

        GroupService oldValue = groupServiceRepo.getOne(groupServiceDTO.getId());
        ActionAudit actionAudit = actionAuditService.log(Constants.GROUP_SERVICE, Constants.EDIT, staffDTO.getStaffCode(), groupServiceDTO.getId(), staffDTO.getShopCode());
        //saveActionDetail(groupServiceDTO, oldValue, actionAudit.getId());
        if (!groupServiceDTO.getCode().trim().equalsIgnoreCase(oldValue.getCode().trim()))
            actionDetailService.createActionDetail(Constants.GROUPSERVICES.CODE, groupServiceDTO.getCode().trim(), actionAudit.getId(), oldValue.getCode());
        if (!groupServiceDTO.getName().trim().equalsIgnoreCase(oldValue.getName().trim()))
            actionDetailService.createActionDetail(Constants.GROUPSERVICES.NAME, groupServiceDTO.getName().trim(), actionAudit.getId(), oldValue.getName());
        if (!groupServiceDTO.getProductId().equals(oldValue.getProductId()))
            actionDetailService.createActionDetail(Constants.GROUPSERVICES.PRODUCT_ID, String.valueOf(groupServiceDTO.getProductId()), actionAudit.getId(), String.valueOf(oldValue.getProductId()));
        if (!groupServiceDTO.getStatus().equals(oldValue.getStatus()))
            actionDetailService.createActionDetail(Constants.GROUPSERVICES.STATUS, groupServiceDTO.getStatus(), actionAudit.getId(), oldValue.getStatus());

        groupServiceDTO.setCode(groupServiceDTO.getCode().trim());
        groupServiceDTO.setUserupdate(staffDTO.getStaffCode());
        groupServiceDTO.setName(groupServiceDTO.getName().trim());
        long millis = System.currentTimeMillis();
        Timestamp date = new Timestamp(millis);
        groupServiceDTO.setCreatedate(date);
        groupServiceRepo.save(groupServiceDTO);

    }

    @Override
    public List<String> getColumnList(GroupServiceDTO groupService) {
        List<String> list = new ArrayList<>();
        list.add("Change_date_time");
        if (groupService.getCode() != null) list.add("code");
        if (groupService.getName() != null) list.add("name");
        if (groupService.getProductId() != null) list.add("product_id");
        if (groupService.getStatus() != null) list.add("status");
        list.add("user_update");
        return list;
    }

    @Override
    public GroupService saveAndFlush(GroupService groupService) {
        return groupServiceRepo.saveAndFlush(groupService);
    }

    @Override
    public List<String> getValue(GroupServiceDTO groupService) {
        List<String> list = new ArrayList<>();
        if (groupService.getChangeDatetime() != null) list.add(groupService.getChangeDatetime().toString());
        if (groupService.getCode() != null) list.add(groupService.getCode());
        if (groupService.getName() != null) list.add(groupService.getName());
        if (groupService.getProductId() != null) list.add(groupService.getProductId().toString());
        if (groupService.getStatus() != null) list.add(groupService.getStatus());
        list.add(groupService.getUserUpdate());
        return list;
    }

    @Override
    public ByteArrayInputStream getTemplate() throws Exception {
        return groupServiceTemplate.writeTemplate();
    }

    @Override
    public BaseUploadEntity upload(MultipartFile file, StaffDTO user) throws Exception {
        int vintCheck = 0;
        int vintSumSuccessfulRecord = 0;
        int vintSumRecord = 0;
        long mil = System.currentTimeMillis();
        Date date = new Date(mil);
        Timestamp timestamp = new Timestamp(mil);

        BaseUploadEntity baseUploadEntity = new BaseUploadEntity();
        String vstrOriginalName = file.getOriginalFilename();
        String vstrPath = mstrUploadPath;

        File customDir = new File(vstrPath);

        if (!customDir.exists()) {
            customDir.mkdir();
        }

        int vintIndex = vstrOriginalName.lastIndexOf(".");
        String vstrResultFileName;
        if (vintIndex > -1) {
            vstrResultFileName = vstrOriginalName.substring(0, vstrOriginalName.lastIndexOf(".")) + "_Ketqua.xlsx";
        } else {
            vstrResultFileName = vstrOriginalName + "_Ketqua.xlsx";
        }
        vstrOriginalName = customDir.getAbsolutePath() + File.separator + vstrOriginalName;


        String vstrResultFilePath = customDir.getAbsolutePath() + File.separator + vstrResultFileName;
        planMonthlyService.saveFile(file.getInputStream(), vstrOriginalName);

        try {
            ExcelReader<GroupServiceExcel> vreader = new ExcelReader<>();
            List<GroupServiceExcel> vlstGroupServices = vreader.read(vstrOriginalName, GroupServiceExcel.class);

            if (DataUtil.isNullOrEmpty(vlstGroupServices)) {
                baseUploadEntity.setMessage(I18N.get(Constants.EMPTY_FILE_ERROR));
                return baseUploadEntity;
            } else {
                planMonthlyService.checkTheSameRecord(vlstGroupServices);

                for (GroupServiceExcel groupServiceExcel : vlstGroupServices) {
                    if (!DataUtil.isNullObject(groupServiceExcel)) {
                        vintSumRecord++;
                        if (DataUtil.isNullOrEmpty(groupServiceExcel.getError())) {
                            if (!productRepo.checkExistedProductCode(groupServiceExcel.getProductCode().trim())) {
                                groupServiceExcel.setError(I18N.get("common.excel.active.error") + ". " + I18N.get("common.excel.column.error") + " B ");
                            } else if (groupServiceExcel.getCode().trim().length() > 50) {
                                groupServiceExcel.setError(I18N.get("common.excel.active.50") + ". " + I18N.get("common.excel.column.error") + " C ");
                            } else if (groupServiceExcel.getName().trim().length() > 100) {
                                groupServiceExcel.setError(I18N.get("common.excel.active.100") + ". " + I18N.get("common.excel.column.error") + " D ");
                            }
                        }
                        if (DataUtil.isNullOrEmpty(groupServiceExcel.getError())) {
                            try {
                                Long productId = productRepo.findIdByCode(groupServiceExcel.getProductCode().trim());
                                GroupService groupService = groupServiceRepo.findByCode(groupServiceExcel.getCode());
                                if (DataUtil.isNullObject(groupService)) {
                                    GroupService groupServiceInserted = new GroupService(groupServiceExcel.getCode().toUpperCase().trim(), groupServiceExcel.getName().trim()
                                            , Constants.PARAM_STATUS, productId, timestamp, user.getStaffCode());
                                    groupServiceRepo.persist(groupServiceInserted);

                                    ActionAudit actionAudit = actionAuditService.log(Constants.GROUP_SERVICE, Constants.CREATE, user.getStaffCode(), groupServiceInserted.getId(), user.getShopCode());
                                    saveNewActionDetail(groupServiceExcel, actionAudit.getId(), user, productId, date);

                                    vintSumSuccessfulRecord++;
                                } else {
                                    List<GroupService> vlstGroups = groupServiceRepo.findActiveGroupCode(groupServiceExcel.getCode());
                                    if (!DataUtil.isNullOrEmpty(vlstGroups)) {
                                        groupServiceExcel.setError(I18N.get("common.excel.exist.group.service.code") + ". " + I18N.get("common.excel.column.error") + " C ");
                                    } else {
                                        ActionAudit actionAudit = actionAuditService.log(Constants.GROUP_SERVICE, Constants.EDIT, user.getStaffCode(), groupService.getId(), user.getShopCode());
                                        if (!groupServiceExcel.getName().trim().equalsIgnoreCase(groupService.getName().trim()))
                                            actionDetailService.createActionDetail(Constants.GROUPSERVICES.NAME, groupServiceExcel.getName().trim(), actionAudit.getId(), groupService.getName());
                                        if (!productId.equals(groupService.getProductId()))
                                            actionDetailService.createActionDetail(Constants.GROUPSERVICES.PRODUCT_ID, String.valueOf(productId), actionAudit.getId(), String.valueOf(groupService.getProductId()));
                                        actionDetailService.createActionDetail(Constants.GROUPSERVICES.STATUS,Constants.PARAM_STATUS , actionAudit.getId(), groupService.getStatus());

                                        groupService.setName(groupServiceExcel.getName().trim());
                                        groupService.setProductId(productId);
                                        groupService.setCreatedate(timestamp);
                                        groupService.setUserupdate(user.getStaffCode());
                                        groupService.setStatus(Constants.PARAM_STATUS);
                                        groupServiceRepo.update(groupService);

                                        vintSumSuccessfulRecord++;

                                    }

                                }
                            } catch (Exception e) {
                                LOGGER.error(e.getMessage(), e);
                                groupServiceExcel.setError(e.getMessage());
                            }
                        }
                    } else {
                        vintCheck++;
                        continue;
                    }
                }
                //check empty file
                if (vintCheck == vlstGroupServices.size()) {
                    baseUploadEntity.setMessage(I18N.get(Constants.EMPTY_FILE_ERROR));
                    return baseUploadEntity;
                }
            }
            vreader.writeResultFile(vstrOriginalName, vstrResultFilePath, GroupServiceExcel.class, vlstGroupServices, I18N.get("common.success.status"));

            //set cac thong tin tra ve frontend
            baseUploadEntity.setResultFileName(vstrResultFileName);
            baseUploadEntity.setSumSuccessfulRecord(vintSumSuccessfulRecord);
            baseUploadEntity.setSumRecord(vintSumRecord);
            return baseUploadEntity;
        } catch (
                Exception e) {
            baseUploadEntity.setMessage(e.getMessage());
            LOGGER.error(e.getMessage(), e);
            return baseUploadEntity;
        }
    }

    @Override
    public GroupService getById(Long Id) {
        return groupServiceRepo.getById(Id);
    }

    @Override
    public String unLockGroupService(String[] arrId, StaffDTO user) throws Exception {
//        String status = I18N.get("common.table.group.service.unlockSuccess");
        for (int i = 0; i < arrId.length; i++) {
            GroupService groupService = getById(Long.parseLong(arrId[i]));
            groupService.setStatus("1");
            groupService.setUserupdate(user.getStaffCode());
            long millis = System.currentTimeMillis();
            Timestamp date = new Timestamp(millis);
            groupService.setCreatedate(date);
            ActionAudit actionAudit = actionAuditService.log(Constants.GROUP_SERVICE, Constants.GROUPSERVICES.ACTION_CODE_EDIT, user.getStaffCode(), groupService.getId(), user.getShopCode());
            saveActionDetail(actionAudit.getId(), "0", "1", Constants.GROUPSERVICES.STATUS);
        }
        return I18N.get(" " + arrId.length + " ");
    }

    @Qualifier
    public List<GroupServiceDTO> getByCondition(GroupService groupServiceDTO, int page, int size) throws Exception {
        return groupServiceRepo.getByCondition(groupServiceDTO, page, size);
    }

    @Override
    public BigInteger countByCondition(GroupService groupServiceDTO, int page, int size) throws Exception {
        return groupServiceRepo.countByCondition(groupServiceDTO, page, size);
    }

    @Override
    public void saveActionDetail(GroupService oldgroup, GroupService newgroup, Long actionId) throws Exception {
        if (!oldgroup.getCode().equalsIgnoreCase(newgroup.getCode())) {
            saveActionDetail(actionId, newgroup.getCode().trim(), oldgroup.getCode(), Constants.GROUPSERVICES.CODE);
        }
        if (!oldgroup.getProductId().equals(newgroup.getProductId())) {
            saveActionDetail(actionId, newgroup.getProductId().toString().trim(), oldgroup.getProductId().toString(), Constants.GROUPSERVICES.PRODUCT_ID);
        }
        if (!oldgroup.getStatus().equals(newgroup.getStatus())) {
            saveActionDetail(actionId, newgroup.getStatus().trim(), oldgroup.getStatus(), Constants.GROUPSERVICES.STATUS);
        }
        if (!oldgroup.getName().equalsIgnoreCase(newgroup.getName())) {
            saveActionDetail(actionId, newgroup.getName().trim(), oldgroup.getName(), Constants.GROUPSERVICES.NAME);
        }

    }


    @Override
    public String lockGroupService(String[] arrId, StaffDTO user) throws Exception {
        for (int i = 0; i < arrId.length; i++) {
            GroupService groupService = getById(Long.parseLong(arrId[i]));
            groupService.setStatus("0");
            groupService.setUserupdate(user.getStaffCode());
            long millis = System.currentTimeMillis();
            Timestamp date = new Timestamp(millis);
            groupService.setCreatedate(date);
            ActionAudit actionAudit = actionAuditService.log(Constants.GROUP_SERVICE, Constants.GROUPSERVICES.ACTION_CODE_EDIT, user.getStaffCode(), groupService.getId(), user.getShopCode());
            saveActionDetail(actionAudit.getId(), "1", "0", Constants.GROUPSERVICES.STATUS);
        }
        return I18N.get(" " + arrId.length + " ");
    }

    public List<GroupService> findAllByStatusNotLike(String status) {
        return groupServiceRepo.findAllByStatusNotLikeOrderByName(status);
    }

    @Override
    public void saveNewActionDetail(Object newObject, Long plngActionId, StaffDTO staffDTO, Long plngProductId, Date date) throws Exception {
        if (newObject instanceof GroupServiceExcel) {
            GroupServiceExcel groupServiceExcel = (GroupServiceExcel) newObject;
            actionDetailService.createActionDetail(Constants.GROUPSERVICES.CODE, groupServiceExcel.getCode().trim(), plngActionId, null);
            actionDetailService.createActionDetail(Constants.GROUPSERVICES.PRODUCT_ID, String.valueOf(plngProductId), plngActionId, null);
            actionDetailService.createActionDetail(Constants.GROUPSERVICES.NAME, groupServiceExcel.getName().trim(), plngActionId, null);
            actionDetailService.createActionDetail(Constants.GROUPSERVICES.STATUS, "1", plngActionId, null);
        } else if (newObject instanceof GroupService) {
            GroupService groupService = (GroupService) newObject;
            actionDetailService.createActionDetail(Constants.GROUPSERVICES.CODE, groupService.getCode().trim(), plngActionId, null);
            actionDetailService.createActionDetail(Constants.GROUPSERVICES.PRODUCT_ID, String.valueOf(plngProductId), plngActionId, null);
            actionDetailService.createActionDetail(Constants.GROUPSERVICES.NAME, groupService.getName().trim(), plngActionId, null);
            actionDetailService.createActionDetail(Constants.GROUPSERVICES.STATUS, "1", plngActionId, null);
        }
    }

    @Override
    public boolean validateGroupService(GroupService groupService) {
        boolean vblnCheck = false;
        if (!DataUtil.isNullOrEmpty(groupService.getName()) && !DataUtil.isNullOrEmpty(groupService.getCode())) {
            Pattern patternCode = Pattern.compile("(^([-a-zA-Z0-9_]{1,50})$)");
            Pattern patternName = Pattern.compile(".{1,100}");
            Matcher matcherCode = patternCode.matcher(groupService.getCode().trim());
            Matcher matcherName = patternName.matcher(groupService.getName().trim());
            if (matcherCode.matches() && matcherName.matches())
                vblnCheck = true;
        }
        return vblnCheck;
    }

    @Override
    public void saveActionDetail(Long actionID, String oldValue, String newValue, String column) {
        ActionDetail actionDetail = new ActionDetail();
        actionDetail.setActionAuditId(actionID);
        actionDetail.setOldValue(oldValue);
        actionDetail.setNewValue(newValue);
        actionDetail.setColumnName(column);
        actionDetailRepo.save(actionDetail);

    }


}
