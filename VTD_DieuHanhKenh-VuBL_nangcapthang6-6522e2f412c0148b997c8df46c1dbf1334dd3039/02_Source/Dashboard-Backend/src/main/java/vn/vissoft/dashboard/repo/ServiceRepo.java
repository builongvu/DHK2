package vn.vissoft.dashboard.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.vissoft.dashboard.model.ConfigObjects;
import vn.vissoft.dashboard.model.Service;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRepo extends JpaRepository<Service, Long>, ServiceRepoCustom {

    List<Service> findByGroupServiceIdAndStatus(Long plngGroupServiceId, String pstrStatus);

    Service findByCode(String pstrCode);

    @Query(value = "select * from service as u where u.status = '1' ", nativeQuery = true)
    List<Service> findAllByStatus();

    @Query(value = "select * from service as u where u.status = '1' and id = :id ", nativeQuery = true)
    List<Service> findAllById(@Param("id") String mlngId);

    @Query(value = "select * from service as u where u.status = '1' and code = :code ", nativeQuery = true)
    List<Service> findAllByCode(@Param("code") String mstrCode);

    List<Service> findAllByStatusNotLike(String status);




}
