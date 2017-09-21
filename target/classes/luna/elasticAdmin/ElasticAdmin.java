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
import org.elasticsearch.common.settings.Settings.Builder;
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

	public ElasticAdmin(Map config){
		outputConfig=config;
		prepare();
	}

	private void prepare(){
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
    
        public void shutdown(){
            if(client!=null){
                client.close();
                log.info("Client is closed!");
            }
        }
        
        public void setIndex(String index,String type,Map source){
            if(!client.admin().indices().prepareExists(index).get().isExists()){
                client.admin().indices().prepareCreate(index).get();
            }
            PutMappingResponse response=client.admin().indices().preparePutMapping(index)
                                              .setType(type)
                                              .setSource(source)
                                              .get();
            log.info(response);
        }

        
	public void setIndex(String index,String type,Map source,int shardNumber,int replicaNumber){
        Builder builder=Settings.builder()
            .put("index.number_of_shards",shardNumber)
            .put("index.number_of_replicas",replicaNumber);
		if(!client.admin().indices().prepareExists(index).get().isExists()){
			client.admin().indices().prepareCreate(index)
            .setSettings(builder)
            .get();
		}
		PutMappingResponse response=client.admin().indices().preparePutMapping(index)
            .setType(type)
            .setSource(source)
            .get();
		log.info(response);
	}

	public void deleteIndex(String index){
		if(!client.admin().indices().prepareExists(index).get().isExists()){
			DeleteIndexResponse response=  client.admin().indices().prepareDelete(index).get();
			log.info(response);
		}
	}
}
