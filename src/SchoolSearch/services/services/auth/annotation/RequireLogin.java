package SchoolSearch.services.services.auth.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.apache.tapestry5.ioc.annotations.AnnotationUseContext.PAGE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.apache.tapestry5.ioc.annotations.UseWith;


@Target(TYPE)
@Retention(RUNTIME)
@Documented
@UseWith({ PAGE })
public @interface RequireLogin {

	boolean admin() default false;

	public String[] roles() default {};

	Class<?> loginPageClass() default Object.class;

	String loginPage() default "";

	Class<?> returnPageClass() default Object.class;

	String returnPage() default "";

}
