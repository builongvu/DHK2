package vn.vissoft.dashboard.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.vissoft.dashboard.config.I18N;
import vn.vissoft.dashboard.dto.ApParamDTO;
import vn.vissoft.dashboard.dto.SearchWarningReceiveDTO;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.excel.*;
import vn.vissoft.dashboard.helper.ApiResponse;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.helper.excelreader.ExcelReader;
import vn.vissoft.dashboard.helper.excelreader.ExcelWarningConfig;
import vn.vissoft.dashboard.model.*;
import vn.vissoft.dashboard.repo.*;
import vn.vissoft.dashboard.services.ActionAuditService;
import vn.vissoft.dashboard.services.ApParamService;
import vn.vissoft.dashboard.services.WarningReceiveService;

import javax.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Transactional
@Service
public class WarningReceiveServiceImpl implements WarningReceiveService {

    @Value("${server.tomcat.basedir}")
    private String mstrUploadPath;

    @Autowired
    private WarningReceiveConfigRepo warningReceiveConfigRepo;

    @Autowired
    private WarningConfigRepo warningConfigCustomRepo;

    @Autowired
    private ApParamService apParamService;

    @Autowired
    private ApParamRepo apParamRepo;

    @Autowired
    private PartnerRepo partnerRepo;

    @Autowired
    private ActionAuditService actionAuditService;

    @Autowired
    private ActionDetailRepo actionDetailRepo;

    private ExcelWarningConfig excelWarningConfig = new ExcelWarningConfig();

    /**
     * Lấy ra tất cả cấu hình nhận cảnh báo
     *
     * @return List<WarningReceiveConfig>
     * @throws Exception
     * @author CuongDT
     * version 1.1
     * @since 18/11/2019
     */
    @Override
    public List<WarningReceiveConfig> getAllWarningReceive() throws Exception {
        return warningReceiveConfigRepo.findAll();
    }


    /**
     * Lấy ra tất cả cấu hình nhận cảnh báo có sắp xếp
     *
     * @return List<WarningReceiveConfig>
     * @throws Exception
     * @author CuongDT
     * version 1.1
     * @since 18/11/2019
     */
    @Override
    public List<WarningReceiveConfig> getAllWarningReceiveOrderBy() throws Exception {
        return warningConfigCustomRepo.getAllReceiveOrderBy();
    }

    /**
     * Tìm kiếm cấu hình nhận cảnh báo theo điều kiện
     *
     * @return List<WarningReceiveConfig>
     * @throws Exception
     * @author CuongDT
     * version 1.1
     * @since 18/11/2019
     */
    @Override
    public List<WarningReceiveConfig> getByCondition(SearchWarningReceiveDTO searchWarningReceiveDTO, int page, int size) throws Exception {
        return warningConfigCustomRepo.getReceiveByCondition(searchWarningReceiveDTO, page, size);
    }


    /**
     * cập nhật cấu hình nhận cảnh báo
     *
     * @param warningReceiveConfig
     * @param user
     * @throws Exception
     * @author CuongDT
     * version 1.1
     * @since 18/11/2019
     */
    @Override
    public String updateWarningReceive(WarningReceiveConfig warningReceiveConfig, StaffDTO user) throws Exception {
        WarningReceiveConfig oldWarning = getById(warningReceiveConfig.getMlngId());
        warningReceiveConfig.setMdtCreateDate(new DataUtil().getDateTimeNow());
        String vstrShopCode = warningReceiveConfig.getMstrShopCode();
        List<WarningReceiveConfig> list = checkDuplicate(vstrShopCode, warningReceiveConfig.getMintWarningLevel(), warningReceiveConfig.getMlngId());
        if (list.size() > 0) {
            return Constants.WARNINGSEND.DUPLICATE;
        } else {
            warningReceiveConfig.setMstrUser(user.getStaffCode());
            ActionAudit actionAudit = actionAuditService.log(Constants.WARNINGRECEIVE.WARNING_RECEIVE, Constants.ACTION_CODE_EDIT, user.getStaffCode(), warningReceiveConfig.getMlngId(), user.getShopCode());
            saveActionDetail(oldWarning, warningReceiveConfig, actionAudit.getId());
        }
        return Constants.WARNINGSEND.SUCCESS;
    }

