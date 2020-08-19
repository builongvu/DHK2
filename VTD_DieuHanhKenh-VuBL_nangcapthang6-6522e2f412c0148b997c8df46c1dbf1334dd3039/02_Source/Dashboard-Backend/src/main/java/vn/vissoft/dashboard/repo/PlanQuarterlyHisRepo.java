package vn.vissoft.dashboard.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.vissoft.dashboard.model.PlanQuarterlyHis;

public interface PlanQuarterlyHisRepo extends JpaRepository<PlanQuarterlyHis,Long>, PlanQuarterlyHisRepoCustom {
}
