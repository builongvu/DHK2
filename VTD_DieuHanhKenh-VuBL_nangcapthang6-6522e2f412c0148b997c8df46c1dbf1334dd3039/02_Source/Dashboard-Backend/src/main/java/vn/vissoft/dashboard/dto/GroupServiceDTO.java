package vn.vissoft.dashboard.dto;


import java.io.Serializable;
import java.sql.Timestamp;


public class GroupServiceDTO extends BaseDTO implements Serializable  {

    private Long Id;
    private String Code;
    private String Name;
    private String Status;
    private Long ProductId;
    private String UserUpdate;
    private String ChangeDatetime1;
    private Timestamp ChangeDatetime;

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public Long getProductId() {
        return ProductId;
    }

    public void setProductId(Long productId) {
        ProductId = productId;
    }

    public String getUserUpdate() {
        return UserUpdate;
    }

    public void setUserUpdate(String userUpdate) {
        UserUpdate = userUpdate;
    }

    public String getChangeDatetime1() {
        return ChangeDatetime1;
    }

    public void setChangeDatetime1(String changeDatetime1) {
        ChangeDatetime1 = changeDatetime1;
    }

    public Timestamp getChangeDatetime() {
        return ChangeDatetime;
    }

    public void setChangeDatetime(Timestamp changeDatetime) {
        ChangeDatetime = changeDatetime;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    private String productName;







}
