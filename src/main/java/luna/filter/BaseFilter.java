package luna.filter;

import luna.util.StringUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class BaseFilter {
    protected Logger logTime;

    protected String database;
    protected String table;
    protected String id;
    protected String type;
    protected Long ts;
    protected Map payload;

    public BaseFilter(){
        logTime= LogManager.getLogger("time");
        payload = new HashMap<String,Object>();
    }

    public void filter(Map<String,Object>data){
        getCleanPayload(data);
        type = (String) data.get("type");
        ts = (Long)data.get("ts");
        database = (String)data.get("database");
        table = (String)data.get(table);
        id = (String)payload.get("id");
    }

    public void getCleanPayload(Map<String,Object>data){
        Map <String, Object> sourcePayload = (Map<String,Object>)data.get("data");
        payload.clear();
        data.forEach((key,value)->{
            if(value instanceof String){
                payload.put(key, StringUtil.stripEscape(StringUtil.stripControl((String)value)));
            }else{
                payload.put(key,value);
            }
        });
    }

}