    /**
     * Thêm mới cấu hình nhận cảnh báo
     *
     * @param user
     * @param warningReceiveConfig
     * @throws Exception
     * @author CuongDT
     * version 1.1
     * @since 18/11/2019
     */
    public String saveNewReceive(WarningReceiveConfig warningReceiveConfig, StaffDTO user) throws Exception {
        warningReceiveConfig.setMdtCreateDate(new DataUtil().getDateTimeNow());
        String vstrShopCode = warningReceiveConfig.getMstrShopCode();
        List<WarningReceiveConfig> list = checkDuplicate(vstrShopCode, warningReceiveConfig.getMintWarningLevel(), warningReceiveConfig.getMlngId());
        if (list.size() > 0) {
            return Constants.WARNINGSEND.DUPLICATE;
        } else {
           Optional<ManageInfoPartner> optional = partnerRepo.getByShopCode(warningReceiveConfig.getMstrShopCode());
           if(optional.isPresent()){
               warningReceiveConfig.setMstrChannel(optional.get().getVdsChannelCode());
           }
            warningReceiveConfig.setMstrUser(user.getStaffCode());
            WarningReceiveConfig newWarning = saveAndFlush(warningReceiveConfig);
            ActionAudit actionAudit = actionAuditService.log(Constants.WARNINGRECEIVE.WARNING_RECEIVE, Constants.ACTION_CODE_ADD, user.getStaffCode(), newWarning.getMlngId(), user.getShopCode());
            saveNewActionDetail(newWarning, actionAudit.getId());
            return Constants.WARNINGSEND.SUCCESS;
        }
    }

    /**
     * Tìm kiếm cấu hình nhận cảnh báo theo mã đơn vị, loại cảnh báo và mã ID
     *
     * @param shopCode
     * @param warningLV
     * @param id
     * @return List<WarningReceiveConfig>
     * @throws Exception
     * @author CuongDT
     * version 1.1
     * @since 18/11/2019
     */
    @Override
    public List<WarningReceiveConfig> checkDuplicate(String shopCode, int warningLV, Long id) {
        return warningConfigCustomRepo.checkDuplicateWarningReceive(shopCode, warningLV, id);
    }

    /**
     * Tạo ra file mẫu Excel tải lên dữ liệu
     *
     * @return ByteArrayInputStream
     * @throws Exception
     * @author CuongDT
     * version 1.1
     * @since 18/11/2019
     */
    @Override
    public ByteArrayInputStream getTemplate() throws Exception {
        return excelWarningConfig.writeReveiceTemplate();
    }


    private void checkTheSameRecord(List<? extends BaseExcelEntity> plstList) {

        for (int i = 0; i < plstList.size(); i++) {
            for (int j = i + 1; j < plstList.size(); j++) {
                if (!DataUtil.isNullObject(plstList.get(i)) && !DataUtil.isNullObject(plstList.get(j))) {
                    if (DataUtil.isNullOrEmpty(plstList.get(i).getError())) {
                        if ((plstList.get(i)).equals(plstList.get(j))) {
                            plstList.get(j).setError(I18N.get("common.excel.row.error") + " " + (j + 6) + " " + I18N.get("common.excel.identical.error") + " " + (i + 6));
                        }
                    }
                }
            }
        }
    }

