package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import play.data.validation.ValidationError;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.beans.Transient;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hendriksaragih on 2/4/17.
 */
@Entity
public class FaqGroup extends BaseModel{
    @Column(unique=true)
    public String name;

    @JsonIgnore
    @JoinColumn(name="user_id")
    @ManyToOne
    public UserCms userCms;

    @Transient
    @JsonProperty("user_creator")
    public String getUserCreator(){
        return userCms.email;
    }

    public static Finder<Long, FaqGroup> find = new Finder<>(Long.class, FaqGroup.class);

    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();

        if (name == null || name.isEmpty()) {
            errors.add(new ValidationError("name", "Name must not empty."));
        }

        if(errors.size() > 0)
            return errors;

        return null;
    }
}
