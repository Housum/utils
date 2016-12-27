package com.hotusm.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Json转换工具
 */
public class JsonUtil {

    private final static Logger log = LogManager.getLogger(JsonUtil.class);

    public static String Bean2Json(Object bean) {
        try {
            return JSON.toJSONString(bean, SerializerFeature.WriteMapNullValue);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T Json2Bean(String json, Class<T> clazz) {
        try {
            return JSON.parseObject(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> List<T> Json2List(String json, Class<T> clazz) {
        try {
            return JSON.parseArray(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
