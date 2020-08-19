package vn.vissoft.dashboard.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vissoft.dashboard.model.ServiceChannel;

import java.util.List;

@Repository
public interface ServiceChannelRepo extends JpaRepository<ServiceChannel,Long>,ServiceChannelRepoCustom {
    List<ServiceChannel> findByVdsChannelCode(String pstrChannelCode);

    List<ServiceChannel> findByServiceId(Long idService);
}
