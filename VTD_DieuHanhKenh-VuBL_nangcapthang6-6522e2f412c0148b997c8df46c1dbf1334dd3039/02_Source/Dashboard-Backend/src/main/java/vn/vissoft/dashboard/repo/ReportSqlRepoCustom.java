package vn.vissoft.dashboard.repo;

import java.util.List;

import vn.vissoft.dashboard.dto.SqlReportDetailDTO;
import vn.vissoft.dashboard.model.ReportSql;

public interface ReportSqlRepoCustom {

    List<ReportSql> findActiveReportSql();
    
    ReportSql findReportSqlById(Long id);

    boolean checkExistedReportSqlCode(String pstrCode);

    List<Object[]> getContentSql(SqlReportDetailDTO sqlReportDetailDTO);

    List<String> getColumnName(String sql);


}
