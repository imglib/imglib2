package net.imglib2.display.projectors;

import net.imglib2.Iterator;
import net.imglib2.RandomAccess;
import net.imglib2.Sampler;

/**
 * provides samples from a projected dimension. E.g. the color values from the
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

interface ProjectedDimSamplerImpl<T> extends ProjectedDimSampler<T> {

        /**
         * @param srcAccess
         *                sets the random access from which the sampler takes
         *                the values of the projected dimension
         */
        void setRandomAccess(RandomAccess<T> srcAccess);
}