package mpicbg.imglib.view;

import mpicbg.imglib.util.Util;
import Jama.Matrix;

/**
 * transform a n-vector to a m-vector.
 * can be represented as a (m+1 x n+1) homogeneous matrix.
 * 
 * @author Tobias Pietzsch
 */
public class ViewTransform
{
	/**
	 * dimension of source vector
	 */
	protected final int n;
	
	/**
	 * dimension of target vector
	 */
	protected final int m;

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
	
	public ViewTransform( final int n, final int m )
	{
		this.n = n;
		this.m = m;
		translation = new long[ m ];
		targetZero = new boolean[ m ];
		targetInv = new boolean[ m ];
		targetComponent = new int[ m ];
		for ( int d = 0; d < m; ++d ) {
			if ( d < n )
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
		return n;
	}
	
	public int targetDim()
	{
		return m;
	}
	
	public static void concatenate( final ViewTransform t1, final ViewTransform t2, final ViewTransform t1t2 )
	{
		assert t1.n == t2.m;
		assert t1t2.m == t1.m;
		assert t1t2.n == t2.n;
		
		for ( int d = 0; d < t1t2.m; ++d )
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
	
	private void permutateVector( long[] in, long[] out )
	{
		assert in.length == n;
		assert out.length == m;

		for (int d = 0; d < m; ++d )
		{
			if ( targetZero[ d ] )
			{
				out[ d ] = 0;
			}
			else
			{
				final long v = in[ targetComponent[ d ] ];
				if ( targetInv[ d ] )
					out[ d ] = -v;
				else
					out[ d ] = v;
			}
		}
	}

	private void permutateVector( int[] in, int[] out )
	{
		assert in.length == n;
		assert out.length == m;

		for (int d = 0; d < m; ++d )
		{
			if ( targetZero[ d ] )
			{
				out[ d ] = 0;
			}
			else
			{
				final int v = in[ targetComponent[ d ] ];
				if ( targetInv[ d ] )
					out[ d ] = -v;
				else
					out[ d ] = v;
			}
		}
	}

	public void set( final ViewTransform transform )
	{
		assert n == transform.n;
		assert m == transform.m;

		for (int d = 0; d < m; ++d)
		{
			translation[ d ] = transform.translation[ d ];
			targetZero[ d ] = transform.targetZero[ d ];
			targetInv[ d ] = transform.targetInv[ d ];
			targetComponent[ d ] = transform.targetComponent[ d ];
		}
	}

	public void getTranslation( final long[] t )
	{
		assert t.length == m;
		for (int d = 0; d < m; ++d)
			t[ d ] = translation[ d ];
	}

	public void setTranslation( final long[] t )
	{
		assert t.length == m;
		for (int d = 0; d < m; ++d)
			translation[ d ] = t[ d ];
	}

	public void getPermutation( final boolean[] zero, final int[] component, final boolean[] inv )
	{
		assert zero.length == m;
		assert inv.length == m;
		assert component.length == m;
		for (int d = 0; d < m; ++d)
		{
			zero[ d ] = targetZero[ d ];
			inv[ d ] = targetInv[ d ];
			component[ d ] = targetComponent[ d ];
		}		
	}
	
	public void setPermutation( final boolean[] zero, final int[] component, final boolean[] inv )
	{
		assert zero.length == m;
		assert inv.length == m;
		assert component.length == m;
		for (int d = 0; d < m; ++d)
		{
			assert component[ d ] < n;
			targetZero[ d ] = zero[ d ];
			targetInv[ d ] = inv[ d ];
			targetComponent[ d ] = component[ d ];
		}
	}
	
	public void setPermutation( final int[] component )
	{
		assert component.length == m;
		for (int d = 0; d < n; ++d)
		{
			assert component[ d ] < n;
			targetZero[ d ] = false;
			targetInv[ d ] = false;
			targetComponent[ d ] = component[ d ];
		}
	}
	
	public Matrix getMatrix()
	{
		Matrix mat = new Matrix( m+1, n+1 );

		mat.set( m, n, 1 );
		
		for ( int d = 0; d < m; ++d )
		{
			mat.set( d, n , translation[ d ] );
		}
		
		for ( int d = 0; d < m; ++d )
		{
			if ( targetZero[ d ] == false )
			{
				mat.set (d, targetComponent[ d ], targetInv[ d ] ? -1 : 1 ); 
			}
		}
		
		return mat;
	}
}
