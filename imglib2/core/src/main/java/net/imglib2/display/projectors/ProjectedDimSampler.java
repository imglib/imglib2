package net.imglib2.display.projectors;

import net.imglib2.Iterator;
import net.imglib2.Sampler;

/**
 * provides samples from a projected ({@link DimProjector2D}) dimension. E.g. the color values from the
 * color dimension when projecting the values onto the xy plane.
 *
 * Implements {@link Iterator} and {@link Sampler} to allow access to the
 * (selected) values of the projected dimension
 *
 * @author zinsmaie
 *
 * @param <A>
 */
public interface ProjectedDimSampler<T> extends Iterator, Sampler<T> {

};
