package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hokeba.util.CommonFunction;

import models.ProductReview;

/**
 * Created by hendriksaragih on 3/2/17.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class MapProductReview {
	private Long id;
	public String title;
	public String comment;
	public int rating;
	@JsonProperty("created_at")
	public String createdAt;

	@JsonProperty("reviewer_name")
	private String reviewerName;
	
	public MapProductReview() {
		super();
	}

	public MapProductReview(ProductReview review) {
		super();
		this.id = review.id;
		this.title = review.title;
		this.comment = review.comment;
		this.rating = review.rating;
		this.createdAt = CommonFunction.getDateTime(review.getCreatedAt());
		this.reviewerName = review.getReviewerName();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public String getReviewerName() {
		return reviewerName;
	}

	public void setReviewerName(String reviewerName) {
		this.reviewerName = reviewerName;
	}
}
