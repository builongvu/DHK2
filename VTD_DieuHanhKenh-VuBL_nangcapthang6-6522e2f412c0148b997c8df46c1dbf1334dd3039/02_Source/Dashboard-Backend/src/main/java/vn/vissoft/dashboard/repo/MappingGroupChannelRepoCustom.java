package vn.vissoft.dashboard.repo;

import vn.vissoft.dashboard.dto.MappingGroupChannelDTO;
import vn.vissoft.dashboard.model.VttGroupChannel;

import java.math.BigInteger;
import java.util.List;

public interface MappingGroupChannelRepoCustom {
    List<Object[]> findByCondition(MappingGroupChannelDTO mappingGroupChannel) throws Exception;

    BigInteger countByCondition(MappingGroupChannelDTO mappingGroupChannelDTO) throws Exception;

    List<VttGroupChannel> getAllNotInMapping(String code) throws Exception;
}
