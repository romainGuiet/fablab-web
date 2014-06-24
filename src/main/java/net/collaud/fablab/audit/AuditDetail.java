package net.collaud.fablab.audit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.interceptor.InterceptorBinding;
import net.collaud.fablab.data.type.AuditAction;
import net.collaud.fablab.data.type.AuditObject;

/**
 *
 * @author gaetan
 */
@Inherited
@InterceptorBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface AuditDetail {
	public AuditObject object();
	public AuditAction action();
}
