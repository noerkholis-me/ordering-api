package com.hokeba.nexmo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import play.Logger;
import play.Play;
import play.libs.F.Promise;
import play.libs.Json;
import play.libs.ws.WS;
import play.libs.ws.WSRequestHolder;

import java.util.concurrent.TimeUnit;


/**
 * Created by hendriksaragih on 12/16/16.
 */
public class Nexmo {
    private final static Logger.ALogger logger = Logger.of(Nexmo.class);
    private static Nexmo instance;
    private String url, api_key, api_secret;
    private final String FROM = "CREDITPLUS";

    public Nexmo() {
        init();
    }

    public static Nexmo getInstance() {
        if (instance == null) {
            instance = new Nexmo();
        }
        return instance;
    }

    private void init() {
        url = Play.application().configuration().getString("masscredit.nexmo.url");
        api_key = Play.application().configuration().getString("masscredit.nexmo.api_key");
        api_secret = Play.application().configuration().getString("masscredit.nexmo.api_secret");
    }

    @SuppressWarnings("unchecked")
    public Object post(String to, String text, @SuppressWarnings("rawtypes") Class responseClass) {
        try {
            RequestJson req = new RequestJson(api_key, api_secret, FROM, to, text);
            WSRequestHolder requestHolder = WS.url(buildUrl());
            Promise<JsonNode> promise = requestHolder.setContentType("application/json")
                    .post(Json.toJson(req)).map(response -> {
                        response.getStatus();
                        JsonNode json = response.asJson();
                        System.out.println(Json.toJson(json));
                        return json;
                    });

            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(promise.get(60, TimeUnit.SECONDS).toString(), responseClass);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String buildUrl() {
        return url + "/sms/json";
    }
}
