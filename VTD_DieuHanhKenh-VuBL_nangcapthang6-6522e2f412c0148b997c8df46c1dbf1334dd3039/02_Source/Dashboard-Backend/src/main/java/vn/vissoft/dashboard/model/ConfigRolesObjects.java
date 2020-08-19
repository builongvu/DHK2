package vn.vissoft.dashboard.model;

import java.util.Date;
import javax.persistence.*;

/**
 * Created by VinhNDQ on 25/10/2019 14:17
 */

@Entity
@Table(name="config_roles_objects")
public class ConfigRolesObjects implements java.io.Serializable{

    private Long roleId;
    private Long objectId;
    private Long id;
    private Integer status;
    private Integer isDefault;
    private String action;

    
    @Column(name="role_id")
    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }
    
    
    @Column(name="object_id")
    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    @Column(name = "status")
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Column(name = "is_default")
    public Integer getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Integer isDefault) {
        this.isDefault = isDefault;
    }

    @Column(name = "action")
    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
