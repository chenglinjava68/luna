package luna.output;

import luna.util.DingDingMsgUtil;
import org.apache.log4j.BasicConfigurator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Map;

public class BaseOutput {
    protected static TransportClient client = null;
    protected Logger log;
    private final Map outputConfig;

    public BaseOutput(Map config){
        outputConfig=config;
        prepare();
    }

    private void prepare(){
        BasicConfigurator.configure();
        log= LogManager.getLogger((String)outputConfig.get("logger"));
        boolean sniff=(boolean)outputConfig.get("sniff");                       //	Find the whole cluster by some host if sniff = true
        boolean compress=(boolean)outputConfig.get("compress");                 //	If compress the message
        String clusterName=(String)outputConfig.get("cluster.name");            //	Default Elasticsearch
        ArrayList<String> hosts=(ArrayList<String>) outputConfig.get("hosts");

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

    public void judgeResponse(DocWriteResponse response){
        if(response.getShardInfo().getFailed()>0){
            DingDingMsgUtil.sendMsg(response.getShardInfo().toString());
        }
    }
}
