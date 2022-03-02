package models;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hokeba.mapping.request.MapVoucherCode;
import com.hokeba.util.CommonFunction;
import com.hokeba.util.Constant;

import play.Logger;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hendriksaragih on 4/26/17.
 */
@Entity
public class SalesOrderDetail extends BaseModel {
    private static final long serialVersionUID = 1L;

    public static Finder<Long, SalesOrderDetail> find = new Finder<>(Long.class,
            SalesOrderDetail.class);

    @JsonIgnore
    @ManyToOne
    public Product product;

    @JsonIgnore
    @ManyToOne
    public ProductDetailVariance productVar;
    
    public String status;

    @JsonIgnore
    @ManyToOne
    public Merchant merchant;

    @JsonIgnore
    @ManyToOne
    public Vendor vendor;

    @JsonIgnore
    @ManyToOne(cascade = { CascadeType.ALL })
    public SalesOrder salesOrder;

    @JsonIgnore
    @ManyToOne(cascade = { CascadeType.ALL })
    public SalesOrderSeller salesOrderSeller;

    @JsonProperty("product_name")
    public String productName;

    @Column(name = "fashion_size")
    @JsonProperty("size")
    public String sizeName;

    @JsonIgnore
    @ManyToOne
    public Size fashionSize;

    public double price;
    @JsonProperty("price_discount")
    public double priceDiscount;
    @JsonProperty("quantity")
    public int quantity;
    @JsonProperty("discount_persen")
    public Double discountPersen;
    @JsonProperty("discount_amount")
    public Double discountAmount;
    @JsonProperty("sub_total")
    public double subTotal;
    @JsonProperty("total_price")
    public double totalPrice;
    public double tax;
    @JsonProperty("tax_price")
    public double taxPrice;
    public Double voucher;
    @JsonProperty("payment_seller")
    public Double paymentSeller;

    //odoo
    @Column(name = "odoo_id")
    public Integer odooId;

    @JsonIgnore
    @ManyToMany
    public List<VoucherDetail> voucherDetails;

    @JsonProperty("loyalty_eligible_use")
    public Long loyaltyEligibleUse;

    @JsonProperty("loyalty_eligible_earn")
    public Long loyaltyEligibleEarn;
    
    @JsonProperty("loyalty_eligible_earn_referral")
    public Long loyaltyEligibleEarnReferral;

    @Transient
    @JsonProperty("name")
    public String getName(){
        return productName;
    }
    @Transient
    @JsonProperty("sku")
    public String getSku(){
        return product == null ? "" : product.sku;
    }
    @Transient
    @JsonProperty("image_url")
    public String getImageUrl(){
    	if (product != null){
            return product.getThumbnailUrl();
        }
        return "";
    }
    @Transient
    @JsonProperty("color")
    public String getColor(){
        return getColorAttribute();
    }
    @Transient
    @JsonProperty("product_id")
    public Long getProductId(){
        return product.id;
    }
    
    @JsonProperty("product_variance_id")
    public Long getProductVarianceId(){
        return productVar.id;
    }

    @Transient
    @JsonProperty("currency")
    public String getCurrency(){
        return Constant.defaultCurrency;
    }
    @JsonGetter("price_display")
    public Double getPriceDisplay() {
    	return this.priceDiscount;
    }
    @JsonGetter("voucher_amount")
    public Double getVoucherAmount() {
    	return this.voucher;
    }
    
    @JsonGetter("vouchers")
    public List<MapVoucherCode> getVouchers() {
    	List<MapVoucherCode> result = new ArrayList<>();
    	for (VoucherDetail voucherDetail : voucherDetails) {
    		MapVoucherCode voucher = new MapVoucherCode();
    		voucher.setVoucherCode(voucherDetail.code);
    		
    		Voucher voucherTarget = voucherDetail.voucher;
    		if (voucherTarget != null) {
	    		voucher.setVoucherId(voucherTarget.id);
	    		voucher.setVoucherInfo(voucherTarget.getDiscountInfo());
	    		voucher.setMessage(voucherTarget.description);
    		}
    		result.add(voucher);
		}
    	return result;
    }


