package vn.vissoft.dashboard.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.vissoft.dashboard.config.I18N;
import vn.vissoft.dashboard.dto.ApParamDTO;
import vn.vissoft.dashboard.dto.ShopUnitDTO;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.excel.*;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.helper.excelreader.ExcelReader;
import vn.vissoft.dashboard.model.*;
import vn.vissoft.dashboard.repo.ActionDetailRepo;
import vn.vissoft.dashboard.repo.PartnerRepo;
import vn.vissoft.dashboard.repo.ShopUnitRepo;
import vn.vissoft.dashboard.repo.UnitRepo;
import vn.vissoft.dashboard.services.*;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ShopUnitServiceImpl implements ShopUnitService {

    @Value("${server.tomcat.basedir}")
    private String mstrUploadPath;

    @Autowired
    private ShopUnitRepo shopUnitRepo;

    @Autowired
    private UnitRepo unitRepo;

    @Autowired
    private PartnerRepo partnerRepo;

    @Autowired
    private ActionDetailRepo actionDetailRepo;

    @Autowired
    private ActionAuditService actionAuditService;

    @Autowired
    private ActionDetailService actionDetailService;

    @Autowired
    private WarningSendService warningSendService;

    @Autowired
    private ServiceService serviceService;

    @Override
    public List<ShopUnit> getAll() {
        return shopUnitRepo.findAll();
    }

    @Override
    public List<ShopUnit> getAllByCondition(ShopUnitDTO shopUnitDTO) throws ParseException {
        return shopUnitRepo.getAllByCondition(shopUnitDTO);
    }

    @Override
    public Long countByCondition(ShopUnitDTO shopUnitDTO) {
        return shopUnitRepo.countByCondition(shopUnitDTO);
    }

    @Override
    public boolean addShopUnit(ShopUnit shopUnit, StaffDTO staffDTO) throws Exception {
        if (checkDuplicate(shopUnit)) {
            return true;
        }
        ShopUnit shopUnit1 = shopUnitRepo.saveAndFlush(shopUnit);
        saveNewAction(shopUnit1, staffDTO);
        return false;
    }

    @Override
    public boolean updateShopUnit(ShopUnit shopUnit, StaffDTO staffDTO) throws Exception {
        if (checkDuplicate(shopUnit)) {
            return true;
        }
        Optional<ShopUnit> optional = shopUnitRepo.getByMlngId(shopUnit.getMlngId());
        if (optional.isPresent()) {
            ShopUnit shopUnit1 = optional.get();
            ActionAudit actionAudit = actionAuditService.log(Constants.SHOP_UNIT.SHOP_UNIT, Constants.ACTION_CODE_EDIT, staffDTO.getStaffCode(), shopUnit1.getMlngId(), staffDTO.getShopCode());
            saveActionDetail(shopUnit1, shopUnit, actionAudit.getId());
        }
        return false;
    }

    @Override
    public ShopUnit getById(Long id) {
        Optional<ShopUnit> optional = shopUnitRepo.getByMlngId(id);
        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
    }

    @Override
    public void lockUnlock(String[] id, String status, StaffDTO staffDTO) {
        for (String shopId : id) {
            ShopUnit shopUnit = getById(Long.parseLong(shopId));
            if (!shopUnit.getMstrStatus().equals(status)) {
                shopUnit.setMstrStatus(status);
                shopUnitRepo.save(shopUnit);
                ActionAudit actionAudit = actionAuditService.log(Constants.SHOP_UNIT.SHOP_UNIT, Constants.ACTION_CODE_EDIT, staffDTO.getStaffCode(), shopUnit.getMlngId(), staffDTO.getShopCode());
                if (Constants.PARAM_STATUS_0.equals(status)) {
                    actionDetailService.createActionDetail(Constants.SHOP_UNIT.STATUS, Constants.PARAM_STATUS_0, actionAudit.getId(), Constants.PARAM_STATUS);
                } else {
                    actionDetailService.createActionDetail(Constants.SHOP_UNIT.STATUS, Constants.PARAM_STATUS, actionAudit.getId(), Constants.PARAM_STATUS_0);
                }
            }
        }
    }

    @Override
    public boolean checkDuplicate(ShopUnit shopUnit) {
        Long id = -1L;
        if (shopUnit.getMlngId() != null) {
            id = shopUnit.getMlngId();
        }
        BigInteger count = shopUnitRepo.countAllByMlngServiceIdAndMstrShopCodeAndMlngIdNotLike(shopUnit.getMlngServiceId(), shopUnit.getMstrShopCode(), id);
        if (count.intValue() > 0) {
            return true;
        }
        return false;
    }

    @Override
    public BaseShopUnit upload(MultipartFile file, StaffDTO staffDTO) throws Exception {
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

        ExcelReader<ShopUnitExcel> vreader = new ExcelReader<>();
        List<ShopUnitExcel> shopUnitExcels = vreader.read(paths.toString(), ShopUnitExcel.class);
        List<ShopUnit> list = new ArrayList<>();
        BaseShopUnit baseShopUnit = new BaseShopUnit();
        int vintTotal = 0;
        int vintSuccess = 0;
        checkTheSameRecord(shopUnitExcels);
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.PATTERNDATE);
        for (ShopUnitExcel shopUnitExcel : shopUnitExcels) {
            if (shopUnitExcel == null) {
                continue;
            }
            vintTotal++;
            if (!DataUtil.isNullOrEmpty(shopUnitExcel.getError())) {
                continue;
            }
            ShopUnit shopUnit = new ShopUnit();

//                Check mã chỉ tiêu có tồn tại
            if (DataUtil.isNullOrEmpty(shopUnitExcel.getServiceCode())) {
                shopUnitExcel.setError(I18N.get("common.table.warning.datanull") + " B");
                continue;
            } else {
                List<vn.vissoft.dashboard.model.Service> varrLsv = warningSendService.getByServiceCode(shopUnitExcel.getServiceCode().trim());
                if (varrLsv.size() == 0) {
                    shopUnitExcel.setError(I18N.get("common.table.warning.ServiceError") + " B");
                    continue;
                } else {
                    shopUnit.setMlngServiceId(varrLsv.get(0).getId());
                }
            }
//            Mã đơn vị

            if (DataUtil.isNullOrEmpty(shopUnitExcel.getShopCode())) {
                shopUnitExcel.setError(I18N.get("common.table.warning.datanull") + " C");
                continue;
            } else {
                Optional<ManageInfoPartner> optional = partnerRepo.getByShopCode(shopUnitExcel.getShopCode().trim());
                if (optional.isPresent() && Constants.PARAM_STATUS.equals(optional.get().getStatus())) {
                    shopUnit.setMstrShopCode(shopUnitExcel.getShopCode().trim());
                    shopUnit.setMstrVdsChannelCode(optional.get().getVdsChannelCode());
                } else {
                    shopUnitExcel.setError(I18N.get("common.table.warning.ServiceError") + " C");
                    continue;
                }
            }

//            Đơn vị tính

            if (DataUtil.isNullOrEmpty(shopUnitExcel.getUnitCode())) {
                shopUnitExcel.setError(I18N.get("common.table.warning.datanull") + " D");
                continue;
            } else {
                Optional<Unit> optional = unitRepo.getByCodeAndStatus(shopUnitExcel.getUnitCode().trim(), Constants.PARAM_STATUS);
                if (optional.isPresent()) {
                    shopUnit.setMstrUnitCode(shopUnitExcel.getUnitCode().trim());
                } else {
                    shopUnitExcel.setError(I18N.get("common.table.warning.ServiceError") + " D");
                    continue;
                }
            }
//            Check Ngày
            if (DataUtil.isNullOrEmpty(shopUnitExcel.getFromDate())) {
                shopUnitExcel.setError(I18N.get("common.table.warning.datanull") + " E");
                continue;
            }
            shopUnitExcel = serviceService.checkDate(shopUnitExcel);
            if (!DataUtil.isNullOrEmpty(shopUnitExcel.getError())) {
                continue;
            }
//            shopUnit.setMdtFromDate(dateFormat.parse(shopUnitExcel.getFromDate()));
//            shopUnit.setMdtToDate(dateFormat.parse(shopUnitExcel.getToDate()));
            java.util.Date FDate = sdf.parse(shopUnitExcel.getFromDate().trim());
            shopUnit.setMdtFromDate(new java.sql.Date(FDate.getTime()));
            if (!DataUtil.isNullOrEmpty(shopUnitExcel.getToDate())) {
                java.util.Date toDate = sdf.parse(shopUnitExcel.getToDate().trim());
                shopUnit.setMdtToDate(new java.sql.Date(toDate.getTime()));
            }
            vintSuccess++;
            list.add(shopUnit);
        }

        for (ShopUnit shopUnit : list) {
            Optional<ShopUnit> optional = shopUnitRepo.getByMlngServiceIdAndMstrShopCodeAndMlngIdNotLike(shopUnit.getMlngServiceId(), shopUnit.getMstrShopCode(), -1L);
            shopUnit.setMstrStatus(Constants.PARAM_STATUS);
            if (!optional.isPresent()) {
                addShopUnit(shopUnit, staffDTO);
            } else {
                shopUnit.setMlngId(optional.get().getMlngId());
                updateShopUnit(shopUnit, staffDTO);
            }
        }

        baseShopUnit.setSumSuccessfulRecord(vintSuccess);
        baseShopUnit.setTotal(vintTotal);
        baseShopUnit.setList(list);
        baseShopUnit.setFileName(vstrResultFileName);
        String vstrResultFilePath = vstrPath + "/" + vstrResultFileName;
        vreader.writeResultWarning(paths.toString(), vstrResultFilePath, ShopUnitExcel.class, shopUnitExcels, I18N.get("common.success.status"), 1);

        return baseShopUnit;
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

    private void saveNewAction(ShopUnit shopUnit, StaffDTO staffDTO) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        ActionAudit actionAudit = actionAuditService.log(Constants.SHOP_UNIT.SHOP_UNIT, Constants.ACTION_CODE_ADD, staffDTO.getStaffCode(), shopUnit.getMlngId(), staffDTO.getShopCode());
        actionDetailService.createActionDetail(Constants.SHOP_UNIT.ID, shopUnit.getMlngId().toString(), actionAudit.getId(), null);
        actionDetailService.createActionDetail(Constants.SHOP_UNIT.VDS_CHANNEL_CODE, shopUnit.getMstrVdsChannelCode(), actionAudit.getId(), null);
        actionDetailService.createActionDetail(Constants.SHOP_UNIT.UNIT_CODE, shopUnit.getMstrUnitCode(), actionAudit.getId(), null);
        actionDetailService.createActionDetail(Constants.SHOP_UNIT.SHOP_CODE, shopUnit.getMstrShopCode(), actionAudit.getId(), null);
        actionDetailService.createActionDetail(Constants.SHOP_UNIT.SERVICE_ID, shopUnit.getMlngServiceId().toString(), actionAudit.getId(), null);
        actionDetailService.createActionDetail(Constants.SHOP_UNIT.FROM_DATE, df.format(shopUnit.getMdtFromDate()), actionAudit.getId(), null);
        if (shopUnit.getMdtToDate() != null) {
            actionDetailService.createActionDetail(Constants.SHOP_UNIT.TO_DATE, df.format(shopUnit.getMdtToDate()), actionAudit.getId(), null);
        }
        actionDetailService.createActionDetail(Constants.SHOP_UNIT.STATUS, shopUnit.getMstrStatus(), actionAudit.getId(), null);
    }

    private void saveActionDetail(ShopUnit oldShopUnit, ShopUnit newShopUnit, Long actionId) throws Exception {
        if (!oldShopUnit.getMstrUnitCode().equals(newShopUnit.getMstrUnitCode())) {
            saveActionDetailS(actionId, oldShopUnit.getMstrUnitCode(), newShopUnit.getMstrUnitCode(), Constants.SHOP_UNIT.UNIT_CODE);
        }
        if (!oldShopUnit.getMstrStatus().equals(newShopUnit.getMstrStatus())) {
            saveActionDetailS(actionId, oldShopUnit.getMstrStatus(), newShopUnit.getMstrStatus(), Constants.SHOP_UNIT.STATUS);
        }
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(oldShopUnit.getMdtFromDate().getTime());
        String old = df.format(date);
        String newDate = df.format(newShopUnit.getMdtFromDate());
        if (!old.equals(newDate)) {
            saveActionDetailS(actionId, old, newDate, Constants.SHOP_UNIT.FROM_DATE);
        }
        if (oldShopUnit.getMdtToDate() == null && newShopUnit.getMdtToDate() == null) {
        } else if (oldShopUnit.getMdtToDate() == null) {
            saveActionDetailS(actionId, null, df.format(newShopUnit.getMdtToDate()), Constants.SHOP_UNIT.TO_DATE);
        } else if (newShopUnit.getMdtToDate() == null) {
            saveActionDetailS(actionId, df.format(oldShopUnit.getMdtToDate().getTime()), null, Constants.SHOP_UNIT.TO_DATE);
        } else {
            Date dateT = new Date(oldShopUnit.getMdtToDate().getTime());
            String oldT = df.format(dateT);
            String newTDate = df.format(newShopUnit.getMdtToDate());
            if (!oldT.equals(newTDate)) {
                saveActionDetailS(actionId, oldT, newTDate, Constants.SHOP_UNIT.TO_DATE);
            }
        }


        shopUnitRepo.save(newShopUnit);
    }

    public void saveActionDetailS(Long actionID, String oldValue, String newValue, String column) throws Exception {
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

}
