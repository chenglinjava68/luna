package luna.translator;

import com.google.common.collect.Lists;
import luna.common.AbstractLifeCycle;
import luna.common.model.OperateType;
import luna.common.model.Record;
import luna.exception.LunaException;
import luna.applier.ElasticApplier;
import luna.util.StringUtil;
import luna.util.TimeUtil;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KafkaRecordTranslator extends AbstractLifeCycle implements Translator {
    private ElasticApplier elasticApplier;

    public KafkaRecordTranslator(ElasticApplier elasticApplier){
        this.elasticApplier=elasticApplier;
    }

    public void start(){
        super.start();
        logger.info("KafkaRecordTranslator is started!");
    }

    public void stop(){
        super.stop();
        logger.info("KafkaRecordTranslator is stopped!");
    }


    public void translate(final List<Map<String,Object>> records){
        List<Record> esRecords = Lists.newArrayList();
        for(Map<String,Object> payload: records){
            Record record=translateToRecord(payload);
            esRecords.add(record);
        }
        long before = System.currentTimeMillis();
        elasticApplier.applyBatch(esRecords);
        long after = System.currentTimeMillis();
        timeLog.info("batch "+(after-before)+" "+records.size());
    }

    public void translateOneByOne(Map<String, Object> payload){
        Record record=translateToRecord(payload);
        String tableName = record.getType();
        long before = System.currentTimeMillis();
        elasticApplier.applyOneByOne(record);
        long after = System.currentTimeMillis();
        timeLog.info(tableName+" " + after + " " + (after-before));
    }

    private Record translateToRecord(Map<String, Object> payload){
        OperateType opType = getOpType((String) payload.get("type"));
        String database = (String) payload.get("database");
        String tableName = (String) payload.get("table");
        Map<String,Object> recordPayload = getCleanPayload(payload);
        String id = String.valueOf(recordPayload.get("id"));
        Record record = new Record(tableName,database,id,recordPayload,opType);

//        String modify_time = (String) recordPayload.get("modify_time");
//        long modifyTimeMillis = 0;
//        try {
//            modifyTimeMillis = TimeUtil.stringToLong(modify_time, "yy-MM-dd HH:mm:ss.SSS");
//        }catch (ParseException e){
//            errorLog.error(ExceptionUtils.getFullStackTrace(e));
//        }
//        long now = System.currentTimeMillis();
//        timeLog.info(tableName+" "+(now-modifyTimeMillis));
        return record;
    }

    private Map<String,Object> getCleanPayload(Map<String,Object>data){
        Map<String,Object> payload = new HashMap<>();
        Map <String, Object> sourcePayload = (Map<String,Object>)data.get("data");
        payload.clear();
        sourcePayload.forEach((key,value)->{
            if(value instanceof String){
                payload.put(key, StringUtil.stripEscape(StringUtil.stripControl((String)value)));
            }else{
                payload.put(key,value);
            }
        });
        return payload;
    }

    private OperateType getOpType(String type){
        switch (type){
            case "insert":
                return OperateType.I;
            case "update":
                return OperateType.U;
            case "delete":
                return OperateType.D;
            default:
                throw new LunaException("Unknown operation type!");
        }

    }
}