    @Transient
    @JsonProperty("order_id")
    public Long getOrderId(){
        return salesOrder.id;
    }
    @Transient
    @JsonProperty("product_image")
    public String getProductImage(){
        return getImageUrl();
    }
    @Transient
    @JsonProperty("product_price")
    public Double getProductPrice(){
        return subTotal;
    }
    @Transient
    @JsonProperty("fee_price")
    public Double getFeePrice(){
        return ((priceDiscount * quantity) - paymentSeller) / quantity;
    }
    @Transient
    @JsonProperty("total")
    public Double getTotal(){
        return subTotal;
    }

    public String getStatus(){
        String result = "";
        switch (status){
            case SalesOrder.ORDER_STATUS_VERIFY : result = "Order Verified";break;
            case SalesOrder.ORDER_STATUS_WAITING_PAYMENT_CONFIRMATION : result = "Waiting Payment Confirmation";break;
            case SalesOrder.ORDER_STATUS_EXPIRE_PAYMENT : result = "Expire Payment";break;
            case SalesOrder.ORDER_STATUS_PICKING : result = "Picking";break;
            case SalesOrder.ORDER_STATUS_PACKING : result = "Packing";break;
            case SalesOrder.ORDER_STATUS_ON_DELIVERY : result = "On Delivery";break;
            case SalesOrder.ORDER_STATUS_RECEIVE_BY_CUSTOMER : result = "Received By Customer";break;
            case SalesOrder.ORDER_STATUS_CUSTOMER_NOT_AT_THE_ADDRESS_STATE : result = "Customer Not At The Address State";break;
            case SalesOrder.ORDER_STATUS_CANCEL : result = "Cancel";break;
            case SalesOrder.ORDER_STATUS_RETURN : result = "Return";break;
            case SalesOrder.ORDER_STATUS_REPLACED : result = "Replaced";break;
            case SalesOrder.ORDER_STATUS_CANCEL_BY_CUSTOMER_SERVICE : result = "Cancel By Customer Service";break;
            default: result = "Invalid Status";
        }
        return result;
    }

    public String getColorAttribute(){
    	if (productVar != null){
    		return productVar.getColorName();
    	}
    	return "";
    }
    @JsonGetter("size")
    public String getSize(){
    	if (productVar != null){
    		return productVar.getSizeName();
    	}
    	return "";
    }
    @JsonGetter("size_id")
    public Long getSizeId(){
        if (productVar != null){
            return productVar.getSizeId();
        }
        return null;
    }

    public String getProductUrl(){
        if (product != null){
            return Constant.getInstance().getFrontEndUrl().concat("/product/").concat(product.slug).concat("?id=")
                    .concat(String.valueOf(product.id));
        }
        return "";
    }

    public String getProductSku(){
        if (product != null && productVar != null){
            return product.sku + "-" + productVar.id; //TODO
        }
        return "";
    }

    public String getProductNameFull(){
        String name = productName;
        String color = product.getProductColor();
        if (!color.isEmpty()){
            name = name.concat(" (Color : ").concat(color).concat(")");
        }
        if (sizeName != null && !sizeName.isEmpty()){
            name = name.concat(" (Size : ").concat(sizeName).concat(")");
        }
        return name;
    }
    
    public String fetchProductNameFull() {
    	String name = productName;
    	if (productVar != null) {
    		if (productVar.color != null) {
    			name += " (Color: " + productVar.color.name + ")";
    		}
    		if (productVar.size != null) {
    			name += " (Size: " + productVar.size.international + ")";
    		}
    	}
        return name;
    }

    public String getPriceString(){
        return CommonFunction.numberFormat(price);
    }

    public Boolean existColor() {
        return !getColor().isEmpty();
    }

    public Boolean existSize(){
        return sizeName != null && !sizeName.isEmpty();
    }
    
    public String fetchProductCategory() {
    	return product != null && product.category != null ? product.category.name : "";
    }

    public void setLoyaltyEligibleUse(Long loyaltyEligibleUse) {
		this.loyaltyEligibleUse = loyaltyEligibleUse;
	}
	public void setLoyaltyEligibleEarn(Long loyaltyEligibleEarn) {
		this.loyaltyEligibleEarn = loyaltyEligibleEarn;
	}
	
	public void setLoyaltyEligibleEarnReferral(Long loyaltyEligibleEarnReferral) {
		this.loyaltyEligibleEarnReferral = loyaltyEligibleEarnReferral;
	}
}
