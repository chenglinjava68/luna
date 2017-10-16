package luna.elasticAdmin;

import java.util.Map;

import luna.output.BaseOutput;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.Settings.Builder;

/**
 * 
* Copyright: Copyright (c) 2017 XueErSi
*
* @version v1.0.0
* @author GaoXing Chen
*
* Modification History:
* Date         Author          Version			Description
*---------------------------------------------------------*
* 2017年8月21日     GaoXing Chen      v1.0.0				添加注释
 */
public class ElasticAdmin extends BaseOutput{

	public ElasticAdmin(Map config){
	    super(config);
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
