package net.imglib2.ops.features.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.imglib2.ops.features.FeatureSet;

/**
 * Marks a feature as required (in another class). The {@link FeatureSet}
 * resolve this dependency
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.FIELD )
public @interface RequiredFeature
{

}
