package vn.vissoft.dashboard.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.vissoft.dashboard.model.Service;
import vn.vissoft.dashboard.model.WarningContent;

import java.util.List;

@Repository
public interface WarningContentRepo extends JpaRepository<WarningContent, Long> {
    List<WarningContent> getByMstrStatusEquals(String mstrStatus);
}
