package vn.vissoft.dashboard.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vissoft.dashboard.model.RevenueDaily;

@Repository
public interface RevenueDailyRepo extends JpaRepository<RevenueDaily, Long>, RevenueDailyRepoCustom {

}
