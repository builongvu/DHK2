package vn.vissoft.dashboard.services.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.vissoft.dashboard.config.I18N;
import vn.vissoft.dashboard.dto.ApParamDTO;
import vn.vissoft.dashboard.dto.SearchWarningSendDTO;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.excel.BaseExcelEntity;
import vn.vissoft.dashboard.dto.excel.BaseWarningSend;
import vn.vissoft.dashboard.dto.excel.WarningSendServiceExcel;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.helper.excelreader.ExcelReader;
import vn.vissoft.dashboard.helper.excelreader.ExcelWarningConfig;
import vn.vissoft.dashboard.model.*;
import vn.vissoft.dashboard.repo.*;
import vn.vissoft.dashboard.services.ActionAuditService;
import vn.vissoft.dashboard.services.ApParamService;
import vn.vissoft.dashboard.services.WarningContentService;
import vn.vissoft.dashboard.services.WarningSendService;

import javax.transaction.Transactional;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Transactional
@Service
public class WarningSendServiceImpl implements WarningSendService {

    @Value("${server.tomcat.basedir}")
    private String mstrUploadPath;

    @Autowired
    private WarningSendConfigRepo warningSendConfigRepo;

    @Autowired
    private WarningContentRepo warningContentRepo;

    @Autowired
    private ApParamService apParamService;

    @Autowired
    private WarningConfigRepo warningConfigCustomRepo;

    @Autowired
    private ServiceRepo serviceRepo;

    @Autowired
    private ActionDetailRepo actionDetailRepo;

    @Autowired
    private ActionAuditService actionAuditService;

    @Autowired
    private WarningContentService warningContentService;

    private ExcelWarningConfig excelWarningConfig = new ExcelWarningConfig();


    /**
     * Lấy ra tất cả cấu hình gửi cảnh báo
     *
     * @return List<WarningSendConfig>
     * @throws Exception
     * @author CuongDT
     * version 1.1
     * @since 18/11/2019
     */
    @Override
    public List<WarningSendConfig> getAllWarningSend(int page, int totalsize) throws Exception {
        return warningConfigCustomRepo.getAllWarningSend(page, totalsize);
    }

    /**
     * Tìm kiếm cấu hình gửi cảnh báo theo điều kiện
     *
     * @param searchWarningSendDTO
     * @return List<WarningSendConfig>
     * @throws Exception
     * @author CuongDT
     * version 1.1
     * @since 18/11/2019
     */
    @Override
    public List<WarningSendConfig> getByCondition(SearchWarningSendDTO searchWarningSendDTO, int page, int size) throws Exception {
        return warningConfigCustomRepo.getByCondition(searchWarningSendDTO, page, size);
    }

    /**
     * Lưu cấu hình gửi cảnh báo
     *
     * @param warningSendConfig
     * @param user
     * @throws Exception
     * @author CuongDT
     * version 1.1
     * @since 18/11/2019
     */
    @Override
    public String saveWarningSend(WarningSendConfig warningSendConfig, StaffDTO user) throws Exception {
        List<WarningSendConfig> list = getAllByServiceIdAndWarningLevel(warningSendConfig.getMlngServiceId(), warningSendConfig.getMintWarningLevel(), warningSendConfig.getMlngId());
        warningSendConfig.setMdtCreateDate(new DataUtil().getDateTimeNow());
        if (list.size() > 0) {
            return Constants.WARNINGSEND.DUPLICATE;
        } else {
            warningSendConfig.setMstrUser(user.getStaffCode());
            WarningSendConfig newWarning = saveAndFlush(warningSendConfig);
            ActionAudit actionAudit = actionAuditService.log(Constants.WARNINGSEND.WARNING_SEND, Constants.ACTION_CODE_ADD, user.getStaffCode(), newWarning.getMlngId(), user.getShopCode());
            saveNewActionDetail(newWarning, actionAudit.getId());
            return Constants.WARNINGSEND.SUCCESS;
        }
    }


