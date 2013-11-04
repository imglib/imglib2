package net.imglib2.ops.features;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker that this input is required. Currently supported are
 * {@link NumericFeature}s and other {@link CachedModule}s
 * 
 */
@Target( ElementType.FIELD )
@Retention( RetentionPolicy.RUNTIME )
public @interface ModuleInput
{}
