package com.hokeba.api;

public class ApiFilter {
	private String property;
	private String operator;
	private ApiFilterValue[] values;

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public ApiFilterValue[] getValues() {
		return values;
	}

	public void setValues(ApiFilterValue[] values) {
		this.values = values;
	}

	public ApiFilter() {
		super();
	}

	public ApiFilter(String property, String operator, ApiFilterValue[] values) {
		super();
		this.property = property;
		this.operator = operator;
		this.values = values;
	}

}
