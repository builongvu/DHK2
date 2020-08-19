package vn.vissoft.dashboard.services;

import org.springframework.web.multipart.MultipartFile;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.model.ConfigObjects;

import java.util.List;
import java.util.Optional;

public interface ConfigObjectsService {
    List<ConfigObjects> getAllObject() throws Exception;

    List<ConfigObjects> getAllObjectNotDelete() throws Exception;

    Long saveObject(ConfigObjects configObjects, StaffDTO user) throws Exception;

    Long updateObject(ConfigObjects configObjects, StaffDTO user) throws Exception;

    List<ConfigObjects> getAllActive() throws Exception;

    Optional<ConfigObjects> findAllById(Long id) throws Exception;

    List<ConfigObjects> getAllByParentId(Long parentId) throws Exception;

    String upload(MultipartFile multipartFile, String code, Long id) throws Exception;

    boolean checkSelectParent(Long parentId, Long idSelect) throws Exception;
}
