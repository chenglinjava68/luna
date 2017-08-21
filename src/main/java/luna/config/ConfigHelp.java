package luna.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

/**
 * 
* Copyright: Copyright (c) 2017 XueErSi
* 
* @ClassName: ConfigHelp.java
* @Description: Helper of parsing .yml file or network .yml file with org.yaml.snakeyaml.Yaml.
*
* @version: v1.0.0
* @author: GaoXing Chen
* @date: 2017年8月21日 下午7:20:58 
*
* Modification History:
* Date         Author          Version			Description
*---------------------------------------------------------*
* 2017年8月21日     GaoXing Chen      v1.0.0				添加注释
 */
public class ConfigHelp {
    private static final String HTTP = "http://";
    private static final String HTTPS = "https://";

    public static Map parse(String filename) throws Exception {
        Yaml yaml = new Yaml();
        InputStream is;
        if (filename.startsWith(ConfigHelp.HTTP) || filename.startsWith(ConfigHelp.HTTPS)) {
            URL httpUrl;
            URLConnection connection;
            httpUrl = new URL(filename);
            connection = httpUrl.openConnection();
            connection.connect();
            is = connection.getInputStream();
        } else {
            is = new FileInputStream(new File(filename));
        }
        return (Map) yaml.load(is);
    }
    
}
