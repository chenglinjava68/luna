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
	private final static boolean DEFAULTSNIFF = true;
    private final static boolean DEFAULTCOMPRESS = false;
    
	private static TransportClient client = null;
	private ArrayList<String> hosts;
	private boolean sniff;								//	Find the whole cluster by some host if sniff = true  
	private boolean compress;							//	If compress the message
	private String clusterName;							//	Default Elasticsearch 
	private Logger log;
	private Logger logTime;
	private final Map outputConfig;
	private BulkRequestBuilder bulkRequest;
	
	/**
	 * 
	* @Function: BulkElasticsearchOutput
	* @Description: Constructor
	*
	* @param: config: Elasticsearch config
	* @version: v1.0.0
	* @author: GaoXing Chen
	* @date: 2017年8月21日 下午8:06:01
	 */
    public BulkElasticsearchOutput(Map config){
    	outputConfig=config;
    	hosts=new ArrayList<String>();
    	prepare();
    }
	
    /**
     * 
    * @Function: prepare
    * @Description: Initialize some properties.
    *
    * @param: void
    * @return: void
    * @throws: void
    *
    * @version: v1.0.0
    * @author: GaoXing Chen
    * @date: 2017年8月21日 下午8:06:29 
    *
    * Modification History:
    * Date         Author          Version			Description
    *---------------------------------------------------------*
    * 2017年8月21日     GaoXing Chen      v1.0.0				添加注释
     */
	public void prepare(){
		BasicConfigurator.configure();
		log=LogManager.getLogger((String)outputConfig.get("logger"));
		logTime=LogManager.getLogger("time");
		sniff=(boolean)outputConfig.get("sniff");
		compress=(boolean)outputConfig.get("compress");
		clusterName=(String)outputConfig.get("cluster.name");
		hosts=(ArrayList<String>) outputConfig.get("hosts");
		
		if (client == null) {
			Settings settings = Settings.builder()
			        .put("client.transport.sniff", sniff)
			        .put("transport.tcp.compress", compress)
	                .put("cluster.name", clusterName).build();
			try{
				client =new PreBuiltTransportClient(settings);
				for (String host : hosts) {
					client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), 9300));
				}
				log.info("Get client!");
						
			}catch(Exception e){
				log.error(e.getMessage());
			}
		}
	}
	
	/**
	 * 
	* @Function: shutdown
	* @Description: Shutdown Elasticsearch 
	*
	* @param: void
	* @return: void
	* @throws: void
	*
	* @version: v1.0.0
	* @author: GaoXing Chen
	* @date: 2017年8月21日 下午8:16:04 
	*
	* Modification History:
	* Date         Author          Version			Description
	*---------------------------------------------------------*
	* 2017年8月21日     GaoXing Chen      v1.0.0				添加注释
	 */
	public void shutdown(){
		if(client!=null){
			client.close();
			log.info("Client is closed !");
		}
	}
	
	/**
	 * 
	* @Function: getBulkRequest
	* @Description: Create bulk request(not instantly emit).
	*
	* @param: void
	* @return: void
	* @throws: void
	*
	* @version: v1.0.0
	* @author: GaoXing Chen
	* @date: 2017年8月21日 下午8:07:29 
	*
	* Modification History:
	* Date         Author          Version			Description
	*---------------------------------------------------------*
	* 2017年8月21日     GaoXing Chen      v1.0.0				添加注释
	 */
	public void getBulkRequest(){
		bulkRequest=client.prepareBulk();
	}
	
	/**
	 * 
	* @Function: emitBulk
	* @Description: Emit bulk request.
	*
	* @param: void
	* @return: void
	* @throws: void
	*
	* @version: v1.0.0
	* @author: GaoXing Chen
	* @date: 2017年8月21日 下午8:09:00 
	*
	* Modification History:
	* Date         Author          Version			Description
	*---------------------------------------------------------*
	* 2017年8月21日     GaoXing Chen      v1.0.0				添加注释
	 */
	public void emitBulk(){
		BulkResponse bulkResponse=bulkRequest.get();
		if (bulkResponse.hasFailures()) {
			log.error(bulkResponse.buildFailureMessage());
		}
	}
	
	/**
	 * 
	* @Function: index
	* @Description: Bulk request add index request
	*
	* @param: skip
	* @return: void
	* @throws: void
	*
	* @version: v1.0.0
	* @author: GaoXing Chen
	* @date: 2017年8月21日 下午8:09:32 
	*
	* Modification History:
	* Date         Author          Version			Description
	*---------------------------------------------------------*
	* 2017年8月21日     GaoXing Chen      v1.0.0				添加注释
	 */
	public void index(String index,String type,String id,final Map data){
		bulkRequest.add(client.prepareIndex(index,type,id).setSource(data));
	}
	
	/**
	 * 
	* @Function: delete
	* @Description: Bulk request add delete request
	*
	* @param: skip
	* @return: void
	* @throws: void
	*
	* @version: v1.0.0
	* @author: GaoXing Chen
	* @date: 2017年8月21日 下午8:09:38 
	*
	* Modification History:
	* Date         Author          Version			Description
	*---------------------------------------------------------*
	* 2017年8月21日     GaoXing Chen      v1.0.0				添加注释
	 */
	public void delete(String index,String type,String id){
		bulkRequest.add(client.prepareDelete(index, type, id));
	}
	
	/**
	 * 
	* @Function: update
	* @Description: Bulk request add update request
	*
	* @param: skip
	* @return: void
	* @throws: void
	*
	* @version: v1.0.0
	* @author: GaoXing Chen
	* @date: 2017年8月21日 下午8:09:43 
	*
	* Modification History:
	* Date         Author          Version			Description
	*---------------------------------------------------------*
	* 2017年8月21日     GaoXing Chen      v1.0.0				添加注释
	 */
	public void update(String index,String type,String id,final Map data){
		bulkRequest.add(client.prepareUpdate(index,type,id).setDoc(data));
	}
	
	
}
