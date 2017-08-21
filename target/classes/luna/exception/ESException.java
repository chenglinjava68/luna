package luna.exception;

/**
 * 
* Copyright: Copyright (c) 2017 XueErSi
* 
* @ClassName: ESException.java
* @Description: Elasticsearch Exception
*
* @version: v1.0.0
* @author: GaoXing Chen
* @date: 2017年8月21日 下午7:51:37 
*
* Modification History:
* Date         Author          Version			Description
*---------------------------------------------------------*
* 2017年8月21日     GaoXing Chen      v1.0.0				添加注释
 */
public class ESException extends Exception{
	public ESException(String msg){
		super(msg);
	}
}
