package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MapEligibleLoyalty {

	private long pointsCanBeUsed;

	@JsonIgnore
	private long pointsCanBeEarned;
	
	public MapEligibleLoyalty() {
		pointsCanBeEarned = 0;
		pointsCanBeUsed = 0;
	}
	
	public MapEligibleLoyalty(long pointsCanBeUsed, long pointsCanBeEarned) {
		this.pointsCanBeEarned = pointsCanBeEarned;
		this.pointsCanBeUsed = pointsCanBeUsed;
	}

	@JsonGetter("points_can_be_used")
	public long getPointsCanBeUsed() {
		return pointsCanBeUsed;
	}

	@JsonProperty("points_can_be_earned")
	public long getPointsCanBeEarned() {
		return pointsCanBeEarned;
	}
}
