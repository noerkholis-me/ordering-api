package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "s_order_status")
public class SOrderStatus extends BaseModel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	@JsonIgnore
	@ManyToOne
	public SOrder order;

	public String status;
	public String notes;
	public Long s_order_id;
	

    public static Finder<Long, SOrderStatus> find = new Finder<Long, SOrderStatus>(Long.class, SOrderStatus.class);

}
