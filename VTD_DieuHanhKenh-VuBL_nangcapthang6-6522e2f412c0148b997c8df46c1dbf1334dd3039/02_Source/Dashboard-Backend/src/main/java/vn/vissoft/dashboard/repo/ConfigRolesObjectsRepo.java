package vn.vissoft.dashboard.repo;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.vissoft.dashboard.model.ConfigRolesObjects;

import java.util.List;
import java.util.Optional;

public interface ConfigRolesObjectsRepo extends JpaRepository<ConfigRolesObjects,Long> {

    @Cacheable(value = "allRolesObjectCache")
    List<ConfigRolesObjects> findAll();

    @Query(value = "SELECT * FROM config_roles_objects WHERE status = 1 AND role_id = :roleId", nativeQuery = true)
    List<ConfigRolesObjects> getByObjectIdAndStatus(@Param("roleId") Long roleId);

    List<ConfigRolesObjects> getAllByRoleId(Long objectId);

    Optional<ConfigRolesObjects> getByObjectId(Long objectId);
}
