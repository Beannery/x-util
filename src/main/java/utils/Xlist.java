package utils;

import java.util.List;
import java.util.Objects;

/**
 * List 工具类
 *
 * @author : XDD
 * @date : 2020-06-03 14:35
 */
public class Xlist {

    /**
     * 判非空
     */
    public static <T extends List> Boolean isNotBlank(T source){
        return Objects.nonNull(source)&&source.size()>0;
    }

    /**
     * 判空
     */
    public static <T extends List> Boolean isBlank(T source){
        return !isNotBlank(source);
    }
}
