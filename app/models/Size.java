package models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "fashion_size")
public class Size extends BaseModel {

    private static final long serialVersionUID = 1L;

    public String international;
    public int eu;
//    public float collar;
    public int sequence;

    @JsonIgnore
    @JoinColumn(name = "user_id")
    @ManyToOne
    public UserCms userCms;

    @Transient
    public String save;

    public static Finder<Long, Size> find = new Finder<>(Long.class, Size.class);

}
