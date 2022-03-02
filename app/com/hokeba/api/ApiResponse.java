package com.hokeba.api;

import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Query;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import play.Logger;
import play.libs.Json;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ApiResponse<T> {
	@SuppressWarnings("rawtypes")
	private static ApiResponse instance;

	@SuppressWarnings("rawtypes")
	public static ApiResponse getInstance() {
		if (instance == null) {
			instance = new ApiResponse();
		}
		return instance;
	}

	// digunakan utk set base respose (new version)
	// ditambah sorting dan filtering
	// stefanus - 29/11/2016
	public BaseResponse<T> setResponseV2(Query<T> query, String sort, String filter, int offset, int limit, Class convert)
			throws IOException {

		// sort
		// ex : &sort=[{"property":"first_name","direction":"asc"}]
		if (!sort.equals("")) {
			ApiSort[] sorts = new ObjectMapper().readValue(sort, ApiSort[].class);
			for (ApiSort apiSort : sorts) {
				query = query.orderBy(apiSort.getProperty() + " " + apiSort.getDirection());
			}
		}

		// filter
		// ex :
		// filter={"filters":[{"property":"page_category_id","operator":"equals","values":[{"value":1}]}]}
		ExpressionList<T> exp = query.where();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		if (filter != "") {
			ApiFilters filters = new ObjectMapper().readValue(filter, ApiFilters.class);
			System.out.println(Json.toJson(filters));
			if (filters.getLogic() == null) {
				exp = exp.conjunction();
			} else if (filters.getLogic().equals("and")) {
				exp = exp.conjunction();
			} else {
				exp = exp.disjunction();
			}

			ApiFilter[] apiFilters = filters.getFilters();
			for (int i = 0; i < apiFilters.length; i++) {
				ApiFilter apiFilter = apiFilters[i];
				setFilter(exp, formatter, apiFilter);
			}
			// exp = exp.endJunction();
		}

		// remove deleted(soft) record from the list automatically
//		ApiFilter removeDeletedRecord = new ApiFilter("t0.is_deleted", "equals",
//				new ApiFilterValue[] { new ApiFilterValue(new Boolean(false)) });
//		setFilter(exp, formatter, removeDeletedRecord);
		exp = exp.endJunction();

		// assign
		query = exp.query();
		int total = query.findList().size();

		if (limit != 0) {
			query = query.setMaxRows(limit);
		}

		// paging
		List<T> list = query.findPagingList(limit).getPage(offset).getList();
		Logger.debug(query.getGeneratedSql());

		// output
		BaseResponse<T> response = new BaseResponse<T>();
		if (convert != null){
			response.setData(new ObjectMapper().convertValue(list, convert));
		}else{
			response.setData(list);
		}
		response.setMeta(total, offset, limit);
		response.setMessage("Success");

		return response;
	}

	public BaseResponse<T> setResponseV2(Query<T> query, String sort, String filter, int offset, int limit) throws IOException {
		return setResponseV2(query, sort, filter, offset, limit, null);
	}

	public BaseResponse<T> setResponse(Query<T> query, String sort, String filter, int offset, int limit) {

		// SORT

		// filter
		ExpressionList<T> exp = query.where();

		// assign
		query = exp.query();
		int total = query.findList().size();

		if (limit != 0) {
			query = query.setMaxRows(limit);
		}

		List<T> list = query.findPagingList(limit).getPage(offset).getList();
		System.out.println(query.getGeneratedSql());

		BaseResponse<T> response = new BaseResponse<T>();
		response.setData(list);
		response.setMeta(total, offset, limit);
		response.setMessage("Success");

		return response;
	}

	public void setFilter(ExpressionList<T> exp, SimpleDateFormat formatter, ApiFilter af) {
		try {
			setDateFilter(exp, formatter, af);
		} catch (ParseException e) {
			setObjectFilter(exp, af);
		}
	}

	public void setObjectFilter(ExpressionList<T> exp, ApiFilter af) {
		if (af.getOperator() == null) {
			exp.eq(af.getProperty(), (af.getValues()[0]).getValue());
		} else {
			switch (af.getOperator()) {
			case "equals":
				exp.eq(af.getProperty(), (af.getValues()[0]).getValue());
				break;
			case "not_equals":
				exp.ne(af.getProperty(), (af.getValues()[0]).getValue());
				break;
			case "between":
				exp.between(af.getProperty(), (af.getValues()[0]).getValue(), (af.getValues()[0]).getValue());
				break;
			case "greater_than":
				exp.gt(af.getProperty(), (af.getValues()[0]).getValue());
				break;
			case "greater_than_or_equals":
				exp.ge(af.getProperty(), (af.getValues()[0]).getValue());
				break;
			case "less_than":
				exp.lt(af.getProperty(), (af.getValues()[0]).getValue());
				break;
			case "less_than_or_equals":
				exp.le(af.getProperty(), (af.getValues()[0]).getValue());
				break;
			case "is_null":
				exp.isNull(af.getProperty());
				break;
			case "is_not_null":
				exp.isNotNull(af.getProperty());
				break;
			case "like":
				exp.ilike(af.getProperty(), "%" + (af.getValues()[0]).getValue().toString() + "%");
				break;
			case "in":
				List<Object> objs = new ArrayList<Object>();
				for (int i = 0; i < af.getValues().length; i++) {
					objs.add(i, (af.getValues()[i]).getValue());
				}
				exp.in(af.getProperty(), objs);
				break;
			case "not_in":
				List<Object> not_objs = new ArrayList<Object>();
				for (int i = 0; i < af.getValues().length; i++) {
					not_objs.add(i, (af.getValues()[i]).getValue());
				}
				exp.not(Expr.in(af.getProperty(), not_objs));
				break;
			}
		}
	}

	public void setDateFilter(ExpressionList<T> exp, SimpleDateFormat formatter, ApiFilter af) throws ParseException {
		if ((af.getValues()[0]).getValue().toString().length() == 10) {
			formatter = new SimpleDateFormat("yyyy-MM-dd");
		} else {
			formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		}
		if (af.getOperator() == null) {
			exp.eq(af.getProperty(), formatter.parse((af.getValues()[0]).getValue().toString()));
		} else {
			switch (af.getOperator()) {
			case "equals":
				exp.eq(af.getProperty(), formatter.parse((af.getValues()[0]).getValue().toString()));
				break;
			case "not_equals":
				exp.ne(af.getProperty(), formatter.parse((af.getValues()[0]).getValue().toString()));
				break;
			case "between":
				exp.between(af.getProperty(), formatter.parse((af.getValues()[0]).getValue().toString()),
						formatter.parse((af.getValues()[1]).getValue().toString()));
				break;
			case "greater_than":
				exp.gt(af.getProperty(), formatter.parse((af.getValues()[0]).getValue().toString()));
				break;
			case "greater_than_or_equals":
				exp.ge(af.getProperty(), formatter.parse((af.getValues()[0]).getValue().toString()));
				break;
			case "less_than":
				exp.lt(af.getProperty(), formatter.parse((af.getValues()[0]).getValue().toString()));
				break;
			case "less_than_or_equals":
				exp.le(af.getProperty(), formatter.parse((af.getValues()[0]).getValue().toString()));
				break;
			case "is_null":
				exp.isNull(af.getProperty());
				break;
			case "like":
				formatter.parse((af.getValues()[0]).getValue().toString());
				break;
			case "is_not_null":
				exp.isNotNull(af.getProperty());
				break;
			case "in":
				List<Date> dates = new ArrayList<Date>();
				for (int i = 0; i < af.getValues().length; i++) {
					dates.add(i, formatter.parse((af.getValues()[i]).getValue().toString()));
				}
				exp.in(af.getProperty(), dates);
				break;
			case "not_in":
				List<Date> not_dates = new ArrayList<Date>();
				for (int i = 0; i < af.getValues().length; i++) {
					not_dates.add(i, formatter.parse((af.getValues()[i]).getValue().toString()));
				}
				exp.not(Expr.in(af.getProperty(), not_dates));
				break;
			}
		}
	}

}
