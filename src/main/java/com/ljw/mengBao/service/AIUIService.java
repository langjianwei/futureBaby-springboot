package com.ljw.mengBao.service;

import com.ljw.mengBao.utils.ToolUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;


public class AIUIService {

    private static final Logger logger = LoggerFactory.getLogger(AIUIService.class);

    public static String aiuiWebApi(String text) throws Exception{
        //讯飞开放平台注册申请应用的应用ID(APPID)
        String xAppid = "5b3c7b1a";
        logger.info("X-Appid:" + xAppid);
        //获取当前UTC时间戳
        long time = System.currentTimeMillis() / 1000;
        String curTime = String.valueOf(time);
        logger.info("X-CurTime:" + curTime);
        String xParam = "{\"auth_id\":\"d2ddad68a400bf65efea6c4b35447997\",\"data_type\":\"text\",\"scene\":\"main\"}";
        String xParamBase64 = ToolUtils.getBase64(xParam);
        logger.info("X-Param:" + xParamBase64);
        //参数
        String fileData = text;
        //ApiKey创建应用时自动生成
        String apiKey = "4ebd2a99f72a4988b9ac5a8736111fda";
        String token = apiKey + curTime + xParamBase64;
        String xCheckSum = ToolUtils.md5Encode(token);
        logger.info("X-CheckSum:" + xCheckSum);
        String resBody = "";
        PrintWriter out = null;
        BufferedReader in = null;
        try {
            String url = "http://openapi.xfyun.cn/v2/aiui";
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            HttpURLConnection conn = (HttpURLConnection) realUrl
                    .openConnection();
            conn.setReadTimeout(2000);
            conn.setConnectTimeout(1000);
            conn.setRequestMethod("POST");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("X-Appid", xAppid);
            conn.setRequestProperty("X-CurTime", curTime);
            conn.setRequestProperty("X-Param", xParamBase64);
            conn.setRequestProperty("X-CheckSum", xCheckSum);
            conn.setRequestProperty("Connection", "keep-alive");
            conn.setRequestProperty("Content-type",
                    "application/x-www-form-urlencoded; charset=utf-8");
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(fileData);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            // 将返回的输入流转换成字符串
            InputStream inputStream = conn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(
                    inputStream, "utf-8");
            in = new BufferedReader(inputStreamReader);
            String line;
            while ((line = in.readLine()) != null) {
                resBody += line;
            }
            logger.info("result body :" + resBody);
            return  resBody;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return "调用讯飞接口失败";
    }

    public static void main(String[] args) throws Exception {
        aiuiWebApi("讲个笑话");
    }


}
