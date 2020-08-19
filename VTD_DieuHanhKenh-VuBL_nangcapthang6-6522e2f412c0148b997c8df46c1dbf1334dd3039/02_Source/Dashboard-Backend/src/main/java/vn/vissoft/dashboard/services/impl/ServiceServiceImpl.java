package vn.vissoft.dashboard.services.impl;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.vissoft.dashboard.config.I18N;
import vn.vissoft.dashboard.dto.LogHistoryServiceDTO;
import vn.vissoft.dashboard.dto.ServiceDTO;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.excel.BaseExcelEntity;
import vn.vissoft.dashboard.dto.excel.BaseUploadEntity;
import vn.vissoft.dashboard.dto.excel.ServiceExcel;
import vn.vissoft.dashboard.dto.excel.ShopUnitExcel;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.helper.excelreader.ExcelReader;
import vn.vissoft.dashboard.mapper.BaseMapper;
import vn.vissoft.dashboard.model.*;
import vn.vissoft.dashboard.repo.GroupServiceRepo;
import vn.vissoft.dashboard.repo.ServiceRepo;
import vn.vissoft.dashboard.repo.UnitRepo;
import vn.vissoft.dashboard.services.*;

import javax.transaction.Transactional;
import java.io.*;
import java.math.BigInteger;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Transactional
@Service
public class ServiceServiceImpl implements ServiceService {

    @Autowired
    private ServiceRepo serviceRepo;

    @Autowired
    private GroupServiceRepo groupServiceRepo;

    @Autowired
    private ChannelService channelService;

    @Autowired
    private ServiceChannelService serviceChannelService;

    @Autowired
    private UnitRepo unitRepo;

    @Autowired
    private ActionDetailService actionDetailService;

    @Autowired
    private ActionAuditService actionAuditService;

    @Value("${server.tomcat.basedir}")
    private String mstrUploadPath;

    public static final Logger LOGGER = LogManager.getLogger(ServiceServiceImpl.class);

    private BaseMapper<vn.vissoft.dashboard.model.Service, ServiceDTO> mapper = new BaseMapper<vn.vissoft.dashboard.model.Service, ServiceDTO>(vn.vissoft.dashboard.model.Service.class, ServiceDTO.class);

    @Override
    public List<vn.vissoft.dashboard.model.Service> getServicesByGroupServiceIdAndStatus(Long plngGroupServiceId, String pstrStatus) throws Exception {
        return serviceRepo.findByGroupServiceIdAndStatus(plngGroupServiceId, pstrStatus);
    }


    @Override
    public List<ServiceDTO> getServicesByCondition(ServiceDTO serviceDTO) throws Exception {
        return mapper.toDtoBean(serviceRepo.findServicesByCondition(serviceDTO));
    }

    @Override
    public List<vn.vissoft.dashboard.model.Service> getByChannelCodeOfUser(StaffDTO staffDTO) throws Exception {
        return serviceRepo.findByChannelCodeOfUser(staffDTO);
    }

