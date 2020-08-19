package vn.vissoft.dashboard.services.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.web.multipart.MultipartFile;
import vn.vissoft.dashboard.config.I18N;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.excel.BaseUploadEntity;
import vn.vissoft.dashboard.dto.excel.VttGroupChannelSaleExcel;
import vn.vissoft.dashboard.dto.excel.VttPositionExcel;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.helper.excelreader.ExcelReader;
import vn.vissoft.dashboard.model.ActionAudit;
import vn.vissoft.dashboard.model.VttGroupChannelSale;
import vn.vissoft.dashboard.model.VttPosition;
import vn.vissoft.dashboard.repo.VttGroupChannelRepo;
import vn.vissoft.dashboard.repo.VttGroupChannelSaleRepo;
import vn.vissoft.dashboard.services.ActionAuditService;
import vn.vissoft.dashboard.services.ActionDetailService;
import vn.vissoft.dashboard.services.PlanMonthlyService;
import vn.vissoft.dashboard.services.VttGroupChannelSaleService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.RollbackException;
import javax.transaction.Transactional;
import javax.transaction.TransactionalException;
import java.io.File;
import java.sql.Timestamp;
import java.util.List;

@Transactional
@Service
public class VttGroupChannelSaleServiceImpl implements VttGroupChannelSaleService {

    private static final Logger LOGGER = LogManager.getLogger(VttGroupChannelSaleServiceImpl.class);

    @PersistenceContext
    EntityManager entityManager;

    @Value("${server.tomcat.basedir}")
    private String mstrUploadPath;

    @Autowired
    private VttGroupChannelSaleRepo vttGroupChannelSaleRepo;

    @Autowired
    private ActionAuditService actionAuditService;

    @Autowired
    private ActionDetailService actionDetailService;

    @Autowired
    private PlanMonthlyService planMonthlyService;

    @Autowired
    private VttGroupChannelRepo vttGroupChannelRepo;

    /**
     * them moi vtt_group_channel_sale
     *
     * @param vttGroupChannelSale
     * @param staffDTO
     * @return
     * @throws Exception
     */
    @Override
    public String persist(VttGroupChannelSale vttGroupChannelSale, StaffDTO staffDTO) throws Exception {
        long vlngTime = System.currentTimeMillis();
        Timestamp timestamp = new Timestamp(vlngTime);
        if (!DataUtil.isNullObject(vttGroupChannelSale)) {
            String vstrCheck = checkDuplicateVttGroupChannelSale(vttGroupChannelSale.getGroupChannelCode(), vttGroupChannelSale.getChannelTypeId());
            if (DataUtil.isNullOrEmpty(vstrCheck)) {
                vttGroupChannelSale.setChannelTypeId(vttGroupChannelSale.getChannelTypeId());
                vttGroupChannelSale.setCreateDate(timestamp);
                vttGroupChannelSale.setCreateUser(staffDTO.getStaffCode());
                vttGroupChannelSale.setStatus(Constants.PARAM_STATUS);
                vttGroupChannelSaleRepo.persist(vttGroupChannelSale);
                saveAction(vttGroupChannelSale, staffDTO);
            } else {
                return vstrCheck;
            }
        }
        return null;
    }

