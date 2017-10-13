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
	private final ElasticsearchOutput esHandler;

	public ElasticsearchFilter(Map config){
	        super();
		esHandler=new ElasticsearchOutput(config);
	}

	public void filter(Map<String, Object>data) throws Exception{
        filter(data,esHandler);
        long currentTimeMillis = System.currentTimeMillis();
        String modify_time =(String)payload.get("modify_time");
        long modifyTimeMillis = TimeUtil.stringToLong(modify_time,"yy-MM-dd HH:mm:ss.SSS");
        long diffMillis = currentTimeMillis - modifyTimeMillis-28800000;
        logTime.info(""+table+" "+diffMillis);

		//logTime.info(""+table+" "+(System.currentTimeMillis()/1000-24-ts));
	}
	
	
}
