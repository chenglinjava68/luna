package luna.filter;

import java.util.Map;
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

	public ElasticsearchFilter(Map config){
	        super();
		eshandler=new ElasticsearchOutput(config);
	}

	public void filter(Map<String, Object>data){
        super.filter(data);
		if(type.contentEquals("insert")){
			eshandler.index(table, database,id,payload);
		}else if(type.contentEquals("delete")){
			eshandler.delete(table, database,id);
		}else if(type.contentEquals("update")){
			eshandler.update(table,database,id,payload);
		}

        long currentTimeMillis = System.currentTimeMillis();
        String modify_time =(String)payload.get("modify_time");
        long modifyTimeMillis = TimeUtil.stringToLong(modify_time,"yy-MM-dd HH:mm:ss.SSS");
        long diffMillis = currentTimeMillis - modifyTimeMillis-28800000;
        logTime.info(""+table+" "+diffMillis);

		//logTime.info(""+table+" "+(System.currentTimeMillis()/1000-24-ts));
	}
	
	
}
