package luna.app;

import java.util.Map;

import luna.input.KafkaInput;

/**
 * 
* Copyright: Copyright (c) 2017 XueErSi
* 
* @ClassName: App.java
* @Description: MySQL-Kafka-Elasticsearch in incremental duplicati process base on MySQL-BinLog
*
* @version: v1.0.0
* @author: GaoXing Chen
* @date: 2017年8月21日 下午7:17:22 
*
* Modification History:
* Date         Author          Version			Description
*---------------------------------------------------------*
* 2017年8月21日     GaoXing Chen      v1.0.0				添加注释
 */
public class App {
	public static void main( String[] args ){
		KafkaInput kafka =new KafkaInput("src/main/java/conf/example.yml");
		kafka.excute();
	}
}
