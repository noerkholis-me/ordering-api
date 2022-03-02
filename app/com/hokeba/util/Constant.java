package com.hokeba.util;

import play.Play;

/**
 * Created by hendriksaragih on 1/31/17.
 */
public class Constant {
    private static Constant instance = null;
    public final static String API_KEY = Play.application().configuration().getString("hellobisnis.api.key");
    public final static String KIOSK_TOKEN = Play.application().configuration().getString("hellobisnis.kiosk.token");
    private String emailUser = null;
    private String emailPassword = null;
    private String emailSmtp = null;
    private String emailSender = null;
    private String apiKeyWeb = null;
    private String apiKeyIOS = null;
    private String apiKeyAndroid = null;
    private String catalogPath = null;
    
    //odoo
    private String apiKeyOdoo = null;
    private String tokenOdoo = null;
    private String imageUrl = null;
    private String imagePath = null;
    private String frontEndUrl = null;
    private String merchantUrl = null;
    private Boolean isRunningOdoo = null;
    private String odooHost = null;
    private Integer odooPort = null;
    private String odooDatabase = null;
    private String odooUser = null;
    private String odooPassword = null;
    private String ckeditorImageUrl = null;
    public static final String defaultCurrency = "IDR";

    public static Constant getInstance() {
        if (instance == null) {
            instance = new Constant();
        }
        return instance;
    }

    public String getFrontEndUrl() {
        if (frontEndUrl == null){
            frontEndUrl = Play.application().configuration().getString("whizliz.frontend.url");
        }
        return frontEndUrl;
    }

    public String getMerchantUrl() {
        if (merchantUrl == null){
            merchantUrl = Play.application().configuration().getString("whizliz.merchant.url");
        }
        return merchantUrl;
    }

    public String getEmailUser() {
        if (emailUser == null){
            emailUser = Play.application().configuration().getString("whizliz.email.user");
        }
        return emailUser;
    }

    public String getEmailPassword() {
        if (emailPassword == null){
            emailPassword = Play.application().configuration().getString("whizliz.email.password");
        }
        return emailPassword;
    }

    public String getEmailSmtp() {
        if (emailSmtp == null){
            emailSmtp = Play.application().configuration().getString("whizliz.email.smtp");
        }
        return emailSmtp;
    }

    public String getEmailSender() {
        if (emailSender == null){
            emailSender = Play.application().configuration().getString("whizliz.email.sender");
        }
        return emailSender;
    }

    public String getApiKeyWeb() {
        if (apiKeyWeb == null){
            apiKeyWeb = Play.application().configuration().getString("whizliz.api_key.web");
        }
        return apiKeyWeb;
    }

    public String getApiKeyIOS() {
        if (apiKeyIOS == null){
            apiKeyIOS = Play.application().configuration().getString("whizliz.api_key.ios");
        }
        return apiKeyIOS;
    }

    public String getApiKeyAndroid() {
        if (apiKeyAndroid == null){
            apiKeyAndroid = Play.application().configuration().getString("whizliz.api_key.android");
        }
        return apiKeyAndroid;
    }

    public String getImageUrl() {
        if (imageUrl == null){
            imageUrl = Play.application().configuration().getString("whizliz.images.url");
        }
        return imageUrl;
    }

    public String getImagePath() {
        if (imagePath == null){
            imagePath = Play.application().configuration().getString("whizliz.images.path");
        }
        return imagePath;
    }

    public String getCatalogPath() {
        if (catalogPath == null){
        	catalogPath = Play.application().configuration().getString("whizliz.catalogs.path");
        }
        return catalogPath;
    }

    public Boolean isRunningOdoo() {
        if (isRunningOdoo == null){
            isRunningOdoo = Play.application().configuration().getBoolean("whizliz.odoo.is_running");
        }
        return isRunningOdoo;
    }

    public String getOdooHost() {
        if (odooHost == null){
            odooHost = Play.application().configuration().getString("whizliz.odoo.host");
        }
        return odooHost;
    }

    public Integer getOdooPort() {
        if (odooPort == null){
            odooPort = Play.application().configuration().getInt("whizliz.odoo.port");
        }
        return odooPort;
    }

    public String getOdooDatabase() {
        if (odooDatabase == null){
            odooDatabase = Play.application().configuration().getString("whizliz.odoo.database");
        }
        return odooDatabase;
    }

    public String getOdooUser() {
        if (odooUser == null){
            odooUser = Play.application().configuration().getString("whizliz.odoo.user");
        }
        return odooUser;
    }

    public String getOdooPassword() {
        if (odooPassword == null){
            odooPassword = Play.application().configuration().getString("whizliz.odoo.password");
        }
        return odooPassword;
    }

    public String getApiKeyOdoo() {
        if (apiKeyOdoo == null){
            apiKeyOdoo = Play.application().configuration().getString("whizliz.api_key.odoo");
        }
        return apiKeyOdoo;
    }

    public String getTokenOdoo() {
        if (tokenOdoo == null){
            tokenOdoo = Play.application().configuration().getString("whizliz.odoo.token");
        }
        return tokenOdoo;
    }

    public String getCKEditorImageUrl() {
        if (ckeditorImageUrl == null){
            ckeditorImageUrl = Play.application().configuration().getString("whizliz.ckeditor_images.url");
        }
        return ckeditorImageUrl;
    }
}
