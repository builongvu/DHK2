package vn.vissoft.dashboard.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vissoft.dashboard.model.VdsGroupChannel;

@Repository
public interface VdsGroupChannelRepo extends JpaRepository<VdsGroupChannel, Long> {
}
