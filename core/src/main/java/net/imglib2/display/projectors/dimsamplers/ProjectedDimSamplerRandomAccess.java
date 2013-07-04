package net.imglib2.display.projectors.dimsamplers;

import net.imglib2.RandomAccess;
import net.imglib2.display.projectors.ProjectedDimSampler;

/**
 * the current implementations of {@link ProjectedDimSampler} work with an
 * underlying random access.
 * 
 * @author Michael Zinsmaier, Martin Horn, Christian Dietz
 * 
 * @param <T>
 */
public interface ProjectedDimSamplerRandomAccess< T > extends ProjectedDimSampler< T >
{

	/**
	 * @param srcAccess
	 *            sets the random access from which the sampler takes the values
	 *            of the projected dimension
	 */
	void setRandomAccess( RandomAccess< T > srcAccess );
}
