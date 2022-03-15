package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "webhook_status")
public class WebhookShipper extends BaseModel{

	private static final long serialVersionUID = 1L;
	
	public String order_id;
	public String logistic;
	
    public static Finder<Long, WebhookShipper> find = new Finder<Long, WebhookShipper>(Long.class, WebhookShipper.class);

}
