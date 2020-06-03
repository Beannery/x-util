package utils;

import com.sun.istack.internal.NotNull;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 通用校验不为空的参数
 * 可配合AOP使用
 *
 * @author : XDD
 * @date : 2020-03-28 15:02
 */
public class Xparams {
    public static <T> List<String> check(T source) {
        Class<?> targetClass = source.getClass();
        List<String> resultList = getResultList(source);
        targetClass = targetClass.getSuperclass();
        if (Objects.nonNull(targetClass)) {
            try {
                resultList.addAll(getParentErrMsgList(targetClass.newInstance()));
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return resultList;
    }

    private static <T> List<String> getResultList(T source) {
        Class<?> targetClass = source.getClass();
        List<String> resultList = new ArrayList<>();
        for (Field field : targetClass.getDeclaredFields()) {
            boolean annotationPresent = field.isAnnotationPresent(NotNull.class);
            if (!annotationPresent) {
                continue;
            }
            String errMsg = getFileErrMsg(field, source);
            if (Xstring.isNotBlank(errMsg)) {
                resultList.add(errMsg);
            }
        }
        return resultList;
    }

    private static <T> String getFileErrMsg(Field field, T source) {
        String errMsg = null;
        Class<?> classType = source.getClass();
        String fieldName = field.getName();
        try {
            Method method = classType.getMethod(getMethodName(fieldName));
            Object fieldValue = method.invoke(source);
            String clazzType = getClassType(field);
            errMsg = getErrMsg(fieldValue, fieldName, clazzType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return errMsg;
    }

    private static String getClassType(Field field) {
        String fieldTypeString = field.getType().toString();
        int index = fieldTypeString.lastIndexOf(DOT);
        return fieldTypeString.substring(index + FIRST_INDEX);
    }

    private static String getMethodName(String fieldName) {
        String firstLetter = fieldName.substring(ZERO_INDEX, FIRST_INDEX).toUpperCase();
        return GET + firstLetter + fieldName.substring(FIRST_INDEX);
    }

    private static <T> String getErrMsg(T fieldValue, String fieldName, String clazzType) {
        String errMsg = null;
        if (Objects.isNull(fieldValue)) {
            errMsg = fieldName + CAN_NOT_BE_NULL;
        } else if (STRING.equals(clazzType)) {
            if (Xstring.isBlank((String) fieldValue)) {
                errMsg = fieldName + CAN_NOT_BE_NULL;
            }
        } else if (LIST.equals(clazzType)) {
            List<String> errorList = getListError((List) fieldValue, fieldName);
            if (Xlist.isNotBlank(errorList)) {
                errMsg = errorList.toString();
            }
        }
        return errMsg;
    }

    private static String getListErrorMsg(String fieldName, int index, List<String> checkList) {
        return ERROR_MSG_4_LIST
                .replace("@FIELD@", fieldName)
                .replace("@INDEX@", String.valueOf(index))
                .replace("@MSG@", checkList.toString());
    }

    private static <T> List<String> getParentErrMsgList(T source) {
        List<String> parentResultList = getResultList(source);
        return parentResultList.stream().map(res -> PARENT + res).collect(Collectors.toList());
    }

    private static <T extends Collection> List<String> getListError(T fieldValue, String fieldName) {
        List fieldValueList = (List) fieldValue;
        List<String> errList = new ArrayList<>();
        for (int index = ZERO_INDEX; index < fieldValueList.size(); index++) {
            List<String> checkList = getResultList(fieldValueList.get(index));
            if (Xlist.isNotBlank(checkList)) {
                errList.add(getListErrorMsg(fieldName, index, checkList));
            }
        }
        return errList;
    }

    private static final Integer ZERO_INDEX       = 0;
    private static final Integer FIRST_INDEX      = 1;
    private static final String  DOT              = ".";
    private static final String  GET              = "get";
    private static final String  LIST             = "List";
    private static final String  STRING           = "String";
    private static final String  PARENT           = "父级属性:";
    private static final String  CAN_NOT_BE_NULL  = ":不可为空";
    private static final String  ERROR_MSG_4_LIST = "@FIELD@在下标为@INDEX@处:@MSG@";
}
