package com.hokeba.api;

public class ApiFilters {
	private String logic;
	private ApiFilter[] filters;

	public String getLogic() {
		return logic;
	}

	public void setLogic(String logic) {
		this.logic = logic;
	}

	public ApiFilter[] getFilters() {
		return filters;
	}

	public void setFilters(ApiFilter[] filters) {
		this.filters = filters;
	}

}
