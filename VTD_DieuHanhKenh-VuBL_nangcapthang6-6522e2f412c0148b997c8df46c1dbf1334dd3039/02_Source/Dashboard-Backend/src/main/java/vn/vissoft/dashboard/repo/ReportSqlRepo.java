package vn.vissoft.dashboard.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vissoft.dashboard.model.Product;
import vn.vissoft.dashboard.model.ReportSql;

@Repository
public interface ReportSqlRepo extends JpaRepository<ReportSql, Long>, ReportSqlRepoCustom {

}

