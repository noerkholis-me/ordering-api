package models;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

public class Attrib extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@ManyToOne
	public AttributeGroup group;

	public String name;

	@Column(columnDefinition = "TEXT")
	public String description;

	@ManyToOne
	@Enumerated(EnumType.STRING)
	public InputType inputType;
	
	@ManyToOne
	@Enumerated(EnumType.STRING)
	public ShowIn showIn;

	public Boolean mandatory;
	
	public boolean status;

}

enum InputType {
	MULTIPLE_CHOICE, SHORT_TEXT, LONG_TEXT, FILE
}

enum ShowIn {
	MASTER_PRODUCT, PRODUCT_DETAIL
}
