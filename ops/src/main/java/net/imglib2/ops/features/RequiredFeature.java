package net.imglib2.ops.features;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a feature as required (in another class). The {@link FeatureSet}
 * resolve this dependency
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.FIELD )
public @interface RequiredFeature
{

}
