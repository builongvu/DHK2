package vn.vissoft.dashboard.services.impl;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.vissoft.dashboard.config.I18N;
import vn.vissoft.dashboard.dto.ServiceScoreDTO;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.excel.BaseUploadEntity;
import vn.vissoft.dashboard.dto.excel.ServiceScoreExcel;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.helper.excelreader.ExcelReader;
import vn.vissoft.dashboard.model.*;
import vn.vissoft.dashboard.repo.*;
import vn.vissoft.dashboard.services.*;

import javax.transaction.Transactional;
import java.io.File;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Transactional
@Service
public class ServiceScoreServiceImpl implements ServiceScoreService {

    @Autowired
    private ServiceScoreRepo serviceScoreRepo;

    @Autowired
    private PlanMonthlyService planMonthlyService;

    @Autowired
    private ChannelRepo channelRepo;

    @Autowired
    private PartnerRepo partnerRepo;

    @Autowired
    private VdsStaffRepo vdsStaffRepo;

    @Autowired
    private ServiceRepo serviceRepo;

    @Autowired
    private ActionAuditService actionAuditService;

    @Autowired
    private ActionDetailService actionDetailService;

    @Autowired
    private ActionAuditRepo actionAuditRepo;

    @Autowired
    private StaffRepo staffRepo;

    @Value("${server.tomcat.basedir}")
    private String mstrUploadPath;

    public static final Logger LOGGER = LogManager.getLogger(ServiceScoreServiceImpl.class);

    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * set du lieu cho bang danh sach chi tieu trong tab trong so chi tieu
     *
     * @param serviceScoreDTO de lay serviceId
     * @return list danh sach chi tieu
     * @throws Exception
     * @author HungNN
     * @version 1.0
     * @since 09/12/2019
     */
    @Override
    public List<ServiceScoreDTO> getServiceTable(ServiceScoreDTO serviceScoreDTO) throws Exception {
        List<ServiceScoreDTO> scoreDTOList = new ArrayList<>();
        List<Object[]> list = serviceScoreRepo.getServiceScore(serviceScoreDTO);
        String nameService;
        String nameStaff;
        String vdsChannelCode;
        String staffCode;
        String shopCode;
        double score;
        double scoreMax;
        String status;
        String nameShop;
        Date fromDate;
        Date toDate;
        long id;
        long serviceId;
        if (!DataUtil.isNullOrEmpty(list)) {
            for (Object[] object : list) {
                ServiceScoreDTO serviceScoreDTO1 = new ServiceScoreDTO();
                id = DataUtil.safeToLong(object[0]);
                serviceId = DataUtil.safeToLong(object[1]);
                vdsChannelCode = String.valueOf(object[2]);
                shopCode = String.valueOf(object[3]);
                staffCode = String.valueOf(object[4]);
                status = String.valueOf(object[5]);
                score = (double) object[6];
                fromDate = (Date) object[7];
                toDate = (Date) object[8];
                scoreMax = (double) object[9];
                nameShop = object[10] == null ? null : String.valueOf(object[10]);
                nameService = object[11] == null ? null : String.valueOf(object[11]);
                nameStaff = object[12] == null ? null : String.valueOf(object[12]);

                serviceScoreDTO1.setId(id);
                serviceScoreDTO1.setServiceId(serviceId);
                serviceScoreDTO1.setVdsChannelCode(vdsChannelCode);
                serviceScoreDTO1.setShopCode(shopCode);
                serviceScoreDTO1.setStaffCode(staffCode);
                serviceScoreDTO1.setShopName(nameShop);
                serviceScoreDTO1.setNameService(nameService);
                serviceScoreDTO1.setStaffName(nameStaff);
                serviceScoreDTO1.setScore(score);
                serviceScoreDTO1.setStatus(status);
                serviceScoreDTO1.setScoreMax(scoreMax);
                if (fromDate != null) {
                    serviceScoreDTO1.setFromDate(fromDate.getTime());
                }
                if (toDate != null) {
                    serviceScoreDTO1.setToDate(toDate.getTime());
                }
                scoreDTOList.add(serviceScoreDTO1);
            }
        }
        return scoreDTOList;
    }

