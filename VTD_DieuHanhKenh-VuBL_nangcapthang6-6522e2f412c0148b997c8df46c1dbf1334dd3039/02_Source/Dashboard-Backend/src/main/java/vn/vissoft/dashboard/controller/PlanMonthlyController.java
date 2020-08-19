package vn.vissoft.dashboard.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.vissoft.dashboard.config.I18N;
import vn.vissoft.dashboard.dto.*;
import vn.vissoft.dashboard.dto.excel.LevelServiceExcel;
import vn.vissoft.dashboard.dto.excel.VTTServiceExcel;
import vn.vissoft.dashboard.helper.ApiResponse;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.helper.DownloadFileFromSystem;
import vn.vissoft.dashboard.helper.excelgenerator.DetailsReportExcelWriter;
import vn.vissoft.dashboard.helper.excelgenerator.VTTExcelWriter;
import vn.vissoft.dashboard.model.PlanMonthly;
import vn.vissoft.dashboard.model.PlanQuarterly;
import vn.vissoft.dashboard.model.PlanYearly;
import vn.vissoft.dashboard.repo.PlanMonthlyRepo;
import vn.vissoft.dashboard.services.PlanMonthlyService;
import vn.vissoft.dashboard.services.PlanQuarterlyService;
import vn.vissoft.dashboard.services.PlanYearlyService;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("${api_base_path}/management/planmonthly")
public class PlanMonthlyController {

    @Value("${server.tomcat.basedir}")
    private String path;

    private DetailsReportExcelWriter detailsReportExcelWriter = new DetailsReportExcelWriter();
    private DownloadFileFromSystem download = new DownloadFileFromSystem();
    private static final Logger LOGGER = LogManager.getLogger(PlanMonthlyController.class);

    @Autowired
    private VTTExcelWriter vttExcelWriter;

    @Autowired
    private PlanMonthlyService planMonthlyService;


    @Autowired
    private PlanQuarterlyService planQuarterlyService;

    @Autowired
    private PlanYearlyService planYearlyService;

    @Autowired
    private PlanMonthlyRepo planMonthlyRepo;