    /**
     * Tải lên dữ liệu từ mẫu file Excel
     *
     * @param file
     * @param user
     * @return BaseWarningReceive
     * @throws Exception
     * @author CuongDT
     * version 1.1
     * @since 18/11/2019
     */
    @Override
    public BaseWarningReceive upload(MultipartFile file, StaffDTO user) throws Exception {
        String vstrOriginalName = file.getOriginalFilename();
        String vstrPath = mstrUploadPath;
        Path paths = Paths.get(vstrPath + "/" + vstrOriginalName);
        int vintIndex = vstrOriginalName.lastIndexOf(".");
        String vstrResultFileName;
        if (vintIndex > -1) {
            vstrResultFileName = vstrOriginalName.substring(0, vstrOriginalName.lastIndexOf(".")) + I18N.get("common.table.warning.result");
        } else {
            vstrResultFileName = vstrOriginalName + I18N.get("common.table.warning.result");
        }

        if (Files.exists(paths)) {
            Files.delete(paths);
        }
        Files.copy(file.getInputStream(), paths);

        ExcelReader<WarningReceiveServiceExcel> vreader = new ExcelReader<>();
        List<WarningReceiveServiceExcel> ListWarningR = vreader.read(paths.toString(), WarningReceiveServiceExcel.class);
        List<WarningReceiveConfig> List = new ArrayList<>();
        List<ManageInfoPartner> partner = partnerRepo.findAll();
        List<ApParamDTO> listWLV = apParamService.findByTypeAndStatus(Constants.WARNINGSEND.WARNINGLV, Constants.PARAM_STATUS);
        List<ApParamDTO> listILV = apParamService.findByTypeAndStatus(Constants.WARNINGSEND.INFORMLV, Constants.PARAM_STATUS);
        BaseWarningReceive baseWarningReceive = new BaseWarningReceive();
        int vintTotal = 0;
        int vintSuccess = 0;
        checkTheSameRecord(ListWarningR);
        for (int i = 0; i < ListWarningR.size(); i++) {
            WarningReceiveConfig warningReceiveConfig = new WarningReceiveConfig();
            WarningReceiveServiceExcel warningReceiveServiceExcel;
            if (ListWarningR.get(i) == null) {
                warningReceiveServiceExcel = new WarningReceiveServiceExcel();
                warningReceiveServiceExcel.setError(" ");
                continue;
            }
            vintTotal++;

            if (DataUtil.isNullOrEmpty(ListWarningR.get(i).getError())) {
//            Check mã đơn vị và check trùng với cấu hình đã có
                if (DataUtil.isNullOrEmpty(ListWarningR.get(i).getMstrShopCode())) {
                    ListWarningR.get(i).setError(I18N.get("common.table.warning.datanull") + " B");
                    continue;
                }
                AtomicInteger vcheck = new AtomicInteger();
                if ("-1".equals(ListWarningR.get(i).getMstrShopCode().trim())) {
                    vcheck.set(1);
                    warningReceiveConfig.setMstrShopCode("-1");
                } else {
                    warningReceiveConfig.setMstrShopCode(ListWarningR.get(i).getMstrShopCode().trim());
                    partner.forEach(manageInfoPartner -> {
                        if (warningReceiveConfig.getMstrShopCode().trim().equals(manageInfoPartner.getShopCode())) {
                            vcheck.set(1);
                            warningReceiveConfig.setMstrChannel(manageInfoPartner.getVdsChannelCode());
                        }
                    });
                }

//                    Check null Loại cảnh báo
                if (DataUtil.isNullOrEmpty(ListWarningR.get(i).getMintWarningLevel())) {
                    if (DataUtil.isNullOrEmpty(ListWarningR.get(i).getError())) {
                        ListWarningR.get(i).setError(I18N.get("common.table.warning.datanull") + " C");
                    }
                    continue;
                } else {
                    int e = 0;
                    String warningLV = ListWarningR.get(i).getMintWarningLevel().trim().substring(0, 1);
                    for (ApParamDTO lst : listWLV) {
                        if (lst.getCode().equals(warningLV)) {
                            e = 1;
                            break;
                        }
                    }
                    ;
                    if (e == 0) {
                        ListWarningR.get(i).setError(I18N.get("common.table.warning.ServiceError") + " C");
                        continue;
                    } else {
                        warningReceiveConfig.setMintWarningLevel(Integer.parseInt(warningLV));
                    }
                }

//                    Check null mức cảnh báo
                if (DataUtil.isNullOrEmpty(ListWarningR.get(i).getMintInformLevel())) {
                    if (DataUtil.isNullOrEmpty(ListWarningR.get(i).getError())) {
                        ListWarningR.get(i).setError(I18N.get("common.table.warning.datanull") + " D");
                    }
                    continue;
                } else {
                    int e = 0;
                    String InfLV = ListWarningR.get(i).getMintInformLevel().trim().substring(0, 1);
                    for (ApParamDTO lst : listILV) {
                        if (lst.getCode().equals(InfLV)) {
                            e = 1;
                            break;
                        }
                    }
                    ;
                    if (e == 0) {
                        ListWarningR.get(i).setError(I18N.get("common.table.warning.ServiceError") + " D");
                        continue;
                    } else {
                        warningReceiveConfig.setMintInformLevel(Integer.parseInt(InfLV));
                    }
                }

                if (vcheck.get() != 1) {
                    ListWarningR.get(i).setError(I18N.get("common.table.warning.ServiceError") + " B");
                    continue;
                } else {
                    List<WarningReceiveConfig> varrList = checkDuplicate(warningReceiveConfig.getMstrShopCode(), warningReceiveConfig.getMintWarningLevel(), -1L);
                    if (varrList.size() > 0) {
                        warningReceiveConfig.setMlngId(varrList.get(0).getMlngId());
                    } else {
                        warningReceiveConfig.setMlngId(-1L);
                    }
                }
                warningReceiveConfig.setMstrUser(user.getStaffCode());
                warningReceiveConfig.setMdtCreateDate(new DataUtil().getDateTimeNow());
                warningReceiveConfig.setMstrStatus(Constants.PARAM_STATUS);
                vintSuccess++;
                List.add(warningReceiveConfig);
            }
        }
        baseWarningReceive.setSumSuccessfulRecord(vintSuccess);
        baseWarningReceive.setMintTotal(vintTotal);
        baseWarningReceive.setList(List);
        baseWarningReceive.setFileName(vstrResultFileName);
        String vstrResultFilePath = vstrPath + "/" + vstrResultFileName;
        vreader.writeResultWarning(paths.toString(), vstrResultFilePath, WarningReceiveServiceExcel.class, ListWarningR, I18N.get("common.success.status"), 0);
        for (int i = 0; i < baseWarningReceive.getList().size(); i++) {
            WarningReceiveConfig warningReceiveConfig = baseWarningReceive.getList().get(i);
            if (warningReceiveConfig.getMlngId() != -1L) {
                WarningReceiveConfig oldWarning = getById(warningReceiveConfig.getMlngId());
                ActionAudit actionAudit = actionAuditService.log(Constants.WARNINGSEND.WARNING_SEND, Constants.ACTION_CODE_EDIT, user.getStaffCode(), warningReceiveConfig.getMlngId(), user.getShopCode());
                saveActionDetail(oldWarning, warningReceiveConfig, actionAudit.getId());
            } else {
                WarningReceiveConfig newWarning = saveAndFlush(warningReceiveConfig);
                ActionAudit actionAudit = actionAuditService.log(Constants.WARNINGSEND.WARNING_SEND, Constants.ACTION_CODE_ADD, user.getStaffCode(), newWarning.getMlngId(), user.getShopCode());
                saveNewActionDetail(newWarning, actionAudit.getId());
            }
        }
        return baseWarningReceive;
    }

