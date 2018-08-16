package com.ljw.mengBao.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

/*
 * unit对话服务
 */
public class UnitService {
    /**
     * 重要提示代码中所需工具类
     * FileUtil,Base64Util,HttpUtil,GsonUtils请从
     * https://ai.baidu.com/file/658A35ABAB2D404FBF903F64D47C1F72
     * https://ai.baidu.com/file/C8D81F3301E24D2892968F09AE1AD6E2
     * https://ai.baidu.com/file/544D677F5D4E4F17B4122FBD60DB82B3
     * https://ai.baidu.com/file/470B3ACCA3FE43788B5A963BF0B625F3
     * 下载
     */
    public static String utterance(String query) {
        // 请求URL
        String talkUrl = "https://aip.baidubce.com/rpc/2.0/unit/bot/chat";
        //请求的参数用map封装
        Map<String , Object> map = new HashMap<>();
        Map<String , Object> mapRequest = new HashMap<>();
        Map<String , Object> mapQueryInfo = new HashMap<>();
        Map<String , Object> mapClientSession = new HashMap<>();
        List<String> asrCandidatesList = new ArrayList<>();
        List<String> candidateOptionsList = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        map.put("bot_session","");
        map.put("log_id",UUID.randomUUID().toString().replaceAll("-", ""));
        map.put("request",mapRequest);
        /*
         *  BOT唯一标识，在『我的BOT』的BOT列表中第一列数字即为bot_id
         */
        map.put("bot_id",5664);
        map.put("version","2.0");
        /**
         *  系统自动发现不置信意图/词槽，
         *  并据此主动发起澄清确认的敏感程度。
         *  取值范围：0(关闭)、1(中敏感度)、2(高敏感度)。
         *  取值越高BOT主动发起澄清的频率就越高，建议值为1
         */
        mapRequest.put("bernard_level",1);
        mapRequest.put("query",query);
        mapRequest.put("query_info",mapQueryInfo);
        mapRequest.put("updates","");
        mapRequest.put("user_id","a0e3a7f711274b5c8cca281c9d7667e4");
        mapQueryInfo.put("asr_candidates",asrCandidatesList);
        mapQueryInfo.put("source","KEYBOARD");
        mapQueryInfo.put("type","TEXT");
        String clientSession = "";
        mapClientSession.put("client_results","");
        mapClientSession.put("candidate_options",candidateOptionsList);
        try {
            clientSession = objectMapper.writeValueAsString(mapClientSession).replace("\"", "\\\\\\\"");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        mapRequest.put("client_session",clientSession);
//        System.out.println(clientSession);

        try {
            // 请求参数

            String params = objectMapper.writeValueAsString(map);
//            System.out.println(params);
            String accessToken = AuthService.getAuth();
            String result = HttpUtil.post(talkUrl, accessToken, "application/json", params);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static void main(String[] args) {
        utterance("播放歌曲");
    }
}