package luna.filter;

import luna.util.StringUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 *
 * Copyright: Copyright (c) 2017 XueErSi
 *
 * @version v1.0.0
 * @author GaoXing Chen
 *
 * Modification History:
 * Date         Author          Version			Description
 *---------------------------------------------------------*
 * 2017年8月21日     GaoXing Chen      v1.0.0		   添加注释
 */
public class BaseFilter {
    protected Logger logTime;

    protected String database;
    protected String table;
    protected String id;
    protected String type;
    protected Long ts;
    protected Map payload;

    protected BaseFilter(){
        logTime= LogManager.getLogger("time");
        payload = new HashMap<String,Object>();
    }

    public void filter(Map<String,Object>data) throws Exception{
        getCleanPayload(data);
        type = (String) data.get("type");
        ts = (Long)data.get("ts");
        database = (String)data.get("database");
                //+"_test";
        table = (String)data.get("table");
        id =  Objects.toString(payload.get("id"),"");
    }

    private void getCleanPayload(Map<String,Object>data){
        Map <String, Object> sourcePayload = (Map<String,Object>)data.get("data");
        payload.clear();
        sourcePayload.forEach((key,value)->{
            if(value instanceof String){
                payload.put(key, StringUtil.stripEscape(StringUtil.stripControl((String)value)));
            }else{
                payload.put(key,value);
            }
        });
    }

}
