package mpicbg.imglib.view;

import mpicbg.imglib.FinalInterval;
import mpicbg.imglib.Interval;
import mpicbg.imglib.Localizable;
import mpicbg.imglib.Positionable;
import mpicbg.imglib.transform.Transform;
import Jama.Matrix;

/**
 * transform a n-dimensional source vector to a m-dimensional target vector. can
 * be represented as a (m+1 x n+1) homogeneous matrix.
 * 
 * @author Tobias Pietzsch
 */
public class ViewTransform implements Transform
{
	/**
	 * dimension of source vector.
	 */
	protected final int numSourceDimensions;

	/**
	 * dimension of target vector.
	 */
	protected final int numTargetDimensions;

	/**
	 * translation is added to the target vector after applying permutation,
	 * projection, inversion operations.
	 */
	protected final long[] translation;

	/**
	 * for each component of the target vector (before translation). should the
	 * value be taken from a source vector component (false) or should it be
	 * zero (true).
	 */
	protected final boolean[] zero;

	/**
	 * for each component of the target vector (before translation). should the
	 * source vector component be inverted (true).
	 */
	protected final boolean[] invert;

	/**
	 * for each component of the target vector (before translation). from which
	 * source vector component should it be taken.
	 */
	protected final int[] component;

	public ViewTransform( final int sourceDim, final int targetDim )
	{
		this.numSourceDimensions = sourceDim;
		this.numTargetDimensions = targetDim;
		translation = new long[ targetDim ];
		zero = new boolean[ targetDim ];
		invert = new boolean[ targetDim ];
		component = new int[ targetDim ];
		for ( int d = 0; d < targetDim; ++d )
		{
			if ( d < sourceDim )
			{
				component[ d ] = d;
			}
			else
			{
				component[ d ] = 0;
				zero[ d ] = true;
			}
		}
	}

	@Override
	public int numSourceDimensions()
	{
		return numSourceDimensions;
	}

	@Override
	public int numTargetDimensions()
	{
		return numTargetDimensions;
	}

	@Override
	public void apply( long[] source, long[] target )
	{
		assert source.length == numSourceDimensions;
		assert target.length == numTargetDimensions;

		for ( int d = 0; d < numTargetDimensions; ++d )
		{
			target[ d ] = translation[ d ];
			if ( !zero[ d ] )
			{
				final long v = source[ component[ d ] ];
				if ( invert[ d ] )
					target[ d ] -= v;
				else
					target[ d ] += v;
			}
		}
	}

	@Override
	public void apply( int[] source, int[] target )
	{
		assert source.length == numSourceDimensions;
		assert target.length == numTargetDimensions;

		for ( int d = 0; d < numTargetDimensions; ++d )
		{
			target[ d ] = ( int ) translation[ d ];
			if ( !zero[ d ] )
			{
				final long v = source[ component[ d ] ];
				if ( invert[ d ] )
					target[ d ] -= v;
				else
					target[ d ] += v;
			}
		}
	}

	@Override
	public void apply( Localizable source, Positionable target )
	{
		assert source.numDimensions() == numSourceDimensions;
		assert target.numDimensions() == numTargetDimensions;

		for ( int d = 0; d < numTargetDimensions; ++d )
		{
			long pos = translation[ d ];
			if ( !zero[ d ] )
			{
				final long v = source.getLongPosition( component[ d ] );;
				if ( invert[ d ] )
					pos -= v;
				else
					pos += v;
			}
			target.setPosition( pos, d );
		}
	}

	public static void concatenate( final ViewTransform t1, final ViewTransform t2, final ViewTransform t1t2 )
	{
		assert t1.numSourceDimensions == t2.numTargetDimensions;
		assert t1t2.numTargetDimensions == t1.numTargetDimensions;
		assert t1t2.numSourceDimensions == t2.numSourceDimensions;

		for ( int d = 0; d < t1t2.numTargetDimensions; ++d )
		{
			t1t2.translation[ d ] = t1.translation[ d ];
			if ( t1.zero[ d ] )
			{
				t1t2.zero[ d ] = true;
				t1t2.invert[ d ] = false;
				t1t2.component[ d ] = 0;
			}
			else
			{
				final long v = t2.translation[ t1.component[ d ] ];
				if ( t1.invert[ d ] )
					t1t2.translation[ d ] -= v;
				else
					t1t2.translation[ d ] += v;

				final int c = t1.component[ d ];
				if ( t2.zero[ c ] )
				{
					t1t2.zero[ d ] = true;
					t1t2.invert[ d ] = false;
					t1t2.component[ d ] = 0;
				}
				else
				{
					t1t2.zero[ d ] = false;
					t1t2.invert[ d ] = ( t1.invert[ d ] != t2.invert[ c ] );
					t1t2.component[ d ] = t2.component[ c ];
				}
			}
		}
	}

