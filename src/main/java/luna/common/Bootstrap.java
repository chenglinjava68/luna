package luna.common;

import luna.common.context.ElasticContext;
import luna.common.context.KafkaContext;
import luna.extractor.KafkaExtractor;
import luna.applier.ElasticApplier;
import luna.translator.KafkaRecordTranslator;
import luna.util.ConfigUtil;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.List;
import java.util.Map;
import java.util.Properties;

public class Bootstrap extends AbstractLifeCycle{
    private KafkaExtractor kafkaExtractor;
    private KafkaRecordTranslator kafkaRecordTranslator;
    private ElasticApplier elasticApplier;
    private final Map inputConfigs;
    private final Map elasticConfigs;
    private KafkaContext kafkaContext = new KafkaContext();
    private ElasticContext elasticContext = new ElasticContext();

    public Bootstrap(String configFile){
        Map configs=null;
        try {
            configs= ConfigUtil.parse(configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        inputConfigs = (Map)configs.get("NewKafka");
        elasticConfigs = (Map)configs.get("Elasticsearch");
    }

    public void start(){
        super.start();
        initKafkaContext();
        initElasticContext();
        elasticApplier = new ElasticApplier(elasticContext);
        elasticApplier.start();
        kafkaRecordTranslator = new KafkaRecordTranslator(elasticApplier);
        kafkaRecordTranslator.start();
        kafkaExtractor = new KafkaExtractor(kafkaContext,kafkaRecordTranslator);
        kafkaExtractor.start();
        kafkaExtractor.extract();
        logger.info("Bootstrap is started!");
    }

    public void stop(){
        logger.info("Bootstrap is stopped!");
        elasticApplier.stop();
        kafkaExtractor.stop();
        kafkaRecordTranslator.stop();
        super.stop();
    }

    private void initKafkaContext(){
        String groupId = (String)inputConfigs.get("group.id");
        List<String> topics=(List<String>) inputConfigs.get("topics");
        String maxFetchByte = ""+inputConfigs.get("max.fetch.byte");
        int maxPollRecords=(int)inputConfigs.get("max.poll.records");
        int retryTimes =(int)inputConfigs.get("retry.times");
        int retryInterval = (int)inputConfigs.get("retry.interval");
        int purgeInterval = (int)inputConfigs.get("purge.interval");

        Properties props = new Properties();
        props.put("bootstrap.servers", inputConfigs.get("bootstrap.servers"));
        props.put("group.id", groupId);
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", StringDeserializer.class.getName());
        props.put("max.partition.fetch.bytes",maxFetchByte);
        props.put("max.poll.records",maxPollRecords);
        props.put("security.protocol", "SASL_PLAINTEXT");
        props.put("sasl.mechanism", "PLAIN");
        props.put("enable.auto.commit", "false");

        kafkaContext.setRetryTimes(retryTimes);
        kafkaContext.setRetryInterval(retryInterval);
        kafkaContext.setProps(props);
        kafkaContext.setTopics(topics);
        kafkaContext.setPurgeInterval(purgeInterval);
        logger.info("KafkaContext has inited!");
    }

    private void initElasticContext(){
        boolean sniff=(boolean)elasticConfigs.get("sniff");
        boolean compress=(boolean)elasticConfigs.get("compress");
        String clusterName=(String)elasticConfigs.get("cluster.name");
        int bulkBorder = (int)elasticConfigs.get("bulk.border");
        List<String> hosts=(List<String>)elasticConfigs.get("hosts");


        elasticContext.setClusterName(clusterName);
        elasticContext.setCompress(compress);
        elasticContext.setHosts(hosts);
        elasticContext.setSniff(sniff);
        elasticContext.setBulkBorder(bulkBorder);
        logger.info("Elasticsearch context has inited!");
    }

}
