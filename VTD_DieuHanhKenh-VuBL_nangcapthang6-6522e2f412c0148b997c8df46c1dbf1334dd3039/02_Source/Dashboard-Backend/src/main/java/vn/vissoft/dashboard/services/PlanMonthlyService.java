package vn.vissoft.dashboard.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.multipart.MultipartFile;
import vn.vissoft.dashboard.dto.DashboardRequestDTO;
import vn.vissoft.dashboard.dto.PlanMonthlyDTO;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.chart.PointDTO;
import vn.vissoft.dashboard.dto.excel.BaseExcelEntity;
import vn.vissoft.dashboard.dto.excel.BaseUploadEntity;
import vn.vissoft.dashboard.model.PlanMonthly;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.List;

public interface PlanMonthlyService {
    Logger LOGGER = LogManager.getLogger(PlanMonthlyService.class);

    List<PlanMonthlyDTO> getPlanMonthlyByCondition(PlanMonthlyDTO planMonthlyDTO, StaffDTO staffDTO) throws Exception;

    List<PlanMonthlyDTO> getPlanMonthlyByReportSql(PlanMonthlyDTO planMonthlyDTO) throws Exception;

    BigInteger countPlanMonthlysByCondition(PlanMonthlyDTO planMonthlyDTO, StaffDTO staffDTO) throws Exception;

    String updatePlanMonthly(List<PlanMonthly> plstPlanMonthly,StaffDTO staffDTO) throws Exception;

    int deletePlanMonthly(List<PlanMonthly> plstPlanMonthly) throws Exception;

    BaseUploadEntity upload(MultipartFile file, Class pclsClazz, StaffDTO staffDTO) throws Exception;

    List<PlanMonthlyDTO> getTemplateFileVDS(PlanMonthlyDTO planMonthlyDTO, StaffDTO staffDTO) throws Exception;

    List<PlanMonthlyDTO> getTemplateFileLevel(PlanMonthlyDTO planMonthlyDTO, StaffDTO staffDTO) throws Exception;

    void saveFile(InputStream inputStream, String pstrPath) throws IOException;

    void checkTheSameRecord(List<? extends BaseExcelEntity> plstList);

    boolean checkShopCodeOfProvince(StaffDTO staffDTO) throws  Exception;

}
