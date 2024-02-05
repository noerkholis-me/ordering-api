package models.store;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import models.BaseModel;
import models.Member;
import models.Store;

import javax.persistence.*;

@Table(name = "store_ratings")
@Getter
@Setter
@Entity
public class StoreRatings extends BaseModel {
    @JsonIgnore
    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "store_id", referencedColumnName = "id")
    public Store store;

    @JsonIgnore
    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    private Member member;

    @Column(columnDefinition = "text")
    private String feedback;

    private float rate;

}
