package luna.filter;

import java.util.Map;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import luna.output.ElasticsearchOutput;
import luna.util.TimeUtil;

/**
 * 
* Copyright: Copyright (c) 2017 XueErSi
* 
* @ClassName: ElasticsearchFilter.java
* @Description: Emit messages to Elasticsearch one by one.
*
* @version: v1.0.0
* @author: GaoXing Chen
* @date: 2017年8月21日 下午7:57:38 
*
* Modification History:
* Date         Author          Version			Description
*---------------------------------------------------------*
* 2017年8月21日     GaoXing Chen      v1.0.0				添加注释
 */
public class ElasticsearchFilter extends BaseFilter {
	private final ElasticsearchOutput eshandler;
	private Logger logTime;
	
	/**
	 * 
	* @Function: ElasticsearchFilter
	* @Description: Constructor
	*
	* @param: config: Elasticsearch config
	* @version: v1.0.0
	* @author: GaoXing Chen
	* @date: 2017年8月21日 下午7:58:56
	 */
	public ElasticsearchFilter(Map config){
		eshandler=new ElasticsearchOutput(config);
		logTime=LogManager.getLogger("time");
	}
	
	/**
	 * 
	* @Function: filter
	* @Description: emit message
	*
	* @param: data: message
	* @return: void
	* @throws: void
	*
	* @version: v1.0.0
	* @author: GaoXing Chen
	* @date: 2017年8月21日 下午7:59:23 
	*
	* Modification History:
	* Date         Author          Version			Description
	*---------------------------------------------------------*
	* 2017年8月21日     GaoXing Chen      v1.0.0				添加注释
	 */
	public void filter(Map<String, Object>data){
		if(((String) data.get("type")).contentEquals("insert")){
			eshandler.index(((String) data.get("table")), ((String) data.get("database")),Objects.toString(((Map)data.get("data")).get("id"),""),(Map)data.get("data"));
		}else if(((String) data.get("type")).contentEquals("delete")){
			eshandler.delete(((String) data.get("table")), ((String) data.get("database")),Objects.toString(((Map)data.get("data")).get("id"),""));
		}else if(((String) data.get("type")).contentEquals("update")){
			eshandler.update(((String) data.get("table")), ((String) data.get("database")),Objects.toString(((Map)data.get("data")).get("id"),""), (Map)data.get("data"));
		}else {
		}
		logTime.info(""+data.get("table")+" "+((Map)data.get("data")).get("id")+" "+(System.currentTimeMillis()/1000-(Long)data.get("ts")));
	}
	
	
}
