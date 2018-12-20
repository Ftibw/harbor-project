package com.whxm.harbor.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The class JacksonUtils
 * <p>
 * json字符与对像转换
 *
 * @version: $Revision$ $Date$ $LastChangedBy$
 */
public final class JacksonUtils {
    private static final Logger logger = LoggerFactory.getLogger(JacksonUtils.class);

    private static ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    private JacksonUtils() {
    }

    /**
     * 使用泛型方法，把json字符串转换为相应的JavaBean对象。
     * (1)转换为普通JavaBean：readValue(json,Student.class)
     * (2)转换为List,如List<Student>,将第二个参数传递为Student
     * [].class.然后使用Arrays.asList();方法把得到的数组转换为特定类型的List
     *
     * @param jsonStr
     * @param valueType
     * @return
     */
    public static <T> T readValue(String jsonStr, Class<T> valueType) {

        try {
            return objectMapper.readValue(jsonStr, valueType);
        } catch (Exception e) {
            logger.error("json格式错误", e.getMessage());
        }
        return null;
    }

    /**
     * json字符串转带泛型的对象
     *
     * @param jsonStr      json字符串
     * @param valueTypeRef 泛型类型
     * @return value
     */
    public static <T> T readGenericTypeValue(String jsonStr, TypeReference<T> valueTypeRef) {
        try {
            return objectMapper.readValue(jsonStr, valueTypeRef);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 把JavaBean转换为json字符串
     *
     * @param object
     * @return
     */
    public static String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void excludeNull(boolean toggle) {
        if (null != objectMapper) {
            if (toggle)
                objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            else
                objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        }
    }
}