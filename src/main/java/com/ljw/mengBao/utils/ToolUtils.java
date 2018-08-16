package com.ljw.mengBao.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ToolUtils {

    private static Logger logger = LoggerFactory.getLogger(ToolUtils.class);

    /**
     * 解析HttpServletRequest请求参数
     * @param request
     * @return
     */
    public static String JsonReq(HttpServletRequest request) {
        String param= null;
        try {
            BufferedReader streamReader = new BufferedReader( new InputStreamReader(request.getInputStream(), "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();
            String inputStr;
            while ((inputStr = streamReader.readLine()) != null)
                responseStrBuilder.append(inputStr);
            param= responseStrBuilder.toString();
            System.out.println("获取请求的参数为："+param);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("解析请求参数失败："+e.getMessage());
        }
        return param;
    }


    /**
     * 判断日期是否合法
     * 假设传入的日期格式是yyyy-MM-dd, 也可以传入yyyy-MM-dd HH:mm:ss，如2018-1-1或者2018-01-01格式
     * @param strDate
     * @return
     */
    public static boolean isValidDate(String strDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            // 设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期，比如2018-02-29会被接受，并转换成2018-03-01

            format.setLenient(false);
            Date date = format.parse(strDate);

            //判断传入的yyyy年-MM月-dd日 字符串是否为数字
            String[] sArray = strDate.split("-");
            for (String s : sArray) {
                boolean isNum = s.matches("[0-9]+");
                //+表示1个或多个（如"3"或"225"），*表示0个或多个（[0-9]*）（如""或"1"或"22"），?表示0个或1个([0-9]?)(如""或"7")
                if (!isNum) {
                    return false;
                }
            }
        } catch (Exception e) {
            // e.printStackTrace();
            // 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
            return false;
        }

        return true;
    }


    /**
     * 利用Introspector和PropertyDescriptor 将javaBean 转换成Map
     * @param obj
     * @return
     */

    public static Map<String, Object> transBean2Map(Object obj) {
        if(obj == null){
            return null;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();
                // 过滤class属性
                if (!key.equals("class")) {
                    // 得到property对应的getter方法
                    Method getter = property.getReadMethod();
                    Object value = getter.invoke(obj);
                    map.put(key, value);
                }
            }
        } catch (Exception e) {
            System.out.println("transBean2Map Error " + e);
        }

        return map;

    }



    /**
     * 获取Ip地址
     * @param request
     * @return
     */
    public static String getIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if(StringUtils.isNotBlank(ip)){
            logger.info("从x-forwarded-for获取到的："+ip);
            return ip;
        }

        ip = request.getHeader("Proxy-Client-IP");
        if(StringUtils.isNotBlank(ip)){
            logger.info("从Proxy-Client-IP获取到的："+ip);
            return ip;
        }

        ip = request.getHeader("WL-Proxy-Client-IP");
        if(StringUtils.isNotBlank(ip)){
            logger.info("WL-Proxy-Client-IP："+ip);
            return ip;
        }

        ip = request.getHeader("X-Real-IP");
        if(StringUtils.isNotBlank(ip)){
            logger.info("X-Real-IP："+ip);
            return ip;
        }

        ip = request.getRemoteAddr();
        if(StringUtils.isNotBlank(ip)){
            logger.info("从getRemoteAddr获取到的："+ip);
            return ip;
        }

        return null;

    }

    /**
     * md5加密
     * @author jlchen4
     * @date 2017年9月16日 下午3:44:46
     * @param source	加密字符串
     * @return
     */
    public static String md5Encode(String source) {
        String result = null;
        try {
            result = source;
            // 获得MD5摘要对象
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            // 使用指定的字节数组更新摘要信息
            messageDigest.update(result.getBytes("utf-8"));
            // messageDigest.digest()获得16位长度
            result = byteArrayToHexString(messageDigest.digest());
        } catch (Exception e) {
            logger.error("Md5 Exception!", e);
        }
        return result;
    }


    /**
     * Base64加密
     * @author jlchen4
     * @date 2017年9月16日 下午3:45:30
     * @param str	加密字符串
     * @return
     */
    public static String getBase64(String str) {
        if (str == null || "".equals(str)) {
            return "";
        }
        try {
            byte[] encodeBase64 = Base64.encodeBase64(str.getBytes("UTF-8"));
            str = new String(encodeBase64);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }


    private static String byteArrayToHexString(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte tem : bytes) {
            stringBuilder.append(byteToHexString(tem));
        }
        return stringBuilder.toString();
    }

    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0) {
            n = 256 + n;
        }
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }


    private final static String[] hexDigits = { "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };


}

