package vn.vissoft.dashboard.services.impl;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.vissoft.dashboard.config.I18N;
import vn.vissoft.dashboard.dto.PlanMonthlyDTO;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.excel.BaseExcelEntity;
import vn.vissoft.dashboard.dto.excel.BaseUploadEntity;
import vn.vissoft.dashboard.dto.excel.LevelServiceExcel;
import vn.vissoft.dashboard.dto.excel.VTTServiceExcel;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.helper.excelreader.ExcelReader;
import vn.vissoft.dashboard.mapper.BaseMapper;
import vn.vissoft.dashboard.model.PlanMonthly;
import vn.vissoft.dashboard.model.PlanQuarterly;
import vn.vissoft.dashboard.model.PlanYearly;
import vn.vissoft.dashboard.model.VdsStaff;
import vn.vissoft.dashboard.repo.*;
import vn.vissoft.dashboard.services.PlanMonthlyService;
import vn.vissoft.dashboard.services.ServiceService;

import java.math.BigInteger;
import java.io.*;
import java.sql.Timestamp;
import java.text.DecimalFormat;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class PlanMonthlyServiceImpl implements PlanMonthlyService {

    @Value("${server.tomcat.basedir}")
    private String mstrUploadPath;
    private DecimalFormat decimalFormat = new DecimalFormat("#.##");
    public static final Logger LOGGER = LogManager.getLogger(PlanMonthlyServiceImpl.class);
    private BaseMapper<PlanMonthly, PlanMonthlyDTO> mapper = new BaseMapper<PlanMonthly, PlanMonthlyDTO>(PlanMonthly.class, PlanMonthlyDTO.class);

    @Autowired
    private PlanMonthlyRepo planMonthlyRepo;

    @Autowired
    private ChannelRepo channelRepo;

    @Autowired
    private ServiceRepo serviceRepo;

    @Autowired
    private ServiceService serviceService;

    @Autowired
    private PartnerRepo partnerRepo;

    @Autowired
    private PlanMonthlyHisRepo planMonthlyHisRepo;

    @Autowired
    private PlanQuarterlyRepo planQuarterlyRepo;

    @Autowired
    private PlanYearlyRepo planYearlyRepo;

    @Autowired
    private ApParamRepo apParamRepo;

    @Autowired
    private VdsStaffRepo vdsStaffRepo;

    @Autowired
    private CycleRepo cycleRepo;

    @Autowired
    private StaffRepo staffRepo;

    @Override
    @Transactional
    public List<PlanMonthlyDTO> getPlanMonthlyByCondition(PlanMonthlyDTO planMonthlyDTO, StaffDTO staffDTO) throws Exception {
        return planMonthlyRepo.findPlanMonthlysByCondition(planMonthlyDTO, staffDTO);
    }

    @Override
    @Transactional
    public List<PlanMonthlyDTO> getPlanMonthlyByReportSql(PlanMonthlyDTO planMonthlyDTO) throws Exception {
        return planMonthlyRepo.findPlanMonthlysByReportSql(planMonthlyDTO);
    }

    @Override
    @Transactional
    public BigInteger countPlanMonthlysByCondition(PlanMonthlyDTO planMonthlyDTO, StaffDTO staffDTO) throws Exception {
        return planMonthlyRepo.countPlanMonthlysByCondition(planMonthlyDTO, staffDTO);
    }

    /**
     * update f_schedule theo dieu kien truyen vao
     *
     * @param (plstPlanMonthly): truyen vao 1 list PlanMonthly
     * @return (neu co): so ban ghi duoc cap nhat
     * @author: hungnn
     * @version: 1.0 (sau khi duoc sua doi se tang them)
     * @since: 21019/10/16
     */
    @Override
    @Transactional
    public String updatePlanMonthly(List<PlanMonthly> plstPlanMonthly, StaffDTO staffDTO) throws Exception {
        int vintCount = 0;
        String vstrMessage = "";
        Date vdtCurrentDate = new Date();
        long vlngTime = vdtCurrentDate.getTime();
        Long vlngPrdId;
        Long vlngServiceId;
        String vstrChannelCode;
        String vstrShopCode;
        String vstrStaffCode;
        Timestamp timestamp = new Timestamp(vlngTime);
        Map<Long, Double> hmpSchedule = new HashMap<>();
        Map<Long, Double> hmpChangeSchedule = new HashMap<>();
        Map<Long, Double> hmpSumScheduleChild = new HashMap<>();
        double dblSchelduleOfShop = 0d;
        double dblSumSchelduleChildShop = 0d;
        double dblChangeSchedule = 0d;
        String serviceName = null;
        boolean checkShopOfProvince = checkShopCodeOfProvince(staffDTO);

        List<Long> idList = plstPlanMonthly.stream()
                .map(PlanMonthly::getId)
                .collect(Collectors.toList());

        for (PlanMonthly plan : plstPlanMonthly) {
            dblSchelduleOfShop = planMonthlyRepo.getScheduleOfShop(plan, staffDTO);
            if (!hmpSchedule.containsKey(plan.getServiceId()))
                hmpSchedule.put(plan.getServiceId(), dblSchelduleOfShop);
            if (!hmpChangeSchedule.containsKey(plan.getServiceId())) {
                dblChangeSchedule = plan.getfSchedule();
                hmpChangeSchedule.put(plan.getServiceId(), dblChangeSchedule);
            } else {
                dblChangeSchedule = hmpChangeSchedule.get(plan.getServiceId()) + plan.getfSchedule();
                hmpChangeSchedule.put(plan.getServiceId(), dblChangeSchedule);
            }

            dblSumSchelduleChildShop = planMonthlyRepo.getSumScheduleChildShop(plan, idList, staffDTO);
            if (!hmpSumScheduleChild.containsKey(plan.getServiceId()))
                hmpSumScheduleChild.put(plan.getServiceId(), dblSumSchelduleChildShop);
        }

        for (PlanMonthly planMonthly1 : plstPlanMonthly) {
            if (!DataUtil.isNullObject(planMonthly1)) {
                for (Map.Entry<Long, Double> entry : hmpSchedule.entrySet()) {
                    if (entry.getKey() == planMonthly1.getServiceId() && hmpSumScheduleChild.containsKey(entry.getKey()) && hmpChangeSchedule.containsKey(entry.getKey())) {
                        if (checkShopOfProvince && Double.sum(hmpSumScheduleChild.get(entry.getKey()), hmpChangeSchedule.get(entry.getKey())) < entry.getValue()) {
                            serviceName = serviceRepo.findNameById(planMonthly1.getServiceId());
                            vstrMessage = I18N.get("schedule.update.error") + " " + serviceName;
                            break;
                        }

                        if (DataUtil.isNullOrEmpty(vstrMessage)) {
                            vlngPrdId = planMonthly1.getPrdId();
                            vlngServiceId = planMonthly1.getServiceId();
                            vstrChannelCode = planMonthly1.getVdsChannelCode();
                            vstrShopCode = planMonthly1.getShopCode();
                            vstrStaffCode = planMonthly1.getStaffCode();

                            PlanMonthly planHis = planMonthlyHisRepo.findPlanMonthlyHis(vlngPrdId, vlngServiceId, vstrChannelCode, vstrShopCode, vstrStaffCode);
                            if (!DataUtil.isNullObject(planHis)) {
                                planMonthlyHisRepo.savePlanMonthlyHis(planHis);
                                vintCount += planMonthlyRepo.updatePlanMonthly(planMonthly1.getId(), planMonthly1.getfSchedule(), timestamp);
                                break;
                            }
                        }
                    }
                }

            }

        }
        if (vstrMessage.length() != 0)
            return vstrMessage;
        else return String.valueOf(vintCount);
    }

    /**
     * xoa planmonthly
     *
     * @param (plstPlanMonthly): truyen vao 1 list PlanMonthly
     * @return (neu co): so ban ghi duoc cap nhat
     * @author: hungnn
     * @version: 1.0 (sau khi duoc sua doi se tang them)
     * @since: 08/11/2019
     */
    @Override
    @Transactional
    public int deletePlanMonthly(List<PlanMonthly> plstPlanMonthly) throws Exception {
        int vintCount = 0;
        Long vlngPrdId;
        Long vlngServiceId;
        String vstrChannelCode;
        String vstrShopCode;
        String vstrStaffCode;

        for (PlanMonthly planMonthly : plstPlanMonthly) {
            if (!DataUtil.isNullObject(planMonthly)) {
                vlngPrdId = planMonthly.getPrdId();
                vlngServiceId = planMonthly.getServiceId();
                vstrChannelCode = planMonthly.getVdsChannelCode();
                vstrShopCode = planMonthly.getShopCode();
                vstrStaffCode = planMonthly.getStaffCode();
                PlanMonthly planHis = planMonthlyHisRepo.findPlanMonthlyHis(vlngPrdId, vlngServiceId, vstrChannelCode, vstrShopCode, vstrStaffCode);
                if (!DataUtil.isNullObject(planHis)) {
                    planMonthlyHisRepo.savePlanMonthlyHis(planHis);
                    vintCount += planMonthlyRepo.deletePlanMonthly(planMonthly.getId());
                }

            }
        }
        return vintCount;
    }

    /**
     * upload file  ke hoach
     *
     * @param file
     * @param pclsClazz
     * @return BaseUploadEntity
     * @throws Exception
     * @author DatNT
     * version 1.1
     * @since 13/09/2019
     */
    @Override
    public BaseUploadEntity upload(MultipartFile file, Class pclsClazz, StaffDTO staffDTO) throws Exception {

        int vintCheck = 0;
        int vintSumSuccessfulRecord = 0;
        int vintSumRecord = 0;
        Date vdtCurrentDate = new Date();
        long vlngTime = vdtCurrentDate.getTime();
        Timestamp timestamp = new Timestamp(vlngTime);
        Long vlngServiceId = null;
        Long vlngPrdId = null;
        String vstrShopCode = null;
        String vstrChannelCode = null;
        String vstrMessage = null;
        List<PlanMonthly> vlstMonthlPlans = new ArrayList<>();
        List<PlanQuarterly> vlstQuarterPlans = new ArrayList<>();
        List<PlanYearly> vlstYearPlans = new ArrayList<>();

        Map<String, Integer> counts = new HashMap<>();
        boolean checkShopOfProvince = checkShopCodeOfProvince(staffDTO);

        VdsStaff vdsStaff = vdsStaffRepo.findVdsStaffByCondition(staffDTO.getVdsChannelCode(), staffDTO.getShopCode(), staffDTO.getStaffCode());
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
        saveFile(file.getInputStream(), vstrOriginalName);

        try {
            //upload vds
            if (Constants.VDS_EXCEL_CLASS.equals(pclsClazz.getSimpleName())) {
                ExcelReader<VTTServiceExcel> vreader = new ExcelReader<>();
                List<VTTServiceExcel> vlstPlanExcels;
                vlstPlanExcels = vreader.read(vstrOriginalName, VTTServiceExcel.class);

                if (DataUtil.isNullOrEmpty(vlstPlanExcels)) {
                    baseUploadEntity.setMessage(I18N.get(Constants.EMPTY_FILE_ERROR));
                    return baseUploadEntity;
                } else {
                    checkTheSameRecord(vlstPlanExcels);
                    for (VTTServiceExcel planVTTExcel : vlstPlanExcels) {
                        if (!DataUtil.isNullObject(planVTTExcel)) {
                            if (!DataUtil.isNullOrEmpty(planVTTExcel.getServiceCode()))
                                vlngServiceId = serviceRepo.findServiceIdByCode(planVTTExcel.getServiceCode().trim());
                            if (!DataUtil.isNullOrEmpty(planVTTExcel.getChannelCode()))
                                vstrShopCode = partnerRepo.findShopCodeByChannelCode(planVTTExcel.getChannelCode().trim());
                            if (!DataUtil.isNullOrEmpty(planVTTExcel.getMonth()))
                                vlngPrdId = DataUtil.convertStringToLong(planVTTExcel.getMonth().trim());
                            vintSumRecord++;
                            if (DataUtil.isNullOrEmpty(planVTTExcel.getError())) {
                                if (!cycleRepo.checkExistedCycleCode(planVTTExcel.getCycleCode().trim())) {
                                    planVTTExcel.setError(I18N.get("common.excel.active.error") + ". " + I18N.get("common.excel.column.error") + " B ");
                                } else if (Constants.CYCLE_CODE.MONTH.equals(planVTTExcel.getCycleCode()) && !DataUtil.compareDateInFileAndSystem(planVTTExcel.getMonth().trim())) {
                                    planVTTExcel.setError(I18N.get("common.excel.greater.current.month.error"));
                                } else if (Constants.CYCLE_CODE.QUARTER.equals(planVTTExcel.getCycleCode()) && !DataUtil.compareQuarterInFileAndSystem(planVTTExcel.getMonth().trim())) {
                                    planVTTExcel.setError(I18N.get("common.excel.greater.current.quarter.error"));
                                } else if (Constants.CYCLE_CODE.YEAR.equals(planVTTExcel.getCycleCode()) && !DataUtil.compareYearInFileAndSystem(planVTTExcel.getMonth().trim())) {
                                    planVTTExcel.setError(I18N.get("common.excel.greater.current.year.error"));
                                } else if (DataUtil.compareDateInFileAnd2100(planVTTExcel.getMonth().trim())) {
                                    planVTTExcel.setError(I18N.get("common.excel.month.range.error"));
                                } else if (!channelRepo.checkExistedChannelCode(planVTTExcel.getChannelCode().trim())) {
                                    planVTTExcel.setError(I18N.get("common.excel.active.error") + " " + I18N.get("common.excel.channel.of.user") + ". " + I18N.get("common.excel.column.error") + " D ");
                                } else if (!serviceRepo.checkExistedServiceCode(planVTTExcel.getServiceCode().trim())) {
                                    planVTTExcel.setError(I18N.get("common.excel.active.error") + ". " + I18N.get("common.excel.column.error") + " E ");
                                } else if (!serviceRepo.checkServiceInChannel(planVTTExcel.getChannelCode().trim(), planVTTExcel.getServiceCode().trim())) {
                                    planVTTExcel.setError(I18N.get("common.excel.service.channel.vds.error") + ". " + I18N.get("common.excel.column.error") + " E ");
                                } else if (!serviceRepo.checkExistedServiceAssign(planVTTExcel.getServiceCode().trim())) {
                                    planVTTExcel.setError(I18N.get("common.excel.service.type.error"));
                                } else if (!serviceService.checkActiveService(vlngServiceId, vlngPrdId, planVTTExcel.getCycleCode().trim())) {
                                    planVTTExcel.setError(I18N.get("common.excel.ineffective.error"));
                                } else if (DataUtil.isNullOrZero(planVTTExcel.getSchedule())) {
                                    planVTTExcel.setError(I18N.get("common.excel.service.assign.error"));
                                }
                            }
                            if (DataUtil.isNullOrEmpty(planVTTExcel.getError())) {
                                try {
                                    switch (planVTTExcel.getCycleCode().trim()) {
                                        case Constants.CYCLE_CODE.MONTH:

                                            planMonthlyRepo.persist(new PlanMonthly(vlngPrdId, planVTTExcel.getChannelCode().trim(),
                                                    vlngServiceId, vstrShopCode, DataUtil.safeToDouble(decimalFormat.format(planVTTExcel.getSchedule())), (DataUtil.isNullObject(vdsStaff) ? null : vdsStaff.getUser()), null, timestamp));
                                            vintSumSuccessfulRecord++;
                                            break;

                                        case Constants.CYCLE_CODE.QUARTER:
                                            vlngPrdId = DataUtil.convertStringToFirstQuarter(planVTTExcel.getMonth().trim());

                                            planQuarterlyRepo.persist(new PlanQuarterly(vlngPrdId, planVTTExcel.getChannelCode().trim(),
                                                    vlngServiceId, vstrShopCode, DataUtil.safeToDouble(decimalFormat.format(planVTTExcel.getSchedule())), (DataUtil.isNullObject(vdsStaff) ? null : vdsStaff.getUser()), null, timestamp));
                                            vintSumSuccessfulRecord++;

                                            break;

                                        case Constants.CYCLE_CODE.YEAR:
                                            vlngPrdId = DataUtil.convertStringToFirstYear(planVTTExcel.getMonth().trim());

                                            planYearlyRepo.persist(new PlanYearly(vlngPrdId, planVTTExcel.getChannelCode().trim(),
                                                    vlngServiceId, vstrShopCode, DataUtil.safeToDouble(decimalFormat.format(planVTTExcel.getSchedule())), (DataUtil.isNullObject(vdsStaff) ? null : vdsStaff.getUser()), null, timestamp));
                                            vintSumSuccessfulRecord++;

                                            break;
                                    }


                                } catch (Exception e) {
                                    planVTTExcel.setError(e.getMessage());
                                    LOGGER.error(e.getMessage(), e);
                                }
                            }
                        } else {
                            vintCheck++;
                            continue;
                        }

                    }
                    //check empty file
                    if (vintCheck == vlstPlanExcels.size()) {
                        baseUploadEntity.setMessage(I18N.get(Constants.EMPTY_FILE_ERROR));
                        return baseUploadEntity;
                    }
                }
                vreader.writeResultFile(vstrOriginalName, vstrResultFilePath, VTTServiceExcel.class, vlstPlanExcels, I18N.get("common.success.status"));

            }

            //upload level
            if (Constants.LEVEL_EXCEL_CLASS.equals(pclsClazz.getSimpleName())) {
                ExcelReader<LevelServiceExcel> vreader = new ExcelReader<>();
                List<LevelServiceExcel> vlstPlanExcels;
                vlstPlanExcels = vreader.read(vstrOriginalName, LevelServiceExcel.class);
                List<LevelServiceExcel> vlstPlanLevelExcels = vlstPlanExcels.stream().sorted(Comparator.comparing(LevelServiceExcel::getServiceCode)).collect(Collectors.toList());

                if (DataUtil.isNullOrEmpty(vlstPlanExcels)) {
                    baseUploadEntity.setMessage(I18N.get(Constants.EMPTY_FILE_ERROR));
                    return baseUploadEntity;
                } else {
                    checkTheSameRecord(vlstPlanExcels);
                    for (LevelServiceExcel planLevelExcel : vlstPlanLevelExcels) {
                        if (!DataUtil.isNullObject(planLevelExcel)) {
                            if (!DataUtil.isNullOrEmpty(planLevelExcel.getServiceCode()))
                                vlngServiceId = serviceRepo.findServiceIdByCode(planLevelExcel.getServiceCode().trim());
                            if (!DataUtil.isNullOrEmpty(planLevelExcel.getUnit()))
                                vstrChannelCode = channelRepo.findChannelByShopCode(planLevelExcel.getUnit().trim()).getCode();
                            if (!DataUtil.isNullOrEmpty(planLevelExcel.getMonth()))
                                vlngPrdId = DataUtil.convertStringToLong(planLevelExcel.getMonth().trim());
                            vintSumRecord++;
                            if (DataUtil.isNullOrEmpty(planLevelExcel.getError())) {
                                if (!cycleRepo.checkExistedCycleCode(planLevelExcel.getCycleCode().trim())) {
                                    planLevelExcel.setError(I18N.get("common.excel.active.error") + ". " + I18N.get("common.excel.column.error") + " B ");
                                } else if (Constants.CYCLE_CODE.MONTH.equals(planLevelExcel.getCycleCode()) && !DataUtil.compareDateInFileAndSystem(planLevelExcel.getMonth().trim())) {
                                    planLevelExcel.setError(I18N.get("common.excel.greater.current.month.error"));
                                } else if (Constants.CYCLE_CODE.QUARTER.equals(planLevelExcel.getCycleCode()) && !DataUtil.compareQuarterInFileAndSystem(planLevelExcel.getMonth().trim())) {
                                    planLevelExcel.setError(I18N.get("common.excel.greater.current.quarter.error"));
                                } else if (Constants.CYCLE_CODE.YEAR.equals(planLevelExcel.getCycleCode()) && !DataUtil.compareQuarterInFileAndSystem(planLevelExcel.getMonth().trim())) {
                                    planLevelExcel.setError(I18N.get("common.excel.greater.current.year.error"));
                                } else if (DataUtil.compareDateInFileAnd2100(planLevelExcel.getMonth().trim())) {
                                    planLevelExcel.setError(I18N.get("common.excel.month.range.error"));
                                } else if (!partnerRepo.checkExistedUnitCode(planLevelExcel.getUnit().trim())) {
                                    planLevelExcel.setError(I18N.get("common.excel.active.error") + ". " + I18N.get("common.excel.column.error") + " D ");
                                } else if (!DataUtil.isNullOrEmpty(planLevelExcel.getStaff()) && !vdsStaffRepo.checkExistedStaffCode(planLevelExcel.getStaff().trim())) {
                                    planLevelExcel.setError(I18N.get("common.excel.active.error") + ". " + I18N.get("common.excel.column.error") + " F ");
                                } else if (!DataUtil.isNullOrEmpty(planLevelExcel.getStaff()) && !staffRepo.checkExistedStaffBusiness(planLevelExcel.getStaff().trim())) {
                                    planLevelExcel.setError(I18N.get("common.excel.staff.business.error"));
                                } else if (!serviceRepo.checkExistedServiceCode(planLevelExcel.getServiceCode().trim())) {
                                    planLevelExcel.setError(I18N.get("common.excel.active.error") + ". " + I18N.get("common.excel.column.error") + " H ");
                                } else if (!serviceRepo.checkServiceInChannel(staffDTO.getVdsChannelCode(), planLevelExcel.getServiceCode().trim())) {
                                    planLevelExcel.setError(I18N.get("common.excel.service.channel.level.error") + ". " + I18N.get("common.excel.column.error") + " H ");
                                } else if (!serviceRepo.checkExistedServiceAssign(planLevelExcel.getServiceCode().trim())) {
                                    planLevelExcel.setError(I18N.get("common.excel.service.type.error"));
                                } else if (!serviceService.checkActiveService(vlngServiceId, vlngPrdId, planLevelExcel.getCycleCode().trim())) {
                                    planLevelExcel.setError(I18N.get("common.excel.ineffective.error"));
                                } else if (DataUtil.isNullOrZero(planLevelExcel.getSchedule())) {
                                    planLevelExcel.setError(I18N.get("common.excel.service.assign.error"));
                                }
                            }
                            if (DataUtil.isNullOrEmpty(planLevelExcel.getError())) {
                                try {
                                    switch (planLevelExcel.getCycleCode().trim()) {
                                        case Constants.CYCLE_CODE.MONTH:
                                            PlanMonthly planMonthly = planMonthlyRepo.findPlanMonthlyFromPlan(planLevelExcel, staffDTO);
                                            if (DataUtil.isNullObject(planMonthly)) {
                                                vlstMonthlPlans.add(new PlanMonthly(vlngPrdId, vstrChannelCode,
                                                        vlngServiceId, DataUtil.isNullOrEmpty(planLevelExcel.getStaff()) ? null : planLevelExcel.getStaff().trim(), planLevelExcel.getUnit().trim(),
                                                        DataUtil.safeToDouble(decimalFormat.format(planLevelExcel.getSchedule())), (DataUtil.isNullObject(vdsStaff) ? null : vdsStaff.getUser()), timestamp));
                                            } else {
                                                vlstMonthlPlans.add(new PlanMonthly(planMonthly.getId(), vlngPrdId, vstrChannelCode,
                                                        vlngServiceId, DataUtil.isNullOrEmpty(planLevelExcel.getStaff()) ? null : planLevelExcel.getStaff().trim(), planLevelExcel.getUnit().trim(),
                                                        DataUtil.safeToDouble(decimalFormat.format(planLevelExcel.getSchedule())), (DataUtil.isNullObject(vdsStaff) ? null : vdsStaff.getUser()), timestamp));
                                            }

                                            break;
                                        case Constants.CYCLE_CODE.QUARTER:
                                            PlanQuarterly planQuarterly = planQuarterlyRepo.findPlanQuarterFromPlan(planLevelExcel, staffDTO);
                                            vlngPrdId = DataUtil.convertStringToFirstQuarter(planLevelExcel.getMonth().trim());
                                            if (DataUtil.isNullObject(planQuarterly)) {
                                                vlstQuarterPlans.add(new PlanQuarterly(vlngPrdId, vstrChannelCode,
                                                        vlngServiceId, DataUtil.isNullOrEmpty(planLevelExcel.getStaff()) ? null : planLevelExcel.getStaff().trim(), planLevelExcel.getUnit().trim(),
                                                        DataUtil.safeToDouble(decimalFormat.format(planLevelExcel.getSchedule())), (DataUtil.isNullObject(vdsStaff) ? null : vdsStaff.getUser()), timestamp));
                                            } else {
                                                vlstQuarterPlans.add(new PlanQuarterly(planQuarterly.getId(), vlngPrdId, vstrChannelCode,
                                                        vlngServiceId, DataUtil.isNullOrEmpty(planLevelExcel.getStaff()) ? null : planLevelExcel.getStaff().trim(), planLevelExcel.getUnit().trim(),
                                                        DataUtil.safeToDouble(decimalFormat.format(planLevelExcel.getSchedule())), (DataUtil.isNullObject(vdsStaff) ? null : vdsStaff.getUser()), timestamp));
                                            }

                                            break;
                                        case Constants.CYCLE_CODE.YEAR:
                                            PlanYearly planYearly = planYearlyRepo.findPlanYearlyFromPlan(planLevelExcel, staffDTO);
                                            vlngPrdId = DataUtil.convertStringToFirstYear(planLevelExcel.getMonth().trim());
                                            if (DataUtil.isNullObject(planYearly)) {
                                                vlstYearPlans.add(new PlanYearly(vlngPrdId, vstrChannelCode,
                                                        vlngServiceId, DataUtil.isNullOrEmpty(planLevelExcel.getStaff()) ? null : planLevelExcel.getStaff().trim(), planLevelExcel.getUnit().trim(),
                                                        DataUtil.safeToDouble(decimalFormat.format(planLevelExcel.getSchedule())), (DataUtil.isNullObject(vdsStaff) ? null : vdsStaff.getUser()), timestamp));
                                            } else {
                                                vlstYearPlans.add(new PlanYearly(planYearly.getId(), vlngPrdId, vstrChannelCode,
                                                        vlngServiceId, DataUtil.isNullOrEmpty(planLevelExcel.getStaff()) ? null : planLevelExcel.getStaff().trim(), planLevelExcel.getUnit().trim(),
                                                        DataUtil.safeToDouble(decimalFormat.format(planLevelExcel.getSchedule())), (DataUtil.isNullObject(vdsStaff) ? null : vdsStaff.getUser()), timestamp));
                                            }
                                            break;
                                    }

                                } catch (Exception e) {
                                    planLevelExcel.setError(e.getMessage());
                                    LOGGER.error(e.getMessage(), e);
                                }
                            }

                        } else {
                            vintCheck++;
                            continue;
                        }

                    }

                    for (LevelServiceExcel planLevelExcel : vlstPlanLevelExcels) {
                        if (DataUtil.isNullOrEmpty(planLevelExcel.getError())) {
                            Map<Long, Double> hmpSchedule = new HashMap<>();
                            Map<Long, Double> hmpChangeSchedule = new HashMap<>();
                            Map<Long, Double> hmpSumScheduleChild = new HashMap<>();

                            try {
                                switch (planLevelExcel.getCycleCode().trim()) {
                                    case Constants.CYCLE_CODE.MONTH:
                                        getAllScheduleMonth(vlstMonthlPlans, hmpSchedule, hmpChangeSchedule, hmpSumScheduleChild, staffDTO);
                                        vstrMessage = validateMonthSchedule(vlstMonthlPlans, planLevelExcel,
                                                hmpSchedule, hmpChangeSchedule, hmpSumScheduleChild, checkShopOfProvince, counts);
                                        if (!DataUtil.isNullOrEmpty(vstrMessage)) {
                                            if (!vstrMessage.matches("^[0-9]*$")) {
                                                planLevelExcel.setError(vstrMessage);
                                            }
                                        }

                                        break;
                                    case Constants.CYCLE_CODE.QUARTER:
                                        getAllScheduleQuarter(vlstQuarterPlans, hmpSchedule, hmpChangeSchedule, hmpSumScheduleChild, staffDTO);
                                        vstrMessage = validateQuarterSchedule(vlstQuarterPlans, planLevelExcel,
                                                hmpSchedule, hmpChangeSchedule, hmpSumScheduleChild, checkShopOfProvince, counts);
                                        if (!DataUtil.isNullOrEmpty(vstrMessage)) {
                                            if (!vstrMessage.matches("^[0-9]*$")) {
                                                planLevelExcel.setError(vstrMessage);
                                            }
                                        }

                                        break;
                                    case Constants.CYCLE_CODE.YEAR:
                                        getAllScheduleYear(vlstYearPlans, hmpSchedule, hmpChangeSchedule, hmpSumScheduleChild, staffDTO);
                                        vstrMessage = validateYearSchedule(vlstYearPlans, planLevelExcel,
                                                hmpSchedule, hmpChangeSchedule, hmpSumScheduleChild, checkShopOfProvince, counts);
                                        if (!DataUtil.isNullOrEmpty(vstrMessage)) {
                                            if (!vstrMessage.matches("^[0-9]*$")) {
                                                planLevelExcel.setError(vstrMessage);
                                            }
                                        }
                                        break;
                                }


                            } catch (Exception e) {
                                planLevelExcel.setError(e.getMessage());
                                LOGGER.error(e.getMessage(), e);
                            }
                        }
                    }

                    for (Map.Entry<String, Integer> entry : counts.entrySet()) {
                        vintSumSuccessfulRecord = vintSumSuccessfulRecord + entry.getValue();
                    }
                    //check empty file
                    if (vintCheck == vlstPlanExcels.size()) {
                        baseUploadEntity.setMessage(I18N.get(Constants.EMPTY_FILE_ERROR));
                        return baseUploadEntity;
                    }
                }
                vreader.writeResultFile(vstrOriginalName, vstrResultFilePath, LevelServiceExcel.class, vlstPlanExcels, I18N.get("common.success.status"));

            }
            //set cac thong tin tra ve frontend
            baseUploadEntity.setResultFileName(vstrResultFileName);
            baseUploadEntity.setSumSuccessfulRecord(vintSumSuccessfulRecord);
            baseUploadEntity.setSumRecord(vintSumRecord);

        } catch (Exception e) {
            baseUploadEntity.setMessage(e.getMessage());
            LOGGER.error(e.getMessage(), e);
        }

        return baseUploadEntity;

    }

    @Override
    @Transactional
    public List<PlanMonthlyDTO> getTemplateFileVDS(PlanMonthlyDTO planMonthlyDTO, StaffDTO staffDTO) throws Exception {
        return planMonthlyRepo.findTemplateFileVDS(planMonthlyDTO, staffDTO);
    }

    @Override
    @Transactional
    public List<PlanMonthlyDTO> getTemplateFileLevel(PlanMonthlyDTO planMonthlyDTO, StaffDTO staffDTO) throws Exception {
        return planMonthlyRepo.findTemplateFileLevel(planMonthlyDTO, staffDTO);
    }

    //save file
    public void saveFile(InputStream inputStream, String pstrPath) throws IOException {
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(new File(pstrPath));
            int vintRead;
            byte[] bytes = new byte[1024];

            while ((vintRead = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, vintRead);
                outputStream.flush();
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }

    }

    /*
     *check duplicate row
     */
    public void checkTheSameRecord(List<? extends BaseExcelEntity> plstList) {

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

    private void getAllScheduleMonth(List<PlanMonthly> plstPlanMonthly, Map<Long, Double> hmpSchedule, Map<Long, Double> hmpChangeSchedule,
                                     Map<Long, Double> hmpSumScheduleChild, StaffDTO staffDTO) throws Exception {
        double dblSchelduleOfShop = 0d;
        double dblSumSchelduleChildShop = 0d;
        double dblChangeSchedule = 0d;

        List<Long> idList = plstPlanMonthly.stream()
                .map(PlanMonthly::getId)
                .collect(Collectors.toList());

        for (PlanMonthly plan : plstPlanMonthly) {
            dblSchelduleOfShop = planMonthlyRepo.getScheduleOfShop(plan, staffDTO);
            if (!hmpSchedule.containsKey(plan.getServiceId()))
                hmpSchedule.put(plan.getServiceId(), dblSchelduleOfShop);
            if (!hmpChangeSchedule.containsKey(plan.getServiceId())) {
                dblChangeSchedule = plan.getfSchedule();
                hmpChangeSchedule.put(plan.getServiceId(), dblChangeSchedule);
            } else {
                dblChangeSchedule = hmpChangeSchedule.get(plan.getServiceId()) + plan.getfSchedule();
                hmpChangeSchedule.put(plan.getServiceId(), dblChangeSchedule);
            }

            dblSumSchelduleChildShop = planMonthlyRepo.getSumScheduleChildShop(plan, idList, staffDTO);
            if (!hmpSumScheduleChild.containsKey(plan.getServiceId()))
                hmpSumScheduleChild.put(plan.getServiceId(), dblSumSchelduleChildShop);
        }
    }

    private void getAllScheduleQuarter(List<PlanQuarterly> plstPlanQuarterly, Map<Long, Double> hmpSchedule, Map<Long, Double> hmpChangeSchedule,
                                       Map<Long, Double> hmpSumScheduleChild, StaffDTO staffDTO) throws Exception {
        double dblSchelduleOfShop = 0d;
        double dblSumSchelduleChildShop = 0d;
        double dblChangeSchedule = 0d;

        List<Long> idList = plstPlanQuarterly.stream()
                .map(PlanQuarterly::getId)
                .collect(Collectors.toList());

        for (PlanQuarterly plan : plstPlanQuarterly) {
            dblSchelduleOfShop = planQuarterlyRepo.getScheduleOfShop(plan, staffDTO);
            if (!hmpSchedule.containsKey(plan.getServiceId()))
                hmpSchedule.put(plan.getServiceId(), dblSchelduleOfShop);
            if (!hmpChangeSchedule.containsKey(plan.getServiceId())) {
                dblChangeSchedule = plan.getfSchedule();
                hmpChangeSchedule.put(plan.getServiceId(), dblChangeSchedule);
            } else {
                dblChangeSchedule = hmpChangeSchedule.get(plan.getServiceId()) + plan.getfSchedule();
                hmpChangeSchedule.put(plan.getServiceId(), dblChangeSchedule);
            }

            dblSumSchelduleChildShop = planQuarterlyRepo.getSumScheduleChildShop(plan, idList, staffDTO);
            if (!hmpSumScheduleChild.containsKey(plan.getServiceId()))
                hmpSumScheduleChild.put(plan.getServiceId(), dblSumSchelduleChildShop);
        }
    }

    private void getAllScheduleYear(List<PlanYearly> plstPlanYearly, Map<Long, Double> hmpSchedule, Map<Long, Double> hmpChangeSchedule,
                                    Map<Long, Double> hmpSumScheduleChild, StaffDTO staffDTO) throws Exception {
        double dblSchelduleOfShop = 0d;
        double dblSumSchelduleChildShop = 0d;
        double dblChangeSchedule = 0d;

        List<Long> idList = plstPlanYearly.stream()
                .map(PlanYearly::getId)
                .collect(Collectors.toList());

        for (PlanYearly plan : plstPlanYearly) {
            dblSchelduleOfShop = planYearlyRepo.getScheduleOfShop(plan, staffDTO);
            if (!hmpSchedule.containsKey(plan.getServiceId()))
                hmpSchedule.put(plan.getServiceId(), dblSchelduleOfShop);
            if (!hmpChangeSchedule.containsKey(plan.getServiceId())) {
                dblChangeSchedule = plan.getfSchedule();
                hmpChangeSchedule.put(plan.getServiceId(), dblChangeSchedule);
            } else {
                dblChangeSchedule = hmpChangeSchedule.get(plan.getServiceId()) + plan.getfSchedule();
                hmpChangeSchedule.put(plan.getServiceId(), dblChangeSchedule);
            }

            dblSumSchelduleChildShop = planYearlyRepo.getSumScheduleChildShop(plan, idList, staffDTO);
            if (!hmpSumScheduleChild.containsKey(plan.getServiceId()))
                hmpSumScheduleChild.put(plan.getServiceId(), dblSumSchelduleChildShop);
        }
    }

    /**
     * validate ke hoach giao chi tieu cac cap chu ky thang
     */
    private String validateMonthSchedule(List<PlanMonthly> plstPlanMonthly,
                                         LevelServiceExcel levelServiceExcel,
                                         Map<Long, Double> hmpSchedule, Map<Long, Double> hmpChangeSchedule,
                                         Map<Long, Double> hmpSumScheduleChild,
                                         boolean checkShopOfProvince, Map<String, Integer> counts) throws Exception {
        int count = 0;
        String serviceName = null;
        String vstrMessage = "";
        Long vlngServiceId = null;

        for (PlanMonthly planMonthly1 : plstPlanMonthly) {
            if (!DataUtil.isNullObject(planMonthly1)) {
                vlngServiceId = serviceRepo.findServiceIdByCode(levelServiceExcel.getServiceCode().trim());
                if (!vlngServiceId.equals(planMonthly1.getServiceId())) continue;

                for (Map.Entry<Long, Double> entry : hmpSchedule.entrySet()) {
                    if (entry.getKey() == planMonthly1.getServiceId() && hmpSumScheduleChild.containsKey(entry.getKey()) && hmpChangeSchedule.containsKey(entry.getKey())) {
                        if (checkShopOfProvince && Double.sum(hmpSumScheduleChild.get(entry.getKey()), hmpChangeSchedule.get(entry.getKey())) < entry.getValue()) {
                            serviceName = serviceRepo.findNameById(planMonthly1.getServiceId());
                            vstrMessage = I18N.get("schedule.update.error") + " " + serviceName;
                            break;
                        }

                        if (DataUtil.isNullOrEmpty(vstrMessage)) {
                            if (DataUtil.isNullOrZero(planMonthly1.getId()))
                                planMonthlyRepo.persist(planMonthly1);
                            else planMonthlyRepo.update(planMonthly1);
                            count++;
                            break;
                        }
                    }
                }
                counts.put(levelServiceExcel.getServiceCode().trim(), count);
            }
        }

        if (!DataUtil.isNullOrEmpty(vstrMessage))
            return vstrMessage;
        else return String.valueOf(count);
    }

    /**
     * validate ke hoach giao chi tieu cac cap chu ky quy
     */
    private String validateQuarterSchedule(List<PlanQuarterly> plstPlanQuarterly, LevelServiceExcel levelServiceExcel,
                                           Map<Long, Double> hmpSchedule, Map<Long, Double> hmpChangeSchedule,
                                           Map<Long, Double> hmpSumScheduleChild,
                                           boolean checkShopOfProvince, Map<String, Integer> counts) throws Exception {
        String serviceName = null;
        String vstrMessage = "";
        Long vlngServiceId = null;
        int count = 0;

        for (PlanQuarterly planQuarterly : plstPlanQuarterly) {
            if (!DataUtil.isNullObject(planQuarterly)) {
                vlngServiceId = serviceRepo.findServiceIdByCode(levelServiceExcel.getServiceCode().trim());
                if (!vlngServiceId.equals(planQuarterly.getServiceId())) continue;
                for (Map.Entry<Long, Double> entry : hmpSchedule.entrySet()) {
                    if (entry.getKey() == planQuarterly.getServiceId() && hmpSumScheduleChild.containsKey(entry.getKey()) && hmpChangeSchedule.containsKey(entry.getKey())) {
                        if (checkShopOfProvince && Double.sum(hmpSumScheduleChild.get(entry.getKey()), hmpChangeSchedule.get(entry.getKey())) < entry.getValue()) {
                            serviceName = serviceRepo.findNameById(planQuarterly.getServiceId());
                            vstrMessage = I18N.get("schedule.update.error") + " " + serviceName;
                            break;
                        }

                        if (DataUtil.isNullOrEmpty(vstrMessage)) {

                            if (DataUtil.isNullOrZero(planQuarterly.getId()))
                                planQuarterlyRepo.persist(planQuarterly);
                            else planQuarterlyRepo.update(planQuarterly);
                            count++;
                            break;
                        }
                    }
                }
                counts.put(levelServiceExcel.getServiceCode().trim(), count);
            }
        }
        if (!DataUtil.isNullOrEmpty(vstrMessage))
            return vstrMessage;
        else return String.valueOf(count);
    }

    /**
     * validate ke hoach giao chi tieu cac cap chu ky nam
     */
    private String validateYearSchedule(List<PlanYearly> plstPlanYearly, LevelServiceExcel levelServiceExcel,
                                        Map<Long, Double> hmpSchedule, Map<Long, Double> hmpChangeSchedule,
                                        Map<Long, Double> hmpSumScheduleChild,
                                        boolean checkShopOfProvince, Map<String, Integer> counts) throws Exception {

        String serviceName = null;
        String vstrMessage = "";
        Long vlngServiceId = null;
        int count = 0;

        for (PlanYearly planYearly : plstPlanYearly) {
            if (!DataUtil.isNullObject(planYearly)) {
                vlngServiceId = serviceRepo.findServiceIdByCode(levelServiceExcel.getServiceCode().trim());
                if (!vlngServiceId.equals(planYearly.getServiceId())) continue;
                for (Map.Entry<Long, Double> entry : hmpSchedule.entrySet()) {
                    if (entry.getKey() == planYearly.getServiceId() && hmpSumScheduleChild.containsKey(entry.getKey()) && hmpChangeSchedule.containsKey(entry.getKey())) {
                        if (checkShopOfProvince && Double.sum(hmpSumScheduleChild.get(entry.getKey()), hmpChangeSchedule.get(entry.getKey())) < entry.getValue()) {
                            serviceName = serviceRepo.findNameById(planYearly.getServiceId());
                            vstrMessage = I18N.get("schedule.update.error") + " " + serviceName;
                            break;
                        }

                        if (DataUtil.isNullOrEmpty(vstrMessage)) {
                            if (DataUtil.isNullObject(planYearly))
                                planYearlyRepo.persist(planYearly);
                            else planYearlyRepo.update(planYearly);
                            count++;
                            break;
                        }
                    }
                }
                counts.put(levelServiceExcel.getServiceCode().trim(), count);
            }
        }
        if (vstrMessage.length() != 0)
            return vstrMessage;
        else return String.valueOf(count);
    }

    public boolean checkShopCodeOfProvince(StaffDTO staffDTO) throws Exception {
        boolean checkShopOfProvince = false;
        List<String> vlstShopCodeOfProvince = partnerRepo.getPartnerOfProvinceChannel();

        if (!DataUtil.isNullOrEmpty(vlstShopCodeOfProvince)) {
            if (vlstShopCodeOfProvince.contains(staffDTO.getShopCode()))
                checkShopOfProvince = true;
        }

        return checkShopOfProvince;
    }
}