    /**
     * them moi vao bang service_score
     *
     * @param serviceScoreDTO, staffDTO
     * @return thong boa
     * @throws Exception
     * @author HungNN
     * @version 1.0
     * @since 06/12/2019
     */
    @Override
    public String addServiceScore(ServiceScoreDTO serviceScoreDTO, StaffDTO staffDTO) throws Exception {
        String message;
        boolean checkDuplicate = serviceScoreRepo.checkDuplicate(serviceScoreDTO, "insert");
        if (checkDuplicate == true) {
            long millis = System.currentTimeMillis();
            ActionAudit actionAudit = new ActionAudit();
            actionAudit.setActionCode(Constants.CREATE);
            actionAudit.setActionDateTime(new java.sql.Date(millis));
            actionAudit.setPkID(serviceScoreDTO.getId());
            actionAudit.setObjectCode(Constants.SERVICE_SCORES);
            actionAudit.setUser(staffDTO.getStaffCode());
            actionAudit.setShopCode(staffDTO.getShopCode());
            actionAuditRepo.save(actionAudit);

            actionDetailService.createActionDetail(Constants.SERVICE_SCORE.VDS_CHANNEL_CODE, serviceScoreDTO.getVdsChannelCode(), actionAudit.getId(), null);
            actionDetailService.createActionDetail(Constants.SERVICE_SCORE.SHOP_CODE, serviceScoreDTO.getShopCode().trim(), actionAudit.getId(), null);
            actionDetailService.createActionDetail(Constants.SERVICE_SCORE.STAFF_CODE, (DataUtil.isNullOrEmpty(serviceScoreDTO.getStaffCode()) ? null : serviceScoreDTO.getStaffCode().trim()), actionAudit.getId(), null);
            actionDetailService.createActionDetail(Constants.SERVICE_SCORE.SERVICE_ID, String.valueOf(serviceScoreDTO.getServiceId()), actionAudit.getId(), null);
            actionDetailService.createActionDetail(Constants.SERVICE_SCORE.SCORE, String.valueOf(serviceScoreDTO.getScore()), actionAudit.getId(), null);
            actionDetailService.createActionDetail(Constants.SERVICE_SCORE.STATUS, serviceScoreDTO.getStatus(), actionAudit.getId(), null);
            if (!DataUtil.isNullOrZero(serviceScoreDTO.getFromDate())) {
                Date fromDate = new Date(serviceScoreDTO.getFromDate());
                actionDetailService.createActionDetail(Constants.SERVICE_SCORE.FROM_DATE, dateFormat.format(fromDate), actionAudit.getId(), null);
            }
            if (!DataUtil.isNullOrZero(serviceScoreDTO.getToDate())) {
                Date toDate = new Date(serviceScoreDTO.getToDate());
                actionDetailService.createActionDetail(Constants.SERVICE_SCORE.TO_DATE, (DataUtil.isNullOrEmpty(toDate.toString()) ? null : dateFormat.format(toDate)), actionAudit.getId(), null);
            }
            actionDetailService.createActionDetail(Constants.SERVICE_SCORE.SCORE_MAX, String.valueOf(serviceScoreDTO.getScoreMax()), actionAudit.getId(), null);
        }
        message = serviceScoreRepo.addServiceScore(serviceScoreDTO);

        return message;
    }

    /**
     * list ten nhan vien theo shop_code truyen vao, neu ko co shop_code, lay tat ca nhan vien
     *
     * @param shopCode de lay shopCode
     * @return ten nhan vien
     * @throws Exception
     * @author HungNN
     * @version 1.0
     * @since 09/12/2019
     */
    @Override
    public List<ServiceScoreDTO> getStaffByCondition(String shopCode, String pstrVdsChannelCode) throws Exception {
        List<Object[]> staffList = staffRepo.findNameStaff(shopCode, pstrVdsChannelCode);
        List<ServiceScoreDTO> scoreDTOList = new ArrayList<>();
        String staffCode;
        String name;

        for (Object[] staff : staffList) {
            ServiceScoreDTO serviceScoreDTO = new ServiceScoreDTO();
            name = String.valueOf(staff[0]);
            staffCode = String.valueOf(staff[1]);

            serviceScoreDTO.setStaffName(name);
            serviceScoreDTO.setStaffCode(staffCode);
            scoreDTOList.add(serviceScoreDTO);
        }
        return scoreDTOList;
    }

