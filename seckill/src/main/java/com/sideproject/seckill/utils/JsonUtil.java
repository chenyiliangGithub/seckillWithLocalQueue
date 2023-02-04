package com.sideproject.seckill.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * json工具类
 */
public class JsonUtil {
    private static Logger logger = LoggerFactory.getLogger(JsonUtil.class);

    private static boolean initialized = false;

    private JsonUtil() {
        synchronized (JsonUtil.class) {
            if (initialized == false) {
                initialized = true;
            } else {
                throw new RuntimeException("单例已被破坏");
            }
        }
    }

    static class SingletonHolder {
        private static final ObjectMapper instance  = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static ObjectMapper getInstance() {
        return JsonUtil.SingletonHolder.instance;
    }



    /**
     * 对象转json
     */
    public static String toJson(Object obj) {
        if (null == obj) return "";
        try {
            return getInstance().writeValueAsString(obj);
        } catch (Exception ex) {
            logger.error("json序列化失败", ex);
            throw new RuntimeException("json序列化失败", ex);
        }
    }

    /**
     * json转对象
     */
    public static <T> T toObj(String jsonString, Class<T> type) {
        try {
            return getInstance().readValue(jsonString, type);
        } catch (Exception ex) {
            logger.error("json反序列化失败", ex);
            throw new RuntimeException("json反序列化失败", ex);
        }
    }

    /**
     * json转Map
     */
    public static Map<String, Object> jsonToMap(String jsonString) {
        try {
            return getInstance().readValue(jsonString, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception ex) {
            logger.error("json转Map失败", ex);
            throw new RuntimeException("json转Map失败", ex);
        }
    }

    /**
     * 带泛型的json转对象 如：json 转 List<ClassA>时， type = List.class， elementClasses = ClassA.class
     *
     * @param jsonStr        json串
     * @param type           泛型类
     * @param elementClasses 泛型指定类
     * @param <T>
     * @return
     */
    public static <T> T toObjWithGeneric(String jsonStr, Class<T> type, Class<?>... elementClasses) {
        try {
            JavaType javaType = getInstance().getTypeFactory().constructParametricType(type, elementClasses);
            return getInstance().readValue(jsonStr, javaType);
        } catch (Exception e) {
            throw new RuntimeException("带泛型json反序列化失败", e);
        }
    }

    /**
     * new TypeReference<BaseVoiceCallRes<AgentStatusVO>>(){}
     * 复杂类型的的json转对象，如 json 转 ClassA<List<ClassB>>时， reference = new TypeReference<ClassA<List<ClassB>>>(){}
     *
     * @param jsonStr   json串
     * @param reference TypeReference
     * @param <T>       Class 要转换的类型
     * @return 返回对象
     */
    public static <T> T toObjWithComplexTypes(String jsonStr, TypeReference<T> reference) {
        try {
            return getInstance().readValue(jsonStr, reference);
        } catch (Exception e) {
            throw new RuntimeException("带泛型json反序列化失败", e);
        }
    }

}