    /**
     * lay ra danh sach ke hoach thang(plan_monthly) theo đieu kien tim kiem
     *
     * @param planMonthlyDTO
     * @return ApiResponse
     * @author VuBL
     * @since 2019/09
     */
    @PostMapping("/getPlanMonthlyByCondition")
    public ApiResponse getPlanMonthlysByCondition(Authentication authentication, @RequestBody PlanMonthlyDTO planMonthlyDTO) {
        try {
            ApiResponse apiResponse = null;
            StaffDTO user = (StaffDTO) authentication.getPrincipal();
            String vstrCycleCode = planMonthlyDTO.getCycleCode();
            if (vstrCycleCode.equals(Constants.CYCLE_CODE.MONTH)) {
                List<PlanMonthlyDTO> vlstPlanMonthlys = planMonthlyService.getPlanMonthlyByCondition(planMonthlyDTO, user);
                apiResponse = ApiResponse.build(HttpServletResponse.SC_OK, true, "", vlstPlanMonthlys);
                apiResponse.setPage(planMonthlyDTO.getPager().getPage());
                apiResponse.setPageSize(planMonthlyDTO.getPager().getPageSize());
                apiResponse.setTotalRow(planMonthlyService.countPlanMonthlysByCondition(planMonthlyDTO, user).intValue());
            }
            if (vstrCycleCode.equals(Constants.CYCLE_CODE.QUARTER)) {
                List<PlanQuarterlyDTO> vlstPlanQuarterlys = planQuarterlyService.getPlanQuarterlyByCondition(planMonthlyDTO, user);
                apiResponse = ApiResponse.build(HttpServletResponse.SC_OK, true, "", vlstPlanQuarterlys);
                apiResponse.setPage(planMonthlyDTO.getPager().getPage());
                apiResponse.setPageSize(planMonthlyDTO.getPager().getPageSize());
                apiResponse.setTotalRow(planQuarterlyService.countPlanQuarterlysByCondition(planMonthlyDTO, user).intValue());
            }
            if (vstrCycleCode.equals(Constants.CYCLE_CODE.YEAR)) {
                List<PlanYearlyDTO> vlstPlanYearlys = planYearlyService.getPlanYearlyByCondition(planMonthlyDTO, user);
                apiResponse = ApiResponse.build(HttpServletResponse.SC_OK, true, "", vlstPlanYearlys);
                apiResponse.setPage(planMonthlyDTO.getPager().getPage());
                apiResponse.setPageSize(planMonthlyDTO.getPager().getPageSize());
                apiResponse.setTotalRow(planYearlyService.countPlanYearlysByCondition(planMonthlyDTO, user).intValue());
            }
            return apiResponse;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    /**
     * download file tu url he thong
     *
     * @param fileName
     * @return
     * @author DatNT
     * @version 1.0
     * @since 13/9/2019
     */
    @GetMapping("/downloadFileFromSystem")
    public ResponseEntity<InputStreamResource> downloadFile(@RequestParam String fileName) {
        ByteArrayInputStream in = null;

        try {
            in = download.downloadFile(path, fileName);
            return ResponseEntity.ok().body(new InputStreamResource(in));
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    /**
     * download file chi tieu vds
     *
     * @param planMonthly
     * @return
     * @author DatNT
     * @version 1.0
     * @since 15/9/2019
     */
    @PostMapping(value = "/downloadVDS")
    public ResponseEntity<InputStreamResource> excelVTTReport(Authentication authentication, @RequestBody PlanMonthlyDTO planMonthly) {

        ByteArrayInputStream in = null;
        List vlstPlanMonthlys = null;

        try {
            StaffDTO user = (StaffDTO) authentication.getPrincipal();
            if (planMonthly.getPager() == null) {
                planMonthly.setPager(new Pager());
            }
            planMonthly.getPager().setPageSize(65000);
            planMonthly.getPager().setPage(1);
            vlstPlanMonthlys = planMonthlyService.getTemplateFileVDS(planMonthly, user);
            in = vttExcelWriter.write(VTTServiceExcel.class, vlstPlanMonthlys, planMonthly.getCycleCode());
            return ResponseEntity.ok().body(new InputStreamResource(in));

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    /**
     * download file chi tieu cac cap
     *
     * @param planMonthly
     * @return
     * @author DatNT
     * @version 1.0
     * @since 15/9/2019
     */
    @PostMapping("/downloadLevel")
    public ResponseEntity<InputStreamResource> excelLevelReport(Authentication authentication, @RequestBody PlanMonthlyDTO planMonthly) {

        ByteArrayInputStream in = null;

        List planMonthlys = null;

        try {
            StaffDTO user = (StaffDTO) authentication.getPrincipal();
            if (planMonthly.getPager() == null) {
                planMonthly.setPager(new Pager());
            }
            planMonthly.getPager().setPageSize(65000);
            planMonthly.getPager().setPage(1);
            planMonthlys = planMonthlyService.getTemplateFileLevel(planMonthly, user);
            in = vttExcelWriter.write(LevelServiceExcel.class, planMonthlys, planMonthly.getCycleCode());
            return ResponseEntity.ok().body(new InputStreamResource(in));

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    /**
     * upload file giao chi tieu vds
     *
     * @param file
     * @return
     * @author DatNT
     * @version 1.0
     * @since 15/9/2019
     */
    @PostMapping("/uploadVDS")
    public ApiResponse uploadVDS(Authentication authentication, @RequestParam("file") MultipartFile file) {
        try {
            StaffDTO user = (StaffDTO) authentication.getPrincipal();
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", planMonthlyService.upload(file, VTTServiceExcel.class, user));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }

    }

    /**
     * upload file giao chi tieu cac cap
     *
     * @param file
     * @return
     * @author DatNT
     * @version 1.0
     * @since 15/9/2019
     */
    @PostMapping("/uploadLevel")
    public ApiResponse uploadLevel(Authentication authentication, @RequestParam("file") MultipartFile file) {

        try {
            StaffDTO user = (StaffDTO) authentication.getPrincipal();
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", planMonthlyService.upload(file, LevelServiceExcel.class, user));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    /**
     * update du lieu giao chi tieu vds va cac cap theo thang
     *
     * @param plstPlanMonthly
     * @param
     * @return
     * @author HungNN
     * @version 1.0
     * @since 20/09/2019
     */
    @PutMapping("/updatePlanMonthly")
    public ApiResponse updatePlanMonthly(Authentication authentication, @Valid @RequestBody List<PlanMonthly> plstPlanMonthly) {
        ApiResponse apiResponse = null;
        try {
            StaffDTO user = (StaffDTO) authentication.getPrincipal();
            String message = planMonthlyService.updatePlanMonthly(plstPlanMonthly, user);
            if (message.matches("[0-9]+")) {
                apiResponse = ApiResponse.build(HttpServletResponse.SC_OK, true, "", I18N.get("schedule.update.success") + " " + message + " " + I18N.get("record"));
                apiResponse.setTotalRow(plstPlanMonthly.size());
            } else {
                apiResponse = ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, "", message);
            }
            return apiResponse;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }

    }

    /**
     * update du lieu giao chi tieu vds va cac cap theo quy
     *
     * @param plstPlanQuarterlies
     * @param
     * @return
     * @author HungNN
     * @version 1.0
     * @since 20/09/2019
     */
    @PutMapping("/updatePlanQuarterly")
    public ApiResponse updatePlanQuarterly(Authentication authentication,@Valid @RequestBody List<PlanQuarterly> plstPlanQuarterlies) {
        ApiResponse apiResponse = null;
        try {
            StaffDTO user = (StaffDTO) authentication.getPrincipal();
            String message = planQuarterlyService.updatePlanQuarterly(plstPlanQuarterlies, user);
            if (message.matches("[0-9]+")) {
                apiResponse = ApiResponse.build(HttpServletResponse.SC_OK, true, "", I18N.get("schedule.update.success") + " " + message + " " + I18N.get("record"));
                apiResponse.setTotalRow(plstPlanQuarterlies.size());
            } else {
                apiResponse = ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, "", message);
            }
            return apiResponse;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }

    }

    /**
     * update du lieu giao chi tieu vds va cac cap theo nam
     *
     * @param plstPlanYearlies
     * @param
     * @return
     * @author HungNN
     * @version 1.0
     * @since 20/09/2019
     */
    @PutMapping("/updatePlanYearly")
    public ApiResponse updatePlanYearly(Authentication authentication,@Valid @RequestBody List<PlanYearly> plstPlanYearlies) {

        ApiResponse apiResponse = null;
        try {
            StaffDTO user = (StaffDTO) authentication.getPrincipal();
            String message = planYearlyService.updatePlanYearly(plstPlanYearlies, user);
            if (message.matches("[0-9]+")) {
                apiResponse = ApiResponse.build(HttpServletResponse.SC_OK, true, "", I18N.get("schedule.update.success") + " " + message + " " + I18N.get("record"));
                apiResponse.setTotalRow(plstPlanYearlies.size());
            } else {
                apiResponse = ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, "", message);
            }
            return apiResponse;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }

    }

    /**
     * xoa du lieu giao chi tieu vds va cac cap theo quy
     *
     * @param plstPlanQuarterlies
     * @param
     * @return
     * @author DatNT
     * @version 1.0
     * @since 08/11/2019
     */
    @PostMapping("/deletePlanQuarterly")
    public ApiResponse deletePlanQuarterly(@RequestBody List<PlanQuarterly> plstPlanQuarterlies) {

        try {
            ApiResponse apiResponse = ApiResponse.build(HttpServletResponse.SC_OK, true, "", planQuarterlyService.deletePlanQuarterly(plstPlanQuarterlies));
            return apiResponse;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }

    }

    /**
     * xoa du lieu giao chi tieu vds va cac cap theo nam
     *
     * @param plstPlanYearlies
     * @param
     * @return
     * @author DatNT
     * @version 1.0
     * @since 08/11/2019
     */
    @PostMapping("/deletePlanYearly")
    public ApiResponse deletePlanYearly(@RequestBody List<PlanYearly> plstPlanYearlies) {

        try {
            ApiResponse apiResponse = ApiResponse.build(HttpServletResponse.SC_OK, true, "", planYearlyService.deletePlanYearly(plstPlanYearlies));
            return apiResponse;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }

    }

    /**
     * xoa du lieu giao chi tieu vds va cac cap theo thang
     *
     * @param plstPlanMonthly
     * @param
     * @return
     * @author DatNT
     * @version 1.0
     * @since 08/11/2019
     */
    @PostMapping("/deletePlanMonthly")
    public ApiResponse deletePlanMonthly(@RequestBody List<PlanMonthly> plstPlanMonthly) {

        try {
            ApiResponse apiResponse = ApiResponse.build(HttpServletResponse.SC_OK, true, "", planMonthlyService.deletePlanMonthly(plstPlanMonthly));
            return apiResponse;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }


    @GetMapping("/getByServiceId")
    public List<PlanMonthly> getByServiceId(@RequestParam("id") Long id) {
        return planMonthlyRepo.getByServiceId(id);
    }

}
