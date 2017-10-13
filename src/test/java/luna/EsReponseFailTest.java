package luna;

import luna.config.ConfigHelp;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EsReponseFailTest {
    public static void main(String [] args) throws InterruptedException{
        Map outputConfig;
        Map configs=null;
        try {
            configs= ConfigHelp.parse("conf/example.yml");
        } catch (Exception e) {
            e.printStackTrace();
        }
        outputConfig = (Map) configs.get("Elasticsearch");
        TransportClient client=null;
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
            }catch(Exception e){
            }
        }
        DeleteResponse indexResponse=client.prepareDelete("test", "user", "4").get();
        System.out.println(indexResponse.toString());
        Map insertdata= new HashMap();
        insertdata.put("name","sss");
        insertdata.put("age",29);
        try {
            IndexResponse response=client.prepareIndex("test", "user", "4").setSource(insertdata).get();
            System.out.println("ddddd"+response);
        }catch (Exception e){
            System.out.println(e);
        }

    }
}
