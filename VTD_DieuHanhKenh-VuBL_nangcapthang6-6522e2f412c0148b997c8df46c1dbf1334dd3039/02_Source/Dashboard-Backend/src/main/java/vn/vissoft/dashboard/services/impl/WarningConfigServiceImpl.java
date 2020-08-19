package vn.vissoft.dashboard.services.impl;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.vissoft.dashboard.config.I18N;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.excel.BaseUploadEntity;
import vn.vissoft.dashboard.dto.excel.WarningServiceExcel;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.helper.excelreader.ExcelReader;
import vn.vissoft.dashboard.helper.excelreader.ExcelWarningConfigService;
import vn.vissoft.dashboard.model.ActionAudit;
import vn.vissoft.dashboard.model.WarningConfig;
import vn.vissoft.dashboard.repo.ApParamRepo;
import vn.vissoft.dashboard.repo.ChannelRepo;
import vn.vissoft.dashboard.repo.ServiceRepo;
import vn.vissoft.dashboard.repo.WarningConfigRepo;
import vn.vissoft.dashboard.services.ActionAuditService;
import vn.vissoft.dashboard.services.ActionDetailService;
import vn.vissoft.dashboard.services.PlanMonthlyService;
import vn.vissoft.dashboard.services.WarningConfigService;

import javax.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
public class WarningConfigServiceImpl implements WarningConfigService {
    @Autowired
    WarningConfigRepo warningConfigRepo;

    @Value("${server.tomcat.basedir}")
    private String mstrUploadPath;

    @Autowired
    private PlanMonthlyService planMonthlyService;

    @Autowired
    private ServiceRepo serviceRepo;

    @Autowired
    private ChannelRepo channelRepo;

    @Autowired
    private ApParamRepo apParamRepo;

    @Autowired
    private ActionAuditService actionAuditService;

    @Autowired
    private ActionDetailService actionDetailService;
    @Autowired
    private WarningConfigRepo warningConfigCustomRepo;
    private ExcelWarningConfigService excelWarningConfigService = new ExcelWarningConfigService();
    public static final Logger LOGGER = LogManager.getLogger(WarningConfigServiceImpl.class);

    @Override
    public List<WarningConfig> getAllWarning() throws Exception {
        return warningConfigRepo.findAll();
    }

    @Override
    public List<WarningConfig> getAllWarningById(Long serviceId) throws Exception {
        List<Object[]> lstWarningConfig = warningConfigRepo.getAllAndSortById(serviceId);
        List<WarningConfig> list = new ArrayList<>();
        if(lstWarningConfig != null) {
            for (Object[] objects : lstWarningConfig) {
                Long id = DataUtil.safeToLong(objects[0]);
                Long svId = DataUtil.safeToLong(objects[1]);
                String vdsChannelCode = DataUtil.safeToString(objects[2]);
                Integer level = DataUtil.safeToInt(objects[3]);
                String status = DataUtil.safeToString(objects[4]);
                Double fromValue = DataUtil.safeToDouble(objects[5]);
                Double toValue = DataUtil.safeToDouble(objects[6]);
                
                String exp = DataUtil.safeToString(objects[7]);
//                if(objects[7] != null) {
//                    exp = DataUtil.safeToString(objects[7]);
//                }

                WarningConfig warningConfig = new WarningConfig();

                warningConfig.setWcID(id);
                warningConfig.setSvID(svId);
                warningConfig.setVdscCode(vdsChannelCode);
                warningConfig.setwStatus(status);
                warningConfig.setWlevel(level);
                warningConfig.setWfvalue(fromValue);
                warningConfig.setWovalue(toValue);
                if(exp != null) {
                    warningConfig.setWexp(exp);
                }
                list.add(warningConfig);
            }
        }
        return list;
    }

//    @Override
//    public WarningConfig addWarning(WarningConfig warningConfig) {
//        return warningConfigRepo.save(warningConfig);
//    }