    /**
     * Cập nhật cấu hình gửi cảnh báo
     * user
     *
     * @param warningSendConfig
     * @throws Exception
     * @author CuongDT
     * version 1.1
     * @since 18/11/2019
     */
    @Override
    public String updateWarningSend(WarningSendConfig warningSendConfig, StaffDTO user) throws Exception {
        WarningSendConfig oldWarning = getByID(warningSendConfig.getMlngId());
        List<WarningSendConfig> list = getAllByServiceIdAndWarningLevel(warningSendConfig.getMlngServiceId(), warningSendConfig.getMintWarningLevel(), warningSendConfig.getMlngId());
        warningSendConfig.setMdtCreateDate(new DataUtil().getDateTimeNow());
        if (list.size() > 0) {
            return Constants.WARNINGSEND.DUPLICATE;
        } else {
            warningSendConfig.setMstrUser(user.getStaffCode());
            ActionAudit actionAudit = actionAuditService.log(Constants.WARNINGSEND.WARNING_SEND, Constants.ACTION_CODE_EDIT, user.getStaffCode(), warningSendConfig.getMlngId(), user.getShopCode());
            saveActionDetail(oldWarning, warningSendConfig, actionAudit.getId());
            return Constants.WARNINGSEND.SUCCESS;
        }
    }

    /**
     * Lưu và trả về cấu hình gửi cảnh báo
     *
     * @param warningSendConfig
     * @return WarningSendConfig
     * @throws Exception
     * @author CuongDT
     * version 1.1
     * @since 18/11/2019
     */
    @Override
    public WarningSendConfig saveAndFlush(WarningSendConfig warningSendConfig) throws Exception {
        return warningSendConfigRepo.saveAndFlush(warningSendConfig);
    }

    /**
     * Lấy danh sách cấu hình gửi cảnh báo theo mã chỉ tiêu, loại cảnh báo và ID
     *
     * @param Service
     * @param WarningLV
     * @param id
     * @return WarningSendConfig
     * @throws Exception
     * @author CuongDT
     * version 1.1
     * @since 18/11/2019
     */
    @Override
    public List<WarningSendConfig> getAllByServiceIdAndWarningLevel(Long Service, int WarningLV, Long id) throws Exception {
        return warningConfigCustomRepo.checkDuplicateWarningSend(Service, WarningLV, id);
    }

    /**
     * Lấy danh sách cấu hình gửi cảnh báo có sắp xếp thứ tự
     *
     * @return WarningSendConfig
     * @throws Exception
     * @author CuongDT
     * version 1.1
     * @since 18/11/2019
     */
    @Override
    public List<WarningSendConfig> getAllOrderBy(String column, String orderby) throws Exception {
        return warningConfigCustomRepo.getAllOrderBy(column, orderby);
    }


    /**
     * Tìm kiếm chỉ tiêu theo trạng thái
     *
     * @return List<Service>
     * @throws Exception
     * @author CuongDT
     * version 1.1
     * @since 18/11/2019
     */
    @Override
    public List<vn.vissoft.dashboard.model.Service> getAllServiceByStatus() throws Exception {
        return serviceRepo.findAllByStatus();
    }


    /**
     * Tìm kiếm chỉ tiêu theo ID
     *
     * @param id
     * @return List<Service>
     * @throws Exception
     * @author CuongDT
     * version 1.1
     * @since 18/11/2019
     */
    @Override
    public List<vn.vissoft.dashboard.model.Service> getByServiceId(String id) throws Exception {
        return serviceRepo.findAllById(id);
    }

    /**
     * Tìm kiếm chỉ tiêu theo mã chỉ tiêu
     *
     * @param code
     * @return List<Service>
     * @throws Exception
     * @author CuongDT
     * version 1.1
     * @since 18/11/2019
     */
    @Override
    public List<vn.vissoft.dashboard.model.Service> getByServiceCode(String code) throws Exception {
        return serviceRepo.findAllByCode(code);
    }

