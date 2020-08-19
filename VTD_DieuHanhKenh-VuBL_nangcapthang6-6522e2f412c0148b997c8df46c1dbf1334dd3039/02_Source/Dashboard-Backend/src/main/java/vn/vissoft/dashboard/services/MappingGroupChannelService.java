package vn.vissoft.dashboard.services;

import vn.vissoft.dashboard.dto.MappingGroupChannelDTO;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.model.MappingGroupChannel;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public interface MappingGroupChannelService {

    List<MappingGroupChannel> getByCondition(MappingGroupChannelDTO mappingGroupChannel) throws Exception;

    String persist(MappingGroupChannel mappingGroupChannel, StaffDTO staffDTO) throws Exception;

    String update(MappingGroupChannel mappingGroupChannel,StaffDTO staffDTO) throws Exception;

    BigInteger countByCondition(MappingGroupChannelDTO mappingGroupChannelDTO) throws Exception;

    MappingGroupChannel getById(Long id) throws Exception;

}
