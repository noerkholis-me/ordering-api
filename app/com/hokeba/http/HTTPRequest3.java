package com.hokeba.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Iterables;
import com.hokeba.http.response.global.ServiceResponse;

import play.Logger;
import play.libs.F.Function;
import play.libs.F.Promise;
import play.libs.Json;
import play.libs.ws.WS;
import play.libs.ws.WSRequestHolder;
import play.libs.ws.WSResponse;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
/**
 * Created by hendriksaragih on 3/19/17.
 */
public class HTTPRequest3 {

    public String getParameter(Iterable<Param> params) {
        String parameter = "";
        int limit = Iterables.size(params);
        int count = 0;
        for (Param param : params) {
            parameter += param.getName() + "=" + param.getValue();
            count++;
            if (count != limit) {
                parameter += "&";
            }
        }
        return parameter;
    }

    public String getURL(String url, String method, String parameter) {
        String urlSet = url + method;
        if (!parameter.equals(""))
            urlSet += "?" + parameter;
        System.out.println("URL : " + urlSet);
        return urlSet;
    }

    public String baseGet(WSRequestHolder wsr) {
        try {
            SSLCertificateValidation.disable();
            Promise<JsonNode> promise = wsr.get().map(new Function<WSResponse, JsonNode>() {
                @SuppressWarnings("deprecation")
                @Override
                public JsonNode apply(WSResponse response) throws Throwable {
                	System.out.println("HTTP RES : " + response.getBody());
                    ObjectNode result = Json.newObject();
                    result.put("code", response.getStatus());
                    result.put("data", response.asJson());
                    return result;
                }
            });
            return promise.get(60, TimeUnit.SECONDS).toString();
        } catch (Exception e) {
            e.printStackTrace();
            ObjectNode result = Json.newObject();
            result.put("code", 408);
            result.put("data", "Request Time Out.");
            return result.toString();
        }
    }

    public String basePost(WSRequestHolder wsr, Object requestBody) {
        try {
            SSLCertificateValidation.disable();
            Promise<JsonNode> promise = wsr.post(Json.toJson(requestBody)).map(new Function<WSResponse, JsonNode>() {
                @SuppressWarnings("deprecation")
                @Override
                public JsonNode apply(WSResponse response) throws Throwable {
                	System.out.println("HTTP RES : " + response.getBody());
                    ObjectNode result = Json.newObject();
                    result.put("code", response.getStatus());
                    result.put("data", response.asJson());
                    return result;
                }
            });
            return promise.get(60, TimeUnit.SECONDS).toString();
        } catch (Exception e) {
            e.printStackTrace();
            ObjectNode result = Json.newObject();
            result.put("code", 408);
            result.put("data", "Request Time Out.");
            return result.toString();
        }
    }

    public String basePatch(WSRequestHolder wsr, Object requestBody) {
        try {
            SSLCertificateValidation.disable();
            Promise<JsonNode> promise = wsr.patch(Json.toJson(requestBody)).map(new Function<WSResponse, JsonNode>() {
                @SuppressWarnings("deprecation")
                @Override
                public JsonNode apply(WSResponse response) throws Throwable {
                	System.out.println("HTTP RES : " + response.getBody());
                    ObjectNode result = Json.newObject();
                    result.put("code", response.getStatus());
                    result.put("data", response.asJson());
                    return result;
                }
            });
            return promise.get(60, TimeUnit.SECONDS).toString();
        } catch (Exception e) {
            e.printStackTrace();
            ObjectNode result = Json.newObject();
            result.put("code", 408);
            result.put("data", "Request Time Out.");
            return result.toString();
        }
    }

    public String basePut(WSRequestHolder wsr, Object requestBody) {
        try {
            SSLCertificateValidation.disable();
            Promise<JsonNode> promise = wsr.put(Json.toJson(requestBody)).map(new Function<WSResponse, JsonNode>() {
                @SuppressWarnings("deprecation")
                @Override
                public JsonNode apply(WSResponse response) throws Throwable {
                	System.out.println("HTTP RES : " + response.getBody());
                    ObjectNode result = Json.newObject();
                    result.put("code", response.getStatus());
                    result.put("data", response.asJson());
                    return result;
                }
            });
            return promise.get(60, TimeUnit.SECONDS).toString();
        } catch (Exception e) {
            e.printStackTrace();
            ObjectNode result = Json.newObject();
            result.put("code", 408);
            result.put("data", "Request Time Out.");
            return result.toString();
        }
    }

