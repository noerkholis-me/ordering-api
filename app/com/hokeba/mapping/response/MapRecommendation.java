package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by hendriksaragih on 3/2/17.
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class MapRecommendation {
    @JsonProperty("best_selling")
    private List<MapProductRecommendation> bestSelling;
    @JsonProperty("top_trending")
    private List<MapProductRecommendation> topTrending;
    @JsonProperty("recent_additions")
    private List<MapProductRecommendation> recentAdditions;

    public MapRecommendation(){

    }

    public MapRecommendation(List<MapProductRecommendation> bestSelling, List<MapProductRecommendation> topTrending, List<MapProductRecommendation> recentAdditions){
        this.bestSelling = bestSelling;
        this.topTrending = topTrending;
        this.recentAdditions = recentAdditions;
    }

    public static MapRecommendation dummy(){
        List<MapProductRecommendation> dummyProduct = dummyProduct();
        return new MapRecommendation(dummyProduct, dummyProduct, dummyProduct);
    }

    public static List<MapProductRecommendation> dummyProduct(){
        List<MapProductRecommendation> data = new LinkedList<>();
        data.add(new MapProductRecommendation(1L, "Redmi 4 Note Pro 32GB + 16gb External Gold", "http://backend-hokeba.stagingapps.net/images/product/keyboard.jpg"));
        data.add(new MapProductRecommendation(2L, "Redmi 4 Note Pro 32GB + 16gb External Gold", "http://backend-hokeba.stagingapps.net/images/product/keyboard.jpg"));
        data.add(new MapProductRecommendation(3L, "Redmi 4 Note Pro 32GB + 16gb External Gold", "http://backend-hokeba.stagingapps.net/images/product/keyboard.jpg"));

        return data;
    }

    public List<MapProductRecommendation> getBestSelling() {
        return bestSelling;
    }

    public void setBestSelling(List<MapProductRecommendation> bestSelling) {
        this.bestSelling = bestSelling;
    }

    public List<MapProductRecommendation> getTopTrending() {
        return topTrending;
    }

    public void setTopTrending(List<MapProductRecommendation> topTrending) {
        this.topTrending = topTrending;
    }

    public List<MapProductRecommendation> getRecentAdditions() {
        return recentAdditions;
    }

    public void setRecentAdditions(List<MapProductRecommendation> recentAdditions) {
        this.recentAdditions = recentAdditions;
    }
}
