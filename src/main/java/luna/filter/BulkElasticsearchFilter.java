package luna.filter;

import java.text.ParseException;
import java.util.Map;
import luna.output.BulkElasticsearchOutput;
import luna.util.TimeUtil;

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
	private final BulkElasticsearchOutput bulkEsHandler;

	public BulkElasticsearchFilter(Map config){
	    super();
		bulkEsHandler=new BulkElasticsearchOutput(config);
	}

	public void prepare(){
        bulkEsHandler.getBulkRequest();
	}

	public void emit(){
	    long beginTime = System.currentTimeMillis();
        bulkEsHandler.emitBulk();
        long endTime = System.currentTimeMillis();
		logTime.info("bulkDelay " + (endTime - beginTime));
	}

	public void filter(Map<String, Object>data) throws Exception{
        filter(data,bulkEsHandler);
		//logTime.info(""+table+" "+(System.currentTimeMillis()/1000-24-ts));
		long getDataTimeMillis = System.currentTimeMillis();
        String modify_time =(String)payload.get("modify_time");
        long modifyTimeMillis=0;
        modifyTimeMillis = TimeUtil.stringToLong(modify_time, "yy-MM-dd HH:mm:ss.SSS");
		long diffMillis = getDataTimeMillis - modifyTimeMillis-28800000;
		logTime.info(""+table+" "+diffMillis);
	}
}
