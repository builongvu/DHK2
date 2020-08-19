package vn.vissoft.dashboard.repo;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.vissoft.dashboard.model.ConfigRolesStaff;

import java.util.List;
import java.util.Optional;

public interface ConfigRolesStaffRepo  extends JpaRepository<ConfigRolesStaff,Long> {

    @Cacheable(value = "allRolesStaffCache")
    public List<ConfigRolesStaff> findAll();

    List<ConfigRolesStaff> getAllByStaffCode(String staffCode);

    List<ConfigRolesStaff> getAllByStatus(int status);

    Optional<ConfigRolesStaff> getByRoleIdAndStaffCode(Long roleId, String staffcode);

}
