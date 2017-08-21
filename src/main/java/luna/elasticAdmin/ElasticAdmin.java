package luna.elasticAdmin;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Map;

import org.apache.log4j.BasicConfigurator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

/**
 * 
* Copyright: Copyright (c) 2017 XueErSi
* 
* @ClassName: ElasticAdmin.java
* @Description: Elastic admin client to create index mapping.
*
* @version: v1.0.0
* @author: GaoXing Chen
* @date: 2017年8月21日 下午7:24:19 
*
* Modification History:
* Date         Author          Version			Description
*---------------------------------------------------------*
* 2017年8月21日     GaoXing Chen      v1.0.0				添加注释
 */
public class ElasticAdmin {
	private static TransportClient client = null;
	private ArrayList<String> hosts;
	private String clusterName;
	private Logger log;
	private final Map outputConfig;
	
	/**
	 * 
	* @Function: ElasticAdmin
	* @Description: Constructor
	*
	* @param: config: elaticsearch config
	* @version: v1.0.0
	* @author: GaoXing Chen
	* @date: 2017年8月21日 下午7:26:15
	 */
	public ElasticAdmin(Map config){
		outputConfig=config;
		prepare();
	}
	
	/**
	 * 
	* @Function: prepare()
	* @Description: Initialize Elasticsearch properties and get client
	*
	* @param: void
	* @return: void
	* @throws: void
	*
	* @version: v1.0.0
	* @author: GaoXing Chen
	* @date: 2017年8月21日 下午7:26:53 
	*
	* Modification History:
	* Date         Author          Version			Description
	*---------------------------------------------------------*
	* 2017年8月21日     GaoXing Chen      v1.0.0				添加注释
	 */
	public void prepare(){
		BasicConfigurator.configure();
		log=LogManager.getLogger("esAdmin");
		log=LogManager.getLogger((String)outputConfig.get("logger"));
		clusterName=(String)outputConfig.get("cluster.name");
		hosts=(ArrayList<String>) outputConfig.get("hosts");
		
		if (client == null) {
			Settings settings = Settings.builder()
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
	* @Function: setIndex
	* @Description: put mapping and update mapping
	*
	* @param: skip
	* @return: void
	* @throws: void
	*
	* @version: v1.0.0
	* @author: GaoXing Chen
	* @date: 2017年8月21日 下午7:49:22 
	*
	* Modification History:
	* Date         Author          Version			Description
	*---------------------------------------------------------*
	* 2017年8月21日     GaoXing Chen      v1.0.0				添加注释
	 */
	public void setIndex(String index,String type,Map source){
		if(!client.admin().indices().prepareExists(index).get().isExists()){
			client.admin().indices().prepareCreate(index).get();
		}
		PutMappingResponse response=client.admin().indices().preparePutMapping(index).setType(type).setSource(source).get();
		log.info(response);
	}
	
	//delete mapping
	/**
	 * 
	* @Function: deleteIndex
	* @Description: delete mapping
	*
	* @param: skip
	* @return: void
	* @throws: void
	*
	* @version: v1.0.0
	* @author: GaoXing Chen
	* @date: 2017年8月21日 下午7:50:52 
	*
	* Modification History:
	* Date         Author          Version			Description
	*---------------------------------------------------------*
	* 2017年8月21日     GaoXing Chen      v1.0.0				添加注释
	 */
	public void deleteIndex(String index){
		if(!client.admin().indices().prepareExists(index).get().isExists()){
			DeleteIndexResponse response=  client.admin().indices().prepareDelete(index).get();
			log.info(response);
		}
	}
}
