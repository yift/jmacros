import me.ykaplan.jmacros.Interpolation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@interface MyAnotation {
    String value();
}

@Interpolation
@MyAnotation("<`1 + 4`>")
public class AnnotationsValue {
    public static String go() {
        var annotation = AnnotationsValue.class.getAnnotation(MyAnotation.class);
        return annotation.value();
    }

}