package vn.vissoft.dashboard.repo;

import vn.vissoft.dashboard.dto.ShopUnitDTO;
import vn.vissoft.dashboard.model.ShopUnit;

import java.math.BigInteger;
import java.text.ParseException;
import java.util.List;

public interface ShopUnitRepoCustom {

   List<ShopUnit> getAllByCondition(ShopUnitDTO shopUnitDTO) throws ParseException;

   Long countByCondition(ShopUnitDTO shopUnitDTO);

}
