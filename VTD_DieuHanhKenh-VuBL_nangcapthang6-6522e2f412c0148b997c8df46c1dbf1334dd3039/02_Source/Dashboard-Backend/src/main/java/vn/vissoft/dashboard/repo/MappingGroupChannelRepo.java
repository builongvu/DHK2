package vn.vissoft.dashboard.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vissoft.dashboard.model.MappingGroupChannel;

import java.util.Optional;

@Repository
public interface MappingGroupChannelRepo extends JpaRepository<MappingGroupChannel,Long>,MappingGroupChannelRepoCustom {
    MappingGroupChannel findByVdsChannelCodeAndGroupChannelCode(String pstrVdsChannelCode,String pstrVttChannelCode) throws Exception;

   Optional<MappingGroupChannel> getById(Long id);

    MappingGroupChannel findByVdsChannelCodeAndGroupChannelCodeAndIdNotLike(String pstrVdsChannelCode,String pstrVttChannelCode, Long id) throws Exception;
}
