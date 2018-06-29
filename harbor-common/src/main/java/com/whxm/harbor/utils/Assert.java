package com.whxm.harbor.utils;

import com.whxm.harbor.exception.ParameterInvalidException;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @Author Ftibw
 * @Email ftibw@live.com
 * @CreateTime 2018/6/26 04:30
 */
public class Assert<T> {

    public static void notNull(Object object, String message, Object... params) {
        if (object == null) {
            throw new ParameterInvalidException(message, JacksonUtils.toJson(params));
        }
    }

    public static void notEmpty(Object object, String message, Object... params) {
        if (object == null) {
            throw new ParameterInvalidException(message, JacksonUtils.toJson(params));
        }
        if (object instanceof List && ((List) object).isEmpty()) {
            throw new ParameterInvalidException(message, JacksonUtils.toJson(params));
        }
        if (object.getClass().isArray()) ;
    }

    public static void isNull(Object object, String message, Object... params) {
        if (object != null) {
            throw new ParameterInvalidException(message, JacksonUtils.toJson(params));
        }
    }

    public static void hasText(String text, String message, Object... params) {
        if (!StringUtils.hasText(text)) {
            throw new ParameterInvalidException(message, JacksonUtils.toJson(params));
        }
    }

    public static void main(String[] args) {
        Assert<Assert> test = new Assert<>();
        //char[] arr = {};
        Assert[] arr = {};
        System.out.println(test.isEmpty(arr, Assert.class));

        System.out.println(String.format("%s", Arrays.asList(new String[]{"2", "3", "4"})));
    }

    //数组的toString结果
    //[I@开头是int数组
    //[B@开头是byte数组
    //[J@开头是long数组
    //[D@开头是double数组
    //[F@开头是float数组
    //[S@开头是short数组
    //[Z@开头是boolean数组
    //char[]本质就是每个字符为ASCII码组成的字符串(例如char[] arr={49, 'B', '3', '4'};System.out.println(arr);结果为"1B34")
    //[Ljava.lang.Xxx;@格式是java.lang包下引用类型数组(例如xxx为String、Integer)
    //[Lcom.xxx.yyy.Zzz;@格式是com.xxx.yyy包下自定义类型数组
    //判断向上转型为Object类型后的对象真是类型是否为数组---object.getClass().isArray();
    //List、Set打印为[.*]
    //Map打印为{.*}

    /**
     * 检测元素类型为T的数组是否为空(null或者长度为0)
     *
     * @param object 数组
     * @param clazz  数组中元素类型
     * @return 数组是否为空
     */
    @SuppressWarnings("unchecked")
    public boolean isEmpty(Object object, Class<T> clazz) {
        return (isEmptyForBase(object)
                || object.getClass().isArray()
                && object.toString().matches("^\\[L" + clazz.getName() + ";@.*$"))
                && 0 == ((T[]) object).length;
    }

    public static boolean isEmptyForBase(Object object) {
        if (null == object) return true;
        if (object.toString().isEmpty() || object.toString().trim().isEmpty()) return true;
        if (object instanceof List) return ((List) object).isEmpty();
        if (object instanceof String[]) return 0 == ((String[]) object).length;
        if (object instanceof Integer[]) return 0 == ((Integer[]) object).length;
        if (object instanceof Long[]) return 0 == ((Long[]) object).length;
        if (object instanceof Double[]) return 0 == ((Double[]) object).length;
        if (object instanceof Byte[]) return 0 == ((Byte[]) object).length;
        if (object instanceof Float[]) return 0 == ((Float[]) object).length;
        if (object instanceof Boolean[]) return 0 == ((Boolean[]) object).length;
        if (object instanceof Short[]) return 0 == ((Short[]) object).length;
        if (object instanceof Character[]) return 0 == ((Character[]) object).length;
        if (object instanceof Map) return ((Map) object).isEmpty();
        if (object instanceof Set) return ((Set) object).isEmpty();
        if (object instanceof int[]) return 0 == ((int[]) object).length;
        if (object instanceof byte[]) return 0 == ((byte[]) object).length;
        if (object instanceof long[]) return 0 == ((long[]) object).length;
        if (object instanceof double[]) return 0 == ((double[]) object).length;
        if (object instanceof float[]) return 0 == ((float[]) object).length;
        if (object instanceof short[]) return 0 == ((short[]) object).length;
        if (object instanceof char[]) return object.toString().isEmpty() || object.toString().trim().isEmpty();
        return object instanceof boolean[] && 0 == ((boolean[]) object).length;
    }

    /*private static <R> R test(Object object, R... r) {
        return r[0];
    }*/

    //判断数组中是否有重复值
    public static void notRepeat(String[] array, String message) {
        Set<String> set = new HashSet<>();
        Collections.addAll(set, array);
        if (set.size() != array.length) {
            throw new ParameterInvalidException(message, Arrays.asList(array));
        }
    }

    public static void notRepeat(Integer[] array, String message) {
        Set<Integer> set = new HashSet<>();
        Collections.addAll(set, array);
        if (set.size() != array.length) {
            throw new ParameterInvalidException(message, Arrays.asList(array));
        }
    }
}