    /**
     * Lưu cấu hình nhận cảnh báo và trả về Object vừa lưu
     *
     * @param warningReceiveConfig
     * @return WarningReceiveConfig
     * @throws Exception
     * @author CuongDT
     * version 1.1
     * @since 18/11/2019
     */
    @Override
    public WarningReceiveConfig saveAndFlush(WarningReceiveConfig warningReceiveConfig) {
        return warningReceiveConfigRepo.saveAndFlush(warningReceiveConfig);
    }

    /**
     * Tìm cấu hình nhận cảnh báo theo ID
     *
     * @param mlngId
     * @return WarningReceiveConfig
     * @throws Exception
     * @author CuongDT
     * version 1.1
     * @since 18/11/2019
     */
    @Override
    public WarningReceiveConfig getById(Long mlngId) {
        return warningReceiveConfigRepo.getByMlngId(mlngId);
    }


    /**
     * Lưu thay đổi vào bảng ActionDetail khi cập nhật
     *
     * @param oldWarning
     * @param newWarning
     * @param actionId
     * @throws Exception
     * @author CuongDT
     * version 1.1
     * @since 18/11/2019
     */
    @Override
    public void saveActionDetail(WarningReceiveConfig oldWarning, WarningReceiveConfig newWarning, Long actionId) throws Exception {

        if (!oldWarning.getMstrStatus().equals(newWarning.getMstrStatus())) {
            saveActionDetail(actionId, oldWarning.getMstrStatus(), newWarning.getMstrStatus(), Constants.WARNINGRECEIVE.STATUS);
        }


        if (oldWarning.getMintInformLevel() != newWarning.getMintInformLevel()) {
            saveActionDetail(actionId, oldWarning.getMintInformLevel().toString(), newWarning.getMintInformLevel().toString(), Constants.WARNINGRECEIVE.INFORMLV);
        }

        if (!oldWarning.getMstrShopCode().equals(newWarning.getMstrShopCode())) {
            saveActionDetail(actionId, oldWarning.getMstrShopCode(), newWarning.getMstrShopCode(), Constants.WARNINGRECEIVE.SHOPCODE);
        }

        if (oldWarning.getMintWarningLevel() != newWarning.getMintWarningLevel()) {
            saveActionDetail(actionId, oldWarning.getMintWarningLevel().toString(), newWarning.getMintWarningLevel().toString(), Constants.WARNINGRECEIVE.WARNINGLV);
        }
        warningReceiveConfigRepo.save(newWarning);
    }

    /**
     * Lưu thay đổi vào file ActionDetail khi thêm mới
     *
     * @param newWarning
     * @param actionId
     * @throws Exception
     * @author CuongDT
     * version 1.1
     * @since 18/11/2019
     */

