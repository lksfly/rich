package com.rich.controller;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@RestController
public class TradingviewApiController {
	
	private Gson gson = new Gson();
	
	
	final private String apiDomainUrl = "https://api.bybit.com";
	final private String getWB = "/v5/account/wallet-balance";
	
	
	final static String API_KEY = "LGPINISDZRMULCFELE";
    final static String API_SECRET = "LVRWFWUXKUKRGIXPZZCGOCSUFRIADYLRKXOE";
    //final static String TIMESTAMP = Long.toString(ZonedDateTime.now().toInstant().toEpochMilli());
    final static String TIMESTAMP = Long.toString(new java.sql.Timestamp(System.currentTimeMillis()).getTime());
    final static String RECV_WINDOW = "50000";
	
	
	@RequestMapping("/tradingview-alerts")
	public void receiveAlert(@RequestBody String alertData) throws InvalidKeyException, NoSuchAlgorithmException {
		System.out.println("hi~~~~!S");
		
		//JsonPrimitive jsonPrimitive = gson.fromJson(alertData, JsonPrimitive.class);
        // Alert 데이터 처리 코드 추가
		System.out.println("hi~~~~");
        System.out.println(alertData);
        
        //?accountType=CONTRACT&coin=USDT
        Map<String, Object> map = new HashMap<>();
        map.put("accountType", "CONTRACT");
        map.put("coin", "USDT");

        String signature = genGetSign(map);
        StringBuilder sb = genQueryStr(map);        
        
        
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        Request request = new Request.Builder()
                .url("https://api.bybit.com/v5/account/wallet-balance?" + sb)
                .get()
                .addHeader("X-BAPI-API-KEY", API_KEY)
                .addHeader("X-BAPI-SIGN", signature)
                .addHeader("X-BAPI-SIGN-TYPE", "2")
                .addHeader("X-BAPI-TIMESTAMP", TIMESTAMP)
                .addHeader("X-BAPI-RECV-WINDOW", RECV_WINDOW)
                .build();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            System.out.println("response:"+response);
            assert response.body() != null;
            System.out.println("~~~");
            System.out.println("!!!!!!!!!"+response.body().string());
        }catch (IOException e){
            e.printStackTrace();
        }
        
         
        
    }
	
	
	  /**
     * The way to generate the sign for GET requests
     * @param params: Map input parameters
     * @return signature used to be a parameter in the header
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public String genGetSign(Map<String, Object> params) throws NoSuchAlgorithmException, InvalidKeyException {
        StringBuilder sb = genQueryStr(params);
        String queryStr = TIMESTAMP + API_KEY + RECV_WINDOW + sb;

        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(API_SECRET.getBytes(), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        return bytesToHex(sha256_HMAC.doFinal(queryStr.getBytes()));
    }
    
    /**
     * To convert bytes to hex
     * @param hash
     * @return hex string
     */
    public String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * To generate query string for GET requests
     * @param map
     * @return
     */
    public StringBuilder genQueryStr(Map<String, Object> map) {
        Set<String> keySet = map.keySet();
        Iterator<String> iter = keySet.iterator();
        StringBuilder sb = new StringBuilder();
        while (iter.hasNext()) {
            String key = iter.next();
            sb.append(key)
                    .append("=")
                    .append(map.get(key))
                    .append("&");
        }
        sb.deleteCharAt(sb.length() - 1);
        System.out.println("sb==============="+sb);
        return sb;
    }
	

}
