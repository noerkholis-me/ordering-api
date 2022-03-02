package models;

import com.avaje.ebean.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(uniqueConstraints={@UniqueConstraint(columnNames={"value", "base_attribute_id"})})
public class Attribute extends BaseModel{

	@JsonProperty("name")
	public String value;
	@JsonProperty("image_url")
	public String imageUrl;
	@JsonProperty("is_default")
	public boolean isDefault;
	//odoo
	@Column(name = "odoo_id")
	public Integer odooId;
	@JsonProperty("additional")
    public String additional;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "base_attribute_id")
	public BaseAttribute baseAttribute;

	@javax.persistence.Transient
	public String save;
	@Transient
	@JsonProperty("name")
	public String getName(){
		return value;
	}

	public static Finder<Long, Attribute> find = new Finder<Long, Attribute>(Long.class, Attribute.class);

	public Attribute(){

	}

	public Attribute(String value, BaseAttribute baseAttribute){
		this.value = value;
		this.baseAttribute = baseAttribute;
		this.isDefault = false;
	}

	public Attribute(String value, BaseAttribute baseAttribute, Integer odooId){
		this.value = value;
		this.baseAttribute = baseAttribute;
		this.isDefault = false;
//		this.odooId = odooId;
	}
	
	public static String validate(Attribute model){
		String res = null;
		if (model.value==null||model.value.trim().equals("")){
			res = "Value must not empty";
		} else if (model.baseAttribute==null&&model.id==null){
			res = "Base attribute not found";
		} else {
			Attribute uniqueCheck = Attribute.find.where().eq("value", model.value).eq("base_attribute_id", model.baseAttribute.id).findUnique();
			if (uniqueCheck != null && uniqueCheck.id != model.id) {
				res = "Attribute already exist.";
			}
		}
		return res;
	}

	public static Page<Attribute> page(Long baseId, int page, int pageSize, String sortBy, String order, String filter) {
		return
				find.where()
						.ilike("value", "%" + filter + "%")
						.eq("is_deleted", false)
						.eq("baseAttribute.id", baseId)
						.orderBy(sortBy + " " + order)
						.findPagingList(pageSize)
						.setFetchAhead(false)
						.getPage(page);
	}

	public static int findRowCount(Long baseId) {
		return
				find.where()
						.eq("is_deleted", false)
						.eq("baseAttribute.id", baseId)
						.findRowCount();
	}

	public static List<Attribute> getDataBy(Long id) {
		return Attribute.find.where()
				.eq("is_deleted", false)
				.eq("base_attribute_id", id)
				.order("value asc").findList();
	}
}
