package vn.vissoft.dashboard.repo;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.vissoft.dashboard.model.ConfigObjects;

import java.util.List;
import java.util.Optional;

public interface ConfigObjectsRepo extends JpaRepository<ConfigObjects,Long> {
    @Cacheable(value = "allObjectsCache",
    key = "#status",
    condition = "#status==1")
    public List<ConfigObjects> findAllByStatusOrderByOrd(Long status);

    List<ConfigObjects> findAllByStatusNotLike(Long status);

    List<ConfigObjects> findAllByStatusLike(Long status);

    Optional<ConfigObjects> getById(Long id);

    List<ConfigObjects> getAllByParentIdAndStatusNotLike(Long parentId, Long status);

    List<ConfigObjects> findAllByStatusAndObjectTypeLikeOrderByObjectName(Long status, String objectType);
}
