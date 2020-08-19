package vn.vissoft.dashboard.model;


import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "GROUP_SERVICE")
public class GroupService  implements Serializable {

    private Long id;
    private String code;
    private String name;
    private String status;
    private Long productId;
    private Timestamp createdate;
    private String userupdate;

    public GroupService() {
    }

    public GroupService(String code, String name, String status, Long productId, Timestamp createdate, String userupdate) {
        this.code = code;
        this.name = name;
        this.status = status;
        this.productId = productId;
        this.createdate = createdate;
        this.userupdate = userupdate;
    }

    @Basic
    @Column(name = "CREATE_DATE")
//    @JsonFormat(pattern="yyyy-MM-dd  ")
    public Timestamp getCreatedate() {
        return createdate;
    }

    public void setCreatedate(Timestamp createdate) {
        this.createdate = createdate;
    }

    @Basic
    @Column(name = "USER")

    public String getUserupdate() {
        return userupdate;
    }

    public void setUserupdate(String userupdate) {
        this.userupdate = userupdate;
    }

    @Column(name = "id")
    @Id
    // @GeneratedValue(generator="system-uuid")
   // @GenericGenerator(name="system-uuid", strategy = "uuid")
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "CODE")
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Basic
    @Column(name = "NAME")
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "STATUS")
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    @Basic
    @Column(name = "product_id")
    public Long getProductId() {
        return productId;
    }
    public void setProductId(Long productId) {
        this.productId = productId;
    }
}
