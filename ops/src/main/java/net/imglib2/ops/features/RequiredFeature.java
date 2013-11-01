package net.imglib2.ops.features;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @author graumanna
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.FIELD )
public @interface RequiredFeature
{

}
