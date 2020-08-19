package vn.vissoft.dashboard.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.vissoft.dashboard.model.PlanYearlyHis;

public interface PlanYearlyHisRepo extends JpaRepository<PlanYearlyHis,Long> , PlanYearlyHisRepoCustom {
}
