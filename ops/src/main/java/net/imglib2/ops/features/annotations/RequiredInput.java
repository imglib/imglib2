package net.imglib2.ops.features.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.imglib2.ops.features.datastructures.CachedSampler;
import net.imglib2.ops.features.datastructures.Feature;

/**
 * Marker that this input is required. Currently supported are {@link Feature}s
 * and other {@link CachedSampler}s
 * 
 */
@Target( ElementType.FIELD )
@Retention( RetentionPolicy.RUNTIME )
public @interface RequiredInput
{}
