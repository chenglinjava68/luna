package luna.filter;

import luna.util.StringUtil;

import java.util.Map;

public class BaseFilter {

    public Map<String, String> getCleanPayload(Map<String,Object>data){
        Map<String,String> payload = (Map<String, String>) data.get("data");
        payload.forEach((key,value)->{
            payload.put(key, StringUtil.stripEscape(StringUtil.stripControl(value)));
        });
        return payload;
    }

}
