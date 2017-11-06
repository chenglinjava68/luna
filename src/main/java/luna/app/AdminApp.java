package luna.app;

import java.util.List;
import java.util.Map;

import luna.config.ConfigHelp;
import luna.elasticAdmin.ElasticAdmin;

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
* 2017年8月21日     GaoXing Chen      v1.0.0		deprecated
 */

@Deprecated
public class AdminApp {
	public static void main(String[] args) {
		Map configs = null;
		Map config = null;
		try {
			config = ConfigHelp.parse("conf/mapping.yml");
			configs = ConfigHelp.parse("conf/example.yml");
		} catch (Exception e) {
			e.printStackTrace();
		}
		final Map outputConfigs = (Map) configs.get("Elasticsearch");
		ElasticAdmin admin = new ElasticAdmin(outputConfigs);
		List<Map<String, Map<String, Map>>> mapping = (List<Map<String, Map<String, Map>>>) config.get("mapping");
        Map<String,Integer> mapSettings=(Map<String, Integer>) config.get("mapping.settings");
		mapping.forEach(index -> {
			index.forEach((indexName, type) -> {
				type.forEach((typeName, source) -> {
                    admin.setIndex(indexName, typeName, source, mapSettings.get("index.shard.number"), mapSettings.get("index.replica.number"));
                    //admin.setIndex(indexName, typeName, source);
				});
			});
		});
        admin.shutdown();
	}
}
