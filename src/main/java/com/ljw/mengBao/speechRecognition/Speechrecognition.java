package com.ljw.mengBao.speechRecognition;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ljw.mengBao.AIUI.AIUIService;
import com.ljw.mengBao.utils.UnitService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class Speechrecognition {
	
	@RequestMapping("/test")
	public String name() {
		return "ljw";
	}
	
	
//	@RequestMapping("/voice")
//	public String fileUpload(HttpServletRequest request,
//                             @RequestParam(value = "voiceText", required = true) String voiceText) {
//
//		//语音识别后进行人机交互，命中意图
//	    String unitResponse = UnitService.utterance(voiceText);
//	    //解析百度相应的动作列表
//		//result
//		JSONObject resultJson = JSONObject.parseObject(unitResponse);
//		String result = resultJson.getString("result");
//		//response
//		JSONObject responseJson = JSONObject.parseObject(result);
//		String response = responseJson.getString("response");
//		//action_list
//		JSONObject actionlistList = JSONObject.parseObject(response);
//		String actionList = actionlistList.getString("action_list");
//		//say
//		String say = "";
//		JSONArray sayArray = JSONArray.parseArray(actionList);
//		for (int i = 0; i <sayArray.size() ; i++) {
//			JSONObject sayJson = sayArray.getJSONObject(i);
//			say = sayJson.getString("say");
//		}
//
//        System.out.println(say);
//		return say;
//	}


	@RequestMapping("/textToAIUI")
	public String textToAIUI(HttpServletRequest request,
							 @RequestParam(value = "voiceText", required = true) String voiceText) {

		//语音识别后进行人机交互，命中意图
		String aiuiResponse = "";
		try {
			aiuiResponse = AIUIService.aiuiWebApi(voiceText);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//解析讯飞AIUI相应的动作列表
		//data
		JSONObject resultJson = JSONObject.parseObject(aiuiResponse);
		String result = resultJson.getString("data");
		//intent
		String intent = "";
		JSONArray sayArray = JSONArray.parseArray(result);
		String say = "";
		for (int i = 0; i <sayArray.size() ; i++) {
			JSONObject sayJson = sayArray.getJSONObject(i);
			intent = sayJson.getString("intent");
			//answer
			JSONObject actionlistList = JSONObject.parseObject(intent);
			String actionList = actionlistList.getString("answer");
			//text
			JSONObject answerList = JSONObject.parseObject(actionList);
			say = answerList.getString("text");
			System.out.println(say);
		}

		return say;
	}

	public static void main(String[] args) {
		//语音识别后进行人机交互，命中意图
		String aiuiResponse = "";
		try {
			aiuiResponse = AIUIService.aiuiWebApi("查看北京今天的天气");
		} catch (Exception e) {
			e.printStackTrace();
		}
		//解析讯飞AIUI相应的动作列表
		//data
		JSONObject resultJson = JSONObject.parseObject(aiuiResponse);
		String result = resultJson.getString("data");
		//intent
		String intent = "";
		JSONArray sayArray = JSONArray.parseArray(result);
		for (int i = 0; i <sayArray.size() ; i++) {
			JSONObject sayJson = sayArray.getJSONObject(i);
			intent = sayJson.getString("intent");
			//answer
			JSONObject actionlistList = JSONObject.parseObject(intent);
			String actionList = actionlistList.getString("answer");
			//text
			JSONObject answerList = JSONObject.parseObject(actionList);
			String say = answerList.getString("text");
			System.out.println(say);
		}



	}

}
