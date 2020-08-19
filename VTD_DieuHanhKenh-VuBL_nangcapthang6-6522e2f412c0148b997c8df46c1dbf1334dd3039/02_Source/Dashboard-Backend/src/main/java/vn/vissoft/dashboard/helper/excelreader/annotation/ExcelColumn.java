package vn.vissoft.dashboard.helper.excelreader.annotation;


import javax.validation.Constraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExcelColumn {
    String name();
    boolean nullable() default false;
    long maxLength() default -1;
    String regex() default "";

}
