package vn.vissoft.dashboard.services.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.vissoft.dashboard.config.I18N;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.excel.BaseUploadEntity;
import vn.vissoft.dashboard.dto.excel.VttPositionExcel;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.helper.excelreader.ExcelReader;
import vn.vissoft.dashboard.model.ActionAudit;
import vn.vissoft.dashboard.model.VttPosition;
import vn.vissoft.dashboard.repo.PositionRepo;
import vn.vissoft.dashboard.repo.VttGroupChannelRepo;
import vn.vissoft.dashboard.repo.VttPositionRepo;
import vn.vissoft.dashboard.services.ActionAuditService;
import vn.vissoft.dashboard.services.ActionDetailService;
import vn.vissoft.dashboard.services.PlanMonthlyService;
import vn.vissoft.dashboard.services.VttPositionService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.io.File;
import java.sql.Timestamp;
import java.util.List;

@Transactional
@Service
public class VttPositionServiceImpl implements VttPositionService {

    private static final Logger LOGGER = LogManager.getLogger(VttPositionServiceImpl.class);

    @Value("${server.tomcat.basedir}")
    private String mstrUploadPath;

    @Autowired
    private VttPositionRepo vttPositionRepo;

    @Autowired
    private VttGroupChannelRepo vttGroupChannelRepo;

    @Autowired
    private PositionRepo positionRepo;

    @Autowired
    private ActionAuditService actionAuditService;

    @Autowired
    private ActionDetailService actionDetailService;

    @Autowired
    private PlanMonthlyService planMonthlyService;


    @PersistenceContext
    EntityManager entityManager;

    /**
     * them moi vttposition
     *
     * @param vttPosition
     * @param staffDTO
     * @return
     * @throws Exception
     */
    @Override
    public String persist(VttPosition vttPosition, StaffDTO staffDTO) throws Exception {
        long vlngTime = System.currentTimeMillis();
        Timestamp timestamp = new Timestamp(vlngTime);
        if (!DataUtil.isNullObject(vttPosition)) {
            String vstrCheck = checkDuplicateVttPosition(vttPosition.getGroupChannelCode(), vttPosition.getPositionCode());
            if (DataUtil.isNullOrEmpty(vstrCheck)) {
                vttPosition.setStatus(1d);
                vttPosition.setPositionCode(vttPosition.getPositionCode().trim());
                vttPosition.setCreateDate(timestamp);
                vttPosition.setUser(staffDTO.getStaffCode());
                vttPositionRepo.persist(vttPosition);
                saveAction(vttPosition, staffDTO);
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
            ExcelReader<VttPositionExcel> vreader = new ExcelReader<>();
            List<VttPositionExcel> vlstVttPositions = vreader.read(vstrOriginalName, VttPositionExcel.class);

            if (DataUtil.isNullOrEmpty(vlstVttPositions)) {
                baseUploadEntity.setMessage(I18N.get(Constants.EMPTY_FILE_ERROR));
                return baseUploadEntity;
            } else {
                planMonthlyService.checkTheSameRecord(vlstVttPositions);

                for (VttPositionExcel vttPositionExcel : vlstVttPositions) {
                    if (!DataUtil.isNullObject(vttPositionExcel)) {
                        vintSumRecord++;
                        if (DataUtil.isNullOrEmpty(vttPositionExcel.getError())) {
                            if (!vttGroupChannelRepo.checkExistedGroupChannelCode(vttPositionExcel.getGroupChannelCode().trim())) {
                                vttPositionExcel.setError(I18N.get("common.excel.active.error") + ". " + I18N.get("common.excel.column.error") + " B ");
                            } else if (!positionRepo.checkExistedPositionCode(vttPositionExcel.getPositionCode().trim())) {
                                vttPositionExcel.setError(I18N.get("common.excel.active.error") + ". " + I18N.get("common.excel.column.error") + " C ");
                            }
                        }
                        if (DataUtil.isNullOrEmpty(vttPositionExcel.getError())) {
                            try {
//
                                String vstrCheckDuplicate = checkDuplicateVttPosition(vttPositionExcel.getGroupChannelCode(), vttPositionExcel.getPositionCode());
                                if (vstrCheckDuplicate == null) {
                                    VttPosition vttPositionInserted = new VttPosition(vttPositionExcel.getPositionCode().trim().toUpperCase(),
                                            timestamp, user.getStaffCode(), vttPositionExcel.getGroupChannelCode().trim().toUpperCase(), 1d);

                                    vttPositionRepo.persist(vttPositionInserted);
                                    saveAction(vttPositionInserted, user);

                                    vintSumSuccessfulRecord++;
                                } else {
                                    vttPositionExcel.setError(I18N.get("common.servicescore.duplicate"));
                                }
                            } catch (Exception e) {
                                LOGGER.error(e.getMessage(), e);
                                vttPositionExcel.setError(e.getMessage());
                            }
                        }
                    } else {
                        vintCheck++;
                        continue;
                    }
                }
                //check empty file
                if (vintCheck == vlstVttPositions.size()) {
                    baseUploadEntity.setMessage(I18N.get(Constants.EMPTY_FILE_ERROR));
                    return baseUploadEntity;
                }
            }
            vreader.writeResultFile(vstrOriginalName, vstrResultFilePath, VttPositionExcel.class, vlstVttPositions, I18N.get("common.success.status"));

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
     * check trung vtt position
     *
     * @param pstrGroupChannelCode
     * @return
     */
    private String checkDuplicateVttPosition(String pstrGroupChannelCode, String pstrPositionCode) throws Exception {
        VttPosition vttPosition = vttPositionRepo.findByGroupChannelCodeAndPositionCode(pstrGroupChannelCode.trim(), pstrPositionCode.trim());
        if (DataUtil.isNullObject(vttPosition))
            return null;
        else return Constants.DUPLICATED_VTT_POSITION;
    }

    /**
     * luu lich su them vtt position
     *
     * @param vttPosition
     * @param staffDTO
     */
    private void saveAction(VttPosition vttPosition, StaffDTO staffDTO) {
        ActionAudit actionAudit = actionAuditService.log(Constants.VTT_POSITION, Constants.CREATE, staffDTO.getStaffCode(), vttPosition.getId(), staffDTO.getShopCode());
        actionDetailService.createActionDetail(Constants.VTT_POSITIONS.GROUP_CHANNEL_CODE, vttPosition.getGroupChannelCode().trim(), actionAudit.getId(), null);
        actionDetailService.createActionDetail(Constants.VTT_POSITIONS.POSITION_CODE, vttPosition.getPositionCode().trim(), actionAudit.getId(), null);
        actionDetailService.createActionDetail(Constants.VTT_POSITIONS.STATUS, Constants.PARAM_STATUS, actionAudit.getId(), null);

    }
}
