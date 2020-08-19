package vn.vissoft.dashboard.controller;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.vissoft.dashboard.dto.*;
import vn.vissoft.dashboard.dto.SqlReportTemplateDTO;
import vn.vissoft.dashboard.dto.excel.DetailsReportServiceExcel;
import vn.vissoft.dashboard.helper.ApiResponse;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.helper.excelgenerator.DetailsReportExcelWriter;
import vn.vissoft.dashboard.helper.excelgenerator.TemplateReportExcelWriter;
import vn.vissoft.dashboard.model.ReportSql;
import vn.vissoft.dashboard.model.VdsStaff;
import vn.vissoft.dashboard.repo.VdsStaffRepo;
import vn.vissoft.dashboard.repo.VdsStaffRepoCustom;
import vn.vissoft.dashboard.repo.impl.VdsStaffRepoImpl;
import vn.vissoft.dashboard.services.BusinessResultsService;
import vn.vissoft.dashboard.services.PartnerService;
import vn.vissoft.dashboard.services.ReportSqlService;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.util.List;

@RestController
@RequestMapping("${api_base_path}/management/reportSql")
public class ReportSqlController {

    private static final Logger LOGGER= LogManager.getLogger(ReportSqlController.class);
    private DetailsReportExcelWriter detailsReportExcelWriter = new DetailsReportExcelWriter();
    private TemplateReportExcelWriter templateReportExcelWriter = new TemplateReportExcelWriter();
    @Autowired
    private ReportSqlService reportSqlService;

    @Autowired
    private PartnerService partnerService;

    /**
     * lay ra danh sach sql content(report sql) trong combobox san pham cua details report
     *
     * @author QuangND
     * @since 2019/09
     * @return ApiResponse
     */
    @GetMapping("/getByStatus")
    public ApiResponse getByStatus() {
        List<ReportSql> vlstReportSqls = reportSqlService.getActiveReportSql();
        if(DataUtil.isNullOrEmpty(vlstReportSqls)) {
            return ApiResponse.build(HttpServletResponse.SC_NO_CONTENT, false, "content is not found", null);
        }
        return ApiResponse.build(HttpServletResponse.SC_OK, true, "", vlstReportSqls);
    }

    /**
     * lay ra data theo id
     *
     * @author QuangND
     * @since 2019/09
     * @return ApiResponse
     */
    @GetMapping("/getById/{id}")
    public ApiResponse getById(@PathVariable Long id) {
        ReportSql vlstReportSqls = reportSqlService.findReportSqlById(id);
        return ApiResponse.build(HttpServletResponse.SC_OK, true, "", vlstReportSqls);
    }

    /**
     * lay ra data theo cau sql content
     *
     * @author QuangND
     * @since 2019/09
     * @return ApiResponse
     */
    @GetMapping("/getResultSql")
    public ApiResponse getResultSql(@RequestBody SqlReportDetailDTO sqlReportDetailDTO) {
        List<Object[]> vlstReportSqls = reportSqlService.getContentSql(sqlReportDetailDTO);
        if(DataUtil.isNullOrEmpty(vlstReportSqls)) {
            return ApiResponse.build(HttpServletResponse.SC_NO_CONTENT, false, "content is not found", null);
        }
        return ApiResponse.build(HttpServletResponse.SC_OK, true, "", vlstReportSqls);
    }


    /**
     * download file
     *
     * @author QuangND
     * @since 2019/09
     * @return ApiResponse
     */
    @PostMapping("/downloadDetailsReport")
    public ResponseEntity<InputStreamResource> excelDetailsReport(@RequestBody SqlReportDetailDTO sqlReportDetailDTO) {
        ByteArrayInputStream in = null;
        try {
            List<Object[]> vlstReportSqls = reportSqlService.getContentSql(sqlReportDetailDTO);
            ReportSql vobjReportSql = reportSqlService.findReportSqlById(sqlReportDetailDTO.getIdReport());

            String shopCode = "'" + sqlReportDetailDTO.getShopCode() + "'";
            String sql =  vobjReportSql.getContent().replace(":fromDate", String.valueOf(sqlReportDetailDTO.getFromDate()))
                    .replace(":toDate", String.valueOf(sqlReportDetailDTO.getToDate()))
                    .replace(":shopCode", (shopCode));

            List<String> vlstListColumn = reportSqlService.getColumnName(sql);
            String vstrShopName = partnerService.getNameByShopAndChannel(sqlReportDetailDTO.getShopCode());
            in = detailsReportExcelWriter.write(DetailsReportServiceExcel.class, vlstReportSqls, sqlReportDetailDTO, vobjReportSql, vlstListColumn, vstrShopName);
            return ResponseEntity.ok().body(new InputStreamResource(in));

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @Autowired
    BusinessResultsService businessResultService;

    /**
     * download unit sum file
     *
     * @author AnhNQ
     * @since 2020/07
     * @return ApiResponse
     */
    @PostMapping("/downloadTemplateReport")
    public ResponseEntity<InputStreamResource> excelTemplateReport(@RequestBody SqlReportTemplateDTO sqlReportTemplateDTO) {
        ByteArrayInputStream in = null;
        try {
            VdsStaff staff = businessResultService.findVdsStaffByCondition(sqlReportTemplateDTO.getUser());
            if(staff!=null)
            {
                sqlReportTemplateDTO.setUser(staff.getStaffName());
            }
            DashboardRequestDTO dbDto = new DashboardRequestDTO();
            dbDto.setMonthYear(sqlReportTemplateDTO.getMonth());
            dbDto.setVdsChannelCode("VDS_TINH");
            if(sqlReportTemplateDTO.getType().equals(TemplateReportExcelWriter.PROVINCE_UNIT_SUM))
            {
                ResultsProvincialDTO resultsProvincialDTO = businessResultService.getResultProvincial(dbDto);
                in = templateReportExcelWriter.write(DetailsReportServiceExcel.class, sqlReportTemplateDTO, resultsProvincialDTO);
                return ResponseEntity.ok().body(new InputStreamResource(in));
            }
            else if(sqlReportTemplateDTO.getType().equals(TemplateReportExcelWriter.STAFF_UNIT_SUM))
            {
                ResultsStaffDTO resultsStaffDTO = businessResultService.getResultsStaff(dbDto);
                in = templateReportExcelWriter.write(DetailsReportServiceExcel.class, sqlReportTemplateDTO, resultsStaffDTO);
                return ResponseEntity.ok().body(new InputStreamResource(in));
            }
            else
            {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