    /**
     * kiem tra chi tieu thoa man dieu kien de update
     *
     * @param plngServiceId
     * @return
     * @throws Exception
     * @author DatNT
     * @version 1.0
     * @since 23/10/2019
     */
    @Override
    public boolean checkActiveService(Long plngServiceId, Long plngMonth, String pstrCycleCode) throws Exception {
        long vlngMillis = System.currentTimeMillis();
        Date vdtDate = new Date(vlngMillis);
        Date vdtPrd = null;
        vn.vissoft.dashboard.model.Service service = serviceRepo.getOne(plngServiceId);
        Date vdtFromDate = serviceRepo.getFromDate(plngServiceId);
        Date vdtToDate = serviceRepo.getToDate(plngServiceId);

        boolean vblnCheck = false;
        List<Date> vlstDates = DataUtil.getFirstDateBetweenDates(vdtFromDate, vdtToDate);

        switch (pstrCycleCode) {
            case Constants.CYCLE_CODE.MONTH:
                vdtPrd = DataUtil.convertToFirstDateOfMonth(plngMonth);
                break;
            case Constants.CYCLE_CODE.QUARTER:
                vdtPrd = DataUtil.getFirstDayQuarter(plngMonth);
                break;
            case Constants.CYCLE_CODE.YEAR:
                vdtPrd = DataUtil.getFirstPrdYear(plngMonth);
                break;
        }
        Date vdtMonthBeforeValue = serviceRepo.getValueValidateBefore(plngServiceId, vdtPrd);
        Date vdtMonthAfterValue = serviceRepo.getValueValidateAfter(plngServiceId, vdtPrd);
        Date vdtQuarterBeforeValue = serviceRepo.getValueBeforeByQuarter(plngServiceId, vdtPrd);
        Date vdtQuarterAfterValue = serviceRepo.getValueAfterByQuerter(plngServiceId, vdtPrd);
        Date vdtYearBeforeValue = serviceRepo.getValueBeforeByYear(plngServiceId, vdtPrd);
        Date vdtYearAfterValue = serviceRepo.getValueAfterByYear(plngServiceId, vdtPrd);

        if (!DataUtil.isNullObject(service.getToDate()) && vdtDate.toLocalDate().compareTo(service.getToDate().toLocalDate()) > 0) {
            vblnCheck = false;
        } else {
            if (service.getServiceCycle() == 0) { //chi tieu theo chuong trinh
                if (vdtPrd != null && vlstDates.contains(vdtPrd)) {
                    for (int i = 0; i < vlstDates.size(); i++) {
                        if (vlstDates.get(0).toLocalDate().compareTo(vdtPrd.toLocalDate()) == 0) {
                            if (checkSysdateAndFromDate(vdtDate, plngServiceId)) {
                                vblnCheck = true;
                                break;
                            }
                        } else {
                            if (checkValueWithFirstDay(DataUtil.convertToPrdIdFirstDay(vlstDates.get(i).getTime()), plngServiceId, vdtDate)) {
                                vblnCheck = true;
                                break;
                            }
                        }
                    }
                }
            } else {//chi tieu theo chu trinh
                switch (pstrCycleCode) {
                    case Constants.CYCLE_CODE.MONTH:
                        if (!DataUtil.isNullObject(vdtMonthBeforeValue) && !DataUtil.isNullObject(vdtMonthAfterValue)) {
                            if (vdtDate.toLocalDate().compareTo(vdtMonthBeforeValue.toLocalDate()) >= 0 &&
                                    vdtDate.toLocalDate().compareTo(vdtMonthAfterValue.toLocalDate()) <= 0)
                                vblnCheck = true;
                        }
                        break;
                    case Constants.CYCLE_CODE.QUARTER:
                        if (!DataUtil.isNullObject(vdtQuarterBeforeValue) && !DataUtil.isNullObject(vdtQuarterAfterValue)) {
                            if (vdtDate.toLocalDate().compareTo(vdtQuarterBeforeValue.toLocalDate()) >= 0 &&
                                    vdtDate.toLocalDate().compareTo(vdtQuarterAfterValue.toLocalDate()) <= 0)
                                vblnCheck = true;
                        }
                        break;
                    case Constants.CYCLE_CODE.YEAR:
                        if (!DataUtil.isNullObject(vdtYearBeforeValue) && !DataUtil.isNullObject(vdtYearAfterValue)) {
                            if (vdtDate.toLocalDate().compareTo(vdtYearBeforeValue.toLocalDate()) >= 0 &&
                                    vdtDate.toLocalDate().compareTo(vdtYearAfterValue.toLocalDate()) <= 0)
                                vblnCheck = true;
                        }
                        break;
                }

            }
        }
        return vblnCheck;
    }

    @Override
    public BigInteger countService() {
        return null;
    }


