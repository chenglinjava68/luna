package luna.filter;

import java.util.Map;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import luna.output.BulkElasticsearchOutput;

/**
 * 
* Copyright: Copyright (c) 2017 XueErSi
* 
* @ClassName: BulkElasticsearchFilter.java
* @Description: Emit messages to Elasticsearch with bulk API.
*
* @version: v1.0.0
* @author: GaoXing Chen
* @date: 2017年8月21日 下午7:52:28 
*
* Modification History:
* Date         Author          Version			Description
*---------------------------------------------------------*
* 2017年8月21日     GaoXing Chen      v1.0.0				添加注释
 */
public class BulkElasticsearchFilter  extends BaseFilter{
	private final BulkElasticsearchOutput eshandler;
	private Logger logTime;

	public BulkElasticsearchFilter(Map config){
		eshandler=new BulkElasticsearchOutput(config);
		logTime=LogManager.getLogger("time");
	}

	public void prepare(){
		eshandler.getBulkRequest();
	}

	public void emit(){
		eshandler.emitBulk();
		//response time
		logTime.info(System.currentTimeMillis());
	}

	public void filter(Map<String, Object>data){
	    //time maxwell get data
		logTime.info((Long) data.get("ts"));
        Map<String,String> payload = getCleanPayload(data);
		if(((String) data.get("type")).contentEquals("insert")){
			eshandler.index(((String) data.get("table")), ((String) data.get("database")),Objects.toString(((Map)data.get("data")).get("id"),""),payload);
		}else if(((String) data.get("type")).contentEquals("delete")){
			eshandler.delete(((String) data.get("table")), ((String) data.get("database")),Objects.toString(((Map)data.get("data")).get("id"),""));
		}else if(((String) data.get("type")).contentEquals("update")){
			eshandler.update(((String) data.get("table")), ((String) data.get("database")),Objects.toString(((Map)data.get("data")).get("id"),""),payload);
		}
	}
}
