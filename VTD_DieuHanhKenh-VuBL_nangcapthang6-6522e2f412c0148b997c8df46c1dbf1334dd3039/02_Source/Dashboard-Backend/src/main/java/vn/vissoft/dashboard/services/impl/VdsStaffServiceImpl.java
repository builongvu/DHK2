package vn.vissoft.dashboard.services.impl;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.vissoft.dashboard.config.I18N;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.VdsStaffDTO;
import vn.vissoft.dashboard.dto.excel.BaseUploadEntity;
import vn.vissoft.dashboard.dto.excel.VdsStaffExcel;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.helper.excelreader.ExcelReader;
import vn.vissoft.dashboard.model.ActionAudit;
import vn.vissoft.dashboard.model.VdsStaff;
import vn.vissoft.dashboard.repo.PartnerRepo;
import vn.vissoft.dashboard.repo.VdsStaffRepo;
import vn.vissoft.dashboard.services.ActionAuditService;
import vn.vissoft.dashboard.services.ActionDetailService;
import vn.vissoft.dashboard.services.PlanMonthlyService;
import vn.vissoft.dashboard.services.VdsStaffService;

import javax.xml.crypto.Data;
import java.io.File;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class VdsStaffServiceImpl implements VdsStaffService {

    @Autowired
    private VdsStaffRepo vdsStaffRepo;

    @Autowired
    private PlanMonthlyService planMonthlyService;

    @Autowired
    private PartnerRepo partnerRepo;

    @Autowired
    private ActionAuditService actionAuditService;

    @Autowired
    private ActionDetailService actionDetailService;

    public static final Logger LOGGER = LogManager.getLogger(ServiceScoreServiceImpl.class);

    @Value("${server.tomcat.basedir}")
    private String mstrUploadPath;

    /**
     * lay ra du lieu cho bang nhan vien cua vds theo dieu kien truyen vao
     *
     * @param vdsStaffDTO
     * @return list
     * @throws Exception
     * @author HungNN
     * @version 1.0
     * @since 27/12/2019
     */
    @Override
    public List<VdsStaffDTO> getStaffVds(VdsStaffDTO vdsStaffDTO) throws Exception {
        List<Object[]> vdsStaffDTOList = vdsStaffRepo.getStaffVDS(vdsStaffDTO);
        List<VdsStaffDTO> staffDTOList = new ArrayList<>();
        for (Object[] object : vdsStaffDTOList) {
            Long id = DataUtil.safeToLong(object[0]);
            String staffCode = (String) object[1];
            String staffName = (String) object[2];
            String phoneNumber = (String) object[3];
            String email = (String) object[4];
            String shopCode = (String) object[5];
            String vdsChannelName = (String) object[6];
            String shopName = (String) object[7];
            String staffType = (String) object[8];
            String shopWarning = (String) object[9];
            String vdsChannelCode = (String) object[10];

            VdsStaffDTO vdsStaffDTO1 = new VdsStaffDTO();
            vdsStaffDTO1.setId(id);
            vdsStaffDTO1.setStaffCode(staffCode);
            vdsStaffDTO1.setStaffName(staffName);
            vdsStaffDTO1.setPhoneNumber(phoneNumber);
            vdsStaffDTO1.setEmail(email);
            vdsStaffDTO1.setShopCode(shopCode);
            vdsStaffDTO1.setVdsChannelName(vdsChannelName);
            vdsStaffDTO1.setShopName(shopName);
            vdsStaffDTO1.setStaffType(staffType);
            vdsStaffDTO1.setShopWarning(shopWarning);
            vdsStaffDTO1.setVdsChannelCode(vdsChannelCode);
            staffDTOList.add(vdsStaffDTO1);
        }
        return staffDTOList;
    }

    /**
     * cap nhat du lieu cho bang vds_staff
     *
     * @param vdsStaffDTO
     * @return thong bao
     * @throws Exception
     * @author HungNN
     * @version 1.0
     * @since 27/12/2019
     */
    @Override
    public String updateVdsStaff(VdsStaffDTO vdsStaffDTO, StaffDTO staffDTO) throws Exception {
        String message = null;
        boolean checkDuplicate = vdsStaffRepo.checkDuplicate(vdsStaffDTO);
        VdsStaff vdsStaff = vdsStaffRepo.getOne(vdsStaffDTO.getId());
        if (!DataUtil.isNullOrEmpty(vdsStaffDTO.getEmail())) {
            // truong hop nhap nhieu dau ; o cuoi
            String phoneBefore = vdsStaffDTO.getPhoneNumber();
            String[] strings = phoneBefore.split(";");
            String s = "";
            for (String s1 : strings) {
                s = s + ";" + s1;
            }
            String phoneAfter = s.substring(1);
            vdsStaffDTO.setPhoneNumber(phoneAfter);
            boolean checkEmail = vdsStaffRepo.checkDuplicateEmail(vdsStaffDTO.getEmail());
            String emailOld = null;
            String emailNew = vdsStaffDTO.getEmail();
            if (!DataUtil.isNullOrEmpty(vdsStaff.getEmail())) {
                emailOld = vdsStaff.getEmail();
            }
            String phoneNum = vdsStaffDTO.getPhoneNumber();
            if (checkDuplicate == false) {
                if (!DataUtil.isNullOrEmpty(emailOld)) {
                    if (emailOld.equals(emailNew) && DataUtil.isValidMultiPhoneNumber(phoneNum)) {
                        // luu action audit truoc khi cap nhat
                        ActionAudit actionAudit = actionAuditService.log(Constants.VDS_STAFF.VDS_STAFF, Constants.EDIT, staffDTO.getStaffCode(), vdsStaffDTO.getId(), vdsStaffDTO.getShopCode());
                        // luu action_detail truoc khi cap nhat
                        if (!DataUtil.isNullOrEmpty(vdsStaffDTO.getStaffName())) {
                            if (!vdsStaffDTO.getStaffName().trim().equals(vdsStaff.getStaffName())) {
                                actionDetailService.createActionDetail(Constants.VDS_STAFF.STAFF_NAME, vdsStaffDTO.getStaffName().trim(), actionAudit.getId(), vdsStaff.getStaffName());
                            }
                        }
                        if (!DataUtil.isNullOrEmpty(vdsStaffDTO.getShopCode())) {
                            if (!vdsStaffDTO.getShopCode().trim().equals(vdsStaff.getShopCode())) {
                                actionDetailService.createActionDetail(Constants.WARNINGRECEIVE.SHOPCODE, vdsStaffDTO.getShopCode().trim(), actionAudit.getId(), vdsStaff.getShopCode());
                            }
                        }
                        if (!DataUtil.isNullOrEmpty(phoneNum)) {
                            if (!vdsStaffDTO.getPhoneNumber().trim().equals(vdsStaff.getPhoneNumber())) {
                                actionDetailService.createActionDetail(Constants.VDS_STAFF.PHONE_NUMBER, vdsStaffDTO.getPhoneNumber().trim(), actionAudit.getId(), vdsStaff.getPhoneNumber());
                            }
                        }
                        if (!DataUtil.isNullOrEmpty(emailNew)) {
                            if (!vdsStaffDTO.getEmail().trim().equals(vdsStaff.getEmail())) {
                                actionDetailService.createActionDetail(Constants.VDS_STAFF.EMAIL, vdsStaffDTO.getEmail().trim(), actionAudit.getId(), vdsStaff.getEmail());
                            }
                        }
                        if (!DataUtil.isNullOrEmpty(vdsStaffDTO.getStatus())) {
                            if (!vdsStaffDTO.getStatus().trim().equals(vdsStaff.getStatus())) {
                                actionDetailService.createActionDetail(Constants.SERVICE_SCORE.STATUS, vdsStaffDTO.getStatus().trim(), actionAudit.getId(), vdsStaff.getStatus());
                            }
                        }
                        if (!DataUtil.isNullOrEmpty(vdsStaffDTO.getStaffType())) {
                            if (!vdsStaffDTO.getStaffType().trim().equals(vdsStaff.getStaffType())) {
                                actionDetailService.createActionDetail(Constants.VDS_STAFF.STAFF_TYPE, vdsStaffDTO.getStaffType(), actionAudit.getId(), vdsStaff.getStaffType());
                            }
                        }
                        if (!DataUtil.isNullOrEmpty(vdsStaffDTO.getShopWarning())) {
                            if (!vdsStaffDTO.getShopWarning().trim().equals(vdsStaff.getShopWarning())) {
                                actionDetailService.createActionDetail(Constants.VDS_STAFF.SHOP_WARNING, vdsStaffDTO.getShopWarning().trim(), actionAudit.getId(), vdsStaff.getShopWarning());
                            }
                        }
                        vdsStaffDTO.setStatus(vdsStaff.getStatus());
                        String vdsChannelCode = partnerRepo.getVdsChannelByShopCode(vdsStaffDTO.getShopCode());
                        if (!DataUtil.isNullOrEmpty(vdsStaffDTO.getVdsChannelCode())) {
                            if (!vdsStaffDTO.getVdsChannelCode().equals(vdsStaff.getVdsChannelCode())) {
                                actionDetailService.createActionDetail(Constants.WARNINGSEND.VDS_CHANNEL_CODE, vdsStaffDTO.getVdsChannelCode().trim(), actionAudit.getId(), vdsChannelCode);
                            }
                        } else {
                            if (!DataUtil.isNullOrEmpty(vdsStaff.getVdsChannelCode())) {
                                actionDetailService.createActionDetail(Constants.WARNINGSEND.VDS_CHANNEL_CODE, null, actionAudit.getId(), vdsStaff.getVdsChannelCode());
                            }
                        }
                        vdsStaffRepo.updateStaffVds(vdsStaffDTO, staffDTO, Constants.UPDATE);
                        message = Constants.WARNINGSEND.SUCCESS;
                    } else if (!emailOld.equals(emailNew) && checkEmail == false && DataUtil.isValidMultiPhoneNumber(phoneNum)) {
                        ActionAudit actionAudit = actionAuditService.log(Constants.VDS_STAFF.VDS_STAFF, Constants.EDIT, staffDTO.getStaffCode(), vdsStaffDTO.getId(), vdsStaffDTO.getShopCode());
                        // luu action_detail truoc khi cap nhat
                        if (!DataUtil.isNullOrEmpty(vdsStaffDTO.getStaffName())) {
                            if (!vdsStaffDTO.getStaffName().trim().equals(vdsStaff.getStaffName())) {
                                actionDetailService.createActionDetail(Constants.VDS_STAFF.STAFF_NAME, vdsStaffDTO.getStaffName().trim(), actionAudit.getId(), vdsStaff.getStaffName());
                            }
                        }
                        if (!DataUtil.isNullOrEmpty(vdsStaffDTO.getShopCode())) {
                            if (!vdsStaffDTO.getShopCode().trim().equals(vdsStaff.getShopCode())) {
                                actionDetailService.createActionDetail(Constants.WARNINGRECEIVE.SHOPCODE, vdsStaffDTO.getShopCode().trim(), actionAudit.getId(), vdsStaff.getShopCode());
                            }
                        }
                        if (!DataUtil.isNullOrEmpty(phoneNum)) {
                            if (!vdsStaffDTO.getPhoneNumber().trim().equals(vdsStaff.getPhoneNumber())) {
                                actionDetailService.createActionDetail(Constants.VDS_STAFF.PHONE_NUMBER, vdsStaffDTO.getPhoneNumber().trim(), actionAudit.getId(), vdsStaff.getPhoneNumber());
                            }
                        }
                        if (!DataUtil.isNullOrEmpty(emailNew)) {
                            if (!vdsStaffDTO.getEmail().trim().equals(vdsStaff.getEmail())) {
                                actionDetailService.createActionDetail(Constants.VDS_STAFF.EMAIL, vdsStaffDTO.getEmail().trim(), actionAudit.getId(), vdsStaff.getEmail());
                            }
                        }
                        if (!DataUtil.isNullOrEmpty(vdsStaffDTO.getStatus())) {
                            if (!vdsStaffDTO.getStatus().trim().equals(vdsStaff.getStatus())) {
                                actionDetailService.createActionDetail(Constants.SERVICE_SCORE.STATUS, vdsStaffDTO.getStatus().trim(), actionAudit.getId(), vdsStaff.getStatus());
                            }
                        }
                        if (!DataUtil.isNullOrEmpty(vdsStaffDTO.getStaffType())) {
                            if (!vdsStaffDTO.getStaffType().trim().equals(vdsStaff.getStaffType())) {
                                actionDetailService.createActionDetail(Constants.VDS_STAFF.STAFF_TYPE, vdsStaffDTO.getStaffType(), actionAudit.getId(), vdsStaff.getStaffType());
                            }
                        }
                        if (!DataUtil.isNullOrEmpty(vdsStaffDTO.getShopWarning())) {
                            if (!vdsStaffDTO.getShopWarning().trim().equals(vdsStaff.getShopWarning())) {
                                actionDetailService.createActionDetail(Constants.VDS_STAFF.SHOP_WARNING, vdsStaffDTO.getShopWarning().trim(), actionAudit.getId(), vdsStaff.getShopWarning());
                            }
                        }
                        vdsStaffDTO.setStatus(vdsStaff.getStatus());
                        String vdsChannelCode = partnerRepo.getVdsChannelByShopCode(vdsStaffDTO.getShopCode());
                        if (!DataUtil.isNullOrEmpty(vdsStaffDTO.getVdsChannelCode())) {
                            if (!vdsStaffDTO.getVdsChannelCode().equals(vdsStaff.getVdsChannelCode())) {
                                actionDetailService.createActionDetail(Constants.WARNINGSEND.VDS_CHANNEL_CODE, vdsStaffDTO.getVdsChannelCode().trim(), actionAudit.getId(), vdsChannelCode);
                            }
                        } else {
                            if (!DataUtil.isNullOrEmpty(vdsStaff.getVdsChannelCode())) {
                                actionDetailService.createActionDetail(Constants.WARNINGSEND.VDS_CHANNEL_CODE, null, actionAudit.getId(), vdsStaff.getVdsChannelCode());
                            }
                        }
                        vdsStaffRepo.updateStaffVds(vdsStaffDTO, staffDTO, Constants.UPDATE);
                        message = Constants.WARNINGSEND.SUCCESS;
                    } else if (!emailOld.equals(emailNew) && checkEmail == true) {
                        message = Constants.VDS_STAFF.DUPLICATE_EMAIL;
                    } else {
                        message = Constants.VDS_STAFF.PHONE_NUMBER_IS_NOT_VALID;
                    }
                }
            } else {
                message = Constants.WARNINGSEND.DUPLICATE;
            }
        }
        return message;
    }

    /**
     * thuc hien doi trang thai nhan vien
     *
     * @param vdsStaffDTO
     * @return thong bao
     * @throws Exception
     * @author HungNN
     * @version 1.0
     * @since 27/12/2019
     */
    @Override
    public String deleteStaffVds(VdsStaffDTO vdsStaffDTO, StaffDTO staffDTO) throws Exception {
        String message;
        VdsStaff vdsStaff = vdsStaffRepo.getOne(vdsStaffDTO.getId());
        ActionAudit actionAudit = actionAuditService.log(Constants.VDS_STAFF.VDS_STAFF, Constants.EDIT, staffDTO.getStaffCode(), vdsStaffDTO.getId(), vdsStaff.getShopCode());
        actionDetailService.createActionDetail(Constants.GROUPSERVICES.STATUS, Constants.PARAM_STATUS_DELETE, actionAudit.getId(), Constants.PARAM_STATUS);
        if (vdsStaff.getShopCode() != null) {
            vdsStaffDTO.setShopCode(vdsStaff.getShopCode());
        }
        if (vdsStaff.getStaffName() != null) {
            vdsStaffDTO.setStaffName(vdsStaff.getStaffName());
        }
        if (vdsStaff.getPhoneNumber() != null) {
            vdsStaffDTO.setPhoneNumber(vdsStaff.getPhoneNumber());
        }
        if (vdsStaff.getEmail() != null) {
            vdsStaffDTO.setEmail(vdsStaff.getEmail());
        }
        if (vdsStaff.getStaffType() != null) {
            vdsStaffDTO.setStaffType(vdsStaff.getStaffType());
        }
        if (vdsStaff.getShopWarning() != null) {
            vdsStaffDTO.setShopWarning(vdsStaff.getShopWarning());
        }
        vdsStaffDTO.setStatus(Constants.PARAM_STATUS_DELETE);
        vdsStaffRepo.updateStaffVds(vdsStaffDTO, staffDTO, Constants.DELETE);
        message = Constants.WARNINGSEND.SUCCESS;
        return message;
    }

    /**
     * them moi nhan vien vao vds_staff
     *
     * @param vdsStaffDTO, staffDTO
     * @return thong bao
     * @throws Exception
     * @author HungNN
     * @version 1.0
     * @since 27/12/2019
     */
    @Override
    public String createStaffVds(VdsStaffDTO vdsStaffDTO, StaffDTO staffDTO) throws Exception {
        String message = null;
        String staffCode = vdsStaffDTO.getStaffCode();
        if (vdsStaffRepo.checkDuplicate(vdsStaffDTO) == false) {
            boolean staffInStaff = vdsStaffRepo.checkStaffInStaff(staffCode);
            boolean staffInVdsStaff = vdsStaffRepo.checkStaffInVdsStaff(staffCode);

            // check Duplicate Email
            if (!DataUtil.isNullOrEmpty(vdsStaffDTO.getEmail())) {
                boolean checkEmail = vdsStaffRepo.checkDuplicateEmail(vdsStaffDTO.getEmail());
                if (checkEmail == false && DataUtil.isValidMultiPhoneNumber(vdsStaffDTO.getPhoneNumber())) {
                    if (staffInStaff == true && staffInVdsStaff == false) {
                        // them vds_staff
                        BigInteger id = vdsStaffRepo.createVdsStaff(vdsStaffDTO, staffDTO);
                        // them action_audit
                        ActionAudit actionAudit = actionAuditService.log(Constants.VDS_STAFF.VDS_STAFF, Constants.CREATE, staffDTO.getStaffCode(), id.longValue(), vdsStaffDTO.getShopCode());
                        //them action_detail
                        if (!DataUtil.isNullOrEmpty(vdsStaffDTO.getVdsChannelCode())) {
                            actionDetailService.createActionDetail(Constants.SERVICE_SCORE.VDS_CHANNEL_CODE, vdsStaffDTO.getVdsChannelCode().trim(), actionAudit.getId(), null);
                        }
                        if (!DataUtil.isNullOrEmpty(vdsStaffDTO.getShopCode())) {
                            actionDetailService.createActionDetail(Constants.SERVICE_SCORE.SHOP_CODE, vdsStaffDTO.getShopCode().trim(), actionAudit.getId(), null);
                        }
                        if (!DataUtil.isNullOrEmpty(vdsStaffDTO.getStaffCode())) {
                            actionDetailService.createActionDetail(Constants.SERVICE_SCORE.STAFF_CODE, vdsStaffDTO.getStaffCode().trim(), actionAudit.getId(), null);
                        }
                        if (!DataUtil.isNullOrEmpty(vdsStaffDTO.getStaffName())) {
                            actionDetailService.createActionDetail(Constants.VDS_STAFF.STAFF_NAME, vdsStaffDTO.getStaffName().trim(), actionAudit.getId(), null);
                        }
                        if (!DataUtil.isNullOrEmpty(vdsStaffDTO.getPhoneNumber())) {
                            actionDetailService.createActionDetail(Constants.VDS_STAFF.PHONE_NUMBER, vdsStaffDTO.getPhoneNumber().trim(), actionAudit.getId(), null);
                        }
                        if (!DataUtil.isNullOrEmpty(vdsStaffDTO.getEmail())) {
                            actionDetailService.createActionDetail(Constants.VDS_STAFF.EMAIL, vdsStaffDTO.getEmail().trim(), actionAudit.getId(), null);
                        }
                        actionDetailService.createActionDetail(Constants.SERVICE_SCORE.STATUS, Constants.PARAM_STATUS, actionAudit.getId(), null);
                        message = Constants.WARNINGSEND.SUCCESS;
                    } else if (staffInStaff == false && staffInVdsStaff == false) {
                        message = Constants.VDS_STAFF.STAFF_CODE_NOT_EXISTS;
                    } else {
                        message = Constants.VDS_STAFF.STAFF_IN_VDS_STAFF;
                    }
                } else if (checkEmail == true) {
                    message = Constants.VDS_STAFF.DUPLICATE_EMAIL;
                } else {
                    message = Constants.VDS_STAFF.PHONE_NUMBER_IS_NOT_VALID;
                }
            }
        } else
            message = Constants.WARNINGSEND.DUPLICATE;
        return message;
    }

    /**
     * upload file excel
     *
     * @param file, staffDTO
     * @return baseUploadEntity
     * @throws Exception
     * @author HungNN
     * @version 1.0
     * @since 27/12/2019
     */
    @Override
    public BaseUploadEntity upload(MultipartFile file, StaffDTO staffDTO) throws Exception {
        BaseUploadEntity baseUploadEntity = new BaseUploadEntity();
        String originName = file.getOriginalFilename();
        String path = mstrUploadPath;
        int vintCheck = 0;
        int vintSumRecord = 0;
        int vintSumSuccessfulRecord = 0;

        File customDir = new File(path);
        if (!customDir.exists()) {
            customDir.mkdir();
        }

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String string = formatter.format(new Date());
        Date date = formatter.parse(string);
        int vintIndex = originName.lastIndexOf(".");
        String resultFileName;
        if (vintIndex > -1) {
            resultFileName = originName.substring(0, originName.lastIndexOf(".")) + "_Ketqua.xlsx";
        } else {
            resultFileName = originName + "_Ketqua.xlsx";
        }
        originName = customDir.getAbsolutePath() + File.separator + originName;
        String resultFilePath = customDir.getAbsolutePath() + File.separator + resultFileName;
        planMonthlyService.saveFile(file.getInputStream(), originName);

        try {
            ExcelReader<VdsStaffExcel> excelReader = new ExcelReader();
            List<VdsStaffExcel> vdsStaffExcels = excelReader.read(originName, VdsStaffExcel.class);

            if (DataUtil.isNullOrEmpty(vdsStaffExcels)) {
                baseUploadEntity.setMessage(I18N.get(Constants.EMPTY_FILE_ERROR));
                return baseUploadEntity;
            } else {
                planMonthlyService.checkTheSameRecord(vdsStaffExcels);

                for (VdsStaffExcel vdsStaffExcel : vdsStaffExcels) {
                    if (vdsStaffExcel != null) {
                        vintSumRecord++;
                        String vdsChannelCode = null;
                        if (vdsStaffExcel.getShopCode() != null) {
                            vdsChannelCode = partnerRepo.getVdsChannelByShopCode(vdsStaffExcel.getShopCode());
                        }

                        if (DataUtil.isNullOrEmpty(vdsStaffExcel.getError())) {
                            // check xem nhan vien co ton tai trong staff ko
                            if (!DataUtil.isNullOrEmpty(vdsStaffExcel.getStaffCode()) && vdsStaffRepo.checkStaffInStaff(vdsStaffExcel.getStaffCode().trim()) == false) {
                                vdsStaffExcel.setError(I18N.get("common.excel.active.error") + ". " + I18N.get("common.excel.column.error") + " C ");
                            }
                            //shop_code phai ton tai va thuoc kenh
                            else if (!DataUtil.isNullOrEmpty(vdsStaffExcel.getShopCode()) && partnerRepo.checkShopByShopCode(vdsStaffExcel.getShopCode()) == false) {
                                vdsStaffExcel.setError(I18N.get("common.excel.active.error") + ". " + I18N.get("common.excel.column.error") + " B ");
                            }
                            // nhan vien phai thuoc don vi
                            else if (!DataUtil.isNullOrEmpty(vdsStaffExcel.getStaffCode()) && !vdsStaffRepo.checkStaffInShop(vdsStaffExcel.getStaffCode()).equals(vdsStaffExcel.getShopCode().trim())) {
                                vdsStaffExcel.setError(I18N.get("common.servicescore.staffnotinshop") + ". " + I18N.get("common.excel.column.error") + " C ");
                            }
                            // check xem ma nhan vien da ton tai trong vds_staff chua
                            else if (!DataUtil.isNullOrEmpty(vdsStaffExcel.getStaffCode()) && vdsStaffRepo.checkStaffInVdsStaff(vdsStaffExcel.getStaffCode()) == true) {
                                vdsStaffExcel.setError(I18N.get("common.table.warning.uploadduplicate") + ". " + I18N.get("common.excel.column.error") + " C ");
                            }
                            // validate nhieu so dien thoai
                            else if (DataUtil.isValidMultiPhoneNumber(vdsStaffExcel.getPhoneNumber()) == false) {
                                vdsStaffExcel.setError(I18N.get("common.excel.invalid.cell.value.error") + ". " + I18N.get("common.excel.column.error") + " F ");
                            }
                            // check duplicate email
                            else if (!DataUtil.isNullOrEmpty(vdsStaffExcel.getEmail()) && vdsStaffRepo.checkDuplicateEmail(vdsStaffExcel.getEmail())) {
                                vdsStaffExcel.setError(I18N.get("common.table.vdsstaff.duplicate.email") + ". " + I18N.get("common.excel.column.error") + " G ");
                            }
                            // validate email
                            else if (!DataUtil.isNullOrEmpty(vdsStaffExcel.getEmail()) && DataUtil.isValidEmail(vdsStaffExcel.getEmail()) == false) {
                                vdsStaffExcel.setError(I18N.get("common.excel.invalid.cell.value.error") + ". " + I18N.get("common.excel.column.error") + " G ");
                            }
                        }

                        if (DataUtil.isNullOrEmpty(vdsStaffExcel.getError())) {
                            try {
                                VdsStaff vdsStaff = vdsStaffRepo.findVdsStaffExcel(vdsStaffExcel);
                                if (vdsStaff == null) {
                                    String staffType = null;
                                    String shopWarning = null;
                                    if (!DataUtil.isNullOrEmpty(vdsStaffExcel.getStaffType())) {
                                        staffType = vdsStaffExcel.getStaffType().substring(0, 1);
                                    }
                                    if (!DataUtil.isNullOrEmpty(vdsStaffExcel.getShopWarning())) {
                                        shopWarning = vdsStaffExcel.getShopWarning().substring(0, 1);
                                    }
                                    vdsStaffRepo.persist(new VdsStaff(vdsChannelCode, vdsStaffExcel.getShopCode().trim(), vdsStaffExcel.getStaffCode().trim(), vdsStaffExcel.getStaffName().trim(),
                                            staffType, vdsStaffExcel.getPhoneNumber(), vdsStaffExcel.getEmail(), shopWarning, Constants.PARAM_STATUS, staffDTO.getStaffCode(), date));
                                    // lay id ban ghi vua them vao
                                    VdsStaffDTO vdsStaffDTO = new VdsStaffDTO();
                                    vdsStaffDTO.setStaffCode(vdsStaffExcel.getStaffCode());
                                    vdsStaffDTO.setShopCode(vdsStaffExcel.getShopCode());
                                    vdsStaffDTO.setVdsChannelCode(vdsChannelCode);
                                    Long idVdsStaff = vdsStaffRepo.getIdByKey(vdsStaffDTO, Constants.CREATE);
                                    // them action_audit va action_detail
                                    ActionAudit actionAudit = actionAuditService.log(Constants.VDS_STAFF.VDS_STAFF, Constants.CREATE, staffDTO.getStaffCode(), idVdsStaff, staffDTO.getShopCode());
                                    actionDetailService.createActionDetail(Constants.SERVICE_SCORE.VDS_CHANNEL_CODE, vdsChannelCode, actionAudit.getId(), null);
                                    actionDetailService.createActionDetail(Constants.SERVICE_SCORE.SHOP_CODE, vdsStaffExcel.getShopCode(), actionAudit.getId(), null);
                                    actionDetailService.createActionDetail(Constants.SERVICE_SCORE.STAFF_CODE, vdsStaffExcel.getStaffCode(), actionAudit.getId(), null);
                                    actionDetailService.createActionDetail(Constants.VDS_STAFF.STAFF_NAME, vdsStaffExcel.getStaffName(), actionAudit.getId(), null);
                                    actionDetailService.createActionDetail(Constants.VDS_STAFF.STAFF_TYPE, staffType, actionAudit.getId(), null);
                                    actionDetailService.createActionDetail(Constants.VDS_STAFF.PHONE_NUMBER, vdsStaffExcel.getPhoneNumber(), actionAudit.getId(), null);
                                    actionDetailService.createActionDetail(Constants.VDS_STAFF.EMAIL, vdsStaffExcel.getEmail(), actionAudit.getId(), null);
                                    actionDetailService.createActionDetail(Constants.VDS_STAFF.SHOP_WARNING, shopWarning, actionAudit.getId(), null);
                                    actionDetailService.createActionDetail(Constants.WARNINGSEND.STATUS, Constants.PARAM_STATUS, actionAudit.getId(), null);
                                    vintSumSuccessfulRecord++;
                                } else {
//                                    VdsStaffDTO vdsStaffDTO = new VdsStaffDTO();
//                                    vdsStaffDTO.setStaffCode(vdsStaffExcel.getStaffCode());
//                                    vdsStaffDTO.setShopCode(vdsStaffExcel.getShopCode());
//                                    vdsStaffDTO.setVdsChannelCode(vdsChannelCode);
//                                    Long idVdsStaff = vdsStaffRepo.getIdByKey(vdsStaffDTO,Constants.CREATE);
//                                    ActionAudit actionAudit = actionAuditService.log(Constants.VDS_STAFF.VDS_STAFF, Constants.EDIT, staffDTO.getStaffCode(), idVdsStaff, staffDTO.getShopCode());
//                                    if(!DataUtil.isNullOrEmpty(vdsStaffExcel.getShopCode())){
//                                       if(!vdsStaffExcel.getShopCode().trim().equals(vdsStaff.getShopCode())){
//                                           actionDetailService.createActionDetail(Constants.WARNINGRECEIVE.SHOPCODE, vdsStaffExcel.getShopCode(), actionAudit.getId(), vdsStaff.getShopCode());
//                                       }
//                                    }
//                                    if(!DataUtil.isNullOrEmpty(vdsStaffExcel.getStaffCode())){
//                                        if(!vdsStaffExcel.getStaffCode().trim().equals(vdsStaff.getStaffCode())){
//                                            actionDetailService.createActionDetail(Constants.SERVICE_SCORE.STAFF_CODE, vdsStaffExcel.getStaffCode(), actionAudit.getId(), vdsStaff.getStaffCode());
//                                        }
//                                    }
//                                    if(!DataUtil.isNullOrEmpty(vdsStaffExcel.getPhoneNumber())){
//                                        if(!vdsStaffExcel.getPhoneNumber().equals(vdsStaff.getPhoneNumber())){
//                                            actionDetailService.createActionDetail(Constants.VDS_STAFF.PHONE_NUMBER, vdsStaffExcel.getPhoneNumber(), actionAudit.getId(), vdsStaff.getPhoneNumber());
//                                        }
//                                    }
//                                    if(!DataUtil.isNullOrEmpty(vdsStaffExcel.getEmail())){
//                                        if(!vdsStaffExcel.getEmail().trim().equals(vdsStaff.getEmail())){
//                                            actionDetailService.createActionDetail(Constants.VDS_STAFF.EMAIL, vdsStaffExcel.getEmail(), actionAudit.getId(), vdsStaff.getEmail());
//                                        }
//                                    }
//                                    if(!DataUtil.isNullOrEmpty(vdsStaffExcel.getStaffName())){
//                                        if(!vdsStaffExcel.getStaffName().equals(vdsStaff.getStaffName())){
//                                            actionDetailService.createActionDetail(Constants.VDS_STAFF.STAFF_NAME, vdsStaffExcel.getStaffName(), actionAudit.getId(), vdsStaff.getStaffName());
//                                        }
//                                    }
//                                    if(!DataUtil.isNullOrEmpty(vdsStaffExcel.getStaffType())){
//                                        if(!vdsStaffExcel.getStaffType().equals(vdsStaff.getStaffType())){
//                                            actionDetailService.createActionDetail(Constants.VDS_STAFF.STAFF_TYPE, vdsStaffExcel.getStaffType(), actionAudit.getId(), vdsStaff.getStaffType());
//                                        }
//                                    }
//                                    vdsStaffRepo.update(vdsStaff);
//                                    vintSumSuccessfulRecord++;
                                    vdsStaffExcel.setError(I18N.get("common.table.warning.uploadduplicate"));
                                }

                            } catch (Exception e) {
                                LOGGER.error(e.getMessage(), e);
                            }
                        }
                    } else {
                        vintCheck++;
                        continue;
                    }
                }
                // check file rong
                if (vintCheck == vdsStaffExcels.size()) {
                    baseUploadEntity.setMessage(I18N.get(Constants.EMPTY_FILE_ERROR));
                    return baseUploadEntity;
                }
            }
            excelReader.writeResultFile(originName, resultFilePath, VdsStaffExcel.class, vdsStaffExcels, I18N.get("common.success.status"));

            baseUploadEntity.setResultFileName(resultFileName);
            baseUploadEntity.setSumSuccessfulRecord(vintSumSuccessfulRecord);
            baseUploadEntity.setSumRecord(vintSumRecord);
        } catch (Exception e) {
            baseUploadEntity.setMessage(e.getMessage());
            LOGGER.error(e.getMessage(), e);
        }
        return baseUploadEntity;
    }

    /**
     * check dieu kien nhan vien
     *
     * @param shopCode, staffCode
     * @return thong bao hoac ten nhan vien
     * @throws Exception
     * @author HungNN
     * @version 1.0
     * @since 27/12/2019
     */
    @Override
    public String checkCodeInStaff(String staffCode, String shopCode) throws Exception {
        String content = null;
        boolean staffInStaff = vdsStaffRepo.checkStaffInStaff(staffCode);
        boolean staffInVdsStaff = vdsStaffRepo.checkStaffInVdsStaff(staffCode);
        if (shopCode != null) {
            String staffName = vdsStaffRepo.getStaffName(staffCode, shopCode);
            if (staffName != null && staffInStaff == true && staffInVdsStaff == false) {
                content = staffName;
            } else
                content = Constants.VDS_STAFF.STAFF_NOT_IN_SHOP;
        }
        if (staffInStaff == false && staffInVdsStaff == false) {
            content = Constants.VDS_STAFF.STAFF_CODE_NOT_EXISTS;
        } else if (staffInStaff == true && staffInVdsStaff == true) {
            content = Constants.VDS_STAFF.STAFF_IN_VDS_STAFF;
        }
        return content;
    }

    /**
     * lay nhan vien trong staff va khong thuoc vds_staff
     *
     * @return list nhan vien
     * @throws Exception
     * @author HungNN
     * @since 30/12/2019
     */
    @Override
    public List<VdsStaffDTO> getStaff(VdsStaffDTO vdsStaffDTO1) throws Exception {
        List<VdsStaffDTO> list = new ArrayList<>();
        List<Object[]> staffDTOList = vdsStaffRepo.getStaffInStaff(vdsStaffDTO1.getShopCode());
        for (Object[] object : staffDTOList) {
            Long id = DataUtil.safeToLong(object[0]);
            String staffCode = DataUtil.safeToString(object[1]);
            String staffName = DataUtil.safeToString(object[2]);

            VdsStaffDTO vdsStaffDTO = new VdsStaffDTO();
            vdsStaffDTO.setId(id);
            vdsStaffDTO.setStaffCode(staffCode);
            vdsStaffDTO.setStaffName(staffName);
            list.add(vdsStaffDTO);
        }
        return list;
    }
}
