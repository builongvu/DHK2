package vn.vissoft.dashboard.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.vissoft.dashboard.dto.ReportSqlDTO;
import vn.vissoft.dashboard.dto.SqlReportDetailDTO;
import vn.vissoft.dashboard.mapper.BaseMapper;
import vn.vissoft.dashboard.model.ReportSql;
import vn.vissoft.dashboard.repo.ReportSqlRepo;
import vn.vissoft.dashboard.services.ReportSqlService;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Service
public class ReportSqlServiceImpl implements ReportSqlService {

    @Autowired
    private ReportSqlRepo reportSqlRepo;

    private BaseMapper<ReportSql, ReportSqlDTO> mapper = new BaseMapper<ReportSql, ReportSqlDTO>(ReportSql.class, ReportSqlDTO.class);

    @Override
    public List<ReportSql> findAll() {
        return reportSqlRepo.findAll();
    }

    @Override
    public List<ReportSql> getActiveReportSql() {
        return reportSqlRepo.findActiveReportSql();
    }

    @Override
    public ReportSql getReportSqlById(Long id) {
        return reportSqlRepo.findReportSqlById(id);
    }

    public List<String> getColumnNames(String sql) {
        return reportSqlRepo.getColumnName(sql);
    }

    @Override
    public List<Object[]> getContentSql(SqlReportDetailDTO sqlReportDetailDTO) {
        return reportSqlRepo.getContentSql(sqlReportDetailDTO);
    }

    @Override
    public ReportSql findReportSqlById(Long id) {
        return reportSqlRepo.findReportSqlById(id);
    }

    @Override
    public List<String> getColumnName(String sql) {
        return reportSqlRepo.getColumnName(sql);
    }


}