    public String baseDelete(WSRequestHolder wsr) {
        try {
            SSLCertificateValidation.disable();
            Promise<JsonNode> promise = wsr.delete().map(new Function<WSResponse, JsonNode>() {
                @SuppressWarnings("deprecation")
                @Override
                public JsonNode apply(WSResponse response) throws Throwable {
                	System.out.println("HTTP RES : " + response.getBody());
                    ObjectNode result = Json.newObject();
                    result.put("code", response.getStatus());
                    result.put("data", response.asJson());
                    return result;
                }
            });
            return promise.get(60, TimeUnit.SECONDS).toString();
        } catch (Exception e) {
            e.printStackTrace();
            ObjectNode result = Json.newObject();
            result.put("code", 408);
            result.put("data", "Request Time Out.");
            return result.toString();
        }
    }

    public ServiceResponse get(String url, Param authorization, Param...headers) {
        WSRequestHolder requestHolder = WS.url(url);
        requestHolder.setHeader("Authorization", (String) authorization.getValue());
        requestHolder.setHeader("Accept", "application/json");
        requestHolder.setHeader("Content-Type", "application/json");
        for (Param param : headers) {
            requestHolder.setHeader(param.getName(), ""+param.getValue());
        }
        ObjectMapper mapper = new ObjectMapper();
        System.out.println("################");
        System.out.println(baseGet(requestHolder));
//        Logger.warn(Tool.prettyPrint(baseGet(requestHolder).t));
        try {
            return mapper.readValue(baseGet(requestHolder), ServiceResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ServiceResponse get(String url) {
        WSRequestHolder requestHolder = WS.url(url);
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(baseGet(requestHolder), ServiceResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ServiceResponse post(String url, Param authorization, final Object request, Param...headers) {
        WSRequestHolder requestHolder = WS.url(url);
        requestHolder.setHeader("Authorization", (String) authorization.getValue());
        requestHolder.setHeader("Accept", "application/json");
        requestHolder.setHeader("Content-Type", "application/json");
        for (Param param : headers) {
            requestHolder.setHeader(param.getName(), ""+param.getValue());
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(basePost(requestHolder, request), ServiceResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public ServiceResponse postXForm(String url, final String request, Param...headers) {
		WSRequestHolder requestHolder = WS.url(url);
		requestHolder.setHeader("Accept", "application/json");
		requestHolder.setContentType("application/x-www-form-urlencoded");
		for (Param param : headers) {
			requestHolder.setHeader(param.getName(), ""+param.getValue());
		}
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(basePost(requestHolder, request), ServiceResponse.class);
		} catch (Exception e) {
			Logger.error("postXForm", e);
		}
		return null;
	}

    public ServiceResponse patch(String url, Param authorization, final Object request, Param...headers) {
        WSRequestHolder requestHolder = WS.url(url);
        requestHolder.setHeader("Authorization", (String) authorization.getValue());
        requestHolder.setHeader("Accept", "application/json");
        requestHolder.setHeader("Content-Type", "application/json");
        for (Param param : headers) {
            requestHolder.setHeader(param.getName(), ""+param.getValue());
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(basePatch(requestHolder, request), ServiceResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ServiceResponse put(String url, Param authorization, final Object request, Param...headers) {
        WSRequestHolder requestHolder = WS.url(url);
        requestHolder.setHeader("Authorization", (String) authorization.getValue());
        requestHolder.setHeader("Accept", "application/json");
        requestHolder.setHeader("Content-Type", "application/json");
        for (Param param : headers) {
            requestHolder.setHeader(param.getName(), ""+param.getValue());
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(basePut(requestHolder, request), ServiceResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ServiceResponse delete(String url, Param authorization, Param...headers) {
        WSRequestHolder requestHolder = WS.url(url);
        requestHolder.setHeader("Authorization", (String) authorization.getValue());
        requestHolder.setHeader("Accept", "application/json");
        requestHolder.setHeader("Content-Type", "application/json");
        for (Param param : headers) {
            requestHolder.setHeader(param.getName(), ""+param.getValue());
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(baseDelete(requestHolder), ServiceResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
}
