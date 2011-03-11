package mpicbg.imglib.view;

import mpicbg.imglib.Interval;
import Jama.Matrix;

/**
 * transform a n-dimensional source vector to a m-dimensional target vector.
 * can be represented as a (m+1 x n+1) homogeneous matrix.
 * 
 * @author Tobias Pietzsch
 */
public class ViewTransform
{
	/**
	 * dimension of source vector
	 */
	protected final int sourceDim;
	
	/**
	 * dimension of target vector
	 */
	protected final int targetDim;

	protected final long[] translation;

	/**
	 * for each component of the target vector (before translation).
	 * should the value be taken from a source vector component (false) or should it be zero (true).
	 */
	protected final boolean[] targetZero;

	/**
	 * for each component of the target vector (before translation).
	 * should the source vector component be inverted (true).
	 */
	protected final boolean[] targetInv;
	
	/**
	 * for each component of the target vector (before translation).
	 * from which source vector component should it be taken.
	 */
	protected final int[] targetComponent;
	
	public ViewTransform( final int sourceDim, final int targetDim )
	{
		this.sourceDim = sourceDim;
		this.targetDim = targetDim;
		translation = new long[ targetDim ];
		targetZero = new boolean[ targetDim ];
		targetInv = new boolean[ targetDim ];
		targetComponent = new int[ targetDim ];
		for ( int d = 0; d < targetDim; ++d ) {
			if ( d < sourceDim )
			{
				targetComponent[ d ] = d;
			}
			else
			{
				targetComponent[ d ] = 0;
				targetZero[ d ] = true;
			}
		}
	}

	public int sourceDim()
	{
		return sourceDim;
	}
	
	public int targetDim()
	{
		return targetDim;
	}
	
	public static void concatenate( final ViewTransform t1, final ViewTransform t2, final ViewTransform t1t2 )
	{
		assert t1.sourceDim == t2.targetDim;
		assert t1t2.targetDim == t1.targetDim;
		assert t1t2.sourceDim == t2.sourceDim;
		
		for ( int d = 0; d < t1t2.targetDim; ++d )
		{
			t1t2.translation[ d ] = t1.translation[ d ];
			if ( t1.targetZero[ d ] )
			{
				t1t2.targetZero[ d ] = true;
				t1t2.targetInv[ d ] = false;
				t1t2.targetComponent[ d ] = 0;
			}
			else
			{
				final long v = t2.translation[ t1.targetComponent[ d ] ];
				if ( t1.targetInv[ d ] )
					t1t2.translation[ d ] -= v;
				else
					t1t2.translation[ d ] += v;

				final int c = t1.targetComponent[ d ];
				if( t2.targetZero[ c ] ) {
					t1t2.targetZero[ d ] = true;
					t1t2.targetInv[ d ] = false;
					t1t2.targetComponent[ d ] = 0;					
				} else {
					t1t2.targetZero[ d ] = false;
					t1t2.targetInv[ d ] = ( t1.targetInv[ d ] != t2.targetInv[ c ] );
					t1t2.targetComponent[ d ] = t2.targetComponent[ c ];
				}
			}
		}
	}

	public void transform( long[] source, long[] target )
	{
		assert source.length == sourceDim;
		assert target.length == targetDim;

		for (int d = 0; d < targetDim; ++d )
		{
			target[ d ] = translation[ d ];
			if ( ! targetZero[ d ] )
			{
				final long v = source[ targetComponent[ d ] ];
				if ( targetInv[ d ] )
					target[ d ] -= v;
				else
					target[ d ] += v;
			}
		}
	}

	public IntervalImp transform( final Interval interval )
	{
		assert interval.numDimensions() == sourceDim;
		
		long[] max = new long[ sourceDim ];
		long[] min = new long[ sourceDim ];
		interval.min( min );
		interval.max( max );
		
		long[] tmax = new long[ targetDim ];
		long[] tmin = new long[ targetDim ];
		transform( min, tmin );
		transform( max, tmax );
		
		return new IntervalImp( tmin, tmax );
	}

	public void set( final ViewTransform transform )
	{
		assert sourceDim == transform.sourceDim;
		assert targetDim == transform.targetDim;

		for (int d = 0; d < targetDim; ++d)
		{
			translation[ d ] = transform.translation[ d ];
			targetZero[ d ] = transform.targetZero[ d ];
			targetInv[ d ] = transform.targetInv[ d ];
			targetComponent[ d ] = transform.targetComponent[ d ];
		}
	}

	public void getTranslation( final long[] t )
	{
		assert t.length == targetDim;
		for (int d = 0; d < targetDim; ++d)
			t[ d ] = translation[ d ];
	}

	public void setTranslation( final long[] t )
	{
		assert t.length == targetDim;
		for (int d = 0; d < targetDim; ++d)
			translation[ d ] = t[ d ];
	}

	public void getPermutation( final boolean[] zero, final int[] component, final boolean[] inv )
	{
		assert zero.length == targetDim;
		assert inv.length == targetDim;
		assert component.length == targetDim;
		for (int d = 0; d < targetDim; ++d)
		{
			zero[ d ] = targetZero[ d ];
			inv[ d ] = targetInv[ d ];
			component[ d ] = targetComponent[ d ];
		}		
	}
	
	public void setPermutation( final boolean[] zero, final int[] component, final boolean[] inv )
	{
		assert zero.length == targetDim;
		assert inv.length == targetDim;
		assert component.length == targetDim;
		for (int d = 0; d < targetDim; ++d)
		{
			assert component[ d ] < sourceDim;
			targetZero[ d ] = zero[ d ];
			targetInv[ d ] = inv[ d ];
			targetComponent[ d ] = component[ d ];
		}
	}
	
	public void setPermutation( final boolean[] zero, final int[] component )
	{
		assert zero.length == targetDim;
		assert component.length == targetDim;
		for (int d = 0; d < targetDim; ++d)
		{
			assert component[ d ] < sourceDim;
			targetZero[ d ] = zero[ d ];
			targetInv[ d ] = false;
			targetComponent[ d ] = component[ d ];
		}
	}
	
	public void setPermutation( final int[] component, final boolean[] inv )
	{
		assert inv.length == targetDim;
		assert component.length == targetDim;
		for (int d = 0; d < targetDim; ++d)
		{
			assert component[ d ] < sourceDim;
			targetZero[ d ] = false;
			targetInv[ d ] = inv[ d ];
			targetComponent[ d ] = component[ d ];
		}
	}
	
	public void setPermutation( final int[] component )
	{
		assert component.length == targetDim;
		for (int d = 0; d < sourceDim; ++d)
		{
			assert component[ d ] < sourceDim;
			targetZero[ d ] = false;
			targetInv[ d ] = false;
			targetComponent[ d ] = component[ d ];
		}
	}
	
	public Matrix getMatrix()
	{
		Matrix mat = new Matrix( targetDim+1, sourceDim+1 );

		mat.set( targetDim, sourceDim, 1 );
		
		for ( int d = 0; d < targetDim; ++d )
		{
			mat.set( d, sourceDim , translation[ d ] );
		}
		
		for ( int d = 0; d < targetDim; ++d )
		{
			if ( targetZero[ d ] == false )
			{
				mat.set (d, targetComponent[ d ], targetInv[ d ] ? -1 : 1 ); 
			}
		}
		
		return mat;
	}
}