    /**
     * list ten don vi cho combobox don vi
     *
     * @return list ten don vi
     * @throws Exception
     * @author HungNN
     * @version 1.0
     * @since 11/12/2019
     */
    @Override
    public List<ManageInfoPartner> getUnitChild(String type) throws Exception {
        List<Object[]> lstObj = partnerRepo.getPartner();
        List<ManageInfoPartner> list = new ArrayList<>();
        String vdsChannelCode;
        String shopCode;
        String shopName;
        String parentShopCode;
        String assignKpi;
        String status;
        String user;
        String shortName;
        for (Object[] objects : lstObj) {
            ManageInfoPartner manageInfoPartner = new ManageInfoPartner();
            if(type.equals(Constants.VDS_STAFF.VDS_STAFF)){
                vdsChannelCode = (String) objects[1];
            } else {
                vdsChannelCode = String.valueOf(objects[1]);
            }
            shopCode = String.valueOf(objects[2]);
            shopName = String.valueOf(objects[3]);
            shortName = String.valueOf(objects[4]);
            parentShopCode = String.valueOf(objects[5]);
            assignKpi = String.valueOf(objects[6]);
 //           activeDate = (Date) objects[7];
            status = String.valueOf(objects[9]);
            user = String.valueOf(objects[10]);

            manageInfoPartner.setVdsChannelCode(vdsChannelCode);
            manageInfoPartner.setShopCode(shopCode);
            manageInfoPartner.setParentShopCode(parentShopCode);
            manageInfoPartner.setShopName(shopName);
            manageInfoPartner.setAssignKpi(assignKpi);
//            manageInfoPartner.setActiveDate(activeDate);
            manageInfoPartner.setStatus(status);
            manageInfoPartner.setUser(user);
            manageInfoPartner.setShortName(shortName);
            list.add(manageInfoPartner);
        }
        return list;
    }

    /**
     * cap nhat bang serivce_score
     *
     * @param serviceScoreDTO, staffDTO
     * @return thong bao
     * @throws Exception
     * @author HungNN
     * @version 1.0
     * @since 05/12/2019
     */
    @Override
    public String updateServiceScore(ServiceScoreDTO serviceScoreDTO, Long id, StaffDTO staffDTO) throws Exception {
        ServiceScore serviceScore = serviceScoreRepo.getOne(serviceScoreDTO.getId());
        boolean checkDuplicate = serviceScoreRepo.checkDuplicate(serviceScoreDTO, "update");
        String message;
        if (checkDuplicate == true) {
            // luu action-audit
            long millis = System.currentTimeMillis();
            ActionAudit actionAudit = new ActionAudit();
            actionAudit.setActionCode(Constants.EDIT);
            actionAudit.setActionDateTime(new java.sql.Date(millis));
            actionAudit.setPkID(serviceScoreDTO.getId());
            actionAudit.setObjectCode(Constants.SERVICE_SCORES);
            actionAudit.setUser(staffDTO.getStaffCode());
            actionAudit.setShopCode(staffDTO.getShopCode());
            actionAuditRepo.save(actionAudit);
            //luu action_detail
            if (Double.compare(serviceScoreDTO.getScore(), serviceScore.getScore()) != 0) {
                //               if (serviceScoreDTO.getScore().equals("0"))
                //                   actionDetailService.createActionDetail(Constants.SERVICE_SCORE.SCORE, "0", actionAudit.getId(), String.valueOf(serviceScore.getScore()));
//                else if (serviceScoreExcel.getScore().equals("1"))
//                    actionDetailService.createActionDetail(Constants.SERVICE_SCORE.SCORE, "1", actionAudit.getId(), String.valueOf(serviceScore.getScore()));
                //  else
                actionDetailService.createActionDetail(Constants.SERVICE_SCORE.SCORE, String.valueOf(serviceScoreDTO.getScore()), actionAudit.getId(), String.valueOf(serviceScore.getScore()));
            }
            if (!serviceScoreDTO.getStatus().equals(serviceScore.getStatus())) {
                actionDetailService.createActionDetail(Constants.SERVICE_SCORE.STATUS, serviceScoreDTO.getStatus(), actionAudit.getId(), serviceScore.getStatus());
            }
            if (serviceScoreDTO.getFromDate() != serviceScore.getFromDate().getTime()) {
                Date date = new Date(serviceScoreDTO.getFromDate());
                actionDetailService.createActionDetail(Constants.SERVICE_SCORE.FROM_DATE, dateFormat.format(date), actionAudit.getId(), serviceScore.getFromDate().toString());
            }
            if (serviceScoreDTO.getToDate() != null && serviceScore.getToDate() == null) {
                Date date2 = new Date(serviceScoreDTO.getToDate());
                actionDetailService.createActionDetail(Constants.SERVICE_SCORE.TO_DATE, dateFormat.format(date2), actionAudit.getId(), null);
            } else if (serviceScoreDTO.getToDate() == null && serviceScore.getToDate() != null) {
                actionDetailService.createActionDetail(Constants.SERVICE_SCORE.TO_DATE, null, actionAudit.getId(), dateFormat.format(serviceScore.getToDate()));
            } else if (serviceScoreDTO.getToDate() != null && serviceScore.getToDate() != null) {
                if (serviceScoreDTO.getToDate() != serviceScore.getToDate().getTime()) {
                    Date date2 = new Date(serviceScoreDTO.getToDate());
                    actionDetailService.createActionDetail(Constants.SERVICE_SCORE.TO_DATE, (DataUtil.isNullOrEmpty(date2.toString()) ? null : dateFormat.format(serviceScoreDTO.getToDate())), actionAudit.getId(), (DataUtil.isNullObject(serviceScore.getToDate()) ? null : String.valueOf(serviceScore.getToDate())));
                }
            } else {}
            if (Double.compare(serviceScoreDTO.getScoreMax(), serviceScore.getScoreMax()) != 0) {
                actionDetailService.createActionDetail(Constants.SERVICE_SCORE.SCORE_MAX, String.valueOf(serviceScoreDTO.getScoreMax()), actionAudit.getId(), String.valueOf(serviceScore.getScoreMax()));
            }
        }
        message = serviceScoreRepo.updateServiceScore(serviceScoreDTO, id);

        return message;
    }

