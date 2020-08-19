package vn.vissoft.dashboard.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vissoft.dashboard.model.Staff;

import java.util.List;

@Repository
public interface StaffRepo extends JpaRepository<Staff, Long> ,StaffRepoCustom {

    List<Staff> findByShopId(Long plngShopId) throws Exception;
}