	public FinalInterval transform( final Interval interval )
	{
		assert interval.numDimensions() == numSourceDimensions;

		long[] max = new long[ numSourceDimensions ];
		long[] min = new long[ numSourceDimensions ];
		interval.min( min );
		interval.max( max );

		long[] tmax = new long[ numTargetDimensions ];
		long[] tmin = new long[ numTargetDimensions ];
		apply( min, tmin );
		apply( max, tmax );

		return new FinalInterval( tmin, tmax );
	}

	public void set( final ViewTransform transform )
	{
		assert numSourceDimensions == transform.numSourceDimensions;
		assert numTargetDimensions == transform.numTargetDimensions;

		for ( int d = 0; d < numTargetDimensions; ++d )
		{
			translation[ d ] = transform.translation[ d ];
			zero[ d ] = transform.zero[ d ];
			invert[ d ] = transform.invert[ d ];
			component[ d ] = transform.component[ d ];
		}
	}

	public void getTranslation( final long[] t )
	{
		assert t.length == numTargetDimensions;
		for ( int d = 0; d < numTargetDimensions; ++d )
			t[ d ] = translation[ d ];
	}

	public void setTranslation( final long[] t )
	{
		assert t.length == numTargetDimensions;
		for ( int d = 0; d < numTargetDimensions; ++d )
			translation[ d ] = t[ d ];
	}

	public void getPermutation( final boolean[] pzero, final int[] pcomponent, final boolean[] pinv )
	{
		assert pzero.length == numTargetDimensions;
		assert pinv.length == numTargetDimensions;
		assert pcomponent.length == numTargetDimensions;
		for ( int d = 0; d < numTargetDimensions; ++d )
		{
			pzero[ d ] = zero[ d ];
			pinv[ d ] = invert[ d ];
			pcomponent[ d ] = component[ d ];
		}
	}

	public void setPermutation( final boolean[] pzero, final int[] pcomponent, final boolean[] pinv )
	{
		assert pzero.length == numTargetDimensions;
		assert pinv.length == numTargetDimensions;
		assert pcomponent.length == numTargetDimensions;
		for ( int d = 0; d < numTargetDimensions; ++d )
		{
			assert pcomponent[ d ] < numSourceDimensions;
			zero[ d ] = pzero[ d ];
			invert[ d ] = pinv[ d ];
			component[ d ] = pcomponent[ d ];
		}
	}

	public void setPermutation( final boolean[] pzero, final int[] pcomponent )
	{
		assert pzero.length == numTargetDimensions;
		assert pcomponent.length == numTargetDimensions;
		for ( int d = 0; d < numTargetDimensions; ++d )
		{
			assert pcomponent[ d ] < numSourceDimensions;
			zero[ d ] = pzero[ d ];
			invert[ d ] = false;
			component[ d ] = pcomponent[ d ];
		}
	}

	public void setPermutation( final int[] pcomponent, final boolean[] pinv )
	{
		assert pinv.length == numTargetDimensions;
		assert pcomponent.length == numTargetDimensions;
		for ( int d = 0; d < numTargetDimensions; ++d )
		{
			assert pcomponent[ d ] < numSourceDimensions;
			zero[ d ] = false;
			invert[ d ] = pinv[ d ];
			component[ d ] = pcomponent[ d ];
		}
	}

	public void setPermutation( final int[] pcomponent )
	{
		assert pcomponent.length == numTargetDimensions;
		for ( int d = 0; d < numSourceDimensions; ++d )
		{
			assert pcomponent[ d ] < numSourceDimensions;
			zero[ d ] = false;
			invert[ d ] = false;
			component[ d ] = pcomponent[ d ];
		}
	}

	public Matrix getMatrix()
	{
		Matrix mat = new Matrix( numTargetDimensions + 1, numSourceDimensions + 1 );

		mat.set( numTargetDimensions, numSourceDimensions, 1 );

		for ( int d = 0; d < numTargetDimensions; ++d )
		{
			mat.set( d, numSourceDimensions, translation[ d ] );
		}

		for ( int d = 0; d < numTargetDimensions; ++d )
		{
			if ( zero[ d ] == false )
			{
				mat.set( d, component[ d ], invert[ d ] ? -1 : 1 );
			}
		}

		return mat;
	}
}
