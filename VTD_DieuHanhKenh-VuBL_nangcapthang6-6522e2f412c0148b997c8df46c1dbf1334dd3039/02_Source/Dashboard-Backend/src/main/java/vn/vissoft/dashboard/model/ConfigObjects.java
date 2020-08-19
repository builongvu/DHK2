package vn.vissoft.dashboard.model;

import java.util.Date;
import javax.persistence.*;

/**
 * Created by VinhNDQ on 25/10/2019 14:15
 */

@Entity
@Table(name="config_objects")
public class ConfigObjects implements java.io.Serializable{

    private String objectIcon;
    private String objectImg;
    private String objectUrl;
    private String objectType;
    private Long parentId;
    private String objectName;
    private String objectNameI18N;
    private String objectCode;
    private Long id;
    private Long status;
    private Long ord;
    private Integer functionType;

    
    
    @Column(name="object_icon")
    public String getObjectIcon() {
        return objectIcon;
    }

    public void setObjectIcon(String objectIcon) {
        this.objectIcon = objectIcon;
    }
    
    
    @Column(name="object_url")
    public String getObjectUrl() {
        return objectUrl;
    }

    public void setObjectUrl(String objectUrl) {
        this.objectUrl = objectUrl;
    }
    
    
    @Column(name="object_type")
    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }
    
    
    @Column(name="parent_id")
    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
    
    
    @Column(name="object_name")
    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    @Column(name="object_name_i18n")
    public String getObjectNameI18N() {
        return objectNameI18N;
    }

    public void setObjectNameI18N(String objectNameI18N) {
        this.objectNameI18N = objectNameI18N;
    }

    @Column(name="object_code")
    public String getObjectCode() {
        return objectCode;
    }

    public void setObjectCode(String objectCode) {
        this.objectCode = objectCode;
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
    
    
    @Column(name="status")
    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }
    @Column(name="ord")
    public Long getOrd() {
        return ord;
    }

    public void setOrd(Long ord) {
        this.ord = ord;
    }

    @Column(name="object_img")
    public String getObjectImg() {
        return objectImg;
    }

    public void setObjectImg(String objectImg) {
        this.objectImg = objectImg;
    }

    @Column(name = "function_type")
    public Integer getFunctionType() {
        return functionType;
    }

    public void setFunctionType(Integer functionType) {
        this.functionType = functionType;
    }
}
