package models;

import java.util.List;

public class BaseAttributeFilter {
	private String base_name;
	private List<AttributeFilter> attributes;

	public BaseAttributeFilter(String key, List<AttributeFilter> attributes) {
		this.base_name = key;
		this.attributes = attributes;
	}

	public String getBase_name() {
		return base_name;
	}

	public void setBase_name(String base_name) {
		this.base_name = base_name;
	}

	public List<AttributeFilter> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<AttributeFilter> attributes) {
		this.attributes = attributes;
	}
}
