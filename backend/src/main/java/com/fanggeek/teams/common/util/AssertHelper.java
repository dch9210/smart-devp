package com.fanggeek.teams.common.util;

import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 空值判断帮助类
 * 
 * 目前可以判断String、Collection、Map三种类型
 * 
 * @author tianjp
 *
 */
public class AssertHelper {
    
    public static final boolean isEmpty(Object obj) {
        return obj == null ? true : isEmpty(obj.toString());
    }

    public static final boolean notEmpty(Object obj) {
        return !isEmpty(obj);
    }

    /**
     * 判断字符串是否为空(null和 空白字符组成的字符串都算空)
     * @param str 入参字符串
     * @return true/false
     */
    public static final boolean isEmpty(String str) {
        return str == null ? true : str.trim().length() == 0;
    }

    public static final boolean notEmpty(String str) {
        return !isEmpty(str);
    }

    public static final boolean isEmpty(Collection collection) {
        return collection == null ? true : collection.size() == 0;
    }

    public static final boolean notEmpty(Collection collection) {
        return !isEmpty(collection);
    }

    public static final boolean isEmpty(Map map) {
        return map == null ? true : map.size() == 0;
    }

    public static final boolean notEmpty(Map map) {
        return !isEmpty(map);
    }

    public static final boolean isEmpty(Object[] objs) {
        return objs == null ? true : objs.length == 0;
    }

    public static final boolean notEmpty(Object[] objs) {
        return !isEmpty(objs);
    }

    public static final boolean isAllEmpty(String... strs) {
        for (String str : strs) {
            if (notEmpty(str)) {
                return false;
            }
        }
        return true;
    }

    public static final boolean isAllEmpty(Object... objs) {
        for (Object obj : objs) {
            if (notEmpty(obj)) {
                return false;
            }
        }
        return true;
    }

    public static final boolean isAllNotEmpty(String... strs) {
        for (String str : strs) {
            if (isEmpty(str)) {
                return false;
            }
        }
        return true;
    }
    
    public static final boolean isNotAllEmpty(String... strs) {
        for (String str : strs) {
            if (notEmpty(str)) {
                return true;
            }
        }
        return false;
    }

    public static final boolean isAnyoneEmpty(Object... objs) {
        for (Object obj : objs) {
            if (isEmpty(obj)) {
                return true;
            }
        }
        return false;
    }

    public static final boolean isEquals(Object obj1, Object obj2) {
        if (obj1 == null && obj2 == null) {
            return true;
        }
        if (obj1 != null && obj2 != null) {
            return obj1.equals(obj2);
        }
        return false;
    }
    
}
