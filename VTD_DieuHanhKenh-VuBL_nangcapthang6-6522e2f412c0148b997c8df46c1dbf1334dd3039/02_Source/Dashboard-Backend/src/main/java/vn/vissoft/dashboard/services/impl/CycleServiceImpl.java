package vn.vissoft.dashboard.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.vissoft.dashboard.model.Cycle;
import vn.vissoft.dashboard.repo.CycleRepo;
import vn.vissoft.dashboard.services.CycleService;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Service
public class    CycleServiceImpl implements CycleService {

    @Autowired
    private CycleRepo cycleRepo;

    /**
     * lay ra danh sach cycle tren combobox chu ky o chuc nang giao chi tieu
     *
     * @param pintAssign
     * @return
     * @throws Exception
     * @author VuBL
     * @since 2019/12
     */
    @Override
    public List<Cycle> getByAssign(int pintAssign) throws Exception {
        return cycleRepo.findByAssign(pintAssign);
    }

}
