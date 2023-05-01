package com.rich.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

@RestController
public class TradingviewApiController {
	
	private Gson gson = new Gson();
	
	@RequestMapping("/tradingview-alerts")
	public void receiveAlert(@RequestBody String alertData) {
		System.out.println("hi~~~~");
		JsonObject jsonObject = gson.fromJson(alertData, JsonObject.class);
        // Alert 데이터 처리 코드 추가
		System.out.println("hi~~~~");
        System.out.println(alertData);
    }
	

}
