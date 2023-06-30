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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.alibaba.fastjson.JSON;
import org.json.*;  

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@RestController
public class TradingviewApiController {
	
	
	final private String apiDomainUrl = "https://api.bybit.com";
	final private String getWB = "/v5/account/wallet-balance";
	
	
	final static String API_KEY = "Hrv70SPyEbnYG1TDSl";
    final static String API_SECRET = "r5J7hkl2AZfjmeqjAttu1Cze79u4wtaCCt2i";
    //final static String TIMESTAMP = Long.toString(ZonedDateTime.now().toInstant().toEpochMilli());
    final static String TIMESTAMP = Long.toString(new java.sql.Timestamp(System.currentTimeMillis()).getTime());
    final static String RECV_WINDOW = "50000";
	
    
    public void placeOrder() throws InvalidKeyException, NoSuchAlgorithmException {
    	System.out.println("===========placeOrder=====================");
    	 Map<String, Object> map = new HashMap<>();
         map.put("category", "linear"); //linear : USDT perpetual, and USDC contract, including USDC perp, USDC futures
         map.put("symbol", "BTCUSDT");
         map.put("side", "Buy");  //buy or sell
         map.put("orderType", "Market");
         map.put("qty", "1");
         map.put("positionIdx", "1");
         
         String signature = genPostSign(map);
         String jsonMap = JSON.toJSONString(map);
         
         OkHttpClient client = new OkHttpClient().newBuilder().build();
         MediaType mediaType = MediaType.parse("application/json");
         
         
         RequestBody requestBody = RequestBody.Companion.create(jsonMap,mediaType);
         System.out.println("requestBody:"+requestBody.toString());
         
         Request request = new Request.Builder()
                 .url("https://api-testnet.bybit.com/v5/order/create")
                 .post(requestBody)
                 .addHeader("X-BAPI-API-KEY", API_KEY)
                 .addHeader("X-BAPI-SIGN", signature)
                 .addHeader("X-BAPI-SIGN-TYPE", "2")
                 .addHeader("X-BAPI-TIMESTAMP", TIMESTAMP)
                 .addHeader("X-BAPI-RECV-WINDOW", RECV_WINDOW)
                 .addHeader("Content-Type", "application/json")
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
    
	
	@RequestMapping("/tradingview-alerts")
	public void receiveAlert( String alertData) throws InvalidKeyException, NoSuchAlgorithmException {
		System.out.println("hi~~~~!S");
		
		//JsonPrimitive jsonPrimitive = gson.fromJson(alertData, JsonPrimitive.class);
        // Alert 데이터 처리 코드 추가
        System.out.println(alertData);
        
        //Gson g = new Gson();
        //Gson s = g.fromJson(alertData);  
       
        //?accountType=CONTRACT&coin=USDT
//        Map<String, Object> map = new HashMap<>();
//        map.put("accountType", "CONTRACT");
//        map.put("coin", "USDT");

        
        placeOrder();
        
//        String signature = genGetSign(map);
//        StringBuilder sb = genQueryStr(map);        
//        
//        
//        OkHttpClient client = new OkHttpClient().newBuilder().build();
//        Request request = new Request.Builder()
//                .url("https://api.bybit.com/v5/account/wallet-balance?" + sb)
//                .get()
//                .addHeader("X-BAPI-API-KEY", API_KEY)
//                .addHeader("X-BAPI-SIGN", signature)
//                .addHeader("X-BAPI-SIGN-TYPE", "2")
//                .addHeader("X-BAPI-TIMESTAMP", TIMESTAMP)
//                .addHeader("X-BAPI-RECV-WINDOW", RECV_WINDOW)
//                .build();
//        Call call = client.newCall(request);
//        try {
//            Response response = call.execute();
//            System.out.println("response:"+response);
//            assert response.body() != null;
//            System.out.println("~~~");
//            System.out.println("!!!!!!!!!"+response.body().string());
//        }catch (IOException e){
//            e.printStackTrace();
//        }
        
         
        
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
     * The way to generate the sign for POST requests
     * @param params: Map input parameters
     * @return signature used to be a parameter in the header
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public String genPostSign(Map<String, Object> params) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(API_SECRET.getBytes(), "HmacSHA256");
        sha256_HMAC.init(secret_key);

        String paramJson = JSON.toJSONString(params);
        System.out.println("paramJson: "+ paramJson);
        //String paramJson = params.toString();        
        String sb = TIMESTAMP + API_KEY + RECV_WINDOW + paramJson;
        return bytesToHex(sha256_HMAC.doFinal(sb.getBytes()));
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
        return sb;
    }
	

}