    /**
     * Tạo file mẫu Excel để tải lên dữ liệu
     *
     * @return ByteArrayInputStream
     * @throws Exception
     * @author CuongDT
     * version 1.1
     * @since 18/11/2019
     */
    @Override
    public ByteArrayInputStream getTemplate() throws Exception {
        return excelWarningConfig.writeTemplate();
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
     * Tải lên dữ liệu từ file Excel
     *
     * @param file
     * @param user
     * @return BaseWarningSend
     * @throws Exception
     * @author CuongDT
     * version 1.1
     * @since 18/11/2019
     */
    @Override
    public BaseWarningSend upload(MultipartFile file, StaffDTO user) throws Exception {
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

        ExcelReader<WarningSendServiceExcel> vreader = new ExcelReader<>();
        List<WarningSendServiceExcel> ListWarningS = vreader.read(paths.toString(), WarningSendServiceExcel.class);
        List<WarningSendConfig> List = new ArrayList<>();
        List<ApParamDTO> listWLV = apParamService.findByTypeAndStatus(Constants.WARNINGSEND.WARNINGLV, Constants.PARAM_STATUS);
        List<ApParamDTO> listILV = apParamService.findByTypeAndStatus(Constants.WARNINGSEND.INFORMLV, Constants.PARAM_STATUS);
        WarningContent vobjContent = warningContentService.getFirst();
        BaseWarningSend baseWarningSend = new BaseWarningSend();
        int vintTotal = 0;
        int vintSuccess = 0;
        checkTheSameRecord(ListWarningS);
        for (int i = 0; i < ListWarningS.size(); i++) {
            WarningSendServiceExcel warningSendServiceExcel = ListWarningS.get(i);
            if (warningSendServiceExcel == null) {
                continue;
            }
            vintTotal++;
            if (DataUtil.isNullOrEmpty(warningSendServiceExcel.getError())) {
                WarningSendConfig warningSendConfig = new WarningSendConfig();
//                Check mã chỉ tiêu có tồn tại
                if (DataUtil.isNullOrEmpty(warningSendServiceExcel.getMstrServiceCode())) {
                    warningSendServiceExcel.setError(I18N.get("common.table.warning.datanull") + " B");
                    continue;
                } else {
                    if ("-1".equals(warningSendServiceExcel.getMstrServiceCode().trim())) {
                        warningSendConfig.setMlngServiceId(-1L);
                    } else {
                        List<vn.vissoft.dashboard.model.Service> varrLsv = getByServiceCode(warningSendServiceExcel.getMstrServiceCode().trim());
                        if (varrLsv.size() == 0) {
                            warningSendServiceExcel.setError(I18N.get("common.table.warning.ServiceError") + " B");
                            continue;
                        } else {
                            warningSendConfig.setMlngServiceId(varrLsv.get(0).getId());
                        }
                    }
                }
                if (DataUtil.isNullOrEmpty(warningSendServiceExcel.getMintWarningLevel())) {
                    warningSendServiceExcel.setError(I18N.get("common.table.warning.datanull") + " C");
                    continue;
                } else {
                    int e = 0;
                    String warningLV = warningSendServiceExcel.getMintWarningLevel().trim().substring(0, 1);
                    for (ApParamDTO lst : listWLV) {
                        if (lst.getCode().equals(warningLV)) {
                            e = 1;
                            break;
                        }
                    }
                    ;
                    if (e == 0) {
                        warningSendServiceExcel.setError(I18N.get("common.table.warning.ServiceError") + " C");
                        continue;
                    } else {
                        warningSendConfig.setMintWarningLevel(Integer.parseInt(warningLV));
                    }
                }
                if (DataUtil.isNullOrEmpty(warningSendServiceExcel.getMintEmail())) {
                    warningSendServiceExcel.setError(I18N.get("common.table.warning.datanull") + " D");
                    continue;
                } else {
                    String emaill = warningSendServiceExcel.getMintEmail().trim().substring(0, 1);
                    if (Constants.PARAM_STATUS.equals(emaill) || Constants.PARAM_STATUS_0.equals(emaill)) {
                        warningSendConfig.setMintEmail(Integer.parseInt(emaill));
                    } else {
                        continue;
                    }
                }
                if (DataUtil.isNullOrEmpty(warningSendServiceExcel.getMintSms())) {
                    warningSendServiceExcel.setError(I18N.get("common.table.warning.emailsms"));
                    continue;
                } else {
                    String sms = warningSendServiceExcel.getMintSms().trim().substring(0, 1);
                    if (Constants.PARAM_STATUS.equals(sms) || Constants.PARAM_STATUS_0.equals(sms)) {
                        warningSendConfig.setMintSms(Integer.parseInt(sms));
                    } else {
                        continue;
                    }
                }
                if (warningSendConfig.getMintSms() == 0 && warningSendConfig.getMintEmail() == 0) {
                    warningSendServiceExcel.setError(I18N.get("common.table.warning.emailsms"));
                    continue;
                }
                if (DataUtil.isNullOrEmpty(warningSendServiceExcel.getMintInformLevel())) {
                    warningSendServiceExcel.setError(I18N.get("common.table.warning.datanull") + " F");
                    continue;
                } else {
                    int e = 0;
                    String InfLV = warningSendServiceExcel.getMintInformLevel().trim().substring(0, 1);
                    for (ApParamDTO lst : listILV) {
                        if (lst.getCode().equals(InfLV)) {
                            e = 1;
                            break;
                        }
                    }
                    ;
                    if (e == 0) {
                        warningSendServiceExcel.setError(I18N.get("common.table.warning.ServiceError") + " F");
                        continue;
                    } else {
                        warningSendConfig.setMintInformLevel(Integer.parseInt(InfLV));
                    }
                }
                warningSendConfig.setMstrStatus(Constants.PARAM_STATUS);

//                Check trùng mã chỉ tiêu và loại cảnh báo
                List<WarningSendConfig> list = getAllByServiceIdAndWarningLevel(warningSendConfig.getMlngServiceId(), warningSendConfig.getMintWarningLevel(), -1L);
                if (list.size() > 0) {
                    warningSendConfig.setMlngId(list.get(0).getMlngId());
                    warningSendConfig.setMlngIdContent(list.get(0).getMlngIdContent());
                } else {
                    warningSendConfig.setMlngId(-1L);
                    warningSendConfig.setMlngIdContent(vobjContent.getMlngId());
                }
                if (DataUtil.isNullOrEmpty(warningSendServiceExcel.getError())) {
                    vintSuccess++;
                }
                warningSendConfig.setMstrUser(user.getStaffCode());
                warningSendConfig.setMdtCreateDate(new DataUtil().getDateTimeNow());
                List.add(warningSendConfig);
            }
        }
        String vstrResultFilePath = vstrPath + "/" + vstrResultFileName;
        vreader.writeResultWarning(paths.toString(), vstrResultFilePath, WarningSendServiceExcel.class, ListWarningS, I18N.get("common.success.status"), 1);
        baseWarningSend.setMintTotal(vintTotal);
        baseWarningSend.setSumSuccessfulRecord(vintSuccess);
        baseWarningSend.setList(List);
        baseWarningSend.setFilename(vstrResultFileName);
        for (int i = 0; i < baseWarningSend.getList().size(); i++) {
            WarningSendConfig warningSendConfig = baseWarningSend.getList().get(i);
            if (warningSendConfig.getMlngId() != -1L) {
                WarningSendConfig oldWarning = getByID(warningSendConfig.getMlngId());
                ActionAudit actionAudit = actionAuditService.log(Constants.WARNINGSEND.WARNING_SEND, Constants.ACTION_CODE_EDIT, user.getStaffCode(), warningSendConfig.getMlngId(), user.getShopCode());
                saveActionDetail(oldWarning, warningSendConfig, actionAudit.getId());
            } else {
                WarningSendConfig newWarning = saveAndFlush(warningSendConfig);
                ActionAudit actionAudit = actionAuditService.log(Constants.WARNINGSEND.WARNING_SEND, Constants.ACTION_CODE_ADD, user.getStaffCode(), newWarning.getMlngId(), user.getShopCode());
                saveNewActionDetail(newWarning, actionAudit.getId());
            }
        }
        return baseWarningSend;
    }


    /**
     * Lưu vào bảng ActionDetail khi cập nhật dữ liệu
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
    public void saveActionDetail(WarningSendConfig oldWarning, WarningSendConfig newWarning, Long actionId) throws Exception {

        if (oldWarning.getMlngIdContent() == null && newWarning.getMlngIdContent() == null) {
        } else if (oldWarning.getMlngIdContent() == null) {
            saveActionDetail(actionId, null, newWarning.getMlngIdContent().toString(), Constants.WARNINGSEND.CONTENT);
        } else if (newWarning.getMlngIdContent() == null) {
            saveActionDetail(actionId, oldWarning.getMlngIdContent().toString(), null, Constants.WARNINGSEND.CONTENT);
        } else {
            if (!oldWarning.getMlngIdContent().equals(newWarning.getMlngIdContent())) {
                saveActionDetail(actionId, oldWarning.getMlngIdContent().toString(), newWarning.getMlngIdContent().toString(), Constants.WARNINGSEND.CONTENT);
            }
        }
        if (!oldWarning.getMstrStatus().equals(newWarning.getMstrStatus())) {
            saveActionDetail(actionId, oldWarning.getMstrStatus(), newWarning.getMstrStatus(), Constants.WARNINGSEND.STATUS);
        }

        if (oldWarning.getMintEmail() != newWarning.getMintEmail()) {
            saveActionDetail(actionId, oldWarning.getMintEmail().toString(), newWarning.getMintEmail().toString(), Constants.WARNINGSEND.EMAIL);
        }

        if (oldWarning.getMintSms() != newWarning.getMintSms()) {
            saveActionDetail(actionId, oldWarning.getMintSms().toString(), newWarning.getMintSms().toString(), Constants.WARNINGSEND.SMS);
        }

        if (oldWarning.getMintInformLevel() != newWarning.getMintInformLevel()) {
            saveActionDetail(actionId, oldWarning.getMintInformLevel().toString(), newWarning.getMintInformLevel().toString(), Constants.WARNINGSEND.INFORMLV);
        }

        if (!oldWarning.getMlngServiceId().equals(newWarning.getMlngServiceId())) {
            saveActionDetail(actionId, oldWarning.getMlngServiceId().toString(), newWarning.getMlngServiceId().toString(), Constants.WARNINGSEND.SERVICEID);
        }

        if (oldWarning.getMintWarningLevel() != newWarning.getMintWarningLevel()) {
            saveActionDetail(actionId, oldWarning.getMintWarningLevel().toString(), newWarning.getMintWarningLevel().toString(), Constants.WARNINGSEND.WARNINGLV);
        }
        warningSendConfigRepo.save(newWarning);
    }

    /**
     * Lưu vào bảng ActionDetail khi thêm mới dữ liệu
     *
     * @param newWarning
     * @param actionId
     * @throws Exception
     * @author CuongDT
     * version 1.1
     * @since 18/11/2019
     */
    @Override
    public void saveNewActionDetail(WarningSendConfig newWarning, Long actionId) throws Exception {
        if (newWarning.getMlngIdContent() != null) {
            saveActionDetail(actionId, null, newWarning.getMlngIdContent().toString(), Constants.WARNINGSEND.CONTENT);
        }
        saveActionDetail(actionId, null, newWarning.getMstrStatus(), Constants.WARNINGSEND.STATUS);
        saveActionDetail(actionId, null, newWarning.getMintEmail().toString(), Constants.WARNINGSEND.EMAIL);
        saveActionDetail(actionId, null, newWarning.getMintSms().toString(), Constants.WARNINGSEND.SMS);
        saveActionDetail(actionId, null, newWarning.getMintWarningLevel().toString(), Constants.WARNINGSEND.WARNINGLV);
        saveActionDetail(actionId, null, newWarning.getMintInformLevel().toString(), Constants.WARNINGSEND.INFORMLV);
        saveActionDetail(actionId, null, newWarning.getMlngServiceId().toString(), Constants.WARNINGSEND.SERVICEID);
    }

    /**
     * Lưu vào bảng ActionDetail
     *
     * @param actionID
     * @param oldValue
     * @param newValue
     * @param column
     * @throws Exception
     * @author CuongDT
     * version 1.1
     * @since 18/11/2019
     */
    public void saveActionDetail(Long actionID, String oldValue, String newValue, String column) throws Exception {
        ActionDetail actionDetail = new ActionDetail();
        actionDetail.setActionAuditId(actionID);
        if (oldValue != null) {
            actionDetail.setOldValue(oldValue);
        }
        if (newValue != null) {
            actionDetail.setNewValue(newValue);
        }
        actionDetail.setColumnName(column);
        actionDetailRepo.save(actionDetail);
    }

    /**
     * Mở khoá cấu hình gửi theo ID
     *
     * @param arrId
     * @param user
     * @throws Exception
     * @author CuongDT
     * version 1.1
     * @since 18/11/2019
     */
    @Override
    public String unLockWarningSend(String[] arrId, StaffDTO user) throws Exception {
        String status = I18N.get("common.table.warning.unlockSuccess");
        for (int i = 0; i < arrId.length; i++) {
            WarningSendConfig warningSendConfig = getByID(Long.parseLong(arrId[i]));
            String service = warningSendConfig.getMlngServiceId().toString();
            List<WarningSendConfig> list = getAllByServiceIdAndWarningLevel(warningSendConfig.getMlngServiceId(), warningSendConfig.getMintWarningLevel(), warningSendConfig.getMlngId());
            warningSendConfig.setMdtCreateDate(new DataUtil().getDateTimeNow());
            if (list.size() > 0) {
                if (status.equals(I18N.get("common.table.warning.unlockSuccess"))) {
                    if ("-1".equals(service)) {
                        status = "Tất cả";
                    } else {
                        status = getByServiceId(warningSendConfig.getMlngServiceId().toString()).get(0).getName();
                    }
                } else {
                    if ("-1".equals(service)) {
                        status = "Tất cả";
                    } else {
                        status = status + ", " + getByServiceId(warningSendConfig.getMlngServiceId().toString()).get(0).getName();
                    }
                }
            } else {
                warningSendConfig.setMstrStatus(Constants.PARAM_STATUS);
                warningSendConfig.setMstrUser(user.getStaffCode());
                warningSendConfig.setMdtCreateDate(new DataUtil().getDateTimeNow());
                ActionAudit actionAudit = actionAuditService.log(Constants.WARNINGSEND.WARNING_SEND, Constants.ACTION_CODE_EDIT, user.getStaffCode(), warningSendConfig.getMlngId(), user.getShopCode());
                saveActionDetail(actionAudit.getId(), Constants.PARAM_STATUS_0, Constants.PARAM_STATUS, Constants.WARNINGSEND.STATUS);
            }
        }
        if (!status.equals(I18N.get("common.table.warning.unlockSuccess"))) {
            status = status + " " + I18N.get("common.table.warning.duplicate");
        }
        return status;
    }


    /**
     * Lấy Cấu hình gửi cảnh báo theo ID
     *
     * @param id
     * @return WarningSendConfig
     * @throws Exception
     * @author CuongDT
     * version 1.1
     * @since 18/11/2019
     */
    @Override
    public WarningSendConfig getByID(Long id) throws Exception {
        return warningConfigCustomRepo.getWarningSendByID(id);
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
    public String lockWarningSend(String[] arrId, StaffDTO user) throws Exception {
        for (int i = 0; i < arrId.length; i++) {
            WarningSendConfig warningSendConfig = getByID(Long.parseLong(arrId[i]));
            warningSendConfig.setMstrStatus(Constants.PARAM_STATUS_0);
            warningSendConfig.setMstrUser(user.getStaffCode());
            warningSendConfig.setMdtCreateDate(new DataUtil().getDateTimeNow());
            ActionAudit actionAudit = actionAuditService.log(Constants.WARNINGSEND.WARNING_SEND, Constants.ACTION_CODE_EDIT, user.getStaffCode(), warningSendConfig.getMlngId(), user.getShopCode());
            saveActionDetail(actionAudit.getId(), Constants.PARAM_STATUS, Constants.PARAM_STATUS_0, Constants.WARNINGSEND.STATUS);
        }
        return I18N.get("common.table.warning.lockSuccess");
    }

}

