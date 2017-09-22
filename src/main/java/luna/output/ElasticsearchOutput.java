package luna.output;

import java.util.Map;

import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;


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

    public ElasticsearchOutput(Map config){
	    super(config);
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
