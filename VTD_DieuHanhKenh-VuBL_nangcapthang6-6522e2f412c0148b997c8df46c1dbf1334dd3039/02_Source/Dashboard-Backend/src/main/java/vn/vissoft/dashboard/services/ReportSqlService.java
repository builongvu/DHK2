package vn.vissoft.dashboard.services;

import vn.vissoft.dashboard.dto.ProductDTO;
import vn.vissoft.dashboard.dto.SqlReportDetailDTO;
import vn.vissoft.dashboard.model.Product;
import vn.vissoft.dashboard.model.ReportSql;

import java.util.List;

public interface ReportSqlService {

    public List<ReportSql> findAll();

    public List<ReportSql> getActiveReportSql();
    
    public ReportSql getReportSqlById(Long id);

    List<String> getColumnNames(String sql);

    List<Object[]> getContentSql(SqlReportDetailDTO sqlReportDetailDTO);

    ReportSql findReportSqlById(Long id);

    List<String> getColumnName(String sql);

}
