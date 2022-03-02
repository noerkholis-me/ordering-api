package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by hendriksaragih on 3/2/17.
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class MapMerchantCouriers {
    private List<MapMerchantShipping> merchant;
    private List<MapMerchantShipping> vendor;

    public List<MapMerchantShipping> getMerchant() {
        return merchant;
    }

    public void setMerchant(List<MapMerchantShipping> merchant) {
        this.merchant = merchant;
    }

    public List<MapMerchantShipping> getVendor() {
        return vendor;
    }

    public void setVendor(List<MapMerchantShipping> vendor) {
        this.vendor = vendor;
    }
}
