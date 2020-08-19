package vn.vissoft.dashboard.services;

import org.springframework.web.multipart.MultipartFile;
import vn.vissoft.dashboard.dto.ShopUnitDTO;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.excel.BaseShopUnit;
import vn.vissoft.dashboard.model.ShopUnit;

import java.math.BigInteger;
import java.text.ParseException;
import java.util.List;

public interface ShopUnitService {
    List<ShopUnit> getAll();

    List<ShopUnit> getAllByCondition(ShopUnitDTO shopUnitDTO) throws ParseException;

    Long countByCondition(ShopUnitDTO shopUnitDTO);

    boolean addShopUnit(ShopUnit shopUnit, StaffDTO staffDTO) throws Exception;

    boolean updateShopUnit(ShopUnit shopUnit, StaffDTO staffDTO) throws Exception;

    ShopUnit getById(Long id);

    void lockUnlock(String[] id, String status, StaffDTO staffDTO);

    boolean checkDuplicate (ShopUnit shopUnit);

    BaseShopUnit upload (MultipartFile file, StaffDTO staffDTO) throws Exception;
}
