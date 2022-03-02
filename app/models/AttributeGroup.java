package models;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class AttributeGroup extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Column(unique = true)
	public String name;
	public String description;
	public boolean status;

}
