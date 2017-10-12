package luna.util;

import luna.config.ConfigHelp;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DingDingMsgUtil {
    private static Map<String,String> header = new HashMap<>();
    private static String charset = "";
    private static HttpClientUtil httpClientUtil = new HttpClientUtil();
    private static String url="";
    private static boolean isAtAll = false;
    private static ArrayList<String> phoneNumbers=new ArrayList<>();
    private static Logger log;

    static{
        log= LogManager.getLogger("ding");
        Map DDconfigs = null;
        try {
            DDconfigs=ConfigHelp.parse("conf/DingDing.yml");
        } catch (Exception e) {
            e.printStackTrace();
        }
        header.put("Content-Type","application/json");
        charset="utf-8";
        url = (String)DDconfigs.get("url");
        isAtAll = (boolean)DDconfigs.get("isAtAll");
        phoneNumbers = (ArrayList<String>)DDconfigs.get("phonenumbers");
    }

    public static void sendMsg(String msg){
        log.info("MESSAGE: "+msg);
        HttpEntity entity = new StringEntity(jsonMsg(msg),charset);
        try {
            String responseString = httpClientUtil.post(url, header, null, entity);
            log.info(responseString);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private static String jsonMsg(String msg){

        JsonArrayBuilder mobiles = Json.createArrayBuilder();
        phoneNumbers.forEach(number->{
            mobiles.add(number);
        });

        String jsonWrap = Json.createObjectBuilder()
                .add("msgtype", "text")
                .add("text", Json.createObjectBuilder().add("content",msg))
                .add("at",Json.createObjectBuilder().add("atMobiles",mobiles).add("isAtAll",isAtAll))
                .build()
                .toString();

        return jsonWrap;
    }
}
