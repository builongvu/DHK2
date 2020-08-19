package vn.vissoft.dashboard.model;

import java.util.Date;
import javax.persistence.*;

/**
 * Created by VinhNDQ on 25/10/2019 14:17
 */

@Entity
@Table(name="config_roles_staff")
public class ConfigRolesStaff implements java.io.Serializable{

    private Long roleId;
    private String staffCode;
    private Long id;
    private Integer status;

    
    
    @Column(name="role_id")
    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }
    


    @Column(name="staff_code")
    public String getStaffCode() {
        return staffCode;
    }

    public void setStaffCode(String staffCode) {
        this.staffCode = staffCode;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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
}