    @Override
    public BaseUploadEntity upload(MultipartFile file, StaffDTO staffDTO) throws Exception {
        int vintCheck = 0;
        int vintSumSuccessfulRecord = 0;
        int vintSumRecord = 0;
        Long vlngServiceId;
        String[] vstrChannelCodes;
        String vstrChannelCode = null;
        String vstrWarning = null;
        List<WarningConfig> vlstWarningConfigs;

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
            ExcelReader<WarningServiceExcel> vreader = new ExcelReader<>();
            List<WarningServiceExcel> vlstWarningServiceExcels = vreader.read(vstrOriginalName, WarningServiceExcel.class);

            if (DataUtil.isNullOrEmpty(vlstWarningServiceExcels)) {
                baseUploadEntity.setMessage(I18N.get(Constants.EMPTY_FILE_ERROR));
                return baseUploadEntity;
            } else {
                planMonthlyService.checkTheSameRecord(vlstWarningServiceExcels);

                for (WarningServiceExcel warningServiceExcel : vlstWarningServiceExcels) {
                    if (!DataUtil.isNullObject(warningServiceExcel)) {
                        vintSumRecord++;

                        if (DataUtil.isNullOrEmpty(warningServiceExcel.getError())) {
                            vstrChannelCodes = warningServiceExcel.getVdsChannelCode().split("-");
                            vstrChannelCode = vstrChannelCodes[0];
                            warningServiceExcel.setVdsChannelCode(vstrChannelCode);
                            vstrWarning = warningServiceExcel.getWarning().trim().substring(0, 1);
                            warningServiceExcel.setWarning(vstrWarning);
                            if (!serviceRepo.checkExistedServiceCode(warningServiceExcel.getServiceCode().trim())) {
                                warningServiceExcel.setError(I18N.get("common.excel.active.error") + ". " + I18N.get("common.excel.column.error") + " C ");
                            } else if (!serviceRepo.checkServiceInChannel(vstrChannelCode, warningServiceExcel.getServiceCode().trim())) {
                                warningServiceExcel.setError(I18N.get("common.excel.service.channel.vds.error") + ". " + I18N.get("common.excel.column.error") + " C");
                            } else if (warningServiceExcel.getFromValue() == null && warningServiceExcel.getToValue() == null) {
                                warningServiceExcel.setError(I18N.get("common.excel.empty.value.error ") + ". " + I18N.get("common.excel.column.error") + " E ");
                            } else if (warningServiceExcel.getFromValue() != null && warningServiceExcel.getToValue() != null
                                    && warningServiceExcel.getFromValue() >= warningServiceExcel.getToValue()) {
                                warningServiceExcel.setError(I18N.get("common.excel.bound.error") + ". " + I18N.get("common.excel.column.error") + " E ");
                            } else if(!DataUtil.isNullOrEmpty(warningServiceExcel.getFormula())){
                                if(warningServiceExcel.getFormula().trim().length()>100) {
                                    warningServiceExcel.setError(I18N.get("common.table.warning.maxleng"));
                                }
                            }
                        }
                        if (DataUtil.isNullOrEmpty(warningServiceExcel.getError())) {
                            vlngServiceId = serviceRepo.findServiceIdByCode(warningServiceExcel.getServiceCode().trim());
                            try {

                                WarningConfig warningConfig = warningConfigRepo.findWarningConfigFromFile(warningServiceExcel);
                                if (DataUtil.isNullObject(warningConfig)) {
                                    WarningConfig warningConfigAdd = new WarningConfig(vlngServiceId, vstrChannelCode, Integer.parseInt(vstrWarning)
                                            , "1", (warningServiceExcel.getFromValue() == null ? null : warningServiceExcel.getFromValue()),
                                            (warningServiceExcel.getToValue() == null ? null : warningServiceExcel.getToValue()), (DataUtil.isNullOrEmpty(warningServiceExcel.getFormula()) ? null : warningServiceExcel.getFormula().trim()));
                                    vlstWarningConfigs = warningConfigRepo.findByChannelServiceNotLv(vstrChannelCode, vlngServiceId, warningConfigAdd.getWlevel());
                                    if (checkDuplicateBound(vlstWarningConfigs, warningConfigAdd)) {
                                        warningServiceExcel.setError(I18N.get("common.excel.duplicate.warning.error") + ". " + I18N.get("common.excel.column.error") + " E ");
                                    } else if (checkBoundInBound(vlstWarningConfigs, warningConfigAdd.getWfvalue(), warningConfigAdd.getWovalue())) {
                                        warningServiceExcel.setError(I18N.get("common.excel.duplicate.warning.error") + ". " + I18N.get("common.excel.column.error") + " E ");
                                    } else {
                                        warningConfigRepo.persist(warningConfigAdd);

                                        ActionAudit actionAudit = actionAuditService.log(Constants.SERVICE_WARNING_CONFIG, Constants.CREATE, staffDTO.getStaffCode(), vlngServiceId, staffDTO.getShopCode());
                                        actionDetailService.createActionDetail(Constants.WARNINGSEND.VDS_CHANNEL_CODE, vstrChannelCode, actionAudit.getId(), null);
                                        actionDetailService.createActionDetail(Constants.WARNINGSEND.SERVICEID, String.valueOf(vlngServiceId), actionAudit.getId(), null);
                                        actionDetailService.createActionDetail(Constants.WARNINGSEND.WARNINGLV, vstrWarning, actionAudit.getId(), null);
                                        actionDetailService.createActionDetail(Constants.WARNINGSEND.FROM_VALUE, (warningServiceExcel.getFromValue() == null ? null : String.valueOf(warningServiceExcel.getFromValue())), actionAudit.getId(), null);
                                        actionDetailService.createActionDetail(Constants.WARNINGSEND.TO_VALUE, (warningServiceExcel.getToValue() == null ? null : String.valueOf(warningServiceExcel.getToValue())), actionAudit.getId(), null);
                                        actionDetailService.createActionDetail(Constants.WARNINGSEND.STATUS, Constants.PARAM_STATUS, actionAudit.getId(), null);
                                        if (!DataUtil.isNullOrEmpty(warningServiceExcel.getFormula()))
                                            actionDetailService.createActionDetail(Constants.WARNINGSEND.EXP, (DataUtil.isNullOrEmpty(warningServiceExcel.getFormula()) ? null : warningServiceExcel.getFormula().trim()), actionAudit.getId(), null);

                                        vintSumSuccessfulRecord++;
                                    }
                                } else {
                                    vlstWarningConfigs = warningConfigRepo.findByChannelServiceNotLv(vstrChannelCode, vlngServiceId, Integer.valueOf(warningServiceExcel.getWarning()));
                                    if (checkDuplicateBound(vlstWarningConfigs, warningConfig)) {
                                        warningServiceExcel.setError(I18N.get("common.excel.duplicate.warning.error") + ". " + I18N.get("common.excel.column.error") + " E ");
                                    } else if (checkBoundInBound(vlstWarningConfigs, warningServiceExcel.getFromValue(), warningServiceExcel.getToValue())) {
                                        warningServiceExcel.setError(I18N.get("common.excel.duplicate.warning.error") + ". " + I18N.get("common.excel.column.error") + " E ");
                                    } else {
                                        ActionAudit actionAudit = actionAuditService.log(Constants.SERVICE_WARNING_CONFIG, Constants.EDIT, staffDTO.getStaffCode(), vlngServiceId, staffDTO.getShopCode());
                                        if (!warningServiceExcel.getFromValue().equals(warningConfig.getWfvalue()))
                                            actionDetailService.createActionDetail(Constants.WARNINGSEND.FROM_VALUE, (warningServiceExcel.getFromValue() == null ? null : String.valueOf(warningServiceExcel.getFromValue())), actionAudit.getId(), String.valueOf(warningConfig.getWfvalue()));
                                        if (!warningServiceExcel.getToValue().equals(warningConfig.getWovalue()))
                                            actionDetailService.createActionDetail(Constants.WARNINGSEND.TO_VALUE, (warningServiceExcel.getToValue() == null ? null : String.valueOf(warningServiceExcel.getToValue())), actionAudit.getId(), String.valueOf(warningConfig.getWovalue()));
                                        if (DataUtil.isNullOrEmpty(warningServiceExcel.getFormula()) && !DataUtil.isNullOrEmpty(warningConfig.getWexp()))
                                            actionDetailService.createActionDetail(Constants.WARNINGSEND.EXP, null, actionAudit.getId(), warningConfig.getWexp());
                                        else if (!DataUtil.isNullOrEmpty(warningServiceExcel.getFormula()) && !warningServiceExcel.getFormula().trim().equalsIgnoreCase(warningConfig.getWexp().trim()))
                                            actionDetailService.createActionDetail(Constants.WARNINGSEND.EXP, warningServiceExcel.getFormula(), actionAudit.getId(), DataUtil.isNullOrEmpty(warningConfig.getWexp()) ? null : warningConfig.getWexp());
                                        warningConfig.setWfvalue((warningServiceExcel.getFromValue() == null ? null : warningServiceExcel.getFromValue()));
                                        warningConfig.setWovalue((warningServiceExcel.getToValue() == null ? null : warningServiceExcel.getToValue()));
                                        warningConfig.setWexp((warningServiceExcel.getFormula() == null ? null : warningServiceExcel.getFormula().trim()));
                                        warningConfigRepo.update(warningConfig);

                                        vintSumSuccessfulRecord++;
                                    }
                                }
                            } catch (Exception e) {
                                LOGGER.error(e.getMessage(), e);
                                warningServiceExcel.setError(e.getMessage());
                            }
                        }
                    } else {
                        vintCheck++;
                        continue;
                    }
                }
                //check empty file
                if (vintCheck == vlstWarningServiceExcels.size()) {
                    baseUploadEntity.setMessage(I18N.get(Constants.EMPTY_FILE_ERROR));
                    return baseUploadEntity;
                }
            }
            vreader.writeResultFile(vstrOriginalName, vstrResultFilePath, WarningServiceExcel.class, vlstWarningServiceExcels, I18N.get("common.success.status"));

            //set cac thong tin tra ve frontend
            baseUploadEntity.setResultFileName(vstrResultFileName);
            baseUploadEntity.setSumSuccessfulRecord(vintSumSuccessfulRecord);
            baseUploadEntity.setSumRecord(vintSumRecord);

        } catch (
                Exception e) {
            baseUploadEntity.setMessage(e.getMessage());
            LOGGER.error(e.getMessage(), e);
        }
        return baseUploadEntity;
    }

    /**
     * check trung 2 can nguong canh bao
     *
     * @param plstWarningConfigs
     * @param warningConfig
     * @return
     */
    private boolean checkDuplicateBound(List<WarningConfig> plstWarningConfigs, WarningConfig warningConfig) {
        boolean vchkCheck = false;
        if (!DataUtil.isNullOrEmpty(plstWarningConfigs)) {
            for (WarningConfig warningConfigCheck : plstWarningConfigs) {
                if (warningConfigCheck.getWfvalue().equals(warningConfig.getWfvalue()) || warningConfigCheck.getWovalue().equals(warningConfig.getWovalue())
                        || warningConfigCheck.getWfvalue().equals(warningConfig.getWovalue()) || warningConfigCheck.getWovalue().equals(warningConfig.getWfvalue())) {
                    vchkCheck = true;
                    break;
                }
            }
        }
        return vchkCheck;
    }

    /**
     * check can trong can
     *
     * @param plstWarningConfigs
     * @param pdblFromValue
     * @param pdblToValue
     * @return
     */
    private boolean checkBoundInBound(List<WarningConfig> plstWarningConfigs, Double pdblFromValue, Double pdblToValue) {
        boolean vchkCheck = false;
        if (!DataUtil.isNullOrEmpty(plstWarningConfigs)) {
            for (WarningConfig warningConfigCheck : plstWarningConfigs) {
                if ((pdblFromValue > warningConfigCheck.getWfvalue() && pdblFromValue < warningConfigCheck.getWovalue())
                        || (pdblToValue > warningConfigCheck.getWfvalue() && pdblToValue < warningConfigCheck.getWovalue())
                        || (pdblFromValue < warningConfigCheck.getWfvalue() && pdblToValue > warningConfigCheck.getWovalue())) {
                    vchkCheck = true;
                    break;
                }
            }
        }
        return vchkCheck;
    }

    @Override
    public ByteArrayInputStream getTemplate() throws Exception {
        return excelWarningConfigService.writeTemplate();
    }


//    @Override
//    public String saveWarningSend(WarningConfig warningConfig, StaffDTO user) throws Exception {
//
//
//        return null;
//    }
}
