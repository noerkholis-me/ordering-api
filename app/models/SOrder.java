package models;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import com.avaje.ebean.Page;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "s_order")
public class SOrder extends BaseModel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    public static final String ORDER_STATUS_WAITING_PAYMENT_CONFIRMATION = "WC";
    public static final String ORDER_STATUS_CHECKOUT = "CH";
    public static final String ORDER_STATUS_VERIFY = "OV";
    public static final String ORDER_STATUS_EXPIRE_PAYMENT = "EX";
    public static final String ORDER_STATUS_CANCEL = "CA";
    public static final String ORDER_STATUS_CANCEL_BY_CUSTOMER_SERVICE = "CC";
	public static final String DEVICE_KIOSK = "KIOSK";
	public static final String DEVICE_MOBILE = "MOBILE";
	
	public static final String ORDER_TYPE_TAKE_AWAY = "TAKE_AWAY";
	public static final String ORDER_TYPE_DINE_IN = "DINE_IN";
	public static final String ORDER_TYPE_DELIVERY = "DELIVERY";
	public static final String ORDER_TYPE_PICK_UP = "PICK_UP";
	
	
    private static final List<String> listStatus = Arrays.asList(ORDER_STATUS_WAITING_PAYMENT_CONFIRMATION, ORDER_STATUS_VERIFY);
	

	public String orderNumber;
	
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "Asia/Jakarta")
	public Date orderDate;
	
	@ManyToOne
	public Member member;
	public String status;
	public Double discount;
	public double totalPrice;
	public String paymentType;
	public Integer userQueue;
	public String device;
	public Double serviceFee;
	public Double tax;
	public String orderType;
	
	public Double deliveryRates;
	public String consigneeName;
	public String consigneePhoneNumber;
	public String consignerName; 
	public String consignerPhoneNumber;
	public String originAddress;
	public String destinationAddress;
	public String orderIdShipper;
	
    @OneToOne(mappedBy = "order")
    @JsonProperty("order_payment")
    public SOrderPayment orderPayment;
	
	@OneToMany
	@JsonIgnore
	public List<SOrderDetail> details;
	
	@Transient
    public long fetchRoundedGrandTotal() {
		return Math.round(Math.ceil(this.totalPrice));
	}
	
    public static Page<SOrder> page(int page, int pageSize, Long memberId, String status) {
		return
				find.where()
				.in("upper(status)", listStatus)
				.eq("member.id", memberId)
				.orderBy("t0.created_at desc")
				.findPagingList(pageSize)
				.setFetchAhead(false)
				.getPage(page);
	}

    public static Finder<Long, SOrder> find = new Finder<Long, SOrder>(Long.class, SOrder.class);

}
