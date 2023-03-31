package dtos.merchant;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class MerchantSessionResponse {

    @JsonProperty("email")
    public String email;
    @JsonProperty("birth_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Jakarta")
    public Date birthDate;
    public String gender;
    @JsonProperty("full_name")
    public String fullName;
    @JsonProperty("domain")
    public String domain;

    @JsonProperty("account_number")
    public String accountNumber;
    @JsonProperty("account_alias")
    public String accountAlias;

    @JsonProperty("merchant_code")
    public String merchantCode;

    public String name;
    public String logo;
    public boolean display;
    public String type;
    @JsonProperty("company_name")
    public String companyName;
    public String status;

    @JsonProperty("city_name")
    public String cityName;
    @JsonProperty("postal_code")
    public String postalCode;
    public String province;
    @JsonProperty("commission_type")
    public String commissionType;
    public String address;
    public String phone;

    @JsonProperty("meta_description")
    public String metaDescription;
    public String story;
    public String url;
    @JsonProperty("merchant_url_page")
    public String merchantUrlPage;
    public boolean anchor;
    @JsonProperty("url_banner")
    public String urlBanner;

    @JsonProperty("quick_response")
    public Long quickResponse;
    @JsonProperty("product_availability")
    public Long productAvailability;
    @JsonProperty("product_quality")
    public Long productQuality;
    public Double rating;
    @JsonProperty("count_rating")
    public int countRating;

    public Double balance;
    public Long resetTime;

    @JsonProperty("is_active")
    public boolean isActive;
    @JsonProperty("merchant_type")
    public String merchantType;
    @JsonProperty("merchant_qr_code")
    public String merchantQrCode;
    @JsonProperty("product_store_required")
    public boolean productStoreRequired;
    @JsonProperty("global_store_qr_group")
    public boolean globalStoreQrGroup;

    public MerchantSessionResponse() {
    }

    public MerchantSessionResponse(String email, Date birthDate, String gender, String fullName, String domain, String accountNumber, String accountAlias, String merchantCode, String name, String logo, boolean display, String type, String companyName, String status, String cityName, String postalCode, String province, String commissionType, String address, String phone, String metaDescription, String story, String url, String merchantUrlPage, boolean anchor, String urlBanner, Long quickResponse, Long productAvailability, Long productQuality, Double rating, int countRating, Double balance, Long resetTime, boolean isActive) {
        this.email = email;
        this.birthDate = birthDate;
        this.gender = gender;
        this.fullName = fullName;
        this.domain = domain;
        this.accountNumber = accountNumber;
        this.accountAlias = accountAlias;
        this.merchantCode = merchantCode;
        this.name = name;
        this.logo = logo;
        this.display = display;
        this.type = type;
        this.companyName = companyName;
        this.status = status;
        this.cityName = cityName;
        this.postalCode = postalCode;
        this.province = province;
        this.commissionType = commissionType;
        this.address = address;
        this.phone = phone;
        this.metaDescription = metaDescription;
        this.story = story;
        this.url = url;
        this.merchantUrlPage = merchantUrlPage;
        this.anchor = anchor;
        this.urlBanner = urlBanner;
        this.quickResponse = quickResponse;
        this.productAvailability = productAvailability;
        this.productQuality = productQuality;
        this.rating = rating;
        this.countRating = countRating;
        this.balance = balance;
        this.resetTime = resetTime;
        this.isActive = isActive;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountAlias() {
        return accountAlias;
    }

    public void setAccountAlias(String accountAlias) {
        this.accountAlias = accountAlias;
    }

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public boolean isDisplay() {
        return display;
    }

    public void setDisplay(boolean display) {
        this.display = display;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCommissionType() {
        return commissionType;
    }

    public void setCommissionType(String commissionType) {
        this.commissionType = commissionType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMetaDescription() {
        return metaDescription;
    }

    public void setMetaDescription(String metaDescription) {
        this.metaDescription = metaDescription;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMerchantUrlPage() {
        return merchantUrlPage;
    }

    public void setMerchantUrlPage(String merchantUrlPage) {
        this.merchantUrlPage = merchantUrlPage;
    }

    public boolean isAnchor() {
        return anchor;
    }

    public void setAnchor(boolean anchor) {
        this.anchor = anchor;
    }

    public String getUrlBanner() {
        return urlBanner;
    }

    public void setUrlBanner(String urlBanner) {
        this.urlBanner = urlBanner;
    }

    public Long getQuickResponse() {
        return quickResponse;
    }

    public void setQuickResponse(Long quickResponse) {
        this.quickResponse = quickResponse;
    }

    public Long getProductAvailability() {
        return productAvailability;
    }

    public void setProductAvailability(Long productAvailability) {
        this.productAvailability = productAvailability;
    }

    public Long getProductQuality() {
        return productQuality;
    }

    public void setProductQuality(Long productQuality) {
        this.productQuality = productQuality;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public int getCountRating() {
        return countRating;
    }

    public void setCountRating(int countRating) {
        this.countRating = countRating;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Long getResetTime() {
        return resetTime;
    }

    public void setResetTime(Long resetTime) {
        this.resetTime = resetTime;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getMerchantType() { return merchantType; }

    public void setMerchantType(String merchantType) { this.merchantType = merchantType; }

    public String getMerchantQrCode() { return merchantQrCode; }

    public void setMerchantQrCode(String merchantQrCode) { this.merchantQrCode = merchantQrCode; }
}
