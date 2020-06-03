package utils;

import java.util.Objects;

/**
 * 字符串操作工具类
 *
 * @author : XDD
 * @date : 2020-06-03 14:26
 */
public class Xstring {

    /**
     * 字符串判空
     */
    public static Boolean isNotBlank(String source){
        return Objects.nonNull(source) && !"".equals(source);
    }

    /**
     * 判非空
     */
    public static Boolean isBlank(String source){
        return !isNotBlank(source);
    }
}
