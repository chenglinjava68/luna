package luna.output;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Map;

import org.apache.log4j.BasicConfigurator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

/**
 * 
* Copyright: Copyright (c) 2017 XueErSi
* 
* @ClassName: BulkElasticsearchOutput.java
* @Description: Bulk Elasticsearch client
*
* @version: v1.0.0
* @author: GaoXing Chen
* @date: 2017年8月21日 下午8:01:27 
*
* Modification History:
* Date         Author          Version			Description
*---------------------------------------------------------*
* 2017年8月21日     GaoXing Chen      v1.0.0				添加注释
 */
public class BulkElasticsearchOutput extends BaseOutput{

	private BulkRequestBuilder bulkRequest;

    public BulkElasticsearchOutput(Map config){
    	super(config);
    }

	public void getBulkRequest(){
		bulkRequest=client.prepareBulk();
	}

	public void emitBulk(){
		BulkResponse bulkResponse=bulkRequest.get();
		if (bulkResponse.hasFailures()) {
			log.error(bulkResponse.buildFailureMessage());
		}
	}

	public void index(String index,String type,String id,final Map data){
		bulkRequest.add(client.prepareIndex(index,type,id).setSource(data));
	}

	public void delete(String index,String type,String id){
		bulkRequest.add(client.prepareDelete(index, type, id));
	}

	public void update(String index,String type,String id,final Map data){
		bulkRequest.add(client.prepareUpdate(index,type,id).setDoc(data));
	}
	
	
}
