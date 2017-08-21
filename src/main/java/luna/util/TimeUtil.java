package luna.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
* Copyright: Copyright (c) 2017 XueErSi
* 
* @ClassName: TimeUtil.java
* @Description: Time Util
*
* @version: v1.0.0
* @author: GaoXing Chen
* @date: 2017年8月21日 下午8:20:59 
*
* Modification History:
* Date         Author          Version			Description
*---------------------------------------------------------*
* 2017年8月21日     GaoXing Chen      v1.0.0				添加注释
 */
public class TimeUtil {

	/**
	 * 
	* @Function: longToString
	* @Description: Change long to date String by format type
	*
	* @param: time: long; formatType: "yyyy-MM-dd HH:mm:ss.SSS" is an example
	* @return: String: date string
	* @throws: void
	*
	* @version: v1.0.0
	* @author: GaoXing Chen
	* @date: 2017年8月21日 下午8:21:29 
	*
	* Modification History:
	* Date         Author          Version			Description
	*---------------------------------------------------------*
	* 2017年8月21日     GaoXing Chen      v1.0.0				添加注释
	 */
    public static String longToString(long time, String formatType) {
        String strTime = "";
        Date date = longToDate(time, formatType);
        strTime = dateToString(date, formatType);
        return strTime;
    }

    /**
     * 
    * @Function: stringToDate
    * @Description: Change time string to Date object 
    *
    * @param: strTime: time string; formatType: strTime's format. "yyyy-MM-dd HH:mm:ss.SSS" is an example
    * @return: Date: Date object
    * @throws: void
    *
    * @version: v1.0.0
    * @author: GaoXing Chen
    * @date: 2017年8月21日 下午8:25:19 
    *
    * Modification History:
    * Date         Author          Version			Description
    *---------------------------------------------------------*
    * 2017年8月21日     GaoXing Chen      v1.0.0				添加注释
     */
    public static Date stringToDate(String strTime, String formatType) {
        SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        Date date = null;
        try {
            date = formatter.parse(strTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
    
    /**
     * 
    * @Function: longToDate
    * @Description: Change time long to Date object 
    *
    * @param: time: time long; formatType: "yyyy-MM-dd HH:mm:ss.SSS" is an example.
    * @return: Date: Date object
    * @throws: void
    *
    * @version: v1.0.0
    * @author: GaoXing Chen
    * @date: 2017年8月21日 下午8:27:28 
    *
    * Modification History:
    * Date         Author          Version			Description
    *---------------------------------------------------------*
    * 2017年8月21日     GaoXing Chen      v1.0.0				添加注释
     */
    public static Date longToDate(long time, String formatType) {
        Date dateOld = new Date(time);
        String sDateTime = dateToString(dateOld, formatType);
        Date date = stringToDate(sDateTime, formatType);
        return date;
    }
 
    /**
     * 
    * @Function: dateToLong
    * @Description: 该函数的功能描述
    *
    * @param: date: Date object
    * @return: long: date long
    * @throws: void
    *
    * @version: v1.0.0
    * @author: GaoXing Chen
    * @date: 2017年8月21日 下午8:29:08 
    *
    * Modification History:
    * Date         Author          Version			Description
    *---------------------------------------------------------*
    * 2017年8月21日     GaoXing Chen      v1.0.0				添加注释
     */
    public static long dateToLong(Date date) {
        return date.getTime();
    }

    /**
     * 
    * @Function: TimeUtil.java
    * @Description: Change time long to "yy-MM-dd HH:mm" format String
    *
    * @param: time: time long
    * @return: String: time string
    * @throws: void
    *
    * @version: v1.0.0
    * @author: GaoXing Chen
    * @date: 2017年8月21日 下午8:30:26 
    *
    * Modification History:
    * Date         Author          Version			Description
    *---------------------------------------------------------*
    * 2017年8月21日     GaoXing Chen      v1.0.0				添加注释
     */
    public static String getTime(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm");
        return format.format(new Date(time));
    }

    /**
     * 
    * @Function: getHourAndMin
    * @Description: Change time long to "yy-MM-dd HH:mm" format String
    *
    * @param: time: time long
    * @return: String: time string
    * @throws: void
    *
    * @version: v1.0.0
    * @author: GaoXing Chen
    * @date: 2017年8月21日 下午8:31:37 
    *
    * Modification History:
    * Date         Author          Version			Description
    *---------------------------------------------------------*
    * 2017年8月21日     GaoXing Chen      v1.0.0				添加注释
     */
    public static String getHourAndMin(long time) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(new Date(time));
    }
    
    /**
     * 
    * @Function: dateToString
    * @Description: Change Date object to String
    *
    * @param: date: Date object; formatType: "yyyy-MM-dd HH:mm:ss.SSS" is an example.
    * @return: void
    * @throws: void
    *
    * @version: v1.0.0
    * @author: GaoXing Chen
    * @date: 2017年8月21日 下午8:32:36 
    *
    * Modification History:
    * Date         Author          Version			Description
    *---------------------------------------------------------*
    * 2017年8月21日     GaoXing Chen      v1.0.0				添加注释
     */
    public static String dateToString(Date data, String formatType) {
        return new SimpleDateFormat(formatType).format(data);
    }
    
    /**
     * 
    * @Function: stringToLong
    * @Description: Change time string to long
    *
    * @param: strTime: time string; formatType: "yyyy-MM-dd HH:mm:ss.SSS" is an example.
    * @return: long: time long
    * @throws: void
    *
    * @version: v1.0.0
    * @author: GaoXing Chen
    * @date: 2017年8月21日 下午8:33:55 
    *
    * Modification History:
    * Date         Author          Version			Description
    *---------------------------------------------------------*
    * 2017年8月21日     GaoXing Chen      v1.0.0				添加注释
     */
    public static long stringToLong(String strTime, String formatType) {
    	SimpleDateFormat sdf = new SimpleDateFormat(formatType);
    	long currentTime=0;
		try {
			currentTime = sdf.parse(strTime).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
    	return currentTime;
    }
}
