package vn.vissoft.dashboard.model;

import java.util.Date;
import javax.persistence.*;

/**
 * Created by VinhNDQ on 25/10/2019 14:16
 */

@Entity
@Table(name="config_roles")
public class ConfigRoles {

    private String roleCode;
    private Long id;
    private String roleDescription;
    private Long status;
    private String roleName;

    @Column(name="role_code")
    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
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
    
    
    @Column(name="role_description")
    public String getRoleDescription() {
        return roleDescription;
    }

    public void setRoleDescription(String roleDescription) {
        this.roleDescription = roleDescription;
    }
    
    
    @Column(name="status")
    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    @Column(name="role_name")
    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
