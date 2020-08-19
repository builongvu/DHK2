package vn.vissoft.dashboard.model;

import javax.persistence.*;

@Entity
@Table(name = "position")
public class Position {
    private Long id;
    private String code;
    private String name;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "position_id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "position_code")
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(name = "position_name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
