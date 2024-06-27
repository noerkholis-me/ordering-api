package models.loyalty;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import models.*;
import models.transaction.*;
import java.math.BigDecimal;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoyaltyPointHistory extends BaseModel {

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="member_id", referencedColumnName = "id")
    public Member member;

    @ManyToOne
    @JoinColumn(name="order_id", referencedColumnName = "id")
    public Order order;
    
    @Column(name = "point")
    public BigDecimal point;

    @Column(name = "added")
    public BigDecimal added;
    
    @Column(name = "used")
    public BigDecimal used;

    @JsonFormat(shape = JsonFormat.Shape.STRING,  pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS", timezone="Asia/Jakarta")
    @Column(name = "expired")
    @JsonProperty("expired")
    public Date expiredDate;

    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "merchant_id", referencedColumnName = "id")
    public Merchant merchant;

    public static Finder<Long, LoyaltyPointHistory> find = new Finder<Long, LoyaltyPointHistory>(Long.class, LoyaltyPointHistory.class);

}