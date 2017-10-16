package luna.output;

import java.util.Map;

import luna.util.DingDingMsgUtil;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;

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
		    if(bulkResponse.getItems().length>10){
                DingDingMsgUtil.sendMsg("BULK ERROR(too much message), for detail, please view the log");
            }else{
                bulkResponse.forEach(
                        bulkItemResponse -> {
                            DingDingMsgUtil.sendMsg(bulkItemResponse.getFailureMessage());
                        }
                );
            }
			log.error(bulkResponse.buildFailureMessage());
		}
	}

	public void prepareIndex(String index,String type,String id,final Map data){
		bulkRequest.add(client.prepareIndex(index,type,id).setSource(data));
	}

	public void prepareDelete(String index,String type,String id){
		bulkRequest.add(client.prepareDelete(index, type, id));
	}

	public void prepareUpdate(String index,String type,String id,final Map data){
		bulkRequest.add(client.prepareUpdate(index,type,id).setDoc(data));
	}
	
	
}
