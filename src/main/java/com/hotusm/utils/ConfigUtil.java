package com.hotusm.utils;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 配置文件工具类，用来获取配置文件的配置内容<br>
 * 1.web工程的时候
 * 文件位置：WEB-INF/classes/configs/config.properties文件中的配置项,修复原初始化代码，<br>
 * 2.非web工程的时候
 *  从类加载其中获取
 */
public class ConfigUtil {

    private final static Logger log = LogManager.getLogger(ConfigUtil.class);

    // 非特殊情况不要公开变量
    private static Properties props;

    //是否是web工程
    private static boolean isWeb=false;

    static {
        InputStream fis = null;
        try {
            // 初始化路径
            String fileName = "config.properties";
            props = new Properties();
            if(isWeb){
                String filePath = getConfigFolderPath() + fileName;
                log.info("Init ConfigUtil, The Properties Path Is = " + filePath);
                // 读取属性文件
                fis = new FileInputStream(filePath);
            }else {
                log.info("not web project,the Properties Path is = "+fileName);
                fis=Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
            }
            props.load(fis);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                fis.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private ConfigUtil() {
    }

    /**
     * 获取配置文件夹的路径
     *
     * @return
     */
    public static String getConfigFolderPath() {
        StringBuilder sb = new StringBuilder();
        String webPath = System.getProperty("WebRoot");
        if (webPath == null) {
            webPath = ConfigUtil.class.getResource("/").getPath();
            sb.append(webPath);
        } else {
            sb.append(webPath);
            sb.append("WEB-INF").append(File.separator);
            sb.append("classes").append(File.separator);
        }
        sb.append("configs").append(File.separator);
        return sb.toString();
    }

    /**
     * 根据给定的key获取:
     *
     * @param key
     * @return
     */
    public static String getValue(String key) {
        if(props.getProperty(key) != null) {
            return props.getProperty(key).trim();
        } else {
            log.error("config property not exist, key:{}", key);
            throw new RuntimeException();
        }
    }

    /**
     * 获取Int值
     *
     * @param key
     * @return
     */
    public static int getIntValue(String key) {
        if(props.getProperty(key) != null) {
            return Integer.parseInt(getValue(key));
        } else {
            log.error("config property not exist, key:{}", key);
            throw new RuntimeException();
        }
    }

    /**
     * 获取Boolean类型的值
     *
     * @param key
     * @return
     */
    public static boolean getBooleanValue(String key) {
        if(props.getProperty(key) != null) {
            return BooleanUtils.toBoolean(getValue(key));
        } else {
            log.error("config property not exist, key:{}", key);
            throw new RuntimeException();

        }
    }

    public static void main(String[] args){

      InputStream is= Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties");
      System.out.print(is);
    }

}