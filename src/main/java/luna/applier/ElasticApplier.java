package luna.applier;

import luna.common.AbstractLifeCycle;
import luna.common.context.ElasticContext;
import luna.common.model.OperateType;
import luna.common.model.Record;
import luna.exception.LunaException;
import luna.util.DingDingMsgUtil;
import org.apache.log4j.BasicConfigurator;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.util.List;

public class ElasticApplier extends AbstractLifeCycle implements Applier{
    private static TransportClient client = null;
    private ElasticContext elasticContext;

    public ElasticApplier(ElasticContext elasticContext){
        this.elasticContext=elasticContext;
    }

    public void applyBatch(final List<Record> records){
        BulkRequestBuilder bulkRequest=getBulkRequest();
        for(Record record:records){
//            System.out.println("******************"+record.getOperateType());
            switch (record.getOperateType()){
                case I:
                    prepareIndex(record,bulkRequest);
                case U:
                    prepareUpdate(record,bulkRequest);
                case D:
                    prepareDelete(record,bulkRequest);
                default:
                    throw new LunaException("Unknown opType " + record.getOperateType());
            }
//            if(record.getOperateType()==OperateType.I){
//                prepareIndex(record,bulkRequest);
//            }else if(record.getOperateType()==OperateType.U){
//                prepareUpdate(record,bulkRequest);
//            }else if(record.getOperateType()==OperateType.D){
//                prepareDelete(record,bulkRequest);
//            }else {
//                throw new LunaException("**********"+record.getOperateType());
//            }
        }

        emitBulk(bulkRequest);
    }

    public void applyOneByOne(Record record){
        switch (record.getOperateType()){
            case I:
                index(record);
            case U:
                update(record);
            case D:
                delete(record);
            case UNKNOWN:
                throw new LunaException("Unknown opType " + record.getOperateType());
        }
    }

    public void start(){
        super.start();
        BasicConfigurator.configure();
        if (client == null) {
            Settings settings = Settings.builder()
                    .put("client.transport.sniff", elasticContext.isSniff())
                    .put("transport.tcp.compress", elasticContext.isCompress())
                    .put("cluster.name", elasticContext.getClusterName()).build();
            try{
                client =new PreBuiltTransportClient(settings);
                for (String host : elasticContext.getHosts()) {
                    client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), 9300));
                }
                logger.info("Get client!");

            }catch(Exception e){
                errorLog.error(e.getMessage());
            }
        }
    }

    public void stop(){
        super.stop();
        if(client!=null){
            client.close();
            logger.info("Client is closed !");
        }
    }

    private void index(Record record){
        IndexResponse response = client.prepareIndex(record.getIndex(),record.getType(),record.getId()).setSource(record.getData()).get();
        judgeResponse(response);
    }

    private void delete(Record record){
        DeleteResponse response = client.prepareDelete(record.getIndex(),record.getType(),record.getId()).get();
        logger.info(response);
        if(response.getShardInfo().getFailed()>0){
            DingDingMsgUtil.sendMsg(response.getShardInfo().toString());
        }
    }

    private void update(Record record){
        UpdateResponse response = client.prepareUpdate(record.getIndex(),record.getType(),record.getId()).setDoc(record.getData()).setUpsert(record.getData()).get();
        //UpdateResponse response = client.prepareUpdate(index, type, id).setDoc(data).get();
        judgeResponse(response);

    }

    private BulkRequestBuilder getBulkRequest(){
        return client.prepareBulk();
    }

    private void emitBulk(BulkRequestBuilder bulkRequest){
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
            errorLog.error(bulkResponse.buildFailureMessage());
        }
    }

    private void prepareIndex(Record record,BulkRequestBuilder bulkRequest){
        bulkRequest.add(client.prepareIndex(record.getIndex(),record.getType(),record.getId()).setSource(record.getData()));
    }

    private void prepareDelete(Record record,BulkRequestBuilder bulkRequest){
        bulkRequest.add(client.prepareDelete(record.getIndex(), record.getType(), record.getId()));
    }

    private void prepareUpdate(Record record,BulkRequestBuilder bulkRequest){
        bulkRequest.add(client.prepareUpdate(record.getIndex(),record.getType(),record.getId()).setDoc(record.getData()).setUpsert(record.getData()));
    }

    private void judgeResponse(DocWriteResponse response) throws LunaException{
        logger.info(response);
        if(response.getShardInfo().getFailed()>0){
            DingDingMsgUtil.sendMsg(response.getShardInfo().toString());
            throw new LunaException(response.getShardInfo().toString());
        }
    }
}
