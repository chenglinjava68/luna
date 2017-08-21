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
	
	/**
	 * 
	* @Function: BulkElasticsearchFilter
	* @Description: Constructor
	*
	* @param: config Elasticsearch config
	* @version: v1.0.0
	* @author: GaoXing Chen
	* @date: 2017年8月21日 下午7:54:06
	 */
	public BulkElasticsearchFilter(Map config){
		eshandler=new BulkElasticsearchOutput(config);
		logTime=LogManager.getLogger("time");
	}
	
	/**
	 * 
	* @Function: prepare
	* @Description: create bulk request.
	*
	* @param: void
	* @return: void
	* @throws: void
	*
	* @version: v1.0.0
	* @author: GaoXing Chen
	* @date: 2017年8月21日 下午7:54:35 
	*
	* Modification History:
	* Date         Author          Version			Description
	*---------------------------------------------------------*
	* 2017年8月21日     GaoXing Chen      v1.0.0				添加注释
	 */
	public void prepare(){
		eshandler.getBulkRequest();
	}
	
	/**
	 * 
	* @Function: emit
	* @Description: emit bulk request
	*
	* @param: void
	* @return: void
	* @throws: void
	*
	* @version: v1.0.0
	* @author: GaoXing Chen
	* @date: 2017年8月21日 下午7:56:27 
	*
	* Modification History:
	* Date         Author          Version			Description
	*---------------------------------------------------------*
	* 2017年8月21日     GaoXing Chen      v1.0.0				添加注释
	 */
	public void emit(){
		eshandler.emitBulk();
		logTime.info(System.currentTimeMillis());
	}
	
	/**
	 * 
	* @Function: filter
	* @Description: bulk request add message
	*
	* @param: data: message
	* @return: void
	* @throws: void
	*
	* @version: v1.0.0
	* @author: GaoXing Chen
	* @date: 2017年8月21日 下午7:55:28 
	*
	* Modification History:
	* Date         Author          Version			Description
	*---------------------------------------------------------*
	* 2017年8月21日     GaoXing Chen      v1.0.0				添加注释
	 */
	public void filter(Map<String, Object>data){
		logTime.info((Long) data.get("ts"));
		if(((String) data.get("type")).contentEquals("insert")){
			eshandler.index(((String) data.get("table")), ((String) data.get("table")),Objects.toString(((Map)data.get("data")).get("id"),""),(Map)data.get("data"));
		}else if(((String) data.get("type")).contentEquals("delete")){
			eshandler.delete(((String) data.get("table")), ((String) data.get("table")),Objects.toString(((Map)data.get("data")).get("id"),""));
		}else if(((String) data.get("type")).contentEquals("update")){
			eshandler.update(((String) data.get("table")), ((String) data.get("table")),Objects.toString(((Map)data.get("data")).get("id"),""),(Map)data.get("data"));
		}else {
		}
	}
}
