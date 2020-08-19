package vn.vissoft.dashboard.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.vissoft.dashboard.model.ManageInfoPartner;
import vn.vissoft.dashboard.model.Unit;

import java.util.List;
import java.util.Optional;

@Repository
public interface UnitRepo extends JpaRepository<Unit, Long>, UnitRepoCustom {

    Optional<Unit> getByCodeAndStatus(String code, String status);

    Optional<Unit> getById(Long id);

    List<Unit> getAllByOrderByName();
}
