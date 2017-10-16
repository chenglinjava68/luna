package luna.app;

import java.util.Map;

import luna.input.KafkaInput;

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
* 2017年8月21日     GaoXing Chen      v1.0.0		  添加注释
 */
public class App {
	public static void main( String[] args ){
		KafkaInput kafka =new KafkaInput("conf/example.yml");
		kafka.excute();
	}
}
