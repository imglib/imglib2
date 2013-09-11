package net.imglib2.display.projectors;

import net.imglib2.Point;
import net.imglib2.display.projectors.sampler.SamplingProjector2D;
import net.imglib2.display.projectors.specializedprojectors.ArrayImgXYByteProjector;

/**
 * Base class for 2D projectors. Projecting means in this case projecting from a
 * source format to a target format (A->B). 2D hints that the result is
 * something 2 dimensional. The base class provides methods to select a
 * reference point in a multi-dimensional data object. Sub classes like
 * {@link Projector2D}, {@link SamplingProjector2D} or
 * {@link ArrayImgXYByteProjector} specify a mapping that uses the reference
 * point to project data into a 2 dimensional representation. <br>
 * A basic example is the extraction of a data plain (containing the reference
 * point) by sampling two axes
 * 
 * 
 * 
 * @author Michael Zinsmaier, Martin Horn, Christian Dietz
 * 
 * @param <A> source type
 * @param <B> target type
 */
public abstract class AbstractProjector2D< A, B > extends Point implements Projector< A, B >
{

	protected final long[] min;

	protected final long[] max;

	/**
	 * initializes a reference point with the specified number of dimensions.
	 * Start position is 0,0,...,0
	 * 
	 * @param numDims
	 */
	public AbstractProjector2D( int numDims )
	{
		// as this is an 2D projector, we need at least two dimensions,
		// even if the source is one-dimensional
		super(Math.max( 2, numDims ) );

		min = new long[ n ];
		max = new long[ n ];
	}

}
