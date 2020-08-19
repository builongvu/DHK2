package vn.vissoft.dashboard.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.StaffRoleDTO;
import vn.vissoft.dashboard.dto.VdsStaffDTO;
import vn.vissoft.dashboard.dto.excel.BaseUploadEntity;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.model.Staff;
import vn.vissoft.dashboard.repo.StaffRepo;
import vn.vissoft.dashboard.repo.StaffRepoCustom;
import vn.vissoft.dashboard.repo.VdsStaffRepo;
import vn.vissoft.dashboard.services.StaffService;

import javax.management.Query;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
public class StaffServiceImpl implements StaffService {

    @Autowired
    private StaffRepo staffRepo;

    @Override
    public List<Staff> getByShopId(Long plngShopId) throws Exception {
        return staffRepo.findByShopId(plngShopId);
    }

    @Override
    public List<Staff> getByShopCodeOfUser(StaffDTO staffDTO) throws Exception {
        return staffRepo.findByUnitCodeOfUser(staffDTO);
    }

    @Override
    public List<Staff> getAllStaff() {
        return staffRepo.findAll();
    }

    @Override
    public List<StaffRoleDTO> getAllByCodeAndName(String staff, String shop, String role, int hasrole) throws Exception{
        return staffRepo.findByStaffCodeAndStaffName(staff, shop, role,hasrole);
    }

}
