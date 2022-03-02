package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "s_order_detail")
public class SOrderDetail extends BaseModel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	@JsonIgnore
	@ManyToOne
	public SOrder order;

	@ManyToOne
	public Product product;
	public Double discount;
	public Double price;
	public Double totalPrice;
	public Integer quantity;
	public String note;
	
	@OneToMany
	@JsonIgnore
	public List<SOrderDetailAdditional> additionals;

    public static Finder<Long, SOrderDetail> find = new Finder<Long, SOrderDetail>(Long.class, SOrderDetail.class);

}
