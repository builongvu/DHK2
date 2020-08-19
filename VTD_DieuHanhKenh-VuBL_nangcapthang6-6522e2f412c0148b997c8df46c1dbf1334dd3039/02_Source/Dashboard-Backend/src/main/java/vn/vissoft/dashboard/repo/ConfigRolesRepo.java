package vn.vissoft.dashboard.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.vissoft.dashboard.model.ConfigRoles;
import vn.vissoft.dashboard.model.ConfigRolesObjects;

import java.util.List;
import java.util.Optional;

public interface ConfigRolesRepo extends JpaRepository<ConfigRoles,Long>, ConfigRolesRepoCustom {

    @Override
    Optional getRoleByCode(String code);

    List<ConfigRoles> getAllByStatus(Long status);

    List<ConfigRoles> findAllByStatusOrderByRoleName(Long status);

    List<ConfigRoles> findAllByOrderByRoleName();
}
