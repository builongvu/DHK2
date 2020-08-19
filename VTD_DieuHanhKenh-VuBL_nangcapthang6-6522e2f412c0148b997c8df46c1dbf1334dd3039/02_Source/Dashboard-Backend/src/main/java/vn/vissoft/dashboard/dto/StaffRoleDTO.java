package vn.vissoft.dashboard.dto;

public class StaffRoleDTO {
    private Long id;
    private String code;
    private String name;
    private String status;
    private String shopcode;
    private String role;
    private int isHaveRole;


    public int getIsHaveRole() {
        return isHaveRole;
    }

    public void setIsHaveRole(int isHaveRole) {
        this.isHaveRole = isHaveRole;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getShopcode() {
        return shopcode;
    }

    public void setShopcode(String shopcode) {
        this.shopcode = shopcode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
