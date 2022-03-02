package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="s_order_detail_additional")
public class SOrderDetailAdditional extends BaseModel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@JsonIgnore
	@ManyToOne
	public SOrderDetail detail;

	@ManyToOne
	public Product product;
	public Double discount;
	public double price;

    public static Finder<Long, SOrderDetailAdditional> find = new Finder<Long, SOrderDetailAdditional>(Long.class, SOrderDetailAdditional.class);

}