    /**
     * Tạo mới service thêm kênh của chỉ tiêu đồng thời tạo lịch sử (Action audit, action detail)
     *
     * @param serviceDTO, staffDTO
     * @return
     * @author Manhtd
     * @version 1.0
     * @since 11/2019
     */
    @Override
    public void createService(ServiceDTO serviceDTO, StaffDTO staffDTO) {
        try {

            serviceDTO.setUser(staffDTO.getStaffCode());
            serviceDTO.setAssignType(0);
            serviceDTO.setImportType(1);
            serviceDTO.setCode(serviceDTO.getCode().toUpperCase());
            serviceDTO.setServiceOrder(null);
            vn.vissoft.dashboard.model.Service service = serviceRepo.saveAndFlush(mapper.toPersistenceBean(serviceDTO));

            ActionAudit actionAudit = actionAuditService.log(Constants.SERVICELOG, Constants.CREATE, staffDTO.getStaffCode(), service.getId(), staffDTO.getShopCode());
            actionDetailService.addLogService(service, null, actionAudit.getId());

            String[] serviceChannelCode = serviceDTO.getVdsChannelCode().split(",");
            // remove duplicate code
            serviceChannelCode = Arrays.stream(serviceChannelCode).distinct().toArray(String[]::new);
            for (String s : serviceChannelCode) {
                ServiceChannel serviceChannel = new ServiceChannel();
                serviceChannel.setServiceId(service.getId());
                serviceChannel.setVdsChannelCode(s.trim());
                serviceChannelService.createServiceChannel(serviceChannel);
            }
            actionDetailService.createActionDetail(I18N.get(Constants.SERVICES.CHANNELCODE), serviceDTO.getVdsChannelCode(), actionAudit.getId(), null);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public List<vn.vissoft.dashboard.model.Service> getAllServices() {
        return serviceRepo.findAll();
    }

    /**
     * Sửa service đồng thời tạo mới lịch sử, thêm kênh của chỉ tiêu (Action audit, action detail)
     *
     * @param serviceDTO, staffDTO
     * @return
     * @author Manhtd
     * @version 1.0
     * @since 11/2019
     */
    @Override
    public void editService(ServiceDTO serviceDTO, StaffDTO staffDTO) {
        try {
            serviceDTO.setUser(staffDTO.getStaffCode());
            serviceDTO.setAssignType(0);
            serviceDTO.setImportType(1);
            serviceDTO.setServiceOrder(null);
            serviceDTO.setCode(serviceDTO.getCode().toUpperCase());
            vn.vissoft.dashboard.model.Service newService = mapper.toPersistenceBean(serviceDTO);
            vn.vissoft.dashboard.model.Service oldService = serviceRepo.findById(serviceDTO.getId()).get();

            ActionAudit actionAudit = actionAuditService.log(Constants.SERVICELOG, Constants.EDIT, staffDTO.getStaffCode(), serviceDTO.getId(), staffDTO.getShopCode());
            actionDetailService.addLogService(newService, oldService, actionAudit.getId());
            vn.vissoft.dashboard.model.Service serviceEdit = serviceRepo.saveAndFlush(mapper.toPersistenceBean(serviceDTO));

            if (!DataUtil.isNullOrEmpty(serviceDTO.getVdsChannelCode())) {

                String[] serviceChannelCode = serviceDTO.getVdsChannelCode().split(",");
                // remove duplicate code
                serviceChannelCode = Arrays.stream(serviceChannelCode).distinct().toArray(String[]::new);

                this.addServiceChannelAndLog(serviceChannelCode, serviceEdit.getId(), actionAudit.getId(), serviceDTO);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }


    /**
     * kiem tra ngay cap nhat voi ngay giao truoc giao sau(dieu kien so sanh from date voi ngay dau thang hien tai)
     *
     * @param pdtPrdId
     * @param plngServiceId
     * @param pdtSysDate
     * @return
     * @throws Exception
     * @author DatNT
     * @version 1.0
     * @since 23/10/2019
     */
    private boolean checkValueWithFirstDay(Long pdtPrdId, Long plngServiceId, Date pdtSysDate) throws Exception {
        boolean vblnCheck = false;

        Date vdtBeforeValue = serviceRepo.getValueBeforeFirstDay(plngServiceId, pdtPrdId);
        Date vdtAfterValue = serviceRepo.getValueAfterFirstDay(plngServiceId, pdtPrdId);
        if (vdtBeforeValue != null && vdtAfterValue != null) {
            if (pdtSysDate.toLocalDate().compareTo(vdtBeforeValue.toLocalDate()) >= 0 && pdtSysDate.toLocalDate().compareTo(vdtAfterValue.toLocalDate()) <= 0)
                vblnCheck = true;
        }
        return vblnCheck;
    }

    /**
     * kiem tra ngay cap nhat voi ngay giao truoc giao sau(dieu kien so sanh from date voi ngay hien tai)
     *
     * @param pdtSysdate
     * @param plngServiceId
     * @return
     * @throws Exception
     * @author DatNT
     * @version 1.0
     * @since 23/10/2019
     */
    private boolean checkSysdateAndFromDate(Date pdtSysdate, Long plngServiceId) throws Exception {
        boolean vblnCheck = false;
        Date vdtBeforeValue = serviceRepo.getValueBefore(plngServiceId);
        Date vdtAfterValue = serviceRepo.getValueAfter(plngServiceId);
        if (!DataUtil.isNullObject(vdtBeforeValue) && !DataUtil.isNullObject(vdtAfterValue)) {
            if (pdtSysdate.toLocalDate().compareTo(vdtBeforeValue.toLocalDate()) >= 0 && pdtSysdate.toLocalDate().compareTo(vdtAfterValue.toLocalDate()) <= 0)
                vblnCheck = true;
        }
        return vblnCheck;
    }


    /**
     * Lấy danh sách lịch sử thay đổi của chỉ tiêu theo id
     *
     * @param idService
     * @return
     * @author Manhtd
     * @version 1.0
     * @since 11/2019
     */
    @Override
    public List<Object[]> getLogOfServiceByServiceId(Long idService) {
        if (!DataUtil.isNullOrZero(idService)) {
            return serviceRepo.getLogOfServiceByServiceId(idService);
        }
        return null;
    }

    /**
     * Convert dữ liệu từ database sang LogHistoryServiceDTO object
     *
     * @param idService
     * @return
     * @author Manhtd
     * @version 1.0
     * @since 11/2019
     */
    public List<LogHistoryServiceDTO> convertToLogHistory(Long idService) {
        List<Object[]> listData = this.getLogOfServiceByServiceId(idService);
        List<LogHistoryServiceDTO> logHistoryServiceDTOs = new ArrayList<>();
        long no = 0;

        if (listData.size() > 0) {
            for (Object[] list : listData) {
                no++;
                LogHistoryServiceDTO logHistoryServiceDTO = new LogHistoryServiceDTO();
                logHistoryServiceDTO.setActionCode(DataUtil.safeToString(list[0]));
                logHistoryServiceDTO.setPkID(DataUtil.safeToLong(list[1]).toString());
                logHistoryServiceDTO.setActionDateTime(DataUtil.safeToString(list[2]));
                logHistoryServiceDTO.setUser(DataUtil.safeToString(list[3]));
                logHistoryServiceDTO.setColumnName(DataUtil.safeToString(list[4]));
                logHistoryServiceDTO.setNo(no);

                if (list[5] == null) {
                    logHistoryServiceDTO.setOldValue(null);
                } else {
                    logHistoryServiceDTO.setOldValue(DataUtil.safeToString(list[5]));
                }
                if (list[6] == null) {
                    logHistoryServiceDTO.setNewValue(null);
                } else {
                    logHistoryServiceDTO.setNewValue(DataUtil.safeToString(list[6]));
                }
                logHistoryServiceDTO.setObjectName(DataUtil.safeToString(list[7]));

                logHistoryServiceDTOs.add(logHistoryServiceDTO);
            }
        }

        return logHistoryServiceDTOs;
    }

    /**
     * Upload file exel chỉ tiêu
     *
     * @param file, pclsClazz, staffDTO
     * @return
     * @author Manhtd
     * @version 1.0
     * @since 11/2019
     */
    public BaseUploadEntity upLoadServices(MultipartFile file, Class pclsClazz, StaffDTO staffDTO) throws Exception {
        int vintCheck = 0;
        int vintSumSuccessfulRecord = 0;
        int vintSumRecord = 0;

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
            vstrResultFileName = vstrOriginalName.substring(0, vstrOriginalName.lastIndexOf(".")) + I18N.get("common.table.warning.result");
        } else {
            vstrResultFileName = vstrOriginalName + I18N.get("common.table.warning.result");
        }
        vstrOriginalName = customDir.getAbsolutePath() + File.separator + vstrOriginalName;

        String vstrResultFilePath = customDir.getAbsolutePath() + File.separator + vstrResultFileName;

        saveFile(file.getInputStream(), vstrOriginalName);

        try {
            if (Constants.SERVICE_EXCEL_CLASS.equals(pclsClazz.getSimpleName())) {
                ExcelReader<ServiceExcel> vreader = new ExcelReader<>();
                List<ServiceExcel> vlstServiceExcels;
                vlstServiceExcels = vreader.read(vstrOriginalName, ServiceExcel.class);

                if (DataUtil.isNullOrEmpty(vlstServiceExcels)) {
                    baseUploadEntity.setMessage(I18N.get(Constants.EMPTY_FILE_ERROR));
                    return baseUploadEntity;
                } else {
                    checkTheSameRecord(vlstServiceExcels);
                    for (ServiceExcel serviceExcel : vlstServiceExcels) {
                        if (!DataUtil.isNullObject(serviceExcel)) {
                            vintSumRecord++;

                            if (DataUtil.isNullOrEmpty(serviceExcel.getError())) {
                                if (serviceExcel.getMstrServiceName().trim().length() > 100) {
                                    serviceExcel.setError(I18N.get("common.excel.active.100") + " " + I18N.get("common.excel.column.error") + " C ");
                                } else if (serviceExcel.getMstrServiceCode().trim().length() > 50) {
                                    serviceExcel.setError(I18N.get("common.excel.active.50") + " " + I18N.get("common.excel.column.error") + " B ");
                                } else if (serviceExcel.getMstrParentCode() != null && serviceExcel.getMstrParentCode().trim().length() > 50) {
                                    serviceExcel.setError(I18N.get("common.excel.active.50") + " " + I18N.get("common.excel.column.error") + " D ");
                                } else if (!this.checkParentCode(serviceExcel.getMstrParentCode())) {
                                    serviceExcel.setError(I18N.get("common.excel.active.error") + " " + I18N.get("common.excel.column.error") + " D ");
                                } else if (!this.checkParentCodeWithChild(serviceExcel.getMstrParentCode(), serviceExcel.getMstrServiceCode())) {
                                    serviceExcel.setError(I18N.get("common.excel.active.parentChild") + " " + I18N.get("common.excel.column.error") + " D ");
                                } else if (!this.checkCodeParent(serviceExcel.getMstrServiceCode(), serviceExcel.getMstrParentCode())) {
                                    serviceExcel.setError(I18N.get("common.excel.parentId.error") + " " + I18N.get("common.excel.column.error") + " D ");
                                } else if (serviceExcel.getMstrGroupCode().trim().length() > 50) {
                                    serviceExcel.setError(I18N.get("common.excel.active.50") + " " + I18N.get("common.excel.column.error") + " D ");
                                } else if (!this.checkGroup(serviceExcel.getMstrGroupCode())) {
                                    serviceExcel.setError(I18N.get("common.excel.active.error") + " " + I18N.get("common.excel.column.error") + " E ");
                                } else if (!this.checkChannels(serviceExcel.getMstrServiceChannel())) {
                                    serviceExcel.setError(I18N.get("common.excel.active.error") + " " + I18N.get("common.excel.column.error") + " F ");
                                } else if (!this.isThisDateValid(serviceExcel.getMstrFromDate())) {
                                    serviceExcel.setError(I18N.get("common.excel.error.errorDate") + " " + I18N.get("common.excel.column.error") + " I");
                                } else if (serviceExcel.getMstrToDate() != null && !this.isThisDateValid(serviceExcel.getMstrToDate())) {
                                    serviceExcel.setError(I18N.get("common.excel.error.errorDate") + " " + I18N.get("common.excel.column.error") + " J");
                                } else if (!this.checkFromDateToDate(serviceExcel.getMstrFromDate(), Constants.MAX_DATE)) {
                                    serviceExcel.setError(I18N.get("common.excel.date.range.error") + " " + I18N.get("common.excel.column.error") + " I");
                                } else if (!this.checkFromDateToDate(Constants.MIN_DATE, serviceExcel.getMstrFromDate())) {
                                    serviceExcel.setError(I18N.get("common.excel.date.range.error") + " " + I18N.get("common.excel.column.error") + " I");
                                } else if (serviceExcel.getMstrToDate() != null && !this.checkFromDateToDate(serviceExcel.getMstrToDate(), Constants.MAX_DATE)) {
                                    serviceExcel.setError(I18N.get("common.excel.date.range.error") + " " + I18N.get("common.excel.column.error") + " J");
                                } else if (serviceExcel.getMstrToDate() != null && !this.checkFromDateToDate(Constants.MIN_DATE, serviceExcel.getMstrToDate())) {
                                    serviceExcel.setError(I18N.get("common.excel.date.range.error") + " " + I18N.get("common.excel.column.error") + " J");
                                } else if (!this.checkDateOfSerivce(serviceExcel.getMstrFromDate())) {
                                    serviceExcel.setError(I18N.get("common.excel.error.fromDate") + " " + I18N.get("common.excel.column.error") + " I");
                                } else if (serviceExcel.getMstrToDate() != null && !this.checkDateOfSerivce(serviceExcel.getMstrToDate())) {
                                    serviceExcel.setError(I18N.get("common.excel.error.toDate") + " " + I18N.get("common.excel.column.error") + " J");
                                } else if (serviceExcel.getMstrToDate() != null && !this.checkFromDateToDate(serviceExcel.getMstrFromDate(), serviceExcel.getMstrToDate())) {
                                    serviceExcel.setError(I18N.get("common.excel.error.fromDateToDate") + " " + I18N.get("common.excel.column.error") + " J");
                                } else if (!this.checkUnitCode(serviceExcel.getMstrUnitCode().trim())) {
                                    serviceExcel.setError(I18N.get("common.excel.active.error") + " " + I18N.get("common.excel.column.error") + " K");
                                } else if (serviceExcel.getExp() != null && serviceExcel.getExp().trim().length() > 300) {
                                    serviceExcel.setError(I18N.get("common.excel.active.300") + " " + I18N.get("common.excel.column.error") + " M");
                                }
                            } else {
                                vintCheck++;
                                continue;
                            }

                            if (DataUtil.isNullOrEmpty(serviceExcel.getError())) {
                                try {
                                    SimpleDateFormat sdf = new SimpleDateFormat(Constants.PATTERNDATE);
                                    long millis = System.currentTimeMillis();

                                    ServiceDTO serviceDTO = new ServiceDTO();
                                    serviceDTO.setAssignType(0);
                                    serviceDTO.setImportType(1);
                                    serviceDTO.setStatus("1");
                                    serviceDTO.setUser(staffDTO.getStaffCode());
                                    serviceDTO.setName(serviceExcel.getMstrServiceName().trim());
                                    serviceDTO.setCode(serviceExcel.getMstrServiceCode().trim());

                                    if (serviceExcel.getMstrParentCode() != null && !("").equals(serviceExcel.getMstrParentCode().trim())) {
                                        List<vn.vissoft.dashboard.model.Service> serviceParent = serviceRepo.findAllByCode(serviceExcel.getMstrParentCode());
                                        serviceDTO.setParentId(serviceParent.get(0).getId());
                                    } else {
                                        serviceDTO.setParentId(0L);
                                    }

                                    GroupService groupService = groupServiceRepo.findByCode(serviceExcel.getMstrGroupCode());
                                    serviceDTO.setGroupServiceId(groupService.getId());

                                    serviceDTO.setServiceType(serviceExcel.getMlongServiceType().trim().substring(0, 1));
                                    serviceDTO.setServiceCalcType(serviceExcel.getMlongServiceCalc().trim().substring(0, 1));
                                    serviceDTO.setServiceCycle(Integer.parseInt(serviceExcel.getMintServiceCycle().trim().substring(0, 1)));

                                    serviceDTO.setVdsChannelCodeFromSC(serviceExcel.getMstrServiceChannel().trim());
                                    serviceDTO.setChangeDatetime(new Date(millis));

                                    java.util.Date fromDate = sdf.parse(serviceExcel.getMstrFromDate().trim());
                                    serviceDTO.setFromDate(new Date(fromDate.getTime()));
                                    serviceDTO.setUnitCode(serviceExcel.getMstrUnitCode().trim().toUpperCase());

                                    if (serviceExcel.getMstrToDate() != null) {
                                        java.util.Date toDate = sdf.parse(serviceExcel.getMstrToDate().trim());
                                        serviceDTO.setToDate(new Date(toDate.getTime()));
                                    } else {
                                        serviceDTO.setToDate(null);
                                    }

                                    serviceDTO.setServiceOrder(null);

                                    if (serviceExcel.getExp() != null) {
                                        serviceDTO.setExp(serviceExcel.getExp().trim());
                                    } else {
                                        serviceDTO.setExp(null);
                                    }

                                    if (!this.checkCodeConflict(serviceExcel.getMstrServiceCode().trim())) {
                                        Long idService = serviceRepo.findServiceIdByCode(serviceExcel.getMstrServiceCode().trim());
                                        serviceDTO.setId(idService);
                                        this.editService(serviceDTO, staffDTO);
                                    } else {
                                        this.createService(serviceDTO, staffDTO);
                                    }

                                    vintSumSuccessfulRecord++;

                                } catch (Exception e) {
                                    serviceExcel.setError(e.getMessage());
                                    LOGGER.error(e.getMessage(), e);
                                }

                            }
                        }
                        if (vintCheck == vlstServiceExcels.size()) {
                            baseUploadEntity.setMessage(I18N.get(Constants.EMPTY_FILE_ERROR));
                            return baseUploadEntity;
                        }
                    }
                    vreader.writeResultWarning(vstrOriginalName, vstrResultFilePath, ServiceExcel.class, vlstServiceExcels, I18N.get("common.success.status"), 3);
                }
            }
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
    public Optional<vn.vissoft.dashboard.model.Service> getServiceById(Long idService) {
        Optional<vn.vissoft.dashboard.model.Service> optionalService = serviceRepo.findById(idService);
        return optionalService;
    }


    /**
     * Kiểm tra mã Service tồn tại hay chưa
     *
     * @param code
     * @return
     * @author Manhtd
     * @version 1.0
     * @since 11/2019
     */
    @Override
    public boolean checkCodeConflict(String code) {
        boolean vblnCheck = true;
        try {
            List<vn.vissoft.dashboard.model.Service> vlstService = serviceRepo.findAllByCode(code);
            if (vlstService.size() > 0) {
                vblnCheck = false;
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return vblnCheck;
    }

    public boolean checkCodeParent(String code, String parentCode) {
        boolean vblnCheck = true;
        try {
            if (parentCode == null) {
                return true;
            } else {
                if (("").equals(parentCode.trim())) {
                    return true;
                } else {
                    List<vn.vissoft.dashboard.model.Service> vlstService = serviceRepo.findAllByCode(code.trim());
                    if (vlstService.size() > 0) {
                        for (vn.vissoft.dashboard.model.Service service : vlstService) {
                            if (parentCode.trim().equals(service.getCode())) {
                                vblnCheck = false;
                                break;
                            }
                        }
                    }
                }
            }


        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return vblnCheck;
    }

    /**
     * Lưu file
     *
     * @param inputStream,pstrPath
     * @return
     * @author Manhtd
     * @version 1.0
     * @since 11/2019
     */
    private void saveFile(InputStream inputStream, String pstrPath) throws IOException {
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

    private void checkTheSameRecord(List<? extends BaseExcelEntity> plstList) {

        for (int i = 0; i < plstList.size(); i++) {
            for (int j = i + 1; j < plstList.size(); j++) {
                if (!DataUtil.isNullObject(plstList.get(i)) && !DataUtil.isNullObject(plstList.get(j))) {
                    if (DataUtil.isNullOrEmpty(plstList.get(i).getError())) {
                        if ((plstList.get(i)).equals(plstList.get(j))) {
                            plstList.get(j).setError(I18N.get("common.excel.row.error") + " " + (i + 5) + " " + I18N.get("common.excel.identical.error") + " " + (j + 5));
                        }
                    }
                }
            }
        }
    }

    /**
     * Kiểm tra parent id khi thêm bằng excel có tồn tại không
     *
     * @param parentID
     * @return
     * @author Manhtd
     * @version 1.0
     * @since 11/2019
     */
    private boolean checkParentCode(String parentCode) {
        try {
            if (parentCode == null) {
                return true;
            } else {
                if ("".equals(parentCode.trim())) {
                    return true;
                }

                List<vn.vissoft.dashboard.model.Service> services = serviceRepo.findAllByCode(parentCode.trim());
                for (vn.vissoft.dashboard.model.Service service : services) {
                    if (("1").equals(service.getStatus())) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return false;
    }

    private boolean checkParentCodeWithChild(String parentCode, String serviceCode) {
        try {
            if (parentCode == null) {
                return true;
            } else {
                if ("".equals(parentCode.trim())) {
                    return true;
                }

                List<vn.vissoft.dashboard.model.Service> services = serviceRepo.findAllByCode(serviceCode);
                if (services.size() > 0) {
                    for (vn.vissoft.dashboard.model.Service service : services) {
                        List<vn.vissoft.dashboard.model.Service> serviceList = serviceRepo.checkServiceParenID(service.getId());
                        for (int i = 0; i < serviceList.size(); i++) {
                            if (parentCode.toUpperCase().equals(serviceList.get(i).getCode().toUpperCase())) {
                                return false;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return true;
    }

    /**
     * Kiểm tra group id khi thêm bằng excel có tồn tại không
     *
     * @param groupID
     * @return
     * @author Manhtd
     * @version 1.0
     * @since 11/2019
     */
    private boolean checkGroup(String groupCode) {
        try {
            GroupService groupService = groupServiceRepo.findByCode(groupCode);
            if (groupService != null) {
                if (("1").equals(groupService.getStatus())) {
                    return true;
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * Kiểm tra mã kênh khi thêm bằng excel có tồn tại không
     *
     * @param channels
     * @return
     * @author Manhtd
     * @version 1.0
     * @since 11/2019
     */
    private boolean checkChannels(String channels) throws Exception {
        boolean vblnCheck = true;
        String[] serviceChannelCode = channels.split(",");
        List<VdsGroupChannel> vlstServiceChannels = channelService.getActiveChannel();

        List<String> vlstChannelCodes = new ArrayList<>();
        for (VdsGroupChannel vdsGroupChannel : vlstServiceChannels) {
            if (("1").equals(vdsGroupChannel.getStatus())) {
                vlstChannelCodes.add(vdsGroupChannel.getCode());
            }
        }

        for (String s : serviceChannelCode) {
            if (!vlstChannelCodes.contains(s.trim())) {
                vblnCheck = false;
                break;
            }
        }
        return vblnCheck;
    }

    /**
     * Kiểm tra mã đơn vị khi thêm bằng excel có tồn tại không
     *
     * @param unitCode
     * @return
     * @author Manhtd
     * @version 1.0
     * @since 11/2019
     */
    private boolean checkUnitCode(String unitCode) {
        try {
            Optional<Unit> unit = unitRepo.getUnitByCode(unitCode.toUpperCase());
            if (unit.isPresent()) {
                if (("1").equals(unit.get().getStatus())) {
                    return true;
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * Kiểm tra ngày bắt đầu, ngày kết thúc (ngày bắt đầu <= systemDate <= ngày kết thúc) trong file excel
     *
     * @param fromDate, toDate
     * @return
     * @author Manhtd
     * @version 1.0
     * @since 11/2019
     */
    private boolean checkDateOfSerivce(String fromDate) {
        boolean check = false;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.PATTERNDATE);
            String vstDatNow = simpleDateFormat.format(new java.util.Date());
            if (!DataUtil.isNullOrEmpty(fromDate)) {
                Instant vinsDateNow = simpleDateFormat.parse(vstDatNow).toInstant();
                Instant vinsBeginDate = simpleDateFormat.parse(fromDate).toInstant();

                long minusBeginAndNow = ChronoUnit.DAYS.between(vinsDateNow, vinsBeginDate);
                if (minusBeginAndNow >= 0) {
                    check = true;

                } else {
                    check = false;
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return check;
    }

    private boolean checkFromDateToDate(String fromDate, String toDate) throws ParseException {
        boolean vblCheck = false;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.PATTERNDATE);
        Instant vinsBeginDate = simpleDateFormat.parse(fromDate).toInstant();
        if (!DataUtil.isNullOrEmpty(toDate)) {
            Instant vinsEndDate = simpleDateFormat.parse(toDate).toInstant();
            long minusEndAndBegin = ChronoUnit.DAYS.between(vinsBeginDate, vinsEndDate);
            if (minusEndAndBegin >= 0) {
                vblCheck = true;
            } else {
                vblCheck = false;
            }
        }
        return vblCheck;
    }

    /**
     * Thêm channel mới, xóa channel cũ ghi log
     *
     * @param newChannelCodes: code của channel dạng mảng
     * @param idService:       id của service được edit
     * @param actionAuditId:   id của action audit
     * @return
     * @author Manhtd
     * @version 1.0
     * @since 11/2019
     */
    private void addServiceChannelAndLog(String[] newChannelCodes, Long idService, Long actionAuditId, ServiceDTO serviceDTO) {
        try {
            List<ServiceChannel> vlstChannels = serviceChannelService.findByServiceId(idService);
            List<String> vlstChannelsCode = new ArrayList<>();

            for (ServiceChannel serviceChannel : vlstChannels) {
                vlstChannelsCode.add(serviceChannel.getVdsChannelCode());
            }

            String[] varrOldChannels = new String[vlstChannelsCode.size()];
            varrOldChannels = vlstChannelsCode.toArray(varrOldChannels);

            String serviceChannels = serviceDTO.getVdsChannelCode().replaceAll("\\s+", "");
            String oldChannelsString = String.join(",", vlstChannelsCode);

            List<String> myList = new ArrayList<>(Arrays.asList(serviceChannels.split(",")));

            List<String> myList2 = new ArrayList<>(Arrays.asList(oldChannelsString.split(",")));
            Collection<String> similar = new HashSet<String>(myList);
            Collection<String> different = new HashSet<String>();
            different.addAll(myList);
            different.addAll(myList2);
            similar.retainAll(myList2);
            different.removeAll(similar);

            if (different.size() > 0) {
                for (String oldChannel : varrOldChannels) {
                    if (!Arrays.asList(newChannelCodes).contains(oldChannel.trim())) {
                        serviceChannelService.deleteServiceChannelByIDandCode(idService, oldChannel, vlstChannels);
                    }
                }

                for (String newChannel : newChannelCodes) {
                    if (!vlstChannelsCode.contains(newChannel.trim())) {
                        ServiceChannel serviceChannel = new ServiceChannel();
                        serviceChannel.setServiceId(idService);
                        serviceChannel.setVdsChannelCode(newChannel.trim());
                        serviceChannelService.createServiceChannel(serviceChannel);
                    }
                }

                actionDetailService.createActionDetail(Constants.SERVICES.CHANNELCODE, serviceDTO.getVdsChannelCode(), actionAuditId, oldChannelsString);
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public boolean isThisDateValid(String dateToValidate) {
        try {

            SimpleDateFormat sdf = new SimpleDateFormat(Constants.PATTERNDATE);
            sdf.setLenient(false);

            sdf.parse(dateToValidate);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return false;
        }

        return true;
    }

    @Override
    public List<vn.vissoft.dashboard.model.Service> findServiceByOrder(Long orderService) {
        return serviceRepo.findServiceByOrder(orderService);
    }

    @Override
    public List<vn.vissoft.dashboard.model.Service> findAllByStatusNotLike(String status) {
        return serviceRepo.findAllByStatusNotLike(status);
    }

    @Override
    public List<vn.vissoft.dashboard.model.Service> checkServiceParenID(Long serviceID) {
        return serviceRepo.checkServiceParenID(serviceID);
    }

    @Override
    public ShopUnitExcel checkDate(ShopUnitExcel shopUnitExcel) throws ParseException {
        if (!this.isThisDateValid(shopUnitExcel.getFromDate())) {
            shopUnitExcel.setError(I18N.get("common.excel.error.errorDate") + " " + I18N.get("common.excel.column.error") + " E");
        } else if (shopUnitExcel.getToDate() != null && !this.isThisDateValid(shopUnitExcel.getToDate())) {
            shopUnitExcel.setError(I18N.get("common.excel.error.errorDate") + " " + I18N.get("common.excel.column.error") + " F");
        } else if (!this.checkFromDateToDate(shopUnitExcel.getFromDate(), Constants.MAX_DATE)) {
            shopUnitExcel.setError(I18N.get("common.excel.date.range.error") + " " + I18N.get("common.excel.column.error") + " E");
        }else if (!this.checkFromDateToDate(Constants.MIN_DATE, shopUnitExcel.getFromDate())) {
            shopUnitExcel.setError(I18N.get("common.excel.date.range.error") + " " + I18N.get("common.excel.column.error") + " E");
        }
//        else if (shopUnitExcel.getToDate() != null && !this.isThisDateValid( shopUnitExcel.getToDate())) {
//            shopUnitExcel.setError(I18N.get("common.table.warning.value.error") + " F");
//        }
        else if (shopUnitExcel.getToDate() != null && !this.checkFromDateToDate(shopUnitExcel.getToDate(), Constants.MAX_DATE)) {
            shopUnitExcel.setError(I18N.get("common.excel.date.range.error") + " " + I18N.get("common.excel.column.error") + " F");
        } else if (shopUnitExcel.getToDate() != null && !this.checkFromDateToDate(Constants.MIN_DATE, shopUnitExcel.getToDate())) {
            shopUnitExcel.setError(I18N.get("common.excel.date.range.error") + " " + I18N.get("common.excel.column.error") + " F");
        } else if (!this.checkDateOfSerivce(shopUnitExcel.getFromDate())) {
            shopUnitExcel.setError(I18N.get("common.excel.error.fromDate") + " " + I18N.get("common.excel.column.error") + " E");
        } else if (shopUnitExcel.getToDate() != null && !this.checkDateOfSerivce(shopUnitExcel.getToDate())) {
            shopUnitExcel.setError(I18N.get("common.excel.error.toDate") + " " + I18N.get("common.excel.column.error") + " F");
        } else if (shopUnitExcel.getToDate() != null && !this.checkFromDateToDate(shopUnitExcel.getFromDate(), shopUnitExcel.getToDate())) {
            shopUnitExcel.setError(I18N.get("common.excel.error.fromDateToDate") + " " + I18N.get("common.excel.column.error") + " F");
        }
        return shopUnitExcel;
    }

    @Override
    public Long getParentIdById(Long id) throws Exception {
        return serviceRepo.findParentIdById(id);
    }

}
