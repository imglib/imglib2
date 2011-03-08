package mpicbg.imglib.view;

import mpicbg.imglib.AbstractRandomAccess;
import mpicbg.imglib.RandomAccess;

/**
 * TODO: extend AbstractSampler and implement RandomAccess< T > interface directly
 *       (keeping our own position[] in addition to target's is not very efficient...)
 * 
 * @author Tobias Pietzsch
 *
 * @param <T>
 */
public final class ViewTransformRandomAccess< T > extends AbstractRandomAccess< T >
{
	private final RandomAccess< T > target;
	
	private final int m;
	
	private final long[] translation;

	/**
	 * for each component of the target vector (before translation).
	 * should the value be taken from a source vector component (false) or should it be zero (true).
	 */
	private final boolean[] targetZero;

	/**
	 * for each component of the target vector (before translation).
	 * should the source vector component be inverted (true).
	 */
	private final boolean[] targetInv;
	
	/**
	 * for each component of the target vector (before translation).
	 * from which source vector component should it be taken.
	 */
	private final int[] targetComponent;
	
	/**
	 * for each component of the source vector.
	 * should the value be taken to a target vector component (false) or should it be discarded (true).
	 */
	private final boolean[] sourceZero;

	/**
	 * for each component of the source vector.
	 * should the target vector component be inverted (true).
	 */
	private final boolean[] sourceInv;
	

	/**
	 * for each component of the source vector.
	 * to which target vector component should it be taken.
	 */
	private final int[] sourceComponent;
	
	private final long[] tmp;

	ViewTransformRandomAccess( RandomAccess< T > target, ViewTransform transform )
	{
		super( transform.sourceDim() );

		assert target.numDimensions() == transform.targetDim();

		this.target = target;
		m = transform.targetDim();
		translation = new long[ m ];
		targetZero = new boolean[ m ];
		targetInv = new boolean[ m ];
		targetComponent = new int[ m ];
		sourceZero = new boolean[ n ];
		sourceInv = new boolean[ n ];
		sourceComponent = new int[ n ];
		transform.getTranslation( translation );
		transform.getPermutation( targetZero, targetComponent, targetInv );

		for( int e = 0; e < n; ++e )
		{
			sourceZero[ e ] = true;
		}
		for( int d = 0; d < m; ++d )
		{
			if ( ! targetZero[ d ] )
			{
				final int e = targetComponent[ d ]; 
				sourceZero[ e ] = false;
				sourceInv[ e ] = targetInv[ d ];
				sourceComponent[ e ] = d;
			}
		}
			
		
		tmp = new long[ m ];
	}

	@Override
	public void fwd( int d )
	{
		position[ d ] += 1;
		if( ! sourceZero[ d ] )
		{
			if( sourceInv[ d ] )
			{
				target.bck( sourceComponent[ d ] );
			}
			else
			{
				target.fwd( sourceComponent[ d ] );
			}
		}
	}

	@Override
	public void bck( int d )
	{
		position[ d ] -= 1;
		if( ! sourceZero[ d ] )
		{
			if( sourceZero[ d ] )
			{
				target.fwd( sourceComponent[ d ] );
			}
			else
			{
				target.bck( sourceComponent[ d ] );
			}
		}
	}

	@Override
	public void move( long distance, int d )
	{
		position[ d ] += distance;
		if( ! targetZero[ d ] )
			target.move( distance, targetInv[ d ] ? - targetComponent[ d ] : targetComponent[ d ] );
	}

	private void transformVector( long[] in, long[] out )
	{
		assert in.length == n;
		assert out.length == m;

		for (int d = 0; d < m; ++d )
		{
			out[ d ] = translation[ d ];
			if ( ! targetZero[ d ] )
			{
				final long v = in[ targetComponent[ d ] ];
				if ( targetInv[ d ] )
					out[ d ] -= v;
				else
					out[ d ] += v;
			}
		}
	}

	private void transformVector( int[] in, long[] out )
	{
		assert in.length == n;
		assert out.length == m;

		for (int d = 0; d < m; ++d )
		{
			out[ d ] = translation[ d ];
			if ( ! targetZero[ d ] )
			{
				final long v = in[ targetComponent[ d ] ];
				if ( targetInv[ d ] )
					out[ d ] -= v;
				else
					out[ d ] += v;
			}
		}
	}

	@Override
	public void setPosition( long[] position )
	{
		for( int d = 0; d < n; ++d )
		{
			this.position[ d ] = position[ d ];
		}
		transformVector( position, tmp );
		target.setPosition( tmp );
	}

	@Override
	public void setPosition( int[] position )
	{
		for( int d = 0; d < n; ++d )
		{
			this.position[ d ] = position[ d ];
		}
		transformVector( position, tmp );
		target.setPosition( tmp );
	}

	@Override
	public void setPosition( long position, int d )
	{
		this.position[ d ] = position; 
		if( ! sourceZero[ d ] )
		{
			final int e = sourceComponent[ d ]; 
			final long targetPos = translation[ e ] + ( targetInv[ e ] ? -position : position );
			target.setPosition( targetPos, e );
		}
	}

	@Override
	public T get()
	{
		return target.get();
	}
}