    @Override
    public BaseUploadEntity upload(MultipartFile file, StaffDTO user) throws Exception {
        int vintCheck = 0;
        int vintSumSuccessfulRecord = 0;
        int vintSumRecord = 0;
        long mil = System.currentTimeMillis();
        Timestamp timestamp = new Timestamp(mil);

        BaseUploadEntity baseUploadEntity = new BaseUploadEntity();
        String vstrOriginalName = file.getOriginalFilename();
        String vstrPath = mstrUploadPath;

        String lastFourChar = vstrOriginalName.substring(vstrOriginalName.length() - 4);
        if (!"xlsx".equals(lastFourChar)) {
            baseUploadEntity.setMessage(I18N.get(Constants.NOT_XLSX));
            return baseUploadEntity;
        }

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
            ExcelReader<VttGroupChannelSaleExcel> vreader = new ExcelReader<>();
            List<VttGroupChannelSaleExcel> vlstVttGroupChannelSales = vreader.read(vstrOriginalName, VttGroupChannelSaleExcel.class);

            if (DataUtil.isNullOrEmpty(vlstVttGroupChannelSales)) {
                baseUploadEntity.setMessage(I18N.get(Constants.EMPTY_FILE_ERROR));
                return baseUploadEntity;
            } else {
                planMonthlyService.checkTheSameRecord(vlstVttGroupChannelSales);

                for (VttGroupChannelSaleExcel vttGroupChannelSaleExcel : vlstVttGroupChannelSales) {
                    if (!DataUtil.isNullObject(vttGroupChannelSaleExcel)) {
                        vintSumRecord++;
                        if (DataUtil.isNullOrEmpty(vttGroupChannelSaleExcel.getError())) {
                            if (!vttGroupChannelRepo.checkExistedGroupChannelCode(vttGroupChannelSaleExcel.getGroupChannelCode().trim())) {
                                vttGroupChannelSaleExcel.setError(I18N.get("common.excel.active.error") + ". " + I18N.get("common.excel.column.error") + " B ");
                            }
                        }
                        if (DataUtil.isNullOrEmpty(vttGroupChannelSaleExcel.getError())) {
                            try {
                                String vstrCheckDuplicate = checkDuplicateVttGroupChannelSale(vttGroupChannelSaleExcel.getGroupChannelCode(), vttGroupChannelSaleExcel.getChannelTypeId());
                                if (vstrCheckDuplicate == null) {
                                    VttGroupChannelSale vttGroupChannelSale = new VttGroupChannelSale(Constants.PARAM_STATUS, vttGroupChannelSaleExcel.getChannelTypeId(), user.getStaffCode(),
                                            timestamp, vttGroupChannelSaleExcel.getGroupChannelCode().trim().toUpperCase());

                                    vttGroupChannelSaleRepo.persist(vttGroupChannelSale);
                                    saveAction(vttGroupChannelSale, user);

                                    vintSumSuccessfulRecord++;
                                } else {
                                    vttGroupChannelSaleExcel.setError(I18N.get("common.servicescore.duplicate"));
                                }
                            } catch (Exception e) {
                                LOGGER.error(e.getMessage(), e);
                                vttGroupChannelSaleExcel.setError(e.getMessage());
                            }
                        }
                    } else {
                        vintCheck++;
                        continue;
                    }
                }
                //check empty file
                if (vintCheck == vlstVttGroupChannelSales.size()) {
                    baseUploadEntity.setMessage(I18N.get(Constants.EMPTY_FILE_ERROR));
                    return baseUploadEntity;
                }
            }
            vreader.writeResultFile(vstrOriginalName, vstrResultFilePath, VttGroupChannelSaleExcel.class, vlstVttGroupChannelSales, I18N.get("common.success.status"));

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
     * check trung vtt group channel sale
     *
     * @param pstrGroupChannelCode
     * @param pstrChannelTypeId
     * @return
     */
    private String checkDuplicateVttGroupChannelSale(String pstrGroupChannelCode, Long pstrChannelTypeId) throws Exception {
        VttGroupChannelSale vttGroupChannelSale = vttGroupChannelSaleRepo.findByGroupChannelCodeAndChannelTypeId(pstrGroupChannelCode.trim(), pstrChannelTypeId);
        if (DataUtil.isNullObject(vttGroupChannelSale))
            return null;
        else return Constants.DUPLICATED_VTT_GROUP_CHANNEL_SALE;
    }

    /**
     * luu lich su them vtt group channel sale
     *
     * @param vttGroupChannelSale
     * @param staffDTO
     */
    private void saveAction(VttGroupChannelSale vttGroupChannelSale, StaffDTO staffDTO) {
        ActionAudit actionAudit = actionAuditService.log(Constants.VTT_GROUP_CHANNEL_SALE, Constants.CREATE, staffDTO.getStaffCode(), vttGroupChannelSale.getId(), staffDTO.getShopCode());
        actionDetailService.createActionDetail(Constants.VTT_GROUP_CHANNEL_SALES.GROUP_CHANNEL_CODE, vttGroupChannelSale.getGroupChannelCode().trim(), actionAudit.getId(), null);
        actionDetailService.createActionDetail(Constants.VTT_GROUP_CHANNEL_SALES.CHANNEL_TYPE_ID, vttGroupChannelSale.getChannelTypeId().toString(), actionAudit.getId(), null);
        actionDetailService.createActionDetail(Constants.VTT_GROUP_CHANNEL_SALES.STATUS, Constants.PARAM_STATUS, actionAudit.getId(), null);

    }
}
