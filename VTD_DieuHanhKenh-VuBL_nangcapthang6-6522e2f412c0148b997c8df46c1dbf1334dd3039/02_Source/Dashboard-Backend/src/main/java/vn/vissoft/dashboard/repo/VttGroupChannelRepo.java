package vn.vissoft.dashboard.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vissoft.dashboard.model.VttGroupChannel;

@Repository
public interface VttGroupChannelRepo extends JpaRepository<VttGroupChannel, Long>, VttGroupChannelRepoCustom {
    VttGroupChannel findByGroupChannelCode(String pstrChannelCode) throws Exception;
}
