package luna.app;

import java.util.List;
import java.util.Map;

import luna.config.ConfigHelp;
import luna.elasticAdmin.ElasticAdmin;

/**
 * 
* Copyright: Copyright (c) 2017 XueErSi
* 
* @ClassName: AdminApp.java
* @Description: Elasticsearch index mapping according to mapping.yml
*
* @version: v1.0.0
* @author: GaoXing Chen
* @date: 2017年8月21日 下午7:15:46 
*
* Modification History:
* Date         Author          Version			Description
*---------------------------------------------------------*
* 2017年8月21日     GaoXing Chen      v1.0.0				添加注释
 */
public class AdminApp {
	public static void main(String[] args) {
		Map configs = null;
		Map config = null;
		try {
			config = ConfigHelp.parse("src/main/java/conf/mapping.yml");
			configs = ConfigHelp.parse("src/main/java/conf/example.yml");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		final Map outputConfigs = (Map) configs.get("Elasticsearch");
		ElasticAdmin admin = new ElasticAdmin(outputConfigs);
		List<Map<String, Map<String, Map>>> mapping = (List<Map<String, Map<String, Map>>>) config.get("mapping");

		mapping.forEach(index -> {
			index.forEach((indexName, type) -> {
				type.forEach((typeName, source) -> {
					admin.setIndex(indexName, typeName, source);
				});
			});
		});
	}
}