    /**
     * upload file trong so chi tieu
     *
     * @param file
     * @param staffDTO
     * @return
     * @throws Exception
     * @author DatNT
     * @since 10/12/2019
     */
    @Override
    public BaseUploadEntity upload(MultipartFile file, StaffDTO staffDTO) throws Exception {
        int vintCheck = 0;
        int vintSumSuccessfulRecord = 0;
        int vintSumRecord = 0;
        Long vlngServiceId;
        Long vlngServiceScoreId;
        java.sql.Date vdtToDate;
        Date vdtToDateUtil;
        Date vdtFromDateUtil;
        java.sql.Date vdtFromDate;
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String[] vstrChannelCodes;
        String vstrChannelCode = null;

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
            ExcelReader<ServiceScoreExcel> vreader = new ExcelReader<>();
            List<ServiceScoreExcel> vlstServiceScoreExcels = vreader.read(vstrOriginalName, ServiceScoreExcel.class);

            if (DataUtil.isNullOrEmpty(vlstServiceScoreExcels)) {
                baseUploadEntity.setMessage(I18N.get(Constants.EMPTY_FILE_ERROR));
                return baseUploadEntity;
            } else {
                planMonthlyService.checkTheSameRecord(vlstServiceScoreExcels);

                for (ServiceScoreExcel serviceScoreExcel : vlstServiceScoreExcels) {
                    if (!DataUtil.isNullObject(serviceScoreExcel)) {
                        vintSumRecord++;
                        if (DataUtil.isNullOrEmpty(serviceScoreExcel.getError())) {
                            vstrChannelCodes = serviceScoreExcel.getVdsChannelCode().split("-");
                            vstrChannelCode = vstrChannelCodes[0];
                            serviceScoreExcel.setVdsChannelCode(vstrChannelCode);
                            if (!channelRepo.checkExistedGroupChannelCode(serviceScoreExcel.getVdsChannelCode().trim())) {
                                serviceScoreExcel.setError(I18N.get("common.excel.active.error") + ". " + I18N.get("common.excel.column.error") + " B ");
                            } else if (!partnerRepo.checkExistedUnitCode(serviceScoreExcel.getShopCode().trim())) {
                                serviceScoreExcel.setError(I18N.get("common.excel.active.error") + ". " + I18N.get("common.excel.column.error") + " C ");
                            } else if (!partnerRepo.checkExistedUnitInChannel(serviceScoreExcel.getShopCode().trim(), serviceScoreExcel.getVdsChannelCode().trim())) {
                                serviceScoreExcel.setError(I18N.get("common.excel.shop.in.channel.error") + I18N.get("common.excel.column.error") + " C ");
                            } else if (!DataUtil.isNullOrEmpty(serviceScoreExcel.getStaffCode()) && !vdsStaffRepo.checkExistedStaffCode(serviceScoreExcel.getStaffCode().trim())) {
                                serviceScoreExcel.setError(I18N.get("common.excel.active.error") + ". " + I18N.get("common.excel.column.error") + " D ");
                            } else if (!DataUtil.isNullOrEmpty(serviceScoreExcel.getStaffCode()) && !vdsStaffRepo.checkExistedStaffInShop(serviceScoreExcel.getStaffCode().trim(), serviceScoreExcel.getShopCode().trim())) {
                                serviceScoreExcel.setError(I18N.get("common.excel.staff.in.unit.error"));
                            } else if (!serviceRepo.checkExistedServiceCode(serviceScoreExcel.getServiceCode().trim())) {
                                serviceScoreExcel.setError(I18N.get("common.excel.active.error") + ". " + I18N.get("common.excel.column.error") + " E ");
                            } else if (!DataUtil.isNullOrEmpty(serviceScoreExcel.getFromDate()) && !DataUtil.compareFullDateFileAndSystem(serviceScoreExcel.getFromDate().trim())) {
                                serviceScoreExcel.setError(I18N.get("common.excel.error.fromDate"));
                            } else if (!DataUtil.isNullOrEmpty(serviceScoreExcel.getToDate()) && !DataUtil.compareFromDateAndToDate(serviceScoreExcel.getFromDate().trim(), serviceScoreExcel.getToDate().trim())) {
                                serviceScoreExcel.setError(I18N.get("common.excel.error.fromDateToDate") + ". " + I18N.get("common.excel.column.error") + " H ");
                            }
                        }
                        if (DataUtil.isNullOrEmpty(serviceScoreExcel.getError())) {
                            vlngServiceId = serviceRepo.findServiceIdByCode(serviceScoreExcel.getServiceCode().trim());
                            vlngServiceScoreId=serviceScoreRepo.findServiceScoreIdByCondition(vlngServiceId,vstrChannelCode.trim(),serviceScoreExcel.getShopCode().trim(),
                                    (DataUtil.isNullObject(serviceScoreExcel.getStaffCode()) ? null : serviceScoreExcel.getStaffCode().trim()));
                            try {
                                vdtFromDateUtil = formatter.parse(serviceScoreExcel.getFromDate().trim());
                                vdtFromDate = new java.sql.Date(vdtFromDateUtil.getTime());
                                if (!DataUtil.isNullObject(serviceScoreExcel.getToDate())) {
                                    vdtToDateUtil = formatter.parse(serviceScoreExcel.getToDate().trim());
                                    vdtToDate = new java.sql.Date(vdtToDateUtil.getTime());
                                } else {
                                    vdtToDate = null;
                                }

                                ServiceScore serviceScore = serviceScoreRepo.findServiceScoreFromFile(serviceScoreExcel);
                                if (DataUtil.isNullObject(serviceScore)) {
                                    serviceScoreRepo.persist(new ServiceScore(vlngServiceId, vstrChannelCode.trim(), serviceScoreExcel.getShopCode().trim()
                                            , (DataUtil.isNullObject(serviceScoreExcel.getStaffCode()) ? null : serviceScoreExcel.getStaffCode().trim()), "1", serviceScoreExcel.getScore(),
                                            vdtFromDate, vdtToDate, serviceScoreExcel.getScoreMax()));

                                    ActionAudit actionAudit = actionAuditService.log(Constants.SERVICE_SCORES, Constants.CREATE, staffDTO.getStaffCode(), vlngServiceScoreId, staffDTO.getShopCode());
                                    actionDetailService.createActionDetail(Constants.SERVICE_SCORE.VDS_CHANNEL_CODE, vstrChannelCode, actionAudit.getId(), null);
                                    actionDetailService.createActionDetail(Constants.SERVICE_SCORE.SHOP_CODE, serviceScoreExcel.getShopCode().trim(), actionAudit.getId(), null);
                                    actionDetailService.createActionDetail(Constants.SERVICE_SCORE.STAFF_CODE, (DataUtil.isNullOrEmpty(serviceScoreExcel.getStaffCode()) ? null : serviceScoreExcel.getStaffCode().trim()), actionAudit.getId(), null);
                                    actionDetailService.createActionDetail(Constants.SERVICE_SCORE.SERVICE_ID, String.valueOf(vlngServiceId), actionAudit.getId(), null);
                                    if (serviceScoreExcel.getScore().equals(Constants.SERVICE_SCORE_SCORE.ZERO))
                                        actionDetailService.createActionDetail(Constants.SERVICE_SCORE.SCORE, Constants.PARAM_STATUS_0, actionAudit.getId(), null);
                                    else if (serviceScoreExcel.getScore().equals(Constants.SERVICE_SCORE_SCORE.ONE))
                                        actionDetailService.createActionDetail(Constants.SERVICE_SCORE.SCORE, Constants.PARAM_STATUS, actionAudit.getId(), null);
                                    else
                                        actionDetailService.createActionDetail(Constants.SERVICE_SCORE.SCORE, String.valueOf(serviceScoreExcel.getScore()), actionAudit.getId(), null);

                                    actionDetailService.createActionDetail(Constants.SERVICE_SCORE.FROM_DATE, serviceScoreExcel.getFromDate().trim(), actionAudit.getId(), null);
                                    actionDetailService.createActionDetail(Constants.SERVICE_SCORE.TO_DATE, (DataUtil.isNullOrEmpty(serviceScoreExcel.getToDate()) ? null : serviceScoreExcel.getToDate().trim()), actionAudit.getId(), null);
                                    if (serviceScoreExcel.getScoreMax().equals(Constants.SERVICE_SCORE_SCORE.ZERO))
                                        actionDetailService.createActionDetail(Constants.SERVICE_SCORE.SCORE_MAX, Constants.PARAM_STATUS_0, actionAudit.getId(), null);
                                    else if (serviceScoreExcel.getScoreMax().equals(Constants.SERVICE_SCORE_SCORE.ONE))
                                        actionDetailService.createActionDetail(Constants.SERVICE_SCORE.SCORE_MAX, Constants.PARAM_STATUS, actionAudit.getId(), null);
                                    else
                                        actionDetailService.createActionDetail(Constants.SERVICE_SCORE.SCORE_MAX, String.valueOf(serviceScoreExcel.getScoreMax()), actionAudit.getId(), null);

                                    vintSumSuccessfulRecord++;
                                } else {

                                    ActionAudit actionAudit = actionAuditService.log(Constants.SERVICE_SCORES, Constants.EDIT, staffDTO.getStaffCode(), vlngServiceScoreId, staffDTO.getShopCode());
                                    if (!serviceScoreExcel.getScore().equals(serviceScore.getScore())) {
                                        if (serviceScoreExcel.getScore().equals(Constants.SERVICE_SCORE_SCORE.ZERO))
                                            actionDetailService.createActionDetail(Constants.SERVICE_SCORE.SCORE, Constants.PARAM_STATUS_0, actionAudit.getId(), String.valueOf(serviceScore.getScore()));
                                        else if (serviceScoreExcel.getScore().equals(Constants.SERVICE_SCORE_SCORE.ONE))
                                            actionDetailService.createActionDetail(Constants.SERVICE_SCORE.SCORE, Constants.PARAM_STATUS, actionAudit.getId(), String.valueOf(serviceScore.getScore()));
                                        else
                                            actionDetailService.createActionDetail(Constants.SERVICE_SCORE.SCORE, String.valueOf(serviceScoreExcel.getScore()), actionAudit.getId(), String.valueOf(serviceScore.getScore()));
                                    }
                                    if (!DataUtil.isNullOrEmpty(serviceScoreExcel.getFromDate())&&!DataUtil.isNullObject(serviceScore.getFromDate())){
                                        if(!serviceScoreExcel.getFromDate().trim().equals(serviceScore.getFromDate().toString()))
                                            actionDetailService.createActionDetail(Constants.SERVICE_SCORE.FROM_DATE, serviceScoreExcel.getFromDate().trim(), actionAudit.getId(), serviceScore.getFromDate().toString());
                                    }
                                    else if(!DataUtil.isNullOrEmpty(serviceScoreExcel.getFromDate())&&DataUtil.isNullObject(serviceScore.getFromDate())){
                                        actionDetailService.createActionDetail(Constants.SERVICE_SCORE.FROM_DATE, serviceScoreExcel.getFromDate().trim(), actionAudit.getId(), null);

                                    }
                                    else if(DataUtil.isNullOrEmpty(serviceScoreExcel.getFromDate())&&!DataUtil.isNullObject(serviceScore.getFromDate())){
                                        actionDetailService.createActionDetail(Constants.SERVICE_SCORE.FROM_DATE, null, actionAudit.getId(), serviceScore.getFromDate().toString());
                                    }

                                    if (!DataUtil.isNullOrEmpty(serviceScoreExcel.getToDate())&&!DataUtil.isNullObject(serviceScore.getToDate())){
                                        if(!serviceScoreExcel.getToDate().trim().equals(serviceScore.getToDate().toString()))
                                            actionDetailService.createActionDetail(Constants.SERVICE_SCORE.TO_DATE, serviceScoreExcel.getToDate(), actionAudit.getId(), String.valueOf(serviceScore.getToDate()));
                                    }
                                    else if(!DataUtil.isNullOrEmpty(serviceScoreExcel.getToDate())&&DataUtil.isNullObject(serviceScore.getToDate())){
                                        actionDetailService.createActionDetail(Constants.SERVICE_SCORE.TO_DATE, serviceScoreExcel.getToDate(), actionAudit.getId(), null);

                                    }
                                    else if(DataUtil.isNullOrEmpty(serviceScoreExcel.getToDate())&&!DataUtil.isNullObject(serviceScore.getToDate())){
                                        actionDetailService.createActionDetail(Constants.SERVICE_SCORE.TO_DATE, null, actionAudit.getId(), String.valueOf(serviceScore.getToDate()));

                                    }

                                    if (!serviceScoreExcel.getScoreMax().equals(serviceScore.getScoreMax())) {
                                        if (serviceScoreExcel.getScoreMax().equals(Constants.SERVICE_SCORE_SCORE.ZERO))
                                            actionDetailService.createActionDetail(Constants.SERVICE_SCORE.SCORE_MAX, Constants.PARAM_STATUS_0, actionAudit.getId(), String.valueOf(serviceScore.getScoreMax()));
                                        else if (serviceScoreExcel.getScoreMax().equals(Constants.SERVICE_SCORE_SCORE.ONE))
                                            actionDetailService.createActionDetail(Constants.SERVICE_SCORE.SCORE_MAX, Constants.PARAM_STATUS, actionAudit.getId(), String.valueOf(serviceScore.getScoreMax()));
                                        else
                                            actionDetailService.createActionDetail(Constants.SERVICE_SCORE.SCORE_MAX, String.valueOf(serviceScoreExcel.getScoreMax()), actionAudit.getId(), String.valueOf(serviceScore.getScoreMax()));
                                    }

                                    serviceScore.setScore(serviceScoreExcel.getScore());
                                    serviceScore.setFromDate(vdtFromDate);
                                    serviceScore.setToDate(vdtToDate);
                                    serviceScoreRepo.update(serviceScore);

                                    vintSumSuccessfulRecord++;
                                }
                            } catch (Exception e) {
                                LOGGER.error(e.getMessage(), e);
                                serviceScoreExcel.setError(e.getMessage());
                            }
                        }
                    } else {
                        vintCheck++;
                        continue;
                    }
                }
                //check empty file
                if (vintCheck == vlstServiceScoreExcels.size()) {
                    baseUploadEntity.setMessage(I18N.get(Constants.EMPTY_FILE_ERROR));
                    return baseUploadEntity;
                }
            }
            vreader.writeResultFile(vstrOriginalName, vstrResultFilePath, ServiceScoreExcel.class, vlstServiceScoreExcels, I18N.get("common.success.status"));

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

    @Override
    public boolean validateGroupService(ServiceScoreDTO serviceScoreDTO) {
        boolean vblnCheck = false;
        if (!DataUtil.isNullOrZero(serviceScoreDTO.getScore())) {
            Pattern patternScore = Pattern.compile("^(0(\\.\\d+)?|1(\\.0+)?)$");
            Matcher matcherScore = patternScore.matcher(String.valueOf(serviceScoreDTO.getScore()));
            if (matcherScore.matches())
                vblnCheck = true;
        }
        return vblnCheck;
    }

}