    public void saveNewActionDetail(WarningReceiveConfig newWarning, Long actionId) throws Exception {
        saveActionDetail(actionId, null, newWarning.getMstrStatus(), Constants.WARNINGRECEIVE.STATUS);
        saveActionDetail(actionId, null, newWarning.getMintWarningLevel().toString(), Constants.WARNINGRECEIVE.WARNINGLV);
        saveActionDetail(actionId, null, newWarning.getMintInformLevel().toString(), Constants.WARNINGRECEIVE.INFORMLV);
        saveActionDetail(actionId, null, newWarning.getMstrShopCode(), Constants.WARNINGRECEIVE.SHOPCODE);
        saveActionDetail(actionId, null, newWarning.getMstrChannel(), Constants.WARNINGRECEIVE.CHANNEL);
    }

    public void saveActionDetail(Long actionID, String oldValue, String newValue, String column) {
        ActionDetail actionDetail = new ActionDetail();
        actionDetail.setActionAuditId(actionID);
        actionDetail.setOldValue(oldValue);
        actionDetail.setNewValue(newValue);
        actionDetail.setColumnName(column);
        actionDetailRepo.save(actionDetail);
    }


    /**
     * Mở khoá cấu hình nhận theo ID
     *
     * @param arrId
     * @param user
     * @throws Exception
     * @author CuongDT
     * version 1.1
     * @since 18/11/2019
     */
    public String unlockWarningReceive(String[] arrId, StaffDTO user) throws Exception {
        String status = I18N.get("common.table.warning.unlockSuccess");
        for (int i = 0; i < arrId.length; i++) {
            WarningReceiveConfig warningReceiveConfig = getById(Long.parseLong(arrId[i]));
            String vstrShopCode = warningReceiveConfig.getMstrShopCode();
            List<WarningReceiveConfig> list = checkDuplicate(vstrShopCode, warningReceiveConfig.getMintWarningLevel(), warningReceiveConfig.getMlngId());
            if (list.size() > 0) {
                if (status.equals(I18N.get("common.table.warning.unlockSuccess"))) {
                    if ("-1".equals(vstrShopCode)) {
                        status = "Tất cả";
                    } else {
                        status = warningConfigCustomRepo.getPartnerByShopCode(vstrShopCode).getShopName();
                    }
                } else {
                    if ("-1".equals(vstrShopCode)) {
                        status = "Tất cả";
                    } else {
                        status = status + ", " + warningConfigCustomRepo.getPartnerByShopCode(vstrShopCode).getShopName();
                    }
                }
            } else {
                warningReceiveConfig.setMstrStatus("1");
                warningReceiveConfig.setMstrUser(user.getStaffCode());
                warningReceiveConfig.setMdtCreateDate(new DataUtil().getDateTimeNow());
                ActionAudit actionAudit = actionAuditService.log(Constants.WARNINGRECEIVE.WARNING_RECEIVE, Constants.ACTION_CODE_EDIT, user.getStaffCode(), warningReceiveConfig.getMlngId(), user.getShopCode());
                saveActionDetail(actionAudit.getId(), "0", "1", Constants.WARNINGRECEIVE.STATUS);
            }
        }
        if (!status.equals(I18N.get("common.table.warning.unlockSuccess"))) {
            status = status + " " + I18N.get("common.table.warning.duplicate");
        }
        return status;
    }


    /**
     * Khoá cấu hình gửi cảnh báo theo ID
     *
     * @param arrId
     * @return user
     * @throws Exception
     * @author CuongDT
     * version 1.1
     * @since 18/11/2019
     */
    @Override
    public String lockWarningReceive(String[] arrId, StaffDTO user) throws Exception {
        for (int i = 0; i < arrId.length; i++) {
            WarningReceiveConfig warningReceiveConfig = getById(Long.parseLong(arrId[i]));
            warningReceiveConfig.setMstrStatus("0");
            warningReceiveConfig.setMstrUser(user.getStaffCode());
            warningReceiveConfig.setMdtCreateDate(new DataUtil().getDateTimeNow());
            ActionAudit actionAudit = actionAuditService.log(Constants.WARNINGRECEIVE.WARNING_RECEIVE, Constants.ACTION_CODE_EDIT, user.getStaffCode(), warningReceiveConfig.getMlngId(), user.getShopCode());
            saveActionDetail(actionAudit.getId(), "1", "0", Constants.WARNINGSEND.STATUS);
        }
        return I18N.get("common.table.warning.lockSuccess");
    }
}
