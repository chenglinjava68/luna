package luna.output;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Map;

import org.apache.log4j.BasicConfigurator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

/**
 * 
* Copyright: Copyright (c) 2017 XueErSi
* 
* @ClassName: ElasticsearchOutput.java
* @Description: Elasticsearch client(one by one)
*
* @version: v1.0.0
* @author: GaoXing Chen
* @date: 2017年8月21日 下午8:12:12 
*
* Modification History:
* Date         Author          Version			Description
*---------------------------------------------------------*
* 2017年8月21日     GaoXing Chen      v1.0.0				添加注释
 */
public class ElasticsearchOutput extends BaseOutput{
	private final static boolean DEFAULTSNIFF = true;
    private final static boolean DEFAULTCOMPRESS = false;
    
	private static TransportClient client = null;
	private ArrayList<String> hosts;
	private boolean sniff;										//	Find the whole cluster by some host if sniff = true  
	private boolean compress;									//	If compress the message
	private String clusterName;									//	Default Elasticsearch 
	private Logger log;
	private Logger logTime;
	private final Map outputConfig;

    public ElasticsearchOutput(Map config){
	outputConfig=config;
    	hosts=new ArrayList<String>();
    	prepare();
    }

	private void prepare(){
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

	public void shutdown(){
		if(client!=null){
			client.close();
			log.info("Client is closed !");
		}
	}

	public void index(String index,String type,String id,final Map data){
		IndexResponse response=client.prepareIndex(index,type,id).setSource(data).get();
		log.info(response);
	}

	public void delete(String index,String type,String id){
		DeleteResponse response = client.prepareDelete(index, type, id).get();
		log.info(response);
	}

	public void update(String index,String type,String id,final Map data){
		UpdateResponse response=client.prepareUpdate(index,type,id).setDoc(data).get();
		log.info(response);
	}

	public void search(String index,String type,String id){
		QueryBuilder queryBuilder = QueryBuilders  
				.disMaxQuery()  
				.add(QueryBuilders.termQuery("id", id));
		SearchResponse response = client.prepareSearch(index).setTypes(type)
				.setQuery(queryBuilder).get();
		log.info(response);
	}
	
	/**********************************parent child************************************/	

	public void indexAndAddParent(String index,String type,String id,String parentId,final Map data){
		IndexResponse response=client.prepareIndex(index, type,id).setParent(parentId).setSource(data).get();
		log.info(response);
	}

	public void deleteWithParent(String index,String type,String id,String pid){
		DeleteResponse response = client.prepareDelete(index, type, id).setParent(pid).get();
		log.info(response);
	}
    
}
