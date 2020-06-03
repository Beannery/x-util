package ano;

import java.lang.annotation.*;

/**
 *  属性不可以为空注解
 * @author :XDD
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface NonNull {
}
