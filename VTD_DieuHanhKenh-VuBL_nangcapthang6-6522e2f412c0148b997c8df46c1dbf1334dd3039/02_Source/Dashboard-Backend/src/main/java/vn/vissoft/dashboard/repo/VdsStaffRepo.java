package vn.vissoft.dashboard.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vissoft.dashboard.model.VdsStaff;

@Repository
public interface VdsStaffRepo extends JpaRepository<VdsStaff,Long> ,VdsStaffRepoCustom{
}
