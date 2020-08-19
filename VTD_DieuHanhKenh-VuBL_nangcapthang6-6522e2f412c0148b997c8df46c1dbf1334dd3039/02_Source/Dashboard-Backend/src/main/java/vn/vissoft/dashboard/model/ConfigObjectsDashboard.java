package vn.vissoft.dashboard.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

/**
 * Created by vinhndq on 28/10/2019 22:03
 */

@Entity
@Table(name="config_objects_dashboard")
public class ConfigObjectsDashboard implements Serializable{

    private Long groupCardId;
    private Long objectId;
    private Long id;
    private Long status;

    
    
    @Column(name="group_card_id")
    public Long getGroupCardId() {
        return groupCardId;
    }

    public void setGroupCardId(Long groupCardId) {
        this.groupCardId = groupCardId;
    }
    
    
    @Column(name="object_id")
    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
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

}
