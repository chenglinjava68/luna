package luna.util;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.simple.JSONValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DingDingMsgUtil {
    private static Map<String,String> header = new HashMap<>();
    private static String charset = "";
    private static HttpClientUtil httpClientUtil = new HttpClientUtil();
    private static String url="";
    private static ArrayList<String> phoneNumber=new ArrayList<>();

    static{
        header.put("Content-Type","application/json");
        charset="utf-8";
        url = "https://oapi.dingtalk.com/robot/send?access_token=043776d6a31c02ceb8c3519c15ffe447dd5f91702c158229ab419dc8705cfbd9";
        phoneNumber.add("18321787920");
    }

    public static void sendMsg(String msg){
        HttpEntity entity = new StringEntity(jsonMsg(msg),charset);
        try {
            String responseString = httpClientUtil.post(url, header, null, entity);
            ((Map<String,String>)JSONValue.parse(responseString)).get("errcode");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static String jsonMsg(String msg){
        return "";
    }
}
