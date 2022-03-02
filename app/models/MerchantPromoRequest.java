package models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


/**
 * Created by nugraha on 5/26/17.
 */
@Entity
@Table(name = "merchant_promo_request")
public class MerchantPromoRequest extends BaseModel {

    @JsonIgnore
    @ManyToOne
    public Promo promo;

    @JsonIgnore
    @ManyToOne
    public Merchant merchant;


    public static Finder<Long, MerchantPromoRequest> find = new Finder<Long, MerchantPromoRequest>(Long.class, MerchantPromoRequest.class);


    public static int findRowCountPromo(Long promoId) {
        return
                find.where()
                        .eq("is_deleted", false)
                        .eq("promo_id", promoId)
                        .findRowCount();
    }

}