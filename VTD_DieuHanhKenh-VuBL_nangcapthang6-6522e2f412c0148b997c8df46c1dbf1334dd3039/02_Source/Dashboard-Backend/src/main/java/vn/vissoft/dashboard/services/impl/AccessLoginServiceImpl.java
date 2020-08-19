package vn.vissoft.dashboard.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.vissoft.dashboard.model.AccessLogin;
import vn.vissoft.dashboard.repo.AccessLoginRepo;
import vn.vissoft.dashboard.services.AccessLoginService;

import javax.transaction.Transactional;

@Transactional
@Service
public class AccessLoginServiceImpl implements AccessLoginService {

    @Autowired
    private AccessLoginRepo accessLoginRepo;

    /**
     * luu log lich su dang nhap
     *
     * @author VuBL
     * @since 2020/06
     * @param dataInsert
     */
    @Override
    public void saveLog(AccessLogin dataInsert) throws Exception {
        accessLoginRepo.saveLog(dataInsert);
    }

}
