package com.ljw.mengBao.controller;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ljw.mengBao.service.AIUIService;
import com.ljw.mengBao.utils.EmailUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

@RestController
public class SpeechRecognitionController {
	
	@Autowired
	private EmailUtils emailUtils;
	
	@Autowired
    private TemplateEngine templateEngine;

	private static final Logger logger = LoggerFactory.getLogger(SpeechRecognitionController.class);
	
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
			logger.error("人机交互，命中意图失败："+e.getMessage());

		}
		Map<String, Object> map = new HashMap<>();
		List<Map<String, Object>> list = new ArrayList<>();
		//解析讯飞AIUI相应的动作列表
		//data
		JSONObject resultJson = JSONObject.parseObject(aiuiResponse);
		String result = resultJson.getString("data");
		//intent
		String intent = "";
		JSONArray sayArray = JSONArray.parseArray(result);
		String say = "";
		String name = "";
		String normValue = "";
		for (int i = 0; i <sayArray.size() ; i++) {
			JSONObject sayJson = sayArray.getJSONObject(i);
			intent = sayJson.getString("intent");
			//answer
			JSONObject actionlistList = JSONObject.parseObject(intent);
			String actionList = actionlistList.getString("answer");
			if (actionList==null){
				map.put("say", "用了洪荒之力都没听明白，您能在说一次吗？");
				return JSONUtils.toJSONString(map);
			}
			//text
			JSONObject answerList = JSONObject.parseObject(actionList);
			say = answerList.getString("text");
			map.put("say", say);
			if ("好的，邮件已为您写好，请检查邮件信息是否正确并确认是否发送。".equals(say)) {
				//semantic得到意图列表
				String semanticList = actionlistList.getString("semantic");
				JSONArray semanticArray = JSONArray.parseArray(semanticList);
				for(int j = 0; j <semanticArray.size() ; j++) {
					JSONObject slotsJson = semanticArray.getJSONObject(j);
					String slots = slotsJson.getString("slots");
					JSONArray slotsArray = JSONArray.parseArray(slots);
					for(int k = 0; k <slotsArray.size() ; k++) {
						HashMap<String, Object> semanticMap = new HashMap<>();
						JSONObject normValueJson = slotsArray.getJSONObject(k);
						name = normValueJson.getString("name");
						normValue = normValueJson.getString("normValue");
						semanticMap.put("name", name);
						semanticMap.put("normValue", normValue);
						list.add(semanticMap);
					}
				}
				logger.info(say+name+normValue);
				map.put("semantic", list);
			}
		}

		return JSONUtils.toJSONString(map);
	}
	
	
	@RequestMapping("/sendEmail")
	public String sendEmail(HttpServletRequest request,
							 @RequestParam(value = "youEmail", required = false) String youEmail,
							 @RequestParam(value = "sendEmail", required = false) String sendEmail,
							 @RequestParam(value = "emailTitle", required = false) String emailTitle,
							 @RequestParam(value = "emailContext", required = false) String emailContext,
							 @RequestParam(value = "remarks", required = false) String remarks) {

		youEmail="17600604700@163.com";
		sendEmail="jianwei.lang@eucita.cn";
		//创建邮件正文
	    Context context = new Context();
	    context.setVariable("emailContext", emailContext);
	    String emailContent = templateEngine.process("HTMLEmailTemplate", context);

	    return emailUtils.sendHtmlMail(youEmail, sendEmail, emailTitle,emailContent);
//		return emailUtils.sendSimpleMail(youEmail, sendEmail, emailTitle,emailContext);

	}
	

	public static void main(String[] args) {
		//语音识别后进行人机交互，命中意图
		String aiuiResponse = "";
		try {
			aiuiResponse = AIUIService.aiuiWebApi("天气zenemayng");
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
			if (actionList==null){
				System.out.println("用了洪荒之力都没明白，您能在说一次吗？");
			}
			//text
			JSONObject answerList = JSONObject.parseObject(actionList);
			String say = answerList.getString("text");
			System.out.println(say);
		}



	}

}
