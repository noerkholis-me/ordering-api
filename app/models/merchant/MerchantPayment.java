package models.merchant;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import models.*;
import models.internal.*;
import java.util.List;

import javax.persistence.*;

@Entity
public class MerchantPayment extends BaseModel {
    private static final long serialVersionUID = 1L;

    @Getter
    @JsonProperty("id")
    public Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="payment_method_id", referencedColumnName = "id")
    @Getter @Setter
    public PaymentMethod paymentMethod;

    @Getter @Setter
    @JsonProperty("type_payment")
    public String typePayment;

    @Getter @Setter
    @JsonProperty("device")
    public String device;

    @Getter @Setter
    @JsonProperty("is_active")
    public Boolean isActive;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="merchant_id", referencedColumnName = "id")
    @Getter @Setter
    public Merchant merchant;

    // GET BY DEVICE
    public static Finder<Long, MerchantPayment> findPayment = new Finder<>(Long.class, MerchantPayment.class);

    public static List<MerchantPayment> findByDevice(String device, Long merchantId) {
        return findPayment.where().eq("t0.merchant_id", merchantId).eq("t0.device", device).eq("t0.is_active", true).eq("t0.is_deleted", false).order("t0.id asc").findList();
    }

    public static List<MerchantPayment> findByMerchantId(Long merchantId) {
        return findPayment.where().eq("t0.merchant_id", merchantId).eq("t0.is_active", true).eq("t0.is_deleted", false).order("t0.id asc").findList();
    }

}