package models;

import com.avaje.ebean.Page;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
@Entity
public class BaseAttribute extends BaseModel{
	public static String VARCHAR_TYPE = "VARCHAR";
	public static String INTEGER_TYPE = "INTEGER";

	@Column(unique=true)
	public String name;
	public String type;
	//odoo
	@Column(name = "odoo_id")
	public Integer odooId;
	
	@OneToMany(mappedBy = "baseAttribute")
	@JsonProperty("attributes")
	public Set<Attribute> attributesData;

	@javax.persistence.Transient
	public String save;

	@Transient
	@JsonProperty("values")
	public Set<Attribute> getAttributes() {
		return attributesData;
	}

	public static Finder<Long, BaseAttribute> find = new Finder<Long, BaseAttribute>(Long.class, BaseAttribute.class);


	public BaseAttribute(){
	}

	public BaseAttribute(String name, String type){
		this.name = name.toLowerCase();
		this.type = type.toLowerCase();
		this.attributesData = new HashSet<Attribute>();
	}

	public BaseAttribute(String name, String type, Integer odooId){
		this.name = name.toLowerCase();
		this.type = type.toLowerCase();
//		this.odooId = odooId;
		this.attributesData = new HashSet<Attribute>();
	}
	
	public static String validate(BaseAttribute model){
		String res = null;
		model.name = model.name.toLowerCase();
		if (model.name==null||model.name.trim().equals("")){
			res = "Name must not empty";
		} else {
			BaseAttribute uniqueCheck = BaseAttribute.find.where().eq("name", model.name).findUnique();
			if (uniqueCheck != null && uniqueCheck.id != model.id) {
				res = "Base attribute already exist";
			}
		}
		return res;
	}
	
	public Attribute fetchDefaultAttribute(){
//		for (AttributeData att: attributesData) {
//			if(att.isDefault) return att;
//		}
//		return null;
		return Attribute.find.where().eq("base_attribute_id", this.id).eq("is_default", true).findUnique();
	}

	public static Page<BaseAttribute> page(int page, int pageSize, String sortBy, String order, String filter) {
		return
				find.where()
						.ilike("name", "%" + filter + "%")
						.eq("is_deleted", false)
						.orderBy(sortBy + " " + order)
						.findPagingList(pageSize)
						.setFetchAhead(false)
						.getPage(page);
	}

	public static int findRowCount() {
		return
				find.where()
						.eq("is_deleted", false)
						.findRowCount();
	}

	public static List<BaseAttribute> getAllData() {
		return BaseAttribute.find.where()
				.eq("is_deleted", false)
				.order("name asc").findList();
	}
}